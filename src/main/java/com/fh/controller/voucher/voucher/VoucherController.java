package com.fh.controller.voucher.voucher;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.sql.visitor.functions.If;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.Common;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.GenerateTransferData;
import com.fh.controller.common.TmplUtil;
import com.fh.entity.CommonBase;
import com.fh.entity.JqGridModel;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.SysSealed;
import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.User;
import com.fh.service.fhoa.department.DepartmentManager;
import com.fh.service.glZrzxFx.glZrzxFx.GlZrzxFxManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysSealedInfo.syssealedinfo.SysSealedInfoManager;
import com.fh.service.sysUnlockInfo.sysunlockinfo.SysUnlockInfoManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.system.user.UserManager;
import com.fh.service.system.user.impl.UserService;
import com.fh.service.tmplConfigDict.tmplconfigdict.TmplConfigDictManager;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;
import com.fh.service.voucher.voucher.VoucherManager;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.SqlTools.CallBack;
import com.fh.util.collectionSql.GroupUtils;
import com.fh.util.collectionSql.GroupUtils.GroupBy;
import com.fh.util.StringUtil;
import com.fh.util.Tools;
import com.fh.util.date.DateFormatUtils;
import com.fh.util.date.DateUtils;
import com.fh.util.enums.BillType;
import com.fh.util.enums.TmplType;
import com.fh.util.enums.TransferOperType;

import net.sf.json.JSONArray;

/**
 * 凭证数据传输
 * 
 * @ClassName: VoucherController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jiachao
 * @date 2017年6月29日
 *
 */
@Controller
@RequestMapping(value = "/voucher")
public class VoucherController extends BaseController {

	String menuUrl = "voucher/list.do"; // 菜单地址(权限用)
	@Resource(name = "voucherService")
	private VoucherManager voucherService;

	@Resource(name = "departmentService")
	private DepartmentManager departmentService;

	@Resource(name = "tmplconfigService")
	private TmplConfigService tmplconfigService;

	@Resource(name = "tmplconfigdictService")
	private TmplConfigDictManager tmplConfigDictService;

	@Resource(name = "dictionariesService")
	private DictionariesManager dictionariesService;

	@Resource(name = "syssealedinfoService")
	private SysSealedInfoManager syssealedinfoService;

	@Resource(name = "sysconfigService")
	private SysConfigManager sysConfigManager;

	@Resource(name = "userService")
	private UserManager userService;

	@Resource(name = "sysunlockinfoService")
	private SysUnlockInfoManager sysUnlockInfoService;

	@Resource(name = "glZrzxFxService")
	private GlZrzxFxManager glZrzxFxService;

	// 底行显示的求和与平均值字段
	private StringBuilder SqlUserdata = new StringBuilder();

	// 判断当前人员的所在组织机构是否只有自己单位
	private int departSelf = 0;

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		this.departSelf = 0;
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("voucher/voucher/voucher_list");
		PageData pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		if (which == null)
			which = "16";// 取默认值-合同化工资传输表
		// String tableCode = getTableCode(which);
		// 此处放当前页面初始化时用到的一些数据，例如搜索的下拉列表数据，所需的字典数据、权限数据等等。
		// mv.addObject("pd", pd);
		// *********************加载单位树*******************************
		String departTreeSource = DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if (departTreeSource.equals("0")) {
			this.departSelf = 1;
			pd.put("departTreeSource", departTreeSource);
		}
		mv.addObject("zTreeNodes", departTreeSource);
		// ***********************************************************

		pd.put("which", which);
		mv.addObject("pd", pd);

		// 设置期间
		pd.put("KEY_CODE", "SystemDataTime");
		String busiDate = sysConfigManager.getSysConfigByKey(pd);
		pd.put("busiDate", busiDate);

		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		// 生成主表结构
		TmplUtil tmplUtil = new TmplUtil(tmplconfigService, tmplConfigDictService, dictionariesService,
				departmentService, userService);
		String jqGridColModel = tmplUtil.generateStructureNoEdit(which, Jurisdiction.getCurrentDepartmentID());
		mv.addObject("jqGridColModel", jqGridColModel);

		// 生成子表结构
		/*
		 * String jqGridColModelSub =
		 * tmplUtil.generateStructureNoEdit(tableCodeSub,
		 * Jurisdiction.getCurrentDepartmentID());
		 * mv.addObject("jqGridColModelSub", jqGridColModelSub);
		 */

		// 底行显示的求和与平均值字段
		SqlUserdata = tmplUtil.getSqlUserdata();
		boolean hasUserData = false;
		if (SqlUserdata != null && !SqlUserdata.toString().trim().equals("")) {
			hasUserData = true;
		}
		mv.addObject("HasUserData", hasUserData);

