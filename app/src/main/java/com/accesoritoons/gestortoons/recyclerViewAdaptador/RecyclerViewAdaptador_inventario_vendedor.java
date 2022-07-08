package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.surtir.Fragmento_pedido_enviado;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_inventario_vendedor extends RecyclerView.Adapter<RecyclerViewAdaptador_inventario_vendedor.MyViewHolder>{
    Context context;
    ArrayList<Modelo_producto_facturacion_app_vendedor> list;
    public RecyclerViewAdaptador_inventario_vendedor(ArrayList<Modelo_producto_facturacion_app_vendedor> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventario_vendedor, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView_nombre_producto.setText(list.get(position).getNombre());
        holder.textView_referencia_pedido.setText(list.get(position).getId_pedido());
        holder.textView_cantidad.setText(list.get(position).getCantidad());
        holder.id_producto_pedido.setText(list.get(position).getId_producto_pedido());
        holder.textView_url_producto.setText(list.get(position).getUrl());
        holder.textView_costo.setText(list.get(position).getCosto());
        holder.TextView_referencia_vendedor.setText(list.get(position).getId_referencia_vendedor());
        holder.TextView_referencia_producto.setText(list.get(position).getId_referencia_producto());
        if(list.get(position).getCliente_mis_productos().equals("Accesory Toons")) {
            holder.layout_principal.setBackgroundColor(context.getColor(R.color.trasparente));
        }else {
            holder.layout_principal.setBackgroundColor(context.getColor(R.color.gris));
        }
        Picasso.with(context).load(list.get(position).getUrl()).into(holder.imageView_foto_producto);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre_producto,textView_referencia_pedido, textView_cantidad, id_producto_pedido,textView_url_producto, TextView_referencia_producto, TextView_referencia_vendedor, textView_costo;
        ImageView imageView_foto_producto;
        LinearLayout layout_principal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto=(TextView)itemView.findViewById(R.id.textView_nombre_producto);
            textView_referencia_pedido=(TextView)itemView.findViewById(R.id.textView_referencia_pedido);
            textView_cantidad=(TextView)itemView.findViewById(R.id.textView_cantidad);
            id_producto_pedido=(TextView)itemView.findViewById(R.id.id_producto_pedido);
            TextView_referencia_vendedor=(TextView)itemView.findViewById(R.id.TextView_referencia_vendedor);
            textView_url_producto=(TextView)itemView.findViewById(R.id.textView_url_producto);
            TextView_referencia_producto=(TextView)itemView.findViewById(R.id.TextView_referencia_producto);
            textView_costo=(TextView)itemView.findViewById(R.id.textView_costo);
            imageView_foto_producto=(ImageView)itemView.findViewById(R.id.imageView_foto_producto);
            layout_principal=(LinearLayout)itemView.findViewById(R.id.layout_principal);


//            checkBox_recibido.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragmento_pedido_enviado lista_productos_recibidos = new Fragmento_pedido_enviado();
//                    if(checkBox_recibido.isChecked()) {
//                        lista_productos_recibidos.lista_productos_recibidos.add(new Modelo_producto_facturacion_app_vendedor(textView_nombre_producto.getText().toString(), "", "", "", textView_cantidad.getText().toString(), "", id_producto_pedido.getText().toString(), TextView_referencia_producto.getText().toString(), "",TextView_referencia_vendedor.getText().toString()));
//                    }else {
//                        for (int x = 0; x < lista_productos_recibidos.lista_productos_recibidos.size(); x++) {
//                            Modelo_producto_facturacion_app_vendedor producto_recibido =  lista_productos_recibidos.lista_productos_recibidos.get(x);
//                            if (producto_recibido.getId_referencia_producto().equals(TextView_referencia_producto.getText().toString())) {
//                                lista_productos_recibidos.lista_productos_recibidos.remove(x);
//                                break;
//                            }
//                        }
//                    }
//                    if (lista_productos_recibidos.lista_productos_recibidos.size()>0)  {
//                        MainActivity.opcion_agregar_inventario.setVisible(true);
//                    }else{
//                        MainActivity.opcion_agregar_inventario.setVisible(false);
//                    }

//                }
//            });

            imageView_foto_producto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(textView_url_producto!=null){
                        Bundle bundle = new Bundle();
                        bundle.putString("url_imagen",textView_url_producto.getText().toString() );
                        Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                    }
                }
            });
        }
    }

}
