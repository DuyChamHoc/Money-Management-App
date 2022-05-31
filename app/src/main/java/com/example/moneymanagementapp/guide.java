package com.example.moneymanagementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


public class guide extends AppCompatActivity {
    private LinearLayout facebook,instagram,twitter,group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        facebook=findViewById(R.id.facebook);
        instagram=findViewById(R.id.instagram);
        twitter=findViewById(R.id.twitter);
        group=findViewById(R.id.group);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://www.facebook.com/UIT.Fanpage";
                Intent i =new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((url)));
                startActivity(i);
            }
        });
    }
}