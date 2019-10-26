package com.klugesoftware.farmainvoice.dbmanager;

import com.klugesoftware.farmainvoice.model.FatturaHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public abstract class DAOFactory {


	public static final String PROPERTIES_FILE_NAME = "./resources/conf/config.properties";
	private static final Properties propDb = new Properties();
	/*
	static {
		try {
			InputStream propertiesFile = new FileInputStream(PROPERTIES_FILE_NAME);
		
			propDb.load(propertiesFile);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	private static final String url = propDb.getProperty("dbUrl");
	
	private static final String driver = propDb.getProperty("dbDriver");
	
	private static final String username = propDb.getProperty("dbUser");
	
	private static final String password = propDb.getProperty("dbPwd");
	*/


	
	public static DAOFactory getInstance(){
		
		/*if (name == null){
			try {
				throw new Exception();
			} catch (Exception e) {

				System.out.println("database name is null");
				e.printStackTrace();
			}
		}*/
		
		DAOFactory instance;
		
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		instance = new DriverManagerDAOFactory("jdbc:hsqldb:file:./resources/db/SettingInvoice","builder","carrot163");
		
		return instance;
	}	

	abstract Connection getConnetcion() throws SQLException;

	public AnagraficaDAO getAnagraficaDAO(){
		return new AnagraficaDAO(this);
	}

	public FatturaHeaderDAO getFatturaHeaderDAO() {
		return new FatturaHeaderDAO(this);
	}

	public FatturaDetailDAO getFatturaDetailDAO(){return new FatturaDetailDAO(this);}

	public InvoiceHeaderDAO getInvoiceHederDAO() {return new InvoiceHeaderDAO(this);}
}
