package com.tassadar.nadacquest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class NadacCarouselAdapter extends FancyCoverFlowAdapter {
    private NadacDB m_db;
    private int m_itWidth;

    public NadacCarouselAdapter(Context ctx, NadacDB db) {
        m_db = db;
        m_itWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                ctx.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return m_db.getNadace().size();
    }
    @Override
    public Nadac getItem(int i) {
        return m_db.getNadac(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemWitdth() {
        return m_itWidth;
    }

    @Override
    public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
        ImageView imageView = null;
        if (reuseableView != null) {
            imageView = (ImageView) reuseableView;
        } else {
            imageView = new ImageView(viewGroup.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setMinimumHeight(m_itWidth);
            imageView.setMinimumWidth(m_itWidth);
            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(m_itWidth, m_itWidth));

        }

        imageView.setImageBitmap(m_db.getNadac(i).photo);
        return imageView;
    }
}
