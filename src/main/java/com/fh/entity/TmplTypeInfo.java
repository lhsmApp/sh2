package com.fh.entity;

import java.util.List;

public class TmplTypeInfo {

	private String TypeCodeDetail;			
	private String TypeCodeSummy;	
	private String TypeCodeListen;
	
	//汇总
	private List<String> SumField;
	private String SumFieldToString;//QueryFeildString.tranferListStringToGroupbyString(SumField);
	
	//核算
    //分组字段
	private String GroupbyFeild;
	//分组字段list  查询表的主键字段，作为标准列，jqgrid添加带__列，mybaits获取带__列
	private List<String> keyListBase;
	
	public String getTypeCodeDetail() {
		return TypeCodeDetail;
	}
	public void setTypeCodeDetail(String typeCodeDetail) {
		TypeCodeDetail = typeCodeDetail;
	}
	public String getTypeCodeSummy() {
		return TypeCodeSummy;
	}
	public void setTypeCodeSummy(String typeCodeSummy) {
		TypeCodeSummy = typeCodeSummy;
	}
	public String getTypeCodeListen() {
		return TypeCodeListen;
	}
	public void setTypeCodeListen(String typeCodeListen) {
		TypeCodeListen = typeCodeListen;
	}
	public List<String> getSumField() {
		return SumField;
	}
	public void setSumField(List<String> sumField) {
		SumField = sumField;
	}
	public String getSumFieldToString() {
		return SumFieldToString;
	}
	public void setSumFieldToString(String sumFieldToString) {
		SumFieldToString = sumFieldToString;
	}
	public String getGroupbyFeild() {
		return GroupbyFeild;
	}
	public void setGroupbyFeild(String groupbyFeild) {
		GroupbyFeild = groupbyFeild;
	}
	public List<String> getKeyListBase() {
		return keyListBase;
	}
	public void setKeyListBase(List<String> keyListBase) {
		this.keyListBase = keyListBase;
	}	

}
