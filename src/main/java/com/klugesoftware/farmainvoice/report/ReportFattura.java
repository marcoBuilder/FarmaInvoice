package com.klugesoftware.farmainvoice.report;

import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportFattura {

    private String pathFolderFilePdf;
    private String pathFatturaXml;
    private org.w3c.dom.Document docXml;
    private boolean isDDT = false;
    private boolean isOrdineAcq = false;
    private boolean isDatiFattureCollegate = false;
    private boolean showTableDDT = false;
    private boolean showTableOrdAcquisto = false;
    private boolean showTableDatiFattureCollegate = false;
    private boolean showTableCausali = false;
    private boolean showTablePagamenti = false;
    private boolean collegaDdtToDetail = false;
    private boolean collegaOrdAcquisToDetail = false;
    private boolean collegadatiFattureCollegatoToDetail = false;
    private String nomeFilePdf;
    private TipoFattura tipoFattura;
    private ArrayList<DDT> listaDDT;

    public ReportFattura(){}

    /*
    public ReportFattura(String pathFileXml, String pathFilePdf,String nomeFilePdf, TipoFattura tipoFattura){
        try {
            this.pathFatturaXml = pathFileXml;
            this.pathFilePdf = pathFilePdf;
            this.nomeFilePdf  = nomeFilePdf;
            this.tipoFattura = tipoFattura;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docXml = docBuilder.parse(new File(this.pathFatturaXml));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    */

    public boolean makeReport(String pathFileXml, String pathFolderFilePdf, TipoFattura tipoFattura){
        boolean ret = false;
        try{
            this.pathFatturaXml = pathFileXml;
            File fileXml = new File(this.pathFatturaXml);
            String tempNamePdf = "";
            if(fileXml.getName().endsWith(".xml"))
                tempNamePdf = fileXml.getName().replace(".xml",".pdf");
            else{
                if(fileXml.getName().endsWith(".XML"))
                    tempNamePdf = fileXml.getName().replace(".XML",".pdf");
            }
            this.pathFolderFilePdf = pathFolderFilePdf;
            this.tipoFattura = tipoFattura;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docXml = docBuilder.parse(fileXml);

            Rectangle pageSize = new Rectangle(PageSize.A4);
            Rectangle pageNumber = new Rectangle(50,50,545,792);
            Document document = new Document(pageSize,50,50,50,50);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.pathFolderFilePdf+tempNamePdf));
            writer.setBoxSize("pageNumber",pageNumber);
            writer.setViewerPreferences(PdfWriter.PageModeFullScreen);
            writer.setPageEvent(new HeaderFooter());
            document.open();
            document.add(getTableIntestazione());
            document.add(getTableHeader());
            document.add(getTableDetail());
            PdfPTable table = getCausaleTable();
            if (showTableCausali)
                document.add(table);
            table = getTableRitenuta();
            if(table != null)
                document.add(table);
            table = getTableCassaPrevidenziale();
            if(table != null)
                document.add(table);
            if (showTableDatiFattureCollegate)
                document.add(getTableDatiFattureCollegate());
            if(showTableOrdAcquisto)
                document.add(getTableOrdineAcquisto());
            //if (showTableDDT)
            table = getTableDDT();
            if ( table != null)
                document.add(getTableDDT());
            document.add(getTableRiepilogo());
            table = getTablePagamenti();
            if(showTablePagamenti)
                document.add(table);
            document.close();
           File pdfOld = new File(this.pathFolderFilePdf+tempNamePdf);
           FileUtils.copyFile(pdfOld,new File(this.pathFolderFilePdf+nomeFilePdf));
           FileUtils.forceDelete(pdfOld);
           //Desktop.getDesktop().open(new File(this.pathFolderFilePdf+nomeFilePdf));

            ret = true;

        }catch(Exception ex){
            ex.printStackTrace();
            ret = false;
        }finally {
            return ret;
        }
    }

    public void openFilePdf(){
        try {
            Desktop.getDesktop().open(new File(this.pathFolderFilePdf + nomeFilePdf));
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private PdfPTable getTableIntestazione(){

        PdfPTable table = getTable(2);

        try {

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();
            //Dati Fornitore
            String denominazioneFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione", docXml);
            String pIvaFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice", docXml);
            String indirizzoFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo", docXml);
            String capFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/CAP", docXml);
            String provFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Provincia", docXml);
            String comuneFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Comune", docXml);

            if (tipoFattura.equals(TipoFattura.PASSIVA)) {
                //String nomeFilePdf
                if (denominazioneFornitore != null && denominazioneFornitore.length() > 0) {
                    String[] temp = denominazioneFornitore.split(" ");
                    if (temp.length > 1)
                        nomeFilePdf = temp[0] + "_" + temp[1];
                    else
                        nomeFilePdf = temp[0];
                } else {
                    String nomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Nome", docXml);
                    String cognomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome", docXml);
                    if (nomeTag != null && nomeTag.length() > 0) {
                        denominazioneFornitore = nomeTag;
                        if (cognomeTag != null && cognomeTag.length() > 0) {
                            denominazioneFornitore += "_" + cognomeTag;
                            nomeFilePdf = denominazioneFornitore;
                        }
                    } else {
                        if (cognomeTag != null && cognomeTag.length() > 0)
                            denominazioneFornitore = cognomeTag;
                        nomeFilePdf = denominazioneFornitore;
                    }
                }
            }



            //Dati Cliente
            String denominazioneCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Denominazione", docXml);
            String pIvaCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice", docXml);
            String indirizzoCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Indirizzo", docXml);
            String capCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP", docXml);
            String provCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia", docXml);
            String comuneCliente = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune", docXml);


            if (denominazioneCliente != null && denominazioneCliente.length() > 0) {
                ;
            } else{
                String nomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Nome", docXml);
                String cognomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Cognome", docXml);
                if(nomeTag != null && nomeTag.length() > 0) {
                    denominazioneCliente = nomeTag;
                    if(cognomeTag != null && cognomeTag.length() > 0)
                        denominazioneCliente+="_"+cognomeTag;
                }else{
                    if(cognomeTag != null && cognomeTag.length() > 0)
                        denominazioneCliente = cognomeTag;
                }
            }

            if(tipoFattura.equals(TipoFattura.ATTIVA)) {
                String temp1 = denominazioneCliente;
                temp1 = temp1.replace("/", "_");
                temp1 = temp1.replace("\\", "_");
                denominazioneCliente = temp1;
                //String nomeFilePdf
                if (denominazioneCliente != null && denominazioneCliente.length() > 0) {
                    String[] temp = denominazioneCliente.split(" ");
                    if (temp.length > 1)
                        nomeFilePdf = temp[0] + "_" + temp[1];
                    else
                        nomeFilePdf = temp[0];
                } else {
                    String nomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Nome", docXml);
                    String cognomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Cognome", docXml);
                    if (nomeTag != null && nomeTag.length() > 0) {
                        denominazioneCliente = nomeTag;
                        if (cognomeTag != null && cognomeTag.length() > 0) {
                            denominazioneCliente += "_" + cognomeTag;
                            nomeFilePdf = denominazioneCliente;
                        }
                    } else {
                        if (cognomeTag != null && cognomeTag.length() > 0)
                            denominazioneCliente = cognomeTag;
                        nomeFilePdf = denominazioneCliente;
                    }
                }
            }

            Paragraph intestazioneFornitore = new Paragraph();
            Font font = new Font(Font.HELVETICA,8);
            Font fontItalic = new Font(Font.HELVETICA,10);
            Chunk labelFornitore = new Chunk("Mittente", new Font(Font.HELVETICA,10,Font.BOLD));
            intestazioneFornitore.add(labelFornitore);
            intestazioneFornitore.add(Chunk.NEWLINE);
            intestazioneFornitore.add(new Chunk("Denominazione: ",font));
            intestazioneFornitore.add(new Chunk(denominazioneFornitore,fontItalic));
            intestazioneFornitore.add(Chunk.NEWLINE);
            intestazioneFornitore.add(new Chunk("Partita Iva: ",font));
            intestazioneFornitore.add(new Chunk(pIvaFornitore,fontItalic));
            intestazioneFornitore.add(Chunk.NEWLINE);
            intestazioneFornitore.add(new Chunk("Indirizzo: ",font));
            intestazioneFornitore.add(new Chunk(indirizzoFornitore,fontItalic));
            intestazioneFornitore.add(Chunk.NEWLINE);
            intestazioneFornitore.add(new Chunk("Città: ",font));
            intestazioneFornitore.add(new Chunk(comuneFornitore,fontItalic));
            intestazioneFornitore.add(Chunk.NEWLINE);
            intestazioneFornitore.add(new Chunk("Prov.: ",font));
            intestazioneFornitore.add(new Chunk(provFornitore,fontItalic));
            intestazioneFornitore.add(new Chunk(" Cap: ",font));
            intestazioneFornitore.add(new Chunk(capFornitore,fontItalic));

            table.addCell(intestazioneFornitore);

            Paragraph intestazioneCliente = new Paragraph();
            Chunk labelCliente = new Chunk("Destinatario", new Font(Font.HELVETICA,10,Font.BOLD));
            intestazioneCliente.add(labelCliente);
            intestazioneCliente.add(Chunk.NEWLINE);
            intestazioneCliente.add(new Chunk("Denominazione: " , font));
            intestazioneCliente.add(new Chunk(denominazioneCliente, fontItalic));
            intestazioneCliente.add(Chunk.NEWLINE);
            intestazioneCliente.add(new Chunk("Partita Iva: ", font));
            intestazioneCliente.add(new Chunk(pIvaCliente, fontItalic));
            intestazioneCliente.add(Chunk.NEWLINE);
            intestazioneCliente.add(new Chunk("Indirizzo: ",font));
            intestazioneCliente.add(new Chunk(indirizzoCliente,fontItalic));
            intestazioneCliente.add(Chunk.NEWLINE);
            intestazioneCliente.add(new Chunk("Città: ",font));
            intestazioneCliente.add(new Chunk(comuneCliente,fontItalic));
            intestazioneCliente.add(Chunk.NEWLINE);
            intestazioneCliente.add(new Chunk("Prov.: ", font));
            intestazioneCliente.add(new Chunk(provCliente, fontItalic));
            intestazioneCliente.add(new Chunk("  Cap: ",font));
            intestazioneCliente.add(new Chunk(capCliente,fontItalic));

            table.addCell(intestazioneCliente);


        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            return table;
        }
    }

    private PdfPTable getTableHeader(){

        PdfPTable table = getTable(4);
        try {

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();

            String tipoDocumento = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/TipoDocumento", docXml);
            String dataFattura = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data", docXml);
            String numeroFattura = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero", docXml);
            String importoTotale = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento", docXml);
            //String causale = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Causale", docXml);

            //Aggiorno string nome filePdf
            String temp = numeroFattura;
            temp = temp.replace("/","_");
            temp = temp.replace("\\","_");
            if(tipoDocumento != null) {
                if (tipoDocumento.equals("TD04")) {
                    nomeFilePdf = "NotaCredito" + "_" + nomeFilePdf + "_" + temp+".pdf";
                } else {
                    nomeFilePdf = nomeFilePdf + "_" + temp+".pdf";
                }
            }

            table.addCell(getHeaderCell("TipoDocumento"));
            table.addCell(getHeaderCell("Numero "));
            table.addCell(getHeaderCell("Data "));
            //table.addCell(getHeaderCell("Causale"));
            table.addCell(getHeaderCell("Importo Totale"));

            table.addCell(getTextHeaderCell(getTipoDocumento(tipoDocumento)));
            table.addCell(getTextHeaderCell(numeroFattura));
            table.addCell(getTextHeaderCell(DateUtility.converteSqlStringToGUIString(dataFattura)));
            //table.addCell(getTextHeaderCell(causale));
            PdfPCell cellTemp = getNumberCell((importoTotale));
            cellTemp.setHorizontalAlignment(Cell.ALIGN_CENTER);
            table.addCell((cellTemp));
            table.setSpacingAfter(20);
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            return table;
        }
    }

    private PdfPTable getTableDetail() throws Exception{
        boolean addDDT = false;
        listaDDT = getDDT();
        if(isDDT && collegaDdtToDetail){
            addDDT = true;
        }

        boolean addOrdAcq = false;
        ArrayList<OrdineAcquisto> listaOrdAcq = getOrdiniAcquisto();
        if(isOrdineAcq && collegaOrdAcquisToDetail){
            addOrdAcq = true;
        }

        boolean addDatiFattColl = false;
        ArrayList<DatiFattureCollegate> listaDatiFattCollegate = getDatiFattureCollegate();
        if(isDatiFattureCollegate && collegadatiFattureCollegatoToDetail){
            addDatiFattColl = true;
        }

        PdfPTable table = getTable(9);
        table.setWidths(new float[]{0.8f,1.5f,5f,0.8f,1.2f,0.5f,0.8f,0.6f,1});
        table.setHeaderRows(1);
        table.getDefaultCell().setBackgroundColor(Color.lightGray);
        table.addCell(getHeaderCell(""));
        table.addCell(getHeaderCell("Codice"));
        table.addCell(getHeaderCell("Descrizione"));
        table.addCell(getHeaderCell("Q.tà"));
        table.addCell(getHeaderCell("Pr.Unitario"));
        table.addCell(getHeaderCell("UM"));
        table.addCell(getHeaderCell("Sconto"));
        table.addCell(getHeaderCell("%Iva"));
        table.addCell(getHeaderCell("Pr.Totale"));
        table.getDefaultCell().setBackgroundColor(null);
        NodeList lineeFattura = docXml.getElementsByTagName("DettaglioLinee");
        for(int i=0;i<lineeFattura.getLength();i++) {
            org.w3c.dom.Element el = (org.w3c.dom.Element) lineeFattura.item(i);

            String numLinea = "";
            if (el.getElementsByTagName("NumeroLinea").getLength() > 0)
                numLinea = el.getElementsByTagName("NumeroLinea").item(0).getTextContent();

            String descrizione = "";
            if(el.getElementsByTagName("Descrizione").getLength() > 0)
                descrizione = el.getElementsByTagName("Descrizione").item(0).getTextContent();
            String quantita = "";
            if(el.getElementsByTagName("Quantita").getLength() > 0)
                quantita = el.getElementsByTagName("Quantita").item(0).getTextContent();
            String prezzoUnitario = "";
            if(el.getElementsByTagName("PrezzoUnitario").getLength() > 0)
                prezzoUnitario = el.getElementsByTagName("PrezzoUnitario").item(0).getTextContent();
            String um = "";
            if(el.getElementsByTagName("UnitaMisura").getLength()>0)
                um=el.getElementsByTagName("UnitaMisura").item(0).getTextContent();
            String iva = "";
            if(el.getElementsByTagName("AliquotaIVA").getLength() > 0)
                iva = el.getElementsByTagName("AliquotaIVA").item(0).getTextContent();
            String prezzoTotale = "";
            if(el.getElementsByTagName("PrezzoTotale").getLength() > 0)
                prezzoTotale = el.getElementsByTagName("PrezzoTotale").item(0).getTextContent();

            //Tag <CodiceArticolo>
            ArrayList<CodiceArticolo> codiceArticoloList = new ArrayList<>();
            NodeList codArticoloTag = el.getElementsByTagName("CodiceArticolo");
            if(codArticoloTag.getLength()>0){
                for(int y=0;y<codArticoloTag.getLength();y++){
                    org.w3c.dom.Element elArt = (org.w3c.dom.Element) codArticoloTag.item(y);
                    CodiceArticolo codiceArticolo = new CodiceArticolo();
                    if(elArt.getElementsByTagName("CodiceTipo").getLength()>0)
                        codiceArticolo.setCodiceTipo(elArt.getElementsByTagName("CodiceTipo").item(0).getTextContent());
                    if(elArt.getElementsByTagName("CodiceValore").getLength()>0)
                        codiceArticolo.setCodiceValore(elArt.getElementsByTagName("CodiceValore").item(0).getTextContent());
                    codiceArticoloList.add(codiceArticolo);
                }
            }

            //TAG <ScontoMaggiorazione>
            ArrayList<String> scMaggDescr = new ArrayList<String>();
            NodeList scontiMagg = el.getElementsByTagName("ScontoMaggiorazione");
            if(scontiMagg.getLength()>0){
             for (int k=0;k<scontiMagg.getLength();k++){
                 org.w3c.dom.Element elSc = (org.w3c.dom.Element) scontiMagg.item(k);
                 if (elSc.getElementsByTagName("Importo").getLength()>0)
                    scMaggDescr.add(elSc.getElementsByTagName("Importo").item(0).getTextContent());
                 else
                     if(elSc.getElementsByTagName("Percentuale").getLength()>0)
                         scMaggDescr.add(elSc.getElementsByTagName("Percentuale").item(0).getTextContent());
             }
            }

            //Tag <AltriDatiGestionali>
            ArrayList<String> datiGestionale = new ArrayList<String>();
            String datiGestTemp = "";
            NodeList datiGestionaliTag = el.getElementsByTagName("AltriDatiGestionali");
            if(datiGestionaliTag.getLength()>0){
                for (int z=0;z<datiGestionaliTag.getLength();z++){
                    org.w3c.dom.Element elDatiGest = (org.w3c.dom.Element) datiGestionaliTag.item(z);
                    if (elDatiGest.getElementsByTagName("TipoDato").getLength()>0){
                        datiGestTemp = elDatiGest.getElementsByTagName("TipoDato").item(0).getTextContent();
                        if (elDatiGest.getElementsByTagName("RiferimentoTesto").getLength()>0)
                            datiGestTemp += " - " + elDatiGest.getElementsByTagName("RiferimentoTesto").item(0).getTextContent();
                        if (elDatiGest.getElementsByTagName("RiferimentoNumero").getLength()>0)
                            datiGestTemp += " - " + elDatiGest.getElementsByTagName("RiferimentoNumero").item(0).getTextContent();
                        if (elDatiGest.getElementsByTagName("RiferimentoData").getLength()>0)
                            datiGestTemp += " - " + elDatiGest.getElementsByTagName("RiferimentoData").item(0).getTextContent();
                    }
                    datiGestionale.add(datiGestTemp);
                }
            }


            String descrDDT = "";
            if(addDDT){
                Iterator<DDT> iterator = listaDDT.iterator();
                while (iterator.hasNext()){
                    DDT ddt = iterator.next();
                    if (ddt.contains(numLinea)) {
                        descrDDT += "\n" + ddt.toString();
                        ddt.addImporto(prezzoTotale);
                    }
                }
                //descrizione += descrDDT;
            }

            String descrOrdAcq = "";
            if(addOrdAcq){
                Iterator<OrdineAcquisto> iterOrd = listaOrdAcq.iterator();
                while (iterOrd.hasNext()){
                    OrdineAcquisto ord = iterOrd.next();
                    if (ord.contains(numLinea))
                        descrOrdAcq += "\n"+ord.toString();
                }
                //descrizione+= descrOrdAcq;
            }

            String descrDatiFattColl = "";
            if(addDatiFattColl){
                Iterator<DatiFattureCollegate> iterFattColl = listaDatiFattCollegate.iterator();
                while (iterFattColl.hasNext()){
                    DatiFattureCollegate datiFattColl = iterFattColl.next();
                    if (datiFattColl.contains(numLinea))
                        descrDatiFattColl += "\n"+datiFattColl.toString();
                }
                //descrizione+= descrDatiFattColl;
            }

            table.addCell(getTextCell(numLinea));
            table.addCell(getTextCodiceCell(codiceArticoloList));
            table.addCell(getTextDescrizioneCell(descrizione,descrOrdAcq,descrDDT,descrDatiFattColl,datiGestionale));
            table.addCell(getNumberDetailCell(quantita,""));
            table.addCell(getNumberDetailCell(prezzoUnitario,""));
            table.addCell(getTextUMCell(um));
            table.addCell(getScontoDetailCell(scMaggDescr));
            table.addCell(getNumberDetailCell(iva,"iva"));
            table.addCell(getNumberDetailCell(prezzoTotale,""));
        }
        table.setSpacingAfter(30);
        //table.setTableEvent(new AlternatingBackground());
        return table;
    }

    private PdfPTable getCausaleTable(){
        PdfPTable table = getTable(1);

        try {
            table.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(getHeaderCell("Causale"));
            table.getDefaultCell().setBackgroundColor(null);
            NodeList causali = docXml.getElementsByTagName("Causale");
            if (causali.getLength() == 0) {
                showTableCausali = false;
            }else {
                showTableCausali = true;
                PdfPCell cell;
                for (int i = 0; i < causali.getLength(); i++) {
                    Node c1 = causali.item(i);
                    org.w3c.dom.Element el = (org.w3c.dom.Element) causali.item(i);
                    String causale = "";
                    causale = el.getFirstChild().getTextContent();
                    cell = new PdfPCell(new Phrase(causale));
                    cell.setBorder(Cell.NO_BORDER);
                    table.addCell(getTextCell(causale));
                }
            }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        return table;
    }

    private PdfPTable getTableOrdineAcquisto(){
        PdfPTable table = getTable(2);
        table.setHeaderRows(1);
        table.getDefaultCell().setBackgroundColor(Color.lightGray);
        PdfPCell cell = getHeaderCell("Dati Ordine D'Acquisto");
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(2);
        table.addCell(cell);
        table.addCell(getHeaderCell("Documento"));
        table.addCell(getHeaderCell("Data"));
        table.getDefaultCell().setBackgroundColor(null);
        NodeList datiOrdineAcquisto = docXml.getElementsByTagName("DatiOrdineAcquisto");
        if(datiOrdineAcquisto.getLength() > 0) {
            for (int i = 0; i < datiOrdineAcquisto.getLength(); i++) {
                Node lin1 = datiOrdineAcquisto.item(i);
                org.w3c.dom.Element el = (org.w3c.dom.Element) datiOrdineAcquisto.item(i);

                String documento = "";
                if (el.getElementsByTagName("IdDocumento").getLength() > 0)
                    documento = el.getElementsByTagName("IdDocumento").item(0).getTextContent();
                String data = "";
                if (el.getElementsByTagName("Data").getLength() > 0)
                    data = el.getElementsByTagName("Data").item(0).getTextContent();

                if (documento.equals(""))
                    table.addCell(getTextCell(""));
                else
                    table.addCell(getTextCell(documento));

                if (data.equals(""))
                    table.addCell(getTextCell(""));
                else
                    table.addCell(getTextCell(DateUtility.converteSqlStringToGUIString(data)));
            }
        }
        return table;
    }

    private PdfPTable getTableDatiFattureCollegate(){
        PdfPTable table = getTable(2);
        table.setHeaderRows(1);
        table.getDefaultCell().setBackgroundColor(Color.lightGray);
        PdfPCell cell = getHeaderCell("Dati Fatture Collegate");
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(2);
        table.addCell(cell);
        table.addCell(getHeaderCell("Documento"));
        table.addCell(getHeaderCell("Data"));
        table.getDefaultCell().setBackgroundColor(null);
        NodeList datiFattureCollegate = docXml.getElementsByTagName("DatiFattureCollegate");
        if(datiFattureCollegate.getLength() > 0) {
            for (int i = 0; i < datiFattureCollegate.getLength(); i++) {
                Node lin1 = datiFattureCollegate.item(i);
                org.w3c.dom.Element el = (org.w3c.dom.Element) datiFattureCollegate.item(i);

                String documento = "";
                if (el.getElementsByTagName("IdDocumento").getLength() > 0)
                    documento = el.getElementsByTagName("IdDocumento").item(0).getTextContent();
                String data = "";
                if (el.getElementsByTagName("Data").getLength() > 0)
                    data = el.getElementsByTagName("Data").item(0).getTextContent();

                if (documento.equals(""))
                    table.addCell(getTextCell(""));
                else
                    table.addCell(getTextCell(documento));
                if (data.equals(""))
                    table.addCell(getTextCell(""));
                else
                    table.addCell(getTextCell(DateUtility.converteSqlStringToGUIString(data)));
            }
        }
        return table;
    }

    private ArrayList<OrdineAcquisto> getOrdiniAcquisto(){
        NodeList datiOrdiniAcq = docXml.getElementsByTagName("DatiOrdineAcquisto");
        if(datiOrdiniAcq.getLength() == 0){
            showTableOrdAcquisto = false;
            isOrdineAcq = false;
            return null;
        }
        isOrdineAcq = true;
        ArrayList<OrdineAcquisto> arrayListOrdiniAcq = new ArrayList<OrdineAcquisto>();
        //questo flag viene usato per indicare se c'è almeno un riferimento ad una linea di fattura.
        boolean isRiferimentoLinea = false;

        for(int i=0;i<datiOrdiniAcq.getLength();i++){
            org.w3c.dom.Element el = (org.w3c.dom.Element) datiOrdiniAcq.item(i);
            OrdineAcquisto ordAcq = new OrdineAcquisto();
            ordAcq.setIdDocumento(el.getElementsByTagName("IdDocumento").item(0).getTextContent());
            if(el.getElementsByTagName("Data").getLength() > 0)
                ordAcq.setData(DateUtility.converteSqlStringToGUIString(el.getElementsByTagName("Data").item(0).getTextContent()));
            NodeList riferimentoLinee = el.getElementsByTagName("RiferimentoNumeroLinea");
            if(riferimentoLinee.getLength() == 0){
                showTableOrdAcquisto = true;
                arrayListOrdiniAcq.add(ordAcq);
            }else{
                isRiferimentoLinea = true;
                for(int k=0;k<riferimentoLinee.getLength();k++){
                    org.w3c.dom.Element el2 = (org.w3c.dom.Element) riferimentoLinee.item(k);
                    ordAcq.addRiferimentoLinea(el2.getFirstChild().getTextContent());
                }
                arrayListOrdiniAcq.add(ordAcq);
            }

        }

        if(isRiferimentoLinea)
            collegaOrdAcquisToDetail = true;
        return arrayListOrdiniAcq;
    }

    private ArrayList<DatiFattureCollegate> getDatiFattureCollegate(){
        NodeList datiFattureCollegate = docXml.getElementsByTagName("DatiFattureCollegate");
        if(datiFattureCollegate.getLength() == 0){
            showTableDatiFattureCollegate = false;
            isDatiFattureCollegate = false;
            return null;
        }
        isDatiFattureCollegate = true;
        ArrayList<DatiFattureCollegate> arrayListDatiFattCollegate = new ArrayList<DatiFattureCollegate>();
        //questo flag viene usato per indicare se c'è almeno un riferimento ad una linea di fattura.
        boolean isRiferimentoLinea = false;

        for(int i=0;i<datiFattureCollegate.getLength();i++){
            org.w3c.dom.Element el = (org.w3c.dom.Element) datiFattureCollegate.item(i);
            DatiFattureCollegate datiFattColl = new DatiFattureCollegate();
            datiFattColl.setIdDocumento(el.getElementsByTagName("IdDocumento").item(0).getTextContent());
            if(el.getElementsByTagName("Data").getLength() > 0)
                datiFattColl.setData(DateUtility.converteSqlStringToGUIString(el.getElementsByTagName("Data").item(0).getTextContent()));
            NodeList riferimentoLinee = el.getElementsByTagName("RiferimentoNumeroLinea");
            if(riferimentoLinee.getLength() == 0){
                showTableDatiFattureCollegate = true;
                arrayListDatiFattCollegate.add(datiFattColl);
            }else{
                isRiferimentoLinea = true;
                for(int k=0;k<riferimentoLinee.getLength();k++){
                    org.w3c.dom.Element el2 = (org.w3c.dom.Element) riferimentoLinee.item(k);
                    datiFattColl.addRiferimentoLinea(el2.getFirstChild().getTextContent());
                }
                arrayListDatiFattCollegate.add(datiFattColl);
            }

        }

        if(isRiferimentoLinea)
            collegadatiFattureCollegatoToDetail = true;
        return arrayListDatiFattCollegate;
    }


    private ArrayList<DDT> getDDT(){

        NodeList datiDDT = docXml.getElementsByTagName("DatiDDT");
        if(datiDDT.getLength() == 0){
            showTableDDT = false;
            isDDT = false;
            return null;
        }
        isDDT = true;
        ArrayList<DDT> arrayListDDT = new ArrayList<DDT>();
        //questo flag viene usato per indicare se c'è almeno un riferimento ad una linea di fattura.
        boolean isRiferimentoLinea = false;

        for(int i=0;i<datiDDT.getLength();i++){
            org.w3c.dom.Element el = (org.w3c.dom.Element) datiDDT.item(i);
            DDT ddt = new DDT();
            ddt.setNumeroDDT(el.getElementsByTagName("NumeroDDT").item(0).getTextContent());
            ddt.setDataDDT(DateUtility.converteSqlStringToGUIString(el.getElementsByTagName("DataDDT").item(0).getTextContent()));
            NodeList riferimentoLinee = el.getElementsByTagName("RiferimentoNumeroLinea");
            if(riferimentoLinee.getLength() == 0){
                showTableDDT = true;
                arrayListDDT.add(ddt);
            }else{
                isRiferimentoLinea = true;
                for(int k=0;k<riferimentoLinee.getLength();k++){
                    org.w3c.dom.Element el2 = (org.w3c.dom.Element) riferimentoLinee.item(k);
                    ddt.addRiferiementoNumeroLinea(el2.getFirstChild().getTextContent());
                }
                arrayListDDT.add(ddt);
            }

        }

        if(isRiferimentoLinea)
            collegaDdtToDetail = true;
        return arrayListDDT;
    }

    private PdfPTable getTableDDT(){
        PdfPTable table = getTable(3);
        table.setHeaderRows(1);
        table.getDefaultCell().setBackgroundColor(Color.lightGray);
        PdfPCell cell = getHeaderCell("Documento di Trasporto");
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(3);
        table.addCell(cell);
        table.addCell(getHeaderCell("Num. DDT"));
        table.addCell(getHeaderCell("Data DDT"));
        table.addCell(getHeaderCell("Importo DDT"));
        table.getDefaultCell().setBackgroundColor(null);
        NodeList datiDDT = docXml.getElementsByTagName("DatiDDT");
        if(datiDDT.getLength() == 0)
            return null;
        for(int i=0;i<datiDDT.getLength();i++) {
            Node lin1 = datiDDT.item(i);
            org.w3c.dom.Element el = (org.w3c.dom.Element) datiDDT.item(i);
            String numDDT = "";
            if(el.getElementsByTagName("NumeroDDT").getLength() > 0)
                numDDT = el.getElementsByTagName("NumeroDDT").item(0).getTextContent();
            String dataDDT = "";
            if(el.getElementsByTagName("DataDDT").getLength() > 0)
                dataDDT = el.getElementsByTagName("DataDDT").item(0).getTextContent();

            if (numDDT.equals(""))
                table.addCell(getTextCell(""));
            else{
                table.addCell(getTextCell(numDDT));
            }
            if (dataDDT.equals(""))
                table.addCell(getTextCell(""));
            else
                table.addCell(getTextCell(DateUtility.converteSqlStringToGUIString(dataDDT)));

            if(!numDDT.equals("")){
                Iterator<DDT> iterator = listaDDT.iterator();
                while (iterator.hasNext()){
                    DDT ddt = iterator.next();
                    if (ddt.getNumeroDDT().equals(numDDT)) {
                        table.addCell(getNumberCellDDT(ddt.getImporto()));
                        break;
                    }
                }
            }

        }

        return table;

    }


    private PdfPTable getTableRiepilogo(){
        PdfPTable table = getTable(5);
        try {
            table.getDefaultCell().setBackgroundColor(Color.lightGray);
            table.addCell(getHeaderCell("Dati Repilogo"));
            table.addCell(getHeaderCell("%Iva"));
            table.addCell(getHeaderCell("Spese Accessorie"));
            table.addCell(getHeaderCell("Totale Imposta"));
            table.addCell(getHeaderCell("Totale Imponibile"));
            table.getDefaultCell().setBackgroundColor(null);
            NodeList datiRiepilogo = docXml.getElementsByTagName("DatiRiepilogo");
            for (int i = 0; i < datiRiepilogo.getLength(); i++) {
                org.w3c.dom.Element el = (org.w3c.dom.Element) datiRiepilogo.item(i);

                String aliquotaIva = "";
                if (el.getElementsByTagName("AliquotaIVA").getLength() > 0)
                    aliquotaIva = el.getElementsByTagName("AliquotaIVA").item(0).getTextContent();
                String speseAccessorie = "";
                if (el.getElementsByTagName("SpeseAccessorie").getLength() > 0)
                    speseAccessorie = el.getElementsByTagName("SpeseAccessorie").item(0).getTextContent();
                String descrizioneIva = "";
                if (el.getElementsByTagName("EsigibilitaIVA").getLength() > 0)
                    descrizioneIva = el.getElementsByTagName("EsigibilitaIVA").item(0).getTextContent();
                String imposta = "";
                if (el.getElementsByTagName("Imposta").getLength() > 0)
                    imposta = el.getElementsByTagName("Imposta").item(0).getTextContent();
                String imponibile = "";
                if (el.getElementsByTagName("ImponibileImporto").getLength() > 0)
                    imponibile = el.getElementsByTagName("ImponibileImporto").item(0).getTextContent();
                String rifNormativo = "";
                if (el.getElementsByTagName("RiferimentoNormativo").getLength() > 0)
                    rifNormativo = el.getElementsByTagName("RiferimentoNormativo").item(0).getTextContent();

                table.addCell(getTextCell(getEsigibilitaIva(descrizioneIva,rifNormativo)));
                table.addCell(getNumberDetailCell(aliquotaIva,"iva"));
                table.addCell(getTextCell(speseAccessorie));
                table.addCell(getNumberDetailCell(imposta,""));
                table.addCell(getNumberDetailCell(imponibile,""));
            }
            table.addCell(getHeaderCell("Importo bollo"));
            table.addCell(getHeaderCell("Sc.Magg"));
            table.addCell(getHeaderCell("Valuta"));
            PdfPCell cellTotale = getHeaderCell("Totale Fattura");
            cellTotale.setColspan(2);
            cellTotale.setBorderColor(Color.LIGHT_GRAY);
            table.addCell(cellTotale);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();

            String importoBollo = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/DatiBollo/ImportoBollo", docXml);
            String scMagg = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ScontoMaggiorazione/Importo", docXml);
            String importoTotale = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento", docXml);
            String valuta = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Divisa", docXml);

            table.addCell(getNumberCell((importoBollo)));
            PdfPCell cellSconto = getTextCell(scMagg);
            cellSconto.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellSconto);
            PdfPCell cellValuta = getTextCell(valuta);
            cellValuta.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellValuta);
            cellTotale = getNumberCell((importoTotale));
            cellTotale.setColspan(2);
            cellTotale.setBorderColor(Color.LIGHT_GRAY);
            table.addCell(cellTotale);

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            return table;
        }

    }

    private PdfPTable getTableRitenuta(){
        PdfPTable table = getTable(4);
        try {
            table.getDefaultCell().setBackgroundColor(Color.lightGray);
            PdfPCell header = getHeaderCell("Dati Ritenuta");
            header.setColspan(4);
            table.addCell(header);
            table.addCell(getHeaderCell("Tipo Ritenuta"));
            table.addCell(getHeaderCell("Importo Ritenuta"));
            table.addCell(getHeaderCell("Aliquota Ritenuta"));
            table.addCell(getHeaderCell("Causale Pagamento"));
            table.getDefaultCell().setBackgroundColor(null);
            NodeList datiRitenuta = docXml.getElementsByTagName("DatiRitenuta");
            if(datiRitenuta.getLength() > 0) {
                for (int i = 0; i < datiRitenuta.getLength(); i++) {
                    org.w3c.dom.Element el = (org.w3c.dom.Element) datiRitenuta.item(i);

                    String tipoRitenuta = "";
                    if (el.getElementsByTagName("TipoRitenuta").getLength() > 0)
                        tipoRitenuta = el.getElementsByTagName("TipoRitenuta").item(0).getTextContent();
                    String importoRitenuta = "";
                    if (el.getElementsByTagName("ImportoRitenuta").getLength() > 0)
                        importoRitenuta = el.getElementsByTagName("ImportoRitenuta").item(0).getTextContent();
                    String aliquotaRitenuta = "";
                    if (el.getElementsByTagName("AliquotaRitenuta").getLength() > 0)
                        aliquotaRitenuta = el.getElementsByTagName("AliquotaRitenuta").item(0).getTextContent();
                    String causalePagamento = "";
                    if (el.getElementsByTagName("CausalePagamento").getLength() > 0)
                        causalePagamento = el.getElementsByTagName("CausalePagamento").item(0).getTextContent();

                    switch (tipoRitenuta) {
                        case "RT01":
                            tipoRitenuta = "ritenuta pers. fisiche";
                            break;
                        case "RT02":
                            tipoRitenuta = "ritenuta pers. giuridiche";
                            break;
                    }

                    table.addCell(getTextCell(tipoRitenuta));
                    table.addCell(getNumberDetailCell(importoRitenuta, ""));
                    table.addCell(getNumberDetailCell(aliquotaRitenuta, "iva"));
                    table.addCell(getTextCell(causalePagamento));
                }
            }else
                table = null;

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            return table;
        }
    }

    private PdfPTable getTableCassaPrevidenziale(){
        PdfPTable table = getTable(8);
        try {
            table.getDefaultCell().setBackgroundColor(Color.lightGray);
            PdfPCell header = getHeaderCell("Dati Cassa Previdenziale");
            header.setColspan(8);
            table.addCell(header);
            table.addCell(getHeaderCell("Tipo Cassa"));
            table.addCell(getHeaderCell("Aliquota Cassa"));
            table.addCell(getHeaderCell("Importo Contributo Cassa"));
            table.addCell(getHeaderCell("Imponibile Cassa"));
            table.addCell(getHeaderCell("Aliquota Iva"));
            table.addCell(getHeaderCell("Ritenuta"));
            table.addCell(getHeaderCell("Natura"));
            table.addCell(getHeaderCell("Riferimento Amministrativo"));
            table.getDefaultCell().setBackgroundColor(null);
            NodeList datiCassa = docXml.getElementsByTagName("DatiCassaPrevidenziale");
            if(datiCassa.getLength()>0) {
                for (int i = 0; i < datiCassa.getLength(); i++) {
                    org.w3c.dom.Element el = (org.w3c.dom.Element) datiCassa.item(i);

                    String tipoCassa = "";
                    if (el.getElementsByTagName("TipoCassa").getLength() > 0)
                        tipoCassa = el.getElementsByTagName("TipoCassa").item(0).getTextContent();
                    String aliquotaCassa = "";
                    if (el.getElementsByTagName("AlCassa").getLength() > 0)
                        aliquotaCassa = el.getElementsByTagName("AlCassa").item(0).getTextContent();
                    String importoContributoCassa = "";
                    if (el.getElementsByTagName("ImportoContributoCassa").getLength() > 0)
                        importoContributoCassa = el.getElementsByTagName("ImportoContributoCassa").item(0).getTextContent();
                    String imponibileCassa = "";
                    if (el.getElementsByTagName("ImponibileCassa").getLength() > 0)
                        imponibileCassa = el.getElementsByTagName("ImponibileCassa").item(0).getTextContent();
                    String aliquotaIva = "";
                    if (el.getElementsByTagName("AliquotaIva").getLength() > 0)
                        aliquotaIva = el.getElementsByTagName("AliquotaIva").item(0).getTextContent();
                    String ritenuta = "";
                    if (el.getElementsByTagName("Ritenuta").getLength() > 0)
                        ritenuta = el.getElementsByTagName("Ritenuta").item(0).getTextContent();
                    String natura = "";
                    if (el.getElementsByTagName("Natura").getLength() > 0)
                        natura = el.getElementsByTagName("Natura").item(0).getTextContent();
                    String riferimentoAmminstrativo = "";
                    if (el.getElementsByTagName("RiferimentoAmministrativo").getLength() > 0)
                        riferimentoAmminstrativo = el.getElementsByTagName("RiferimentoAmministrativo").item(0).getTextContent();

                    table.addCell(getTextCell(getTipoCassa(tipoCassa)));
                    table.addCell(getNumberDetailCell(aliquotaCassa, "iva"));
                    table.addCell(getNumberDetailCell(importoContributoCassa, ""));
                    table.addCell(getNumberDetailCell(imponibileCassa, ""));
                    table.addCell(getNumberDetailCell(aliquotaIva, "iva"));
                    table.addCell(getTextCell(ritenuta));
                    table.addCell(getTextCell(getNatura(natura)));
                    table.addCell(getTextCell(riferimentoAmminstrativo));

                }
            }else
                table = null;

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            return table;
        }
    }


    private PdfPTable getTablePagamenti() {
        PdfPTable table = getTable(5);
        table.addCell(getHeaderCell("Modalità Pagamento"));
        table.addCell(getHeaderCell("IBAN"));
        table.addCell(getHeaderCell("Istituto"));
        table.addCell(getHeaderCell("Data Scadenza"));
        table.addCell(getHeaderCell("Importo"));
        NodeList dettaglioPagamento = docXml.getElementsByTagName("DettaglioPagamento");
        if (dettaglioPagamento.getLength() > 0){
            showTablePagamenti = true;
            for (int i = 0; i < dettaglioPagamento.getLength(); i++) {
                Node lin1 = dettaglioPagamento.item(i);
                org.w3c.dom.Element el = (org.w3c.dom.Element) dettaglioPagamento.item(i);

                String modPagamento = "";
                if (el.getElementsByTagName("ModalitaPagamento").getLength() > 0)
                    modPagamento = el.getElementsByTagName("ModalitaPagamento").item(0).getTextContent();
                String iban = "";
                if (el.getElementsByTagName("IBAN").getLength() > 0)
                    iban = el.getElementsByTagName("IBAN").item(0).getTextContent();
                String istituto = "";
                if (el.getElementsByTagName("IstitutoFinanziario").getLength() > 0)
                    istituto = el.getElementsByTagName("IstitutoFinanziario").item(0).getTextContent();
                String dataScadenza = "";
                if (el.getElementsByTagName("DataScadenzaPagamento").getLength() > 0)
                    dataScadenza = DateUtility.converteSqlStringToGUIString(el.getElementsByTagName("DataScadenzaPagamento").item(0).getTextContent());
                String importo = "";
                if (el.getElementsByTagName("ImportoPagamento").getLength() > 0)
                    importo = el.getElementsByTagName("ImportoPagamento").item(0).getTextContent();
                table.addCell(getTextHeaderCell(getModalitaPagamento(modPagamento)));
                table.addCell(getTextHeaderCell(iban));
                table.addCell(getTextHeaderCell(istituto));
                table.addCell(getTextHeaderCell(dataScadenza));
                table.addCell(getNumberCell((importo)));
            }
        }else{
            showTablePagamenti = false;
        }
        return table;
    }

    private PdfPTable getTable(int numCol){
        PdfPTable table = new PdfPTable((numCol));
        table.setLockedWidth(true);
        table.setTotalWidth(550);
        table.setSpacingAfter(5);
        table.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);
        return table;
    }

    private PdfPCell getTextCell(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(Font.HELVETICA,8)));
        cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getTextUMCell(String text){
        String paternToSearchFor = "conf*";
        Pattern patter = Pattern.compile(paternToSearchFor,Pattern.CASE_INSENSITIVE);
        Matcher m = patter.matcher(text);
        if(m.find())
            text = "Conf";
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(Font.HELVETICA,8)));
        cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getTextCodiceCell(ArrayList<CodiceArticolo> codiceArticoloList){
        Font fontValore =  new Font(Font.HELVETICA,8);
        Font fontTipo =  new Font(Font.HELVETICA,6);
        Paragraph par = new Paragraph();
        if(codiceArticoloList.size()>0){
            Iterator<CodiceArticolo> iter = codiceArticoloList.iterator();
            while(iter.hasNext()){
                CodiceArticolo cod = iter.next();
                par.add(new Chunk(cod.getCodiceValore(),fontValore));
                par.add(Chunk.NEWLINE);
                if(cod.getCodiceTipo().length()>0) {
                    String temp = "("+cod.getCodiceTipo()+")";
                    par.add(new Chunk(temp,fontTipo));
                    par.add(Chunk.NEWLINE);
                }
            }
        }else
            par.add(new Chunk(""));

        PdfPCell cell = new PdfPCell(par);
        cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getTextDescrizioneCell(String descrizioneArt,String descrOrdAcqu,String descrDdt,String descrDatiFattColl,ArrayList<String > datiGestionali){
        Font fontArt =  new Font(Font.HELVETICA,8);
        Font fontDoc =  new Font(Font.HELVETICA,6);
        Paragraph par = new Paragraph();
        par.add(new Chunk(descrizioneArt,fontArt));
        if(datiGestionali.size() > 0){
            Iterator<String> iter = datiGestionali.iterator();
            while (iter.hasNext()){
                par.add(Chunk.NEWLINE);
                par.add(new Chunk(iter.next(),fontDoc));
            }
        }
        par.add(new Chunk(descrDatiFattColl,fontDoc));
        par.add(new Chunk(descrOrdAcqu,fontDoc));
        par.add(new Chunk(descrDdt,fontDoc));
        PdfPCell cell = new PdfPCell(par);
        cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }


    private PdfPCell getTextHeaderCell(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(Font.HELVETICA,10)));//cell.setHorizontalAlignment(Cell.ALIGN_BASELINE);
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getHeaderCell(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text,new Font(Font.HELVETICA,10)));
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getNumberCell(String text){

        if (text.isEmpty()) {
            PdfPCell cellTemp = new PdfPCell(new Phrase((text), new Font(Font.HELVETICA, 10)));
            cellTemp.setBorderColor(Color.LIGHT_GRAY);
            return cellTemp;
        }
        else {
            text = text.replaceAll("\\s+","");
            BigDecimal number = new BigDecimal(text);
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            String pattern = "###,##0.00";
            //DecimalFormat df = (DecimalFormat)nf;
            DecimalFormat df = new DecimalFormat(pattern);
            PdfPCell cell = new PdfPCell(new Phrase(df.format(number), new Font(Font.HELVETICA, 10)));
            cell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
            cell.setBorderColor(Color.LIGHT_GRAY);
            return cell;
        }
    }

    private PdfPCell getNumberCellDDT(BigDecimal totale){
        String text = "";
        if (totale.doubleValue() == 0) {
            PdfPCell cellTemp = new PdfPCell(new Phrase((text), new Font(Font.HELVETICA, 8)));
            cellTemp.setBorderColor(Color.LIGHT_GRAY);
            return cellTemp;
        }
        else {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            String pattern = "###,##0.00";
            //DecimalFormat df = (DecimalFormat)nf;
            DecimalFormat df = new DecimalFormat(pattern);
            PdfPCell cell = new PdfPCell(new Phrase(df.format(totale), new Font(Font.HELVETICA, 8)));
            cell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
            cell.setBorderColor(Color.LIGHT_GRAY);
            return cell;
        }
    }

    private PdfPCell getScontoDetailCell(ArrayList<String> sconto){
        Paragraph par = new Paragraph();
        Font font = new Font(Font.HELVETICA, 8);
        if (sconto.size()>0) {
            Iterator<String> iter = sconto.iterator();
            while (iter.hasNext()){
                String scText = iter.next().replace(".",",");
                par.add(new Chunk(scText,font));
                par.add(Chunk.NEWLINE);
            }
        }else
            par.add(new Chunk("",font));

        PdfPCell cellSconto = new PdfPCell(par);
        cellSconto.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellSconto.setBorderColor(Color.LIGHT_GRAY);
        return cellSconto;
    }

    private PdfPCell getNumberDetailCell(String text,String tipoCell){

        Font font = new Font(Font.HELVETICA,8);

        if(tipoCell.equals("iva")){
            text = text.replace(".",",");
            PdfPCell cellIva = new PdfPCell(new Phrase((text), new Font(Font.HELVETICA, 8)));
            cellIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellIva.setBorderColor(Color.LIGHT_GRAY);
            return cellIva;
        }

        if (text.isEmpty())
            return new PdfPCell(new Phrase((text),new Font(Font.HELVETICA,8)));
        else {
            text = text.replaceAll("\\s+","");
            BigDecimal number = new BigDecimal(text);
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            String pattern = "###,##0.00";
            //DecimalFormat df = (DecimalFormat)nf;
            DecimalFormat df = new DecimalFormat(pattern);
            PdfPCell cell = new PdfPCell(new Phrase(df.format(number), new Font(Font.HELVETICA, 8)));
            cell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
            cell.setBorderColor(Color.LIGHT_GRAY);
            return cell;
        }
    }

    class CodiceArticolo {
        private String codiceTipo;
        private String codiceValore;

        public CodiceArticolo(){
            codiceTipo = "";
            codiceValore = "";
        }

        public String getCodiceTipo() {
            return codiceTipo;
        }

        public void setCodiceTipo(String codiceTipo) {
            this.codiceTipo = codiceTipo;
        }

        public String getCodiceValore() {
            return codiceValore;
        }

        public void setCodiceValore(String codiceValore) {
            this.codiceValore = codiceValore;
        }
    }

    class DDT {
        private ArrayList<String>  riferimentoNumeroLinea;
        private String numeroDDT;
        private String dataDDT;
        private BigDecimal importo;

        public DDT(){
            riferimentoNumeroLinea = new ArrayList<String>();
            numeroDDT = "";
            dataDDT = "";
            importo = new BigDecimal(0);
        }

        public DDT(ArrayList<String> riferiementoNumeroLinea,String numeroDDT,String dataDDT){
            this.riferimentoNumeroLinea = riferiementoNumeroLinea;
            this.dataDDT = dataDDT;
            this.numeroDDT = numeroDDT;
        }

        public DDT(String numeroDDT,String dataDDT){
            this.numeroDDT = numeroDDT;
            this.dataDDT = dataDDT;
            this.riferimentoNumeroLinea = new ArrayList<String>();
        }

        public void addRiferiementoNumeroLinea(String riferiemntoNumLinea){
            this.riferimentoNumeroLinea.add(riferiemntoNumLinea);
        }

        public boolean contains(String numeroLinea){
            return riferimentoNumeroLinea.contains(numeroLinea);
        }

        public ArrayList<String> getRiferimentoNumeroLinea() {
            return riferimentoNumeroLinea;
        }

        public void setRiferimentoNumeroLinea(ArrayList<String> riferimentoNumeroLinea) {
            this.riferimentoNumeroLinea = riferimentoNumeroLinea;
        }

        public String getNumeroDDT() {
            return numeroDDT;
        }

        public void setNumeroDDT(String numeroDDT) {
            this.numeroDDT = numeroDDT;
        }

        public String getDataDDT() {
            return dataDDT;
        }

        public void setDataDDT(String dataDDT) {
            this.dataDDT = dataDDT;
        }

        public void addImporto(String importoTag){
            BigDecimal temp = new BigDecimal(importoTag).setScale(2, RoundingMode.HALF_DOWN);
            this.importo = getImporto().add(temp);
        }

        public BigDecimal getImporto() {
            return importo;
        }

        public void setImporto(BigDecimal importo) {
            this.importo = importo;
        }

        public String toString(){
            return "DDT num: "+numeroDDT+" del "+dataDDT;
        }
    }

    class OrdineAcquisto {
        private String idDocumento;
        private String data;
        private ArrayList<String> riferimentoLineee;

        public OrdineAcquisto(){
            riferimentoLineee = new ArrayList<String>();
            data="";
        }

        public OrdineAcquisto(String idDocuemento){
            this.idDocumento = idDocuemento;
            this.riferimentoLineee = new ArrayList<String>();
        }

        public String getIdDocumento() {
            return idDocumento;
        }

        public void setIdDocumento(String idDocumento) {
            this.idDocumento = idDocumento;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public ArrayList<String> getRiferimentoLineee() {
            return riferimentoLineee;
        }

        public void setRiferimentoLineee(ArrayList<String> riferimentoLineee) {
            this.riferimentoLineee = riferimentoLineee;
        }

        public void addRiferimentoLinea(String riferimentoLinea){
            riferimentoLineee.add(riferimentoLinea);
        }

        public boolean contains(String riferimento){
            return riferimentoLineee.contains(riferimento);
        }

        public String toString(){
            String ret = "Ord.d'Acquisto: "+idDocumento;
            if(!data.equals(""))
                ret += " del "+data;
            return  ret;
        }
    }

    class DatiFattureCollegate{
        private String idDocumento;
        private String data;
        private ArrayList<String> riferimentoLineee;

        public DatiFattureCollegate(){
            riferimentoLineee = new ArrayList<String>();
        }

        public String getIdDocumento() {
            return idDocumento;
        }

        public void setIdDocumento(String idDocumento) {
            this.idDocumento = idDocumento;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public ArrayList<String> getRiferimentoLineee() {
            return riferimentoLineee;
        }

        public void setRiferimentoLineee(ArrayList<String> riferimentoLineee) {
            this.riferimentoLineee = riferimentoLineee;
        }

        public void addRiferimentoLinea(String riferimentoLinea){
            riferimentoLineee.add(riferimentoLinea);
        }

        public boolean contains(String riferimento){
            return riferimentoLineee.contains(riferimento);
        }

        public String toString(){
            String ret = "Rif.Fattura: "+idDocumento;
            if(!data.isEmpty())
                ret += " del "+data;
            return  ret;
        }

    }

    class AlternatingBackground implements PdfPTableEvent{
        public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases){
            int columns;
            Rectangle rect;
            int footer = widths.length - table.getFooterRows();
            int header = table.getHeaderRows() - table.getFooterRows() + 1;
            for(int row=header;row<footer;row+=2){
                columns = widths[row].length-1;
                rect = new Rectangle(widths[row][0],heights[row],widths[row][columns],heights[row+1]);
                rect.setBackgroundColor(Color.gray);
                rect.setBorder(Rectangle.NO_BORDER);
                canvases[PdfPTable.BASECANVAS].rectangle(rect);
            }

        }
    }

    class HeaderFooter extends PdfPageEventHelper{

        int pagenumber;
        int totalePageNumber;

        public void onStartPage(PdfWriter writer, Document document){
            pagenumber++;
        }

        public void onEndPage(PdfWriter writer,Document document){
            Rectangle rect = writer.getBoxSize("pageNumber");
            ColumnText.showTextAligned(
                    writer.getDirectContent(),
                    Element.ALIGN_CENTER,
                    new Phrase(String.format("pag. %d ",writer.getPageNumber()),new Font(Font.HELVETICA,8)),
                    (rect.getLeft()+rect.getRight())/2,
                    rect.getBottom()-30,
                    0);
        }

        public void onCloseDocument(PdfWriter writer,Document document){
            totalePageNumber = writer.getPageNumber()-1;
        }
    }

    class TableHeader extends PdfPageEventHelper{
        //TODO: completare il page number
        PdfTemplate total;

        public void onOpenDocument(PdfWriter writer,Document document){
            total = writer.getDirectContent().createTemplate(30,16);
        }

        public void onEndPage(PdfWriter writer,Document document){
                PdfPTable table = new PdfPTable(2);
                try{
                    table.setWidths(new int[]{24,2});
                    table.setTotalWidth(527);
                    table.setLockedWidth(true);
                    table.getDefaultCell().setFixedHeight(20);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(String.format("pag. %d di",writer.getPageNumber()));
                    PdfPCell cell = new PdfPCell(Image.getInstance(total));
                    table.addCell(cell);
                    table.writeSelectedRows(0,-1,34,803,writer.getDirectContent());
                }catch(Exception ex){
                    ex.printStackTrace();
                }
        }

        public void onCloseDocument(PdfWriter writer,Document document){

            ColumnText.showTextAligned(total,Element.ALIGN_CENTER,new Phrase(String.valueOf(writer.getPageNumber()-1)),2,2,0);
        }

    }

    private String getEsigibilitaIva(String tagDescr,String tagRifNorm){
        String ret = "";
        switch (tagDescr){
            case "I": ret = "Iva ad esigibilità immediata";break;
            case "D": ret = "Iva ad esigibilità differita";break;
            case "S": ret = "scissione dei pagamenti";break;
        }
        if(tagRifNorm.length()>0)
            ret+="\n("+tagRifNorm+")";
        return  ret;
    }

    private String getModalitaPagamento(String tag){
        String ret = "";
        switch (tag){
            case "MP01": ret = "contanti";break;
            case "MP02": ret = "assegno";break;
            case "MP03": ret = "assegno circolare";break;
            case "MP04": ret = "contanti presso Tesoreria";break;
            case "MP05": ret = "bonifico";break;
            case "MP06": ret = "vaglia cambiario";break;
            case "MP07": ret = "bollettino bancario";break;
            case "MP08": ret = "carta di pagamento";break;
            case "MP09": ret = "RID";break;
            case "MP10": ret = "RID utenze";break;
            case "MP11": ret = "RID veloce";break;
            case "MP12": ret = "RIBA";break;
            case "MP13": ret = "MAV";break;
            case "MP14": ret = "quietanza erario";break;
            case "MP15": ret = "giroconto su conti di contabilità speciale";break;
            case "MP16": ret = "domiciliazione bancaria";break;
            case "MP17": ret = "domiciliazione postale";break;
            case "MP18": ret = "bollettino di c/c postale";break;
            case "MP19": ret = "SEPA Direct Debit";break;
            case "MP20": ret = "SEPA Direct Debit CORE";break;
            case "MP21": ret = "SEPA Direct Debit B2B";break;
            case "MP22": ret = "Trattenuta su somme già riscosse";break;
        }
        return ret;
    }

    private String getTipoDocumento(String tag){
        String ret = "";
        switch (tag){
            case "TD01": ret = "fattura";break;
            case "TD02": ret = "acconto/anticipo su fattura";break;
            case "TD03": ret = "acconto/anticipo su parcella";break;
            case "TD04": ret = "nota di credito";break;
            case "TD05": ret = "nota di debito";break;
            case "TD06": ret = "parcella";break;

        }
        return ret;
    }

    private String getNatura(String tag){
        String ret = "";
        switch (tag){
            case "N1": ret = "escluse ex art. 15";break;
            case "N2": ret = "non soggette";break;
            case "N3": ret = "non imponibili";break;
            case "N4": ret = "esenti";break;
            case "N5": ret = "regime del margine / IVA non esposta in fattura";break;
            case "N6": ret = "inversione contabile (per le operazioni in reverse charge ovvero nei casi di autofatturazione per acquisti extra UE di servizi ovvero per importazioni di beni nei soli casi previsti)";break;
            case "N7": ret = "IVA assolta in altro stato UE (vendite a distanza ex art. 40 c. 3 e 4 e art. 41 c. 1 lett. b,  DL 331/93; prestazione di servizi di telecomunicazioni, tele-radiodiffusione ed elettronici ex art. 7-sexies lett. f, g, art. 74-sexies DPR 633/72)";break;

        }
        return ret;
    }


    private String getTipoCassa(String tag){
        String ret = "";
        switch (tag){
            case "TC01": ret = "Cassa nazionale previdenza e assistenza avvocati e procuratori legali ";break;
            case "TC02": ret = "Cassa previdenza dottori commercialisti";break;
            case "TC03": ret = "Cassa previdenza e assistenza geometri";break;
            case "TC04": ret = "Cassa nazionale previdenza e assistenza ingegneri e architetti liberi professionisti";break;
            case "TC05": ret = "Cassa nazionale del notariato";break;
            case "TC06": ret = "Cassa nazionale previdenza e assistenza ragionieri e periti commerciali";break;
            case "TC07": ret = "Ente nazionale assistenza agenti e rappresentanti di commercio (ENASARCO)";break;
            case "TC08": ret = "Ente nazionale previdenza e assistenza consulenti del lavoro (ENPACL)";break;
            case "TC09": ret = "Ente nazionale previdenza e assistenza medici (ENPAM)";break;
            case "TC10": ret = "Ente nazionale previdenza e assistenza farmacisti (ENPAF)";break;
            case "TC11": ret = "Ente nazionale previdenza e assistenza veterinari (ENPAV)";break;
            case "TC12": ret = "Ente nazionale previdenza e assistenza impiegati dell'agricoltura (ENPAIA)";break;
            case "TC13": ret = "Fondo previdenza impiegati imprese di spedizione e agenzie marittime";break;
            case "TC14": ret = "stituto nazionale previdenza giornalisti italiani (INPGI)";break;
            case "TC15": ret = "Opera nazionale assistenza orfani sanitari italiani (ONAOSI)";break;
            case "TC16": ret = "Cassa autonoma assistenza integrativa giornalisti italiani (CASAGIT)";break;
            case "TC17": ret = "Ente previdenza periti industriali e periti industriali laureati (EPPI)";break;
            case "TC18": ret = "Ente previdenza e assistenza pluricategoriale (EPAP)";break;
            case "TC19": ret = "Ente nazionale previdenza e assistenza biologi (ENPAB)";break;
            case "TC20": ret = "Ente nazionale previdenza e assistenza professione infermieristica (ENPAPI)";break;
            case "TC21": ret = "Ente nazionale previdenza e assistenza psicologi (ENPAP)";break;
            case "TC22": ret = "INPS";break;
        }
        return ret;
    }



    public static void main(String args[]){


        //ReportFattura report = new ReportFattura();
        //report.makeReport("./resources/examples/xml/IT05262890014_2PUDC.xml","./resources/examples/pdf/Fattura_IT05262890014_2PUDC_2.pdf",TipoFattura.PASSIVA);
        //ReportFattura report = new ReportFattura("IT04007020714_3E.xml","Fattura.pdf");
    }
}
