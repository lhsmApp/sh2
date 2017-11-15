package com.fh.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.fh.util.PageData;
/**
 *  
* @ClassName: DaoSupport
* @Description: TODO(这里用一句话描述这个类的作用)
* @author lhsmplus
* @date 2017年6月30日
*
 */
@Repository("daoSupport")
public class DaoSupport implements DAO {

	@Resource(name = "sqlSessionTemplate")
	private SqlSessionTemplate sqlSessionTemplate;

	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object findForObject(String str, Object obj) throws Exception {
		return sqlSessionTemplate.selectOne(str, obj);
	}

	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object findForList(String str, Object obj) throws Exception {
		return sqlSessionTemplate.selectList(str, obj);
	}
	
	public Object findForMap(String str, Object obj, String key, String value) throws Exception {
		return sqlSessionTemplate.selectMap(str, obj, key);
	}

	/**
	 * 保存对象
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object save(String str, Object obj) throws Exception {
		return sqlSessionTemplate.insert(str, obj);
	}
	
	/**
	 * 批量更新
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object batchSave(String str, List objs )throws Exception{
		return sqlSessionTemplate.insert(str, objs);
	}
	
	/**
	 * 修改对象
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object update(String str, Object obj) throws Exception {
		return sqlSessionTemplate.update(str, obj);
	}

	/**
	 * 删除对象 
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object delete(String str, Object obj) throws Exception {
		return sqlSessionTemplate.delete(str, obj);
	}

	/**
	 * 批量更新
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object batchDelete(String str, List objs )throws Exception{
		return sqlSessionTemplate.delete(str, objs);
	}
	
	/**
	 * 批量更新
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void batchUpdate(String str, List<?> objs )throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null){
				for(int i=0,size=objs.size();i<size;i++){
					sqlSession.update(str, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}
	public void batchDeleteAllUpdate(String deleteAll, String insert, List<?> objs )throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null){
				sqlSession.delete(deleteAll, objs);
				for(int i=0,size=objs.size();i<size;i++){
					sqlSession.update(insert, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}
	public void batchDeleteOneUpdate(String delete, String insert, List<?> objs )throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null){
				for(int i=0,size=objs.size();i<size;i++){
					sqlSession.delete(delete, objs.get(i));
					sqlSession.update(insert, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}

	/**
	 * 插入复制数据
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void insertCopy(String strDelete, String strInsert, String updateSummyBillState, List<?> objs)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null&&objs.size()>0){
				for(int i=0,size=objs.size();i<size;i++){
				    sqlSession.delete(strDelete, objs.get(i));
					sqlSession.update(updateSummyBillState, objs.get(i));
					sqlSession.update(strInsert, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}

	/**
	 * 导入
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void batchImport(String importDelete, String importInsert, List<?> objs)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null&&objs.size()>0){
				for(int i=0,size=objs.size();i<size;i++){
				    sqlSession.delete(importDelete, objs.get(i));
				}
				for(int i=0,size=objs.size();i<size;i++){
					sqlSession.insert(importInsert, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		} finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 上报
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void saveReport(String reportDelete, String reportInsert, List<?> objs)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(objs!=null&&objs.size()>0){
				for(int i=0,size=objs.size();i<size;i++){
					sqlSession.delete(reportDelete, objs.get(i));
					sqlSession.update(reportInsert, objs.get(i));
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}


	/**
	 * 单条记录先删后插
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public void batch_One_del_Ins(String delSum, String insSum, String editBillCode, List<Map<String, Object>> listMap,  
			String deleteBillNum, String insertBillNum, PageData pdBillNum)throws Exception{
		//List<Map<String, Object>> listMap,
		//, String deleteReportListen, List<?> listReportListen
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			if(listMap!=null&&listMap.size()>0){
				//最大单号
				if(pdBillNum != null){
					sqlSession.update(deleteBillNum, pdBillNum);
					sqlSession.update(insertBillNum, pdBillNum);
				}
				//删掉接口的所有上报记录
				//if(listReportListen!=null&&listReportListen.size()>0){
				//	for(int i=0,size=listReportListen.size();i<size;i++){
				//	    sqlSession.delete(deleteReportListen, listReportListen.get(i));
				//	}
				//}
				for(Map<String, Object> map : listMap){
					//Boolean bolDelSum = (Boolean) map.get("DelSum");
					List<?> objs = (List<?>) map.get("AddList");
					//1、能删情况下(bolDelSum取决于是否有接口的上报记录)，删除能删的汇总记录
					//if(bolDelSum){
					for(int i=0,size=objs.size();i<size;i++){
						sqlSession.delete(delSum, objs.get(i));
					}
					//}
					//2、把区间内本部门的汇总记录都作废（必须在修改明细单号前）
					//for(int i=0,size=objs.size();i<size;i++){
					//    sqlSession.update(updateBillState, objs.get(i));
					//}
					//3、修改明细单号（必须在插入汇总记录前）
					for(int i=0,size=objs.size();i<size;i++){
						sqlSession.update(editBillCode, objs.get(i));
					}
					//4、插入汇总记录
					for(int i=0,size=objs.size();i<size;i++){
						sqlSession.update(insSum, objs.get(i));
					}
				}
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
			}
		}finally{
			sqlSession.close();
		}
	}
	
}


