package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.surtir.Fragmento_informacion_vendedor;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_pedidos extends RecyclerView.Adapter<RecyclerViewAdaptador_pedidos.MyViewHolder>{
    Context context;
    ArrayList<Modelo_pedido> list;
    public RecyclerViewAdaptador_pedidos(ArrayList<Modelo_pedido> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidos, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_referencia_pedido.setText(list.get(position).getId_pedido());
        holder.textView_pedido_fecha.setText(list.get(position).getFecha());
        holder.textView_usuario.setText(list.get(position).getUsuario());
        holder.textView_referencia_usuario.setText(list.get(position).getReferencia_vendedor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_pedido_fecha,textView_referencia_pedido, textView_usuario,textView_referencia_usuario;
        LinearLayout linearLayout_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_pedido_fecha=(TextView)itemView.findViewById(R.id.textView_pedido_fecha);
            textView_referencia_pedido=(TextView)itemView.findViewById(R.id.textView_referencia_pedido);
            textView_usuario=(TextView)itemView.findViewById(R.id.textView_usuario);
            linearLayout_usuario=(LinearLayout)itemView.findViewById(R.id.layout_principal);
            textView_referencia_usuario=(TextView)itemView.findViewById(R.id.textView_referencia_usuario);

            linearLayout_usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        MainActivity.opcion_crear_pedido.setVisible(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("nombre_usuario", textView_usuario.getText().toString());
                        bundle.putString("id_pedido", textView_referencia_pedido.getText().toString());
                        bundle.putString("pedido_fecha", textView_pedido_fecha.getText().toString());
                        bundle.putString("referencia_vendedor",textView_referencia_usuario.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragmento_pedido_enviado,bundle);
                }
            });
        }
    }

}
