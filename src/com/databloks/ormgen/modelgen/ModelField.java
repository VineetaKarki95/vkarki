package com.databloks.ormgen.modelgen;

public class ModelField {
    public enum ComparisonOperator { 
        equal
        ,notEqual
        ,lessThan
        ,lessThanOrEqual
        ,greaterThan
        ,greaterThanOrEqual
        ,isNull
        ,isNotNull
        ,isIn
        ,isNotIn
        ,isEmpty
        ,isNotEmpty
    }
    public enum DataType
    {
        typeInt
        ,typeLong
        ,typeString
        ,typeDateTime
        ,typeBool
        ,typeUnknown
    }
    public enum GroupParmsDirective
    {
        none
        ,openGroup
        ,closeGroup
    }
    public enum ConjoinType
    {
        none
        ,and
        ,or
        ,not
    }
    DataType fieldDataType;
    String fieldName;
    String fieldValue;
    ComparisonOperator fieldComparisionOperator;
    GroupParmsDirective groupDirective;
    ConjoinType conjoiner;

    public ModelField(String FieldName, ComparisonOperator FieldComparisonOperator, String FieldValue, DataType FieldDataType)
    {
        fieldName = FieldName;
        fieldComparisionOperator = FieldComparisonOperator;
        fieldValue = FieldValue;
        fieldDataType = FieldDataType;
        groupDirective = GroupParmsDirective.none;
        conjoiner = ConjoinType.and;
    }
    public ModelField(String FieldName, ComparisonOperator FieldComparisonOperator, String FieldValue, DataType FieldDataType, 
        GroupParmsDirective GroupDirective, ConjoinType Conjoiner)
    {
        fieldName = FieldName;
        fieldComparisionOperator = FieldComparisonOperator;
        fieldValue = FieldValue;
        fieldDataType = FieldDataType;
        groupDirective = GroupDirective;
        conjoiner = Conjoiner;
    }
    public ModelField(ModelField Field)
    {
        fieldName = Field.getFieldName();
        fieldComparisionOperator = Field.getFieldComparisonOperator();
        fieldValue = Field.getFieldValue();
        fieldDataType = Field.getFieldDataType();
        groupDirective = GroupParmsDirective.none;
        conjoiner = ConjoinType.and;
    }


    public DataType getFieldDataType(){ return this.fieldDataType; }
    public void setFieldDataType(DataType fieldDataType){ this.fieldDataType = fieldDataType; }
    public String getFieldName(){ return this.fieldName; }
    public void setFieldName(String fieldName){ this.fieldName = fieldName; }
    public String getFieldValue(){ return this.fieldValue; }
    public void setFieldValue(String fieldValue){ this.fieldValue = fieldValue; }
    public ComparisonOperator getFieldComparisonOperator(){ return this.fieldComparisionOperator; }
    public void setFieldComparisonOperator(ComparisonOperator fieldComparisionOperator){ this.fieldComparisionOperator = fieldComparisionOperator; }
    public GroupParmsDirective getGroupDirective(){ return this.groupDirective; }
    public void setGroupDirective(GroupParmsDirective groupDirective){ this.groupDirective = groupDirective; }
    public ConjoinType getConjoiner(){ return this.conjoiner; }
    public void setConjoiner(ConjoinType conjoiner){ this.conjoiner = conjoiner; }

}
