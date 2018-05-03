package com.fh.service.sysTableMapping.sysTableMapping;

import java.util.List;
import java.util.Map;

import com.fh.entity.JqPage;
import com.fh.entity.SysTableMapping;
import com.fh.util.PageData;

/** 
 * 说明： 工资明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-19
 * @version
 */
public interface SysTableMappingManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(JqPage page)throws Exception;
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;

	
	/**
	 * @param pd
	 * @throws Exception
	 */
	public List<SysTableMapping> getUseTableMapping(SysTableMapping mapping)throws Exception;

	
	/**
	 * @param pd
	 * @throws Exception
	 */
	//public List<SysTableMapping> getDetailBillCodeSysTableMapping(SysTableMapping mapping)throws Exception;
	
	
	
}

