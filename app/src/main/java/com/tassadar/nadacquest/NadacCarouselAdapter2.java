package com.tassadar.nadacquest;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class NadacCarouselAdapter2 extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {
    private Context context;
    private FragmentManager fm;
    private NadacDB m_db;
    private int m_id;

    public NadacCarouselAdapter2(Context context, FragmentManager fm, NadacDB db, int id) {
        super(fm);
        this.fm = fm;
        this.context = context;
        m_db = db;
        m_id = id;
    }

    @Override
    public Fragment getItem(int position)
    {
        final float scale = position == 0 ? NadacImageView.BIG_SCALE : NadacImageView.SMALL_SCALE;
        return NadacImageFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount()
    {
        return m_db.getNadace().size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels)
    {
        if (positionOffset < 0f || positionOffset > 1f)
            return;

        Fragment f = fm.findFragmentByTag(this.getFragmentTag(position));
        if(f != null) {
            View v = f.getView();
            if(v != null) {
                NadacImageView cur = (NadacImageView) v.findViewById(R.id.photo);
                if(cur != null)
                    cur.setScaleBoth(NadacImageView.BIG_SCALE
                            - NadacImageView.DIFF_SCALE * positionOffset);
            }
        }

        f = fm.findFragmentByTag(this.getFragmentTag(position+1));
        if(f != null) {
            View v = f.getView();
            if(v != null) {
                NadacImageView  next = (NadacImageView) v.findViewById(R.id.photo);
                if(next != null)
                    next.setScaleBoth(NadacImageView.SMALL_SCALE
                            + NadacImageView.DIFF_SCALE * positionOffset);
            }
        }
    }
    @Override
    public void onPageSelected(int position) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {}

    private String getFragmentTag(int position)
    {
        return "android:switcher:" + m_id + ":" + position;
    }
}
