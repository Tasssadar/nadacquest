package com.tassadar.nadacquest;

import android.graphics.Bitmap;

public class Nadac {
    public static final int VAL_NAME = 0;
    public static final int VAL_SCHOOL = 1;
    public static final int VAL_YEAR = 2;
    public static final int VALS_MAX = 3;

    public String valStr(int type) {
        switch(type) {
            case VAL_NAME: return name;
            case VAL_SCHOOL: return school;
            case VAL_YEAR: return String.valueOf(year);
        }
        return "";
    }

    public static String valId(int type) {
        switch(type) {
            case VAL_NAME: return "nameVal";
            case VAL_SCHOOL: return "schoolVal";
            case VAL_YEAR: return "yearVal";
        }
        return null;
    }

    public int id;
    public String name;
    public String school;
    public int year;
    public Bitmap photo;
}
