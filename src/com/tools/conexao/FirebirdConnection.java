package com.tools.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FirebirdConnection {
	
	public Connection con = null;
	
	public Statement stm = null;
	
	
	public FirebirdConnection(){
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
						   	
			con = DriverManager.getConnection(
					"jdbc:firebirdsql:127.0.0.1:C:/ecosis/dados/ECODADOS.ECO",
					"SYSDBA",
					"masterkey"
					);
			this.stm = con.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public FirebirdConnection(String caminhoBase){
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			
			caminhoBase = "jdbc:firebirdsql:127.0.0.1:" + caminhoBase;
						   	
			con = DriverManager.getConnection(
					caminhoBase,
					"SYSDBA",
					"masterkey"
					);
			this.stm = con.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
