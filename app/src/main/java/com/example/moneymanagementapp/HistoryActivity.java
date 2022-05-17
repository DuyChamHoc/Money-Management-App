package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private BottomNavigationView bottomNavigationView;
    private int totalAmount = 0;
    private String Date="";
    private RecyclerView recyclerView;
    private ItemAdapter ItemsAdapter;
    private List<Data> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private Button search;
    private TextView historyTotalAmountSpent,dateSearch;

    private LinearLayout historyLayout;
    private RelativeLayout beforeSearchLayout,noResultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_history);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_history:
                        return true;
                    case R.id.action_home:
                        startActivity(new Intent(HistoryActivity.this,HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_profile:
                        startActivity(new Intent(HistoryActivity.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_chart:
                        startActivity(new Intent(HistoryActivity.this,ChooseAnalyticActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        search = findViewById(R.id.search);
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmountSpent);
        dateSearch = findViewById(R.id.date);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recycler_View_Id_feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        myDataList = new ArrayList<>();
        ItemsAdapter = new ItemAdapter(HistoryActivity.this, myDataList);
        recyclerView.setAdapter(ItemsAdapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        historyLayout = findViewById(R.id.historyList);
        beforeSearchLayout = findViewById(R.id.before_search_results);
        noResultLayout = findViewById(R.id.no_results_found);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
        historyTotalAmountSpent.setText("");
        dateSearch.setText("");
        totalAmount=0;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        int months = month + 1;
        String date="";
        if(dayOfMonth<10&&months<10)
        {
            date =  "0"+dayOfMonth + "-" + "0" + months + "-" + year;
        }
        else{
            if(dayOfMonth<10&&months>=10){
                date =  "0"+dayOfMonth + "-" +months + "-" + year;
            }
            else {
                if(dayOfMonth>10&&months<10){
                    date =  dayOfMonth + "-" + "0" +months + "-" + year;
                }
                else {
                    date =  dayOfMonth + "-" +  +months + "-" + year;
                }
            }
        }
        Date=date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                myDataList.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Data data = snapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                if(myDataList.size()==0){
                    noResultLayout.setVisibility(View.VISIBLE);
                    historyLayout.setVisibility(View.GONE);
                    beforeSearchLayout.setVisibility(View.GONE);
                    Toast.makeText(HistoryActivity.this, "Please choose another day", Toast.LENGTH_SHORT).show();
                }
                ItemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    if(totalAmount>0){
                        historyTotalAmountSpent.setVisibility(View.VISIBLE);
                        dateSearch.setVisibility(View.VISIBLE);

                        historyLayout.setVisibility(View.VISIBLE);
                        noResultLayout.setVisibility(View.GONE);
                        beforeSearchLayout.setVisibility(View.GONE);

                        historyTotalAmountSpent.setText(totalAmount+"$");
                        dateSearch.setText(" "+Date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}

