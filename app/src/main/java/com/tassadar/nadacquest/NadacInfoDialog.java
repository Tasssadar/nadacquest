package com.tassadar.nadacquest;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NadacInfoDialog extends DialogFragment {
    static NadacInfoDialog newInstance(int id) {
        NadacInfoDialog f = new NadacInfoDialog();

        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_nadac_info, container);
        NadacDB db = NadacDB.Get();
        int idx;
        if(db == null || (idx = db.getNadacIdxById(getArguments().getInt("id", -1))) == -1) {
            TextView t = (TextView)v.findViewById(R.id.error_text);
            t.setText("Nepodařilo se načíst nadáče :(");
            t.setVisibility(View.VISIBLE);
            return v;
        }

        final Nadac n = db.getNadac(idx);
        getDialog().setTitle(n.name);

        TextView t = (TextView)v.findViewById(R.id.birthday);
        t.setText(n.birthday);
        t = (TextView)v.findViewById(R.id.school);
        t.setText(String.format("%s (%d. ročník)", n.school, n.year));
        t = (TextView)v.findViewById(R.id.hobbies);
        t.setText(n.hobbies.isEmpty() ? "Žádné jsem o sobě neprozradil(a) :(" : n.hobbies);
        t = (TextView)v.findViewById(R.id.start_year);
        t.setText(String.format("%d (%s)", n.start_year, n.is_in_program ? "je nadáč" : "už není nadáč"));

        ImageView img = (ImageView)v.findViewById(R.id.photo);
        img.setImageBitmap(n.photo);
        return v;
    }

}
