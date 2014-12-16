package com.tassadar.nadacquest;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();

        final ViewPager p = (ViewPager)findViewById(R.id.pager);
        p.setAdapter(new WelcomePagerAdapter());
        p.setOnPageChangeListener(this);

        m_maxIdx = p.getAdapter().getCount() - 1;
        setResult(RESULT_CANCELED);
    }

    public void nextClicked(View btn) {
        final ViewPager p = (ViewPager)findViewById(R.id.pager);

        final int curIdx = p.getCurrentItem();
        if(curIdx == m_maxIdx) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        p.setCurrentItem(curIdx+1, true);
    }

    @Override
    public void onPageSelected(int position) {
        Button b = (Button)findViewById(R.id.next);
        b.setText(position == m_maxIdx ? "DO APLIKACE!" : "DALŠÍ");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private int m_maxIdx;
}