		// CUST_COL7 FMISACC 帐套字典
		mv.addObject("fmisacc", DictsUtil.getDictsByParentBianma(dictionariesService, "FMISACC"));
		// USER_CATG PARTUSERTYPE 特定员工分类字典
		mv.addObject("partusertype", DictsUtil.getDictsByParentBianma(dictionariesService, "PARTUSERTYPE"));
		// SAL_RANGE SALARYRANGE 工资范围字典
		mv.addObject("salaryrange", DictsUtil.getDictsByParentBianma(dictionariesService, "SALARYRANGE"));
		return mv;
	}

	/**
	 * 传输数据查询
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/voucherSearch")
	public ModelAndView voucherSearch(Page page) throws Exception {
		this.departSelf = 0;
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("voucher/voucher/voucher_search");
		PageData pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		if (which == null)
			which = "16";// 取默认值-合同化工资传输表
		// String tableCode = getTableCode(which);
		// 此处放当前页面初始化时用到的一些数据，例如搜索的下拉列表数据，所需的字典数据、权限数据等等。
		// mv.addObject("pd", pd);
		// *********************加载单位树*******************************
		String departTreeSource = DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if (departTreeSource.equals("0")) {
			this.departSelf = 1;
			pd.put("departTreeSource", departTreeSource);
		}
		mv.addObject("zTreeNodes", departTreeSource);
		// ***********************************************************

		pd.put("which", which);
		mv.addObject("pd", pd);

		// 设置期间
		pd.put("KEY_CODE", "SystemDataTime");
		String busiDate = sysConfigManager.getSysConfigByKey(pd);
		pd.put("busiDate", busiDate);

		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		// 生成主表结构
		TmplUtil tmplUtil = new TmplUtil(tmplconfigService, tmplConfigDictService, dictionariesService,
				departmentService, userService);
		String jqGridColModel = tmplUtil.generateStructureNoEdit(which, Jurisdiction.getCurrentDepartmentID());
		mv.addObject("jqGridColModel", jqGridColModel);

		// 生成子表结构
		/*
		 * String jqGridColModelSub =
		 * tmplUtil.generateStructureNoEdit(tableCodeSub,
		 * Jurisdiction.getCurrentDepartmentID());
		 * mv.addObject("jqGridColModelSub", jqGridColModelSub);
		 */

		// 底行显示的求和与平均值字段
		SqlUserdata = tmplUtil.getSqlUserdata();
		boolean hasUserData = false;
		if (SqlUserdata != null && !SqlUserdata.toString().trim().equals("")) {
			hasUserData = true;
		}
		mv.addObject("HasUserData", hasUserData);

		// mv.addObject("emplgrp",
		// DictsUtil.getDictsByParentBianma(dictionariesService, "EMPLGRP"));
		mv.addObject("fmisacc", DictsUtil.getDictsByParentBianma(dictionariesService, "FMISACC"));
		// USER_CATG PARTUSERTYPE 特定员工分类字典
		mv.addObject("partusertype", DictsUtil.getDictsByParentBianma(dictionariesService, "PARTUSERTYPE"));
		// SAL_RANGE SALARYRANGE 工资范围字典
		mv.addObject("salaryrange", DictsUtil.getDictsByParentBianma(dictionariesService, "SALARYRANGE"));
		return mv;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "获取待传输列表Voucher");
		PageData pd = new PageData();
		pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		String voucherType = pd.getString("VOUCHER_TYPE");
		String tableCode = getTableCode(which);
		pd.put("TABLE_CODE", tableCode);
		String sealTypeTransfer = which == null ? TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey() : which;// 传输接口类型
		String sealType = getSealType(which);// 接口封存类型对应的汇总接口类型
		pd.put("BILL_TYPE_TRANSFER", sealTypeTransfer);// 接口封存类型
		pd.put("VOUCHER_TYPE", voucherType);// 接口封存类型
		pd.put("BILL_TYPE", sealType);// 接口封存类型对应的汇总接口类型
		pd.put("USER_GROP", DictsUtil.getEmplGroupType(which));
		String strDeptCode = "";
		if (departSelf == 1)
			strDeptCode = Jurisdiction.getCurrentDepartmentID();
		else
			strDeptCode = pd.getString("DEPT_CODE");// 单位检索条件
		if (StringUtil.isNotEmpty(strDeptCode)) {
			String[] strDeptCodes = strDeptCode.split(",");
			pd.put("DEPT_CODES", strDeptCodes);
		}

		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		String filters = pd.getString("filters"); // 多条件过滤条件
		if (null != filters && !"".equals(filters)) {
			pd.put("filterWhereResult", SqlTools.constructWhere(filters, new CallBack() {
				public String executeField(String f) {
					if (f.equals("BILL_CODE"))
						return "A.BILL_CODE";
					else
						return f;
				}
			}));
		}

		page.setPd(pd);
		List<PageData> varList = voucherService.listAll(pd); // 列出Voucher列表
		// int records = voucherService.countJqGrid(pd);
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		// result.setRowNum(page.getRowNum());
		// result.setRecords(records);
		// result.setPage(page.getPage());
		PageData userdata = null;
		if (SqlUserdata != null && !SqlUserdata.toString().trim().equals("")) {
			// 底行显示的求和与平均值字段
			pd.put("Userdata", SqlUserdata.toString());
			userdata = voucherService.getFooterSummary(page);
			result.setUserdata(userdata);
		}
		return result;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getSyncDelList")
	public @ResponseBody PageResult<PageData> getSyncDelList(JqPage page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "获取待传输列表Voucher");
		PageData pd = new PageData();
		pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		// String voucherType = pd.getString("VOUCHER_TYPE");
		String tableCode = getTableCode(which);
		pd.put("TABLE_CODE", tableCode);
		String sealType = which == null ? TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey() : which;// 传输接口类型;
		pd.put("BILL_TYPE", sealType);// 封存类型
		pd.put("USER_GROP", DictsUtil.getEmplGroupType(which));
		// String strDeptCode = pd.getString("DEPT_CODE");// 单位检索条件
		String strDeptCode = "";
		if (departSelf == 1)
			strDeptCode = Jurisdiction.getCurrentDepartmentID();
		else
			strDeptCode = pd.getString("DEPT_CODE");// 单位检索条件
		if (StringUtil.isNotEmpty(strDeptCode)) {
			String[] strDeptCodes = strDeptCode.split(",");
			pd.put("DEPT_CODES", strDeptCodes);
		}

		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		String filters = pd.getString("filters"); // 多条件过滤条件
		if (null != filters && !"".equals(filters)) {
			pd.put("filterWhereResult", SqlTools.constructWhere(filters, new CallBack() {
				public String executeField(String f) {
					if (f.equals("BILL_CODE"))
						return "A.BILL_CODE";
					else
						return f;
				}
			}));
		}

		page.setPd(pd);
		List<PageData> varList = voucherService.listSyncDelList(pd); // 列出Voucher列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);

		/*
		 * PageData userdata = null; if (SqlUserdata != null &&
		 * !SqlUserdata.toString().trim().equals("")) { // 底行显示的求和与平均值字段
		 * pd.put("Userdata", SqlUserdata.toString()); userdata =
		 * voucherService.getFooterSummary(page); result.setUserdata(userdata);
		 * }
		 */
		return result;
	}

	/**
	 * 获取明细显示结构
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getDetailColModel")
	public @ResponseBody CommonBase getDetailColModel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "getDetailColModel");
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		PageData pd = this.getPageData();
		String DEPT_CODE = (String) pd.get("DEPT_CODE");
		String which = pd.getString("TABLE_CODE");
		/*String tableCodeSub = "";
		if (which != null && which.equals("1")) {
			tableCodeSub = "TB_STAFF_DETAIL";
		} else if (which != null && which.equals("2")) {
			tableCodeSub = "TB_SOCIAL_INC_DETAIL";
		} else if (which != null && which.equals("3")) {
			tableCodeSub = "TB_HOUSE_FUND_DETAIL";
		} else {
			tableCodeSub = "TB_STAFF_DETAIL";
		}*/
		TmplUtil tmpl = new TmplUtil(tmplconfigService, tmplConfigDictService, dictionariesService, departmentService,
				userService);
		String detailColModel = tmpl.generateStructureNoEdit(which, DEPT_CODE);

		commonBase.setCode(0);
		commonBase.setMessage(detailColModel);

		return commonBase;
	}

	/**
	 * 列表-获取明细信息
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getDetailList")
	public @ResponseBody PageResult<PageData> getDetailList() throws Exception {
		PageData pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		String subTableCode = getSubTableCode(which);
		pd.put("TABLE_CODE", subTableCode);
		List<PageData> varList = voucherService.listDetail(pd); // 列出Betting列表
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		return result;
	}

	/**
	 * 批量传输
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/voucherTransfer")
	public @ResponseBody CommonBase voucherTransfer() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "凭证传输");
		// String orgCode = Tools.readTxtFile(Const.ORG_CODE); // 读取总部组织机构编码
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;}
		// //校验权限
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		PageData pd = this.getPageData();
		String strDataRows = pd.getString("DATA_ROWS");
		JSONArray array = JSONArray.fromObject(strDataRows);
		@SuppressWarnings("unchecked")
		List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);// 过时方法
		if (null != listTransferData && listTransferData.size() > 0) {
			List<PageData> listTransferDataFiltered=filterGroupRow(listTransferData);
			/********************** 生成传输数据 ************************/
			String which = pd.getString("TABLE_CODE");
			addLineNumForTransferData(which, listTransferDataFiltered);
			String tableCode = getTableCode(which);
			String tableCodeOnFmis = getTableCodeOnFmis(which);
			// String voucherType=pd.getString("VOUCHER_TYPE");
			// 根据表编号和真实表名称获取用于传输的字段列配置信息
			List<TableColumns> tableColumnsForTransfer = getTableColumnsForTransfer(which, tableCode);
			GenerateTransferData generateTransferData = new GenerateTransferData();
			Map<String, List<PageData>> mapTransferData = new HashMap<String, List<PageData>>();
			mapTransferData.put(tableCodeOnFmis, listTransferDataFiltered);
			PageData pdFirst = listTransferData.get(0);
			String orgCode = pdFirst.getString("CUST_COL7");
			String transferData = generateTransferData.generateTransferData(tableColumnsForTransfer, mapTransferData,
					orgCode, TransferOperType.DELETE);

			// 执行上传FIMS
			Service service = new Service();
			Call call = (Call) service.createCall();
			pd.put("KEY_CODE", "JSynFactTableData");
			String strUrl = sysConfigManager.getSysConfigByKey(pd);
			URL url = new URL(strUrl);
			call.setTargetEndpointAddress(url);
			call.setOperationName(new QName("http://JSynFactTableData.j2ee", "synFactData"));
			call.setUseSOAPAction(true);
			String message = (String) call.invoke(new Object[] { transferData });
			System.out.println(message);
			if (message == null) {
				commonBase.setCode(-1);
				commonBase.setMessage("传输接口出错,请联系管理员！");
			} else {
				if (message.equals("TRUE")) {
					String transferDataInsert = generateTransferData.generateTransferData(tableColumnsForTransfer,
							mapTransferData, orgCode, TransferOperType.INSERT);
					String messageInsert = (String) call.invoke(new Object[] { transferDataInsert });
					if (messageInsert.equals("TRUE")) {
						// 执行上传成功后对数据进行封存
						List<SysSealed> listSysSealed = new ArrayList<SysSealed>();
						User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
						String userId = user.getUSER_ID();
						// 将获取的字典数据进行分组
						Map<String, List<PageData>> mapListTransferData = GroupUtils.group(listTransferDataFiltered,
								new GroupBy<String>() {
									@Override
									public String groupby(Object obj) {
										PageData d = (PageData) obj;
										return d.getString("DEPT_CODE"); // 分组依据为DEPT_CODE
									}
								});
						// for (PageData pageData : mapListTransferData) {
						for (Map.Entry<String, List<PageData>> entry : mapListTransferData.entrySet()) {
							SysSealed item = new SysSealed();
							// item.setBILL_CODE(pageData.getString("BILL_CODE"));
							item.setBILL_CODE(" ");
							item.setRPT_DEPT(entry.getKey());
							List<PageData> listItem = entry.getValue();
							PageData pgItem = listItem.get(0);
							item.setRPT_DUR(pgItem.getString("BUSI_DATE"));
							item.setBILL_OFF(pgItem.getString("CUST_COL7"));
							item.setRPT_USER(userId);
							item.setRPT_DATE(DateUtils.getCurrentTime());// YYYY-MM-DD
																			// HH:MM:SS
							// String sealType = getSealType(which, "2");// 封存类型
							String sealType = which == null ? TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey() : which;// 传输接口类型
							item.setBILL_TYPE(sealType);
							item.setSTATE("1");// 枚举 1封存,0解封
							listSysSealed.add(item);
						}
						syssealedinfoService.insertBatch(listSysSealed);
						/******************************************************/
						commonBase.setCode(0);
					} else {
						commonBase.setCode(-1);
						commonBase.setMessage(messageInsert);
					}
				} else {
					commonBase.setCode(-1);
					commonBase.setMessage(message);
				}
			}
		}
		return commonBase;
	}

	/**
	 * 根据表编号和真实表名称获取用于传输的字段列配置信息
	 * 
	 * @param which
	 * @param tableCode
	 * @return
	 * @throws Exception
	 */
	private List<TableColumns> getTableColumnsForTransfer(String which, String tableCode) throws Exception {
		// 用语句查询出数据库表的所有字段及其属性；拼接成jqgrid全部列
		List<TableColumns> tableColumns = tmplconfigService.getTableColumns(tableCode);
		//TmplUtil tmplUtil = new TmplUtil(tmplconfigService, tmplConfigDictService, dictionariesService,
		//		departmentService, userService);
		PageData pdTableCodeTmpl = new PageData();
		pdTableCodeTmpl.put("TABLE_NO", which);
		PageData pdTableCodeTmplResult = tmplconfigService.findTableCodeByTableNo(pdTableCodeTmpl);
		String tableCodeTmpl = pdTableCodeTmplResult.getString("TABLE_CODE");
		//List<TmplConfigDetail> tmplColumns = tmplUtil.getShowColumnList(tableCodeTmpl,
		//		Jurisdiction.getCurrentDepartmentID());
		List<TmplConfigDetail> tmplColumns = Common.getShowColumnList(tableCodeTmpl,
				Jurisdiction.getCurrentDepartmentID(), tmplconfigService);
		List<TableColumns> tableColumnsMerge = new ArrayList<TableColumns>();
		for (TmplConfigDetail tmplConfigDetail : tmplColumns) {
			if (tmplConfigDetail.getCOL_TRANSFER().equals("1")) {
				TableColumns tableColumnMergeItem = new TableColumns();
				for (TableColumns tableColumn : tableColumns) {
					if (tmplConfigDetail.getCOL_CODE().equals(tableColumn.getColumn_name())) {
						tableColumnMergeItem.setColumn_key(tableColumn.getColumn_key());
						tableColumnMergeItem.setColumn_name(tableColumn.getColumn_name());
						tableColumnMergeItem.setColumn_type(tableColumn.getColumn_type());
						break;
					}
				}
				tableColumnsMerge.add(tableColumnMergeItem);
			}
		}
		// 增加F_LINE_NO 分线编号 字符型
		TableColumns tableColumnLineNo = new TableColumns();
		tableColumnLineNo.setColumn_name("LINE_NO");
		tableColumnLineNo.setColumn_type("VARCHAR");
		tableColumnsMerge.add(tableColumnLineNo);
		return tableColumnsMerge;
	}

	/**
	 * 根据表编号和真实表名称获取用于传输的字段列配置信息
	 * 
	 * @param which
	 * @param tableCode
	 * @return
	 * @throws Exception
	 */
	private void addLineNumForTransferData(String which, List<PageData> listTransferData) throws Exception {
		// 用语句查询出数据库表的所有字段及其属性；拼接成jqgrid全部列
		List<PageData> listGlZrzxfx = glZrzxFxService.listAll();
		for (PageData pdSource : listTransferData) {
			for (PageData pdGl : listGlZrzxfx) {
				if (pdSource.getString("DEPT_CODE").equals(pdGl.getString("DEPT_CODE"))) {
					pdSource.put("LINE_NO", pdGl.getString("LINE_NO"));
				}
			}
		}
	}

	/**
	 * 根据表编号和真实表名称获取用于传输的字段列配置信息
	 * 
	 * @param which
	 * @param tableCode
	 * @return
	 * @throws Exception
	 */
	private List<PageData> filterGroupRow(List<PageData> listTransferData) throws Exception {
		List<PageData> listTransferDataFiltered = new ArrayList<PageData>();
		for (PageData pdSource : listTransferData) {
			if (StringUtil.isEmpty(pdSource.getString("BILL_CODE")))
				continue;
			listTransferDataFiltered.add(pdSource);
		}
		return listTransferDataFiltered;
	}

	/**
	 * 同步删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/syncDel")
	public @ResponseBody CommonBase syncDel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "同步删除");
		// String orgCode = Tools.readTxtFile(Const.ORG_CODE); // 读取总部组织机构编码
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;}
		// //校验权限
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		PageData pd = this.getPageData();
		String strDataRows = pd.getString("DATA_ROWS");
		JSONArray array = JSONArray.fromObject(strDataRows);
		@SuppressWarnings("unchecked")
		List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);// 过时方法
		if (null != listTransferData && listTransferData.size() > 0) {
			List<PageData> listTransferDataFiltered=filterGroupRow(listTransferData);
			/********************** 生成传输数据 ************************/
			String which = pd.getString("TABLE_CODE");
			String tableCode = getTableCode(which);
			String tableCodeOnFmis = getTableCodeOnFmis(which);
			// String voucherType=pd.getString("VOUCHER_TYPE");

			// 根据表编号和真实表名称获取用于传输的字段列配置信息
			// List<TableColumns> tableColumns =
			// tmplconfigService.getTableColumns(tableCode);
			List<TableColumns> tableColumnsForTransfer = getTableColumnsForTransfer(which, tableCode);
			GenerateTransferData generateTransferData = new GenerateTransferData();
			Map<String, List<PageData>> mapTransferData = new HashMap<String, List<PageData>>();
			mapTransferData.put(tableCodeOnFmis, listTransferDataFiltered);
			PageData pdFirst = listTransferDataFiltered.get(0);
			String orgCode = pdFirst.getString("CUST_COL7");
			String transferData = generateTransferData.generateTransferData(tableColumnsForTransfer, mapTransferData,
					orgCode, TransferOperType.DELETE);

			// 执行上传FIMS
			Service service = new Service();
			Call call = (Call) service.createCall();
			pd.put("KEY_CODE", "JSynFactTableData");
			String strUrl = sysConfigManager.getSysConfigByKey(pd);
			URL url = new URL(strUrl);
			call.setTargetEndpointAddress(url);
			call.setOperationName(new QName("http://JSynFactTableData.j2ee", "synFactData"));
			call.setUseSOAPAction(true);
			String message = (String) call.invoke(new Object[] { transferData });
			System.out.println(message);
			if (message == null) {
				commonBase.setCode(-1);
				commonBase.setMessage("传输接口出错,请联系管理员！");
			} else {
				if (message.equals("TRUE")) {
					// 更改TB_SYS_UNLOCK_INFO删除状态
					User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
					String userId = user.getUSER_ID();
					for (PageData pdItem : listTransferDataFiltered) {
						pdItem.put("DEL_USER", userId);
						pdItem.put("DEL_DATE", DateUtils.getCurrentTime());// YYYY-MM-DD
																			// HH:MM:SS
					}
					sysUnlockInfoService.edit(listTransferDataFiltered);
					commonBase.setCode(0);
				} else {
					commonBase.setCode(-1);
					commonBase.setMessage(message);
				}
			}
		}
		return commonBase;
	}

	/**
	 * 批量获取凭证号
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/batchVoucher")
	public @ResponseBody CommonBase batchVoucher() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "获取凭证号");
		// String orgCode = Tools.readTxtFile(Const.ORG_CODE); // 读取总部组织机构编码
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;}
		// //校验权限
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		PageData pd = this.getPageData();
		String strDataRows = pd.getString("DATA_ROWS");
		JSONArray array = JSONArray.fromObject(strDataRows);
		@SuppressWarnings("unchecked")
		List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);// 过时方法
		if (null != listTransferData && listTransferData.size() > 0) {
			List<PageData> listTransferDataFiltered=filterGroupRow(listTransferData);
			String which = pd.getString("TABLE_CODE");
			List<PageData> listVoucherNo = new ArrayList<PageData>();
			// 执行从FIMS获取凭证号
			Service service = new Service();
			Call call = (Call) service.createCall();
			pd.put("KEY_CODE", "JQueryPzInformation");
			String strUrl = sysConfigManager.getSysConfigByKey(pd);
			URL url = new URL(strUrl);
			call.setTargetEndpointAddress(url);
			call.setOperationName(new QName("http://JQueryPzInformation.j2ee", "commonQueryPzBh"));
			call.setUseSOAPAction(true);
			// 遍历批量获取凭证号
			User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
			String userId = user.getUSER_ID();
			for (PageData item : listTransferDataFiltered) {
				String invoiceNumber = item.getString("BILL_CODE");// 单据编号
				// String fmisOrg = item.getString("DEPT_CODE");// FMIS组织机构编码
				// String fmisOrg = Tools.readTxtFile(Const.ORG_CODE); //
				// 读取总部组织机构编码
				String fmisOrg = item.getString("CUST_COL7"); // 读取总部组织机构编码
				// String tableName = "T_" + getTableCode(which);// 在fmis建立的业务表名
				String tableName = "T_" + getTableCodeOnFmis(which);// 在fmis建立的业务表名
				String result = (String) call.invoke(new Object[] { tableName, invoiceNumber, fmisOrg });// 对应定义参数
				if (result.length() > 0) {
					String[] stringArr = result.split(";");
					String pzbh = stringArr[0]; // 凭证编号
					// String kjqj = stringArr[1];// 会计期间
					// 执行获取凭证成功后对数据表进行凭证号更新
					PageData pdCert = new PageData();
					pdCert.put("BILL_CODE", item.getString("BILL_CODE"));
					pdCert.put("CERT_CODE", pzbh);
					pdCert.put("BILL_USER", userId);
					pdCert.put("BILL_DATE", DateUtils.getCurrentTime());// YYYY-MM-DD
																		// HH:MM:SS
					listVoucherNo.add(pdCert);
				}
			}
			if (null != listVoucherNo && listVoucherNo.size() > 0) {
				voucherService.updateCertCode(listVoucherNo);
			}
		}
		return commonBase;
	}

	/**
	 * 批量获取冲销凭证号
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/batchWriteOffVoucher")
	public @ResponseBody CommonBase batchWriteOffVoucher() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "获取冲销凭证号");
		// String orgCode = Tools.readTxtFile(Const.ORG_CODE); // 读取总部组织机构编码
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;}
		// //校验权限
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		PageData pd = this.getPageData();
		String strDataRows = pd.getString("DATA_ROWS");
		JSONArray array = JSONArray.fromObject(strDataRows);
		@SuppressWarnings("unchecked")
		List<PageData> listTransferData = (List<PageData>) JSONArray.toCollection(array, PageData.class);// 过时方法
		if (null != listTransferData && listTransferData.size() > 0) {
			List<PageData> listTransferDataFiltered=filterGroupRow(listTransferData);
			/********************** 生成传输数据 ************************/
			String which = pd.getString("TABLE_CODE");
			List<PageData> listVoucherNo = new ArrayList<PageData>();
			// 执行从FIMS获取冲销凭证号
			Service service = new Service();
			Call call = (Call) service.createCall();
			pd.put("KEY_CODE", "JRevertVoucher");
			String strUrl = sysConfigManager.getSysConfigByKey(pd);
			URL url = new URL(strUrl);
			call.setTargetEndpointAddress(url);
			call.setOperationName(new QName("http://JRevertVoucher.j2ee", "AmisRevertVoucher"));
			call.setUseSOAPAction(true);
			// 遍历批量获取凭证号
			User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
			String userId = user.getUSER_ID();
			for (PageData item : listTransferDataFiltered) {
				// String fmisOrg = item.getString("DEPT_CODE");// FMIS组织机构编码
				String fmisOrg = item.getString("CUST_COL7"); // 读取总部组织机构编码
				String fmisUrl = strUrl;// FMIS应用地址
				String invoiceNumber = item.getString("BILL_CODE");// 单据编号
				String invoiceState = item.getString("BILL_STATE");// 单据状态
				String voucherDate = item.getString("CERT_BILL_DATE");// 凭证日期
				String voucherNumber = item.getString("CERT_CODE");// 凭证编号
				// String tableName = "T_" + getTableCode(which);// 在fmis建立的业务表名
				String tableName = "T_" + getTableCodeOnFmis(which);// 在fmis建立的业务表名
				String workDate = DateUtils.getCurrentTime(DateFormatUtils.DATE_NOFUll_FORMAT);// 当前工作日期格式20170602

				String result = (String) call
						.invoke(new Object[] { tableName, fmisOrg, voucherDate, voucherNumber, workDate });// 对应定义参数
				if (result.length() > 0) {
					String[] stringArr = result.split(";");
					String flag = stringArr[0];
					if (flag.equals("TRUE")) {
						String reseverNumber = stringArr[1];// 冲销凭证编号
						// String invoiceNumbers = "";// 冲销凭证编号

						// 执行获取凭证成功后对数据表进行凭证号更新
						PageData pdCert = new PageData();
						pdCert.put("BILL_CODE", item.getString("BILL_CODE"));
						pdCert.put("CERT_CODE", item.getString("CERT_CODE"));
						pdCert.put("REVCERT_CODE", reseverNumber);
						pdCert.put("BILL_USER", userId);
						pdCert.put("BILL_DATE", DateUtils.getCurrentTime());// YYYY-MM-DD
																			// HH:MM:SS
						listVoucherNo.add(pdCert);
					}
				}
			}
			if (null != listVoucherNo && listVoucherNo.size() > 0) {
				voucherService.updateRevCertCode(listVoucherNo);
			}
		}
		return commonBase;
	}

	/**
	 * 根据前端业务表索引获取表名称
	 * 
	 * @param which
	 *            1、合同化工资 2、社保 3、公积金 4、市场化工资 5、系统内劳务工资 6、运行人员工资 7、劳务派遣工资
	 * @return
	 * @throws Exception
	 */
	private String getTableCode(String which) throws Exception {
		PageData pd = new PageData();
		pd.put("TABLE_NO", which);
		PageData pdResult = tmplconfigService.findTableCodeByTableNo(pd);
		String tableCodeTmpl = pdResult.getString("TABLE_CODE");
		String tableCodeOri = "";// 数据库真实业务数据表
		if (tableCodeTmpl.startsWith("TB_STAFF_TRANSFER")) {
			tableCodeOri = "TB_STAFF_SUMMY";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_TRANSFER")) {
			tableCodeOri = "TB_SOCIAL_INC_SUMMY";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_TRANSFER")) {
			tableCodeOri = "TB_HOUSE_FUND_SUMMY";
		} else {
			tableCodeOri = "TB_STAFF_SUMMY";
		}
		return tableCodeOri;

		/*
		 * String tableCode = ""; if (which != null && which.equals("1")) {
		 * tableCode = "TB_STAFF_SUMMY"; } else if (which != null &&
		 * which.equals("2")) { tableCode = "TB_SOCIAL_INC_SUMMY"; } else if
		 * (which != null && which.equals("3")) { tableCode =
		 * "TB_HOUSE_FUND_SUMMY"; } else { tableCode = "TB_STAFF_SUMMY"; }
		 * return tableCode;
		 */
	}

	/**
	 * 根据前端业务表索引获取定义在Fmis系统上定义的表名称
	 * 
	 * @param which
	 *            1、合同化工资 2、社保 3、公积金 4、市场化工资 5、系统内劳务工资 6、运行人员工资 7、劳务派遣工资
	 * @return
	 * @throws Exception
	 */
	private String getTableCodeOnFmis(String which) throws Exception {
		PageData pd = new PageData();
		String tableCodeOri = "";// 数据库真实业务数据表
		if (which.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
			pd.put("KEY_CODE", "StaffTransferHT");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		} else if (which.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
			pd.put("KEY_CODE", "StaffTransferSC");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		} else if (which.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
			pd.put("KEY_CODE", "StaffTransferXT");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		} else if (which.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
			pd.put("KEY_CODE", "StaffTransferYX");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		} else if (which.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
			pd.put("KEY_CODE", "StaffTransferLW");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		} else if (which.equals(TmplType.TB_SOCIAL_INC_TRANSFER.getNameKey())) {
			tableCodeOri = "TB_SOCIAL_INC_SUMMY";
		} else if (which.equals(TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey())) {
			tableCodeOri = "TB_HOUSE_FUND_SUMMY";
		} else {
			pd.put("KEY_CODE", "StaffTransferHT");
			tableCodeOri = sysConfigManager.getSysConfigByKey(pd);
		}
		return tableCodeOri;
	}

	/**
	 * 根据前端业务表索引获取表名称
	 * 
	 * @param which
	 * @return
	 * @throws Exception
	 */
	private String getSubTableCode(String which) throws Exception {
		PageData pd = new PageData();
		pd.put("TABLE_NO", which);
		PageData pdResult = tmplconfigService.findTableCodeByTableNo(pd);
		String tableCodeTmpl = pdResult.getString("TABLE_CODE");
		String tableCodeOri = "";// 数据库真实业务数据表
		if (tableCodeTmpl.startsWith("TB_STAFF_TRANSFER")) {
			tableCodeOri = "TB_STAFF_DETAIL";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_TRANSFER")) {
			tableCodeOri = "TB_SOCIAL_INC_DETAIL";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_TRANSFER")) {
			tableCodeOri = "TB_HOUSE_FUND_DETAIL";
		} else {
			tableCodeOri = "TB_STAFF_DETAIL";
		}
		return tableCodeOri;

		/*
		 * String tableCode = ""; if (which != null && which.equals("1")) {
		 * tableCode = "TB_STAFF_DETAIL"; } else if (which != null &&
		 * which.equals("2")) { tableCode = "TB_SOCIAL_INC_DETAIL"; } else if
		 * (which != null && which.equals("3")) { tableCode =
		 * "TB_HOUSE_FUND_DETAIL"; } else { tableCode = "TB_STAFF_DETAIL"; }
		 * return tableCode;
		 */
	}

	/**
	 * 根据前端业务表接口类型,获取对应的汇总类型
	 * 
	 * @param which
	 * @return
	 */
	private String getSealType(String which) {
		/*
		 * String sealType = ""; if (voucherType.equals("1")) { if (which !=
		 * null && which.equals("1")) { sealType =
		 * BillType.SALLARY_SUMMARY.getNameKey(); } else if (which != null &&
		 * which.equals("2")) { sealType =
		 * BillType.SECURITY_SUMMARY.getNameKey(); } else if (which != null &&
		 * which.equals("3")) { sealType = BillType.GOLD_SUMMARY.getNameKey(); }
		 * else { sealType = BillType.SALLARY_SUMMARY.getNameKey(); } } else if
		 * (voucherType.equals("2")) { if (which != null && which.equals("1")) {
		 * sealType = BillType.SALLARY_LISTEN.getNameKey(); } else if (which !=
		 * null && which.equals("2")) { sealType =
		 * BillType.SECURITY_LISTEN.getNameKey(); } else if (which != null &&
		 * which.equals("3")) { sealType = BillType.GOLD_LISTEN.getNameKey(); }
		 * else { sealType = BillType.SALLARY_LISTEN.getNameKey(); } } return
		 * sealType;
		 */

		String sealType = "";
		if (which != null) {
			if (which.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
				sealType = TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
				sealType = TmplType.TB_STAFF_SUMMY_MARKET.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
				sealType = TmplType.TB_STAFF_SUMMY_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
				sealType = TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
				sealType = TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey();
			} else if (which.equals(TmplType.TB_SOCIAL_INC_TRANSFER.getNameKey())) {
				sealType = TmplType.TB_SOCIAL_INC_SUMMY.getNameKey();
			} else if (which.equals(TmplType.TB_HOUSE_FUND_TRANSFER.getNameKey())) {
				sealType = TmplType.TB_HOUSE_FUND_SUMMY.getNameKey();
			}
		} else {
			sealType = TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey();
		}
		return sealType;
	}

	/**
	 * 汇总数据未上报单位
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/transferValidate")
	public ModelAndView transferValidate() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		// String billOff=pd.getString("BILL_OFF");
		pd.put("TABLE_CODE", which);
		pd.put("TABLE_NAME", TmplType.getValueByKey(which));
		// pd.put("BILL_OFF", billOff);
		// String empty=" : ;";
		String strDict = DictsUtil.getDepartmentValue(departmentService);
		pd.put("strDict", strDict);

		String billOffValus = DictsUtil.getDicValue(dictionariesService, "FMISACC");
		String billOffString = ":[All];" + billOffValus;
		pd.put("strBillOff", billOffString);

		mv.setViewName("voucher/voucher/voucher_transvali");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 列表-汇总数据未上报单位
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTransferValidate")
	public @ResponseBody PageResult<PageData> getTransferValidate() throws Exception {
		PageData pd = this.getPageData();
		String which = pd.getString("TABLE_CODE");
		String tableCode = getTableCode(which);
		pd.put("TABLE_CODE", tableCode);
		String sealType = getSealType(which);
		pd.put("BILL_TYPE", sealType);// 汇总封存类型

		String strDeptCode = "";
		if (departSelf == 1)
			strDeptCode = Jurisdiction.getCurrentDepartmentID();
		else
			strDeptCode = pd.getString("DEPT_CODE");// 单位检索条件
		if (StringUtil.isNotEmpty(strDeptCode)) {
			String[] strDeptCodes = strDeptCode.split(",");
			pd.put("DEPT_CODES", strDeptCodes);
		}

		String filters = pd.getString("filters"); // 多条件过滤条件
		if (null != filters && !"".equals(filters)) {
			pd.put("filterWhereResult", SqlTools.constructWhere(filters, null));
		}
		List<PageData> varList = voucherService.getTransferValidate(pd);
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		return result;
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "导出Voucher到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("备注1"); // 1
		titles.add("备注2"); // 2
		titles.add("备注3"); // 3
		titles.add("备注4"); // 4
		titles.add("备注5"); // 5
		titles.add("备注6"); // 6
		titles.add("备注7"); // 7
		titles.add("备注8"); // 8
		titles.add("备注9"); // 9
		titles.add("备注10"); // 10
		titles.add("备注11"); // 11
		titles.add("备注12"); // 12
		titles.add("备注13"); // 13
		titles.add("备注14"); // 14
		titles.add("备注15"); // 15
		titles.add("备注16"); // 16
		titles.add("备注17"); // 17
		titles.add("备注18"); // 18
		titles.add("备注19"); // 19
		titles.add("备注20"); // 20
		titles.add("备注21"); // 21
		titles.add("备注22"); // 22
		titles.add("备注23"); // 23
		titles.add("备注24"); // 24
		titles.add("备注25"); // 25
		titles.add("备注26"); // 26
		titles.add("备注27"); // 27
		titles.add("备注28"); // 28
		dataMap.put("titles", titles);
		List<PageData> varOList = voucherService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("BILL_CODE")); // 1
			vpd.put("var2", varOList.get(i).getString("BUSI_DATE")); // 2
			vpd.put("var3", varOList.get(i).getString("ESTB_DEPT")); // 3
			vpd.put("var4", varOList.get(i).getString("USER_GROP")); // 4
			vpd.put("var5", varOList.get(i).getString("SOC_INC_BASE")); // 5
			vpd.put("var6", varOList.get(i).getString("PER_BASIC_FUND")); // 6
			vpd.put("var7", varOList.get(i).getString("PER_SUPP_FUND")); // 7
			vpd.put("var8", varOList.get(i).getString("PER_TOTAL")); // 8
			vpd.put("var9", varOList.get(i).getString("DEPT_BASIC_FUND")); // 9
			vpd.put("var10", varOList.get(i).getString("DEPT_SUPP_FUND")); // 10
			vpd.put("var11", varOList.get(i).getString("DEPT_TOTAL")); // 11
			vpd.put("var12", varOList.get(i).getString("DEPT_CODE")); // 12
			vpd.put("var13", varOList.get(i).getString("USER_CATG")); // 13
			vpd.put("var14", varOList.get(i).getString("PMT_PLACE")); // 14
			vpd.put("var15", varOList.get(i).getString("CUST_COL1")); // 15
			vpd.put("var16", varOList.get(i).getString("CUST_COL2")); // 16
			vpd.put("var17", varOList.get(i).getString("CUST_COL3")); // 17
			vpd.put("var18", varOList.get(i).getString("CUST_COL4")); // 18
			vpd.put("var19", varOList.get(i).getString("CUST_COL5")); // 19
			vpd.put("var20", varOList.get(i).getString("CUST_COL6")); // 20
			vpd.put("var21", varOList.get(i).getString("CUST_COL7")); // 21
			vpd.put("var22", varOList.get(i).getString("CUST_COL8")); // 22
			vpd.put("var23", varOList.get(i).getString("CUST_COL9")); // 23
			vpd.put("var24", varOList.get(i).getString("CUST_COL10")); // 24
			vpd.put("var25", varOList.get(i).getString("ZRZC_CODE")); // 25
			vpd.put("var26", varOList.get(i).getString("BILL_STATE")); // 26
			vpd.put("var27", varOList.get(i).getString("BILL_USER")); // 27
			vpd.put("var28", varOList.get(i).getString("BILL_DATE")); // 28
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
}
