package com.klugesoftware.farmainvoice.dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerDAOFactory extends DAOFactory{

	private String url;
	
	private String username;
	
	private String password;

	
	DriverManagerDAOFactory(String url, String username, String password){
		
		this.url = url;
		
		this.password = password;
		
		this.username = username;
		
	}
	
	@Override
    Connection getConnetcion() throws SQLException {

		return DriverManager.getConnection(url,username,password);
	}
}
