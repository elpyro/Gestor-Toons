package com.accesoritoons.gestortoons;
//https://www.youtube.com/watch?v=l9M_DSdeNXc

import android.content.Context;
import android.content.SharedPreferences;


public class Preferencias_app {


    public static String informacion_superior;



    public static void obtener_preferencias(SharedPreferences preferences, Context context){
        try{
            informacion_superior=preferences.getString("informacion_superior","");
        }catch (Exception e){

        }



    }
}
