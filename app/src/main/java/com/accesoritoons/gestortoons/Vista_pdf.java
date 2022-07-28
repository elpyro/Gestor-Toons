package com.accesoritoons.gestortoons;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class Vista_pdf extends AppCompatActivity {


    String bandera="";
    private PDFView pdfView;
    private File file;
    private String telefono;
    private MenuItem opcion_compartir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_pdf);
        pdfView=(PDFView)findViewById(R.id.vista_pdf);


        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            file=new File(bundle.getString("path",""));
             }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
       //Toast.makeText(this, getString(R.string.PDF_guardado_con_exito)+" "+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Factura_Toons.pdf", Toast.LENGTH_LONG).show();
    }


        public void compartir_pdf(){
            //COMPARTIR EL PDF


            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Factura_Toons.pdf");

            Uri uri = FileProvider.getUriForFile(this, "com.toons.fileprovider", file);
        Intent intent2 = ShareCompat.IntentBuilder.from(this)
                .setType("application/pdf")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            this.startActivity(intent2);
        }catch (Exception e){
            Toast.makeText(this, getString(R.string.No_puede_compartir_pdf)+e, Toast.LENGTH_SHORT).show();
        }
        }


    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // CREA EL MENU CON TRES PUNTICOS
        getMenuInflater().inflate(R.menu.main, menu);
        opcion_compartir=menu.findItem(R.id.compartir);
        opcion_compartir.setVisible(true);

        return true;
    }
    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compartir:
                compartir_pdf();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}