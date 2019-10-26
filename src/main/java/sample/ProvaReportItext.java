package sample;




import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class ProvaReportItext {

    public static void main(String args[]){
        try {
            Rectangle pageSize = new Rectangle(PageSize.A4);
            Document document = new Document(pageSize,0,0,0,0);
            PdfWriter.getInstance(document, new FileOutputStream("./resources/examples/ProvaItext.pdf"));
            document.open();
            PdfPTable table = new PdfPTable(2);
            table.setLockedWidth(true);
            table.setTotalWidth(585);
            table.setSpacingAfter(5);
            table.setHeaderRows(1);
            //table.addCell("Header row");
            PdfPCell cell = new PdfPCell(new Phrase("Hello"));
            Phrase p1 = new Phrase();
            p1.add(new Chunk("Denominazopne: Cifarma"));
            p1.add(Chunk.NEWLINE);
            p1.add(new Chunk("Indirizzo: Cifarma"));
            p1.add(new Chunk("Piva: 01600880718"));
            table.addCell(p1);
            table.addCell(p1);
            table.addCell(p1);
            table.addCell(p1);
            document.add(Chunk.NEWLINE);
            document.add(table);
            //document.add(Chunk.NEWLINE);
            document.add(table);
            document.close();

        }catch(Exception ex){
           ex.printStackTrace();
        }
    }
}
