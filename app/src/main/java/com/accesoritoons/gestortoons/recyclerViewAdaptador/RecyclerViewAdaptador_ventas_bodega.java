package com.accesoritoons.gestortoons.recyclerViewAdaptador;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.bodega.Fragment_crear_factura;
import com.accesoritoons.gestortoons.bodega.Fragment_venta_bodega_mayor;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.surtir.Fragmento_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_carrito;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class RecyclerViewAdaptador_ventas_bodega extends RecyclerView.Adapter<RecyclerViewAdaptador_ventas_bodega.MyViewHolder>{
    Context context;
    ArrayList<Modelo_producto> list;

    private  final String PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA = "PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA";


    public RecyclerViewAdaptador_ventas_bodega(ArrayList<Modelo_producto> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agregar_inventario, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id.setText(list.get(position).getId());
        holder.textView_cantidad.setText(list.get(position).getCantidad());
        holder.textView_cantidad_original_invisible.setText(list.get(position).getCantidad());
        holder.textView_Pcompra.setText((list.get(position).getP_compra()));

        if(list.get(position).getEstado().equals("false")) {
            holder.linearLayout_producto.setBackgroundColor(Color.parseColor("#A8757575"));
        }else{
            holder.linearLayout_producto.setBackgroundColor(context.getColor(R.color.trasparente));
        }

        //costo diamante para ventas al mayor en bodega
        holder.textView_costo.setText(list.get(position).getP_diamante());



        holder.textView_url_producto.setText(list.get(position).getUrl());
        //IMPLEMENTAR PICASSO PARA CARGAR LAS IMAGENES DIRECTAMENTE DE LA WEB
        Picasso.with(context).load(list.get(position).getUrl()).into(holder.imagen_producto);


        // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
        boolean existe=false;
        for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {
            Modelo_producto productos =  MainActivity.lista_seleccion_venta_mayor_bodega.get(x);
            if (productos.getId().equals(list.get(position).getId())) {
                existe=true;
                holder.editText_cantidad.setText(MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getSeleccion());
                break;
            }
        }

        if(existe==true){
            holder.button_disminuir.setVisibility(View.VISIBLE);
            holder.linearLayout_producto.setBackgroundColor(Color.parseColor("#A8003335"));
        }else{
            holder.button_disminuir.setVisibility(View.GONE);
            holder.editText_cantidad.setText("");
            holder.linearLayout_producto.setBackgroundColor(context.getColor(R.color.trasparente));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


   public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView textView_nombre,  textView_id,  textView_url_producto,textView_cantidad_original_invisible,textView_cantidad, textView_categoria, textView_Pcompra;
        EditText editText_cantidad,textView_costo;
        ImageView imagen_producto;
        Button button_disminuir;
        private LinearLayout linearLayout_producto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_cantidad_original_invisible=(TextView)itemView.findViewById(R.id.textView_cantidad_original_invisible);
            textView_cantidad=(TextView)itemView.findViewById(R.id.textView_cantidad);
            textView_id=(TextView)itemView.findViewById(R.id.textView_id_invisible);
            textView_costo=(EditText) itemView.findViewById(R.id.textView_costo);
            editText_cantidad=(EditText) itemView.findViewById(R.id.textView_seleccion);
            textView_nombre=(TextView) itemView.findViewById(R.id.textview_producto);
            imagen_producto=(ImageView) itemView.findViewById(R.id.imageView_foto_producto);
            textView_categoria=(TextView)itemView.findViewById(R.id.textView_categoria);
            textView_Pcompra=(TextView)itemView.findViewById(R.id.textView_Pcompra);
            textView_url_producto=(TextView)itemView.findViewById(R.id.textView_url_producto);
            button_disminuir=(Button)itemView.findViewById(R.id.button_disminuir);
            linearLayout_producto=(LinearLayout)itemView.findViewById(R.id.layout_principal);

            if (Fragment_crear_factura.recview!=null) textView_costo.setEnabled(true);
            try {

            imagen_producto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(textView_url_producto!=null){
                        MainActivity.opcion_comprar.setVisible(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("url_imagen",textView_url_producto.getText().toString() );
                        Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                    }
                }
            });

            linearLayout_producto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // MainActivity.lista_seleccion_venta_mayor_bodega.clear();
//
//                    linearLayout_producto.setBackgroundColor(Color.parseColor("#A8003335"));
                    if (editText_cantidad.getText().toString().trim().equals(""))editText_cantidad.setText("0");
                    int cantidad= Integer.parseInt(editText_cantidad.getText().toString().trim());
                    cantidad=cantidad+1;
                    editText_cantidad.setText(cantidad+"");
            //        agregar_lista_seleccion_venta_mayor_bodega(textView_id.getText().toString(), textView_nombre.getText().toString(),textView_costo.getText().toString(),editText_cantidad.getText().toString(), textView_url_producto.getText().toString());
                }
            });
            
            linearLayout_producto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //abrir fragemnt
                    MainActivity.opcion_nuevo_producto.setVisible(false);
                    MainActivity.opcion_comprar.setVisible(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("id_producto", textView_id.getText().toString().trim());
                    Navigation.findNavController(view).navigate(R.id.fragmento_nuevo_producto,bundle);
                    return false;
                }
            });

            button_disminuir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    int cantidad= Integer.parseInt(editText_cantidad.getText().toString().trim());
                    if (cantidad>0){
                        cantidad=cantidad-1;
                        editText_cantidad.setText(cantidad+"");
                    }


                }
            });

                //ACCIONES AL CAMBIAR EDITTEXT PRECIO
                textView_costo.addTextChangedListener(new TextWatcher() {
                    int previousLength;  boolean backSpace;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        previousLength = s.length();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                        for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {
                            Modelo_producto productos = MainActivity.lista_seleccion_venta_mayor_bodega.get(x);
                            if (productos.getId().equals(textView_id.getText().toString())){
                                MainActivity.lista_seleccion_venta_mayor_bodega.get(x).setP_detal(textView_costo.getText().toString().trim());
                                actulizar_total();
                                break;
                            }
                        }
                    }
                });

            //ACCIONES AL CAMBIAR EDITTEXT SELECCION
            editText_cantidad.addTextChangedListener(new TextWatcher() {
                int previousLength;  boolean backSpace;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    previousLength = s.length();
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {

                    //ACCIONES AL CAMBIAR SELECCION DE PRODUCTOS (VALORES SUPERIORES A "")
                    actulizar_total();
                    agregar_lista_seleccion_venta_mayor_bodega(textView_id.getText().toString(), textView_nombre.getText().toString(), textView_Pcompra.getText().toString(),textView_costo.getText().toString(),editText_cantidad.getText().toString(), textView_cantidad.getText().toString(), textView_url_producto.getText().toString(),textView_categoria.getText().toString());
                    existencias();
                }
            });
        }catch (Exception e){
                Toast.makeText(context, "error: "+e, Toast.LENGTH_SHORT).show();
            }
        }

       private void existencias() {


           int exitencia, seleccion,total=0;
           if (editText_cantidad.getText().toString().equals("0")||editText_cantidad.getText().toString()==null||editText_cantidad.getText().toString().equals("")){
               textView_cantidad.setText(textView_cantidad_original_invisible.getText().toString());
           }else{
               seleccion= Integer.parseInt(editText_cantidad.getText().toString());
               exitencia= Integer.parseInt(textView_cantidad_original_invisible.getText().toString());

               total=exitencia-seleccion;
               if(total<0){

                   // Colocar este texto si no se quiere factuar productos fuera de existencia

//                   editText_cantidad.setText(textView_cantidad_original_invisible.getText().toString());
//                   textView_cantidad.setText("0");
               }else{
                   textView_cantidad.setText(total+"");
               }

           }

       }
        public void agregar_lista_seleccion_venta_mayor_bodega(String id, String nombre, String Pcompra, String precio, String cantidad, String existencias, String url_imagen, String categoria){
            boolean existe=false;
            linearLayout_producto.setBackgroundColor(Color.parseColor("#A8003335"));
            for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {
                Modelo_producto productos =  MainActivity.lista_seleccion_venta_mayor_bodega.get(x);
                if (productos.getId().equals(id)) {
                    existe=true;
                    if (cantidad.equals("0")||cantidad==null||cantidad.equals("")){
                        button_disminuir.setVisibility(View.GONE);
                        MainActivity.lista_seleccion_venta_mayor_bodega.remove(x);
                        linearLayout_producto.setBackgroundColor(Color.parseColor("#112DC1AB"));
                        if(Fragmento_carrito.adapador2!=null) Fragmento_carrito.adapador2.notifyDataSetChanged();
                        }else {
                        button_disminuir.setVisibility(View.VISIBLE);
                        MainActivity.lista_seleccion_venta_mayor_bodega.get(x).setSeleccion(cantidad);
                    }
                    break;
                }
            }
            if (existe==false&&!cantidad.equals("")&&!cantidad.equals("0")) {
                MainActivity.lista_seleccion_venta_mayor_bodega.add(new Modelo_producto(id,nombre,"",Pcompra,precio,precio,precio,precio,existencias,"","","","","","",url_imagen,cantidad,"",""));                button_disminuir.setVisibility(View.VISIBLE);
            }
            Gson gson = new Gson();
            String jsonString = gson.toJson(MainActivity.lista_seleccion_venta_mayor_bodega);
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA, jsonString);
            editor.apply();

            actulizar_total();
        }

    }

    public static void actulizar_total(){


        //VERIFICAR EL ARRAY Y CONTAR, CANTIDADES POR LOS PRECIOS PARA TENER TOTAL DE FACTURA
//        bandera = true;
          int  total = 0;

        for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {
            Modelo_producto productos = MainActivity.lista_seleccion_venta_mayor_bodega.get(x);

            String precio_producto = productos.getP_detal();
            String seleccion = productos.getSeleccion();
            int valorseleccion;
            try{
                valorseleccion = Integer.parseInt(seleccion) * Integer.parseInt(precio_producto);
            }catch (Exception e){
                valorseleccion=0;
            }
            total=total + valorseleccion;
        }

//        if(MainActivity.lista_seleccion_venta_mayor_bodega.size()>0){
//            MainActivity.opcion_factura.setEnabled(true);
//        }else{
//            MainActivity.opcion_factura.setEnabled(false);
//        }
        NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
        if(Fragment_crear_factura.textView_total!=null)Fragment_crear_factura.textView_total.setText("Total: "+formatoImporte.format(total));
        try {
            Fragment_venta_bodega_mayor.textView_monto_seleccionado_carrito.setText(""+formatoImporte.format(total));
//            Fragmento_carrito.textView_monto_seleccionado_carrito.setText(""+total);

        }catch (Exception e){        }



    }
}
