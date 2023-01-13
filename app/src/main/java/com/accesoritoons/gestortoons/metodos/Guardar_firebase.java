package com.accesoritoons.gestortoons.metodos;

import static com.accesoritoons.gestortoons.MainActivity.progressDialog;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_empresa;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_historial;
import com.accesoritoons.gestortoons.modelos.Modelo_producto;

import com.accesoritoons.gestortoons.modelos.Modelo_producto_facturacion_app_vendedor;
import com.accesoritoons.gestortoons.surtir.Fragmento_pedido_enviado;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Guardar_firebase extends Application {

    String descripcion_compra;
    public void guardar_historial(String referencia, String actividad,String visibilidad, String Descripcion, Context context) {
        String fecha_hora= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
        DatabaseReference myRefe;
        myRefe = FirebaseDatabase.getInstance().getReference();

        //guardando HISTORIAL
        Modelo_historial historial = new Modelo_historial();
        historial.setId(UUID.randomUUID().toString());
        historial.setReferencia(referencia);
        historial.setActividad(actividad);
        historial.setVisible(visibilidad);
        historial.setFecha(fecha_hora);
        historial.setDescripcion(Descripcion);
        historial.setUsuario(MainActivity.Usuario);
        myRefe.child("Historial").child(historial.getId()).setValue(historial);
        Toast.makeText(context, Descripcion, Toast.LENGTH_SHORT).show();
    }

    public void verificar_existencia_inventario(ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos_recibidos, String referencia_vendedor, Context contexto) {


        for (int x = 0; x < lista_productos_recibidos.size(); x++) {
            String id_referencia_producto=lista_productos_recibidos.get(x).getId_referencia_producto();

            Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Inventarios").orderByChild("vendedor_producto").equalTo(referencia_vendedor+"-"+ id_referencia_producto);

            referencia_id_producto.keepSynced(true);
            try {
                Thread.sleep(1 * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(referencia_id_producto!=null) {
                int finalX = x;
                referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ArrayList<Modelo_producto_facturacion_app_vendedor> lista_inventario = new ArrayList<>();
                            String nombre = null;
                            String venta = null;
                            String costo = null;
                            String url = null;
                            String cantidad = null;
                            String id_pedido_inventario = null;
                            String id_producto_pedido_inventario = null;
                            String id_referencia_producto_inventario = null;
                            String estado = null;
                            String id_referencia_vendedor_inventario = null;
                            String vendedor_producto=null;
                            int Cantidad = 0;
                            for (DataSnapshot snap : snapshot.getChildren()) {

                                lista_inventario.add(snap.getValue(Modelo_producto_facturacion_app_vendedor.class));
                                nombre = lista_inventario.get(0).getNombre();
                                costo = lista_inventario.get(0).getCosto();
                                venta = lista_inventario.get(0).getVenta();
                                url = lista_inventario.get(0).getUrl();
                                int cantidad_recibida=Integer.parseInt(lista_productos_recibidos.get(finalX).getCantidad());
                                int cantidad_inventario=Integer.parseInt(lista_inventario.get(0).getCantidad());
                                Cantidad = cantidad_inventario+cantidad_recibida ;
                                id_pedido_inventario = lista_inventario.get(0).getId_pedido();
                                id_producto_pedido_inventario = lista_inventario.get(0).getId_producto_pedido();
                                id_referencia_producto_inventario = lista_inventario.get(0).getId_referencia_producto();
                                estado = lista_inventario.get(0).getEstado();
                                id_referencia_vendedor_inventario = referencia_vendedor;
                                vendedor_producto=lista_inventario.get(0).getVendedor_producto();

                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                cantidad = Cantidad + "";
                                Modelo_producto_facturacion_app_vendedor productos = lista_productos_recibidos.get(finalX);
                                productos.setNombre(nombre);
                                productos.setCosto(costo);
                                productos.setVenta(venta);
                                productos.setUrl(url);
                                productos.setCantidad(cantidad);
                                productos.setId_pedido(id_pedido_inventario);
                                productos.setId_producto_pedido(id_producto_pedido_inventario);
                                productos.setId_referencia_producto(id_referencia_producto_inventario);
                                productos.setEstado(estado);
                                productos.setId_referencia_vendedor(id_referencia_vendedor_inventario);
                                productos.setVendedor_producto(vendedor_producto);
                                myRefe.child("Inventarios").child(productos.getVendedor_producto()).setValue(productos);
                                return;
                            }
//                            Fragmento_pedido_enviado actulizar =new Fragmento_pedido_enviado();
//                            actulizar.registrar_como_venta_bodega();

                        }else{

                            guardar_inventario(finalX,lista_productos_recibidos,referencia_vendedor);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

    }
    public void guardar_inventario(int x, ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos_recibidos,String referencia_vendedor) {
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();

        Modelo_producto_facturacion_app_vendedor productos =  lista_productos_recibidos.get(x);

        productos.setNombre(lista_productos_recibidos.get(x).getNombre());
        productos.setId_referencia_vendedor(referencia_vendedor);
        productos.setCantidad(lista_productos_recibidos.get(x).getCantidad());
        productos.setEstado("Inventario"+"-"+referencia_vendedor);
        productos.setId_referencia_producto(lista_productos_recibidos.get(x).getId_referencia_producto());
        productos.setVendedor_producto(referencia_vendedor+"-"+lista_productos_recibidos.get(x).getId_referencia_producto());
        myRefe.child("Inventarios").child(productos.getVendedor_producto()).setValue(productos);

    }

    public void eliminar_pedido_enviado(ArrayList<Modelo_producto_facturacion_app_vendedor> lista_productos_recibidos) {
        //eliminar el pedido enviado
        Query referencia= FirebaseDatabase.getInstance().getReference();
        for (int x = 0; x < lista_productos_recibidos.size(); x++) {
            referencia=null;
            referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_producto_pedido").equalTo(lista_productos_recibidos.get(x).getId_producto_pedido()).limitToFirst(1);
                            try {
                    Thread.sleep(1 * 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            if(referencia!=null){
                int finalX = x;
                referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ds.getRef().removeValue();
                            }


//                            String cantidad = null;
//                            String estado = null;
//                            String fecha = null;
//                            String id_pedido_actualizar = null;
//                            String id_producto_pedido = null;
//                            String referencia_producto = null;
//                            String referencia_vendedor = null;
//                            String usuario = null;
//                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                lista_producto_pedido.add(snapshot.getValue(Modelo_pedido.class));
//
//                                cantidad = lista_producto_pedido.get(finalX).getCantidad();
//                                estado= context.getString(R.string.Recibido);
//                                fecha = lista_producto_pedido.get(finalX).getFecha();
//                                id_pedido_actualizar = lista_producto_pedido.get(finalX).getId_pedido();
//                                id_producto_pedido = lista_producto_pedido.get(finalX).getId_producto_pedido();
//                                referencia_producto = lista_producto_pedido.get(finalX).getReferencia_producto();
//                                referencia_vendedor = lista_producto_pedido.get(finalX).getReferencia_vendedor();
//                                usuario = MainActivity.Usuario;
//
//                            }
//
//                            myRefe = FirebaseDatabase.getInstance().getReference();
//                            Modelo_pedido actualizar_pedido = new Modelo_pedido();
//
//                            actualizar_pedido.setCantidad(cantidad);
//                            actualizar_pedido.setEstado(estado);
//                            actualizar_pedido.setFecha(fecha);
//                            actualizar_pedido.setId_pedido(id_pedido_actualizar);
//                            actualizar_pedido.setId_producto_pedido(id_producto_pedido);
//                            actualizar_pedido.setReferencia_producto(referencia_producto);
//                            actualizar_pedido.setReferencia_vendedor(referencia_vendedor);
//                            actualizar_pedido.setUsuario(usuario);
//
//
//                            myRefe.child("Pedidos").child(actualizar_pedido.getId_producto_pedido()).setValue(actualizar_pedido);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                      //  Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void eliminar_producto_pedido(String id_producto_pedido,String id_pedido, String id_producto, String cantidad){
        Query referencia= null;

            referencia= FirebaseDatabase.getInstance().getReference().child("Pedidos").orderByChild("id_producto_pedido").equalTo(id_producto_pedido).limitToFirst(1);
                           referencia.keepSynced(true);
                            try {
                    Thread.sleep(1 * 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            if(referencia!=null){

                referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ds.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //  Toast.makeText(getContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        //RESTA INVENTARIO
        Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_producto);
        referencia_id_producto.keepSynced(true);
        try {
            Thread.sleep(1 * 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(referencia_id_producto!=null) {
            referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Modelo_producto producto = ds.getValue(Modelo_producto.class);
                            int cantidad_seleccionada = Integer.parseInt(cantidad);
                            int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                            int Cantidad = 0;
                            Cantidad=cantidad_inventario+cantidad_seleccionada;

                            producto.setCantidad(Cantidad+"");
                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                            myRefe.child("Productos").child(producto.getId()).setValue(producto);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void eliminar_producto_factura( String id_producto, String id_producto_factura, String cantidad, boolean compra){
        Query referencia= null;

        referencia= FirebaseDatabase.getInstance().getReference().child("Factura_productos").orderByChild("id_producto_pedido").equalTo(id_producto_factura).limitToFirst(1);
                referencia.keepSynced(true);
        try {
            Thread.sleep(1 * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
        if(referencia!=null){

            referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                      Toast.makeText(getBaseContext(), "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //SUMAR AL  INVENTARIO
        Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_producto);
        referencia_id_producto.keepSynced(true);
        try {
            Thread.sleep(1 * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(referencia_id_producto!=null) {
            referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Modelo_producto producto = ds.getValue(Modelo_producto.class);
                            int cantidad_seleccionada = Integer.parseInt(cantidad);
                            int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                            int Cantidad = 0;

                            if (compra==true){
                                Cantidad=cantidad_inventario-cantidad_seleccionada;
                            }else{
                                Cantidad=cantidad_inventario+cantidad_seleccionada;
                            }


                            producto.setCantidad(Cantidad+"");
                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                            myRefe.child("Productos").child(producto.getId()).setValue(producto);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void agregar_a_factura_existente(Context context, String id_factura){
        String fecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
        descripcion_compra=context.getString(R.string.Venta_bodega_mayor);
        for (int x = 0; x < MainActivity.lista_seleccion_venta_mayor_bodega.size(); x++) {

            String id_referencia_producto=MainActivity.lista_seleccion_venta_mayor_bodega.get(x).getId();

            Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_referencia_producto);

            referencia_id_producto.keepSynced(true);
            try {
                Thread.sleep(1 * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(referencia_id_producto!=null) {

                int finalX1 = x;

                referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Modelo_producto producto = ds.getValue(Modelo_producto.class);

                                String nombre=MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getNombre();
                                String referencia_producto=id_referencia_producto;
                                String vendedor_producto=MainActivity.Id_Usuario + "-" + id_referencia_producto;
                                String costo_compra=MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getP_compra();
                                String precio_venta=MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getP_detal();
                                String url=MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getUrl();
                                int cantidad_seleccionada = Integer.parseInt(MainActivity.lista_seleccion_venta_mayor_bodega.get(finalX1).getSeleccion());

                                //RESTA INVENTARIO
                                int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                                int Cantidad = 0;
                                Cantidad=cantidad_inventario-cantidad_seleccionada;

                                producto.setCantidad(Cantidad+"");
                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                myRefe.child("Productos").child(producto.getId()).setValue(producto);

                                //guardar datos de productos facturados
                                DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

                                Modelo_producto_facturacion_app_vendedor productos = new Modelo_producto_facturacion_app_vendedor();
                                productos.setId_pedido(id_factura);
                                productos.setId_producto_pedido(UUID.randomUUID().toString());
                                productos.setNombre(nombre);
//                                          productos.setCodigo_barras(MainActivity.lista_seleccion.get(finalX2).getCodigo());
                                productos.setId_referencia_vendedor(MainActivity.Id_Usuario);
                                productos.setCantidad(cantidad_seleccionada+"");
                                productos.setEstado(context.getString(R.string.Venta_bodega));
                                productos.setId_referencia_producto(referencia_producto);
                                productos.setVendedor_producto(vendedor_producto);
                                productos.setNombre_vendedor("Bodega");
                                productos.setFecha(fecha.substring(0,10));
                                productos.setFecha_recaudo(fecha.substring(0,10));
                                productos.setCosto(costo_compra);
                                productos.setVenta(precio_venta);
                                productos.setId_administrador_recaudo(MainActivity.Id_Usuario);
                                productos.setRecaudo(MainActivity.Usuario);
                                productos.setUrl(url);
                                referencia.child("Factura_productos").child(productos.getId_producto_pedido()).setValue(productos);

                                descripcion_compra= descripcion_compra + "\n" +"x"+cantidad_seleccionada+" $"+precio_venta+" "+nombre;

                            }

                            if (finalX1+1==MainActivity.lista_seleccion_venta_mayor_bodega.size()) {
                                guardar_historial(id_factura, context.getString(R.string.Venta_bodega), "", descripcion_compra, context);
                                MainActivity.lista_seleccion_venta_mayor_bodega.clear();
                            }



                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    public void agregar_a_factura_compra_existente(Context context, String id_factura){
        String fecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
        descripcion_compra=context.getString(R.string.Compra);
        for (int x = 0; x < MainActivity.lista_seleccion_compra.size(); x++) {

            String id_referencia_producto=MainActivity.lista_seleccion_compra.get(x).getId();

            Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Productos").orderByChild("id").equalTo(id_referencia_producto);
            referencia_id_producto.keepSynced(true);
            try {
                Thread.sleep(1 * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(referencia_id_producto!=null) {

                int finalX1 = x;

                referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Modelo_producto producto = ds.getValue(Modelo_producto.class);

                                String nombre=MainActivity.lista_seleccion_compra.get(finalX1).getNombre();
                                String referencia_producto=id_referencia_producto;
                                String vendedor_producto=MainActivity.Id_Usuario + "-" + id_referencia_producto;
                                String costo_compra=MainActivity.lista_seleccion_compra.get(finalX1).getP_compra();
                                String precio_venta=MainActivity.lista_seleccion_compra.get(finalX1).getP_detal();
                                String url=MainActivity.lista_seleccion_compra.get(finalX1).getUrl();
                                int cantidad_seleccionada = Integer.parseInt(MainActivity.lista_seleccion_compra.get(finalX1).getSeleccion());

                                //sumar INVENTARIO
                                int cantidad_inventario = Integer.parseInt(producto.getCantidad());
                                int Cantidad = 0;
                                Cantidad=cantidad_inventario+cantidad_seleccionada;

                                producto.setCantidad(Cantidad+"");
                                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                                myRefe.child("Productos").child(producto.getId()).setValue(producto);

                                //guardar datos de productos facturados
                                DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

                                Modelo_producto_facturacion_app_vendedor productos = new Modelo_producto_facturacion_app_vendedor();
                                productos.setId_pedido(id_factura);
                                productos.setId_producto_pedido(UUID.randomUUID().toString());

                                productos.setNombre(nombre);
//                                          productos.setCodigo_barras(MainActivity.lista_seleccion.get(finalX2).getCodigo());
                                productos.setId_referencia_vendedor(MainActivity.Id_Usuario);
                                productos.setCantidad(cantidad_seleccionada+"");
                                productos.setEstado(context.getString(R.string.Compra));
                                productos.setId_referencia_producto(referencia_producto);
                                productos.setNombre_vendedor(MainActivity.Usuario);
                                productos.setVendedor_producto(vendedor_producto);
                                productos.setFecha(fecha);
                                productos.setCosto(costo_compra);
                                productos.setVenta(precio_venta);
                                productos.setRecaudo(MainActivity.Usuario);
                                productos.setUrl(url);
                                referencia.child("Factura_productos").child(productos.getId_producto_pedido()).setValue(productos);

                                descripcion_compra= descripcion_compra + "\n" +"x"+cantidad_seleccionada+" $"+costo_compra+" "+nombre;

                            }
                            if (finalX1+1==MainActivity.lista_seleccion_compra.size()) {
                                guardar_historial(id_factura, context.getString(R.string.Compra), "", descripcion_compra, context);
                                MainActivity.lista_seleccion_compra.clear();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    public ArrayList  cargar_datos_empresa(Context context){
        ArrayList<Modelo_empresa>datos_empresa=new ArrayList<>();

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Empresa").orderByChild("id").equalTo("1").limitToFirst(1);
        dataQuery.keepSynced(true);
        try {
            Thread.sleep(1 * 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_empresa empresa = userSnapshot.getValue(Modelo_empresa.class);
                    datos_empresa.add(new Modelo_empresa(empresa.getId(),empresa.getNombre(),empresa.getDocumento(),empresa.getCorreo(),empresa.getDominio(),empresa.getTelefono1(),empresa.getTelefono2(),empresa.getDireccion(),empresa.getUrl(), empresa.getGarantia()));

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(getBaseContext(), getString(R.string.problemas_conexion), Toast.LENGTH_SHORT).show();
            }

        });
       return datos_empresa;
    }

    public void actualizar_datos_cliente_factura(String id_factura,String nombre, String telefono, String documento, String direccion){

        Query referencia_id_producto= FirebaseDatabase.getInstance().getReference().child("Factura_cliente").orderByChild("id").equalTo(id_factura);
        referencia_id_producto.keepSynced(true);
        try {
            Thread.sleep(1 * 200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(referencia_id_producto!=null) {
            referencia_id_producto.addListenerForSingleValueEvent(new  ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Modelo_factura_cliente datos = ds.getValue(Modelo_factura_cliente.class);


                            datos.setNombre(nombre);
                            datos.setTelefono(telefono);
                            datos.setDocumento(documento);
                            datos.setDireccion(direccion);

                            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                            myRefe.child("Factura_cliente").child(id_factura).setValue(datos);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

}
