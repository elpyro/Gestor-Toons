package com.accesoritoons.gestortoons;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.preference.PreferenceFragmentCompat;

public class Fragment_configuracion extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getContext());
        Preferencias_app.obtener_preferencias(preferencias,getContext());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getContext());
        Preferencias_app.obtener_preferencias(preferencias,getContext());
    }
}