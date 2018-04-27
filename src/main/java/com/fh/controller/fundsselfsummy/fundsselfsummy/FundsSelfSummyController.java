﻿package com.fh.controller.fundsselfsummy.fundsselfsummy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.BillCodeUtil;
import com.fh.controller.common.Common;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.Message;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.SqlFeildToSave;
import com.fh.controller.common.SysStruMappingList;
import com.fh.controller.common.TmplUtil;
import com.fh.controller.common.TmplVoucherUtil;
import com.fh.controller.common.VoucherToBillType;
import com.fh.entity.CertParmConfig;
import com.fh.entity.ClsTwoFeild;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.SysStruMapping;
import com.fh.entity.SysTableMapping;
import com.fh.entity.TableColumns;
import com.fh.entity.system.Department;
import com.fh.entity.system.Dictionaries;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.BillState;

import net.sf.json.JSONArray;

import com.fh.util.DateUtil;
import com.fh.util.Jurisdiction;
import com.fh.service.fundsselfsummy.fundsselfsummy.FundsSelfSummyManager;
import com.fh.service.sysBillnum.sysbillnum.SysBillnumManager;
import com.fh.service.certParmConfig.certParmConfig.impl.CertParmConfigService;
import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysStruMapping.sysStruMapping.impl.SysStruMappingService;
import com.fh.service.sysTableMapping.sysTableMapping.impl.SysTableMappingService;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明： 汇总单据确认
 * 创建人：张晓柳
 * 创建时间：2018-04-11
 * @version
 */
@Controller
@RequestMapping(value="/fundsselfsummy")
public class FundsSelfSummyController extends BaseController {
	
	String menuUrl = "fundsselfsummy/list.do"; //菜单地址(权限用)
	@Resource(name="fundsselfsummyService")
	private FundsSelfSummyManager fundsselfsummyService;
	
