package com.fh.entity;

public class ClsTwoFeild{
	
	private String SqlSelectFeild; 
	private String SqlWhere;
	public ClsTwoFeild(){
		SqlSelectFeild = "";
		SqlWhere = "";
	}
	public String getSqlSelectFeild() {
		return SqlSelectFeild;
	}
	public void setSqlSelectFeild(String sqlSelectFeild) {
		SqlSelectFeild = sqlSelectFeild;
	}
	public String getSqlWhere() {
		return SqlWhere;
	}
	public void setSqlWhere(String sqlWhere) {
		SqlWhere = sqlWhere;
	} 
    
}