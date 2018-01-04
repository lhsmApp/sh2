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
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TmplConfigDetail;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.StaffDataType;
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
	//默认的which值
	String DefaultWhile =  TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey();
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("BUSI_DATE", "DEPT_CODE", "USER_GROP", "CUST_COL7");

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
		mv.addObject("SystemDateTime", SystemDateTime);
		//while
		getPd.put("which", SelectedTableNo);
		//单号下拉列表
		getPd.put("InitBillCodeOptions", SelectBillCodeOptions.getSelectBillCodeOptions(null, SelectBillCodeFirstShow, SelectBillCodeLastShow));
		
		//"BUSI_DATE", "DEPT_CODE", "USER_CATG", "USER_GROP", "CUST_COL7"
		//CUST_COL7 FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
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
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
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
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
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
		
		PageData pdTransfer = setPutPd(getPd);
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
	
	private PageData setPutPd(PageData getPd) throws Exception{
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
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
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += QueryFeildString.getQueryFeildBillCodeDetail(SelectedBillCode, SelectBillCodeFirstShow);
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
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出HouseFundDetail到excel");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		//工资或奖金枚举编码
		//String SalaryOrBonus = getPd.getString("SalaryOrBonus");
		
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, strShowCalModelDepaet, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, "");
		
		PageData pdTransfer = setPutPd(getPd);
		page.setPd(pdTransfer);
		List<PageData> varOList = detailimportqueryService.datalistExport(page);
		
		/*
		
		Map<String, Object> DicList = new LinkedHashMap<String, Object>();
				//Common.GetDicList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, 
				//tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);

		Map<String, TmplConfigDetail> map_SetColumnsList = new LinkedHashMap<String, TmplConfigDetail>();
				//Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		map_SetColumnsList.put("USER_CODE", new TmplConfigDetail("USER_CODE", "工号", "1"));
		map_SetColumnsList.put("USER_NAME", new TmplConfigDetail("USER_NAME", "姓名", "1"));
		map_SetColumnsList.put("CERT_TYPE", new TmplConfigDetail("CERT_TYPE", "证件类型", "1"));
		map_SetColumnsList.put("STAFF_IDENT", new TmplConfigDetail("STAFF_IDENT", "证件号码", "1"));
		map_SetColumnsList.put("TAX_BURDENS", new TmplConfigDetail("TAX_BURDENS", "税款负担方式", "1"));
		if(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey())){
			map_SetColumnsList.put("GROSS_PAY", new TmplConfigDetail("GROSS_PAY", "收入额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "免税所得", "1"));
			map_SetColumnsList.put("ENDW_INS", new TmplConfigDetail("ENDW_INS", "基本养老保险费", "1"));
			map_SetColumnsList.put("MED_INS", new TmplConfigDetail("MED_INS", "基本医疗保险费", "1"));
			map_SetColumnsList.put("UNEMPL_INS", new TmplConfigDetail("UNEMPL_INS", "失业保险费", "1"));
			map_SetColumnsList.put("HOUSE_FUND", new TmplConfigDetail("HOUSE_FUND", "住房公积金", "1"));
			map_SetColumnsList.put("KID_ALLE", new TmplConfigDetail("KID_ALLE", "允许扣除的税费", "1"));
			map_SetColumnsList.put("SUP_PESN", new TmplConfigDetail("SUP_PESN", "年金", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "商业健康保险费", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "其他扣除", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "减除费用", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "实际捐赠额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "允许列支的捐赠比例", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "准予扣除的捐赠额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "减免税额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "已扣缴税额", "1"));
		}
        if(SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey())){
			map_SetColumnsList.put("CUST_COL14", new TmplConfigDetail("CUST_COL14", "全年一次性奖金额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "免税所得", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "允许扣除的税费", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "商业健康保险费", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "其他费用", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "实际捐赠额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "允许列支的捐赠比例", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "准予扣除的捐赠额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "减免税额", "1"));
			map_SetColumnsList.put("", new TmplConfigDetail("", "已缴税额", "1"));
		}
		TmplConfigDetail col = new TmplConfigDetail("", "备注", "1");
		map_SetColumnsList.put("", col);

		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		//页面显示数据的二级单位
		getPd.put("SelectedDepartCode", SelectedDepartCode);
		//账套
		getPd.put("SelectedCustCol7", SelectedCustCol7);
		//员工组
		getPd.put("emplGroupType", emplGroupType);

		String strBillCode = QueryFeildString.getQueryFeildBillCodeDetail(SelectedBillCode, SelectBillCodeFirstShow);
		getPd.put("CheckBillCode", strBillCode);
		
		String strSelectFeild = " USER_CODE, USER_NAME, STAFF_IDENT, "
				+ " sum(GROSS_PAY) GROSS_PAY, "
				+ " sum(ENDW_INS) ENDW_INS, "
				+ " sum(MED_INS - CASD_INS) MED_INS, "
				+ " sum(UNEMPL_INS) UNEMPL_INS, "
				+ " sum(HOUSE_FUND) HOUSE_FUND, "
				+ " sum(KID_ALLE) KID_ALLE, "
				+ " sum(SUP_PESN) SUP_PESN, "
				+ " sum(CUST_COL14) CUST_COL14 ";
		getPd.put("SelectFeild", strSelectFeild);
		getPd.put("OrderByFeild", " USER_CODE, USER_NAME, STAFF_IDENT ");
		
		page.setPd(getPd);
		List<PageData> varOList = staffdetailService.exportList(page);
		if(varOList!=null && varOList.size()>0){
			for(PageData each : varOList){
				each.put("CERT_TYPE", "居民身份证");
				each.put("TAX_BURDENS", "自行负担");
			}
		}*/
		
		ModelAndView mv = new ModelAndView();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		dataMap.put("filename", "");
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
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
