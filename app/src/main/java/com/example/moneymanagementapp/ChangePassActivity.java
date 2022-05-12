package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {

    private EditText edt_new_password, edt_old_password, edt_confirm_new_password;
    private Button btn_change_password;
    private ProgressDialog progressDialog;
    private ImageView tv_show;
    private ImageView tv_show1;
    private ImageView tv_show2;
    private Boolean test = false;
    private Boolean test1 = false;
    private Boolean test2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        tv_show = findViewById(R.id.tv_show);
        tv_show1 = findViewById(R.id.tv_show1);
        tv_show2 = findViewById(R.id.tv_show2);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_old_password = findViewById(R.id.edt_old_password);
        edt_confirm_new_password = findViewById(R.id.edt_confirm_new_password);
        btn_change_password = findViewById(R.id.btn_change_password);
        progressDialog = new ProgressDialog(this);

        tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (test) {
                        edt_old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        tv_show.setBackground(getDrawable(R.drawable.hidepass));
                    test = false;
                } else {
                    edt_old_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    tv_show.setBackground(getDrawable(R.drawable.showpass));
                    test = true;
                }
            }
        });
        tv_show1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (test1) {
                    edt_new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tv_show1.setBackground(getDrawable(R.drawable.hidepass));
                    test1 = false;
                } else {
                    edt_new_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    tv_show1.setBackground(getDrawable(R.drawable.showpass));
                    test1 = true;
                }
            }
        });
        tv_show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (test2) {
                    edt_confirm_new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tv_show2.setBackground(getDrawable(R.drawable.hidepass));
                    test2 = false;
                } else {
                    edt_confirm_new_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    tv_show2.setBackground(getDrawable(R.drawable.showpass));
                    test2 = true;
                }
            }
        });
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
        ImageView icon_arrow_back = findViewById(R.id.arrow_back);
        TextView title = findViewById(R.id.txv_title);

        icon_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChangePassActivity.this,ProfileActivity.class);
                startActivity(intent);
            }}
        );
        title.setText("Change Password");
    }

    private void onClickChangePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String strNewPassword = edt_new_password.getText().toString().trim();
        String strNewConfirm = edt_confirm_new_password.getText().toString().trim();
        String pass = edt_old_password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            edt_old_password.setError("Current password is required");
        } else {
            if (TextUtils.isEmpty(strNewPassword)) {
                edt_new_password.setError("New password is required");
            } else {
                if (TextUtils.isEmpty(strNewConfirm)) {
                    edt_confirm_new_password.setError("Please re-enter your password");
                } else {
                    if (!strNewPassword.equals(strNewConfirm)) {
                        Toast.makeText(this, "Password confirmation failed", Toast.LENGTH_SHORT).show();
                    } else {
                        if (strNewPassword.equals(pass)) {
                            Toast.makeText(this, "The new password is the same as the current password", Toast.LENGTH_SHORT).show();
                        } else {
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), pass);

                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });

                            progressDialog.show();
                            user.updatePassword(strNewPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePassActivity.this, "User password updated", Toast.LENGTH_SHORT).show();
                                                new AlertDialog.Builder(ChangePassActivity.this)
                                                        .setTitle("Money Management App")
                                                        .setMessage("Do you want to exit?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                FirebaseAuth.getInstance().signOut();
                                                                Intent intent=new Intent(ChangePassActivity.this,LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .setNegativeButton("No",null)
                                                        .show();
                                            }
                                            else {
                                                Toast.makeText(ChangePassActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }
                    }


                }
            }
        }

    }
}