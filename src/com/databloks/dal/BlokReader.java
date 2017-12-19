package com.databloks.dal;

import com.databloks.ormgen.modelgen.ModelProvider.providerType;
import java.sql.ResultSet;
import java.sql.Connection;

public class BlokReader {
	String r;
    ResultSet reader;
    providerType targetProvider;
	Connection connectionHandle;

    public BlokReader()
    {

    }
    public BlokReader(providerType TargetProvider)
    {
        this.targetProvider = TargetProvider;
    }

    public ResultSet getReader(){return reader;} 
    public void setResultSet(ResultSet reader){this.reader = reader;}
    public providerType getTargetProvider(){return targetProvider;} 
    public void setTargetProvider(providerType targetProvider){this.targetProvider = targetProvider;}
    public Connection getConnectionHandle(){return connectionHandle;} 
	public void setConnectionHandle(Connection connectionHandle){this.connectionHandle = connectionHandle;}

    public void Close()
    {
    	try
    	{
	        reader.close();
	        connectionHandle.close();
    	}
    	catch(Exception e){
    		System.out.println("GetConnection failed: " + e.getMessage());
    		e.printStackTrace();
    	};
    }
}
