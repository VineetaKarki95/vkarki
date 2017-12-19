package com.databloks.ormgen;

import com.databloks.dal.BlokReader;
import com.databloks.dal.GenerationSet;
import com.databloks.dal.Substitution;
import com.databloks.dal.Substitutions;
import com.databloks.ormgen.modelgen.ModelProvider;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.util.ArrayList;

public class DashboardManager {

    public enum discoverySqlType { tables, tableColumns, keyList, parameterList, isIdentity }
    
    ArrayList<String> classIncludeStrings = new ArrayList<String>();
    //public ModelProvider.providerType targetProvider = ModelProvider.providerType.msSqlServer;
//    ArrayList<string> classIncludeStrings = new ArrayList<string>();
//    ArrayList<Substitution> substitutes = new ArrayList<Substitution>();
    ArrayList<String> reservedWordList = new ArrayList<String>();

    private GenerationSet generationSet;
    private ModelProvider.providerType targetProvider;
    
	public DashboardManager(GenerationSet generationSet)
	{
		this.generationSet = generationSet;
		this.targetProvider = ModelProvider.providerType.valueOf(generationSet.getProviderType());
	}
	
    public GenerationSet getGenerationSet(){ return this.generationSet; }
    public void setFieldName(GenerationSet generationSet){ this.generationSet = generationSet; }
    public ModelProvider.providerType getTargetProvider(){ return this.targetProvider; }
    public void setFieldName(ModelProvider.providerType targetProvider){ this.targetProvider = targetProvider; }
    public ArrayList<String> getReservedWordList(){ return this.reservedWordList; }
    public void setReservedWordList(ArrayList<String> reservedWordList){ this.reservedWordList = reservedWordList; }
    
	
    public String BuildDiscoveryStrings(ModelProvider.providerType targetProvider, discoverySqlType discoverType, String arg1, String arg2)
    {
    	ModelProvider modelProvider = new ModelProvider(generationSet);
        StringBuffer sqlString = new StringBuffer();
        switch(targetProvider)
        {
            default:
            case msSqlServer:
                switch(discoverType)
                {
                    default:
                        break;
                    case tables:
                        sqlString.append("select name, id from sysobjects where type = 'U' order by name");
                        break;
                    case tableColumns:
                        sqlString.append("select * from " + arg1 + " where 1 != 1");
                        break;
                    case keyList:
                        sqlString.append("BEGIN ");
                        sqlString.append("SELECT ");
                        sqlString.append("  COL_NAME(T.object_id, IC.column_id) AS column_name, ");
                        sqlString.append("  ST.name, ");
                        sqlString.append("  IC.key_ordinal, ");
                        sqlString.append("  IC.is_descending_key ");
                        sqlString.append("FROM ");
                        sqlString.append("  sys.tables AS T ");
                        sqlString.append("  INNER JOIN ");
                        sqlString.append("  sys.key_constraints AS KC ");
                        sqlString.append("  ON KC.parent_object_id = T.object_id ");
                        sqlString.append("  INNER JOIN ");
                        sqlString.append("  sys.indexes AS I ");
                        sqlString.append("  ON KC.unique_index_id = I.index_id ");
                        sqlString.append("  AND KC.parent_object_id = I.object_id ");
                        sqlString.append("  INNER JOIN ");
                        sqlString.append("  sys.index_columns AS IC ");
                        sqlString.append("  ON I.object_id = IC.object_id ");
                        sqlString.append("  AND I.index_id = IC.index_id ");
                        sqlString.append("  INNER JOIN ");
                        sqlString.append("  sys.all_columns AS AC ");
                        sqlString.append("  ON IC.column_id = AC.column_id ");
                        sqlString.append("  and T.object_id = AC.object_id ");
                        sqlString.append("  INNER JOIN ");
                        sqlString.append("  sys.systypes ST ");
                        sqlString.append("  ON AC.user_type_id = ST.xtype ");
                        sqlString.append("WHERE ");
                        sqlString.append("  T.object_id = " + Integer.parseInt(arg1) + " ");
                        sqlString.append("  AND ST.name != 'sysname' ");
                        sqlString.append("  AND KC.type = 'PK' ");
                        sqlString.append("ORDER BY ");
                        sqlString.append("  T.object_id, ");
                        sqlString.append("  IC.key_ordinal; ");
                        sqlString.append("END ");
                        break;
                    case parameterList:
                        sqlString.append("select ");
                        sqlString.append("SO.NAME PROCNAME ");
                        sqlString.append(",P.PARAMETER_ID PARMID ");
                        sqlString.append(",P.NAME PARMNAME ");
                        sqlString.append(",T.NAME PARMTYPE ");
                        sqlString.append("FROM sys.objects so ");
                        sqlString.append("INNER JOIN sys.parameters P ON SO.OBJECT_ID = P.OBJECT_ID ");
                        sqlString.append("INNER JOIN sys.types T ON P.system_type_id = T.system_type_id ");
                        sqlString.append("WHERE SO.TYPE IN ('P') ");
                        sqlString.append("AND SO.OBJECT_ID = " + Integer.parseInt(arg1));
                        sqlString.append("AND T.NAME != 'sysname' ");
                        sqlString.append("ORDER BY ");
                        sqlString.append("SO.NAME ");
                        sqlString.append(",P.PARAMETER_ID ");
                        sqlString.append(",P.NAME ");
                        sqlString.append(",T.NAME ");
                        break;
                    case isIdentity:
                        sqlString.append("SELECT IS_IDENTITY ");
                        sqlString.append("FROM SYS.COLUMNS ");
                        sqlString.append("WHERE object_id = object_id('" + arg1 + "') ");
                        sqlString.append("AND name = '" + arg2 + "' ");
                        break;
                }
                break;
            case mySql:
                switch (discoverType)
                {
                    default:
                        break;
                    case tables:
                        sqlString.append("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ");
                        sqlString.append("WHERE TABLE_SCHEMA = '" + modelProvider.getCatalog() + "' ");
                        sqlString.append("and table_type = 'BASE TABLE';");
                        break;
                    case tableColumns:
                        //sqlString.append("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS ");
                        //sqlString.append("WHERE TABLE_SCHEMA = '" + ModelProvider.Catalog + "' ");
                        //sqlString.append("AND TABLE_NAME = '" + arg1 + "';");
                        sqlString.append("select * from " + arg1 + " where 1 != 1");
                        break;
                    case keyList:
                        sqlString.append("SELECT COLUMN_NAME, DATA_TYPE, ORDINAL_POSITION, FALSE ");
                        sqlString.append("FROM information_schema.COLUMNS ");
                        sqlString.append("WHERE TABLE_SCHEMA = '" + modelProvider.getCatalog() + "' ");
                        sqlString.append("AND TABLE_NAME = '" + arg2 + "' ");
                        sqlString.append("AND COLUMN_KEY = 'PRI'; ");
                        break;
                    case isIdentity:
                        sqlString.append("SELECT count(*) IS_IDENTITY ");
                        sqlString.append("FROM INFORMATION_SCHEMA.COLUMNS ");
                        sqlString.append("WHERE TABLE_NAME = '" + arg1 + "' ");
                        sqlString.append("AND COLUMN_NAME = '" + arg2 + "' ");
                        sqlString.append("AND DATA_TYPE = 'int' ");
                        sqlString.append("AND COLUMN_DEFAULT IS NULL ");
                        sqlString.append("AND IS_NULLABLE = 'NO' ");
                        sqlString.append("AND EXTRA like '%auto_increment%' ");
                        break;
                }
                break;
        }
        return sqlString.toString();
    }
    private void GenerateClasses()
    {
    	ModelProvider modelProvider = new ModelProvider(generationSet);
        EntityModelGenerator gen = new EntityModelGenerator(this);
        String sqlCommand = BuildDiscoveryStrings(targetProvider, discoverySqlType.tables, null, null);
        BlokReader blokReader = modelProvider.SqlDispatch(targetProvider, sqlCommand);
    	try
    	{
	        while (blokReader.getReader().next())
	        {
	            String entityName = blokReader.getReader().getString(1);
	            System.out.println("Table: " + entityName);
	            String entityId = "";
	            if (blokReader.getReader().getMetaData().getColumnCount() > 2)
	            {
	                entityId = blokReader.getReader().getString(1);
	            }
	            String sqlCommandEntity = BuildDiscoveryStrings(targetProvider, discoverySqlType.tableColumns, entityName, null);
	        	ModelProvider entityModelProvider = new ModelProvider(generationSet);
	            BlokReader blokReaderEntity = entityModelProvider.SqlDispatch(targetProvider, sqlCommandEntity);
	            gen.entity.writeClass(blokReaderEntity, entityName, entityId);
	//            classIncludeStrings.Add("Entities\\" + entityName + ".cs");
	            gen.collection.writeCollectionClass(blokReaderEntity, entityName);
	//            classIncludeStrings.Add("Collections\\" + entityName + "Collection.cs");
	            blokReaderEntity.Close();
	        }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        blokReader.Close();
    }
    public void GenerateModel()
    {
        boolean success = true;
        try
        {
            InitializeModelDirectory();
            classIncludeStrings.clear();
            GenerateClasses();
            //GenerateViewClasses();
            //GenerateProcedureClasses();
            //GenProjectFile();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            success = false;
        }
        if (success)
        {
        	System.out.println("A new data model has been generated.");
        }
    }
    private boolean InitializeModelDirectory()
    {
    	boolean success = true;
    	String destinationFolder = generationSet.getDestinationFolder();
    	System.out.println("Character: " + destinationFolder.substring(destinationFolder.length()-1));
    	if(destinationFolder.substring(destinationFolder.length()-1) != "/")
    	{
    		destinationFolder = destinationFolder + "/";
    	}
    	String templateFolder = generationSet.getTemplateFolder();
    	if(templateFolder.substring(templateFolder.length()-1) != "/")
    	{
    		templateFolder = templateFolder + "/";
    	}
        try
        {
        	System.out.println("Directory initialization begins");
        	new File(destinationFolder).mkdir();
        	new File(destinationFolder + "Procedures").mkdir();
        	new File(destinationFolder + "Collections").mkdir();
        	new File(destinationFolder + "Entities").mkdir();
        	new File(destinationFolder + "ExtendedCollections").mkdir();
        	new File(destinationFolder + "ExtendedEntities").mkdir();
        	new File(destinationFolder + "Properties").mkdir();
        	System.out.println("Directory initialization ends");
        	
    		File folder = new File(templateFolder);
        	File[] listOfFiles = folder.listFiles();
        	System.out.println(String.format("File count: %d", listOfFiles.length));
        	
            for (int i = 0; i < listOfFiles.length; i++) {
              if (listOfFiles[i].isFile()) {
                System.out.println("File: " + listOfFiles[i].getName());
                String destinationFile = destinationFolder + listOfFiles[i].getName();
                if (listOfFiles[i].getName().equals("ModelProvider.java"))
                {
                	Substitutions substitutes = new Substitutions();
                    substitutes.add(new Substitution("SOURCE", generationSet.getDataSource()));
                    substitutes.add(new Substitution("USERID", generationSet.getUserId()));
                    substitutes.add(new Substitution("PASSWORD", generationSet.getPassword()));
                    substitutes.add(new Substitution("CATALOG", generationSet.getDatabaseName()));
                    TemplateUtilities.CopyFileWithTokenSubstitution(listOfFiles[i].getPath(), destinationFile, substitutes);
                }
                else
                {
                	Path source = Paths.get(listOfFiles[i].getPath());
                	Path target = Paths.get(destinationFile);
                	Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
              }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
		return success;
    }
}
