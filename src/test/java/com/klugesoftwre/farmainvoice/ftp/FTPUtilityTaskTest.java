package com.klugesoftwre.farmainvoice.ftp;

import com.klugesoftware.farmainvoice.ftp.FTPDownloaderTask;
import com.klugesoftware.farmainvoice.ftp.FtpPath;
import org.junit.jupiter.api.Test;

public class FTPUtilityTaskTest {

    @Test
    void ftpUtlityTaskTest(){
        String FTP_HOST_NAME="ftp.klugesoftware.com";
        String FTP_USER_NAME="farmamanager@klugesoftware.com";
        String FTP_PASSWORD="builder163";
        FTPDownloaderTask task = new FTPDownloaderTask(FtpPath.REMOTE_PATH_DIGITHERA_FATTURE_PASSIVE,"","./resources/examples/xmlIn/",FTP_HOST_NAME,FTP_USER_NAME,FTP_PASSWORD);
        //TODO: ...da qui....
    }
}
