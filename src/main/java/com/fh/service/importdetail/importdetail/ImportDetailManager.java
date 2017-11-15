package com.fh.service.importdetail.importdetail;

import java.util.List;

import com.fh.util.PageData;

/** 
 * 说明： 导入明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-08-22
 * @version
 */
public interface ImportDetailManager{

	/**导出列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> getCopyInsertList(PageData pd)throws Exception;
	
	/**插入复制数据
	 * @param pd
	 * @throws Exception
	 */
	public void insertCopy(List<PageData> listData)throws Exception;
	
	
}

