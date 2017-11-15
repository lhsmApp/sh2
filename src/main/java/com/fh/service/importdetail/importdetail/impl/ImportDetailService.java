package com.fh.service.importdetail.importdetail.impl;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fh.dao.DaoSupport;
import com.fh.util.PageData;
import com.fh.service.importdetail.importdetail.ImportDetailManager;

/** 
 * 说明：导入明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-08-22
 * @version
 */
@Service("importdetailService")
public class ImportDetailService implements ImportDetailManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getCopyInsertList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ImportDetailMapper.getCopyInsertList", pd);
	}
	
	/**插入复制数据
	 * @param pd
	 * @throws Exception
	 */
	public void insertCopy(List<PageData> listData)throws Exception{
		dao.insertCopy("SysSealedInfoMapper.deleteReportRecord", 
				"ImportDetailMapper.save", 
				"ImportDetailMapper.updateSummyBillState", 
				listData);
	}
}

