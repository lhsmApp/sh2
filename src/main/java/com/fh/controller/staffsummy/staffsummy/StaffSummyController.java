package com.fh.controller.staffsummy.staffsummy;

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
import com.fh.controller.common.CheckSystemDateTime;
import com.fh.controller.common.Common;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.ItemAlloc;
import com.fh.controller.common.Message;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SelectBillCodeOptions;
import com.fh.controller.common.TmplUtil;
import com.fh.controller.common.Corresponding;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.TmplTypeInfo;
import com.fh.entity.system.User;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.Jurisdiction;
import com.fh.util.enums.BillNumType;
import com.fh.util.enums.BillState;
import com.fh.util.enums.TmplType;

import net.sf.json.JSONArray;

import com.fh.service.detailimportcommon.detailimportcommon.impl.DetailImportCommonService;
import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.glItemUser.glItemUser.GlItemUserManager;
import com.fh.service.staffsummy.staffsummy.StaffSummyManager;
import com.fh.service.sysBillnum.sysbillnum.SysBillnumManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysDeptLtdTime.sysDeptLtdTime.impl.SysDeptLtdTimeService;
import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明：工资汇总
 * 创建人：zhangxiaoliu
 * 创建时间：2017-07-07
 */
@Controller
@RequestMapping(value="/staffsummy")
public class StaffSummyController extends BaseController {
	
	String menuUrl = "staffsummy/list.do"; //菜单地址(权限用)
	@Resource(name="staffsummyService")
	private StaffSummyManager staffsummyService;
	@Resource(name="detailimportcommonService")
	private DetailImportCommonService detailimportcommonService;
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
	@Resource(name="sysDeptLtdTimeService")
	private SysDeptLtdTimeService sysDeptLtdTimeService;
	@Resource(name="glItemUserService")
	private GlItemUserManager glItemUserService;

	//表名
	String TableNameBase = "tb_staff_summy_bill";
	String TableNameFirstDetail = "tb_staff_summy";
	String TableNameSecondDetail = "tb_staff_detail";
	String TableNameFirstItem = "TB_ITEM_staff_detail";

	//默认的which值
	String DefaultWhile = TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey();
	//临时数据
	String SelectBillCodeFirstShow = "临时数据";
	String SelectBillCodeLastShow = "全部单据";
	
	//页面显示数据的年月
	//String SystemDateTime = "";

