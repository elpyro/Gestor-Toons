package com.accesoritoons.gestortoons;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseApp extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //PARA CREAR LA PERSISTENCIA DE DATOS https://www.youtube.com/watch?v=r6Rub6sMWqs
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
