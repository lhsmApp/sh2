package com.fh.controller.salaryLaborCostFactSheet.salaryLaborCostFactSheet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.AddCostFactSheetItem;
import com.fh.controller.common.DictsUtil;
import com.fh.entity.ClsCostFactSheet;
import com.fh.entity.JqGridModel;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.salaryLaborCostFactSheet.salaryLaborCostFactSheet.SalaryLaborCostFactSheetManager;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;
import com.fh.util.SqlTools;

import net.sf.json.JSONArray;

/**
 * 
 * 
 * @ClassName: SalaryLaborCostFactSheetController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 
 *
 */
@Controller
@RequestMapping(value = "/salaryLaborCostFactSheet")
public class SalaryLaborCostFactSheetController extends BaseController {

	String menuUrl = "salaryLaborCostFactSheet/list.do"; // 菜单地址(权限用)
	@Resource(name = "salaryLaborCostFactSheetService")
	private SalaryLaborCostFactSheetManager salaryLaborCostFactSheetService;

	@Resource(name = "sysconfigService")
	private SysConfigManager sysConfigManager;
	
	@Resource(name = "dictionariesService")
	private DictionariesManager dictionariesService;
	
	/**
	 * 列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		PageData getPd = this.getPageData();
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("salaryLaborCostFactSheet/salaryLaborCostFactSheet/salaryLaborCostFactSheet_list");

		//当前期间,取自tb_system_config的SystemDateTime字段
		String SystemDateTime = sysConfigManager.currentSection(getPd);
		mv.addObject("SystemDateTime", SystemDateTime);

		//BILL_OFF FMISACC 帐套字典
		mv.addObject("FMISACC", DictsUtil.getDictsByParentCode(dictionariesService, "FMISACC"));
		
		mv.addObject("pd", getPd);
		return mv;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername()+"列表SalaryLaborCostFactSheet");

		PageData getPd = this.getPageData();
		//账套
		//String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//业务区间
		//String SelectedBusiDate = getPd.getString("SelectedBusiDate");

		/*PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("TYPE_CODE", SelectedTypeCode);
		getQueryFeildPd.put("BILL_OFF", SelectedCustCol7);
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeild = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SelectedBusiDate != null && !SelectedBusiDate.trim().equals(""))){
			QueryFeild += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeild);*/
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		//页面显示数据的年月
		//getPd.put("SystemDateTime", SelectedBusiDate);
		page.setPd(getPd);
		//List<PageData> getList = salaryLaborCostFactSheetService.JqPage(page);	//列出Betting列表
		
		List<ClsCostFactSheet> setList = new ArrayList<ClsCostFactSheet>();
		AddCostFactSheetItem.initStructure(setList);
		
		String jsonString = JSON.toJSONString(setList); 
        JSONArray array = JSONArray.fromObject(jsonString);  
        List<PageData> varList = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setPage(page.getPage());
		
		return result;
	}
	
}
