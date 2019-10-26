package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.FatturaHeader;

import java.util.ArrayList;

public class FatturaHeaderDAOManager {

    public static FatturaHeader insert(FatturaHeader fatturaHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.create(fatturaHeader);
    }

    public static FatturaHeader modifica(FatturaHeader fatturaHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.update(fatturaHeader);
    }

    public static FatturaHeader findById(Integer idFatturaHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.findById(idFatturaHeader);
    }

    public static ArrayList<FatturaHeader> findByIdAnagrafica(Integer idAnagrafica){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.findByIdAnagrafica(idAnagrafica);
    }

    public static ArrayList<FatturaHeader> findAll(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.findAll();
    }

    public static boolean deleteFatturaHeader(int idFatturaHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        return fatturaHeaderDAO.deleteRecordById(idFatturaHeader);
    }

    public static void svuotaTable(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaHeaderDAO fatturaHeaderDAO = daoFactory.getFatturaHeaderDAO();
        fatturaHeaderDAO.emptyTable();
    }

}
