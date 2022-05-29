package com.example.moneymanagementapp;


import static com.example.moneymanagementapp.NotificationActivity.CountNotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import java.util.Random;

public class TodaySpendingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private TextView totalAmountSpentOn,this_day;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expensesRef;
    private DatabaseReference budgetRef;
    private TodayItemAdapter todayItemsAdapter;
    private List<Data> myDataList;
    String idApparel="channel_Apparel";
    String idCharity="channel_Charity";
    String idEducation="channel_Education";
    String idEntertainment="channel_Entertainment";
    String idFood="channel_Food";
    String idHealth="channel_Health";
    String idHouse="channel_House";
    String idPersonal="channel_Personal";
    String idTransport="channel_Transport";
    String idOther="channel_Other";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        mAuth=FirebaseAuth.getInstance();
        totalAmountSpentOn=findViewById(R.id.totalAmountSpentOn);
        this_day=findViewById(R.id.tv_this_day);
        progressBar=findViewById(R.id.progressBar);
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        TextView title = findViewById(R.id.txv_title);

        fab=findViewById(R.id.fab);
        loader=new ProgressDialog(this);


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
        View myView=inflater.inflate(R.layout.input_layout,null);
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
                checkIn();
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
                                check(Item);
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

    //////////////////////////////////
    /////////////////////////////////
    ///////////////////////////////

    int totalAmount;

    private int getTotalWeekOtherExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Other" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount;
    }

    public int total;

    private int getMonthOtherBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Other" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total;
    }

    public int totalAmount1;

    private int getTotalWeekPersonalExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Personal" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount1 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount1;
    }

    public int totalPersonal;

    private int getMonthPersonalBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Personal" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalx = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalx += pTotal;
                    }
                    totalPersonal = totalx;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalPersonal;
    }

    public int totalAmount2;

    private int getTotalWeekHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Health" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount2 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount2;
    }

    public int total2;

    private int getMonthHealthBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Health" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total2 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total2;
    }

    public int totalAmount3;

    private int getTotalWeekApparelExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Apparel" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount3 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount3;
    }

    public int total3;

    private int getMonthApparelBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Apparel" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total3 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total3;
    }

    public int totalAmount4;

    private int getTotalWeekCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Charity" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount4 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount4;
    }

    public int total4;

    private int getMonthCharityBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Charity" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total4 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total4;
    }

    public int totalAmount5;

    private int getTotalWeekEducationExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Education" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount5 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount5;
    }

    public int total5;

    private int getMonthEducationBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Education" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total5 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total5;
    }

    public int totalAmount6;

    private int getTotalWeekEntertainmentExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Entertainment" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount6 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount6;
    }

    public int total6;

    private int getMonthEntertainmentBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Entertainment" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total6 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total6;
    }

    public int totalAmount7;

    private int getTotalWeekHouseExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "House" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount7 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount7;
    }

    public int total7;

    private int getMonthHouseBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        String itemNmonth = "House" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total7 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total7;
    }

    public int totalAmount8;
    private int getTotalWeekFoodExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Food" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount8 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount8;
    }

    public int total8;

    private int getMonthFoodBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Food" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total8 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total8;
    }

    public int totalAmount9;

    private int getTotalWeekTransportExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Transport" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    totalAmount9 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount9;
    }

    public int total9;

    private int getMonthTransportBudgetRatio() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Transport" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total1 = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        total1 += pTotal;
                    }
                    total9 = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodaySpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total9;
    }
  private void  checkIn()
    {
        if(getTotalWeekApparelExpenses()>getMonthApparelBudgetRatio())
        {

        }
        if(getTotalWeekCharityExpenses()>getMonthCharityBudgetRatio())
        {

        }
        if(getTotalWeekEntertainmentExpenses()>getMonthEntertainmentBudgetRatio())
        {

        }
        if(getTotalWeekEducationExpenses()>getMonthEducationBudgetRatio())
        {

        }
        if(getTotalWeekFoodExpenses()>getMonthFoodBudgetRatio())
        {

        }
        if(getTotalWeekHealthExpenses()>getMonthHealthBudgetRatio())
        {

        }
        if(getTotalWeekHouseExpenses()>getMonthHouseBudgetRatio())
        {

        }
        if(getTotalWeekPersonalExpenses()>getMonthPersonalBudgetRatio())
        {

        }
        if(getTotalWeekTransportExpenses()>getMonthTransportBudgetRatio())
        {

        }
        if(getTotalWeekOtherExpenses()>getMonthOtherBudgetRatio())
        {

        }
    }
    private void check(String Item)
    {
        if(Item.equals("Apparel")&&getTotalWeekApparelExpenses()>getMonthApparelBudgetRatio()&&getMonthApparelBudgetRatio()>0)
        {
            createNotificationApparel();
            CountNotification();
        }
        if(Item.equals("Charity")&&getTotalWeekCharityExpenses()>getMonthCharityBudgetRatio()&&getMonthCharityBudgetRatio()>0)
        {
            createNotificationCharity();
            CountNotification();
        }
        if(Item.equals("Entertainment")&&getTotalWeekEntertainmentExpenses()>getMonthEntertainmentBudgetRatio()&&getMonthEntertainmentBudgetRatio()>0)
        {
             createNotificationEntertainment();
        }
        if(Item.equals("Education")&&getTotalWeekEducationExpenses()>getMonthEducationBudgetRatio()&&getMonthEducationBudgetRatio()>0)
        {
                  createNotificationEducation();
            CountNotification();
        }
        if(Item.equals("Food")&&getTotalWeekFoodExpenses()>getMonthFoodBudgetRatio()&&getMonthFoodBudgetRatio()>0)
        {
                  createNotificationFood();
            CountNotification();
        }
        if(Item.equals("Health")&&getTotalWeekHealthExpenses()>getMonthHealthBudgetRatio()&&getMonthHealthBudgetRatio()>0)
        {
                  createNotificationHealth();
            CountNotification();
        }
        if(Item.equals("House")&&getTotalWeekHouseExpenses()>getMonthHouseBudgetRatio()&&getMonthHouseBudgetRatio()>0)
        {
                  createNotificationHouse();
            CountNotification();
        }
        if(Item.equals("Personal")&&getTotalWeekPersonalExpenses()>getMonthPersonalBudgetRatio()&&getMonthPersonalBudgetRatio()>0)
        {
                 createNotificationPersonal();
            CountNotification();
        }
        if(Item.equals("Transport")&&getTotalWeekTransportExpenses()>getMonthTransportBudgetRatio()&&getMonthTransportBudgetRatio()>0)
        {
               createNotificationTransport();
            CountNotification();
        }
        if(Item.equals("Other")&&getTotalWeekOtherExpenses()>getMonthOtherBudgetRatio()&&getMonthOtherBudgetRatio()>0)
        {
             createNotificationOther();
            CountNotification();
        }
    }
