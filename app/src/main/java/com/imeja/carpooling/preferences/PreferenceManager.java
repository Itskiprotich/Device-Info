package com.imeja.carpooling.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    public static final String TITLE_DEED = "TitleDeed";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(TITLE_DEED, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void setGoing(String going, String siku, String viti, String hapa) {
        editor.putString("going", going);
        editor.putString("siku", siku);
        editor.putString("viti", viti);
        editor.putString("hapa", hapa);
        editor.commit();
    }

    public String getGoing() {
        return sharedpreferences.getString("going", "going");
    }

    public String getSiku() {
        return sharedpreferences.getString("siku", "siku");
    }

    public String getViti() {
        return sharedpreferences.getString("viti", "viti");
    }

    public String getHapa() {
        return sharedpreferences.getString("hapa", "hapa");
    }
}
