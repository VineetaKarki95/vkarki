package com.databloks.ormgen;

import java.util.ArrayList;

import com.databloks.dal.BlokReader;
import com.databloks.ormgen.modelgen.ModelEntityKey;
import com.databloks.ormgen.modelgen.ModelEntityKeys;
import com.databloks.ormgen.modelgen.ModelField;
import com.databloks.ormgen.modelgen.ModelProvider;

public class GeneratorHelpers 
{	
	private ArrayList<String> reservedWordList;
	private DashboardManager dashboardManager;	
	public GeneratorHelpers(){}
	public GeneratorHelpers(DashboardManager dashboardManager)
	{
		reservedWordList = dashboardManager.getReservedWordList();
		this.dashboardManager = dashboardManager;
	}
    public String AddAccessorMutator(String cSharpDataType, String value, StringBuffer buf)
    {
    	String returnValue = "";
        switch (cSharpDataType)
        {
            default:
            	returnValue = "{ return " + value + ";}";
                break;
            case "String":
            	returnValue = "{ return (" + value + " != null) ? " + value + " : \"\";}";
                break;
            case "DateTime":
            	returnValue = "{ if(" + value + " == null) return DateTime.Now; else return " + value + ";}";
                break;
        }
        return returnValue;
    }
    public int AddAccessorMutator(String cSharpDataType, String value, StringBuffer buf, int level)
    {
        switch (cSharpDataType)
        {
            default:
                AppendLine(buf, indent(++level) + "{ return " + value + ";}");
                break;
            case "String":
                AppendLine(buf, indent(++level) + "{ return " + value + " ?? \"\";}");
                break;
            case "DateTime":
                AppendLine(buf, indent(++level) + "{ if(" + value + " == null) return DateTime.Now; else return " + value + ";}");
                break;
        }
        return level;
    }
    public void AppendLine(StringBuffer buf)
    {
        buf.append("\n");
    }
    public void AppendLine(StringBuffer buf, String s)
    {
        buf.append(s + "\n");
    }
    public void AppendLineIndented(StringBuffer buf, String s, int level)
    {
        buf.append(indent(level) + s + "\n");
    }
    private String AugmentReservedWord(String fieldName)
    {
        String augmentedFieldName = fieldName;
        for(String reservedWord : reservedWordList)
        {
            if(fieldName.toLowerCase().equals(reservedWord))
            {
                augmentedFieldName = fieldName + "ReservedWord";
                break;
            }
        }
        return augmentedFieldName;
    }
    public String GenConstructor(String className, ArrayList<ModelEntityKey> keys)
    {
        StringBuilder codeString = new StringBuilder();
        codeString.append("public " + className + "(");

        String separator = "";
        int keyIndex = 0;
        for(ModelEntityKey key : keys)
        {
        	String fieldName = key.getKeyName();
            String parsedName = ParseFieldName(fieldName);
            String castString = TypeTranslation(key.getKeyDataType(), dashboardManager.getTargetProvider());
            codeString.append(separator + castString + " " + fieldName);
            if (keyIndex == 0)
            {
                separator = ", ";
            }
            keyIndex++;
        }


        codeString.append(")");
        return codeString.toString();
    }
    public String GenMethodCall(ArrayList<ModelEntityKey> keys)
    {
        StringBuilder codeString = new StringBuilder();
        codeString.append("GetByKey(");

        String separator = "";
        int keyIndex = 0;
        for(ModelEntityKey key : keys)
        {
        	String fieldName = key.getKeyName();
            String parsedName = ParseFieldName(fieldName);
            codeString.append(separator + fieldName);
            if (keyIndex == 0)
            {
                separator = ", ";
            }
            keyIndex++;
        }


        codeString.append(")");
        return codeString.toString();
    }
    public String GenMethodInvoke(String methodName, ArrayList<ModelEntityKey> keys)
    {
    	return GenMethodInvoke(methodName, keys, "boolean");
    }
    public String GenMethodInvoke(String methodName, ArrayList<ModelEntityKey> keys, String methodReturnType)
    {
        StringBuffer codeString = new StringBuffer();
        codeString.append("public " + methodReturnType + " " + methodName + "(");

        String separator = "";
        int keyIndex = 0;
        for(ModelEntityKey key : keys)
        {
        	String fieldName = key.getKeyName();
            String parsedName = ParseFieldName(fieldName);
            String castString = TypeTranslation(key.getKeyDataType(), dashboardManager.getTargetProvider());
            codeString.append(separator + castString + " " + fieldName);
            if (keyIndex == 0)
            {
                separator = ", ";
            }
            keyIndex++;
        }


        codeString.append(")");
        return codeString.toString();
    }
    public String GenWhereCompositeKey(ModelEntityKeys keys, int level, boolean fromInstance, String bufferName)
    {
        StringBuffer codeString = new StringBuffer();
        AppendLineIndented(codeString, bufferName + ".append(\"where \");", level);
        String instanceQualifier = "";
        if(fromInstance)
        {
        	instanceQualifier = "this.";
        }
        String separator = "";
        int keyIndex = 0;
        String singleQuote = "";
        for(ModelEntityKey key : keys)
        {
        	String fieldName = key.getKeyName();
            String parsedName = ParseFieldName(fieldName);
            String fieldNameInitialLower = ReplaceFirstCharacterToLower(parsedName);
            String castString = TypeTranslation(key.getKeyDataType(), dashboardManager.getTargetProvider());
            singleQuote = TypeQualifier(castString);
            AppendLineIndented(codeString, bufferName + ".append(\"" + separator + fieldName + " = " + singleQuote +
                "\" + " + TypeTranslation(key.getKeyDataType(), dashboardManager.getTargetProvider(), instanceQualifier + fieldName, true) +
                		" + \"" + singleQuote + " \");", level);
            if (keyIndex == 0)
            {
                separator = "and ";
            }
            keyIndex++;
        }
        return codeString.toString();
    }
    public String indent(int level)
    {
        int INDENTSIZE = 4;
        String SPACE = " ";
        String s = "";
        for (int i = 0; i < level * INDENTSIZE; i++)
        {
            s += SPACE;
        }
        return s;
    }
    public String ParseFieldName(String fieldName)
    {
        String augmentedFieldName = AugmentReservedWord(fieldName);
        String parsedName = augmentedFieldName;
        if (Character.isDigit(parsedName.charAt(0)))
        {
            parsedName = "No" + parsedName;
        }
        parsedName = parsedName.replaceAll("#", "_");
        return parsedName;
    }
    public String ReplaceFirstCharacterToLower(String name)
    {
    	String firstChar = name.substring(0, 1);
    	String tail = name.substring(1);
        return String.format("%s%s", firstChar.toLowerCase(), tail);
    }
    public String ReplaceFirstCharacterToUpper(String name)
    {
    	String firstChar = name.substring(0, 1);
    	String tail = name.substring(1);
        return String.format("%s%s", firstChar.toUpperCase(), tail);
    }
    public void ReturnExecuteNonQueryForCmdSql(StringBuffer sb, int level)
    {
        AppendLineIndented(sb, "ModelProvider modelProvider = new ModelProvider();", level);
        AppendLineIndented(sb, "int rows = modelProvider.SqlExecuteNonQuery(modelProvider.getTargetProvider(), CmdSql.toString());", level);
    }    
    public void ReturnReaderForCmdSql(StringBuffer sb, int level)
    {
        AppendLineIndented(sb, "ModelProvider modelProvider = new ModelProvider();", level);
        AppendLineIndented(sb, "BlokReader reader = modelProvider.SqlDispatch(modelProvider.getTargetProvider(), CmdSql.toString());", level);
    }
    private String TypeQualifier(String cSharpDataType)
    {
        String qualifier = "";
        switch (cSharpDataType.toLowerCase())
        {
            default:
            case "int":
            case "tinyint":
            case "bigint":
            case "decimal":
            case "numeric":
                qualifier = "";
                break;
            case "varchar":
            case "char":
            case "String":
                qualifier = "'";
                break;
            case "datetime":
            case "date":
                qualifier = "'";
                break;
        }
        return qualifier;
    }
    public String TypeTranslation(String serverDataType, ModelProvider.providerType targetProvider)
    {
        String castString = "String";
        switch(targetProvider)
        {
            default:
            case msSqlServer:
                switch (serverDataType.toLowerCase())
                {
                    default:
                    case "varchar":
                    case "char":
                    case "String":
                        castString = "string";
                        break;
                    case "datetime":
                        castString = "DateTime";
                        break;
                    case "int":
                    case "tinyint":
                        castString = "int";
                        break;
                    case "bigint":
                        castString = "long";
                        break;
                    case "decimal":
                        castString = "decimal";
                        break;
                    case "numeric":
                        castString = "double";
                        break;
                    case "bit":
                        castString = "bool";
                        break;
                }
                break;
            case mySql:
                switch (serverDataType.toLowerCase())
                {
                    default:
                    case "varchar":
                    case "char":
                    case "String":
                        castString = "String";
                        break;
                    case "date":
                    case "datetime":
                        castString = "Date";
                        break;
                    case "int":
                    case "tinyint":
                        castString = "int";
                        break;
                    case "bigint":
                        castString = "long";
                        break;
                    case "decimal":
                        castString = "float";
                        break;
                    case "numeric":
                        castString = "double";
                        break;
                    case "bit":
                        castString = "boolean";
                        break;
                }
                break;
        }
        return castString;
    }
    public String TypeTranslation(
    		String serverDataType, 
    		ModelProvider.providerType targetProvider, 
    		String fieldNameToParse)
    {
    	String valueToParse = "reader.getReader().getString(\"" + fieldNameToParse + "\")";
        String parseString = valueToParse;
        switch(targetProvider)
        {
            default:
            case mySql:
            case msSqlServer:
                switch (serverDataType.toLowerCase())
                {
                    default:
                    case "varchar":
                    case "char":
                    case "string":
                    	parseString = valueToParse;
                        break;
                    case "date":          	
                    	parseString = "dateFormatYyyyMmDd.parse(" + valueToParse +")";
                        break;
                    case "datetime":          	
                    	parseString = "dateFormatYyyyMmDdHhMmSs.parse(" + valueToParse +")";
                        break;
                    case "int":
                    case "tinyint":
                    	parseString = "Integer.parseInt(" + valueToParse + ")";
                        break;
                    case "bigint":
                    	parseString = "Long.parseLong(" + valueToParse + ")";
                        break;
                    case "float":
                    case "decimal":
                    	parseString = "Float.parseFloat(" + valueToParse + ")";
                        break;
                    case "numeric":
                    	parseString = "Double.valueOf(" + valueToParse + ")";
                        break;
                    case "bit":
                    	parseString = "Boolean.parseBoolean(" + valueToParse + ")";
                        break;
                }
                break;
        }
        return parseString;
    }
    public String TypeTranslation(String serverDataType, ModelProvider.providerType targetProvider, String fieldNameToParse, boolean CastWrapper)
    {
    	if(!CastWrapper)
    	{
    		return TypeTranslation(serverDataType, targetProvider, fieldNameToParse);
    	}
    	String valueToParse = fieldNameToParse;
        String parseString = valueToParse;
        switch(targetProvider)
        {
            default:
            case msSqlServer:
            case mySql:
                switch (serverDataType.toLowerCase())
                {
                    default:
                    case "varchar":
                    case "char":
                    case "String":
                    	parseString = valueToParse;
                        break;
                    case "datetime":
                    	parseString = "((" + valueToParse + " != null) ? dateFormatYyyyMmDdHhMmSs.format(" + valueToParse + ") : \"\")" ; 
                        break;
                    case "date":
                    	parseString = "((" + valueToParse + " != null) ? dateFormatYyyyMmDd.format(" + valueToParse + ") : \"\")" ; 
                        break;
                    case "int":
                    case "tinyint":
                    	parseString = "Integer.toString(" + valueToParse + ")";
                        break;
                    case "bigint":
                    	parseString = "Long.toString(" + valueToParse + ")";
                        break;
                    case "decimal":
                    	parseString = "Float.toString(" + valueToParse + ")";
                        break;
                    case "numeric":
                    	parseString = "Double.toString(" + valueToParse + ")";
                        break;
                    case "bit":
                    	parseString = "Boolean.toString(" + valueToParse + ")";
                        break;
                }
                break;
        }
        return parseString;
    }
    public ModelField.DataType TypeTranslationEnum(String type, ModelProvider.providerType targetProvider)
    {
        ModelField.DataType dataType = ModelField.DataType.typeUnknown;

        switch (targetProvider)
        {
            default:
            case msSqlServer:
            case mySql:
                switch (type.toLowerCase())
                {
                    default:
                        dataType = ModelField.DataType.typeUnknown;
                        break;
                    case "varchar":
                    case "char":
                        dataType = ModelField.DataType.typeString;
                        break;
                    case "datetime":
                        dataType = ModelField.DataType.typeDateTime;
                        break;
                    case "tinyint":
                        dataType = ModelField.DataType.typeInt;
                        break;
                    case "bigint":
                        dataType = ModelField.DataType.typeLong;
                        break;
                    case "bit":
                        dataType = ModelField.DataType.typeBool;
                        break;
                }
                break;
        }
        return dataType;
    }    
}
//private String GenExecute(List<OrmGenDashboard.Parameter> parms, int level)
//{
//  StringBuilder codeString = new StringBuilder();            
//  if(parms == null)
//  {
//      AppendLineIndented(codeString, @"public bool Execute()", level);
//  }
//  else
//  {
//      StringBuilder argString = new StringBuilder();
//      AppendLineIndented(codeString, @"public bool Execute(", level++);
//      foreach(OrmGenDashboard.Parameter parm in parms)
//      {
//          if(argString.Length > 0)
//          {
//              argString.Append(", ");
//          }
//          argString.Append(parm.Type + " " + parm.Name);
//      }
//      AppendLineIndented(codeString, argString.ToString(), level);
//      AppendLineIndented(codeString, @")", --level);
//  }
//  return codeString.ToString();
//}
//private String BuildResultRow(ModelProvider.BlokReader reader, int level)
//{
//  StringBuilder resultlRow = new StringBuilder();
//
//  AppendLineIndented(resultlRow, "public class resultRow", level);
//  AppendLineIndented(resultlRow, "{", level++);
//
//  /* CREATE DECLARATIONS */
//  resultlRow.AppendLine(indent(level) + @"#region Declarations");
//  for (int i = 0; i < reader.Reader.FieldCount; i++)
//  {
//      String dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
//      String fieldName = ParseFieldName(reader.Reader.GetName(i));
//      String fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldName);
//      fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldNameInitialLower);
//      resultlRow.AppendLine(indent(level) + dataType + @" " + fieldNameInitialLower + @";");
//  }
//  AppendLineIndented(resultlRow, @"#endregion", level);
//  resultlRow.AppendLine();
//
//  /* CREATE ACCESSOR/MUTATOR METHODS */
//  resultlRow.AppendLine(indent(level) + @"#region Accessor/Mutator Methods");
//
//  for (int i = 0; i < reader.Reader.FieldCount; i++)
//  {
//      String dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
//      String fieldName = ParseFieldName(reader.Reader.GetName(i));
//      String fieldNameInitialLower = ReplaceFirstCharacterToLowerInvariant(fieldName);
//      String fieldNameInitialUpper = ReplaceFirstCharacterToUpperInvariant(fieldName);
//
//      resultlRow.AppendLine(indent(level) + "public " + dataType + @" " + fieldNameInitialUpper + @"{");
//      AddAccessorMutator(dataType, fieldNameInitialLower, ref resultlRow, ref level);
//      resultlRow.AppendLine(indent(level--) + @"set{ " + fieldNameInitialLower + @" = value;}");
//      resultlRow.AppendLine(indent(level) + "}");
//  }
//  AppendLineIndented(resultlRow, @"#endregion", level);
//  resultlRow.AppendLine();
//
//  /* CREATE MAPRECORD METHOD */
//  AppendLineIndented(resultlRow, @"#region Methods", level);
//  AppendLineIndented(resultlRow, @"public bool MapRecord(ModelProvider.BlokReader reader)", level);
//  AppendLineIndented(resultlRow, @"{", level++);
//  AppendLineIndented(resultlRow, @"if (!reader.Reader.Read())", level);
//  AppendLineIndented(resultlRow, @"{", level++);
//  AppendLineIndented(resultlRow, @"return false;", level);
//  AppendLineIndented(resultlRow, @"}", --level);
//  for (int i = 0; i < reader.Reader.FieldCount; i++)
//  {
//      String dataType = TypeTranslation(reader.Reader.GetDataTypeName(i), ModelProvider.TargetProvider);
//      String fieldName = reader.Reader.GetName(i);
//      String parsedName = ParseFieldName(fieldName);
//      String fieldNameInitialUpper = ReplaceFirstCharacterToUpperInvariant(parsedName);
//      AppendLineIndented(resultlRow, "if(ModelEntityBase.NotNull(reader.Reader[\"" + fieldName + "\"])) " + fieldNameInitialUpper + @" = (" + dataType + ") reader.Reader[\"" + fieldName + "\"];", level);
//  }
//  AppendLineIndented(resultlRow, @"return true;", level);
//  AppendLineIndented(resultlRow, @"}", --level);
//  AppendLineIndented(resultlRow, @"#endregion", level);
//  resultlRow.AppendLine();
//
//  AppendLineIndented(resultlRow, "}", --level);
//  return resultlRow.ToString();
//}
//#region Helper Methods
//public object NullAssign(object value, string type)
//{
//  object returnValue = value;
//  //System.Console.WriteLine(value.GetType());
//  if(value.GetType().Name == "DBNull")
//  {
//      switch (type)
//      {
//          default:
//              returnValue = "";
//              break;
//          case "varchar":
//          case "char":
//              returnValue = "";
//              break;
//          case "datetime":
//              returnValue = "";
//              break;
//          case "int":
//              returnValue = 0;
//              break;
//          case "tinyint":
//              returnValue = 0;
//              break;
//          case "bigint":
//              returnValue = 0;
//              break;
//      }
//  }
//  return returnValue;
//}
//public static bool NotNull(object value)
//{
//  if (value == DBNull.Value)
//  {
//      return false;
//  }
//  return true;
//}
//#endregion
