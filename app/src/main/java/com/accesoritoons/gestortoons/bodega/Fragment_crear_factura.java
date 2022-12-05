package com.accesoritoons.gestortoons.bodega;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.modelos.Modelo_productos_para_facturar;
import com.accesoritoons.gestortoons.reportes_pdf.PDFUtility_factura_bodega;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.Vista_pdf;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_ventas_bodega;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class Fragment_crear_factura extends Fragment implements PDFUtility_factura_bodega.OnDocumentClose {


    public static String nombreCliente, telefono, documento, direccion;
    public static TextView textView_total;
    public static RecyclerView recview;
    public static SearchView searchView_produtos;
    View vista;
    Context context;
    private EditText editText_nombre, editText_telefono, editText_documento, editText_direccion;
    ArrayList<Modelo_producto> lista_inventario;
    int total=0;
    ArrayList<Modelo_producto> lista_productos=new ArrayList<>();
    String descripcion_compra="";
    ArrayList<Modelo_productos_para_facturar> productos_para_pdf = new ArrayList<>();
    ArrayList<Modelo_factura_cliente>  datos_cliente = new ArrayList<>();
    RadioButton radioButton_compartir,radioButton_whatapp, radioButton_nignuno;
    ValueEventListener oyente;
    Query referencia_productos;
    private ImageButton imageButton_clientes;
    boolean hiloActivo=false;
    NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
    private  final String PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA = "PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_crear_factura, container, false);
        recview = (RecyclerView) vista.findViewById(R.id.recycler_inventario);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_produtos = (SearchView) vista.findViewById(R.id.searchview);
        editText_nombre=vista.findViewById(R.id.editTextTextPersonName);
        editText_telefono=vista.findViewById(R.id.editTextPhone);
        textView_total=vista.findViewById(R.id.textView_total);
        editText_documento=vista.findViewById(R.id.editTextNumberSigned);
        radioButton_compartir=vista.findViewById(R.id.radioButton_compartir);
        radioButton_whatapp=vista.findViewById(R.id.radioButton_whatapp);
        radioButton_nignuno=vista.findViewById(R.id.radioButton_ninguno);
        editText_direccion =vista.findViewById(R.id.editTextTextMultiLine_direccion);
        imageButton_clientes=vista.findViewById(R.id.imageButton_clientes);


        //ocultar teclado
        try {
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

        }catch (Exception e){}

        imageButton_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(vista).navigate(R.id.fragmentoClientes);
            }
        });

        MainActivity.opcion_confirmar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                try {
                    InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                    input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

                }catch (Exception e){}


                if (radioButton_whatapp.isChecked()){
                    if (editText_telefono.getText().toString().length()!=10){
                        Snackbar.make(vista, context.getString(R.string.Numero_invalido), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return false;
                    }
                }

                if(MainActivity.lista_seleccion_venta_mayor_bodega.size()<1){
                       Toast.makeText(context, context.getString(R.string.Productos_no_selecionados), Toast.LENGTH_SHORT).show();
                }else{

                    total=0;
                    RecyclerViewAdaptador_ventas_bodega.actulizar_total();
                    String fecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
                    String id_factura= UUID.randomUUID().toString();
                    descripcion_compra=textView_total.getText().toString().trim();
                    Context context=getContext();

                    if (!hiloActivo) {
                        hiloActivo = true;


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {

                                    String id_referencia_producto = MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getId();

                                    Query referencia_id_producto = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_referencia_producto);

                                    referencia_id_producto.keepSynced(true);

                                    try {
                                        Thread.sleep(1 * 250);
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

                                                        String nombre = MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getNombre();
                                                        String referencia_producto = id_referencia_producto;
                                                        String vendedor_producto = MainActivity.Id_Usuario + "-" + id_referencia_producto;
                                                        String costo_compra = MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getP_compra();
                                                        String precio_venta = MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getP_detal();
                                                        String categoria = MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getCliente_mis_productos();
                                                        String url = MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getUrl();
                                                        int cantidad_seleccionada = Integer.parseInt(MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getSeleccion());

                                                        //RESTA INVENTARIO
                                                        int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                                                        int Cantidad = 0;
                                                        Cantidad = cantidad_inventario - cantidad_seleccionada;


                                                        //guardar datos de productos facturados
                                                        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

                                                        Modelo_producto_facturacion_app_vendedor productos = new Modelo_producto_facturacion_app_vendedor();
                                                        productos.setId_pedido(id_factura);
                                                        productos.setId_producto_pedido(UUID.randomUUID().toString());
                                                        productos.setNombre(nombre);
                                                        // productos.setCodigo_barras(MainActivity.lista_seleccion.get(finalX1).getCodigo());
                                                        productos.setId_referencia_vendedor(MainActivity.Id_Usuario);
                                                        productos.setCantidad(cantidad_seleccionada + "");
                                                        productos.setEstado(context.getString(R.string.Venta_bodega));
                                                        productos.setId_referencia_producto(referencia_producto);
                                                        productos.setVendedor_producto(vendedor_producto);
                                                        productos.setFecha(fecha.substring(0, 10));
                                                        productos.setCliente_mis_productos(categoria);
                                                        productos.setCosto(costo_compra);
                                                        productos.setVenta(precio_venta);
                                                        productos.setRecaudo(MainActivity.Usuario);
                                                        productos.setId_administrador_recaudo(MainActivity.Id_Usuario);
                                                        productos.setFecha_recaudo(fecha.substring(0, 10));
                                                        productos.setNombre_vendedor("Bodega");
                                                        productos.setUrl(url);
                                                        productos.setVendedor(MainActivity.Usuario);

                                                        //verfica que hay existencia para regisrar el producto
                                                        if (Cantidad > -1) {
                                                            producto.setCantidad(Cantidad + "");
                                                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                                            myRefe.child("Productos").child(producto.getId()).setValue(producto);
                                                            referencia.child("Factura_productos").child(productos.getId_producto_pedido()).setValue(productos);
                                                        }


                                                        descripcion_compra = descripcion_compra + "\n" + "x" + cantidad_seleccionada + " $" + precio_venta + " " + nombre;


                                                        int subtotal = cantidad_seleccionada * Integer.parseInt(precio_venta);
                                                        total = total + subtotal;
                                                        productos_para_pdf.add(new Modelo_productos_para_facturar(nombre, cantidad_seleccionada + "", precio_venta, subtotal + ""));

                                                    }

                                                    if (finalX1 + 1 == MainActivity.lista_seleccion_venta_mayor_bodega.size()) {
                                                        //datos de factura de compra

                                                        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                                        Modelo_factura_cliente factura_cliente = new Modelo_factura_cliente();
                                                        factura_cliente.setId(id_factura);
                                                        factura_cliente.setTipo(context.getString(R.string.Venta_bodega));
                                                        String nombre_cliente = context.getString(R.string.Anonimo);
                                                        if (!editText_nombre.getText().toString().equals(""))
                                                            nombre_cliente = editText_nombre.getText().toString().trim();
                                                        factura_cliente.setNombre(nombre_cliente);
                                                        factura_cliente.setTelefono(editText_telefono.getText().toString().trim());
                                                        factura_cliente.setDocumento(editText_documento.getText().toString().trim());
                                                        factura_cliente.setFecha(fecha);
                                                        factura_cliente.setDireccion(editText_direccion.getText().toString().trim());
                                                        factura_cliente.setId_vendedor("Bodega");
                                                        factura_cliente.setVendedor(MainActivity.Usuario);
                                                        myRefe.child("Factura_cliente").child(factura_cliente.getId()).setValue(factura_cliente);


                                                        Guardar_firebase nube = new Guardar_firebase();
                                                        nube.guardar_historial(id_factura, context.getString(R.string.Venta_bodega), "", descripcion_compra, context);

                                                        //datos para la factura pdf
                                                        datos_cliente.add(new Modelo_factura_cliente(id_factura, nombre_cliente, editText_telefono.getText().toString().trim(), editText_documento.getText().toString().trim(), fecha, MainActivity.Id_Usuario, "", editText_direccion.getText().toString().trim(), MainActivity.Usuario, ""));
                                                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Factura_Toons.pdf";

                                                        try {
                                                            PDFUtility_factura_bodega crear = new PDFUtility_factura_bodega();
                                                            productos_para_pdf.sort(Comparator.comparing(Modelo_productos_para_facturar::getNombre));
                                                            crear.crearPdf(context, Fragment_crear_factura.this, productos_para_pdf, datos_cliente, total + "", path, true);

                                                        } catch (Exception e) {
                                                            Toast.makeText(context, "Error Creating Pdf " + e, Toast.LENGTH_SHORT).show();
                                                        }


                                                        MainActivity.lista_seleccion_venta_mayor_bodega.clear();
                                                        nombreCliente="";
                                                        telefono="";
                                                        documento="";
                                                        direccion="";

                                                        Gson gson = new Gson();
                                                        String jsonString = gson.toJson(MainActivity.lista_seleccion_venta_mayor_bodega);
                                                        SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
                                                        SharedPreferences.Editor editor = pref.edit();
                                                        editor.putString(PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA, jsonString);
                                                        editor.apply();


                                                        if (radioButton_compartir.isChecked()) {

                                                            Intent intent = new Intent(context, Vista_pdf.class);
                                                            getActivity().onBackPressed();
                                                            intent.putExtra("path", path);
                                                            startActivity(intent);

                                                        } else if (radioButton_whatapp.isChecked()) {
                                                            Navigation.findNavController(vista).navigateUp();
                                                            openWhatsApp();

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
                            }
                        }).start();
                    }
                if (radioButton_nignuno.isChecked()){
                    Navigation.findNavController(vista).navigateUp();
                    Navigation.findNavController(vista).navigateUp();
                }
                    
                    
                }
                return false;
            }
        });

        return vista;
    }






        private void openWhatsApp() {
        String smsNumber = "57"+editText_telefono.getText().toString().trim();
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

    @Override
    public void onPDFDocumentClose(File file)
    {
//        Toast.makeText(context,"Pdf Created",Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onResume() {
        super.onResume();
        MainActivity.opcion_factura.setVisible(false);
        MainActivity.opcion_confirmar.setVisible(true);
        editText_nombre.setText(nombreCliente);
        editText_telefono.setText(telefono);
        editText_documento.setText(documento);
        editText_direccion.setText(direccion);
        cargar_inventario();
    }

    public void cargar_inventario() {

        referencia_productos= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("cliente_mis_productos").equalTo("Accesory Toons");


        if(referencia_productos!=null){
            oyente = referencia_productos.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lista_inventario=new ArrayList<>();
                    lista_productos.clear();
                    total=0;
                    for(DataSnapshot ds:snapshot.getChildren()){
                        lista_inventario.add(ds.getValue(Modelo_producto.class));

                        for(int x=0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size();x++){
                            if(MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getId().equals(ds.getValue(Modelo_producto.class).getId())){
                                String costo=MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getP_detal();
                                 total= total+ Integer.parseInt(costo);
                                lista_productos.add(new Modelo_producto(ds.getValue(Modelo_producto.class).getId(),ds.getValue(Modelo_producto.class).getNombre(),ds.getValue(Modelo_producto.class).getCodigo(),ds.getValue(Modelo_producto.class).getP_compra(),costo+"",costo+"",costo+"",costo+"",ds.getValue(Modelo_producto.class).getCantidad(),"","","","","","",ds.getValue(Modelo_producto.class).getUrl(),MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getSeleccion(),ds.getValue(Modelo_producto.class).getMis_productos(),ds.getValue(Modelo_producto.class).getCliente_mis_productos()));
                            }
                        }
                       try {
                           lista_productos.sort(Comparator.comparing(Modelo_producto::getNombre));
                           RecyclerViewAdaptador_ventas_bodega adapador = new RecyclerViewAdaptador_ventas_bodega(lista_productos);
                           recview.setAdapter(adapador);
                           textView_total.setText("Total: "+formatoImporte.format(total));
                       }catch (Exception e){}

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
        for(Modelo_producto objeto:lista_productos){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        filtro.sort(Comparator.comparing(Modelo_producto::getNombre));
        RecyclerViewAdaptador_ventas_bodega adapador= new RecyclerViewAdaptador_ventas_bodega(filtro);
        recview.setAdapter(adapador);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        recview=null;

        try {
            referencia_productos.removeEventListener(oyente);
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vista.getWindowToken(), 0);
            Glide.get(context).clearMemory();//clear memory

        }catch (Exception e){}

        MainActivity.opcion_confirmar.setVisible(false);
        vista=null;
    }
}