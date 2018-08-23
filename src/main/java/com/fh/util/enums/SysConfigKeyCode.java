package com.fh.util.enums;

/**
 * 配置表
* @ClassName: SysConfig
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年6月22日
*
 */
public class SysConfigKeyCode {
	//定义系统区间
	public static final String SystemDataTime = "SystemDataTime";
	
	//计算税率（）里的汇总公式
	public static final String StaffFormulaSalary = "StaffFormulaSalary";
	public static final String StaffFormulaBonus = "StaffFormulaBonus";

	//免征税
	public static final String ExemptionTax = "ExemptionTax";
	
	//有权限导出表的部门
	public static final String CanExportTable = "CanExportTable";
    //不导出数据的部门
	public static final String DepartNotExportData = "UnitsNotExportData";
    //不导出数据的二级单位
	public static final String UnitsNotExportData = "UnitsNotExportData";
    //导出数据的员工组
	public static final String GroupIsExportData = "GroupIsExportData";
	
	//定义核算汇总字段
	//核算合同化工资汇总
	public static final String ChkStaffContractGRP = "ChkStaffContractGRP";
	//核算市场化工资汇总
	public static final String ChkStaffMarketGRP = "ChkStaffMarketGRP";
	//核算运行人员工资汇总
	public static final String ChkStaffOperLaborGRP = "ChkStaffOperLaborGRP";
	//核算系统内工资汇总
	public static final String ChkStaffSysLaborGRP = "ChkStaffSysLaborGRP";
	//核算劳务派遣工资汇总
	public static final String ChkStaffLaborGRP = "ChkStaffLaborGRP";
	//社保核算汇总
	public static final String ChkSocialGRP = "ChkSocialGRP";
	//公积金核算汇总
	public static final String ChkHouseGRP = "ChkHouseGRP";
	

	//定义导入汇总字段
	//合同化工资汇总
	public static final String ContractGRPCond = "ContractGRPCond";
	//市场化工资汇总
	public static final String MarketGRPCond = "MarketGRPCond";
	//运行人员工资汇总
	public static final String OperLaborGRPCond = "OperLaborGRPCond";
	//系统内工资汇总
	public static final String SysLaborGRPCond = "SysLaborGRPCond";
	//劳务派遣工资汇总
	public static final String LaborGRPCond = "LaborGRPCond";
	//社保汇总
	public static final String SocialIncGRPCond = "SocialIncGRPCond";
	//公积金汇总
	public static final String HouseFundGRPCond = "HouseFundGRPCond";
	
	//
	//个人所得税列编码
	public static final String IndividualIncomeTax = "IndividualIncomeTax";
	//全年一次性奖金税列编码
	public static final String BonusIncomeTax = "BonusIncomeTax";
	
}
