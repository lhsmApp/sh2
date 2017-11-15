package com.fh.service.houseFundDetail.housefunddetail;

import java.util.List;
import java.util.Map;

import com.fh.entity.JqPage;
import com.fh.util.PageData;

/** 
 * 说明： 公积金明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 * @version
 */
public interface HouseFundDetailManager{

	
	/**获取数据
	 * 张晓柳
	 * @param pd
	 * @throws Exception
	 */
	public List<String> findUserCodeByModel(List<PageData> listData)throws Exception;
	public List<String> exportHaveUserCode(PageData listData)throws Exception;

	/**导出列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> exportList(JqPage page)throws Exception;
	/**导出模板
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> exportModel(PageData pd)throws Exception;
	
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
	
	/**批量删除
	 * @param 
	 * @throws Exception
	 */
	public void deleteAll(List<PageData> listData)throws Exception;
	
	/**批量修改
	 * @param pd
	 * @throws Exception
	 */
	public void deleteUpdateAll(List<PageData> listData)throws Exception;
	
	/**导入
	 * @param pd
	 * @throws Exception
	 */
	public void batchImport(List<PageData> listData)throws Exception;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**获取汇总里的明细
	 * @param
	 * @throws Exception
	 */
	public List<PageData> getDetailList(PageData pd)throws Exception;

	/**获取汇总数据
	 * @param
	 * @throws Exception
	 */
	public List<PageData> getSum(Map<String, String> map)throws Exception;
	
	
	
	
}

