package com.tassadar.nadacquest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        Bundle args = getArguments();
        if (container == null || getActivity() == null || args == null || !args.containsKey("idx")) {
            return null;
        }
        int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                container.getContext().getResources().getDisplayMetrics());
        Nadac n = NadacDB.Get().getNadac(getArguments().getInt("idx"));
        NadacImageView v = new NadacImageView(getActivity());
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setMaxWidth(w);
        v.setMaxHeight(w);
        v.setMinimumHeight(w);
        v.setMinimumWidth(w);
        v.setImageBitmap(n.photo);
        v.setScaleBoth(getArguments().getFloat("scale", 1.f));
        v.setId(R.id.photo);
        return v;
    }
}
