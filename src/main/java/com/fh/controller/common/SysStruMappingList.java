package com.fh.controller.common;

import java.util.List;

import com.fh.entity.SysStruMapping;
import com.fh.entity.SysTableMapping;
import com.fh.service.sysStruMapping.sysStruMapping.impl.SysStruMappingService;
import com.fh.service.sysTableMapping.sysTableMapping.impl.SysTableMappingService;
import com.fh.util.enums.BillState;

/**
 * 下拉列表单号数据源
* @ClassName: SysStruMappingList
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 张晓柳
* @date 2018年04月20日
*
 */
public class SysStruMappingList {

	public static List<SysTableMapping> getUseTableMapping(String pzType, String busiDate, String billOff, 
			SysTableMappingService sysTableMappingService) throws Exception{
		// 动态取自SysTableMapping
		SysTableMapping sysTableMapping = new SysTableMapping();
		sysTableMapping.setBILL_OFF(billOff);
		sysTableMapping.setBUSI_DATE(busiDate);
		sysTableMapping.setTYPE_CODE(pzType);
		sysTableMapping.setSTATE(BillState.Normal.getNameKey());
		List<SysTableMapping> getSysTableMappingList =sysTableMappingService.getUseTableMapping(sysTableMapping);
		return getSysTableMappingList;
	}

	public static List<SysStruMapping> getSysStruMappingList(String pzType, String tableName, 
			String busiDate, String billOff, SysStruMappingService sysStruMappingService) throws Exception{
		// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
		SysStruMapping sysStruMapping = new SysStruMapping();
		sysStruMapping.setBILL_OFF(billOff);
		sysStruMapping.setBUSI_DATE(busiDate);
		sysStruMapping.setTYPE_CODE(pzType);
		sysStruMapping.setTABLE_NAME_MAPPING(tableName);
		List<SysStruMapping> getSysStruMappingList = sysStruMappingService.getShowStruList(sysStruMapping);
		return getSysStruMappingList;
	}

	public static List<SysStruMapping> getDetailBillCodeSysStruMapping(String pzType, String tableName, 
			String busiDate, String billOff, SysStruMappingService sysStruMappingService) throws Exception{
		// 前端数据表格界面字段,动态取自SysStruMapping，根据当前单位编码及表名获取字段配置信息
		SysStruMapping sysStruMapping = new SysStruMapping();
		sysStruMapping.setBILL_OFF(billOff);
		sysStruMapping.setBUSI_DATE(busiDate);
		sysStruMapping.setTYPE_CODE(pzType);
		sysStruMapping.setTABLE_NAME_MAPPING(tableName);
		List<SysStruMapping> getSysStruMappingList = sysStruMappingService.getShowStruList(sysStruMapping);
		return getSysStruMappingList;
	}
}
	
