package com.klugesoftware.farmainvoice.DTO;

import com.klugesoftware.farmainvoice.utility.DateUtility;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Mapping dell'header delle fatture xml in ElencoHeaderRowData objects;
 * Il path delle dir dei file xml viene letto dal properties file
 */
public class MappingXmlToRowData {

    private final Logger logger = LogManager.getLogger(MappingXmlToRowData.class.getName());
    private final String PROPERTIES_FILE_NAME = "./resources/conf/config.properties";
    private final Properties properties = new Properties();


    public MappingXmlToRowData(){
        try{
            properties.load(new FileInputStream(PROPERTIES_FILE_NAME));
            }catch(Exception ex){
            logger.error(ex);
        }

    }

    /**
     *
     * @return Array of ElencoHeaderDataRow per la creazione della tabella
     * per la visualizzazione delle testate delle fatture. In caso di errori nella
     * lettura della directory dove dovrebbero essere presenti i file xml, ritorna
     * ElencoHeaderDataRow vuoto( con size = 0);
     */
    public ArrayList<ElencoHeaderRowData> mappingHeaderFattureXml(){
        ArrayList<ElencoHeaderRowData> elenco = new ArrayList<ElencoHeaderRowData>();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            String pathDir = properties.getProperty("dirFileXml");
            File dirFileXml = new File(pathDir);
            if (!dirFileXml.isDirectory() || !dirFileXml.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Errore nella lettura della directory !");
                alert.setContentText("Il percorso " + pathDir + " non è una directory oppure non esiste !");
                alert.showAndWait();
            } else {
                File[] filesFatture = dirFileXml.listFiles();
                if (filesFatture.length == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Directory vuota !");
                    alert.setContentText("Nella directory " + pathDir + " non sono presenti file xml da leggere !");
                    alert.showAndWait();
                } else {
                    for (int i = 0; i < filesFatture.length; i++) {
                        if (filesFatture[i].isFile()) {
                            elenco.add(mapping(docBuilder.parse(filesFatture[i]),filesFatture[i]));
                        }
                    }
                }
            }
        }catch(Exception ex){
            logger.error(ex);
        }
        return elenco;
    }

    private ElencoHeaderRowData mapping(Document doc,File fileXml){

        ElencoHeaderRowData row = new ElencoHeaderRowData();
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();

            String denominazioneFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione", doc);
            String partitaIvaFornitore = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice", doc);
            String codiceFiscale = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/CodiceFiscale", doc);
            String tipoDocumento = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/TipoDocumento", doc);
            String dataFattura = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data", doc);
            String numeroFattura = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero", doc);
            String importoTotale = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento", doc);


            if (denominazioneFornitore != null && denominazioneFornitore.length() > 0) {
                ;
            } else{
                String nomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Nome", doc);
                String cognomeTag = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome", doc);
                if(nomeTag != null && nomeTag.length() > 0) {
                    denominazioneFornitore = nomeTag;
                    if(cognomeTag != null && cognomeTag.length() > 0)
                        denominazioneFornitore+=" "+cognomeTag;
                }else{
                    if(cognomeTag != null && cognomeTag.length() > 0)
                        denominazioneFornitore = cognomeTag;
                }
            }

            //Node denominazione = doc.getElementsByTagName("Denominazione").item(0);
            //Node partitaIva = doc.getElementsByTagName("IdCodice").item(0);
            //Node dataFattura = doc.getElementsByTagName("Data").item(0);
            //Node numeroFattura = doc.getElementsByTagName("Numero").item(0);
            //Node importoTotale = doc.getElementsByTagName("ImportoTotaleDocumento").item(0);
            //Node causale = doc.getElementsByTagName("Causale").item(0);

            row.setDenominazione((denominazioneFornitore == null) ? "" : denominazioneFornitore);

            if(partitaIvaFornitore != null)
                row.setPartitaIva(partitaIvaFornitore);
            else
                if (codiceFiscale != null)
                    row.setPartitaIva(codiceFiscale);
                else
                    row.setPartitaIva("");

            row.setDataFattura((dataFattura == null) ? "" : DateUtility.converteDateToGUIStringDDMMYYYY(DateUtility.converteDBStringYYYMMDDToDate(dataFattura)));
            row.setNumeroFattura((numeroFattura == null) ? "" : numeroFattura);
            if(importoTotale != null && importoTotale.length() > 0) {
                importoTotale = importoTotale.replaceAll("\\s+","");
                row.setImporto((new BigDecimal(importoTotale)));
            }else
                row.setImporto(new BigDecimal(0));
            row.setCausale((tipoDocumento == null) ? "" : getTipoDocumento(tipoDocumento));
            row.setNomeFile(fileXml.getName());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return row;
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

    public static void main(String[] args){
        MappingXmlToRowData m = new MappingXmlToRowData();
        m.mappingHeaderFattureXml();
    }
}
