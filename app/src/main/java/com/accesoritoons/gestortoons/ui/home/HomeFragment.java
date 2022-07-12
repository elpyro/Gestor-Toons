package com.accesoritoons.gestortoons.ui.home;

import static com.accesoritoons.gestortoons.MainActivity.Correo;
import static com.accesoritoons.gestortoons.MainActivity.Id_Usuario;
import static com.accesoritoons.gestortoons.MainActivity.Perfil;
import static com.accesoritoons.gestortoons.MainActivity.navigationView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accesoritoons.gestortoons.Activity_scanner;
import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.databinding.FragmentHomeBinding;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_usuario_activo;
import com.accesoritoons.gestortoons.recyclerViewAdaptador.RecyclerViewAdaptador_historial;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    Context context;
    public RecyclerView recview;
    SearchView searchView_perfiles;
    Query referencia;
    View root;
    ImageView imageView_scann;
    ArrayList<Modelo_historial> lista;

    //https://www.youtube.com/watch?v=IwdjCApjIzA&t=442s autenticacion
    FirebaseAuth firebaseAutenticacion;
    FirebaseAuth.AuthStateListener authStateListener;
    Query dataQuery;

    public static final int REQUEST_CODE=1234;
    List<AuthUI.IdpConfig> provinder= Arrays.asList(
            // new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        context=getContext();

        root = binding.getRoot();


        imageView_scann=(ImageView)root.findViewById(R.id.imageView_scan);
        recview=(RecyclerView)root.findViewById(R.id.recycler_historial);

//                   recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView_perfiles=(SearchView)root.findViewById(R.id.search_historial);


        imageView_scann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.fragment_venta_bodega);
                Intent intent = new Intent(getContext(), Activity_scanner.class);
                startActivity(intent);
            }
        });

        //LOGIN
        firebaseAutenticacion = FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAutenticacion.getCurrentUser();
                if (user != null) {

                    Correo=user.getEmail();

                    //verificar perfil segun el correo
                    DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                    dataQuery = myRefe.child("Usuarios").orderByChild("correo").equalTo(Correo).limitToFirst(1);
                    dataQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Modelo_usuario_activo usuario = userSnapshot.getValue(Modelo_usuario_activo.class);
                                    MainActivity.Perfil=usuario.getPerfil();
                                    MainActivity.Id_Usuario=usuario.getId();
                                    MainActivity.Usuario=usuario.getNombre();

                                    if (Perfil.equals(getString(R.string.Administrador))){
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, true);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);
                                        recview.setVisibility(View.VISIBLE);
                                        //cargar historial  si los datos del usuario son correctos
                                        cargar_historial();
                                    }else if (Perfil.equals(getString(R.string.Auditor))){
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);
                                        recview.setVisibility(View.VISIBLE);
                                        //cargar historial  si los datos del usuario son correctos
                                        cargar_historial_auditor();
                                    }else{
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, false);
                                        recview.setVisibility(View.GONE);
                                    }
                                }

                                return;
                            } else {
                                Perfil=getString(R.string.Usuario_no_registrado);
                                NavigationView navigationView = (NavigationView)root. findViewById(R.id.nav_view);
                                Toast.makeText(getContext(), getString(R.string.Usuario_no_registrado), Toast.LENGTH_LONG).show();
                                try{
                                    navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                    navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, false);
                                }catch (Exception e){

                                }

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            MainActivity.progressDialog.cancel();
                            Toast.makeText(getContext(), getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    MainActivity.Usuario=null;
//                    textView_credenciales.setVisibility(View.VISIBLE);
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(provinder)
                            .setIsSmartLockEnabled(false).build(), REQUEST_CODE);
                }
            }
        };

        return root;
    }


    @Override
    public void onPause() {
        super.onPause();
        firebaseAutenticacion.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            MainActivity.opcion_agregar_producto.setVisible(false);
            MainActivity.opcion_editar_producto.setVisible(false);
            MainActivity.opcion_linterna.setVisible(false);
            MainActivity.opcion_nuevo_producto.setVisible(false);
            MainActivity.opcion_buscar.setVisible(false);
            MainActivity.opcion_crear_pedido.setVisible(false);
            MainActivity.opcion_nuevo_perfil.setVisible(false);
            MainActivity.opcion_scanner.setVisible(false);
            MainActivity.opcion_agregar_inventario.setVisible(false);
            MainActivity.opcion_cambiar_cantidad.setVisible(false);
            MainActivity.opcion_comprar.setVisible(false);
            MainActivity.opcion_factura.setVisible(false);
            MainActivity.opcion_confirmar.setVisible(false);
            MainActivity.opcion_compartir.setVisible(false);
            MainActivity.opcion_compartir_logo.setVisible(false);
            MainActivity.opcion_whatapps.setVisible(false);
            MainActivity.opcion_no_internet.setVisible(false);
            MainActivity.opcion_realizar_recaudo.setVisible(false);
            //ocultar teclado
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(root.getWindowToken(), 0);

        }catch (Exception e){

        }

        if (Perfil.equals(getString(R.string.Administrador))){
            navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, true);
            navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);
            recview.setVisibility(View.VISIBLE);
            //cargar historial  si los datos del usuario son correctos
            cargar_historial();
        }else if (Perfil.equals(getString(R.string.Auditor))){
            navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
            navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);
            recview.setVisibility(View.VISIBLE);
            //cargar historial  si los datos del usuario son correctos
            cargar_historial_auditor();
        }else{
            navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
            navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, false);
            recview.setVisibility(View.GONE);
        }

        Runtime.getRuntime().gc();
        firebaseAutenticacion.addAuthStateListener(authStateListener);

    }

    private void cargar_historial_auditor(){

    }
    private void cargar_historial() {
        referencia= FirebaseDatabase.getInstance().getReference().child("Historial").orderByChild("fecha").limitToLast(300);
        if(referencia!=null){
            referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_historial.class));
                        }
                        //invertir array para invertir el orden que mustra el recycler
                        Collections.reverse(lista);

                        RecyclerViewAdaptador_historial adapador= new RecyclerViewAdaptador_historial(lista);
                        recview.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
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
        ArrayList<Modelo_historial> filtro =new ArrayList<>();
        for(Modelo_historial objeto:lista){
            if(objeto.getActividad().toLowerCase().contains(valor.toLowerCase())||objeto.getUsuario().toLowerCase().contains(valor.toLowerCase())||objeto.getFecha().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        RecyclerViewAdaptador_historial adaptador = new RecyclerViewAdaptador_historial(filtro);
        recview.setAdapter(adaptador);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataQuery=null;
        referencia=null;
        binding = null;

    }
}