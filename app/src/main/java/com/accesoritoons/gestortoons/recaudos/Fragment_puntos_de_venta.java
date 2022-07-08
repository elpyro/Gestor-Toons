package com.accesoritoons.gestortoons.recaudos;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_vendedores;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Fragment_puntos_de_venta extends Fragment {

    View vista;
    Context context;
    RecyclerView recview;
    SearchView searchView_perfiles;
    Query referencia;
    ArrayList<Modelo_usuario> lista;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_fragmento_vendedores, container, false);
        context=getContext();
        recview=(RecyclerView)vista.findViewById(R.id.recycler_vendedores);
                   recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_perfiles=(SearchView)vista.findViewById(R.id.searchview);
        referencia= FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("perfil").equalTo(getString(R.string.Vendedor));
        return vista;
    }


    @Override
    public void onStart() {
        super.onStart();

        if(referencia!=null){
            referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_usuario.class));
                        }
                        RecyclerViewAdaptador_vendedores adapador= new RecyclerViewAdaptador_vendedores(lista);
                        recview.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView_perfiles!=null){
            searchView_perfiles.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_usuario> filtro =new ArrayList<>();
        for(Modelo_usuario objeto:lista){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerViewAdaptador_vendedores adaptador = new RecyclerViewAdaptador_vendedores(filtro);
        recview.setAdapter(adaptador);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }
}