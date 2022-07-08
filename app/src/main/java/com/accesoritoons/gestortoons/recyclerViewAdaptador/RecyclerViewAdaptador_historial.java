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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_historial extends RecyclerView.Adapter<RecyclerViewAdaptador_historial.MyViewHolder>{
    Context context;
    ArrayList<Modelo_historial> list;
    public RecyclerViewAdaptador_historial(ArrayList<Modelo_historial> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_historial_actividad.setText(list.get(position).getActividad());
        holder.textView_id_invisible.setText(list.get(position).getReferencia());
        holder.textView_historial_descripcion.setText(list.get(position).getDescripcion());
        holder.textView_historial_visibilidad.setText(list.get(position).getVisible());
        holder.textView_historial_usuario.setText(list.get(position).getUsuario());
        holder.textView_historial_usuario.setText(list.get(position).getUsuario());
        holder.textView_historial_fecha.setText(list.get(position).getFecha());
        holder.textView_historial_referencia.setText(list.get(position).getReferencia());
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Garantia)))holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.gris));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Surtido_independiente)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#46F1C423"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Devolucion_bodega)))holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.gris));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Devolucion_proveedor)))holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.gris));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Venta)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#A46BFF00"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Venta_bodega)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#A12AEF00"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Compra)))holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.purple_200));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Invetario)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#112DC1AB"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Perfiles)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#3403DAC5"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Pedido)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#1D3CDA03"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Pedido_enviado)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#103CDA03"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Pedido_solicitado)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#103CDA03"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Producto_no_recibido)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#40C12D2F"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Surtido)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#46F1C423"));
//        if(holder.textView_historial_actividad.getText().equals(context.getString(R.string.Pedido_eliminado)))holder.linearLayout_usuario.setBackgroundColor(Color.parseColor("#20878787"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_id_invisible, textView_historial_actividad, textView_historial_descripcion, textView_historial_referencia, textView_historial_visibilidad,textView_historial_usuario,textView_historial_fecha;
        LinearLayout linearLayout_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_id_invisible=(TextView)itemView.findViewById(R.id.textView_id_invisible);
            textView_historial_actividad=(TextView)itemView.findViewById(R.id.textView_historial_actividad);
            textView_historial_descripcion=(TextView)itemView.findViewById(R.id.textView_historial_descripcion);
            textView_historial_visibilidad=(TextView)itemView.findViewById(R.id.textView_historial_visibilidad);
            textView_historial_usuario=(TextView)itemView.findViewById(R.id.textView_historial_usuario);
            textView_historial_fecha=(TextView)itemView.findViewById(R.id.textView_historial_fecha);
            textView_historial_referencia=(TextView)itemView.findViewById(R.id.textView_historial_referencia);


            linearLayout_usuario=(LinearLayout)itemView.findViewById(R.id.layout_principal);



            linearLayout_usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //abrir fragemnt

                    if (textView_historial_actividad.getText().equals(context.getString(R.string.Venta))||textView_historial_actividad.getText().equals(context.getString(R.string.Devolucion))||textView_historial_actividad.getText().equals(context.getString(R.string.Garantia))||textView_historial_actividad.getText().equals(context.getString(R.string.Compra))||textView_historial_actividad.getText().equals(context.getString(R.string.Venta_bodega))||textView_historial_actividad.getText().equals(context.getString(R.string.Devolucion_bodega))||textView_historial_actividad.getText().equals(context.getString(R.string.Devolucion_proveedor))||textView_historial_actividad.getText().equals(context.getString(R.string.Venta_Diamante))){
                        Bundle bundle = new Bundle();
                        bundle.putString("id_factura", textView_historial_referencia.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragment_detalle_factura,bundle);
                    }

                    if (textView_historial_actividad.getText().equals(context.getString(R.string.Perfiles))){
                        Bundle bundle = new Bundle();
                        bundle.putString("id_usuario", textView_id_invisible.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragmento_nuevo_perfil,bundle);
                    }
                    if (textView_historial_actividad.getText().equals(context.getString(R.string.Invetario))){
                        Bundle bundle = new Bundle();
                        bundle.putString("id_producto", textView_id_invisible.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragmento_nuevo_producto,bundle);
                    }


                    if (textView_historial_actividad.getText().equals(context.getString(R.string.Surtido))){
                        Bundle bundle = new Bundle();
                        bundle.putString("id_vendedor", textView_historial_referencia.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragmento_inventario_vendedor,bundle);
                    }

//                    if (textView_historial_actividad.getText().equals(context.getString(R.string.Pedido_enviado))|| textView_historial_actividad.getText().equals(context.getString(R.string.Producto_no_recibido))|| textView_historial_actividad.getText().equals(context.getString(R.string.Pedido_solicitado))) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("nombre_usuario", textView_historial_usuario.getText().toString());
//                        bundle.putString("id_pedido", textView_id_invisible.getText().toString());
//                        bundle.putString("pedido_fecha", textView_historial_fecha.getText().toString());
//                        bundle.putString("referencia_vendedor", textView_historial_referencia.getText().toString());
//                        Navigation.findNavController(view).navigate(R.id.fragmento_pedido_enviado, bundle);
//
//
//                    }
                }
            });
        }
    }

}
