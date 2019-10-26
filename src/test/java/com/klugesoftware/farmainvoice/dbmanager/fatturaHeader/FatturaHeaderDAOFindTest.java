package com.klugesoftware.farmainvoice.dbmanager.fatturaHeader;

import com.klugesoftware.farmainvoice.dbmanager.DAOFactory;
import com.klugesoftware.farmainvoice.dbmanager.FatturaHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.FatturaHeader;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class FatturaHeaderDAOFindTest {

    private String dbUrl;

    @BeforeEach
    void setUp() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(DAOFactory.PROPERTIES_FILE_NAME));
            dbUrl = prop.getProperty("dbUrl");
            prop.setProperty("dbUrl","jdbc:mysql://localhost:3306/FarmaInvoiceUnitTest");
            prop.store(new FileOutputStream(DAOFactory.PROPERTIES_FILE_NAME),null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Test
    void findById() {
        FatturaHeader fatturaHeader = new FatturaHeader();
        fatturaHeader = FatturaHeaderDAOManager.findById(1);
        assertEquals("1E",fatturaHeader.getNumeroFattura());
        assertEquals(TipoFattura.ATTIVA,fatturaHeader.getTipoFattura());
        assertEquals("01600880718_01.xml",fatturaHeader.getNomeFile());
    }


    @Test
    void findAll() {
        assertEquals(2,FatturaHeaderDAOManager.findAll().size());
    }

    @Test
    void findByIdAnagrafica() {
        assertEquals(2,FatturaHeaderDAOManager.findByIdAnagrafica(1).size());
    }



    @AfterEach
    void tearDown() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(DAOFactory.PROPERTIES_FILE_NAME));
            prop.setProperty("dbUrl",dbUrl);
            prop.store(new FileOutputStream(DAOFactory.PROPERTIES_FILE_NAME),null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}