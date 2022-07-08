package com.accesoritoons.gestortoons.ViewHolder;

import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Fragmento_foto_apliada extends Fragment {
    ImageView imageView_ampliada;
    View vista;
    Context context;
    TextView textView_descripcion,textView_porcentaje,textView_ganacia,textView_nombre,textView_proveedor,textView_platino,textView_oro,textView_diamante,textView_venta,textView_compra;
    LinearLayout linearLayout_detalles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista=inflater.inflate(R.layout.fragment_fragmento_foto_apliada, container, false);
        imageView_ampliada=vista.findViewById(R.id.imageView_ampliada);
        textView_descripcion=vista.findViewById(R.id.textView_descripcion);
        textView_nombre=vista.findViewById(R.id.textView_nombre);
        textView_proveedor=vista.findViewById(R.id.textView_proveedor);
        textView_platino=vista.findViewById(R.id.textView_platino);
        textView_oro=vista.findViewById(R.id.textView_oro);
        textView_diamante=vista.findViewById(R.id.textView_diamante);
        textView_venta=vista.findViewById(R.id.textView_venta);
        textView_compra=vista.findViewById(R.id.textView_compra);
        linearLayout_detalles=vista.findViewById(R.id.linearLayout_detalles);

        MainActivity.opcion_editar_producto.setVisible(false);
        MainActivity.opcion_nuevo_producto.setVisible(false);
        MainActivity.opcion_scanner.setVisible(false);
        MainActivity.opcion_crear_pedido.setVisible(false);

        //recibir argumentos
        if (getArguments() != null) {
            String url_imagen =getArguments().getString("url_imagen");
            Picasso.with(getContext()).load(url_imagen).into(imageView_ampliada);
            producto_existente(url_imagen);

        }

        return vista;
    }

    public void producto_existente(String url_imagen){



        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Query dataQuery = myRefe.child("Productos").orderByChild("url").equalTo(url_imagen).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    linearLayout_detalles.setVisibility(View.VISIBLE);
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_producto producto = userSnapshot.getValue(Modelo_producto.class);

                        textView_nombre.setText(producto.getNombre());
                        textView_descripcion.setText(producto.getDescripcion());

                        textView_proveedor.setText(producto.getProveedor());
                        textView_platino.setText(producto.getP_plantino());
                        textView_oro.setText(producto.getP_oro());
                        textView_diamante.setText(producto.getP_diamante());
                        textView_venta.setText(producto.getP_detal());
                        textView_compra.setText(producto.getP_compra());

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            }

        });
    }
    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }
}