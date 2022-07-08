package com.accesoritoons.gestortoons;

import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.metodos.Guardar_firebase;
import com.accesoritoons.gestortoons.modelos.Modelo_empresa;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Fragment_empresa extends Fragment {

    public static ImageView imageView_incluir_foto1;
    public static Uri imageUri;

    View vista;
    Context context;
    private EditText nombre, documento, dominio, telefono1, telefono2, pais, ciudad, direccion, fecha_modificacion, usuario_modificacion, correo,editTextNumber_garantia;
    private TextView logo;
    private String codigo_empresa, fecha_hora;
    private byte[] blob=null;
    private static final String LIST_KEY = "list_key100";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_empresa, container, false);

        nombre=vista.findViewById(R.id.editText_incluir_nombre_empresa);
        documento=vista.findViewById(R.id.textView_documento_empresa);
        correo=vista.findViewById(R.id.editText_correo_empresa);
        dominio=vista.findViewById(R.id.textView_dominio_empresa);
        telefono1=vista.findViewById(R.id.editText_telefono1);
        telefono2=vista.findViewById(R.id.textView_telefono2);
        direccion=vista.findViewById(R.id.editTextTextMultiLine_direccion_empresa);
        editTextNumber_garantia=vista.findViewById(R.id.editTextNumber_garantia);
        imageView_incluir_foto1=vista.findViewById(R.id.imageView_incluir_foto1);
//        logo= vista.findViewById(R.id.textView_logo);
        fecha_hora = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //foto vacia


//        imageView_incluir_foto1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // muestra el cuadro de diálogo de selección de imagen
//
//                //ocultar teclado
//                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
//                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//                Intent intent=new Intent(context, Activity_simple_foto.class);
//                intent.putExtra("nombre_imagen", "Fragment_empresa.imageView_incluir_foto1");
//                startActivity(intent);
//
//            }
//        });

        cargar_datos_empresa();
        return  vista;
    }

    public void cargar_datos_empresa(){

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Empresa").orderByChild("id").equalTo("1").limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_empresa empresa = userSnapshot.getValue(Modelo_empresa.class);

                        nombre.setText(empresa.getNombre());
                        documento.setText((empresa.getDocumento()));
                        correo.setText(empresa.getCorreo());
                        dominio.setText(empresa.getDominio());
                        telefono1.setText(empresa.getTelefono1());
                        telefono2.setText(empresa.getTelefono2());
                        direccion.setText(empresa.getDireccion());
                        editTextNumber_garantia.setText(empresa.getGarantia());
                        String url =empresa.getUrl();
//                        Picasso.with(context).load(url).into(imageView_incluir_foto1);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @SuppressLint("WrongThread")
    @Override
    public void onDetach() {
        super.onDetach();



        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Modelo_empresa empresa =new Modelo_empresa();

        empresa.setId("1");
        empresa.setNombre(nombre.getText().toString().trim());
        empresa.setDocumento(documento.getText().toString().trim());
        empresa.setCorreo(correo.getText().toString().trim());
        empresa.setDominio(dominio.getText().toString().trim());
        empresa.setTelefono1(telefono1.getText().toString().trim());
        empresa.setTelefono2(telefono2.getText().toString().trim());
        empresa.setDireccion(direccion.getText().toString().trim());
        empresa.setGarantia(editTextNumber_garantia.getText().toString().trim());



        myRefe.child("Empresa").child(empresa.getId()).setValue(empresa);
        MainActivity.datos_empresa.clear();
        MainActivity.datos_empresa.add(new Modelo_empresa(empresa.getId(),empresa.getNombre(),empresa.getDocumento(),empresa.getCorreo(),empresa.getDominio(),empresa.getTelefono1(),empresa.getTelefono2(),empresa.getDireccion(),empresa.getUrl(), empresa.getGarantia()));

        vista=null;
    }

}