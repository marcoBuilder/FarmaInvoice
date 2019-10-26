package com.klugesoftware.farmainvoice.task;

import com.klugesoftware.farmainvoice.ftp.FTPConnectorManager;
import com.klugesoftware.farmainvoice.service.WSClientDownloaderFattureAttive;
import javafx.concurrent.Task;

public class SincronizzaEdInviaFattureTask extends Task {

    private boolean fattureAttive;
    private boolean fatturePassive;
    private boolean sincronizzaEmesse;

    public SincronizzaEdInviaFattureTask(boolean invioFattureAttive, boolean invioFatturePassive, boolean sincronizzaEmesse){
        this.fattureAttive = invioFattureAttive;
        this.fatturePassive = invioFatturePassive;
        this.sincronizzaEmesse = sincronizzaEmesse;
    }

    @Override
    protected Object call() throws Exception {

        String fileElencoClienti = "./resources/ElencoClientiPartitaIvaDigithera.csv";
        FTPConnectorManager ftpConnectorManager = new FTPConnectorManager();
        WSClientDownloaderFattureAttive wsClientDownloaderFattureAttive  = new WSClientDownloaderFattureAttive();
        if(sincronizzaEmesse){
            updateMessage("...sincronizzazione fatture emesse");
            wsClientDownloaderFattureAttive.downloadFattureAttive(fileElencoClienti);
            ftpConnectorManager.sincronizzaFattureEmesse(fileElencoClienti);
        }
        if(fatturePassive) {
            updateMessage("...sto inviando le fatture passive...");
            ftpConnectorManager.invioFatturePassiveGiornaliere(fileElencoClienti);
        }
        if(fattureAttive) {
            updateMessage("...sto inviando le fatture attive...");
            ftpConnectorManager.invioFattureAttiveGiornaliere(fileElencoClienti);
        }
        updateMessage("operazione terminata");


        return null;
    }
}
