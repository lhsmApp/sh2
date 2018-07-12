package com.fh.controller.syslogrec.syslogrec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.fh.controller.base.BaseController;
import com.fh.controller.common.DictsUtil;
import com.fh.controller.common.QueryFeildString;
import com.fh.entity.JqPage;
import com.fh.entity.Page;
import com.fh.entity.PageResult;
import com.fh.util.PageData;
import com.fh.util.SqlTools;
import com.fh.util.DateUtil;
import com.fh.util.Jurisdiction;
import com.fh.service.fhoa.department.DepartmentManager;
import com.fh.service.syslogrec.syslogrec.SysLogRecManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.system.user.UserManager;

/** 
 * 说明：  日志
 * 创建人：zhangxiaoliu
 * 创建时间：2018-06-25
 * @version
 */
@Controller
@RequestMapping(value="/syslogrec")
public class SysLogRecController extends BaseController {
	
	String menuUrl = "syslogrec/list.do"; //菜单地址(权限用)
	@Resource(name="syslogrecService")
	private SysLogRecManager syslogrecService;

	@Resource(name = "dictionariesService")
	private DictionariesManager dictionariesService;
	@Resource(name = "departmentService")
	private DepartmentManager departmentService;
	@Resource(name = "userService")
	private UserManager userService;
	
	//界面查询字段
    List<String> QueryFeildList = Arrays.asList("TYPE_CODE", "BILL_OFF");
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表SysLogRec");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("syslogrec/syslogrec/syslogrec_list");
		PageData getPd = this.getPageData();
		
		//当前期间,取自tb_system_config的SystemDateTime字段
		String strDateTime = DateUtil.getDay();
		mv.addObject("CurryDateTime", strDateTime);
		
		//TYPE_CODE PZTYPE 凭证字典
		mv.addObject("PZTYPE", DictsUtil.getDictsByParentCode(dictionariesService, "PZTYPE"));
		String typeCodeValus = DictsUtil.getDicValue(dictionariesService, "PZTYPE");
		String typeCodeStringAll = ":[All];" + typeCodeValus;
		String typeCodeStringSelect = ":;" + typeCodeValus;
		mv.addObject("typeCodeStrAll", typeCodeStringAll);
		mv.addObject("typeCodeStrSelect", typeCodeStringSelect);

		// *********************加载单位树  DEPT_CODE*******************************
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService, DictsUtil.DepartShowAll);
		mv.addObject("zTreeNodes", DepartmentSelectTreeSource);
		// ***********************************************************
		String departmentValus = DictsUtil.getDepartmentValue(departmentService);
		String departmentStringAll = ":[All];" + departmentValus;
		String departmentStringSelect = ":;" + departmentValus;
		mv.addObject("departmentStrAll", departmentStringAll);
		mv.addObject("departmentStrSelect", departmentStringSelect);

		String userCodeValus = DictsUtil.getSysUserValue(userService);
		String userCodeStringAll = ":[All];" + userCodeValus;
		String userCodeStringSelect = ":;" + userCodeValus;
		mv.addObject("userCodeStrAll", userCodeStringAll);
		mv.addObject("userCodeStrSelect", userCodeStringSelect);
		
		mv.addObject("pd", getPd);
		return mv;
	}

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getPageList")
	public @ResponseBody PageResult<PageData> getPageList(JqPage page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表");

		PageData getPd = this.getPageData();
		//日期
		String SelectedBusiDate = getPd.getString("SelectedBusiDate");
		//凭证字典
		String SelectedTypeCode = getPd.getString("SelectedTypeCode");
		//账套
		String SelectedDepartCode = getPd.getString("SelectedDepartCode");

		String QueryFeild = " and TYPE_CODE = '" + SelectedTypeCode + "' ";
		if(SelectedDepartCode!=null && !SelectedDepartCode.trim().equals("")){
			QueryFeild += " and DEPT_CODE in (" + QueryFeildString.getSqlInString(SelectedDepartCode) + ") ";
		}
		QueryFeild += " and (REC_DATE like '" + SelectedBusiDate + "%' or REC_DATE like '" + SelectedBusiDate.replace("-", "") + "%') ";
		getPd.put("QueryFeild", QueryFeild);
		
		//多条件过滤条件
		String filters = getPd.getString("filters");
		if(null != filters && !"".equals(filters)){
			getPd.put("filterWhereResult", SqlTools.constructWhere(filters,null));
		}
		page.setPd(getPd);
		List<PageData> varList = syslogrecService.JqPage(page);	//列出Betting列表
		int records = syslogrecService.countJqGridExtend(page);
		
		PageResult<PageData> result = new PageResult<PageData>();
		result.setRows(varList);
		result.setRowNum(page.getRowNum());
		result.setRecords(records);
		result.setPage(page.getPage());
		
		return result;
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
