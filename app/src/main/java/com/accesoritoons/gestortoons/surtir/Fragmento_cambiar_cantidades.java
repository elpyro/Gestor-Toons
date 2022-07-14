package com.accesoritoons.gestortoons.surtir;

import static com.accesoritoons.gestortoons.MainActivity.opcion_agregar_producto;
import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_producto_enviado;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;


public class Fragmento_cambiar_cantidades extends Fragment {

    View vista;
    Context context;
    String id_producto, nombre, cantidad, url;
    int cantidad_modificada;
    ImageView imageView_foto;
    ImageButton button_incrementar, button_disminuir;
    TextView textView_catidad, textView_nombre_producto;
    ValueEventListener oyente, oyente2;
    Query referencia2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       context=getContext();
       vista= inflater.inflate(R.layout.fragment_fragmento_cambiar_cantidades, container, false);
       imageView_foto=vista.findViewById(R.id.imagen_foto);
       textView_nombre_producto=vista.findViewById(R.id.textview_producto);
       textView_catidad=vista.findViewById(R.id.textView_cantidad);
       button_disminuir=vista.findViewById(R.id.imageButton_disminuir);
       button_incrementar=vista.findViewById(R.id.imageButton_incrementar);

       MainActivity.opcion_cambiar_cantidad.setVisible(true);
       MainActivity.opcion_cambiar_cantidad.setEnabled(false);
       MainActivity.opcion_agregar_inventario.setVisible(false);
        button_incrementar.setEnabled(false);

        if (getArguments() != null) {
            url =getArguments().getString("url");
            id_producto =getArguments().getString("id_producto_pedido");
            nombre =getArguments().getString("nombre");
            cantidad =getArguments().getString("cantidad");
            cantidad_modificada= Integer.parseInt(cantidad);
           producto_existente();
        }else {
            Toast.makeText(context, context.getString(R.string.Producto_inexistente), Toast.LENGTH_LONG).show();
        }

        button_incrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.opcion_cambiar_cantidad.setEnabled(true);
                MainActivity.opcion_cambiar_cantidad.setEnabled(true);
                cantidad_modificada=cantidad_modificada+1;
                textView_catidad.setText(cantidad_modificada+"");
                button_disminuir.setEnabled(true);
                if (cantidad_modificada==Integer.parseInt(cantidad)){
                    button_incrementar.setEnabled(false);
                    MainActivity.opcion_cambiar_cantidad.setEnabled(false);
                }
            }
        });

        button_disminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cantidad_modificada==1)return;
                MainActivity.opcion_cambiar_cantidad.setEnabled(true);
                cantidad_modificada=cantidad_modificada-1;
                textView_catidad.setText(cantidad_modificada+"");
                button_incrementar.setEnabled(true);
                if (cantidad_modificada<2)button_disminuir.setEnabled(false);
            }
        });

        MainActivity.opcion_cambiar_cantidad.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {



                Query referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_producto_pedido").equalTo(id_producto).limitToFirst(1);
                        referencia.keepSynced(true);               
        try {
            Thread.sleep(1 * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
                    if(referencia!=null){

                        referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Modelo_pedido pedido = ds.getValue(Modelo_pedido.class);
                                        pedido.setCantidad(cantidad_modificada+"");

                                        ds.getRef().removeValue();

                                        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                        myRefe.child("Pedidos").child(pedido.getId_producto_pedido()).setValue(pedido);

                                        cantidad_modificada=Integer.parseInt(cantidad)-cantidad_modificada;
                                        pedido.setCantidad(cantidad_modificada+"");
                                        pedido.setId_producto_pedido(UUID.randomUUID().toString());

                                        myRefe.child("Pedidos").child(pedido.getId_producto_pedido()).setValue(pedido);
                                        Navigation.findNavController(vista).navigateUp();

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //  Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                return false;
            }
        });
       return vista;
    }

    public void producto_existente(){
        Picasso.with(context).load(url).into(imageView_foto);
        textView_catidad.setText(cantidad_modificada+"");
        textView_nombre_producto.setText(nombre);

    }


//
//    public void cargar_pedido_enviado(String id_pedido) {
//
//
//        //cargar id pedidos y cantidades
//        Query referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_producto_pedido").equalTo(id_producto).limitToFirst(1);
//                referencia.keepSynced(true);
//        try {
//            Thread.sleep(1 * 500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        };
//        if(referencia!=null){
//            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    ArrayList<Modelo_pedido> lista_producto_pedido=new ArrayList<>();
//                    if (snapshot.exists()){
//                        for(DataSnapshot ds:snapshot.getChildren()){
//                            lista_producto_pedido.add(ds.getValue(Modelo_pedido.class));
//                        }
//
//                        ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos = new ArrayList<>();
//                        //cargar datos del producto
//                        for (int x = 0; x < lista_producto_pedido.size(); x++) {
//                            referencia2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_producto_pedido.get(x).getReferencia_producto()).limitToFirst(1);
//                            String id_producto_buscado=lista_producto_pedido.get(x).getReferencia_producto();
//
//
//                            if (referencia2 != null) {
//                                int finalX = x;
//                                oyente=referencia2.addValueEventListener(new ValueEventListener() {
//
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.exists()) {
//                                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                                Modelo_producto_facturacion_app_vendedor producto = snapshot.getValue(Modelo_producto_facturacion_app_vendedor.class);
//                                                textView_nombre_producto.setText(producto.getNombre());
//
//                                            }
//
//                                        }
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        Glide.get(context).clearMemory();//clear memory
        MainActivity.opcion_cambiar_cantidad.setVisible(false);
        vista=null;
    }
}