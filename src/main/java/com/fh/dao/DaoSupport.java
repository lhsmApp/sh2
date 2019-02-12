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
import com.fh.entity.StaffFilterInfo;
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
	public List<PageData> findDataCalculation(String tableNameBackup, String TmplUtil_KeyExtra,
			String TableFeildSalarySelf, String TableFeildSalaryTax, String TableFeildBonusSelf, String TableFeildBonusTax,
			String TableFeildSalaryTaxConfigGradeOper, String TableFeildBonusTaxConfigGradeOper,
			String TableFeildSalaryTaxConfigSumOper, String TableFeildBonusTaxConfigSumOper,
			String TableFeildSalaryTaxSelfSumOper, String TableFeildBonusTaxSelfSumOper,
			String sqlInsetBackup, PageData pdInsetBackup,
			String sqlBatchDelAndIns, 
			List<String> listSalaryFeildUpdate, String sqlRetSelect, List<PageData> listData,
			String sqlSumByUserCodeSalary1,
			String sqlSumByUserCodeBonus1,
			Boolean bolCalculation, List<StaffFilterInfo> listStaffFilterInfo)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<StaffTax> tbStaffTax = sqlSession.selectList("DataCalculation.getStaffTax");
		//List<BonusTax> tbBonusTax = sqlSession.selectList("DataCalculation.getBonusTax");
		sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
		
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			sqlSession.update(sqlInsetBackup, pdInsetBackup);
			if(listData!=null && !listData.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listData){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listData);
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
				List<PageData> retList = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(bolCalculation && retList!=null && retList.size()>0){
					for(PageData eachAdd : retList){
						eachAdd.put("bolBonusTax", true);
						eachAdd.put("bolSalaryTax", true);

					    //工资范围编码
						String getSAL_RANGE = (String) eachAdd.get("SAL_RANGE");
						if(getSAL_RANGE!=null && listStaffFilterInfo!=null){
							for(StaffFilterInfo filter : listStaffFilterInfo){
								if(("ANY").toUpperCase().trim().equals(filter.getSAL_RANGE().toUpperCase().trim()) || getSAL_RANGE.equals(filter.getSAL_RANGE())){
									eachAdd.put("bolBonusTax", false);
									eachAdd.put("bolSalaryTax", false);
									continue;
								}
							}
						}
						
						BigDecimal selfBonus = new BigDecimal(eachAdd.get(TableFeildBonusSelf).toString());
						selfBonus = selfBonus.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax).toString());
						selfB_Tax = selfB_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildBonusTax, selfB_Tax);
						eachAdd.put(TableFeildBonusTax + TmplUtil_KeyExtra, selfB_Tax);
						if(selfBonus.compareTo(new BigDecimal(0)) == 0 && selfB_Tax.compareTo(new BigDecimal(0)) == 0){
							eachAdd.put("bolBonusTax", false);
						}
						
						BigDecimal selfSalary = new BigDecimal(eachAdd.get(TableFeildSalarySelf).toString());
						selfSalary = selfSalary.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax).toString());
						selfS_Tax = selfS_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildSalaryTax, selfS_Tax);
						eachAdd.put(TableFeildSalaryTax + TmplUtil_KeyExtra, selfS_Tax);
						if(selfSalary.compareTo(new BigDecimal(0)) == 0 && selfS_Tax.compareTo(new BigDecimal(0)) == 0){
							eachAdd.put("bolSalaryTax", false);
						}
					}
					List<String> B_STAFF_IDENTList = new ArrayList<String>();
					List<String> S_STAFF_IDENTList = new ArrayList<String>();
					for(PageData eachAdd : retList){
						String STAFF_IDENT = eachAdd.getString("STAFF_IDENT");
						eachAdd.put("B_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("B_YDRZE", new BigDecimal(0));//已导入纳税额
						eachAdd.put("S_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("S_YDRZE", new BigDecimal(0));//已导入纳税额
						//本条记录自己的税额
						BigDecimal addB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax + TmplUtil_KeyExtra).toString());
						BigDecimal addS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax + TmplUtil_KeyExtra).toString());

						if(((Boolean)eachAdd.get("bolSalaryTax")) == true && !S_STAFF_IDENTList.contains(STAFF_IDENT)){
							S_STAFF_IDENTList.add(STAFF_IDENT);
							
							PageData getSumByStaffIdentSalary = new PageData();
							getSumByStaffIdentSalary.put("sqlSumByStaffIdent", sqlSumByUserCodeSalary1);
							getSumByStaffIdentSalary.put("STAFF_IDENT", STAFF_IDENT);
							PageData getSumSalary = sqlSession.selectOne("DataCalculation.getSumByStaffIdent",  getSumByStaffIdentSalary);
							BigDecimal checkSalaryTaxConfigGrade = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigGradeOper).toString());
							BigDecimal getSalaryTaxConfigSum = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigSumOper).toString());

							BigDecimal douSalaryTAX_RATE = new BigDecimal(0);
							BigDecimal douSalaryQUICK_DEDUCTION = new BigDecimal(0);
							if(tbStaffTax!=null){
								for(int i=0; i<tbStaffTax.size(); i++){
									StaffTax eachTax = tbStaffTax.get(i);
									//int a = bigdemical.compareTo(bigdemical2)
									//a = -1,表示bigdemical小于bigdemical2；
									//a = 0,表示bigdemical等于bigdemical2；
									//a = 1,表示bigdemical大于bigdemical2；
									BigDecimal eachMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
									BigDecimal eachMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
									
									int SalaryMIN_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMIN_VALUE.multiply(new BigDecimal(12)));
									int SalaryMAX_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMAX_VALUE.multiply(new BigDecimal(12)));
									if(SalaryMIN_VALUE == 0 || SalaryMIN_VALUE == 1){//sumNumCheck >= eachMIN_VALUE
										if(i == tbStaffTax.size() -1){
											douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
											BigDecimal getQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											douSalaryQUICK_DEDUCTION = getQUICK_DEDUCTION.multiply(new BigDecimal(12));
										} else {
											if(SalaryMAX_VALUE == 0 || SalaryMAX_VALUE == -1){//sumNumCheck <= eachTax.getMAX_VALUE()
												douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												BigDecimal getQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
												douSalaryQUICK_DEDUCTION = getQUICK_DEDUCTION.multiply(new BigDecimal(12));
											}
										}
									}
								}
							}

							BigDecimal bd3Salary = new BigDecimal(Double.toString(0.010000));
							BigDecimal sumSalaryTaxConfig = getSalaryTaxConfigSum.multiply(douSalaryTAX_RATE).multiply(bd3Salary).subtract(douSalaryQUICK_DEDUCTION);
							sumSalaryTaxConfig = sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal sumSalaryTaxSelf = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxSelfSumOper).toString());
							sumSalaryTaxSelf = sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal douTableFeildSalaryTax = sumSalaryTaxConfig.subtract(sumSalaryTaxSelf).add(addS_Tax);
							eachAdd.put(TableFeildSalaryTax, douTableFeildSalaryTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YSZE", sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YDRZE", sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
							
							/*if(douTableFeildSalaryTax.compareTo(new BigDecimal(0)) < 0){
								for(PageData eachSet0 : retList){
									if(USER_CODE.equals(eachSet0.getString("USER_CODE"))){
										eachAdd.put(TableFeildSalaryTax, 0);
									}
								}
							}*/
						}
						if(((Boolean)eachAdd.get("bolBonusTax")) == true && !B_STAFF_IDENTList.contains(STAFF_IDENT)){
							B_STAFF_IDENTList.add(STAFF_IDENT);
							PageData getSumByStaffIdentBonus = new PageData();
							getSumByStaffIdentBonus.put("sqlSumByStaffIdent", sqlSumByUserCodeBonus1);
							getSumByStaffIdentBonus.put("STAFF_IDENT", STAFF_IDENT);
							PageData getSumBonus = sqlSession.selectOne("DataCalculation.getSumByStaffIdent",  getSumByStaffIdentBonus);
							BigDecimal checkBonusTaxConfigGrade = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigGradeOper).toString());
							BigDecimal getBonusTaxConfigSum = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigSumOper).toString());

							BigDecimal douBonusTAX_RATE = new BigDecimal(0);
							BigDecimal douBonusQUICK_DEDUCTION = new BigDecimal(0);
							if(tbStaffTax!=null){
								for(int i=0; i<tbStaffTax.size(); i++){
									StaffTax eachTax = tbStaffTax.get(i);
									//int a = bigdemical.compareTo(bigdemical2)
									//a = -1,表示bigdemical小于bigdemical2；
									//a = 0,表示bigdemical等于bigdemical2；
									//a = 1,表示bigdemical大于bigdemical2；
									BigDecimal eachBounsMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
									BigDecimal eachBounsMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
									
									int BonusMIN_VALUE = checkBonusTaxConfigGrade.compareTo(eachBounsMIN_VALUE.multiply(new BigDecimal(12)));
									int BonusMAX_VALUE = checkBonusTaxConfigGrade.compareTo(eachBounsMAX_VALUE.multiply(new BigDecimal(12)));
									if(BonusMIN_VALUE == 0 || BonusMIN_VALUE == 1){//eachMouth >= eachTax.getMIN_VALUE()
										if(i == tbStaffTax.size() -1){
											douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
											douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(BonusMAX_VALUE == 0 || BonusMAX_VALUE == -1){//eachMouth <= eachTax.getMAX_VALUE()
												douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}

							BigDecimal bd3Bonus = new BigDecimal(Double.toString(0.0100000));
							BigDecimal sumBonusTaxConfig = getBonusTaxConfigSum.multiply(douBonusTAX_RATE).multiply(bd3Bonus).subtract(douBonusQUICK_DEDUCTION);
							sumBonusTaxConfig = sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal sumBonusTaxSelf = new BigDecimal(getSumBonus.get(TableFeildBonusTaxSelfSumOper).toString());
							sumBonusTaxSelf = sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal douTableFeildBonusTax = sumBonusTaxConfig.subtract(sumBonusTaxSelf).add(addB_Tax);
							eachAdd.put(TableFeildBonusTax, douTableFeildBonusTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("B_YSZE", sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("B_YDRZE", sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						returnList.add(eachAdd);
					}
				} else {
					returnList = retList;
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
	/*public List<PageData> findDataCalculation(String tableNameBackup, String TmplUtil_KeyExtra,
			String TableFeildSalarySelf, String TableFeildSalaryTax, String TableFeildBonusSelf, String TableFeildBonusTax,
			String TableFeildSalaryTaxConfigGradeOper, String TableFeildBonusTaxConfigGradeOper,
			String TableFeildSalaryTaxConfigSumOper, String TableFeildBonusTaxConfigSumOper,
			String TableFeildSalaryTaxSelfSumOper, String TableFeildBonusTaxSelfSumOper,
			String sqlInsetBackup, PageData pdInsetBackup,
			String sqlBatchDelAndIns, 
			List<String> listSalaryFeildUpdate, String sqlRetSelect, List<PageData> listData,
			String sqlSumByUserCodeSalary,
			String sqlSumByUserCodeBonus_Not0, String sqlSumByUserCodeBonus_Same0)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<StaffTax> listStaffTax = sqlSession.selectList("DataCalculation.getStaffTax");
		sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
		
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			sqlSession.update(sqlInsetBackup, pdInsetBackup);
			if(listData!=null && !listData.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listData){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listData);
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
				List<PageData> retList = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(retList!=null){
					//Boolean bolBonusTax = true;
					//Boolean bolSalaryTax = true;
					for(PageData eachAdd : retList){
						BigDecimal selfBonus = new BigDecimal(eachAdd.get(TableFeildBonusSelf).toString());
						selfBonus = selfBonus.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax).toString());
						selfB_Tax = selfB_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildBonusTax, selfB_Tax);
						eachAdd.put(TableFeildBonusTax + TmplUtil_KeyExtra, selfB_Tax);
						if(selfBonus.compareTo(new BigDecimal(0)) == 0 && selfB_Tax.compareTo(new BigDecimal(0)) == 0){
							bolBonusTax = false;
						}
						
						BigDecimal selfSalary = new BigDecimal(eachAdd.get(TableFeildSalarySelf).toString());
						selfSalary = selfSalary.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax).toString());
						selfS_Tax = selfS_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildSalaryTax, selfS_Tax);
						eachAdd.put(TableFeildSalaryTax + TmplUtil_KeyExtra, selfS_Tax);
						if(selfSalary.compareTo(new BigDecimal(0)) == 0 && selfS_Tax.compareTo(new BigDecimal(0)) == 0){
							bolSalaryTax = false;
						}
					}
					List<String> B_UserCodeList = new ArrayList<String>();
					List<String> S_UserCodeList = new ArrayList<String>();
					for(PageData eachAdd : retList){
						String USER_CODE = eachAdd.getString("USER_CODE");
						eachAdd.put("B_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("B_YDRZE", new BigDecimal(0));//已导入纳税额
						eachAdd.put("S_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("S_YDRZE", new BigDecimal(0));//已导入纳税额
						eachAdd.put("B_CheckSalarySelf", "");
						//本条记录自己的税额
						BigDecimal addB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax + TmplUtil_KeyExtra).toString());
						BigDecimal addS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax + TmplUtil_KeyExtra).toString());

						if(bolSalaryTax == true && !S_UserCodeList.contains(USER_CODE)){
							S_UserCodeList.add(USER_CODE);
							
							PageData getSumByUserCodeSalary = new PageData();
							getSumByUserCodeSalary.put("sqlSumByUserCode", sqlSumByUserCodeSalary);
							getSumByUserCodeSalary.put("USER_CODE", USER_CODE);
							PageData getSumSalary = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCodeSalary);
							BigDecimal checkSalaryTaxConfigGrade = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigGradeOper).toString());
							BigDecimal getSalaryTaxConfigSum = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigSumOper).toString());

							BigDecimal douSalaryTAX_RATE = new BigDecimal(0);
							BigDecimal douSalaryQUICK_DEDUCTION = new BigDecimal(0);
							if(listStaffTax!=null){
								for(int i=0; i<listStaffTax.size(); i++){
									StaffTax eachTax = listStaffTax.get(i);
									//int a = bigdemical.compareTo(bigdemical2)
									//a = -1,表示bigdemical小于bigdemical2；
									//a = 0,表示bigdemical等于bigdemical2；
									//a = 1,表示bigdemical大于bigdemical2；
									BigDecimal eachMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
									BigDecimal eachMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
									
									int SalaryMIN_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMIN_VALUE);
									int SalaryMAX_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMAX_VALUE);
									if(SalaryMIN_VALUE == 0 || SalaryMIN_VALUE == 1){//sumNumCheck >= eachMIN_VALUE
										if(i == listStaffTax.size() -1){
											douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
											douSalaryQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(SalaryMAX_VALUE == 0 || SalaryMAX_VALUE == -1){//sumNumCheck <= eachTax.getMAX_VALUE()
												douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												douSalaryQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}

							BigDecimal bd3Salary = new BigDecimal(Double.toString(0.010000));
							BigDecimal sumSalaryTaxConfig = getSalaryTaxConfigSum.multiply(douSalaryTAX_RATE).multiply(bd3Salary).subtract(douSalaryQUICK_DEDUCTION);
							sumSalaryTaxConfig = sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal sumSalaryTaxSelf = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxSelfSumOper).toString());
							sumSalaryTaxSelf = sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal douTableFeildSalaryTax = sumSalaryTaxConfig.subtract(sumSalaryTaxSelf).add(addS_Tax);
							eachAdd.put(TableFeildSalaryTax, douTableFeildSalaryTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YSZE", sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YDRZE", sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						if(bolBonusTax == true && !B_UserCodeList.contains(USER_CODE)){
							B_UserCodeList.add(USER_CODE);

							PageData getSumByUserCodeSalary = new PageData();
							getSumByUserCodeSalary.put("sqlSumByUserCode", sqlSumByUserCodeSalary);
							getSumByUserCodeSalary.put("USER_CODE", USER_CODE);
							PageData getSumSalary = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCodeSalary);
							BigDecimal checkSalaryTaxSelfSumOper = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxSelfSumOper).toString());
							BigDecimal checkSalarySelf = new BigDecimal(getSumSalary.get(TableFeildSalarySelf).toString());
							
							if(checkSalarySelf.compareTo(new BigDecimal(0)) == 1){
								PageData getSumByUserCodeBonus = new PageData();
								if(checkSalaryTaxSelfSumOper.compareTo(new BigDecimal(0)) == 1){
									getSumByUserCodeBonus.put("sqlSumByUserCode", sqlSumByUserCodeBonus_Not0);
								} else {
									getSumByUserCodeBonus.put("sqlSumByUserCode", sqlSumByUserCodeBonus_Same0);
								}
								getSumByUserCodeBonus.put("USER_CODE", USER_CODE);
								PageData getSumBonus = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCodeBonus);
								BigDecimal checkBonusTaxConfigGrade = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigGradeOper).toString());
								BigDecimal getBonusTaxConfigSum = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigSumOper).toString());

								BigDecimal douBonusTAX_RATE = new BigDecimal(0);
								BigDecimal douBonusQUICK_DEDUCTION = new BigDecimal(0);
								if(listStaffTax!=null){
									for(int i=0; i<listStaffTax.size(); i++){
										StaffTax eachTax = listStaffTax.get(i);
										//int a = bigdemical.compareTo(bigdemical2)
										//a = -1,表示bigdemical小于bigdemical2；
										//a = 0,表示bigdemical等于bigdemical2；
										//a = 1,表示bigdemical大于bigdemical2；
										BigDecimal eachBounsMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
										BigDecimal eachBounsMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
										
										int BonusMIN_VALUE = checkBonusTaxConfigGrade.compareTo(eachBounsMIN_VALUE.multiply(new BigDecimal(12)));
										int BonusMAX_VALUE = checkBonusTaxConfigGrade.compareTo(eachBounsMAX_VALUE.multiply(new BigDecimal(12)));
										if(BonusMIN_VALUE == 0 || BonusMIN_VALUE == 1){//eachMouth >= eachTax.getMIN_VALUE()
											if(i == listStaffTax.size() -1){
												douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											} else {
												if(BonusMAX_VALUE == 0 || BonusMAX_VALUE == -1){//eachMouth <= eachTax.getMAX_VALUE()
													douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
													douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
												}
											}
										}
									}
								}

								BigDecimal bd3Bonus = new BigDecimal(Double.toString(0.0100000));
								BigDecimal sumBonusTaxConfig = getBonusTaxConfigSum.multiply(douBonusTAX_RATE).multiply(bd3Bonus).subtract(douBonusQUICK_DEDUCTION);
								sumBonusTaxConfig = sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
								BigDecimal sumBonusTaxSelf = new BigDecimal(getSumBonus.get(TableFeildBonusTaxSelfSumOper).toString());
								sumBonusTaxSelf = sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
								BigDecimal douTableFeildBonusTax = sumBonusTaxConfig.subtract(sumBonusTaxSelf).add(addB_Tax);
								eachAdd.put(TableFeildBonusTax, douTableFeildBonusTax.setScale(2, BigDecimal.ROUND_HALF_UP));
								eachAdd.put("B_YSZE", sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
								eachAdd.put("B_YDRZE", sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
							} else {
								eachAdd.put("B_CheckSalarySelf", "必须有工资信息(应发合计)才可导入奖金！");
							}
							
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
	}*/
	/*public List<PageData> findDataCalculation(String tableNameBackup, String TmplUtil_KeyExtra,
			String TableFeildSalarySelf, String TableFeildSalaryTax, String TableFeildBonusSelf, String TableFeildBonusTax,
			String TableFeildSalaryTaxConfigGradeOper, String TableFeildBonusTaxConfigGradeOper,
			String TableFeildSalaryTaxConfigSumOper, String TableFeildBonusTaxConfigSumOper,
			String TableFeildSalaryTaxSelfSumOper, String TableFeildBonusTaxSelfSumOper,
			String sqlInsetBackup, PageData pdInsetBackup,
			String sqlBatchDelAndIns, 
			List<String> listSalaryFeildUpdate, String sqlRetSelect, List<PageData> listData,
			String sqlSumByUserCodeSalary,
			String sqlSumByUserCodeBonus)throws Exception{
		SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
		//批量执行器
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		List<StaffTax> listStaffTax = sqlSession.selectList("DataCalculation.getStaffTax");
		sqlSession.delete("DataCalculation.deleteTableData", tableNameBackup);
		
		List<PageData> returnList = new ArrayList<PageData>();
		try{
			sqlSession.update(sqlInsetBackup, pdInsetBackup);
			if(listData!=null && !listData.isEmpty()){
				Integer strMaxNum = sqlSession.selectOne("DataCalculation.getMaxSerialNo", tableNameBackup);
				String SqlInBillCode = "";
				for(PageData eachPd : listData){
					String SERIAL_NO = eachPd.getString("SERIAL_NO");
					if(SERIAL_NO!=null && !SERIAL_NO.trim().equals("")){
						if(SqlInBillCode!=null && !SqlInBillCode.trim().equals("")){
							SqlInBillCode += ",";
						}
						SqlInBillCode += SERIAL_NO;
					}
				}
				sqlSession.update(sqlBatchDelAndIns, listData);
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
				List<PageData> retList = sqlSession.selectList("DataCalculation.getListBySerialNo",  getListBySerialNo);
				if(retList!=null){
					Boolean bolBonusTax = true;
					Boolean bolSalaryTax = true;
					for(PageData eachAdd : retList){
						BigDecimal selfBonus = new BigDecimal(eachAdd.get(TableFeildBonusSelf).toString());
						selfBonus = selfBonus.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax).toString());
						selfB_Tax = selfB_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildBonusTax, selfB_Tax);
						eachAdd.put(TableFeildBonusTax + TmplUtil_KeyExtra, selfB_Tax);
						if(selfBonus.compareTo(new BigDecimal(0)) == 0 && selfB_Tax.compareTo(new BigDecimal(0)) == 0){
							bolBonusTax = false;
						}
						
						BigDecimal selfSalary = new BigDecimal(eachAdd.get(TableFeildSalarySelf).toString());
						selfSalary = selfSalary.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal selfS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax).toString());
						selfS_Tax = selfS_Tax.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachAdd.put(TableFeildSalaryTax, selfS_Tax);
						eachAdd.put(TableFeildSalaryTax + TmplUtil_KeyExtra, selfS_Tax);
						if(selfSalary.compareTo(new BigDecimal(0)) == 0 && selfS_Tax.compareTo(new BigDecimal(0)) == 0){
							bolSalaryTax = false;
						}
					}
					List<String> B_UserCodeList = new ArrayList<String>();
					List<String> S_UserCodeList = new ArrayList<String>();
					for(PageData eachAdd : retList){
						String USER_CODE = eachAdd.getString("USER_CODE");
						eachAdd.put("B_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("B_YDRZE", new BigDecimal(0));//已导入纳税额
						eachAdd.put("S_YSZE", new BigDecimal(0));//应税总额
						eachAdd.put("S_YDRZE", new BigDecimal(0));//已导入纳税额
						//本条记录自己的税额
						BigDecimal addB_Tax = new BigDecimal(eachAdd.get(TableFeildBonusTax + TmplUtil_KeyExtra).toString());
						BigDecimal addS_Tax = new BigDecimal(eachAdd.get(TableFeildSalaryTax + TmplUtil_KeyExtra).toString());

						if(bolSalaryTax == true && !S_UserCodeList.contains(USER_CODE)){
							S_UserCodeList.add(USER_CODE);
							
							PageData getSumByUserCodeSalary = new PageData();
							getSumByUserCodeSalary.put("sqlSumByUserCode", sqlSumByUserCodeSalary);
							getSumByUserCodeSalary.put("USER_CODE", USER_CODE);
							PageData getSumSalary = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCodeSalary);
							BigDecimal checkSalaryTaxConfigGrade = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigGradeOper).toString());
							BigDecimal getSalaryTaxConfigSum = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxConfigSumOper).toString());

							BigDecimal douSalaryTAX_RATE = new BigDecimal(0);
							BigDecimal douSalaryQUICK_DEDUCTION = new BigDecimal(0);
							if(listStaffTax!=null){
								for(int i=0; i<listStaffTax.size(); i++){
									StaffTax eachTax = listStaffTax.get(i);
									//int a = bigdemical.compareTo(bigdemical2)
									//a = -1,表示bigdemical小于bigdemical2；
									//a = 0,表示bigdemical等于bigdemical2；
									//a = 1,表示bigdemical大于bigdemical2；
									BigDecimal eachMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
									BigDecimal eachMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
									
									int SalaryMIN_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMIN_VALUE);
									int SalaryMAX_VALUE = checkSalaryTaxConfigGrade.compareTo(eachMAX_VALUE);
									if(SalaryMIN_VALUE == 0 || SalaryMIN_VALUE == 1){//sumNumCheck >= eachMIN_VALUE
										if(i == listStaffTax.size() -1){
											douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
											douSalaryQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(SalaryMAX_VALUE == 0 || SalaryMAX_VALUE == -1){//sumNumCheck <= eachTax.getMAX_VALUE()
												douSalaryTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												douSalaryQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}

							BigDecimal bd3Salary = new BigDecimal(Double.toString(0.010000));
							BigDecimal sumSalaryTaxConfig = getSalaryTaxConfigSum.multiply(douSalaryTAX_RATE).multiply(bd3Salary).subtract(douSalaryQUICK_DEDUCTION);
							sumSalaryTaxConfig = sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal sumSalaryTaxSelf = new BigDecimal(getSumSalary.get(TableFeildSalaryTaxSelfSumOper).toString());
							sumSalaryTaxSelf = sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal douTableFeildSalaryTax = sumSalaryTaxConfig.subtract(sumSalaryTaxSelf).add(addS_Tax);
							eachAdd.put(TableFeildSalaryTax, douTableFeildSalaryTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YSZE", sumSalaryTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("S_YDRZE", sumSalaryTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						if(bolBonusTax == true && !B_UserCodeList.contains(USER_CODE)){
							B_UserCodeList.add(USER_CODE);
							
							PageData getSumByUserCodeBonus = new PageData();
							getSumByUserCodeBonus.put("sqlSumByUserCode", sqlSumByUserCodeBonus);
							getSumByUserCodeBonus.put("USER_CODE", USER_CODE);
							PageData getSumBonus = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumByUserCodeBonus);
							BigDecimal checkBonusTaxConfigGrade = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigGradeOper).toString());
							BigDecimal getBonusTaxConfigSum = new BigDecimal(getSumBonus.get(TableFeildBonusTaxConfigSumOper).toString());

							BigDecimal douBonusTAX_RATE = new BigDecimal(0);
							BigDecimal douBonusQUICK_DEDUCTION = new BigDecimal(0);
							if(listStaffTax!=null){
								for(int i=0; i<listStaffTax.size(); i++){
									StaffTax eachTax = listStaffTax.get(i);
									//int a = bigdemical.compareTo(bigdemical2)
									//a = -1,表示bigdemical小于bigdemical2；
									//a = 0,表示bigdemical等于bigdemical2；
									//a = 1,表示bigdemical大于bigdemical2；
									BigDecimal eachMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
									BigDecimal eachMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
									
									int BonusMIN_VALUE = checkBonusTaxConfigGrade.compareTo(eachMIN_VALUE);
									int BonusMAX_VALUE = checkBonusTaxConfigGrade.compareTo(eachMAX_VALUE);
									if(BonusMIN_VALUE == 0 || BonusMIN_VALUE == 1){//eachMouth >= eachTax.getMIN_VALUE()
										if(i == listStaffTax.size() -1){
											douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
											douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
										} else {
											if(BonusMAX_VALUE == 0 || BonusMAX_VALUE == -1){//eachMouth <= eachTax.getMAX_VALUE()
												douBonusTAX_RATE = new BigDecimal(Double.toString(eachTax.getTAX_RATE()));
												douBonusQUICK_DEDUCTION = new BigDecimal(Double.toString(eachTax.getQUICK_DEDUCTION()));
											}
										}
									}
								}
							}

							BigDecimal bd3Bonus = new BigDecimal(Double.toString(0.0100000));
							BigDecimal sumBonusTaxConfig = getBonusTaxConfigSum.multiply(douBonusTAX_RATE).multiply(bd3Bonus).subtract(douBonusQUICK_DEDUCTION);
							sumBonusTaxConfig = sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal sumBonusTaxSelf = new BigDecimal(getSumBonus.get(TableFeildBonusTaxSelfSumOper).toString());
							sumBonusTaxSelf = sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP);
							BigDecimal douTableFeildBonusTax = sumBonusTaxConfig.subtract(sumBonusTaxSelf).add(addB_Tax);
							eachAdd.put(TableFeildBonusTax, douTableFeildBonusTax.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("B_YSZE", sumBonusTaxConfig.setScale(2, BigDecimal.ROUND_HALF_UP));
							eachAdd.put("B_YDRZE", sumBonusTaxSelf.setScale(2, BigDecimal.ROUND_HALF_UP));
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
	}*/

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
							BigDecimal decGROSS_PAY = new BigDecimal(eachAdd.get("GROSS_PAY").toString());
							BigDecimal decACCRD_TAX = new BigDecimal(eachAdd.get("ACCRD_TAX").toString());
							BigDecimal GROSS_PAY_ACCRD_TAX = decGROSS_PAY.subtract(decACCRD_TAX);
							eachAdd.put("ACT_SALY", GROSS_PAY_ACCRD_TAX.setScale(2, BigDecimal.ROUND_HALF_UP));
							continue;
						}
						
						BigDecimal addTax = new BigDecimal(eachAdd.get("ACCRD_TAX").toString());

						PageData getSumGroupBy = new PageData();
						//getSumGroupBy.put("sqlSumByUserCode", sqlSumByUserCode);
						getSumGroupBy.put("sqlSumByUserNameStaffIdent", sqlSumByUserNameStaffIdent);
						//getSumGroupBy.put("USER_CODE", USER_CODE);
						getSumGroupBy.put("USER_NAME", USER_NAME);
						getSumGroupBy.put("STAFF_IDENT", STAFF_IDENT);
						//PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserCode",  getSumGroupBy);
						PageData getSum = sqlSession.selectOne("DataCalculation.getSumByUserNameStaffIdent",  getSumGroupBy);
						//所有记录计算的税额
						BigDecimal sumNumCheck = new BigDecimal(getSum.get("GROSS_PAY").toString());
						String strTAX_FORMULA = "";
						if(listLaborTax!=null){
							for(int i_listLaborTax=0; i_listLaborTax<listLaborTax.size(); i_listLaborTax++){
								LaborTax eachTax = listLaborTax.get(i_listLaborTax);
								//int a = bigdemical.compareTo(bigdemical2)
								//a = -1,表示bigdemical小于bigdemical2；
								//a = 0,表示bigdemical等于bigdemical2；
								//a = 1,表示bigdemical大于bigdemical2；
								BigDecimal eachMIN_VALUE = new BigDecimal(Double.toString(eachTax.getMIN_VALUE()));
								BigDecimal eachMAX_VALUE = new BigDecimal(Double.toString(eachTax.getMAX_VALUE()));
								int aMIN_VALUE = sumNumCheck.compareTo(eachMIN_VALUE);
								int aMAX_VALUE = sumNumCheck.compareTo(eachMAX_VALUE);
								if(aMIN_VALUE == 0 || aMIN_VALUE == 1){//sumNumCheck >= eachTax.getMIN_VALUE()
									if(i_listLaborTax == listLaborTax.size() -1){
										strTAX_FORMULA = eachTax.getTAX_FORMULA();
									} else {
										if(aMAX_VALUE == 0 || aMAX_VALUE == -1){//sumNumCheck <= eachTax.getMAX_VALUE()
											strTAX_FORMULA = eachTax.getTAX_FORMULA();
										}
									}
								}
							}
						}
						BigDecimal sumNum = new BigDecimal(0);
						if(strTAX_FORMULA!=null && !strTAX_FORMULA.trim().equals("")){
							PageData getTaxFormula = new PageData();
							getTaxFormula.put("taxFormula", strTAX_FORMULA.replace("累计应发评审费", String.valueOf(sumNumCheck)) + " TaxFormulaValue ");
							getTaxFormula.put("tableName", tableNameBackup);
							List<PageData> getListTaxFormula = sqlSession.selectList("DataCalculation.getTaxFormula",  getTaxFormula);
							sumNum = new BigDecimal(getListTaxFormula.get(0).get("TaxFormulaValue").toString());
						}
						//所有记录汇总的税额
						BigDecimal sumTax = new BigDecimal(getSum.get("ACCRD_TAX").toString());
						BigDecimal douTableFeildTax = sumNum.subtract(sumTax).add(addTax);
						eachAdd.put("ACCRD_TAX", douTableFeildTax.setScale(2, BigDecimal.ROUND_HALF_UP));

						BigDecimal decGROSS_PAY = new BigDecimal(eachAdd.get("GROSS_PAY").toString());
						BigDecimal decACCRD_TAX = new BigDecimal(eachAdd.get("ACCRD_TAX").toString());
						BigDecimal GROSS_PAY_ACCRD_TAX = decGROSS_PAY.subtract(decACCRD_TAX);
						eachAdd.put("ACT_SALY", GROSS_PAY_ACCRD_TAX.setScale(2, BigDecimal.ROUND_HALF_UP));
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


