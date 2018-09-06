package com.fh.controller.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fh.entity.TableColumns;
import com.fh.entity.TmplConfigDetail;
import com.fh.service.detailimportcommon.detailimportcommon.impl.DetailImportCommonService;
import com.fh.service.glItemUser.glItemUser.GlItemUserManager;
import com.fh.service.tmplconfig.tmplconfig.impl.TmplConfigService;
import com.fh.util.DecimalUtil;
import com.fh.util.PageData;

/**
 * 获取分摊数据
 * 
 * @ClassName: ItemAlloc
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 2018年9月5日
 *
 */
public class ItemAlloc {

	@SuppressWarnings("unchecked")
	public static List<PageData> getSetItemUser(String SystemDateTime, String QueryFeild, String TableNameSecondDetail, 
			List<String> DetailSerialNoFeild, List<String> DetailUserCodeFeild, 
			List<String> SumFieldBill, DetailImportCommonService detailimportcommonService) throws Exception {
		PageData pdSetItemUser = new PageData();
		pdSetItemUser.put("SystemDateTime", SystemDateTime);
		pdSetItemUser.put("QueryFeild", QueryFeild);
		pdSetItemUser.put("TableName", TableNameSecondDetail);
		List<String> retSetItemUserFeild = new ArrayList<String>();
		for(String strfeild : DetailSerialNoFeild){
			retSetItemUserFeild.add(strfeild);
		}
		for(String strfeild : DetailUserCodeFeild){
			retSetItemUserFeild.add(strfeild);
		}
		retSetItemUserFeild = QueryFeildString.extraSumField(retSetItemUserFeild, SumFieldBill);
		String SelectFeildSetItemUser = QueryFeildString.tranferListStringToKeyString(retSetItemUserFeild, TmplUtil.keyExtra);
		pdSetItemUser.put("SelectFeild", " *, " + SelectFeildSetItemUser);
		List<PageData> getSetItemUser = detailimportcommonService.getSum(pdSetItemUser);
		return getSetItemUser;
	}

