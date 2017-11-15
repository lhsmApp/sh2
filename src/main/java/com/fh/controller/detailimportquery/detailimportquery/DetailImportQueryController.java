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
import com.fh.controller.common.TmplUtil;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.TmplType;

import net.sf.json.JSONArray;

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

	//默认的which值
	String DefaultWhile =  TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey();
	//页面显示数据的二级单位
	//String UserDepartCode = "";
    
	//底行显示的求和与平均值字段
	//StringBuilder SqlUserdata = new StringBuilder();
	//字典
	//Map<String, Object> DicList = new LinkedHashMap<String, Object>();
	//表结构  
	//Map<String, TableColumns> map_HaveColumnsList = new LinkedHashMap<String, TableColumns>();
	// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
	//Map<String, TmplConfigDetail> map_SetColumnsList = new LinkedHashMap<String, TmplConfigDetail>();
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("BUSI_DATE", "DEPT_CODE", "USER_GROP", "CUST_COL7");
    //查询的所有可操作的责任中心
    //List<String> AllDeptCode = new ArrayList<String>();

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表DetailImportQuery");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		//查询的所有可操作的责任中心
	    //AllDeptCode = new ArrayList<String>();

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));

		//当前登录人所在二级单位
		String UserDepartCode = Jurisdiction.getCurrentDepartmentID();//
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("detailimportquery/detailimportquery/detailimportquery_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		String SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime);
		//while
		getPd.put("which", SelectedTableNo);
		mv.addObject("pd", getPd);
		
		//"BUSI_DATE", "DEPT_CODE", "USER_CATG", "USER_GROP", "CUST_COL7"
		//CUST_COL7 FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		// *********************加载单位树  DEPT_CODE*******************************
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if(DepartmentSelectTreeSource.equals("0"))
		{
			getPd.put("departTreeSource", DepartmentSelectTreeSource);
			//AllDeptCode.add(UserDepartCode);
		} else {
			getPd.put("departTreeSource", 1);
	        //JSONArray jsonArray = JSONArray.fromObject(DepartmentSelectTreeSource);  
			//List<PageData> listDepart = (List<PageData>) JSONArray.toCollection(jsonArray, PageData.class);
			//if(listDepart!=null && listDepart.size()>0){
			//	for(PageData pdDept : listDepart){
			//		AllDeptCode.add(pdDept.getString(DictsUtil.Id));
			//	}
			//}
		}
		mv.addObject("zTreeNodes", DepartmentSelectTreeSource);
		// ***********************************************************
		
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService);
		String jqGridColModel = tmpl.generateStructureNoEdit(SelectedTableNo, UserDepartCode);
		mv.addObject("jqGridColModel", jqGridColModel);

		//SqlUserdata = tmpl.getSqlUserdata();
		//字典
		//DicList = tmpl.getDicList();
		//表结构  
		//map_HaveColumnsList = tmpl.getHaveColumnsList();
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		//map_SetColumnsList = tmpl.getSetColumnsList();
		
		return mv;
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
		//底行显示的求和与平均值字段
		StringBuilder SqlUserdata = Common.GetSqlUserdata(SelectedTableNo, Jurisdiction.getCurrentDepartmentID(), tmplconfigService);
		
		PageData pdTransfer = setPutPd(getPd);
		page.setPd(pdTransfer);
		List<PageData> varList = detailimportqueryService.JqPage(page);	//列出Betting列表
		int records = detailimportqueryService.countJqGridExtend(page);
		PageData userdata = null;
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
	private String getSummyTableCode(String which) {
		String tableCode = "";
		if (which != null){
			if(which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
					||which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())) {
				tableCode = "tb_staff_summy";
			} else if (which.equals(TmplType.TB_SOCIAL_INC_DETAIL.getNameKey())) {
				tableCode = "tb_social_inc_summy";
			} else if (which.equals(TmplType.TB_HOUSE_FUND_DETAIL.getNameKey())) {
				tableCode = "tb_house_fund_summy";
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
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		
		String tableNameDetail = getDetailTableCode(SelectedTableNo);
		String tableNameSummy = getSummyTableCode(SelectedTableNo);
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		//QueryFeild += FilterBillCode.getHelpfulBillCode(tableNameSummy);
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
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
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, Jurisdiction.getCurrentDepartmentID(), tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, Jurisdiction.getCurrentDepartmentID(), 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, "");
		
		PageData pdTransfer = setPutPd(getPd);
		page.setPd(pdTransfer);
		List<PageData> varOList = detailimportqueryService.datalistExport(page);
		
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
