package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.InvoiceHeader;

import java.util.ArrayList;

public class InvoiceHeaderDAOManager {

    public static InvoiceHeader insert(InvoiceHeader invoiceHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.create(invoiceHeader);
    }

    public static InvoiceHeader modifica(InvoiceHeader invoiceHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.update(invoiceHeader);
    }

    public static InvoiceHeader cercaByIdInvoice(Integer idInvoice){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.findById(idInvoice);
    }

    public static ArrayList<InvoiceHeader> listAll(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.findAll();
    }

    public static boolean deleteById(Integer idInvoice){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.deleteRecordById(idInvoice);
    }

    public static boolean svuotaTable(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        InvoiceHeaderDAO invoiceHeaderDAO = daoFactory.getInvoiceHederDAO();
        return invoiceHeaderDAO.deleteTable();
    }

}
