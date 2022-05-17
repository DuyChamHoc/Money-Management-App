package com.example.moneymanagementapp;


import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.ViewHolder>{

    private Context mContext;
    private List<Data> myDataList;
    private String post_key="";
    private String item="";
    private String note="";
    private int amount=0;

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
        holder.date.setText("On "+data.getDate());
        holder.notes.setText("Notes: "+data.getNotes());
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
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
}
