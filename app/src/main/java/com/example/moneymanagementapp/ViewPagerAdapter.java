package com.example.moneymanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    int images[] = {

            R.drawable.b,
            R.drawable.c,
            R.drawable.a,

    };

    int headings[] = {

            R.string.heading_one,
            R.string.heading_two,
            R.string.heading_three,
    };

    int description[] = {

            R.string.desc_one,
            R.string.desc_two,
            R.string.desc_three,
    };
    int description1[] = {

            R.string.desc_one1,
            R.string.desc_two1,
            R.string.desc_three1,
    };
    int description2[] = {

            R.string.desc_one2,
            R.string.desc_two2,
            R.string.desc_three2,
    };
    int description3[] = {

            R.string.desc_one3,
            R.string.desc_two3,
            R.string.desc_three3,
    };

    public ViewPagerAdapter(Context context){

        this.context = context;

    }

    @Override
    public int getCount() {
        return  headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);

        ImageView slidetitleimage = (ImageView) view.findViewById(R.id.titleImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.texttitle);
        TextView slideDesciption = (TextView) view.findViewById(R.id.textdeccription);
        TextView slideDesciption1 = (TextView) view.findViewById(R.id.textdeccription1);
        TextView slideDesciption2 = (TextView) view.findViewById(R.id.textdeccription2);
        TextView slideDesciption3 = (TextView) view.findViewById(R.id.textdeccription3);

        slidetitleimage.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDesciption.setText(description[position]);
        slideDesciption1.setText(description1[position]);
        slideDesciption2.setText(description2[position]);
        slideDesciption3.setText(description3[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);

    }
}
