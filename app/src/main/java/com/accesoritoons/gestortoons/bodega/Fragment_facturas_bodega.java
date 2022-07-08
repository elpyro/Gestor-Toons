package com.accesoritoons.gestortoons.bodega;

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
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerView_mis_facturas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;


public class Fragment_facturas_bodega extends Fragment {


    View vista;
    Context context;
    RecyclerView recyclerView_facturas;
    SearchView searchview;
    ArrayList<Modelo_factura_cliente> lista_facturas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_facturas_bodega, container, false);
        searchview=(SearchView) vista.findViewById(R.id.searchview);
        recyclerView_facturas=vista.findViewById(R.id.recycler_mis_facturas);
        recyclerView_facturas.setLayoutManager(new LinearLayoutManager(context));

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargar_facturas();
    }

    private void cargar_facturas() {


        Query referencia;
        referencia= FirebaseDatabase.getInstance().getReference().child("Factura_cliente").orderByChild("id_vendedor");

        if(referencia!=null){
            referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_facturas=new ArrayList<>();
                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){
                            boolean existe=false;
                            for (int x = 0; x < lista_facturas.size(); x++) {
                                Modelo_factura_cliente factura =  lista_facturas.get(x);
                                if (factura.getId().equals(ds.getValue(Modelo_factura_cliente.class).getId())) {
                                    lista_facturas.remove(x);
                                    break;
                                }
                            }
                            lista_facturas.add(ds.getValue(Modelo_factura_cliente.class));
                        }
                        //ordenar por fecha
                        lista_facturas.sort(Comparator.comparing(Modelo_factura_cliente::getFecha).reversed());
                    }
                    RecyclerView_mis_facturas adaptador= new RecyclerView_mis_facturas(lista_facturas);
                    recyclerView_facturas.setAdapter(adaptador);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchview!=null){
            searchview.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_factura_cliente> filtro =new ArrayList<>();
        for(Modelo_factura_cliente objeto:lista_facturas){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())||objeto.getTelefono().toLowerCase().contains(valor.toLowerCase())||objeto.getId().toLowerCase().contains(valor.toLowerCase())||objeto.getVendedor().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerView_mis_facturas adaptador = new RecyclerView_mis_facturas(filtro);
        recyclerView_facturas.setAdapter(adaptador);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }
}