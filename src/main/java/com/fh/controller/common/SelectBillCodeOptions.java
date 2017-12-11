package com.fh.controller.common;

import java.util.List;

/**
 * 下拉列表单号数据源
* @ClassName: SelectBillCodeOptions
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2017年11月21日
*
 */
public class SelectBillCodeOptions {

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
}
	