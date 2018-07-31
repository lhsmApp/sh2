package com.fh.controller.common;

import com.fh.util.enums.BillNumType;
import com.fh.util.enums.EmplGroupType;
import com.fh.util.enums.PZTYPE;
import com.fh.util.enums.SysConfirmInfoBillType;
import com.fh.util.enums.TmplType;

/**
 * 
 * 
 * @ClassName: Corresponding
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 2017年8月18日
 *
 */
public class Corresponding {
	/**
	 * 根据模板基本类型获取SysConfirmInfoBillType
	 * 
	 * @param which
	 * @return
	 */
	public static String getSysConfirmInfoBillTypeFromTmplType(String which) {
		String billType = "";
		if (which != null){
			if (which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
					//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_CONTRACT.getNameKey())
					|| which.equals(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey())
					|| which.equals(TmplType.TB_STAFF_AUDIT_CONTRACT.getNameKey())
					|| which.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
				billType = SysConfirmInfoBillType.STAFF_CONTRACT.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
					//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_MARKET.getNameKey())
					|| which.equals(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey())
					|| which.equals(TmplType.TB_STAFF_AUDIT_MARKET.getNameKey())
					|| which.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
				billType = SysConfirmInfoBillType.STAFF_MARKET.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
					//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_SYS_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_AUDIT_SYS_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
				billType = SysConfirmInfoBillType.STAFF_SYS_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
					//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_OPER_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_AUDIT_OPER_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
				billType = SysConfirmInfoBillType.STAFF_OPER_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())
					//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_AUDIT_LABOR.getNameKey())
					|| which.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
				billType = SysConfirmInfoBillType.STAFF_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_SOCIAL_INC_DETAIL.getNameKey())
					//|| which.equals(TmplType.TB_SOCIAL_INC_SUMMY_BILL.getNameKey())
					|| which.equals(TmplType.TB_SOCIAL_INC_SUMMY.getNameKey())
					|| which.equals(TmplType.TB_SOCIAL_INC_AUDIT.getNameKey())
					|| which.equals(TmplType.TB_SOCIAL_INC_TRANSFER.getNameKey())) {
				billType = SysConfirmInfoBillType.SOCIAL_INC.getNameKey();
			} else if (which.equals(TmplType.TB_HOUSE_FUND_DETAIL.getNameKey())
					//|| which.equals(TmplType.TB_HOUSE_FUND_SUMMY_BILL.getNameKey())
					|| which.equals(TmplType.TB_HOUSE_FUND_SUMMY.getNameKey())
					|| which.equals(TmplType.TB_HOUSE_FUND_AUDIT.getNameKey())
					|| which.equals(TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey())) {
				billType = SysConfirmInfoBillType.HOUSE_FUND.getNameKey();
			}
		}
		return billType;
	}

	/**
	 * 根据凭证类型获取单号前缀
	 * @return
	 * @throws Exception
	 */
	public static String getBillTypeFromPZTYPE(String SelectedTypeCode) {
		String strReturn = "";
		if (SelectedTypeCode != null && !SelectedTypeCode.trim().equals("")) { 
			if(SelectedTypeCode.equals(PZTYPE.GFZYJF.getNameKey())){
				strReturn = BillNumType.GZJF;// 1工会经费、教育经费凭证 
			} else if(SelectedTypeCode.equals(PZTYPE.DF.getNameKey())){
				strReturn = BillNumType.DFJT;// 2党费凭证
			}  else if(SelectedTypeCode.equals(PZTYPE.SB.getNameKey())){
				strReturn = BillNumType.SBHT;// 3社保互推凭证
			} else if(SelectedTypeCode.equals(PZTYPE.GJJ.getNameKey())){
				strReturn = BillNumType.ZHHT;// 4公积金互推凭证
			} else if(SelectedTypeCode.equals(PZTYPE.GJ.getNameKey())){
				strReturn = BillNumType.SBGJ;// 5个缴凭证
			} else if(SelectedTypeCode.equals(PZTYPE.YFLWF.getNameKey())){
				strReturn = BillNumType.YFLW;// 6应付劳务费凭证
			} else if(SelectedTypeCode.equals(PZTYPE.QYNJTQ.getNameKey())){
				strReturn = BillNumType.NJTQ;// 7企业年金提取凭证
			} else if(SelectedTypeCode.equals(PZTYPE.BCYLTQ.getNameKey())){
				strReturn = BillNumType.YLTQ;// 8补充医疗提取凭证
			} else if(SelectedTypeCode.equals(PZTYPE.QYNJFF.getNameKey())){
				strReturn = BillNumType.NJFF;// 9企业年金发放凭证
			} else if(SelectedTypeCode.equals(PZTYPE.PGTZ.getNameKey())){
				strReturn = BillNumType.PGTZ;// 10评估调整凭证
			}
		}
		return strReturn;
	}

	public static String getTypeCodeTransferFromTmplType(String which) throws Exception{
		String strReturn = "";
		if (which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
			//合同化
			strReturn = TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
			//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_MARKET.getNameKey())
			|| which.equals(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey())
			|| which.equals(TmplType.TB_STAFF_AUDIT_MARKET.getNameKey())
			|| which.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
			//市场化
			strReturn = TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
		//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_SYS_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_AUDIT_SYS_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
			//系统内劳务
			strReturn = TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
		//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_OPER_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_AUDIT_OPER_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
			//运行人员
			strReturn = TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())
		//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_AUDIT_LABOR.getNameKey())
		|| which.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
			//劳务派遣工资
			strReturn = TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey();
		} else if (which.equals(TmplType.TB_SOCIAL_INC_DETAIL.getNameKey())
				//|| which.equals(TmplType.TB_SOCIAL_INC_SUMMY_BILL.getNameKey())
				|| which.equals(TmplType.TB_SOCIAL_INC_SUMMY.getNameKey())
				|| which.equals(TmplType.TB_SOCIAL_INC_AUDIT.getNameKey())
				|| which.equals(TmplType.TB_SOCIAL_INC_TRANSFER.getNameKey())) {
			strReturn = TmplType.TB_SOCIAL_INC_TRANSFER.getNameKey();
		} else if (which.equals(TmplType.TB_HOUSE_FUND_DETAIL.getNameKey())
				//|| which.equals(TmplType.TB_HOUSE_FUND_SUMMY_BILL.getNameKey())
				|| which.equals(TmplType.TB_HOUSE_FUND_SUMMY.getNameKey())
				|| which.equals(TmplType.TB_HOUSE_FUND_AUDIT.getNameKey())
				|| which.equals(TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey())) {
			strReturn = TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey();
		}
		return strReturn;
	}

	/**
	 * 根据模板基本类型获取员工组编码
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static String getUserGroupTypeFromTmplType(String which) throws Exception {
		String emplGroupType = "";
		if (which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_CONTRACT.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
			emplGroupType = EmplGroupType.HTH.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_MARKET.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_MARKET.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
			emplGroupType = EmplGroupType.SCH.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_SYS_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_SYS_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.XTNLW.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_OPER_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_OPER_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.YXRY.getNameKey();
		} else if (which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())
				//|| which.equals(TmplType.TB_STAFF_SUMMY_BILL_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_AUDIT_LABOR.getNameKey())
				|| which.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.LWPQ.getNameKey();
		}
		return emplGroupType;
	}

	/**
	 * 凭证字典获取SysConfirmInfoBillType业务类型
	STAFF_CONTRACT("1","合同化工资传输"),//
	STAFF_MARKET("2","市场化工资传输"),
	STAFF_SYS_LABOR("3","运行人员工资传输"),
	STAFF_OPER_LABOR("4","劳务用工传输"),
	STAFF_LABOR("5","劳务人员在建传输"),
	SOCIAL_INC("6","社保传输"),
	HOUSE_FUND("7","公积金传输");
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	/*public static String getSysConfirmInfoBillTypeFromPZTYPE(String pzTYPE) throws Exception {
		String vocherType = "";// 数据库真实业务数据表
		if(pzTYPE != null){
			if (pzTYPE.equals(PZTYPE.GFZYJF.getNameKey())) {
				vocherType = SysConfirmInfoBillType.GFZYJF.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.DF.getNameKey())) {
				vocherType = SysConfirmInfoBillType.DF.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.SB.getNameKey())) {
				vocherType = SysConfirmInfoBillType.SB.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.GJJ.getNameKey())) {
				vocherType = SysConfirmInfoBillType.GJJ.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.GJ.getNameKey())) {
				vocherType = SysConfirmInfoBillType.GJ.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.YFLWF.getNameKey())) {
				vocherType = SysConfirmInfoBillType.YFLWF.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.QYNJTQ.getNameKey())) {
				vocherType = SysConfirmInfoBillType.QYNJTQ.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.BCYLTQ.getNameKey())) {
				vocherType = SysConfirmInfoBillType.BCYLTQ.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.QYNJFF.getNameKey())) {
				vocherType = SysConfirmInfoBillType.QYNJFF.getNameKey();
			} else if (pzTYPE.equals(PZTYPE.PGTZ.getNameKey())) {
				vocherType = SysConfirmInfoBillType.PGTZ.getNameKey();
			}
		}
		return vocherType;
	}*/

}
