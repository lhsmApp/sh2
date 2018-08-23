package com.fh.entity;

/**
 * 
* @ClassName: SysDeptLtdTime
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2018年8月14日
*
 */
public class SysDeptLtdTime {
	private String DEPT_CODE;
	private String DEPT_NAME;
	private String BUSI_TYPE;
	private String LTD_DAY;
	private String LTD_HOUR;
	public String getDEPT_NAME() {
		return DEPT_NAME;
	}
	public void setDEPT_NAME(String dEPT_NAME) {
		DEPT_NAME = dEPT_NAME;
	}
	public String getDEPT_CODE() {
		return DEPT_CODE;
	}
	public void setDEPT_CODE(String dEPT_CODE) {
		DEPT_CODE = dEPT_CODE;
	}
	public String getBUSI_TYPE() {
		return BUSI_TYPE;
	}
	public void setBUSI_TYPE(String bUSI_TYPE) {
		BUSI_TYPE = bUSI_TYPE;
	}
	public String getLTD_DAY() {
		return LTD_DAY;
	}
	public void setLTD_DAY(String lTD_DAY) {
		LTD_DAY = lTD_DAY;
	}
	public String getLTD_HOUR() {
		return LTD_HOUR;
	}
	public void setLTD_HOUR(String lTD_HOUR) {
		LTD_HOUR = lTD_HOUR;
	}
}
