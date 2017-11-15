package com.fh.service.voucher.voucher.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.JqPage;
import com.fh.service.voucher.voucher.VoucherManager;
import com.fh.util.PageData;

/**
 * 凭证数据传输接口
* @ClassName: VoucherService
* @Description: TODO(这里用一句话描述这个类的作用)
* @author jiachao
* @date 2017年6月23日
*
 */
@Service("voucherService")
public class VoucherService implements VoucherManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("VoucherMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("VoucherMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("VoucherMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(JqPage page)throws Exception{
		return (List<PageData>)dao.findForList("VoucherMapper.datalistJqPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("VoucherMapper.listAll", pd);
	}
	
	/**列表(同步删除)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listSyncDelList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("VoucherMapper.listSyncDelList", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("VoucherMapper.findById", pd);
	}
	
	/**获取明细表信息
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listDetail(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("VoucherMapper.listDetail", pd);
	}
	
	/**获取记录数量
	 * @param pd
	 * @throws Exception
	 */
	public int countJqGrid(PageData pd)throws Exception{
		return (int)dao.findForObject("VoucherMapper.countJqGrid", pd);
	}
	
	/**获取记录总合计
	 * @param pd
	 * @throws Exception
	 */
	public PageData getFooterSummary(JqPage page)throws Exception{
		return (PageData)dao.findForObject("VoucherMapper.getFooterSummary", page);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("VoucherMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**获取汇总信息还没有进行上报的信息
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> getTransferValidate(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("VoucherMapper.getTransferValidate", pd);
	}
	
	/**批量更新凭证号
	 * @param pd
	 * @throws Exception
	 */
	public void updateCertCode(List<PageData> pd)throws Exception{
		dao.update("VoucherMapper.updateCertCode", pd);
	}
	
	/**批量更新冲销凭证号
	 * @param pd
	 * @throws Exception
	 */
	public void updateRevCertCode(List<PageData> pd)throws Exception{
		dao.update("VoucherMapper.updateRevCertCode", pd);
	}
}

