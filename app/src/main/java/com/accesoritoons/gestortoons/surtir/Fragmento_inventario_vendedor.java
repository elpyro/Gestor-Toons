package com.accesoritoons.gestortoons.surtir;

import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_inventario_vendedor;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_perfiles;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;


public class Fragmento_inventario_vendedor extends Fragment {

View vista;
Context context;
TextView textView_inventario;
RecyclerView recview_inventario;
String id_usuario_fragmento;
RecyclerViewAdaptador_inventario_vendedor adapador;
LinearLayout scrollView_scroll;
SearchView searchView_perfiles;
    Query referencia_productos2;
    Query referencia_productos;
    Query dataQuery;

    public static int total_inventario=0;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_inventario;
    ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_inventario_vendedor, container, false);
        textView_inventario=(TextView)vista.findViewById(R.id.textView_inventario);
        recview_inventario=(RecyclerView)vista.findViewById(R.id.recycler_inventario);
        recview_inventario.setHasFixedSize(true);
        recview_inventario.setLayoutManager(new LinearLayoutManager(context));
        scrollView_scroll=(LinearLayout) vista.findViewById(R.id.scroll);

        searchView_perfiles=(SearchView)vista.findViewById(R.id.searchview);

scrollView_scroll.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(MainActivity.id_vendedor!=null){
            MainActivity.opcion_crear_pedido.setVisible(false);
            Navigation.findNavController(view).navigate(R.id.fragmento_inventario_vendedor);
        }

    }
});

        //recibir argumentos
        if (getArguments() != null) {
            id_usuario_fragmento =getArguments().getString("id_vendedor");
        }else{
            id_usuario_fragmento= MainActivity.id_vendedor;
        }

        //verificar tipo de vendedor y cargar inventario
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_usuario_fragmento).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        MainActivity.tipo_vendedor=userSnapshot.getValue(Modelo_usuario.class).getTipo();
                        cargar_inventario(id_usuario_fragmento);

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

        return vista;
    }


    public void cargar_inventario(String id_usuario_fragmento) {
            total_inventario=0;

            lista_inventario=new ArrayList<>();

            referencia_productos= FirebaseDatabase.getInstance().getReference().child("Inventarios").orderByChild("estado").equalTo("Inventario"+"-"+id_usuario_fragmento);
        referencia_productos.keepSynced(true);   
        try {
            Thread.sleep(1 * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            if(referencia_productos!=null){
                referencia_productos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista_inventario.add(ds.getValue(Modelo_producto_facturacion_app_vendedor.class));
                        }
                        total_inventario=0;
                        lista_productos=new ArrayList<>();
                        for (int x = 0; x < lista_inventario.size(); x++) {

                            referencia_productos2 = FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(lista_inventario.get(x).getId_referencia_producto());

                            if (referencia_productos2 != null) {
                                int finalX = x;

                                referencia_productos2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            try{
                                            for (DataSnapshot ds : snapshot.getChildren()) {

                                                for (int x = 0; x < lista_productos.size(); x++) {
                                                    Modelo_producto_facturacion_app_vendedor pedido =  lista_productos.get(x);
                                                    if (pedido.getId_referencia_producto().equals(ds.getValue(Modelo_producto.class).getId())) {
                                                        //eliminar de la lista si existe
                                                        int cantidad=0, costo=0;
                                                        if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Plata))){
                                                            costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_plantino()));
                                                        }else if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Oro))){
                                                            costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_oro()));
                                                        }if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Diamante))){
                                                            costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_diamante()));
                                                        }

                                                            cantidad=  Integer.parseInt(lista_inventario.get(finalX).getCantidad());
                                                            total_inventario=total_inventario-(cantidad*costo);
                                                            lista_productos.remove(x);

                                                        break;
                                                    }
                                                }

                                                int cantidad=  Integer.parseInt(lista_inventario.get(finalX).getCantidad());
                                                int costo=0;
                                                if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Plata))){
                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_plantino()));
                                                }else if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Oro))){
                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_oro()));
                                                }if(MainActivity.tipo_vendedor.equals(context.getString(R.string.Diamante))){
                                                    costo= Integer.parseInt((ds.getValue(Modelo_producto.class).getP_diamante()));
                                                }

                                                lista_productos.add(new Modelo_producto_facturacion_app_vendedor(ds.getValue(Modelo_producto.class).getNombre(), costo+"",  ds.getValue(Modelo_producto.class).getP_detal(),  ds.getValue(Modelo_producto.class).getUrl(), lista_inventario.get(finalX).getCantidad(),"","",lista_inventario.get(finalX).getId_referencia_producto(),"",id_usuario_fragmento,id_usuario_fragmento+"-"+lista_inventario.get(finalX).getId_referencia_producto(),"","",ds.getValue(Modelo_producto.class).getCliente_mis_productos(),ds.getValue(Modelo_producto.class).getMis_productos(),ds.getValue(Modelo_producto.class).getCodigo(),MainActivity.Usuario,"","","",""));
                                                total_inventario=total_inventario+(cantidad*costo);
                                            }
                                        }catch (Exception e){
                                            Navigation.findNavController(getView()).navigateUp();

                                        }
                                            //ordenar
                                            referencia_productos2=null;
                                            lista_productos.sort(Comparator.comparing(Modelo_producto_facturacion_app_vendedor::getCliente_mis_productos));
                                            adapador= new RecyclerViewAdaptador_inventario_vendedor(lista_productos);
                                            recview_inventario.setAdapter(adapador);
                                        }
                                        NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
                                        textView_inventario.setText(context.getString(R.string.Invetario)+": "+formatoImporte.format(total_inventario));
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
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
            if (searchView_perfiles!=null){
                searchView_perfiles.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_producto_facturacion_app_vendedor> filtro =new ArrayList<>();
        for(Modelo_producto_facturacion_app_vendedor objeto:lista_productos){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerViewAdaptador_inventario_vendedor adaptador = new RecyclerViewAdaptador_inventario_vendedor(filtro);
        recview_inventario.setAdapter(adaptador);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dataQuery=null;
        referencia_productos2=null;
        referencia_productos=null;
        vista=null;
    }
}