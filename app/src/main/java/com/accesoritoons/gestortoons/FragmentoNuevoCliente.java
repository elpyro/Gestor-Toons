package com.accesoritoons.gestortoons;

import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.accesoritoons.gestortoons.modelos.Modelo_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class FragmentoNuevoCliente extends Fragment {

    private View vista;
    private Context context;
    private String id_cliente;
    private EditText editTextTextPersonName, editTextNumberSigned,editTextPhone,editTextTextMultiLine_direccion;
    private Button button_guardar_cliente;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista=inflater.inflate(R.layout.fragment_fragmento_nuevo_cliente, container, false);
        inicializar();
        recibirArgumentos();
        button_guardar_cliente.setOnClickListener(view -> guardar());
        return vista;
    }

    private void recibirArgumentos() {

            if (getArguments() != null) {
                id_cliente =getArguments().getString("id_cliente");


                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

                Query dataQuery = myRefe.child("Clientes").orderByChild("id").equalTo(id_cliente).limitToFirst(1);
                dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                Modelo_cliente cliente = userSnapshot.getValue(Modelo_cliente.class);
                                editTextTextPersonName.setText(cliente.getNombre());
                                editTextPhone.setText(cliente.getTelefono());
                                editTextNumberSigned.setText(cliente.getDocumento());
                                editTextTextMultiLine_direccion.setText(cliente.getDireccion());
                            }
                        } else {

                            Toast.makeText(context,"Cliente inexistente" , Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.cancel();
                        Toast.makeText(context, getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }


    private void guardar() {
        if(editTextTextPersonName.getText().toString().isEmpty()) {
            editTextTextPersonName.setError("Obligatorio");
        }else {

            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Modelo_cliente cliente = new Modelo_cliente();

            if(id_cliente==null){
                cliente.setId(UUID.randomUUID().toString());
            }else{
                cliente.setId(id_cliente);
            }
            cliente.setNombre(editTextTextPersonName.getText().toString().trim());
            cliente.setDocumento(editTextNumberSigned.getText().toString().trim());
            cliente.setTelefono(editTextPhone.getText().toString().trim());
            cliente.setDireccion(editTextTextMultiLine_direccion.getText().toString().trim());
          
            myRefe.child("Clientes").child(cliente.getId()).setValue(cliente);
            Toast.makeText(context, "Cliente guardado", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(vista).navigateUp();


        }
    }

    private void inicializar() {
        editTextTextPersonName=vista.findViewById(R.id.editTextTextPersonName);
        editTextNumberSigned=vista.findViewById(R.id.editTextNumberSigned);
        editTextPhone=vista.findViewById(R.id.editTextPhone);
        editTextTextMultiLine_direccion=vista.findViewById(R.id.editTextTextMultiLine_direccion);
        button_guardar_cliente=vista.findViewById(R.id.button_guardar_cliente);
    }
}