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

class FatturaHeaderDAOCreateTest {

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
        FatturaHeader fatturaHeader = new FatturaHeader();
        fatturaHeader.setIdAnagrafica(1);
        fatturaHeader.setTipoFattura(TipoFattura.ATTIVA);
        fatturaHeader.setNumeroFattura("1E");
        fatturaHeader.setDataFattura(DateUtility.converteGUIStringDDMMYYYYToDate("12/12/2018"));
        fatturaHeader.setImportoTotale(new BigDecimal(123456));
        fatturaHeader.setCausale("test inserimanto");
        fatturaHeader.setIbanPagamento("IBAN ....");
        fatturaHeader.setStatoPagamento("da pagare");
        fatturaHeader.setTipoPagamento("tipo pagamento...");
        fatturaHeader.setNotePagamento("note fattura...");
        fatturaHeader.setNomeFile("01600880718_01.xml");

        fatturaHeader = FatturaHeaderDAOManager.insert(fatturaHeader);
        assertNotNull(fatturaHeader.getIdFattura());

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