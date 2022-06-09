package com.example.moneymanagementapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoanActivity extends AppCompatActivity {
    VPAdapter vpAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private String[] title=new String[]{"Borrow","Lend"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
        viewPager=findViewById(R.id.viewpager);
        tabLayout=findViewById(R.id.tab_layout);
        vpAdapter=new VPAdapter(this);
        viewPager.setAdapter(vpAdapter);
        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(title[position]))).attach();
    }
}