package com.fh.service.fundsconfirmquery.fundsconfirmquery.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.util.PageData;
import com.fh.service.fundsconfirmquery.fundsconfirmquery.FundsConfirmQueryManager;

/** 
 * 说明： 汇总单据确认
 * 创建人：张晓柳
 * 创建时间：2018-04-11
 * @version
 */
@Service("fundsconfirmqueryService")
public class FundsConfirmQueryService implements FundsConfirmQueryManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**获取单号下拉列表数据源 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getBillCodeList(PageData pd)throws Exception{
		return (List<String>)dao.findForList("FundsConfirmQueryMapper.getBillCodeList", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> JqPage(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("FundsConfirmQueryMapper.datalistJqPage", page);
	}
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception{
		return (int)dao.findForObject("FundsConfirmQueryMapper.countJqGridExtend", page);
	}
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception{
		return (PageData)dao.findForObject("FundsConfirmQueryMapper.getFooterSummary", page);
	}
	
	/**明细
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getFirstDetailList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FundsConfirmQueryMapper.getFirstDetailList", pd);
	}

	/**导出
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> datalistExport(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("FundsConfirmQueryMapper.datalistExport", page);
	}
}

