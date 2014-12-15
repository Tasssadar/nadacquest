package com.tassadar.nadacquest;

import android.content.Intent;
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
}
