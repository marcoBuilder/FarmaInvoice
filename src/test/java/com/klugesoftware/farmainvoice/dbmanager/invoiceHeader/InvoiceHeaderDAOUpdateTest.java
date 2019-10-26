package com.klugesoftware.farmainvoice.dbmanager.invoiceHeader;

import com.klugesoftware.farmainvoice.dbmanager.InvoiceHeaderDAOManager;
import com.klugesoftware.farmainvoice.model.InvoiceHeader;
import com.klugesoftware.farmainvoice.model.TipoDocumento;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.utility.DateUtility;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvoiceHeaderDAOUpdateTest {


    @Test
    void update() {

        InvoiceHeader invoiceHeader = InvoiceHeaderDAOManager.cercaByIdInvoice(1);
        invoiceHeader.setTipoDocumento(TipoDocumento.FATTURA_PASSIVA);
        invoiceHeader.setDenominazione("Cifarma 2.0 srl");
        invoiceHeader.setPartitaIva("01600880719");
        invoiceHeader.setCodiceFiscale("SCGMRC73B12L219L");
        invoiceHeader.setDataEmissione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setDataInvioRicezione(DateUtility.converteGUIStringDDMMYYYYToDate("12/02/2018"));
        invoiceHeader.setImporto(new BigDecimal(99999.99));
        invoiceHeader.setVisualizzato(true);
        invoiceHeader.setInviato(true);
        invoiceHeader.setNomeFileXml("01600880718_001.xml");
        invoiceHeader.setTipoFattura(TipoFattura.PASSIVA);

        invoiceHeader = InvoiceHeaderDAOManager.modifica(invoiceHeader);

        InvoiceHeader invoiceHeaderFound = InvoiceHeaderDAOManager.cercaByIdInvoice(invoiceHeader.getIdInvoice());

        assertEquals(invoiceHeader.getPartitaIva(),invoiceHeaderFound.getPartitaIva());
    }

}
