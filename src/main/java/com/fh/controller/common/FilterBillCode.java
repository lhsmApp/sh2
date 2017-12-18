package com.fh.controller.common;

import com.fh.util.enums.BillState;

/**
 * 单号
* @ClassName: FilterBillCode
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年8月21日
*
 */
public class FilterBillCode {
	
    //汇总单据状态不为0，就是没汇总或汇总但没作废
	public static String getBillCodeNotInSumInvalidBill(){
		String strReturn = " and BILL_STATE not in ('" + BillState.Invalid.getNameKey() + "') ";
		return strReturn;
	}
	
    //汇总单据状态不为0，就是没汇总或汇总但没作废
	public static String getBillCodeNotInSumInvalidDetail(String tableNameSummy){
		String strReturn = " and BILL_CODE not in (select BILL_CODE from " + tableNameSummy + " where BILL_STATE = '" + BillState.Invalid.getNameKey() + "') ";
		return strReturn;
	}
	
    //单据没汇总
	public static String getBillCodeNotSum(String tableNameSummy){
		String strReturn = " and BILL_CODE not in (select BILL_CODE from " + tableNameSummy + ") ";
		return strReturn;
	}
}
	