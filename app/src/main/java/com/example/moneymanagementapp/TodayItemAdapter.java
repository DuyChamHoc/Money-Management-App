package com.example.moneymanagementapp;


import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.moneymanagementapp.NotificationActivity.CountNotification;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.ViewHolder>{

    private Context mContext;
    private List<Data> myDataList;
    private String post_key="";
    private String item="";
    private String note="";
    private int amount=0;
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
    private String onlineUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    public TodayItemAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.retrieve1,parent,false);
        return new TodayItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Data data=myDataList.get(position);

        holder.item.setText(data.getItem());
        holder.amount.setText(data.getAmount()+"$");
        holder.date.setText(" "+data.getDate());
        holder.notes.setText(" "+data.getNotes());
        switch (data.getItem()){
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key=data.getId();
                item=data.getItem();
                amount=data.getAmount();
                note=data.getNotes();
                updateData(data);
            }
        });
    }

    private void updateData(Data data) {
        AlertDialog.Builder myDialog=new AlertDialog.Builder(mContext);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View mView=inflater.inflate(R.layout.update_layout1,null);
        myDialog.setView(mView);
        final AlertDialog dialog=myDialog.create();
        final TextView mItem=mView.findViewById(R.id.itemName);
        final EditText mAmount =mView.findViewById(R.id.amount);
        final EditText mNotes=mView.findViewById(R.id.note);
        final ImageView btn_close=mView.findViewById(R.id.btn_close);
        final ImageView mImage=mView.findViewById(R.id.mImage);
        final TextView mDate=mView.findViewById(R.id.mDate);

        switch (data.getItem()) {
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
        mDate.setText(" "+data.getDate());

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNotes.setText(note);
        mNotes.setSelection(note.length());

        ImageView delBut=mView.findViewById(R.id.btnDelete);
        ImageView btnUpdate=mView.findViewById(R.id.btnUpdate);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
       checkIn();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIn();
                if(TextUtils.isEmpty(mAmount.getText().toString()))
                {
                    mAmount.setError("Amount is required");
                    return;
                }
                amount=Integer.parseInt(mAmount.getText().toString());
                note=mNotes.getText().toString();

                DateFormat dateFormat =new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal=Calendar.getInstance();
                String date=dateFormat.format(cal.getTime());

                MutableDateTime epoch=new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks=Weeks.weeksBetween(epoch,now);
                Months months=Months.monthsBetween(epoch,now);


                String itemNday=item+date;
                String itemNweek=item+weeks.getWeeks();
                String itemNmonth=item+months.getMonths();

                Data data=new Data(item,date,post_key,note,itemNday,itemNweek,itemNmonth,amount,months.getMonths(),weeks.getWeeks());

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            check(item);
                            Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkIn();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            check(item);
                            Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item,amount,date,notes;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.item);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
            notes=itemView.findViewById(R.id.note);
            imageView=itemView.findViewById(R.id.imageView);
        }
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idApparel)
                .setSmallIcon(R.drawable.apparel)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.apparel))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idCharity)
                .setSmallIcon(R.drawable.charity)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.charity))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idEducation)
                .setSmallIcon(R.drawable.education)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.education))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idEntertainment)
                .setSmallIcon(R.drawable.entertainment)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.entertainment))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idFood)
                .setSmallIcon(R.drawable.food)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.food))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idHealth)
                .setSmallIcon(R.drawable.health)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.health))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idHouse)
                .setSmallIcon(R.drawable.house)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.house))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idTransport)
                .setSmallIcon(R.drawable.transport)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.transport))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idPersonal)
                .setSmallIcon(R.drawable.personal)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.personal))
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
        NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        Intent notificationIntent =new Intent(mContext,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =PendingIntent.getActivity(mContext,0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,idOther)
                .setSmallIcon(R.drawable.other)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.other))
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
