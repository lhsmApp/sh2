package com.fh.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.fh.entity.LaborTax;
import com.fh.entity.StaffTax;
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


	/**
	 * 获取计算数据
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findDataCalculation(String tableNameBackup, 
			String sqlBatchDelAndIns, 
			String sqlRetSelect, List<PageData> listAdd)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			if(listAdd!=null && !listAdd.isEmpty()){
				sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listAdd){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listAdd);
				sqlSession.flushStatements();
				sqlSession.commit();
				sqlSession.clearCache();
				PageData getAddSerialNo = new PageData();
				getAddSerialNo.put("tableName", tableNameBackup);
				getAddSerialNo.put("strMaxNum", strMaxNum);
				List<Integer> getInsertBillCodeList =  sqlSession.selectList("DataCalculation.getAddSerialNo",  getAddSerialNo);
				if(getInsertBillCodeList!=null){
					for(Integer billCode : getInsertBillCodeList){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += billCode;
					}
				}
				PageData getListBySerialNo = new PageData();
				getListBySerialNo.put("sqlRetSelect", sqlRetSelect);
				getListBySerialNo.put("SqlInBillCode", SqlInBillCode);
				returnList = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
			}
		} finally{
			sqlSession.rollback(); 
			sqlSession.close();
		}
		return returnList;
	}
	
	/**
	 * 获取计算数据
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findDataCalculation(String tableNameBackup, String TableFeildTax, String TmplUtil_KeyExtra,
			String sqlInsetBackup, PageData pdInsetBackup,
			String sqlBatchDelAndIns, 
			List<String> listSalaryFeildUpdate, String sqlRetSelect, 
			List<PageData> listAddSalary, List<PageData> listAddBonus,
			String sqlSumByUserCodeSalary,  String sqlSumByUserCodeBonus, String TableFeildSum)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<StaffTax> listStaffTax = sqlSession.selectList("DataCalculation.getStaffTax");
		sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
		
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			sqlSession.update(sqlInsetBackup, pdInsetBackup);
			if(listAddBonus!=null && !listAddBonus.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listAddBonus){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listAddBonus);
				PageData getAddSerialNo = new PageData();
				getAddSerialNo.put("tableName", tableNameBackup);
				getAddSerialNo.put("strMaxNum", strMaxNum);
				List<Integer> getInsertBillCodeList =  sqlSession.selectList("DataCalculation.getAddSerialNo",  getAddSerialNo);
				if(getInsertBillCodeList!=null){
					for(Integer billCode : getInsertBillCodeList){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += billCode;
					}
				}
				PageData getListBySerialNo = new PageData();
				getListBySerialNo.put("sqlRetSelect", sqlRetSelect);
				getListBySerialNo.put("SqlInBillCode", SqlInBillCode);
				List<PageData> retListBonus = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(retListBonus!=null){
					List<String> userCodeList = new ArrayList<String>();
					for(PageData eachAdd : retListBonus){
						String USER_CODE = eachAdd.getString("USER_CODE");
						eachAdd.put("YSZE", new BigDecimal(0));
						eachAdd.put("YDRZE", new BigDecimal(0));
						//本条记录自己的税额
						BigDecimal addTax = (BigDecimal) eachAdd.get(TableFeildTax + TmplUtil_KeyExtra);
						if(!userCodeList.contains(USER_CODE)){
							userCodeList.add(USER_CODE);
							BigDecimal douTableFeildTax = new BigDecimal(0);
							PageData getSumByUserCode = new PageData();
							getSumByUserCode.put("sqlSumByUserCode", sqlSumByUserCodeBonus);
							getSumByUserCode.put("USER_CODE", USER_CODE);
							PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCode);
							//所有记录计算的税额
							BigDecimal sumNumCheck = (BigDecimal) getSum.get(TableFeildSum);
							BigDecimal sumNum = new BigDecimal(0);
							if(listStaffTax!=null){
								for(int i=0; i<listStaffTax.size(); i++){
									StaffTax eachTax = listStaffTax.get(i);
									if(sumNumCheck.doubleValue()/12 >= eachTax.getMIN_VALUE()){
										if(i == listStaffTax.size() -1){
											sumNum = sumNumCheck.multiply(new BigDecimal(eachTax.getTAX_RATE() * 0.01)).subtract(new BigDecimal(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(sumNumCheck.doubleValue()/12 <= eachTax.getMAX_VALUE()){
												sumNum = sumNumCheck.multiply(new BigDecimal(eachTax.getTAX_RATE() * 0.01)).subtract(new BigDecimal(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}
							//所有记录汇总的税额
							BigDecimal sumTax = (BigDecimal) getSum.get(TableFeildTax);
							douTableFeildTax = sumNum.subtract(sumTax).add(addTax);
							eachAdd.put(TableFeildTax, douTableFeildTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("YSZE", sumNum.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("YDRZE", sumTax.setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						returnList.add(eachAdd);
					}
				}
			}
			if(listAddSalary!=null && !listAddSalary.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listAddSalary){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listAddSalary);
				PageData getAddSerialNo = new PageData();
				getAddSerialNo.put("tableName", tableNameBackup);
				getAddSerialNo.put("strMaxNum", strMaxNum);
				List<Integer> getInsertBillCodeList =  sqlSession.selectList("DataCalculation.getAddSerialNo",  getAddSerialNo);
				if(getInsertBillCodeList!=null){
					for(Integer billCode : getInsertBillCodeList){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += billCode;
					}
				}
				if(listSalaryFeildUpdate!=null){
					for(String strUpdateFeild : listSalaryFeildUpdate){
						PageData setUpdateFeild = new PageData();
						setUpdateFeild.put("sqlUpdateFeild", strUpdateFeild);
						setUpdateFeild.put("SqlInBillCode", SqlInBillCode);
						sqlSession.selectList("DataCalculation.editSalaryFeild",  setUpdateFeild);
					}
				}
				PageData getListBySerialNo = new PageData();
				getListBySerialNo.put("sqlRetSelect", sqlRetSelect);
				getListBySerialNo.put("SqlInBillCode", SqlInBillCode);
				List<PageData> retListSalary = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(retListSalary!=null){
					List<String> userCodeList = new ArrayList<String>();
					for(PageData eachAdd : retListSalary){
						String USER_CODE = eachAdd.getString("USER_CODE");
						eachAdd.put("YSZE", new BigDecimal(0));
						eachAdd.put("YDRZE", new BigDecimal(0));
						BigDecimal addTax = (BigDecimal) eachAdd.get(TableFeildTax + TmplUtil_KeyExtra);
						if(!userCodeList.contains(USER_CODE)){
							userCodeList.add(USER_CODE);
							BigDecimal douTableFeildTax = new BigDecimal(0);
							PageData getSumByUserCode = new PageData();
							getSumByUserCode.put("sqlSumByUserCode", sqlSumByUserCodeSalary);
							getSumByUserCode.put("USER_CODE", USER_CODE);
							PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCode);
							BigDecimal sumNumCheck = (BigDecimal) getSum.get(TableFeildSum);
							BigDecimal sumNum = new BigDecimal(0);
							if(listStaffTax!=null){
								for(int i=0; i<listStaffTax.size(); i++){
									StaffTax eachTax = listStaffTax.get(i);
									if(sumNumCheck.doubleValue() >= eachTax.getMIN_VALUE()){
										if(i == listStaffTax.size() -1){
											sumNum = sumNumCheck.multiply(new BigDecimal(eachTax.getTAX_RATE() * 0.01)).subtract(new BigDecimal(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(sumNumCheck.doubleValue() <= eachTax.getMAX_VALUE()){
												sumNum = sumNumCheck.multiply(new BigDecimal(eachTax.getTAX_RATE() * 0.01)).subtract(new BigDecimal(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}
							BigDecimal sumTax = (BigDecimal) getSum.get(TableFeildTax);
							douTableFeildTax = sumNum.subtract(sumTax).add(addTax);
							eachAdd.put(TableFeildTax, douTableFeildTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("YSZE", sumNum.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("YDRZE", sumTax.setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						returnList.add(eachAdd);
					}
				}
			}
			sqlSession.flushStatements();
			sqlSession.commit();
			sqlSession.clearCache();
		} finally{
			sqlSession.rollback(); 
			sqlSession.close();
		}
		return returnList;
	}

	/**
	 * 获取计算数据
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findDataCalculationLaborDetail(String tableNameBackup, String ccTmplUtil_KeyExtra,
			String sqlInsetBackup, PageData pdInsetBackup,
			String sqlBatchDelAndIns, List<PageData> listAdd,
			String sqlRetSelect, 
			String sqlSumByUserNameStaffIdent)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<LaborTax> listLaborTax = sqlSession.selectList("DataCalculation.getLaborTax");
		sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
		
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			sqlSession.update(sqlInsetBackup, pdInsetBackup);
			if(listAdd!=null && !listAdd.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listAdd){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listAdd);
				PageData getAddSerialNo = new PageData();
				getAddSerialNo.put("tableName", tableNameBackup);
				getAddSerialNo.put("strMaxNum", strMaxNum);
				List<Integer> getInsertBillCodeList =  sqlSession.selectList("DataCalculation.getAddSerialNo",  getAddSerialNo);
				if(getInsertBillCodeList!=null){
					for(Integer billCode : getInsertBillCodeList){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += billCode;
					}
				}
				PageData getListBySerialNo = new PageData();
				getListBySerialNo.put("sqlRetSelect", sqlRetSelect);
				getListBySerialNo.put("SqlInBillCode", SqlInBillCode);
				List<PageData> retList = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(retList!=null){
					for(PageData eachAdd : retList){
						returnList.add(eachAdd);
						//String USER_CODE = eachAdd.getString("USER_CODE");
						String USER_NAME = eachAdd.getString("USER_NAME");
						String STAFF_IDENT = eachAdd.getString("STAFF_IDENT");
						eachAdd.put("YSZE", new BigDecimal(0));
						eachAdd.put("YDRZE", new BigDecimal(0));

					    String pdSetDistinctColumn = eachAdd.getString("DistinctColumn");
						if(!(pdSetDistinctColumn!=null && !pdSetDistinctColumn.trim().equals(""))){
							eachAdd.put("DistinctColumn", "DistinctColumn");
							for(PageData eachSetDistinctColumn : retList){
								String eachUSER_NAME = eachSetDistinctColumn.getString("USER_NAME");
								String eachSTAFF_IDENT = eachSetDistinctColumn.getString("STAFF_IDENT");
								if(USER_NAME.equals(eachUSER_NAME) && STAFF_IDENT.equals(eachSTAFF_IDENT)){
									eachSetDistinctColumn.put("DistinctColumn", "DistinctColumn");
								}
							}
						} else {
							BigDecimal decGROSS_PAY = (BigDecimal) eachAdd.get("GROSS_PAY");
							BigDecimal decACCRD_TAX = (BigDecimal) eachAdd.get("ACCRD_TAX");
							eachAdd.put("ACT_SALY", decGROSS_PAY.subtract(decACCRD_TAX));
							continue;
						}
						
						BigDecimal addTax = (BigDecimal) eachAdd.get("ACCRD_TAX");

						BigDecimal douTableFeildTax = new BigDecimal(0);
						PageData getSumGroupBy = new PageData();
						//getSumGroupBy.put("sqlSumByUserCode", sqlSumByUserCode);
						getSumGroupBy.put("sqlSumByUserNameStaffIdent", sqlSumByUserNameStaffIdent);
						//getSumGroupBy.put("USER_CODE", USER_CODE);
						getSumGroupBy.put("USER_NAME", USER_NAME);
						getSumGroupBy.put("STAFF_IDENT", STAFF_IDENT);
						//PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumGroupBy);
						PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserNameStaffIdent",  getSumGroupBy);
						//所有记录计算的税额
						BigDecimal sumNumCheck = (BigDecimal) getSum.get("GROSS_PAY");
						String strTAX_FORMULA = "";
						if(listLaborTax!=null){
							for(int i_listLaborTax=0; i_listLaborTax<listLaborTax.size(); i_listLaborTax++){
								LaborTax eachTax = listLaborTax.get(i_listLaborTax);
								if(sumNumCheck.doubleValue() >= eachTax.getMIN_VALUE()){
									if(i_listLaborTax == listLaborTax.size() -1){
										strTAX_FORMULA = eachTax.getTAX_FORMULA();
									} else {
										if(sumNumCheck.doubleValue() <= eachTax.getMAX_VALUE()){
											strTAX_FORMULA = eachTax.getTAX_FORMULA();
										}
									}
								}
							}
						}
						BigDecimal sumNum = new BigDecimal(0);
						if(strTAX_FORMULA!=null && !strTAX_FORMULA.trim().equals("")){
							PageData getTaxFormula = new PageData();
							getTaxFormula.put("taxFormula", strTAX_FORMULA.replace("累计应发评审费", sumNumCheck.toString()) + " TaxFormulaValue ");
							getTaxFormula.put("tableName", tableNameBackup);
							List<PageData> getListTaxFormula = sqlSession.selectList("DataCalculation.getTaxFormula",  getTaxFormula);
							sumNum = new BigDecimal(getListTaxFormula.get(0).get("TaxFormulaValue").toString());
						}
						//所有记录汇总的税额
						BigDecimal sumTax = (BigDecimal) getSum.get("ACCRD_TAX");
						douTableFeildTax = sumNum.subtract(sumTax).add(addTax);
						eachAdd.put("ACCRD_TAX", douTableFeildTax.setScale(2, BigDecimal.ROUND_HALF_UP));

						BigDecimal decGROSS_PAY = (BigDecimal) eachAdd.get("GROSS_PAY");
						BigDecimal decACCRD_TAX = (BigDecimal) eachAdd.get("ACCRD_TAX");
						eachAdd.put("ACT_SALY", decGROSS_PAY.subtract(decACCRD_TAX));
						eachAdd.put("YSZE", sumNum.setScale(2, BigDecimal.ROUND_HALF_UP));
						eachAdd.put("YDRZE", sumTax.setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				}
			}
			sqlSession.flushStatements();
			sqlSession.commit();
			sqlSession.clearCache();
		} finally{
			sqlSession.rollback(); 
			sqlSession.close();
		}
		return returnList;
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

}


