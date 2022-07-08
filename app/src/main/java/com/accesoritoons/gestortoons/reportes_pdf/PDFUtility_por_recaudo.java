package com.accesoritoons.gestortoons.reportes_pdf;

//https://medium.com/@raveesh08/creating-pdf-table-using-itextpdf-in-android-eb7e00163ab0


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.accesoritoons.gestortoons.MainActivity;
import com.accesoritoons.gestortoons.Preferencias_app;
import com.accesoritoons.gestortoons.R;
import com.accesoritoons.gestortoons.modelos.Modelo_registro_por_recaudo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

//CATALOGO LLAMADO DEL MAIN ACTIVITY
final class PDFUtility_por_recaudo
{
    int total_inventario=0;
    private static final String TAG = PDFUtility_factura_bodega.class.getSimpleName();
    private static Font FONT_TITLE     = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font FONT_SUBTITLE      = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    private static Font Datos_empresa_fuente      = new Font(Font.FontFamily.TIMES_ROMAN,  10, Font.BOLD);

    private static Font FONT_CELL      = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
    private static Font FONT_COLUMN    = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);
    private static NumberFormat formatoImporte = NumberFormat.getIntegerInstance(new Locale("es","ES"));
    public interface OnDocumentClose
    {
        void onPDFDocumentClose(File file);
    }

    void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, ArrayList<Modelo_registro_por_recaudo> items, String total_costos, String total_ventas, String total_ganancias, @NonNull String filePath, boolean isPortrait) throws Exception
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


        cabezera(mContext,document,total_costos,total_ventas,total_ganancias);
        addEmptyLine(document, 1);

        document.add(createDataTable(mContext,items));

        Paragraph temp = new Paragraph("Total Por Recaudo: "+formatoImporte.format(total_inventario), FONT_TITLE);
        temp.setAlignment(Element.ALIGN_CENTER);
        document.add(temp);
//        document.add(createSignBox());

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
        document.addAuthor( "Eloy Castellanos");
        document.addCreator("Accesory Toons");
        document.addHeader("Accesory Toons","Reporte");
    }

    private static void cabezera(Context mContext, Document document,String total_costos, String total_ventas, String total) throws Exception
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

                cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(4f);
                cell.setUseAscender(true);

                Paragraph temp = new Paragraph(MainActivity.datos_empresa.get(0).getNombre(), Datos_empresa_fuente);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

                temp = new Paragraph(MainActivity.datos_empresa.get(0).getDocumento(), Datos_empresa_fuente);
                temp.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(temp);

//
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

                Paragraph temp = new Paragraph(mContext.getString(R.string.Por_Recaudo), FONT_TITLE);
                temp.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(temp);


                temp = new Paragraph(Activity_vista_pdf.vendedor ,FONT_TITLE);
                temp.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(temp);



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

                cell = new PdfPCell(logoTable);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);
            }

            document.add(table);

            addEmptyLine(document, 1);

            PdfPTable datos_superiores = new PdfPTable(2);
            datos_superiores.setWidthPercentage(100);
            datos_superiores.setWidths(new float[]{5F,5F});
            datos_superiores.getDefaultCell().setBorder(PdfPCell.NO_BORDER);


            cell = new PdfPCell(new Phrase(Preferencias_app.informacion_superior, FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            datos_superiores.addCell(cell);

            document.add(datos_superiores);

        }
    }

    public PdfPTable createDataTable(Context mContext, ArrayList<Modelo_registro_por_recaudo> dataTable) throws DocumentException
    {


        //ELEMENTOS A LAS TABLAS
        PdfPTable table1 = new PdfPTable(6);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{0.6f,0.4f,1.5F,0.4F,0.6F,0.8F});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);



         PdfPCell cell;
        {

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Fecha), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Ref), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Nombre), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);


            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Cant_), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Monto), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.SubTotal), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);
        }



        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
        BaseColor cell_color;

        int size = dataTable.size();

        String cliente_diferente=dataTable.get(0).getId_vendedor();

        int total_vendedor =0;

        for (int i = 0; i < size; i++)
        {

            cell_color = alternate ? lt_gray : BaseColor.WHITE;

            //total de cliente
            if(!cliente_diferente.equals(dataTable.get(i).getId_vendedor())){

                PdfPCell celda_cliente;
                {


                    celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setPadding(0f);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    table1.addCell(celda_cliente);

                    celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setPadding(0f);
                    table1.addCell(celda_cliente);

                    celda_cliente = new PdfPCell(new Phrase(dataTable.get(i-1).getNombre_vendedor(), FONT_COLUMN));
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    celda_cliente.setPadding(10f);
                    table1.addCell(celda_cliente);

                    celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    celda_cliente.setPadding(0f);
                    table1.addCell(celda_cliente);

                    celda_cliente = new PdfPCell(new Phrase("Total", FONT_COLUMN));
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    celda_cliente.setPadding(0f);
                    table1.addCell(celda_cliente);

                    celda_cliente = new PdfPCell(new Phrase(formatoImporte.format(total_vendedor)+"", FONT_COLUMN));
                    celda_cliente.setPadding(0f);
                    celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda_cliente.setBackgroundColor(BaseColor.YELLOW);
                    celda_cliente.setBorder(PdfPCell.NO_BORDER);
                    table1.addCell(celda_cliente);
                    total_vendedor=0;

                }
                cliente_diferente=dataTable.get(i).getId_vendedor();
            }

            cell = new PdfPCell(new Phrase(dataTable.get(i).getFecha(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(dataTable.get(i).getRef(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

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

            cell = new PdfPCell(new Phrase(formatoImporte.format(Integer.parseInt(dataTable.get(i).getValor_recaudo())), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(formatoImporte.format(Integer.parseInt(dataTable.get(i).getSubtotal())), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            total_vendedor= total_vendedor+Integer.parseInt(dataTable.get(i).getSubtotal());
            total_inventario=total_inventario+Integer.parseInt(dataTable.get(i).getSubtotal());
            alternate = !alternate;


        }

        PdfPCell celda_cliente;
        {
            int ultimo_elemento=dataTable.size()-1;


            celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            celda_cliente.setPadding(0f);
            table1.addCell(celda_cliente);

            celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            celda_cliente.setPadding(0f);
            table1.addCell(celda_cliente);

            celda_cliente = new PdfPCell(new Phrase(dataTable.get(ultimo_elemento).getNombre_vendedor(), FONT_COLUMN));
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            celda_cliente.setPadding(10f);
            table1.addCell(celda_cliente);

            celda_cliente = new PdfPCell(new Phrase("", FONT_COLUMN));
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setPadding(0f);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            table1.addCell(celda_cliente);

            celda_cliente = new PdfPCell(new Phrase("Total", FONT_COLUMN));
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setPadding(0f);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            table1.addCell(celda_cliente);

            celda_cliente = new PdfPCell(new Phrase(formatoImporte.format(total_vendedor)+"", FONT_COLUMN));
            celda_cliente.setPadding(0f);
            celda_cliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda_cliente.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda_cliente.setBackgroundColor(BaseColor.YELLOW);
            celda_cliente.setBorder(PdfPCell.NO_BORDER);
            table1.addCell(celda_cliente);

        }


        return table1;
    }


}