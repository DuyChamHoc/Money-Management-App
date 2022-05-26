package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ImageView image_hoso;
private ImageButton bell;
    private LinearLayout linearNgansach;
    private RelativeLayout bigestlayout;

    private TextView tv_homnay,tv_thunhap,tv_week,tv_month,tv_chiphi,moneytoday,moneyweek,moneymonth,tv_sodu;

    private BottomNavigationView bottomNavigationView;

    private FloatingActionButton add,add_chiphi,add_ngansach;
    private Boolean aBoolean=true;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private DatabaseReference budgetRef,personalRef,expensesRef;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList;
    private String onlineUserId = "";

    private int totalAmountMonth=0;
    private int totalAmountBudget=0;
    private int totalAmountBudgetB=0;
    private int totalAmountBudgetC=0;
    private int totalAmountBudgetD=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bell=findViewById(R.id.bell);
        tv_homnay = findViewById(R.id.tv_homnay);
        tv_week = findViewById(R.id.tv_week);
        tv_month = findViewById(R.id.tv_month);
        tv_thunhap = findViewById(R.id.tv_thunhap);
        tv_chiphi = findViewById(R.id.tv_chiphi);
        moneytoday = findViewById(R.id.moneytoday);
        moneyweek = findViewById(R.id.moneyweek);
        moneymonth = findViewById(R.id.moneymonth);
        tv_sodu = findViewById(R.id.tv_sodu);
        image_hoso = findViewById(R.id.image_hoso);

        bigestlayout = findViewById(R.id.bigestlayout);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        linearNgansach = findViewById(R.id.linearNgansach);
        linearNgansach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,BudgetActivity.class);
                startActivity(intent);
            }
        });
        add = findViewById(R.id.add);
        add_chiphi = findViewById(R.id.btn_chiphi);
        add_ngansach = findViewById(R.id.btn_ngansach);
        fab_open = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this,R.anim.rotate_backforward);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        mAuth = FirebaseAuth.getInstance();
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        myDataList = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(HomeActivity.this, myDataList);
        recyclerView.setAdapter(weekSpendingAdapter);

        readMonthSpendingItems();
        showUserInformation();

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_history:
                        startActivity(new Intent(HomeActivity.this,HistoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_home:
                        return true;
                    case R.id.action_profile:
                        startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_chart:
                        startActivity(new Intent(HomeActivity.this,ChooseAnalyticActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFab();
            }
        });
bell.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(HomeActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
});
        bigestlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouch(motionEvent);
                return true;
            }
        });
        bigestlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouch(motionEvent);
                return true;
            }
        });


        add_ngansach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,BudgetActivity.class);
                startActivity(intent);
                add.startAnimation(rotate_backward);
                add_chiphi.hide();
                add_ngansach.hide();
                aBoolean = true;
            }
        });

        add_chiphi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
                add.startAnimation(rotate_backward);
                add_chiphi.hide();
                add_ngansach.hide();
                aBoolean = true;
            }
        });
        tv_homnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });
        tv_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });
        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,MonthSpendingActivity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });
        image_hoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
        showUserInformation();
    }

    private void handleFab() {
        if(aBoolean)
        {
            add.startAnimation(rotate_forward);
            add_chiphi.startAnimation(fab_open);
            add_ngansach.startAnimation(fab_open);
            add_chiphi.setClickable(true);
            add_ngansach.setClickable(true);
            add_chiphi.show();
            add_ngansach.show();
            aBoolean = false;
        }else{
            add.startAnimation(rotate_backward);
            add_chiphi.startAnimation(fab_close);
            add_ngansach.startAnimation(fab_close);
            add_chiphi.setClickable(false);
            add_ngansach.setClickable(false);
            add_chiphi.hide();
            add_ngansach.hide();
            aBoolean = true;
        }
    }

    private void handleTouch(MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            add_chiphi.hide();
            add_ngansach.hide();
            add.startAnimation(rotate_backward);
            aBoolean = true;
        }
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

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB+=pTotal;
                    }
                    totalAmountBudgetC=totalAmountBudgetB;
                    personalRef.child("budget").setValue(totalAmountBudgetC);
                    totalAmountBudgetB=0;
                }else {
                    personalRef.child("budget").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getBudgetAmount();
        getTodaySpendingAmount();
        getWeekSpendingAmount();
        getMonthSpendingAmount();
        getSavings();

    }
    private void showUserInformation(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(image_hoso);
    }

    private void getSavings() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int budget;
                    if(snapshot.hasChild("budget")){
                        budget=Integer.parseInt(Objects.requireNonNull(snapshot.child("budget").getValue().toString()));
                    }
                    else {
                        budget=0;
                    }
                    int monthSpending;
                    if(snapshot.hasChild("month")){
                        monthSpending=Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    }
                    else {
                        monthSpending=0;
                    }
                    int savings=budget-monthSpending;
                    tv_sodu.setText(savings+"$");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMonthSpendingAmount() {
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount=0;
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        moneymonth.setText(totalAmount+"$");
                        tv_chiphi.setText(totalAmount+"$");
                    }
                    personalRef.child("month").setValue(totalAmount);
                    totalAmountMonth=totalAmount;
                }
                else {
                    totalAmount=0;
                    moneymonth.setText(totalAmount + "$");
                    tv_chiphi.setText(totalAmount+"$");
                    personalRef.child("month").setValue(totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWeekSpendingAmount() {
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);
        DateTime now=new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoch,now);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount=0;
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        moneyweek.setText(totalAmount+"$");
                    }
                    personalRef.child("week").setValue(totalAmount);
                }
                else {
                    totalAmount=0;
                    moneyweek.setText(totalAmount + "$");
                    personalRef.child("week").setValue(totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTodaySpendingAmount() {
        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal=Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount=0;
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        moneytoday.setText(totalAmount + "$");
                    }
                    personalRef.child("today").setValue(totalAmount);
                }
                else {
                    totalAmount=0;
                    moneytoday.setText(totalAmount + "$");
                    personalRef.child("today").setValue(totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmountBudget+=pTotal;
                        tv_thunhap.setText(String.valueOf(totalAmountBudget)+"$");
                    }
                    totalAmountBudget=0;
                }else {
                    totalAmountBudget=0;
                    tv_thunhap.setText(String.valueOf(totalAmountBudget)+"$");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}