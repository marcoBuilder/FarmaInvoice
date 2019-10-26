package sample;

import com.klugesoftware.farmainvoice.utility.DateUtility;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRElementsVisitor;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProvaReport {

    public ProvaReport(){

    }

    public static void main(String[] args){
        String fileName = "./resources/examples/Fattura.jasper";
        String outFileName = "./resources/examples/testFattura.pdf";
        HashMap hm = new HashMap();

        hm.put("denFornitore","PIPPO");
	    hm.put("pivaFornitore","01600880718");
        hm.put("indFornitore","Via Roma 12345");
        hm.put("cittaFornitore","Milano");
        hm.put("denCliente","Cifarma");
        hm.put("pivaCliente","01600880718");
        hm.put("indCliente","Via Lucca 12345");
        hm.put("cittaCliente","Bari");
        hm.put("codFiscFornitore","SCGMRC73B12L219L");
        hm.put("codFiscCliente","SCGMRC73B12L219L");
        hm.put("provFornitore","FG");
        hm.put("provCliente","BA");
        hm.put("capFornitore","71017");
        hm.put("capCliente","71017");
        hm.put("numDocumento","12345678");
        hm.put("dataDocumento","12/12/2018");
        hm.put("causaleFattura","pipo  pippo");

        hm.put("modalitaPagamento1","pipo  pippo");
        hm.put("ibanPagamento1","pipo  pippo");
        hm.put("dataScadenza1","pipo  pippo");
        hm.put("importoPagamento1","pipo  pippo");
        hm.put("modalitaPagamento2","pipo  pippo");
        hm.put("ibanPagamento2","pipo  pippo");
        hm.put("dataScadenza2","pipo  pippo");
        hm.put("importoPagamento2","pipo  pippo");
        hm.put("modalitaPagamento3","pipo  pippo");
        hm.put("ibanPagamento3","pipo  pippo");
        hm.put("dataScadenza3","pipo  pippo");
        hm.put("importoPagamento3","pipo  pippo");

        hm.put("iva4Descr","pipo  pippo");
        hm.put("iva4Perc","pipo  pippo");
        hm.put("iva4Imposta","pipo  pippo");
        hm.put("iva4Imponibile","pipo  pippo");
        hm.put("iva10Descr","pipo  pippo");
        hm.put("iva10Perc","pipo  pippo");
        hm.put("iva10Imposta","pipo  pippo");
        hm.put("iva10Imponibile","pipo  pippo");
        hm.put("iva22Descr","pipo  pippo");
        hm.put("iva22Perc","pipo  pippo");
        hm.put("iva22Imposta","pipo  pippo");
        hm.put("iva22Imponibile","pipo  pippo");

        hm.put("valoreScMagg","pipo  pippo");
        hm.put("totaleFattura","pipo  pippo");


        ArrayList<Object> elenco = new ArrayList<Object>();
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));
        elenco.add("123456789");
        elenco.add("moment 12 mg");
        elenco.add("12");
        elenco.add(new BigDecimal(12));
        elenco.add("04%");
        elenco.add(new BigDecimal(2345));
        elenco.add(new BigDecimal(0));

        Iterator<Object> iter = elenco.iterator();
        try
        {

            JasperPrint print = JasperFillManager.fillReport(
                    fileName,
                    hm,
                    new JRDataSource() {
                        @Override
                        public boolean next() throws JRException {
                            return iter.hasNext();
                        }

                        @Override
                        public Object getFieldValue(JRField jrField) throws JRException {
                            return iter.next();
                        }
                    });



            JRExporter exporter =
                    new net.sf.jasperreports.engine.export.JRPdfExporter();
            exporter.setParameter(
                    JRExporterParameter.OUTPUT_FILE_NAME,
                    outFileName);
            exporter.setParameter(
                    JRExporterParameter.JASPER_PRINT,print);
            exporter.exportReport();
            System.out.println("Created file: " + outFileName);
            Desktop.getDesktop().open(new File("./resources/examples/testFattura.pdf"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
