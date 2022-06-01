package com.example.moneymanagementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class guide extends AppCompatActivity {
    private LinearLayout facebook,instagram,twitter,group,guide,help;
    private ImageView arrow_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        facebook=findViewById(R.id.facebook);
        instagram=findViewById(R.id.instagram);
        twitter=findViewById(R.id.twitter);
        group=findViewById(R.id.group);
        guide=findViewById(R.id.guide);
        help=findViewById(R.id.help);
        arrow_back=findViewById(R.id.arrow_back);

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://www.facebook.com/UIT.Fanpage";
                Intent i =new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((url)));
                startActivity(i);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://www.instagram.com/uituniversity/";
                Intent i =new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((url)));
                startActivity(i);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://twitter.com/elonmusk?s=21&t=MNfCK1wOGzwYak7OpAUVvg\n";
                Intent i =new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((url)));
                startActivity(i);
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://www.facebook.com/groups/711837306156494";
                Intent i =new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((url)));
                startActivity(i);
            }
        });

        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(guide.this,screenguide.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(guide.this,help.class);
                startActivity(intent);
            }
        });

    }
}