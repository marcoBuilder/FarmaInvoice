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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FatturaDetailDAOUpdateTest {

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
    void update() {

        FatturaDetail fatturaDetail = new FatturaDetail();
        fatturaDetail.setIdFattura(1);
        fatturaDetail.setNumeroLinea(2);
        fatturaDetail.setDescrizione("seconda linea fattura");
        fatturaDetail.setCodiceArticolo("ean 123456");
        fatturaDetail.setQuantita(5);
        fatturaDetail.setPrezzoUnitario(new BigDecimal(12345));
        fatturaDetail.setPrezzoTotale(new BigDecimal(12345));
        fatturaDetail.setAliquotaIva("10");

        fatturaDetail = FatturaDetailDAOManager.insert(fatturaDetail);
        assertNotNull(fatturaDetail.getIdDetail());

        fatturaDetail.setPrezzoUnitario(new BigDecimal(54321));
        fatturaDetail.setDescrizione("linea modificata");

        fatturaDetail = FatturaDetailDAOManager.modifica(fatturaDetail);
        fatturaDetail = FatturaDetailDAOManager.findById(2);

        assertEquals(new BigDecimal(54321),fatturaDetail.getPrezzoUnitario());
        assertEquals("linea modificata",fatturaDetail.getDescrizione());

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