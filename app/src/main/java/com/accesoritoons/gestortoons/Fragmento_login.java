package com.accesoritoons.gestortoons;

import static com.accesoritoons.gestortoons.MainActivity.Perfil;
import static com.accesoritoons.gestortoons.MainActivity.navigationView;
import static com.accesoritoons.gestortoons.R.drawable.avatar_usuario;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.accesoritoons.gestortoons.modelos.Modelo_usuario_activo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class Fragmento_login extends Fragment {

    //https://www.youtube.com/watch?v=IwdjCApjIzA&t=442s autenticacion
    FirebaseAuth firebaseAutenticacion;
    Context context;
    FirebaseAuth.AuthStateListener authStateListener;
    TextView textView_correo;
    TextView textView_perfil;
    Button buttonCerrar_sesion;
    View vista;
    ShimmerTextView myShimmerTextView;
    ImageView imageView_avatar;
    public static final int REQUEST_CODE=1234;
    List<AuthUI.IdpConfig> provinder= Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       vista=inflater.inflate(R.layout.fragment_fragmento_login, container, false);
        context=getContext();
        textView_correo=vista.findViewById(R.id.textView_correo);
        textView_perfil=vista.findViewById(R.id.textView_perfil);
        buttonCerrar_sesion=vista.findViewById(R.id.Cerrar_sesion);
        myShimmerTextView=vista.findViewById(R.id.shimmer_tv);
        imageView_avatar=vista.findViewById(R.id.imageView_avatar);


        Shimmer shimmer = new Shimmer();
        shimmer.start(myShimmerTextView);

        firebaseAutenticacion =FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAutenticacion.getCurrentUser();
                if (user != null) {

                    //verificar perfil segun el correo
                    DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                    Query dataQuery = myRefe.child("Usuarios").orderByChild("correo").equalTo(user.getEmail()).limitToFirst(1);
                    dataQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Modelo_usuario_activo usuario = userSnapshot.getValue(Modelo_usuario_activo.class);

                                    Picasso.with(context).load(usuario.getUrl_foto_usuario()).into(imageView_avatar);
                                    MainActivity.Perfil=usuario.getPerfil();
                                    MainActivity.Id_Usuario=usuario.getId();
                                    MainActivity.Usuario=usuario.getNombre();
                                    MainActivity.Correo=usuario.getCorreo();
                                    myShimmerTextView.setText(MainActivity.Usuario);
                                    textView_perfil.setText(MainActivity.Perfil);
                                    textView_correo.setText(MainActivity.Correo);
                                    if (Perfil.equals(getString(R.string.Administrador))){
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, true);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);

                                        //cargar historial  si los datos del usuario son correctos

                                    }else if (Perfil.equals(getString(R.string.Auditor))){
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, true);

                                    }else {
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                        navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, false);

                                    }

                                }
                                return;
                            } else {
                                MainActivity.Perfil=getString(R.string.Usuario_no_registrado);

                                MainActivity.Id_Usuario="";

                                myShimmerTextView.setText("");
                                textView_perfil.setText(getString(R.string.Usuario_no_registrado));
                                textView_correo.setText("");
                                imageView_avatar.setImageDrawable(getResources().getDrawable(avatar_usuario));
                                navigationView.getMenu().setGroupVisible(R.id.grupo_administrador, false);
                                navigationView.getMenu().setGroupVisible(R.id.grupo_auditor, false);

                                MainActivity.Perfil=getString(R.string.Usuario_no_registrado);
                                MainActivity.Usuario=getString(R.string.Usuario_no_registrado);
                                MainActivity.tipo_vendedor=getString(R.string.Usuario_no_registrado);
                                MainActivity.Correo=user.getEmail();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(getContext(), getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
                        }

                    });




                } else {
                    myShimmerTextView.setText("Iniciando Sesión");
                    textView_perfil.setText("");
                    textView_correo.setText("");
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(provinder)
                            .setIsSmartLockEnabled(false).build(), REQUEST_CODE);
                }
            }
        };

        buttonCerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cerrar_sesion(view);
            }
        });

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAutenticacion.addAuthStateListener(authStateListener);
    }
    @Override
    public void onPause() {
        super.onPause();
        firebaseAutenticacion.removeAuthStateListener(authStateListener);
    }

    public void Cerrar_sesion(View view) {

        AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Sesion Cerrada", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }

}