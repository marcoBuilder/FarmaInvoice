package com.klugesoftware.farmainvoice.dbmanager;


import com.klugesoftware.farmainvoice.model.Anagrafica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class AnagraficaDAO {

    private final Logger logger = LogManager.getLogger(AnagraficaDAO.class.getName());
    private DAOFactory daoFactory;

    private static final String SQL_INSERT = "INSERT INTO anagrafica (" +
            "idAnagrafica,denominazione,tipoAnagrafica,partitaIva,codiceFiscale,indirizzo,cap,comune,nazione) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE anagrafica SET " +
            "denominazione = ?,tipoAnagrafica = ?,partitaIva = ?,codiceFiscale = ?,indirizzo = ?,cap = ?,comune = ?,nazione = ? " +
            "WHERE idAnagrafica = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM anagrafica ";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM anagrafica WHERE idAnagrafica = ?";
    private static final String SQL_FIND_BY_PARTITA_IVA = "SELECT * FROM anagrafica WHERE partitaIva = ?";
    private static final String SQL_TRUNCATE = "TRUNCATE TABLE anagrafica ";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM anagrafica WHERE idAnagrafica = ?";

    public AnagraficaDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    private Anagrafica find(String sql,Object...values){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Anagrafica anagrafica = new Anagrafica();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null) {
                if(resultSet.next()){
                    anagrafica = DAOUtil.mapAnagrafica(resultSet);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally {
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return anagrafica;
    }

    private ArrayList<Anagrafica> findList(String sql, Object...values){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Anagrafica> elenco = new ArrayList<Anagrafica>();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                elenco.add(DAOUtil.mapAnagrafica(resultSet));
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return elenco;
    }


    public Anagrafica create(Anagrafica anagrafica){

        if(anagrafica.getIdAnagrafica() != null){
            throw new IllegalArgumentException("Record già presente! ");
        }

        Object[] values = {

                anagrafica.getIdAnagrafica(),
                anagrafica.getDenominazione(),
                anagrafica.getTipoAnagrafica().toString(),
                anagrafica.getPartitaIva(),
                anagrafica.getCodiceFiscale(),
                anagrafica.getIndirizzo(),
                anagrafica.getCap(),
                anagrafica.getComune(),
                anagrafica.getNazione()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generetedKey = null;
        try{
            connection = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0){
                throw new SQLException("La creazione di un nuovo record non è andata a buon fine!");
            }
            generetedKey = preparedStatement.getGeneratedKeys();
            if(generetedKey.next()){
                anagrafica.setIdAnagrafica(new Integer(generetedKey.getInt(1)));
            }else{
                throw new SQLException("La creazione di un nuovo record non è andata a buon fine!");
            }
        }catch(SQLException ex){
            logger.error("AnagraficaDAO.create: I can't create new record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement, generetedKey);
        }
        return anagrafica;
    }

    public Anagrafica findById(int idAnagrafica){
        return find(SQL_FIND_BY_ID,idAnagrafica);
    }

    public Anagrafica findByPartitaIva(String partitaIva){
        return find(SQL_FIND_BY_PARTITA_IVA,partitaIva);
    }

    public ArrayList<Anagrafica> findAll(){
        return findList(SQL_FIND_ALL);
    }

    public Anagrafica update(Anagrafica anagrafica){
        if(anagrafica.getIdAnagrafica() == null){
            throw new IllegalArgumentException("Id null: record non creato! ");
        }

        Object[] values = {
                anagrafica.getDenominazione(),
                anagrafica.getTipoAnagrafica().toString(),
                anagrafica.getPartitaIva(),
                anagrafica.getCodiceFiscale(),
                anagrafica.getIndirizzo(),
                anagrafica.getCap(),
                anagrafica.getComune(),
                anagrafica.getNazione(),
                anagrafica.getIdAnagrafica()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(connection, SQL_UPDATE, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("La modifica non è andata a buon fine: non è stato modificato nessun record.");

        }catch(SQLException ex){
            logger.error("AnagraficaDAO.update: I can't update record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement);
        }
        return anagrafica;
    }

    public boolean deleteRecordById(int idAnagrafica){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        boolean ret = false;
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID);
            preparedStatement.setInt(1, idAnagrafica);
            int affestedRows = preparedStatement.executeUpdate();
            if(affestedRows == 0){
                throw new SQLException("La cancellazione non è andata a buon fine!");
            }
            ret = true;
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement);
        }
        return ret;

    }

    public void deleteTable(){
        Connection connection = null;
        Statement stmt = null;
        try{
            connection = daoFactory.getConnetcion();
            stmt = connection.createStatement();
            stmt.executeUpdate(SQL_TRUNCATE);
        }catch(SQLException ex){
            logger.error("AnagraficaDAO.update: I can't update record...",ex);
        }finally{
            try{
                stmt.close();
                connection.close();
            }catch(SQLException ex){
                logger.error("AnagraficaDAO.deleteTable(): I can't close db connection");
            }
        }
    }

}
