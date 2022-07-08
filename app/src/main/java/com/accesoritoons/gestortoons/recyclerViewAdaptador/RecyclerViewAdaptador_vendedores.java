package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
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
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_vendedores extends RecyclerView.Adapter<RecyclerViewAdaptador_vendedores.MyViewHolder>{
    Context context;
    ArrayList<Modelo_usuario> list;
    public RecyclerViewAdaptador_vendedores(ArrayList<Modelo_usuario> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendedores, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id.setText(list.get(position).getId());
        holder.textView_tipo.setText(list.get(position).getTipo());
        holder.textView_direccion.setText(list.get(position).getDireccion());
        holder.textView_url_avatar.setText(list.get(position).getUrl_foto_usuario());

        //IMPLEMENTAR PICASSO PARA CARGAR LAS IMAGENES DIRECTAMENTE DE LA WEB
        Picasso.with(context).load(list.get(position).getUrl_foto_usuario()).into(holder.imagen_avatar);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_id,  textView_url_avatar, textView_direccion,textView_tipo;
        ImageView imagen_avatar;
        LinearLayout linearLayout_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_id=(TextView)itemView.findViewById(R.id.textView_id_invisible);
            textView_direccion=(TextView) itemView.findViewById(R.id.textView_direccion);
            textView_nombre=(TextView) itemView.findViewById(R.id.textview_nombre_usuario);
            textView_tipo=(TextView)itemView.findViewById(R.id.textView_tipo);
            imagen_avatar=(ImageView) itemView.findViewById(R.id.imagen_avatar);
            textView_url_avatar=(TextView)itemView.findViewById(R.id.textView_url_avatar);
            linearLayout_usuario=(LinearLayout)itemView.findViewById(R.id.layout_principal);

            imagen_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(textView_url_avatar!=null){
                        MainActivity.opcion_nuevo_producto.setVisible(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("url_imagen",textView_url_avatar.getText().toString() );
                        Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                    }
                }
            });

            linearLayout_usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //abrir fragemnt
                    MainActivity.id_vendedor=textView_id.getText().toString().trim();
                    MainActivity.tipo_vendedor=textView_tipo.getText().toString().trim();
                    if(textView_tipo.getText().toString().trim().equals(context.getString(R.string.Plata))){
                        MainActivity.tipo_vendedor=context.getString(R.string.Plata);
                    }else if(textView_tipo.getText().toString().trim().equals(context.getString(R.string.Oro))){
                        MainActivity.tipo_vendedor=context.getString(R.string.Oro);
                    }else if(textView_tipo.getText().toString().trim().equals(context.getString(R.string.Diamante))){
                        MainActivity.tipo_vendedor=context.getString(R.string.Diamante);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("id_usuario", textView_id.getText().toString().trim());
                    Navigation.findNavController(view).navigate(R.id.fragmento_informacion_vendedor,bundle);

                }
            });
        }
    }

}
