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

	private String processid = null; //��������id

	private String code = null; //����

	private String wfname = null; //����ͼ����ʾ������

	private String uiname = null; //�����а�ť������

	private String dealtype; //��������,��SUBMIT��REJECT֮��,���ύ��ܾ�֮��!

	
	//��������ɫ
	private String fonttype;  //��������
	private Integer fontsize;  //�����С
	private String foreground;  //ǰ����ɫ
	private String background;  //������ɫ
	
	private String mailsubject; //�ʼ�����
	private String mailcontent; //�ʼ�����..

	private Integer fromactivity = null; //Դ���ڱ���

	private Integer toactivity = null; //Ŀ�껷�ڱ���

	private String condition = null; //����,����isValid=='YES'

	private String reasoncodesql = null; //
	private String intercept = null;  //
	private Integer lineType = new Integer(2);
	private boolean isSingle = true;

	List points = new ArrayList(); //����·��

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
		return getWfname(); //��ʾ������
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
