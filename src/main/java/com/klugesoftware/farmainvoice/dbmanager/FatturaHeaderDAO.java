package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.FatturaHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class FatturaHeaderDAO {

    private final Logger logger = LogManager.getLogger(FatturaHeaderDAO.class.getName());
    private DAOFactory daoFactory;

    private static final String SQL_INSERT = "INSERT INTO fatture_header (" +
            "idFattura,idAnagrafica,tipoFattura,numeroFattura,dataFattura,importoTotale,causale,ibanPagamento,statoPagamento,tipoPagamento,notePagamento,nomeFile) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE fatture_header SET " +
            "idAnagrafica = ?,tipoFattura = ?,numeroFattura = ?,dataFattura = ?,importoTotale = ?,causale = ?,ibanPagamento = ?,statoPagamento = ?," +
            "tipoPagamento = ?,notePagamento = ?,nomeFile = ?" +
            "WHERE idFattura = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM fatture_header ";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM fatture_header WHERE idFattura = ?";
    private static final String SQL_FIND_BY_ID_ANAGRAFICA = "SELECT * FROM fatture_header WHERE idAnagrafica = ?";
    private static final String SQL_EMPTY_TABLE = "DELETE FROM fatture_header ";
    private static final String SQL_RESET_AUTOINCREMENT = "ALTER TABLE fatture_header AUTO_INCREMENT = 1";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM fatture_header WHERE idFattura = ?";

    public FatturaHeaderDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    private FatturaHeader find(String sql, Object...values){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FatturaHeader fatturaHeader = new FatturaHeader();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null) {
                if(resultSet.next()){
                    fatturaHeader = DAOUtil.mapFatturaHeader(resultSet);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally {
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return fatturaHeader;
    }

    private ArrayList<FatturaHeader> findList(String sql, Object...values){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<FatturaHeader> elenco = new ArrayList<FatturaHeader>();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                elenco.add(DAOUtil.mapFatturaHeader(resultSet));
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return elenco;
    }


    public FatturaHeader create(FatturaHeader fatturaHeader){

        if(fatturaHeader.getIdFattura() != null){
            throw new IllegalArgumentException("Record già presente! ");
        }

        Object[] values = {
                fatturaHeader.getIdFattura(),
                fatturaHeader.getIdAnagrafica(),
                fatturaHeader.getTipoFattura().toString(),
                fatturaHeader.getNumeroFattura(),
                fatturaHeader.getDataFattura(),
                fatturaHeader.getImportoTotale(),
                fatturaHeader.getCausale(),
                fatturaHeader.getIbanPagamento(),
                fatturaHeader.getStatoPagamento(),
                fatturaHeader.getTipoPagamento(),
                fatturaHeader.getNotePagamento(),
                fatturaHeader.getNomeFile()
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
                fatturaHeader.setIdFattura(new Integer(generetedKey.getInt(1)));
            }else{
                throw new SQLException("La creazione di un nuovo record non è andata a buon fine!");
            }
        }catch(SQLException ex){
            logger.error("FatturaHeaderDAO.create: I can't create new record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement, generetedKey);
        }
        return fatturaHeader;
    }

    public FatturaHeader findById(int idFattura){
        return find(SQL_FIND_BY_ID,idFattura);
    }

    public ArrayList<FatturaHeader> findAll(){
        return findList(SQL_FIND_ALL);
    }

    public ArrayList<FatturaHeader> findByIdAnagrafica(int idAnagrafica){
        return findList(SQL_FIND_BY_ID_ANAGRAFICA,idAnagrafica);
    }

    public FatturaHeader update(FatturaHeader fatturaHeader){
        if(fatturaHeader.getIdFattura() == null){
            throw new IllegalArgumentException("Id null: record non creato! ");
        }

        Object[] values = {
                fatturaHeader.getIdAnagrafica(),
                fatturaHeader.getTipoFattura().toString(),
                fatturaHeader.getNumeroFattura(),
                fatturaHeader.getDataFattura(),
                fatturaHeader.getImportoTotale(),
                fatturaHeader.getCausale(),
                fatturaHeader.getIbanPagamento(),
                fatturaHeader.getStatoPagamento(),
                fatturaHeader.getTipoPagamento(),
                fatturaHeader.getNotePagamento(),
                fatturaHeader.getNomeFile(),
                fatturaHeader.getIdFattura(),
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
            logger.error("FatturaHeaderDAO.update: I can't update record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement);
        }
        return fatturaHeader;
    }


    public boolean deleteRecordById(int idFattura){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        boolean ret = false;
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID);
            preparedStatement.setInt(1, idFattura);
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

    public void emptyTable(){
        Connection connection = null;
        Statement stmt = null;
        try{
            connection = daoFactory.getConnetcion();
            stmt = connection.createStatement();
            stmt.executeUpdate(SQL_EMPTY_TABLE);
            stmt.executeUpdate(SQL_RESET_AUTOINCREMENT);
        }catch(SQLException ex){
            logger.error("FatturaHeaderDAO.update: I can't update record...",ex);
        }finally{
            try{
                stmt.close();
                connection.close();
            }catch(SQLException ex){
                logger.error("FatturaHeaderDAO.deleteTable(): I can't close db connection");
            }
        }
    }

}
