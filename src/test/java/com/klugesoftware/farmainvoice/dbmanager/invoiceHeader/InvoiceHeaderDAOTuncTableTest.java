package com.klugesoftware.farmainvoice.dbmanager.invoiceHeader;

import com.klugesoftware.farmainvoice.dbmanager.InvoiceHeaderDAOManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvoiceHeaderDAOTuncTableTest {


    @Test
    void truncate() {

        assertTrue(InvoiceHeaderDAOManager.svuotaTable());


    }

}
