package com.example.moneymanagementapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private ImageView img_avatar, img_camera;
    private TextInputEditText edtFullName, phonenumber, birthday, tv_email;
    private String a[];
    private Uri muri;
    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;
    private Button btn_update, btn_cancel;
    private ProgressDialog progressDialog;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }
                        Uri uri = intent.getData();
                        setMuri(uri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            img_avatar.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        img_avatar = findViewById(R.id.img_avatar);
        img_camera = findViewById(R.id.img_camera);
        edtFullName = findViewById(R.id.edtFullName);
        tv_email = findViewById(R.id.tv_email);
        phonenumber = findViewById(R.id.phonenumber);
        birthday = findViewById(R.id.date);
        btn_update = findViewById(R.id.button_update);
        btn_cancel = findViewById(R.id.button_cancel);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        budgetRef.child("birthday").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    birthday.setText(String.valueOf(task.getResult().getValue()));
                } else {
                    Toast.makeText(AccountActivity.this, "return data false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        budgetRef.child("phone").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    phonenumber.setText(String.valueOf(task.getResult().getValue()));
                } else {
                    Toast.makeText(AccountActivity.this, "return data false", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String email = user.getEmail();
        if (user.getDisplayName() == null || user.getDisplayName().equals("")) {
            budgetRef.child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        edtFullName.setText(String.valueOf(task.getResult().getValue()));
                    } else {
                        Toast.makeText(AccountActivity.this, "return data false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            budgetRef.child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        tv_email.setText(String.valueOf(task.getResult().getValue()));
                    } else {
                        Toast.makeText(AccountActivity.this, "return data false", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
            muri = photoUrl;
            showUserInformation1();
        } else {
            tv_email.setText(email);
            edtFullName.setText(user.getDisplayName());
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
            muri = photoUrl;
            showUserInformation();
        }
        initListener();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initListener() {
        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
    }


    private void onClickUpdateProfile() {
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        if (user.getDisplayName() == null || user.getDisplayName().equals("")) {
            budgetRef.child("fullname").setValue(edtFullName.getText().toString().trim());
            budgetRef.child("phone").setValue(phonenumber.getText().toString().trim());
            budgetRef.child("birthday").setValue(birthday.getText().toString().trim());
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("")
                    .setPhotoUri(muri)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountActivity.this, "Update profile success", Toast.LENGTH_SHORT).show();
                                showUserInformation1();
                            }
                            Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            String strFullName = edtFullName.getText().toString().trim();
            String phone = phonenumber.getText().toString().trim();
            String date = birthday.getText().toString().trim();
            User user1=new User(strFullName,date,user.getEmail(),phone);
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user1);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("")
                    .setPhotoUri(muri)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountActivity.this, "Update profile success", Toast.LENGTH_SHORT).show();
                                showUserInformation();
                            }
                            Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    });
        }

    }

    private void onClickRequestPermission() {
        openGallery();
    }

    public void setMuri(Uri muri) {
        this.muri = muri;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        Uri photoUrl = user.getPhotoUrl();
        edtFullName.setText(user.getDisplayName());
        Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
    }

    private void showUserInformation1() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
    }
}