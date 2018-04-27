package com.fh.service.sysStruMapping.sysStruMapping.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.entity.SysStruMapping;
import com.fh.util.PageData;
import com.fh.service.sysStruMapping.sysStruMapping.SysStruMappingManager;

/** 
 * 说明：  工资明细
 * 创建人：zhangxiaoliu
 * 创建时间：2017-06-30
 * @version
 */
@Service("sysStruMappingService")
public class SysStruMappingService implements SysStruMappingManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("SysStruMappingMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("SysStruMappingMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("SysStruMappingMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("SysStruMappingMapper.datalistJqPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SysStruMappingMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("SysStruMappingMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("SysStruMappingMapper.deleteAll", ArrayDATA_IDS);
	}

	
	/**
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<SysStruMapping> getShowStruList(SysStruMapping mapping)throws Exception{
		return (List<SysStruMapping>)dao.findForList("SysStruMappingMapper.getShowStruList", mapping);
	}
}

