package com.fh.controller.common;

import com.fh.util.enums.BillNumType;
import com.fh.util.enums.PZTYPE;

/**
 * 模板通用类
 * 
 * @ClassName: VoucherToBillType
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhangxiaoliu
 * @date 2018年04月19日
 *
 */
public class VoucherToBillType {

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getVoucherToBillType(String SelectedTypeCode) {
		String strReturn = "";
		if (SelectedTypeCode != null && !SelectedTypeCode.trim().equals("")) { 
			if(SelectedTypeCode.equals(PZTYPE.GFZYJF.getNameKey())){
				strReturn = BillNumType.GZJF;// 1工会经费、教育经费凭证 
			} else if(SelectedTypeCode.equals(PZTYPE.DF.getNameKey())){
				strReturn = BillNumType.DFJT;// 2党费凭证
			}  else if(SelectedTypeCode.equals(PZTYPE.SB.getNameKey())){
				strReturn = BillNumType.SBHT;// 3社保互推凭证
			} else if(SelectedTypeCode.equals(PZTYPE.GJJ.getNameKey())){
				strReturn = BillNumType.ZHHT;// 4公积金互推凭证
			} else if(SelectedTypeCode.equals(PZTYPE.GJ.getNameKey())){
				strReturn = BillNumType.SBGJ;// 5个缴凭证
			} else if(SelectedTypeCode.equals(PZTYPE.YFLWF.getNameKey())){
				strReturn = BillNumType.YFLW;// 6应付劳务费凭证
			} else if(SelectedTypeCode.equals(PZTYPE.QYNJTQ.getNameKey())){
				strReturn = BillNumType.NJTQ;// 7企业年金提取凭证
			} else if(SelectedTypeCode.equals(PZTYPE.BCYLTQ.getNameKey())){
				strReturn = BillNumType.YLTQ;// 8补充医疗提取凭证
			} else if(SelectedTypeCode.equals(PZTYPE.QYNJFF.getNameKey())){
				strReturn = BillNumType.NJFF;// 9企业年金发放凭证
			} else if(SelectedTypeCode.equals(PZTYPE.PGTZ.getNameKey())){
				strReturn = BillNumType.PGTZ;// 10评估调整凭证
			}
		}
		return strReturn;
	}
}
