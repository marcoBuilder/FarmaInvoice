package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.Anagrafica;

import java.util.ArrayList;

public class AnagraficaDAOManager {

    public static Anagrafica insert(Anagrafica anagrafica){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.create(anagrafica);
    }

    public static Anagrafica modifica(Anagrafica anagrafica){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.update(anagrafica);
    }

    public static Anagrafica findById(Integer idAnagrafica){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.findById(idAnagrafica);
    }

    public static Anagrafica findByPartitaIva(String partitaIva){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.findByPartitaIva(partitaIva);
    }

    public static ArrayList<Anagrafica> findAll(){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.findAll();
    }

    public static boolean deleteAnagrafica(Anagrafica anagrafica){
        DAOFactory daoFactory = DAOFactory.getInstance();
        AnagraficaDAO anagraficaDAO = daoFactory.getAnagraficaDAO();
        return anagraficaDAO.deleteRecordById(anagrafica.getIdAnagrafica());
    }


}
