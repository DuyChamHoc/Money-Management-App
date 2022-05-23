package com.example.moneymanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Button registerQn,registerBtn,btn_back;
    private EditText name,phone,email,password;
    private Button date;
    private String Date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        registerQn = findViewById(R.id.registerQn);
        registerBtn = findViewById(R.id.registerBtn);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phonenumber);
        date = findViewById(R.id.date);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        registerQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        int months = month + 1;
        String date="";
        if(dayOfMonth<10&&months<10)
        {
            date =  "0"+dayOfMonth + "-" + "0" + months + "-" + year;
        }
        else{
            if(dayOfMonth<10&&months>=10){
                date =  "0"+dayOfMonth + "-" +months + "-" + year;
            }
            else {
                if(dayOfMonth>10&&months<10){
                    date =  dayOfMonth + "-" + "0" +months + "-" + year;
                }
                else {
                    date =  dayOfMonth + "-" +  +months + "-" + year;
                }
            }
        }
        Date=date;
    }
    public void register(){
        String username=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String fullName=name.getText().toString().trim();
        String phonenumber=phone.getText().toString().trim();
        if(fullName.isEmpty()){
            name.setError(("Ful name is required !"));
            name.requestFocus();
            return;
        }
        if(phonenumber.isEmpty()){
            phone.setError(("Phone number is required !"));
            phone.requestFocus();
            return;
        }
        if(username.isEmpty()){
            email.setError(("Username is required !"));
            email.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            password.setError(("Password is required !"));
            password.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            email.setError("Please provide valid email");
            email.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(username,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user=new User(fullName,Date,username,phonenumber);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Add account successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, "Add account failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}