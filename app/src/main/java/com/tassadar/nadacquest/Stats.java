package com.tassadar.nadacquest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Stats {
    public static final int STAT_CORRECT = 0;
    public static final int STAT_WRONG   = 1;
    public static final int STAT_GAVE_UP = 2;
    public static final int STAT_TOTAL   = 3;
    public static final int STAT_CNT     = 4;

    public static void loadStats(Context ctx) {
        m_stats = new int[STAT_CNT];
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(ctx);
        for(int i = 0; i < STAT_CNT; ++i) {
            m_stats[i] = p.getInt("stat" + i, 0);
        }
    }

    public static void saveStat(Context ctx, int type) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        if(type != -1) {
            e.putInt("stat" + type, m_stats[type]);
        } else {
            for(int i = 0; i < STAT_CNT; ++i)
                e.putInt("stat" + i, m_stats[i]);
        }
        e.apply();
    }

    public static int getStat(Context ctx, int type) {
        if(type < 0 || type >= STAT_CNT)
            return -1;

        if(m_stats == null)
            loadStats(ctx);

        return m_stats[type];
    }

    public static void incStat(Context ctx, int type) {
        if(type < 0 || type >= STAT_CNT)
            return;

        if(m_stats == null)
            loadStats(ctx);

        m_stats[type] += 1;
        saveStat(ctx, type);
    }

    public static void reset(Context ctx) {
        if(m_stats == null)
            m_stats = new int[STAT_CNT];

        for(int i = 0; i < STAT_CNT; ++i)
            m_stats[i] = 0;
        saveStat(ctx, -1);
    }

    private static int m_stats[] = null;
}
