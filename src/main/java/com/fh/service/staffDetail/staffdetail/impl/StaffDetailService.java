package com.fh.service.staffDetail.staffdetail.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.util.PageData;
import com.fh.service.staffDetail.staffdetail.StaffDetailManager;

/** 
 * 说明：  工资明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 * @version
 */
@Service("staffdetailService")
public class StaffDetailService implements StaffDetailManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**获取单号下拉列表数据源 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getBillCodeList(PageData pd)throws Exception{
		return (List<String>)dao.findForList("StaffDetailMapper.getBillCodeList", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> JqPage(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.datalistJqPage", page);
	}
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception{
		return (int)dao.findForObject("StaffDetailMapper.countJqGridExtend", page);
	}
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception{
		return (PageData)dao.findForObject("StaffDetailMapper.getFooterSummary", page);
	}

	/**通过流水号获取流水号，用于判断数据是否变更 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getSerialNoBySerialNo(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.getSerialNoBySerialNo", pd);
	}
	
	/**导出列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> exportList(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.exportList", page);
	}
	/**导出模板
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> exportModel(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.exportModel", pd);
	}

	/**批量删除
	 * @param 
	 * @throws Exception
	 */
	public void deleteAll(List<PageData> listData)throws Exception{
		dao.delete("StaffDetailMapper.deleteAll", listData);
	}
	
	/**获取计算数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getDataCalculation(String tableName, String TableFeildTax, String TmplUtil_KeyExtra,
			PageData pdInsetBackup,
			List<String> listSalaryFeildUpdate, String sqlRetSelect, 
			List<PageData> listAddSalary, List<PageData> listAddBonus,
			String sqlSumByUserCodeSalary,  String sqlSumByUserCodeBonus, String TableFeildSum)throws Exception{
		return dao.findDataCalculation(tableName, TableFeildTax, TmplUtil_KeyExtra,
				    "StaffDetailMapper.insetBackup", pdInsetBackup,
				    "StaffDetailMapper.batchDelAndIns", 
				    listSalaryFeildUpdate, sqlRetSelect, 
				    listAddSalary, listAddBonus,
					sqlSumByUserCodeSalary, sqlSumByUserCodeBonus, TableFeildSum);
	}
	/**更新数据库
	 * @param pd
	 * @throws Exception
	 */
	public void batchUpdateDatabase(List<PageData> listData)throws Exception{
		dao.update("StaffDetailMapper.batchDelAndIns", listData);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/**获取汇总里的明细
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getDetailList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.getDetailList", pd);
	}
	
	/**获取汇总数据
	 * @param
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getSum(Map<String, String> map)throws Exception{
		return (List<PageData>)dao.findForList("StaffDetailMapper.getSum", map);
	}
}

