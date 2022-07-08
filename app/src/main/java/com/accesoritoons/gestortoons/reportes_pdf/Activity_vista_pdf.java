package com.accesoritoons.gestortoons.reportes_pdf;

import static android.content.ContentValues.TAG;

import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.bodega.Fragment_crear_factura;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_compras;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_garantias;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_inventario_productos;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_pedidos_enviados;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_por_recaudo;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_recaudados;
import com.accesoritoons.gestortoons.modelos.Modelo_reporte_ganancias;
import com.accesoritoons.gestortoons.modelos.Modelo_reporte_ganancias_lista_pdf;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;

import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_producto_enviado;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Activity_vista_pdf extends AppCompatActivity implements PDFUtility_ganancias.OnDocumentClose,PDFUtility_por_recaudo.OnDocumentClose, PDFUtility_inventario.OnDocumentClose,PDFUtility_recaudado.OnDocumentClose, PDFUtility_compras.OnDocumentClose,PDFUtility_pedidos_enviados.OnDocumentClose,PDFUtility_garantias.OnDocumentClose,PDFUtility_pedido.OnDocumentClose {


    private PDFView pdfView;
    Context context;
    private File file;
    int subtotal =0;
    int total_costos=0;
    int total_registro =0;
    String tipo_vendedor, nombre_vendedor;
    public static GraphView grafico=null;
    public static String desde, hasta, vendedor, id_vendedor, tipo_reporte;
    String id_pedido;
    int total_inventario=0;
    Date fecha_registro=null;
    Date fecha_desde=null;
    Date fecha_hasta=null;
    String perfil_cliente="";
    List<String[]> productos_para_pdf = new ArrayList<>();
    ArrayList<Modelo_factura_cliente>  datos_cliente = new ArrayList<>();
    private MenuItem opcion_compartir;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<Modelo_reporte_ganancias_lista_pdf> registros=new ArrayList<>() ;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_inventario=new ArrayList<>();
    ArrayList<Modelo_registro_inventario_productos> lista_registros=new ArrayList<>();
    ArrayList<Modelo_registro_por_recaudo> lista_por_recaudo=new ArrayList<>();
    ArrayList<Modelo_registro_recaudados> lista_recaudados=new ArrayList<>();
    ArrayList<Modelo_registro_compras> lista_compras=new ArrayList<>();
    ArrayList<Modelo_registro_garantias> lista_garantias=new ArrayList<>();
    ArrayList<Modelo_registro_pedidos_enviados> registros_pedidos=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_ganancias);
        context=getApplicationContext();
        pdfView=(PDFView)findViewById(R.id.vista_pdf);

        try {
            MainActivity.progressDialog = ProgressDialog.show(this, getString(R.string.Creando_reporte),
                    getString(R.string.Un_Momento), true);
        }catch (Exception e){

        }



        try {
            Bundle parametros = this.getIntent().getExtras();
            if(parametros !=null){
                tipo_reporte=parametros.getString("tipo_reporte").trim();
                desde = parametros.getString("desde");
                hasta = parametros.getString("hasta");
                if(parametros.getString("vendedor")!=null) vendedor = parametros.getString("vendedor").trim();
                if(parametros.getString("id_pedido")!=null)id_pedido=parametros.getString("id_pedido").trim();
                if(parametros.getString("id_vendedor")!=null)id_vendedor = parametros.getString("id_vendedor").trim();

            }

            if (tipo_reporte.equals("Ganancias"))  cargar_datos_ganancias();

            if (tipo_reporte.equals("Inventario")){
                if(vendedor.equals("Bodega")){
                    cargar_invetario_bodega();
                }else{
                    cargar_inventario(id_vendedor);
                }
            }
            if (tipo_reporte.equals("Recaudados"))  cargar_recaudados();
            if (tipo_reporte.equals("Por recaudo"))  cargar_por_recaudo();
            if (tipo_reporte.equals("Compras"))  cargar_compras();
            if (tipo_reporte.equals("Pedidos enviados"))  cargar_pedidos_enviados();
            if (tipo_reporte.equals("Garantias")) cargar_garantias();

            if(tipo_reporte.equals("Pedido del cliente"))cargar_pedido_cliente();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error "+e);
            Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void cargar_pedido_cliente() {





        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Query dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_vendedor).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        //datos para la factura pdf
                        perfil_cliente=userSnapshot.getValue(Modelo_usuario.class).getTipo();
                        datos_cliente.add(new Modelo_factura_cliente(id_pedido,userSnapshot.getValue(Modelo_usuario.class).getNombre(),userSnapshot.getValue(Modelo_usuario.class).getTelefono(),userSnapshot.getValue(Modelo_usuario.class).getDocumento(),desde,MainActivity.Id_Usuario,"",userSnapshot.getValue(Modelo_usuario.class).getDireccion(),MainActivity.Usuario,""));


                        //cargar id pedidos y cantidades
                        Query referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_pedido").equalTo(id_pedido);

                        if(referencia!=null){
                            referencia.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    ArrayList<Modelo_pedido>  lista_producto_pedido=new ArrayList<>();
                                    if (snapshot.exists()){
                                        for(DataSnapshot ds:snapshot.getChildren()){
                                            lista_producto_pedido.add(ds.getValue(Modelo_pedido.class));
                                        }


                                        //cargar datos del producto
                                        for (int x = 0; x < lista_producto_pedido.size(); x++) {
                                            Query referencia2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_producto_pedido.get(x).getReferencia_producto()).limitToFirst(1);
                                            referencia2.keepSynced(true);
                                            try {
                                                Thread.sleep(1 * 1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (referencia2 != null) {
                                                int finalX = x;
                                                int finalX1 = x;
                                                referencia2.addListenerForSingleValueEvent(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                                int costo=0;
                                                                if(perfil_cliente.equals(context.getString(R.string.Plata))){
                                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_plantino()));
                                                                }else if(perfil_cliente.equals(context.getString(R.string.Oro))){
                                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_oro()));
                                                                }if(perfil_cliente.equals(context.getString(R.string.Diamante))){
                                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_diamante()));
                                                                }
                                                                subtotal=Integer.parseInt(lista_producto_pedido.get(finalX1).getCantidad())*costo;
                                                                total_costos=total_costos+subtotal;
                                                                productos_para_pdf.add(new String[] {ds.getValue(Modelo_producto.class).getNombre(), lista_producto_pedido.get(finalX1).getCantidad()+"",costo+"",subtotal+""});
                                                            }
                                                            if (finalX+1==lista_producto_pedido.size()) {


                                                                crear_pdf();

                                                            }

                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(getApplicationContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }



                                        }



                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }



                    }
                } else {
                    Toast.makeText(context,getString(R.string.problemas_conexion) , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            }
        });





    }

    private void cargar_garantias() {

        Query referencia_productos=null;

        referencia_productos = FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("estado").equalTo("Garantía");

        referencia_productos.keepSynced(true);   
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia_productos!=null){
            referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_garantias.clear();
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        boolean registro_calificado=false;
                        if(vendedor.equals("Todos")){
                            registro_calificado=true;
                        }else {
                            if (!ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("Pendiente")){
                                if (ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_administrador_recaudo().equals(id_vendedor)) registro_calificado=true;
                            }
                        }
                            if (registro_calificado==true){
                                if (!ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("No aplica")) {
                                    try {
                                        fecha_registro = dateFormat.parse(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha());
                                        if (desde.equals("")) desde = "1980-01-01";
                                        if (hasta.equals("")) hasta = "3000-01-01";
                                        fecha_desde = dateFormat.parse(desde);
                                        fecha_hasta = dateFormat.parse(hasta);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    //bandera para determinar si el registro esta en el rango de fechas
                                    boolean bandera = false;
                                    if (fecha_registro.compareTo(fecha_desde) > 0 && fecha_registro.compareTo(fecha_hasta) < 0) {
                                        bandera = true;
                                    } else if (fecha_registro.compareTo(fecha_desde) == 0 || fecha_registro.compareTo(fecha_hasta) == 0)
                                        bandera = true;


                                    if (bandera == true) {
                                        String fecha = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha_recaudo();
                                        String ref = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_pedido().substring(0, 5);
                                        String nombre = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre();
                                        String id_administrador_recaudo = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_administrador_recaudo();
                                        String nombre_administrador_recaudo = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo();
                                        String nombre_vendedor = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor();
                                        String id_vendedor = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_referencia_vendedor();
                                        String p_compra = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getP_compra();

                                        if (nombre_administrador_recaudo.equals("Pendiente")){
                                            lista_garantias.add(new Modelo_registro_garantias(fecha, ref, nombre, p_compra + "", id_vendedor, nombre_vendedor, nombre_administrador_recaudo, "0"));
                                        }else {
                                            lista_garantias.add(new Modelo_registro_garantias(fecha, ref, nombre, p_compra + "", id_vendedor, nombre_vendedor, nombre_administrador_recaudo, "1"));
                                        }
                                    }
                                }
                            }

                    }

                    if (lista_garantias.size()==0){
                        finish();
                        Toast.makeText(context, "No hay garantías", Toast.LENGTH_SHORT).show();
                    }else{
                        crear_pdf();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void cargar_pedidos_enviados() {

        ArrayList<Modelo_pedido> lista_producto_pedido=new ArrayList<>();
        Query referencia=null;
        if(vendedor.equals("Todos")){
             referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("referencia_vendedor");
        }else if(vendedor.equals("Bodega")){
            finish();
            Toast.makeText(context, "Bodega genera este registro", Toast.LENGTH_SHORT).show();
        }else{
             referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("referencia_vendedor").equalTo(id_vendedor);
        }

                referencia.keepSynced(true);               
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };

        if(referencia!=null){
            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {



                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista_producto_pedido.add(ds.getValue(Modelo_pedido.class));
                        }


                        //cargar datos del producto
                        for (int x = 0; x < lista_producto_pedido.size(); x++) {
                            Query referencia2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_producto_pedido.get(x).getReferencia_producto()).limitToFirst(1);
                            referencia2.keepSynced(true);
                            try {
                                Thread.sleep(1 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (referencia2 != null) {
                                int finalX = x;
                                referencia2.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                int costo= Integer.parseInt(ds.getValue(Modelo_producto.class).getP_compra());
                                                int cantidad= Integer.parseInt(lista_producto_pedido.get(finalX).getCantidad());
                                                int subtotal=costo*cantidad;
                                                registros_pedidos.add(new Modelo_registro_pedidos_enviados(lista_producto_pedido.get(finalX).getFecha().substring(0,10),ds.getValue(Modelo_producto.class).getNombre(),cantidad+"",costo+"",subtotal+"",lista_producto_pedido.get(finalX).getReferencia_vendedor(),lista_producto_pedido.get(finalX).getNombre_vendedor()));
                                            }
                                            if (lista_producto_pedido.size()== finalX +1 ){
                                                crear_pdf();
                                            }

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Activity_vista_pdf.this, "Error en conexion", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }else{
                        finish();
                        Toast.makeText(context, "No hay pedidos", Toast.LENGTH_SHORT).show();

                    }



                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Activity_vista_pdf.this, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void cargar_compras() {
        Query referencia_productos=null;
        referencia_productos= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("estado").equalTo("Compra");

        referencia_productos.keepSynced(true);   
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia_productos!=null){
            referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_compras.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){

                        Modelo_registro_compras compras = ds.getValue(Modelo_registro_compras.class);

                        if(vendedor.equals("Todos")){

                            try {
                                fecha_registro = dateFormat.parse(compras.getFecha());
                                if (desde.equals("")) desde = "1980-01-01";
                                if (hasta.equals("")) hasta = "3000-01-01";
                                fecha_desde = dateFormat.parse(desde);
                                fecha_hasta = dateFormat.parse(hasta);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //bandera para determinar si el registro esta en el rango de fechas
                            boolean bandera = false;
                            if (fecha_registro.compareTo(fecha_desde) > 0 && fecha_registro.compareTo(fecha_hasta) < 0) {
                                bandera = true;
                            } else if (fecha_registro.compareTo(fecha_desde) == 0 || fecha_registro.compareTo(fecha_hasta) == 0)
                                bandera = true;


                            if (bandera == true) {
                                int costo = Integer.parseInt(compras.getCosto());
                                int cantidad = Integer.parseInt(compras.getCantidad());
                                int subtotal = costo * cantidad;
                                compras.setSubtotal(subtotal + "");
                                lista_compras.add(compras);
                            }
                        }else{
                            if(compras.getId_referencia_vendedor().equals(id_vendedor)){
                                try {
                                    fecha_registro = dateFormat.parse(compras.getFecha());
                                    if (desde.equals("")) desde = "1980-01-01";
                                    if (hasta.equals("")) hasta = "3000-01-01";
                                    fecha_desde = dateFormat.parse(desde);
                                    fecha_hasta = dateFormat.parse(hasta);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //bandera para determinar si el registro esta en el rango de fechas
                                boolean bandera = false;
                                if (fecha_registro.compareTo(fecha_desde) > 0 && fecha_registro.compareTo(fecha_hasta) < 0) {
                                    bandera = true;
                                } else if (fecha_registro.compareTo(fecha_desde) == 0 || fecha_registro.compareTo(fecha_hasta) == 0)
                                    bandera = true;


                                if (bandera == true) {
                                    int costo = Integer.parseInt(compras.getCosto());
                                    int cantidad = Integer.parseInt(compras.getCantidad());
                                    int subtotal = costo * cantidad;
                                    compras.setSubtotal(subtotal + "");
                                    lista_compras.add(compras);
                                }

                            }
                        }
                    }
                    if (lista_compras.size()==0){
                        finish();
                        Toast.makeText(context, "No hay compras", Toast.LENGTH_SHORT).show();
                    }else{
                        crear_pdf();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void cargar_por_recaudo() {
        Query referencia_productos=null;
        if(vendedor.equals("Todos")){
            referencia_productos= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_referencia_vendedor");
        }else{
            referencia_productos= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_referencia_vendedor").equalTo(id_vendedor);
        }
        referencia_productos.keepSynced(true);   
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia_productos!=null){
            referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_recaudados.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Ventas") || ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Ventas bodega")|| ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Garantía")){
                            if(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("Pendiente")){
                                String fecha=ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha();
                                String ref=ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_pedido().substring(0,5);
                                String nombre=ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre();
                                int cantidad= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
                                if(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Garantía")){
                                    lista_por_recaudo.add(new Modelo_registro_por_recaudo(fecha,ref, nombre, cantidad+"", "0","0",ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_referencia_vendedor(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor()));
                                }else{
                                    cantidad= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
                                    int recaudo= Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCosto());
                                    int subtotal= cantidad*recaudo;
                                    lista_por_recaudo.add(new Modelo_registro_por_recaudo(fecha,ref, nombre, cantidad+"", recaudo+"",subtotal+"",ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_referencia_vendedor(),ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor()));
                                }

                            }
                        }

                    }
                    if (lista_por_recaudo.size()==0){
                        finish();
                        Toast.makeText(context, "No hay por recaudar", Toast.LENGTH_SHORT).show();
                    }else{
                        crear_pdf();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void cargar_recaudados() {
        Query referencia_productos=null;


        if(vendedor.equals("Todos")) {
            referencia_productos = FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_administrador_recaudo");
        }else{
            referencia_productos = FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_administrador_recaudo").equalTo(id_vendedor);
        }
        referencia_productos.keepSynced(true);
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia_productos!=null){
            referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_recaudados.clear();
                    for(DataSnapshot ds:snapshot.getChildren()) {
                            if(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Ventas")||ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Ventas bodega")||ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Garantía")){
                                if (!ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("No aplica") && !ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("Pendiente")) {
                                    try {
                                        fecha_registro = dateFormat.parse(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha_recaudo());
                                        if (desde.equals("")) desde = "1980-01-01";
                                        if (hasta.equals("")) hasta = "3000-01-01";
                                        fecha_desde = dateFormat.parse(desde);
                                        fecha_hasta = dateFormat.parse(hasta);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    //bandera para determinar si el registro esta en el rango de fechas
                                    boolean bandera = false;
                                    if (fecha_registro.compareTo(fecha_desde) > 0 && fecha_registro.compareTo(fecha_hasta) < 0) {
                                        bandera = true;
                                    } else if (fecha_registro.compareTo(fecha_desde) == 0 || fecha_registro.compareTo(fecha_hasta) == 0)
                                        bandera = true;


                                    if (bandera == true) {
                                        String fecha = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha_recaudo();
                                        String ref = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_pedido().substring(0, 5);
                                        String nombre = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre();
                                        String id_administrador_recaudo = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_administrador_recaudo().trim();
                                        String nombre_administrador_recaudo = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo();
                                        String nombre_vendedor = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor();
                                        String id_vendedor = ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_referencia_vendedor();
                                        int recaudo = 0;
                                        if( ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals("Garantía")){
                                            recaudo=0;
                                        }else{
                                            if (nombre_vendedor.equals("Bodega")) {
                                                recaudo = Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getVenta());
                                            } else {
                                                recaudo = Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCosto());
                                            }
                                        }
                                        int cantidad = Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
                                        int subtotal = cantidad * recaudo;
                                        lista_recaudados.add(new Modelo_registro_recaudados(fecha, ref, nombre, cantidad + "", recaudo + "", subtotal + "", id_vendedor, nombre_vendedor, nombre_administrador_recaudo, id_administrador_recaudo));
                                    }
                                }
                            }

                        }

                    if (lista_recaudados.size()==0){
                        finish();
                        Toast.makeText(context, "No hay recaudos", Toast.LENGTH_SHORT).show();
                    }else{
                        crear_pdf();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    public void cargar_inventario(String id_vendedor) {
        total_inventario=0;

        Query referencia_productos=null;
        if(vendedor.equals("Todos")){
            referencia_productos= FirebaseDatabase.getInstance().getReference().child("Inventarios");
        }else{
            referencia_productos= FirebaseDatabase.getInstance().getReference().child("Inventarios").orderByChild("estado").equalTo("Inventario"+"-"+id_vendedor);
        }

        referencia_productos.keepSynced(true);   
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia_productos!=null){
            referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_inventario.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                       if(!ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getRecaudo().equals("No aplica")) lista_inventario.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));
                    }

                    if(lista_inventario.size()==0){
                        finish();
                        Toast.makeText(context, "No hay inventario", Toast.LENGTH_SHORT).show();
                    }
                    total_inventario=0;

                    for (int x = 0; x < lista_inventario.size(); x++) {
                        Query referencia_productos2;
                        referencia_productos2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_inventario.get(x).getId_referencia_producto());
                        referencia_productos2.keepSynced(true);
                        try {
                            Thread.sleep(1 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (referencia_productos2 != null) {
                            int finalX = x;

                            int finalX1 = x;
                            int finalX2 = x;
                            referencia_productos2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    lista_registros.clear();
                                    if (snapshot.exists()) {

                                            for (DataSnapshot ds : snapshot.getChildren()) {


                                                int cantidad=  Integer.parseInt(lista_inventario.get(finalX).getCantidad());
                                                int costo=0;

                                                costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_compra()));
                                                int subtotal=cantidad*costo;


                                                total_inventario=total_inventario+(cantidad*costo);


                                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                                Query dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(lista_inventario.get(finalX1).getId_referencia_vendedor()).limitToFirst(1);
                                                int finalCosto = costo;
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
                                                                lista_registros.add(new Modelo_registro_inventario_productos(ds.getValue(Modelo_producto.class).getNombre(), cantidad+"", finalCosto +"",subtotal+"",lista_inventario.get(finalX).getId_referencia_vendedor(),userSnapshot.getValue(Modelo_usuario.class).getNombre(),userSnapshot.getValue(Modelo_usuario.class).getTipo()  ));

                                                                if (lista_inventario.size()== finalX1 +1 ){

                                                                    if (vendedor.equals("Todos")){
                                                                        cargar_invetario_bodega();
                                                                    }else{
                                                                        crear_pdf();
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                            }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void cargar_invetario_bodega(){

            Query referencia=FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("cliente_mis_productos").equalTo("Accesory Toons");
                referencia.keepSynced(true);               
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
        if(referencia!=null){
            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){
                            int finalCosto= Integer.parseInt(ds.getValue(Modelo_producto.class).getP_compra());
                            int subtotal=Integer.parseInt(ds.getValue(Modelo_producto.class).getCantidad())*finalCosto;
                            lista_registros.add(new Modelo_registro_inventario_productos(ds.getValue(Modelo_producto.class).getNombre(), ds.getValue(Modelo_producto.class).getCantidad(), finalCosto +"",subtotal+"","1","Accesory Toons","Bodega"));
                        }
                        crear_pdf();

                    }else{
                        finish();
                        Toast.makeText(context, "No hay inventario", Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Activity_vista_pdf.this, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void cargar_datos_ganancias() {
        Query referencia;

        if (vendedor.equals("Todos")){
            referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos");
        }else if(vendedor.equals("Bodega")){
            referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("nombre_vendedor").equalTo("Bodega");
        }else {
            referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_referencia_vendedor").equalTo(id_vendedor);
        }
                referencia.keepSynced(true);               

        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(referencia!=null){
            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                            referencia.keepSynced(true);               
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
                    if (snapshot.exists()){

                            registros.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if( !ds.getValue(Modelo_reporte_ganancias.class).getEstado().equals("Garantía")&& !ds.getValue(Modelo_reporte_ganancias.class).getEstado().equals("Compra")) {


                                if (!ds.getValue(Modelo_reporte_ganancias.class).getRecaudo().equals("No aplica")) {
                                    try {
                                        fecha_registro = dateFormat.parse(ds.getValue(Modelo_reporte_ganancias.class).getFecha());
                                        if (desde.equals("")) desde = "1980-01-01";
                                        if (hasta.equals("")) hasta = "3000-01-01";
                                        fecha_desde = dateFormat.parse(desde);
                                        fecha_hasta = dateFormat.parse(hasta);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e(TAG,"Error "+e);
                                        Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    //bandera para determinar si el registro esta en el rango de fechas
                                    boolean bandera = false;
                                    if (fecha_registro.compareTo(fecha_desde) > 0 && fecha_registro.compareTo(fecha_hasta) < 0) {
                                        bandera = true;
                                    } else if (fecha_registro.compareTo(fecha_desde) == 0 || fecha_registro.compareTo(fecha_hasta) == 0)
                                        bandera = true;

                                    if (bandera == true) {
                                        int costo = Integer.parseInt(ds.getValue(Modelo_reporte_ganancias.class).getCosto());
                                        int venta = Integer.parseInt(ds.getValue(Modelo_reporte_ganancias.class).getVenta());
                                        int cant = Integer.parseInt(ds.getValue(Modelo_reporte_ganancias.class).getCantidad());
                                        String estado = ds.getValue(Modelo_reporte_ganancias.class).getEstado();
                                        int ganancia = 0;

                                        if (estado.equals("Ventas")) {
                                            //cuando el recaudo no aplica es porque el producto es indempendiente de el vendedor .

                                            int p_compra = Integer.parseInt(ds.getValue(Modelo_reporte_ganancias.class).getP_compra());
                                            ganancia = (costo - p_compra) * cant;
                                            subtotal = subtotal + (costo * cant);
                                            total_costos = total_costos + (p_compra * cant);
                                            registros.add(new Modelo_reporte_ganancias_lista_pdf(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_pedido(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre(), cant + "", p_compra + "", costo + "", ganancia + "", ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_referencia_vendedor(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor()));

                                            // }

                                        } else if (estado.equals("Ventas bodega")) {
                                            ganancia = (venta - costo) * cant;
                                            subtotal = subtotal + (venta * cant);
                                            total_costos = total_costos + (costo * cant);
                                            registros.add(new Modelo_reporte_ganancias_lista_pdf(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getFecha(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getId_pedido(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre(), cant + "", costo + "", venta + "", ganancia + "", "1", ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre_vendedor()));
                                        }

                                        total_registro = total_registro + ganancia;

                                    }
                                }
                            }
                        }
                        if (registros.size()==0){
                            Toast.makeText(Activity_vista_pdf.this,"No hay registros" , Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            crear_pdf();
                        }

                    }else{
                        Toast.makeText(Activity_vista_pdf.this,"No hay registros" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void crear_pdf() {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Reporte_Toons.pdf";
        try {
            if(tipo_reporte.equals("Ganancias")){
                if(vendedor.equals("Todos")) registros.sort(Comparator.comparing(Modelo_reporte_ganancias_lista_pdf::getId_vendedor)); else registros.sort(Comparator.comparing(Modelo_reporte_ganancias_lista_pdf::getNombre));
                PDFUtility_ganancias crear =new PDFUtility_ganancias();
                crear.createPdf(this, Activity_vista_pdf.this,registros, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Inventario")){
                if(vendedor.equals("Todos")) lista_registros.sort(Comparator.comparing(Modelo_registro_inventario_productos::getId_vendedor)); else lista_registros.sort(Comparator.comparing(Modelo_registro_inventario_productos::getNombre));
                PDFUtility_inventario crear =new PDFUtility_inventario();
                crear.createPdf(this, Activity_vista_pdf.this,lista_registros, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Por recaudo")){
                if(vendedor.equals("Todos")) lista_por_recaudo.sort(Comparator.comparing(Modelo_registro_por_recaudo::getId_vendedor)); else lista_por_recaudo.sort(Comparator.comparing(Modelo_registro_por_recaudo::getNombre));
                PDFUtility_por_recaudo crear =new PDFUtility_por_recaudo();
                crear.createPdf(this, Activity_vista_pdf.this,lista_por_recaudo, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Recaudados")){
                if(vendedor.equals("Todos"))lista_recaudados.sort(Comparator.comparing(Modelo_registro_recaudados::getId_administrador_recaudo)); else lista_recaudados.sort(Comparator.comparing(Modelo_registro_recaudados::getNombre));
                PDFUtility_recaudado crear =new PDFUtility_recaudado();
                crear.createPdf(this, Activity_vista_pdf.this,lista_recaudados, total_costos+"", subtotal +"", total_registro +"",path,true);
            }

            if(tipo_reporte.equals("Compras")){
                if(vendedor.equals("Todos")) lista_compras.sort(Comparator.comparing(Modelo_registro_compras::getId_referencia_vendedor)); else lista_compras.sort(Comparator.comparing(Modelo_registro_compras::getNombre));
                PDFUtility_compras crear =new PDFUtility_compras();
                crear.createPdf(this, Activity_vista_pdf.this,lista_compras, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Pedidos enviados")){
                if(vendedor.equals("Todos")) registros_pedidos.sort(Comparator.comparing(Modelo_registro_pedidos_enviados::getId_vendedor)); else registros_pedidos.sort(Comparator.comparing(Modelo_registro_pedidos_enviados::getNombre));
                PDFUtility_pedidos_enviados crear =new PDFUtility_pedidos_enviados();
                crear.createPdf(this, Activity_vista_pdf.this,registros_pedidos, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Garantias")){
                if(vendedor.equals("Todos")) lista_garantias.sort(Comparator.comparing(Modelo_registro_garantias::getId_administrador_recaudo)); else lista_garantias.sort(Comparator.comparing(Modelo_registro_garantias::getNombre));
                PDFUtility_garantias crear =new PDFUtility_garantias();
                crear.createPdf(this, Activity_vista_pdf.this,lista_garantias, total_costos+"", subtotal +"", total_registro +"",path,true);
            }
            if(tipo_reporte.equals("Pedido del cliente")){

                PDFUtility_pedido crear =new PDFUtility_pedido();
                crear.crearPdf(this, Activity_vista_pdf.this, productos_para_pdf, datos_cliente,total_costos+"",path,true);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG,"Error "+e);
            Toast.makeText(this,"Error Creating Pdf "+e,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPDFDocumentClose(File file) {
        try {
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .load();
            MainActivity.progressDialog.cancel();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error "+e);
            Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
            finish();
        }

    //    Toast.makeText(this, getString(R.string.PDF_guardado_con_exito)+" "+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Factura_Toons.pdf", Toast.LENGTH_LONG).show();
    }


    public void compartir_pdf(){
        //COMPARTIR EL PDF


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Reporte_Toons.pdf");

        Uri uri = FileProvider.getUriForFile(this, "com.toons.fileprovider", file);
        Intent intent2 = ShareCompat.IntentBuilder.from(this)
                .setType("application/pdf")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            this.startActivity(intent2);
        }catch (Exception e){
            Toast.makeText(this, getString(R.string.No_puede_compartir_pdf)+e, Toast.LENGTH_SHORT).show();
        }
    }




    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // CREA EL MENU CON TRES PUNTICOS
        getMenuInflater().inflate(R.menu.main, menu);
        opcion_compartir=menu.findItem(R.id.compartir);
        opcion_compartir.setVisible(true);

        return true;
    }
    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compartir:
                compartir_pdf();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}