	@Resource(name="sysconfigService")
	private SysConfigManager sysConfigManager;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;
	@Resource(name="sysTableMappingService")
	private SysTableMappingService sysTableMappingService;
	@Resource(name="sysStruMappingService")
	private SysStruMappingService sysStruMappingService;
	@Resource(name="tmplconfigdictService")
	private TmplConfigDictService tmplconfigdictService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="departmentService")
	private DepartmentService departmentService;
	@Resource(name = "userService")
	private UserManager userService;
	
	@Resource(name="certParmConfigService")
	private CertParmConfigService certParmConfigService;
	@Resource(name="sysbillnumService")
	private SysBillnumManager sysbillnumService;
	
	//表名
	String TB_GEN_BUS_SUMMY_BILL = "TB_GEN_BUS_SUMMY_BILL";
	String TB_GEN_SUMMY = "TB_GEN_SUMMY";
	String TB_GEN_BUS_DETAIL = "TB_GEN_BUS_DETAIL";

	//当前期间,取自tb_system_config的SystemDateTime字段
	String SystemDateTime = "";
	// 查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
	private List<String> keyListBase = Arrays.asList("BILL_CODE", "BUSI_DATE", "TYPE_CODE", "DEPT_CODE", "BILL_OFF");
	List<String> SumFieldBill = Arrays.asList("BILL_CODE", "BUSI_DATE", "TYPE_CODE", "DEPT_CODE", "BILL_OFF");
	//临时数据
	String SelectedTypeCodeFirstShow = "请选择凭证类型";
	String SelectedDepartCodeFirstShow = "请选择责任中心";
	String SelectedBillCodeFirstShow = "请选择单号";//"临时数据";

	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("TYPE_CODE", "BILL_CODE", "BILL_OFF", "DEPT_CODE", "BUSI_DATE", "BILL_STATE");
	
    //获取汇总条件传的责任中心
    String DeptCodeSumGroupField = "01";

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表fundsselfsummy");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();

		//单号下拉列表
		getPd.put("SelectedTypeCodeFirstShow", SelectedTypeCodeFirstShow);
		getPd.put("InitSelectedTypeCodeOptions", SelectBillCodeOptions.getSelectDicOptions(null, SelectedTypeCodeFirstShow));
		getPd.put("SelectedDepartCodeFirstShow", SelectedDepartCodeFirstShow);
		getPd.put("InitSelectedDepartCodeOptions", SelectBillCodeOptions.getSelectDicOptions(null, SelectedDepartCodeFirstShow));
		getPd.put("SelectedBillCodeFirstShow", SelectedBillCodeFirstShow);
		getPd.put("InitSelectedBillCodeOptions", SelectBillCodeOptions.getSelectDicOptions(null, SelectedBillCodeFirstShow));
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("fundsselfsummy/fundsselfsummy/fundsselfsummy_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime);

		//BILL_OFF FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		//TYPE_CODE PZTYPE 凭证字典
		//mv.addObject("PZTYPE", DictsUtil.getDictsByParentCode(dictionariesService, "PZTYPE"));

		mv.addObject("pd", getPd);
		return mv;
	}

	/**账套列表
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getTypeCodeList")
	public @ResponseBody CommonBase getTypeCodeList() throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		
		String strCondition = " and PARENT_CODE = 'PZTYPE' ";
		strCondition += " and DICT_CODE in (select TYPE_CODE from tb_sys_bill_off_mapping where BILL_OFF = '" + SelectedCustCol7 +"') ";
		List<Dictionaries> list = dictionariesService.findByCondition(strCondition);
		
		String returnString = SelectBillCodeOptions.getSelectDicOptions(list, SelectedTypeCodeFirstShow);
		commonBase.setMessage(returnString);
		commonBase.setCode(0);
		return commonBase;
	}

	/**责任中心下拉列表
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getDepartCodeList")
	public @ResponseBody CommonBase getDepartCodeList() throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		
		String strCondition = " and DEPARTMENT_CODE in (select DEPT_CODE from tb_sys_dept_mapping where BILL_OFF = '" + SelectedCustCol7 +"' and TYPE_CODE = '" + SelectedTypeCode +"' ";
		if(!Jurisdiction.getCurrentDepartmentID().equals(DictsUtil.DepartShowAll)){
			strCondition += "                               and DEPT_CODE = '" + Jurisdiction.getCurrentDepartmentID() +"' ";
		}
		strCondition += "                               ) ";
		List<Department> list = departmentService.findByCondition(strCondition);
		
		String returnString = SelectBillCodeOptions.getSelectDeptOptions(list, SelectedDepartCodeFirstShow);
		commonBase.setMessage(returnString);
		commonBase.setCode(0);
		return commonBase;
	}

	/**单号下拉列表
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getBillCodeList")
	public @ResponseBody CommonBase getBillCodeList() throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("BILL_STATE", BillState.Normal.getNameKey());
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("BILL_OFF", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SystemDateTime);
		getQueryFeildPd.put("TYPE_CODE", SelectedTypeCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SystemDateTime!=null && !SystemDateTime.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedTypeCode!=null && !SelectedTypeCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//QueryFeild += QueryFeildString.getNotReportBillCode(TypeCodeTransfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		
		PageData transferPd = new PageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("TableName", TB_GEN_BUS_SUMMY_BILL);
		transferPd.put("CanOperate", QueryFeild);
		List<String> getCodeList = fundsselfsummyService.getBillCodeList(transferPd);
		String returnString = SelectBillCodeOptions.getSelectBillCodeOptions(getCodeList, SelectedBillCodeFirstShow);
		commonBase.setMessage(returnString);
		commonBase.setCode(0);
		
		return commonBase;
	}

	/**显示结构
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getShowColModel")
	public @ResponseBody CommonBase getShowColModel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getShowColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//单位
		//String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		//单号
		//String SelectedBillCode = getPd.getString("SelectedBillCode");

		TmplVoucherUtil tmplVoucherUtil = new TmplVoucherUtil(sysTableMappingService, sysStruMappingService, tmplconfigService, 
				tmplconfigdictService, dictionariesService, departmentService, userService, keyListBase);
			String jqGridColModel = tmplVoucherUtil.generateStructureNoEdit(SelectedTypeCode, TB_GEN_BUS_SUMMY_BILL, SystemDateTime, SelectedCustCol7);

		commonBase.setCode(0);
		commonBase.setMessage(jqGridColModel);
		
		return commonBase;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表FinanceAccounts");

		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		
		PageData pdTransfer = setTransferPd(getPd);
		pdTransfer.put("TableName", TB_GEN_BUS_SUMMY_BILL);
		page.setPd(pdTransfer);
		
		List<PageData> varList = fundsselfsummyService.JqPage(page);	//列出Betting列表
		int records = fundsselfsummyService.countJqGridExtend(page);
		PageData userdata = null;
		//底行显示的求和与平均值字段
		StringBuilder SqlUserdata = Common.GetSqlUserdata(SelectedTypeCode, TB_GEN_BUS_SUMMY_BILL, SystemDateTime, SelectedCustCol7, sysStruMappingService);
		if(SqlUserdata!=null && !SqlUserdata.toString().trim().equals("")){
			//底行显示的求和与平均值字段
			getPd.put("Userdata", SqlUserdata.toString());
		    userdata = fundsselfsummyService.getFooterSummary(page);
		}
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setRecords(records);
		result.setPage(page.getPage());
		result.setUserdata(userdata);
		
		return result;
	}

	/**明细显示结构
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getFirstDetailColModel")
	public @ResponseBody CommonBase getFirstDetailColModel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getFirstDetailColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		PageData getPd = this.getPageData();
		//账套
		String DataCustCol7 = getPd.getString("DataCustCol7");
		//凭证字典
		String DataTypeCode = getPd.getString("DataTypeCode");
		//单位
		//String DataDeptCode = getPd.getString("DataDeptCode");
		
		List<String> SumFieldDetail = getGroupSummyField(DataTypeCode, DataCustCol7, DeptCodeSumGroupField);

		TmplVoucherUtil tmplVoucherUtil = new TmplVoucherUtil(sysTableMappingService, sysStruMappingService, tmplconfigService, 
				tmplconfigdictService, dictionariesService, departmentService, userService, SumFieldDetail);
	    String detailColModel = tmplVoucherUtil.generateStructureNoEdit(DataTypeCode, TB_GEN_SUMMY, SystemDateTime, DataCustCol7);

		commonBase.setCode(0);
		commonBase.setMessage(detailColModel);
		
		return commonBase;
	}

	/**明细数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getFirstDetailList")
	public @ResponseBody PageResult<PageData> getFirstDetailList() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getFirstDetailList");

		PageData getPd = this.getPageData();
		//账套
		String DataCustCol7 = getPd.getString("DataCustCol7");
		//凭证字典
		String DataTypeCode = getPd.getString("DataTypeCode");
		//单位
		//String DataDeptCode = getPd.getString("DataDeptCode");
		
		List<String> SumFieldDetail = getGroupSummyField(DataTypeCode, DataCustCol7, DeptCodeSumGroupField);
		String strBillCode = getPd.getString("DetailListBillCode");
		
		PageData pdCode = new PageData();
		pdCode.put("TableName", TB_GEN_SUMMY);
		pdCode.put("BILL_CODE", strBillCode);
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(SumFieldDetail, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			pdCode.put("FieldSelectKey", strFieldSelectKey);
		}
		List<PageData> varList = fundsselfsummyService.getFirstDetailList(pdCode);	//列出Betting列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		
		return result;
	}

	/**明细显示结构
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getSecondDetailColModel")
	public @ResponseBody CommonBase getSecondDetailColModel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getSecondDetailColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		PageData getPd = this.getPageData();
		//账套
		String DataCustCol7 = getPd.getString("DataCustCol7");
		//凭证字典
		String DataTypeCode = getPd.getString("DataTypeCode");
		//单位
		//String DataDeptCode = getPd.getString("DataDeptCode");

		TmplVoucherUtil tmplVoucherUtil = new TmplVoucherUtil(sysTableMappingService, sysStruMappingService, tmplconfigService, 
				tmplconfigdictService, dictionariesService, departmentService, userService, null);
	    String detailColModel = tmplVoucherUtil.generateStructureNoEdit(DataTypeCode, TB_GEN_BUS_DETAIL, SystemDateTime, DataCustCol7);

		commonBase.setCode(0);
		commonBase.setMessage(detailColModel);
		
		return commonBase;
	}

	/**明细数据
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getSecondDetailList")
	public @ResponseBody PageResult<PageData> getSecondDetailList() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getSecondDetailList");

		PageData getPd = this.getPageData();
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        PageData pdGet = listData.get(0);
        //账套
		String CUST_COL7 = pdGet.getString("BILL_OFF" + TmplUtil.keyExtra);
		//凭证字典
		String TYPE_CODE = pdGet.getString("TYPE_CODE" + TmplUtil.keyExtra);
		List<String> SumFieldDetail = getGroupDetailField(TYPE_CODE, CUST_COL7, DeptCodeSumGroupField);
		List<String> listTransferSumFieldDetail = new ArrayList<String>();

		PageData pdFieldDetail = new PageData();
		// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
		List<SysStruMapping> getSysStruMappingList = SysStruMappingList.getSysStruMappingList(TYPE_CODE, TB_GEN_SUMMY, SystemDateTime, CUST_COL7, sysStruMappingService);
		if(getSysStruMappingList!=null && SumFieldDetail!=null){
			for(SysStruMapping mapping : getSysStruMappingList){
				String COL_CODE = mapping.getCOL_CODE().toUpperCase();
				String COL_MAPPING_CODE = mapping.getCOL_MAPPING_CODE().toUpperCase();
				if(SumFieldDetail.contains(COL_CODE.toUpperCase())){
					pdFieldDetail.put(COL_CODE + TmplUtil.keyExtra, pdGet.getString(COL_MAPPING_CODE + TmplUtil.keyExtra));
					SumFieldDetail.remove(COL_CODE);
					listTransferSumFieldDetail.add(COL_CODE);
				}
			}
		}
		PageData pdCode = new PageData();
		String QueryFeild = QueryFeildString.getDetailQueryFeild(pdFieldDetail, listTransferSumFieldDetail, TmplUtil.keyExtra);
	    if(!(QueryFeild!=null && !QueryFeild.trim().equals(""))){
	    	QueryFeild += " and 1 != 1 ";
	    }
	    if(SumFieldDetail!=null && SumFieldDetail.size()>0){
	    	QueryFeild += " and 1 != 1 ";
	    }
	    pdCode.put("QueryFeild", QueryFeild);
		
	    pdCode.put("TableName", TB_GEN_BUS_DETAIL);
		List<PageData> varList = fundsselfsummyService.getSecondDetailList(pdCode);	//列出Betting列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		
		return result;
	}

	/**汇总
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/summyBill")
	public @ResponseBody CommonBase summyBill() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "delete")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedCustCol7, SelectedTypeCode, SelectedDepartCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
			return commonBase;
		}
		String checkState = CheckState(SelectedTypeCode, SelectedCustCol7, SelectedDepartCode, null);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}

		/***************获取最大单号及更新最大单号********************/
	    String billNumType = VoucherToBillType.getVoucherToBillType(SelectedTypeCode);
	    if(!(billNumType!=null && !billNumType.trim().equals(""))){
	    	commonBase.setCode(2);
	    	commonBase.setMessage(Message.NotGetBillTypeFromVoucher);
	    	return commonBase;
	    }
		String month = DateUtil.getMonth();
		PageData pdBillNum=new PageData();
		pdBillNum.put("BILL_CODE", billNumType);
		pdBillNum.put("BILL_DATE", month);
		PageData pdBillNumResult = sysbillnumService.findById(pdBillNum);
		if(pdBillNumResult == null){
			pdBillNumResult = new PageData();
		}
		Object objGetNum = pdBillNumResult.get("BILL_NUMBER");
		if(!(objGetNum != null && !objGetNum.toString().trim().equals(""))){
			objGetNum = 0;
		}
		int getNum = (int) objGetNum;
		getNum++;
		String getBILL_CODE = BillCodeUtil.getBillCode(billNumType, month, getNum);
		pdBillNum.put("BILL_NUMBER", getNum);
		/***************************************************/
		
		List<SysTableMapping> getSysTableMappingList = SysStruMappingList.getUseTableMapping(SelectedTypeCode, SystemDateTime, SelectedCustCol7, sysTableMappingService);
		if(getSysTableMappingList != null && getSysTableMappingList.size() == 1){
			SysTableMapping tableMappingDetail = getSysTableMappingList.get(0);

			// 用语句查询出数据库表的所有字段及其属性；拼接成jqgrid全部列
			List<TableColumns> tableColumnsBill = tmplconfigService.getTableColumns(TB_GEN_BUS_SUMMY_BILL);
			List<TableColumns> tableColumnsSummy = tmplconfigService.getTableColumns(TB_GEN_SUMMY);
			List<TableColumns> tableColumnsDetail = tmplconfigService.getTableColumns(TB_GEN_BUS_DETAIL);
			// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
			List<SysStruMapping> getSysStruMappingBillList = SysStruMappingList.getSysStruMappingList(SelectedTypeCode, TB_GEN_BUS_SUMMY_BILL, SystemDateTime, SelectedCustCol7, sysStruMappingService);
			List<SysStruMapping> getSysStruMappingSummyList = SysStruMappingList.getSysStruMappingList(SelectedTypeCode, TB_GEN_SUMMY, SystemDateTime, SelectedCustCol7, sysStruMappingService);
			List<SysStruMapping> getSysStruMappingDetailList = SysStruMappingList.getSysStruMappingList(SelectedTypeCode, TB_GEN_BUS_DETAIL, SystemDateTime, SelectedCustCol7, sysStruMappingService);

			String strSqlBillGroupBy = "";
			List<CertParmConfig> getCertParmConfigList = getSelfCertParmConfig(SelectedTypeCode, SelectedCustCol7, DeptCodeSumGroupField);
			if(getCertParmConfigList!=null && getCertParmConfigList.size()>0){
				strSqlBillGroupBy = getCertParmConfigList.get(0).getGROUP_COND1();
			}
			String strSqlSummyGroupBy = "";
			List<String> SumFieldDetail = getGroupDetailField(SelectedTypeCode, SelectedCustCol7, DeptCodeSumGroupField);
			strSqlSummyGroupBy = QueryFeildString.tranferListStringToGroupbyString(SumFieldDetail);
			
			ClsTwoFeild sqlFeildDetail = SqlFeildToSave.getSqlFeildToSave(SelectedCustCol7, SelectedTypeCode, SelectedDepartCode, SystemDateTime, 
					getBILL_CODE, getSysStruMappingDetailList);
			String strSqlDetail = " select " + sqlFeildDetail.getSqlSelectFeild() 
			              + " from " + tableMappingDetail.getTABLE_NAME()
			              + sqlFeildDetail.getSqlWhere();
			List<PageData> pdGetSaveDetailList = fundsselfsummyService.getSaveList(strSqlDetail);
			if(pdGetSaveDetailList!=null && pdGetSaveDetailList.size()>0){
				for(PageData detail : pdGetSaveDetailList){
					detail.put("TableName", TB_GEN_BUS_DETAIL);
		        	Common.setModelDefault(detail, tableColumnsDetail, getSysStruMappingDetailList);
				}
			} else {
		    	commonBase.setCode(2);
		    	commonBase.setMessage(Message.NotHaveOperateData);
		    	return commonBase;
			}

			ClsTwoFeild sqlFeildSummy = SqlFeildToSave.getSqlFeildToSave(SelectedCustCol7, SelectedTypeCode, SelectedDepartCode, SystemDateTime, 
					getBILL_CODE, getSysStruMappingSummyList);
			String strSqlSummy = " select " + sqlFeildSummy.getSqlSelectFeild() 
		              + " from (" + strSqlDetail +") detail ";
			if(strSqlSummyGroupBy!=null && !strSqlSummyGroupBy.trim().equals("")){
				strSqlSummy += " group by " + strSqlSummyGroupBy;
			}
			List<PageData> pdGetSaveSummyList = fundsselfsummyService.getSaveList(strSqlSummy);
			if(pdGetSaveSummyList!=null && pdGetSaveSummyList.size()>0){
				for(PageData summy : pdGetSaveSummyList){
					summy.put("TableName", TB_GEN_SUMMY);
		        	Common.setModelDefault(summy, tableColumnsSummy, getSysStruMappingSummyList);
				}
			} else {
		    	commonBase.setCode(2);
		    	commonBase.setMessage(Message.NotHaveOperateData);
		    	return commonBase;
			}

			ClsTwoFeild sqlFeildBill = SqlFeildToSave.getSqlFeildToSave(SelectedCustCol7, SelectedTypeCode, SelectedDepartCode, SystemDateTime, 
					getBILL_CODE, getSysStruMappingBillList);
			String strSqlBill = " select distinct * from (select " + sqlFeildBill.getSqlSelectFeild();
			strSqlBill += "                               from (" + strSqlSummy +") summy ";
			if(strSqlBillGroupBy!=null && !strSqlBillGroupBy.trim().equals("")){
				strSqlBill += " group by " + strSqlBillGroupBy;
			}
			strSqlBill += "                               ) bill ";
			List<PageData> pdGetSaveBillList = fundsselfsummyService.getSaveList(strSqlBill);
			if(pdGetSaveBillList!=null && pdGetSaveBillList.size()>0){
				for(PageData bill : pdGetSaveBillList){
					bill.put("TableName", TB_GEN_BUS_SUMMY_BILL);
		        	Common.setModelDefault(bill, tableColumnsBill, getSysStruMappingBillList);
				}
			} else {
		    	commonBase.setCode(2);
		    	commonBase.setMessage(Message.NotHaveOperateData);
		    	return commonBase;
			}

        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("UpdateBillNum", pdBillNum);
        	map.put("SaveBillList", pdGetSaveBillList);
        	map.put("SaveSummyList", pdGetSaveSummyList);
        	map.put("SaveDetailList", pdGetSaveDetailList);
        	fundsselfsummyService.batchSummyBill(map);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}

	/**取消
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/cancelSummy")
	public @ResponseBody CommonBase cancelSummy() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "delete")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        List<PageData> listTransfer = new ArrayList<PageData>();
        List<String> listBillCode = new ArrayList<String>();
        for(PageData each : listData){
        	String BILL_CODE = each.getString("BILL_CODE" + TmplUtil.keyExtra);
        	listBillCode.add(BILL_CODE);
        	PageData addPd = new PageData();
        	addPd.put("TableName", TB_GEN_BUS_SUMMY_BILL);
        	addPd.put("BILL_CODE", BILL_CODE);
        	addPd.put("BILL_USER", Jurisdiction.getCurrentDepartmentID());
        	addPd.put("BILL_STATE", BillState.Invalid.getNameKey());
        	addPd.put("CanOperate", "");
        	listTransfer.add(addPd);
        }
        String strSqlInBillCode = QueryFeildString.tranferListValueToSqlInString(listBillCode);
		String checkState = CheckState(null, null, null, strSqlInBillCode);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
        if(null != listData && listData.size() > 0){
        	fundsselfsummyService.batchCancelSummy(listTransfer);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}
	
	private String CheckMustSelectedAndSame(String CUST_COL7, String TYPE_CODE, String DEPT_CODE) throws Exception{
		String strRut = "";
		if(!(CUST_COL7 != null && !CUST_COL7.trim().equals(""))){
			strRut += "查询条件中的账套必须选择！";
		}
		if(!(TYPE_CODE != null && !TYPE_CODE.trim().equals(""))){
			strRut += "查询条件中的凭证类型必须选择！";
		}
		if(!(DEPT_CODE != null && !DEPT_CODE.trim().equals(""))){
			strRut += "查询条件中的责任中心必须选择！";
		}
		return strRut;
	}
	
	//判断单据状态
	private String CheckState(String typeCode, String billOff, String deptCode, String strSqlInBillCode) throws Exception{
		String strRut = "";

		String QueryFeild = " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' AND BUSI_DATE = '" + SystemDateTime + "' ";
		if(strSqlInBillCode!=null && !strSqlInBillCode.trim().equals("")){
			//有单号判断取消汇总，判断是否没传输上报，如已传输上报不能取消
			QueryFeild += " and BILL_CODE in (" + strSqlInBillCode + ") ";
			QueryFeild += QueryFeildString.getReportBillCode("", SystemDateTime, "", "");
		} else {
			//没有单号判断汇总，判断是否已汇总，如已汇总不能再次汇总
			PageData getQueryFeildPd = new PageData();
			getQueryFeildPd.put("DEPT_CODE", deptCode);
			getQueryFeildPd.put("BILL_OFF", billOff);
			getQueryFeildPd.put("TYPE_CODE", typeCode);
			QueryFeild += QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
			if(!(deptCode!=null && !deptCode.trim().equals(""))){
				QueryFeild += " and 1 = 1 ";
			}
			if(!(billOff!=null && !billOff.trim().equals(""))){
				QueryFeild += " and 1 = 1 ";
			}
			if(!(typeCode!=null && !typeCode.trim().equals(""))){
				QueryFeild += " and 1 = 1 ";
			}
			
		}
		PageData transferPd = new PageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("CanOperate", QueryFeild);
		transferPd.put("TableName", TB_GEN_BUS_SUMMY_BILL);
		List<PageData> getCodeList = fundsselfsummyService.getCheckStateList(transferPd);
		
		if(getCodeList != null && getCodeList.size()>0){
			if(strSqlInBillCode!=null && !strSqlInBillCode.trim().equals("")){
			    strRut = Message.OperDataSumAlreadyChange;
			} else {
			    strRut = Message.OperDataAlreadySum;
			}
		}
		return strRut;
	}
	
	private PageData setTransferPd(PageData getPd) throws Exception{
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("BILL_STATE", BillState.Normal.getNameKey());
		getQueryFeildPd.put("BILL_CODE", SelectedBillCode);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("BILL_OFF", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SystemDateTime);
		getQueryFeildPd.put("TYPE_CODE", SelectedTypeCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SystemDateTime!=null && !SystemDateTime.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedTypeCode!=null && !SelectedTypeCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		QueryFeild += QueryFeildString.getNotReportBillCode("", SystemDateTime, "", "");
		getPd.put("QueryFeild", QueryFeild);
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(keyListBase, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			getPd.put("FieldSelectKey", strFieldSelectKey);
		}
		return getPd;
	}

	private List<String> getGroupSummyField(String typeCode, String billOff, String deptCode) throws Exception{
		List<String> SumFieldReturn = new ArrayList<String>();
		List<String> SumFieldDetail = getGroupDetailField(typeCode, billOff, deptCode);

		// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
		List<SysStruMapping> getSysStruMappingList = SysStruMappingList.getSysStruMappingList(typeCode, TB_GEN_SUMMY, SystemDateTime, billOff, sysStruMappingService);
		
		if(getSysStruMappingList!=null && getSysStruMappingList.size()>0 
				&& SumFieldDetail!=null && SumFieldDetail.size()>0){
			for(SysStruMapping mapping : getSysStruMappingList){
				if(SumFieldDetail.contains(mapping.getCOL_CODE().toUpperCase())){
					SumFieldReturn.add(mapping.getCOL_MAPPING_CODE().toUpperCase());
				}
			}
		}
		return SumFieldReturn;
	}
	
	private List<String> getGroupDetailField(String typeCode, String billOff, String deptCode) throws Exception{
		//List<String> SumFieldBill = Arrays.asList("BILL_CODE", "BUSI_DATE", "DEPT_CODE", "BILL_OFF");
		List<CertParmConfig> getCertParmConfigList = getSelfCertParmConfig(typeCode, billOff, deptCode);
		String strSumFieldDetail = "";
		if(getCertParmConfigList!=null && getCertParmConfigList.size()>0){
			strSumFieldDetail = getCertParmConfigList.get(0).getGROUP_COND();
		}
		List<String> listSumFieldDetail = QueryFeildString.tranferStringToList(strSumFieldDetail);
		listSumFieldDetail = QueryFeildString.extraSumField(listSumFieldDetail, SumFieldBill);
		
		return listSumFieldDetail;
	}
	
	private List<CertParmConfig> getSelfCertParmConfig(String typeCode, String billOff, String deptCode) throws Exception{
		CertParmConfig certParmConfig = new CertParmConfig();
		certParmConfig.setTYPE_CODE(typeCode);
		certParmConfig.setBILL_OFF(billOff);
		certParmConfig.setDEPT_CODE(deptCode);
		List<CertParmConfig> getCertParmConfigList = certParmConfigService.getSelfCertParmConfig(certParmConfig);
		return getCertParmConfigList;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}