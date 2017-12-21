package com.fh.entity;

/**
* @ClassName: LaborDetail
* @Description: 劳务报酬所得导入
* @author 张晓柳
* @date 2017年12月17日
*
 */
public class LaborDetail {
	private int SERIAL_NO;
	private String USER_CODE;
	private String USER_NAME;
	private double GROSS_PAY;
	private double ACCRD_TAX;
	private double ACT_SALY;
	public int getSERIAL_NO() {
		return SERIAL_NO;
	}
	public void setSERIAL_NO(int sERIAL_NO) {
		SERIAL_NO = sERIAL_NO;
	}
	public String getUSER_CODE() {
		return USER_CODE;
	}
	public void setUSER_CODE(String uSER_CODE) {
		USER_CODE = uSER_CODE;
	}
	public String getUSER_NAME() {
		return USER_NAME;
	}
	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}
	public double getGROSS_PAY() {
		return GROSS_PAY;
	}
	public void setGROSS_PAY(double gROSS_PAY) {
		GROSS_PAY = gROSS_PAY;
	}
	public double getACCRD_TAX() {
		return ACCRD_TAX;
	}
	public void setACCRD_TAX(double aCCRD_TAX) {
		ACCRD_TAX = aCCRD_TAX;
	}
	public double getACT_SALY() {
		return ACT_SALY;
	}
	public void setACT_SALY(double aCT_SALY) {
		ACT_SALY = aCT_SALY;
	}
}
