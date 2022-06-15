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
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;


public class BudgetActivity extends AppCompatActivity {

    private TextView totalBudgetAmountTextView,This_month;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference budgetRef, personalRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private String test = "";
    private String onlineUserId="";
    private String post_key = "";
    private String item = "";
    private int amount = 0;
    private String time="";
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
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        This_month = findViewById(R.id.tv_this_month);
        recyclerView = findViewById(R.id.recyclerView);
        onlineUserId=mAuth.getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        TextView title = findViewById(R.id.txv_title);
        icon_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BudgetActivity.this,HomeActivity.class);
                startActivity(intent);
            }}
        );

        Calendar c = Calendar.getInstance();
        String[]monthName={"January","February","March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        String month=monthName[c.get(Calendar.MONTH)];
        This_month.setText(month);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalamount = 0;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Data data = snap.getValue(Data.class);

                        totalamount += data.getAmount();

                        String sttotal = String.valueOf("$ " + totalamount);

                        totalBudgetAmountTextView.setText(sttotal);
                    }
                }
                else {
                    String sttotal = String.valueOf("$ " + 0);
                    totalBudgetAmountTextView.setText(sttotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });

        getMonthTransportBudgetRatio();
        getMonthFoodBudgetRatio();
        getMonthHouseBudgetRatio();
        getMonthEntBudgetRatio();
        getMonthEduBudgetRatio();
        getMonthCharityBudgetRatio();
        getMonthAppBudgetRatio();
        getMonthHealthBudgetRatio();
        getMonthPerBudgetRatio();
        getMonthOtherBudgetRatio();
    }

    private void getMonthOtherBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayOtherRatio = pTotal / 30;
                    int weekOtherRatio = pTotal / 4;
                    int monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);
                } else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPerBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Personal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayPerRatio = pTotal / 30;
                    int weekPerRatio = pTotal / 4;
                    int monthPerRatio = pTotal;

                    personalRef.child("dayPerRatio").setValue(dayPerRatio);
                    personalRef.child("weekPerRatio").setValue(weekPerRatio);
                    personalRef.child("monthPerRatio").setValue(monthPerRatio);
                } else {
                    personalRef.child("dayPerRatio").setValue(0);
                    personalRef.child("weekPerRatio").setValue(0);
                    personalRef.child("monthPerRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHealthBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Health");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayHealthRatio = pTotal / 30;
                    int weekHealthRatio = pTotal / 4;
                    int monthHealthRatio = pTotal;

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio);
                } else {
                    personalRef.child("dayHealthRatio").setValue(0);
                    personalRef.child("weekHealthRatio").setValue(0);
                    personalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthAppBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Apparel");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayAppRatio = pTotal / 30;
                    int weekAppRatio = pTotal / 4;
                    int monthAppRatio = pTotal;

                    personalRef.child("dayAppRatio").setValue(dayAppRatio);
                    personalRef.child("weekAppRatio").setValue(weekAppRatio);
                    personalRef.child("monthAppRatio").setValue(monthAppRatio);
                } else {
                    personalRef.child("dayAppRatio").setValue(0);
                    personalRef.child("weekAppRatio").setValue(0);
                    personalRef.child("monthAppRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthCharityBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayCharRatio = pTotal / 30;
                    int weekCharRatio = pTotal / 4;
                    int monthCharRatio = pTotal;

                    personalRef.child("dayCharRatio").setValue(dayCharRatio);
                    personalRef.child("weekCharRatio").setValue(weekCharRatio);
                    personalRef.child("monthCharRatio").setValue(monthCharRatio);
                } else {
                    personalRef.child("dayCharRatio").setValue(0);
                    personalRef.child("weekCharRatio").setValue(0);
                    personalRef.child("monthCharRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEduBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayEduRatio = pTotal / 30;
                    int weekEduRatio = pTotal / 4;
                    int monthEduRatio = pTotal;

                    personalRef.child("dayEduRatio").setValue(dayEduRatio);
                    personalRef.child("weekEduRatio").setValue(weekEduRatio);
                    personalRef.child("monthEduRatio").setValue(monthEduRatio);
                } else {
                    personalRef.child("dayEduRatio").setValue(0);
                    personalRef.child("weekEduRatio").setValue(0);
                    personalRef.child("monthEduRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEntBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Entertainment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayEnRatio = pTotal / 30;
                    int weekEnRatio = pTotal / 4;
                    int monthEnRatio = pTotal;

                    personalRef.child("dayEnRatio").setValue(dayEnRatio);
                    personalRef.child("weekEnRatio").setValue(weekEnRatio);
                    personalRef.child("monthEnRatio").setValue(monthEnRatio);
                } else {
                    personalRef.child("dayEnRatio").setValue(0);
                    personalRef.child("weekEntRatio").setValue(0);
                    personalRef.child("monthEntRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHouseBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("House");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayHouseRatio = pTotal / 30;
                    int weekHouseRatio = pTotal / 4;
                    int monthHouseRatio = pTotal;

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio);
                } else {
                    personalRef.child("dayHouseRatio").setValue(0);
                    personalRef.child("weekHouseRatio").setValue(0);
                    personalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthFoodBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayFoodRatio = pTotal / 30;
                    int weekFoodRatio = pTotal / 4;
                    int monthFoodRatio = pTotal;

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio);
                } else {
                    personalRef.child("dayFoodRatio").setValue(0);
                    personalRef.child("weekFoodRatio").setValue(0);
                    personalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthTransportBudgetRatio() {
        Query query = budgetRef.orderByChild("item").equalTo("Transport");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }
                    int dayTransRatio = pTotal / 30;
                    int weekTransRatio = pTotal / 4;
                    int monthTransRatio = pTotal;

                    personalRef.child("dayTransRatio").setValue(dayTransRatio);
                    personalRef.child("weekTransRatio").setValue(weekTransRatio);
                    personalRef.child("monthTransRatio").setValue(monthTransRatio);
                } else {
                    personalRef.child("dayTransRatio").setValue(0);
                    personalRef.child("weekTransRatio").setValue(0);
                    personalRef.child("monthTransRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
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
        final EditText amount = myView.findViewById(R.id.amount);
        final ImageView save = myView.findViewById(R.id.save);
        final ImageView btn_close = myView.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();
                checkIn();
                if (TextUtils.isEmpty(budgetAmount)) {
                    amount.setError("Amount is required");
                    return;
                }
                if (budgetItem.equals("Chọn danh mục:"))
                    Toast.makeText(BudgetActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                else {
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemNday = budgetItem + date;
                    String itemNweek = budgetItem + weeks.getWeeks();
                    String itemNmonth = budgetItem + months.getMonths();

                    Data data = new Data(budgetItem, date, id, null, itemNday, itemNweek, itemNmonth, Integer.parseInt(budgetAmount), months.getMonths(), weeks.getWeeks());

                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BudgetActivity.this, "Budget item added successful", Toast.LENGTH_SHORT).show();
                                check(budgetItem);

                            } else {
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class)
                .build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount(model.getAmount() + "$");
                holder.setDate(" " + model.getDate());
                holder.setItemName(model.getItem());
                switch (model.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.food);
                        break;
                    case "House":
                        holder.imageView.setImageResource(R.drawable.house);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.entertainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.charity);
                        break;
                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.apparel);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.health);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.personal);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.other);
                        break;
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(holder.getAdapterPosition()).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        time=model.getDate();
                        updateData(model);
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.onDataChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            date = itemView.findViewById(R.id.date);
        }

        public void setItemName(String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate) {

            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }

    private void updateData(@NonNull Data model) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final TextView mDate = mView.findViewById(R.id.mDate);
        final ImageView mImage = mView.findViewById(R.id.mImage);
        switch (model.getItem()) {
            case "Transport":
                mImage.setImageResource(R.drawable.transport);
                break;
            case "Food":
                mImage.setImageResource(R.drawable.food);
                break;
            case "House":
                mImage.setImageResource(R.drawable.house);
                break;
            case "Entertainment":
                mImage.setImageResource(R.drawable.entertainment);
                break;
            case "Education":
                mImage.setImageResource(R.drawable.education);
                break;
            case "Charity":
                mImage.setImageResource(R.drawable.charity);
                break;
            case "Apparel":
                mImage.setImageResource(R.drawable.apparel);
                break;
            case "Health":
                mImage.setImageResource(R.drawable.health);
                break;
            case "Personal":
                mImage.setImageResource(R.drawable.personal);
                break;
            case "Other":
                mImage.setImageResource(R.drawable.other);
                break;
        }

        mItem.setText(item);
        mDate.setText(time);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        ImageView delBut = mView.findViewById(R.id.btnDelete);
        ImageView btnUpdate = mView.findViewById(R.id.btnUpdate);
        ImageView btn_close = mView.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mAmount.getText().toString()))
                {
                    mAmount.setError("Amount is required");
                    return;
                }
                amount = Integer.parseInt(mAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);


                String itemNday = item + date;
                String itemNweek = item + weeks.getWeeks();
                String itemNmonth = item + months.getMonths();
                checkIn();
                Data data = new Data(item, date, post_key, null, itemNday, itemNweek, itemNmonth, amount, months.getMonths(), weeks.getWeeks());
                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            check(item);
                            Toast.makeText(BudgetActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            check(item);
                            Toast.makeText(BudgetActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        dialog.show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount;
    }

    public int total;

    private int getMonthOtherBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount1;
    }

    public int totalPersonal;

    private int getMonthPersonalBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount2;
    }

    public int total2;

    private int getMonthHealthBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount3;
    }

    public int total3;

    private int getMonthApparelBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount4;
    }

    public int total4;

    private int getMonthCharityBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount5;
    }

    public int total5;

    private int getMonthEducationBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount6;
    }

    public int total6;

    private int getMonthEntertainmentBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount7;
    }

    public int total7;

    private int getMonthHouseBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount8;
    }

    public int total8;

    private int getMonthFoodBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return totalAmount9;
    }

    public int total9;

    private int getMonthTransportBudget() {
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
                Toast.makeText(BudgetActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return total9;
    }
    private void  checkIn()
    {
        if(getTotalWeekApparelExpenses()>getMonthApparelBudget())
        {

        }
        if(getTotalWeekCharityExpenses()>getMonthCharityBudget())
        {

        }
        if(getTotalWeekEntertainmentExpenses()>getMonthEntertainmentBudget())
        {

        }
        if(getTotalWeekEducationExpenses()>getMonthEducationBudget())
        {

        }
        if(getTotalWeekFoodExpenses()>getMonthFoodBudget())
        {

        }
        if(getTotalWeekHealthExpenses()>getMonthHealthBudget())
        {

        }
        if(getTotalWeekHouseExpenses()>getMonthHouseBudget())
        {

        }
        if(getTotalWeekPersonalExpenses()>getMonthPersonalBudget())
        {

        }
        if(getTotalWeekTransportExpenses()>getMonthTransportBudget())
        {

        }
        if(getTotalWeekOtherExpenses()>getMonthOtherBudget())
        {

        }
    }
    private void check(String Item)
    {
        if(Item.equals("Apparel")&&getTotalWeekApparelExpenses()>getMonthApparelBudget()&&getMonthApparelBudget()>0)
        {
            createNotificationApparel();
            CountNotification();
        }
        if(Item.equals("Charity")&&getTotalWeekCharityExpenses()>getMonthCharityBudget()&&getMonthCharityBudget()>0)
        {
            createNotificationCharity();
            CountNotification();
        }
        if(Item.equals("Entertainment")&&getTotalWeekEntertainmentExpenses()>getMonthEntertainmentBudget()&&getMonthEntertainmentBudget()>0)
        {
            createNotificationEntertainment();
            CountNotification();
        }
        if(Item.equals("Education")&&getTotalWeekEducationExpenses()>getMonthEducationBudget()&&getMonthEducationBudget()>0)
        {
            createNotificationEducation();
            CountNotification();
        }
        if(Item.equals("Food")&&getTotalWeekFoodExpenses()>getMonthFoodBudget()&&getMonthFoodBudget()>0)
        {
                    createNotificationFood();
            CountNotification();
        }
        if(Item.equals("Health")&&getTotalWeekHealthExpenses()>getMonthHealthBudget()&&getMonthHealthBudget()>0)
        {
            createNotificationHealth();
            CountNotification();
        }
        if(Item.equals("House")&&getTotalWeekHouseExpenses()>getMonthHouseBudget()&&getMonthHouseBudget()>0)
        {
            createNotificationHouse();
            CountNotification();
        }
        if(Item.equals("Personal")&&getTotalWeekPersonalExpenses()>getMonthPersonalBudget()&&getMonthPersonalBudget()>0)
        {
            createNotificationPersonal();
            CountNotification();
        }
        if(Item.equals("Transport")&&getTotalWeekTransportExpenses()>getMonthTransportBudget()&&getMonthTransportBudget()>0)
        {
            createNotificationTransport();
            CountNotification();
        }
        if(Item.equals("Other")&&getTotalWeekOtherExpenses()>getMonthOtherBudget()&&getMonthOtherBudget()>0)
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationApparelB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationCharityB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationEducationB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationEntertainmentB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationFoodB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationHealthB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationHouseB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationTransportB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationPersonalB");
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
        Intent notificationIntent =new Intent(BudgetActivity.this,NotificationActivity.class);
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
                .setTicker("NotificationOtherB");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m =NotificationManagerCompat.from(getApplicationContext());
        m.notify(new Random().nextInt(),builder.build());
    }
}