package com.klugesoftware.farmainvoice.ftp;

import com.chilkatsoft.CkEmail;
import com.chilkatsoft.CkFtp2;
import com.chilkatsoft.CkMailMan;
import com.klugesoftware.farmainvoice.model.Cliente;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.report.ReportFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import com.klugesoftware.farmainvoice.utility.DecriptP7m;
import com.klugesoftware.farmainvoice.utility.XmlFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FTPConnectorManager {

    static {
        try {
            String pathFile = System.getProperty("user.dir") + "/lib/libchilkat.jnilib";
            //System.out.println(System.getProperty("user.dir"));
            //System.load("/Users/marcoscagliosi/Documents/IntelliJ/KlugeSoftware/FarmaInvoice/lib/libchilkat.jnilib");
            System.load(pathFile);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    private Logger logger = LogManager.getLogger(com.klugesoftware.farmainvoice.ftp.FTPConnectorManager.class.getName());
    private final String hostDigithera = "ftp-connector.digithera.it";
    private final String pwdDigithera = "FatturEl01!";
    private final String hostKluge = "ftp.klugesoftware.com";
    private final String pwdKluge = "FatturEl01!";

    /**
     * @return false if it's wrong
     *         true se sono stati eseguiti i seguenti step( saranno presenti i reportPdf nella cartella pdf)
     * Il metodo si occupa di:
     * - download from Digithera
     * - decript file p7m to xml file
     * - copy file on Kluge ftp and local folder( fattureXml)
     * - delete file on digithera
     * - delete file p7m
     */
    private boolean xmlPassiveManagement(String dirPassive, String user) {
        boolean ret = false;
        try {
            // download from digithera file xml e p7m
            CkFtp2 ftpDigithera = getFtp(user, hostDigithera, pwdDigithera,true);

            ftpDigithera.ChangeRemoteDir(FtpPath.REMOTE_PATH_DIGITHERA_FATTURE_PASSIVE);
            ftpDigithera.DownloadTree(dirPassive);

            //backup sul folder locale
            File folder = new File(dirPassive);
            File folderBackup = new File(dirPassive + "/backupFileFtp/");
            File[] listaFileDownloaded = folder.listFiles();
            for (File file : listaFileDownloaded) {
                if (file.isFile())
                    FileUtils.copyFileToDirectory(file, folderBackup);
            }

            //delete file su digithera
            boolean successedDelete = ftpDigithera.DeleteTree();
            if (successedDelete != true) {
                logger.error(ftpDigithera.lastErrorText());
                //System.out.println(ftpDigithera.lastErrorText());
                return false;
            }

            boolean success = ftpDigithera.Disconnect();

            // decripta i file p7m in xml
            DecriptP7m decriptP7m = new DecriptP7m();
            decriptP7m.decriptFilesP7m(dirPassive, dirPassive);

            //connect to klugeSoftware e
            // upload dei file
            FTPConnector ftpKluge = new FTPConnector(hostKluge, user, pwdKluge);
            FTPClient ftpClient = ftpKluge.getFtpClient();
            // delete file p7m
            File folderFattureXml = new File(dirPassive + "/fattureXml/");
            File[] listPassive = folder.listFiles();
            for (File fileXml : listPassive) {
                if (fileXml.isFile()) {
                    if (fileXml.getName().toLowerCase().endsWith(".p7m")) {
                        FileUtils.forceDelete(fileXml);
                    } else {
                        //upload file xml passive to Kluge e copy in localDir fattureXml
                        if ( fileXml.getName().endsWith(".xml") || fileXml.getName().endsWith(".XML")) {
                            ftpClient.changeWorkingDirectory(FtpPath.REMOTE_PATH_KLUGE_BACKUP_PASSIVE);
                            ftpKluge.upLoadFile(fileXml.toString(), fileXml.getName());
                            ftpClient.changeWorkingDirectory(FtpPath.REMOTE_PATH_KLUGE_FATTURE_PASSIVE);
                            ftpKluge.upLoadFile(fileXml.toString(), fileXml.getName());
                            FileUtils.copyFileToDirectory(fileXml, folderFattureXml);
                        }
                    }
                }
            }
            ftpKluge.closeFtpClient();


        } catch (IOException ex) {
            ret = false;
            logger.error(ex);
        } finally {
            return ret;
        }
    }


    /**
     *
     * @param /dirAttive
     * @param cliente
     * @return true if success else return false;
     *
     * Il metodo si occupa di :
     * - fare il download in dirAttive delle xmlAttive da Ftp-Kluge
     * - delete on ftp-kluge
     * - upload di xmlAttive su ftp-digithera
     * - move file xmlAttive in dirAttive/inviateSDI/
     *
     */


    private boolean xmlAttiveManagement(String dirAttive, Cliente cliente) {
        boolean ret = false;
        try {

            File folderFattureXmlAttive = new File(dirAttive);
            CkFtp2 ftpKluge = getFtp(cliente.getUser() + "@klugesoftware.com", hostKluge, pwdKluge,false);
            if (ftpKluge == null) {
                logger.error("User: " + cliente.getNomeCliente() + " Cannot establish ftp-kluge connect!");
                return false;
            }
            boolean uploadXmlAttive = false;
            ftpKluge.ChangeRemoteDir(FtpPath.REMOTE_PATH_KLUGE_FATTURE_ATTIVE);
            int countFattureAttive = ftpKluge.GetDirCount();
            if (countFattureAttive > 0) {
                //download fatture attive from kluge-ftp
                ftpKluge.DownloadTree(dirAttive);
                ftpKluge.DeleteTree();
                uploadXmlAttive = true;
            }

            ftpKluge.Disconnect();

                if (cliente.getAsl().equals("aslBat")) {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(dirAttive + "sync.properties"));
                    if (properties.getProperty("invioScheduled").equals("true")) {
                        Date toDay = DateUtility.getToday();
                        if (toDay.after(DateUtility.converteGUIStringDDMMYYYYToDate(properties.getProperty("dataInvio")))) {
                            uploadXmlAttive = true;
                            properties.setProperty("invioScheduled", "false");
                            properties.setProperty("dataInvio", "");
                            properties.store(new FileOutputStream(dirAttive + "sync.properties"), null);
                        } else {
                            uploadXmlAttive = false;
                        }
                    }else{
                        if(countFattureAttive>0) {
                            if (isNotFatturaAsl(dirAttive, properties)) {
                                uploadXmlAttive = true;
                            } else {
                                uploadXmlAttive = false;
                            }
                        }
                    }
                }

                if (uploadXmlAttive) {
                        //upload fatture attive
                        CkFtp2 ftpDigithera = getFtp(cliente.getUser(),hostDigithera,pwdDigithera,true);
                        //Test: CkFtp2 ftpDigithera = getFtp(cliente.getUser() + "@klugesoftware.com", hostKluge, pwdKluge);
                        if (ftpDigithera == null) {
                            logger.error("User: " + cliente.getNomeCliente() + " Cannot establish ftp-DigitHub connect!");
                            return false;
                        }
                        ftpDigithera.ChangeRemoteDir(FtpPath.REMOTE_PATH_DIGITHERA_FATTURE_ATTIVE);
                        //Test:ftpDigithera.ChangeRemoteDir("/uploadingTest");

                        int countUploaded = 0;
                        File[] elencoFattureAttive = folderFattureXmlAttive.listFiles();
                        File dirInviateSDI = new File(dirAttive + "inviateSDI/");
                        for (File fileXmlAttive : elencoFattureAttive) {
                            if (fileXmlAttive.isFile() && fileXmlAttive.getName().toLowerCase().endsWith("xml")) {
                                if (ftpDigithera.PutFile(fileXmlAttive.toString(), fileXmlAttive.getName())){
                                    countUploaded++;
                                }else{
                                    logger.error(ftpDigithera.lastErrorText());
                                    return false;
                                }
                            }
                        }

                        ftpDigithera.Disconnect();
                        if(countUploaded==countFattureAttive){
                            for (File fileXmlAttive : elencoFattureAttive) {
                                if (fileXmlAttive.isFile() && fileXmlAttive.getName().toLowerCase().endsWith("xml")) {
                                    FileUtils.moveFileToDirectory(fileXmlAttive, dirInviateSDI, false);
                                }
                            }
                            logger.info("User: "+cliente.getNomeCliente()+" uploaded "+countUploaded+" file xmlAttive on Ftp-DigitHub");
                        } else {
                            logger.error("User: " + cliente.getNomeCliente() + " Uploaded "+countUploaded+" file xmlAttive on Ftp-DigitHub");
                            return false;
                        }
                    } else
                        return true;

            } catch(IOException ex){
                logger.error(ex);
                return false;
            }

            return true;
    }

    /**
     * Il metodo si occupa di :
     *      * - fare il download in dirAttive delle xmlAttive da Ftp-Kluge
     *      * - delete on ftp-kluge
     *      * - upload di xmlAttive su ftp-digithera
     *      * - move file xmlAttive in dirAttive/inviateSDI/
     *
     * Inoltre per i clienti che hanno installato al versione di InvoiceManager v2.0.0;
     * la gestione dei file delle fatture attive avviene nel modo seguente:
     * - download from ftp-kluge\syncXmlAttive
     * - upload to ftp-kluge\xmlAttive
     * - upload to ftp-DIGITHUB\input fatture
     * - move xml from dirAttive to dirAttive/inviateSDI
     *
     * @param dirAttive
     * @param cliente
     * @return true if success else false
     */
    private boolean xmlAttiveManagementUpdated(String dirAttive, Cliente cliente) {
        boolean ret = false;
        try {

            File folderFattureXmlAttive = new File(dirAttive);

            CkFtp2 ftpKluge = getFtp(cliente.getUser() + "@klugesoftware.com", hostKluge, pwdKluge,false);
            if (ftpKluge == null) {
                logger.error("User: " + cliente.getNomeCliente() + " Cannot establish ftp-kluge connect!");
                return false;
            }
            boolean uploadXmlAttive = false;

            if(cliente.getUpdated().equals("si"))
                ftpKluge.ChangeRemoteDir(FtpPath.REMOTE_PATH_KLUGE_SYNC_FATTURE_ATTIVE);
            else
                ftpKluge.ChangeRemoteDir(FtpPath.REMOTE_PATH_KLUGE_FATTURE_ATTIVE);

            //download fatture attive from kluge-ftp
            if(ftpKluge.DownloadTree(dirAttive)) {
                ftpKluge.DeleteTree();
            }else{
                logger.error(ftpKluge.lastErrorText());
            }



            File[] elencoFattureAttive = folderFattureXmlAttive.listFiles(new XmlFileFilter());
            int countFattureAttive = elencoFattureAttive.length;
            if(countFattureAttive > 0)
                uploadXmlAttive = true;

            if(cliente.getUpdated().equals("si")) {
                ftpKluge.ChangeRemoteDir(FtpPath.REMOTE_PATH_KLUGE_FATTURE_ATTIVE);
                for (File f : elencoFattureAttive) {
                    if (f.isFile() && f.getName().toLowerCase().endsWith("xml")) {
                        if(isFileInviato(f.getName(),dirAttive)){
                         logger.error("File "+f.getName()+" già inviato !!!");
                         FileUtils.moveFileToDirectory(f,new File(dirAttive+"/notSent/"),true);
                         countFattureAttive--;
                        }else {
                            if (ftpKluge.PutFile(f.toString(), f.getName())) {
                                appendFileInviati(f.getName(),dirAttive);
                            }else{
                                logger.error(ftpKluge.lastErrorText());
                            }
                        }
                    }
                }
            }

            ftpKluge.Disconnect();


            if (cliente.getAsl().equals("aslBat")) {
                Properties properties = new Properties();
                properties.load(new FileInputStream(dirAttive + "sync.properties"));
                if (properties.getProperty("invioScheduled").equals("true")) {
                    Date toDay = DateUtility.getToday();
                    if (toDay.after(DateUtility.converteGUIStringDDMMYYYYToDate(properties.getProperty("dataInvio")))) {
                        uploadXmlAttive = true;
                        properties.setProperty("invioScheduled", "false");
                        properties.setProperty("dataInvio", "");
                        properties.store(new FileOutputStream(dirAttive + "sync.properties"), null);
                        File dirScheduled = new File(dirAttive+"scheduled/");
                        File[] fileScheduled = dirScheduled.listFiles(new XmlFileFilter());
                        for(File fxml : fileScheduled) {
                            FileUtils.moveFileToDirectory(fxml, new File(dirAttive), false);
                            countFattureAttive++;
                        }
                    } else {
                        uploadXmlAttive = false;
                        elencoFattureAttive = folderFattureXmlAttive.listFiles(new XmlFileFilter());
                        for (File fs : elencoFattureAttive){
                            FileUtils.moveFileToDirectory(fs, new File(dirAttive+"scheduled/"), true);
                        }
                    }
                }else{
                    if(countFattureAttive>0) {
                        if (isNotFatturaAsl(dirAttive, properties)) {
                            uploadXmlAttive = true;
                        } else {
                            uploadXmlAttive = false;
                            elencoFattureAttive = folderFattureXmlAttive.listFiles(new XmlFileFilter());
                            for (File fs : elencoFattureAttive){
                                FileUtils.moveFileToDirectory(fs, new File(dirAttive+"scheduled/"), true);
                            }
                        }
                    }
                }
            }

            //aggiorno l'elenco dei file da inviare: questo per escludere i file che risultano già inviati e presenti nella cartella /notSent
            elencoFattureAttive = folderFattureXmlAttive.listFiles(new XmlFileFilter());
            if(elencoFattureAttive.length == 0) {
                uploadXmlAttive = false;
            }



            if (uploadXmlAttive) {
                //upload fatture attive
                CkFtp2 ftpDigithera = getFtp(cliente.getUser(),hostDigithera,pwdDigithera,true);
                //Test:CkFtp2 ftpDigithera = getFtp(cliente.getUser() + "@klugesoftware.com", hostKluge, pwdKluge);
                if (ftpDigithera == null) {
                    logger.error("User: " + cliente.getNomeCliente() + " Cannot establish ftp-DigitHub connect!");
                    return false;
                }
                ftpDigithera.ChangeRemoteDir(FtpPath.REMOTE_PATH_DIGITHERA_FATTURE_ATTIVE);
                //Test:ftpDigithera.ChangeRemoteDir("/uploadingTest");


                int countUploaded = 0;

                File dirInviateSDI = new File(dirAttive + "inviateSDI/");
                for (File fileXmlAttive : elencoFattureAttive) {
                    if (fileXmlAttive.isFile() && fileXmlAttive.getName().toLowerCase().endsWith("xml")) {
                        if (ftpDigithera.PutFile(fileXmlAttive.toString(), fileXmlAttive.getName())){
                            FileUtils.moveFileToDirectory(fileXmlAttive, dirInviateSDI, false);
                            countUploaded++;
                        }else{
                            logger.error(ftpDigithera.lastErrorText());
                            return false;
                        }
                    }
                }

                ftpDigithera.Disconnect();
                if(countUploaded==countFattureAttive){
                    logger.info("User: "+cliente.getNomeCliente()+" uploaded "+countUploaded+" file xmlAttive on Ftp-DigitHub");
                } else {
                    logger.error("User: " + cliente.getNomeCliente() + " Uploaded "+countUploaded+" file xmlAttive on Ftp-DigitHub");
                    return false;
                }
            } else
                return true;

        } catch(IOException ex){
            logger.error(ex);
            return false;
        }

        return true;
    }

    /**
     * Trasferisce le fatture presenti nelle dir <path_cliente>/emesseWs su FTP.Kluge /syncXmlAttive
     */
    private boolean syncFattureEmesseWsToFTP(String pathDirEmesseWs,Cliente cliente){


            //upload fatture attive
            CkFtp2 ftpDigithera = getFtp(cliente.getUser(),hostDigithera,pwdDigithera,true);
            //Test:CkFtp2 ftpDigithera = getFtp(cliente.getUser() + "@klugesoftware.com", hostKluge, pwdKluge);
            if (ftpDigithera == null) {
                logger.error("User: " + cliente.getNomeCliente() + " Cannot establish ftp-DigitHub connect!");
                return false;
            }
            ftpDigithera.ChangeRemoteDir(FtpPath.REMOTE_PATH_KLUGE_SYNC_FATTURE_ATTIVE);
            //Test:ftpDigithera.ChangeRemoteDir("/uploadingTest");

            File dirEmesseWs = new File(pathDirEmesseWs);
            File[] elencoFattureAttive = dirEmesseWs.listFiles(new XmlFileFilter());
            for (File fileXmlAttive : elencoFattureAttive) {
                if (ftpDigithera.PutFile(fileXmlAttive.toString(), fileXmlAttive.getName())){
                    try {
                        logger.info("syncFattureEmesseWsToFTP: syncronized to klugeFtp.syncXmlAttive file "+fileXmlAttive.getName());
                        FileUtils.forceDelete(fileXmlAttive);
                    }catch (IOException ex){
                        logger.error("syncFattureEmesseWsToFTP: can't delete file "+fileXmlAttive.getName(),ex);
                    }
                }else{
                    logger.error(ftpDigithera.lastErrorText());
                    return false;
                }
            }
            ftpDigithera.Disconnect();
            return true;
        }

    /**
     * Questo metodo controlla, leggendo il file FileInviati.txt, se il file è da inviare oppure è stato già inviato;
     * nel caso in cui non è presente nel file allora lo appende.
     * @param nameFile
     * @param dirXmlAttive
     * @return true se il file è stato già inviato;
     *
     */
    private boolean isFileInviato(String nameFile,String dirXmlAttive){
        try {
            File elencoFileInviati = new File(dirXmlAttive + "FileInviati.txt");
            if(!elencoFileInviati.exists())
                elencoFileInviati.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(elencoFileInviati));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains(nameFile)) {
                    br.close();
                    return true;
                }
            }
            br.close();
        }catch(IOException ex){
            logger.error(ex);
            return true;
        }
        return false;
    }

    /**
     * Aggiorna il file FileInviati.txt aggiungendo il nome del file inviato
     * @param nameFile
     * @param dirXmlAttive
     */
    private void appendFileInviati(String nameFile,String dirXmlAttive){
        try{
            File elencoFileInviati = new File(dirXmlAttive + "FileInviati.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(elencoFileInviati,true));
            writer.append(nameFile+"\n");
            writer.close();
        }catch (IOException ex){
            logger.error(ex);
        }

    }

    /**
     *
     * @return true se non è presente fattura asl e quindi è possibile inviare tutti i file altrimenti torna false.
     */
    private boolean isNotFatturaAsl(String dirAttive, Properties properties){
        try {
            //lettura di tutti i file xml e controllo del tag Destinatario: se è ASLBAT allora set invioScheduled=true e dataInvio sul file properties
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document docXml;
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();
            String pIvaAsl;
            String dataFattura;
            File dirFileFattureAttive = new File(dirAttive);
            File[] elencoFileXml = dirFileFattureAttive.listFiles();
            for (File fileXml : elencoFileXml) {
                if(fileXml.isFile() && fileXml.getName().toLowerCase().endsWith("xml")) {
                    docXml = docBuilder.parse(fileXml);
                    pIvaAsl = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice", docXml);
                    if (pIvaAsl.equals(properties.getProperty("partitaIvaAsl"))) {
                        dataFattura = (String) xPath.evaluate("/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data", docXml);
                        dataFattura = DateUtility.converteSqlStringToGUIString(dataFattura);
                        Date dataTemp = DateUtility.aggiungeGiorniAData(5, DateUtility.converteGUIStringDDMMYYYYToDate(dataFattura));
                        String dataInvio = DateUtility.converteDateToGUIStringDDMMYYYY(dataTemp);
                        properties.setProperty("invioScheduled", "true");
                        properties.setProperty("dataInvio", dataInvio);
                        properties.store(new FileOutputStream(dirAttive+"sync.properties"), null);
                        return false;
                    }
                }
            }
        }catch (Exception ex){
            logger.error(ex.getMessage());
            return false;
        }
        return true;
    }

    private CkFtp2 getFtp(String user, String host, String pwd,boolean authTls) {

        CkFtp2 ftp = new CkFtp2();
        boolean success;
        //  Any string unlocks the component for the 1st 30-days.
        success = ftp.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            //System.out.println(ftp.lastErrorText());
            logger.error(ftp.lastErrorText());
            return null;
        }

        ftp.put_Hostname(host);
        ftp.put_Username(user);
        ftp.put_Password(pwd);

        //  Establish an AUTH SSL secure channel after connection
        //  on the standard FTP port 21.
        ftp.put_AuthTls(authTls);

        //  The Ssl property is for establishing an implicit SSL connection
        //  on port 990.  Do not set it.
        ftp.put_Ssl(false);

        //  Connect and login to the FTP server.
        success = ftp.Connect();
        if (success != true) {
            //System.out.println(ftp.lastErrorText());
            logger.error(ftp.lastErrorText());

            return null;
        } else {
            //  LastErrorText contains information even when
            //  successful. This allows you to visually verify
            //  that the secure connection actually occurred.
           // System.out.println(ftp.lastErrorText());
            //logger.info(ftp.lastErrorText());
        }

        //System.out.println("Secure FTP Channel Established!");
        logger.info("Secure FTP Channel Established!");

        return ftp;
    }


    public void makePdf(String folderXml,String folderPdf, TipoFattura tipoFattura) {

        ReportFattura report = new ReportFattura();
        String nomeFileXml;
        File folder = new File(folderXml);
        File[] listaFile = folder.listFiles();
        if(listaFile.length > 0) {
            for (File fileXml : listaFile) {
                if (fileXml.isFile() && ( fileXml.getName().endsWith(".xml") || fileXml.getName().endsWith(".XML") ) ) {
                    nomeFileXml = fileXml.getName();
                    report.makeReport(folderXml + nomeFileXml, folderPdf, tipoFattura);
                    //report.makeReport(folderXml + nomeFileXml, folderXml, tipoFattura);
                }
            }
        }
    }


    /**
     * metodo che invia l'email per avvisare della presenza di fatture su InvoiceManager
     */
    private void sendAlertEmail(String folder, String toEmail){

        try {
            File foldeFileXml = new File(folder);
            File[] listaFile = foldeFileXml.listFiles();
            boolean sendIt = false;
            for (File fileXml : listaFile) {
                if (fileXml.isFile() && (fileXml.getName().endsWith(".xml") || fileXml.getName().endsWith(".XML"))) {
                    sendIt = true;
                }
            }
            if(sendIt) {
                CkMailMan mailman = new CkMailMan();

                boolean success = mailman.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
                if (success != true) {
                    //System.out.println(mailman.lastErrorText());
                    logger.error(mailman.lastErrorText());
                    return;
                }

                //  Set the SMTP server.
                mailman.put_SmtpHost("smtp.gmail.com");

                //  Set the SMTP login/password (if required)
                mailman.put_SmtpUsername("amministrazione@cifarma.it");
                mailman.put_SmtpPassword("Carrot&606&");

                //  Connect to SMTP port 465 using TLS.
                mailman.put_SmtpSsl(true);
                mailman.put_SmtpPort(465);

                //  Create a new email object
                CkEmail email = new CkEmail();

                email.put_Subject("Avviso Fatture Ricevute");
                email.put_Body("Buongiorno\n" +
                        "Avvisiamo che vi sono delle fatture elettroniche ricevute.\n" +
                        "Vi invitiamo a scaricarle tramite InvoiceManager presente sul vostro pc. \n" +
                        "Vi ricordiamo di seguito la sequeza per lo scarico e l'invio al vostro commercialista.\n" +
                        "Una volta avviato il programma InvoiceManager, digitare il tasto AGGIORNA per lo scarico \n" +
                        "e poi il tasto INVIA FATTURE IN CONTABILITA' per l'invio al commercialista. \n" +
                        "Grazie. \n" +
                        "Saluti.\n\n Cifarma Srl ");
                email.put_From("Cifarma <amministrazione@cifarma.it>");
                success = email.AddTo("", toEmail);

                success = mailman.SendEmail(email);
                if (success != true) {
                    logger.error(mailman.lastErrorText());
                    return;
                }

                success = mailman.CloseSmtpConnection();
                if (success != true) {
                    logger.warn("Connection to SMTP server not closed cleanly.");
                }
                logger.info("Mail sent to " + toEmail);
            }
        } catch (Exception ex) {
            logger.error(ex.getStackTrace());
        }

        }



    private void sendDailyEmailFatturePassive(String folderPdf,String toEmail) {
        CkMailMan mailman = new CkMailMan();

        boolean success = mailman.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            //System.out.println(mailman.lastErrorText());
            logger.error(mailman.lastErrorText());
            return;
        }

        //  Set the SMTP server.
        mailman.put_SmtpHost("smtp.gmail.com");

        //  Set the SMTP login/password (if required)
        mailman.put_SmtpUsername("amministrazione@cifarma.it");
        mailman.put_SmtpPassword("Carrot&606&");

        //  Connect to SMTP port 465 using TLS.
        mailman.put_SmtpSsl(true);
        mailman.put_SmtpPort(465);

        //  Create a new email object
        CkEmail email = new CkEmail();

        email.put_Subject("PDF Fatture Ricevute Farmacia Ventura Precedente Partita Iva");
        email.put_Body("Buongiorno\n" +
                "Alleghiamo pdf di fatture elettroniche ricevute.\n" +
                "Vi ricordiamo che questi file non hanno nessuna validità fiscale. \n" +
                "Saluti ");
        email.put_From("Cifarma <amministrazione@cifarma.it>");
        success = email.AddTo("", toEmail);

        String contentType;
        File folder = new File(folderPdf);
        File[] listaFile = folder.listFiles();
        boolean sendIt = false;
        if(listaFile.length > 0) {
            for (File filePdf : listaFile) {
                if (filePdf.isFile() && filePdf.getName().endsWith(".pdf")) {
                    contentType = email.addFileAttachment(folderPdf + filePdf.getName());
                    sendIt = true;
                }
            }

            if (email.get_LastMethodSuccess() != true) {
                logger.error(email.lastErrorText());
                return;
            }

            if(sendIt) {
                success = mailman.SendEmail(email);
                if (success != true) {
                    logger.error(mailman.lastErrorText());
                    return;
                }

                success = mailman.CloseSmtpConnection();
                if (success != true) {
                    logger.warn("Connection to SMTP server not closed cleanly.");
                }
                logger.info("Mail with attachments sent to " + toEmail);

                //sposta i file nella cartella inviati
                try {
                    File folderInviati = new File(folderPdf + "/inviati/");
                    for (File filePdf : listaFile) {
                        if (filePdf.isFile() && filePdf.getName().endsWith(".pdf")) {
                            FileUtils.copyFileToDirectory(filePdf, folderInviati);
                            FileUtils.forceDelete(filePdf);
                        }
                    }
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
        }
    }

    /*
    public void sendEmailAttivePerContabilita(String folderFile,String toEmail){
        CkMailMan mailman = new CkMailMan();

        boolean success = mailman.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            System.out.println(mailman.lastErrorText());
            return;
        }

        //  Set the SMTP server.
        mailman.put_SmtpHost("smtp.gmail.com");

        //  Set the SMTP login/password (if required)
        mailman.put_SmtpUsername("amministrazione@cifarma.it");
        mailman.put_SmtpPassword("cifarmasrl202");

        //  Connect to SMTP port 465 using TLS.
        mailman.put_SmtpSsl(true);
        mailman.put_SmtpPort(465);

        //  Create a new email object
        CkEmail email = new CkEmail();

        email.put_Subject("PDF ed XML Fatture Emesse");
        email.put_Body("Buongiorno\n" +
                "alleghiamo i file pdf ed xml delle fatture elettroniche emesse.\n" +
                "Vi invitiamo ad INOLTRARE QUESTA EMAIL AL VOSTRO COMMERCIALISTA. \n"+
                "Grazie. \n"+
                "Saluti. ");
        email.put_From("Cifarma <amministrazione@cifarma.it>");
        success = email.AddTo("", toEmail);
        //  To add more recipients, call AddTo, AddCC, or AddBcc once per recipient.

        //  Add some attachments.
        //  The AddFileAttachment method returns the value of the content-type it chose for the attachment.
        //String contentType = email.addFileAttachment("qa_data/jpg/starfish.jpg");
        //if (email.get_LastMethodSuccess() != true) {
        //    System.out.println(email.lastErrorText());
        //    return;
        //}
        String contentType;
        File folder = new File(folderFile);
        File[] listaFile = folder.listFiles();
        boolean sendIt = false;
        if(listaFile.length > 0) {
            for (File filePdf : listaFile) {
                if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf") || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))) {
                    contentType = email.addFileAttachment(folderFile + filePdf.getName());
                    sendIt = true;
                }

            }

            //contentType = email.addFileAttachment("qa_data/pdf/fishing.pdf");
            if (email.get_LastMethodSuccess() != true) {
                System.out.println(email.lastErrorText());
                return;
            }

            //  Call SendEmail to connect to the SMTP server and send.
            //  The connection (i.e. session) to the SMTP server remains
            //  open so that subsequent SendEmail calls may use the
            //  same connection.
            if(sendIt) {
                success = mailman.SendEmail(email);
                if (success != true) {
                    System.out.println(mailman.lastErrorText());
                    return;
                }

                success = mailman.CloseSmtpConnection();
                if (success != true) {
                    System.out.println("Connection to SMTP server not closed cleanly.");
                }

                System.out.println("Mail with attachments sent to " + toEmail);

                //sposta i file nella cartella inviati
                try {
                    File folderInviati = new File(folder + "/inviateContabilita/");
                    for (File filePdf : listaFile) {
                        if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf")) || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML")) {
                            FileUtils.copyFileToDirectory(filePdf, folderInviati);
                            FileUtils.forceDelete(filePdf);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
    */

    public void invioFatturePassivePerContabilita(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();
            String folder;
            while(iter.hasNext()){
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/passive/downloadedFileFtp/fattureXml/";

                makePdf(folder,folder,TipoFattura.PASSIVA);
                sendEmailFatturePassivePerContabilita(folder,cliente.getEmail(),cliente.getNomeCliente());
            }


        }catch (Exception ex){
            //ex.printStackTrace();
            logger.error(ex);
        }

    }

    public void invioFattureAttivePerContabilita(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();
            String folder;
            while(iter.hasNext()){
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/attive/inviateSDI/";

                makePdf(folder,folder,TipoFattura.ATTIVA);
                sendEmailFattureAttivePerContabilita(folder,cliente.getEmail(),cliente.getNomeCliente());
            }


        }catch (Exception ex){
            //ex.printStackTrace();
            logger.error(ex);
        }

    }

    private void sendEmailFattureAttivePerContabilita(String folderFile,String toEmail,String nomeCliente){
        CkMailMan mailman = new CkMailMan();

        boolean success = mailman.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            //System.out.println(mailman.lastErrorText());
            logger.error(mailman.lastErrorText());
            return;
        }

        //  Set the SMTP server.
        mailman.put_SmtpHost("smtp.gmail.com");

        //  Set the SMTP login/password (if required)
        mailman.put_SmtpUsername("amministrazione@cifarma.it");
        mailman.put_SmtpPassword("Carrot&606&");

        //  Connect to SMTP port 465 using TLS.
        mailman.put_SmtpSsl(true);
        mailman.put_SmtpPort(465);

        //  Create a new email object
        CkEmail email = new CkEmail();

        email.put_Subject("PDF ed XML Fatture Emesse ");
        email.put_Body("Buongiorno\n" +
                "Vi invitiamo ad INOLTRARE QUESTA EMAIL AL VOSTRO COMMERCIALISTA. \n"+
                "alleghiamo PDF ed XMl delle fatture emesse.\n"+
                "Grazie. \n"+
                "Saluti. ");
        email.put_From("Cifarma <amministrazione@cifarma.it>");
        success = email.AddTo("", toEmail);
        //  To add more recipients, call AddTo, AddCC, or AddBcc once per recipient.

        //  Add some attachments.
        //  The AddFileAttachment method returns the value of the content-type it chose for the attachment.
        //String contentType = email.addFileAttachment("qa_data/jpg/starfish.jpg");
        //if (email.get_LastMethodSuccess() != true) {
        //    System.out.println(email.lastErrorText());
        //    return;
        //}
        FileOutputStream foutPdf = null;
        FileOutputStream foutXml = null;
        ZipOutputStream zoutPdf = null;
        ZipOutputStream zoutXml = null;
        String nameZipPdf = folderFile+"ArchivioPdfEmesse.zip";
        String nameZipXml = folderFile+"ArchivioXmlEmesse.zip";
        FileInputStream in = null;
        try {
            foutPdf = new FileOutputStream(nameZipPdf);
            foutXml = new FileOutputStream(nameZipXml);
            zoutPdf = new ZipOutputStream(foutPdf);
            zoutXml = new ZipOutputStream(foutXml);

        }catch(IOException ex){
            ex.printStackTrace();
        }
        String contentType;
        File folder = new File(folderFile);
        File[] listaFile = folder.listFiles();
        boolean sendIt = false;
        byte[] buffer = new byte[1024];
        if(listaFile.length > 0) {
            for (File filePdf : listaFile) {
                if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf") || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))) {
                    if(filePdf.getName().endsWith(".pdf")){
                        ZipEntry zipEntry = new ZipEntry(filePdf.getName());
                        try {
                            zoutPdf.putNextEntry(zipEntry);
                            in = new FileInputStream(filePdf);
                            int len;
                            while((len = in.read(buffer)) > 0){
                                zoutPdf.write(buffer,0,len);
                            }
                            in.close();
                            zoutPdf.closeEntry();
                        }catch(IOException ex){
                            ex.printStackTrace();
                        }
                    }else
                    if( filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML")){
                        ZipEntry zipEntry = new ZipEntry(filePdf.getName());
                        try {
                            zoutXml.putNextEntry(zipEntry);
                            in = new FileInputStream(filePdf);
                            int len;
                            while((len = in.read(buffer)) > 0){
                                zoutXml.write(buffer,0,len);
                            }
                            in.close();
                            zoutXml.closeEntry();
                        }catch(IOException ex){
                            ex.printStackTrace();
                        }
                    }
                    sendIt = true;
                }
            }
            try {
                zoutPdf.close();
                zoutXml.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }

            contentType = email.addFileAttachment(nameZipPdf);
            contentType = email.addFileAttachment(nameZipXml);

            if (email.get_LastMethodSuccess() != true) {
                logger.error(email.lastErrorText());
                return;
            }

            //  Call SendEmail to connect to the SMTP server and send.
            //  The connection (i.e. session) to the SMTP server remains
            //  open so that subsequent SendEmail calls may use the
            //  same connection.
            if(sendIt) {
                success = mailman.SendEmail(email);
                if (success != true) {
                    logger.error(mailman.lastErrorText());
                    //System.out.println(mailman.lastErrorText());
                    return;
                }

                success = mailman.CloseSmtpConnection();
                if (success != true) {
                    //System.out.println("Connection to SMTP server not closed cleanly.");
                    logger.error("Connection to SMTP server not closed cleanly.");
                }

                //System.out.println("Mail with attachments sent to " + toEmail);
                logger.info("Mail with attachments sent to " + toEmail);


                //sposta i file nella cartella inviati
                try {
                    File folderInviati = new File(folder + "/inviateContabilita/");
                    for (File filePdf : listaFile) {
                        if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf") || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))) {
                            if( filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))
                                FileUtils.copyFileToDirectory(filePdf, folderInviati);
                            FileUtils.forceDelete(filePdf);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }

    }


    private void sendEmailFatturePassivePerContabilita(String folderFile,String toEmail,String nomeCliente){
        CkMailMan mailman = new CkMailMan();

        boolean success = mailman.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            //System.out.println(mailman.lastErrorText());
            logger.error(mailman.lastErrorText());
            return;
        }

        //  Set the SMTP server.
        mailman.put_SmtpHost("smtp.gmail.com");

        //  Set the SMTP login/password (if required)
        mailman.put_SmtpUsername("amministrazione@cifarma.it");
        mailman.put_SmtpPassword("Carrot&606&");

        //  Connect to SMTP port 465 using TLS.
        mailman.put_SmtpSsl(true);
        mailman.put_SmtpPort(465);

        //  Create a new email object
        CkEmail email = new CkEmail();

        email.put_Subject("PDF ed XML Fatture Ricevute ");
        email.put_Body("Buongiorno\n" +
                "Vi invitiamo ad INOLTRARE QUESTA EMAIL AL VOSTRO COMMERCIALISTA. \n"+
                "alleghiamo PDF ed XML delle fatture ricevute.\n"+
                "Grazie. \n"+
                "Saluti. ");
        email.put_From("Cifarma <amministrazione@cifarma.it>");
        success = email.AddTo("", toEmail);
        //  To add more recipients, call AddTo, AddCC, or AddBcc once per recipient.

        //  Add some attachments.
        //  The AddFileAttachment method returns the value of the content-type it chose for the attachment.
        //String contentType = email.addFileAttachment("qa_data/jpg/starfish.jpg");
        //if (email.get_LastMethodSuccess() != true) {
        //    System.out.println(email.lastErrorText());
        //    return;
        //}
        FileOutputStream foutPdf = null;
        FileOutputStream foutXml = null;
        ZipOutputStream zoutPdf = null;
        ZipOutputStream zoutXml = null;
        String nameZipPdf = folderFile+"ArchivioPdfRicevute.zip";
        String nameZipXml = folderFile+"ArchivioXmlRicevute.zip";
        FileInputStream in = null;
        try {
            foutPdf = new FileOutputStream(nameZipPdf);
            foutXml = new FileOutputStream(nameZipXml);
            zoutPdf = new ZipOutputStream(foutPdf);
            zoutXml = new ZipOutputStream(foutXml);

        }catch(IOException ex){
            ex.printStackTrace();
        }
        String contentType;
        File folder = new File(folderFile);
        File[] listaFile = folder.listFiles();
        boolean sendIt = false;
        byte[] buffer = new byte[1024];
        if(listaFile.length > 0) {
            for (File filePdf : listaFile) {
                if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf") || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))) {
                    if(filePdf.getName().endsWith(".pdf")){
                        ZipEntry zipEntry = new ZipEntry(filePdf.getName());
                        try {
                            zoutPdf.putNextEntry(zipEntry);
                            in = new FileInputStream(filePdf);
                            int len;
                            while((len = in.read(buffer)) > 0){
                                zoutPdf.write(buffer,0,len);
                            }
                            in.close();
                            zoutPdf.closeEntry();
                        }catch(IOException ex){
                            ex.printStackTrace();
                        }
                    }else
                        if(filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML")){
                            ZipEntry zipEntry = new ZipEntry(filePdf.getName());
                            try {
                                zoutXml.putNextEntry(zipEntry);
                                in = new FileInputStream(filePdf);
                                int len;
                                while((len = in.read(buffer)) > 0){
                                    zoutXml.write(buffer,0,len);
                                }
                                in.close();
                                zoutXml.closeEntry();
                            }catch(IOException ex){
                                ex.printStackTrace();
                            }
                        }
                    sendIt = true;
                }
            }
            try {
                zoutPdf.close();
                zoutXml.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }

            contentType = email.addFileAttachment(nameZipPdf);
            contentType = email.addFileAttachment(nameZipXml);

            if (email.get_LastMethodSuccess() != true) {
                logger.error(email.lastErrorText());
                return;
            }

            //  Call SendEmail to connect to the SMTP server and send.
            //  The connection (i.e. session) to the SMTP server remains
            //  open so that subsequent SendEmail calls may use the
            //  same connection.
            if(sendIt) {
                success = mailman.SendEmail(email);
                if (success != true) {
                    logger.error(mailman.lastErrorText());
                    //System.out.println(mailman.lastErrorText());
                    return;
                }

                success = mailman.CloseSmtpConnection();
                if (success != true) {
                    //System.out.println("Connection to SMTP server not closed cleanly.");
                    logger.error("Connection to SMTP server not closed cleanly.");
                }

                //System.out.println("Mail with attachments sent to " + toEmail);
                logger.info("Mail with attachments sent to " + toEmail);


                //sposta i file nella cartella inviati
                try {
                    File folderInviati = new File(folder + "/inviateContabilita/");
                    for (File filePdf : listaFile) {
                        if (filePdf.isFile() && (filePdf.getName().endsWith(".pdf") || filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML") )){
                            if(filePdf.getName().endsWith(".xml") || filePdf.getName().endsWith(".XML"))
                                FileUtils.copyFileToDirectory(filePdf, folderInviati);
                            FileUtils.forceDelete(filePdf);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }

    }


    public void invioFatturePassiveGiornaliere(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();
            String folder;
            while(iter.hasNext()){
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/passive/downloadedFileFtp/";
                xmlPassiveManagement(folder,cliente.getUser());
                /*
                    metodi per l'invio dell'email  le fatture allegate
                 */
                //makePdf(folder,folder+"/pdf/",TipoFattura.PASSIVA);
                //sendDailyEmailFatturePassive(folder+"/pdf/",cliente.getEmail());

                /*
                    metodo per l'invio dell'email di avviso di ricezione fatture elettroniche da scaricare
                 */
                sendAlertEmail(folder,cliente.getEmail());

                //cancellazione dei file xml
                try {
                    File foldeFileXml = new File(folder);
                    File[] listaFile = foldeFileXml.listFiles();
                    for (File fileXml : listaFile) {
                        if (fileXml.isFile() && ( fileXml.getName().endsWith(".xml") || fileXml.getName().endsWith(".XML")))
                        {
                            FileUtils.forceDelete(fileXml);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception ex){
            //ex.printStackTrace();
            logger.error(ex);
        }

    }

    public void invioFattureAttiveGiornaliere(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                cl.setAsl(temp[3]);
                cl.setUpdated(temp[4]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();
            String folder;
            while(iter.hasNext()){
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/attive/";
                if(!xmlAttiveManagementUpdated(folder,cliente))
                    logger.error(cliente.getNomeCliente()+" Uploading xmlAttive ko !!!");
            }
        }catch (Exception ex){

            logger.error(ex);
        }
    }

    public void sincronizzaFattureEmesse(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                cl.setAsl(temp[3]);
                cl.setUpdated(temp[4]);
                account.add(cl);
            }
            Iterator<Cliente> iter = account.iterator();
            String folder;
            while(iter.hasNext()){
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/emesseWs/";
                if(!syncFattureEmesseWsToFTP(folder,cliente))
                    logger.error(cliente.getNomeCliente()+" Error: Syncronize fatture emesse ko !!!");
            }
        }catch (Exception ex){
            logger.error(ex);
        }
    }


    public void makeDirectory(String nomeFileElencoClienti){
        try {
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);
                cl.setEmail(temp[2]);
                cl.setAsl(temp[3]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();

            while(iter.hasNext()){
                Cliente cliente = iter.next();
                CkFtp2 ftp = getFtp(cliente.getUser()+ "@klugesoftware.com",hostKluge,pwdKluge,false);
                String[] dirTodo = {"log","syncUpdate","syncXmlAttive","update",};
                for(String s : dirTodo){
                    if(ftp.CreateRemoteDir(s)){
                        logger.info(cliente.getNomeCliente()+" created "+s+" remoteDir");
                    }else{
                        logger.error(ftp.lastErrorText());
                        ftp.Disconnect();
                        return;
                    }
                }
                ftp.Disconnect();
            }

        }catch (Exception ex){

            logger.error(ex);
        }

    }



    public static void main(String[] args){


            String fileElencoClienti = "./resources/ElencoClientiPartitaIvaDigithera.csv";
            FTPConnectorManager ftpConnectorManager = new FTPConnectorManager();
            ftpConnectorManager.invioFatturePassiveGiornaliere(fileElencoClienti);
            ftpConnectorManager.invioFattureAttiveGiornaliere(fileElencoClienti);

            //ftpConnectorManager.makeDirectory(fileElencoClienti);
            //ftpConnectorManager.invioFatturePassivePerContabilita(fileElencoClienti);
            //ftpConnectorManager.invioFattureAttivePerContabilita(fileElencoClienti);

    }
}
