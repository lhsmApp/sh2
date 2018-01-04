package com.fh.controller.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.entity.system.Department;
import com.fh.entity.system.Dictionaries;
import com.fh.service.fhoa.department.DepartmentManager;
import com.fh.service.system.dictionaries.DictionariesManager;
import com.fh.service.system.user.UserManager;
import com.fh.service.tmplConfigDict.tmplconfigdict.TmplConfigDictManager;
import com.fh.service.tmplconfig.tmplconfig.TmplConfigManager;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Tools;
import com.fh.util.enums.BillState;
import com.fh.util.enums.DurState;
import com.fh.util.enums.StaffDataType;

import net.sf.json.JSONArray;

/**
 * 
* @ClassName: Common
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年7月14日
*
 */
public class Common {
	
	public static StringBuilder GetSqlUserdata(String tableNo, String departCode, String billOff, 
			TmplConfigManager tmplconfigService) throws Exception{
		//底行显示的求和与平均值字段
		StringBuilder m_sqlUserdata = new StringBuilder();
		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		List<TmplConfigDetail> m_columnsList = Common.getShowColumnList(tableCodeTmpl, departCode, billOff, tmplconfigService);
		if (m_columnsList != null && m_columnsList.size() > 0) {
			for (int i = 0; i < m_columnsList.size(); i++) {
				// 底行显示的求和与平均值字段
				// 1汇总 0不汇总,默认0
				if (Integer.parseInt(m_columnsList.get(i).getCOL_SUM()) == 1) {
					if (m_sqlUserdata != null && !m_sqlUserdata.toString().trim().equals("")) {
						m_sqlUserdata.append(", ");
					}
					m_sqlUserdata.append(" sum(" + m_columnsList.get(i).getCOL_CODE() + ") "
							+ m_columnsList.get(i).getCOL_CODE());
				}
				// 0不计算 1计算 默认0
				else if (Integer.parseInt(m_columnsList.get(i).getCOL_AVE()) == 1) {
					if (m_sqlUserdata != null && !m_sqlUserdata.toString().trim().equals("")) {
						m_sqlUserdata.append(", ");
					}
					m_sqlUserdata.append(" round(avg(" + m_columnsList.get(i).getCOL_CODE() + "), 2) "
							+ m_columnsList.get(i).getCOL_CODE());
				}
			}
		}
		return m_sqlUserdata;
	}

	public static Map<String, Object> GetDicList(String tableNo, String departCode, String billOff, 
			TmplConfigManager tmplconfigService,
			TmplConfigDictManager tmplConfigDictService, DictionariesManager dictionariesService, 
			DepartmentManager departmentService,UserManager userService,
			String AdditionalReportColumns) throws Exception{
		Map<String, Object> m_DicList = new LinkedHashMap<String, Object>();
		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		List<TmplConfigDetail> m_columnsList = Common.getShowColumnList(tableCodeTmpl, departCode, billOff,tmplconfigService);
		if (m_columnsList != null && m_columnsList.size() > 0) {
			for (int i = 0; i < m_columnsList.size(); i++) {
				String getDICT_TRANS = m_columnsList.get(i).getDICT_TRANS();
				if (getDICT_TRANS != null && !getDICT_TRANS.trim().equals("") && !m_DicList.containsKey(getDICT_TRANS)) {
				Common.getDicValue(m_DicList, m_columnsList.get(i).getDICT_TRANS(),
						tmplConfigDictService, dictionariesService, 
						departmentService, userService, AdditionalReportColumns);
				}
			}
		}
		return m_DicList;
	}

	public static Map<String, TableColumns> GetHaveColumnsList(String tableNo, TmplConfigManager tmplconfigService) throws Exception{
		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		String tableCodeOri=DictsUtil.getActualTable(tableCodeTmpl);//数据库真实业务数据表
		return GetHaveColumnsListByTableName(tableCodeOri, tmplconfigService);
	}

