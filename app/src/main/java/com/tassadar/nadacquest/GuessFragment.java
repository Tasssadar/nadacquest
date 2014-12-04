package com.tassadar.nadacquest;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
        b = (Button)v.findViewById(R.id.next);
        b.setOnClickListener(this);

        m_correctNadacIdx = -1;
        for(int i = 0; i < Nadac.VALS_MAX; ++i)
            m_selectedValIdx[i] = -1;

        if(savedInstanceState != null) {
            m_correctNadacIdx = savedInstanceState.getInt("correctNadacIdx");
            m_correctValIdx = savedInstanceState.getIntArray("correctValIdx");
            m_selectedValIdx = savedInstanceState.getIntArray("selectedValIdx");
            for(int i = 0; i < Nadac.VALS_MAX; ++i) {
                getValsArray(i).clear();
                getValsArray(i).addAll(savedInstanceState.getStringArrayList(Nadac.valId(i)));
            }
        }

        if (m_db == null) {
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
        outState.putIntArray("correctValIdx", m_correctValIdx);
        outState.putInt("correctNadacIdx", m_correctNadacIdx);

        View v = getView();
        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            if(v != null) {
                m_selectedValIdx[i] = ((Spinner)v.findViewById(getValsSpinnerId(i)))
                        .getSelectedItemPosition();
            }
            outState.putStringArrayList(Nadac.valId(i), getValsArray(i));
        }

        outState.putIntArray("selectedValIdx", m_selectedValIdx);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNadacDBLoaded(NadacDB result) {
        m_db = result;

        if(m_db == null) {
            View v = getView().findViewById(R.id.progress);
            v.setVisibility(View.GONE);

            TextView t = (TextView) getView().findViewById(R.id.error_text);
            t.setVisibility(View.VISIBLE);
            t.setText("Nepodařilo se načíst nadáče :(");
            return;
        }

        if(m_correctNadacIdx == -1)
            loadRandomNadac();
        else
            loadDataToViews(getView());
    }

    private void loadRandomNadac() {
        ArrayList<Integer> selected = new ArrayList<Integer>();
        ArrayList<Nadac> nadaci = m_db.getNadace();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.ensureCapacity(nadaci.size());
        for(int i = 0; i < nadaci.size(); ++i)
            indexes.add(i);

        int chunkCnt = Math.min(nadaci.size(), 6);
        Random r = new Random();
        for(int i = 0; i < chunkCnt; ++i) {
            int n = r.nextInt(indexes.size());
            selected.add(indexes.get(n));
            indexes.remove(n);
        }

        indexes.clear();
        indexes = null;

        m_correctNadacIdx = selected.get(r.nextInt(selected.size()));
        final Nadac correctNadac = nadaci.get(m_correctNadacIdx);

        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            Collections.shuffle(selected);
            getValsArray(i).clear();
            for(int x = 0; x < selected.size(); ++x) {
                Nadac n = nadaci.get(selected.get(x));
                if(n.id == correctNadac.id)
                    m_correctValIdx[i] = x;
                getValsArray(i).add(n.valStr(i));
            }
        }

        loadDataToViews(getView());
    }

    private void loadDataToViews(View v) {
        final Nadac correctNadac = m_db.getNadace().get(m_correctNadacIdx);
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

        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(getValsArray(i));
            Spinner s = (Spinner) v.findViewById(getValsSpinnerId(i));
            s.setAdapter(adapter);
            if(m_selectedValIdx[i] != -1)
                s.setSelection(m_selectedValIdx[i]);
            s.setEnabled(true);
        }

        View it = v.findViewById(R.id.progress);
        it.setVisibility(View.GONE);

        it = v.findViewById(R.id.guess_scroll_view);
        it.setVisibility(View.VISIBLE);
        it = v.findViewById(R.id.guess_buttons);
        it.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> getValsArray(int type) {
        switch(type) {
            case Nadac.VAL_NAME: return m_nameVals;
            case Nadac.VAL_SCHOOL: return m_schoolVals;
            case Nadac.VAL_YEAR: return m_yearVals;
        }
        return null;
    }

    private static int getValsSpinnerId(int type) {
        switch(type) {
            case Nadac.VAL_NAME: return R.id.name;
            case Nadac.VAL_SCHOOL: return R.id.school;
            case Nadac.VAL_YEAR: return R.id.year;
        }
        return -1;
    }

    private static int getValsMarkId(int type) {
        switch(type) {
            case Nadac.VAL_NAME: return R.id.name_mark;
            case Nadac.VAL_SCHOOL: return R.id.school_mark;
            case Nadac.VAL_YEAR: return R.id.year_mark;
        }
        return -1;
    }

    private void setMark(int type, boolean correct) {
        ImageView v = (ImageView)getView().findViewById(getValsMarkId(type));
        v.setImageResource(correct ? R.drawable.ic_action_accept : R.drawable.ic_action_remove);
        v.setColorFilter(correct ? 0xFF007700 : Color.RED, PorterDuff.Mode.SRC_ATOP);
        v.setVisibility(View.VISIBLE);
    }

    private void hideMarks() {
        View v = getView();
        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            v.findViewById(getValsMarkId(i)).setVisibility(View.GONE);
        }
    }

    private void showInfoText(final String text) {
        TextView t = (TextView)getView().findViewById(R.id.info_text);
        if(text == null) {
            t.setVisibility(View.GONE);
            return;
        }

        t.setVisibility(View.VISIBLE);
        t.setText(text);
        final int id = ++m_currInfoTextId;

        //View v = getView().findViewById(R.id.wrong_gif);
        //v.setVisibility(View.VISIBLE);
        //((AnimationDrawable)v.getBackground()).start();

        t.postDelayed(new Runnable() {
            @Override
            public void run() {
                View v = getView();
                if(v == null || m_currInfoTextId != id)
                    return;
                TextView t = (TextView)v.findViewById(R.id.info_text);
                if(t == null)
                    return;
                t.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void showView(int id, boolean show) {
        View v = getView();
        if(v == null)
            return;
        v.findViewById(id).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_abort: {
                View v = getView();
                for(int i = 0; i < Nadac.VALS_MAX; ++i) {
                    Spinner s = (Spinner)v.findViewById(getValsSpinnerId(i));
                    s.setEnabled(false);
                    s.setSelection(m_correctValIdx[i], true);
                }

                hideMarks();
                showView(R.id.guess_buttons, false);
                showView(R.id.next_layout, true);
                showInfoText("Ale no táák :)\nTeď je vybrané správné řešení.");
                Stats.incStat(getActivity(), Stats.STAT_GAVE_UP);
                Stats.incStat(getActivity(), Stats.STAT_TOTAL);
                break;
            }
            case R.id.button_check:
            {
                View v = getView();
                boolean correct = true;
                for(int i = 0; i < Nadac.VALS_MAX; ++i) {
                    Spinner s = (Spinner)v.findViewById(getValsSpinnerId(i));
                    setMark(i, m_correctValIdx[i] == s.getSelectedItemPosition());
                    if(m_correctValIdx[i] != s.getSelectedItemPosition())
                        correct = false;
                }

                if(correct) {
                    showView(R.id.guess_buttons, false);
                    showView(R.id.next_layout, true);
                    showInfoText("Všechno správně, výborně!");
                    Stats.incStat(getActivity(), Stats.STAT_CORRECT);
                    Stats.incStat(getActivity(), Stats.STAT_TOTAL);
                } else {
                    Stats.incStat(getActivity(), Stats.STAT_WRONG);
                    showInfoText("Špatně!");
                }
                break;
            }
            case R.id.next:
            {
                hideMarks();
                showView(R.id.next_layout, false);
                loadRandomNadac();
                showView(R.id.guess_buttons, true);
                showInfoText("Načteno, můžeš hádat.");
                break;
            }
        }
    }

    private int[] m_correctValIdx = new int[Nadac.VALS_MAX];
    private int[] m_selectedValIdx = new int[Nadac.VALS_MAX];
    private int m_correctNadacIdx;
    private ArrayList<String> m_nameVals = new ArrayList<String>();
    private ArrayList<String> m_schoolVals = new ArrayList<String>();
    private ArrayList<String> m_yearVals = new ArrayList<String>();
    private NadacDB m_db;
    private int m_currInfoTextId;
}
