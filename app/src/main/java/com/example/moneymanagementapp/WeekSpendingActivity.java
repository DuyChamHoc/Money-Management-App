package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class WeekSpendingActivity extends AppCompatActivity {

    private TextView totalWeekAmountTextView,tv_this_week;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef;

    private String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        totalWeekAmountTextView = findViewById(R.id.totalWeekAmountTextView);
        progressBar = findViewById(R.id.progressBar);

        tv_this_week = findViewById(R.id.tv_this_week);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        myDataList = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpendingActivity.this, myDataList);
        recyclerView.setAdapter(weekSpendingAdapter);
        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        TextView title = findViewById(R.id.txv_title);

        icon_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeekSpendingActivity.this,HomeActivity.class);
                startActivity(intent);
            }}
        );

        if(getIntent().getExtras()!=null){
            type=getIntent().getStringExtra("type");
            if(type.equals("week")){
                readWeekSpendingItems();
            }else if(type.equals("month")){
                readMonthSpendingItems();
            }
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar ca1 = Calendar.getInstance();
        ca1.set(year, month, day);
        ca1.setMinimalDaysInFirstWeek(1);
        int wk = ca1.get(Calendar.WEEK_OF_MONTH);
        tv_this_week.setText(": "+String.valueOf(wk));
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
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }

                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTextView.setText("$ " + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readWeekSpendingItems() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }

                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTextView.setText("$ " + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}