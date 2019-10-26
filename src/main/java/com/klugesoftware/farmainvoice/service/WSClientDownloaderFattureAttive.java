package com.klugesoftware.farmainvoice.service;

import com.chilkatsoft.CkJsonArray;
import com.chilkatsoft.CkJsonObject;
import com.klugesoftware.farmainvoice.model.Cliente;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class WSClientDownloaderFattureAttive {

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

    private Logger logger = LogManager.getLogger(WSClientDownloaderFattureAttive.class.getName());

    //private final String wsHost = "http://localhost:8880/webservicestest/downloadservice/";
    private  final String wsHost = "http://klugesoftware.com/webservices/downloadservice/";
    private final String wsDownloadHost = wsHost+"/downloadByPartitaIva/";
    private final String wsDeleteHost = wsHost+"/deleteFattura/";


    public WSClientDownloaderFattureAttive(){

    }

    public void downloadFattureAttive(String nomeFileElencoClienti) {
        try {
            Client wsClient = Client.create();
            WebResource webResources = null;
            ClientResponse response = null;
            ArrayList<Cliente> account = new ArrayList<>();
            File fileCsv = new File(nomeFileElencoClienti);
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while ((line = br.readLine()) != null) {
                temp = line.split(";");
                Cliente cl = new Cliente();
                cl.setNomeCliente(temp[0]);
                cl.setUser(temp[1]);//partita iva
                cl.setEmail(temp[2]);
                account.add(cl);
            }

            Iterator<Cliente> iter = account.iterator();
            String folder;
            CkJsonObject json = new CkJsonObject();
            while (iter.hasNext()) {
                Cliente cliente = iter.next();
                folder = "/Users/marcoscagliosi/Documents/fattureElettronicheClienti/"+cliente.getNomeCliente()+"/emesseWS/";
                webResources = wsClient.resource(wsDownloadHost +cliente.getUser());
                response = webResources.type("applicatio/json").get(ClientResponse.class);
                if(response.getStatus() == 200) {
                    // sono presenti dei file
                    boolean success = json.Load(response.getEntity(String.class));
                    if (success != true) {
                        logger.error(json.lastErrorText());
                    } else {
                        CkJsonArray fatture = json.ArrayOf("elenco");
                        int numFatture = fatture.get_Size();
                        int i = 0;
                        OutputStream os = null;
                        while (i < numFatture) {
                            CkJsonObject fattura = fatture.ObjectAt(i);
                            byte[] decoded = Base64.decodeBase64(fattura.stringOf("dati"));
                            File outfile = new File(folder + fattura.stringOf("nomeFile"));
                            String idFattura = fattura.stringOf("idFattura");
                            os = new FileOutputStream(outfile);
                            os.write(decoded);
                            logger.info("ws-client: downloaded and local saving file " + fattura.stringOf("nomeFile"));
                            webResources = wsClient.resource(wsDeleteHost+idFattura);
                            response = webResources.get(ClientResponse.class);
                            if(response.getStatus() == 200){
                                logger.info("ws-client: cancellazione ok( fatturaName,idFfattura): "+fattura.stringOf("nomeFile")+" "+fattura.stringOf("idFattura"));
                            }else{
                                if(response.getStatus() == 204){
                                    logger.warn("ws-client: cancellazione non possibile: la fattura non esiste( fatturaName,idFfattura): "+fattura.stringOf("nomeFile")+" "+fattura.stringOf("idFattura"));
                                }else{
                                    logger.error("ws-client: errore cancellazione( fatturaName,idFfattura): "+fattura.stringOf("nomeFile")+" "+fattura.stringOf("idFattura"));
                                }
                            }
                            i = i + 1;
                        }
                    }
                }
                else{
                    if (response.getStatus() == 204){
                        // assenza di file da scaricare
                        logger.info("ws-client non ci sono file da scaricare per "+cliente.getNomeCliente()+" "+cliente.getUser());
                    }else{
                        logger.error("ws-client: web service error. Can't get any file.");
                        }
                    }
                }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public static void main (String[] args){
        String fileElencoClienti = "./resources/ElencoClientiPartitaIvaDigithera.csv";
        WSClientDownloaderFattureAttive wsc = new WSClientDownloaderFattureAttive();
        wsc.downloadFattureAttive(fileElencoClienti);
    }
}
