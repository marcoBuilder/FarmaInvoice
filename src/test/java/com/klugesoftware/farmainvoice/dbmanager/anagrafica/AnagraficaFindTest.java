package com.klugesoftware.farmainvoice.dbmanager.anagrafica;

import com.klugesoftware.farmainvoice.dbmanager.AnagraficaDAOManager;
import com.klugesoftware.farmainvoice.dbmanager.DAOFactory;
import com.klugesoftware.farmainvoice.model.Anagrafica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AnagraficaFindTest {

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
    void findAnagraficaById(){
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setIdAnagrafica(1);
        anagrafica = AnagraficaDAOManager.findById(anagrafica.getIdAnagrafica());

        assertEquals("prova srl",anagrafica.getDenominazione());
        assertEquals("01600880718",anagrafica.getPartitaIva());
    }

    @Test
    void findAnagraficaByPartitaIva(){
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setPartitaIva("01600880718");
        anagrafica = AnagraficaDAOManager.findByPartitaIva(anagrafica.getPartitaIva());

        assertEquals("prova srl",anagrafica.getDenominazione());
        assertEquals("01600880718",anagrafica.getPartitaIva());
    }

    @Test
    void elencoAnagrafiche(){
        assertEquals(2,AnagraficaDAOManager.findAll().size());
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