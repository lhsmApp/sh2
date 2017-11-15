package com.fh.util.enums;

public enum TmplType {
	TB_STAFF_DETAIL_CONTRACT("1","合同化工资明细导入表"),
	TB_STAFF_DETAIL_MARKET("2","市场化工资明细导入表"),
	TB_STAFF_DETAIL_SYS_LABOR("3","系统内劳务工资明细导入表"),
	TB_STAFF_DETAIL_OPER_LABOR("4","运行人员工资明细导入表"),
	TB_STAFF_DETAIL_LABOR("5","劳务派遣工资明细导入表"),
	
	TB_STAFF_SUMMY_CONTRACT("6","合同化工资汇总表"),
	TB_STAFF_SUMMY_MARKET("7","市场化工资汇总表"),
	TB_STAFF_SUMMY_SYS_LABOR("8","系统内劳务工资汇总表"),
	TB_STAFF_SUMMY_OPER_LABOR("9","运行人员工资汇总表"),
	TB_STAFF_SUMMY_LABOR("10","劳务派遣工资汇总表"),
	
	TB_STAFF_AUDIT_CONTRACT("11","合同化工资核算表"),
	TB_STAFF_AUDIT_MARKET("12","市场化工资核算表"),
	TB_STAFF_AUDIT_SYS_LABOR("13","系统内劳务工资核算表"),
	TB_STAFF_AUDIT_OPER_LABOR("14","运行人员工资核算表"),
	TB_STAFF_AUDIT_LABOR("15","劳务派遣工资核算表"),
	
	TB_STAFF_TRANSFER_CONTRACT("16","合同化工资传输表"),
	TB_STAFF_TRANSFER_MARKET("17","市场化工资传输表"),
	TB_STAFF_TRANSFER_SYS_LABOR("18","系统内劳务工资传输表"),
	TB_STAFF_TRANSFER_OPER_LABOR("19","运行人员工资传输表"),
	TB_STAFF_TRANSFER_LABOR("20","劳务派遣工资传输表"),
	
	TB_SOCIAL_INC_DETAIL("21","社保明细导入表"),
	TB_SOCIAL_INC_SUMMY("22","社保汇总表"),
	TB_SOCIAL_INC_AUDIT("23","社保核算表"),
	TB_SOCIAL_INC_TRANSFER("24","社保传输表"),
	
	TB_HOUSE_FUND_DETAIL("25","公积金明细导入表"),
	TB_HOUSE_FUND_SUMMY("26","公积金汇总表"),
	TB_HOUSE_FUND_AUDIT("27","公积金核算表"),
	TB_HOUSE_FUND_TRANSFER("28","公积金传输表");
	private String nameKey;

    private String nameValue;
    
    
    private TmplType(String nameKey, String nameValue) {
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
