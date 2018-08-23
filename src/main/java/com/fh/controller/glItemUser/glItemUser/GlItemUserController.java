package com.fh.controller.glItemUser.glItemUser;

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
import com.fh.controller.common.CheckSystemDateTime;
import com.fh.controller.common.Common;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.Message;
import com.fh.controller.common.QueryFeildString;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.User;
import com.fh.exception.CustomException;
import com.fh.util.Const;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.enums.TmplType;
import com.fh.util.Jurisdiction;
import com.fh.util.excel.LeadingInExcelToPageData;
import com.fh.util.excel.TransferSbcDbc;

import net.sf.json.JSONArray;

import com.fh.service.fhoa.department.impl.DepartmentService;
import com.fh.service.glItemUser.glItemUser.GlItemUserManager;
import com.fh.service.housefundsummy.housefundsummy.HouseFundSummyManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.service.system.dictionaries.impl.DictionariesService;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.impl.TmplConfigDictService;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;

/** 
 * 说明： 在建工程信息维护
 * 创建人：zhangxiaoliu
 * 创建时间：2018-08-17
 */
@Controller
@RequestMapping(value="/glItemUser")
public class GlItemUserController extends BaseController {
	
	String menuUrl = "glItemUser/list.do"; //菜单地址(权限用)
	@Resource(name="glItemUserService")
	private GlItemUserManager glItemUserService;
	@Resource(name="housefundsummyService")
	private HouseFundSummyManager housefundsummyService;
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
	String TableNameDetail = "tb_house_fund_detail";
	String TableNameSummy = "tb_house_fund_summy_BILL";
	String TableNameBackup = "tb_house_fund_detail_backup";
	//临时数据
	String SelectBillCodeFirstShow = "临时数据";
	String SelectBillCodeLastShow = "";
	//枚举类型  1工资明细,2工资汇总,3公积金明细,4公积金汇总,5社保明细,6社保汇总,7工资接口,8公积金接口,9社保接口
    String TypeCodeDetail = TmplType.TB_HOUSE_FUND_DETAIL.getNameKey();
    String TypeCodeTransfer = TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey();

