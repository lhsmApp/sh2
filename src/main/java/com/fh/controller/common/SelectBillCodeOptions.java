package com.fh.controller.common;

import java.util.List;

import com.fh.entity.system.Department;
import com.fh.entity.system.Dictionaries;

/**
 * 下拉列表单号数据源
* @ClassName: SelectBillCodeOptions
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年11月21日
*
 */
public class SelectBillCodeOptions {

	//在导入汇总界面使用
	public static String getSelectBillCodeOptions(List<String> listBillCode, String strSelectBillCodeFirstShow,  String strSelectBillCodeLastShow){
		String strReturn = "";
		if(strSelectBillCodeFirstShow!=null && !strSelectBillCodeFirstShow.trim().equals("")){
			strReturn += "<option value='" + strSelectBillCodeFirstShow + "' selected='selected'>" + strSelectBillCodeFirstShow + "</option>";
		}
		if(listBillCode!=null){
			for(String billCode : listBillCode){
				if(billCode!=null && !billCode.trim().equals("")){
					strReturn += "<option value='" + billCode + "'>" + billCode + "</option>";
				}
		    }
		}
		if(strSelectBillCodeLastShow!=null && !strSelectBillCodeLastShow.trim().equals("")){
			strReturn += "<option value='" + strSelectBillCodeLastShow + "'>" + strSelectBillCodeLastShow + "</option>";
		}
		return strReturn;
	}

	//二期汇总界面使用
	public static String getSelectBillCodeOptions(List<String> listBillCode, String strSelectBillCodeFirstShow){
		String strReturn = "";
		if(strSelectBillCodeFirstShow!=null && !strSelectBillCodeFirstShow.trim().equals("")){
			strReturn += "<option value='' selected='selected'>" + strSelectBillCodeFirstShow + "</option>";
		}
		if(listBillCode!=null){
			for(String billCode : listBillCode){
				if(billCode!=null && !billCode.trim().equals("")){
					strReturn += "<option value='" + billCode + "'>" + billCode + "</option>";
				}
		    }
		}
		return strReturn;
	}
	public static String getSelectDicOptions(List<Dictionaries> list, String strSelectBillCodeFirstShow){
		String strReturn = "";
		if(strSelectBillCodeFirstShow!=null && !strSelectBillCodeFirstShow.trim().equals("")){
			strReturn += "<option value='' selected='selected'>" + strSelectBillCodeFirstShow + "</option>";
		}
		if(list!=null){
			for(Dictionaries dic : list){
				strReturn += "<option value='" + dic.getDICT_CODE() + "'>" + dic.getNAME() + "</option>";
		    }
		}
		return strReturn;
	}
	public static String getSelectDeptOptions(List<Department> list, String strSelectBillCodeFirstShow){
		String strReturn = "";
		if(strSelectBillCodeFirstShow!=null && !strSelectBillCodeFirstShow.trim().equals("")){
			strReturn += "<option value='' selected='selected'>" + strSelectBillCodeFirstShow + "</option>";
		}
		if(list!=null){
			for(Department dept : list){
				strReturn += "<option value='" + dept.getDEPARTMENT_CODE() + "'>" + dept.getNAME() + "</option>";
		    }
		}
		return strReturn;
	}
}
	