package com.accesoritoons.gestortoons.surtir;

import static android.content.ContentValues.TAG;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.Vista_pdf;
import com.accesoritoons.gestortoons.bodega.Fragment_crear_factura;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_producto_enviado;
import com.accesoritoons.gestortoons.reportes_pdf.Activity_vista_pdf;
import com.accesoritoons.gestortoons.reportes_pdf.PDFUtility_factura_bodega;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Fragmento_pedido_enviado extends Fragment {
    public  View vista;
    Context context;

    public String nombre_usuario, id_pedido,pedido_fecha,id_vendedor, fecha_hora, referencia_vendedor, referencia_actividad;
    TextView textView_pedido_fecha, textview_nombre_usuario, textView_recibido;
    public static RecyclerView recview_pedidos;

    Query referencia;
    int cargado=0;
    String actividad, visibilidad, agregando_inventario;
    Guardar_firebase metodo = new Guardar_firebase();
    ShimmerTextView myShimmerTextView;


   public ArrayList<Modelo_pedido> lista_producto_pedido;
   public ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos = new ArrayList<>();;
   public static ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos_recibidos=new ArrayList<>();
   public ArrayList<Modelo_pedido> lista_productos_a_facturar=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_pedido, container, false);
        textView_pedido_fecha=vista.findViewById(R.id.textView_pedido_fecha);
        textview_nombre_usuario=vista.findViewById(R.id.textView_nombre_usuario);
        textView_recibido=vista.findViewById(R.id.textView5);
        //recibir argumentos
        if (getArguments() != null) {
            nombre_usuario =getArguments().getString("nombre_usuario");
            id_pedido =getArguments().getString("id_pedido");
            pedido_fecha =getArguments().getString("pedido_fecha");
            referencia_vendedor=getArguments().getString("referencia_vendedor");
            textView_pedido_fecha.setText(pedido_fecha);
            textview_nombre_usuario.setText(nombre_usuario);
            MainActivity.id_vendedor=referencia_vendedor;
        }

        fecha_hora= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());


        myShimmerTextView=vista.findViewById(R.id.shimmer_tv);

        Shimmer shimmer = new Shimmer();
        shimmer.start(myShimmerTextView);


        recview_pedidos=(RecyclerView)vista.findViewById(R.id.recycler_pedidos);
        recview_pedidos.setHasFixedSize(true);
        recview_pedidos.setLayoutManager(new LinearLayoutManager(context));

        MainActivity.opcion_compartir_logo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(context, Activity_vista_pdf.class);
                intent.putExtra("tipo_reporte", "Pedido del cliente");
                intent.putExtra("id_vendedor", id_vendedor);
                intent.putExtra("id_pedido", id_pedido);
                intent.putExtra("desde", pedido_fecha.substring(0,10));
                startActivity(intent);

                return false;
            }
        });
        MainActivity.opcion_agregar_inventario.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                if(lista_productos_recibidos.size()<1) {
                    Toast.makeText(context, context.getString(R.string.Productos_no_selecionados), Toast.LENGTH_SHORT).show();
                }else {

                    String nombre_vendedor = "";
                    String descripcion_pedido = "\n" + getString(R.string.Venta_bodega);
                    for (int x = 0; x < lista_productos_recibidos.size(); x++) {
                        lista_productos_a_facturar.add(new Modelo_pedido("", "", "", "", "", lista_productos_recibidos.get(x).getCantidad(), "", "", ""));
                        descripcion_pedido = descripcion_pedido + "\n" + lista_productos_recibidos.get(x).getCantidad() + " " + lista_productos_recibidos.get(x).getNombre();
                    }

                    try {
                        nombre_vendedor = Fragmento_informacion_vendedor.nombre_vendedor;
                    } catch (Exception e) {
                    }

                    if (lista_producto_pedido.size() == lista_productos_recibidos.size()) {
                        agregando_inventario = nombre_vendedor + descripcion_pedido;
                        actividad = getString(R.string.Surtido);
                        referencia_actividad = id_vendedor;
                    } else {

                        agregando_inventario = nombre_vendedor + descripcion_pedido;
                        actividad = getString(R.string.Producto_no_recibido);
                        referencia_actividad = id_pedido;
                    }
                    visibilidad = id_vendedor + "";

                    registrar_como_venta_bodega();
                    agregar_al_inventario();

                }
                return false;
            }

        });

        return vista;
    }

    public void registrar_como_venta_bodega() {



        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Query dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_vendedor).limitToFirst(1);
        dataQuery.keepSynced(true);
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_usuario usuario = userSnapshot.getValue(Modelo_usuario.class);

                        if (usuario.getTipo().equals(context.getString(R.string.Diamante))) {

                            String fecha_hora =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
                            String id=UUID.randomUUID().toString();
                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                            Modelo_factura_cliente factura_cliente = new Modelo_factura_cliente();
                            factura_cliente.setId(id);
                            factura_cliente.setTipo(context.getString(R.string.Venta_bodega));
                            factura_cliente.setNombre(usuario.getNombre());
                            factura_cliente.setTelefono(usuario.getTelefono());
                            factura_cliente.setDocumento(usuario.getDocumento());
                            factura_cliente.setFecha(fecha_hora);
                            factura_cliente.setCliente_diamante("true");
                            factura_cliente.setDireccion(usuario.getDireccion());
                            factura_cliente.setId_vendedor("Bodega");

                            factura_cliente.setVendedor(MainActivity.Usuario);
                            myRefe.child("Factura_cliente").child(factura_cliente.getId()).setValue(factura_cliente);


                            for (int x = 0; x < lista_productos_a_facturar.size(); x++) {

                                String id_referencia_producto=lista_productos_recibidos.get(x).getId_referencia_producto();
                               // Toast.makeText(getContext(), ""+lista_productos_a_facturar.get(x).getCantidad(), Toast.LENGTH_SHORT).show();

                                Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_referencia_producto).limitToFirst(1);

                                referencia_id_producto.keepSynced(true);
                                try {
                                    Thread.sleep(1 * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(referencia_id_producto!=null) {

                                    int finalX = x;
                                    referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if(snapshot.exists()) {

                                                for (DataSnapshot ds : snapshot.getChildren()) {

                                                    Modelo_producto producto = ds.getValue(Modelo_producto.class);

                                                    String nombre= producto.getNombre();
                                                    String referencia_producto=id_referencia_producto;
                                                    String vendedor_producto=MainActivity.Id_Usuario + "-" + id_referencia_producto;
                                                    String costo_compra=producto.getP_compra();
                                                    String precio_venta=producto.getP_diamante();
                                                    String categoria=producto.getCliente_mis_productos();
                                                    String url=producto.getUrl();
//                                                   int cantidad_seleccionada = Integer.parseInt(lista_productos_a_facturar.get(finalX).getCantidad());
//

                                                    //guardar datos de productos facturados
                                                    DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

                                                    Modelo_producto_facturacion_app_vendedor productos = new Modelo_producto_facturacion_app_vendedor();
                                                    productos.setId_pedido(id);
                                                    productos.setId_producto_pedido(UUID.randomUUID().toString());
                                                    productos.setNombre(nombre);
                                                    // productos.setCodigo_barras(MainActivity.lista_seleccion.get(finalX1).getCodigo());
                                                    productos.setId_referencia_vendedor(MainActivity.Id_Usuario);
                                                    productos.setCantidad(lista_productos_a_facturar.get(finalX).getCantidad());
                                                    productos.setEstado(context.getString(R.string.Venta_bodega));
                                                    productos.setId_referencia_producto(referencia_producto);
                                                    productos.setVendedor_producto(vendedor_producto);
                                                    productos.setFecha(fecha_hora.substring(0,10));
                                                    productos.setCliente_mis_productos(categoria);
                                                    productos.setCosto(costo_compra);
                                                    productos.setVenta(precio_venta);
                                                    productos.setRecaudo(MainActivity.Usuario);
                                                    productos.setId_administrador_recaudo(MainActivity.Id_Usuario);
                                                    productos.setFecha_recaudo(fecha_hora.substring(0,10));
                                                    productos.setNombre_vendedor("Bodega");
                                                    productos.setUrl(url);
                                                    productos.setVendedor(MainActivity.Usuario);

                                                    referencia.child("Factura_productos").child(productos.getId_producto_pedido()).setValue(productos);


//
//                                                    descripcion_compra= descripcion_compra + "\n" +"x"+cantidad_seleccionada+" $"+costo_compra+" "+nombre;




                                                }


                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }













                        }
                    }
                } else {

                    Toast.makeText(context,getString(R.string.Producto_inexistente) , Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            }

        });

        }

    private void agregar_al_inventario() {

        metodo.eliminar_pedido_enviado(lista_productos_recibidos);
        metodo.verificar_existencia_inventario(lista_productos_recibidos,referencia_vendedor,context);
        metodo.guardar_historial(referencia_actividad,actividad,visibilidad,agregando_inventario,context);
        Navigation.findNavController(vista).navigateUp();

    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.opcion_compartir_logo.setVisible(true);
        lista_productos_recibidos.clear();
        MainActivity.opcion_agregar_inventario.setVisible(false);
        cargar_pedido_enviado(id_pedido);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.opcion_compartir_logo.setVisible(false);
        MainActivity.opcion_agregar_inventario.setVisible(false);
        vista=null;
    }

    public void cargar_pedido_enviado(String id_pedido) {

        try {


            cargado = 0;
            //cargar id pedidos y cantidades
            Query referencia = FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_pedido").equalTo(id_pedido);

            if (referencia != null) {
                referencia.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        cargado = cargado + 1;
                        if (cargado > 1) {
                            referencia.removeEventListener(this);
                            lista_productos.clear();

                            recview_pedidos.setVisibility(View.VISIBLE);
                            textView_recibido.setVisibility(View.VISIBLE);
                            myShimmerTextView.setVisibility(View.GONE);

                            RecyclerViewAdaptador_producto_enviado adapador = new RecyclerViewAdaptador_producto_enviado(lista_productos);
                            recview_pedidos.setAdapter(adapador);

                            try {
                                Thread.sleep(1 * 500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            cargar_pedido_enviado(id_pedido);
                            return;
                        }
                        lista_producto_pedido = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                id_vendedor = ds.getValue(Modelo_pedido.class).getReferencia_vendedor();
                                lista_producto_pedido.add(ds.getValue(Modelo_pedido.class));
                            }

                            lista_productos = new ArrayList<>();
                            //cargar datos del producto
                            for (int x = 0; x < lista_producto_pedido.size(); x++) {
                                Query referencia2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_producto_pedido.get(x).getReferencia_producto()).limitToFirst(1);
                                referencia2.keepSynced(true);
                                try {
                                    Thread.sleep(1 * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String id_producto_buscado = lista_producto_pedido.get(x).getReferencia_producto();
                                if (referencia2 != null) {
                                    int finalX = x;
                                    referencia2.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    lista_productos.add(new Modelo_producto_facturacion_app_vendedor(ds.getValue(Modelo_producto.class).getNombre(), "", ds.getValue(Modelo_producto.class).getP_detal(), ds.getValue(Modelo_producto.class).getUrl(), lista_producto_pedido.get(finalX).getCantidad(), lista_producto_pedido.get(finalX).getId_pedido(), lista_producto_pedido.get(finalX).getId_producto_pedido(), id_producto_buscado, lista_producto_pedido.get(finalX).getEstado(), lista_producto_pedido.get(finalX).getReferencia_vendedor(), lista_producto_pedido.get(finalX).getReferencia_vendedor() + "-" + id_producto_buscado, "", "", ds.getValue(Modelo_producto.class).getCliente_mis_productos(), ds.getValue(Modelo_producto.class).getMis_productos(), ds.getValue(Modelo_producto.class).getCodigo(), MainActivity.Usuario, "", "", "", ""));
                                                }
                                                RecyclerViewAdaptador_producto_enviado adapador = new RecyclerViewAdaptador_producto_enviado(lista_productos);
                                                recview_pedidos.setAdapter(adapador);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                        } else {
                            try {
                                recview_pedidos.setVisibility(View.GONE);
                                textView_recibido.setVisibility(View.GONE);
                                myShimmerTextView.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error "+e);
            Navigation.findNavController(vista).navigateUp();
            Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
        }

    }


}