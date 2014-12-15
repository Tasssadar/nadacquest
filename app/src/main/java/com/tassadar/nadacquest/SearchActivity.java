package com.tassadar.nadacquest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, LoadNadaceTask.NadacDbListener, AdapterView.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_initialized = false;
        if (m_db == null) {
            new LoadNadaceTask(this).execute(this);
        } else {
            onNadacDBLoaded(m_db);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        m_searchItem = null;
        m_adapter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        m_searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(m_searchItem);
        searchView.setQueryHint("Zadej jméno...");
        searchView.setOnQueryTextListener(this);
        m_searchItem.setEnabled(false);

        if(m_db != null)
            onNadacDBLoaded(m_db);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(m_adapter != null)
            m_adapter.updateQuery(s);
        return true;
    }

    @Override
    public void onNadacDBLoaded(NadacDB result) {
        m_db = result;
        if((m_searchItem == null && m_db != null) || m_initialized)
            return;

        m_initialized = true;

        View v = findViewById(R.id.progress);
        v.setVisibility(View.GONE);

        if(result == null) {
            TextView t = (TextView)findViewById(R.id.error_text);
            t.setVisibility(View.VISIBLE);
            t.setText("Nepodařilo se načíst nadáče :(");
        } else {
            m_adapter = new SearchNadacAdapter(m_db);
            m_searchItem.setEnabled(true);
            m_searchItem.expandActionView();

            m_adapter = new SearchNadacAdapter(m_db);
            ListView l = (ListView)findViewById(R.id.nadac_list);
            l.setAdapter(m_adapter);
            l.setOnItemClickListener(this);

            m_adapter.updateQuery("");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int idx, long id) {
        NadacInfoDialog.newInstance((int)id).show(this.getSupportFragmentManager(), "nadacInfo");
    }

    private NadacDB m_db;
    private MenuItem m_searchItem;
    private SearchNadacAdapter m_adapter;
    private boolean m_initialized;
}
