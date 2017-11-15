package com.fh.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fh.entity.system.Department;
import com.fh.entity.system.Dictionaries;
import com.fh.entity.system.User;
import com.fh.service.fhoa.department.DepartmentManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.TmplConfigDictManager;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;
import com.fh.util.StringUtil;
import com.fh.util.Tools;
import com.fh.util.enums.BillState;
import com.fh.util.enums.EmplGroupType;
import com.fh.util.enums.TmplType;

import net.sf.json.JSONArray;

/**
 * 字典信息通用类
 * 
 * @ClassName: DictsUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jiachao
 * @date 2017年5月10日
 *
 */
public class DictsUtil {
	/**
	 * 根据字典父编码获取所有字典详情信息
	 * 
	 * @param parentBianma
	 *            字典父编码
	 * @return
	 * @throws Exception
	 */
	
	public static String Id = "id";
	//机关编码
	public static String DepartShowAll = "01001";
	
	public static List<Dictionaries> getDictsByParentBianma(DictionariesManager dictionariesService,
			String parentBianma) throws Exception {
		List<Dictionaries> listDict = dictionariesService.getSysDictionaries(parentBianma);
		return listDict;
	}

	public static List<Dictionaries> getDictsByParentCode(DictionariesManager dictionariesService, String dicName)
			throws Exception {
		List<Dictionaries> dicList = dictionariesService.getSysDictionaries(dicName);
		return dicList;
	}

	/**
	 * 根据字典名称获取字典信息，生成Jqgrid editOptions和SearchOptions所需的Select格式。
	 * 
	 * @param dicName
	 *            字典名称
	 * @return
	 * @throws Exception
	 */
	public static String getDicValue(DictionariesManager dictionariesService, String dicName) throws Exception {
		StringBuilder ret = new StringBuilder();
		Map<String, String> dicAdd = new HashMap<String, String>();
		List<Dictionaries> dicList = dictionariesService.getSysDictionaries(dicName);
		for (Dictionaries dic : dicList) {
			if (ret != null && !ret.toString().trim().equals("")) {
				ret.append(";");
			}
			ret.append(dic.getDICT_CODE() + ":" + dic.getNAME());
			dicAdd.put(dic.getDICT_CODE(), dic.getNAME());
		}
		return ret.toString();
	}

	/**
	 * 获取组织结构信息，生成Jqgrid editOptions和SearchOptions所需的Select格式。
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static String getDepartmentValue(DepartmentManager departmentService) throws Exception {
		StringBuilder ret = new StringBuilder();
		PageData pd = new PageData();
		List<Department> listPara = (List<Department>) departmentService.getDepartDic(pd);
		for (Department dic : listPara) {
			if (ret != null && !ret.toString().trim().equals("")) {
				ret.append(";");
			}
			ret.append(dic.getDEPARTMENT_CODE() + ":" + dic.getNAME());
		}
		return ret.toString();
	}

	/**
	 * 获取组织结构信息，生成Jqgrid editOptions和SearchOptions所需的Select格式。
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static String getSysUserValue(UserManager userService) throws Exception {
		StringBuilder ret = new StringBuilder();
		PageData pd = new PageData();
		List<PageData> listPara = (List<PageData>) userService.getUserValue(pd);
		for (PageData dic : listPara) {
			if (ret != null && !ret.toString().trim().equals("")) {
				ret.append(";");
			}
			ret.append(StringUtil.toString(dic.get("USER_ID"), "") + ":" + dic.getString("NAME"));
		}
		return ret.toString();
	}

	/**
	 * 获取自定类型信息，生成Jqgrid editOptions和SearchOptions所需的Select格式。
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static String getDicTypeValue(TmplConfigDictManager tmplconfigdictService) throws Exception {
		StringBuilder ret = new StringBuilder();
		PageData pd = new PageData();
		List<PageData> listPara = (List<PageData>) tmplconfigdictService.listAll(pd);
		for (PageData dic : listPara) {
			if (ret != null && !ret.toString().trim().equals("")) {
				ret.append(";");
			}
			ret.append(dic.getString("DICT_CODE") + ":" + dic.getString("DICT_NAME"));
		}
		return ret.toString();
	}

	/**
	 * 获取组织机构树数据源
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static String getDepartmentSelectTreeSource(DepartmentManager departmentService) throws Exception {
		String curUserDepartCode = Jurisdiction.getCurrentDepartmentID();//当前登录人所在二级单位
		User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USERROL);
		String departName = user.getDEPARTMENT_NAME();
		String parentDepartCode="";
		String parentDepartName="";
		String orgCode = Tools.readTxtFile(Const.ORG_CODE); 
		String [] orgInfo=orgCode.split(",");
		
		if(curUserDepartCode.equals(DepartShowAll)){//机关
			parentDepartCode=orgInfo[0];
			parentDepartName=orgInfo[1];
		}
		else{
			parentDepartCode=curUserDepartCode;
			parentDepartName=departName;
		}
		List<PageData> zdepartmentPdList = new ArrayList<PageData>();
		PageData pd = new PageData();
		pd.put(DictsUtil.Id, parentDepartCode);
		pd.put("parentId", "");
		pd.put("name",parentDepartName);
		pd.put("icon", "static/images/user.gif");
		zdepartmentPdList.add(pd);
		List<PageData> listResult=departmentService.listAllDepartmentAndSelfToSelect(parentDepartCode,zdepartmentPdList);
		if(zdepartmentPdList.size()==1) return "0";
		JSONArray arr = JSONArray.fromObject(listResult);
		return (null == arr ? "" : arr.toString());
	}

	/**
	 * 获取组织机构树数据源
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static String getDepartmentSelectTreeSource(DepartmentManager departmentService, String curUserDepartCode) throws Exception {
		String departName = curUserDepartCode;
		String parentDepartCode="";
		String parentDepartName="";
		String orgCode = Tools.readTxtFile(Const.ORG_CODE); 
		String [] orgInfo=orgCode.split(",");
		
		if(curUserDepartCode.equals(DepartShowAll)){//机关
			parentDepartCode=orgInfo[0];
			parentDepartName=orgInfo[1];
		}
		else{
			parentDepartCode=curUserDepartCode;
			parentDepartName=departName;
		}
		List<PageData> zdepartmentPdList = new ArrayList<PageData>();
		PageData pd = new PageData();
		pd.put(DictsUtil.Id, parentDepartCode);
		pd.put("parentId", "");
		pd.put("name",parentDepartName);
		pd.put("icon", "static/images/user.gif");
		zdepartmentPdList.add(pd);
		List<PageData> listResult=departmentService.listAllDepartmentAndSelfToSelect(parentDepartCode,zdepartmentPdList);
		if(zdepartmentPdList.size()==1) return "0";
		JSONArray arr = JSONArray.fromObject(listResult);
		return (null == arr ? "" : arr.toString());
	}

	/**
	 * 获取组织机构树数据源
	 * 
	 * @param departmentService
	 * @return
	 * @throws Exception
	 */
	public static List<PageData> getDepartmentSelectTreeSourceList(DepartmentManager departmentService)
			throws Exception {
		List<PageData> zdepartmentPdList = new ArrayList<PageData>();
		return departmentService.listAllDepartmentToSelect("0", zdepartmentPdList);
	}