	public static Map<String, TableColumns> GetHaveColumnsListByTableName(String tableName, TmplConfigManager tmplconfigService) throws Exception{
		Map<String, TableColumns> m_HaveColumnsList = new LinkedHashMap<String, TableColumns>();
		List<TableColumns> tableColumns = tmplconfigService.getTableColumns(tableName);
		for (TableColumns col : tableColumns) {
			//表结构
			m_HaveColumnsList.put(col.getColumn_name(), col);
		}
		return m_HaveColumnsList;
	}
	
	
	private static String getTableCodeTmpl(String tableNo,
			TmplConfigManager tmplconfigService) throws Exception{
		PageData pd=new PageData();
		pd.put("TABLE_NO", tableNo);
		PageData pdResult=tmplconfigService.findTableCodeByTableNo(pd);
		String tableCodeTmpl=pdResult.getString("TABLE_CODE");
		return tableCodeTmpl;
	}
	public static Map<String, TmplConfigDetail> GetSetColumnsList(String tableNo, String departCode, String billOff,
			TmplConfigManager tmplconfigService) throws Exception{
		Map<String, TmplConfigDetail> m_SetColumnsList = new LinkedHashMap<String, TmplConfigDetail>();
		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		List<TmplConfigDetail> m_columnsList = Common.getShowColumnList(tableCodeTmpl, departCode, billOff,tmplconfigService);
		if (m_columnsList != null && m_columnsList.size() > 0) {
			for (int i = 0; i < m_columnsList.size(); i++) {
				String getCOL_CODE = m_columnsList.get(i).getCOL_CODE();
				m_SetColumnsList.put(getCOL_CODE, m_columnsList.get(i));
			}
		}
		return m_SetColumnsList;
	}

	public static String GetRetSelectColoumns(Map<String, TableColumns> haveColumnsList,
			String tableNo, String tableNameBackup, String departCode, String billOff, 
			//String TaxCanNotHaveFormulaFeildList, 
			String keyExtra, List<String> keyListBase,
			TmplConfigManager tmplconfigService) throws Exception{
		String strRetSelectColoumn ="";

		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		List<TmplConfigDetail> setColumnsList = Common.getShowColumnList(tableCodeTmpl, departCode, billOff,tmplconfigService);
		
		if(haveColumnsList!=null && haveColumnsList.size()>0 && setColumnsList != null && setColumnsList.size() > 0){
			List<String> listInitColumns = new ArrayList<String>();
			//
			for (TableColumns col : haveColumnsList.values()) {
		    	String column_name = col.getColumn_name().toUpperCase();
		    	listInitColumns.add(column_name);
			}
			int MinCAL_ORDER = 0;
			int MaxCAL_ORDER = 0;
			for (int i = 0; i < setColumnsList.size(); i++) {
				String getCOL_FORMULA = setColumnsList.get(i).getCOL_FORMULA();
				String getCOL_CODE = setColumnsList.get(i).getCOL_CODE().toUpperCase();
				if(getCOL_FORMULA!=null && !getCOL_FORMULA.trim().equals("")){
					listInitColumns.remove(getCOL_CODE);
				}
				int getCAL_ORDER = setColumnsList.get(i).getCAL_ORDER();
				if(MinCAL_ORDER > getCAL_ORDER) MinCAL_ORDER = getCAL_ORDER;
				if(MaxCAL_ORDER < getCAL_ORDER) MaxCAL_ORDER = getCAL_ORDER;
			}
			String strColsFormulaNull = "";
			//if(TaxCanNotHaveFormulaFeildList!=null && !TaxCanNotHaveFormulaFeildList.trim().equals("") && !keyListBase.contains(TaxCanNotHaveFormulaFeildList.trim())){
			//	strColsFormulaNull += TaxCanNotHaveFormulaFeildList + " " + TaxCanNotHaveFormulaFeildList + keyExtra;
			//}
			for(String strCol : listInitColumns){
				if(strColsFormulaNull!=null && !strColsFormulaNull.trim().equals("")){
					strColsFormulaNull += ", ";
				}
				strColsFormulaNull += strCol;
			}
			strColsFormulaNull += QueryFeildString.getFieldSelectKey(keyListBase, keyExtra);
			strRetSelectColoumn = " select " + strColsFormulaNull + " from " + tableNameBackup;
			
			for(int order=MinCAL_ORDER; order<=MaxCAL_ORDER; order++){
				String strColsFormulaOrder = "";
				for (int i = 0; i < setColumnsList.size(); i++) {
					int getCAL_ORDER = setColumnsList.get(i).getCAL_ORDER();
					String getCOL_FORMULA = setColumnsList.get(i).getCOL_FORMULA();
					String getCOL_CODE = setColumnsList.get(i).getCOL_CODE();
					if(getCAL_ORDER == order && getCOL_FORMULA!=null && !getCOL_FORMULA.trim().equals("")){
						if(strColsFormulaOrder!=null && !strColsFormulaOrder.trim().equals("")){
							strColsFormulaOrder += ", ";
						}
						strColsFormulaOrder += getCOL_FORMULA + " " + getCOL_CODE;
					}
				}
				if(strColsFormulaOrder!=null && !strColsFormulaOrder.trim().equals("")){
					strRetSelectColoumn = " select *, " + strColsFormulaOrder + " from (" + strRetSelectColoumn + ") t ";
				}
			}
		}
		return strRetSelectColoumn;
	}
	
