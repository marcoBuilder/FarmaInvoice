package com.klugesoftware.farmainvoice.ftp;

import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class FTPDownloaderTask extends Task {

    boolean wholeDirectory = false;
    private String parentDir;
    private String currentDir;
    private String saveDir;
    private String ftpHostName;
    private String ftpUser;
    private String ftpPassword;

    /**
     * Download a whole directrory from a FTP server.
     * @param parentDir Path of parent directory of the current directory being downloaded.
     * @param currentDir Path of the current directory being downloaded.
     * @param saveDir Path of directory where the whole remote directory will be downloaded and saved.
     */
    public FTPDownloaderTask(String parentDir, String currentDir, String saveDir, String ftpHostName, String ftpUser, String ftpPassword){
        this.ftpHostName = ftpHostName;
        this.ftpUser = ftpUser;
        this.ftpPassword = ftpPassword;
        this.parentDir = parentDir;
        this.currentDir = currentDir;
        this.saveDir = saveDir;
        wholeDirectory = true;
    }

    @Override
    protected Object call() throws Exception {
        //FIXME: bisogna passare le credenziali....
        FTPConnector ftpConnector = new FTPConnector(ftpHostName,ftpUser,ftpPassword);
        updateMessage("Sto scaricando l'aggiornamento...");
        File toSaveDir = new File(saveDir);
        if(toSaveDir.exists() && toSaveDir.isDirectory())
            FileUtils.deleteDirectory(toSaveDir);

        if(wholeDirectory)
            ftpConnector.downLoadDirectory(parentDir,currentDir,saveDir);

        updateMessage("Operazione terminata");

        if(isCancelled()){
            updateMessage("Operazione annullata!");
            return null;
        }

        return null;
    }
}
