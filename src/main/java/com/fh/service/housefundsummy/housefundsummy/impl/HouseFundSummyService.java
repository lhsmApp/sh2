package com.fh.service.housefundsummy.housefundsummy.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.entity.SysSealed;
import com.fh.util.PageData;
import com.fh.service.housefundsummy.housefundsummy.HouseFundSummyManager;

/** 
 * 说明： 公积金汇总
 * 创建人：zhangxiaoliu
 * 创建时间：2017-07-07
 * @version
 */ 
@Service("housefundsummyService")
public class HouseFundSummyService implements HouseFundSummyManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> JqPage(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundSummyMapper.datalistJqPage", page);
	}
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception{
		return (int)dao.findForObject("HouseFundSummyMapper.countJqGridExtend", page);
	}
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception{
		return (PageData)dao.findForObject("HouseFundSummyMapper.getFooterSummary", page);
	}
	
	/**获取汇总数据
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getHave(Map<String, String> map)throws Exception{
		return (List<PageData>)dao.findForList("HouseFundSummyMapper.getHave", map);
	}
	
	/**汇总
	 * @param 
	 * @throws Exception
	 */
	public void saveSummyModelList(List<Map<String, Object>> listMap, PageData pdBillNum)throws Exception{
		dao.batch_One_del_Ins("HouseFundSummyMapper.delete", "HouseFundSummyMapper.save", "HouseFundDetailMapper.editBillCode", listMap, 
				"SysBillnumMapper.delete", "SysBillnumMapper.save", pdBillNum);
		        //, List<SysSealed> delReportList
				//"SysSealedInfoMapper.reportDelete", delReportList
	}
	
}

