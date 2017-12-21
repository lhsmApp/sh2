package com.fh.controller.common;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fh.entity.TableColumns;
import com.fh.util.PageData;
import com.fh.util.enums.BillState;

/**
 * 
 * 
 * @ClassName: SqlInString
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 2017年8月18日
 *
 */
public class QueryFeildString {
	
	public static String getQueryFeild(PageData pd, List<String> feildList) throws Exception{
		//业务日期:"BUSI_DATE"
		//责任中心oa_department:"DEPT_CODE"
		//企业特定员工分类PARTUSERTYPE:"USER_CATG"
		//员工组EMPLGRP:"USER_GROP"
		//账套FMISACC:"CUST_COL7" 
		//工资范围SALARYRANGE:"SAL_RANGE"
		//二级单位oa_department:"UNITS_CODE"
		//组织单元文本字典ORGUNIT:"ORG_UNIT"
		String BUSI_DATE = "";
		String DEPT_CODE = ""; 
		String USER_CATG = "";
		String USER_GROP = "";
		String CUST_COL7 = "";
		String SAL_RANGE = "";
		String UNITS_CODE = "";
		String ORG_UNIT = "";
		if(feildList!=null && feildList.size()>0){
			if(feildList.contains("BUSI_DATE")){
				BUSI_DATE = pd.getString("BUSI_DATE");
			}
			if(feildList.contains("DEPT_CODE")){
				DEPT_CODE = pd.getString("DEPT_CODE");
			}
			if(feildList.contains("USER_CATG")){
				USER_CATG = pd.getString("USER_CATG");
			}
			if(feildList.contains("USER_GROP")){
				USER_GROP = pd.getString("USER_GROP");
			}
			if(feildList.contains("CUST_COL7")){
				CUST_COL7 = pd.getString("CUST_COL7");
			}
			if(feildList.contains("SAL_RANGE")){
				SAL_RANGE = pd.getString("SAL_RANGE");
			}
			if(feildList.contains("UNITS_CODE")){
				UNITS_CODE = pd.getString("UNITS_CODE");
			}
			if(feildList.contains("ORG_UNIT")){
				ORG_UNIT = pd.getString("ORG_UNIT");
			}
		}
		String QueryFeild = "";
		if(BUSI_DATE!=null && !BUSI_DATE.trim().equals("")){
			QueryFeild += " and BUSI_DATE like '%" + BUSI_DATE.trim() + "%' ";
		}
		if(DEPT_CODE!=null && !DEPT_CODE.trim().equals("")){
			String strIn = getSqlInString(DEPT_CODE);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and DEPT_CODE in (" + strIn + ") ";
			}
		}
		if(USER_CATG!=null && !USER_CATG.trim().equals("")){
			String strIn = getSqlInString(USER_CATG);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and USER_CATG in (" + strIn + ") ";
			}
		}
		if(USER_GROP!=null && !USER_GROP.trim().equals("")){
			String strIn = getSqlInString(USER_GROP);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and USER_GROP in (" + strIn + ") ";
			}
		}
		if(CUST_COL7!=null && !CUST_COL7.trim().equals("")){
			String strIn = getSqlInString(CUST_COL7);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and CUST_COL7 in (" + strIn + ") ";
			}
		}
		if(SAL_RANGE!=null && !SAL_RANGE.trim().equals("")){
			String strIn = getSqlInString(SAL_RANGE);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and SAL_RANGE in (" + strIn + ") ";
			}
		}
		if(UNITS_CODE!=null && !UNITS_CODE.trim().equals("")){
			String strIn = getSqlInString(UNITS_CODE);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and UNITS_CODE in (" + strIn + ") ";
			}
		}
		if(ORG_UNIT!=null && !ORG_UNIT.trim().equals("")){
			String strIn = getSqlInString(ORG_UNIT);
			if(strIn!=null && !strIn.equals("")){
				QueryFeild += " and ORG_UNIT in (" + strIn + ") ";
			}
		}
		return QueryFeild;
	}
	
	public static String getQueryFeildBillCodeDetail(String SelectedBillCode, String SelectNoBillCodeShow) throws Exception{
		String QueryFeild = "";
		if(SelectedBillCode!=null){
			if(SelectedBillCode.equals(SelectNoBillCodeShow)){
				SelectedBillCode = "";
			}
			if(SelectedBillCode!=null && !SelectedBillCode.trim().equals("")){
				QueryFeild += " and BILL_CODE = '" + SelectedBillCode + "' ";
			} else {
				QueryFeild += " and BILL_CODE like ' %' ";
			}
		}
		return QueryFeild;
	}
	
	public static String getQueryFeildBillCodeSummy(String SelectedBillCode, String SelectBillCodeAllShow, String SelectBillCodeDetailShow) throws Exception{
		String QueryFeild = "";
		if(SelectedBillCode!=null){
			if(!SelectedBillCode.equals(SelectBillCodeAllShow)){
				if(!SelectedBillCode.equals(SelectBillCodeDetailShow)){
					QueryFeild += " and BILL_CODE = '" + SelectedBillCode + "' ";
				} else {
					QueryFeild += " and BILL_CODE like ' %' ";
				}
			}
		}
		return QueryFeild;
	}
	
	public static String getNotReportBillCode(String BILL_TYPE, String RPT_DUR, String BILL_OFF, String SqlInRPT_DEPT) throws Exception{
		String strInRPT_DEPT = getSqlInString(SqlInRPT_DEPT);
		String strRet = " and BILL_CODE not in (SELECT bill_code FROM tb_sys_sealed_info "
				+ "                             WHERE state = '1' "
				+ "                             AND RPT_DUR = '" + RPT_DUR + "' "
				+ "                             AND RPT_DEPT in (" + strInRPT_DEPT + ") "
				+ "                             AND BILL_TYPE = '" + BILL_TYPE + "' "
				+ "                             AND BILL_OFF = '" + BILL_OFF + "') ";
		return strRet;
	}
	
	public static String getReportBillCode(String BILL_TYPE, String RPT_DUR, String BILL_OFF, String SqlInRPT_DEPT) throws Exception{
		String strInRPT_DEPT = getSqlInString(SqlInRPT_DEPT);
		String strRet = " and BILL_CODE in (SELECT bill_code FROM tb_sys_sealed_info "
				+ "                             WHERE state = '1' "
				+ "                             AND RPT_DUR = '" + RPT_DUR + "' "
				+ "                             AND RPT_DEPT in (" + strInRPT_DEPT + ") "
				+ "                             AND BILL_TYPE = '" + BILL_TYPE + "' "
				+ "                             AND BILL_OFF = '" + BILL_OFF + "') ";
		return strRet;
	}
	
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
	
    //
	public static String getCheckDetailBillCode(String tableNameSummy,
			String BILL_TYPE, String RPT_DUR, String BILL_OFF, String SqlInRPT_DEPT) throws Exception{
		String strReturn = " and (BILL_CODE in (select BILL_CODE from " + tableNameSummy + " where BILL_STATE = '" + BillState.Invalid.getNameKey() + "') "
				+ "               or "
				+ "               (1=1 " + getReportBillCode(BILL_TYPE, RPT_DUR, BILL_OFF, SqlInRPT_DEPT) + ") "
				+ "               ) ";
		return strReturn;
	}
	
    //
	public static String getCheckSummyBillCode(String BILL_TYPE, String RPT_DUR, String BILL_OFF, String SqlInRPT_DEPT) throws Exception{
		String strReturn = " and (BILL_STATE = '" + BillState.Invalid.getNameKey() + "' "
				+ "               or "
				+ "               (1=1 " + getReportBillCode(BILL_TYPE, RPT_DUR, BILL_OFF, SqlInRPT_DEPT) + ") "
				+ "               ) ";
		return strReturn;
	}
	
	
	
	
	
	
	
	

	public static String getDetailQueryFeild(PageData pd, List<String> SumFieldDetail, String keyExtra){
    	String strQueryFeild = "";
	    if(SumFieldDetail!=null && SumFieldDetail.size()>0){
	    	for(String feild : SumFieldDetail){
	    		strQueryFeild += " and " + feild + " = '" + pd.getString(feild + keyExtra) + "' ";
	    	}
	    }
		return strQueryFeild;
	}
	
	/**
	 * 
	 * 
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public static String getSqlInString(String strList) throws Exception {
		String strIn = "";
		if(strList!=null && !strList.trim().equals("")){
			String[] list = strList.replace(" ", "").split(",");
			for(String str : list){
				if(strIn!=null && !strIn.trim().equals("")){
					strIn += ",";
				}
				strIn += "'" + str +"'";
			}
		}
		return strIn;
	}
	
	public static String getSqlInString(List<PageData> pdList, String str, String strFeild, String strFeildExtra){
		StringBuilder ret = new StringBuilder();
		if(str==null) str = "";
		for(PageData val : pdList){
			if(!ret.toString().trim().equals("")){
				ret.append(",");
			}
			ret.append(" " + str + val.get(strFeild + strFeildExtra) + str + " ");
		}
		return ret.toString();
	}
	
	public static String getFieldSelectKey(List<String> keyListBase, String keyExtra) throws Exception{
		String strReturn = "";
		if(keyListBase!=null && keyListBase.size()>0){
			for(String each : keyListBase){
				strReturn += ", " + each + " " + each + keyExtra;
			}
		}
		return strReturn.trim();
	}
	
	public static String tranferListStringToGroupbyString(List<String> SumField){
		StringBuilder ret = new StringBuilder();
		for(String field : SumField){
			if(!ret.toString().trim().equals("")){
				ret.append(",");
			}
			ret.append(field);
		}
		return ret.toString();
	}

	public static String tranferListStringToKeyString(List<String> listField, String keyExtra){
		StringBuilder ret = new StringBuilder();
		for(String field : listField){
			if(!ret.toString().trim().equals("")){
				ret.append(",");
			}
			ret.append(field).append(", ").append(field).append(" ").append(field + keyExtra);
		}
		return ret.toString();
	}
	
	public static String tranferListValueToSqlInString(List<String> valueList){
		StringBuilder ret = new StringBuilder();
		for(String val : valueList){
			if(!ret.toString().trim().equals("")){
				ret.append(",");
			}
			ret.append("'" + val + "'");
		}
		return ret.toString();
	}
    public static List<String> tranferStringToList(String strFeild){
        List<String> list = new ArrayList<String>();
        if(strFeild != null && !strFeild.trim().equals("")){
            for(String t : strFeild.replace(" ", "").toUpperCase().split(",")){  
            	list.add(t);  
            } 
        }
        return list;
    }
    
    public static List<String> extraSumField(List<String> listSumField, List<String> listExtraField){
    	if(listSumField==null){
    		listSumField = new ArrayList<String>();
    	}
        if(listExtraField != null && listExtraField.size()>0){
            for(String t : listExtraField){  
    			if(!listSumField.contains(t.toUpperCase())) listSumField.add(t.toUpperCase());
            } 
        }
        return listSumField;
    }
	
	public static String tranferListValueToSelectString(Map<String, TableColumns> map_HaveColumnsList){
		StringBuilder ret = new StringBuilder();
		for(String val : map_HaveColumnsList.keySet()){
			if(!ret.toString().trim().equals("")){
				ret.append(",");
			}
			ret.append(val);
		}
		return ret.toString();
	}

}
