package com.fh.controller.staffDetail.staffdetail;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.Common;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.Message;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.CommonBaseAndList;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.User;
import com.fh.exception.CustomException;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.BillState;
import com.fh.util.enums.EmplGroupType;
import com.fh.util.enums.StaffDataType;
import com.fh.util.enums.SysConfigKeyCode;
import com.fh.util.enums.TmplType;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.excel.LeadingInExcelToPageData;
import com.fh.util.excel.TransferSbcDbc;

import net.sf.json.JSONArray;

import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.staffDetail.staffdetail.StaffDetailManager;
import com.fh.service.staffsummy.staffsummy.StaffSummyManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明：工资明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-16
 */
@Controller
@RequestMapping(value="/staffdetail")
public class StaffDetailController extends BaseController {
	
	String menuUrl = "staffdetail/list.do"; //菜单地址(权限用)
	@Resource(name="staffdetailService")
	private StaffDetailManager staffdetailService;
	@Resource(name="staffsummyService")
	private StaffSummyManager staffsummyService;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;
	@Resource(name="sysconfigService")
	private SysConfigManager sysConfigManager;
	@Resource(name="tmplconfigdictService")
	private TmplConfigDictService tmplconfigdictService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="departmentService")
	private DepartmentService departmentService;
	@Resource(name = "userService")
	private UserManager userService;

	//表名
	String TableNameDetail = "TB_STAFF_DETAIL";
	String TableNameSummy = "TB_STAFF_SUMMY_BILL";
	String TableNameBackup = "TB_STAFF_DETAIL_backup";
	//临时数据
	String SelectBillCodeFirstShow = "临时数据";
	String SelectBillCodeLastShow = "";
	//个税字段，不能定义公式，
	String TableFeildTaxCanNotHaveFormula = "ACCRD_TAX";
	//税字段
	String TableFeildSumOper = "SUM_OPER";

	//默认的which值
	String DefaultWhile = TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey();

	//页面显示数据的年月
	String SystemDateTime = "";
    //
	String AdditionalReportColumns = "";
	//
	private List<String> MustInputList = Arrays.asList("USER_CODE", "DATA_TYPE", "UNITS_CODE");
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("DEPT_CODE", "CUST_COL7", "USER_GROP");
    //设置必定不用编辑的列            SERIAL_NO 设置字段类型是数字，但不管隐藏 或显示都必须保存的
    List<String> MustNotEditList = Arrays.asList("SERIAL_NO", "BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP");
	// 查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
    List<String> keyListAdd = new ArrayList<String>();
	List<String> keyListBase = getKeyListBase();
	private List<String> getKeyListBase(){
		List<String> list = new ArrayList<String>();
		for(String strFeild : MustNotEditList){
			if (!list.contains(strFeild)) {
			    list.add(strFeild);
			}
		}
		for(String strFeild : keyListAdd){
			if (!list.contains(strFeild)) {
				list.add(strFeild);
			}
		}
		return list;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表StaffDetail");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("staffDetail/staffdetail/staffdetail_list");
		//while
		getPd.put("which", SelectedTableNo);
		//单号下拉列表
		//getPd.put("SelectNoBillCodeShow", SelectBillCodeFirstShow);
		getPd.put("InitBillCodeOptions", SelectBillCodeOptions.getSelectBillCodeOptions(null, SelectBillCodeFirstShow, SelectBillCodeLastShow));
		mv.addObject("SystemDateTime", SystemDateTime);
		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String DepartName = user.getDEPARTMENT_NAME();
		mv.addObject("DepartName", DepartName);
		
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
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		
		PageData transferPd = new PageData();
		transferPd.put("SelectedCustCol7", SelectedCustCol7);
		transferPd.put("SelectedDepartCode", SelectedDepartCode);
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("emplGroupType", emplGroupType);
		String strCanOperate = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
			strCanOperate += " and 1 != 1 ";
		} else {
			strCanOperate += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
			strCanOperate += " and 1 != 1 ";
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			strCanOperate += " and 1 != 1 ";
		}
		transferPd.put("CanOperate", strCanOperate);
		List<String> getCodeList = staffdetailService.getBillCodeList(transferPd);
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
		//员工组 必须执行，用来设置汇总和传输上报类型
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
				departmentService,userService, keyListBase, null, AdditionalReportColumns, MustInputList);
		String jqGridColModel = tmpl.generateStructure(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, 3, MustNotEditList);
		
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
		logBefore(logger, Jurisdiction.getUsername()+"列表StaffDetail");

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		QueryFeild += QueryFeildString.getQueryFeildBillCodeDetail(SelectedBillCode, SelectBillCodeFirstShow);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			QueryFeild += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		QueryFeild += QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		getPd.put("QueryFeild", QueryFeild);
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(keyListBase, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			getPd.put("FieldSelectKey", strFieldSelectKey);
		}
		page.setPd(getPd);
		List<PageData> varList = staffdetailService.JqPage(page);	//列出Betting列表
		int records = staffdetailService.countJqGridExtend(page);
		PageData userdata = null;
		//底行显示的求和与平均值字段
		StringBuilder SqlUserdata = Common.GetSqlUserdata(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		if(SqlUserdata!=null && !SqlUserdata.toString().trim().equals("")){
			//底行显示的求和与平均值字段
			getPd.put("Userdata", SqlUserdata.toString());
			userdata = staffdetailService.getFooterSummary(page);
		}
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setRecords(records);
		result.setPage(page.getPage());
		result.setUserdata(userdata);
		
		return result;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public @ResponseBody CommonBase edit() throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		logBefore(logger, Jurisdiction.getUsername()+"修改StaffDetail");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");
		//操作
		String oper = getPd.getString("oper");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
			return commonBase;
		}
		
		String strHelpful = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			strHelpful += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		if(!(strHelpful != null && !strHelpful.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.GetHelpfulDetailFalue);
			return commonBase;
		}
		
		//必定不用编辑的列  MustNotEditList  Arrays.asList("SERIAL_NO", "BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP");
		if(oper.equals("add")){
			getPd.put("SERIAL_NO", "");
			getPd.put("BUSI_DATE", SystemDateTime); 
			getPd.put("DEPT_CODE", SelectedDepartCode); 
			getPd.put("CUST_COL7", SelectedCustCol7); 
			getPd.put("USER_GROP", emplGroupType); 
			if(SelectedBillCode.equals(SelectBillCodeFirstShow)){
				getPd.put("BILL_CODE", "");
			} else {
				getPd.put("BILL_CODE", SelectedBillCode);
			}
			String getESTB_DEPT = (String) getPd.get("ESTB_DEPT");
			if(!(getESTB_DEPT!=null && !getESTB_DEPT.trim().equals(""))){
				getPd.put("ESTB_DEPT", SelectedDepartCode);
			}
			List<PageData> listData = new ArrayList<PageData>();
			listData.add(getPd);
			commonBase = CalculationUpdateDatabase(false, true, commonBase, "",
					SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
					listData, strHelpful);
		} else {
			Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
			Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(SelectedTableNo, tmplconfigService);
			List<PageData> listCheckState = new ArrayList<PageData>();
			listCheckState.add(getPd);
			String checkState = CheckState(SelectedBillCode,
					SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, listCheckState, "SERIAL_NO", TmplUtil.keyExtra);
			if(checkState!=null && !checkState.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(checkState);
				return commonBase;
			}
			for(String strFeild : MustNotEditList){
				getPd.put(strFeild, getPd.get(strFeild + TmplUtil.keyExtra));
			}
			Common.setModelDefault(getPd, map_HaveColumnsList, map_SetColumnsList, MustNotEditList);
			getPd.put("TableName", TableNameDetail);
			getPd.put("CanOperate", strHelpful);
			
			List<PageData> listData = new ArrayList<PageData>();
			listData.add(getPd);

			//此处执行集合添加 
			staffdetailService.batchUpdateDatabase(listData);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}
	
	 /**批量修改
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/updateAll")
	public @ResponseBody CommonBase updateAll() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
			return commonBase;
		}

		String strHelpful = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
		    strHelpful += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		if(!(strHelpful != null && !strHelpful.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.GetHelpfulDetailFalue);
			return commonBase;
		}
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(SelectedTableNo, tmplconfigService);
        for(PageData item : listData){
        	item.put("CanOperate", strHelpful);
      	    item.put("TableName", TableNameDetail);
        	Common.setModelDefault(item, map_HaveColumnsList, map_SetColumnsList, MustNotEditList);
        }
		if(null != listData && listData.size() > 0){
			//此处执行集合添加 
			staffdetailService.batchUpdateDatabase(listData);
			commonBase.setCode(0);
		}
		return commonBase;
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
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
			return commonBase;
		}
		
		String strHelpful = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			strHelpful += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		if(!(strHelpful != null && !strHelpful.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.GetHelpfulDetailFalue);
			return commonBase;
		}
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
        if(null != listData && listData.size() > 0){
        	for(PageData item : listData){
        	    item.put("CanOperate", strHelpful);
            }
			staffdetailService.deleteAll(listData);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}

	 /**计算
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/calculation")
	public @ResponseBody CommonBase calculation() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "calculation")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
			return commonBase;
		}

		String strHelpful = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			strHelpful += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
		}
		if(!(strHelpful != null && !strHelpful.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.GetHelpfulDetailFalue);
			return commonBase;
		}
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
		/*List<PageData> listAddSalary = new ArrayList<PageData>();
		List<PageData> listAddBonus = new ArrayList<PageData>();
        for(PageData item : listData){
         	item.put("CanOperate", strHelpful);
       	    Common.setModelDefault(item, map_HaveColumnsList, map_SetColumnsList);

			String strDATA_TYPE = item.getString("DATA_TYPE");
			if(!(strDATA_TYPE!=null && (!strDATA_TYPE.trim().equals("")))){
				commonBase.setCode(2);
				commonBase.setMessage(Message.RowDataTypeMustInput);
				return commonBase;
			}
			if(!(strDATA_TYPE.trim().equals(StaffDataType.Salary.getNameKey()) || strDATA_TYPE.trim().equals(StaffDataType.Bonus.getNameKey()))){
				commonBase.setCode(2);
				commonBase.setMessage(Message.RowDataTypeInputError);
				return commonBase;
			}
			if(strDATA_TYPE.equals(StaffDataType.Salary.getNameKey())){
				listAddSalary.add(item);
			}
			if(strDATA_TYPE.equals(StaffDataType.Bonus.getNameKey())){
				listAddBonus.add(item);
			}
        }
		List<PageData> dataCalculation = getCalculation(SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
				listAddSalary, listAddBonus);
		String strJson =JSONArray.fromObject(dataCalculation).toString();
		if(strJson.startsWith("[")) strJson = strJson.substring(1);
		if(strJson.endsWith("]")) strJson = strJson.substring(0, strJson.length()-1);
		commonBase.setCode(0);
		commonBase.setMessage(strJson);*/
		commonBase = CalculationUpdateDatabase(false, false, commonBase, "",
				SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
				listData, strHelpful);
		return commonBase;
	}

	/**打开上传EXCEL页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/goUploadExcel")
	public ModelAndView goUploadExcel()throws Exception{
		CommonBase commonBase = new CommonBase();
	    commonBase.setCode(-1);
	    
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");
		//工资或奖金枚举编码
		String SalaryOrBonus = getPd.getString("SalaryOrBonus");

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
		}
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow) && commonBase.getCode() != 2){
			String checkState = CheckState(SelectedBillCode,
					SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, null, "SERIAL_NO", TmplUtil.keyExtra);
			if(checkState!=null && !checkState.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(checkState);
			}
		}
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "staffdetail");
		mv.addObject("which", SelectedTableNo);
		mv.addObject("SelectedDepartCode", SelectedDepartCode);
		mv.addObject("SelectedCustCol7", SelectedCustCol7);
		mv.addObject("SelectedBillCode", SelectedBillCode);
		mv.addObject("DepartTreeSource", DepartTreeSource);
		mv.addObject("ShowDataDepartCode", ShowDataDepartCode);
		mv.addObject("ShowDataCustCol7", ShowDataCustCol7);
		mv.addObject("ShowDataBillCode", ShowDataBillCode);
		mv.addObject("SalaryOrBonus", SalaryOrBonus);
		mv.addObject("commonBaseCode", commonBase.getCode());
		mv.addObject("commonMessage", commonBase.getMessage());
		return mv;
	}

	/**从EXCEL导入到数据库
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/readExcel")
	public ModelAndView readExcel(@RequestParam(value="excel",required=false) MultipartFile file) throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;}//校验权限
		
		String strErrorMessage = "";
		//String strImportDataType = "";
	    String CurrentDepartCode = Jurisdiction.getCurrentDepartmentID();

		String YXRY = EmplGroupType.YXRY.getNameKey();
		String LWPQ = EmplGroupType.LWPQ.getNameKey();
		//责任中心-管道分公司廊坊油气储运公司-0100106
		String DEPT_CODE_0100106 = "0100106";
		//责任中心-华北石油管理局-0100107
		String DEPT_CODE_0100107 = "0100107";
		//责任中心-中国石油天然气管道局-0100108
		String DEPT_CODE_0100108 = "0100108";
		//责任中心-华北采油二厂-0100109
		String DEPT_CODE_0100109 = "0100109";

		//工资范围编码-东零
		String SAL_RANGE_dong_0 = "S12";
		//企业特定员工分类-管道局劳务-PUT05
		String USER_CATG_GDJLW = "PUT05";
		//企业特定员工分类-华北油田公司劳务-PUT06
		String USER_CATG_hbytgslw = "PUT06";
		//企业特定员工分类-华北采油二厂劳务-PUT07
		String USER_CATG_HBCYECLW = "PUT07";
		//企业特定员工分类-管道公司劳务-PUT08
		String USER_CATG_GDGSLW = "PUT08";

		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String strTypeCodeTramsfer = getWhileValueToTypeCodeTramsfer(SelectedTableNo);
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//
		String DepartTreeSource = getPd.getString("DepartTreeSource");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		String ShowDataCustCol7 = getPd.getString("ShowDataCustCol7");
		String ShowDataBillCode = getPd.getString("ShowDataBillCode");
		//工资或奖金枚举编码
		String SalaryOrBonus = getPd.getString("SalaryOrBonus");
		
		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(emplGroupType, 
				SelectedCustCol7, ShowDataCustCol7, 
				SelectedDepartCode, ShowDataDepartCode, DepartTreeSource,
				SelectedBillCode, ShowDataBillCode);
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(strGetCheckMustSelected);
		} else {
			if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
				String checkState = CheckState(SelectedBillCode,
						SelectedCustCol7, SelectedDepartCode, emplGroupType, strTypeCodeTramsfer, null, "SERIAL_NO", TmplUtil.keyExtra);
				if(checkState!=null && !checkState.trim().equals("")){
					commonBase.setCode(2);
					commonBase.setMessage(checkState);
				}
			}
			if(commonBase.getCode() != 2){
				if(!(SystemDateTime!=null && !SystemDateTime.trim().equals("")
						&& SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
					commonBase.setCode(2);
					commonBase.setMessage("当前区间和当前单位不能为空！");
				} else {
					String strHelpful = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
					if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
						strHelpful += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, SelectedDepartCode);
					}
					if(!(strHelpful != null && !strHelpful.trim().equals(""))){
						commonBase.setCode(2);
						commonBase.setMessage(Message.GetHelpfulDetailFalue);
					} else {
						Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
						Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(SelectedTableNo, tmplconfigService);
						Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, 
								tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
						// 局部变量
						LeadingInExcelToPageData<PageData> testExcel = null;
						Map<Integer, Object> uploadAndReadMap = null;
						try {
							// 定义需要读取的数据
							String formart = "yyyy-MM-dd";
							String propertiesFileName = "config";
							String kyeName = "file_path";
							int sheetIndex = 0;
							Map<String, String> titleAndAttribute = null;
							// 定义对应的标题名与对应属性名
							titleAndAttribute = new LinkedHashMap<String, String>();
							
							//配置表设置列
							if(map_SetColumnsList != null && map_SetColumnsList.size() > 0){
								for (TmplConfigDetail col : map_SetColumnsList.values()) {
									titleAndAttribute.put(TransferSbcDbc.ToDBC(col.getCOL_NAME()), col.getCOL_CODE());
								}
							}
							// 调用解析工具包
							testExcel = new LeadingInExcelToPageData<PageData>(formart);
							// 解析excel，获取客户信息集合
							
							Boolean bolIsDicSetSAL_RANGE = false;
							Boolean bolIsDicSetUSER_CATG = false;
						    if((CurrentDepartCode!=null && CurrentDepartCode.equals(DictsUtil.DepartShowAll))
						    		&& (emplGroupType.equals(YXRY))
									&& (SelectedDepartCode!=null && SelectedDepartCode.equals(DictsUtil.DepartShowAll))){
						    	bolIsDicSetSAL_RANGE = true;
						    }
						    if((CurrentDepartCode!=null && CurrentDepartCode.equals(DictsUtil.DepartShowAll))
						    		&& (emplGroupType.equals(YXRY))
									&& (SelectedDepartCode.equals(DEPT_CODE_0100107) || SelectedDepartCode.equals(DEPT_CODE_0100108) || SelectedDepartCode.equals(DEPT_CODE_0100106) || SelectedDepartCode.equals(DEPT_CODE_0100109))){
						    	//LWPQ.equals(getUSER_GROP) && (USER_CATG_GDJLW.equals(getUSER_CATG) || USER_CATG_hbytgslw.equals(getUSER_CATG))
						    	bolIsDicSetUSER_CATG = true;
						    }
							uploadAndReadMap = testExcel.uploadAndRead(file, propertiesFileName, kyeName, sheetIndex,
									titleAndAttribute, map_HaveColumnsList, map_SetColumnsList, DicList, bolIsDicSetSAL_RANGE, bolIsDicSetUSER_CATG);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("读取Excel文件错误", e);
							throw new CustomException("读取Excel文件错误:" + e.getMessage(),false);
						}
						boolean judgement = false;

						Map<String, Object> returnError =  (Map<String, Object>) uploadAndReadMap.get(2);
						if(returnError != null && returnError.size()>0){
							strErrorMessage += "字典无此翻译： "; // \n
							for (String k : returnError.keySet())  
						    {
								strErrorMessage += k + " : " + returnError.get(k);
						    }
						}

						List<PageData> listUploadAndRead = (List<PageData>) uploadAndReadMap.get(1);
						List<PageData> listAdd = new ArrayList<PageData>();
						if (listUploadAndRead != null && !"[]".equals(listUploadAndRead.toString()) && listUploadAndRead.size() >= 1) {
							judgement = true;
						}
						if (judgement) {
							List<String> sbRet = new ArrayList<String>();
							int listSize = listUploadAndRead.size();
							if(listSize > 0){
								List<PageData> listAddSalary = new ArrayList<PageData>();
								List<PageData> listAddBonus = new ArrayList<PageData>();
								for(int i=0;i<listSize;i++){
									PageData pdAdd = listUploadAndRead.get(i);
									String getUSER_CODE = (String) pdAdd.get("USER_CODE");
									if(getUSER_CODE!=null && !getUSER_CODE.trim().equals("")){
									    String getCUST_COL7 = (String) pdAdd.get("CUST_COL7");
									    String getUSER_GROP = (String) pdAdd.get("USER_GROP");
										
										if(!(getCUST_COL7!=null && !getCUST_COL7.trim().equals(""))){
										    pdAdd.put("CUST_COL7", SelectedCustCol7);
										    getCUST_COL7 = SelectedCustCol7;
									    }
									    /*if(!SelectedCustCol7.equals(getCUST_COL7)){
									    	continue;
									    }*/
									    /*if(!(getUSER_GROP!=null && !getUSER_GROP.trim().equals(""))){
									        pdAdd.put("USER_GROP", emplGroupType);
									        getUSER_GROP = emplGroupType;
								        }*/

									    //工资范围编码
										String getSAL_RANGE = (String) pdAdd.get("SAL_RANGE");
										//企业特定员工分类
										String getUSER_CATG = (String) pdAdd.get("USER_CATG");
									    if((CurrentDepartCode!=null && CurrentDepartCode.equals(DictsUtil.DepartShowAll))
									    		&& (emplGroupType.equals(YXRY))
												&& (SelectedDepartCode!=null && SelectedDepartCode.equals(DictsUtil.DepartShowAll))){
									    	if(YXRY.equals(getUSER_GROP)){
									    	    continue;
									    	}
									    	if(LWPQ.equals(getUSER_GROP) && SAL_RANGE_dong_0.equals(getSAL_RANGE)){
											    pdAdd.put("USER_GROP", YXRY);
											    getUSER_GROP = YXRY;
									    	}
									    }
									    if((CurrentDepartCode!=null && CurrentDepartCode.equals(DictsUtil.DepartShowAll))
									    		&& (emplGroupType.equals(YXRY))
												&& (SelectedDepartCode.equals(DEPT_CODE_0100107) || SelectedDepartCode.equals(DEPT_CODE_0100108) || SelectedDepartCode.equals(DEPT_CODE_0100106) || SelectedDepartCode.equals(DEPT_CODE_0100109))){
									    	if(YXRY.equals(getUSER_GROP)){
									    	    continue;
									    	}
									    	if(LWPQ.equals(getUSER_GROP)){
											    if(!((SelectedDepartCode.equals(DEPT_CODE_0100107) && USER_CATG_hbytgslw.equals(getUSER_CATG))
											    		|| (SelectedDepartCode.equals(DEPT_CODE_0100108) && USER_CATG_GDJLW.equals(getUSER_CATG))
											    		|| (SelectedDepartCode.equals(DEPT_CODE_0100106) && USER_CATG_GDGSLW.equals(getUSER_CATG))
											    		|| (SelectedDepartCode.equals(DEPT_CODE_0100109) && USER_CATG_HBCYECLW.equals(getUSER_CATG)))){
										    	    continue;
											    }
											    pdAdd.put("USER_GROP", YXRY);
											    getUSER_GROP = YXRY;
									    	}
									    }
									    
									    if(!emplGroupType.equals(getUSER_GROP)){
								    	    continue;
								        }
										String SCH = EmplGroupType.SCH.getNameKey();
										String HTH = EmplGroupType.HTH.getNameKey();
										String XTNLW = EmplGroupType.XTNLW.getNameKey();
										if((CurrentDepartCode!=null && CurrentDepartCode.equals(DictsUtil.DepartShowAll))
												&& (SelectedDepartCode!=null && SelectedDepartCode.equals(DictsUtil.DepartShowAll))
												&& (emplGroupType.equals(SCH) || emplGroupType.equals(HTH) || emplGroupType.equals(XTNLW))){
											//账套-新西气东输公司-9870
											String CUST_COL7_xxqdsgs = "9870";
											//企业特定员工分类-东部管道机关-PUT02
											String USER_CATG_DBGDJG = "PUT02";
											//账套-西气东输管道-9100
											String CUST_COL7_xqdsgd = "9100";
											//企业特定员工分类-西气东输管道机关-PUT04
											String USER_CATG_XQDSGDJG = "PUT04";
											
											//工资范围编码-东零    String SAL_RANGE_dong_0 = "S12";
											if(!(getSAL_RANGE!=null && getSAL_RANGE.equals(SAL_RANGE_dong_0))){
									    	    continue;
											}
											//账套-新西气东输公司-9870 String CUST_COL7_xxqdsgs = "9870";
											//企业特定员工分类-东部管道机关-PUT02 String USER_CATG_DBGDJG = "PUT02";
											if(getCUST_COL7.equals(CUST_COL7_xxqdsgs)){
												if(!(getUSER_CATG!=null && getUSER_CATG.equals(USER_CATG_DBGDJG))){
										    	    continue;
												}
											}
											//账套-西气东输管道-9100 String CUST_COL7_xqdsgd = "9100";
											//企业特定员工分类-西气东输管道机关-9870 String USER_CATG_XQDSGDJG = "PUT04";
											if(getCUST_COL7.equals(CUST_COL7_xqdsgd)){
												if(!(getUSER_CATG!=null && getUSER_CATG.equals(USER_CATG_XQDSGDJG))){
										    	    continue;
												}
											}
										}
										
									    if(!SelectedCustCol7.equals(getCUST_COL7)){
									    	if(!sbRet.contains("导入账套和当前账套必须一致！")){
											    sbRet.add("导入账套和当前账套必须一致！");
										    }
									    }
									    /*if(!emplGroupType.equals(getUSER_GROP)){
										    if(!sbRet.contains("导入员工组和当前员工组必须一致！")){
											    sbRet.add("导入员工组和当前员工组必须一致！");
										    }
									    }*/

										pdAdd.put("SERIAL_NO", "");
										String getBILL_CODE = (String) pdAdd.get("BILL_CODE");
										if(!(getBILL_CODE!=null && !getBILL_CODE.trim().equals(""))){
											if(SelectedBillCode.equals(SelectBillCodeFirstShow)){
												pdAdd.put("BILL_CODE", "");
												getBILL_CODE = "";
											} else {
												pdAdd.put("BILL_CODE", SelectedBillCode);
												getBILL_CODE = SelectedBillCode;
											}
										}
										if(SelectedBillCode.equals(SelectBillCodeFirstShow)){
											if(!"".equals(getBILL_CODE)){
												if(!sbRet.contains("导入单号和当前单号必须一致！")){
													sbRet.add("导入单号和当前单号必须一致！");
												}
											}
										} else {
											if(!SelectedBillCode.equals(getBILL_CODE)){
												if(!sbRet.contains("导入单号和当前单号必须一致！")){
													sbRet.add("导入单号和当前单号必须一致！");
												}
											}
										}
										String getBUSI_DATE = (String) pdAdd.get("BUSI_DATE");
										String getDEPT_CODE = (String) pdAdd.get("DEPT_CODE");
										String getUNITS_CODE = (String) pdAdd.get("UNITS_CODE");
										/*String getDATA_TYPE = (String) pdAdd.get("DATA_TYPE");*/
										/*if(!(getDATA_TYPE!=null && !getDATA_TYPE.trim().equals(""))){
											if(!sbRet.contains("导入数据类型列不能为空！")){
												sbRet.add("导入数据类型列不能为空！");
											}
										} else {
											if(strImportDataType!=null && !strImportDataType.trim().equals("")){
												if(!getDATA_TYPE.equals(strImportDataType)){
													if(!sbRet.contains("导入数据类型必须一致！")){
														sbRet.add("导入数据类型必须一致！");
													}
												}
											} else {
												strImportDataType = getDATA_TYPE;
											}
										}*/
										pdAdd.put("DATA_TYPE", SalaryOrBonus);
										if(SalaryOrBonus!=null && (!SalaryOrBonus.trim().equals(""))){
											if(!(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey()) || SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey()))){
												if(!sbRet.contains(Message.RowDataTypeInputError)){
													sbRet.add(Message.RowDataTypeInputError);
												}
											}
											if(SalaryOrBonus.equals(StaffDataType.Salary.getNameKey())){
												listAddSalary.add(pdAdd);
											}
											if(SalaryOrBonus.equals(StaffDataType.Bonus.getNameKey())){
												listAddBonus.add(pdAdd);
											}
										} else {
											if(!sbRet.contains(Message.RowDataTypeMustInput)){
												sbRet.add(Message.RowDataTypeMustInput);
											}
										}
										if(!(getBUSI_DATE!=null && !getBUSI_DATE.trim().equals(""))){
											pdAdd.put("BUSI_DATE", SystemDateTime);
											getBUSI_DATE = SystemDateTime;
										}
										if(!SystemDateTime.equals(getBUSI_DATE)){
											if(!sbRet.contains("导入区间和当前区间必须一致！")){
												sbRet.add("导入区间和当前区间必须一致！");
											}
										}
										if(!(getDEPT_CODE!=null && !getDEPT_CODE.trim().equals(""))){
											pdAdd.put("DEPT_CODE", SelectedDepartCode);
											getDEPT_CODE = SelectedDepartCode;
										}
										if(!SelectedDepartCode.equals(getDEPT_CODE)){
											if(!sbRet.contains("导入单位和当前单位必须一致！")){
												sbRet.add("导入单位和当前单位必须一致！");
											}
										}
										if(!(getUSER_CODE!=null && !getUSER_CODE.trim().equals(""))){
											if(!sbRet.contains("人员编码不能为空！")){
												sbRet.add("人员编码不能为空！");
											}
										}
										if(!(getUNITS_CODE!=null && !getUNITS_CODE.trim().equals(""))){
											if(!sbRet.contains("所属二级单位不能为空！")){
												sbRet.add("所属二级单位不能为空！");
											}
										}
										String getESTB_DEPT = (String) pdAdd.get("ESTB_DEPT");
										if(!(getESTB_DEPT!=null && !getESTB_DEPT.trim().equals(""))){
											pdAdd.put("ESTB_DEPT", SelectedDepartCode);
										}
										//Common.setModelDefault(pdAdd, map_HaveColumnsList, map_SetColumnsList);
										//pdAdd.put("CanOperate", strHelpful);
										//pdAdd.put("TableName", TableNameBackup);
										listAdd.add(pdAdd);
									}
								}
								if(sbRet.size()>0){
									StringBuilder sbTitle = new StringBuilder();
									for(String str : sbRet){
										sbTitle.append(str + "  "); // \n
									}
									commonBase.setCode(2);
									commonBase.setMessage(sbTitle.toString());
								} else {
									if(!(listAdd!=null && listAdd.size()>0)){
										commonBase.setCode(2);
										commonBase.setMessage("请导入符合条件的数据！");
									} else {
										String strCalculationMessage = "";
										CommonBaseAndList getCommonBaseAndList = new CommonBaseAndList();
										if(SelectedTableNo.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey()) 
												|| SelectedTableNo.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())){
											getCommonBaseAndList = getCalculationData(true, true, commonBase,
													SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
													listAdd, strHelpful);
											for(PageData pdSet : getCommonBaseAndList.getList()){
												String pdSetUSER_CODE = pdSet.getString("USER_CODE");
												BigDecimal douCalTax = new BigDecimal(0);
												BigDecimal douImpTax = new BigDecimal(0);
												BigDecimal douYDRZE = new BigDecimal(0);
												BigDecimal douYSZE = new BigDecimal(0);
												for(PageData pdsum : getCommonBaseAndList.getList()){
													String pdsumUSER_CODE = pdsum.getString("USER_CODE");
													if(pdSetUSER_CODE!=null && pdSetUSER_CODE.equals(pdsumUSER_CODE)){
														douCalTax = douCalTax.add(new BigDecimal(pdsum.get(TableFeildTaxCanNotHaveFormula).toString()));
														douImpTax = douImpTax.add(new BigDecimal(pdsum.get(TableFeildTaxCanNotHaveFormula + TmplUtil.keyExtra).toString()));

														douYDRZE = douYDRZE.add(new BigDecimal(pdsum.get("YDRZE").toString()));
														
														douYSZE = douYSZE.add(new BigDecimal(pdsum.get("YSZE").toString()));
													}
												}
												pdSet.put(TableFeildTaxCanNotHaveFormula + TmplUtil.keyExtra + TmplUtil.keyExtra, douCalTax);
												pdSet.put(TableFeildTaxCanNotHaveFormula + TmplUtil.keyExtra + TmplUtil.keyExtra + TmplUtil.keyExtra, douImpTax);
												pdSet.put("YDRZE" + TmplUtil.keyExtra, douYDRZE);
												pdSet.put("YSZE" + TmplUtil.keyExtra, douYSZE);
											}
											List<String> listUserCode = new ArrayList<String>();
											for(PageData pdSet : getCommonBaseAndList.getList()){
												String pdSetUSER_CODE = pdSet.getString("USER_CODE");
												if(!listUserCode.contains(pdSetUSER_CODE)){
													BigDecimal douCalTax = new BigDecimal(pdSet.get(TableFeildTaxCanNotHaveFormula + TmplUtil.keyExtra + TmplUtil.keyExtra).toString());
													BigDecimal douImpTax = new BigDecimal(pdSet.get(TableFeildTaxCanNotHaveFormula + TmplUtil.keyExtra + TmplUtil.keyExtra + TmplUtil.keyExtra).toString());
													BigDecimal douYSZE = new BigDecimal(pdSet.get("YSZE" + TmplUtil.keyExtra).toString());
													BigDecimal douYDRZE = new BigDecimal(pdSet.get("YDRZE" + TmplUtil.keyExtra).toString());
													if(!(douCalTax.compareTo(douImpTax) == 0)){
														strCalculationMessage += "员工编号:" + pdSetUSER_CODE 
																+ " 员工姓名:" + pdSet.getString("USER_NAME")
																+ " 应税总额:" + douYSZE 
																+ " 已导入纳税额:" + (douYDRZE.subtract(douImpTax)) 
																+ " 本次导入纳税额:" + douImpTax 
																+ " 实际应导入纳税额:" + douCalTax + "     ";//"<br/>";
													}
												}
												listUserCode.add(pdSetUSER_CODE);
											}
										} else {
											getCommonBaseAndList.setCommonBase(commonBase);
											getCommonBaseAndList.setList(listAdd);
										}
										if(strCalculationMessage!=null && !strCalculationMessage.trim().equals("")){
											commonBase.setCode(3);
											commonBase.setMessage(strCalculationMessage);
										} else {
											commonBase = UpdateDatabase(true, commonBase, strErrorMessage,
													SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
													getCommonBaseAndList, strHelpful);
										}
										/*
										List<PageData> dataCalculation = getCalculation(SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
														listAddSalary, listAddBonus);
										if(dataCalculation!=null){
											for(PageData each : dataCalculation){
												each.put("SERIAL_NO", "");
												Common.setModelDefault(each, map_HaveColumnsList, map_SetColumnsList);
												each.put("CanOperate", strHelpful);
											}
										}
										//此处执行集合添加 
										staffdetailService.batchUpdateDatabase(dataCalculation);
										commonBase.setCode(0);
										commonBase.setMessage(strErrorMessage);*/
									}
								}
							}
						} else {
							commonBase.setCode(-1);
							commonBase.setMessage("TranslateUtil");
						}
					}
				}
			}
		}
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "staffdetail");
		mv.addObject("which", SelectedTableNo);
		mv.addObject("SelectedDepartCode", SelectedDepartCode);
		mv.addObject("SelectedCustCol7", SelectedCustCol7);
		mv.addObject("SelectedBillCode", SelectedBillCode);
		mv.addObject("DepartTreeSource", DepartTreeSource);
		mv.addObject("ShowDataDepartCode", ShowDataDepartCode);
		mv.addObject("ShowDataCustCol7", ShowDataCustCol7);
		mv.addObject("ShowDataBillCode", ShowDataBillCode);
		mv.addObject("SalaryOrBonus", SalaryOrBonus);
		mv.addObject("commonBaseCode", commonBase.getCode());
		mv.addObject("commonMessage", commonBase.getMessage());
		return mv;
	}
	
	private CommonBaseAndList getCalculationData(Boolean IsImport, Boolean IsAdd, CommonBase commonBase,
			String SelectedTableNo, String SelectedCustCol7, String SelectedDepartCode, String emplGroupType,
			List<PageData> listData, String strHelpful) throws Exception{
		CommonBaseAndList retCommonBaseAndList = new CommonBaseAndList();
		if(listData!=null && listData.size()>0){
			Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
			Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(SelectedTableNo, tmplconfigService);
			//个税字段，不能定义公式
			if(map_SetColumnsList.containsKey(TableFeildTaxCanNotHaveFormula.toUpperCase())){
				TmplConfigDetail col = map_SetColumnsList.get(TableFeildTaxCanNotHaveFormula.toUpperCase());
				if(col.getCOL_FORMULA()!=null && !col.getCOL_FORMULA().trim().equals("")){
					commonBase.setCode(2);
					commonBase.setMessage("应交税金字段不能定义公式，请联系管理员修改！");
					retCommonBaseAndList.setCommonBase(commonBase);
					return retCommonBaseAndList;
				}
			}
			
			List<PageData> listAddSalary = new ArrayList<PageData>();
			List<PageData> listAddBonus = new ArrayList<PageData>();
	        for(PageData item : listData){
	         	item.put("CanOperate", strHelpful);
          	    item.put("TableName", TableNameBackup);
	       	    Common.setModelDefault(item, map_HaveColumnsList, map_SetColumnsList, MustNotEditList);

				String strDATA_TYPE = item.getString("DATA_TYPE");
				if(!(strDATA_TYPE!=null && (!strDATA_TYPE.trim().equals("")))){
					commonBase.setCode(2);
					commonBase.setMessage(Message.RowDataTypeMustInput);
					retCommonBaseAndList.setCommonBase(commonBase);
					return retCommonBaseAndList;
				}
				if(!(strDATA_TYPE.trim().equals(StaffDataType.Salary.getNameKey()) || strDATA_TYPE.trim().equals(StaffDataType.Bonus.getNameKey()))){
					commonBase.setCode(2);
					commonBase.setMessage(Message.RowDataTypeInputError);
					retCommonBaseAndList.setCommonBase(commonBase);
					return retCommonBaseAndList;
				}
				if(strDATA_TYPE.equals(StaffDataType.Salary.getNameKey())){
					listAddSalary.add(item);
				}
				if(strDATA_TYPE.equals(StaffDataType.Bonus.getNameKey())){
					listAddBonus.add(item);
				}
	        }
			PageData getQueryFeildPd = new PageData();
			getQueryFeildPd.put("USER_GROP", emplGroupType);
			getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
			getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
			String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
			if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			QueryFeild += " and BUSI_DATE = '" + SystemDateTime + "' ";
			String strSumInvalidNotInsert = QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameSummy);
			
	        PageData SalaryExemptionTaxPd = new PageData();
	        SalaryExemptionTaxPd.put("KEY_CODE", SysConfigKeyCode.ExemptionTax);
	        String salaryExemptionTax = sysConfigManager.getSysConfigByKey(SalaryExemptionTaxPd);
	        PageData SalaryPd = new PageData();
	        SalaryPd.put("KEY_CODE", SysConfigKeyCode.StaffFormulaSalary);
	        String configFormulaSalary = sysConfigManager.getSysConfigByKey(SalaryPd);
	        PageData BonusPd = new PageData();
	        BonusPd.put("KEY_CODE", SysConfigKeyCode.StaffFormulaBonus);
	        String configFormulaBonus = sysConfigManager.getSysConfigByKey(BonusPd);

			//String sqlRetSelectSalary = Common.GetRetSelectColoumns(map_HaveColumnsList, 
			//		SelectedTableNo, TableNameBackup, SelectedDepartCode, 
			//		TableFeildTaxCanNotHaveFormula, TmplUtil.keyExtra, keyListBase, 
			//		tmplconfigService);
			String sqlRetSelect = Common.GetRetSelectNotCalculationColoumns(TableNameBackup, 
					TableFeildTaxCanNotHaveFormula, TmplUtil.keyExtra, keyListBase, 
					tmplconfigService);
			List<String> listSalaryFeildUpdate = Common.GetSalaryFeildUpdate(SelectedTableNo, TableNameBackup, SelectedDepartCode, SelectedCustCol7, 
					tmplconfigService);

			String sqlSumByUserCodeSalary = Common.GetRetSumByUserColoumns(TableNameBackup, QueryFeild + strSumInvalidNotInsert, configFormulaSalary, salaryExemptionTax, TableFeildSumOper, TableFeildTaxCanNotHaveFormula, StaffDataType.Salary.getNameKey(), tmplconfigService);
			String sqlSumByUserCodeBonus = Common.GetRetSumByUserColoumns(TableNameBackup, QueryFeild + strSumInvalidNotInsert, configFormulaBonus, "0", TableFeildSumOper, TableFeildTaxCanNotHaveFormula, StaffDataType.Bonus.getNameKey(), tmplconfigService);
			
			PageData pdInsetBackup = new PageData();
			pdInsetBackup.put("QueryFeild", QueryFeild);
			String strInsertFeild = QueryFeildString.tranferListValueToSelectString(map_HaveColumnsList);
			pdInsetBackup.put("FeildList", strInsertFeild);
			pdInsetBackup.put("SumInvalidNotInsert", strSumInvalidNotInsert);
			
			List<PageData> dataCalculation = staffdetailService.getDataCalculation(TableNameBackup, TableFeildTaxCanNotHaveFormula, TmplUtil.keyExtra,
					pdInsetBackup,
					listSalaryFeildUpdate, sqlRetSelect, listAddSalary, listAddBonus,
					sqlSumByUserCodeSalary, sqlSumByUserCodeBonus, TableFeildSumOper);
			retCommonBaseAndList.setList(dataCalculation);
		}
		retCommonBaseAndList.setCommonBase(commonBase);
		return retCommonBaseAndList;
	}
	
	private CommonBase UpdateDatabase(Boolean IsAdd, CommonBase commonBase, String strErrorMessage,
			String SelectedTableNo, String SelectedCustCol7, String SelectedDepartCode, String emplGroupType,
			CommonBaseAndList getCommonBaseAndList, String strHelpful) throws Exception{
		if(getCommonBaseAndList!=null && getCommonBaseAndList.getList()!=null && getCommonBaseAndList.getList().size()>0){
			Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
			Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(SelectedTableNo, tmplconfigService);

			for(PageData each : getCommonBaseAndList.getList()){
				if(IsAdd){
					each.put("SERIAL_NO", "");
				}
				Common.setModelDefault(each, map_HaveColumnsList, map_SetColumnsList, MustNotEditList);
				each.put("CanOperate", strHelpful);
				each.put("TableName", TableNameDetail);
			}
    		
    		//此处执行集合添加 
			staffdetailService.batchUpdateDatabase(getCommonBaseAndList.getList());
    		commonBase.setCode(0);
    		commonBase.setMessage(strErrorMessage);
		} else {
			commonBase = getCommonBaseAndList.getCommonBase();
		}
		return commonBase;
	}
	
	private CommonBase CalculationUpdateDatabase(Boolean IsImport, Boolean IsAdd, CommonBase commonBase, String strErrorMessage,
			String SelectedTableNo, String SelectedCustCol7, String SelectedDepartCode, String emplGroupType,
			List<PageData> listData, String strHelpful) throws Exception{
		CommonBaseAndList getCommonBaseAndList = getCalculationData(IsImport, IsAdd, commonBase,
				SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
				listData, strHelpful);
		return UpdateDatabase(IsAdd, commonBase, strErrorMessage,
				SelectedTableNo, SelectedCustCol7, SelectedDepartCode, emplGroupType,
				getCommonBaseAndList, strHelpful);
	}
	
	/**下载模版
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/downExcel")
	public ModelAndView downExcel(JqPage page) throws Exception{
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
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);

		PageData transferPd = this.getPageData();
		//页面显示数据的二级单位
		transferPd.put("SelectedDepartCode", SelectedDepartCode);
		//账套
		transferPd.put("SelectedCustCol7", SelectedCustCol7);
		//员工组
		transferPd.put("emplGroupType", emplGroupType);
		
		//页面显示数据的二级单位
		List<PageData> varOList = staffdetailService.exportModel(transferPd);
		return export(varOList, "StaffDetail", map_SetColumnsList, DicList); //工资明细
	}
	
	/**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出StaffDetail到excel");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
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
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
		Map<String, Object> DicList = Common.GetDicList(SelectedTableNo, SelectedDepartCode, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
		
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
		
		page.setPd(getPd);
		List<PageData> varOList = staffdetailService.exportList(page);
		return export(varOList, "", map_SetColumnsList, DicList);
	}
	
	@SuppressWarnings("unchecked")
	private ModelAndView export(List<PageData> varOList, String ExcelName, 
			Map<String, TmplConfigDetail> map_SetColumnsList, Map<String, Object> DicList){
		ModelAndView mv = new ModelAndView();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		dataMap.put("filename", ExcelName);
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
	
	private String CheckMustSelectedAndSame(String emplGroupType, 
			String CUST_COL7, String ShowDataCustCol7, 
			String DEPT_CODE, String ShowDataDepartCode, String DepartTreeSource,
			String BILL_CODE, String ShowDataBillCode)//
			throws Exception{
		String strRut = "";
		if(!(CUST_COL7 != null && !CUST_COL7.trim().equals(""))){
			strRut += "查询条件中的账套必须选择！";
		} else {
		    if(!CUST_COL7.equals(ShowDataCustCol7)){
				strRut += "查询条件中所选账套与页面显示数据账套不一致，请单击查询再进行操作！";
		    }
		}
		if(!(DEPT_CODE != null && !DEPT_CODE.trim().equals(""))){
			strRut += "查询条件中的责任中心不能为空！";
		} else {
		    if(!String.valueOf(0).equals(DepartTreeSource) && !DEPT_CODE.equals(ShowDataDepartCode)){
				strRut += "查询条件中所选责任中心与页面显示数据责任中心不一致，请单击查询再进行操作！";
		    }
		}
		if(!(BILL_CODE != null && !BILL_CODE.trim().equals(""))){
			strRut += "查询条件中的单号必须选择！";
		} else {
		    if(!BILL_CODE.equals(ShowDataBillCode)){
				strRut += "查询条件中所选单号与页面显示数据单号不一致，请单击查询再进行操作！";
		    }
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			strRut += Message.StaffSelectedTabOppositeGroupTypeIsNull;
		}
		return strRut;
	}

	private String CheckState(String SelectedBillCode,
			String SelectedCustCol7, String SelectedDepartCode, String emplGroupType, 
			String TypeCodeTransfer, 
			List<PageData> pdList, String strFeild, String strFeildExtra) throws Exception{
		String strRut = "";
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			String QueryFeild = " and BILL_CODE in ('" + SelectedBillCode + "') ";
			QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
			QueryFeild += " and BILL_CODE not in (SELECT bill_code FROM tb_sys_sealed_info WHERE state = '1') ";
			
			PageData transferPd = new PageData();
			transferPd.put("SystemDateTime", SystemDateTime);
			transferPd.put("CanOperate", QueryFeild);
			List<String> getCodeList = staffsummyService.getBillCodeList(transferPd);
			
			if(!(getCodeList != null && getCodeList.size()>0)){
				strRut = Message.OperDataSumAlreadyChange;
			}
		} else {
	        if(pdList!=null && pdList.size()>0){
				List<Integer> listStringSerialNo = QueryFeildString.getListIntegerFromListPageData(pdList, strFeild, strFeildExtra);
				String strSqlInSerialNo = QueryFeildString.tranferListIntegerToGroupbyString(listStringSerialNo);
	    		PageData transferPd = new PageData();
	    		PageData getQueryFeildPd = new PageData();
	    		getQueryFeildPd.put("USER_GROP", emplGroupType);
	    		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
	    		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
	    		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
	    		if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
	    			QueryFeild += " and 1 != 1 ";
	    		}
	    		if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
	    			QueryFeild += " and 1 != 1 ";
	    		}
	    		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
	    			QueryFeild += " and 1 != 1 ";
	    		}
	    		QueryFeild += " and BILL_CODE like ' %' ";
	    		QueryFeild += " and SERIAL_NO in (" + strSqlInSerialNo + ") ";
	    		transferPd.put("QueryFeild", QueryFeild);
	    		
	    		//页面显示数据的年月
	    		transferPd.put("SystemDateTime", SystemDateTime);
	    		transferPd.put("SelectFeildName", strFeild);
	    		List<PageData> getSerialNo = staffdetailService.getSerialNoBySerialNo(transferPd);
	    		if(!(listStringSerialNo!=null && getSerialNo!=null && listStringSerialNo.size() == getSerialNo.size())){
	    			strRut = Message.OperDataAlreadyChange;
	    		} else {
	    			for(PageData each : getSerialNo){
	    				if(!listStringSerialNo.contains((Integer)each.get(strFeild))){
	    					strRut = Message.OperDataAlreadyChange;
	    				}
	    			}
	    		}
	        }
		}
		return strRut;
	}

	private String getWhileValue(String value){
        String which = DefaultWhile;
		if(value != null && !value.trim().equals("")){
			which = value;
		}
		return which;
	}

	private String getWhileValueToTypeCodeTramsfer(String which) throws Exception{
		String strReturn = "";
		if(which.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())){
			//合同化
			strReturn = TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey();
		}
		if(which.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())){
			//市场化
			strReturn = TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey();
		}
		if(which.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())){
			//系统内劳务
			strReturn = TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey();
		}
		if(which.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())){
			//运行人员
			strReturn = TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey();
		}
		if(which.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())){
			//劳务派遣工资
			strReturn = TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey();
		}
		return strReturn;
	}
	
	 /**导入提示
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/showErrorTaxMessage")
	public ModelAndView showErrorTaxMessage() throws Exception{
		PageData getPd = this.getPageData();
		String ErrorTaxMessage = getPd.getString("ErrorTaxMessage");
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/ErrorTax");
		mv.addObject("commonMessage", ErrorTaxMessage);
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
