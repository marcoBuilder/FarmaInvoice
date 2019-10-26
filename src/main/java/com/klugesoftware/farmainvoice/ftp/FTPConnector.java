package com.klugesoftware.farmainvoice.ftp;


import javafx.scene.control.Alert;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class FTPConnector {

    private final boolean DEBUG = false;
    //private final String FTP_HOST_NAME="ftp.klugesoftware.com";
    //private final String FTP_USER_NAME="farmamanager@klugesoftware.com";
    //private final String FTP_PASSWORD="builder163";
    private Logger logger = LogManager.getLogger(com.klugesoftware.farmainvoice.ftp.FTPConnector.class.getName());
    private FTPClient ftpClient;
    private FTPClientConfig ftpclientConfig;
    private String PROPERTIES_FILE = "./resources/config/configFtp.properties";
    private Properties properties;
    private String ftpHostName;
    private String ftpUser;
    private String ftpPassword;

    public FTPConnector(String ftpHostName,String ftpUser,String ftpPassword){
        this.ftpHostName = ftpHostName;
        this.ftpUser = ftpUser+"@klugesoftware.com";
        this.ftpPassword = ftpPassword;

    }

    private void initFtpClient(){
        try {

            //properties = new Properties();
            //properties.load(new FileInputStream(PROPERTIES_FILE));

            int reply;
            ftpClient = new FTPClient();
            //ftpClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            if(DEBUG)
                ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintStream(new FileOutputStream("./logs/ftp.log",true))));
            ftpclientConfig = new FTPClientConfig();
            ftpClient.configure(ftpclientConfig);
            ftpClient.connect(ftpHostName,21);
            boolean connected = ftpClient.login(ftpUser,ftpPassword);
            if(connected) {
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    logger.error("Problemi di connessione FTP Server");
                    ftpClient.disconnect();
                    logger.error("Ci sono problemi di connessione ftp.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore di Connessione");
                    alert.setHeaderText("Errore di Connessione");
                    alert.setContentText("Verificare la connessione!");
                    alert.showAndWait();
                    ftpClient = null;
                }
            }else{

                logger.error("Errore connessione. Reply Code: "+ ftpClient.getReplyCode());
                logger.error("Reply Message: "+ftpClient.getReplyString());
            }
        }catch(Exception ex){
            logger.error("Host name: "+ftpHostName+"\n");
            logger.error(ex.getMessage());
        }
    }

    public FTPClient getFtpClient(){
        initFtpClient();
        return ftpClient;
    }

    public void closeFtpClient(){
        try {
            if(ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }catch(IOException ex){
            logger.error(ex.getStackTrace());
        }
    }

    /**
     * Download a single file from FTP server
     * @param remoteFilePath path of the file on the server
     * @param savePath path of directory where the file will be stored
     * @return true if the files was downloaded successfully, false otherwise
     */
    public void downLoadSingleFile(String remoteFilePath, String savePath){
        try {
            initFtpClient();
            downloadSingleFile(remoteFilePath, savePath);
            closeFtpClient();
        }catch(IOException ex){
            logger.error(ex.getStackTrace());
        }
    }


    private boolean downloadSingleFile(String remoteFilePath, String savePath) throws IOException {
        OutputStream outputStream = null;
        try{
            File downloadFile = new File(savePath);
            File parentDir = downloadFile.getParentFile();
            if(!parentDir.exists()){
                parentDir.mkdir();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath,outputStream);
        }catch(IOException ex){
            logger.error(ex);
        }finally {
            if(outputStream != null)
                outputStream.close();
        }
        return false;
    }

    /**
     * Download a whole directrory from a FTP server.
     * @param parentDir Path of parent directory of the current directory being downloaded.
     * @param currentDir Path of the current directory being downloaded.
     * @param saveDir Path of directory where the whole remote directory will be downloaded and saved.
     * @throws IOException If any network or IO error occured.
     */
    public void downLoadDirectory(String parentDir, String currentDir, String saveDir){
        try{
            initFtpClient();
            downloadDirectory(parentDir, currentDir, saveDir);
            closeFtpClient();
        }catch (IOException ex){
            logger.error(ex.getStackTrace());
        }
    }

    private void downloadDirectory(String parentDir, String currentDir, String saveDir) throws IOException {
        String dirToList = parentDir;
        if(!currentDir.equals("")){
            dirToList += "/"+currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if(subFiles != null && subFiles.length > 0){
            for(FTPFile aFile : subFiles){
                String currentFileName = aFile.getName();
                if(currentFileName.equals(".") || currentFileName.equals("..")){
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/" +currentFileName;
                if(currentDir.equals("")){
                    filePath = parentDir + "/" + currentFileName;
                }
                String newDirPath = saveDir + parentDir + File.separator + currentDir + File.separator+ currentFileName;
                if (currentDir.equals("")) {
                    newDirPath = saveDir + parentDir + File.separator
                            + currentFileName;
                }
                if(aFile.isDirectory()){
                    //create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.mkdirs();
                    if(created)
                        logger.info("Created the director:"+newDirPath);
                    else
                        logger.info("Could not create the directory: "+newDirPath);

                    //download the sub directory
                    downloadDirectory(dirToList,currentFileName,saveDir);
                }else{
                    //download the file
                    boolean success = downloadSingleFile(filePath,newDirPath);
                    if(success){
                        logger.info("Downloaded the file: "+filePath);
                    }else
                        logger.info("Could not download the file: "+filePath);
                }
            }
        }
    }

    /**
     * Upload a single file to FTP server
     * @param fileToUpload path + nameFile to upload
     * @param remoteFile path + nameFile which upload FTP to
     */
    public boolean upLoadFile(String fileToUpload, String remoteFile){
        try {
            //initFtpClient();
            int reply = ftpClient.getReplyCode();
            String stringReply = ftpClient.getReplyString();
            if (FTPReply.isPositiveCompletion(reply)) {
                FileInputStream in = new FileInputStream(fileToUpload);
                boolean ret = ftpClient.storeFile(remoteFile,in);
                if(!ret){
                    int ret2 = ftpClient.getReplyCode();
                    stringReply = ftpClient.getReplyString();
                    logger.warn("Upload non riuscito!!!");
                    logger.warn("replyCode: "+ret2);
                    logger.warn(stringReply);
                }
                in.close();
                //closeFtpClient();
                return ret;
            }
        }catch(Exception ex){
            logger.error(ex.getStackTrace());
        }
        return false;
    }



    /**
     * Delete file and directory of folder update on FTP
     * @param path viene passato il seguente path: "/FarmaManagerApp/update
     * @return boolean se nella directory non esistono altri file, ad eccezione delle hidden directory
     */
    public boolean deleteFile(String path){
        boolean ret = true;
        try {
            initFtpClient();
            ftpClient.doCommand("CWD", path);
            FTPFile[] files = ftpClient.listFiles();
            if(files.length > 0){
                for (FTPFile item : files){
                    if (item.getName().equals(".") || item.getName().equals("..") || item.isDirectory()){
                        ;
                    }else{
                        ftpClient.deleteFile(item.getName());
                    }
                }
            }
        }catch(IOException ex){
            logger.error(ex.getMessage());
            ret = false;
        }finally {
            closeFtpClient();
            return ret;
        }
    }

    private void deleteDirectory(String path){

        try {
            ftpClient.doCommand("CWD", path);
            FTPFile[] files = ftpClient.listFiles();
            for(FTPFile item : files){
                if (item.getName().equals(".") || item.getName().equals("..")){
                    ;
                }else{
                    if (item.isDirectory()){
                        deleteDirectory(path+"/"+item.getName());
                        ftpClient.removeDirectory(path+"/"+item.getName());
                    }else{
                        ftpClient.deleteFile(item.getName());
                    }
                }
            }
        }catch(IOException ex){
            logger.error(ex.getMessage());
        }
    }

    /**
     * Verifica la directory /FarmaManagerApp/update
     * @return boolean true se la directory è vuota
     */
    public boolean isEmptyFolderUpdate(){
        boolean ret = true;
        try {
            initFtpClient();
            //ftpClient.cwd(properties.getProperty("remotePath")+"/update");
            ftpClient.cwd("/FarmaManagerApp/update/");
            FTPFile[] files = ftpClient.listFiles();
            for(FTPFile item : files){
                if(item.getName().equals(".") || item.getName().equals("..")){
                    ;
                }else
                    ret = false;
            }
        }catch(IOException ex){
            logger.error(ex.getMessage());
        }finally {
            closeFtpClient();
            return ret;
        }
    }

    /**
     * Create direcories on KlugeSoftware FTP account
     * @return true if it's ok or false it's wrong
     */
    public boolean makeDirectory(){
        boolean ret = false;
        try {
            initFtpClient();
            ret = ftpClient.makeDirectory("/xmlPassive");
            ret = ftpClient.makeDirectory("/xmlAttive");
            ret = ftpClient.makeDirectory("/backup");
            ret = ftpClient.changeWorkingDirectory("/backup");
            ret = ftpClient.makeDirectory("xmlPassive");
            ret = ftpClient.changeWorkingDirectory("/backup");
            ret = ftpClient.makeDirectory("xmlAttive");
            closeFtpClient();
        }catch(IOException ex){
            logger.error(ex);
            ret = false;
        }finally {
            return ret;
        }
    }


    public static void main(String[] args){

        /*
        try {
            ArrayList<String> account = new ArrayList<>();
            File fileCsv = new File("./resources/ElencoClientiPartitaIvaDigithera.csv");
            BufferedReader br = new BufferedReader(new FileReader(fileCsv));
            String line;
            String[] temp;
            while( (line = br.readLine())!= null ){
                temp = line.split(";");
                account.add(temp[1]);
            }
            FTPConnector ftpConnector;
            Iterator<String> iter = account.iterator();
            while(iter.hasNext()){
                String usr = iter.next()+"@klugesoftware.com";
                ftpConnector = new FTPConnector("ftp.klugesoftware.com", usr, "FatturEl01!");
                ftpConnector.makeDirectory();
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        */
    }
}