private void createNotificationApparel()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idApparel);
            if(channel==null)
            {
                channel =new NotificationChannel(idApparel,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idApparel)
                .setSmallIcon(R.drawable.apparel)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.apparel))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationApparel");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationCharity()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idCharity);
            if(channel==null)
            {
                channel =new NotificationChannel(idCharity,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idCharity)
                .setSmallIcon(R.drawable.charity)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.charity))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationCharity");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationEducation()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idEducation);
            if(channel==null)
            {
                channel =new NotificationChannel(idEducation,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idEducation)
                .setSmallIcon(R.drawable.education)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.education))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationEducation");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationEntertainment()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idEntertainment);
            if(channel==null)
            {
                channel =new NotificationChannel(idEntertainment,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idEntertainment)
                .setSmallIcon(R.drawable.entertainment)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.entertainment))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationEntertainment");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationFood()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idFood);
            if(channel==null)
            {
                channel =new NotificationChannel(idFood,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idFood)
                .setSmallIcon(R.drawable.food)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.food))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationFood");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationHealth()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idHealth);
            if(channel==null)
            {
                channel =new NotificationChannel(idHealth,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idHealth)
                .setSmallIcon(R.drawable.health)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.health))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationHealth");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationHouse()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idHouse);
            if(channel==null)
            {
                channel =new NotificationChannel(idHouse,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idHouse)
                .setSmallIcon(R.drawable.house)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.house))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationHouse");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationTransport()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idTransport);
            if(channel==null)
            {
                channel =new NotificationChannel(idTransport,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idTransport)
                .setSmallIcon(R.drawable.transport)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.transport))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationTransport");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationPersonal()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idPersonal);
            if(channel==null)
            {
                channel =new NotificationChannel(idPersonal,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idPersonal)
                .setSmallIcon(R.drawable.personal)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.personal))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationPersonal");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
    private void createNotificationOther()
    {
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=manager.getNotificationChannel(idOther);
            if(channel==null)
            {
                channel =new NotificationChannel(idOther,"Cảnh báo",NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Vượt mức chi tiêu");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent =new Intent(TodaySpendingActivity.this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,idOther)
                .setSmallIcon(R.drawable.other)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.other))
                .setContentTitle("Cảnh báo")
                .setContentText("Vượt mức chi tiêu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(true)
                .setTicker("NotificationOther");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }

}