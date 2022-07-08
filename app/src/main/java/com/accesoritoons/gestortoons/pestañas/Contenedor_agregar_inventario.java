package com.accesoritoons.gestortoons.pestañas;

import static com.accesoritoons.gestortoons.MainActivity.opcion_agregar_producto;
import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.accesoritoons.gestortoons.Activity_scanner;
import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_carrito;
import com.accesoritoons.gestortoons.surtir.Fragmento_inventario_vendedor;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Contenedor_agregar_inventario extends Fragment {

    View vista;
    Context context;
    private Pestaña_agregar_inventario adaptador;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;



    private  final String PREFERENCIA_SELECCION = "PREFERENCIA_SELECCION";

    public static Contenedor_agregar_inventario newInstance(String param1, String param2) {
        Contenedor_agregar_inventario fragment = new Contenedor_agregar_inventario();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista= inflater.inflate(R.layout.fragment_contenedor_agregar_inventario, container, false);
        context=getContext();
        tabLayout= vista.findViewById(R.id.tab_layout);
        viewPager2= vista.findViewById(R.id.view_pager_carrito);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Invetario));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Carrito));

        FragmentManager fm= getActivity().getSupportFragmentManager();
        adaptador=new Pestaña_agregar_inventario(fm,getLifecycle());
        viewPager2.setAdapter(adaptador);

        MainActivity.opcion_crear_pedido.setVisible(true);
        MainActivity.opcion_scanner.setVisible(true);
        MainActivity.opcion_crear_pedido.setEnabled(false);

        Contenedor_compras_bodega.compra_activa=false;
            String id_usuario =getArguments().getString("id_usuario");
            String nombre_usuario =getArguments().getString("nombre_usuario");




        MainActivity.opcion_scanner.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent=new Intent(getContext(), Activity_scanner.class);
                intent.putExtra("id_usuario_pedido",id_usuario);
                startActivity(intent);
                return false;
            }
        });



        MainActivity.opcion_crear_pedido.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                RecyclerViewAdaptador_agregar_inventario.actulizar_total();
                registrar_pedido();
                return false;
            }

            private void registrar_pedido() {

                if(MainActivity.lista_seleccion.size()<1) {
                    Toast.makeText(context, context.getString(R.string.Productos_no_selecionados), Toast.LENGTH_SHORT).show();
                }else {
                String fecha_hora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());

                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                String id_pedido = UUID.randomUUID().toString();

                for (int x = 0; x < MainActivity.lista_seleccion.size(); x++) {
                    Modelo_producto productos =  MainActivity.lista_seleccion.get(x);

                    Modelo_pedido pedido = new Modelo_pedido();
                    pedido.setId_pedido(id_pedido);//ENTREGA UN ID DISTINTO);
                    pedido.setId_producto_pedido( UUID.randomUUID().toString());
                    pedido.setEstado(getString(R.string.Pedido_enviado));
                    pedido.setReferencia_vendedor(id_usuario);
                    pedido.setNombre_vendedor(nombre_usuario);
                    pedido.setFecha(fecha_hora);
                    pedido.setUsuario(MainActivity.Usuario);
                    pedido.setCantidad(productos.getSeleccion());
                    pedido.setReferencia_producto(productos.getId());


                    //RESTA INVENTARIO
                    Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(productos.getId());
                    referencia_id_producto.keepSynced(true);
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(referencia_id_producto!=null) {
                        int finalX1 = x;
                        referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {

                                        Modelo_producto producto = ds.getValue(Modelo_producto.class);
                                        int cantidad_seleccionada = Integer.parseInt(productos.getSeleccion());
                                        int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                                        int Cantidad = 0;
                                        Cantidad=cantidad_inventario-cantidad_seleccionada;

                                        //verfica que hay existencia para regisrar el producto
                                        if(Cantidad>-1){
                                            producto.setCantidad(Cantidad+"");
                                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                            myRefe.child("Productos").child(producto.getId()).setValue(producto);
                                            myRefe.child("Pedidos").child(pedido.getId_producto_pedido()).setValue(pedido);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                }
                Toast.makeText(getContext(), getString(R.string.Pedido_enviado), Toast.LENGTH_SHORT).show();
                //guardando HISTORIAL
                Modelo_historial historial = new Modelo_historial();
                historial.setId(UUID.randomUUID().toString());
                historial.setReferencia(id_pedido);
                historial.setActividad(getString(R.string.Pedido_enviado));
                historial.setVisible(id_usuario);
                historial.setFecha(fecha_hora);
                historial.setDescripcion(getString(R.string.Pedido_enviado) + ": " + nombre_usuario);
                historial.setUsuario(MainActivity.Usuario);
                myRefe.child("Historial").child(historial.getId()).setValue(historial);
                Navigation.findNavController(vista).navigateUp();
                MainActivity.lista_seleccion.clear();

                Gson gson = new Gson();
                String jsonString = gson.toJson(MainActivity.lista_seleccion);
                SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREFERENCIA_SELECCION, jsonString);
                editor.apply();

            }
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
        return vista;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.opcion_crear_pedido.setVisible(false);
        MainActivity.opcion_crear_pedido.setEnabled(false);
        MainActivity.opcion_scanner.setVisible(false);

    }
}