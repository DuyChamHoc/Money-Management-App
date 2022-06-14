package com.example.moneymanagementapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAdapter extends FragmentStateAdapter {
    private String[] title=new String[]{"Borrow","Lend"};
    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new borrow_tab();
            case 1:
                return  new lend_tab();
        }
        return new borrow_tab();
    }

    @Override
    public int getItemCount() {
        return title.length;
    }
}
