package com.example.moneymanagementapp;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link lend_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class lend_tab extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressBar progressBar;
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
    private LendAdapter lendAdapter;
    private List<DataLoan> myDataList;
    public lend_tab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment lend_tab.
     */
    // TODO: Rename and change types and number of parameters
    public static lend_tab newInstance(String param1, String param2) {
        lend_tab fragment = new lend_tab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_lend_tab, container, false);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("Lend").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(getContext());

        totalBudgetAmountTextView = (TextView) view.findViewById(R.id.totalLend);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewloan);
        onlineUserId=mAuth.getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myDataList = new ArrayList<>();
        lendAdapter=new LendAdapter(getContext(),myDataList);
        recyclerView.setAdapter(lendAdapter);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        readItems();
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
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });
        return view;
    }
    private void readItems() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Lend").child(onlineUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    DataLoan data=dataSnapshot.getValue(DataLoan.class);
                    myDataList.add(data);
                }
                lendAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        totalBudgetAmountTextView.setText("$ " + totalAmount);
                    }
                }
                else {
                    totalAmount=0;
                    totalBudgetAmountTextView.setText("$ " + totalAmount);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myView = inflater.inflate(R.layout.input_layout_loan, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText companion=myView.findViewById(R.id.companionName);
        final EditText amount = myView.findViewById(R.id.amount);
        final ImageView save = myView.findViewById(R.id.btnSave);
        final EditText note=myView.findViewById(R.id.note);
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
                if (TextUtils.isEmpty(budgetAmount)) {
                    amount.setError("Amount is required");
                    return;
                }
                String notes = note.getText().toString();
                String companionname = companion.getText().toString();
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

                String itemNday = date;

                DataLoan data = new DataLoan(companionname, itemNday, id, notes, Integer.parseInt(budgetAmount), Integer.parseInt(budgetAmount));

                budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Budget item added successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });
            }
        });
        dialog.dismiss();
        dialog.show();
    }
}