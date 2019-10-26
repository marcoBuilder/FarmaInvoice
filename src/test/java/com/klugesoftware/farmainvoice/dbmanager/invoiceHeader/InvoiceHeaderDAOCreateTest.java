package com.klugesoftware.farmainvoice.dbmanager.invoiceHeader;

import com.klugesoftware.farmainvoice.dbmanager.InvoiceHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.InvoiceHeader;
import com.klugesoftware.farmainvoice.model.TipoDocumento;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class InvoiceHeaderDAOCreateTest {


    @Test
    void create1() {

        InvoiceHeader invoiceHeader = new InvoiceHeader();
        invoiceHeader.setTipoDocumento(TipoDocumento.FATTURA_ATTIVA);
        invoiceHeader.setDenominazione("Cifarma srl");
        invoiceHeader.setPartitaIva("01600880718");
        invoiceHeader.setCodiceFiscale("SCGMRC73B12L219L");
        invoiceHeader.setDataEmissione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setDataInvioRicezione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setImporto(new BigDecimal(12345.68));
        invoiceHeader.setVisualizzato(false);
        invoiceHeader.setInviato(false);
        invoiceHeader.setNomeFileXml("01600880718_001.xml");
        invoiceHeader.setTipoFattura(TipoFattura.ATTIVA);

        invoiceHeader = InvoiceHeaderDAOManager.insert(invoiceHeader);

        assertEquals(new Integer(1),invoiceHeader.getIdInvoice());
    }

    @Test
    void create2() {

        InvoiceHeader invoiceHeader = new InvoiceHeader();
        invoiceHeader.setTipoDocumento(TipoDocumento.FATTURA_ATTIVA);
        invoiceHeader.setDenominazione("Cifarma srl");
        invoiceHeader.setPartitaIva("01600880719");
        invoiceHeader.setCodiceFiscale("SCGMRC73B12L219L");
        invoiceHeader.setDataEmissione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setDataInvioRicezione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setImporto(new BigDecimal(12345.68));
        invoiceHeader.setVisualizzato(false);
        invoiceHeader.setInviato(false);
        invoiceHeader.setNomeFileXml("01600880718_002.xml");
        invoiceHeader.setTipoFattura(TipoFattura.ATTIVA);

        invoiceHeader = InvoiceHeaderDAOManager.insert(invoiceHeader);

        assertEquals(new Integer(2),invoiceHeader.getIdInvoice());
    }

}
