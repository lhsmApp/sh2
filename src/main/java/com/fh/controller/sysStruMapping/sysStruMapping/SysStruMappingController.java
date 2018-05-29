package com.fh.controller.sysStruMapping.sysStruMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.QueryFeildString;
import com.fh.controller.common.SysStruMappingList;
import com.fh.entity.CommonBase;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.entity.SysStruMapping;
import com.fh.entity.SysTableMapping;
import com.fh.entity.TableColumns;
import com.fh.entity.system.Dictionaries;
import com.fh.service.fhoa.department.DepartmentManager;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;
import com.fh.service.sysStruMapping.sysStruMapping.impl.SysStruMappingService;
import com.fh.service.sysTableMapping.sysTableMapping.impl.SysTableMappingService;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;
import com.fh.util.SqlTools;

import net.sf.json.JSONArray;

/**
 * 数据模板详情
 * 
 * @ClassName: SysStruMappingController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 2018年5月16日
 *
 */
@Controller
@RequestMapping(value = "/sysStruMapping")
public class SysStruMappingController extends BaseController {

	String menuUrl = "sysStruMapping/list.do"; // 菜单地址(权限用)
	@Resource(name = "sysStruMappingService")
	private SysStruMappingService sysStruMappingService;
	@Resource(name = "sysTableMappingService")
	private SysTableMappingService sysTableMappingService;
	
	@Resource(name = "sysconfigService")
	private SysConfigManager sysConfigManager;
	@Resource(name="tmplconfigService")
	private TmplConfigService tmplconfigService;

	@Resource(name = "departmentService")
	private DepartmentManager departmentService;
	@Resource(name = "dictionariesService")
	private DictionariesManager dictionariesService;

	//页面显示数据的年月
	String ssSystemDateTime = "";

	//表
	String TB_GEN_BUS_SUMMY_BILL = "TB_GEN_BUS_SUMMY_BILL";
	String TB_GEN_SUMMY = "TB_GEN_SUMMY";
	String TB_GEN_BUS_DETAIL = "TB_GEN_BUS_DETAIL";
	String BILL_STATE = "BILL_STATE";
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("TYPE_CODE", "BILL_OFF", "BUSI_DATE");

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		PageData getPd = this.getPageData();
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("sysStruMapping/sysStruMapping/sysStruMapping_list");
		//当前期间,取自tb_system_config的SystemDateTime字段
		ssSystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", ssSystemDateTime);
		
		//BILL_OFF FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));

		//TYPE_CODE PZTYPE 凭证字典
		mv.addObject("PZTYPE", DictsUtil.getDictsByParentCode(dictionariesService, "PZTYPE"));

		//TYPE_CODE MappingTable 对应表
		List<Dictionaries> listTableName = new ArrayList<Dictionaries>();
		listTableName.add(new Dictionaries("TB_GEN_BUS_DETAIL", "TB_GEN_BUS_DETAIL"));
		listTableName.add(new Dictionaries("TB_GEN_SUMMY", "TB_GEN_SUMMY"));
		listTableName.add(new Dictionaries("TB_GEN_BUS_SUMMY_BILL", "TB_GEN_BUS_SUMMY_BILL"));
		mv.addObject("MappingTable", listTableName);

		String billOffValus = DictsUtil.getDicValue(dictionariesService, "FMISACC");
		String billOffStringAll = ":[All];" + billOffValus;
		String billOffStringSelect = ":;" + billOffValus;
		mv.addObject("billOffStrAll", billOffStringAll);
		mv.addObject("billOffStrSelect", billOffStringSelect);

		//TYPE_CODE PZTYPE 凭证字典
		mv.addObject("PZTYPE", DictsUtil.getDictsByParentCode(dictionariesService, "PZTYPE"));
		String typeCodeValus = DictsUtil.getDicValue(dictionariesService, "PZTYPE");
		String typeCodeStringAll = ":[All];" + typeCodeValus;
		String typeCodeStringSelect = ":;" + typeCodeValus;
		mv.addObject("typeCodeStrAll", typeCodeStringAll);
		mv.addObject("typeCodeStrSelect", typeCodeStringSelect);

		mv.addObject("pd", getPd);
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
		PageData getPd = this.getPageData();
		//账套
		String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//业务区间
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		//
		String SelectedStruMappingTableName = getPd.getString("SelectedStruMappingTableName");
		
