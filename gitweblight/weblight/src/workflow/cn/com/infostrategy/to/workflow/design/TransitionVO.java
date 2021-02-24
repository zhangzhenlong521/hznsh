/**************************************************************************
 * $RCSfile: TransitionVO.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:18:02 $
 **************************************************************************/

package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransitionVO implements Serializable {

	private static final long serialVersionUID = 130078071339911530L;

	private Integer id = null; //id

	private String processid = null; //所属流程id

	private String code = null; //编码

	private String wfname = null; //流程图中显示的名称

	private String uiname = null; //单据中按钮的名称

	private String dealtype; //处理类型,有SUBMIT与REJECT之分,即提交与拒绝之分!

	
	//字体与颜色
	private String fonttype;  //字体类型
	private Integer fontsize;  //字体大小
	private String foreground;  //前景颜色
	private String background;  //背景颜色
	
	private String mailsubject; //邮件主题
	private String mailcontent; //邮件内容..

	private Integer fromactivity = null; //源环节编码

	private Integer toactivity = null; //目标环节编码

	private String condition = null; //条件,比如isValid=='YES'

	private String reasoncodesql = null; //
	private String intercept = null;  //
	private Integer lineType = new Integer(2);
	private boolean isSingle = true;

	List points = new ArrayList(); //连线路径

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getFromactivity() {
		return fromactivity;
	}

	public void setFromactivity(Integer fromActivity) {
		this.fromactivity = fromActivity;
	}

	public String getUiname() {
		return uiname;
	}

	public void setUiname(String uiname) {
		this.uiname = uiname;
	}

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public Integer getToactivity() {
		return toactivity;
	}

	public void setToactivity(Integer toActivity) {
		this.toactivity = toActivity;
	}

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

	public List getPoints() {
		return points;
	}

	public void setPoints(List points) {
		this.points = points;
	}

	public String toString() {
		return getWfname(); //显示的名称
	}

	public String getDealtype() {
		return dealtype;
	}

	public void setDealtype(String dealtype) {
		this.dealtype = dealtype;
	}

	public String getReasoncodesql() {
		return reasoncodesql;
	}

	public void setReasoncodesql(String reasoncodesql) {
		this.reasoncodesql = reasoncodesql;
	}

	public String getMailcontent() {
		return mailcontent;
	}

	public void setMailcontent(String mailcontent) {
		this.mailcontent = mailcontent;
	}

	public String getMailsubject() {
		return mailsubject;
	}

	public void setMailsubject(String mailsubject) {
		this.mailsubject = mailsubject;
	}

	public String getIntercept() {
		return intercept;
	}

	public void setIntercept(String intercept) {
		this.intercept = intercept;
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

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
}
