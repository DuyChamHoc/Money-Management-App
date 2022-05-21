package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private BottomNavigationView bottomNavigationView;
    private int totalAmount = 0;
    private String values ="";
    private String Date="";
    private ImageView ImgSpinner;
    private RecyclerView recyclerView;
    private String lang="";

    private String dataEN[]={"Search", "Transport", "Food", "House", "Entertainment", "Education", "Charity", "Apparel", "Health", "Personal", "Other"};
    private String dataVI[]={"Tìm kiếm", "Phương tiện", "Thức ăn", "Nhà ở", "Giải trí", "Giáo dục", "Từ thiện", "Mua sắm", "Sức khỏe", "Cá nhân", "Khác"};
    private Spinner itemSpinner;

    private ItemAdapter ItemsAdapter;
    private List<Data> myDataList;

    private RecyclerView recyclerView1;
    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList1;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private Button search;
    private TextView historyTotalAmountSpent,dateSearch;

    private LinearLayout historyLayout,results,results_day,historyList_Values,detail;
    private RelativeLayout beforeSearchLayout,noResultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyList_Values = findViewById(R.id.historyList_Values);
        recyclerView1 = findViewById(R.id.recyclerView1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(linearLayoutManager);
        myDataList1 = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(HistoryActivity.this, myDataList1);
        recyclerView1.setAdapter(weekSpendingAdapter);

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
        ImgSpinner = findViewById(R.id.mImage);
        final String Adapter_name[]=dataEN;
        itemSpinner =findViewById(R.id.itemsspinner);
        itemSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Adapter_name));
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[0].toString())){
                    ImgSpinner.setImageResource(R.drawable.searchh);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[1].toString())){
                    ImgSpinner.setImageResource(R.drawable.transport);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[2].toString())){
                    ImgSpinner.setImageResource(R.drawable.food);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[3].toString())){
                    ImgSpinner.setImageResource(R.drawable.house);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[4].toString())){
                    ImgSpinner.setImageResource(R.drawable.entertainment);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[5].toString())){
                    ImgSpinner.setImageResource(R.drawable.education);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[6].toString())){
                    ImgSpinner.setImageResource(R.drawable.charity);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[7].toString())){
                    ImgSpinner.setImageResource(R.drawable.apparel);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[8].toString())){
                    ImgSpinner.setImageResource(R.drawable.health);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[9].toString())){
                    ImgSpinner.setImageResource(R.drawable.personal);
                }
                else if(itemSpinner.getSelectedItem().toString().equals(Adapter_name[10].toString())){
                    ImgSpinner.setImageResource(R.drawable.other);
                }
                values = itemSpinner.getSelectedItem().toString();
                readMonthSpendingItems();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search = findViewById(R.id.search);
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmountSpent);
        dateSearch = findViewById(R.id.date);
        results = findViewById(R.id.results);
        results_day = findViewById(R.id.results_day);
        detail = findViewById(R.id.detail);

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
        ImgSpinner.setImageResource(R.drawable.searchh);

        beforeSearchLayout = findViewById(R.id.before_search_results);
        noResultLayout = findViewById(R.id.no_results_found);
    }
    private void readMonthSpendingItems() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList1.clear();
                int totalAmount = 0;
                int i=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    if(data.getItem().toString().equals(values)) {
                        myDataList1.add(data);
                        totalAmount +=data.getAmount();
                        i++;
                    }
                }
                weekSpendingAdapter.notifyDataSetChanged();
                if(i!=0)
                {
                    historyList_Values.setVisibility(View.VISIBLE);
                    historyTotalAmountSpent.setVisibility(View.VISIBLE);

                    noResultLayout.setVisibility(View.GONE);
                    historyLayout.setVisibility(View.GONE);
                    beforeSearchLayout.setVisibility(View.GONE);
                    results.setVisibility(View.GONE);
                    results_day.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                    historyTotalAmountSpent.setText("$ " + totalAmount);
                }
                else{
                    historyList_Values.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        ImgSpinner.setImageResource(R.drawable.searchh);
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
                    results.setVisibility(View.GONE);
                    results_day.setVisibility(View.GONE);
                    historyList_Values.setVisibility(View.GONE);
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

                        detail.setVisibility(View.VISIBLE);
                        results.setVisibility(View.VISIBLE);
                        results_day.setVisibility(View.VISIBLE);
                        historyLayout.setVisibility(View.VISIBLE);
                        noResultLayout.setVisibility(View.GONE);
                        beforeSearchLayout.setVisibility(View.GONE);
                        historyList_Values.setVisibility(View.GONE);
                        historyTotalAmountSpent.setText(" "+totalAmount+"$");
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

