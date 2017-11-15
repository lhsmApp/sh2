package com.fh.controller.common;

import java.util.List;
import java.util.Map;

import com.fh.entity.CommonBase;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.service.importdetail.importdetail.impl.ImportDetailService;
import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.util.PageData;
import com.fh.util.enums.BillState;
import com.fh.util.enums.DurState;

/**
 * 单号
* @ClassName: FilterBillCode
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年8月21日
*
 */
public class FilterBillCode {
	
	
	
	//复制插入数据：在凭证接口已上报过（凭证凭证有记录），汇总已上报过（汇总有记录）时,执行复制插入，并删掉汇总上报记录和明细上报记录和凭证上报记录，作废汇总记录
	public static CommonBase copyInsert(SysSealedInfoService syssealedinfoService, ImportDetailService importdetailService, 
			String DepartCode, String SystemDateTime, String CUST_COL7,
			String TypeCodeListen, String TypeCodeSummy, String TypeCodeDetail, 
			String TableNameSummy, String TableNameDetail,
			String emplGroupType,
			Map<String, TableColumns> map_HaveColumnsList, 
			Map<String, TmplConfigDetail> map_SetColumnsList) throws Exception{
		//, Boolean report
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		String strHelpful = "";
		PageData pdGetState = new PageData();
		pdGetState.put("RPT_DEPT", DepartCode);
		pdGetState.put("RPT_DUR", SystemDateTime);
		pdGetState.put("BILL_TYPE", TypeCodeListen);
		pdGetState.put("BILL_OFF", CUST_COL7);
		String stateListen = syssealedinfoService.getState(pdGetState);
		if(stateListen != null && !stateListen.equals("")){
			//重新汇总接口上报记录删掉，明细有改动汇总上报记录删掉
			//接口已上报过（接口有记录），汇总没上报过（汇总没记录），取未在汇总表中出现单号的记录
			pdGetState.put("BILL_TYPE", TypeCodeSummy);// 枚举  1工资明细,2工资汇总,3公积金明细,4公积金汇总,5社保明细,6社保汇总,7工资接口,8公积金接口,9社保接口
			String stateSummy = syssealedinfoService.getState(pdGetState);
			if(stateSummy != null && !stateSummy.equals("")){
				//汇总已上报过（汇总有记录），取汇总单据状态不为0的
				strHelpful += FilterBillCode.getBillCodeNotInSumInvalid(TableNameSummy);
				
				PageData pdGetList = new PageData();
				pdGetList.put("TableName", TableNameDetail);
				pdGetList.put("SystemDateTime", SystemDateTime);
				pdGetList.put("DepartCode", DepartCode);
				pdGetList.put("BILL_OFF", CUST_COL7);
				pdGetList.put("USER_GROP", emplGroupType);
				pdGetList.put("QueryFeild", strHelpful);
				List<PageData> getList = importdetailService.getCopyInsertList(pdGetList);
				if(!(getList != null && getList.size() > 0)){
					commonBase.setCode(2);
					commonBase.setMessage("没有可操作的数据！");
				} else {
					for(PageData each : getList){
						each.put("TableNameDetail", TableNameDetail);
						each.put("BILL_CODE", "");
						Common.setModelDefault(each, map_HaveColumnsList, map_SetColumnsList);

						each.put("TableNameSummy", TableNameSummy);
						if(emplGroupType != null && !emplGroupType.equals("")){
							each.put("EmplGroupType", emplGroupType);
						}
						
						each.put("TypeCodeSummy", TypeCodeSummy);
						each.put("TypeCodeDetail", TypeCodeDetail);
						each.put("TypeCodeListen", TypeCodeListen);
					}
					importdetailService.insertCopy(getList);
				}
			}
		}
		commonBase.setCode(0);
		return commonBase;
	}
	
	
	//导入界面的显示数据     
	public static String getExportViewShowList(SysSealedInfoService syssealedinfoService, 
			String DepartCode, String SystemDateTime, String CUST_COL7,
			String TypeCodeListen, String TypeCodeSummy, String TypeCodeDetail, 
			String TableNameSummy) throws Exception{
		String strHelpful = "";
		strHelpful += FilterBillCode.getBillCodeNotInSumInvalid(TableNameSummy);
		/*PageData pd = new PageData();
		pd.put("RPT_DEPT", DepartCode);
		pd.put("RPT_DUR", SystemDateTime);
		pd.put("BILL_TYPE", TypeCodeListen);
		pd.put("BILL_OFF", CUST_COL7);
		String stateListen = syssealedinfoService.getState(pd);
		//1、凭证接口没上报过（凭证接口没记录），分情况
		//2、凭证接口有记录，取汇总单据状态不为0的
		if(stateListen != null && !stateListen.equals("")){
			//2、凭证接口有记录，取汇总单据状态不为0的
			strHelpful += FilterBillCode.getBillCodeNotInSumInvalid(TableNameSummy);
		} else {
			//1、凭证接口没上报过（凭证接口没记录），分情况
			//1、1明细没上报过（明细没记录），取汇总单据中不存在的单号
			//1、2明细已上报过（明细有记录），取汇总单据状态不为0的
			pd.put("BILL_TYPE", TypeCodeDetail);
			String stateDetail = syssealedinfoService.getState(pd);
			if(!(stateDetail != null && !stateDetail.equals(""))){
				//明细没上报过（明细没记录），取汇总单据中不存在的单号
				strHelpful += FilterBillCode.getBillCodeNotInSummy(TableNameSummy);
			} else {
				//明细已上报过（明细有记录），取汇总单据状态不为0的
				strHelpful += FilterBillCode.getBillCodeNotInSumInvalid(TableNameSummy);
			}
		}*/
		return strHelpful;
	}
	
	
	//导入界面的显示数据 Detail
	/*public static String getDetailCanOperateCondition(SysSealedInfoService syssealedinfoService, 
			String DepartCode, String SystemDateTime, String CUST_COL7,
			String TypeCodeDetail, String TypeCodeSummy, String TableNameSummy) throws Exception{
		String strHelpful = "";
		PageData pd = new PageData();
		pd.put("RPT_DEPT", DepartCode);
		pd.put("RPT_DUR", SystemDateTime);
		pd.put("BILL_OFF", CUST_COL7);
		pd.put("BILL_TYPE", TypeCodeDetail);
		String stateDetail = syssealedinfoService.getState(pd);
		if(!(stateDetail != null && !stateDetail.equals(""))){
			//明细没上报过（明细没记录），取汇总单据中不存在的单号
			strHelpful += FilterBillCode.getBillCodeNotInSummy(TableNameSummy);
		} else {
			//明细已上报过（明细有记录），取汇总单据状态不为0的
			if(stateDetail.equals(DurState.Release.getNameKey())){
				strHelpful += FilterBillCode.getBillCodeNotInSumInvalid(TableNameSummy);
			}
		}
		return strHelpful;
	}*/
	
	
	//在汇总中，判断明细数据是否是正常上报，不是从凭证接口解封的
	public static String CheckCanSummyOperate(SysSealedInfoService syssealedinfoService, 
			String DepartCode, String SystemDateTime, String CUST_COL7,
			String TypeCodeListen) throws Exception{
		String ret = "";
		PageData pd = new PageData();
		pd.put("RPT_DEPT", DepartCode);
		pd.put("RPT_DUR", SystemDateTime);
		pd.put("BILL_TYPE", TypeCodeListen);
		pd.put("BILL_OFF", CUST_COL7);
		String stateListen = syssealedinfoService.getState(pd);
		//1、凭证接口没上报过（凭证接口没记录），分情况
		//2、凭证接口有记录，取汇总单据状态不为0的
		if(stateListen != null && !stateListen.equals("")){
			ret = "请解封明细信息，重新上报！";
		}
		return ret;
	}
	
