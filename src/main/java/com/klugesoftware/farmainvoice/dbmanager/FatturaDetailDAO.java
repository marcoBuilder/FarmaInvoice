package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.FatturaDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;

public class FatturaDetailDAO {

    private final Logger logger = LogManager.getLogger(FatturaDetailDAO.class.getName());
    private DAOFactory daoFactory;

    private static final String SQL_INSERT = "INSERT INTO fatture_detail (" +
            "idDetail,idFattura,numeroLinea,descrizione,codiceArticolo,quantita,prezzoUnitario,prezzoTotale,aliquotaIva) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE fatture_detail  SET " +
            "idFattura = ?,numeroLinea = ?,descrizione = ?,codiceArticolo = ?,quantita = ?,prezzoUnitario = ?,prezzoTotale = ?,aliquotaIva = ?" +
            "WHERE idDetail = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM fatture_detail ";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM fatture_detail WHERE idDetail = ?";
    private static final String SQL_FIND_BY_ID_HEADER = "SELECT * FROM fatture_detail WHERE idFattura = ?";
    private static final String SQL_EMPTY_TABLE = "DELETE FROM fatture_detail ";
    private static final String SQL_RESET_AUTOINCREMENT = "ALTER TABLE fatture_detail AUTO_INCREMENT = 1";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM fatture_detail WHERE ";

    public FatturaDetailDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    private FatturaDetail find(String sql, Object...values){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FatturaDetail fatturaDetail = new FatturaDetail();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null) {
                if(resultSet.next()){
                    fatturaDetail = DAOUtil.mapFatturaDetail(resultSet);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally {
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return fatturaDetail;
    }

    private ArrayList<FatturaDetail> findList(String sql, Object...values){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<FatturaDetail> elenco = new ArrayList<FatturaDetail>();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                elenco.add(DAOUtil.mapFatturaDetail(resultSet));
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return elenco;
    }


    public FatturaDetail create(FatturaDetail fatturaDetail){

        if(fatturaDetail.getIdDetail() != null){
            throw new IllegalArgumentException("Record già presente! ");
        }

        Object[] values = {
                fatturaDetail.getIdDetail(),
                fatturaDetail.getIdFattura(),
                fatturaDetail.getNumeroLinea(),
                fatturaDetail.getDescrizione(),
                fatturaDetail.getCodiceArticolo(),
                fatturaDetail.getQuantita(),
                fatturaDetail.getPrezzoUnitario(),
                fatturaDetail.getPrezzoTotale(),
                fatturaDetail.getAliquotaIva()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generetedKey = null;
        try{
            connection = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0){
                throw new SQLException("La creazione  di un nuovo record non è andata a buon fine!");
            }
            generetedKey = preparedStatement.getGeneratedKeys();
            if(generetedKey.next()){
                fatturaDetail.setIdDetail(generetedKey.getInt(1));
            }else{
                throw new SQLException("La creazione di un nuovo record non è andata a buon fine!");
            }
        }catch(SQLException ex){
            logger.error("FatturaDetailDAO.create: I can't create new record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement, generetedKey);
        }
        return fatturaDetail;
    }

    public FatturaDetail findById(int idDetail){
        return find(SQL_FIND_BY_ID,idDetail);
    }

    public ArrayList<FatturaDetail> findAll(){
        return findList(SQL_FIND_ALL);
    }

    public ArrayList<FatturaDetail> findByIdFatturaHeader(int idFatturaHeader){
        return findList(SQL_FIND_BY_ID_HEADER,idFatturaHeader);
    }

    public FatturaDetail update(FatturaDetail fatturaDetail){
        if(fatturaDetail.getIdDetail() == null){
            throw new IllegalArgumentException("Id null: record non creato! ");
        }

        Object[] values = {
                fatturaDetail.getIdFattura(),
                fatturaDetail.getNumeroLinea(),
                fatturaDetail.getDescrizione(),
                fatturaDetail.getCodiceArticolo(),
                fatturaDetail.getQuantita(),
                fatturaDetail.getPrezzoUnitario(),
                fatturaDetail.getPrezzoTotale(),
                fatturaDetail.getAliquotaIva(),
                fatturaDetail.getIdDetail(),
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(connection, SQL_UPDATE, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("La modifica  non è andata a buon fine: non è stato modificato nessun record.");

        }catch(SQLException ex){
            logger.error("FatturaDetailDAO.update: I can't update record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement);
        }
        return fatturaDetail;
    }


    public boolean deleteRecordById(int idDetail){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        boolean ret = false;
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID);
            preparedStatement.setInt(1, idDetail);
            int affestedRows = preparedStatement.executeUpdate();
            if(affestedRows == 0){
                throw new SQLException("La cancellazione  non è andata a buon fine!");
            }
            ret = true;
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement);
        }
        return ret;

    }

    public void emptyTable(){
        Connection connection = null;
        Statement stmt = null;
        try{
            connection = daoFactory.getConnetcion();
            stmt = connection.createStatement();
            stmt.executeUpdate(SQL_EMPTY_TABLE);
            stmt.executeUpdate(SQL_RESET_AUTOINCREMENT);
        }catch(SQLException ex){
            logger.error("FatturaDetailDAO.update: I can't update record...",ex);
        }finally{
            try{
                stmt.close();
                connection.close();
            }catch(SQLException ex){
                logger.error("FatturaDetailDAO.deleteTable(): I can't close db connection");
            }
        }
    }


}
