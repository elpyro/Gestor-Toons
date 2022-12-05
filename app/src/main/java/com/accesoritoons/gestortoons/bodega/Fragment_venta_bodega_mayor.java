package com.accesoritoons.gestortoons.bodega;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.Activity_scanner;
import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_agregar_inventario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_compras_bodega;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_ventas_bodega;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class Fragment_venta_bodega_mayor extends Fragment {
    RecyclerView recview;
    SearchView searchView_produtos;
    Query referencia;
    ArrayList<Modelo_producto> lista ;
    private Button button_borrar_seleccion;
    View vista;
    Context context;
    public static TextView textView_monto_seleccionado_carrito;
    public static ArrayList<Modelo_producto>lista_produtos_completa=new ArrayList<>();
    public static boolean venta_bodega_mayor=false;
    String id_factura;
    ValueEventListener oyente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_agregar_inventario, container, false);
        textView_monto_seleccionado_carrito=(TextView)vista.findViewById(R.id.textView_monto_seleccionado);
        button_borrar_seleccion=vista.findViewById(R.id.button_borrar_seleccion);
        recview=(RecyclerView)vista.findViewById(R.id.recycler_inventario);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_produtos=(SearchView)vista.findViewById(R.id.searchview);

        venta_bodega_mayor=true;

try{


    MainActivity.opcion_factura.setVisible(true);
    MainActivity.opcion_scanner.setVisible(true);
}catch (Exception e){}


        button_borrar_seleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.lista_seleccion_venta_mayor_bodega.clear();
                onResume();
            }
        });

        MainActivity.opcion_scanner.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent=new Intent(getContext(), Activity_scanner.class);
                intent.putExtra("id_usuario_pedido",MainActivity.Id_Usuario);
                startActivity(intent);
                return false;
            }
        });

        MainActivity.opcion_factura.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {



                if(MainActivity.lista_seleccion_venta_mayor_bodega.size()<1) {
                    Toast.makeText(context, context.getString(R.string.Productos_no_selecionados), Toast.LENGTH_SHORT).show();
                }else {


                    try {
                        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

                    }catch (Exception e){}

                    if (id_factura!=null){
                        //agregar productos a factura existente
                        Guardar_firebase agregar = new Guardar_firebase();
                        agregar.agregar_a_factura_existente(context,id_factura);
                        Navigation.findNavController(vista).navigateUp();

                    }else{
                        //nueva factura
                        MainActivity.opcion_factura.setVisible(false);
                        Navigation.findNavController(vista).navigate(R.id.fragment_crear_factura);
                    }

                }
                return false;
            }
        });

        referencia= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("estado").equalTo("true");

        if (getArguments() != null) {
            id_factura =getArguments().getString("id_factura");
        }

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.opcion_confirmar.setVisible(false);
        RecyclerViewAdaptador_ventas_bodega.actulizar_total();
        if(MainActivity.lista_seleccion.size()>0){
            MainActivity.opcion_crear_pedido.setEnabled(true);
        }else{
            MainActivity.opcion_crear_pedido.setEnabled(false);
        }
        if(referencia!=null){
            oyente=referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_produtos_completa.clear();
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if (!ds.getValue(Modelo_producto.class).getCantidad().equals("0")) lista.add(ds.getValue(Modelo_producto.class));
                        }
                        lista_produtos_completa=lista;
                        lista.sort(Comparator.comparing(Modelo_producto::getNombre));
                        RecyclerViewAdaptador_ventas_bodega adapador= new RecyclerViewAdaptador_ventas_bodega(lista);
                        recview.setAdapter(adapador);
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
        RecyclerViewAdaptador_ventas_bodega adaptador = new RecyclerViewAdaptador_ventas_bodega(filtro);
        recview.setAdapter(adaptador);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        venta_bodega_mayor=false;
        MainActivity.opcion_factura.setVisible(false);
        MainActivity.opcion_scanner.setVisible(false);
        Glide.get(context).clearMemory();//clear memory
        referencia.removeEventListener(oyente);
        referencia=null;
        vista=null;
    }
}