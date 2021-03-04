package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

/**
 * 工作流图形中组的数据定义..
 * @author xch
 *
 */
public class GroupVO implements Serializable {

	private static final long serialVersionUID = -4900363756753098731L;

	private Integer id = null;
	private String processid;
	private String grouptype = null; //组类型,即是横向的还是纵向的,取值范围是DEPT/STATION

	private String code = null; //编码
	private String wfname = null; //名称
	private String uiname = null; //名称

	private Integer x = null; //X坐标
	private Integer y = null; //Y坐标

	private Integer width = null; //宽度
	private Integer height = null; //高度

	//字体与颜色
	private String fonttype; //字体类型
	private Integer fontsize; //字体大小
	private String foreground; //前景颜色
	private String background; //背景颜色

	private String descr = null; //备注说明

	private String posts;//岗位

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
