package com.fh.service.detailimportquery.detailimportquery;

import java.util.List;

import com.fh.entity.JqPage;
import com.fh.util.PageData;

/** 
 * 说明： 明细导入查询
 * 创建人：张晓柳
 * 创建时间：2017-08-07
 * @version
 */
public interface DetailImportQueryManager{

	/**获取单号下拉列表数据源 
	 * @param pd
	 * @throws Exception
	 */
	public List<String> getBillCodeList(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> JqPage(JqPage page)throws Exception;
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception;
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception;
	
	/**导出
	 * @param 
	 * @throws Exception
	 */
	public List<PageData> datalistExport(JqPage page)throws Exception;
	
}

