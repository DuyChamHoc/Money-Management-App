package com.example.moneymanagementapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

public class AccountActivity extends AppCompatActivity {

    private ImageView img_avatar,img_camera;
    private EditText edtFullName;
    private TextView tv_email;
    private String a[];
    private Uri muri;

    private Button btn_update,btn_cancel;
    private ProgressDialog progressDialog;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK)
                    {
                        Intent intent=result.getData();
                        if(intent==null){
                            return;
                        }
                        Uri uri=intent.getData();
                        setMuri(uri);
                        try {
                            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri) ;
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


        btn_update = findViewById(R.id.button_update);
        btn_cancel = findViewById(R.id.button_cancel);


        progressDialog=new ProgressDialog(this);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        String email=user.getEmail();
        String name = user.getDisplayName();
        tv_email.setText(email);
        edtFullName.setText(name);
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
        muri=photoUrl;

        initListener();
        showUserInformation();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this,ProfileActivity.class);
                startActivity(intent);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.show();
        if(user==null){
            return;
        }
        String strFullName=edtFullName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
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
                        Intent intent=new Intent(AccountActivity.this,ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void onClickRequestPermission() {
        openGallery();
    }

    public void setMuri(Uri muri) {
        this.muri = muri;
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

    private void showUserInformation(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        String name = user.getDisplayName();
        Uri photoUrl = user.getPhotoUrl();

        edtFullName.setText(name);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_person_24).into(img_avatar);
    }
}