package com.accesoritoons.gestortoons.pestañas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.accesoritoons.gestortoons.Activity_scanner;
import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.surtir.Fragmento_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_inventario_vendedor;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Contenedor_compras_bodega extends Fragment {

    Context context;
    View vista;
    private Pestaña_agregar_inventario adaptador;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    public static boolean compra_activa=false;
    String descripcion_compra="", id_factura;


    private  final String PREFERENCIA_SELECCION_COMPRA = "PREFERENCIA_SELECCION_COMPRA";




    public static Contenedor_compras_bodega newInstance(String param1, String param2) {
        Contenedor_compras_bodega fragment = new Contenedor_compras_bodega();


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=getContext();
        vista= inflater.inflate(R.layout.fragment_contenedor_compras_bodega, container, false);

        tabLayout= vista.findViewById(R.id.tab_layout);
        viewPager2= vista.findViewById(R.id.view_pager_carrito);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Invetario_bodega));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Carrito));

        FragmentManager fm= getActivity().getSupportFragmentManager();
        adaptador=new Pestaña_agregar_inventario(fm,getLifecycle());
        viewPager2.setAdapter(adaptador);

        MainActivity.opcion_comprar.setVisible(true);
        MainActivity.opcion_comprar.setEnabled(false);

        compra_activa=true;

        MainActivity.opcion_comprar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
            registrar_compra();
                return false;
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        if (getArguments() != null) {
            id_factura =getArguments().getString("id_factura");
        }
        return vista;
    }

    public void registrar_compra() {

        if (id_factura != null) {
            //agregar productos a factura existente
            Guardar_firebase agregar = new Guardar_firebase();
            agregar.agregar_a_factura_compra_existente(context, id_factura);
            Navigation.findNavController(vista).navigateUp();

        } else {
           //nueva factura
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
        String id_factura = UUID.randomUUID().toString();
        descripcion_compra = context.getString(R.string.Productos_agregados);
        for (int x = 0; x < MainActivity.lista_seleccion_compra.size(); x++) {

            String id_referencia_producto = MainActivity.lista_seleccion_compra.get(x).getId();

            Query referencia_id_producto = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_referencia_producto);

            referencia_id_producto.keepSynced(true);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (referencia_id_producto != null) {

                int finalX1 = x;

                referencia_id_producto.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Modelo_producto producto = ds.getValue(Modelo_producto.class);


                                String nombre = MainActivity.lista_seleccion_compra.get(finalX1).getNombre();
                                String referencia_producto = id_referencia_producto;
                                String vendedor_producto = MainActivity.Id_Usuario + "-" + id_referencia_producto;
                                String costo_compra = MainActivity.lista_seleccion_compra.get(finalX1).getP_compra();
                                String url = MainActivity.lista_seleccion_compra.get(finalX1).getUrl();
                                int cantidad_seleccionada = Integer.parseInt(MainActivity.lista_seleccion_compra.get(finalX1).getSeleccion());

                                int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                                int Cantidad = 0;
                                Cantidad = cantidad_inventario + cantidad_seleccionada;

                                producto.setCantidad(Cantidad + "");
                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                myRefe.child("Productos").child(producto.getId()).setValue(producto);

                                //guardar datos de productos facturados
                                DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

                                Modelo_producto_facturacion_app_vendedor productos = new Modelo_producto_facturacion_app_vendedor();
                                productos.setId_pedido(id_factura);
                                productos.setId_producto_pedido(UUID.randomUUID().toString());
                                productos.setNombre(nombre);
//                                productos.setCodigo_barras(MainActivity.lista_seleccion.get(finalX2).getCodigo());
                                productos.setId_referencia_vendedor(MainActivity.Id_Usuario);
                                productos.setNombre_vendedor(MainActivity.Usuario);
                                productos.setCantidad(cantidad_seleccionada + "");
                                productos.setEstado(context.getString(R.string.Compra));
                                productos.setId_referencia_producto(referencia_producto);
                                productos.setVendedor_producto(vendedor_producto);
                                productos.setCosto(costo_compra);
                                productos.setVenta(costo_compra);
                                String fecha= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                productos.setFecha(fecha);
                                productos.setNombre_vendedor(MainActivity.Usuario);

//                                productos.setRecaudo(context.getString(R.string.NoAplica));
                                productos.setUrl(url);
                                referencia.child("Factura_productos").child(productos.getId_producto_pedido()).setValue(productos);

                                descripcion_compra = descripcion_compra + "\n" + "x" + cantidad_seleccionada + " " + nombre;

                            }

                            if (finalX1 + 1 == MainActivity.lista_seleccion_compra.size()) {
                                //datos de factura de compra

                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                Modelo_factura_cliente factura_cliente = new Modelo_factura_cliente();
                                factura_cliente.setId(id_factura);
                                factura_cliente.setTipo(context.getString(R.string.Compra));
                                factura_cliente.setNombre(context.getString(R.string.Compras_bodega));
                                factura_cliente.setTelefono("");
                                factura_cliente.setDocumento("");
                                factura_cliente.setFecha(fecha);
                                factura_cliente.setId_vendedor("Bodega");
                                factura_cliente.setVendedor(MainActivity.Usuario);
                                myRefe.child("Factura_cliente").child(factura_cliente.getId()).setValue(factura_cliente);

                                Guardar_firebase guardar_historial = new Guardar_firebase();
                                guardar_historial.guardar_historial(id_factura, context.getString(R.string.Compra), "", descripcion_compra, context);

                                MainActivity.lista_seleccion_compra.clear();
                                if (Fragmento_agregar_inventario.textView_monto_seleccionado_carrito!=null)Fragmento_agregar_inventario.textView_monto_seleccionado_carrito.setText("Total: 0");
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(MainActivity.lista_seleccion_compra);
                                SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(PREFERENCIA_SELECCION_COMPRA, jsonString);
                                editor.apply();

                                Navigation.findNavController(vista).navigateUp();
                            }
                        }


//
//                            }

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.opcion_comprar.setVisible(false);
        MainActivity.opcion_comprar.setEnabled(false);
        MainActivity.opcion_scanner.setVisible(false);
        compra_activa=false;
        vista=null;
    }
}