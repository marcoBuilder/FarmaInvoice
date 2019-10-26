package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.Anagrafica;
import com.klugesoftware.farmainvoice.model.InvoiceHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class InvoiceHeaderDAO {

    private final Logger logger = LogManager.getLogger(InvoiceHeaderDAO.class.getName());
    private DAOFactory daoFactory;

    private static final String SQL_INSERT = "INSERT INTO invoice_header (" +
            "idinvoice,tipodocumento,denominazione,partitaiva,codicefiscale,dataemissione,datainvioricezione,importo,visualizzato,inviato,nomefilexml,tipofattura) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE invoice_header SET " +
            "tipodocumento = ?,denominazione = ?,partitaiva = ?,codicefiscale = ?,dataemissione = ?,datainvioricezione = ?,importo = ?,visualizzato = ?,inviato = ?,nomefilexml = ?,tipofattura = ? " +
            "WHERE idinvoice = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM invoice_header ";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM invoice_header WHERE idinvoice = ?";
    private static final String SQL_FIND_BY_PARTITA_IVA = "SELECT * FROM invoice_header WHERE partitaIva = ?";
    private static final String SQL_TRUNCATE = "TRUNCATE TABLE invoice_header RESTART IDENTITY";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM invoice_header WHERE idinvoice = ?";

    public InvoiceHeaderDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    private InvoiceHeader find(String sql, Object...values){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        InvoiceHeader invoiceHeader = new InvoiceHeader();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null) {
                if(resultSet.next()){
                    invoiceHeader = DAOUtil.mapInvoiceHeader(resultSet);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally {
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return invoiceHeader;
    }

    private ArrayList<InvoiceHeader> findList(String sql, Object...values){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<InvoiceHeader> elenco = new ArrayList<InvoiceHeader>();
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = DAOUtil.prepareStatement(conn, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                elenco.add(DAOUtil.mapInvoiceHeader(resultSet));
            }
        }catch(SQLException ex){
            logger.error(ex);
        }finally{
            DAOUtil.close(conn, preparedStatement, resultSet);
        }
        return elenco;
    }

    public InvoiceHeader findById(Integer idInvoice){
        return find(SQL_FIND_BY_ID,idInvoice);
    }

    public ArrayList<InvoiceHeader> findAll(){
        return findList(SQL_FIND_ALL);
    }

    public InvoiceHeader create(InvoiceHeader invoiceHeader){

        if(invoiceHeader.getIdInvoice() != null){
            throw new IllegalArgumentException("Record già presente! ");
        }

        Object[] values = {
                invoiceHeader.getIdInvoice(),
                invoiceHeader.getTipoDocumento().toString(),
                invoiceHeader.getDenominazione(),
                invoiceHeader.getPartitaIva(),
                invoiceHeader.getCodiceFiscale(),
                invoiceHeader.getDataEmissione(),
                invoiceHeader.getDataInvioRicezione(),
                invoiceHeader.getImporto(),
                invoiceHeader.getVisualizzato(),
                invoiceHeader.getInviato(),
                invoiceHeader.getNomeFileXml(),
                invoiceHeader.getTipoFattura().toString()
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
                invoiceHeader.setIdInvoice(new Integer(generetedKey.getInt(1)));
            }else{
                throw new SQLException("La creazione di un nuovo record non è andata a buon fine!");
            }
        }catch(SQLException ex){
            logger.error("InvoiceHeaderDAO.create: I can't create new record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement, generetedKey);
        }
        return invoiceHeader;
    }

    public InvoiceHeader update(InvoiceHeader invoiceHeader){
        if(invoiceHeader.getIdInvoice() == null){
            throw new IllegalArgumentException("Id null: record non creato! ");
        }

        Object[] values = {
                invoiceHeader.getTipoDocumento().toString(),
                invoiceHeader.getDenominazione(),
                invoiceHeader.getPartitaIva(),
                invoiceHeader.getCodiceFiscale(),
                invoiceHeader.getDataEmissione(),
                invoiceHeader.getDataInvioRicezione(),
                invoiceHeader.getImporto(),
                invoiceHeader.getVisualizzato(),
                invoiceHeader.getInviato(),
                invoiceHeader.getNomeFileXml(),
                invoiceHeader.getTipoFattura().toString(),
                invoiceHeader.getIdInvoice(),
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
            logger.error("InvoiceHeaderDAO.update: I can't update record...",ex);
        }finally{
            DAOUtil.close(connection, preparedStatement);
        }
        return invoiceHeader;
    }


    public boolean deleteRecordById(int idInvoice){

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        boolean ret = false;
        try{
            conn = daoFactory.getConnetcion();
            preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID);
            preparedStatement.setInt(1, idInvoice);
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

    public boolean deleteTable(){
        Connection connection = null;
        Statement stmt = null;
        boolean ret = false;
        try{
            connection = daoFactory.getConnetcion();
            stmt = connection.createStatement();
            stmt.executeUpdate(SQL_TRUNCATE);
            ret = true;
        }catch(SQLException ex){
            logger.error("InvoiceHeaderDAO.update: I can't update record...",ex);
            ret = false;
        }finally{
            try{
                stmt.close();
                connection.close();
            }catch(SQLException ex){
                logger.error("InvoiceHeaderDAO.deleteTable(): I can't close db connection");
            }
            return ret;
        }
    }


}
