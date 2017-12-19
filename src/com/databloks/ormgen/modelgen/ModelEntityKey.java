package com.databloks.ormgen.modelgen;

public class ModelEntityKey
{
    String keyDataType;
    String keyName;
    int keyOrder;
    boolean keySortDescending;

    public String getKeyDataType(){ return this.keyDataType; }
    public void setKeyDataType(String keyDataType){ this.keyDataType = keyDataType; }
    public String getKeyName(){ return this.keyName; }
    public void setKeyName(String keyName){ this.keyName = keyName; }
    public int getKeyOrder(){ return this.keyOrder; }
    public void setKeyOrder(int keyOrder){ this.keyOrder = keyOrder; }
    public boolean getKeySortDescending(){ return this.keySortDescending; }
    public void setKeySortDescending(boolean keySortDescending){ this.keySortDescending = keySortDescending; }
}