	// 查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
	private List<String> keyListBase = Arrays.asList("BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
	//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
	//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
    //List<String> SumField = new ArrayList<String>();
    //String SumFieldToString = "";//QueryFeildString.tranferListStringToGroupbyString(SumField);
	//界面查询字段   员工组、账套、组织机构（特殊处理）、所属二级单位、组织单元文本
    List<String> QueryFeildList = Arrays.asList("USER_GROP", "CUST_COL7", "DEPT_CODE");
    
	//修改导入明细获取字段
	List<String> DetailSerialNoFeild = Arrays.asList("SERIAL_NO");
	//
	List<String> DetailUserCodeFeild = Arrays.asList("USER_CODE");
    //设置必定不用编辑的列            SERIAL_NO 设置字段类型是数字，但不管隐藏 或显示都必须保存的, 还有汇总要排除
    List<String> FirstItemMustNotEditList = Arrays.asList("SERIAL_NO", "BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
    //设置必定不用项目分摊的数值列            SERIAL_NO 设置字段类型是数字，但不用项目分摊
    List<String> MustNotItemAllocList = Arrays.asList("SERIAL_NO");
	//另加的列、配置模板之外的列 
    //目前只能这么设置，改设置改的地方多
	String AdditionalReportColumn = "";//ReportState
    //设置分组时不求和字段            SERIAL_NO 设置字段类型是数字，但不用求和
    List<String> jqGridGroupNotSumFeild = Arrays.asList("SERIAL_NO");
    
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表StaffSummy");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		//当前期间,取自tb_system_config的SystemDateTime字段
		String SystemDateTime = sysConfigManager.currentSection(getPd);
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("staffsummy/staffsummy/staffsummy_list");
		//while
		getPd.put("which", SelectedTableNo);
		//单号下拉列表
		getPd.put("SelectNoBillCodeShow", SelectBillCodeFirstShow);
		getPd.put("SelectAllBillCodeShow", SelectBillCodeLastShow);
		getPd.put("InitBillCodeOptions", SelectBillCodeOptions.getSelectBillCodeOptions(null, SelectBillCodeFirstShow, SelectBillCodeLastShow));
        mv.addObject("SystemDateTime", SystemDateTime.trim());
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
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		String strTypeCodeTramsfer = Corresponding.getTypeCodeTransferFromTmplType(SelectedTableNo);
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		
		PageData getQueryFeildPd = new PageData();
		//工资分的类型, 只有工资返回值
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
		QueryFeild += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		QueryFeild += QueryFeildString.getBillCodeNotInInvalidSysUnlockInfo();
		//工资无账套无数据
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		
		PageData transferPd = new PageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("CanOperate", QueryFeild);
		List<String> getCodeList = staffsummyService.getBillCodeList(transferPd);
		// 下拉列表 value和显示一致
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
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		String TypeCodeSummyBill = implTypeCode.getTypeCodeSummyBill();
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//设置结构单位，默认登录人单位，界面只选一个单位，设置成界面所选
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		String jqGridColModel = "";
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService, keyListBase, null, AdditionalReportColumn, null, jqGridGroupNotSumFeild);
		if(SelectedBillCode.equals(SelectBillCodeFirstShow)){
			jqGridColModel = tmpl.generateStructureNoEdit(TypeCodeDetail, strShowCalModelDepaet, SelectedCustCol7);
		} else {
			jqGridColModel = tmpl.generateStructureNoEdit(TypeCodeSummyBill, strShowCalModelDepaet, SelectedCustCol7);
		}

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
		logBefore(logger, Jurisdiction.getUsername()+"列表StaffSummy");
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		String strTypeCodeTramsfer = Corresponding.getTypeCodeTransferFromTmplType(SelectedTableNo);
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		String TypeCodeSummyBill = implTypeCode.getTypeCodeSummyBill();
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		
		PageData getQueryFeildPd = new PageData();
		//工资分的类型, 只有工资返回值
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
			QueryFeild += QueryFeildString.getBillCodeNotInInvalidSysUnlockInfo();
			QueryFeild += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		}
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		//工资无账套无数据
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		//单号
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
		List<PageData> varList = staffsummyService.JqPage(page);	//列出Betting列表
		int records = staffsummyService.countJqGridExtend(page);
		PageData userdata = null;
		StringBuilder SqlUserdata = new StringBuilder();
		String strShowCalModelDepaet = Jurisdiction.getCurrentDepartmentID();
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("") && !SelectedDepartCode.contains(",")){
			strShowCalModelDepaet = SelectedDepartCode;
		}
		if(SelectedBillCode.equals(SelectBillCodeFirstShow)){
			//底行显示的求和与平均值字段
			SqlUserdata = Common.GetSqlUserdata(TypeCodeDetail, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		} else {
			//底行显示的求和与平均值字段
			SqlUserdata = Common.GetSqlUserdata(TypeCodeSummyBill, strShowCalModelDepaet, SelectedCustCol7, tmplconfigService);
		}
		if(SqlUserdata!=null && !SqlUserdata.toString().trim().equals("")){
			//底行显示的求和与平均值字段
			getPd.put("Userdata", SqlUserdata.toString());
			userdata = staffsummyService.getFooterSummary(page);
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
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		String TypeCodeSummyDetail = implTypeCode.getTypeCodeSummyDetail();
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		List<String> SumFieldDetail = implTypeCode.getSumFieldDetail();
		String DEPT_CODE = (String) getPd.get("DataDeptCode");
		String CUST_COL7 = (String) getPd.get("DataCustCol7");
		String strBillCode = getPd.getString("DetailListBillCode");
		//项目人员分配表
		String TableNameFirstItem = Corresponding.getItemAllocDetailTableNameFromTmplType(SelectedTableNo);

		//有项目人员分配信息，取项目人员分配信息配置，返回8，没下级；没有，取汇总明细配置，返回9，有下级
		Boolean bolHaveItemList = Common.checkHaveItemList(strBillCode, TableNameFirstItem, detailimportcommonService);
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, departmentService,userService,
				SumFieldDetail, null, null, null, jqGridGroupNotSumFeild);
		if(bolHaveItemList){
			String detailColModel = tmpl.generateStructureNoEdit(TypeCodeDetail, DEPT_CODE, CUST_COL7);
			commonBase.setCode(8);
			commonBase.setMessage(detailColModel);
		} else {
			String detailColModel = tmpl.generateStructureNoEdit(TypeCodeSummyDetail, DEPT_CODE, CUST_COL7);
			commonBase.setCode(9);
			commonBase.setMessage(detailColModel);
		}
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
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		List<String> SumFieldDetail = implTypeCode.getSumFieldDetail();
		
		String strBillCode = getPd.getString("DetailListBillCode");

		//有项目人员分配信息，取项目人员分配信息；没有，取汇总明细
		Boolean bolHaveItemList = Common.checkHaveItemList(strBillCode, TableNameFirstItem, detailimportcommonService);
		PageResult<PageData> result = new PageResult<PageData>();
		if(bolHaveItemList){
			PageData pdCode = new PageData();
			String QueryFeild = " and BILL_CODE = '" + strBillCode + "' ";
		    pdCode.put("QueryFeild", QueryFeild);
		    pdCode.put("OrderbyFeild", "ITEM_CODE");
		    pdCode.put("TableName", TableNameFirstItem);
			List<PageData> varList = detailimportcommonService.getDetailList(pdCode);	//列出Betting列表
			result.setRows(varList);
		} else {
			PageData pdCode = new PageData();
			pdCode.put("BILL_CODE", strBillCode);
			String strFieldSelectKey = QueryFeildString.getFieldSelectKey(SumFieldDetail, TmplUtil.keyExtra);
			if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
				pdCode.put("FieldSelectKey", strFieldSelectKey);
			}
			List<PageData> varList = staffsummyService.findSummyDetailList(pdCode);	//列出Betting列表
			result.setRows(varList);
		}
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
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		
		String DEPT_CODE = (String) getPd.get("DataDeptCode");
		String CUST_COL7 = (String) getPd.get("DataCustCol7");
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, departmentService,userService);
		String detailColModel = tmpl.generateStructureNoEdit(TypeCodeDetail, DEPT_CODE, CUST_COL7);

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
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		List<String> SumFieldDetail = implTypeCode.getSumFieldDetail();
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        PageData pdGet = listData.get(0);

		PageData pdCode = new PageData();
		//根据汇总字段获取第二层明细
		String QueryFeild = QueryFeildString.getDetailQueryFeild(pdGet, SumFieldDetail, TmplUtil.keyExtra);
	    if(!(QueryFeild!=null && !QueryFeild.trim().equals(""))){
	    	QueryFeild += " and 1 != 1 ";
	    }
	    pdCode.put("TableName", TableNameSecondDetail);
	    pdCode.put("QueryFeild", QueryFeild);
		List<PageData> varList = detailimportcommonService.getDetailList(pdCode);	//列出Betting列表
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
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
			return commonBase;
		}
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		//String strTypeCodeTramsfer = Corresponding.getTypeCodeTransferFromTmplType(SelectedTableNo);
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        List<String> listBillCode = new ArrayList<String>();
        for(PageData each : listData){
        	listBillCode.add(each.getString("BILL_CODE" + TmplUtil.keyExtra));

			String strCustCol7 = each.getString("CUST_COL7" + TmplUtil.keyExtra);
			String strDepartCode = each.getString("DEPT_CODE" + TmplUtil.keyExtra);
			String mesSysDeptLtdTime = CheckSystemDateTime.CheckSysDeptLtdTime(SelectedTableNo, strCustCol7, strDepartCode, sysDeptLtdTimeService);
			if(mesSysDeptLtdTime!=null && !mesSysDeptLtdTime.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(mesSysDeptLtdTime);
				return commonBase;
			}
        }
		String checkState = CheckState(QueryFeildString.tranferListValueToSqlInString(listBillCode), SystemDateTime, listBillCode.size());
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
        if(null != listData && listData.size() > 0){
        	staffsummyService.cancelAll(listData);
			commonBase.setCode(0);
		}
		
		return commonBase;
	}
	
	 /**汇总
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
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
			return commonBase;
		}
		//员工组
		String SelectedTableNo = Corresponding.getWhileValue(getPd.getString("SelectedTableNo"), DefaultWhile);
		String strTypeCodeTramsfer = Corresponding.getTypeCodeTransferFromTmplType(SelectedTableNo);
		String emplGroupType = Corresponding.getUserGroupTypeFromTmplType(SelectedTableNo);
		TmplTypeInfo implTypeCode = Corresponding.getWhileValueToTypeCode(SelectedTableNo, sysConfigManager);
		String TypeCodeSummyBill = implTypeCode.getTypeCodeSummyBill();
		String TypeCodeSummyDetail = implTypeCode.getTypeCodeSummyDetail();
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		String strSumFieldBill = QueryFeildString.tranferListStringToGroupbyString(Corresponding.SumFieldBillStaff);
		List<String> SumFieldDetail = implTypeCode.getSumFieldDetail();
		String strSumFieldDetail = QueryFeildString.tranferListStringToGroupbyString(SumFieldDetail);
		
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//单号
		String SelectedBillCode = getPd.getString("SelectedBillCode");

		if(!(SelectedBillCode!=null && !SelectedBillCode.equals(SelectBillCodeLastShow))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.SelectCanSumOption);
			return commonBase;
		}

        //员工组不能为空
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.StaffSelectedTabOppositeGroupTypeIsNull);
			return commonBase;
		}

		//Map<String, TmplConfigDetail> map_SetColumnsListBill = Common.GetSetColumnsList(TypeCodeSummyBill, UserDepartCode, tmplconfigService);
		Map<String, TableColumns> map_HaveColumnsListSummyBill = Common.GetHaveColumnsList(TypeCodeSummyBill, tmplconfigService);
		//Map<String, TmplConfigDetail> map_SetColumnsListDetail = Common.GetSetColumnsList(TypeCodeSummyDetail, UserDepartCode, tmplconfigService);
		Map<String, TableColumns> map_HaveColumnsListSummyDetail = Common.GetHaveColumnsList(TypeCodeSummyDetail, tmplconfigService);
		
		//获取汇总的select的字段
		List<TableColumns> tableDetailColumns = tmplconfigService.getTableColumns(TableNameSecondDetail);
		boolean bolDeleteSummy;
		
		String QueryFeild = "";
		//临时数据：导入明细表设置单号
		List<PageData> getDetailSetBillCode = new ArrayList<PageData>();
		//updateFilter
		List<String> retSetBillCodeFeild = new ArrayList<String>();
		for(String strfeild : DetailSerialNoFeild){
			retSetBillCodeFeild.add(strfeild);
		}
		retSetBillCodeFeild = QueryFeildString.extraSumField(retSetBillCodeFeild, Corresponding.SumFieldBillStaff);
		if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){//if(listData!=null && listData.size()>0){//
			String checkState = CheckState("'" + SelectedBillCode + "'", SystemDateTime, 1);
			if(checkState!=null && !checkState.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(checkState);
				return commonBase;
			}
			bolDeleteSummy = true;
			List<String> listBillCode = new ArrayList<String>(); 
			listBillCode.add(SelectedBillCode);
			QueryFeild += " and BILL_CODE in (" + QueryFeildString.tranferListValueToSqlInString(listBillCode) + ") ";
			QueryFeild += QueryFeildString.getBillCodeNotInInvalidSysUnlockInfo();
			QueryFeild += QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
			QueryFeild += QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameBase);
		} else {
			bolDeleteSummy = false;
			PageData getQueryFeildPd = new PageData();
			//工资分的类型, 只有工资返回值
			getQueryFeildPd.put("USER_GROP", emplGroupType);
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
			if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
				QueryFeild += " and 1 != 1 ";
			}
			QueryFeild += " and BILL_CODE like ' %' ";

			PageData pdSetBillCode = new PageData();
			pdSetBillCode.put("SystemDateTime", SystemDateTime);
			pdSetBillCode.put("QueryFeild", QueryFeild);
			pdSetBillCode.put("TableName", TableNameSecondDetail);
			String SelectFeildSetBillCode = QueryFeildString.tranferListStringToKeyString(retSetBillCodeFeild, TmplUtil.keyExtra, true);
			pdSetBillCode.put("SelectFeild", SelectFeildSetBillCode);
			//临时数据：导入明细表设置单号
			getDetailSetBillCode = detailimportcommonService.getSum(pdSetBillCode);
		}
		//从导入明细表获取数据，要设置项目信息
		List<PageData> getSetItemUser = ItemAlloc.getSetItemUser(SystemDateTime, QueryFeild, TableNameSecondDetail, 
				DetailSerialNoFeild, DetailUserCodeFeild, Corresponding.SumFieldBillStaff, detailimportcommonService);
		if(!(getSetItemUser!=null && getSetItemUser.size()>0)){
			commonBase.setCode(2);
			commonBase.setMessage(Message.NotHaveOperateData);
			return commonBase;
		}
		
		/*PageData pdDetail = new PageData();
		pdDetail.put("SystemDateTime", SystemDateTime);
		pdDetail.put("QueryFeild", QueryFeild);
		pdDetail.put("TableName", TableNameSecondDetail);
		pdDetail.put("GroupbyFeild", strSumFieldDetail);
		String SelectFeildDetail = Common.getSumFeildSelect(SumFieldDetail, tableDetailColumns, TmplUtil.keyExtra);
		pdDetail.put("SelectFeild", SelectFeildDetail);
		List<PageData> getSaveDetail = detailimportcommonService.getSum(pdDetail);
		//TableName CanOperate*/
		//获取汇总基础信息
		PageData pdBill = new PageData();
		pdBill.put("SystemDateTime", SystemDateTime);
		pdBill.put("QueryFeild", QueryFeild);
		pdBill.put("TableName", TableNameSecondDetail);
		pdBill.put("GroupbyFeild", strSumFieldBill);
		String SelectFeildBill = Common.getSumFeildSelect(Corresponding.SumFieldBillStaff, tableDetailColumns, FirstItemMustNotEditList, TmplUtil.keyExtra);
		pdBill.put("SelectFeild", SelectFeildBill);
		List<PageData> getSaveBill = detailimportcommonService.getSum(pdBill);
		//TableName CanOperate

		String CanOperNotReport = QueryFeildString.getNotReportBillCode(strTypeCodeTramsfer, SystemDateTime, SelectedCustCol7, AllDeptCode + "," + SelectedDepartCode);
		String CanOperNotReportNotInSumInvalidBill = CanOperNotReport + QueryFeildString.getBillCodeNotInSumInvalidBill();
		String CanOperNotReportNotInSumInvalidDetail = CanOperNotReport + QueryFeildString.getBillCodeNotInSumInvalidDetail(TableNameBase);

		String strGetSetItemDeptCode = "";
		PageData pdBillNum=new PageData();
		if(bolDeleteSummy){//删除添加
			for(PageData bill : getSaveBill){
				String strDepartCode = bill.getString("DEPT_CODE" + TmplUtil.keyExtra);
				String strBillCode = bill.getString("BILL_CODE" + TmplUtil.keyExtra);
				if(strGetSetItemDeptCode!=null && !strGetSetItemDeptCode.trim().equals("")){
					strGetSetItemDeptCode += ",";
				}
				strGetSetItemDeptCode += strDepartCode;
				String mesSysDeptLtdTime = CheckSystemDateTime.CheckSysDeptLtdTime(SelectedTableNo, SelectedCustCol7, strDepartCode, sysDeptLtdTimeService);
				if(mesSysDeptLtdTime!=null && !mesSysDeptLtdTime.trim().equals("")){
					commonBase.setCode(2);
					commonBase.setMessage(mesSysDeptLtdTime);
					return commonBase;
				}
        		Map<String, TmplConfigDetail> map_SetColumnsListBill = Common.GetSetColumnsList(TypeCodeSummyBill, strDepartCode, SelectedCustCol7, tmplconfigService);

        		bill.put("SERIAL_NO", "");
				bill.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		bill.put("BILL_USER", user.getUSER_ID());
        		bill.put("BILL_DATE", DateUtil.getTime());
        		bill.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
        		bill.put("TableName", TableNameBase);
        		bill.put("CanOperateBill", CanOperNotReportNotInSumInvalidBill);//未传输 未作废
        		bill.put("CanOperateDetail", CanOperNotReportNotInSumInvalidDetail);//未传输 未作废
                //添加未设置字段默认值
    			Common.setModelDefault(bill, map_HaveColumnsListSummyBill, map_SetColumnsListBill, null);
    			
                //汇总明细 从工资人员项目信息表中获取
        		bill.put("IntoSumDetailTableName", TableNameFirstDetail);
        		bill.put("FromItemDetailTableName", TableNameFirstItem);
        		bill.put("FromItemDetailWhere", " and BILL_CODE = '" + strBillCode + "' ");
        		bill.put("FromItemDetailGroupBy", " group by " + strSumFieldDetail);
        		Common.setSumDetailSave(bill, map_HaveColumnsListSummyDetail, strSumFieldDetail, FirstItemMustNotEditList, SumFieldDetail);
			}
			/*for(PageData detail : getSaveDetail){
				String strDepartCode = detail.getString("DEPT_CODE" + TmplUtil.keyExtra);
				Map<String, TmplConfigDetail> map_SetColumnsListDetail = Common.GetSetColumnsList(TypeCodeSummyDetail, strDepartCode, SelectedCustCol7, tmplconfigService);

				detail.put("SERIAL_NO", "");
				detail.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		detail.put("BILL_USER", user.getUSER_ID());
        		detail.put("BILL_DATE", DateUtil.getTime());
        		detail.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
				detail.put("TableName", TableNameFirstDetail);
                //添加未设置字段默认值
    			Common.setModelDefault(detail, map_HaveColumnsListSummyDetail, map_SetColumnsListDetail, null);
			}*/
		} else {//设置单号，直接添加
			/***************获取最大单号及更新最大单号********************/
		    String billNumType = BillNumType.YGGZ;
			String monthSystemDateTime = SystemDateTime;
			pdBillNum.put("BILL_CODE", billNumType);
			pdBillNum.put("BILL_DATE", monthSystemDateTime);
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
				String strDepartCode = bill.getString("DEPT_CODE" + TmplUtil.keyExtra);
				if(strGetSetItemDeptCode!=null && !strGetSetItemDeptCode.trim().equals("")){
					strGetSetItemDeptCode += ",";
				}
				strGetSetItemDeptCode += strDepartCode;
				String mesSysDeptLtdTime = CheckSystemDateTime.CheckSysDeptLtdTime(SelectedTableNo, SelectedCustCol7, strDepartCode, sysDeptLtdTimeService);
				if(mesSysDeptLtdTime!=null && !mesSysDeptLtdTime.trim().equals("")){
					commonBase.setCode(2);
					commonBase.setMessage(mesSysDeptLtdTime);
					return commonBase;
				}
        		Map<String, TmplConfigDetail> map_SetColumnsListBill = Common.GetSetColumnsList(TypeCodeSummyBill, strDepartCode, SelectedCustCol7, tmplconfigService);
				
				billNum++;
        		bill.put("SERIAL_NO", "");
				String getBILL_CODE = BillCodeUtil.getBillCode(billNumType, monthSystemDateTime, billNum);
				bill.put("BILL_CODE", getBILL_CODE);
				bill.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		bill.put("BILL_USER", user.getUSER_ID());
        		bill.put("BILL_DATE", DateUtil.getTime());
        		bill.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
        		bill.put("TableName", TableNameBase);
                //添加未设置字段默认值
    			Common.setModelDefault(bill, map_HaveColumnsListSummyBill, map_SetColumnsListBill, null);

                //汇总明细 从工资人员项目信息表中获取
        		bill.put("IntoSumDetailTableName", TableNameFirstDetail);
        		bill.put("FromItemDetailTableName", TableNameFirstItem);
        		bill.put("FromItemDetailWhere", " and BILL_CODE = '" + getBILL_CODE + "' ");
        		bill.put("FromItemDetailGroupBy", " group by " + strSumFieldDetail);
        		Common.setSumDetailSave(bill, map_HaveColumnsListSummyDetail, strSumFieldDetail, FirstItemMustNotEditList, SumFieldDetail);
			}
			//getSaveDetail = getListTo(getSaveBill, getSaveDetail, Corresponding.SumFieldBillStaff);
			//设置单号 根据基础汇总字段
			getDetailSetBillCode = getListTo(getSaveBill, getDetailSetBillCode, Corresponding.SumFieldBillStaff);
			getSetItemUser = getListTo(getSaveBill, getSetItemUser, Corresponding.SumFieldBillStaff);
			
			//未匹配的单号和没有单号的记录
			/*for(PageData detail : getSaveDetail){
				String strDepartCode = detail.getString("DEPT_CODE" + TmplUtil.keyExtra);
				Map<String, TmplConfigDetail> map_SetColumnsListDetail = Common.GetSetColumnsList(TypeCodeSummyDetail, strDepartCode, SelectedCustCol7, tmplconfigService);

				detail.put("SERIAL_NO", "");
				detail.put("BILL_STATE", BillState.Normal.getNameKey());
        		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
        		detail.put("BILL_USER", user.getUSER_ID());
        		detail.put("BILL_DATE", DateUtil.getTime());
        		detail.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                
				detail.put("TableName", TableNameFirstDetail);
                //添加未设置字段默认值
    			Common.setModelDefault(detail, map_HaveColumnsListSummyDetail, map_SetColumnsListDetail, null);
    			
				Object getBILL_CODE = detail.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					commonBase.setCode(2);
					commonBase.setMessage(Message.SetSumCodeError);
				}
			}*/
			//临时数据：导入明细表设置单号
			for(PageData setBillode : getDetailSetBillCode){
				setBillode.put("updateFilter", " and BILL_CODE like ' %' ");
				
				Object getBILL_CODE = setBillode.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					commonBase.setCode(2);
					commonBase.setMessage(Message.SetSumCodeError);
				}
			}

			for(PageData setBillode : getSetItemUser){
				Object getBILL_CODE = setBillode.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					commonBase.setCode(2);
					commonBase.setMessage(Message.SetSumCodeError);
				}
			}/**/
			
			//单号没变化，pdBillNum为null，不更新数据库单号
		    if(getNum == billNum){
				pdBillNum = null;
			} else {
				pdBillNum.put("BILL_NUMBER", billNum);
			}
		}

		//根据导入明细（已有单号），设置工资人员项目信息，
		List<PageData> getSaveItem = new ArrayList<PageData>();
		List<PageData> getItemInfo = new ArrayList<PageData>();
        if(commonBase.getCode() == -1 && getSetItemUser!=null && getSetItemUser.size()>0){
        	PageData retPd = ItemAlloc.getSaveItem(SystemDateTime, strGetSetItemDeptCode, glItemUserService,
        			TypeCodeDetail, tmplconfigService, getSetItemUser, SelectedCustCol7, TableNameFirstItem,
        			FirstItemMustNotEditList, MustNotItemAllocList);
    		getSaveItem = (List<PageData>) retPd.get("SaveItem");
    		getItemInfo = (List<PageData>) retPd.get("ItemInfo");
        }/**/
        if(commonBase.getCode() == -1){
        	Map<String, Object> map = new HashMap<String, Object>();
            if(pdBillNum!=null && pdBillNum.size()>0){
            	map.put("UpdateBillNum", pdBillNum);
            }
            if(bolDeleteSummy){
            	map.put("DetailBillAndDetail", getSaveBill);
            }
        	map.put("SaveBill", getSaveBill);
        	//map.put("SaveDetail", getSaveDetail);
            map.put("SaveItem", getSaveItem);/**/
            map.put("SaveItemInfo", getItemInfo);
            if(!bolDeleteSummy){
            	map.put("DetailSetBillCode", getDetailSetBillCode);
            }
			staffsummyService.saveSummyModelList(map);
			commonBase.setCode(0);
        }
		return commonBase;
	}

	//设置单号 根据基础汇总字段
	private List<PageData> getListTo(List<PageData> listBill, List<PageData> listDetail, List<String> SumFieldBillAll){
	    if(listBill!=null && listBill.size()>0 && listDetail!=null && listDetail.size()>0 && SumFieldBillAll!=null){
	    	List<String> listSumFieldBill = new ArrayList<String>();
	    	for(String feild : SumFieldBillAll){
	    		if(!feild.equals("BILL_CODE")){
	    			listSumFieldBill.add(feild);
	    		}
	    	}
			for(PageData bill : listBill){
				String getbillCode = bill.getString("BILL_CODE");
				for(PageData detail : listDetail){
					Boolean bol = true;
					for(String field : listSumFieldBill){
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
	private String CheckState(String strSqlInBillCode, String SystemDateTime, int size) throws Exception{
		String strRut = "";
		
		String QueryFeild = " and BILL_CODE in (" + strSqlInBillCode + ") ";
		QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
		QueryFeild += " and BILL_CODE not in (SELECT bill_code FROM tb_sys_sealed_info WHERE state = '1') ";
		QueryFeild += QueryFeildString.getBillCodeNotInInvalidSysUnlockInfo();
		
		PageData transferPd = new PageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		transferPd.put("CanOperate", QueryFeild);
		List<String> getCodeList = staffsummyService.getBillCodeList(transferPd);
		
		int getSize = 0;
		if(getCodeList != null && getCodeList.size()>0){
			 getSize = getCodeList.size();
		}
		
		if(getSize != size){
			strRut = Message.OperDataSumAlreadyChange;
		}
		return strRut;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
