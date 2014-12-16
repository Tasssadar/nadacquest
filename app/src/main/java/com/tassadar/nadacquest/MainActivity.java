package com.tassadar.nadacquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView v = (ListView)findViewById(R.id.opt_list);
        v.setAdapter(new OptListAdapter());
        v.setOnItemClickListener(this);

        if(savedInstanceState != null)
            m_welcomeShown = savedInstanceState.getBoolean("welcomeShown", false);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if(!m_welcomeShown && !p.getBoolean("welcomeScreenShown", false)) {
            m_welcomeShown = true;
            startActivityForResult(new Intent(this, WelcomeActivity.class), 0);
        } else {
            m_welcomeShown = false;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("welcomeShown", m_welcomeShown);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        m_welcomeShown = false;
        if(resCode != RESULT_OK) {
            finish();
        } else {
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
            e.putBoolean("welcomeScreenShown", true);
            e.apply();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int idx, long id) {
        Class<?> cls;
        switch(idx)
        {
            case OptListAdapter.OPT_GUESS: cls = GuessActivity.class; break;
            case OptListAdapter.OPT_STAT: cls = StatsActivity.class; break;
            case OptListAdapter.OPT_SEARCH: cls = SearchActivity.class; break;
            default:
                return;
        }
        startActivity(new Intent(this, cls));
    }

    private boolean m_welcomeShown;
}
