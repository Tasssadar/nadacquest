package com.tassadar.nadacquest;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WelcomePagerAdapter extends PagerAdapter {
    private static final int[] layouts = {
            R.layout.welcome_page1,
            R.layout.welcome_page2,
            R.layout.welcome_page3,
            R.layout.welcome_page4,
    };

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int idx) {
        View v = LayoutInflater.from(container.getContext()).inflate(layouts[idx], container, false);
        container.addView(v, 0);
        return v;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