	public static String GetRetSelectNotCalculationColoumns(String tableName, 
			String TableFeildTax, String keyExtra, List<String> keyListBase,
			TmplConfigManager tmplconfigService) throws Exception{
		String strRetSelectBonusColoumn = " select * ";
		if(keyListBase==null || (keyListBase!=null && !keyListBase.contains(TableFeildTax))){
			strRetSelectBonusColoumn += ", " + TableFeildTax + " " + TableFeildTax + keyExtra;
		}
		strRetSelectBonusColoumn += QueryFeildString.getFieldSelectKey(keyListBase, keyExtra);
		strRetSelectBonusColoumn += " from " + tableName;
		return strRetSelectBonusColoumn;
	}
	
	public static List<String> GetSalaryFeildUpdate(String tableNo, String tableNameBackup, String departCode, String billOff, 
			TmplConfigManager tmplconfigService) throws Exception{
		List<String> listSalaryFeildCal = new ArrayList<String>();

		String tableCodeTmpl = getTableCodeTmpl(tableNo, tmplconfigService);
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		List<TmplConfigDetail> setColumnsList = Common.getShowColumnList(tableCodeTmpl, departCode, billOff,tmplconfigService);
		
		if(setColumnsList != null && setColumnsList.size() > 0){
			int MinCAL_ORDER = 0;
			int MaxCAL_ORDER = 0;
			for (int i = 0; i < setColumnsList.size(); i++) {
				int getCAL_ORDER = setColumnsList.get(i).getCAL_ORDER();
				if(MinCAL_ORDER > getCAL_ORDER) MinCAL_ORDER = getCAL_ORDER;
				if(MaxCAL_ORDER < getCAL_ORDER) MaxCAL_ORDER = getCAL_ORDER;
			}
			
			for(int order=MinCAL_ORDER; order<=MaxCAL_ORDER; order++){
				String strSalaryFeildCal = "";
				for (int i = 0; i < setColumnsList.size(); i++) {
					int getCAL_ORDER = setColumnsList.get(i).getCAL_ORDER();
					String getCOL_FORMULA = setColumnsList.get(i).getCOL_FORMULA();
					String getCOL_CODE = setColumnsList.get(i).getCOL_CODE();
					if(getCAL_ORDER == order && getCOL_FORMULA!=null && !getCOL_FORMULA.trim().equals("")){
						if(strSalaryFeildCal!=null && !strSalaryFeildCal.trim().equals("")){
							strSalaryFeildCal += ", ";
						}
						strSalaryFeildCal += getCOL_CODE + " = " + getCOL_FORMULA;
					}
				}
				if(strSalaryFeildCal!=null && !strSalaryFeildCal.trim().equals("")){
					listSalaryFeildCal.add(" update " + tableNameBackup + " set " + strSalaryFeildCal);
				}
			}
		}
		return listSalaryFeildCal;
	}
	
	public static String GetRetSumByUserColoumns(String tableName, String QueryFeild, 
			String configFormula, String salaryExemptionTax, String TableFeildSumOper,
			String TableFeildTax, String DATA_TYPE, 
			TmplConfigManager tmplconfigService) throws Exception{
		if(!(salaryExemptionTax!=null && !salaryExemptionTax.trim().equals(""))){
			salaryExemptionTax = "0";
		}
		String strRetSelectSalaryColoumn = " select USER_CODE, " 
		        + " sum(" + configFormula + ") - " + salaryExemptionTax + " " + TableFeildSumOper + ", "
				+ " sum(" + TableFeildTax + ") " + TableFeildTax + " "
				+ " from " + tableName 
				+ " where DATA_TYPE = '" + DATA_TYPE + "' "
				+ QueryFeild 
				+ " group by USER_CODE";
		return strRetSelectSalaryColoumn;
	}
	
