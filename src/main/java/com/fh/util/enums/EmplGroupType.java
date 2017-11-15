package com.fh.util.enums;

public enum EmplGroupType {
	SCH("50210001","市场化"),
	HTH("50210002","合同化"),
	YXRY("50210003","运行人员"),
	LWPQ("50210004","劳务派遣"),
	XTNLW("50210005","系统内劳务");
	
	private String nameKey;

    private String nameValue;
    
    
    private EmplGroupType(String nameKey, String nameValue) {
    	this.nameKey = nameKey;
        this.setNameValue(nameValue);
	}

	

	public String getNameKey() {
		return nameKey;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}



	public String getNameValue() {
		return nameValue;
	}



	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}
	
	/** 
     * 根据key获取value 
     *  
     * @param key 
     *            : 键值key 
     * @return String 
     */  
    public static String getValueByKey(String key) {  
    	BillType[] enums = BillType.values();  
        for (int i = 0; i < enums.length; i++) {  
            if (enums[i].getNameKey().equals(key)) {  
                return enums[i].getNameValue();  
            }  
        }  
        return "";  
    }  
	
}
