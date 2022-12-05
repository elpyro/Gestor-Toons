package com.accesoritoons.gestortoons;

import static com.accesoritoons.gestortoons.MainActivity.opcion_editar_producto;
import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.accesoritoons.gestortoons.modelos.Modelo_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_clientes;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_perfiles;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FragmentoClientes extends Fragment {

    private View vista;
    private Context context;
    private Button button_nuevo_cliente;
    private SearchView search_clientes;
    private RecyclerView recycler_clientes;
    ArrayList<Modelo_cliente> lista;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_fragmento_clientes, container, false);
        inicializando();

        button_nuevo_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(vista).navigate(R.id.fragmentoNuevoCliente);
            }
        });
        return vista;
    }



    @Override
    public void onResume() {
        super.onResume();
        MainActivity.opcion_confirmar.setVisible(false);

        DatabaseReference referencia= FirebaseDatabase.getInstance().getReference().child("Clientes");
        if(referencia!=null){
             referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_cliente.class));
                        }
                        RecyclerViewAdaptador_clientes adapador= new RecyclerViewAdaptador_clientes(lista);
                        recycler_clientes.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (search_clientes!=null){
            search_clientes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_cliente> filtro =new ArrayList<>();
        for(Modelo_cliente objeto:lista){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerViewAdaptador_clientes adaptador = new RecyclerViewAdaptador_clientes(filtro);
        recycler_clientes.setAdapter(adaptador);
    }

    private void inicializando() {
        button_nuevo_cliente=vista.findViewById(R.id.button_nuevo_cliente);
        search_clientes=vista.findViewById(R.id.search_clientes);
        recycler_clientes=vista.findViewById(R.id.recycler_clientes);
        recycler_clientes.setLayoutManager(new LinearLayoutManager(context));
    }
}