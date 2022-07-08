package com.accesoritoons.gestortoons.recyclerViewAdaptador;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;

import java.util.ArrayList;


//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerView_mis_facturas extends RecyclerView.Adapter<RecyclerView_mis_facturas.MyViewHolder>{
    Context context;
    ArrayList<Modelo_factura_cliente> list;
    public RecyclerView_mis_facturas(ArrayList<Modelo_factura_cliente> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mis_facturas, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id.setText(list.get(position).getId());
        holder.textView_id_visible.setText(list.get(position).getId().substring(0,5));
        holder.textView_pedido_fecha.setText(list.get(position).getFecha());
        holder.texview_actividad.setText(list.get(position).getTipo());
        try{
        holder.textview_nombre_usuario.setText(list.get(position).getVendedor());
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_pedido_fecha,textView_id, textView_nombre, textView_id_visible,texview_actividad,textview_nombre_usuario;
        LinearLayout linearLayout_factura;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_pedido_fecha=(TextView)itemView.findViewById(R.id.textView_pedido_fecha);
            textView_id=(TextView)itemView.findViewById(R.id.textView_id_invisible);
            textView_id_visible=(TextView)itemView.findViewById(R.id.textView_id_visible);
            textView_nombre=(TextView) itemView.findViewById(R.id.textview_nombre_cliente);
            texview_actividad=(TextView)itemView.findViewById(R.id.texview_actividad);
            linearLayout_factura=(LinearLayout)itemView.findViewById(R.id.layout_principal) ;
            textview_nombre_usuario=(TextView)itemView.findViewById(R.id.textview_nombre_usuario);


            linearLayout_factura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("id_factura", textView_id.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.fragment_detalle_factura,bundle);
                }
            });
        }
    }

}
