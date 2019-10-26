package com.klugesoftware.farmainvoice.dbmanager.invoiceHeader;

import com.klugesoftware.farmainvoice.dbmanager.InvoiceHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.InvoiceHeader;
import com.klugesoftware.farmainvoice.model.TipoDocumento;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvoiceHeaderDAOFindTest {


    @Test
    void findById() {

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

        InvoiceHeader invoiceHeaderFound = InvoiceHeaderDAOManager.cercaByIdInvoice(1);

        assertEquals(invoiceHeader.getDenominazione(),invoiceHeaderFound.getDenominazione());
        assertEquals(invoiceHeader.getCodiceFiscale(),invoiceHeaderFound.getCodiceFiscale());
    }

}
