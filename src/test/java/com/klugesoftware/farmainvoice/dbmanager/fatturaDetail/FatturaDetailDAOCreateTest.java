package com.klugesoftware.farmainvoice.dbmanager.fatturaDetail;

import com.klugesoftware.farmainvoice.dbmanager.DAOFactory;
import com.klugesoftware.farmainvoice.dbmanager.FatturaDetailDAOManager;
import com.klugesoftware.farmainvoice.dbmanager.FatturaHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.FatturaDetail;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FatturaDetailDAOCreateTest {

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
    void create() {
        FatturaDetail fatturaDetail = new FatturaDetail();
        fatturaDetail.setIdFattura(1);
        fatturaDetail.setNumeroLinea(1);
        fatturaDetail.setDescrizione("prima linea");
        fatturaDetail.setCodiceArticolo("ean 123456");
        fatturaDetail.setQuantita(12);
        fatturaDetail.setPrezzoUnitario(new BigDecimal(12345));
        fatturaDetail.setPrezzoTotale(new BigDecimal(99999));
        fatturaDetail.setAliquotaIva("04");

        fatturaDetail = FatturaDetailDAOManager.insert(fatturaDetail);
        assertNotNull(fatturaDetail.getIdFattura());

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