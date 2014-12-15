package com.tassadar.nadacquest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class StatsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.reset_stats:
                Stats.reset(this);
                loadStats();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadStats() {
        final int total = Stats.getStat(this, Stats.STAT_TOTAL);
        TextView t = (TextView)findViewById(R.id.stat_total);
        t.setText(String.format("%d", total));
        t = (TextView)findViewById(R.id.stat_correct);
        t.setText(String.format("%d (%.1f%%)", Stats.getStat(this, Stats.STAT_CORRECT),
                total == 0 ? 0.f : (((float)Stats.getStat(this, Stats.STAT_CORRECT))/total)*100));
        t = (TextView)findViewById(R.id.stat_wrong);
        t.setText(String.format("%d", Stats.getStat(this, Stats.STAT_WRONG)));
        t = (TextView)findViewById(R.id.stat_gave_up);
        t.setText(String.format("%d (%.1f%%)", Stats.getStat(this, Stats.STAT_GAVE_UP),
                total == 0 ? 0.f : (((float)Stats.getStat(this, Stats.STAT_GAVE_UP))/total)*100));
    }
}
