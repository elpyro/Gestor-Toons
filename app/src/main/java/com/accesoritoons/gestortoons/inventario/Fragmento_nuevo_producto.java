package com.accesoritoons.gestortoons.inventario;


import static com.accesoritoons.gestortoons.MainActivity.opcion_agregar_producto;
import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.Activity_scanner;
import com.accesoritoons.gestortoons.Activity_simple_foto;
import com.accesoritoons.gestortoons.MainActivity;

import com.accesoritoons.gestortoons.R;

import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.surtir.Fragmento_carrito;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class Fragmento_nuevo_producto extends Fragment {


    EditText editText_incluir_nombre_producto,editTextText_descripcion, editTextText_proveedor,editTextNumber_carrito, editTextNumber_cantidad;
    public static EditText editText_codigo_barras;
    TextView textView_fecha_actulizacion, textView_usuario_actualizacion;
    Switch switch_visible;
    Spinner spinner_categoria;
    EditText txtBillingMount_detal, txtBillingMount_platino, txtBillingMount_oro, txtBillingMount_diamante, txtBillingMount_compra;
    LinearLayout linearLayout_ulitma_modificacion;
    String id_exitente="", url_producto;
    String codigo_barras="";
    DatabaseReference myRefe;
    Button button_scanner;
    Query dataQuery;
    Context context;
    View vista;
    ValueEventListener oyente, oyente2;
    private String id_producto;
    public static  ImageView image_foto1;
    public static Uri imageuri;
    private byte[] blob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista=inflater.inflate(R.layout.fragment_fragmento_nuevo_producto, container, false);
        editText_incluir_nombre_producto =vista.findViewById(R.id.editText_incluir_nombre_empresa);
        editText_codigo_barras=vista.findViewById(R.id.editText_codigo_barras);
        txtBillingMount_platino=vista.findViewById(R.id.editText_platino);
        spinner_categoria=vista.findViewById(R.id.spinner_categoria);
        txtBillingMount_oro=vista.findViewById(R.id.editText_oro);
        editTextNumber_carrito=vista.findViewById(R.id.editTextNumber_carrito);
        txtBillingMount_diamante=vista.findViewById(R.id.editText_diamante);
        txtBillingMount_detal=vista.findViewById(R.id.editText_detal);
        txtBillingMount_compra=vista.findViewById(R.id.editText_compra);
        editTextNumber_cantidad=vista.findViewById(R.id.editTextNumber_cantidad);
        editTextText_descripcion=vista.findViewById(R.id.editTextText_incluir_marca);
        editTextText_proveedor=vista.findViewById(R.id.editTextText_proveedor);
        switch_visible=vista.findViewById(R.id.switch_visible);
        image_foto1=vista.findViewById(R.id.imageView_incluir_foto1);
        textView_fecha_actulizacion=vista.findViewById(R.id.textView_fecha_actualizacion);
        textView_usuario_actualizacion=vista.findViewById(R.id.textView_actulizacion_usuario);
        linearLayout_ulitma_modificacion=vista.findViewById(R.id.linearLayout_ulitma_modificacion);
        button_scanner=vista.findViewById(R.id.button_scanner);


        MainActivity.opcion_nuevo_producto.setVisible(false);
        //recibir argumentos
        if (getArguments() != null) {
            opcion_editar_producto.setVisible(true);
            editTextNumber_cantidad.setVisibility(View.VISIBLE);
            producto_existente();
        }else {
//            editTextNumber_carrito.setVisibility(View.VISIBLE);
            opcion_agregar_producto.setVisible(true);
        }

        button_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
                input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

                Intent intent=new Intent(context, Activity_scanner.class);
                startActivity(intent);
            }
        });

        image_foto1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(url_producto!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString("url_imagen",url_producto );
                    Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                }else{
                    Toast.makeText(context, getString(R.string.Registro_no_guardado), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        opcion_agregar_producto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                guardar(vista);
                return false;
            }
        });

        opcion_editar_producto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                guardar(vista);
                return false;
            }
        });

        image_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // muestra el cuadro de diálogo de selección de imagen

                //ocultar teclado
                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent=new Intent(context, Activity_simple_foto.class);
                intent.putExtra("nombre_imagen", "Fragmento_nuevo_producto.image_foto1");
                startActivity(intent);

            }
        });
        return vista;
    }



    public void producto_existente(){
        id_producto =getArguments().getString("id_producto");
        opcion_editar_producto.setVisible(true);

        myRefe = FirebaseDatabase.getInstance().getReference();

        dataQuery = myRefe.child("Productos").orderByChild("id").equalTo(id_producto).limitToFirst(1);
        dataQuery.keepSynced(true);
        try {
            Thread.sleep(1 * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_producto producto = userSnapshot.getValue(Modelo_producto.class);
                        id_exitente=producto.getId();
                        editText_incluir_nombre_producto.setText(producto.getNombre());
                        editText_codigo_barras.setText((producto.getCodigo()));
                        codigo_barras=producto.getCodigo();
                        txtBillingMount_compra.setText(producto.getP_compra());
                        txtBillingMount_platino.setText(producto.getP_plantino());
                        txtBillingMount_oro.setText(producto.getP_oro());
                        txtBillingMount_diamante.setText(producto.getP_diamante());
                        editTextNumber_cantidad.setText(producto.getCantidad());
                        txtBillingMount_detal.setText(producto.getP_detal());
                        editTextText_descripcion.setText(producto.getDescripcion());
                        editTextText_proveedor.setText(producto.getProveedor());
                        textView_usuario_actualizacion.setText(producto.getUsuario_ultima_modificacion());
                        textView_fecha_actulizacion.setText(producto.getFecha_ultima_modificacion());
                        linearLayout_ulitma_modificacion.setVisibility(View.VISIBLE);

                        if (!producto.getCliente_mis_productos().equals("Accesory Toons")) opcion_editar_producto.setVisible(false);

                        if (producto.getEstado().equals("true")){
                            switch_visible.setChecked(true);
                        }else{
                            switch_visible.setChecked(false);
                        }
                        url_producto=producto.getUrl();
                        try {
                            Picasso.with(context).load(producto.getUrl()).into(image_foto1);
                        }catch (Exception e){
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

    //METODO PARA FORMATO MONEDA
    public TextWatcher amount(final EditText editText) {
        return new TextWatcher() {
            DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!s.toString().equals(current) && s.toString().compareTo("")!=0){
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("["+getResources().getString(R.string.MonedaMonto)+",.]", "").replace(" ","");

                    double parsed = Double.parseDouble(cleanString.replaceAll("\\s","").trim());
                    // Obtienes la instancia del formateador

                    DecimalFormat decimalFormat  = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

                    // obtener la instancia del formatiador de simbolos
                    DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

                    // cambias el simbolo por US
                    symbols.setCurrencySymbol("");

                    // le asignamos el nuevo formateador de simbolo
                    decimalFormat.setDecimalFormatSymbols(symbols);
                    // formateamos
                    String formatted = decimalFormat.format((parsed/100));
                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                    editText.addTextChangedListener(this);
                }
            }
        };
    }

    private void guardar(View vista) {
        //verificar campos
        if(editText_incluir_nombre_producto.getText().toString().equals("")){
            editText_incluir_nombre_producto.setError(getString(R.string.Requerido));
            return;
        }
        if(txtBillingMount_detal.getText().toString().equals("")){
            txtBillingMount_detal.setError(getString(R.string.Requerido));
            return;
        }



//      //CONSULTAR

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        if (editText_codigo_barras.getText().toString().equals("")){
            guardando_firebase(vista,myRefe);
            return;
        }

        verificar_codigo_barras(vista, myRefe);



    }

    public void guardando_firebase(View vista, DatabaseReference myRefe){
        MainActivity.progressDialog = ProgressDialog.show(context, getString(R.string.nuevo_producto),
                getString(R.string.Guardando), true);

        //https://lightsthing.net/blog/subir-imagen-firebase-storage-android-studio-java
        //subir foto https://firebase.google.com/docs/storage/android/upload-files?hl=es-419
        String ID;
        if (id_exitente.equals("")){
             ID = UUID.randomUUID().toString();//ENTREGA UN ID DISTINTO

        }else{
             ID = id_exitente;
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("imagen_productos/"+ID);



        // [START upload_memory]
        // Get the data from an ImageView as bytes

            image_foto1.setDrawingCacheEnabled(true);
            image_foto1.buildDrawingCache();
        byte[] data=null;
            try {
                Bitmap bitmap = ((BitmapDrawable) image_foto1.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                 data = baos.toByteArray();


                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.cancel();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //guardando la url para la imagen
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if(txtBillingMount_platino.getText().toString().equals("")){
                            txtBillingMount_platino.setText("0");
                        }
                        if(txtBillingMount_oro.getText().toString().equals("")){
                            txtBillingMount_oro.setText("0");
                        }
                        if(txtBillingMount_diamante.getText().toString().equals("")){
                            txtBillingMount_diamante.setText("0");
                        }
                        if(txtBillingMount_compra.getText().toString().equals("")){
                            txtBillingMount_compra.setText("0");
                        }
                        if(txtBillingMount_detal.getText().toString().equals("")){
                            txtBillingMount_detal.setText("0");
                        }
                        //guardando los datos
                        String fecha =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
                        Modelo_producto producto= new Modelo_producto();
                        producto.setId(ID);
                        producto.setNombre(editText_incluir_nombre_producto.getText().toString());
                        producto.setCodigo(editText_codigo_barras.getText().toString().trim().toLowerCase());
                        producto.setP_compra(txtBillingMount_compra.getText().toString());
                        producto.setP_plantino(txtBillingMount_platino.getText().toString());
                        producto.setP_oro(txtBillingMount_oro.getText().toString());
                        producto.setP_diamante(txtBillingMount_diamante.getText().toString());
                        producto.setP_detal(txtBillingMount_detal.getText().toString());
                        if(editTextNumber_cantidad.getText().toString().equals(""))editTextNumber_cantidad.setText("0");
                        producto.setCantidad(editTextNumber_cantidad.getText().toString());
                        producto.setDescripcion(editTextText_descripcion.getText().toString());
                        producto.setProveedor(editTextText_proveedor.getText().toString());
                        producto.setMis_productos("Accesory Toons");
                        producto.setCliente_mis_productos("Accesory Toons");
                        producto.setFecha_ultima_modificacion( fecha);
                        //producto.setFecha_y_hora_modificacion( java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                        producto.setUsuario_ultima_modificacion(MainActivity.Usuario);
                        producto.setUrl(downloadUri.toString());
                        if (switch_visible.isChecked()){
                            producto.setEstado("true");
                        }else{
                            producto.setEstado("false");
                        }
                        myRefe.child("Productos").child(producto.getId()).setValue(producto);
                        Toast.makeText(context, getString(R.string.producto_guardado), Toast.LENGTH_SHORT).show();



                        //guardando HISTORIAL
                        Modelo_historial historial = new Modelo_historial();
                        historial.setId(UUID.randomUUID().toString());
                        historial.setReferencia(ID);
                        historial.setActividad(getString(R.string.Invetario));
                        historial.setVisible(getString(R.string.Administrador));
                        historial.setFecha(fecha);
                       // historial.setFecha_y_hora(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                        historial.setUsuario(MainActivity.Usuario);
                        if (id_exitente==""){
                            historial.setDescripcion(getString(R.string.nuevo_producto)+": "+editText_incluir_nombre_producto.getText().toString().toString());
                        }else{
                            historial.setDescripcion(getString(R.string.producto_editado)+": "+editText_incluir_nombre_producto.getText().toString().toString());
                        }
                        myRefe.child("Historial").child(historial.getId()).setValue(historial);


                        for (int x = 0; x < MainActivity.lista_seleccion_compra.size(); x++) {
                            Modelo_producto productos =  MainActivity.lista_seleccion_compra.get(x);
                            if (productos.getId().equals(ID)) {
                                    MainActivity.lista_seleccion_compra.remove(x);
                                Toast.makeText(context, "Producto deseleccionado", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {
                            Modelo_producto productos =  MainActivity.lista_seleccion_venta_mayor_bodega.get(x);
                            if (productos.getId().equals(ID)) {
                                MainActivity.lista_seleccion_venta_mayor_bodega.remove(x);
                                Toast.makeText(context, "Producto deseleccionado", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        for (int x = 0; x < MainActivity.lista_seleccion.size(); x++) {
                            Modelo_producto productos =  MainActivity.lista_seleccion.get(x);
                            if (productos.getId().equals(ID)) {
                                MainActivity.lista_seleccion.remove(x);
                                Toast.makeText(context, "Producto deseleccionado", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }


                        //ocultar teclado en fragmento
                        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
                        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);
                        //cerrar el fragmento y volver al anterior
                        progressDialog.cancel();
                        Navigation.findNavController(vista).navigateUp();
                    }
                });
            }catch (Exception e){
                progressDialog.cancel();
                Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
                return;
            }


    }



    private void verificar_codigo_barras(View vista,  DatabaseReference myRefe) {

        if (!codigo_barras.equals(editText_codigo_barras.getText().toString().trim())) {
            dataQuery = myRefe.child("Productos").orderByChild("codigo").equalTo(editText_codigo_barras.getText().toString().trim().toLowerCase()).limitToFirst(1);
            dataQuery.keepSynced(true);
            try {
                Thread.sleep(1 * 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            Modelo_producto producto = userSnapshot.getValue(Modelo_producto.class);

                            try {
                                editText_codigo_barras.setError(getString(R.string.codigo_existente)+": "+producto.getNombre());
                            } catch (Exception e) { }

                        }


                        return;
                    } else {

                        guardando_firebase(vista, myRefe);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.cancel();
                    Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
                }

            });
        }else {
            guardando_firebase(vista,myRefe);
        }
    }

    public void onDetach() {
        super.onDetach();
        myRefe=null;


        dataQuery=null;
        Glide.get(context).clearMemory();//clear memory
        opcion_agregar_producto.setVisible(false);
        opcion_editar_producto.setVisible(false);
        imageuri=null;
        image_foto1=null;
        vista=null;
    }
}