		List<PageData> varList = new ArrayList<PageData>();
		List<SysTableMapping> getSysTableMappingList = SysStruMappingList.getUseTableMapping(SelectedTypeCode, SelectedBusiDate, SelectedCustCol7, TB_GEN_BUS_DETAIL, sysTableMappingService);
		if(getSysTableMappingList != null && getSysTableMappingList.size() == 1){
			// 用语句查询出数据库表的所有字段及其属性；拼接成jqgrid全部列
			List<TableColumns> tableColumns = tmplconfigService.getTableColumns(SelectedStruMappingTableName);
			String struTableNameTranfer = "";
			if(SelectedStruMappingTableName.toUpperCase().equals(TB_GEN_BUS_DETAIL.toUpperCase())){
				struTableNameTranfer = getSysTableMappingList.get(0).getTABLE_NAME();
			}
			if(SelectedStruMappingTableName.toUpperCase().equals(TB_GEN_SUMMY.toUpperCase())){
				struTableNameTranfer = TB_GEN_BUS_DETAIL;
			}
			if(SelectedStruMappingTableName.toUpperCase().equals(TB_GEN_BUS_SUMMY_BILL.toUpperCase())){
				struTableNameTranfer = TB_GEN_SUMMY;
			}
			getPd.put("SelectedStruMappingTableName", SelectedStruMappingTableName);
			getPd.put("SelectedTableNameTranfer", struTableNameTranfer);
			page.setPd(getPd);
			List<PageData> struList = sysStruMappingService.JqPage(page);	//列出Betting列表
			if (struList != null && struList.size() > 0) {
				for (PageData struEach : struList) {
					struEach.put("", "");
					String str = struEach.getString("");
				}
			}
			
			
			
			
			// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
			List<SysStruMapping> getSysStruMappingList = SysStruMappingList.getSysStruMappingList(SelectedTypeCode, struTableNameTranfer, SelectedStruMappingTableName, SelectedBusiDate, SelectedCustCol7, sysStruMappingService, false);
			// 添加配置表设置列，字典（未设置就使用表默认，text或number）、隐藏、表头显示
			if (getSysStruMappingList != null && getSysStruMappingList.size() > 0) {
				for (int i = 0; i < getSysStruMappingList.size(); i++) {
					
				}
			}
		}

		
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setPage(page.getPage());
		
		
		
		
		//PageData tpd = sysStruMappingService.findTableCodeByTableNo(pd);
		//String tmplTableCode=tpd.getString("TABLE_CODE");
		//pd.put("TABLE_CODE",tmplTableCode );
		//String filters = pd.getString("filters"); // 多条件过滤条件
		//if (null != filters && !"".equals(filters)) {
		//	pd.put("filterWhereResult", SqlTools.constructWhere(filters, null));
		//}
		//page.setPd(pd);
		//List<PageData> varList = sysStruMappingService.listAll(pd);
		//PageResult<PageData> result = new PageResult<PageData>();
		/*
		 * if (varList.size() != 0) { result.setRows(varList); } else {
		 * List<PageData> temporaryList = sysStruMappingService.temporaryList(page);
		 * result.setRows(temporaryList); }
		 */
		//String tableCodeOri = DictsUtil.getActualTable(tmplTableCode);// 数据库真实业务数据表
		//pd.put("TABLE_CODE", tableCodeOri);
		/*List<PageData> temporaryList = sysStruMappingService.temporaryList(page);
		if (varList!=null&&varList.size() != 0) {
			List<PageData> plusList = new ArrayList<PageData>();
			for (PageData temp : temporaryList) {
				boolean plus=true;
				for (PageData item : varList) {
					if (temp.getString("COL_CODE").equals(item.getString("COL_CODE"))) {
						plus=false;
						break;
					}
				}
				if(plus){
					temp.put("TABLE_CODE", tmplTableCode);
					plusList.add(temp);
					//temp.put("TABLE_CODE", item.get("TABLE_CODE"));
				}
			}
			for (PageData plusItem : plusList) {
				varList.add(plusItem);
			}
			result.setRows(varList);
		}else{
			for (PageData temp : temporaryList) {
				temp.put("TABLE_CODE", tmplTableCode);
				result.setRows(temporaryList);
			}
		}*/

		/*if (varList.size() != 0) {
			for (PageData temp : temporaryList) {
				for (PageData item : varList) {
					if (temp.getString("COL_CODE").equals(item.getString("COL_CODE"))) {
						temp.put("TABLE_CODE", item.get("TABLE_CODE"));
						temp.put("COL_NAME", item.get("COL_NAME"));
						temp.put("DISP_ORDER", item.get("DISP_ORDER"));
						temp.put("DICT_TRANS", item.get("DICT_TRANS"));
						temp.put("COL_HIDE", item.get("COL_HIDE"));
						temp.put("COL_SUM", item.get("COL_SUM"));
						temp.put("COL_AVE", item.get("COL_AVE"));
						temp.put("COL_TRANSFER", item.get("COL_TRANSFER"));
					}
				}
			}
		}
		result.setRows(temporaryList);*/
		return result;
	}
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public @ResponseBody CommonBase save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增SysDeptMapping");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);
		
		PageData pd = this.getPageData();
		String oper = pd.getString("oper");
		if(oper.equals("add")){
		} else {
		}
		
		List<PageData> listData = new ArrayList<PageData>();
		listData.add(pd);
		sysStruMappingService.batchPartDelAndIns(listData);
		commonBase.setCode(0);
		return commonBase;
	}

	/**
	 * 批量修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateAll")
	public @ResponseBody CommonBase updateAll() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "批量");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;}
		CommonBase commonBase = new CommonBase();
		commonBase.setCode(-1);

		PageData getPd = this.getPageData();
		
		Object DATA_ROWS = getPd.get("DataRows");
		String json = DATA_ROWS.toString();  
        JSONArray array = JSONArray.fromObject(json);  
        List<PageData> listData = (List<PageData>) JSONArray.toCollection(array,PageData.class);
        
		if(null != listData && listData.size() > 0){
			sysStruMappingService.batchAllUpdateDatabase(listData);
			commonBase.setCode(0);
		}
		return commonBase;
	}
}
