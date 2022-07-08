package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;


import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.modelos.Modelo_recaudos_seleccionados;
import com.accesoritoons.gestortoons.surtir.Fragment_detalle_factura;
import com.accesoritoons.gestortoons.surtir.Fragment_lista_recaudos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_recaudado_cliente extends RecyclerView.Adapter<RecyclerViewAdaptador_recaudado_cliente.MyViewHolder>{
    Context context;
    int total_factura;
    ArrayList<Modelo_producto_facturacion_app_vendedor> list;
    public RecyclerViewAdaptador_recaudado_cliente(ArrayList<Modelo_producto_facturacion_app_vendedor> list){
        this.list=list;
    }

    public RecyclerViewAdaptador_recaudado_cliente() {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_recaudado, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int precio=0;
        int cantidad=0;
        int total=0;
        holder.textView_nombre_producto.setText(list.get(position).getNombre());
        holder.textView_cantidad.setText(list.get(position).getCantidad());
        holder.id_producto_pedido.setText(list.get(position).getId_producto_pedido());
        holder.textView_fecha.setText(list.get(position).getFecha_recaudo());
        holder.TextView_referencia_producto.setText(list.get(position).getId_referencia_producto());
        if(MainActivity.vista_recaudo==true){
            holder.textView_precio.setText(list.get(position).getCosto());
             precio= Integer.parseInt(list.get(position).getCosto());
             cantidad= Integer.parseInt(list.get(position).getCantidad());
             total=precio*cantidad;
        }else{
            holder.textView_precio.setText(list.get(position).getVenta());
             precio= Integer.parseInt(list.get(position).getVenta());
             cantidad= Integer.parseInt(list.get(position).getCantidad());
             total=precio*cantidad;
        }

        holder.textView_recaudo.setText(list.get(position).getRecaudo());

        holder.textView_referencia_pedido.setText(list.get(position).getId_pedido());


        holder.textView_total.setText(total+"");
        holder.textView_url_producto.setText(list.get(position).getUrl());
        Picasso.with(context).load(list.get(position).getUrl()).into(holder.imageView_foto_producto);

        if(list.get(position).getEstado().equals(context.getString(R.string.Venta))||list.get(position).getEstado().equals(context.getString(R.string.Compra))||list.get(position).getEstado().equals(context.getString(R.string.Venta_bodega))){
            holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.trasparente));
            holder.textView_precio.setVisibility(View.VISIBLE);
            holder.textView_total.setVisibility(View.VISIBLE);
            holder.textView_cantidad.setVisibility(View.VISIBLE);
            holder.textViewigual.setVisibility(View.VISIBLE);
            holder.textViewx.setVisibility(View.VISIBLE);
        }else{
            holder.linearLayout_usuario.setBackgroundColor(context.getColor(R.color.gris));
            holder.textView_precio.setText("0");
            holder.textView_precio.setVisibility(View.GONE);
            holder.textView_total.setVisibility(View.GONE);
            holder.textView_cantidad.setVisibility(View.GONE);
            holder.textViewigual.setVisibility(View.GONE);
            holder.textViewx.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_total,textView_precio, textView_cantidad, textView_nombre_producto,textView_recaudo,textView_url_producto,textView_referencia_pedido, id_producto_pedido,textViewigual,textViewx,TextView_referencia_producto, textView_fecha;

        ImageView imageView_foto_producto;
        ImageButton button_imageButton_eliminar;

        LinearLayout linearLayout_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewx=(TextView)itemView.findViewById(R.id.textViewx);
            textViewigual=(TextView)itemView.findViewById(R.id.textViewigual);
            textView_nombre_producto=(TextView)itemView.findViewById(R.id.textView_nombre_producto);
            textView_precio=(TextView)itemView.findViewById(R.id.textView_precio);
            textView_cantidad=(TextView)itemView.findViewById(R.id.textView_cantidad);
            textView_total=(TextView)itemView.findViewById(R.id.textView_total);
            textView_recaudo=(TextView)itemView.findViewById(R.id.textView_recaudo);
            textView_url_producto=(TextView)itemView.findViewById(R.id.textView_url_producto);
            imageView_foto_producto=(ImageView)itemView.findViewById(R.id.imageView_foto_producto);
            textView_referencia_pedido=(TextView)itemView.findViewById(R.id.textView_referencia_pedido);
            id_producto_pedido=(TextView)itemView.findViewById(R.id.id_producto_pedido);
            button_imageButton_eliminar=(ImageButton)itemView.findViewById(R.id.imageButton_eliminar);
            TextView_referencia_producto=(TextView)itemView.findViewById(R.id.TextView_referencia_producto);
            textView_fecha=(TextView)itemView.findViewById(R.id.textView_fecha);


            linearLayout_usuario=(LinearLayout)itemView.findViewById(R.id.layout_principal);

            //COLOCAR BOTON DE ELIMINAR
            if (Fragment_detalle_factura.textView_actividad!=null)if(Fragment_detalle_factura.textView_actividad.getText().equals(context.getString(R.string.Venta_bodega))||Fragment_detalle_factura.textView_actividad.getText().equals(context.getString(R.string.Compra))) button_imageButton_eliminar.setVisibility(View.VISIBLE);
            //COLOCAR CHECKLIST


            button_imageButton_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.Eliminar_producto));
                    builder.setMessage(textView_nombre_producto.getText()+" X"+textView_cantidad.getText());

                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Guardar_firebase eliminar =new Guardar_firebase();
                            boolean compra= false;
                            if (Fragment_detalle_factura.textView_actividad.getText().equals(context.getString(R.string.Compra))) compra=true;
                            eliminar.eliminar_producto_factura(TextView_referencia_producto.getText().toString(),id_producto_pedido.getText().toString(),textView_cantidad.getText().toString(), compra);
                            String descripcion, actividad;
                            if (compra==true){
                                actividad=context.getString(R.string.Devolucion_proveedor);
                                descripcion= context.getString(R.string.restar_inventario);
                            }else{
                                actividad=context.getString(R.string.Devolucion_bodega);
                                descripcion=context.getString(R.string.Agregar_inventario);
                            }

                            descripcion=descripcion+": "+"\n"+"x"+textView_cantidad.getText().toString()+" "+textView_nombre_producto.getText().toString();
                            eliminar.guardar_historial(textView_referencia_pedido.getText().toString(),actividad,MainActivity.Id_Usuario,descripcion,context);

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            });


            linearLayout_usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Fragment_detalle_factura.total_factura==null){
                        Bundle bundle = new Bundle();
                        bundle.putString("id_factura", textView_referencia_pedido.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.fragment_detalle_factura,bundle);
                        Fragment_lista_recaudos.Lista_productos_seleccionados.clear();
                        MainActivity.opcion_realizar_recaudo.setVisible(false);
                    }
                }
            });

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
        public  String calcular_total(){

            int precio=0;
            int cantidad=0;
            int total=0;

            for (int x = 0; x < Fragment_lista_recaudos.Lista_productos_seleccionados.size(); x++) {
                precio= Integer.parseInt( Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getPrecio());
                cantidad= Integer.parseInt( Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getCantidad());
                total=total+(precio*cantidad);
            }
            Fragment_lista_recaudos.textView_total_recaudos.setText(context.getString(R.string.Recaudos_pendientes)+": "+total+"");
            return total+"";
        }
    }

}
