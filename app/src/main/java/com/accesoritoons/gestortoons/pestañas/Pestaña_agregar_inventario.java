package com.accesoritoons.gestortoons.pestañas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.accesoritoons.gestortoons.surtir.Fragmento_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_carrito;

public class Pestaña_agregar_inventario extends FragmentStateAdapter {

    public Pestaña_agregar_inventario(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new Fragmento_carrito();
        }

        return new Fragmento_agregar_inventario();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}