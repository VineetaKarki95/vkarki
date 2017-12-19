package com.databloks.dal;

import java.sql.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.databloks.ormgen.modelgen.ModelProvider;


public class SqlHelper{

	private String forName = "com.mysql.jdbc.Driver";
	private String connectString = "jdbc:mysql://50.62.176.220:3306/dbloktutor";
	private String user = "dbloktutor";
	private String password = "1@valamp";
	private Connection activeConnection = null;
	
	public static String GetDbFullPath(HttpServletRequest request, String dbName)
	{
		ServletContext context = request.getServletContext();
		String fullPath = context.getRealPath("/");
		return fullPath + "/" + dbName + ".db";
	}
	
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

    public SqlHelper()
    {   	
    }
    public SqlHelper(GenerationSet generationSet)
    {   	
		ModelProvider.providerType targetProvider = ModelProvider.providerType.valueOf(generationSet.getProviderType());
		if(targetProvider == ModelProvider.providerType.mySql)
		{
			this.forName = "com.mysql.jdbc.Driver";
			this.connectString = String.format("jdbc:mysql://%s:3306/%s", generationSet.getDataSource(), generationSet.getDatabaseName());
			this.user = generationSet.getUserId();
			this.password = generationSet.getPassword();
		}    	
    }
	public String getConnectString(){ 
		return this.connectString;
	}
	public void setConnectString(String connectString){ this.connectString = connectString;}
	
	public void close()
	{
		try
		{
			activeConnection.close();
		}
		catch(Exception e){
    		System.out.println("close failed: " + e.getMessage());
    		System.out.println(e.getStackTrace());
		}
	}

	private Connection GetConnection()
    {
    	Connection con = null;
    	try{
		      Class.forName(forName);
			  con = DriverManager.getConnection(
				         connectString, user, password); 
			  if(activeConnection != null)
			  {
				  activeConnection.close();
			  }
			  activeConnection = con;
    	}
    	catch(Exception e){
    		System.out.println("GetConnection failed: " + e.getMessage());
    		e.printStackTrace();
    	};
    	return con;
    }
    
    public ResultSet GetResultSet(String sql)
    {
        ResultSet rs = null;
		try{
			  Connection con = GetConnection(); 
	          Statement stmt = con.createStatement();
	          rs = stmt.executeQuery(sql);
		}
		catch (Exception e) {};
		return rs;    	
    }
    public int ExecuteSql(String sql){
    	Integer stmtResult = -1;
		try{
			  Connection conn = GetConnection(); 
	          Statement stmt = conn.createStatement();
	          stmtResult = stmt.executeUpdate(sql);
			  System.out.println("Statement executed with exit value: " + stmtResult.toString());
	          stmt.close();
	          conn.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		};  
		return stmtResult;
    }
    public int ExecuteSql(String sql, Connection conn)
    {
    	Integer stmtResult = -1;
        try{
            if (conn != null) {
        		Statement stmt = conn.createStatement();
        		stmtResult = stmt.executeUpdate(sql);
        		System.out.println("Statement executed with exit value: " + stmtResult.toString());
				stmt.close();
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
			e.printStackTrace();
        }
        return stmtResult;
    }
    public String ValidateOrCreateDatabase()
    {
        String message = "no message";
        
        try (Connection conn = GetConnection()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("Database validated (and created if necessary)");
                message = "connection successful";
            }
            else
            {
            	message = "conn is null";
            }
 
        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = e.getMessage();
        }	
        return message;
    }
}
