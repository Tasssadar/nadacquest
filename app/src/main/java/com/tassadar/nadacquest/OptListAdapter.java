package com.tassadar.nadacquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OptListAdapter extends BaseAdapter {
    public static final int OPT_GUESS = 0;
    public static final int OPT_SEARCH = 1;
    public static final int OPT_STAT  = 2;
    public static final int OPT_COUNT = 3;

    private static final int ICONS[] = {
            R.drawable.ic_guess,
            R.drawable.ic_search_sect,
            R.drawable.ic_stat_sect
    };
    private static final String TEXTS[] = {
            "…hádat nadáče!",
            "…hledat nadáče!",
            "…statistiky!",
    };

    @Override
    public int getCount() {
        return OPT_COUNT;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if(view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opt_item, parent, false);
        }
        ImageView img = (ImageView)view.findViewById(R.id.icon);
        img.setImageResource(ICONS[i]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(TEXTS[i]);
        return view;
    }
}
