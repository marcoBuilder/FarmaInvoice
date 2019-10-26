package com.klugesoftware.farmainvoice.dbmanager.invoiceHeader;

import com.klugesoftware.farmainvoice.dbmanager.InvoiceHeaderDAOManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceHeaderDAODeleteByIdTest {


    @Test
    void deleteById() {

        assertTrue(InvoiceHeaderDAOManager.deleteById(1));

        assertNull(InvoiceHeaderDAOManager.cercaByIdInvoice(1).getIdInvoice());


    }

}
