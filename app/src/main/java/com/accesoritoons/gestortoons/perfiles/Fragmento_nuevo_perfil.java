package com.accesoritoons.gestortoons.perfiles;





import static com.accesoritoons.gestortoons.MainActivity.opcion_agregar_producto;
import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.opcion_nuevo_perfil;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.Activity_simple_foto;
import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.inventario.Fragmento_nuevo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Fragmento_nuevo_perfil extends Fragment {

    View vista;
    Context context;

    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    //selección de imagen Constants
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    private static final int LEER_SCANNER = 104;

    // matrices de permisos
    private String[] cameraPermissions; // cámara y almacenamiento
    private String [] storagePermissions;// solo almacenamiento
    private Uri imageUri;
    private Uri resultUri;

    public Uri Url_uri_foto_avatar,Url_uri_documento1, Url_uri_documento2 ;
    private EditText editText_nombre, editText_documento, editText_correo, editText_telefono,  editText_direccion, editText_inventario_maximo;
    private TextView  textView_fecha_actulizacion, textView_usuario_actualizacion;
    public static ImageView imageView_foto_usuario, imageView_documento1, imageView_documento2;
    private String fecha_hora,id_extra,url_avatar, url_documento1, url_documento2, correo_existente="";
    private Spinner spinner_perfil,spinner_tipo;
    private CheckBox checkBox_ver_ganancia;
    String id_exitente="", id_usuario;
    Query dataQuery;
    ValueEventListener oyente;

    private LinearLayout linearlayout_ultima_modificacion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         vista=inflater.inflate(R.layout.fragment_fragmento_nuevo_perfil, container, false);
         context=getContext();

        editText_nombre=vista.findViewById(R.id.editText_incluir_nombre_empresa);
        editText_documento=vista.findViewById(R.id.textView_documento_empresa);
        editText_correo=vista.findViewById(R.id.editText_correo_empresa);
        editText_telefono=vista.findViewById(R.id.editText_telefono1);
        spinner_tipo=vista.findViewById(R.id.spinner_tipo);
        editText_direccion=vista.findViewById(R.id.editTextTextMultiLine_direccion_empresa);
        linearlayout_ultima_modificacion=vista.findViewById(R.id.linearLayout_ulitma_modificacion);
        spinner_perfil=vista.findViewById(R.id.spinner_perfil);
        editText_inventario_maximo=vista.findViewById(R.id.edittext_inventario_maximo);
        imageView_foto_usuario=vista.findViewById(R.id.imagen_avatar);
        imageView_documento1=vista.findViewById(R.id.imageView_documento1);
        imageView_documento2=vista.findViewById(R.id.imageView_documento2);
        textView_fecha_actulizacion=vista.findViewById(R.id.textView_fecha_actualizacion);
        textView_usuario_actualizacion=vista.findViewById(R.id.textView_actulizacion_usuario);



        //recibir argumentos
        if (getArguments() != null) {
            opcion_editar_producto.setVisible(true);
            usuario_existente();
        }else {
            MainActivity.opcion_nuevo_perfil.setVisible(true);
        }

        opcion_editar_producto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                verificar_campos();
                return false;
            }
        });

        MainActivity.opcion_nuevo_perfil.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                verificar_campos();
                return false;
            }
        });
        spinner_perfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==3){
                    spinner_tipo.setVisibility(View.VISIBLE);
                    editText_inventario_maximo.setVisibility(View.VISIBLE);
                }else{
                    editText_inventario_maximo.setVisibility(View.GONE);
                    spinner_tipo.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        imageView_foto_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultar_teclado(view);
                Intent intent=new Intent(context, Activity_simple_foto.class);
                intent.putExtra("nombre_imagen", "Fragmento_nuevo_perfil.imageView_foto_usuario");
                startActivity(intent);

            }
        });
        imageView_foto_usuario.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(url_avatar!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString("url_imagen",url_avatar );
                    Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                }else{
                    Toast.makeText(context, getString(R.string.Registro_no_guardado), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        imageView_documento1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultar_teclado(view);
                Intent intent=new Intent(context, Activity_simple_foto.class);
                intent.putExtra("nombre_imagen", "Fragmento_nuevo_perfil.imageView_documento1");
                startActivity(intent);
            }
        });
        imageView_documento1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(url_avatar!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString("url_imagen",url_documento1 );
                    Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                }else{
                    Toast.makeText(context, getString(R.string.Registro_no_guardado), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        imageView_documento2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultar_teclado(view);
                Intent intent=new Intent(context, Activity_simple_foto.class);
                intent.putExtra("nombre_imagen", "Fragmento_nuevo_perfil.imageView_documento2");
                startActivity(intent);
            }
        });
        imageView_documento2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(url_avatar!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString("url_imagen",url_documento2 );
                    Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                }else{
                    Toast.makeText(context, getString(R.string.Registro_no_guardado), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return vista;
    }

    private void usuario_existente() {
        MainActivity.opcion_nuevo_perfil.setVisible(false);
        spinner_tipo.setEnabled(false);
        id_usuario =getArguments().getString("id_usuario");
        opcion_editar_producto.setVisible(true);

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_usuario).limitToFirst(1);
        oyente=dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_usuario usuario = userSnapshot.getValue(Modelo_usuario.class);
                        id_exitente=usuario.getId();
                        editText_nombre.setText(usuario.getNombre());
                        editText_documento.setText(usuario.getDocumento());
                        editText_correo.setText(usuario.getCorreo());
                        correo_existente=usuario.getCorreo();
                        editText_inventario_maximo.setText(usuario.getMaximo_inventario());
                        editText_telefono.setText(usuario.getTelefono());
                        editText_direccion.setText(usuario.getDireccion());
                        url_avatar=(usuario.getUrl_foto_usuario());
                        url_documento1=(usuario.getUrl_documento1());
                        url_documento2=(usuario.getUrl_documento2());
                        textView_usuario_actualizacion.setText(usuario.getUsuario_ultima_modificacion());
                        textView_fecha_actulizacion.setText(usuario.getFecha_ultima_modificacion());
                        linearlayout_ultima_modificacion.setVisibility(View.VISIBLE);

                        if (usuario.getPerfil().equals(context.getString(R.string.Administrador))){
                            spinner_perfil.setSelection(1);
                        } else if (usuario.getPerfil().equals(context.getString(R.string.Auditor))){
                            spinner_perfil.setSelection(2);
                        } else if (usuario.getPerfil().equals(context.getString(R.string.Vendedor))){
                            spinner_perfil.setSelection(3);
                        }

                        if (usuario.getTipo().equals(context.getString(R.string.Plata))){
                            spinner_tipo.setSelection(1);
                        } else if (usuario.getTipo().equals(context.getString(R.string.Oro))){
                            spinner_tipo.setSelection(2);
                        } else if (usuario.getTipo().equals(context.getString(R.string.Diamante))){
                            spinner_tipo.setSelection(3);
                        }


                        Picasso.with(context).load(url_avatar).into(imageView_foto_usuario);
                        Picasso.with(context).load(url_documento1).into(imageView_documento1);
                        Picasso.with(context).load(url_documento2).into(imageView_documento2);

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

    private void verificar_campos() {
        //verificar campos
        if(editText_nombre.getText().toString().equals("")){
            editText_nombre.setError(getString(R.string.Requerido));
            return;
        }
        if(editText_documento.getText().toString().equals("")){
            editText_documento.setError(getString(R.string.Requerido));
            return;
        }
        if(editText_telefono.getText().toString().equals("")){
            editText_telefono.setError(getString(R.string.Requerido));
            return;
        }
        if(editText_correo.getText().toString().equals("")){
            editText_correo.setError(getString(R.string.Requerido));
            return;
        }
        if(spinner_perfil.getSelectedItem().toString().equals("Perfil")){
            Toast.makeText(context, getString(R.string.Seleccione_perfil), Toast.LENGTH_LONG).show();
            return;
        }
        if(spinner_perfil.getSelectedItem().toString().equals("Vendedor") && spinner_tipo.getSelectedItem().toString().equals("Tipo")){
            Toast.makeText(context, getString(R.string.Seleccione_tipo), Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        //https://lightsthing.net/blog/subir-imagen-firebase-storage-android-studio-java
        //subir foto https://firebase.google.com/docs/storage/android/upload-files?hl=es-419


        if (!correo_existente.equals(editText_correo.getText().toString().trim())) {
            //verificar correo
            dataQuery = myRefe.child("Usuarios").orderByChild("correo").equalTo(editText_correo.getText().toString().trim().toLowerCase()).limitToFirst(1);

            oyente=dataQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Modelo_producto usuario = userSnapshot.getValue(Modelo_producto.class);

                            try {
                                editText_correo.setError(getString(R.string.correo_existent) + ": " + usuario.getNombre());
                                return;
                            } catch (Exception e) {
                            }

                        }

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
        }else{
            guardando_firebase(vista, myRefe);
        }

    }

    private void guardando_firebase(View vista, DatabaseReference myRefe) {
        MainActivity.progressDialog = ProgressDialog.show(context, getString(R.string.Guardando),
                getString(R.string.Guardando), true);

        Url_uri_documento1=null;
        Url_uri_documento2=null;
        Url_uri_foto_avatar=null;
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

        try {


        // [START upload_memory]
        // Get the data from an ImageView as bytes
        imageView_foto_usuario.setDrawingCacheEnabled(true);
        imageView_foto_usuario.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView_foto_usuario.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        imageView_documento1.setDrawingCacheEnabled(true);
        imageView_documento1.buildDrawingCache();
        Bitmap bitmap_documento1 = ((BitmapDrawable) imageView_documento1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos_documento1 = new ByteArrayOutputStream(8);
        bitmap_documento1.compress(Bitmap.CompressFormat.JPEG, 20, baos_documento1);
        byte[] data_documento1 = baos_documento1.toByteArray();

        imageView_documento2.setDrawingCacheEnabled(true);
        imageView_documento2.buildDrawingCache();
        Bitmap bitmap_documento2 = ((BitmapDrawable) imageView_documento2.getDrawable()).getBitmap();
        ByteArrayOutputStream baos_documento2 = new ByteArrayOutputStream(8);
        bitmap_documento2.compress(Bitmap.CompressFormat.JPEG, 20, baos_documento2);
        byte[] data_documento2 = baos_documento2.toByteArray();


        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("imagenes_usuarios/avatar/"+ID);

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
                Url_uri_foto_avatar = uriTask.getResult();
            }
        });
        StorageReference mountainsRef2 = storageRef.child("imagenes_usuarios/documento1/"+ID);
        UploadTask uploadTask2 = mountainsRef2.putBytes(data_documento1);
        uploadTask2.addOnFailureListener(new OnFailureListener() {
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
                Url_uri_documento1 = uriTask.getResult();
            }
        });

        StorageReference mountainsRef3 = storageRef.child("imagenes_usuarios/documento2/"+ID);
        UploadTask uploadTask3 = mountainsRef3.putBytes(data_documento2);
        uploadTask3.addOnFailureListener(new OnFailureListener() {
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
                Url_uri_documento2 = uriTask.getResult();

                if (Url_uri_foto_avatar!=null && Url_uri_documento1!=null) {

                    String fecha_hora =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());

                    //guardando los datos
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Modelo_usuario usuario = new Modelo_usuario();
                    usuario.setId(ID);
                    usuario.setNombre(editText_nombre.getText().toString().trim());
                    usuario.setCorreo(editText_correo.getText().toString().toLowerCase().trim());
                    usuario.setPerfil(spinner_perfil.getSelectedItem().toString().trim());
                    usuario.setTipo(spinner_tipo.getSelectedItem().toString().trim());
                    usuario.setDocumento(editText_documento.getText().toString().trim());
                    usuario.setTelefono(editText_telefono.getText().toString().trim());
                    usuario.setDireccion(editText_direccion.getText().toString().trim());
                    usuario.setUrl_foto_usuario(Url_uri_foto_avatar.toString().trim());
                    usuario.setUrl_documento1(Url_uri_documento1.toString().trim());
                    usuario.setUrl_documento2(Url_uri_documento2.toString().trim());
                    usuario.setFecha_ultima_modificacion( fecha_hora);
                    //usuario.setFecha_y_hora_modificacion( java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    usuario.setUsuario_ultima_modificacion(MainActivity.Usuario);

                    if(spinner_perfil.getSelectedItem().toString().equals(getString(R.string.Vendedor))){
                        if(editText_inventario_maximo.getText().toString().trim().equals("")){
                            usuario.setMaximo_inventario("0");
                        }else{
                            usuario.setMaximo_inventario(editText_inventario_maximo.getText().toString().trim());
                        }

                    }else{
                        usuario.setMaximo_inventario("0");
                    }
                    myRefe.child("Usuarios").child(usuario.getId()).setValue(usuario);
                    Toast.makeText(context, getString(R.string.Usuario_guardado), Toast.LENGTH_SHORT).show();



                    //guardando HISTORIAL
                    Modelo_historial historial = new Modelo_historial();
                    historial.setId(UUID.randomUUID().toString());
                    historial.setReferencia(ID);
                    historial.setActividad(getString(R.string.Perfiles));
                    historial.setVisible(ID);
                    historial.setFecha(fecha_hora);
                   // historial.setFecha_y_hora(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    historial.setUsuario(MainActivity.Usuario);
                    if (id_exitente==""){
                        historial.setDescripcion(getString(R.string.Registro_usuario)+" "+editText_nombre.getText().toString().toString());
                    }else{
                        historial.setDescripcion(getString(R.string.Usuario_editado)+" "+editText_nombre.getText().toString().toString());
                    }

                    myRefe.child("Historial").child(historial.getId()).setValue(historial);



                    progressDialog.cancel();
                    //ocultar teclado en fragmento
                    InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
                    input.hideSoftInputFromWindow(vista.getWindowToken(), 0);
                    //cerrar el fragmento y volver al anterior
                    editText_nombre.setText("");
                    Navigation.findNavController(vista).navigateUp();
             //       StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mImageUrl);

                }else{
                    progressDialog.cancel();
                    Toast.makeText(context, R.string.problemas_conexion, Toast.LENGTH_SHORT).show();
                }

            }
        });


        }catch (Exception e){
            progressDialog.cancel();
            Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            return;

        }


    }

    public void ocultar_teclado(View view){
        //ocultar teclado
        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataQuery.removeEventListener(oyente);
        Glide.get(context).clearMemory();//clear memory
        opcion_editar_producto.setVisible(false);
        dataQuery=null;
        opcion_nuevo_perfil.setVisible(false);
        vista=null;
    }
}