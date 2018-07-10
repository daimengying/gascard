/**
 * 
 */
package com.lirong.gascard.vo;

import java.io.Serializable;
import java.util.List;


/**
 * @author： mydai
 * @Date：2018年4月1日 上午9:44:35
 * @Description：分页相关
 */
public class PageResultForBootstrap<T> implements Serializable{


	private static final long serialVersionUID = -3585653350456566843L;
	//request params
	private String opertype;//查询类型
	private String begintime;//开始时间
	private String endtime;//结束时间
	private String opercontext;//
	private int pageSize;//查询的行数
	private int pageNumber;//页数
	private String searchText;//搜索内容
	private String sortName;//排序相关
	private String sortOrder ;//排序相关
	//response params
	private int total;
	private List<T> rows;
	public String getOpertype() {
		return opertype;
	}
	public void setOpertype(String opertype) {
		this.opertype = opertype;
	}
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getOpercontext() {
		return opercontext;
	}
	public void setOpercontext(String opercontext) {
		this.opercontext = opercontext;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
}
