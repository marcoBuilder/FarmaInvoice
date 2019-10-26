package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.FatturaDetail;

import java.util.ArrayList;

public class FatturaDetailDAOManager {

    public static FatturaDetail insert(FatturaDetail fatturaDetail){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.create(fatturaDetail);
    }

    public static FatturaDetail modifica(FatturaDetail fatturaDetail){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.update(fatturaDetail);
    }

    public static FatturaDetail findById(Integer idFatturaDetail){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.findById(idFatturaDetail);
    }

    public static ArrayList<FatturaDetail> findByIdFatturaHeader(Integer idFatturaHeader){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.findByIdFatturaHeader(idFatturaHeader);
    }

    public static ArrayList<FatturaDetail> findAll(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.findAll();
    }

    public static boolean deleteFatturaDetail(int idFatturaDetail){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        return FatturaDetailDAO.deleteRecordById(idFatturaDetail);
    }

    public static void svuotaTable(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        FatturaDetailDAO FatturaDetailDAO = daoFactory.getFatturaDetailDAO();
        FatturaDetailDAO.emptyTable();
    }

}