	//页面显示数据的年月
	//String SystemDateTime = "";
    //
	String AdditionalReportColumns = "";
	//
	private List<String> MustInputList = Arrays.asList("USER_CODE", "UNITS_CODE");
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("CUST_COL7", "DEPT_CODE");
    //设置必定不用编辑的列            SERIAL_NO 设置字段类型是数字，但不管隐藏 或显示都必须保存的
    List<String> MustNotEditList = Arrays.asList("SERIAL_NO", "BILL_CODE", "BUSI_DATE", "DEPT_CODE", "CUST_COL7");
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
		logBefore(logger, Jurisdiction.getUsername()+"列表GlItemUser");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)

		PageData getPd = this.getPageData();
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("glItemUser/glItemUser/glItemUser_list");

		//当前期间,取自tb_system_config的SystemDateTime字段
		String SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime.trim());
		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String DepartName = user.getDEPARTMENT_NAME();
		mv.addObject("DepartName", DepartName);
		
		// *********************加载单位树  DEPT_CODE*******************************
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService, DictsUtil.DepartShowAll);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表GlItemUser");

		PageData getPd = this.getPageData();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");

		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("DEPT_CODE", SelectedDepartCode);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		//if(!(SelectedDepartCode != null && !SelectedDepartCode.trim().equals(""))){
		//	QueryFeild += " and 1 != 1 ";
		//}
		getPd.put("QueryFeild", QueryFeild);
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		page.setPd(getPd);
		List<PageData> varList = glItemUserService.JqPage(page);	//列出Betting列表
		int records = glItemUserService.countJqGridExtend(page);
		
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
		logBefore(logger, Jurisdiction.getUsername()+"修改GlItemUser");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限

		PageData getPd = this.getPageData();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		//操作
		String oper = getPd.getString("oper");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
			return commonBase;
		}

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedDepartCode, ShowDataDepartCode, "1");
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			//commonBase.setCode(2);
			//commonBase.setMessage(strGetCheckMustSelected);
			//return commonBase;
		}

		//必定不用编辑的列  MustNotEditList Arrays.asList("BUSI_DATE");//, "DEPT_CODE"
		if(oper.equals("add")){
			getPd.put("BUSI_DATE", SystemDateTime);
		}
		
		List<PageData> listData = new ArrayList<PageData>();
		listData.add(getPd);
		String checkState = CheckState(listData);
		if(checkState!=null && !checkState.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(checkState);
			return commonBase;
		}
		glItemUserService.batchUpdateDatabase(listData);
		commonBase.setCode(0);
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
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
			return commonBase;
		}

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedDepartCode, ShowDataDepartCode, "1");
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			//commonBase.setCode(2);
			//commonBase.setMessage(strGetCheckMustSelected);
			//return commonBase;
		}
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);

		if(null != listData && listData.size() > 0){
			String checkState = CheckState(listData);
			if(checkState!=null && !checkState.trim().equals("")){
				commonBase.setCode(2);
				commonBase.setMessage(checkState);
				return commonBase;
			}
			glItemUserService.batchUpdateDatabase(listData);
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
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
			return commonBase;
		}
		
		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedDepartCode, ShowDataDepartCode, "1");
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			//commonBase.setCode(2);
			//commonBase.setMessage(strGetCheckMustSelected);
			//return commonBase;
		}
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        if(null != listData && listData.size() > 0){
        	glItemUserService.deleteAll(listData);
			commonBase.setCode(0);
		}
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
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
		}

		//判断选择为必须选择的
		String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedDepartCode, ShowDataDepartCode, "1");
		if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			//commonBase.setCode(2);
			//commonBase.setMessage(strGetCheckMustSelected);
		}
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "glItemUser");
		mv.addObject("SelectedDepartCode", SelectedDepartCode);
		mv.addObject("ShowDataDepartCode", ShowDataDepartCode);
		mv.addObject("SystemDateTime", SystemDateTime);
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
	//public @ResponseBody CommonBase readExcel(@RequestParam(value="excel",required=false) MultipartFile file) throws Exception{
	public ModelAndView readExcel(@RequestParam(value="excel",required=false) MultipartFile file) throws Exception{
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;}//校验权限
		
		String strErrorMessage = "";

		PageData getPd = this.getPageData();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		String ShowDataDepartCode = getPd.getString("ShowDataDepartCode");
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");
		String mesDateTime = CheckSystemDateTime.CheckTranferSystemDateTime(SystemDateTime, sysConfigManager, false);
		if(mesDateTime!=null && !mesDateTime.trim().equals("")){
			commonBase.setCode(2);
			commonBase.setMessage(mesDateTime);
		}
		if(commonBase.getCode()==-1){
			//判断选择为必须选择的
			String strGetCheckMustSelected = CheckMustSelectedAndSame(SelectedDepartCode, ShowDataDepartCode, "1");
			//if(strGetCheckMustSelected!=null && !strGetCheckMustSelected.trim().equals("")){
			//	commonBase.setCode(2);
			//	commonBase.setMessage(strGetCheckMustSelected);
			//} else {
							Map<String, TmplConfigDetail> map_SetColumnsList = null;//Common.GetSetColumnsList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, tmplconfigService);
							Map<String, TableColumns> map_HaveColumnsList = Common.GetHaveColumnsList(TypeCodeDetail, tmplconfigService);
							Map<String, Object> DicList = null;//Common.GetDicList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
							
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

								uploadAndReadMap = testExcel.uploadAndRead(file, propertiesFileName, kyeName, sheetIndex,
										titleAndAttribute, map_HaveColumnsList, map_SetColumnsList, DicList, false, false);
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
									for(int i=0; i<listSize; i++){
										PageData pdAdd = listUploadAndRead.get(i);
										String getUSER_CODE = (String) pdAdd.get("USER_CODE");
										if(getUSER_CODE!=null && !getUSER_CODE.trim().equals("")){
											
											String getBUSI_DATE = (String) pdAdd.get("BUSI_DATE");
											String getDEPT_CODE = (String) pdAdd.get("DEPT_CODE");
											String getUNITS_CODE = (String) pdAdd.get("UNITS_CODE");
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
										    String getUSER_GROP = (String) pdAdd.get("USER_GROP");
										    if(!(getUSER_GROP!=null && !getUSER_GROP.trim().equals(""))){
												if(!sbRet.contains("员工组不能为空！")){
													sbRet.add("员工组不能为空！");
												}
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
											//commonBase = CalculationUpdateDatabase(true, commonBase, strErrorMessage, SelectedDepartCode, SelectedCustCol7, listAdd, strHelpful);
											
											/*String strFieldSelectKey = QueryFeildString.getFieldSelectKey(keyListBase, TmplUtil.keyExtra);
											String sqlRetSelect = Common.GetRetSelectColoumns(map_HaveColumnsList, TypeCodeDetail, TableNameBackup, SelectedDepartCode, strFieldSelectKey, tmplconfigService);
													
											List<PageData> dataCalculation = glItemUserService.getDataCalculation(TableNameBackup, sqlRetSelect, listAdd);
											if(dataCalculation!=null){
												for(PageData each : dataCalculation){
													each.put("SERIAL_NO", "");
													Common.setModelDefault(each, map_HaveColumnsList, map_SetColumnsList);
													each.put("CanOperate", strHelpful);
												}
											}
											//此处执行集合添加 
											glItemUserService.batchUpdateDatabase(dataCalculation);
											commonBase.setCode(0);
											commonBase.setMessage(strErrorMessage);*/
										}
									}
							    }
							} else {
								commonBase.setCode(-1);
							    commonBase.setMessage("TranslateUtil");
						    }
			//}
		}
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("common/uploadExcel");
		mv.addObject("local", "glItemUser");
		mv.addObject("SelectedDepartCode", SelectedDepartCode);
		mv.addObject("ShowDataDepartCode", ShowDataDepartCode);
		mv.addObject("SystemDateTime", SystemDateTime);
		mv.addObject("commonBaseCode", commonBase.getCode());
		mv.addObject("commonMessage", commonBase.getMessage());
		return mv;
	}
	
	/**下载模版
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/downExcel")
	//public void downExcel(HttpServletResponse response)throws Exception{
	public ModelAndView downExcel(JqPage page) throws Exception{
		PageData getPd = this.getPageData();
		//单位
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");
		int departSelf = Common.getDepartSelf(departmentService);
		if(departSelf == 1){
			SelectedDepartCode = Jurisdiction.getCurrentDepartmentID();
		}
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, tmplconfigService);

		PageData transferPd = this.getPageData();
		//页面显示数据的二级单位
		transferPd.put("SelectedDepartCode", SelectedDepartCode);
		//账套
		transferPd.put("SelectedCustCol7", SelectedCustCol7);
		//页面显示数据的二级单位
		List<PageData> varOList = glItemUserService.exportModel(transferPd);
		return export(SelectedDepartCode, SelectedCustCol7, varOList, "GlItemUser", map_SetColumnsList); //公积金明细
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出GlItemUser到excel");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
	    
		PageData getPd = this.getPageData();
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
		//当前区间
		String SystemDateTime = getPd.getString("SystemDateTime");

		Map<String, TmplConfigDetail> map_SetColumnsList = Common.GetSetColumnsList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, tmplconfigService);

		//页面显示数据的年月
		getPd.put("SystemDateTime", SystemDateTime);
		//账套
		getPd.put("SelectedCustCol7", SelectedCustCol7);
		//页面显示数据的二级单位
		getPd.put("SelectedDepartCode", SelectedDepartCode);

		String strBillCode = QueryFeildString.getQueryFeildBillCodeDetail(SelectedBillCode, SelectBillCodeFirstShow);
		getPd.put("CheckBillCode", strBillCode);
		
		page.setPd(getPd);
		List<PageData> varOList = glItemUserService.exportList(page);
		return export(SelectedDepartCode, SelectedCustCol7, varOList, "", map_SetColumnsList);
	}
	
	@SuppressWarnings("unchecked")
	private ModelAndView export(String SelectedDepartCode, String SelectedCustCol7, 
			List<PageData> varOList, String ExcelName, Map<String, TmplConfigDetail> map_SetColumnsList) throws Exception{
		Map<String, Object> DicList = Common.GetDicList(TypeCodeDetail, SelectedDepartCode, SelectedCustCol7, 
				tmplconfigService, tmplconfigdictService, dictionariesService, departmentService, userService, AdditionalReportColumns);
		
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
	
	private String CheckMustSelectedAndSame(String DEPT_CODE, String ShowDataDepartCode, String DepartTreeSource) throws Exception{
		String strRut = "";
		if(!(DEPT_CODE != null && !DEPT_CODE.trim().equals(""))){
			strRut += "查询条件中的责任中心不能为空！";
		} else {
		    if(!String.valueOf(0).equals(DepartTreeSource) && !DEPT_CODE.equals(ShowDataDepartCode)){
				strRut += "查询条件中所选责任中心与页面显示数据责任中心不一致，请单击查询再进行操作！";
		    }
		}
		return strRut;
	}
	
	private String CheckState(List<PageData> listData) throws Exception{
		String strRet = "";
		List<PageData> repeatList = glItemUserService.getRepeatList(listData);
		if(repeatList!=null && repeatList.size()>0){
			strRet = Message.HaveRepeatRecord;
		}
		return strRet;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
