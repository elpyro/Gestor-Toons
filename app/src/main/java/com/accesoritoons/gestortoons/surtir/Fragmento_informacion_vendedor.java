package com.accesoritoons.gestortoons.surtir;

import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_pedido;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_pedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

public class Fragmento_informacion_vendedor extends Fragment {
    View vista;
    Context context;
    public String id_usuario;
    public static String   nombre_vendedor;
    String id_exitente="", url_avatar;
    TextView  textView_telefono, textView_maximo_inventario, textView_direccion, textview_tipo, textview_inventario;
    TextView textView_nombre;
 

    ImageView imageView_avatar, imageView_enviar;
    LinearLayout linearLayout_datos_usuario;
    Button button_inventario, button_por_recaudo, button_recaudos;
    double total_inventario=0.0;

    RecyclerView recview_pedidos;
    Query referencia;
    ArrayList<Modelo_pedido> lista_pedidos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_informacion_vendedor, container, false);

        textView_nombre=vista.findViewById(R.id.textView_nombre);
        textView_telefono=vista.findViewById(R.id.textView_telefono);
        textView_maximo_inventario=vista.findViewById(R.id.textView_maximo_inventario);
        textView_direccion=vista.findViewById(R.id.textView_direccion);
        imageView_avatar=vista.findViewById(R.id.imageView_avatar);
        imageView_enviar=vista.findViewById(R.id.imageView_whatsapp);
        linearLayout_datos_usuario=vista.findViewById(R.id.linearLayout_id);
        textview_tipo =vista.findViewById(R.id.textView_tipo);
        button_inventario=vista.findViewById(R.id.button_inventario);
        button_por_recaudo=vista.findViewById(R.id.button_por_recaudo);
        button_recaudos=vista.findViewById(R.id.button_recaudos);



        recview_pedidos=(RecyclerView)vista.findViewById(R.id.recycler_pedidos);
        recview_pedidos.setLayoutManager(new LinearLayoutManager(context));


        button_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.opcion_crear_pedido.setVisible(false);
                Navigation.findNavController(view).navigate(R.id.fragmento_inventario_vendedor);
            }
        });

        button_por_recaudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.opcion_crear_pedido.setVisible(false);
                Navigation.findNavController(view).navigate(R.id.fragment_lista_recaudos);
            }
        });

        button_recaudos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.opcion_crear_pedido.setVisible(false);
                Bundle bundle = new Bundle();
                bundle.putString("id_vendedor", id_exitente);
                Navigation.findNavController(view).navigate(R.id.fragment_recaudado_cliente, bundle);
            }
        });


        //recibir argumentos
        if (getArguments() != null) {
            usuario_existente();
        }else{
            Toast.makeText(context, "Usuario no Existente", Toast.LENGTH_SHORT).show();
        }





        imageView_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(url_avatar!=null){
                    MainActivity.opcion_crear_pedido.setVisible(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("url_imagen",url_avatar);
                    Navigation.findNavController(view).navigate(R.id.fragmento_foto_apliada,bundle);
                }

            }
        });
//        linearLayout_datos_usuario.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                MainActivity.opcion_crear_pedido.setVisible(false);
//                Bundle bundle = new Bundle();
//                bundle.putString("id_usuario", id_exitente);
//                Navigation.findNavController(view).navigate(R.id.fragmento_nuevo_perfil,bundle);
//                return false;
//            }
//        });
        imageView_enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                enviarMensaje_whatsapps();
            }
        });
        MainActivity.opcion_crear_pedido.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MainActivity.opcion_crear_pedido.setVisible(false);
                Bundle bundle = new Bundle();
                bundle.putString("id_usuario", id_exitente);
                bundle.putString("nombre_usuario", textView_nombre.getText().toString());
                bundle.putString("max_inventario", textView_maximo_inventario.getText().toString());
                Navigation.findNavController(vista).navigate(R.id.contenedor_agregar_inventario,bundle);
                return false;
            }
        });

        return vista;
    }

    private void cargar_pedidos() {
        try {
            referencia=null;
            referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("referencia_vendedor").equalTo(id_usuario);

            if(referencia!=null){
                referencia.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lista_pedidos=new ArrayList<>();
                        if (snapshot.exists()){
                            for(DataSnapshot ds:snapshot.getChildren()){
                                boolean existe=false;
                                for (int x = 0; x < lista_pedidos.size(); x++) {
                                    Modelo_pedido pedido =  lista_pedidos.get(x);
                                    if (pedido.getId_pedido().equals(ds.getValue(Modelo_pedido.class).getId_pedido())) {
                                        existe=true;
                                        break;
                                    }
                                }
                                if (existe==false) lista_pedidos.add(ds.getValue(Modelo_pedido.class));
                            }
                            //ordenar por fecha
                            lista_pedidos.sort(Comparator.comparing(Modelo_pedido::getFecha).reversed());
                        }
                        RecyclerViewAdaptador_pedidos adapador= new RecyclerViewAdaptador_pedidos(lista_pedidos);
                        recview_pedidos.setAdapter(adapador);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        total_inventario=0;
        if (MainActivity.Perfil.equals("Administrador"))MainActivity.opcion_crear_pedido.setVisible(true);
        MainActivity.opcion_crear_pedido.setEnabled(true);
        cargar_pedidos();
    }



    private void enviarMensaje_whatsapps (){
        String smsNumber = "57"+textView_telefono.getText().toString();
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            try { Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola, ");
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
                //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent); } catch(Exception e)
            { Toast.makeText(context, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show(); } } else
        { Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(context,"Whatapp no instaldo", Toast.LENGTH_SHORT).show();
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


    private void usuario_existente() {

        id_usuario =getArguments().getString("id_usuario");

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Query dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_usuario).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_usuario usuario = userSnapshot.getValue(Modelo_usuario.class);
                        id_exitente=usuario.getId();
                        textView_nombre.setText(usuario.getNombre());
                        textView_maximo_inventario.setText(usuario.getMaximo_inventario());
                        textView_telefono.setText(usuario.getTelefono());
                        textView_direccion.setText(usuario.getDireccion());
                        textview_tipo.setText(usuario.getTipo());
                        url_avatar=(usuario.getUrl_foto_usuario());

                        nombre_vendedor=textView_nombre.getText().toString();
                         Picasso.with(context).load(url_avatar).into(imageView_avatar);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Fragmento_inventario_vendedor.total_inventario=0;
        MainActivity.opcion_crear_pedido.setVisible(false);
        MainActivity.id_vendedor=null;
        vista=null;
    }
}