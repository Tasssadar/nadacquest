package com.tassadar.nadacquest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static String extractAsset(Context ctx, String name) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String path = ctx.getCacheDir() + "/" + name;
        File f = new File(path);

        if(!BuildConfig.DEBUG && f.exists() && pref.getInt(name + "_ver", 0) == BuildConfig.VERSION_CODE) {
            return path;
        }

        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = ctx.getAssets().open(name);
            out = new FileOutputStream(path);
            byte[] buff = new byte[4096];
            for(int len; (len = in.read(buff)) != -1; )
                out.write(buff, 0, len);

            f.setReadable(true, false);
            pref.edit().putInt(name + "_ver", BuildConfig.VERSION_CODE).apply();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(in);
            close(out);
        }
    }

    public static void close(Closeable c) {
        if(c == null)
            return;
        try {
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
