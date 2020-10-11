package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

/**
 * ������ͼ����������ݶ���..
 * @author xch
 *
 */
public class GroupVO implements Serializable {

	private static final long serialVersionUID = -4900363756753098731L;

	private Integer id = null;
	private String processid;
	private String grouptype = null; //������,���Ǻ���Ļ��������,ȡֵ��Χ��DEPT/STATION

	private String code = null; //����
	private String wfname = null; //����
	private String uiname = null; //����

	private Integer x = null; //X����
	private Integer y = null; //Y����

	private Integer width = null; //���
	private Integer height = null; //�߶�

	//��������ɫ
	private String fonttype; //��������
	private Integer fontsize; //�����С
	private String foreground; //ǰ����ɫ
	private String background; //������ɫ

	private String descr = null; //��ע˵��

	private String posts;//��λ

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public String getUiname() {
		return uiname;
	}

	public void setUiname(String uiname) {
		this.uiname = uiname;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getGrouptype() {
		return grouptype;
	}

	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
	}

	public String toString() {
		return getWfname(); //
	}

	public String getFonttype() {
		return fonttype;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public Integer getFontsize() {
		return fontsize;
	}

	public void setFontsize(Integer fontsize) {
		this.fontsize = fontsize;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getPosts() {
		return posts;
	}

	public void setPosts(String posts) {
		this.posts = posts;
	}
}
