package com.accesoritoons.gestortoons.recyclerViewAdaptador;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.bodega.Fragment_crear_factura;
import com.accesoritoons.gestortoons.modelos.Modelo_cliente;

import java.util.ArrayList;

public class RecyclerViewAdaptador_clientes  extends RecyclerView.Adapter<RecyclerViewAdaptador_clientes.MyViewHolder>{
    Context context;
    ArrayList<Modelo_cliente> list;
    View vista;
    public RecyclerViewAdaptador_clientes(ArrayList<Modelo_cliente> list){
        this.list=list;
    }

    @NonNull
    @Override
    public RecyclerViewAdaptador_clientes.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clientes, parent,false);
        return new RecyclerViewAdaptador_clientes.MyViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdaptador_clientes.MyViewHolder holder, int position) {
        holder.textView_id.setText(list.get(position).getId());
        holder.textView_cliente.setText(list.get(position).getNombre());
        holder.textView_telefono.setText(list.get(position).getTelefono());
        holder.textView_documento.setText(list.get(position).getDocumento());
        holder.textView_direccion.setText(list.get(position).getDireccion());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView_id, textView_cliente, textView_telefono, textView_documento,textView_direccion;
        CardView CardView_cliente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_id=itemView.findViewById(R.id.textView_id);
            textView_cliente=itemView.findViewById(R.id.textView_cliente);
            textView_telefono=itemView.findViewById(R.id.textView_telefono);
            textView_documento=itemView.findViewById(R.id.textView_documento);
            textView_direccion=itemView.findViewById(R.id.textView_direccion);
            CardView_cliente=itemView.findViewById(R.id.CardView_cliente);

            CardView_cliente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment_crear_factura.nombreCliente=textView_cliente.getText().toString().trim();
                    Fragment_crear_factura.telefono=textView_telefono.getText().toString().trim();
                    Fragment_crear_factura.documento=textView_documento.getText().toString().trim();
                    Fragment_crear_factura.direccion=textView_direccion.getText().toString().trim();
                    Navigation.findNavController(vista).navigateUp();
                }
            });

            CardView_cliente.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id_cliente", textView_id.getText().toString().trim());
                    Navigation.findNavController(vista).navigate(R.id.fragmentoNuevoCliente, bundle);
                    return false;
                }
            });

        }
    }

}

