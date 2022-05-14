package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private TextView totalAmountSpentOn,this_day;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expensesRef;

    private TodayItemAdapter todayItemsAdapter;
    private List<Data> myDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        totalAmountSpentOn=findViewById(R.id.totalAmountSpentOn);
        this_day=findViewById(R.id.tv_this_day);
        progressBar=findViewById(R.id.progressBar);

        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        TextView title = findViewById(R.id.txv_title);

        fab=findViewById(R.id.fab);
        loader=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myDataList = new ArrayList<>();
        todayItemsAdapter=new TodayItemAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        readItems();

        icon_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TodaySpendingActivity.this,HomeActivity.class);
                startActivity(intent);
            }}
        );
        title.setText("Today's Spending");


        Format f = new SimpleDateFormat("HH.mm.ss Z");
        f = new SimpleDateFormat("EEEE");
        String str = f.format(new Date());
        this_day.setText(str);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });
    }

    private void readItems() {
        DateFormat dateFormat =new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal=Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data=dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                todayItemsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        totalAmountSpentOn.setText("$ " + totalAmount);
                    }
                }
                else {
                    totalAmount=0;
                    totalAmountSpentOn.setText("$ " + totalAmount);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn() {
        AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View myView=inflater.inflate(R.layout.input_layout1,null);
        myDialog.setView(myView);

        final AlertDialog dialog=myDialog.create();
        dialog.setCancelable(false);

        final ImageView ImgSpinner =myView.findViewById(R.id.mImage);
        final String Adapter_name[]={"Transport","Food","House","Entertainment","Education","Charity","Apparel","Health","Personal","Other"};
        final Spinner itemSpinner =myView.findViewById(R.id.itemsspinner);
        itemSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Adapter_name));
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(itemSpinner.getSelectedItem().toString().equals("Transport")){
                    ImgSpinner.setImageResource(R.drawable.transport);
                }else if(itemSpinner.getSelectedItem().toString().equals("Food")){
                    ImgSpinner.setImageResource(R.drawable.food);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("House")){
                    ImgSpinner.setImageResource(R.drawable.house);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Entertainment")){
                    ImgSpinner.setImageResource(R.drawable.entertainment);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Education")){
                    ImgSpinner.setImageResource(R.drawable.education);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Charity")){
                    ImgSpinner.setImageResource(R.drawable.charity);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Apparel")){
                    ImgSpinner.setImageResource(R.drawable.apparel);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Health")){
                    ImgSpinner.setImageResource(R.drawable.health);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Personal")){
                    ImgSpinner.setImageResource(R.drawable.personal);
                }
                else if(itemSpinner.getSelectedItem().toString().equals("Other")){
                    ImgSpinner.setImageResource(R.drawable.other);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText amount =myView.findViewById(R.id.amount);
        final EditText note=myView.findViewById(R.id.note);
        final ImageView save =myView.findViewById(R.id.save);
        final ImageView btn_close =myView.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Amount=amount.getText().toString();
                String Item =itemSpinner.getSelectedItem().toString();
                String notes=note.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    amount.setError("Amount is required");
                    return;
                }
                if(TextUtils.isEmpty(notes))
                {
                    note.setError("Note is required");
                    return;
                }
                else
                {
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id =expensesRef.push().getKey();
                    DateFormat dateFormat =new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal=Calendar.getInstance();
                    String date=dateFormat.format(cal.getTime());

                    MutableDateTime epoch=new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks=Weeks.weeksBetween(epoch,now);
                    Months months=Months.monthsBetween(epoch,now);
                    String itemNday=Item+date;
                    String itemNweek=Item+weeks.getWeeks();
                    String itemNmonth=Item+months.getMonths();

                    Data data=new Data(Item,date,id,notes,itemNday,itemNweek,itemNmonth,Integer.parseInt(Amount),months.getMonths(),weeks.getWeeks());
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this, "Budget item added successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(TodaySpendingActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}