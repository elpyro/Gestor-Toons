package com.accesoritoons.gestortoons.surtir;

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
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.pestañas.Contenedor_compras_bodega;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_agregar_inventario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_compras_bodega;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragmento_carrito extends Fragment {

    RecyclerView recview;
    SearchView searchView_produtos;
    View vista;
    Query referencia;
    Context context;
    public static RecyclerViewAdaptador_agregar_inventario adapador;
    public static RecyclerViewAdaptador_compras_bodega adapador2;
    public static TextView textView_monto_seleccionado_carrito;
    ArrayList<Modelo_producto> lista ;
    public static ArrayList<Modelo_producto> lista_produtos_completa ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragemento_carrito, container, false);
        textView_monto_seleccionado_carrito=(TextView)vista.findViewById(R.id.textView_monto_seleccionado);
        recview=(RecyclerView)vista.findViewById(R.id.recycler_inventario);
                   recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_produtos=(SearchView)vista.findViewById(R.id.searchview);
        referencia= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("estado").equalTo("true");
        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();

        //mismo carrito para compras y surtir
        //se diferncia si la compra es activa o no
        if(Contenedor_compras_bodega.compra_activa==true){
            if(MainActivity.lista_seleccion_compra.size()>0){
                MainActivity.opcion_crear_pedido.setEnabled(true);
            }else{
                MainActivity.opcion_crear_pedido.setEnabled(false);
            }
            if(MainActivity.lista_seleccion_compra!=null){
                adapador2= new RecyclerViewAdaptador_compras_bodega(MainActivity.lista_seleccion_compra);
                recview.setAdapter(adapador2);
                RecyclerViewAdaptador_compras_bodega.actulizar_total();
            }


        }else{
            if(MainActivity.lista_seleccion.size()>0){
                MainActivity.opcion_crear_pedido.setEnabled(true);
            }else{
                MainActivity.opcion_crear_pedido.setEnabled(false);
            }

            if(referencia!=null){
                referencia.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            lista=new ArrayList<>();
                            for(DataSnapshot ds:snapshot.getChildren()){
                                //AGREGA PRODUCTO SI SE SELECCIONO
                                if (MainActivity.lista_seleccion!=null){
                                    for (int x = 0; x < MainActivity.lista_seleccion.size(); x++) {
                                        if (MainActivity.lista_seleccion.get(x).getId().equals(ds.getValue(Modelo_producto.class).getId()))  lista.add(ds.getValue(Modelo_producto.class));
                                    }
                                }
                            }

                            adapador= new RecyclerViewAdaptador_agregar_inventario(lista);
                            recview.setAdapter(adapador);
                            RecyclerViewAdaptador_agregar_inventario.actulizar_total();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }


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
        if(Contenedor_compras_bodega.compra_activa==true){
            for(Modelo_producto objeto:MainActivity.lista_seleccion_compra){
                if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                    filtro.add(objeto);
                }
            }
            adapador2 = new RecyclerViewAdaptador_compras_bodega(filtro);
            recview.setAdapter(adapador2);
        }else{
            for(Modelo_producto objeto:MainActivity.lista_seleccion){
                if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                    filtro.add(objeto);
                }
            }
            adapador = new RecyclerViewAdaptador_agregar_inventario(filtro);
            recview.setAdapter(adapador);
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        referencia=null;
        vista=null;
    }

}