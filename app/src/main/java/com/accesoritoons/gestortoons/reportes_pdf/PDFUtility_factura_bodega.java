package com.accesoritoons.gestortoons.reportes_pdf;

//https://medium.com/@raveesh08/creating-pdf-table-using-itextpdf-in-android-eb7e00163ab0
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.Preferencias_app;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_factura_cliente;
import com.accesoritoons.gestortoons.modelos.Modelo_productos_para_facturar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public final class PDFUtility_factura_bodega
{
    private static final String TAG = PDFUtility_factura_bodega.class.getSimpleName();
    private static Font FONT_TITLE     = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private static Font FONT_SUBTITLE      = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    private static Font Datos_empresa_fuente      = new Font(Font.FontFamily.TIMES_ROMAN,  10, Font.BOLD);

    private static Font FONT_CELL      = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
    private static Font FONT_COLUMN    = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);
    private static Font FONT_GARANTIA      = new Font(Font.FontFamily.TIMES_ROMAN,  7, Font.NORMAL);
    private static NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
    public interface OnDocumentClose
    {
        void onPDFDocumentClose(File file);
    }

    public  void crearPdf(@NonNull Context mContext, OnDocumentClose mCallback, ArrayList<Modelo_productos_para_facturar> items, ArrayList<Modelo_factura_cliente>  datos_cliente, String total, @NonNull String filePath, boolean isPortrait) throws Exception
    {
        if(filePath.equals(""))
        {
            throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
        }

        File file = new File(filePath);

        if(file.exists())
        {
            file.delete();
            Thread.sleep(50);
        }


        Document document = new Document();
        document.setMargins(24f,24f,32f,32f);
        document.setPageSize(isPortrait? PageSize.A4:PageSize.A4.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        pdfWriter.setFullCompression();
        pdfWriter.setPageEvent(new PageNumeration());

        document.open();

        setMetaData(document);

        cabezera(mContext,document,datos_cliente, total);
        addEmptyLine(document, 1);

        document.add(createDataTable(items));

        addEmptyLine(document, 2);

        Paragraph temp = new Paragraph(MainActivity.datos_empresa.get(0).getGarantia(), FONT_GARANTIA);

        document.add(temp);
        //muestra logo final y datos de al empresa
      //  document.add(createSignBox(mContext));

        document.close();

        try
        {
            pdfWriter.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG,"Error While Closing pdfWriter : "+ex.toString());
        }

        if(mCallback!=null)
        {
            mCallback.onPDFDocumentClose(file);
        }
    }

    private static  void addEmptyLine(Document document, int number) throws DocumentException
    {
        for (int i = 0; i < number; i++)
        {
            document.add(new Paragraph(" "));
        }
    }

    private static void setMetaData(Document document)
    {
        document.addCreationDate();
        //document.add(new Meta("",""));
        document.addAuthor( "Accesory toons");
        document.addCreator("Eloy Castellanos");
        document.addHeader("3125866072","Ing");
    }

    private static void cabezera(Context mContext, Document document, ArrayList<Modelo_factura_cliente>datos_cliente, String total) throws Exception
    {

        if (MainActivity.datos_empresa.size()>0) {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 7, 2});
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cell;
            {
                /*LEFT TOP LOGO*/
//                Drawable d = ContextCompat.getDrawable(mContext, R.drawable.logo_toons);
//                Bitmap bmp = ((BitmapDrawable) d).getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//                Image logo = Image.getInstance(stream.toByteArray());
//                logo.setWidthPercentage(80);
//                logo.scaleToFit(105, 55);

                cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(4f);
                cell.setUseAscender(true);

                Paragraph temp = new Paragraph("Fecha: "+datos_cliente.get(0).getFecha().substring(0,10), Datos_empresa_fuente);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

                temp = new Paragraph("Ref: "+datos_cliente.get(0).getId().substring(0,5), Datos_empresa_fuente);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

                temp = new Paragraph(MainActivity.datos_empresa.get(0).getNombre(), FONT_SUBTITLE);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

                temp = new Paragraph(MainActivity.datos_empresa.get(0).getDocumento(), FONT_SUBTITLE);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

                temp = new Paragraph(MainActivity.datos_empresa.get(0).getTelefono1()+"  "+MainActivity.datos_empresa.get(0).getTelefono2(), FONT_SUBTITLE);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);



                table.addCell(cell);
            }

            {
                /*MIDDLE TEXT*/
                cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(8f);
                cell.setUseAscender(true);

                Paragraph temp = new Paragraph("Factura", FONT_TITLE);
                temp.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(temp);

//                temp = new Paragraph("Mayorista", FONT_SUBTITLE);
//                temp.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(temp);

                table.addCell(cell);
            }
            /* RIGHT TOP LOGO*/
            {
                PdfPTable logoTable = new PdfPTable(1);
                logoTable.setWidthPercentage(100);
                logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.logo_toons);
                Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
              //  Bitmap bmp = ((BitmapDrawable) HomeFragment.logo.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image logo = Image.getInstance(stream.toByteArray());
                logo.setWidthPercentage(80);
                logo.scaleToFit(155, 70);

                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                logoTable.addCell(logoCell);

                logoCell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getNombre(), FONT_CELL));
                logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                logoCell.setPadding(4f);
                logoTable.addCell(logoCell);

                logoCell = new PdfPCell(new Phrase("Vendedor: "+datos_cliente.get(0).getVendedor(), FONT_CELL));
                logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                logoCell.setPadding(4f);
                logoTable.addCell(logoCell);

                cell = new PdfPCell(logoTable);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);
            }

            //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
            //paragraph.add(table);
            //document.add(paragraph);

            document.add(table);
