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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.bodega.Fragment_compra__bodega;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.pestañas.Contenedor_compras_bodega;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_agregar_inventario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_compras_bodega;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;


public class Fragmento_agregar_inventario extends Fragment {
    RecyclerView recview;
    SearchView searchView_produtos;
    Query referencia;
    private ValueEventListener oyente;
    ArrayList<Modelo_producto> lista ;
    public static ArrayList<Modelo_producto> lista_produtos_completa ;
    View vista;
    Context context;
    public static TextView textView_monto_seleccionado_carrito;

    NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_agregar_inventario, container, false);
        textView_monto_seleccionado_carrito=(TextView)vista.findViewById(R.id.textView_monto_seleccionado);

        recview=(RecyclerView)vista.findViewById(R.id.recycler_inventario);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_produtos=(SearchView)vista.findViewById(R.id.searchview);

        MainActivity.opcion_nuevo_producto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MainActivity.opcion_nuevo_producto.setVisible(false);
                Navigation.findNavController(vista).navigate(R.id.fragmento_nuevo_producto);
                return false;
            }
        });


        try {
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

        }catch (Exception E){}


        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();


//        if(MainActivity.lista_seleccion.size()>0){
            MainActivity.opcion_crear_pedido.setEnabled(true);
//        }else{
//            MainActivity.opcion_crear_pedido.setEnabled(false);
//        }

        referencia= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("cliente_mis_productos").equalTo("Accesory Toons");
        referencia.keepSynced(true);
        if(referencia!=null){
            oyente=referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if(Contenedor_compras_bodega.compra_activa!=true){
                                if (!ds.getValue(Modelo_producto.class).getCantidad().equals("0"))
                                    lista.add(ds.getValue(Modelo_producto.class));
                            }else{
                                lista.add(ds.getValue(Modelo_producto.class));
                            }
                        }

                   //     Toast.makeText(context, "Productos disponibles "+lista.size(), Toast.LENGTH_SHORT).show();
                        lista_produtos_completa=lista;
                        if(Contenedor_compras_bodega.compra_activa==true){

                            RecyclerViewAdaptador_compras_bodega.actulizar_total();
                            lista.sort(Comparator.comparing(Modelo_producto::getNombre));
                            RecyclerViewAdaptador_compras_bodega adapador= new RecyclerViewAdaptador_compras_bodega(lista);
                            recview.setAdapter(adapador);

                        }else{
                            RecyclerViewAdaptador_agregar_inventario.actulizar_total();
                            lista.sort(Comparator.comparing(Modelo_producto::getNombre));
                            RecyclerViewAdaptador_agregar_inventario adapador= new RecyclerViewAdaptador_agregar_inventario(lista);
                            recview.setAdapter(adapador);
                        }
//                        referencia.removeEventListener(this);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
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

        if(Contenedor_compras_bodega.compra_activa==true){
            RecyclerViewAdaptador_compras_bodega.actulizar_total();
            RecyclerViewAdaptador_compras_bodega adaptador = new RecyclerViewAdaptador_compras_bodega(filtro);
            recview.setAdapter(adaptador);
        }else{
            RecyclerViewAdaptador_agregar_inventario.actulizar_total();
            RecyclerViewAdaptador_agregar_inventario adaptador = new RecyclerViewAdaptador_agregar_inventario(filtro);
            recview.setAdapter(adaptador);
        }



    }
    @Override
    public void onDetach() {
        super.onDetach();
        referencia.removeEventListener(oyente);
        Glide.get(context).clearMemory();//clear memory
        referencia=null;

        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

        vista=null;
    }
}