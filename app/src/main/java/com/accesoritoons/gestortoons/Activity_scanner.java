package com.accesoritoons.gestortoons;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.bodega.Fragment_venta_bodega_mayor;
import com.accesoritoons.gestortoons.inventario.Fragmento_nuevo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.surtir.Fragmento_agregar_inventario;
import com.accesoritoons.gestortoons.surtir.Fragmento_carrito;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Activity_scanner extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    public ZXingScannerView vistaescaner;
    boolean linterna=true;
    private MenuItem opcion_linterna;
    String id_usuario_pedido, codigo_escaneado;

    private  final String PREFERENCIA_SELECCION = "PREFERENCIA_SELECCION";
    private  final String PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA = "PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA";


    private Toast ImageToast=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Bundle extras=getIntent().getExtras();
        if (extras!=null) {
            if (extras.getString("id_usuario_pedido") != null) {
                id_usuario_pedido = extras.getString("id_usuario_pedido");
            }
        }
        escaner2(null);
    }


    // permisos de la camara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");

                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }

    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true;
            escaner2(null);
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }


    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(this, getString(R.string.Se_requieren_permisos), Toast.LENGTH_SHORT).show();
    }


    public void escaner2(View w) {

        if (!permisoCamaraConcedido) {
            permisoSolicitadoDesdeBoton = true;
            verificarYPedirPermisosDeCamara();

            return;
        }
        //permito aceptado continuar con el scaner
        escanear();

    }



    //SCANNER
    private void escanear() {
        vistaescaner = new ZXingScannerView(this);
        vistaescaner.setResultHandler(new zxingscanner());
        setContentView(vistaescaner);
        vistaescaner.startCamera();
        vistaescaner.setFlash(linterna);

//        vistaescaner.setFlash(Preferencias_app.linterna);
//        vistaescaner.setAutoFocus(Preferencias_app.autofoco);
    }



    //SCANER CONTINUACION
    class zxingscanner implements ZXingScannerView.ResultHandler {

        @Override
        public void handleResult(Result rawResult) {

            codigo_escaneado = rawResult.getText();
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);//sonido

            if (id_usuario_pedido == null) {

                try {
                    vistaescaner.stopCamera();
                    Fragmento_nuevo_producto.editText_codigo_barras.setText(codigo_escaneado);
                } catch (Exception e) {
                }
                finish();
                return;
            } else {
                try {
                    Thread.sleep(1 * 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(ImageToast!=null)ImageToast.cancel();


                if (Fragment_venta_bodega_mayor.venta_bodega_mayor==true){
                    agregar_al_carrito_venta_bodega_mayor(codigo_escaneado);
                }else{
                    agregar_al_carrito(codigo_escaneado);
                }




                vistaescaner.resumeCameraPreview(this);
            }

        }

        private void agregar_al_carrito_venta_bodega_mayor(String codigo_escaneado) {
            int costo=0;
            boolean existe=false;
            boolean existe_inventario=false;
            for (int x = 0; x < Fragment_venta_bodega_mayor.lista_produtos_completa.size(); x++) {
                Modelo_producto productos = Fragment_venta_bodega_mayor.lista_produtos_completa.get(x);
                if (productos.getCodigo().equals(codigo_escaneado)) {
                    existe_inventario=true;
                    for (int i = 0; i < MainActivity.lista_seleccion_venta_mayor_bodega.size(); i++) {
                        Modelo_producto productos_selecionados =  MainActivity.lista_seleccion_venta_mayor_bodega.get(i);

                        if (productos_selecionados.getId().equals(productos.getId())) {
                            existe=true;

                            int cantidad= Integer.parseInt(productos_selecionados.getSeleccion())+1;
                            int cantidad_inventario=Integer.parseInt(Fragment_venta_bodega_mayor.lista_produtos_completa.get(x).getCantidad());
                            if(cantidad<= cantidad_inventario ){
                                MainActivity.lista_seleccion_venta_mayor_bodega.get(i).setSeleccion(cantidad+"");//cambiar cantidad del array
                                crear_toast(productos.getNombre(), cantidad+"");
                            }else{
                                crear_toast("Agotado","");
                                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);//sonido
                            }


                            break;
                        }
                    }

                        costo= Integer.parseInt((productos.getP_diamante()));//las ventas al mayor en bodega tiene costo al mayor en diamante

                    if (existe==false) {
                        MainActivity.lista_seleccion_venta_mayor_bodega.add(new Modelo_producto(productos.getId(),productos.getNombre(),"",productos.getP_compra()+"",costo+"",costo+"",costo+"",costo+"","","","","","","","",productos.getUrl(),"1",productos.getMis_productos(),productos.getCliente_mis_productos()));
                        crear_toast(productos.getNombre(), 1+"");
                    }
                    break;
                }
            }
            if(existe_inventario==true) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(MainActivity.lista_seleccion_venta_mayor_bodega);
                SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREFERENCIA_SELECCION_VENTA_MAYOR_BODEGA, jsonString);
                editor.apply();
            }else{
                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);//sonido
            }
        }


        private void crear_toast(String nombre, String cantidad){
            ImageToast = new Toast(getBaseContext());
            LinearLayout toastLayout = new LinearLayout(getBaseContext());
            toastLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView image = new ImageView(getBaseContext());

            TextView text = new TextView(getBaseContext());
            TextView text_espacio = new TextView(getBaseContext());
            //  image.setImageResource(R.drawable.ic_empresa);
            image.setImageDrawable(getResources().getDrawable(R.drawable.logo_toons));

//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
//            image.setLayoutParams(layoutParams);
            String mensaje=cantidad+" x "+nombre;
            text.setText( mensaje);

            text.setGravity(Gravity.CENTER);
            text.setTextColor(Color.parseColor("#000000"));
            text.setBackgroundColor(Color.parseColor("#FFFFFF"));
            text.setTextSize(20);
            toastLayout.addView(text);
            //toastLayout.addView(image);

            ImageToast.setView(toastLayout);
            ImageToast.setGravity(Gravity.TOP,0 ,240 );
            ImageToast.setDuration(Toast.LENGTH_LONG);
            ImageToast.show();
            image=null;
        }

        private void agregar_al_carrito(String codigo_escaneado) {
            int costo=0;

            boolean existe=false;
            boolean existe_inventario=false;
                        for (int x = 0; x < Fragmento_agregar_inventario.lista_produtos_completa.size(); x++) {
                            Modelo_producto productos = Fragmento_agregar_inventario.lista_produtos_completa.get(x);
                            if (productos.getCodigo().equals(codigo_escaneado)) {
                                existe_inventario=true;
                                for (int i = 0; i < MainActivity.lista_seleccion.size(); i++) {
                                    Modelo_producto productos_selecionados =  MainActivity.lista_seleccion.get(i);
                                    if (productos_selecionados.getId().equals(productos.getId())) {
                                        existe=true;
                                        int cantidad= Integer.parseInt(productos_selecionados.getSeleccion())+1;
                                        int cantidad_inventario=Integer.parseInt(Fragmento_agregar_inventario.lista_produtos_completa.get(x).getCantidad());

                                        if(cantidad<= cantidad_inventario ){
                                            MainActivity.lista_seleccion.get(i).setSeleccion(cantidad+"");
                                            crear_toast(productos.getNombre(), cantidad+"");
                                        }else{
                                            crear_toast("Agotado","");
                                            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);//sonido
                                        }

                                        break;
                                    }
                                }
                                if(MainActivity.tipo_vendedor.equals(getApplicationContext().getString(R.string.Plata))){
                                    costo= Integer.parseInt((productos.getP_plantino()));
                                }else if(MainActivity.tipo_vendedor.equals(getApplicationContext().getString(R.string.Oro))){
                                    costo= Integer.parseInt((productos.getP_oro()));
                                }if(MainActivity.tipo_vendedor.equals(getApplicationContext().getString(R.string.Diamante))){
                                    costo= Integer.parseInt((productos.getP_diamante()));
                                }
                                if (existe==false) {
                                    MainActivity.lista_seleccion.add(new Modelo_producto(productos.getId(),productos.getNombre(),"",costo+"",costo+"",costo+"",costo+"",costo+"","","","","","","","",productos.getUrl(),"1",productos.getMis_productos(),productos.getCliente_mis_productos()));

                                    crear_toast(productos.getNombre(), 1+"");
                                }
                                break;
                            }
                        }
                        if(existe_inventario==true) {
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(MainActivity.lista_seleccion);
                            SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(PREFERENCIA_SELECCION, jsonString);
                            editor.apply();
                        }else{
                            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);//sonido
                        }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            vistaescaner.setFlash(false);
            //    Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto.notifyDataSetChanged();
        }catch (Exception e){}


    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // CREA EL MENU CON TRES PUNTICOS
        getMenuInflater().inflate(R.menu.main, menu);
        opcion_linterna=menu.findItem(R.id.linterna);
        opcion_linterna.setVisible(true);

        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.linterna:
                vistaescaner.setFlash(!linterna);
                linterna=!linterna;
                if (linterna==true){
                    opcion_linterna.setIcon(R.drawable.ic_baseline_flashlight_on_24);
                }else {
                    opcion_linterna.setIcon(R.drawable.ic_baseline_flashlight_off_24);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            vistaescaner.stopCamera();

        }catch (Exception e){}
    }
    @Override
    public void onDestroy(){
        super.onDestroy();


        try{
            vistaescaner.stopCamera();

        }catch (Exception e){}
    }

}