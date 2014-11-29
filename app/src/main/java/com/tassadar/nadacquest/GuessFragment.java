package com.tassadar.nadacquest;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GuessFragment extends Fragment implements LoadNadaceTask.NadacDbListener, View.OnClickListener {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.guess_fragment, container);
        Button b = (Button)v.findViewById(R.id.button_abort);
        b.setOnClickListener(this);
        b = (Button)v.findViewById(R.id.button_check);
        b.setOnClickListener(this);

        if(savedInstanceState != null) {
            m_selected = savedInstanceState.getIntegerArrayList("selected");
            m_correctNadacIdx = savedInstanceState.getInt("correctNadacIdx");
        }

        if(m_db == null) {
            new LoadNadaceTask(this).execute(getActivity());
        } else {
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNadacDBLoaded(m_db);
                }
            }, 1);
        }
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("selected", m_selected);
        outState.putInt("correctNadacIdx", m_correctNadacIdx);

    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNadacDBLoaded(NadacDB result) {
        m_db = result;

        View v = getView().findViewById(R.id.progress);
        v.setVisibility(View.GONE);

        if(m_db == null) {
            TextView t = (TextView) getView().findViewById(R.id.error_text);
            t.setVisibility(View.VISIBLE);
            t.setText("Nepodařilo se načíst nadáče :(");
            return;
        }

        if(m_selected.size() == 0)
            loadRandomNadac();
        else
            loadCurrentNadac();

        v = getView().findViewById(R.id.guess_scroll_view);
        v.setVisibility(View.VISIBLE);
        v = getView().findViewById(R.id.guess_buttons);
        v.setVisibility(View.VISIBLE);
    }

    private void loadRandomNadac() {
        m_selected.clear();
        ArrayList<Nadac> nadaci = m_db.getNadace();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.ensureCapacity(nadaci.size());
        for(int i = 0; i < nadaci.size(); ++i)
            indexes.add(i);

        int chunkCnt = Math.min(nadaci.size(), 6);
        Random r = new Random();
        for(int i = 0; i < chunkCnt; ++i) {
            int n = r.nextInt(indexes.size());
            m_selected.add(indexes.get(n));
            indexes.remove(n);
        }

        indexes.clear();
        indexes = null;

        m_correctNadacIdx = m_selected.get(r.nextInt(m_selected.size()));
        loadCurrentNadac();
    }

    private void loadCurrentNadac() {
        ArrayList<Nadac> nadaci = m_db.getNadace();
        final Nadac correctNadac = nadaci.get(m_correctNadacIdx);

        // lol, closures!
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap b = m_db.LoadPhoto(correctNadac);
                View v = getView();
                if(b != null && v != null)
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            View v = getView();
                            if(v == null)
                                return;

                            ImageView i = (ImageView)v.findViewById(R.id.photo);
                            i.setImageBitmap(b);
                        }
                    });
            }
        }).start();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item);
        for(int i = 0; i < m_selected.size(); ++i) {
            Nadac n = nadaci.get(m_selected.get(i));
            if(n.id == correctNadac.id)
                m_correctNameIdx = i;
            adapter.add(n.name);
        }
        Spinner s = (Spinner)getView().findViewById(R.id.name);
        s.setAdapter(adapter);

        Collections.shuffle(m_selected);

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item);
        for(int i = 0; i < m_selected.size(); ++i) {
            Nadac n = nadaci.get(m_selected.get(i));
            if(n.id == correctNadac.id)
                m_correctSchoolIdx = i;
            adapter.add(n.school);
        }
        s = (Spinner)getView().findViewById(R.id.school);
        s.setAdapter(adapter);

        Collections.shuffle(m_selected);

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item);
        for(int i = 0; i < m_selected.size(); ++i) {
            Nadac n = nadaci.get(m_selected.get(i));
            if(n.id == correctNadac.id)
                m_correctYearIdx = i;
            adapter.add(String.valueOf(n.year));
        }
        s = (Spinner)getView().findViewById(R.id.year);
        s.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_abort:
                break;
            case R.id.button_check:
                break;
        }
    }

    private int m_correctNameIdx;
    private int m_correctSchoolIdx;
    private int m_correctYearIdx;
    private int m_correctNadacIdx;
    private ArrayList<Integer> m_selected = new ArrayList<Integer>();
    private NadacDB m_db;
}