    //汇总单据状态不为0，就是没汇总或汇总但没作废
	public static String getBillCodeNotInSumInvalid(String tableNameSummy){
		String strReturn = " and BILL_CODE not in (select BILL_CODE from " + tableNameSummy + " where BILL_STATE = " + BillState.Invalid.getNameKey() + ") ";
		return strReturn;
	}

    //没汇总过
	//private static String getBillCodeNotInSummy(String tableNameSummy){
	//	String strReturn = " and BILL_CODE not in (select BILL_CODE from " + tableNameSummy + ") ";
	//	return strReturn;
	//}

    //财务核算用到的
	public static String getReportListenNotSummy(String tableNameSummy, String TypeCodeSummy, String TypeCodeListen){
		String strReturn = " and BILL_CODE not in (select BILL_CODE from " + tableNameSummy;
		strReturn += "                             where (BUSI_DATE, DEPT_CODE, CUST_COL7) not in (select RPT_DUR, RPT_DEPT, BILL_OFF from tb_sys_sealed_info where BILL_TYPE = '" + TypeCodeSummy + "') ";
		strReturn += "                             and (BUSI_DATE, DEPT_CODE, CUST_COL7) in (select RPT_DUR, RPT_DEPT, BILL_OFF from tb_sys_sealed_info where BILL_TYPE = '" + TypeCodeListen + "') ";
		strReturn += "                             ) ";
		return strReturn;
	}
}
	