package com.klugesoftware.farmainvoice.utility;

import com.chilkatsoft.CkBinData;
import com.chilkatsoft.CkCrypt2;
import javafx.scene.control.Alert;

import java.io.File;

public class DecriptP7m {

    static {
        try {
            String pathFile = System.getProperty("user.dir")+"/lib/libchilkat.jnilib";
            //System.out.println(System.getProperty("user.dir"));
            //System.load("/Users/marcoscagliosi/Documents/IntelliJ/KlugeSoftware/FarmaInvoice/lib/libchilkat.jnilib");
            System.load(pathFile);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public DecriptP7m(){}


    /**
     * @param pathDirP7m: directory dei file p7m;
     * @param pathDirXml: directory dove vengono scompattati gli xml.
     */
    public void decriptFilesP7m(String pathDirP7m, String pathDirXml){

        CkCrypt2 decript = new CkCrypt2();

        boolean success = decript.UnlockComponent("MCSCGL.CB1122019_MJVdtXWC8Rm8");
        if (success != true) {
            System.out.println(decript.lastErrorText());
            return;
        }

        File dirFileP7m = new File(pathDirP7m);
        if (!dirFileP7m.isDirectory() || !dirFileP7m.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Errore nella lettura della directory !");
            alert.setContentText("Il percorso " + pathDirP7m + " non è una directory oppure non esiste !");
            alert.showAndWait();
        } else {
            File[] filesFatture = dirFileP7m.listFiles();
            if (filesFatture.length == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Directory vuota !");
                alert.setContentText("Nella directory " + pathDirP7m + " non sono presenti file xml da leggere !");
                alert.showAndWait();
            } else {
                for (int i = 0; i < filesFatture.length; i++) {
                    if (filesFatture[i].isFile() && filesFatture[i].getName().toLowerCase().contains("p7m")) {
                        String temp = filesFatture[i].getName().toLowerCase();
                        String[] temp2 = temp.split(".xml.p7m");
                        success = decript.VerifyP7M(pathDirP7m + filesFatture[i].getName(), pathDirXml + temp2[0] + ".xml");
                        if (success == false) {
                            System.out.println(decript.lastErrorText());
                            return;
                        }
                    }
                }
            }
        }

    }

    public static void main(String[] args)
    {
        DecriptP7m decr = new DecriptP7m();
        decr.decriptFilesP7m("./resources/examples/p7m/","./resources/examples/xml/");
    }

}
