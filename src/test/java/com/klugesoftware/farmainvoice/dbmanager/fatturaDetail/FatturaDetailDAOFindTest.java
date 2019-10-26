package com.klugesoftware.farmainvoice.dbmanager.fatturaDetail;

import com.klugesoftware.farmainvoice.dbmanager.DAOFactory;
import com.klugesoftware.farmainvoice.dbmanager.FatturaDetailDAOManager;
import com.klugesoftware.farmainvoice.dbmanager.FatturaHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.FatturaDetail;
import com.klugesoftware.farmainvoice.model.FatturaHeader;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FatturaDetailDAOFindTest {

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
        FatturaDetail fatturaDetail = new FatturaDetail();
        fatturaDetail.setIdDetail(1);
        fatturaDetail = FatturaDetailDAOManager.findById(fatturaDetail.getIdDetail());

        assertEquals("ean 123456",fatturaDetail.getCodiceArticolo());
        assertEquals(new Integer(1),fatturaDetail.getNumeroLinea());

    }


    @Test
    void findAll() {
        assertEquals(2,FatturaDetailDAOManager.findAll().size());
    }

    @Test
    void findByIdAnagrafica() {
        assertEquals(2,FatturaDetailDAOManager.findByIdFatturaHeader(1).size());
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