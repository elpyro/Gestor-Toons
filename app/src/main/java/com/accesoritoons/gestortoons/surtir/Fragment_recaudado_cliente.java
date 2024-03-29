package com.accesoritoons.gestortoons.surtir;

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

import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_recaudado_cliente;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class Fragment_recaudado_cliente extends Fragment {

    View vista;
    Context context;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_garantias;
    RecyclerView recyclerview_recaudado;
    SearchView searchview;
    TextView textView_articulos,textView_total;
    String id_vendedor="";
    ValueEventListener oyente;
    NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
    Query referencia;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_recaudado_cliente, container, false);
        searchview=(SearchView)vista.findViewById(R.id.searchview);
        recyclerview_recaudado =vista.findViewById(R.id.recycler_recaudos_pendientes);
        recyclerview_recaudado.setLayoutManager(new LinearLayoutManager(context));
        textView_articulos=(TextView)vista.findViewById(R.id.textView_articulos);
        textView_total=(TextView) vista.findViewById(R.id.textView_total);

        if (getArguments() != null) {
            id_vendedor =getArguments().getString("id_vendedor");
        }

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.vista_recaudo=true;
        cargar_recaudos();
    }

    private void cargar_recaudos() {



        referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_referencia_vendedor").equalTo(id_vendedor);

        if(referencia!=null){
            oyente=referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_productos=new ArrayList<>();
                    lista_garantias=new ArrayList<>();
                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){

                            Modelo_producto_facturacion_app_vendedor factura = ds.getValue(Modelo_producto_facturacion_app_vendedor.class);
                            if (!factura.getRecaudo().equals(context.getString(R.string.Pendiente))&&!factura.getRecaudo().equals(context.getString(R.string.No_aplica))) {

//
//                                for (int x = 0; x < lista_productos.size(); x++) {
//                                    Modelo_producto_facturacion_app_vendedor factura2 =  lista_productos.get(x);
//                                    if (factura2.getId_producto_pedido().equals(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido())) {
//                                        lista_productos.remove(x);
//                                        break;
//                                    }
//                                }


                                lista_productos.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));
//                                int precio= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCosto());
//                                int cantidad= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
//                                int total=precio*cantidad;


                            }



                        }
                    }

                   // ordenar por fecha

                    lista_productos.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getFecha_recaudo));
                    Collections.reverse(lista_productos);
                    RecyclerViewAdaptador_recaudado_cliente adaptador= new RecyclerViewAdaptador_recaudado_cliente(lista_productos);
                    recyclerview_recaudado.setAdapter(adaptador);
//
//                    RecyclerViewAdaptador_productos_facturados adaptador_garantias= new RecyclerViewAdaptador_productos_facturados(lista_garantias);
//                    recycler_garantias.setAdapter(adaptador_garantias);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
            if (searchview!=null){
                searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
            }else{
                textView_total.setVisibility(View.GONE);
                textView_articulos.setVisibility(View.GONE);
            }
        }
    }
    private void filtro(String valor) {
        ArrayList<Modelo_producto_facturacion_app_vendedor> filtro =new ArrayList<>();
        int total=0;
        int garantias=0;
        textView_total.setVisibility(View.VISIBLE);
        textView_articulos.setVisibility(View.VISIBLE);
        for(Modelo_producto_facturacion_app_vendedor objeto:lista_productos){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())|| objeto.getRecaudo().toLowerCase().contains(valor.toLowerCase())|| objeto.getFecha_recaudo().toLowerCase().contains(valor.toLowerCase()) ){

                if(objeto.getEstado().equals(context.getString(R.string.Garantia))) {
                    garantias = garantias + 1;
                }else{
                    String validacion_oro=objeto.getRecaudo().substring(0,5);
                    if (validacion_oro.equals("Oro, ")){
                        total=total+ (Integer.parseInt(objeto.getVenta())*Integer.parseInt(objeto.getCantidad()));
                    }else{
                        total=total+ (Integer.parseInt(objeto.getCosto())*Integer.parseInt(objeto.getCantidad()));
                    }
                }
                filtro.add(objeto);
            }
        }

        textView_total.setText(context.getString(R.string.Recaudos)+": "+formatoImporte.format(total)+"");
        textView_articulos.setText(context.getString(R.string.Garantia)+": "+garantias);
        filtro.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getFecha));
        RecyclerViewAdaptador_recaudado_cliente adaptador = new RecyclerViewAdaptador_recaudado_cliente(filtro);
        recyclerview_recaudado.setAdapter(adaptador);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        referencia.removeEventListener(oyente);
        referencia=null;
        Glide.get(context).clearMemory();//clear memory
        MainActivity.vista_recaudo=false;
        vista=null;
    }
}