package com.tassadar.nadacquest;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

public class LoadNadaceTask extends AsyncTask<Context, Void, NadacDB> {
    public interface NadacDbListener {
        void onNadacDBLoaded(NadacDB result);
    }

    public LoadNadaceTask(NadacDbListener listener) {
        super();
        m_listener = listener;
    }

    @Override
    protected NadacDB doInBackground(Context... ctx) {
        NadacDB db = NadacDB.Get();
        if(db == null) {
            db = NadacDB.Open(ctx[0]);
            if(db == null)
                return null;

            if(!db.LoadAll()) {
                NadacDB.Destroy();
                return null;
            }
        }
        return db;
    }

    protected void onPostExecute(NadacDB result) {
        m_listener.onNadacDBLoaded(result);
    }

    private NadacDbListener m_listener;
}
