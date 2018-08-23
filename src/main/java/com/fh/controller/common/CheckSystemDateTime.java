package com.fh.controller.common;

import com.fh.util.DateUtil;
import com.fh.util.PageData;

import com.fh.entity.SysDeptLtdTime;
import com.fh.service.sysConfig.sysconfig.SysConfigManager;
import com.fh.service.sysDeptLtdTime.sysDeptLtdTime.impl.SysDeptLtdTimeService;

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
	public static String CheckTranferSystemDateTime(String TranferSystemDateTime, SysConfigManager sysConfigManager,
			Boolean bolSysDeptLtdTime) 
			throws Exception {
		String strReturn = "";
		//当前期间,取自tb_system_config的SystemDateTime字段
		String getSystemDateTime = sysConfigManager.currentSection(new PageData());
		if(getSystemDateTime!=null && !getSystemDateTime.trim().equals("")){
			if(!getSystemDateTime.equals(TranferSystemDateTime)){
				strReturn += Message.SystemDateTimeNotSameTranferSystemDateTime;
			}
		} else {
			strReturn += Message.SystemDateTimeMustNotKong;
		}
		return strReturn;
	}

	/**
	 * SystemDateTime取年
	 * @return
	 * @throws Exception
	 */
	public static String getSystemDateTimeYear(String SystemDateTime) 
			throws Exception {
		String strReturn = SystemDateTime.substring(0, SystemDateTime.length() - 1 - 2);
		return strReturn;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String CheckSysDeptLtdTime(String DEPT_CODE, String BUSI_TYPE, SysDeptLtdTimeService sysDeptLtdTimeService) 
			throws Exception {
		String strReturn = "";
		SysDeptLtdTime sysTransfer = new SysDeptLtdTime();
		sysTransfer.setDEPT_CODE(DEPT_CODE);
		sysTransfer.setBUSI_TYPE(BUSI_TYPE);
		SysDeptLtdTime getSysDeptLtdTime = sysDeptLtdTimeService.getUseSysDeptLtdTime(sysTransfer);
		if(getSysDeptLtdTime!=null){
			String LTD_DAY = getSysDeptLtdTime.getLTD_DAY();
			String LTD_HOUR = getSysDeptLtdTime.getLTD_HOUR();
			String strCurrentDay = DateUtil.getCurrentDay();
			String strCurrentHour = DateUtil.getCurrentHour();
			if(Integer.valueOf(strCurrentDay) >= Integer.valueOf(LTD_DAY)){
				strReturn += getSysDeptLtdTime.getDEPT_NAME() + " " + Message.CurrentDay_LTD_DAY;
			} else if(Integer.valueOf(strCurrentHour) >= Integer.valueOf(LTD_HOUR)){
				strReturn += getSysDeptLtdTime.getDEPT_NAME() + " " + Message.CurrentHour_LTD_HOUR;
			}
		}
		return strReturn;
	}
}
