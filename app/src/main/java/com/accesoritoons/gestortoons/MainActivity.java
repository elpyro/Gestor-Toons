package com.accesoritoons.gestortoons;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.accesoritoons.gestortoons.databinding.ActivityMainBinding;
import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_empresa;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// or en java ||
public class MainActivity extends AppCompatActivity {

    public static ProgressDialog progressDialog;
    public static MenuItem opcion_agregar_producto, opcion_nuevo_producto, opcion_editar_producto, opcion_linterna, opcion_buscar,opcion_scanner, opcion_agregar_inventario, opcion_cambiar_cantidad, opcion_comprar, opcion_factura, opcion_confirmar,opcion_compartir, opcion_compartir_logo, opcion_whatapps, opcion_no_internet, opcion_realizar_recaudo;
    public static MenuItem opcion_nuevo_perfil;
    public static MenuItem opcion_crear_pedido;
    public static  NavigationView navigationView;

    public static ArrayList<Modelo_empresa> datos_empresa=new ArrayList<>();
    public static ArrayList<Modelo_producto> lista_seleccion=new ArrayList<>();
    public static ArrayList<Modelo_producto> lista_seleccion_compra=new ArrayList<>();
    public static ArrayList<Modelo_producto> lista_seleccion_venta_mayor_bodega=new ArrayList<>();

    private  final String PREFERENCIA_SELECCION = "PREFERENCIA_SELECCION";
    private  final String PREFERENCIA_SELECCION_COMPRA = "PREFERENCIA_SELECCION_COMPRA";
    private  final String PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA = "PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public  static String Usuario, Correo,Perfil="",Id_Usuario;
    public static String id_vendedor, tipo_vendedor;
    public static ImageView logo;
    public static boolean vista_recaudo=false;

    // matrices de permisos
    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private String[] cameraPermissions; // cámara y almacenamiento
    private String [] storagePermissions;// solo almacenamiento


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //llamar las preferencias guardadas
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        Preferencias_app.obtener_preferencias(preferencias,this);


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Guardar_firebase nube =new Guardar_firebase();
        datos_empresa=nube.cargar_datos_empresa(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmento_perfiles, R.id.fragment_recaudos_auditor,R.id.contenedor_compras_bodega, R.id.fragment_venta_bodega, R.id.fragmento_nuevo_perfil,R.id.fragmento_inventario2,R.id.contenedor_agregar_inventario, R.id.fragmento_foto_apliada, R.id.fragmento_inventario_vendedor, R.id.fragmento_cambiar_cantidades, R.id.fragment_puntos_de_venta, R.id.fragment_compra__bodega, R.id.fragmento_pedido_enviado, R.id.fragment_facturas_bodega, R.id.fragment_detalle_factura, R.id.vista_pdf,R.id.fragmento_reportes,R.id.fragment_puntos_de_venta,R.id.fragment_empresa,R.id.fragmento_nuevo_producto)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //cargar arraylist guardados detal
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(PREFERENCIA_SELECCION, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Modelo_producto>>() {
            }.getType();
            ArrayList<Modelo_producto> listPrefer = gson.fromJson(jsonString, type);

            lista_seleccion = (ArrayList) listPrefer.clone();

        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_seleccion);
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCIA_SELECCION, jsonString);
            editor.apply();
        }
        //cargar arraylist guardados
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(PREFERENCIA_SELECCION_COMPRA, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Modelo_producto>>() {
            }.getType();
            ArrayList<Modelo_producto> listPrefer = gson.fromJson(jsonString, type);

            lista_seleccion_compra = (ArrayList) listPrefer.clone();

        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_seleccion_compra);
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCIA_SELECCION_COMPRA, jsonString);
            editor.apply();
        }

        //cargar arraylist guardados detal
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Modelo_producto>>() {
            }.getType();
            ArrayList<Modelo_producto> listPrefer = gson.fromJson(jsonString, type);

            lista_seleccion_venta_mayor_bodega = (ArrayList) listPrefer.clone();

        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_seleccion_venta_mayor_bodega);
            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA, jsonString);
            editor.apply();
        }


        //permisos de almacenamiento y camara
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!checkCameraPermission()){
            requestCameraPermission();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        opcion_agregar_producto = menu.findItem(R.id.agregar_producto);
        opcion_editar_producto = menu.findItem(R.id.editar_producto);
        opcion_linterna=menu.findItem(R.id.linterna);
        opcion_nuevo_producto=menu.findItem(R.id.nuevo_producto);
        opcion_buscar=menu.findItem(R.id.app_bar_search);
        opcion_crear_pedido=menu.findItem(R.id.crear_pedido);
        opcion_nuevo_perfil=menu.findItem(R.id.nuevo_perfil);
        opcion_scanner=menu.findItem(R.id.app_bar_scanner);
        opcion_agregar_inventario=menu.findItem(R.id.agregar_inventario);
        opcion_cambiar_cantidad=menu.findItem(R.id.cambiar_cantidad);
        opcion_comprar=menu.findItem(R.id.comprar);
        opcion_factura=menu.findItem(R.id.factura);
        opcion_confirmar=menu.findItem(R.id.confirmar);
        opcion_compartir=menu.findItem(R.id.compartir);
        opcion_compartir_logo=menu.findItem(R.id.compartir_logo);
        opcion_whatapps=menu.findItem(R.id.whatapps);
        opcion_no_internet=menu.findItem(R.id.no_internet);
        opcion_realizar_recaudo=menu.findItem(R.id.realizar_recaudo);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    MainActivity.opcion_no_internet.setVisible(false);
                    MainActivity.opcion_factura.setEnabled(true);
                    MainActivity.opcion_confirmar.setEnabled(true);
                    MainActivity.opcion_crear_pedido.setEnabled(true);
                    MainActivity.opcion_realizar_recaudo.setEnabled(true);
                    MainActivity.opcion_cambiar_cantidad.setEnabled(true);
                    MainActivity.opcion_agregar_inventario.setEnabled(true);

                }
                else {
                    MainActivity.opcion_no_internet.setVisible(true);
                    MainActivity.opcion_factura.setEnabled(false);
                    MainActivity.opcion_confirmar.setEnabled(false);
                    MainActivity.opcion_crear_pedido.setEnabled(false);
                    MainActivity.opcion_realizar_recaudo.setEnabled(false);
                    MainActivity.opcion_cambiar_cantidad.setEnabled(false);
                    MainActivity.opcion_agregar_inventario.setEnabled(false);
                    ; } }
            @Override public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled"); } });


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    //PERMISOS DE CAMARA Y ALMACENAMIENTO
    private boolean checkStoragePermission(){
        //comprobar si el permiso de almacenamiento está habilitado o no
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private  void requestStoragePermission(){
        // solicita el permiso de almacenamiento
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        // verifica si el permiso de la cámara está habilitado o no
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        // solicita el permiso de la cámara
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // resultado del permiso permitido / denegado
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){

                    }
                    else{
                        Toast.makeText(this, getString(R.string.Se_requieren_permisos), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    // si se permite devolver verdadero de lo contrario falso
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        // permiso de almacenamiento permitido

                    }
                    else{
                        Toast.makeText(this, getString(R.string.Se_requieren_permiso_almacenamiento), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            progressDialog.cancel();
        }catch (Exception e){        }

    }
}