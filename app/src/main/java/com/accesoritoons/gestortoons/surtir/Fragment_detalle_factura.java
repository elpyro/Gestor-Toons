package com.accesoritoons.gestortoons.surtir;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.modelos.Modelo_productos_para_facturar;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_garantias;
import com.accesoritoons.gestortoons.reportes_pdf.PDFUtility_factura_bodega;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.Vista_pdf;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;

import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_productos_facturados;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class Fragment_detalle_factura extends Fragment implements PDFUtility_factura_bodega.OnDocumentClose {
    public static View vista;
    Context context;
    public static TextView textView_telefono,textView_documento,textview_nombre_cliente,textView_id, textView_actividad, textView_fecha,textView_direccion,textView_vendedor;
    String id_factura, nombre, telefono, direccion, documento;
    public static String cliente_diamante="false";
    RecyclerView recyclerView_productos;
    Button button_agregar;

    public static String producto_scaneado="", id_producto_factura;
    public static TextView total_factura;
    int cuenta_total_factura;
    ArrayList<Modelo_factura_cliente>  datos_cliente = new ArrayList<>();
    ArrayList<Modelo_productos_para_facturar> productos_para_pdf = new ArrayList<>();
    public static ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos_facturados;
    String path="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fragment_lista_recaudos.textView_total_recaudos=null;
        context=getContext();
        vista=inflater.inflate(R.layout.fragment_detalle_factura, container, false);
        textView_id=vista.findViewById(R.id.textView_id);
        textView_documento=vista.findViewById(R.id.textView_documento);
        textview_nombre_cliente=vista.findViewById(R.id.textview_nombre_cliente);
        textView_telefono=vista.findViewById(R.id.textView_telefono);
        textView_actividad=vista.findViewById(R.id.texview_actividad);
        total_factura=vista.findViewById(R.id.textView_total);
        textView_fecha=vista.findViewById(R.id.textView_fecha);
        textView_direccion=vista.findViewById(R.id.textView_direccion);
        textView_vendedor=vista.findViewById(R.id.textView_vendedor);
        button_agregar=vista.findViewById(R.id.button_agregar);
        recyclerView_productos=vista.findViewById(R.id.recycler_productos);
        recyclerView_productos.setLayoutManager(new LinearLayoutManager(context));

        button_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.opcion_whatapps.setVisible(false);
                MainActivity.opcion_compartir_logo.setVisible(false);

                Bundle bundle = new Bundle();
                bundle.putString("id_factura", id_factura);

                if (textView_actividad.getText().equals(context.getString(R.string.Compra))){
                    MainActivity.lista_seleccion_venta_mayor_bodega.clear();
                    Navigation.findNavController(view).navigate(R.id.contenedor_compras_bodega,bundle);
                }else if(textView_actividad.getText().equals(context.getString(R.string.Venta_bodega))){
                    MainActivity.lista_seleccion_venta_mayor_bodega.clear();
                    Navigation.findNavController(view).navigate(R.id.fragment_venta_bodega,bundle);
                }


            }
        });

        MainActivity.opcion_compartir_logo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MainActivity.opcion_whatapps.setVisible(false);
                MainActivity.opcion_compartir_logo.setVisible(false);

                crear_pdf();
                Intent intent=new Intent(context, Vista_pdf.class);
                intent.putExtra("path",path);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });

        MainActivity.opcion_whatapps.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                crear_pdf();
                openWhatsApp();
                return false;
            }
        });

        if (getArguments() != null) {
            id_factura =getArguments().getString("id_factura");
            cargar_datos_factura(id_factura);
        }else {
            Toast.makeText(context, context.getString(R.string.Producto_inexistente), Toast.LENGTH_LONG).show();
        }



        return vista;
    }

    private void crear_pdf() {

        //datos para la factura pdf
        datos_cliente.add(new Modelo_factura_cliente(id_factura,textview_nombre_cliente.getText().toString(),textView_telefono.getText().toString().trim(),textView_documento.getText().toString().trim(),textView_fecha.getText().toString(),textView_vendedor.getText().toString(),"",textView_direccion.getText().toString().trim(),textView_vendedor.getText().toString(),""));
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Factura_Toons.pdf";
        try {
            PDFUtility_factura_bodega crear =new PDFUtility_factura_bodega();
            productos_para_pdf.sort(Comparator.comparing(Modelo_productos_para_facturar::getNombre));
            crear.crearPdf(getContext(), Fragment_detalle_factura.this, productos_para_pdf, datos_cliente,cuenta_total_factura+"",path,true);

        }catch (Exception e)
        {
            Toast.makeText(context,"Error Creating Pdf",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MainActivity.opcion_whatapps.setVisible(true);
            MainActivity.opcion_compartir_logo.setVisible(true);
            cargar_productos_facturados(id_factura);
        }catch (Exception e){
            Navigation.findNavController(vista).navigateUp();
        }


    }


    private void cargar_productos_facturados(String id_factura) {
        try {


            Query referencia;
            productos_para_pdf.clear();
            referencia = FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_pedido").equalTo(id_factura);

            if (referencia != null) {
                referencia.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cuenta_total_factura = 0;
                        NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
                        total_factura.setText(context.getString(R.string.Total) + ": " + formatoImporte.format(cuenta_total_factura));
                        lista_productos_facturados = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                for (int x = 0; x < lista_productos_facturados.size(); x++) {
                                    Modelo_producto_facturacion_app_vendedor factura = lista_productos_facturados.get(x);
                                    if (factura.getId_pedido().equals(ds.getValue(Modelo_factura_cliente.class).getId())) {
                                        lista_productos_facturados.remove(x);
                                        break;
                                    }
                                }
                                lista_productos_facturados.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));
                                if (ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals(context.getString(R.string.Venta)) || ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals(context.getString(R.string.Compra)) || ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals(context.getString(R.string.Venta_bodega))|| ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getEstado().equals(context.getString(R.string.Venta_Diamante))) {
                                    int precio = Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getVenta());
                                    int cantidad = Integer.parseInt(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad());
                                    int total = precio * cantidad;

                                    productos_para_pdf.add(new Modelo_productos_para_facturar(ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getNombre(), ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getCantidad() + "", ds.getValue(Modelo_producto_facturacion_app_vendedor.class).getVenta(), total + ""));

                                    cuenta_total_factura = cuenta_total_factura + total;
                                }


                            }
                            //ordenar por fecha
                            lista_productos_facturados.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getEstado));
                            Collections.reverse(lista_productos_facturados);

                        }
                        try {
                            total_factura.setText(context.getString(R.string.Total) + ": " + formatoImporte.format(cuenta_total_factura));
                            RecyclerViewAdaptador_productos_facturados adaptador = new RecyclerViewAdaptador_productos_facturados(lista_productos_facturados);
                            recyclerView_productos.setAdapter(adaptador);
                        }catch (Exception ex){
                            Navigation.findNavController(vista).navigateUp();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
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

    private void openWhatsApp() {

        if (textView_telefono.getText().toString().length()!=10){
            Snackbar.make(vista, context.getString(R.string.Numero_invalido), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        String smsNumber = "57"+textView_telefono.getText().toString().trim();
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            try { Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Factura_Toons.pdf");
                Uri uri = FileProvider.getUriForFile(context, "com.toons.fileprovider", file);

                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
                //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent); } catch(Exception e)
            { Toast.makeText(context, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show(); } } else
        { Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(context, getString(R.string.WhatsApp_no_instalado), Toast.LENGTH_SHORT).show();
            this.startActivity(goToMarket); }
    }



    private boolean whatsappInstalledOrNot(String uri){
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES); app_installed = true;
        } catch (PackageManager.NameNotFoundException e)
        { app_installed = false; } return app_installed;
    }


    public void cargar_datos_factura(String id_factura) {



        Query referencia= FirebaseDatabase.getInstance().getReference().child("Factura_cliente").orderByChild("id").equalTo(id_factura).limitToFirst(1);
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
                    if (snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){
                            textview_nombre_cliente.setText(ds.getValue(Modelo_factura_cliente.class).getNombre());
                            textView_id.setText(ds.getValue(Modelo_factura_cliente.class).getId().substring(0,5));
                            textView_telefono.setText(ds.getValue(Modelo_factura_cliente.class).getTelefono());
                            textView_documento.setText(ds.getValue(Modelo_factura_cliente.class).getDocumento());
                            textView_actividad.setText(ds.getValue(Modelo_factura_cliente.class).getTipo());
                            textView_fecha.setText(ds.getValue(Modelo_factura_cliente.class).getFecha().substring(0,10));
                            textView_vendedor.setText(ds.getValue(Modelo_factura_cliente.class).getVendedor());
                            if(ds.getValue(Modelo_factura_cliente.class).getCliente_diamante()!=null)cliente_diamante="true";

                            nombre=ds.getValue(Modelo_factura_cliente.class).getNombre();
                            telefono=ds.getValue(Modelo_factura_cliente.class).getTelefono();
                            documento=ds.getValue(Modelo_factura_cliente.class).getDocumento();
                            direccion=ds.getValue(Modelo_factura_cliente.class).getDireccion();

                            if (textView_actividad.getText().equals(context.getString(R.string.Venta))||textView_actividad.getText().equals(context.getString(R.string.Venta_Diamante))||!cliente_diamante.equals("false")){
                                button_agregar.setVisibility(View.GONE);
                            }

                            try {
                                textView_direccion.setText(ds.getValue(Modelo_factura_cliente.class).getDireccion());
                            }catch (Exception e){}
                        }

                   }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        total_factura=null;
        Fragment_detalle_factura.producto_scaneado=null;
        Fragment_detalle_factura.textView_actividad=null;
        MainActivity.opcion_whatapps.setVisible(false);
        MainActivity.opcion_compartir_logo.setVisible(false);
        vista=null;
        cliente_diamante="false";

        boolean cambios=false;
        if(nombre!=textview_nombre_cliente.getText().toString().trim())cambios=true;
        if(telefono!=textView_telefono.getText().toString().trim())cambios=true;
        if(documento!=textView_documento.getText().toString().trim())cambios=true;
        if(direccion!=textView_direccion.getText().toString().trim())cambios=true;

        if (cambios==true){
            Guardar_firebase registrar_cambios_cliente=new Guardar_firebase();
            registrar_cambios_cliente.actualizar_datos_cliente_factura(id_factura, textview_nombre_cliente.getText().toString().trim(),textView_telefono.getText().toString().trim(),textView_documento.getText().toString().trim(),textView_direccion.getText().toString().trim());
        }

    }

    @Override
    public void onPDFDocumentClose(File file) {

    }

}