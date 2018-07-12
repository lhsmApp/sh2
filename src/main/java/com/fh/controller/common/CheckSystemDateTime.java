package com.fh.controller.common;

import com.fh.util.PageData;

import com.fh.service.sysConfig.sysconfig.SysConfigManager;

/**
 * 模板通用类
 * 
 * @ClassName: CheckSystemDateTime
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhangxiaoliu
 * @date 2018年04月19日
 *
 */
public class CheckSystemDateTime {

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String CheckTranferSystemDateTime(String TranferSystemDateTime, SysConfigManager sysConfigManager) 
			throws Exception {
		String strReturn = "";
		//当前期间,取自tb_system_config的SystemDateTime字段
		String getSystemDateTime = sysConfigManager.currentSection(new PageData());
		if(getSystemDateTime!=null && !getSystemDateTime.trim().equals("")){
			if(!getSystemDateTime.equals(TranferSystemDateTime)){
				strReturn = Message.SystemDateTimeNotSameTranferSystemDateTime;
			}
		} else {
			strReturn = Message.SystemDateTimeMustNotKong;
		}
		return strReturn;
	}
}
