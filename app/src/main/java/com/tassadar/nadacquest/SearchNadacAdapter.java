package com.tassadar.nadacquest;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchNadacAdapter extends BaseAdapter implements Comparator<Nadac> {
    public SearchNadacAdapter(NadacDB db) {
        m_db = db;
    }

    @Override
    public int getCount() {
        return m_currentSet.size();
    }

    @Override
    public Nadac getItem(int i) {
        return m_currentSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).id;
    }

    @Override
    public View getView(int idx, View view, ViewGroup parent) {
        if(view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opt_item, parent, false);
        }
        final Nadac n = m_currentSet.get(idx);
        ImageView img = (ImageView)view.findViewById(R.id.icon);
        img.setImageBitmap(n.photo);
        TextView text = (TextView)view.findViewById(R.id.text);

        if(m_lastQuery.isEmpty()) {
            text.setText(n.name);
        } else {
            final int start = n.normalizedName.indexOf(m_lastQuery);
            final int end = start + m_lastQuery.length();
            final String name = String.format("%s<b>%s</b>%s", n.name.substring(0, start), n.name.substring(start, end), n.name.substring(end, n.name.length()));
            text.setText(Html.fromHtml(name));
        }
        return view;
    }

    public void updateQuery(final String query) {
        if(query == null || query.isEmpty()) {
            m_lastQuery = "";
            m_currentSet = m_db.getNadace();
            Collections.sort(m_currentSet, this);
            notifyDataSetChanged();
            return;
        }

        final String normalizedQuery = Utils.normalizeName(query);
        if(normalizedQuery.equals(m_lastQuery))
            return;

        m_lastQuery = normalizedQuery;

        ArrayList<Nadac> list = new ArrayList<>();
        for(Nadac n : m_db.getNadace()) {
            if(n.normalizedName.contains(normalizedQuery)) {
                Utils.listAddSorted(list, n, this);
            }
        }

        m_currentSet = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int compare(final Nadac a, final Nadac b) {
        return a.name.compareTo(b.name);
    }

    private NadacDB m_db;
    private ArrayList<Nadac> m_currentSet = new ArrayList<>();
    private String m_lastQuery;
}
