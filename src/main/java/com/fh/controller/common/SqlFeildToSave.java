package com.fh.controller.common;

import java.util.List;

import com.fh.entity.ClsTwoFeild;
import com.fh.entity.SysStruMapping;
import com.fh.util.DateUtil;
import com.fh.util.Jurisdiction;
import com.fh.util.enums.BillState;

/**
 * 模板通用类
 * 
 * @ClassName: getSqlToSave
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhangxiaoliu
 * @date 2018年04月19日
 *
 */
public class SqlFeildToSave {

    //表结构where条件的开头和结尾
    private static String StruMappingWhereStartEndWith = "@";

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ClsTwoFeild getSqlFeildToSave(String SelectedCustCol7, String SelectedTypeCode, String SelectedDepartCode, String SystemDateTime, 
			String BILL_CODE, List<SysStruMapping> getSysStruMappingList) throws Exception {
		String strSelectFeild = "";
		String strWhere = "";
		if (getSysStruMappingList != null && getSysStruMappingList.size() > 0) { 
			for(SysStruMapping struMapping : getSysStruMappingList){
				String COL_CODE = struMapping.getCOL_CODE();
				String COL_MAPPING_CODE = struMapping.getCOL_MAPPING_CODE();
				String COL_VALUE = struMapping.getCOL_VALUE();
				String COL_MAPPING_VALUE = struMapping.getCOL_MAPPING_VALUE();
				if(COL_CODE!=null && !COL_CODE.trim().equals("")){
					if(strSelectFeild!=null && !strSelectFeild.trim().equals("")){
						strSelectFeild += ", ";
					}
					strSelectFeild += COL_CODE + " " + COL_MAPPING_CODE;
				} else {
					if(COL_MAPPING_VALUE!=null && !COL_MAPPING_VALUE.trim().equals("")){
						//COL_MAPPING_VALUE 
						//1）列作用：本列作用于凭证数据汇总功能；
						//2）变量说明：用于接收传入的常量，如单位、时间、帐套、单号的字符值。
						//a、@BILLNO@，接收单据号变量；
						//b、@DATE@，接收期间变量；
						//c、@STATE@，接收单据状态变量；
						//d、@USER@，接收用户变量；
						//e、@DEPART@,接收单位变量；
						if(strSelectFeild!=null && !strSelectFeild.trim().equals("")){
							strSelectFeild += ", ";
						}
						if(COL_MAPPING_VALUE.equals("@BILLOFF@")){
							strSelectFeild += " '" + SelectedCustCol7 + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@BILLNO@")){
							strSelectFeild += " '" + BILL_CODE + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@USER@")){
							strSelectFeild += " '" + Jurisdiction.getCurrentDepartmentID() + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@DATE@")){
							strSelectFeild += " '" + SystemDateTime + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@DEPART@")){
							strSelectFeild += " '" + SelectedDepartCode + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@CERTTYPE@")){
							strSelectFeild += " '" + SelectedTypeCode + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@STATE@")){
							strSelectFeild += " '" + BillState.Normal.getNameKey() + "' " + COL_MAPPING_CODE;
						} else if(COL_MAPPING_VALUE.equals("@LONGDATE@")){
							strSelectFeild += " '" + DateUtil.getTime() + "' " + COL_MAPPING_CODE;
						}
					}
				}
				if(COL_VALUE!=null && !COL_VALUE.trim().equals("")){
					if(strWhere!=null && !strWhere.trim().equals("")){
						strWhere += " and ";
					} else {
						strWhere += " where ";
					}
					if(COL_VALUE.endsWith(StruMappingWhereStartEndWith) && COL_VALUE.endsWith(StruMappingWhereStartEndWith)){
						//COL_VALUE
						//1）列作用：本列作于生成查询条件；
						//2）说明： 变量部分：
						//a、 @DEPARTMAP@，为单位映射变量；用于设置单位查询条件，查询SQL如下，TYPE_CODE凭证类型,BILL_OFF账套,DEPT_CODE单位，以上三条件均在汇总时根据选择的凭证类型，选择的帐套及以所选择操作的单位获得，以下示例为获取凭证1帐套9100机关01001单位的单位映射：
						//               SELECT mapping_code FROM tb_sys_dept_mapping WHERE  TYPE_CODE = '1'  AND BILL_OFF = '9100' AND DEPT_CODE = '01001'
						//b、@DATE@,接收传入的当前期间值做为条件；
						//c、@BILLNOMAP@，为已确认单据映射变量；用于设置单据编号查询条件，查询SQL如下：TYPE_CODE凭证类型,BILL_OFF账套,DEPT_CODE单位，以下示例 为获取凭证1帐套9100机关01001单位已确认完成的业务单号。
						//               SELECT  bill_code  FROM	tb_sys_confirm_info WHERE	rpt_dept IN ( SELECT mapping_code FROM tb_sys_dept_mapping WHERE  TYPE_CODE = '1'  AND BILL_OFF = '9100' AND DEPT_CODE = '01001'）AND state = '1'
						if(COL_VALUE.equals("@BILLNOMAP@")){
							strWhere += COL_CODE + " in (select BILL_CODE from TB_SYS_CONFIRM_INFO where STATE = '" + BillState.Normal.getNameKey() +"') ";
						} else if(COL_VALUE.equals("@DATE@")){
							strWhere += COL_CODE + " in ('" + SystemDateTime +"') ";
						} else if(COL_VALUE.equals("@DEPARTMAP@")){
							strWhere += COL_CODE + " in (SELECT mapping_code FROM tb_sys_dept_mapping WHERE TYPE_CODE = '" + SelectedTypeCode + "'  AND BILL_OFF = '" + SelectedCustCol7 + "' AND DEPT_CODE = '" + SelectedDepartCode + "') ";
						} else if(COL_VALUE.equals("@CERTTYPE@")){
							strWhere += COL_CODE + " in ('" + SelectedTypeCode + "') ";
						} else if(COL_VALUE.equals("@BILLOFF@")){
							strWhere += COL_CODE + " in ('" + SelectedCustCol7 + "') ";
						} 
						//@BILLNO@
						//
						//
					} else {
						strWhere += COL_CODE + " in (" + COL_VALUE +") ";
					}
				}
			}
		}
		ClsTwoFeild sqlFeild = new ClsTwoFeild();
		sqlFeild.setSqlSelectFeild(strSelectFeild);
		sqlFeild.setSqlWhere(strWhere);
		return sqlFeild;
	}
}
