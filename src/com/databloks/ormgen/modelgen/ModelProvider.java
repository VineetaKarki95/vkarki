package com.databloks.ormgen.modelgen;

import com.databloks.dal.BlokReader;
import com.databloks.dal.GenerationSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ModelProvider {
    private String source = "50.62.176.220";
    private String userId = "dbloktutor";
    private String password = "1@valamp";
    private String catalog = "dbloktutor";
    public enum providerType { msSqlServer, mySql }
    private providerType targetProvider = providerType.mySql;
	private Connection activeConnection = null;

    public String getSource(){ return source; } 
    public void setSource(String Source){ source = Source; }
    public String getUserId(){ return userId; } 
    public void setUserId(String UserId){ userId = UserId; }
    public String getPassword(){ return password; } 
    public void setPassword(String Password){ password = Password; }
    public String getCatalog(){ return catalog; } 
    public void setCatalog(String Catalog){ catalog = Catalog; }
    public providerType getTargetProvider(){ return targetProvider; } 
    public void setProviderType(providerType TargetProvider){ targetProvider = TargetProvider; }
    
    public ModelProvider(){}
    public ModelProvider(GenerationSet generationSet)
    {
    	source = generationSet.getDataSource();
    	userId = generationSet.getUserId();
    	password = generationSet.getPassword();
    	catalog = generationSet.getDatabaseName();
    }
    
    public void setConnectionParameters(String Source, String UserId, String Password, String Catalog)
    {
    	source = Source;
    	userId = UserId;
    	password = Password;
    	catalog = Catalog;
    }

    public BlokReader SqlDispatch(providerType targetProvider, String cmd)
    {
        BlokReader blokReader = null;
        switch(targetProvider)
        {
            default:
            case msSqlServer:
                //blokReader = SqlServerDispatch(cmd);
                break;
            case mySql:
                blokReader = MySqlDispatch(cmd);
                break;
        }
        return blokReader;
    }
//    public BlokReader SqlServerDispatch(string cmdText)
//    {
//        BlokReader blokReader = new BlokReader(providerType.msSqlServer);
//        SqlConnection connection = GetOpenSqlConnection();
//        blokReader.ConnectionHandle = connection;
//        SqlCommand cmd = new SqlCommand(cmdText, connection);
//        blokReader.Reader = cmd.ExecuteReader();
//        return blokReader;
//    }
    public BlokReader MySqlDispatch(String cmdText)
    {
    	BlokReader blokReader = null;
    	try
    	{
            blokReader = new BlokReader(providerType.mySql);
            Connection connection = GetOpenMySqlConnection();
            blokReader.setConnectionHandle(connection);
            Statement cmd = connection.createStatement();
            blokReader.setResultSet(cmd.executeQuery(cmdText));
            
            return blokReader;
    	}
    	catch(Exception e){
    		System.out.println("GetConnection failed: " + e.getMessage());
    		e.printStackTrace();
    	}
    	return blokReader;
    }
//    public SqlConnection GetOpenSqlConnection()
//    {
//        //string connectionString = @"Data Source=.\SQLEXPRESS;Password=dbadmin;User ID=sa;Initial Catalog=MDAT03PRD";
//        string connectionString = MakeSqlConnectionString(source, userId, password, catalog);
//        SqlConnection sqlConnect = new SqlConnection(connectionString);
//        sqlConnect.Open();
//        return sqlConnect;
//    }
    public Connection GetOpenMySqlConnection()
    {
        String connectString = MakeMySqlConnectionString(source, catalog);
        String forName = "com.mysql.jdbc.Driver";
    	Connection con = null;
    	try{
		      Class.forName(forName);
			  con = DriverManager.getConnection(
				         connectString, userId, password); 
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
//    public void SetSqlConnection(string Source, string UserId, string Password, string Catalog)
//    {
//        source = Source;
//        userId = UserId;
//        password = Password;
//        catalog = Catalog;
//    }
//    public string MakeSqlConnectionString(string Source, string UserId, string Password, string Catalog)
//    {
//        StringBuilder sb = new StringBuilder();
//        sb.Append(@"Data Source=" + Source + @";");
//        sb.Append(@"Password=" + Password + @";");
//        sb.Append(@"User ID=" + UserId + @";");
//        sb.Append(@"Initial Catalog=" + Catalog + @";");
//        return sb.ToString();
//    }
    public String MakeMySqlConnectionString(String Source, String Catalog)
    {
		String connectString = String.format("jdbc:mysql://%s:3306/%s", Source, Catalog);
		return connectString;
    }

//    public int SqlExecuteNonQuery(providerType targetProvider, string cmd)
//    {
//        int rows = 0;
//        switch (targetProvider)
//        {
//            default:
//            case providerType.msSqlServer:
//                rows = SqlServerExecuteNonQuery(cmd);
//                break;
//            case providerType.mySql:
//                rows = MySqlExecuteNonQuery(cmd);
//                break;
//        }
//        return rows;
//    }
//    public int SqlServerExecuteNonQuery(string cmdText)
//    {
//        int rows = 0;
//        SqlConnection connection = GetOpenSqlConnection();
//        SqlCommand cmd = new SqlCommand(cmdText, connection);
//        rows = cmd.ExecuteNonQuery();
//        connection.Close();
//        return rows;
//    }
//    public int MySqlExecuteNonQuery(string cmdText)
//    {
//        int rows = 0;
//        MySqlConnection connection = GetOpenMySqlConnection();
//        MySqlCommand cmd = new MySqlCommand(cmdText, connection);
//        rows = cmd.ExecuteNonQuery();
//        connection.Close();
//        return rows;
//    }
}