//            document.add(new Paragraph(Preferencias_app.informacion_superior));
            addEmptyLine(document, 1);
//            document.add(new Paragraph());

            PdfPTable datos_superiores = new PdfPTable(2);
            datos_superiores.setWidthPercentage(100);
            datos_superiores.setWidths(new float[]{5F,5F});
            datos_superiores.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            cell = new PdfPCell(new Phrase( mContext.getResources().getString(R.string.Cliente)+": " + datos_cliente.get(0).getNombre() + "\n" + "CC: " + datos_cliente.get(0).getDocumento() + "\n" + mContext.getResources().getString(R.string.Telefono)+": " + datos_cliente.get(0).getTelefono() + "\n" + mContext.getResources().getString(R.string.Direccion)+": " + datos_cliente.get(0).getDireccion(), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            datos_superiores.addCell(cell);

            cell = new PdfPCell(new Phrase(Preferencias_app.informacion_superior, FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            datos_superiores.addCell(cell);

            document.add(datos_superiores);

            PdfPTable total_tabla = new PdfPTable(2);
            total_tabla.setWidthPercentage(100);
            total_tabla.setWidths(new float[]{9F,1.8F});
            total_tabla.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            cell = new PdfPCell(new Phrase( mContext.getResources().getString(R.string.Total), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(PdfPCell.NO_BORDER);
            total_tabla.addCell(cell);

            cell = new PdfPCell(new Phrase(formatoImporte.format(Integer.parseInt(total))+"", FONT_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(PdfPCell.NO_BORDER);
            total_tabla.addCell(cell);

            document.add(total_tabla);


        }
    }

    private static PdfPTable createDataTable(ArrayList<Modelo_productos_para_facturar> dataTable) throws DocumentException
    {
        PdfPTable table1 = new PdfPTable(4);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{8f,1f,3f,4f});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            cell = new PdfPCell(new Phrase("Nombre", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Cant.", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Total", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);
        }

        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
        BaseColor cell_color;

        int size = dataTable.size();

        for (int i = 0; i < size; i++)
        {
            cell_color = alternate ? lt_gray : BaseColor.WHITE;


            cell = new PdfPCell(new Phrase(dataTable.get(i).getNombre(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(dataTable.get(i).getCantidad(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(formatoImporte.format(Integer.parseInt(dataTable.get(i).getCosto())), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(formatoImporte.format(Integer.parseInt(dataTable.get(i).getTotal())), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            alternate = !alternate;
        }

        return table1;
    }

    private static PdfPTable createSignBox(Context mContext) throws DocumentException
    {



        PdfPTable innerTable = new PdfPTable(2);

        //ELEMENTOS A LAS TABLAS
        PdfPTable table3 = new PdfPTable(1);
        table3.setWidthPercentage(100);
        table3.setWidths(new float[]{1f});
        table3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        //table1.setHeaderRows(1);
        table3.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table3.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);


         PdfPCell cell;
        {

            cell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getNombre(), FONT_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(cell);

            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.logo_toons);
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image logo = null;
            try {
                logo = Image.getInstance(stream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            logo.setWidthPercentage(80);
            logo.scaleToFit(155, 70);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(logoCell);


            cell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getTelefono1()+" / " +MainActivity.datos_empresa.get(0).getTelefono2(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getDocumento(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getDireccion(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase(MainActivity.datos_empresa.get(0).getDominio(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            cell.setBorder(PdfPCell.NO_BORDER);
            table3.addCell(cell);


        }


        PdfPCell signRow = new PdfPCell(table3);
        signRow.setHorizontalAlignment(Element.ALIGN_LEFT);
        signRow.setBorder(PdfPCell.NO_BORDER);
        signRow.setPadding(4f);

        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);
        outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        outerTable.addCell(signRow);

        return outerTable;
    }

    private static Image getImage(byte[] imageByte, boolean isTintingRequired) throws Exception
    {
        Paint paint=new Paint();
        if(isTintingRequired)
        {
            paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
        }
        Bitmap input  = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
        Bitmap output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input,0,0,paint);

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.PNG,100,stream);
        Image image=Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(80);
        return image;
    }

    private static Image getBarcodeImage(PdfWriter pdfWriter, String barcodeText)
    {
        Barcode128 barcode=new Barcode128();
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.setFont(null);
        barcode.setCode(barcodeText);
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setTextAlignment(Element.ALIGN_BASELINE);
        return barcode.createImageWithBarcode(pdfWriter.getDirectContent(),BaseColor.BLACK,null);
    }
}