package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 首页各组的配置数据!!
 * @author xch
 *
 */
public class DeskTopNewGroupDefineVO implements Serializable {

	private static final long serialVersionUID = -6517657044334779204L;

	private String title; //标题

	private String datatype; //数据类型,有文字,柱形图,曲线图,饼图
	private String viewcols; //有[半列/全列]

	private String imgicon; //图标
	private String logoimg; //图标

	private String databuildername; //数据生成类
	private String templetcode; //默认模板编码
	private String actionname; //自定义动作名称
	private String linkmenu; //关联的菜单

	private String capimg; //帽子图片!!即该组内容上面还可以有个腰带一样的图片,就像sohu,sina等网站一样
	private String titlecolor; //标题字的颜色,中铁建的王部长说有的组的标题颜色是红色的!!
	private int newcount; //前几条新闻后面跟一个new图标!
	private String isLazyLoad; //是否懒装入,首页的内容太多时,需要搞一个线程加载,保证界面先出来!!!
	
	private HashMap otherconfig;  //其他参数(有效期限=30;有效期限匹配字段=publishdate)【杨科/2012-07-30】

	private String descr;

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgicon() {
		return imgicon;
	}

	public void setImgicon(String imgicon) {
		this.imgicon = imgicon;
	}

	public String getLogoimg() {
		return logoimg;
	}

	public void setLogoimg(String logoimg) {
		this.logoimg = logoimg;
	}

	public String getDatabuildername() {
		return databuildername;
	}

	public void setDatabuildername(String databuildername) {
		this.databuildername = databuildername;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getActionname() {
		return actionname;
	}

	public void setActionname(String actionname) {
		this.actionname = actionname;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getViewcols() {
		return viewcols;
	}

	public void setViewcols(String viewcols) {
		this.viewcols = viewcols;
	}

	public String getLinkmenu() {
		return linkmenu;
	}

	public void setLinkmenu(String linkmenu) {
		this.linkmenu = linkmenu;
	}

	public String getCapimg() {
		return capimg;
	}

	public void setCapimg(String capimg) {
		this.capimg = capimg;
	}

	public int getNewcount() {
		return newcount;
	}

	public void setNewcount(int _newcount) {
		this.newcount = _newcount;
	}

	public String getTitlecolor() {
		return titlecolor;
	}

	public void setTitlecolor(String titlecolor) {
		this.titlecolor = titlecolor;
	}

	public String getIsLazyLoad() {
		return isLazyLoad;
	}

	public void setIsLazyLoad(String isLazyLoad) {
		this.isLazyLoad = isLazyLoad;
	}

	public HashMap getOtherconfig() {
		return otherconfig;
	}

	public void setOtherconfig(HashMap otherconfig) {
		this.otherconfig = otherconfig;
	}

}
