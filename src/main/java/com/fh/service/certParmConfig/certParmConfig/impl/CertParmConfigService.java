package com.fh.service.certParmConfig.certParmConfig.impl;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.CertParmConfig;
import com.fh.entity.JqPage;
import com.fh.util.PageData;
import com.fh.service.certParmConfig.certParmConfig.CertParmConfigManager;

/** 
 * 说明：  工资明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 * @version
 */
@Service("certParmConfigService")
public class CertParmConfigService implements CertParmConfigManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> JqPage(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("CertParmConfigMapper.datalistJqPage", page);
	}
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGridExtend(JqPage page)throws Exception{
		return (int)dao.findForObject("CertParmConfigMapper.countJqGridExtend", page);
	}

	/**用于判断数据是否重复
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getRepeatList(List<PageData> list)throws Exception{
		return (List<PageData>)dao.findForList("CertParmConfigMapper.getRepeatList", list);
	}
	
	/**更新数据库
	 * @param pd
	 * @throws Exception
	 */
	public void batchUpdateDatabase(List<PageData> listData)throws Exception{
		dao.update("CertParmConfigMapper.batchEdit", listData);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(List<PageData> listData)throws Exception{
		dao.delete("CertParmConfigMapper.deleteAll", listData);
	}
	
	
	
	
	
	
	
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("CertParmConfigMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("CertParmConfigMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("CertParmConfigMapper.edit", pd);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CertParmConfigMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("CertParmConfigMapper.findById", pd);
	}

	
	/**
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CertParmConfig> getSelfCertParmConfig(CertParmConfig con)throws Exception{
		return (List<CertParmConfig>)dao.findForList("CertParmConfigMapper.getSelfCertParmConfig", con);
	}
}

