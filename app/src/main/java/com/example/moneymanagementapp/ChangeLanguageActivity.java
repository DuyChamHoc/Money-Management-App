package com.example.moneymanagementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    Locale Mylocale;
    private Button vn, en,home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        vn= findViewById(R.id.btn_change_language_vn);
        vn.setOnClickListener(v -> OnChangeLanguageVN());
        en= findViewById(R.id.btn_change_language_en);
        en.setOnClickListener(v -> OnChangeLanguage());
        home= findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeLanguageActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private void OnChangeLanguage(){
        Mylocale = new Locale("en");
        Locale.setDefault(Mylocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = Mylocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
    private void OnChangeLanguageVN(){
        Mylocale = new Locale("vi");
        Locale.setDefault(Mylocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = Mylocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}