package com.fh.util.enums;

//凭证字典
public enum PZTYPE {
	GFZYJF("PZ01","工会教育经费"),
	DF("PZ02","党费"),
	SB("PZ03","社保"),
	GJJ("PZ04","公积金"),
	GJ("PZ05","个缴"),
	YFLWF("PZ06","应付劳务费"),
	QYNJTQ("PZ07","企业年金提取"),
	BCYLTQ("PZ08","补充医疗提取"),
	QYNJFF("PZ09","企业年金发放"),
	PGTZ("PZ10","评估调整");

	private String nameKey;

    private String nameValue;
    
    
    private PZTYPE(String nameKey, String nameValue) {
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
    	PZTYPE[] enums = PZTYPE.values();  
        for (int i = 0; i < enums.length; i++) {  
            if (enums[i].getNameKey().equals(key)) {  
                return enums[i].getNameValue();  
            }  
        }  
        return "";  
    }  
	
}
