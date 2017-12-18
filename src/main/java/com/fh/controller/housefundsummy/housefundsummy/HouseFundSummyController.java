package com.fh.controller.housefundsummy.housefundsummy;

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
import com.fh.controller.common.FilterBillCode;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.User;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.Jurisdiction;
import com.fh.util.enums.BillNumType;
import com.fh.util.enums.BillState;
import com.fh.util.enums.SysConfigKeyCode;
import com.fh.util.enums.TmplType;

import net.sf.json.JSONArray;

import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.houseFundDetail.housefunddetail.HouseFundDetailManager;
import com.fh.service.housefundsummy.housefundsummy.HouseFundSummyManager;
import com.fh.service.sysBillnum.sysbillnum.SysBillnumManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明：公积金汇总
 * 创建人：张晓柳
 * 创建时间：2017-07-07
 */
@Controller
@RequestMapping(value="/housefundsummy")
public class HouseFundSummyController extends BaseController {
	
	String menuUrl = "housefundsummy/list.do"; //菜单地址(权限用)
	@Resource(name="housefundsummyService")
	private HouseFundSummyManager housefundsummyService;
	@Resource(name="housefunddetailService")
	private HouseFundDetailManager housefunddetailService;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;
	@Resource(name="syssealedinfoService")
	private SysSealedInfoService syssealedinfoService;
	@Resource(name="tmplconfigdictService")
	private TmplConfigDictService tmplconfigdictService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="departmentService")
	private DepartmentService departmentService;
	@Resource(name="sysconfigService")
	private SysConfigManager sysConfigManager;
	@Resource(name="sysbillnumService")
	private SysBillnumManager sysbillnumService;
	@Resource(name = "userService")
	private UserManager userService;

	//表名
	String TableNameBase = "tb_house_fund_summy_bill";
	String TableNameFirstDetail = "tb_house_fund_summy";
	String TableNameSecondDetail = "tb_house_fund_detail";
	//枚举类型  1工资明细,2工资汇总,3公积金明细,4公积金汇总,5社保明细,6社保汇总,7工资接口,8公积金接口,9社保接口
	String TypeCodeDetail = TmplType.TB_HOUSE_FUND_DETAIL.getNameKey();
	String TypeCodeSummyBill = TmplType.TB_HOUSE_FUND_SUMMY.getNameKey();
	String TypeCodeSummyDetail = TmplType.TB_HOUSE_FUND_SUMMY.getNameKey();
    String TypeCodeTransfer = TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey();
	//临时数据
	String SelectBillCodeFirstShow = "临时数据";
	String SelectBillCodeLastShow = "全部单据";
	
	//页面显示数据的年月
	String SystemDateTime = "";
	//底行显示的求和与平均值字段
	StringBuilder SqlUserdataSummy = new StringBuilder();
	StringBuilder SqlUserdataDetail = new StringBuilder();
	
	//界面分组字段
	List<String> jqGridGroupColumn = Arrays.asList("DEPT_CODE");

	// 查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
	private List<String> keyListBase = Arrays.asList("BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
    //汇总字段
	List<String> SumFieldBill = Arrays.asList("BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
    List<String> SumFieldDetail = new ArrayList<String>();//Arrays.asList("BUSI_DATE", "DEPT_CODE", "USER_CATG", "USER_GROP", "CUST_COL7", "UNITS_CODE", "ORG_UNIT");
    //界面查询字段
    List<String> QueryFeildList = Arrays.asList("DEPT_CODE", "CUST_COL7");
	//修改导入明细获取字段
	List<String> DetailSetBillCodeFeild = Arrays.asList("SERIAL_NO");
	//另加的列、配置模板之外的列 
    //目前只能这么设置，改设置改的地方多
	String AdditionalReportColumn = "";//ReportState

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表housefundFundSummy");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData pdSysConfig = new PageData();
		pdSysConfig.put("KEY_CODE", SysConfigKeyCode.HouseFundGRPCond);
		String strSumFieldToString = sysConfigManager.getSysConfigByKey(pdSysConfig);
		List<String> listSumFieldDetail = QueryFeildString.tranferStringToList(strSumFieldToString);
		listSumFieldDetail = QueryFeildString.extraSumField(listSumFieldDetail, SumFieldBill);
		SumFieldDetail = listSumFieldDetail;
		
		PageData getPd = this.getPageData();
		//单号下拉列表
		getPd.put("SelectNoBillCodeShow", SelectBillCodeFirstShow);
		getPd.put("InitBillCodeOptions", SelectBillCodeOptions.getSelectBillCodeOptions(null, SelectBillCodeFirstShow, SelectBillCodeLastShow));
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("housefundsummy/housefundsummy/housefundsummy_list");
		mv.addObject("SystemDateTime", SystemDateTime);
		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String DepartName = user.getDEPARTMENT_NAME();
		mv.addObject("DepartName", DepartName);

		//CUST_COL7 FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		// *********************加载责任中心oa_department树  DEPT_CODE*******************************
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if(DepartmentSelectTreeSource.equals("0"))
		{
			getPd.put("departTreeSource", DepartmentSelectTreeSource);
		} else {
			getPd.put("departTreeSource", 1);
		}
		mv.addObject("zTreeNodes1", DepartmentSelectTreeSource);
		// ***********************************************************

		//当前登录人所在二级单位
		String UserDepartCode = Jurisdiction.getCurrentDepartmentID();//
		TmplUtil tmplSummy = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService, keyListBase, jqGridGroupColumn, AdditionalReportColumn, null);
		String jqGridColModelSummy = tmplSummy.generateStructureNoEdit(TypeCodeSummyBill, UserDepartCode);
		//分组字段是否显示在表中
		List<String> m_jqGridGroupColumnShow = tmplSummy.getJqGridGroupColumnShow();
		//底行显示的求和与平均值字段
		SqlUserdataSummy = tmplSummy.getSqlUserdata();

		TmplUtil tmplDetail = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService);
		String jqGridColModelDetail = tmplDetail.generateStructureNoEdit(TypeCodeDetail, UserDepartCode);
		SqlUserdataDetail = tmplDetail.getSqlUserdata();

		mv.addObject("pd", getPd);
		mv.addObject("jqGridColModelSummy", jqGridColModelSummy);
		mv.addObject("jqGridColModelDetail", jqGridColModelDetail);
        //分组字段  格式：groupField: ['DEPT_CODE'],
		String jqGridGroupField = QueryFeildString.tranferListValueToSqlInString(jqGridGroupColumn);
		mv.addObject("jqGridGroupField", jqGridGroupField);
        //分组字段是否显示在表中  格式：groupColumnShow: [true],
		String jqGridGroupColumnShow = QueryFeildString.tranferListStringToGroupbyString(m_jqGridGroupColumnShow);
		mv.addObject("jqGridGroupColumnShow", jqGridGroupColumnShow);
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
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
		QueryFeild += QueryFeildString.getNotReportBillCode(TypeCodeTransfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		//工资无账套无数据
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		
		PageData transferPd = new PageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("CanOperate", QueryFeild);
		List<String> getCodeList = housefundsummyService.getBillCodeList(transferPd);
		String returnString = SelectBillCodeOptions.getSelectBillCodeOptions(getCodeList, SelectBillCodeFirstShow, SelectBillCodeLastShow);
		commonBase.setMessage(returnString);
		commonBase.setCode(0);
		
		return commonBase;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表HouseFundSummy");
		
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(SelectedBillCode!=null && !SelectedBillCode.equals(SelectBillCodeFirstShow)){
			QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
			QueryFeild += QueryFeildString.getNotReportBillCode(TypeCodeTransfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		}
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		//工资无账套无数据
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
        QueryFeild += QueryFeildString.getQueryFeildBillCodeSummy(SelectedBillCode, SelectBillCodeLastShow, SelectBillCodeFirstShow);
		getPd.put("QueryFeild", QueryFeild);

		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}

		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		if(SelectedBillCode!=null && !SelectedBillCode.equals(SelectBillCodeFirstShow)){
			getPd.put("tableName", TableNameBase);
		} else {
			getPd.put("tableName", TableNameSecondDetail);
		}
		
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(keyListBase, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			getPd.put("FieldSelectKey", strFieldSelectKey);
		}

		page.setPd(getPd);
		List<PageData> varList = housefundsummyService.JqPage(page);	//列出Betting列表
		int records = housefundsummyService.countJqGridExtend(page);
		PageData userdata = null;
		if(SelectedBillCode!=null && !SelectedBillCode.equals(SelectBillCodeFirstShow)){
			if(SqlUserdataSummy!=null && !SqlUserdataSummy.toString().trim().equals("")){
				//底行显示的求和与平均值字段
				getPd.put("Userdata", SqlUserdataSummy.toString());
				userdata = housefundsummyService.getFooterSummary(page);
			}
		} else {
			if(SqlUserdataDetail!=null && !SqlUserdataDetail.toString().trim().equals("")){
				//底行显示的求和与平均值字段
				getPd.put("Userdata", SqlUserdataDetail.toString());
				userdata = housefundsummyService.getFooterSummary(page);
			}
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
		String DEPT_CODE = (String) getPd.get("DataDeptCode");
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService, SumFieldDetail, null, null, null);
		String detailColModel = tmpl.generateStructureNoEdit(TypeCodeSummyDetail, DEPT_CODE);
		
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
		String strBillCode = getPd.getString("DetailListBillCode");

		PageData pdCode = new PageData();
		pdCode.put("BILL_CODE", strBillCode);
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(SumFieldDetail, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			pdCode.put("FieldSelectKey", strFieldSelectKey);
		}
		List<PageData> varList = housefundsummyService.findSummyDetailList(pdCode);	//列出Betting列表
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
		String DEPT_CODE = (String) getPd.get("DataDeptCode");
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService);
		String detailColModel = tmpl.generateStructureNoEdit(TypeCodeDetail, DEPT_CODE);
		
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

		PageData pdCode = new PageData();
		String QueryFeild = QueryFeildString.getDetailQueryFeild(pdGet, SumFieldDetail, TmplUtil.keyExtra);
	    if(!(QueryFeild!=null && !QueryFeild.trim().equals(""))){
	    	QueryFeild += " and 1 != 1 ";
	    }
	    pdCode.put("QueryFeild", QueryFeild);
		List<PageData> varList = housefunddetailService.getDetailList(pdCode);	//列出Betting列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		
		return result;
	}

	/**批量删除
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/deleteAll")
	public @ResponseBody CommonBase deleteAll() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "delete")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		String checkState = CheckState(listData);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
        if(null != listData && listData.size() > 0){
        	housefundsummyService.cancelAll(listData);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}

	 /**汇总 接口有上报记录，生成新单号；没有就原有单号；
	  * 先把接口有上报记录的汇总作废，删掉接口的上报记录，在先删后插
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/summaryDepartString")
	public @ResponseBody CommonBase summaryDepartString() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "delete")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		PageData getPd = this.getPageData();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");

		String strSumFieldBill = QueryFeildString.tranferListStringToGroupbyString(SumFieldBill);
		String strSumFieldDetail = QueryFeildString.tranferListStringToGroupbyString(SumFieldDetail);

		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);

		String UserDepartCode = Jurisdiction.getCurrentDepartmentID();

		Map<String, TmplConfigDetail> map_SetColumnsListBill = Common.GetSetColumnsList(TypeCodeSummyBill, UserDepartCode, tmplconfigService);
		Map<String, TableColumns> map_HaveColumnsListBill = Common.GetHaveColumnsList(TypeCodeSummyBill, tmplconfigService);
		Map<String, TmplConfigDetail> map_SetColumnsListDetail = Common.GetSetColumnsList(TypeCodeSummyDetail, UserDepartCode, tmplconfigService);
		Map<String, TableColumns> map_HaveColumnsListDetail = Common.GetHaveColumnsList(TypeCodeSummyDetail, tmplconfigService);

		//获取汇总的select的字段
		List<TableColumns> tableDetailColumns = tmplconfigService.getTableColumns(TableNameSecondDetail);
		boolean bolDeleteSummy;
		
		String QueryFeild = "";
		List<PageData> getDetailSetBillCode = new ArrayList<PageData>();
		//updateFilter
		List<String> retSetBillCodeFeild = new ArrayList<String>();
		for(String strfeild : DetailSetBillCodeFeild){
			retSetBillCodeFeild.add(strfeild);
		}
		retSetBillCodeFeild = QueryFeildString.extraSumField(retSetBillCodeFeild, SumFieldBill);
		if(listData!=null && listData.size()>0){
			bolDeleteSummy = true;
			List<String> listBillCode = new ArrayList<String>(); 
			for(PageData each : listData){
				listBillCode.add(each.getString("BILL_CODE" + TmplUtil.keyExtra));
			}
			QueryFeild += " and BILL_CODE in (" + QueryFeildString.tranferListValueToSqlInString(listBillCode) + ") ";
			QueryFeild += QueryFeildString.getNotReportBillCode(TypeCodeTransfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
			QueryFeild += FilterBillCode.getBillCodeNotInSumInvalidDetail(TableNameBase);
		} else {
			bolDeleteSummy = false;
			PageData getQueryFeildPd = new PageData();
			getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
			getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
			QueryFeild += QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
			QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
			//工资无账套无数据
			if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			QueryFeild += " and BILL_CODE like ' %' ";

			PageData pdSetBillCode = new PageData();
			pdSetBillCode.put("SystemDateTime", SystemDateTime);
			pdSetBillCode.put("QueryFeild", QueryFeild);
			pdSetBillCode.put("TableName", TableNameSecondDetail);
			String SelectFeildSetBillCode = QueryFeildString.tranferListStringToKeyString(retSetBillCodeFeild, TmplUtil.keyExtra);
			pdSetBillCode.put("SelectFeild", SelectFeildSetBillCode);
			getDetailSetBillCode = housefunddetailService.getSum(pdSetBillCode);
		}
		
		PageData pdDetail = new PageData();
		pdDetail.put("SystemDateTime", SystemDateTime);
		pdDetail.put("QueryFeild", QueryFeild);
		pdDetail.put("TableName", TableNameSecondDetail);
		pdDetail.put("GroupbyFeild", strSumFieldDetail);
		String SelectFeildDetail = Common.getSumFeildSelect(SumFieldDetail, tableDetailColumns, TmplUtil.keyExtra);
		pdDetail.put("SelectFeild", SelectFeildDetail);
		List<PageData> getSaveDetail = housefunddetailService.getSum(pdDetail);
		//TableName CanOperate
		
		PageData pdBill = new PageData();
		pdBill.put("SystemDateTime", SystemDateTime);
		pdBill.put("QueryFeild", QueryFeild);
		pdBill.put("TableName", TableNameSecondDetail);
		pdBill.put("GroupbyFeild", strSumFieldBill);
		String SelectFeildBill = Common.getSumFeildSelect(SumFieldBill, tableDetailColumns, TmplUtil.keyExtra);
		pdBill.put("SelectFeild", SelectFeildBill);
		List<PageData> getSaveBill = housefunddetailService.getSum(pdBill);
		//TableName CanOperate

		String CanOperNotReport = QueryFeildString.getNotReportBillCode(TypeCodeTransfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		String CanOperNotReportNotInSumInvalidBill = CanOperNotReport + FilterBillCode.getBillCodeNotInSumInvalidBill();
		String CanOperNotReportNotInSumInvalidDetail = CanOperNotReport + FilterBillCode.getBillCodeNotInSumInvalidDetail(TableNameBase);
		
		PageData pdBillNum=new PageData();
		if(bolDeleteSummy){//删除添加
			for(PageData bill : getSaveBill){
				bill.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		bill.put("BILL_USER", user.getUSER_ID());
        		bill.put("BILL_DATE", DateUtil.getTime());
        		bill.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
        		bill.put("TableName", TableNameBase);
        		bill.put("CanOperateBill", CanOperNotReportNotInSumInvalidBill);//未传输 未作废
        		bill.put("CanOperateDetail", CanOperNotReportNotInSumInvalidDetail);//未传输 未作废
                //添加未设置字段默认值
    			Common.setModelDefault(bill, map_HaveColumnsListBill, map_SetColumnsListBill);
			}
			for(PageData detail : getSaveDetail){
				detail.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		detail.put("BILL_USER", user.getUSER_ID());
        		detail.put("BILL_DATE", DateUtil.getTime());
        		detail.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
				detail.put("TableName", TableNameFirstDetail);
                //添加未设置字段默认值
    			Common.setModelDefault(detail, map_HaveColumnsListDetail, map_SetColumnsListDetail);
			}
		} else {//设置单号，直接添加
			/***************获取最大单号及更新最大单号********************/
		    String billNumType = BillNumType.ZFGJ;
			String month = DateUtil.getMonth();
			pdBillNum.put("BILL_CODE", billNumType);
			pdBillNum.put("BILL_DATE", month);
			PageData pdBillNumResult=sysbillnumService.findById(pdBillNum);
			if(pdBillNumResult == null){
				pdBillNumResult = new PageData();
			}
			Object objGetNum = pdBillNumResult.get("BILL_NUMBER");
			if(!(objGetNum != null && !objGetNum.toString().trim().equals(""))){
				objGetNum = 0;
			}
			int getNum = (int) objGetNum;
			int billNum=getNum;
			/***************************************************/
			for(PageData bill : getSaveBill){
				billNum++;
				String getBILL_CODE = BillCodeUtil.getBillCode(billNumType, month, billNum);
				bill.put("BILL_CODE", getBILL_CODE);
				bill.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		bill.put("BILL_USER", user.getUSER_ID());
        		bill.put("BILL_DATE", DateUtil.getTime());
        		bill.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
        		bill.put("TableName", TableNameBase);
                //添加未设置字段默认值
    			Common.setModelDefault(bill, map_HaveColumnsListBill, map_SetColumnsListBill);
			}
			getSaveDetail = getListTo(getSaveBill, getSaveDetail, SumFieldBill);
			getDetailSetBillCode = getListTo(getSaveBill, getDetailSetBillCode, SumFieldBill);
			
			//未匹配的单号和没有单号的记录
			for(PageData detail : getSaveDetail){
				detail.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		detail.put("BILL_USER", user.getUSER_ID());
        		detail.put("BILL_DATE", DateUtil.getTime());
        		detail.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
				detail.put("TableName", TableNameFirstDetail);
                //添加未设置字段默认值
    			Common.setModelDefault(detail, map_HaveColumnsListDetail, map_SetColumnsListDetail);
    			
				Object getBILL_CODE = detail.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					commonBase.setCode(2);
				}
			}
			for(PageData setBillode : getDetailSetBillCode){
				setBillode.put("updateFilter", " and BILL_CODE like ' %' ");
				
				Object getBILL_CODE = setBillode.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					commonBase.setCode(2);
				}
			}
			
			//单号没变化，pdBillNum为null，不更新数据库单号
		    if(getNum == billNum){
				pdBillNum = null;
			} else {
				pdBillNum.put("BILL_NUMBER", billNum);
			}
		}
        if(commonBase.getCode() == -1){
        	Map<String, Object> map = new HashMap<String, Object>();
            if(pdBillNum!=null && pdBillNum.size()>0){
            	map.put("UpdateBillNum", pdBillNum);
            }
            if(bolDeleteSummy){
            	map.put("DetailBillAndDetail", getSaveBill);
            }
        	map.put("SaveBill", getSaveBill);
        	map.put("SaveDetail", getSaveDetail);
            if(!bolDeleteSummy){
            	map.put("DetailSetBillCode", getDetailSetBillCode);
            }
			housefundsummyService.saveSummyModelList(map);
			commonBase.setCode(0);
        }
		return commonBase;
	}
	
	private List<PageData> getListTo(List<PageData> listBill, List<PageData> listDetail, List<String> SumFieldBillAll){
	    if(listBill!=null && listBill.size()>0 && listDetail!=null && listDetail.size()>0 && SumFieldBillAll!=null){
	    	List<String> SumFieldBill = new ArrayList<String>();
	    	for(String feild : SumFieldBillAll){
	    		if(!feild.equals("BILL_CODE")){
	    			SumFieldBill.add(feild);
	    		}
	    	}
			for(PageData bill : listBill){
				String getbillCode = bill.getString("BILL_CODE");
				for(PageData detail : listDetail){
					Boolean bol = true;
					for(String field : SumFieldBill){
						String strBill = (String) bill.get(field);
						if(strBill == null) strBill = "";
						String strDetail = (String) detail.get(field);
						if(strDetail == null) strDetail = "";
						if(!strBill.equals(strDetail)){
							bol = false;
						}
					}
					if(bol){
						detail.put("BILL_CODE", getbillCode);
					}
				}
			}
	    }
		return listDetail;
	}

	//判断已传输或作废或删除
	private String CheckState(List<PageData> pdSerialNo) throws Exception{
		String strRut = "";
		//List<PageData> pdBillCode = staffdetailService.getBillCodeBySerialNo(pdSerialNo);
		//if(pdBillCode != null){
		//	for(PageData pd : pdBillCode){
		//		String BILL_CODE = pd.getString("BILL_CODE");
		//		if(BILL_CODE!=null && !BILL_CODE.trim().equals("")){
		//			strRut = Message.OperDataAlreadySum;
		//		}
		//	}
		//}
		return strRut;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
