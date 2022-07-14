package com.accesoritoons.gestortoons.inventario;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.ViewHolder.ProductosViewHoler;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_productos;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class Fragmento_inventario extends Fragment {

    // crear searchview https://www.youtube.com/watch?v=PmqYd-AdmC0

    RecyclerView recview;
    SearchView searchView_produtos;
    Query referencia;
    ArrayList<Modelo_producto> lista;
    View vista;
    private Context contex;
    ValueEventListener oyente;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_fragmento_inventario, container, false);
        contex=getContext();

        recview=(RecyclerView)vista.findViewById(R.id.recycler_inventario);
        recview.setHasFixedSize(true);

        recview.setLayoutManager(new LinearLayoutManager(contex));
        searchView_produtos=(SearchView)vista.findViewById(R.id.searchview);

        MainActivity.opcion_nuevo_producto.setVisible(true);

        MainActivity.opcion_nuevo_producto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MainActivity.opcion_nuevo_producto.setVisible(false);
                Navigation.findNavController(vista).navigate(R.id.fragmento_nuevo_producto);
                return false;
            }
        });


        return vista;
    }


    @Override
    public void onStart() {
        super.onStart();
        referencia=FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("cliente_mis_productos").equalTo("Accesory Toons");
        if(referencia!=null){
            oyente=referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_producto.class));
                        }
                        lista.sort(Comparator.comparing(Modelo_producto::getNombre));
                        RecyclerViewAdaptador_productos adapador= new RecyclerViewAdaptador_productos(lista);
                        recview.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(contex, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView_produtos!=null){
            searchView_produtos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filtro(newText);
                    return true;
                }


            });
        }
    }

    private void filtro(String valor) {
        ArrayList<Modelo_producto> filtro =new ArrayList<>();
        for(Modelo_producto objeto:lista){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerViewAdaptador_productos adaptador = new RecyclerViewAdaptador_productos(filtro);
        recview.setAdapter(adaptador);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        referencia.removeEventListener(oyente);
        Glide.get(contex).clearMemory();//clear memory
        referencia=null;
        MainActivity.opcion_nuevo_producto.setVisible(false);
        vista=null;
    }


}