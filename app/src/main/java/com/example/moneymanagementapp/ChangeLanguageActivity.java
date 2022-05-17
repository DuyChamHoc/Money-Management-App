package com.example.moneymanagementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    Locale Mylocale;
    private LinearLayout vn, en;
    public String languageToLoad="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        vn= findViewById(R.id.btn_change_language_vn);
        vn.setOnClickListener(v -> OnChangeLanguageVN());
        en= findViewById(R.id.btn_change_language_en);
        en.setOnClickListener(v -> OnChangeLanguage());
    }
    private void OnChangeLanguage(){
        languageToLoad = "en";
        Mylocale = new Locale("en");
        Locale.setDefault(Mylocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = Mylocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(ChangeLanguageActivity.this, SettingActivity.class);
        startActivity(intent);
    }
    private void OnChangeLanguageVN(){
        languageToLoad = "vi";
        Mylocale = new Locale("vi");
        Locale.setDefault(Mylocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = Mylocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(ChangeLanguageActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences mypref = getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = mypref.edit();
        editor.putString("language",languageToLoad);
        editor.commit();
    }
}