package com.fh.controller.dataInput.dataInput;

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
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.User;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.BillState;

import net.sf.json.JSONArray;

import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.Jurisdiction;
import com.fh.service.dataInput.dataInput.DataInputManager;
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
@RequestMapping(value="/dataInput")
public class DataInputController extends BaseController {
	
	String menuUrl = "dataInput/list.do"; //菜单地址(权限用)
	@Resource(name="dataInputService")
	private DataInputManager dataInputService;
	
	@Resource(name="sysconfigService")
	private SysConfigManager sysConfigManager;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="departmentService")
	private DepartmentService departmentService;
	
	//当前期间,取自tb_system_config的SystemDateTime字段
	String SystemDateTime = "";
    //设置必定不用编辑的列
    List<String> MustNotEditList = Arrays.asList("BUSI_DATE");

	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("TYPE_CODE", "BILL_OFF", "DEPT_CODE", "BUSI_DATE");
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表dataInput");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();

		ModelAndView mv = this.getModelAndView();
		mv.setViewName("dataInput/dataInput/dataInput_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime);
		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String DepartName = user.getDEPARTMENT_NAME();
		mv.addObject("DepartName", DepartName);

		//BILL_OFF FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		//TYPE_CODE PZTYPE 凭证字典
		mv.addObject("PZTYPE", DictsUtil.getDictsByParentCode(dictionariesService, "PZTYPE"));
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
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表FinanceAccounts");

		PageData getPd = this.getPageData();
		
		PageData pdTransfer = setTransferPd(getPd);
		//页面显示数据的年月
		pdTransfer.put("SystemDateTime", SystemDateTime);
		page.setPd(pdTransfer);
		
		List<PageData> varList = dataInputService.JqPage(page);	//列出Betting列表
		int records = dataInputService.countJqGridExtend(page);
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setRecords(records);
		result.setPage(page.getPage());
		
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
		logBefore(logger, Jurisdiction.getUsername()+"修改");

		PageData getPd = this.getPageData();
		//操作
		String oper = getPd.getString("oper");

		/*String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}*/
		//必定不用编辑的列  MustNotEditList Arrays.asList("BUSI_DATE");
		if(oper.equals("add")){
			getPd.put("BUSI_DATE", SystemDateTime);
			dataInputService.save(getPd);
			commonBase.setCode(0);
		} else {
			for(String strFeild : MustNotEditList){
				getPd.put(strFeild, getPd.get(strFeild + TmplUtil.keyExtra));
			}
			List<PageData> listData = new ArrayList<PageData>();
			listData.add(getPd);
			dataInputService.batchUpdateDatabase(listData);
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
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		/*String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}*/
		if(null != listData && listData.size() > 0){
				dataInputService.batchUpdateDatabase(listData);
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
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		/*String checkState = CheckState(SelectedBillCode,
				SelectedCustCol7, SelectedDepartCode, listData, "SERIAL_NO", TmplUtil.keyExtra);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}*/
        if(null != listData && listData.size() > 0){
			dataInputService.deleteAll(listData);
			commonBase.setCode(0);
		}
		return commonBase;
	}
	
	private String CheckState(String SelectedBillCode,
			String SelectedCustCol7, String SelectedDepartCode, 
			List<PageData> pdList, String strFeild, String strFeildExtra) throws Exception{
		String strRut = "";
		/*if(!SelectedBillCode.equals(SelectBillCodeFirstShow)){
			String QueryFeild = " and BILL_CODE in ('" + SelectedBillCode + "') ";
			QueryFeild += " and BILL_STATE = '" + BillState.Normal.getNameKey() + "' ";
			QueryFeild += " and BILL_CODE not in (SELECT bill_code FROM tb_sys_sealed_info WHERE state = '1') ";
			
			PageData transferPd = new PageData();
			transferPd.put("SystemDateTime", SystemDateTime);
			transferPd.put("CanOperate", QueryFeild);
			List<String> getCodeList = housefundsummyService.getBillCodeList(transferPd);
			
			if(!(getCodeList != null && getCodeList.size()>0)){
				strRut = Message.OperDataSumAlreadyChange;
			}
		} else {
	        if(pdList!=null && pdList.size()>0){
	        	List<Integer> listStringSerialNo = QueryFeildString.getListIntegerFromListPageData(pdList, strFeild, strFeildExtra);
				String strSqlInSerialNo = QueryFeildString.tranferListIntegerToGroupbyString(listStringSerialNo);
	    		PageData transferPd = new PageData();
	    		PageData getQueryFeildPd = new PageData();
	    		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
	    		getQueryFeildPd.put("CUST_COL7", SelectedCustCol7);
	    		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
	    		if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
	    			QueryFeild += " and 1 != 1 ";
	    		}
	    		if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
	    			QueryFeild += " and 1 != 1 ";
	    		}
	    		QueryFeild += " and BILL_CODE like ' %' ";
	    		QueryFeild += " and SERIAL_NO in (" + strSqlInSerialNo + ") ";
	    		transferPd.put("QueryFeild", QueryFeild);
	    		
	    		//页面显示数据的年月
	    		transferPd.put("SystemDateTime", SystemDateTime);
	    		transferPd.put("SelectFeildName", strFeild);
	    		List<PageData> getSerialNo = housefunddetailService.getSerialNoBySerialNo(transferPd);
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
		}*/
		return strRut;
	}
	
	private PageData setTransferPd(PageData getPd) throws Exception{
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		
		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		getQueryFeildPd.put("BILL_OFF", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SystemDateTime);
		getQueryFeildPd.put("TYPE_CODE", SelectedTypeCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SystemDateTime!=null && !SystemDateTime.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		return getPd;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}