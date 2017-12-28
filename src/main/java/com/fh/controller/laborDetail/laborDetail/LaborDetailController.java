package com.fh.controller.laborDetail.laborDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import com.fh.controller.common.Message;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.CommonBaseAndList;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.exception.CustomException;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.Jurisdiction;
import com.fh.util.excel.LeadingInExcelToPageData;

import net.sf.json.JSONArray;

import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.laborDetail.laborDetail.impl.LaborDetailService;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明：劳务报酬所得导入
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 */
@Controller
@RequestMapping(value="/laborDetail")
public class LaborDetailController extends BaseController {
	
	String menuUrl = "laborDetail/list.do"; //菜单地址(权限用)
	@Resource(name="laborDetailService")
	private LaborDetailService laborDetailService;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;
	@Resource(name="syssealedinfoService")
	private SysSealedInfoService syssealedinfoService;
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
	String TableNameDetail = "TB_LABOR_DETAIL";
	String TableNameBackup = "TB_LABOR_DETAIL_backup";

	//页面显示数据的年月
	String SystemDateTime = "";
	//设置字段类型是数字，但不管隐藏 或显示都必须保存的
	List<String> IsNumFeildButMustInput = Arrays.asList("SERIAL_NO");
	Map<String, TableColumns> map_HaveColumnsList = new LinkedHashMap<String, TableColumns>();
	Map<String, TmplConfigDetail> map_SetColumnsList = new LinkedHashMap<String, TmplConfigDetail>();

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表laborDetail");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("laborDetail/laborDetail/laborDetail_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime);
		mv.addObject("pd", getPd);
		
		map_HaveColumnsList = Common.GetHaveColumnsListByTableName(TableNameDetail, tmplconfigService);
		
		map_SetColumnsList.put("SERIAL_NO", new TmplConfigDetail("SERIAL_NO", "流水号", "0"));
		map_SetColumnsList.put("BUSI_DATE", new TmplConfigDetail("BUSI_DATE", "当前区间", "0"));
		map_SetColumnsList.put("USER_CODE", new TmplConfigDetail("USER_CODE", "编码", "1"));
		map_SetColumnsList.put("USER_NAME", new TmplConfigDetail("USER_NAME", "姓名", "1"));
		map_SetColumnsList.put("STAFF_IDENT", new TmplConfigDetail("STAFF_IDENT", "身份证号", "1"));
		map_SetColumnsList.put("GROSS_PAY", new TmplConfigDetail("GROSS_PAY", "应发合计", "1"));
		map_SetColumnsList.put("ACCRD_TAX", new TmplConfigDetail("ACCRD_TAX", "应交税金", "1"));
		map_SetColumnsList.put("ACT_SALY", new TmplConfigDetail("ACT_SALY", "实发合计", "1"));
		
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表laborDetail");

		PageData getPd = this.getPageData();
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}

		String QueryFeild = "";
		if(!(SystemDateTime != null && !SystemDateTime.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);
		
		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		page.setPd(getPd);
		List<PageData> varList = laborDetailService.JqPage(page);	//列出Betting列表
		int records = laborDetailService.countJqGridExtend(page);
		PageData userdata = laborDetailService.getFooterSummary(page);
		
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
		logBefore(logger, Jurisdiction.getUsername()+"修改laborDetail");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限

		PageData getPd = this.getPageData();
		//操作
		String oper = getPd.getString("oper");

		if(oper.equals("add")){
			getPd.put("SERIAL_NO", "");
			getPd.put("BUSI_DATE", SystemDateTime);
			List<PageData> listData = new ArrayList<PageData>();
			listData.add(getPd);
			commonBase = CalculationUpdateDatabase(true, commonBase, "", listData);
		} else {
			List<PageData> listCheckState = new ArrayList<PageData>();
			listCheckState.add(getPd);
			String checkState = CheckState(listCheckState);
			if(checkState!=null && !checkState.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(checkState);
				return commonBase;
			}
			getPd.put("TableName", TableNameDetail);
			Common.setModelDefault(getPd, map_HaveColumnsList, map_SetColumnsList, IsNumFeildButMustInput);
			List<PageData> listData = new ArrayList<PageData>();
			listData.add(getPd);
			laborDetailService.batchUpdateDatabase(listData);
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
		String checkState = CheckState(listData);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
		if(null != listData && listData.size() > 0){
			for(PageData item : listData){
	      	    item.put("TableName", TableNameDetail);
        	    Common.setModelDefault(item, map_HaveColumnsList, map_SetColumnsList, IsNumFeildButMustInput);
            }
			laborDetailService.batchUpdateDatabase(listData);
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
		String checkState = CheckState(listData);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
        if(null != listData && listData.size() > 0){
			laborDetailService.deleteAll(listData);
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
		commonBase = CalculationUpdateDatabase(false, commonBase, "", listData);
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
	    
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "laborDetail");
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
		if(!(SystemDateTime!=null && !SystemDateTime.trim().equals(""))){
			commonBase.setCode(2);
			commonBase.setMessage("当前区间不能为空！");
		} else {
			Map<String, TmplConfigDetail> map_SetColumnsList = new HashMap<String, TmplConfigDetail>();
			Map<String, Object> DicList = new HashMap<String, Object>();
			
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
					
				// 调用解析工具包
				testExcel = new LeadingInExcelToPageData<PageData>(formart);
				// 解析excel，获取客户信息集合

				uploadAndReadMap = testExcel.uploadAndRead(file, propertiesFileName, kyeName, sheetIndex,
						titleAndAttribute, map_HaveColumnsList, map_SetColumnsList, DicList);
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
					for(int i=0;i<listSize;i++){
						PageData pdAdd = listUploadAndRead.get(i);
						String getUSER_CODE = (String) pdAdd.get("USER_CODE");
						if(getUSER_CODE!=null && !getUSER_CODE.trim().equals("")){
							pdAdd.put("SERIAL_NO", "");
							String getBUSI_DATE = (String) pdAdd.get("BUSI_DATE");
							if(!(getBUSI_DATE!=null && !getBUSI_DATE.trim().equals(""))){
								pdAdd.put("BUSI_DATE", SystemDateTime);
								getBUSI_DATE = SystemDateTime;
							}
							if(!SystemDateTime.equals(getBUSI_DATE)){
								if(!sbRet.contains("导入区间和当前区间必须一致！")){
									sbRet.add("导入区间和当前区间必须一致！");
								}
							}
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
						commonBase = CalculationUpdateDatabase(true, commonBase, strErrorMessage, listAdd);
						
						
						
						
					}
				}
			} else {
				commonBase.setCode(-1);
				commonBase.setMessage("TranslateUtil");
			}
		}
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "laborDetail");
		mv.addObject("commonBaseCode", commonBase.getCode());
		mv.addObject("commonMessage", commonBase.getMessage());
		return mv;
	}
	
	private CommonBaseAndList getCalculationData(Boolean IsAdd, CommonBase commonBase, List<PageData> listData) throws Exception{
		CommonBaseAndList retCommonBaseAndList = new CommonBaseAndList();
		if(listData!=null && listData.size()>0){
	        for(PageData item : listData){
          	    item.put("TableName", TableNameBackup);
	       	    Common.setModelDefault(item, map_HaveColumnsList, map_SetColumnsList, IsNumFeildButMustInput);
	        }
			PageData getQueryFeildPd = new PageData();
			String QueryFeild = " and BUSI_DATE = '" + SystemDateTime + "' ";
			
			String GROSS_PAY = "GROSS_PAY";
			String ACCRD_TAX = "ACCRD_TAX";
			String ACT_SALY = "ACT_SALY";
			String sqlRetSelect = " select *, " 
			    + ACCRD_TAX + " " + ACCRD_TAX + TmplUtil.keyExtra + ", "
		        + ACT_SALY + " " + ACT_SALY + TmplUtil.keyExtra + " "
                + " from " + TableNameBackup;
			
			String sqlSumByUserCode =  " select USER_CODE, " 
			        + " sum(" + GROSS_PAY + ") " + GROSS_PAY + ", "
					+ " sum(" + ACCRD_TAX + ") " + ACCRD_TAX + ", "
					+ " sum(" + ACT_SALY + ") " + ACT_SALY + " "
					+ " from " + TableNameBackup 
					+ " where 1=1 "
					+ QueryFeild 
					+ " group by USER_CODE";
			//List<PageData> dataCalculation = laborDetailService.getDataCalculation(TableNameBackup, TmplUtil.keyExtra,
			//		sqlRetSelect, listData,
			//		sqlSumByUserCode);
			//retCommonBaseAndList.setList(dataCalculation);
		}
		retCommonBaseAndList.setCommonBase(commonBase);
		return retCommonBaseAndList;
	}
	
	private CommonBase UpdateDatabase(Boolean IsAdd, CommonBase commonBase, String strErrorMessage,
			CommonBaseAndList getCommonBaseAndList) throws Exception{
		if(getCommonBaseAndList!=null && getCommonBaseAndList.getList()!=null && getCommonBaseAndList.getList().size()>0){
			for(PageData each : getCommonBaseAndList.getList()){
				if(IsAdd){
					each.put("SERIAL_NO", "");
				}
				Common.setModelDefault(each, map_HaveColumnsList, map_SetColumnsList, IsNumFeildButMustInput);
				each.put("TableName", TableNameDetail);
			}
    		
    		//此处执行集合添加 
			laborDetailService.batchUpdateDatabase(getCommonBaseAndList.getList());
    		commonBase.setCode(0);
    		commonBase.setMessage(strErrorMessage);
		} else {
			commonBase = getCommonBaseAndList.getCommonBase();
		}
		return commonBase;
	}
	
	private CommonBase CalculationUpdateDatabase(Boolean IsAdd, CommonBase commonBase, String strErrorMessage,
			List<PageData> listData) throws Exception{
		CommonBaseAndList getCommonBaseAndList = getCalculationData(IsAdd, commonBase,     listData);
		return UpdateDatabase(IsAdd, commonBase, strErrorMessage,
				getCommonBaseAndList);
	}

	
	/**下载模版
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/downExcel")
	//public void downExcel(HttpServletResponse response)throws Exception{
	public ModelAndView downExcel(JqPage page) throws Exception{
		PageData transferPd = this.getPageData();
		transferPd.put("SystemDateTime", SystemDateTime);
		List<PageData> varOList = laborDetailService.exportModel(transferPd);
		return export(varOList, "SocialIncDetail", map_SetColumnsList);
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出SocialIncDetail到excel");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
	    
		PageData getPd = this.getPageData();
		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		page.setPd(getPd);
		List<PageData> varOList = laborDetailService.exportList(page);
		return export(varOList, "", map_SetColumnsList);
	}
	
	@SuppressWarnings("unchecked")
	private ModelAndView export(List<PageData> varOList, String ExcelName, 
			Map<String, TmplConfigDetail> map_SetColumnsList) throws Exception{
		//Map<String, Object> DicList = Common.GetDicList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, 
		//		tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
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
						    //String trans = col.getDICT_TRANS();
						    Object getCellValue = varOList.get(i).get(col.getCOL_CODE().toUpperCase());
						    //if(trans != null && !trans.trim().equals("")){
						    //	String value = "";
						    //	Map<String, String> dicAdd = (Map<String, String>) DicList.getOrDefault(trans, new LinkedHashMap<String, String>());
						    //	value = dicAdd.getOrDefault(getCellValue, "");
						    //	vpd.put("var" + j, value);
						    //} else {
							    vpd.put("var" + j, getCellValue.toString());
						    //}
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
	
	private String CheckState(List<PageData> pdSerialNo) throws Exception{
		String strRut = "";
		List<PageData> pdBillCode = laborDetailService.getBillCodeBySerialNo(pdSerialNo);
		//String strCanOperate = FilterBillCode.getBillCodeNotInSumInvalidDetail(TableNameSummy) + QueryFeildString.getNotReportBillCode();
		if(pdBillCode != null){
			for(PageData pd : pdBillCode){
				String BILL_CODE = pd.getString("BILL_CODE");
				if(BILL_CODE!=null && !BILL_CODE.trim().equals("")){
					strRut = Message.OperDataAlreadySum;
				}
			}
		}
		return strRut;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
