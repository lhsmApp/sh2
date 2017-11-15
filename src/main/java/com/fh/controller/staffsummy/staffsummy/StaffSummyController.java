package com.fh.controller.staffsummy.staffsummy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.fh.controller.common.Message;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SetListReportState;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.SysSealed;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.TmplTypeInfo;
import com.fh.entity.system.User;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.Jurisdiction;
import com.fh.util.date.DateFormatUtils;
import com.fh.util.date.DateUtils;
import com.fh.util.enums.BillNumType;
import com.fh.util.enums.BillState;
import com.fh.util.enums.DurState;
import com.fh.util.enums.SysConfigKeyCode;
import com.fh.util.enums.TmplType;

import net.sf.json.JSONArray;

import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.importdetail.importdetail.impl.ImportDetailService;
import com.fh.service.staffDetail.staffdetail.StaffDetailManager;
import com.fh.service.staffsummy.staffsummy.StaffSummyManager;
import com.fh.service.sysBillnum.sysbillnum.SysBillnumManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
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
	@Resource(name="staffdetailService")
	private StaffDetailManager staffdetailService;
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
	@Resource(name = "importdetailService")
	private ImportDetailService importdetailService;

	//表名
	String TableNameBase = "tb_staff_summy";
	String TableNameDetail = "tb_staff_detail";

	//默认的which值
	String DefaultWhile = TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey();
	//枚举类型 TmplType
	//String TypeCodeDetail = "";
	//String TypeCodeSummy = "";
	//String TypeCodeListen = "";
	
	//页面显示数据的年月
	String SystemDateTime = "";
	//页面显示数据的二级单位
	//String UserDepartCode = "";
	//登录人的二级单位是最末层
	//private int departSelf = 0;
	//底行显示的求和与平均值字段
	StringBuilder SqlUserdata = new StringBuilder();
	//表结构  
	Map<String, TableColumns> map_HaveColumnsList = new HashMap<String, TableColumns>();
	// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
	//Map<String, TmplConfigDetail> map_SetColumnsList = new HashMap<String, TmplConfigDetail>();
	
	//界面分组字段
	List<String> jqGridGroupColumn = Arrays.asList("DEPT_CODE");//, "ReportState"

	// 查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
	private List<String> keyListBase = Arrays.asList("BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
	//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
	//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
    //List<String> SumField = new ArrayList<String>();
   // String SumFieldToString = "";//QueryFeildString.tranferListStringToGroupbyString(SumField);
	//界面查询字段   员工组、账套、组织机构（特殊处理）、所属二级单位、组织单元文本
    List<String> QueryFeildList = Arrays.asList("USER_GROP", "CUST_COL7", "DEPT_CODE", "UNITS_CODE", "ORG_UNIT");
	//另加的列、配置模板之外的列 
    //目前只能这么设置，改设置改的地方多
	String AdditionalReportColumn = "ReportState";
    //查询的所有可操作的责任中心
    //List<String> AllDeptCode = new ArrayList<String>();
    
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表StaffSummy");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		
		//查询的所有可操作的责任中心
	    //AllDeptCode = new ArrayList<String>();
		
		PageData getPd = this.getPageData();
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		//当前登录人所在二级单位
		String UserDepartCode = Jurisdiction.getCurrentDepartmentID();
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("staffsummy/staffsummy/staffsummy_list");
		//while
		getPd.put("which", SelectedTableNo);
		mv.addObject("pd", getPd);
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
			//this.departSelf = 1;
			getPd.put("departTreeSource", DepartmentSelectTreeSource);
			//AllDeptCode.add(UserDepartCode);
		} else {
			//departSelf = 0;
			getPd.put("departTreeSource", 1);
	        /*JSONArray jsonArray = JSONArray.fromObject(DepartmentSelectTreeSource);  
			List<PageData> listDepart = (List<PageData>) JSONArray.toCollection(jsonArray, PageData.class);
			if(listDepart!=null && listDepart.size()>0){
				for(PageData pdDept : listDepart){
					AllDeptCode.add(pdDept.getString(DictsUtil.Id));
				}
			}*/
		}
		mv.addObject("zTreeNodes1", DepartmentSelectTreeSource);
		// ***********************************************************
		//二级单位oa_department:"UNITS_CODE"
		String UnitsCodeSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService, DictsUtil.DepartShowAll);
		mv.addObject("zTreeNodes2", UnitsCodeSelectTreeSource);
		//组织单元文本字典ORGUNIT:"ORG_UNIT"
		mv.addObject("ORGUNIT", DictsUtil.getDictsByParentCode(dictionariesService, "ORGUNIT"));

		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, 
				departmentService,userService, keyListBase, jqGridGroupColumn, AdditionalReportColumn);
		String jqGridColModel = tmpl.generateStructureNoEdit(SelectedTableNo, UserDepartCode);

		//分组字段是否显示在表中
		List<String> m_jqGridGroupColumnShow = tmpl.getJqGridGroupColumnShow();
		//底行显示的求和与平均值字段
		SqlUserdata = tmpl.getSqlUserdata();
		//表结构  
		map_HaveColumnsList = tmpl.getHaveColumnsList();
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		//map_SetColumnsList = tmpl.getSetColumnsList();
		
		mv.addObject("jqGridColModel", jqGridColModel);
        //分组字段  格式：groupField: ['DEPT_CODE'],
		String jqGridGroupField = QueryFeildString.tranferListValueToSqlInString(jqGridGroupColumn);
		mv.addObject("jqGridGroupField", jqGridGroupField);
        //分组字段是否显示在表中  格式：groupColumnShow: [true],
		String jqGridGroupColumnShow = QueryFeildString.tranferListStringToGroupbyString(m_jqGridGroupColumnShow);
		mv.addObject("jqGridGroupColumnShow", jqGridGroupColumnShow);
		return mv;
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
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		TmplTypeInfo implTypeCode = getWhileValueToTypeCode(SelectedTableNo);
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		String TypeCodeSummy = implTypeCode.getTypeCodeSummy();
		String TypeCodeListen = implTypeCode.getTypeCodeListen();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		List<String> AllDeptCode = Common.getAllDeptCode(departmentService, Jurisdiction.getCurrentDepartmentID());
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//组织单元文本字典ORGUNIT:"ORG_UNIT"
		String SelectedOrgUnit = getPd.getString("SelectedOrgUnit");
		//二级单位oa_department:"UNITS_CODE"
		String SelectedUnitsCode = getPd.getString("SelectedUnitsCode");
		
		PageData getQueryFeildPd = new PageData();
		//工资分的类型, 只有工资返回值
		getQueryFeildPd.put("USER_GROP", emplGroupType);
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
		getQueryFeildPd.put("ORG_UNIT", SelectedOrgUnit);
		getQueryFeildPd.put("UNITS_CODE", SelectedUnitsCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
		QueryFeild += " and DEPT_CODE in (" + QueryFeildString.tranferListValueToSqlInString(AllDeptCode) + ") ";
		//工资无账套无数据
		if(!(SelectedCustCol7!=null && !SelectedCustCol7.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}

		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		//类型
		getPd.put("TypeCodeDetail", TypeCodeDetail);
		getPd.put("TypeCodeSummy", TypeCodeSummy);
		getPd.put("TypeCodeListen", TypeCodeListen);
		getPd.put("SelectedCustCol7", SelectedCustCol7);
		getPd.put("DurState", DurState.Sealed.getNameKey());
		
		String strFieldSelectKey = QueryFeildString.getFieldSelectKey(keyListBase, TmplUtil.keyExtra);
		if(null != strFieldSelectKey && !"".equals(strFieldSelectKey.trim())){
			getPd.put("FieldSelectKey", strFieldSelectKey);
		}
		
		StringBuilder sbSpellingTableName = SetListReportState.GetHaveReportStateSql(AdditionalReportColumn, TableNameBase, TypeCodeSummy);
		getPd.put("SpellingTableName", sbSpellingTableName);
		
		page.setPd(getPd);
		List<PageData> varList = staffsummyService.JqPage(page);	//列出Betting列表
		int records = staffsummyService.countJqGridExtend(page);
		PageData userdata = null;
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
	@RequestMapping(value="/getDetailColModel")
	public @ResponseBody CommonBase getDetailColModel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getDetailColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData getPd = this.getPageData();
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		TmplTypeInfo implTypeCode = getWhileValueToTypeCode(SelectedTableNo);
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		
		String DEPT_CODE = (String) getPd.get("DataDeptCode");
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, departmentService,userService);
		String detailColModel = tmpl.generateStructureNoEdit(TypeCodeDetail, DEPT_CODE);

		commonBase.setCode(0);
		commonBase.setMessage(detailColModel);
		
		return commonBase;
	}

	/**明细数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getDetailList")
	public @ResponseBody PageResult<PageData> getDetailList() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"getDetailList");
		PageData getPd = this.getPageData();
		String strBillCode = getPd.getString("DetailListBillCode");

		PageData pdCode = new PageData();
		pdCode.put("BILL_CODE", strBillCode);
		List<PageData> varList = staffdetailService.getDetailList(pdCode);	//列出Betting列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		
		return result;
	}
	
	 /**上报
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/report")
	public @ResponseBody CommonBase report() throws Exception{
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "report")){return null;} //校验权限	
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String userId = user.getUSER_ID();
        String time = DateUtils.getCurrentTime(DateFormatUtils.DATE_FORMAT2);

		PageData getPd = this.getPageData();
		//员工组 必须执行，用来设置汇总和传输上报类型
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		TmplTypeInfo implTypeCode = getWhileValueToTypeCode(SelectedTableNo);
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		String TypeCodeSummy = implTypeCode.getTypeCodeSummy();
		String TypeCodeListen = implTypeCode.getTypeCodeListen();
		if(!(TypeCodeSummy!=null && !TypeCodeSummy.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.SelectedTabOppositeReportTypeIsNull);
			return commonBase;
		}
		
		Object DATA_ROWS_REPORT = getPd.get("DataRowsReport");
		String json = DATA_ROWS_REPORT.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        
		List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);// 过时方法
		if (null != listTransferData && listTransferData.size() > 0) {
			List<SysSealed> listSysSealed = new ArrayList<SysSealed>();
			/* // 将获取的字典数据进行分组
			Map<String, List<PageData>> mapListTransferData = GroupUtils.group(listTransferData,
					new GroupBy<String>() {
						@Override
						public String groupby(Object obj) {
							PageData d = (PageData) obj;
							return d.getString("DEPT_CODE__"); // 分组依据为DEPT_CODE
						}
					});
			for (Map.Entry<String, List<PageData>> entry : mapListTransferData.entrySet()) { */
			for (PageData data : listTransferData) { 
				String BUSI_DATE__ = data.getString("BUSI_DATE__");
				String DEPT_CODE__ = data.getString("DEPT_CODE__");
				String CUST_COL7__ = data.getString("CUST_COL7__");
				
				if(BUSI_DATE__ != null && !BUSI_DATE__.trim().equals("")){
				//		&& DEPT_CODE__ != null && !DEPT_CODE__.trim().equals("")
				//		&& CUST_COL7__ != null && !CUST_COL7__.trim().equals("")
					SysSealed item = new SysSealed();
					item.setBILL_CODE(" ");
					item.setRPT_DEPT(DEPT_CODE__);
					item.setRPT_DUR(BUSI_DATE__);
					item.setRPT_USER(userId);
					item.setRPT_DATE(time);// YYYY-MM-DD HH:MM:SS
					item.setBILL_TYPE(TypeCodeSummy);
					item.setBILL_OFF(CUST_COL7__);
					item.setSTATE(DurState.Sealed.getNameKey());// 枚举  1封存,0解封
					listSysSealed.add(item);
					
	        		//判断汇总信息为未上报
	    			String checkStateLast = CheckStateLast(item);
	    			if(checkStateLast!=null && !checkStateLast.trim().equals("")){
	    				commonBase.setCode(2);
	    				commonBase.setMessage(checkStateLast);
	    				return commonBase;
	    			}
	    			//判断明细信息为已上报
	    			SysSealed itemBefore = new SysSealed();
	    			itemBefore.setBILL_OFF(CUST_COL7__);
	    			itemBefore.setRPT_DEPT(DEPT_CODE__);
	    			itemBefore.setRPT_DUR(BUSI_DATE__);
	    			itemBefore.setBILL_TYPE(TypeCodeDetail.toString());// 枚举
	    			String checkStateBefore = CheckStateBefore(itemBefore);
	    			if(checkStateBefore!=null && !checkStateBefore.trim().equals("")){
	    				commonBase.setCode(2);
	    				commonBase.setMessage(checkStateBefore);
	    				return commonBase;
	    			}
	    			//判断凭证信息为没有记录
	    			String checkStatePz = FilterBillCode.CheckCanSummyOperate(syssealedinfoService, 
	    					DEPT_CODE__, BUSI_DATE__, CUST_COL7__,
	    					TypeCodeListen);
	    			if(checkStatePz!=null && !checkStatePz.trim().equals("")){
	    				commonBase.setCode(2);
	    				commonBase.setMessage(checkStatePz);
	    				return commonBase;
	    			}
				}
			}
			syssealedinfoService.saveReport(listSysSealed);
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
		//员工组
		String SelectedTableNo = getWhileValue(getPd.getString("SelectedTableNo"));
		String emplGroupType = DictsUtil.getEmplGroupType(SelectedTableNo);
		TmplTypeInfo implTypeCode = getWhileValueToTypeCode(SelectedTableNo);
		String TypeCodeDetail = implTypeCode.getTypeCodeDetail();
		String TypeCodeSummy = implTypeCode.getTypeCodeSummy();
		String TypeCodeListen = implTypeCode.getTypeCodeListen();
		List<String> SumField = implTypeCode.getSumField();
		String SumFieldToString = implTypeCode.getSumFieldToString();
		
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");

		if(!(emplGroupType!=null && !emplGroupType.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage(Message.StaffSelectedTabOppositeGroupTypeIsNull);
			return commonBase;
		}
		
		//传输的列
		String json = getPd.getString("DataRowSummy");
        
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<SysSealed> delReportList = new ArrayList<SysSealed>();
		PageData pdBillNum=new PageData();

		/***************获取最大单号及更新最大单号********************/
		String billNumType = BillNumType.YGGZ;
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
		
		/***************去掉重复的单位编码********************/
        List<PageData> listSummy = new ArrayList<PageData>();
		if(json!=null && !json.trim().equals("")){
	        JSONArray array = JSONArray.fromObject(json);  
			List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);
			for(PageData transferData : listTransferData){
				String DEPT_CODE = transferData.getString("DEPT_CODE__");
				String CUST_COL7 = transferData.getString("CUST_COL7__");
				Boolean isAdd = true;
	        	for(PageData summy : listSummy){
	        		if(!(DEPT_CODE != null && !DEPT_CODE.trim().equals("")
	        				&& CUST_COL7 != null && !CUST_COL7.trim().equals(""))){
	        			isAdd = false;
	        		} else {
		        		if(DEPT_CODE.equals(summy.getString("DEPT_CODE"))
		        				&& CUST_COL7.equals(summy.getString("CUST_COL7"))){
		        			isAdd = false;
		        		}
	        		}
	        	}
				if(isAdd){
		        	PageData add = new PageData();
		        	add.put("DEPT_CODE", DEPT_CODE);
		        	add.put("CUST_COL7", CUST_COL7);
		        	listSummy.add(add);
				}
			}
		} else {
			if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("")
					&& SelectedCustCol7!=null && !SelectedCustCol7.trim().equals("")){
				//去掉重复的单位编码
				String[] listDATA_DEPART = SelectedDepartCode.toString().split(",");  
				List<String> list = Arrays.asList(listDATA_DEPART);
		        Set<String> set = new HashSet<String>(list);
		        String [] listDepart=(String[])set.toArray(new String[0]);
		        for(String depart : listDepart){
		        	PageData add = new PageData();
		        	add.put("DEPT_CODE", depart);
		        	add.put("CUST_COL7", SelectedCustCol7);
		        	listSummy.add(add);
		        }
			}
		}
		/***************************************************/

        if(!(listSummy!=null && listSummy.size()>0)){
			commonBase.setCode(2);
			commonBase.setMessage(Message.NotTransferOperateData);
			return commonBase;
        }
        	for(PageData eachSummy : listSummy){
        		String strDepartCode = eachSummy.getString("DEPT_CODE");
        		String strCustCol7 = eachSummy.getString("CUST_COL7");

        		//判断汇总信息为未上报
    			SysSealed itemLast = new SysSealed();
    			itemLast.setBILL_OFF(strCustCol7);
    			itemLast.setRPT_DEPT(strDepartCode);
    			itemLast.setRPT_DUR(SystemDateTime);
    			itemLast.setBILL_TYPE(TypeCodeSummy.toString());// 枚举
    			String checkStateLast = CheckStateLast(itemLast);
    			if(checkStateLast!=null && !checkStateLast.trim().equals("")){
    				commonBase.setCode(2);
    				commonBase.setMessage(checkStateLast);
    				return commonBase;
    			}
    			//判断明细信息为已上报
    			SysSealed itemBefore = new SysSealed();
    			itemBefore.setBILL_OFF(strCustCol7);
    			itemBefore.setRPT_DEPT(strDepartCode);
    			itemBefore.setRPT_DUR(SystemDateTime);
    			itemBefore.setBILL_TYPE(TypeCodeDetail.toString());// 枚举
    			String checkStateBefore = CheckStateBefore(itemBefore);
    			if(checkStateBefore!=null && !checkStateBefore.trim().equals("")){
    				commonBase.setCode(2);
    				commonBase.setMessage(checkStateBefore);
    				return commonBase;
    			}
    			//判断凭证信息为没有记录
    			String checkStatePz = FilterBillCode.CheckCanSummyOperate(syssealedinfoService, 
    					strDepartCode, SystemDateTime, strCustCol7,
    					TypeCodeListen);
    			if(checkStatePz!=null && !checkStatePz.trim().equals("")){
    				commonBase.setCode(2);
    				commonBase.setMessage(checkStatePz);
    				break;
    			}

    			TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplconfigdictService, dictionariesService, departmentService,userService);
    			tmpl.generateStructureNoEdit(TypeCodeDetail, strDepartCode);
    			Map<String, TableColumns> getHaveColumnsList = tmpl.getHaveColumnsList();
    			Map<String, TmplConfigDetail> getSetColumnsList = tmpl.getSetColumnsList();
    			
    			//FilterBillCode.copyInsert(syssealedinfoService, importdetailService, 
    			//		strDepartCode, SystemDateTime, strCustCol7, 
    			//		TypeCodeListen, TypeCodeSummy, TypeCodeDetail,
    			//		TableNameBase, TableNameDetail, 
    			//		emplGroupType, 
    			//		getHaveColumnsList, getSetColumnsList);
    			//String strHelpfulDetail = FilterBillCode.getCanOperateCondition(syssealedinfoService, 
    			//		strDepartCode, SystemDateTime, strCustCol7,
    			//		TypeCodeListen, TypeCodeSummy, TableNameBase);
    			String strHelpfulDetail = FilterBillCode.getBillCodeNotInSumInvalid(TableNameBase);
    			if(!(strHelpfulDetail != null && !strHelpfulDetail.trim().equals(""))){
    				commonBase.setCode(2);
    				commonBase.setMessage(Message.GetHelpfulDetailFalue);
    				return commonBase;
    			}
    			SysSealed delReportEach = new SysSealed();
    			delReportEach.setBILL_OFF(strCustCol7);
    			delReportEach.setRPT_DEPT(strDepartCode);
    			delReportEach.setRPT_DUR(SystemDateTime);
    			delReportEach.setBILL_TYPE(TypeCodeListen);
    			delReportList.add(delReportEach);
    			
                //获取单位已有的汇总信息
    			//Boolean bolDelSum = false;
    			List<PageData> getHaveDate = new ArrayList<PageData>();
    			PageData pdReportListen = new PageData();
    			pdReportListen.put("BILL_OFF", strCustCol7);
    			pdReportListen.put("RPT_DEPT", strDepartCode);
    			pdReportListen.put("RPT_DUR", SystemDateTime);
    			pdReportListen.put("BILL_TYPE", TypeCodeListen);// 枚举
    			String stateListen = syssealedinfoService.getState(pdReportListen);
    			if(stateListen != null && !stateListen.equals("")){
    				//接口已上报过（接口有记录），单号要全部生成
    				getHaveDate = new ArrayList<PageData>();
    			//	bolDelSum = false;
    			} else {
    				//接口未上报过（接口没记录），单号要获取原有的
        			Map<String, String> mapHave = new HashMap<String, String>();
        			mapHave.put("BUSI_DATE", SystemDateTime);
        			mapHave.put("CUST_COL7", strCustCol7);
        			mapHave.put("DEPT_CODE", strDepartCode);
        			mapHave.put("USER_GROP", emplGroupType);
        			mapHave.put("BILL_STATE", BillState.Normal.getNameKey());
        			getHaveDate = staffsummyService.getHave(mapHave);
        		//	bolDelSum = true;
    			}
    			
    			//获取单位重新汇总信息
    			List<TableColumns> tableDetailColumns = tmplconfigService.getTableColumns(TableNameDetail);
    			Map<String, String> mapSave = new HashMap<String, String>();
    			mapSave.put("BUSI_DATE", SystemDateTime);
    			mapSave.put("DEPT_CODE", strDepartCode);
    			mapSave.put("CUST_COL7", strCustCol7);
    			mapSave.put("USER_GROP", emplGroupType);
    			mapSave.put("GroupbyFeild", SumFieldToString);
    			//获取汇总的select的字段
    			String SelectFeild = Common.getSumFeildSelect(SumField, tableDetailColumns, TmplUtil.keyExtra);
    			mapSave.put("SelectFeild", SelectFeild);
    			mapSave.put("CanOperate", strHelpfulDetail);
    			List<PageData> getSaveDate = staffdetailService.getSum(mapSave);
    			
    			List<PageData> listAdd = getListTo(getHaveDate, getSaveDate, SumField);

    			for(PageData addTo : listAdd){
    				Object getBILL_CODE = addTo.get("BILL_CODE");
    				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
    					billNum++;
    					getBILL_CODE = BillCodeUtil.getBillCode(billNumType, month, billNum);
    				}
                    addTo.put("BILL_CODE", getBILL_CODE);
                    addTo.put("BUSI_DATE", SystemDateTime);
                    addTo.put("DEPT_CODE", strDepartCode);
                    addTo.put("CUST_COL7", strCustCol7);
                    addTo.put("USER_GROP", emplGroupType);
                    addTo.put("BILL_STATE", BillState.Normal.getNameKey());
            		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
                    addTo.put("BILL_USER", user.getUSER_ID());
                    addTo.put("BILL_DATE", DateUtil.getTime());
                    addTo.put("ESTB_DEPT", Jurisdiction.getCurrentDepartmentID());
                    
                    //更新明细单号的条件
                    StringBuilder updateFilter = new StringBuilder();
                    for(String field : SumField){
                    	updateFilter.append(" and " + field + " = '" + addTo.getString(field) + "' ");
                    }
                    updateFilter.append(FilterBillCode.getBillCodeNotInSumInvalid(TableNameBase));
	    			addTo.put("updateFilter", updateFilter);
                    //添加未设置字段默认值
	    			Common.setModelDefault(addTo, map_HaveColumnsList, getSetColumnsList);
    			}
        		Map<String, Object> mapAdd = new HashMap<String, Object>();
    			//mapAdd.put("DelSum", bolDelSum);
    			mapAdd.put("AddList", listAdd);
    			listMap.add(mapAdd);
        	}
		//单号没变化，pdBillNum为null，不更新数据库单号
		if(getNum == billNum){
			pdBillNum = null;
		} else {
			pdBillNum.put("BILL_NUMBER", billNum);
		}
        if(commonBase.getCode() == -1){
			staffsummyService.saveSummyModelList(listMap, pdBillNum);//listMap, delReportList
			commonBase.setCode(0);
        }
		return commonBase;
	}
	
	private List<PageData> getListTo(List<PageData> listHave, List<PageData> listSave, List<String> SumField){
		List<String> listNotSetCode = new ArrayList<String>();
	    if(listSave!=null && listSave.size()>0){
			for(PageData eachSave : listSave){
			    //先清除汇总生成数据的单号
				eachSave.remove("BILL_CODE");
				//在根据汇总字段匹配设置单号
			    if(listHave!=null && listHave.size()>0){
					for(PageData eachHave : listHave){
						Object getBILL_CODE = eachHave.get("BILL_CODE");
						if(getBILL_CODE!=null && !getBILL_CODE.toString().equals("")){
							Boolean bol = true;
							for(String field : SumField){
								String strHave = (String) eachHave.get(field);
								if(strHave == null) strHave = "";
								String strSave = (String) eachSave.get(field);
								if(strSave == null) strSave = "";
								if(!strHave.equals(strSave)){
									bol = false;
								}
							}
							if(bol){
								eachSave.put("BILL_CODE", getBILL_CODE);
							} else {
							    listNotSetCode.add(getBILL_CODE.toString());
							}
						}
					}
			    }
			}
			//未匹配的单号和没有单号的记录设置
			for(PageData eachSave : listSave){
				if(!(listNotSetCode!=null && listNotSetCode.size()>0)){
					break;
				}
				Object getBILL_CODE = eachSave.get("BILL_CODE");
				if(!(getBILL_CODE != null && !getBILL_CODE.toString().trim().equals(""))){
					eachSave.put("BILL_CODE", listNotSetCode.get(0));
					listNotSetCode.remove(0);
				}
			}
	    }
		return listSave;
	}
	
	private String CheckStateLast(SysSealed item) throws Exception{
		String strRut = "单位：" + item.getRPT_DEPT() + "汇总已上报！";
		String State = syssealedinfoService.getStateFromModel(item);
		if(!DurState.Sealed.getNameKey().equals(State)){// 枚举  1封存,0解封
			strRut = "";
		}
		return strRut;
	}
	private String CheckStateBefore(SysSealed item) throws Exception{
		String strRut = "单位：" + item.getRPT_DEPT() + "明细未上报！";
		String State = syssealedinfoService.getStateFromModel(item);
		if(DurState.Sealed.getNameKey().equals(State)){// 枚举  1封存,0解封
			strRut = "";
		}
		return strRut;
	}

	private String getWhileValue(String value) throws Exception{
        String which = DefaultWhile;
		if(value != null && !value.trim().equals("")){
			which = value;
		}
		return which;
	}

	private TmplTypeInfo getWhileValueToTypeCode(String which) throws Exception{
		TmplTypeInfo retItem = new TmplTypeInfo();
		//枚举类型 TmplType
		//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
		//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本retItem
		if(which.equals(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey())){
			//合同化
			retItem.setTypeCodeDetail(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey());
			retItem.setTypeCodeSummy(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey());
			retItem.setTypeCodeListen(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey());
			//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
			//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
		    //SumField = Arrays.asList("BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP", "UNITS_CODE", "ORG_UNIT", "USER_CATG", "SAL_RANGE");
		    //SumFieldToString = QueryFeildString.tranferListStringToGroupbyString(SumField);
			
			PageData pdSysConfig = new PageData();
			pdSysConfig.put("KEY_CODE", SysConfigKeyCode.ContractGRPCond);
			retItem.setSumFieldToString(sysConfigManager.getSysConfigByKey(pdSysConfig));
			retItem.setSumField(QueryFeildString.tranferStringToList(retItem.getSumFieldToString()));
		}
		if(which.equals(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey())){
			//市场化
			retItem.setTypeCodeDetail(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey());
			retItem.setTypeCodeSummy(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey());
			retItem.setTypeCodeListen(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey());
			//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
			//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
		    //SumField = Arrays.asList("BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP", "UNITS_CODE", "ORG_UNIT", "USER_CATG", "SAL_RANGE");
		    //SumFieldToString = QueryFeildString.tranferListStringToGroupbyString(SumField);
			
			PageData pdSysConfig = new PageData();
			pdSysConfig.put("KEY_CODE", SysConfigKeyCode.MarketGRPCond);
			retItem.setSumFieldToString(sysConfigManager.getSysConfigByKey(pdSysConfig));
			retItem.setSumField(QueryFeildString.tranferStringToList(retItem.getSumFieldToString()));
		}
		if(which.equals(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey())){
			//系统内劳务
			retItem.setTypeCodeDetail(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey());
			retItem.setTypeCodeSummy(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey());
			retItem.setTypeCodeListen(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey());
			//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
			//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
		    //SumField = Arrays.asList("BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP", "UNITS_CODE", "ORG_UNIT", "USER_CATG", "SAL_RANGE");
		    //SumFieldToString = QueryFeildString.tranferListStringToGroupbyString(SumField);
			
			PageData pdSysConfig = new PageData();
			pdSysConfig.put("KEY_CODE", SysConfigKeyCode.SysLaborGRPCond);
			retItem.setSumFieldToString(sysConfigManager.getSysConfigByKey(pdSysConfig));
			retItem.setSumField(QueryFeildString.tranferStringToList(retItem.getSumFieldToString()));
		}
		if(which.equals(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey())){
			//运行人员
			retItem.setTypeCodeDetail(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey());
			retItem.setTypeCodeSummy(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey());
			retItem.setTypeCodeListen(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey());
			//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
			//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
		    //SumField = Arrays.asList("BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP", "UNITS_CODE", "ORG_UNIT", "USER_CATG", "SAL_RANGE");
		    //SumFieldToString = QueryFeildString.tranferListStringToGroupbyString(SumField);
			
			PageData pdSysConfig = new PageData();
			pdSysConfig.put("KEY_CODE", SysConfigKeyCode.OperLaborGRPCond);
			retItem.setSumFieldToString(sysConfigManager.getSysConfigByKey(pdSysConfig));
			retItem.setSumField(QueryFeildString.tranferStringToList(retItem.getSumFieldToString()));
		}
		if(which.equals(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey())){
			//劳务派遣工资
			retItem.setTypeCodeDetail(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey());
			retItem.setTypeCodeSummy(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey());
			retItem.setTypeCodeListen(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey());
			//1、合同化、市场化、运行人员、系统内运行按6列汇总：业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本、企业特定员工分类、工资范围编码
			//2、劳务派遣运行按4列汇总：                                                      业务日期、组织机构、帐套、员工组、所属二级单位、组织单元文本
		    //SumField = Arrays.asList("BUSI_DATE", "DEPT_CODE", "CUST_COL7", "USER_GROP", "UNITS_CODE", "ORG_UNIT");
		    //SumFieldToString = QueryFeildString.tranferListStringToGroupbyString(SumField);
			
			PageData pdSysConfig = new PageData();
			pdSysConfig.put("KEY_CODE", SysConfigKeyCode.LaborGRPCond);
			retItem.setSumFieldToString(sysConfigManager.getSysConfigByKey(pdSysConfig));
			retItem.setSumField(QueryFeildString.tranferStringToList(retItem.getSumFieldToString()));
		}
		return retItem;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
