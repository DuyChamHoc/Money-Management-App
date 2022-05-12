package com.example.moneymanagementapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChooseAnalyticActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private CardView todayCardView,weekCardView,monthCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytic);

        todayCardView=findViewById(R.id.todayCardView);
        weekCardView=findViewById(R.id.weekCardView);
        monthCardView=findViewById(R.id.monthCardView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.action_chart);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_history:
                        startActivity(new Intent(ChooseAnalyticActivity.this,HistoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_home:
                        startActivity(new Intent(ChooseAnalyticActivity.this,HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_profile:
                        startActivity(new Intent(ChooseAnalyticActivity.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_chart:
                        return true;

                }
                return false;
            }
        });


        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseAnalyticActivity.this,DailyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseAnalyticActivity.this,WeeklyAnalyticActivity.class);
                startActivity(intent);
            }
        });
        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseAnalyticActivity.this,MonthlyAnalyticActivity.class);
                startActivity(intent);
            }
        });
    }
}