package com.example.moneymanagementapp;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.BitSet;

public class ProfileActivity extends AppCompatActivity {

    private TextView tv_name, logoutBtn, tv_account, tv_changepass,tv_setting,tv_help,tv_guide,tv_loan;
    private ImageView img_avatar;
    private BottomNavigationView bottomNavigationView;
    GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutBtn = findViewById(R.id.logoutBtn);
        tv_name = findViewById(R.id.tv_name);
        tv_loan=findViewById(R.id.tv_loan);
        img_avatar = findViewById(R.id.img_avatar);
        tv_account = findViewById(R.id.tv_account);
        tv_changepass = findViewById(R.id.tv_changepass);
        tv_setting = findViewById(R.id.tv_setting);
        tv_help=findViewById(R.id.tv_help);
        tv_guide=findViewById(R.id.tv_guide);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setBackground(null);

        mAuth=FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_history:
                        startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.action_home:
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.action_profile:
                        return true;
                    case R.id.action_chart:
                        startActivity(new Intent(ProfileActivity.this, ChooseAnalyticActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        tv_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        tv_changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });
        tv_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LoanActivity.class);
                startActivity(intent);
            }
        });
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,help.class);
                startActivity(intent);
            }
        });
        tv_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,guide.class);
                startActivity(intent);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Money Management App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        showUserInformation();
    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        if(user.getDisplayName()==null||user.getDisplayName().equals("")){
            Uri photoUrl = user.getPhotoUrl();
            budgetRef.child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        tv_name.setText(String.valueOf(task.getResult().getValue()));
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "return data false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
        }
        else{
            Uri photoUrl = user.getPhotoUrl();
            tv_name.setText(user.getDisplayName());
            Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);}
    }
}

