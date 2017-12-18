package com.fh.service.houseFundDetail.housefunddetail.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.util.PageData;
import com.fh.service.houseFundDetail.housefunddetail.HouseFundDetailManager;

/** 
 * 说明： 公积金明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 * @version
 */
@Service("housefunddetailService")
public class HouseFundDetailService implements HouseFundDetailManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**获取单号下拉列表数据源 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getBillCodeList(PageData pd)throws Exception{
		return (List<String>)dao.findForList("HouseFundDetailMapper.getBillCodeList", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> JqPage(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.datalistJqPage", page);
	}
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception{
		return (int)dao.findForObject("HouseFundDetailMapper.countJqGridExtend", page);
	}
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception{
		return (PageData)dao.findForObject("HouseFundDetailMapper.getFooterSummary", page);
	}

	/**通过流水号获取单号，用于判断数据是否已汇总
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getBillCodeBySerialNo(List<PageData> listCode)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.getBillCodeBySerialNo", listCode);
	}
	
	/**导出列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> exportList(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.exportList", page);
	}
	/**导出模板
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> exportModel(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.exportModel", pd);
	}

	/**批量删除
	 * @param 
	 * @throws Exception
	 */
	public void deleteAll(List<PageData> listData)throws Exception{
		dao.delete("HouseFundDetailMapper.deleteAll", listData);
	}
	
	/**获取计算数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getDataCalculation(String tableName, 
			String sqlRetSelect, List<PageData> listAdd)throws Exception{
		return dao.findDataCalculation(tableName, 
				    "HouseFundDetailMapper.batchDelAndIns", 
				    sqlRetSelect, listAdd);
	}
	/**更新数据库
	 * @param pd
	 * @throws Exception
	 */
	public void batchUpdateDatabase(List<PageData> listData)throws Exception{
		dao.update("HouseFundDetailMapper.batchDelAndIns", listData);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/**获取汇总里的明细
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getDetailList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.getDetailList", pd);
	}
	
	/**获取汇总数据
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getSum(Map<String, String> map)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundDetailMapper.getSum", map);
	}
}

