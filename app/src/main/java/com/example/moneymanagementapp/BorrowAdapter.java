package com.example.moneymanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {
    private Context mContext;
    private List<DataLoan> myDataList;
    private String post_key = "";
    private String companion = "";
    private String note = "";
    private int amount = 0;
    private int moneyLeft=0;
    public BorrowAdapter(Context mContext, List<DataLoan> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.retrieve_loan,parent,false);
        return new BorrowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DataLoan data=myDataList.get(position);
        holder.companion.setText(" "+data.getCompanion());
        holder.amount.setText(data.getAmount()+"$");
        holder.date.setText(" "+data.getDate());
        //holder.notes.setText(" "+data.getNotes());
        holder.moneyLeft.setText(" "+data.getMoneyLeft());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key=data.getId();
                companion=data.getCompanion();
                amount=data.getAmount();
                note=data.getNotes();
                moneyLeft=data.getMoneyLeft();
                updateData(data);
            }
        });
    }
    private void updateData(DataLoan data) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_loan, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final EditText mCompanion = mView.findViewById(R.id.companionName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);
        final ImageView btn_close = mView.findViewById(R.id.btn_close);
        final ImageView mImage = mView.findViewById(R.id.mImage);
        final TextView mDate = mView.findViewById(R.id.mDate);
        mCompanion.setText(companion);
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
                moneyLeft=moneyLeft-Integer.parseInt(mAmount.getText().toString());
                note=mNotes.getText().toString();
                DateFormat dateFormat =new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal=Calendar.getInstance();
                String date=dateFormat.format(cal.getTime());
                MutableDateTime epoch=new MutableDateTime();
                epoch.setDate(0);
                DataLoan data=new DataLoan(companion,date,post_key,note,amount,moneyLeft);

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Borrow").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Borrow").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView companion, amount, date, notes,moneyLeft;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companion = itemView.findViewById(R.id.companion);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            notes = itemView.findViewById(R.id.note);
            moneyLeft=itemView.findViewById(R.id.moneyLeft);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