	public static void getSaveItem(List<PageData> getSaveItem, 
			String SystemDateTime, String strGetSetItemDeptCode, GlItemUserManager glItemUserService,
			String TypeCodeDetail, TmplConfigService tmplconfigService,
			List<PageData> getSetItemUser, 
			String SelectedCustCol7, String TableNameFirstItem) throws Exception {
		PageData pdGlItemUser = new PageData();
		pdGlItemUser.put("BUSI_DATE", SystemDateTime);
		pdGlItemUser.put("DEPT_CODE", strGetSetItemDeptCode);
	    List<String> ItemQueryFeildList = Arrays.asList("BUSI_DATE", "DEPT_CODE");
		String ItemQueryFeild = QueryFeildString.getQueryFeild(pdGlItemUser, ItemQueryFeildList);
		pdGlItemUser.put("QueryFeild", ItemQueryFeild);
		List<PageData> getGlItemUserList = glItemUserService.getSumUseList(pdGlItemUser);

		if(getGlItemUserList!=null && getGlItemUserList.size()>0){
			Map<String, TableColumns> map_HaveColumnsListDetail = Common.GetHaveColumnsList(TypeCodeDetail, tmplconfigService);
			for(PageData getItem : getSetItemUser){
				String strItemBusiDate = getItem.getString("BUSI_DATE" + TmplUtil.keyExtra);
				String strItemDepartCode = getItem.getString("DEPT_CODE" + TmplUtil.keyExtra);
				String strItemUserCode = getItem.getString("USER_CODE" + TmplUtil.keyExtra);
		        for(PageData glItemUser : getGlItemUserList){
    				String strGlBusiDate = glItemUser.getString("BUSI_DATE");
    				String strGlDepartCode = glItemUser.getString("DEPT_CODE");
    				String strGlUserCode = glItemUser.getString("USER_CODE");
    				if(strItemBusiDate.equals(strGlBusiDate) && strItemDepartCode.equals(strGlDepartCode) && strItemUserCode.equals(strGlUserCode)){
    					String ITEM1_CODE = glItemUser.getString("ITEM1_CODE");
    					String ITEM2_CODE = glItemUser.getString("ITEM2_CODE");
    					String ITEM3_CODE = glItemUser.getString("ITEM3_CODE");
    					String ITEM4_CODE = glItemUser.getString("ITEM4_CODE");
    					String ITEM5_CODE = glItemUser.getString("ITEM5_CODE");
    					double douItemBudSum = 0;
    					List<PageData> listItemUser = new ArrayList<PageData>();
    					
    					if(ITEM1_CODE!=null && !ITEM1_CODE.trim().equals("")) {
    						String ITEM1_BUD = glItemUser.getString("ITEM1_BUD");
    						douItemBudSum += Double.parseDouble(ITEM1_BUD);
    						PageData pdItemUser = new PageData();
    						pdItemUser.put("ITEM_CODE", ITEM1_CODE);
    						pdItemUser.put("ITEM_BUD", ITEM1_BUD);
    						listItemUser.add(pdItemUser);
    					}
    					if(ITEM2_CODE!=null && !ITEM2_CODE.trim().equals("")) {
    						String ITEM2_BUD = glItemUser.getString("ITEM2_BUD");
    						douItemBudSum += Double.parseDouble(ITEM2_BUD);
    						PageData pdItemUser = new PageData();
    						pdItemUser.put("ITEM_CODE", ITEM2_CODE);
    						pdItemUser.put("ITEM_BUD", ITEM2_BUD);
    						listItemUser.add(pdItemUser);
    					}
    					if(ITEM3_CODE!=null && !ITEM3_CODE.trim().equals("")) {
    						String ITEM3_BUD = glItemUser.getString("ITEM3_BUD");
    						douItemBudSum += Double.parseDouble(ITEM3_BUD);
    						PageData pdItemUser = new PageData();
    						pdItemUser.put("ITEM_CODE", ITEM3_CODE);
    						pdItemUser.put("ITEM_BUD", ITEM3_BUD);
    						listItemUser.add(pdItemUser);
    					}
    					if(ITEM4_CODE!=null && !ITEM4_CODE.trim().equals("")) {
    						String ITEM4_BUD = glItemUser.getString("ITEM4_BUD");
    						douItemBudSum += Double.parseDouble(ITEM4_BUD);
    						PageData pdItemUser = new PageData();
    						pdItemUser.put("ITEM_CODE", ITEM4_CODE);
    						pdItemUser.put("ITEM_BUD", ITEM4_BUD);
    						listItemUser.add(pdItemUser);
    					}
    					if(ITEM5_CODE!=null && !ITEM5_CODE.trim().equals("")) {
    						String ITEM5_BUD = glItemUser.getString("ITEM5_BUD");
    						douItemBudSum += Double.parseDouble(ITEM5_BUD);
    						PageData pdItemUser = new PageData();
    						pdItemUser.put("ITEM_CODE", ITEM5_CODE);
    						pdItemUser.put("ITEM_BUD", ITEM5_BUD);
    						listItemUser.add(pdItemUser);
    					}
    					if(listItemUser!=null && listItemUser.size()>0){
    						Map<String, TmplConfigDetail> map_SetColumnsListDetail = Common.GetSetColumnsList(TypeCodeDetail, strItemDepartCode, SelectedCustCol7, tmplconfigService);
    						List<PageData> listAddSaveItem = new ArrayList<PageData>();
    						for(int i=0; i<listItemUser.size(); i++){
        						PageData pdItemAdd = Common.copyPdToOther(getItem, map_HaveColumnsListDetail);
    							PageData pdItemUser = listItemUser.get(i);
								pdItemAdd.put("ITEM_CODE", pdItemUser.get("ITEM_CODE"));
								pdItemAdd.put("GlItemUserItemBud", pdItemUser.get("ITEM_BUD"));
								listAddSaveItem.add(pdItemAdd);
    						}
    					    for (TableColumns column : map_HaveColumnsListDetail.values()) {
    					    	String column_name = column.getColumn_name().toUpperCase();
    					    	String data_type = column.getData_type().toUpperCase();
    							if(Common.IsNumFeild(data_type)){
    								double douGetValue = Double.parseDouble(getItem.getString(column_name));
    								double douGetBud = Double.parseDouble(getItem.getString("GlItemUserItemBud"));
    								double douValueLast = douGetValue;
            						for(int i=0; i<listAddSaveItem.size(); i++){
            							PageData pdSaveItem = listAddSaveItem.get(i);
        								double douItemAdd = 0;
        								if(!(douItemBudSum == 0 || douGetValue == 0)){
        									douItemAdd = DecimalUtil.InterceptTwoDecimalDigits(douGetValue * douGetBud / douItemBudSum);
                							if(i == listItemUser.size()-1){
                								douItemAdd = douValueLast;
                							} else {
            									douValueLast -= douItemAdd;
                							}
        								}
        								pdSaveItem.put(column_name, douItemAdd);
            						}
    							}
    					    }
    						for(PageData pdItemAdd : listAddSaveItem){
    							pdItemAdd.put("TableName", TableNameFirstItem);
    	                        //添加未设置字段默认值
    	            			Common.setModelDefault(pdItemAdd, map_HaveColumnsListDetail, map_SetColumnsListDetail, null);
    	            			getSaveItem.add(pdItemAdd);
    						}
    					}
    				}
    			}
			}
		}
	}
}
