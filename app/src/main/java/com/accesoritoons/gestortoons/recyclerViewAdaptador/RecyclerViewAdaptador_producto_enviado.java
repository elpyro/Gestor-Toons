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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;

import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.surtir.Fragmento_informacion_vendedor;
import com.accesoritoons.gestortoons.surtir.Fragmento_pedido_enviado;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_producto_enviado extends RecyclerView.Adapter<RecyclerViewAdaptador_producto_enviado.MyViewHolder>{
    Context context;
    Fragmento_pedido_enviado lista_productos_recibidos = new Fragmento_pedido_enviado();
    ArrayList<Modelo_producto_facturacion_app_vendedor> list;
    public RecyclerViewAdaptador_producto_enviado(ArrayList<Modelo_producto_facturacion_app_vendedor> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidos_enviados, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView_nombre_producto.setText(list.get(position).getNombre());
        holder.textView_referencia_pedido.setText(list.get(position).getId_pedido());
        holder.textView_cantidad.setText(list.get(position).getCantidad());
        holder.id_producto_pedido.setText(list.get(position).getId_producto_pedido());
        holder.textView_url_producto.setText(list.get(position).getUrl());
        holder.TextView_referencia_vendedor.setText(list.get(position).getId_referencia_vendedor());
        holder.TextView_referencia_producto.setText(list.get(position).getId_referencia_producto());
        Picasso.with(context).load(list.get(position).getUrl()).into(holder.imageView_foto_producto);
        if(list.get(position).getEstado().equals(context.getString(R.string.Recibido))){
            holder.checkBox_recibido.setChecked(true);
            holder.checkBox_recibido.setEnabled(false);
        }


        for (int x = 0; x < lista_productos_recibidos.lista_productos_recibidos.size(); x++) {
            Modelo_producto_facturacion_app_vendedor producto_recibido =  lista_productos_recibidos.lista_productos_recibidos.get(x);
            if (producto_recibido.getId_referencia_producto().equals(list.get(position).getId_referencia_producto())) {
                holder.checkBox_recibido.setChecked(true);
                holder.imageButton_eliminar.setVisibility(View.VISIBLE);
                break;
            }else{
                holder.imageButton_eliminar.setVisibility(View.GONE);
                holder.checkBox_recibido.setChecked(false);
            }
        }

        if(MainActivity.Perfil.equals(context.getString(R.string.Vendedor))){
            holder.imageView_foto_producto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre_producto,textView_referencia_pedido, textView_cantidad, id_producto_pedido,textView_url_producto, TextView_referencia_producto, TextView_referencia_vendedor;
        ImageView imageView_foto_producto;
        CheckBox checkBox_recibido;
        ImageButton imageButton_eliminar;
        LinearLayout linearLayout_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto=(TextView)itemView.findViewById(R.id.textView_nombre_producto);
            textView_referencia_pedido=(TextView)itemView.findViewById(R.id.textView_referencia_pedido);
            textView_cantidad=(TextView)itemView.findViewById(R.id.textView_cantidad);
            id_producto_pedido=(TextView)itemView.findViewById(R.id.id_producto_pedido);
            checkBox_recibido=(CheckBox)itemView.findViewById(R.id.checkBox_recibido);
            TextView_referencia_vendedor=(TextView)itemView.findViewById(R.id.TextView_referencia_vendedor);
            textView_url_producto=(TextView)itemView.findViewById(R.id.textView_url_producto);
            TextView_referencia_producto=(TextView)itemView.findViewById(R.id.TextView_referencia_producto);
            imageView_foto_producto=(ImageView)itemView.findViewById(R.id.imageView_foto_producto);
            linearLayout_usuario=(LinearLayout)itemView.findViewById(R.id.layout_principal);
            imageButton_eliminar=(ImageButton)itemView.findViewById(R.id.imageButton_eliminar);


            if(!MainActivity.Perfil.equals("Administrador"))checkBox_recibido.setVisibility(View.GONE); else checkBox_recibido.setVisibility(View.VISIBLE);

//            linearLayout_usuario.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    if (textView_cantidad.getText().toString().equals("1"))return false;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("url",textView_url_producto.getText().toString() );
//                    bundle.putString("nombre",textView_nombre_producto.getText().toString() );
//                    bundle.putString("cantidad",textView_cantidad.getText().toString() );
//                    bundle.putString("id_producto_pedido",id_producto_pedido.getText().toString() );
//                    Navigation.findNavController(view).navigate(R.id.fragmento_cambiar_cantidades,bundle);
//
//                    return false;
//                }
//            });

            imageButton_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.Eliminar_producto));
        builder.setMessage(textView_nombre_producto.getText()+" X"+textView_cantidad.getText());

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Guardar_firebase eliminar =new Guardar_firebase();
                //eliminar del array
                for (int x = 0; x < lista_productos_recibidos.lista_productos_recibidos.size(); x++) {
                    Modelo_producto_facturacion_app_vendedor producto_recibido = lista_productos_recibidos.lista_productos_recibidos.get(x);
                    if (producto_recibido.getId_referencia_producto().equals(TextView_referencia_producto.getText().toString())) {
                        lista_productos_recibidos.lista_productos_recibidos.remove(x);
                        break;
                    }
                }
                eliminar.eliminar_producto_pedido(id_producto_pedido.getText().toString(),textView_referencia_pedido.getText().toString(),TextView_referencia_producto.getText().toString(),textView_cantidad.getText().toString());
                eliminar.guardar_historial("",context.getString(R.string.Pedido_eliminado),context.getString(R.string.Administrador), Fragmento_informacion_vendedor.nombre_vendedor +": "+textView_nombre_producto.getText()+" X"+textView_cantidad.getText(),context);
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

            checkBox_recibido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkBox_recibido.isChecked()) {

                        if(!MainActivity.Perfil.equals(context.getString(R.string.Vendedor))){
                            imageButton_eliminar.setVisibility(View.VISIBLE);
                        }

                        lista_productos_recibidos.lista_productos_recibidos.add(new Modelo_producto_facturacion_app_vendedor(textView_nombre_producto.getText().toString(), "", "", "", textView_cantidad.getText().toString(), "", id_producto_pedido.getText().toString(), TextView_referencia_producto.getText().toString(), "",TextView_referencia_vendedor.getText().toString(),TextView_referencia_vendedor.getText().toString()+"-"+TextView_referencia_producto.getText().toString(),"","","","","",MainActivity.Usuario,"","","",""));
                    }else {
                        imageButton_eliminar.setVisibility(View.GONE);
                        for (int x = 0; x < lista_productos_recibidos.lista_productos_recibidos.size(); x++) {
                            Modelo_producto_facturacion_app_vendedor producto_recibido =  lista_productos_recibidos.lista_productos_recibidos.get(x);
                            if (producto_recibido.getId_referencia_producto().equals(TextView_referencia_producto.getText().toString())) {
                                lista_productos_recibidos.lista_productos_recibidos.remove(x);
                                break;
                            }
                        }
                    }
//                    if (lista_productos_recibidos.lista_productos_recibidos.size()>0)  {
//                        MainActivity.opcion_agregar_inventario.setVisible(true);
//                    }else{
//                        MainActivity.opcion_agregar_inventario.setVisible(false);
//                    }

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
    }



}
