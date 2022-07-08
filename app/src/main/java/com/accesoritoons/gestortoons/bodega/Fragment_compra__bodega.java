package com.accesoritoons.gestortoons.bodega;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accesoritoons.gestortoons.R;


public class Fragment_compra__bodega extends Fragment {
    View vista;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_compra__bodega, container, false);

        return vista;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }
}