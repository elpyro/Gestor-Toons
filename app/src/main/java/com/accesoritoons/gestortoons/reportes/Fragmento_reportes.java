package com.accesoritoons.gestortoons.reportes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;

import com.accesoritoons.gestortoons.modelos.Modelo_spinner_vendedores;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;

import com.accesoritoons.gestortoons.reportes_pdf.Activity_vista_pdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Fragmento_reportes extends Fragment {

    View vista;
    Context context;
    Button button_desde, button_hasta,button_crear_pdf;
    TextView editText_desde, editText_hasta;
    Spinner spinner_vendedores, spinner_reporte, spinner_administradores;
    LinearLayout linearLayout_fechas;
    private String fecha_hasta, fecha_desde, busqueda_vendedor, tipo_reporte, id_tipo_reporte, id_vendedor;
    private int dia,mes,ano;
    List<String[]> productos_para_pdf = new ArrayList<>();

    ArrayList<Modelo_spinner_vendedores> vendedores =new ArrayList<>();
    ArrayList<Modelo_spinner_vendedores> administradores =new ArrayList<>();
    Query referencia;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_fragmento_reportes, container, false);
        context=getContext();

        button_desde=(Button) vista.findViewById(R.id.button_desde);
        button_hasta=(Button) vista.findViewById(R.id.button_hasta);
        button_crear_pdf=(Button) vista.findViewById(R.id.button_crear_pdf);
        editText_desde=(TextView)vista.findViewById(R.id.editText_desde);
        editText_hasta=(TextView)vista.findViewById(R.id.textView_hasta);
        spinner_vendedores=(Spinner)vista.findViewById(R.id.spinner_vendedores);
        spinner_administradores=(Spinner)vista.findViewById(R.id.spinner_administradores);
        spinner_reporte=(Spinner)vista.findViewById(R.id.spinner_reportes);
        linearLayout_fechas=(LinearLayout)vista.findViewById(R.id.linearLayout_fechas);

        cargar_vendedores();

        button_crear_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(id_tipo_reporte.equals("0")) {
                    tipo_reporte="Ganancias";
                }
                if(id_tipo_reporte.equals("1")) {
                    tipo_reporte = "Inventario";

                }

                if(id_tipo_reporte.equals("2")) {
                    tipo_reporte = "Recaudados";

                }

                if(id_tipo_reporte.equals("3")) {
                    tipo_reporte = "Por recaudo";

                }

                if(id_tipo_reporte.equals("3")) {
                    tipo_reporte = "Por recaudo";

                }

                if(id_tipo_reporte.equals("4")) {
                    tipo_reporte = "Compras";
                }
                if(id_tipo_reporte.equals("5")) {
                    tipo_reporte = "Pedidos enviados";
                }

                if(id_tipo_reporte.equals("6")) {
                    tipo_reporte = "Garantias";
                }


                crear_reporte();

                }
        });

        spinner_reporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_tipo_reporte= String.valueOf(adapterView.getItemIdAtPosition(i));

                //SEGUN EL REPORTE MUESTRA LAS FECHAS
                if(id_tipo_reporte.equals("1")||id_tipo_reporte.equals("3")||id_tipo_reporte.equals("5") ){
                    linearLayout_fechas.setVisibility(View.GONE);
                    spinner_vendedores.setVisibility(View.VISIBLE);
                }else {
                    linearLayout_fechas.setVisibility(View.VISIBLE);
                }

                //SEGUN EL REPORTE MUESTRA EL SPINNER DE ADMINISTRADORES O VENDEDORES
                if(id_tipo_reporte.equals("2")||id_tipo_reporte.equals("4")||id_tipo_reporte.equals("6")){
                    spinner_administradores.setVisibility(View.VISIBLE);
                    spinner_vendedores.setVisibility(View.GONE);
                }else{
                    spinner_administradores.setVisibility(View.GONE);
                    spinner_vendedores.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_vendedores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemIdAtPosition(i)==0)busqueda_vendedor="Todos";
                if (adapterView.getItemIdAtPosition(i)==1)busqueda_vendedor="Bodega";
                if (adapterView.getItemIdAtPosition(i)>1){
                    busqueda_vendedor= vendedores.get(i).getVendedor();
                    id_vendedor= vendedores.get(i).getId_vendedor();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_administradores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemIdAtPosition(i)==0)busqueda_vendedor="Todos";

                if (adapterView.getItemIdAtPosition(i)>0){
                            busqueda_vendedor= administradores.get(i).getVendedor();
                            id_vendedor= administradores.get(i).getId_vendedor();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        button_desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                ano=c.get(Calendar.YEAR);

                DatePickerDialog datepickerDialogo = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        if(dayOfMonth<10){
                            fecha_desde=year+"-"+month+"-"+"0"+dayOfMonth;
                        }else{
                            fecha_desde=year+"-"+month+"-"+dayOfMonth;
                        }
                        editText_desde.setText(fecha_desde);
                    }
                },ano,mes,dia);
                datepickerDialogo.show();
            }
        });

        button_hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                ano=c.get(Calendar.YEAR);

                DatePickerDialog datepickerDialogo = new DatePickerDialog (getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        if(dayOfMonth<10){
                            fecha_hasta=year+"-"+month+"-"+"0"+dayOfMonth;
                        }else{
                            fecha_hasta=year+"-"+month+"-"+dayOfMonth;
                        }

                        editText_hasta.setText(fecha_hasta);
                    }
                },ano,mes,dia);
                datepickerDialogo.show();
            }
        });

        return vista;
    }

    private void crear_reporte() {
        Intent intent = new Intent(context, Activity_vista_pdf.class);
        intent.putExtra("tipo_reporte", tipo_reporte);
        intent.putExtra("desde", editText_desde.getText());
        intent.putExtra("hasta", editText_hasta.getText());
        intent.putExtra("vendedor",busqueda_vendedor);
        intent.putExtra("id_vendedor", id_vendedor);
        startActivity(intent);
    }


    private void cargar_vendedores() {

        referencia= FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("perfil");
        referencia.keepSynced(true);
        try {
            Thread.sleep(1 * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(referencia!=null){
            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //AGREGAR PREDETERMINADO <TODOS Y BODEGA A LISTA DE VENDEDORES
                    int contador=2;
                    vendedores.add(new Modelo_spinner_vendedores("1",context.getString(R.string.Todos),""));
                    administradores.add(new Modelo_spinner_vendedores("1",context.getString(R.string.Todos),""));
                    vendedores.add(new Modelo_spinner_vendedores("2",context.getString(R.string.Bodega),""));
                    if (snapshot.exists()){

                        for(DataSnapshot ds:snapshot.getChildren()){
                            contador=contador+1;


                            if(ds.getValue(Modelo_usuario.class).getPerfil().equals("Vendedor")){
                                vendedores.add(new Modelo_spinner_vendedores(contador+"",ds.getValue(Modelo_usuario.class).getNombre(),ds.getValue(Modelo_usuario.class).getId()));
                            }else{
                                administradores.add(new Modelo_spinner_vendedores(contador+"",ds.getValue(Modelo_usuario.class).getNombre(),ds.getValue(Modelo_usuario.class).getId()));
                            }
                        }

                        //llenando el spinner con los vendedores
                        ArrayAdapter<Modelo_spinner_vendedores>adapter=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item, vendedores);
                        spinner_vendedores.setAdapter(adapter);
                        ArrayAdapter<Modelo_spinner_vendedores>adapter_adaministradores=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item, administradores);
                        spinner_administradores.setAdapter(adapter_adaministradores);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        referencia=null;
        vista=null;

    }

}

