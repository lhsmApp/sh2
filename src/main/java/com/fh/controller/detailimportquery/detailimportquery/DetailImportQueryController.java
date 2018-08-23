package com.fh.controller.detailimportquery.detailimportquery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
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
import com.fh.controller.common.Common;
import com.fh.controller.common.Corresponding;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.Department;
import com.fh.entity.system.Dictionaries;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.EmplGroupType;
import com.fh.util.enums.StaffDataType;
import com.fh.util.enums.SysConfigKeyCode;
import com.fh.util.enums.TmplType;

import com.fh.util.Jurisdiction;
import com.fh.service.detailimportquery.detailimportquery.DetailImportQueryManager;
import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明： 明细导入查询
 * 创建人：张晓柳
 * 创建时间：2017-08-07
 * @version
 */
@Controller
@RequestMapping(value="/detailimportquery")
public class DetailImportQueryController extends BaseController {
	
	String menuUrl = "detailimportquery/list.do"; //菜单地址(权限用)
	@Resource(name="detailimportqueryService")
	private DetailImportQueryManager detailimportqueryService;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;
	@Resource(name="tmplconfigdictService")
	private TmplConfigDictService tmplconfigdictService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="departmentService")
	private DepartmentService departmentService;
	@Resource(name = "userService")
	private UserManager userService;
	@Resource(name="sysconfigService")
	private SysConfigManager sysConfigManager;

	//临时数据
	String SelectBillCodeFirstShow = "临时数据";
	String SelectBillCodeLastShow = "";
	//当前期间,取自tb_system_config的SystemDateTime字段
	//String SystemDateTime = "";
    //
	String AdditionalReportColumns = "";
	//默认的which值
	String DefaultWhile =  TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey();
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("BUSI_DATE", "DEPT_CODE", "USER_GROP", "CUST_COL7");
    //有权限导出表的部门
    List<String> DepartCanExportTable = new ArrayList<String>();
    //不导出数据的二级单位UNITS_CODE
    List<String> UnitsNotExportData = new ArrayList<String>();
    //不导出数据的部门
    List<String> DepartNotExportData = new ArrayList<String>();
    //导出数据的员工组
    List<String> GroupIsExportData = new ArrayList<String>();
    List<Dictionaries> ListDicFMISACC = new ArrayList<Dictionaries>();

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表DetailImportQuery");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));

		ModelAndView mv = this.getModelAndView();
		mv.setViewName("detailimportquery/detailimportquery/detailimportquery_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		String SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime.trim());
		//while
		getPd.put("which", SelectedTableNo);
	    //导出数据的员工组
	    GroupIsExportData = new ArrayList<String>();
		PageData pdGroupIsExportData = new PageData();
		pdGroupIsExportData.put("KEY_CODE", SysConfigKeyCode.GroupIsExportData);
		String strGroupIsExportData = sysConfigManager.getSysConfigByKey(pdGroupIsExportData);
		if(strGroupIsExportData == null) strGroupIsExportData = "";
		String SCH = EmplGroupType.SCH.getNameKey();
		String HTH = EmplGroupType.HTH.getNameKey();
		String YXRY = EmplGroupType.YXRY.getNameKey();
		strGroupIsExportData = SCH + "," + HTH + "," + YXRY;
		String[] listGroupIsExportData = strGroupIsExportData.replace(" ", "").split(",");
		if(listGroupIsExportData!=null && listGroupIsExportData.length>0){
			GroupIsExportData = Arrays.asList(listGroupIsExportData);
		}
	    //不导出数据的部门
	    DepartNotExportData = new ArrayList<String>();
		PageData pdDepartNotExportData = new PageData();
		pdDepartNotExportData.put("KEY_CODE", SysConfigKeyCode.DepartNotExportData);
		String strDepartNotExportData = sysConfigManager.getSysConfigByKey(pdDepartNotExportData);
		if(strDepartNotExportData == null) strDepartNotExportData = "";
		strDepartNotExportData = "01009,01017";
		String[] listDepartNotExportData = strDepartNotExportData.replace(" ", "").split(",");
		if(listDepartNotExportData!=null && listDepartNotExportData.length>0){
			DepartNotExportData = Arrays.asList(listDepartNotExportData);
		}
	    //不导出数据的部门
		UnitsNotExportData = new ArrayList<String>();
		PageData pdUnitsNotExportData = new PageData();
		pdUnitsNotExportData.put("KEY_CODE", SysConfigKeyCode.UnitsNotExportData);
		String strUnitsNotExportData = sysConfigManager.getSysConfigByKey(pdUnitsNotExportData);
		if(strUnitsNotExportData == null) strUnitsNotExportData = "";
		strUnitsNotExportData = "0100106,0100107,0100108,0100109";
		String[] listUnitsNotExportData = strUnitsNotExportData.replace(" ", "").split(",");
		if(listUnitsNotExportData!=null && listUnitsNotExportData.length>0){
			UnitsNotExportData = Arrays.asList(listUnitsNotExportData);
		}
		//有权限导出表的部门
		DepartCanExportTable = new ArrayList<String>();
		Boolean bolCanExportTable = false;
		PageData pdCanExportTable = new PageData();
		pdCanExportTable.put("KEY_CODE", SysConfigKeyCode.CanExportTable);
		String strCanExportTable = sysConfigManager.getSysConfigByKey(pdCanExportTable);
		if(strCanExportTable == null) strCanExportTable = "";
		String[] listCanExportTable = strCanExportTable.replace(" ", "").split(",");
		if(listCanExportTable!=null && listCanExportTable.length>0){
			DepartCanExportTable = Arrays.asList(listCanExportTable);
			if(DepartCanExportTable.contains(Jurisdiction.getCurrentDepartmentID())){
				bolCanExportTable = true;
			}
		}
		getPd.put("CanExportTable", bolCanExportTable);
		
		//单号下拉列表
		getPd.put("InitBillCodeOptions", SelectBillCodeOptions.getSelectBillCodeOptions(null, SelectBillCodeFirstShow, SelectBillCodeLastShow));
		
		//"BUSI_DATE", "DEPT_CODE", "USER_CATG", "USER_GROP", "CUST_COL7"
		//CUST_COL7 FMISACC 帐套字典
		ListDicFMISACC = DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC");
		mv.addObject("FMISACC", ListDicFMISACC);
		// *********************加载单位树  DEPT_CODE*******************************
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if(DepartmentSelectTreeSource.equals("0"))
		{
			getPd.put("departTreeSource", DepartmentSelectTreeSource);
		} else {
			getPd.put("departTreeSource", 1);
		}
		mv.addObject("zTreeNodes", DepartmentSelectTreeSource);
		// ***********************************************************

		mv.addObject("pd", getPd);
		return mv;
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
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		
		String tableNameDetail = getDetailTableCode(SelectedTableNo);
		String TableNameSummy = getSummyBillTableCode(SelectedTableNo);
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedBusiDate!=null && !SelectedBusiDate.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//表名
		getPd.put("TableName", tableNameDetail);
		           
		List<String> getCodeList = detailimportqueryService.getBillCodeList(getPd);
		String returnString = SelectBillCodeOptions.getSelectBillCodeOptions(getCodeList, SelectBillCodeFirstShow, SelectBillCodeLastShow);
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
		logBefore(logger, Jurisdiction.getUsername()+"getFirstDetailColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService);
		String jqGridColModel = tmpl.generateStructureNoEdit(SelectedTableNo, SelectedDepartCode, SelectedCustCol7);
		
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
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		
		PageData pdTransfer = setPutPd(getPd, true);
		page.setPd(pdTransfer);
		List<PageData> varList = detailimportqueryService.JqPage(page);	//列出Betting列表
		int records = detailimportqueryService.countJqGridExtend(page);
		PageData userdata = null;
		//底行显示的求和与平均值字段
		StringBuilder SqlUserdata = Common.GetSqlUserdata(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		if(SqlUserdata!=null && !SqlUserdata.toString().trim().equals("")){
			//底行显示的求和与平均值字段
			pdTransfer.put("Userdata", SqlUserdata.toString());
		    userdata = detailimportqueryService.getFooterSummary(page);
		}
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setRecords(records);
		result.setPage(page.getPage());
		result.setUserdata(userdata);
		
		return result;
	}

	private String getWhileValue(String value){
        String which = DefaultWhile;
		if(value != null && !value.trim().equals("")){
			which = value;
		}
		return which;
	}
	/**
	 * 根据前端业务表索引获取表名称
	 * 
	 * @param which
	 * @return
	 */
	private String getDetailTableCode(String which) {
		String tableCode = "";
		if (which != null){
			if(which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())) {
				tableCode = "tb_staff_detail";
			} else if (which.equals(TmplType.TB_SOCIAL_INC_DETAIL.getNameKey())) {
				tableCode = "tb_social_inc_detail";
			} else if (which.equals(TmplType.TB_HOUSE_FUND_DETAIL.getNameKey())) {
				tableCode = "tb_house_fund_detail";
			}
		}
		return tableCode;
	}
	private String getSummyBillTableCode(String which) {
		String tableCode = "";
		if (which != null){
			if(which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())) {
				tableCode = "tb_staff_summy_bill";
			} else if (which.equals(TmplType.TB_SOCIAL_INC_DETAIL.getNameKey())) {
				tableCode = "tb_social_inc_summy_bill";
			} else if (which.equals(TmplType.TB_HOUSE_FUND_DETAIL.getNameKey())) {
				tableCode = "tb_house_fund_summy_bill";
			}
		}
		return tableCode;
	}
	
	private PageData setPutPd(PageData getPd, Boolean bolBillCodeUse) throws Exception{
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		
		String tableNameDetail = getDetailTableCode(SelectedTableNo);
		String TableNameSummy = getSummyBillTableCode(SelectedTableNo);
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(bolBillCodeUse){
			QueryFeild += QueryFeildString.getQueryFeildBillCodeDetail(SelectedBillCode, SelectBillCodeFirstShow);
		}
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedBusiDate!=null && !SelectedBusiDate.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//表名
		getPd.put("TableName", tableNameDetail);
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		return getPd;
	}

	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/excelDetail")
	public ModelAndView exportDetailExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出HouseFundDetail到excel");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		
		String tableNameDetail = getDetailTableCode(SelectedTableNo);
		String TableNameSummy = getSummyBillTableCode(SelectedTableNo);
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		if(!(SelectedBusiDate!=null && !SelectedBusiDate.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//表名
		getPd.put("TableName", tableNameDetail);
		
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, "");
		
		page.setPd(getPd);
		List<PageData> varOList = detailimportqueryService.datalistExport(page);

		/*PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, "");
		
		PageData pdTransfer = setPutPd(getPd, false);
		page.setPd(pdTransfer);
		List<PageData> varOList = detailimportqueryService.datalistExport(page);*/
		
		ModelAndView mv = new ModelAndView();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		String fileName = "导出明细";
		dataMap.put("filename", new String(fileName.getBytes("gb2312"), "ISO-8859-1"));
		List<String> titles = new ArrayList<String>();
		List<PageData> varList = new ArrayList<PageData>();
		if(map_SetColumnsList != null && map_SetColumnsList.size() > 0){
		    for (TmplConfigDetail col : map_SetColumnsList.values()) {
				if(col.getCOL_HIDE().equals("1")){
					titles.add(col.getCOL_NAME());
				}
			}
			if(varOList!=null && varOList.size()>0){
				for(int i=0;i<varOList.size();i++){
					PageData vpd = new PageData();
					int j = 1;
					for (TmplConfigDetail col : map_SetColumnsList.values()) {
						if(col.getCOL_HIDE().equals("1")){
						    String trans = col.getDICT_TRANS();
						    Object getCellValue = varOList.get(i).get(col.getCOL_CODE().toUpperCase());
						    if(trans != null && !trans.trim().equals("")){
							    String value = "";
							    Map<String, String> dicAdd = (Map<String, String>) DicList.getOrDefault(trans, new LinkedHashMap<String, String>());
							    value = dicAdd.getOrDefault(getCellValue, "");
							    vpd.put("var" + j, value);
						    } else {
						    	vpd.put("var" + j, getCellValue.toString());
						    }
						    j++;
						}
					}
					varList.add(vpd);
				}
			}
		}
		dataMap.put("titles", titles);
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap); 
		return mv;
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goDownExcel")
	public ModelAndView goDownExcel() throws Exception{
		CommonBase commonBase = new CommonBase();
	    commonBase.setCode(-1);
	    
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		//工资或奖金枚举编码
		String SalaryOrBonus = getPd.getString("SalaryOrBonus");
		
		//if(!(SelectedBusiDate!=null && !SelectedBusiDate.trim().equals("") && SystemDateTime!=null
		//		&& SelectedBusiDate.length() == SystemDateTime.length())){
		//	commonBase.setCode(2);
		//	commonBase.setMessage("查询条件中的当前区间位数不正确！");
		//} 

		List<Dictionaries> dicList = new ArrayList<Dictionaries>();
		String DepartTreeSource = "";
		if(DictsUtil.DepartShowAll.equals(Jurisdiction.getCurrentDepartmentID())){
			DepartTreeSource = "1";
			Dictionaries itemAll = new Dictionaries();
			itemAll.setDICT_CODE("ALL");
			itemAll.setNAME("全部");
			dicList.add(itemAll);
			Dictionaries itemHome = new Dictionaries();
			itemHome.setDICT_CODE("HOME");
			itemHome.setNAME("公司本部");
			dicList.add(itemHome);
			List<Department> listDepartDic = departmentService.getDepartDic(getPd);
			for(String strDeptCode : DepartCanExportTable){
				if(strDeptCode!=null && !strDeptCode.trim().equals("")
						&& !strDeptCode.equals(Jurisdiction.getCurrentDepartmentID())){
                    String strDeptName = "";
                    for(Department dicDept : listDepartDic){
    					if(strDeptCode.equals(dicDept.getDEPARTMENT_CODE())){
    						strDeptName = dicDept.getNAME();
    					}
                    }
                    if(strDeptName!=null && !strDeptName.equals("")){
    					Dictionaries itemAdd = new Dictionaries();
    					itemAdd.setDICT_CODE(strDeptCode);
    					itemAdd.setNAME(strDeptName);
    					dicList.add(itemAdd);
                    }
				}
			}
		} else {
			DepartTreeSource = "0";
		}
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/downExcel");
		mv.addObject("local", "detailimportquery");
		mv.addObject("SelectedTableNo", SelectedTableNo);
		mv.addObject("SelectedBusiDate", SelectedBusiDate);
		mv.addObject("DepartTreeSource", DepartTreeSource);
		mv.addObject("SalaryOrBonus", SalaryOrBonus);
		mv.addObject("commonBaseCode", commonBase.getCode());
		mv.addObject("commonMessage", commonBase.getMessage());
		//FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		//DEPARTMENT 责任中心字典
		mv.addObject("DEPARTMENT", dicList);
		return mv;
	}
	
	/**导出到excel
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("DownSelectedTableNo"));
		//String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		String TableName = getDetailTableCode(SelectedTableNo);
		//日期
		String SelectedBusiDate = getPd.getString("DownSelectedBusiDate");
		//账套
		String SelectedCustCol7 = getPd.getString("DownSelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("DownSelectedDepartCode");
		//工资或奖金枚举编码
		String SalaryOrBonus = getPd.getString("DownSalaryOrBonus");
		
		List<PageData> varOList = new ArrayList<PageData>();
		
		String WhereSql = " and BUSI_DATE = '" + SelectedBusiDate + "' ";
		WhereSql += " and CUST_COL7 = '" + SelectedCustCol7 + "' ";
		//WhereSql += " and USER_GROP = '" + emplGroupType + "' ";
	    //导出数据的员工组
		if(GroupIsExportData != null){
			List<String> listGroupIsExportData = new ArrayList<String>();
			for(String strDeptCode : GroupIsExportData){
				if(strDeptCode!=null && !strDeptCode.trim().equals("")){
					listGroupIsExportData.add(strDeptCode);
				}
			}
			if(listGroupIsExportData!=null && listGroupIsExportData.size()>0){
				String strGroupIsExportData = QueryFeildString.tranferListValueToSqlInString(listGroupIsExportData);
				WhereSql += " and USER_GROP in (" + strGroupIsExportData + ") ";
			}
		}
		if(DictsUtil.DepartShowAll.equals(Jurisdiction.getCurrentDepartmentID())){
			if(!(SelectedDepartCode!=null && !SelectedDepartCode.equals(""))){
				WhereSql += " and 1 != 1 ";
			} else {
				if(SelectedDepartCode.equals("ALL")){
					
				} else if(SelectedDepartCode.equals("HOME")){
					List<String> listDeptSqlNotIn = new ArrayList<String>();
					for(String strDeptCode : DepartCanExportTable){
						if(strDeptCode!=null && !strDeptCode.trim().equals("")
								&& !strDeptCode.equals(DictsUtil.DepartShowAll)){
							listDeptSqlNotIn.add(strDeptCode);
						}
					}
					String strDeptSqlNotIn = QueryFeildString.tranferListValueToSqlInString(listDeptSqlNotIn);
					WhereSql += " and DEPT_CODE not in (" + strDeptSqlNotIn + ") ";
					if(SelectedCustCol7.equals("9100")){
						String strSALARYRANGE_dongxiang = "S17";
						WhereSql += " and SAL_RANGE not in ('" + strSALARYRANGE_dongxiang + "') ";
						
					}
				} else {
					WhereSql += " and DEPT_CODE = '" + SelectedDepartCode + "' ";
				}
			}
		} else {
			WhereSql += " and DEPT_CODE = '" + Jurisdiction.getCurrentDepartmentID() + "' ";
		}
		//责任中心-管道分公司廊坊油气储运公司-0100106
		String DEPT_CODE_0100106 = "0100106";
		//责任中心-华北石油管理局-0100107
		String DEPT_CODE_0100107 = "0100107";
		//责任中心-中国石油天然气管道局-0100108
		String DEPT_CODE_0100108 = "0100108";
		//责任中心-华北采油二厂-0100109
		String DEPT_CODE_0100109 = "0100109";
		WhereSql += " and DEPT_CODE not in ('" + DEPT_CODE_0100106 + "', '" + DEPT_CODE_0100107 + "', '" + DEPT_CODE_0100108 + "', '" + DEPT_CODE_0100109 + "') ";
	    //不导出数据的部门
		/*if(DictsUtil.DepartShowAll.equals(Jurisdiction.getCurrentDepartmentID())){
			if(SelectedDepartCode.equals("ALL") || SelectedDepartCode.equals("HOME")){
				List<String> listDepartNotExportData = new ArrayList<String>();
				for(String strDepartCode : DepartNotExportData){
					if(strDepartCode!=null && !strDepartCode.trim().equals("")){
						listDepartNotExportData.add(strDepartCode);
					}
				}
				if(listDepartNotExportData!=null && listDepartNotExportData.size()>0){
					String strDepartNotExportData = QueryFeildString.tranferListValueToSqlInString(listDepartNotExportData);
					WhereSql += " and DEPT_CODE not in (" + strDepartNotExportData + ") ";
				}
			}
		}*/
	    //不导出数据的二级单位
		if(UnitsNotExportData != null){
			List<String> listUnitsNotExportData = new ArrayList<String>();
			for(String strUnitsCode : UnitsNotExportData){
				if(strUnitsCode!=null && !strUnitsCode.trim().equals("")){
					listUnitsNotExportData.add(strUnitsCode);
				}
			}
			if(listUnitsNotExportData!=null && listUnitsNotExportData.size()>0){
				String strUnitsNotExportData = QueryFeildString.tranferListValueToSqlInString(listUnitsNotExportData);
				WhereSql += " and UNITS_CODE not in (" + strUnitsNotExportData + ") ";
			}
		}
		WhereSql += QueryFeildString.getBillCodeNotInSumInvalidDetail(getSummyBillTableCode(SelectedTableNo));
		WhereSql += QueryFeildString.getBillConfirm();
		
		if(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey())){
			WhereSql += " and DATA_TYPE = '" + StaffDataType.Salary.getNameKey() + "' ";
			String SelectGroupFeild = " USER_CODE, USER_NAME, STAFF_IDENT, DEPT_CODE, "//USER_GROP, 
					+ " sum(GROSS_PAY) GROSS_PAY, "
					+ " sum(ENDW_INS) ENDW_INS, "
					+ " sum(MED_INS + CASD_INS) MED_INS, "
					+ " sum(UNEMPL_INS) UNEMPL_INS, "
					+ " sum(HOUSE_FUND) HOUSE_FUND, "
					+ " sum(KID_ALLE) KID_ALLE, "
					+ " sum(SUP_PESN) SUP_PESN, sum(ACCRD_TAX) ACCRD_TAX ";
			//if(SelectedDepartCode.equals("HOME")){
				SelectGroupFeild += ", UNITS_CODE ";
			//}
			getPd.put("SelectGroupFeild", SelectGroupFeild);
		}
        if(SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey())){
			WhereSql += " and DATA_TYPE = '" + StaffDataType.Bonus.getNameKey() + "' ";
			String SelectGroupFeild = " USER_CODE, USER_NAME, STAFF_IDENT, DEPT_CODE, "//USER_GROP, 
					+ " sum(CUST_COL14) CUST_COL14 ";
			//if(SelectedDepartCode.equals("HOME")){
				SelectGroupFeild += ", UNITS_CODE ";
			//}
			getPd.put("SelectGroupFeild", SelectGroupFeild);
        }
		//getPd.put("SelectAddFeild", " IFNULL(a.USER_NAME, ' ') USER_NAME, IFNULL(a.STAFF_IDENT, ' ') STAFF_IDENT ");
		String strGroupByFeild = " USER_CODE, USER_NAME, STAFF_IDENT, DEPT_CODE ";
		//if(SelectedDepartCode.equals("HOME")){
			strGroupByFeild += ", UNITS_CODE ";
		//}
		getPd.put("GroupByFeild", strGroupByFeild);//, USER_GROP
		getPd.put("WhereSql", WhereSql);
		getPd.put("TableName", TableName);
		//getPd.put("LeftJoin", " left join " + TableName + " a on a.USER_CODE = t.USER_CODE and a.DEPT_CODE = t.DEPT_CODE ");// and a.USER_GROP = t.USER_GROP
		//(SELECT DISTINCT USER_CODE, DEPT_CODE, USER_NAME, STAFF_IDENT FROM tb_staff_detail)
        page.setPd(getPd);
		List<PageData> varOListvarOList = detailimportqueryService.exportSumList(page);
		if(varOListvarOList!=null && varOListvarOList.size()>0){
			//List<Dictionaries> listUserCode_DeptCode_UserGrop = new ArrayList<Dictionaries>();
			for(PageData each : varOListvarOList){
			//	String UserCode = each.getString("USER_CODE");
			//	String DeptCode = each.getString("DEPT_CODE");
			//	//String UserGrop = each.getString("USER_GROP");
				Boolean bolHave = false;
			//	if(listUserCode_DeptCode_UserGrop==null) listUserCode_DeptCode_UserGrop = new ArrayList<Dictionaries>();
			//	for(Dictionaries eachHave : listUserCode_DeptCode_UserGrop){
			//		if(eachHave.getDICT_CODE().equals(UserCode) 
			//				&& eachHave.getNAME().equals(DeptCode)){
			//				//&& eachHave.getNAME_EN().equals(UserGrop)
			//			bolHave = true;
			//		}
			//	}
				if(!bolHave){
					varOList.add(each);
					//Dictionaries addHave = new Dictionaries();
					//addHave.setDICT_CODE(UserCode);
					//addHave.setNAME(DeptCode);
					////addHave.setNAME_EN(UserGrop);
					//listUserCode_DeptCode_UserGrop.add(addHave);
				}
			}
		}
		if(varOList!=null && varOList.size()>0){
			for(PageData each : varOList){
				each.put("CERT_TYPE", "居民身份证");
				each.put("TAX_BURDENS", "自行负担");
			}
		}
		
		String strGetDicSelectedDepartCode = SelectedDepartCode;
		if(SelectedDepartCode != null && (SelectedDepartCode.equals("ALL") || SelectedDepartCode.equals("HOME"))){
			strGetDicSelectedDepartCode = DictsUtil.DepartShowAll;
		}
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, strGetDicSelectedDepartCode, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
		
		Map<String, TmplConfigDetail> map_GetDicSetColumnsList = Common.GetSetColumnsList(SelectedTableNo, strGetDicSelectedDepartCode, SelectedCustCol7, tmplconfigService);

		Map<String, TmplConfigDetail> map_SetColumnsList = new LinkedHashMap<String, TmplConfigDetail>();
				//Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		map_SetColumnsList.put("USER_CODE", new TmplConfigDetail("USER_CODE", "工号", "1", false));
		map_SetColumnsList.put("USER_NAME", new TmplConfigDetail("USER_NAME", "姓名", "1", false));
		map_SetColumnsList.put("CERT_TYPE", new TmplConfigDetail("CERT_TYPE", "证件类型", "1", false));
		map_SetColumnsList.put("STAFF_IDENT", new TmplConfigDetail("STAFF_IDENT", "证件号码", "1", false));
		map_SetColumnsList.put("TAX_BURDENS", new TmplConfigDetail("TAX_BURDENS", "税款负担方式", "1", false));
		//if(SelectedDepartCode.equals("HOME")){
			String strUNITS_CODE = "UNITS_CODE";
			TmplConfigDetail tmplGetDic = map_GetDicSetColumnsList.get(strUNITS_CODE);
			TmplConfigDetail tmplPut = new TmplConfigDetail(strUNITS_CODE, "所属二级单位", "1", false);
			tmplPut.setDICT_TRANS(tmplGetDic.getDICT_TRANS());
			map_SetColumnsList.put(strUNITS_CODE, tmplPut);
		//}
		if(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey())){
			map_SetColumnsList.put("GROSS_PAY", new TmplConfigDetail("GROSS_PAY", "收入额", "1", true));
			map_SetColumnsList.put("ACCRD_TAX", new TmplConfigDetail("ACCRD_TAX", "税额", "1", true));
			map_SetColumnsList.put("免税所得", new TmplConfigDetail("免税所得", "免税所得", "1", true));
			map_SetColumnsList.put("ENDW_INS", new TmplConfigDetail("ENDW_INS", "基本养老保险费", "1", true));
			map_SetColumnsList.put("MED_INS", new TmplConfigDetail("MED_INS", "基本医疗保险费", "1", true));
			map_SetColumnsList.put("UNEMPL_INS", new TmplConfigDetail("UNEMPL_INS", "失业保险费", "1", true));
			map_SetColumnsList.put("HOUSE_FUND", new TmplConfigDetail("HOUSE_FUND", "住房公积金", "1", true));
			map_SetColumnsList.put("KID_ALLE", new TmplConfigDetail("KID_ALLE", "允许扣除的税费", "1", true));
			map_SetColumnsList.put("SUP_PESN", new TmplConfigDetail("SUP_PESN", "年金", "1", true));
			map_SetColumnsList.put("商业健康保险费", new TmplConfigDetail("商业健康保险费", "商业健康保险费", "1", true));
			map_SetColumnsList.put("其他扣除", new TmplConfigDetail("其他扣除", "其他扣除", "1", true));
			map_SetColumnsList.put("减除费用", new TmplConfigDetail("减除费用", "减除费用", "1", true));
			map_SetColumnsList.put("实际捐赠额", new TmplConfigDetail("实际捐赠额", "实际捐赠额", "1", true));
			map_SetColumnsList.put("允许列支的捐赠比例", new TmplConfigDetail("允许列支的捐赠比例", "允许列支的捐赠比例", "1", false));
			map_SetColumnsList.put("准予扣除的捐赠额", new TmplConfigDetail("准予扣除的捐赠额", "准予扣除的捐赠额", "1", true));
			map_SetColumnsList.put("减免税额", new TmplConfigDetail("减免税额", "减免税额", "1", true));
			map_SetColumnsList.put("已扣缴税额", new TmplConfigDetail("已扣缴税额", "已扣缴税额", "1", true));
		}
        if(SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey())){
			map_SetColumnsList.put("CUST_COL14", new TmplConfigDetail("CUST_COL14", "全年一次性奖金额", "1", true));
			map_SetColumnsList.put("免税所得", new TmplConfigDetail("免税所得", "免税所得", "1", true));
			map_SetColumnsList.put("允许扣除的税费", new TmplConfigDetail("允许扣除的税费", "允许扣除的税费", "1", true));
			map_SetColumnsList.put("商业健康保险费", new TmplConfigDetail("商业健康保险费", "商业健康保险费", "1", true));
			map_SetColumnsList.put("其他费用", new TmplConfigDetail("其他费用", "其他费用", "1", true));
			map_SetColumnsList.put("实际捐赠额", new TmplConfigDetail("实际捐赠额", "实际捐赠额", "1", true));
			map_SetColumnsList.put("允许列支的捐赠比例", new TmplConfigDetail("允许列支的捐赠比例", "允许列支的捐赠比例", "1", false));
			map_SetColumnsList.put("准予扣除的捐赠额", new TmplConfigDetail("准予扣除的捐赠额", "准予扣除的捐赠额", "1", true));
			map_SetColumnsList.put("减免税额", new TmplConfigDetail("减免税额", "减免税额", "1", true));
			map_SetColumnsList.put("已缴税额", new TmplConfigDetail("已缴税额", "已缴税额", "1", true));
		}
		map_SetColumnsList.put("备注", new TmplConfigDetail("备注", "备注", "1", false));

		String strBillOffName = "";
		if(ListDicFMISACC != null){
			for(Dictionaries dic : ListDicFMISACC){
				if(SelectedCustCol7.equals(dic.getDICT_CODE())){
					strBillOffName = dic.getNAME();
				}
			}
		}
		String fileName = SelectedBusiDate + "_" + strBillOffName;
		if(SelectedDepartCode.equals("ALL")){
			fileName += "_全部";
		} else if(SelectedDepartCode.equals("HOME")){
			fileName += "_公司本部";
		} else {
			fileName += "_";
			String strDeptName = "";
			List<Department> listDepartDic = departmentService.getDepartDic(getPd);
			if(listDepartDic!=null){
                for(Department dicDept : listDepartDic){
    				if(SelectedDepartCode.equals(dicDept.getDEPARTMENT_CODE())){
						strDeptName = dicDept.getNAME();
					}
                }
			}
			fileName += strDeptName;
		}
		if(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey())){
			fileName += "_工资薪酬个税表";
		}
        if(SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey())){
			fileName += "_奖金个税表";
        }
		
		ModelAndView mv = new ModelAndView();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		dataMap.put("filename", new String(fileName.getBytes("gb2312"), "ISO-8859-1"));
		List<String> titles = new ArrayList<String>();
		List<PageData> varList = new ArrayList<PageData>();
		if(map_SetColumnsList != null && map_SetColumnsList.size() > 0){
		    for (TmplConfigDetail col : map_SetColumnsList.values()) {
				if(col.getCOL_HIDE().equals("1")){
					titles.add(col.getCOL_NAME());
				}
			}
			if(varOList!=null && varOList.size()>0){
				for(int i=0;i<varOList.size();i++){
					PageData vpd = new PageData();
					int j = 1;
					for (TmplConfigDetail col : map_SetColumnsList.values()) {
						if(col.getCOL_HIDE().equals("1")){
						    String trans = col.getDICT_TRANS();
						    Object getCellValue = varOList.get(i).get(col.getCOL_CODE().toUpperCase());
						    if(getCellValue==null) getCellValue = "";
						    if(trans != null && !trans.trim().equals("")){
							    String value = "";
							    Map<String, String> dicAdd = (Map<String, String>) DicList.getOrDefault(trans, new LinkedHashMap<String, String>());
							    value = dicAdd.getOrDefault(getCellValue, "");
							    vpd.put("var" + j, value);
						    } else {
						    	if(getCellValue != null && !getCellValue.toString().trim().equals("")){
						    		if(col.getIsNum()){
								    	vpd.put("var" + j, getCellValue.toString());
						    		} else {
								    	vpd.put("var" + j, getCellValue.toString());
						    		}
						    	} else {
						    		if(col.getIsNum()){
								    	vpd.put("var" + j, "0.00");
						    		} else {
								    	vpd.put("var" + j, " ");
						    		}
						    	}
						    }
						    j++;
						}
					}
					varList.add(vpd);
				}
			}
		}
		dataMap.put("titles", titles);
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap); 
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