	/**
	 * 根据模板基本类型获取员工组编码
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static String getEmplGroupType(String tmplCode) throws Exception {
		String emplGroupType = "";
		if (tmplCode.equals(TmplType.TB_STAFF_DETAIL_CONTRACT.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_SUMMY_CONTRACT.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_AUDIT_CONTRACT.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_TRANSFER_CONTRACT.getNameKey())) {
			emplGroupType = EmplGroupType.HTH.getNameKey();
		} else if (tmplCode.equals(TmplType.TB_STAFF_DETAIL_MARKET.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_SUMMY_MARKET.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_AUDIT_MARKET.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_TRANSFER_MARKET.getNameKey())) {
			emplGroupType = EmplGroupType.SCH.getNameKey();
		} else if (tmplCode.equals(TmplType.TB_STAFF_DETAIL_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_SUMMY_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_AUDIT_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_TRANSFER_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.LWPQ.getNameKey();
		} else if (tmplCode.equals(TmplType.TB_STAFF_DETAIL_SYS_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_SUMMY_SYS_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_AUDIT_SYS_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_TRANSFER_SYS_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.XTNLW.getNameKey();
		} else if (tmplCode.equals(TmplType.TB_STAFF_DETAIL_OPER_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_SUMMY_OPER_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_AUDIT_OPER_LABOR.getNameKey())
				|| tmplCode.equals(TmplType.TB_STAFF_TRANSFER_OPER_LABOR.getNameKey())) {
			emplGroupType = EmplGroupType.YXRY.getNameKey();
		}
		return emplGroupType;
	}

	/**
	 * 根据模板基本表名称获取对应的实际数据库表名称
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static String getActualTable(String tableCodeTmpl) throws Exception {
		String tableCodeOri = "";// 数据库真实业务数据表
		if (tableCodeTmpl.startsWith("TB_STAFF_DETAIL")) {
			tableCodeOri = "TB_STAFF_DETAIL";
		} else if (tableCodeTmpl.startsWith("TB_STAFF_SUMMY")) {
			tableCodeOri = "TB_STAFF_SUMMY";
		} else if (tableCodeTmpl.startsWith("TB_STAFF_AUDIT")) {
			tableCodeOri = "TB_STAFF_AUDIT";
		} else if (tableCodeTmpl.startsWith("TB_STAFF_TRANSFER")) {
			tableCodeOri = "TB_STAFF_SUMMY";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_DETAIL")) {
			tableCodeOri = "TB_SOCIAL_INC_DETAIL";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_SUMMY")) {
			tableCodeOri = "TB_SOCIAL_INC_SUMMY";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_AUDIT")) {
			tableCodeOri = "TB_SOCIAL_INC_AUDIT";
		} else if (tableCodeTmpl.equals("TB_SOCIAL_INC_TRANSFER")) {
			tableCodeOri = "TB_SOCIAL_INC_SUMMY";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_DETAIL")) {
			tableCodeOri = "TB_HOUSE_FUND_DETAIL";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_SUMMY")) {
			tableCodeOri = "TB_HOUSE_FUND_SUMMY";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_AUDIT")) {
			tableCodeOri = "TB_HOUSE_FUND_AUDIT";
		} else if (tableCodeTmpl.equals("TB_HOUSE_FUND_TRANSFER")) {
			tableCodeOri = "TB_HOUSE_FUND_SUMMY";
		}
		return tableCodeOri;
	}
}
