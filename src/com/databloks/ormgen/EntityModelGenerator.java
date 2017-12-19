package com.databloks.ormgen;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.databloks.dal.BlokReader;
import com.databloks.dal.GenerationSet;
import com.databloks.ormgen.DashboardManager.discoverySqlType;
import com.databloks.ormgen.modelgen.ModelEntityKey;
import com.databloks.ormgen.modelgen.ModelEntityKeys;
import com.databloks.ormgen.modelgen.ModelField;
import com.databloks.ormgen.modelgen.ModelFields;
import com.databloks.ormgen.modelgen.ModelProvider;

public class EntityModelGenerator {

	DashboardManager dashboardManager;
	String destinationFolder;
	GenerationSet generationSet;
	ModelProvider.providerType targetProvider;
	
	Entity entity;
	Collection collection;
	View view;
	ViewCollection viewCollection;
	Procedure procedure;

	public EntityModelGenerator(DashboardManager dashboardManager)
	{
		this.dashboardManager = dashboardManager;
		this.generationSet = dashboardManager.getGenerationSet();
		this.targetProvider = dashboardManager.getTargetProvider();

		this.destinationFolder = generationSet.getDestinationFolder();
		
		entity = new Entity();
		collection = new Collection();
		view = new View();
		viewCollection = new ViewCollection();
		procedure = new Procedure();
	}
    public int GetKeyArray(
    		String name, 
    		StringBuffer sb, 
    		int level, 
    		GeneratorHelpers helper, 
    		ModelEntityKeys keys,
    		ModelProvider modelProvider,
    		String id)
    {
    	try
    	{
	        String sqlCommand = dashboardManager.BuildDiscoveryStrings(targetProvider,
	        		discoverySqlType.keyList, id, name);
	        BlokReader blokReaderKey = modelProvider.SqlDispatch(targetProvider, sqlCommand);
	        while (blokReaderKey.getReader().next())
	        {
	        	int keyCount = blokReaderKey.getReader().getMetaData().getColumnCount();
	            ModelEntityKey entityKey = new ModelEntityKey();
	            entityKey.setKeyName(blokReaderKey.getReader().getString(1));
	            if (keyCount >= 2)
	            {
	                entityKey.setKeyDataType(blokReaderKey.getReader().getString(2));
	            }
	            if (keyCount >= 3)
	            {
	                entityKey.setKeyOrder(Integer.parseInt(blokReaderKey.getReader().getString(3)));
	            }
	            if (keyCount >= 4)
	            {
	                if (blokReaderKey.getReader().getString(4).equals("1"))
	                {
	                    entityKey.setKeySortDescending(true);
	                }
	            }
	            keys.add(entityKey);
	        }
            blokReaderKey.getReader().close();    		
		}
		catch(Exception e)
		{
	        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
	        e.printStackTrace();			
		}
        return level;   	
    }
	public class Entity
	{
	    public void writeClass(BlokReader blokReader, String name, String id)
	    {
	        try
	        {
	        	ModelProvider modelProvider = new ModelProvider(generationSet);
	        	GeneratorHelpers helper = new GeneratorHelpers(dashboardManager);
	            int level = 0;

	            StringBuffer sb = new StringBuffer();
	            
		        ModelEntityKeys keys = new ModelEntityKeys();

		        /* GET KEY ARRAY */
		        GetKeyArray(name, sb, level, helper, keys, modelProvider, id);
		
		        /* CREATE CLASS HEADER */
		        level = writeClass_Header(name, sb, level, helper);
		
		        /* CREATE DECLARATIONS */
		        level = writeClass_Declarations(name, sb, level, helper, blokReader);
		
		        /* CREATE CONSTRUCTORS */
		        level = writeClass_Constructors(name, sb, level, helper, blokReader, keys);
		
		        /* CREATE ACCESSOR/MUTATOR METHODS */
		        level = writeClass_AccessorMutators(name, sb, level, helper, blokReader);
		        
		        /* CREATE GET COLUMN BY NAME */
		        level = writeClass_GetColumnByName(name, sb, level, helper, blokReader, "String ColumnName", "ColumnName");
		        level = writeClass_GetColumnByName(name, sb, level, helper, blokReader, "ModelField Column", "Column.getFieldName()");
		
		        /* CREATE MAPRECORD METHOD */
		        level = writeClass_MapRecord(name, sb, level, helper, blokReader);
		
		        /* CREATE DELETE METHODS */
		        /* DELETE BY KEY METHOD */
		        level = writeClass_DeleteByKey(name, sb, level, helper, blokReader, keys);	        
		        /* DELETE CURRENT RECORD METHOD */
		        level = writeClass_Delete(name, sb, level, helper, blokReader, keys);
		        
		        /* GET BY KEYS METHOD */
		        level = writeClass_GetByKeys(name, sb, level, helper, blokReader, keys);
		
		        /* CREATE GETINSERTSQL OVERRIDE */
		        level = writeClass_GetInsertSql(name, sb, level, helper, blokReader, keys);
		
		        /* CREATE GETUPDATESQL OVERRIDE */
		        level = writeClass_GetUpdateSql(name, sb, level, helper, blokReader, keys);
		        	
		        /* CREATE FIELDS SUBCLASS */
		        level = writeClass_SubClass_Fields(name, sb, level, helper, blokReader, keys);

		        /* CREATE CLASS FOOTER */
		        helper.AppendLine(sb, helper.indent(--level) + "}");
		        
		        /* WRITE CLASS */
	            String fileName = destinationFolder + "\\Entities\\" + name + ".java";
	            PrintWriter file = new PrintWriter(fileName);
		        file.println(sb.toString());
		        file.flush();
		        file.close();

	        }
	        catch (Exception e)
	        {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	        }
	    }	
	    public int writeClass_Template(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader)
	    {
	    	try
	    	{
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_AccessorMutators(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader)
	    {
	    	try
	    	{
		        helper.AppendLine(sb, helper.indent(level) + "/* ACCESSOR/MUTATOR METHODS */");
				
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		            String dataType = helper.TypeTranslation(blokReader.getReader().getMetaData().getColumnTypeName(i), targetProvider);
		            String fieldName = helper.ParseFieldName(blokReader.getReader().getMetaData().getColumnName(i));
		            String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(fieldName);
		            String fieldNameInitialUpper = helper.ReplaceFirstCharacterToUpper(fieldName);
		
		            helper.AppendLine(sb, helper.indent(level) + "public " + dataType + " get" + fieldNameInitialUpper + "()" + 
		            		helper.AddAccessorMutator(dataType, fieldName, sb)); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
		            helper.AppendLine(sb, helper.indent(level) + "public void set" + fieldNameInitialUpper + "(" + dataType + " " + 
		            		fieldName + "){ this." + fieldName + " = " + fieldName + "; }"); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
		        }
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_AccessorMutators: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_Constructors(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "/* CONSTRUCTORS */", level);
		        helper.AppendLine(sb, helper.indent(level) + "public " + name + "()");
		        helper.AppendLine(sb, helper.indent(level) + "{");
		        helper.AppendLine(sb, helper.indent(++level) + "setEntityName(\"" + name + "\");");
		        for(ModelEntityKey key : keys)
		        {
		            helper.AppendLine(sb, helper.indent(level) + "AddEntityKey(\"" + key.getKeyName() + "\"," +
		                Integer.toString(key.getKeyOrder()) + "," +
		                String.valueOf(key.getKeySortDescending()).toLowerCase() + ", " +
		                "\"" + key.getKeyDataType() + "\"" +
		                ");");
		        }
		        helper.AppendLine(sb, helper.indent(--level) + "}");
		        helper.AppendLine(sb);
		
		        if (keys.size() > 0)
		        {
		            String classConstructor = helper.GenConstructor(name, keys);
		            helper.AppendLine(sb, helper.indent(level) + classConstructor);
		            helper.AppendLine(sb, helper.indent(level) + "{");
		            helper.AppendLine(sb, helper.indent(++level) + helper.GenMethodCall(keys) + ";");
		            helper.AppendLine(sb, helper.indent(level) + "setEntityName(\"" + name + "\");");
		            helper.AppendLine(sb, helper.indent(--level) + "}");
		        }
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_Constructors: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_Declarations(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader)
	    {
	    	try
	    	{
	            helper.AppendLine(sb, helper.indent(level) + "/* REGION DECLARATIONS */");
	            for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
	            {
	                String dataType = helper.TypeTranslation(blokReader.getReader().getMetaData().getColumnTypeName(i), targetProvider);
	                String fieldName = helper.ParseFieldName(blokReader.getReader().getMetaData().getColumnName(i));
	                String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(fieldName);
	                helper.AppendLine(sb, helper.indent(level) + dataType + " " + fieldName + ";"); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
	            }
	            helper.AppendLine(sb);   		
	    	}
	    	catch(Exception e)
	    	{
	            System.out.println("Failed: writeClass_Declarations: \n" + e.getMessage());
	            e.printStackTrace();
	    		
	    	}
	    	return level;
	    }
	    public int writeClass_Delete(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "public boolean Delete()", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "StringBuffer CmdSql = new StringBuffer();", level);
		        helper.AppendLineIndented(sb, "CmdSql.append(\"delete from " + name + " \");", level);
		        helper.AppendLine(sb, helper.GenWhereCompositeKey(keys, level, true, "CmdSql"));
		        helper.AppendLineIndented(sb, "boolean wasDeleted = true;", level);
		        helper.AppendLineIndented(sb, "try", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.ReturnExecuteNonQueryForCmdSql(sb, level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "catch(Exception e)", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "System.out.println(\"GetConnection failed: \" + e.getMessage());", level);
		        helper.AppendLineIndented(sb, "e.printStackTrace();", level);
		        helper.AppendLineIndented(sb, "wasDeleted = false;", level);
		        helper.AppendLineIndented(sb, "}", --level);      
		        helper.AppendLineIndented(sb, "return wasDeleted;", level);
		        helper.AppendLineIndented(sb, "}", --level);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_Delete: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_DeleteByKey(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        String methodInvoke = helper.GenMethodInvoke("Delete", keys);
		        helper.AppendLineIndented(sb, methodInvoke, level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "StringBuffer CmdSql = new StringBuffer();", level);
		        helper.AppendLineIndented(sb, "CmdSql.append(\"delete from " + name + " \");", level);
		        helper.AppendLine(sb, helper.GenWhereCompositeKey(keys, level, false, "CmdSql"));
		        helper.AppendLineIndented(sb, "boolean wasDeleted = false;", level);
		        helper.AppendLineIndented(sb, "try", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.ReturnExecuteNonQueryForCmdSql(sb, level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "catch(Exception e)", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "System.out.println(\"GetConnection failed: \" + e.getMessage());", level);
		        helper.AppendLineIndented(sb, "e.printStackTrace();", level);
		        helper.AppendLineIndented(sb, "wasDeleted = false;", level);
		        helper.AppendLineIndented(sb, "}", --level);      
		        helper.AppendLineIndented(sb, "return wasDeleted;", level);
		        helper.AppendLineIndented(sb, "}", --level);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_Delete: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_GetByKeys(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        String getMthodInvoke = helper.GenMethodInvoke("GetByKey", keys, "BlokReader");
		        helper.AppendLineIndented(sb, getMthodInvoke, level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "StringBuffer CmdSql = new StringBuffer();", level);
		        helper.AppendLineIndented(sb, "CmdSql.append(\"select * from " + name + " \");", level);
		        helper.AppendLine(sb, helper.GenWhereCompositeKey(keys, level, false, "CmdSql"));
		        helper.ReturnReaderForCmdSql(sb, level);
		        helper.AppendLineIndented(sb,  "MapRecord(reader);", level);
		        helper.AppendLineIndented(sb, "return reader;", level);
		        helper.AppendLineIndented(sb, "}", --level);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetByKeys: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_GetColumnByName(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, String argConstructor, String argSwitch)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "public String GetColumn(" + argConstructor + ")", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "String columnValueAsString = \"\";", level);
		        helper.AppendLineIndented(sb, "try", level);
		        helper.AppendLineIndented(sb,  "{", level++);
		        helper.AppendLineIndented(sb, "switch(" + argSwitch + ")", level);
		        helper.AppendLineIndented(sb,  "{", level++);
		        
		        StringBuffer sbCaseStatements = new StringBuffer();
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		        	String serverDataType = blokReader.getReader().getMetaData().getColumnTypeName(i);
		            String dataType = helper.TypeTranslation(serverDataType, targetProvider);
		            String fieldName = blokReader.getReader().getMetaData().getColumnName(i);
		            String parsedName = helper.ParseFieldName(fieldName);
		            String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(fieldName);
		            String fieldNameInitialUpper = helper.ReplaceFirstCharacterToUpper(fieldName);
		            /* THIS FORMULA WILL ALLOW ELIMINATION OF NOTNULL METHOD */
		            helper.AppendLineIndented(sbCaseStatements, "case \"" + fieldName + "\":", level++);
		            helper.AppendLineIndented(sbCaseStatements,  "columnValueAsString = " + helper.TypeTranslation(serverDataType, targetProvider, fieldName, true) + ";", level); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
		            helper.AppendLineIndented(sbCaseStatements,  "break;", --level);
		        }
		        sb.append(sbCaseStatements.toString());
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "catch(Exception e){", level);
		        helper.AppendLineIndented(sb, "System.out.println(\"GetConnection failed: \" + e.getMessage());", ++level);
		        helper.AppendLineIndented(sb, "e.printStackTrace();", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "return columnValueAsString;", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLine(sb);        
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_GetInsertSql(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        StringBuffer sbValueList = new StringBuffer();
		        helper.AppendLineIndented(sb, "/* METHOD OVERRIDES */", level);
		        helper.AppendLineIndented(sb, "public String GetInsertSql()", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "StringBuilder insertSQL = new StringBuilder();", level);
		        helper.AppendLineIndented(sb, "insertSQL.append(\"insert into " + name + " \");", level);
		        helper.AppendLineIndented(sb, "insertSQL.append(\"( \");", level);
		        String separator = "";
		        String singleQuote = "";
		        boolean isFirstTerm = true;
		        
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		        	boolean isAutoIncrement = blokReader.getReader().getMetaData().isAutoIncrement(i);
		            String dataType = helper.TypeTranslation(blokReader.getReader().getMetaData().getColumnTypeName(i), targetProvider);
		            String fieldName = blokReader.getReader().getMetaData().getColumnName(i);
		            String parsedName = helper.ParseFieldName(fieldName);
		            String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(parsedName);
	                if (dataType == "String" || dataType == "DateTime" || dataType == "Date" || dataType == "bool")
	                {
	                    singleQuote = "'";
	                }
	                else
	                {
	                    singleQuote = "";
	                }
		            if (!isAutoIncrement)
		            {
			            helper.AppendLineIndented(sbValueList, "insertSQL.append(\"" + separator + singleQuote + "\" + " + fieldName + " + \"" + singleQuote + " \");", level); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
		                helper.AppendLineIndented(sb, "insertSQL.append(\"" + separator + fieldName + " \");", level);
		                if (isFirstTerm)
		                {
		                    isFirstTerm = false;
		                    separator = ",";
		                }
		            }
		            else
		            {
		            	if(targetProvider == ModelProvider.providerType.mySql)
		            	{
				            helper.AppendLineIndented(sbValueList, "insertSQL.append(\"" + separator + singleQuote + "0" + singleQuote + " \");", level);
			                helper.AppendLineIndented(sb, "insertSQL.append(\"" + separator + fieldName + " \");", level); 
			                if (isFirstTerm)
			                {
			                    isFirstTerm = false;
			                    separator = ",";
			                }
		                }
		            }            
		        }
		        helper.AppendLineIndented(sb, "insertSQL.append(\") values ( \");", level);
		        helper.AppendLineIndented(sb, sbValueList.toString(), level - 3);
		        helper.AppendLineIndented(sb, "insertSQL.append(\") \");", level);
		        helper.AppendLineIndented(sb, "return insertSQL.toString();", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetInsertSql: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_GetUpdateSql(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        StringBuffer sbAssignments = new StringBuffer();
		        helper.AppendLineIndented(sb, "public String GetUpdateSql()", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "StringBuilder updateSQL = new StringBuilder();", level);
		        helper.AppendLineIndented(sb, "updateSQL.append(\"update " + name + " \");", level);
		        helper.AppendLineIndented(sb, "updateSQL.append(\"set \");", level);
		        boolean isFirstTerm = true;
		        String separator = "";
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		            String dataType = helper.TypeTranslation(blokReader.getReader().getMetaData().getColumnTypeName(i), targetProvider);
		            String fieldName = blokReader.getReader().getMetaData().getColumnName(i);
		            String parsedName = helper.ParseFieldName(fieldName);
		            String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(parsedName);
	                String singleQuote = "";
		        	boolean isKeyField = keys.IsKey(fieldName);
		        	boolean isAutoIncrement = blokReader.getReader().getMetaData().isAutoIncrement(i);
	                if (dataType == "String" || dataType == "DateTime" || dataType == "Date" || dataType == "bool")
	                {
	                    singleQuote = "'";
	                }
		            if (!isAutoIncrement && !isKeyField)
		            {
		            	helper.AppendLineIndented(sb, "updateSQL.append(\"" + separator + fieldName + " = " + singleQuote + "\" + " + fieldName + " + \"" + singleQuote + " \");", level); /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME */
		                if (isFirstTerm)
		                {
		                    isFirstTerm = false;
		                    separator = ",";
		                }
		            }
		        }
		        helper.AppendLine(sb, helper.GenWhereCompositeKey(keys, level, false, "updateSQL"));
		        helper.AppendLineIndented(sb, "return updateSQL.toString();", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetUpdateSql: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_Header(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	        helper.AppendLineIndented(sb, "package com.databloks.model.Entities;", level);
	        helper.AppendLine(sb);
	        helper.AppendLineIndented(sb, "import com.databloks.model.ModelEntityBase;", level);
	        helper.AppendLineIndented(sb, "import com.databloks.model.BlokReader;", level);
	        helper.AppendLineIndented(sb, "import com.databloks.model.ModelField;", level);
	        helper.AppendLineIndented(sb, "import com.databloks.model.ModelFields;", level);
	        helper.AppendLineIndented(sb, "import com.databloks.model.ModelProvider;", level); 
	        //helper.AppendLineIndented(sb, "import java.text.DateFormat;", level); 
	        helper.AppendLineIndented(sb, "import java.text.Format;", level); 
	        //helper.AppendLineIndented(sb, "import java.text.SimpleDateFormat;", level); 
	        helper.AppendLineIndented(sb, "import java.util.Date;", level); 
	        helper.AppendLineIndented(sb, "import java.util.Locale;", level);
	        helper.AppendLine(sb);
	
	        helper.AppendLine(sb, helper.indent(level) + "public class " + name + " extends ModelEntityBase");
	        helper.AppendLine(sb, helper.indent(level) + "{");
	        level++;
	        return level;
	    }
	    public int writeClass_MapRecord(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "/* METHODS */", level);
		        helper.AppendLineIndented(sb, "public boolean MapRecord(BlokReader reader)", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "try", level);
		        helper.AppendLineIndented(sb,  "{", level++);
		        helper.AppendLineIndented(sb, "if (!reader.getReader().next())", level);
		        helper.AppendLineIndented(sb, "{", level++);
		        helper.AppendLineIndented(sb, "return false;", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        
		        StringBuffer sbColumnReader = new StringBuffer();
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		        	String serverDataType = blokReader.getReader().getMetaData().getColumnTypeName(i);
		            String dataType = helper.TypeTranslation(serverDataType, targetProvider);
		            String fieldName = blokReader.getReader().getMetaData().getColumnName(i);
		            String parsedName = helper.ParseFieldName(fieldName);
		            String fieldNameInitialLower = helper.ReplaceFirstCharacterToLower(fieldName);
		            /* THIS FORMULA WILL ALLOW ELIMINATION OF NOTNULL METHOD */
		            helper.AppendLineIndented(sbColumnReader, "if(reader.getReader().getString(\"" + fieldName + "\")!=null) " +
		            		fieldName + " = " + helper.TypeTranslation(serverDataType, targetProvider, fieldName) +";", /* FIELDNAMEINITIALLOWER REPLACeD WITH FIELDNAME (1ST INSTANCE ONLY) */
		            		level);
		        }
		        if(sbColumnReader.toString().contains("dateFormat.parse"))
		        {
		        	helper.AppendLineIndented(sb, "DateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd hh:mm:ss\", Locale.ENGLISH);", level);
		        }
		        sb.append(sbColumnReader.toString());
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "catch(Exception e){", level);
		        helper.AppendLineIndented(sb, "System.out.println(\"GetConnection failed: \" + e.getMessage());", ++level);
		        helper.AppendLineIndented(sb, "e.printStackTrace();", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "return true;", level);
		        helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_MapRecord: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeClass_SubClass_Fields(String name, StringBuffer sb, int level, GeneratorHelpers helper, BlokReader blokReader, ModelEntityKeys keys)
	    {
	    	try
	    	{
		        StringBuffer sbFieldGenerator = new StringBuffer();
				
		        helper.AppendLineIndented(sbFieldGenerator, "/* CLASSES */", level);
		        helper.AppendLineIndented(sbFieldGenerator, "public ModelFields GetFields(){return " + name + ".Fields.List();}", level);
		        helper.AppendLineIndented(sbFieldGenerator, "public static class Fields ", level);
		        helper.AppendLineIndented(sbFieldGenerator, "{", level++);
	
	        	helper.AppendLineIndented(sbFieldGenerator, "static ModelFields fields = new ModelFields();", level);
	        	helper.AppendLineIndented(sbFieldGenerator, "public static ModelFields getFields(){ return fields;}", level);
	        	helper.AppendLineIndented(sbFieldGenerator, "public void setFields(ModelFields fields){ this.fields = fields; }", level);
	        	
	        	ArrayList<String> listFields = new ArrayList<String>();
	        	
		        for (int i = 1; i <= blokReader.getReader().getMetaData().getColumnCount(); i++)
		        {
		            ModelField.DataType dataTypeEnum = helper.TypeTranslationEnum(blokReader.getReader().getMetaData().getColumnTypeName(i), targetProvider);
		            String fieldName = blokReader.getReader().getMetaData().getColumnName(i);
		            String parsedName = helper.ParseFieldName(fieldName);
		            String fieldNameInitialUpper = helper.ReplaceFirstCharacterToUpper(parsedName);
		
		            helper.AppendLineIndented(sbFieldGenerator, "public static ModelField " + fieldName + " = new ModelField(\"" + /* FIELDNAMEINITIALUPPER REPLACED WITH FIELDNAME */
		            		fieldName + "\", ModelField.ComparisonOperator.isEmpty, null, ModelField.DataType." + dataTypeEnum + ", " + keys.IsKey(fieldName) + ");", level); /* FIELDNAMEINITIALUPPER REPLACED WITH FIELDNAME */
		            
		            listFields.add(fieldName); /* FIELDNAMEINITIALUPPER REPLACED WITH FIELDNAME */
		        }
	        	helper.AppendLineIndented(sbFieldGenerator, "public static ModelFields List()", level);
	        	helper.AppendLineIndented(sbFieldGenerator, "{", level++);
	        	helper.AppendLineIndented(sbFieldGenerator, "if(fields.size() == 0)", level);
	        	helper.AppendLineIndented(sbFieldGenerator, "{", level++);
	        	for(String addName : listFields)
	        	{
	        		helper.AppendLineIndented(sbFieldGenerator, "fields.add(" + addName + ");", level);
	        	}
	        	helper.AppendLineIndented(sbFieldGenerator, "}", --level);
	        	helper.AppendLineIndented(sbFieldGenerator, "return fields;", level);
	        	helper.AppendLineIndented(sbFieldGenerator, "}", --level);
		        helper.AppendLineIndented(sbFieldGenerator, "}", --level);
		
		        sb.append(sbFieldGenerator.toString());
		        helper.AppendLine(sb);
		
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_SubClass_Fields: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	}
	public class Collection
	{
	    public void writeCollectionClass(BlokReader reader, String name)
	    {
	        try
		        {
		    	GeneratorHelpers helper = new GeneratorHelpers(dashboardManager);
		        int level = 0;
		
		        StringBuffer sb = new StringBuffer();
				
		        /* CREATE CLASS HEADER */
		        level = writeCollectionClass_Header(name, sb, level, helper);
		
		        /* CREATE DECLARATIONS */
		        level = writeCollectionClass_Declarations(name, sb, level, helper);
		
		        /* CREATE CONSTRUCTOR */
		        level = writeCollectionClass_Constructors(name, sb, level, helper);
		
		        /* CREATE ACCESSOR/MUTATOR METHODS */
		        level = writeCollectionClass_AccessorMutators(name, sb, level, helper);
		
			    /* CREATE METHODS */
			    helper.AppendLineIndented(sb, "/* METHODS */", level);
			    		    
		        /* CREATE GET ENTITY FIELDS METHOD */
		        level = writeCollectionClass_GetEntityFields(name, sb, level, helper);
		        
		        /* CREATE MAPCOLLECTION METHOD */
		        level = writeCollectionClass_MapCollection(name, sb, level, helper);
			    
		        /* CREATE GET ENTITY FIELDS METHOD */
		        level = writeCollectionClass_SortMethods(name, sb, level, helper);
		
		        /* CREATE CLASS FOOTER */
		        helper.AppendLineIndented(sb, "}", --level);
		
		        /* WRITE CLASS */
		        String fileName = destinationFolder + "/Collections/" + name + "Collection.java";
		        PrintWriter file = new PrintWriter(fileName);
		        file.println(sb.toString());
		        file.flush();
		        file.close();
	        }
	        catch (Exception e)
	        {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    public int writeCollectionClass_AccessorMutators(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
		        helper.AppendLine(sb, helper.indent(level) + "/* ACCESSOR/MUTATOR METHODS */");
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_Constructors(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "/* CONSTRUCTORS */", level);
		        helper.AppendLine(sb, helper.indent(level) + "public " + name + "Collection()");
		        helper.AppendLine(sb, helper.indent(level) + "{");
		        helper.AppendLine(sb, helper.indent(++level) + "setEntityName(\"" + name + "\");");
		        helper.AppendLine(sb, helper.indent(--level) + "}");
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_Declarations(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
		        helper.AppendLine(sb, helper.indent(level) + "/* DECLARATIONS */");
		        helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_GetEntityFields(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
			    helper.AppendLineIndented(sb, "public ModelFields GetEntityFields()", level);
			    helper.AppendLineIndented(sb, "{", level++);
			    helper.AppendLineIndented(sb, "return " + name + ".Fields.List();", level);
			    helper.AppendLineIndented(sb, "}", --level);
			    helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_Header(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
		        helper.AppendLineIndented(sb, "package com.databloks.model.Collections;", level);
		        helper.AppendLine(sb);
		        helper.AppendLineIndented(sb, "import com.databloks.model.ModelCollectionBase;", level);
		        helper.AppendLineIndented(sb, "import com.databloks.model.BlokReader;", level);
		        helper.AppendLineIndented(sb, "import com.databloks.model.ModelFields;", level); 
		        helper.AppendLineIndented(sb, "import com.databloks.model.Entities." + name + ";", level); 
		        helper.AppendLineIndented(sb, "import java.util.Collections;", level); 
		        helper.AppendLineIndented(sb, "import java.util.Comparator;", level); 
		        helper.AppendLine(sb);
	
		        helper.AppendLine(sb, helper.indent(level) + "public class " + name + "Collection extends ModelCollectionBase<" + name + ">");
		        helper.AppendLine(sb, helper.indent(level) + "{");
		        level++;
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_MapCollection(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
			    helper.AppendLineIndented(sb, "protected void MapCollection(BlokReader reader)", level);
			    helper.AppendLineIndented(sb, "{", level++);
			    helper.AppendLineIndented(sb, name + " entity = new " + name + "();", level);
			    helper.AppendLineIndented(sb, "while(entity.MapRecord(reader))", level);
			    helper.AppendLineIndented(sb, "{", level++);
			    helper.AppendLineIndented(sb, "this.add(entity);", level);
			    helper.AppendLineIndented(sb, "entity = new " + name + "();", level);
			    helper.AppendLineIndented(sb, "}", --level);
		        helper.AppendLineIndented(sb, "}", --level);
			    helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	    public int writeCollectionClass_SortMethods(String name, StringBuffer sb, int level, GeneratorHelpers helper)
	    {
	    	try
	    	{
			    helper.AppendLineIndented(sb, "public void Sort(String columnName)", level);    	    	
			    helper.AppendLineIndented(sb, "{", level++);
			    helper.AppendLineIndented(sb, "Collections.sort(this, sortBy(columnName));", level);    	    	
				helper.AppendLineIndented(sb, "}", --level);
			    helper.AppendLineIndented(sb, String.format("public static Comparator<%s> sortBy(final String columnName)", name), level);
			    helper.AppendLineIndented(sb, "{", level++);
			    helper.AppendLineIndented(sb, String.format("Comparator comp = new Comparator<%s>(){", name), level++);
			    helper.AppendLineIndented(sb, "@Override", level);
			    helper.AppendLineIndented(sb, String.format("public int compare(%s c1, %s c2){", name, name), level++);
			    helper.AppendLineIndented(sb, "return c1.GetColumn(columnName).compareTo(c2.GetColumn(columnName));", level);
			    helper.AppendLineIndented(sb, "}", --level);
			    helper.AppendLineIndented(sb, "};", --level);
			    helper.AppendLineIndented(sb, "return comp;", level);
			    helper.AppendLineIndented(sb, "}", --level);
			    helper.AppendLine(sb);
			}
			catch(Exception e)
			{
		        System.out.println("Failed: writeClass_GetColumnByName: \n" + e.getMessage());
		        e.printStackTrace();			
			}
	        return level;   	
	    }
	}
	public class View
	{
	//public void writeViewClass(ModelProvider.BlokReader reader, String name, String id)
    //{
    //    int level = 0;
    //    String dataType = "";

    //    String fileName = destinationFolder + @"\Entities\" + name + @".cs";
    //    StringBuilder sb = new StringBuilder();
    //    StreamWriter file = new StreamWriter(fileName);

    //    /* CREATE CLASS HEADER */
    //    helper.AppendLineIndented(sb, @"using System;", level);
    //    helper.AppendLineIndented(sb, @"using System.Data;", level);
    //    helper.AppendLineIndented(sb, @"using System.Text;", level);
    //    helper.AppendLineIndented(sb, @"using System.Data.SqlClient;", level);

    //    helper.AppendLineIndented(sb, @"namespace model", level);

    //    sb.AppendLine(@"{");
    //    helper.AppendLine(sb, helper.indent(++level) + "public partial class " + name + " : ModelViewBase");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");

    //    /* CREATE DECLARATIONS */
    //    level++;
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Declarations");
    //    for (int i = 1; i <= reader.Reader.FieldCount; i++)
    //    {
    //        dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
    //        String fieldName = ParseFieldName(reader.Reader.GetName(i));
    //        String fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldName);
    //        fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldNameInitialLower);
    //        helper.AppendLine(sb, helper.indent(level) + dataType + @" " + fieldNameInitialLower + @";");
    //    }
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE CONSTRUCTORS */
    //    helper.AppendLineIndented(sb, @"#region Constructors", level);
    //    helper.AppendLine(sb, helper.indent(level) + @"public " + name + @"()");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");
    //    helper.AppendLine(sb, helper.indent(++level) + "ViewName = \"" + name + "\";");
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");
    //    helper.AppendLine(sb);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE ACCESSOR/MUTATOR METHODS */
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Accessor/Mutator Methods");

    //    for (int i = 1; i <= reader.Reader.FieldCount; i++)
    //    {
    //        dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
    //        String fieldName = ParseFieldName(reader.Reader.GetName(i));
    //        String fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldName);
    //        String fieldNameInitialUpper = ReplaceFirstCharacterToUpperInvariant(fieldName);

    //        helper.AppendLine(sb, helper.indent(level) + "public " + dataType + @" " + fieldNameInitialUpper + @"{");
    //        AddAccessorMutator(dataType, fieldNameInitialLower, ref sb, ref level);
    //        helper.AppendLine(sb, helper.indent(level--) + @"set{ " + fieldNameInitialLower + @" = value;}");
    //        helper.AppendLine(sb, helper.indent(level) + "}");
    //    }
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE MAPRECORD METHOD */
    //    helper.AppendLineIndented(sb, @"#region Methods", level);
    //    helper.AppendLineIndented(sb, @"public override bool MapRecord(ModelProvider.BlokReader reader)", level);
    //    helper.AppendLineIndented(sb, @"{", level++);
    //    helper.AppendLineIndented(sb, @"if (!reader.Read())", level);
    //    helper.AppendLineIndented(sb, @"{", level++);
    //    helper.AppendLineIndented(sb, @"return false;", level);
    //    helper.AppendLineIndented(sb, @"}", --level);
    //    for (int i = 1; i <= reader.Reader.FieldCount; i++)
    //    {
    //        dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
    //        String fieldName = reader.Reader.GetName(i);
    //        String parsedName = ParseFieldName(fieldName);
    //        String fieldNameInitialUpper = ReplaceFirstCharacterToUpperInvariant(parsedName);
    //        helper.AppendLineIndented(sb, "if(NotNull(reader[\"" + fieldName + "\"])) " + fieldNameInitialUpper + @" = (" + dataType + ") reader[\"" + fieldName + "\"];", level);
    //    }
    //    helper.AppendLineIndented(sb, @"return true;", level);
    //    helper.AppendLineIndented(sb, @"}", --level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE FIELDS SUBCLASS */
    //    StringBuilder sbFieldGenerator = new StringBuilder();

    //    helper.AppendLineIndented(sbFieldGenerator, "#region Classes", level);
    //    helper.AppendLineIndented(sbFieldGenerator, @"public static partial class Fields ", level);
    //    helper.AppendLineIndented(sbFieldGenerator, @"{", level++);
    //    for (int i = 1; i <= reader.Reader.FieldCount; i++)
    //    {
    //        ModelField.DataType dataTypeEnum = TypeTranslationEnum(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
    //        String fieldName = reader.Reader.GetName(i);
    //        String parsedName = ParseFieldName(fieldName);
    //        String fieldNameInitialUpper = ReplaceFirstCharacterToUpperInvariant(parsedName);

    //        helper.AppendLineIndented(sbFieldGenerator, "public static ModelField " + fieldNameInitialUpper + " = new ModelField(\"" +
    //            fieldNameInitialUpper + "\", ModelField.ComparisonOperator.empty, null, ModelField.DataType." + dataTypeEnum + ");", level);
    //    }
    //    helper.AppendLineIndented(sbFieldGenerator, @"}", --level);
    //    helper.AppendLineIndented(sbFieldGenerator, @"#endregion", level);

    //    sb.Append(sbFieldGenerator.toString());
    //    helper.AppendLine(sb);

    //    /* CREATE CLASS FOOTER */
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");

    //    file.WriteLine(sb.toString());
    //    file.Flush();
    //    file.Close();
    //}
	}
	public class ViewCollection
	{
    //public void writeViewCollectionClass(ModelProvider.BlokReader reader, String name)
    //{
    //    int level = 0;
    //    String dataType = "";

    //    String fileName = destinationFolder + @"\Collections\" + name + @"Collection.cs";
    //    StringBuilder sb = new StringBuilder();
    //    StreamWriter file = new StreamWriter(fileName);

    //    /* CREATE CLASS HEADER */
    //    helper.AppendLineIndented(sb, @"using System;", level);
    //    helper.AppendLineIndented(sb, @"using System.Data;", level);
    //    helper.AppendLineIndented(sb, @"using System.Collections.Generic;", level);
    //    helper.AppendLineIndented(sb, @"namespace model", level);

    //    sb.AppendLine(@"{");
    //    helper.AppendLine(sb, helper.indent(++level) + "public partial class " + name + "Collection : ModelCollectionBase<" + name + ">");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");

    //    /* CREATE DECLARATIONS */
    //    level++;
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Declarations");
    //    helper.AppendLineIndented(sb, @"private List<" + name + "> collection = new List<" + name + ">();", level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE CONSTRUCTOR */
    //    helper.AppendLineIndented(sb, @"#region Constructors", level);
    //    helper.AppendLine(sb, helper.indent(level) + @"public " + name + @"Collection()");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");
    //    helper.AppendLine(sb, helper.indent(++level) + "EntityName = \"" + name + "\";");
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE ACCESSOR/MUTATOR METHODS */
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Accessor/Mutator Methods");
    //    helper.AppendLineIndented(sb, @"public List<" + name + "> Collection", level);
    //    helper.AppendLineIndented(sb, @"{", level);
    //    helper.AppendLineIndented(sb, @"get { return collection; }", level);
    //    helper.AppendLineIndented(sb, @"set { collection = value; }", level);
    //    helper.AppendLineIndented(sb, @"}", level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE MAPCOLLECTION METHOD */
    //    helper.AppendLineIndented(sb, @"#region Methods", level);
    //    helper.AppendLineIndented(sb, @"protected override void MapCollection(ModleProvider.BlokReader reader)", level);
    //    helper.AppendLineIndented(sb, @"{", level++);
    //    helper.AppendLineIndented(sb, name + @" entity = new " + name + "();", level);
    //    helper.AppendLineIndented(sb, @"while(entity.MapRecord(reader))", level);
    //    helper.AppendLineIndented(sb, @"{", level++);
    //    //helper.AppendLineIndented(sb, @"Collection.Add(entity);", level);
    //    helper.AppendLineIndented(sb, @"this.Add(entity);", level);
    //    helper.AppendLineIndented(sb, @"entity = new " + name + "();", level);
    //    helper.AppendLineIndented(sb, @"}", --level);
    //    helper.AppendLineIndented(sb, @"}", --level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE CLASS FOOTER */
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");

    //    file.WriteLine(sb.toString());
    //    file.Flush();
    //    file.Close();
    //}
	}
	public class Procedure
	{
    //public void writeProcClass(ModelProvider.BlokReader reader, String name, String id, List<OrmGenDashboard.Parameter> parms)
    //{
    //    int level = 0;
    //    String dataType = "";

    //    String fileName = destinationFolder + @"\Procedures\" + name + @".cs";
    //    StringBuilder sb = new StringBuilder();
    //    StreamWriter file = new StreamWriter(fileName);

    //    /* CREATE CLASS HEADER */
    //    helper.AppendLineIndented(sb, @"using System;", level);
    //    helper.AppendLineIndented(sb, @"using System.Data;", level);
    //    helper.AppendLineIndented(sb, @"using System.Data.SqlClient;", level);
    //    helper.AppendLineIndented(sb, @"using System.Collections.Generic;", level);
    //    helper.AppendLineIndented(sb, @"namespace model", level);

    //    sb.AppendLine(@"{");
    //    helper.AppendLine(sb, helper.indent(++level) + "public partial class " + name +  " : ModelProcedureBase");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");

    //    /* CREATE DECLARATIONS */
    //    level++;
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Declarations");
    //    sb.AppendLine(BuildResultRow(reader, level));
    //    helper.AppendLineIndented(sb, @"private List<resultRow> resultSet = new List<resultRow>();", level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE CONSTRUCTOR */
    //    helper.AppendLineIndented(sb, @"#region Constructors", level);
    //    helper.AppendLine(sb, helper.indent(level) + @"public " + name + @"()");
    //    helper.AppendLine(sb, helper.indent(level) + @"{");
    //    helper.AppendLine(sb, helper.indent(level) + @"}");
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE ACCESSOR/MUTATOR METHODS */
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Accessor/Mutator Methods");
    //    helper.AppendLineIndented(sb, @"public List<resultRow> ResultSet", level);
    //    helper.AppendLineIndented(sb, @"{", level);
    //    helper.AppendLineIndented(sb, @"get { return resultSet; }", level);
    //    helper.AppendLineIndented(sb, @"set { resultSet = value; }", level);
    //    helper.AppendLineIndented(sb, @"}", level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);
    //    helper.AppendLine(sb);

    //    /* CREATE CLASS BODY */
    //    helper.AppendLine(sb, helper.indent(level) + @"#region Methods");
    //    sb.Append(GenExecute(parms, level));
    //    //helper.AppendLineIndented(sb, @"public bool Execute()", level);
    //    helper.AppendLineIndented(sb, @"{", level++);
    //    helper.AppendLineIndented(sb, @"StringBuilder CmdSql = new StringBuilder();", level);
    //    helper.AppendLineIndented(sb, "CmdSql.Append(\"exec " + name + "\");", level);
    //    helper.AppendLineIndented(sb, ReturnReaderForCmdSql(targetProvider), level);
    //    helper.AppendLineIndented(sb, "resultRow row = new resultRow();", level);
    //    helper.AppendLineIndented(sb, "while (row.MapRecord(reader))", level);
    //    helper.AppendLineIndented(sb, "{", level++);
    //    helper.AppendLineIndented(sb, "resultSet.Add(row);", level);
    //    helper.AppendLineIndented(sb, "row = new resultRow();", level);
    //    helper.AppendLineIndented(sb, "}", --level);
    //    helper.AppendLineIndented(sb, "return true;", level);
    //    helper.AppendLineIndented(sb, @"}", --level);
    //    helper.AppendLineIndented(sb, @"#endregion", level);

    //    /* CREATE CLASS FOOTER */
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");
    //    helper.AppendLine(sb, helper.indent(--level) + @"}");

    //    file.WriteLine(sb.toString());
    //    file.Flush();
    //    file.Close();
    //}
	}	
}
