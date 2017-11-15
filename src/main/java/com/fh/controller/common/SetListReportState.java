package com.fh.controller.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fh.service.sysSealedInfo.syssealedinfo.impl.SysSealedInfoService;
import com.fh.util.PageData;
import com.fh.util.enums.DurState;

/**
 * 
 * 
 * @ClassName: SqlInString
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 张晓柳
 * @date 2017年8月18日
 *
 */
public class SetListReportState {
	
	public static StringBuilder GetHaveReportStateSql(String AdditionalReportColumn, 
			String TableNameBase, String TypeCodeSummy) throws Exception{
		String strReportTableName = "tb_sys_sealed_info";
		StringBuilder sbSpellingTableName = new StringBuilder();
		sbSpellingTableName.append(" ( ");
		sbSpellingTableName.append("  select t.*, IFNULL(rep.state, '" + DurState.Release.getNameKey() + "') " + AdditionalReportColumn + " ");
		sbSpellingTableName.append("  from " + TableNameBase + " t ");
		sbSpellingTableName.append("  left join " + strReportTableName + " rep ");
		sbSpellingTableName.append("  on t.DEPT_CODE = rep.RPT_DEPT ");
		sbSpellingTableName.append("  and t.CUST_COL7 = rep.BILL_OFF ");
		sbSpellingTableName.append("  and t.BUSI_DATE = rep.RPT_DUR ");
		sbSpellingTableName.append("  and rep.BILL_TYPE = '" + TypeCodeSummy + "' ");
		sbSpellingTableName.append("  )  spill ");
		return sbSpellingTableName;
	}
}
