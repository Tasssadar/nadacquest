package com.tassadar.nadacquest;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;

public class GuessFragment extends Fragment implements LoadNadaceTask.NadacDbListener, View.OnClickListener {
    private static final int SAMPLE_SIZE = 5;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.guess_fragment_frame, container);
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
            m_correctNadacId = savedInstanceState.getInt("correctNadacId");
            m_correctValIdx = savedInstanceState.getIntArray("correctValIdx");
            m_selectedValIdx = savedInstanceState.getIntArray("selectedValIdx");
            for(int i = 0; i < Nadac.VALS_MAX; ++i) {
                getValsArray(i).clear();
                getValsArray(i).addAll(savedInstanceState.getStringArrayList(Nadac.valId(i)));
            }
            m_lastGuesses = savedInstanceState.getIntegerArrayList("lastGuesses");
            m_loaded = true;
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
        outState.putInt("correctNadacId", m_correctNadacId);

        View v = getView();
        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            if(v != null) {
                m_selectedValIdx[i] = ((Spinner)v.findViewById(getValsSpinnerId(i)))
                        .getSelectedItemPosition();
            }
            outState.putStringArrayList(Nadac.valId(i), getValsArray(i));
        }

        outState.putIntArray("selectedValIdx", m_selectedValIdx);
        outState.putIntegerArrayList("lastGuesses", m_lastGuesses);
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

        FancyCoverFlow f = (FancyCoverFlow)getView().findViewById(R.id.photo);
        f.setSpacing(0);
        f.setAdapter(new NadacCarouselAdapter(getActivity(), m_db));

        // DB has loaded again and has been shuffled, we need to look for the idx again
        if(m_correctNadacIdx != -1 && m_db.getNadac(m_correctNadacIdx).id != m_correctNadacId) {
            m_correctNadacIdx = m_db.getNadacIdxById(m_correctNadacId);
        }

        if(m_correctNadacIdx == -1)
            loadRandomNadac();
        else
            loadDataToViews(getView());
    }

    public <E> List<E> getRandomSample(List<E> list, int n, E req) {
        n = Math.min(n, list.size());
        HashSet<Integer> indexes = new HashSet<Integer>(n);
        indexes.add(list.indexOf(req));
        while (indexes.size() < n) {
            indexes.add(m_random.nextInt(list.size()));
        }
        List<E> sample = new ArrayList<E>(n);
        for (Integer i : indexes) {
            sample.add(list.get(i));
        }
        Collections.shuffle(sample, m_random);
        return sample;
    }

    private void loadRandomNadac() {
        ArrayList<Nadac> nadaci = m_db.getNadace();
        do {
            m_correctNadacIdx = m_random.nextInt(nadaci.size());
        } while(m_lastGuesses.contains(m_correctNadacIdx));

        m_lastGuesses.add(m_correctNadacIdx);
        while(m_lastGuesses.size() > 10)
            m_lastGuesses.remove(0);

        final Nadac correctNadac = nadaci.get(m_correctNadacIdx);
        m_correctNadacId = correctNadac.id;

        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            getValsArray(i).clear();

            switch(i)
            {
                case Nadac.VAL_NAME: {
                    List<Nadac> selected_names = getRandomSample(nadaci, SAMPLE_SIZE, correctNadac);
                    m_correctValIdx[i] = selected_names.indexOf(correctNadac);

                    for(Nadac n : selected_names)
                        getValsArray(i).add(n.name);
                    break;
                }
                case Nadac.VAL_SCHOOL: {
                    List<String> selected_schools = getRandomSample(m_db.getSchools(), SAMPLE_SIZE, correctNadac.school);
                    m_correctValIdx[i] = selected_schools.indexOf(correctNadac.school);
                    getValsArray(i).addAll(selected_schools);
                    break;
                }
                case Nadac.VAL_YEAR: {
                    for(int y = 1; y <= 5; ++y) {
                        if(y == correctNadac.year)
                            m_correctValIdx[i] = y-1;
                        getValsArray(i).add(String.valueOf(y));
                    }
                    break;
                }
            }
        }

        loadDataToViews(getView());
    }

    private void loadDataToViews(View v) {
        final Nadac correctNadac = m_db.getNadace().get(m_correctNadacIdx);
        FancyCoverFlow f = (FancyCoverFlow)getView().findViewById(R.id.photo);
        f.scrollToIndex(m_correctNadacIdx, !m_loaded);
        m_loaded = false;

        String hobbies = correctNadac.hobbies;
        if(hobbies.isEmpty())
            hobbies = "Nic jsem o sobě nenapsal(a) :(";
        ((TextView)v.findViewById(R.id.hobbies)).setText(hobbies);

        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.opt_layout);
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

    private boolean checkCorrect(View v) {
        boolean res = true;
        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            Spinner s = (Spinner)v.findViewById(getValsSpinnerId(i));
            setMark(i, m_correctValIdx[i] == s.getSelectedItemPosition());
            if(m_correctValIdx[i] != s.getSelectedItemPosition())
                res = false;
        }
        return res;
    }

    private void hideMarks() {
        View v = getView();
        for(int i = 0; i < Nadac.VALS_MAX; ++i) {
            v.findViewById(getValsMarkId(i)).setVisibility(View.INVISIBLE);
        }
    }

    private void showInfoText(final String text) {
        TextView t = (TextView)getView().findViewById(R.id.info_text);
        if(text == null) {
            t.setText("");
            t.setVisibility(View.GONE);
            return;
        }

        t.setText(text);
        t.setVisibility(View.VISIBLE);
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
                t.setText("");
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
                checkCorrect(v);

                for(int i = 0; i < Nadac.VALS_MAX; ++i) {
                    Spinner s = (Spinner)v.findViewById(getValsSpinnerId(i));
                    //s.setEnabled(false);
                    s.setSelection(m_correctValIdx[i], true);
                }

                showView(R.id.guess_buttons, false);
                showView(R.id.next_layout, true);

                /*v.findViewById(R.id.guess_scroll_view).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View v = getView();
                        if(v == null)
                            return;
                        ScrollView s = (ScrollView)v.findViewById(R.id.guess_scroll_view);
                        if(s == null)
                            return;
                        s.fullScroll(View.FOCUS_DOWN);
                    }
                }, 500);*/

                showInfoText("Ale no táák :)\nTeď je vybrané správné řešení.");
                Stats.incStat(getActivity(), Stats.STAT_GAVE_UP);
                Stats.incStat(getActivity(), Stats.STAT_TOTAL);
                break;
            }
            case R.id.button_check:
            {
                View v = getView();
                if(checkCorrect(v)) {
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
                //showInfoText("Načteno, můžeš hádat.");
                break;
            }
        }
    }

    private int[] m_correctValIdx = new int[Nadac.VALS_MAX];
    private int[] m_selectedValIdx = new int[Nadac.VALS_MAX];
    private int m_correctNadacIdx;
    private int m_correctNadacId;
    private ArrayList<String> m_nameVals = new ArrayList<String>();
    private ArrayList<String> m_schoolVals = new ArrayList<String>();
    private ArrayList<String> m_yearVals = new ArrayList<String>();
    private ArrayList<Integer> m_lastGuesses = new ArrayList<>();
    private NadacDB m_db;
    private int m_currInfoTextId;
    private Random m_random = new Random();
    private boolean m_loaded = false;
}
