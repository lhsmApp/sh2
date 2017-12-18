package com.fh.util.enums;

public enum TmplTypeTransfer {

	TB_STAFF_TRANSFER_CONTRACT("16","合同化工资传输表"),
	TB_STAFF_TRANSFER_MARKET("17","市场化工资传输表"),
	TB_STAFF_TRANSFER_SYS_LABOR("18","系统内劳务工资传输表"),
	TB_STAFF_TRANSFER_OPER_LABOR("19","运行人员工资传输表"),
	TB_STAFF_TRANSFER_LABOR("20","劳务派遣工资传输表"),
	
	TB_SOCIAL_INC_TRANSFER("24","社保传输表"),
	TB_HOUSE_FUND_TRANSFER("28","公积金传输表");

	
	private String nameKey;

    private String nameValue;
    
    
    private TmplTypeTransfer(String nameKey, String nameValue) {
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
    	TmplType[] enums = TmplType.values();  
        for (int i = 0; i < enums.length; i++) {  
            if (enums[i].getNameKey().equals(key)) {  
                return enums[i].getNameValue();  
            }  
        }  
        return "";  
    }  
	
}