	/**
	 * 获取显示结构，未设置获取上级单位
	 * 
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public static List<TmplConfigDetail> getShowColumnList(String tableCode, String departCode, String billOff,
			TmplConfigManager tmplconfigService) throws Exception{
		// 前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		TmplConfigDetail item = new TmplConfigDetail();
		item.setDEPT_CODE(departCode);
		item.setTABLE_CODE(tableCode);
		item.setBILL_OFF(billOff);
		List<TmplConfigDetail> m_columnsList = tmplconfigService.listNeed(item);
		if(m_columnsList.size()==0){
			String rootDeptCode=Tools.readTxtFile(Const.ROOT_DEPT_CODE);
			item.setDEPT_CODE(rootDeptCode);
			item.setTABLE_CODE(tableCode);
			m_columnsList = tmplconfigService.listNeed(item);
		}
		return m_columnsList;
	}



	public static String getDicValue(Map<String, Object> m_dicList, String dicName,
			TmplConfigDictManager tmplConfigDictService, DictionariesManager dictionariesService, 
			DepartmentManager departmentService,UserManager userService,
			String AdditionalReportColumns) throws Exception {
		StringBuilder ret = new StringBuilder();
		Map<String, String> dicAdd = new LinkedHashMap<String, String>();
		if(AdditionalReportColumns != null 
				&& AdditionalReportColumns.toUpperCase().equals((dicName).toUpperCase())){
			for(DurState dur : DurState.values()){
				ret.append(dur.getNameKey() + ":" + dur.getNameValue());
				ret.append(';');
				dicAdd.put(dur.getNameKey(), dur.getNameValue());
			}
			if (!ret.toString().trim().equals("")) {
				ret.deleteCharAt(ret.length()-1);
			}
		} else {
			String strDicType = tmplConfigDictService.getDicType(dicName);
			if (strDicType.equals("1")) {
				List<Dictionaries> dicList = dictionariesService.getSysDictionaries(dicName);
				for (Dictionaries dic : dicList) {
					if (ret != null && !ret.toString().trim().equals("")) {
						ret.append(";");
					}
					ret.append(dic.getDICT_CODE() + ":" + dic.getNAME());
					dicAdd.put(dic.getDICT_CODE(), dic.getNAME());
				}
			} else if (strDicType.equals("2")) {
				if (dicName.toUpperCase().equals(("oa_department").toUpperCase())) {
					PageData pd = new PageData();
					List<Department> listPara = (List<Department>) departmentService.getDepartDic(pd);
					for (Department dic : listPara) {
						if (ret != null && !ret.toString().trim().equals("")) {
							ret.append(";");
						}
						ret.append(dic.getDEPARTMENT_CODE() + ":" + dic.getNAME());
						dicAdd.put(dic.getDEPARTMENT_CODE(), dic.getNAME());
					}
				}else if (dicName.toUpperCase().equals(("sys_user").toUpperCase())) {
					PageData pd = new PageData();
					List<PageData> listUser = (List<PageData>) userService.getUserValue(pd);
					for (PageData dic : listUser) {
						if (ret != null && !ret.toString().trim().equals("")) {
							ret.append(";");
						}
						ret.append(dic.get("USER_ID").toString() + ":" + dic.getString("NAME"));
						dicAdd.put(dic.get("USER_ID").toString(), dic.getString("NAME"));
					}
				}
			}else if (strDicType.equals("3")) {//枚举
				if (dicName.toUpperCase().equals(("BILL_STATE").toUpperCase())) {
					for(BillState billState:BillState.values()){
						ret.append(billState.getNameKey() + ":" + billState.getNameValue());
						ret.append(';');
						dicAdd.put(billState.getNameKey(), billState.getNameValue());
					}
					if (!ret.toString().trim().equals("")) {
						ret.deleteCharAt(ret.length()-1);
					}
				} else if (dicName.toUpperCase().equals(("DATA_TYPE").toUpperCase())) {
					for(StaffDataType staffDataType:StaffDataType.values()){
						ret.append(staffDataType.getNameKey() + ":" + staffDataType.getNameValue());
						ret.append(';');
						dicAdd.put(staffDataType.getNameKey(), staffDataType.getNameValue());
					}
					if (!ret.toString().trim().equals("")) {
						ret.deleteCharAt(ret.length()-1);
					}
				}
			}
		}
		if (m_dicList!=null && !m_dicList.containsKey(dicName)) {
			m_dicList.put(dicName, dicAdd);
		}
		return ret.toString();
	}
	
	public static int getColumnLength(String Column_type, String Data_type) {
		int ret = 0;
		String[] listLength = Column_type.replace(Data_type, "").replace("(", "").replace(")", "").split(",");
		for (String length : listLength) {
			ret += Integer.parseInt(length);
		}
		return ret;
	}
	
	public static Boolean IsNumFeild(String Data_type){
		Boolean bol = false;
		if(Data_type.trim().equals("DECIMAL") || Data_type.trim().equals("DOUBLE")
		    || Data_type.trim().equals("INT") || Data_type.trim().equals("FLOAT")){
				bol = true;
		}
		return bol;
	}

	public static String getSumFeildSelect(List<String> GroupbyFeild, List<TableColumns> tableDetailColumns, String keyExtra){
		String SelectFeild = "";
		if(GroupbyFeild != null){
			for(String feild : GroupbyFeild){
				if(SelectFeild!=null && !SelectFeild.trim().equals("")){
					SelectFeild += ", ";
				}
				SelectFeild += feild + ", " + feild + " " + feild + keyExtra;
			}
		}
		if(tableDetailColumns != null && tableDetailColumns.size() > 0){
			for(TableColumns col : tableDetailColumns){
				if(Common.IsNumFeild(col.getData_type())){
					String getCOL_CODE = col.getColumn_name();
					if(GroupbyFeild == null || (GroupbyFeild != null && !GroupbyFeild.contains(getCOL_CODE))){
						if(SelectFeild!=null && !SelectFeild.trim().equals("")){
							SelectFeild += ", ";
						}
						SelectFeild += " sum(" + getCOL_CODE +") " + getCOL_CODE;
					}
				}
			}
		}
		return SelectFeild;
	}

	//IsNumFeildButMustInput 设置字段类型是数字，但不管隐藏 或显示都必须保存的
	public static void setModelDefault(PageData pd, Map<String, TableColumns> haveColumnsList, 
			Map<String, TmplConfigDetail> map_SetColumnsList, List<String> IsNumFeildButMustInput)
			throws ClassNotFoundException {
		String InsertField = "";
		String InsertVale = "";
	    for (TableColumns col : haveColumnsList.values()) {
	    	String column_name = col.getColumn_name().toUpperCase();
	    	String data_type = col.getData_type().toUpperCase();
	    	TmplConfigDetail configDetail = map_SetColumnsList.get(column_name);
	    	int intHide = 0;
	    	if(configDetail != null){
				intHide = Integer.parseInt(configDetail.getCOL_HIDE());
	    	}
	    	if(IsNumFeildButMustInput!=null && IsNumFeildButMustInput.contains(column_name)){
	    		intHide = 1;//显示
	    	}
			// 0隐藏 1显示, intHide != 1 隐藏
			if(!(IsNumFeild(data_type) && intHide != 1)){
				Object value = pd.get(column_name);
				if(value != null && value.toString() != null && !value.toString().trim().equals("")){
					if(InsertField!=null && !InsertField.trim().equals("")){
						InsertField += ",";
						InsertVale += ",";
					}
					InsertField += col.getColumn_name();
					InsertVale += "'" + value.toString() + "'";
				}
			}
		}
		pd.put("InsertField", InsertField);
		pd.put("InsertVale", InsertVale);
	}
	
	public static int getDepartSelf(DepartmentManager departmentService) throws Exception{
		int DepartSelf = 0;
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if(DepartmentSelectTreeSource.equals("0"))
		{
			DepartSelf = 1;
		} else {
			DepartSelf = 0;
		}
		return DepartSelf;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getAllDeptCode(DepartmentManager departmentService, String UserDepartCode) throws Exception{
		List<String> AllDeptCode = new ArrayList<String>();
		String DepartmentSelectTreeSource=DictsUtil.getDepartmentSelectTreeSource(departmentService);
		if(DepartmentSelectTreeSource.equals("0"))
		{
			AllDeptCode.add(UserDepartCode);
		} else {
	        JSONArray jsonArray = JSONArray.fromObject(DepartmentSelectTreeSource);  
			List<PageData> listDepart = (List<PageData>) JSONArray.toCollection(jsonArray, PageData.class);
			if(listDepart!=null && listDepart.size()>0){
				for(PageData pdDept : listDepart){
					AllDeptCode.add(pdDept.getString(DictsUtil.Id));
				}
			}
		}
		return AllDeptCode;
	}
}
	