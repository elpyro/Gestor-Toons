package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import androidx.annotation.NonNull;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_productos extends RecyclerView.Adapter<RecyclerViewAdaptador_productos.MyViewHolder>{
    Context context;
    ArrayList<Modelo_producto> list;
    public RecyclerViewAdaptador_productos(ArrayList<Modelo_producto> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventario, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id.setText(list.get(position).getId());
        holder.textView_compra.setText(list.get(position).getP_compra());
        holder.textView_platino.setText(list.get(position).getP_plantino());
        holder.textView_oro.setText(list.get(position).getP_oro());
        holder.textView_cantidad.setText(list.get(position).getCantidad());
        holder.textView_diamante.setText(list.get(position).getP_diamante());
        holder.textView_venta.setText(list.get(position).getP_detal());
        holder.textView_url_producto.setText(list.get(position).getUrl());
        holder.textview_cliente_mis_productos.setText((list.get(position).getCliente_mis_productos()));
        if(list.get(position).getEstado().equals("false")){
            holder.linearLayout_producto.setBackgroundColor(Color.parseColor("#A8757575"));
        }else{
            holder.linearLayout_producto.setBackgroundColor(context.getColor(R.color.trasparente));
        }

        //IMPLEMENTAR PICASSO PARA CARGAR LAS IMAGENES DIRECTAMENTE DE LA WEB
        Picasso.with(context).load(list.get(position).getUrl()).into(holder.imagen_producto);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_compra, textView_platino, textView_oro, textView_diamante, textView_id, textView_venta, textView_url_producto,textView_cantidad,textview_cliente_mis_productos;
        ImageView imagen_producto;
        LinearLayout linearLayout_producto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_id=(TextView)itemView.findViewById(R.id.textView_id);
            textView_compra=(TextView)itemView.findViewById(R.id.textView_costo);
            textView_venta=(TextView)itemView.findViewById(R.id.textView_venta);
            textView_platino=(TextView)itemView.findViewById(R.id.textView_platino);
            textView_oro=(TextView)itemView.findViewById(R.id.textView_oro);
            textView_diamante=(TextView)itemView.findViewById(R.id.textView_diamante);
            textView_cantidad=(TextView)itemView.findViewById(R.id.textView_cantidad);
            textView_nombre=(TextView) itemView.findViewById(R.id.textview_producto);
            textview_cliente_mis_productos=(TextView)itemView.findViewById(R.id.textview_cliente_mis_productos);
            imagen_producto=(ImageView) itemView.findViewById(R.id.imageView_foto_producto);
            textView_url_producto=(TextView)itemView.findViewById(R.id.textView_url_producto);
            linearLayout_producto=(LinearLayout)itemView.findViewById(R.id.layout_principal);


            imagen_producto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(textView_url_producto!=null){
                        MainActivity.opcion_nuevo_producto.setVisible(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("url_imagen",textView_url_producto.getText().toString() );
                        Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                    }
                }
            });

            linearLayout_producto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //abrir fragemnt
                    MainActivity.opcion_nuevo_producto.setVisible(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("id_producto", textView_id.getText().toString().trim());
                    Navigation.findNavController(view).navigate(R.id.fragmento_nuevo_producto,bundle);

                }
            });
        }
    }

}
