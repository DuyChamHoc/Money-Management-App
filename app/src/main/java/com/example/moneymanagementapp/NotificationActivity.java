package com.example.moneymanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView totalAmountSpentOn,totalOverSpend;
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef;
    private DatabaseReference budgetRef;
    private OverSpendAdapter overSpendAdapter;
    private List<DataOverSpend> myDataList;
   private int tong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        totalOverSpend=findViewById(R.id.totalOverSpend);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn);
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myDataList = new ArrayList<>();
        overSpendAdapter = new OverSpendAdapter(NotificationActivity.this, myDataList);
        recyclerView.setAdapter(overSpendAdapter);
        icon_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
        );
    }

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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    total = total1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount1;
    }

    public int total1;

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
                    total1 = totalx;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total1;
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total9;
    }
private void overOther()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekOtherExpenses()>getMonthOtherBudgetRatio()&&getMonthOtherBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Other",getTotalWeekOtherExpenses()-getMonthOtherBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekOtherExpenses()-getMonthOtherBudgetRatio();
            }
        }
    },4000);
    int h=getMonthOtherBudgetRatio();
    int k=getTotalWeekOtherExpenses();
}
private void overApparel()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekApparelExpenses()>getMonthApparelBudgetRatio()&&getMonthApparelBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Apparel",getTotalWeekApparelExpenses()-getMonthApparelBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekApparelExpenses()-getMonthApparelBudgetRatio();
            }
        }
    },4000);
    int h=getMonthApparelBudgetRatio();
    int k=getTotalWeekApparelExpenses();
}
private void overCharity()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekCharityExpenses()>getMonthCharityBudgetRatio()&&getMonthCharityBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Charity",getTotalWeekCharityExpenses()-getMonthCharityBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekCharityExpenses()-getMonthCharityBudgetRatio();
            }
        }
    },4000);
    int h=getMonthCharityBudgetRatio();
    int k=getTotalWeekCharityExpenses();
}
private void overEducation()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekEducationExpenses()>getMonthEducationBudgetRatio()&&getMonthEducationBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Education",getTotalWeekEducationExpenses()-getMonthEducationBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekEducationExpenses()-getMonthEducationBudgetRatio();
            }
        }
    },4000);
    int h=getMonthEducationBudgetRatio();
    int k=getTotalWeekEducationExpenses();
}
private void overEntertainment()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekEntertainmentExpenses()>getMonthEntertainmentBudgetRatio()&&getMonthEntertainmentBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Entertainment",getTotalWeekEntertainmentExpenses()-getMonthEntertainmentBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekEntertainmentExpenses()-getMonthEntertainmentBudgetRatio();
            }
        }
    },4000);
    int h=getMonthEntertainmentBudgetRatio();
    int k=getTotalWeekEntertainmentExpenses();
}
private void overFood()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekFoodExpenses()>getMonthFoodBudgetRatio()&&getMonthFoodBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("Food",getTotalWeekFoodExpenses()-getMonthFoodBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekFoodExpenses()-getMonthFoodBudgetRatio();
            }
        }
    },4000);
    int h=getMonthFoodBudgetRatio();
    int k=getTotalWeekFoodExpenses();
}
private void overHouse()
{
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(getTotalWeekHouseExpenses()>getMonthHouseBudgetRatio()&&getMonthHouseBudgetRatio()>0)
            {
                myDataList.add(new DataOverSpend("House",getTotalWeekHouseExpenses()-getMonthHouseBudgetRatio()));
                overSpendAdapter.notifyDataSetChanged();
                tong+=getTotalWeekHouseExpenses()-getMonthHouseBudgetRatio();
            }
        }
    },4000);
    int h=getMonthHouseBudgetRatio();
    int k=getTotalWeekHouseExpenses();
}
    private void overPersonal()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getTotalWeekPersonalExpenses()>getMonthPersonalBudgetRatio()&&getMonthPersonalBudgetRatio()>0)
                {
                    myDataList.add(new DataOverSpend("Personal",getTotalWeekPersonalExpenses()-getMonthPersonalBudgetRatio()));
                    overSpendAdapter.notifyDataSetChanged();
                    tong+=getTotalWeekPersonalExpenses()-getMonthPersonalBudgetRatio();
                }
            }
        },4000);
        int h=getMonthPersonalBudgetRatio();
        int k=getTotalWeekPersonalExpenses();
    }

    private void overHealth()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getTotalWeekHealthExpenses()>getMonthHealthBudgetRatio()&&getMonthHealthBudgetRatio()>0)
                {
                    myDataList.add(new DataOverSpend("Health",getTotalWeekHealthExpenses()-getMonthHealthBudgetRatio()));
                    overSpendAdapter.notifyDataSetChanged();
                    tong+=getTotalWeekHealthExpenses()-getMonthHealthBudgetRatio();
                }
            }
        },4000);
        int h=getMonthHealthBudgetRatio();
        int k=getTotalWeekHealthExpenses();
    }
    private void overTransport()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getTotalWeekTransportExpenses()>getMonthTransportBudgetRatio()&&getMonthTransportBudgetRatio()>0)
                {
                    myDataList.add(new DataOverSpend("Transport",getTotalWeekTransportExpenses()-getMonthTransportBudgetRatio()));
                    overSpendAdapter.notifyDataSetChanged();
                    tong+=getTotalWeekTransportExpenses()-getMonthTransportBudgetRatio();
                }
            }
        },4000);
        int h=getMonthTransportBudgetRatio();
        int k=getTotalWeekTransportExpenses();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressBar.setVisibility(View.VISIBLE);
        tong=0;
        super.onStart();
        overOther();
        overTransport();
        overPersonal();
        overHouse();
        overHealth();
        overFood();
        overEntertainment();
        overEducation();
        overCharity();
        overApparel();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                totalOverSpend.setText("$ "+Integer.toString(tong));
            }
        },4000);
    }

    @Override
    protected void onStart() {
        tong=0;
        super.onStart();
        overOther();
        overTransport();
        overPersonal();
        overHouse();
        overHealth();
        overFood();
        overEntertainment();
        overEducation();
        overCharity();
        overApparel();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                totalOverSpend.setText("$ "+Integer.toString(tong));
            }
        },4000);
    }
}