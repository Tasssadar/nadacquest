package com.tassadar.nadacquest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NadacImageFragment extends Fragment {
    public static Fragment newInstance(Context context, int pos, float scale)
    {
        Bundle b = new Bundle();
        b.putInt("idx", pos);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, NadacImageFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                container.getContext().getResources().getDisplayMetrics());
        Nadac n = NadacDB.Get().getNadac(getArguments().getInt("idx"));
        NadacImageView v = new NadacImageView(container.getContext());
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setMaxWidth(w);
        v.setMaxHeight(w);
        v.setMinimumHeight(w);
        v.setMinimumWidth(w);
        v.setImageBitmap(n.photo);
        v.setScaleBoth(getArguments().getFloat("scale"));
        v.setId(R.id.photo);
        return v;
    }
}
