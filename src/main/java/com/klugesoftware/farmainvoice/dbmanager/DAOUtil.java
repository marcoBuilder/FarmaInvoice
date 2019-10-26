package com.klugesoftware.farmainvoice.dbmanager;


import com.klugesoftware.farmainvoice.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

public final class DAOUtil {
	
	private DAOUtil(){
	
	}

	public static PreparedStatement prepareStatement(Connection connection, String sql, boolean returnGeneretedKeys, Object...values) throws SQLException {
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql, returnGeneretedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		
		setValues(preparedStatement, values);
		
		return preparedStatement;
	
	}
	

	public static void setValues(PreparedStatement preparedStatement, Object...values) throws SQLException {
		
		for (int i = 0; i < values.length; i++){
			
			preparedStatement.setObject(i+1, values[i]);
		}
		
	}
	
	public static void close(Connection connection){

		if (connection != null){
			
			try {
				connection.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}
	
	public static void close(Statement statement){
		
		if (statement != null){
			
			try {
				statement.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}
	
	public static void close(ResultSet resultSet){
		
		if (resultSet != null){
			
			try {
				resultSet.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		
	}
	
	public static void close(Connection connection, Statement statement){
		
		close(statement);
		
		close(connection);
	}
	
	public static void close(Connection connection, Statement statement, ResultSet resultSet){
	
		close(resultSet);

		close(statement);
		
		close(connection);
	}

	public static Anagrafica mapAnagrafica(ResultSet resultSet) throws SQLException{
		return new Anagrafica(
				resultSet.getInt("idAnagrafica"),
				resultSet.getString("denominazione"),
				((resultSet.getString("tipoAnagrafica")).equals(TipoAnagrafica.FORNITORE.toString()) ? TipoAnagrafica.FORNITORE : TipoAnagrafica.CLIENTE),
				resultSet.getString("partitaIva"),
				resultSet.getString("codiceFiscale"),
				resultSet.getString("indirizzo"),
				resultSet.getString("cap"),
				resultSet.getString("comune"),
				resultSet.getString("nazione")
		);
	}

	public static FatturaHeader mapFatturaHeader(ResultSet resultSet) throws SQLException{
		return new FatturaHeader(
				resultSet.getInt("idFattura"),
				resultSet.getInt("idAnagrafica"),
				((resultSet.getString("tipoFattura")).equals(TipoFattura.PASSIVA.toString()) ? TipoFattura.PASSIVA : TipoFattura.ATTIVA),
				resultSet.getString("numeroFattura"),
				resultSet.getDate("dataFattura"),
				resultSet.getBigDecimal("importoTotale"),
				resultSet.getString("causale"),
				resultSet.getString("ibanPagamento"),
				resultSet.getString("statoPagamento"),
				resultSet.getString("tipoPagamento"),
				resultSet.getString("notePagamento"),
				resultSet.getString("nomeFile")
		);
	}

	public static FatturaDetail mapFatturaDetail(ResultSet resultSet)  throws SQLException{
		return new FatturaDetail(
				resultSet.getInt("idDetail"),
				resultSet.getInt("idFattura"),
				resultSet.getInt("numeroLinea"),
				resultSet.getString("descrizione") ,
				resultSet.getString("codiceArticolo"),
				resultSet.getInt("quantita"),
				resultSet.getBigDecimal("prezzoUnitario"),
				resultSet.getBigDecimal("prezzoTotale"),
				resultSet.getString("aliquotaIva")
		);
	}

	public static InvoiceHeader mapInvoiceHeader(ResultSet resultSet)  throws SQLException{
		return new InvoiceHeader(
				resultSet.getInt("IDINVOICE"),
				TipoDocumento.valueOf(resultSet.getString("TIPODOCUMENTO")),
				TipoFattura.valueOf(resultSet.getString("TIPOFATTURA")),
				resultSet.getString("DENOMINAZIONE"),
				resultSet.getString("PARTITAIVA"),
				resultSet.getString("CODICEFISCALE"),
				resultSet.getDate("DATAEMISSIONE"),
				resultSet.getDate("DATAINVIORICEZIONE"),
				resultSet.getBigDecimal("IMPORTO"),
				resultSet.getBoolean("VISUALIZZATO"),
				resultSet.getBoolean("INVIATO"),
				resultSet.getString("NOMEFILEXML")
		);
	}

}

