package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ��ҳ�������������!!
 * @author xch
 *
 */
public class DeskTopNewGroupDefineVO implements Serializable {

	private static final long serialVersionUID = -6517657044334779204L;

	private String title; //����

	private String datatype; //��������,������,����ͼ,����ͼ,��ͼ
	private String viewcols; //��[����/ȫ��]

	private String imgicon; //ͼ��
	private String logoimg; //ͼ��

	private String databuildername; //����������
	private String templetcode; //Ĭ��ģ�����
	private String actionname; //�Զ��嶯������
	private String linkmenu; //�����Ĳ˵�

	private String capimg; //ñ��ͼƬ!!�������������滹�����и�����һ����ͼƬ,����sohu,sina����վһ��
	private String titlecolor; //�����ֵ���ɫ,��������������˵�е���ı�����ɫ�Ǻ�ɫ��!!
	private int newcount; //ǰ�������ź����һ��newͼ��!
	private String isLazyLoad; //�Ƿ���װ��,��ҳ������̫��ʱ,��Ҫ��һ���̼߳���,��֤�����ȳ���!!!
	
	private HashMap otherconfig;  //��������(��Ч����=30;��Ч����ƥ���ֶ�=publishdate)�����/2012-07-30��

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
