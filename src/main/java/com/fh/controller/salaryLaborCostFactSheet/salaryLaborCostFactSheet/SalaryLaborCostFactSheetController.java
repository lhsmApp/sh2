package com.fh.controller.salaryLaborCostFactSheet.salaryLaborCostFactSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.AddCostFactSheetItem;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.QueryFeildString;
import com.fh.entity.ClsCostFactSheet;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.salaryLaborCostFactSheet.salaryLaborCostFactSheet.SalaryLaborCostFactSheetManager;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelSalaryLaborCostFactSheet;
import com.fh.util.PageData;
import com.fh.util.enums.BindingType;

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
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("BUSI_DATE", "CUST_COL7");
	
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
		
		List<ClsCostFactSheet> setList = getShowList(getPd, page, false);
		String jsonString = JSON.toJSONString(setList); 
        JSONArray array = JSONArray.fromObject(jsonString);  
        List<PageData> varList = (List<PageData>) JSONArray.toCollection(array,PageData.class);
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setPage(page.getPage());
		
		return result;
	}
	
	private List<ClsCostFactSheet> getShowList(PageData getPd, JqPage page, Boolean bolIfExport) throws Exception{
		//账套
		//String SelectedCustCol7 = getPd.getString("SelectedCustCol7");
		//业务区间
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");

		PageData getQueryFeildPd = new PageData();
		getQueryFeildPd.put("BUSI_DATE", SelectedBusiDate);
		String QueryFeildTotal = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SelectedBusiDate != null && !SelectedBusiDate.trim().equals(""))){
			QueryFeildTotal += " and 1 != 1 ";
		}
		getPd.put("QueryFeild", QueryFeildTotal);
		page.setPd(getPd);
		List<PageData> getTotalList = salaryLaborCostFactSheetService.getRptTotalList(page);	//列出Betting列表
		
		//getQueryFeildPd.put("BILL_OFF", SelectedCustCol7);
		String QueryFeildDetail = QueryFeildString.getQueryFeild(getQueryFeildPd, QueryFeildList);
		if(!(SelectedBusiDate != null && !SelectedBusiDate.trim().equals(""))){
			QueryFeildDetail += " and 1 != 1 ";
		}
		//if(!(SelectedCustCol7 != null && !SelectedCustCol7.trim().equals(""))){
		//	QueryFeildDetail += " and 1 != 1 ";
		//}
		getPd.put("QueryFeild", QueryFeildDetail);
		page.setPd(getPd);
		List<PageData> getDetailList = salaryLaborCostFactSheetService.getRptDetailList(page);	//列出Betting列表
		
		List<ClsCostFactSheet> setList = new ArrayList<ClsCostFactSheet>();
		AddCostFactSheetItem.initStructure(setList, bolIfExport);
		
		for(ClsCostFactSheet cost : setList){
			if(cost.getOrder()!=null && !cost.getOrder().trim().equals("")){
				for(PageData pdTotal : getTotalList){
					if(cost.getOrder().equals(pdTotal.getString("dipsorder"))){
						if(cost.getOrder().equals(BindingType.Total0.getNameKey())){
							//cost.setName05(pdTotal.get("").toString());//总额合计
							cost.setName06(pdTotal.get("calgz").toString());//工资
							cost.setName07(pdTotal.get("HOUSE_ALLE").toString());//无房补贴
							cost.setName08(pdTotal.get("TRF_ALLE").toString());//交通补贴
							cost.setName09(pdTotal.get("TEL_EXPE").toString());//通讯补贴
							cost.setName10(pdTotal.get("HLDY_ALLE").toString());//节日补贴
							cost.setName11(pdTotal.get("MEAL_EXPE").toString());//误餐补贴
							cost.setName12(pdTotal.get("ITEM_ALLE").toString());//项目补贴
							//cost.setName13(pdTotal.get("").toString());//
							cost.setName14(pdTotal.get("KID_ALLE").toString());//儿贴
							cost.setName15(pdTotal.get("COOL_EXPE").toString());//防暑降温费
							//cost.setName16("");//疗养费
							cost.setName17(pdTotal.get("EXT_SGL_AWAD").toString());//总额外单项奖
							//cost.setName18("");//单独制表
							//cost.setName19("");//期末人数
						}
						if(cost.getOrder().equals(BindingType.Total44.getNameKey())){
							//cost.setName05(pdTotal.get("").toString().toString());//总额合计
							cost.setName06(pdTotal.get("calgz").toString());//工资
							cost.setName07(pdTotal.get("HOUSE_ALLE").toString());//无房补贴
							cost.setName08(pdTotal.get("TRF_ALLE").toString());//交通补贴
							cost.setName09(pdTotal.get("TEL_EXPE").toString());//通讯补贴
							cost.setName10(pdTotal.get("HLDY_ALLE").toString());//节日补贴
							cost.setName11(pdTotal.get("MEAL_EXPE").toString());//误餐补贴
							cost.setName12(pdTotal.get("ITEM_ALLE").toString());//项目补贴
							//cost.setName13(pdTotal.get("").toString());//
							//cost.setName14(pdTotal.get("").toString());//
							//cost.setName15(pdTotal.get("").toString());//
							//cost.setName16(pdTotal.get("").toString());//
							//cost.setName17(pdTotal.get("").toString());//
							//cost.setName18("");//单独制表
							//cost.setName19("");//期末人数
						}
					}
				}
				for(PageData pdDetail : getDetailList){
					if(cost.getOrder().equals(pdDetail.getString("dipsorder"))){
						if(cost.getOrder().equals(BindingType.DetailSCHHTH.getNameKey())){
							//cost.setName05(pdDetail.get("").toString());//总额合计
							cost.setName06(pdDetail.get("calgz").toString());//工资
							cost.setName07(pdDetail.get("HOUSE_ALLE").toString());//无房补贴
							cost.setName08(pdDetail.get("TRF_ALLE").toString());//交通补贴
							cost.setName09(pdDetail.get("TEL_EXPE").toString());//通讯补贴
							cost.setName10(pdDetail.get("HLDY_ALLE").toString());//节日补贴
							cost.setName11(pdDetail.get("MEAL_EXPE").toString());//误餐补贴
							cost.setName12(pdDetail.get("ITEM_ALLE").toString());//项目补贴
							//cost.setName13("");//
							cost.setName14(pdDetail.get("KID_ALLE").toString());//儿贴
							cost.setName15(pdDetail.get("COOL_EXPE").toString());//防暑降温费
							//cost.setName16("");//疗养费
							cost.setName17(pdDetail.get("EXT_SGL_AWAD").toString());//总额外单项奖
							//cost.setName18("");//单独制表
							//cost.setName19("");//期末人数
						}
						if(cost.getOrder().equals(BindingType.DetailXTNLW.getNameKey())){
							//cost.setName05(pdDetail.get("").toString());//总额合计
							cost.setName06(pdDetail.get("calgz").toString());//工资
							cost.setName07(pdDetail.get("HOUSE_ALLE").toString());//无房补贴
							cost.setName08(pdDetail.get("TRF_ALLE").toString());//交通补贴
							cost.setName09(pdDetail.get("TEL_EXPE").toString());//通讯补贴
							cost.setName10(pdDetail.get("HLDY_ALLE").toString());//节日补贴
							cost.setName11(pdDetail.get("MEAL_EXPE").toString());//误餐补贴
							cost.setName12(pdDetail.get("ITEM_ALLE").toString());//项目补贴
							//cost.setName13("");//
							cost.setName14(pdDetail.get("KID_ALLE").toString());//儿贴
							cost.setName15(pdDetail.get("COOL_EXPE").toString());//防暑降温费
							//cost.setName16(pdDetail.get("").toString());//管理费
							//cost.setName17(pdDetail.get("").toString());//可抵税费
							//cost.setName18("");//单独制表
							//cost.setName19("");//期末人数
						}
						if(cost.getOrder().equals(BindingType.DetailLWPQ.getNameKey())){
							//cost.setName05(pdDetail.get("").toString());//总额合计
							cost.setName06(pdDetail.get("calgz").toString());//工资
							cost.setName07(pdDetail.get("TRF_ALLE").toString());//交通补贴
							cost.setName08(pdDetail.get("TEL_EXPE").toString());//通讯补贴
							cost.setName09(pdDetail.get("HLDY_ALLE").toString());//节日补贴
							cost.setName10(pdDetail.get("MEAL_EXPE").toString());//误餐补贴
							cost.setName11(pdDetail.get("CUST_COL1").toString());//劳保
							cost.setName12(pdDetail.get("COOL_EXPE").toString());//防暑降温费
							cost.setName13(pdDetail.get("Ins").toString());//社保公积金
							cost.setName14(pdDetail.get("AFTER_TAX").toString());//税后加项
							cost.setName15(pdDetail.get("CUST_COL13").toString());//工会经费
							//cost.setName16(pdDetail.get("").toString());//管理费
							//cost.setName17(pdDetail.get("").toString());//可抵税费
							//cost.setName18("");//单独制表
							//cost.setName19("");//期末人数
						}
					}
				}
			}
		}
		return setList;
	}
	
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出SocialIncDetail到excel");

		PageData getPd = this.getPageData();
		//业务区间
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");

		List<ClsCostFactSheet> setList = getShowList(getPd, page, true);
		String jsonString = JSON.toJSONString(setList); 
        JSONArray array = JSONArray.fromObject(jsonString);  
        List<PageData> varList = (List<PageData>) JSONArray.toCollection(array,PageData.class);

		ModelAndView mv = new ModelAndView();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		String fileName = SelectedBusiDate.substring(0,4) + "年" + SelectedBusiDate.substring(4, 6) + "月工资、劳务费情况表";
		dataMap.put("filename", fileName);
		
		dataMap.put("setList", varList);
		ObjectExcelSalaryLaborCostFactSheet erv = new ObjectExcelSalaryLaborCostFactSheet();
		mv = new ModelAndView(erv,dataMap); 
		return mv;
	}
	
}
