package com.accesoritoons.gestortoons.ViewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.R;

public class ProductosViewHoler extends RecyclerView.ViewHolder {
    public TextView textview_producto;

    public ProductosViewHoler(@NonNull View itemView) {
        super(itemView);
        textview_producto=itemView.findViewById(R.id.textview_producto);





    }
}
