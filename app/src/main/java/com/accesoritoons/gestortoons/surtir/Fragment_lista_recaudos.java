package com.accesoritoons.gestortoons.surtir;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;

import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;

import com.accesoritoons.gestortoons.modelos.Modelo_recaudos_seleccionados;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_productos_facturados;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Fragment_lista_recaudos extends Fragment {
    Context context;
    View vista;
    public static TextView textView_total_recaudos;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_garantias;

    public static ArrayList<Modelo_recaudos_seleccionados> Lista_productos_seleccionados=new ArrayList<>();


    int cuenta_total_factura=0;
    RecyclerView recyclerView_recaudos_pendientes, recycler_garantias;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista=inflater.inflate(R.layout.fragment_lista_recaudos, container, false);
        textView_total_recaudos=vista.findViewById(R.id.textView_total_recaudos);

        recyclerView_recaudos_pendientes=vista.findViewById(R.id.recycler_recaudos_pendientes);
        recyclerView_recaudos_pendientes.setLayoutManager(new LinearLayoutManager(context));

        recycler_garantias=vista.findViewById(R.id.recycler_garantias);
        recycler_garantias.setLayoutManager(new LinearLayoutManager(context));

        MainActivity.opcion_realizar_recaudo.setVisible(true);



        MainActivity.opcion_realizar_recaudo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(Fragment_lista_recaudos.Lista_productos_seleccionados.size()<1) {
                    Toast.makeText(context, context.getString(R.string.Productos_no_selecionados), Toast.LENGTH_SHORT).show();
                }else {
                    //ALERT DIALOGO
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Recaudos");
                    RecyclerViewAdaptador_productos_facturados recuado = new RecyclerViewAdaptador_productos_facturados();

                    //calcular total
                    int precio = 0;
                    int cantidad = 0;
                    int total = 0;
                    int garantias = 0;
                    for (int x = 0; x < Fragment_lista_recaudos.Lista_productos_seleccionados.size(); x++) {
                        if (Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getPrecio().equals("0")) {
                            garantias = garantias + 1;
                        } else {
                            precio = Integer.parseInt(Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getPrecio());
                            cantidad = Integer.parseInt(Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getCantidad());
                            total = total + (precio * cantidad);
                        }

                    }
                    NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es", "ES"));
                    Fragment_lista_recaudos.textView_total_recaudos.setText(context.getString(R.string.Recaudos) + ": " + formatoImporte.format(total) + " " + context.getString(R.string.Garantias) + ": " + garantias);


                    builder.setMessage("Está a punto de recibir un recaudo de: " + formatoImporte.format(total) + " " + context.getString(R.string.Y) + " " + context.getString(R.string.Garantias) + " " + garantias + " ¿Esta seguro?");

                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            registrar_recaudo();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }

        });
        return vista;
    }

    private void registrar_recaudo() {
        for (int x = 0; x < Fragment_lista_recaudos.Lista_productos_seleccionados.size(); x++) {

            Query referencia_id_producto = FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_producto_pedido").equalTo(Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getId());
            referencia_id_producto.keepSynced(true);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (referencia_id_producto != null) {
                int finalX = x;
                referencia_id_producto.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Modelo_producto_facturacion_app_vendedor producto = ds.getValue(Modelo_producto_facturacion_app_vendedor.class);
                                if (MainActivity.tipo_vendedor.equals("Oro")){
                                    producto.setRecaudo("Oro, "+MainActivity.Usuario);
                                }else{
                                    producto.setRecaudo(MainActivity.Usuario);
                                }

                                String fecha=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
                                producto.setFecha_recaudo(fecha.substring(0,10));
                                producto.setId_administrador_recaudo(MainActivity.Id_Usuario);
                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                myRefe.child("Factura_productos").child(producto.getId_producto_pedido()).setValue(producto);
                            }
                        }
                        if(finalX ==Fragment_lista_recaudos.Lista_productos_seleccionados.size()){
                            Guardar_firebase guardar_historial=new Guardar_firebase();
                            guardar_historial.guardar_historial(MainActivity.id_vendedor,context.getString(R.string.Recaudos),MainActivity.id_vendedor,textView_total_recaudos.getText().toString(),context);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
        Navigation.findNavController(vista).navigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.vista_recaudo=true;
        Fragment_lista_recaudos.Lista_productos_seleccionados.clear();
        cargar_productos_facturados();
    }

    private void cargar_productos_facturados() {
       cuenta_total_factura=0;
        textView_total_recaudos.setText(context.getString(R.string.Recaudos_pendientes)+": "+cuenta_total_factura);
        Query referencia;
        referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_referencia_vendedor").equalTo(MainActivity.id_vendedor);

        if(referencia!=null){
            referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_productos=new ArrayList<>();
                    lista_garantias=new ArrayList<>();
                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){

                            Modelo_producto_facturacion_app_vendedor factura = ds.getValue(Modelo_producto_facturacion_app_vendedor.class);
                            if (factura.getRecaudo().equals(context.getString(R.string.Pendiente))&& factura.getEstado().equals(context.getString(R.string.Venta))) {


                                for (int x = 0; x < lista_productos.size(); x++) {
                                    Modelo_producto_facturacion_app_vendedor factura2 =  lista_productos.get(x);
                                    if (factura2.getId_producto_pedido().equals(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido())) {
                                        lista_productos.remove(x);
                                        break;
                                    }
                                }


                                lista_productos.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));

                                int precio, cantidad, total;
                                if (MainActivity.tipo_vendedor.equals("Oro")){
                                    Lista_productos_seleccionados.add(new Modelo_recaudos_seleccionados(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getVenta(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad()));
                                     precio= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getVenta());
                                }else{
                                    Lista_productos_seleccionados.add(new Modelo_recaudos_seleccionados(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCosto(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad()));
                                     precio= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCosto());
                                }

                                cantidad= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
                                total=precio*cantidad;
                                cuenta_total_factura=cuenta_total_factura+total;

                            }else if (factura.getRecaudo().equals(context.getString(R.string.Pendiente))&& factura.getEstado().equals(context.getString(R.string.Garantia))) {
                                for (int x = 0; x < lista_garantias.size(); x++) {
                                    Modelo_producto_facturacion_app_vendedor factura2 =  lista_garantias.get(x);
                                    if (factura2.getId_producto_pedido().equals(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido())) {
                                        lista_garantias.remove(x);
                                        break;
                                    }
                                }
                                Lista_productos_seleccionados.add(new Modelo_recaudos_seleccionados(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_producto_pedido(),"0",ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad()));
                                lista_garantias.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));
                            }


                            }
                    }
                    //ordenar por fecha
                    lista_productos.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getId_pedido));
                    lista_garantias.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getId_pedido));

                    calcular_total();
                    RecyclerViewAdaptador_productos_facturados adaptador= new RecyclerViewAdaptador_productos_facturados(lista_productos);
                    recyclerView_recaudos_pendientes.setAdapter(adaptador);

                    RecyclerViewAdaptador_productos_facturados adaptador_garantias= new RecyclerViewAdaptador_productos_facturados(lista_garantias);
                    recycler_garantias.setAdapter(adaptador_garantias);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void calcular_total() {
        int precio=0;
        int cantidad=0;
        int total=0;
        int garantias=0;

        for (int x = 0; x < Fragment_lista_recaudos.Lista_productos_seleccionados.size(); x++) {
            if(Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getPrecio().equals("0")){
                garantias=garantias+1;
            }else{

                precio= Integer.parseInt( Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getPrecio());
                cantidad= Integer.parseInt( Fragment_lista_recaudos.Lista_productos_seleccionados.get(x).getCantidad());
                total=total+(precio*cantidad);
            }

        }
        NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
        Fragment_lista_recaudos.textView_total_recaudos.setText(context.getString(R.string.Recaudos)+": "+formatoImporte.format(total)+" "+context.getString(R.string.Garantias)+": "+garantias);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Fragment_lista_recaudos.Lista_productos_seleccionados.clear();
        MainActivity.opcion_realizar_recaudo.setVisible(false);
        MainActivity.vista_recaudo=false;
        vista=null;
    }
}