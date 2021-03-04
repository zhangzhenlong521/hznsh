/**************************************************************************
 * $RCSfile: Pub_Templet_1_ItemVO.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

public class Pub_Templet_1_ItemVO implements Serializable {

	private static final long serialVersionUID = -9091409633160181848L;

	private Pub_Templet_1VO pub_Templet_1VO = null;

	private String id; //����
	private String itemkey;

	private String itemname;
	private String itemname_e; //Ӣ������
	private String itemtiptext; //��ǩ����˵��

	private String itemtype; //
	private String savedcolumndatatype; //��������е�������������,����varchar,number,date

	//�����������������!!!!!
	private String comboxdesc; //�����������
	private ComBoxItemVO[] comBoxItemVos = null; //�������Ӧ������������.���������������ȡ��ѯģ��ʱ��ֱ��ȡ������!!!,�������ǵ����ťʱ��ȡ��!!

	private String queryitemtype; //��ѯ���еĿؼ�����,�������ؼ��ڱ༭���ѯ״̬��ʱ�ǲ�һ����
	private String queryitemdefine; //��ѯ���еĿؼ�����,����������,���ն����,��������������BS��ִ��,�����Ķ���UI��ִ��!!!!
	private ComBoxItemVO[] queryComBoxItemVos = null; //��ѯ����,����������ʱ����������.  

	//���ն������������!!!!
	private String refdesc; //���ն�Ӧ��SQL,���������VO,��CommUCDefineVO
	private CommUCDefineVO uCDfVO = null; //�ؼ�����VO,�����������пؼ��Ķ��嶼ͳһ�����������!!

	private String hyperlinkdesc; //�����Ӷ���
	private Boolean issave; //�Ƿ���뱣��
	private Boolean isencrypt; //����ʱ�Ƿ����?
	private int saveLimit = -1; //����ʱ������,�ڹ���ģ��VOʱ�Զ���ֵ
	private int saveScale = -1; //Ԭ���� 20140127���  ��ʾ�ַ�С���������λ��
	private boolean isuniquecheck;//�Ƿ�У��Ψһ��

	private String ismustinput;
	private Boolean iskeeptrace; //�Ƿ����ۼ�
	private String showbgcolor;
	private String loadformula;
	private String editformula;
	private Integer showorder;
	private Integer listwidth;
	private Integer labelwidth; //label�Ŀ��
	private Integer cardwidth; //
	private Integer cardheight; //��ʱ��ʾʱ�߶�

	private Boolean listisshowable;
	private String listiseditable; //�б��Ƿ�ɱ༭1�����Ա༭/2ֻ���������Ա༭/3�������ܱ༭�޸Ŀ��Ա༭/4ȫ�����ܱ༭
	private Boolean listishtmlhref; //�б��Ƿ���Html�����ӷ�ʽ,�����Ե����������
	private Boolean listiscombine; //�б��Ƿ�ϲ� �����/2013-07-26�� 
	private Boolean cardisshowable;
	private String cardisexport; //��Ƭ���Ƿ���뵼��!0-������/1-���е���/2-ȫ�е���
	private Boolean listisexport; //�б����Ƿ���뵼��!Y/N
	private String cardiseditable; //��Ƭ�Ƿ�ɱ༭
	private Boolean propisshowable; //���Կ����Ƿ���ʾ
	private Boolean propiseditable; //���Կ����Ƿ�ɱ༭
	private String defaultvalueformula;
	private Boolean iswrap;
	private String grouptitle; //������,ֻ�Կ�Ƭ���б�����!

	//��ѯ����������
	private CommUCDefineVO queryUCDfVO = null; //��ѯ�ؼ���VO�������!!!

	private Integer querylabelwidth; //��ѯlabel�Ŀ��..
	private Integer querycompentwidth; //��ѯ�ؼ��Ŀ��..
	private Integer querycompentheight; //��ѯ�ؼ��ĸ߶�..

	private String queryeditformula; //��ѯ�ı༭��ʽ
	private String querycreatetype; //��ѯ����������
	private String querycreatecustdef; //��ѯ�������Զ���
	private Boolean isQuickQueryWrap; //���ٲ�ѯʱ�Ƿ���..
	private boolean isQuickQueryShowable; //���ٲ�ѯ�Ƿ���ʾ..
	private boolean isQuickQueryEditable; //���ٲ�ѯ�Ƿ�ɱ༭..
	private boolean isCommQueryWrap; //ͨ�ò�ѯ�Ƿ���
	private boolean isCommQueryShowable; //ͨ�ò�ѯ�Ƿ���
	private boolean isCommQueryEditable; //ͨ�ò�ѯ�Ƿ�ɱ༭
	private String isQueryMustInput; //��ѯ���Ƿ����
	private String querydefaultformula; //��ѯ��Ĭ��ֵ��ʽ.

	private Boolean workflowiseditable; //
	private Boolean isRefCanEdit; //����ʱ�Ƿ�����༭

	public String isCardiseditable() {
		return cardiseditable;
	}

	public void setCardiseditable(String _cardiseditable) {
		this.cardiseditable = _cardiseditable;
	}

	public Boolean isCardisshowable() {
		return cardisshowable;
	}

	public void setCardisshowable(Boolean cardisshowable) {
		this.cardisshowable = cardisshowable;
	}

	public Integer getCardwidth() {
		return this.cardwidth;
	}

	public void setCardwidth(Integer _cardwidth) {
		this.cardwidth = _cardwidth;
	}

	public Integer getCardHeight() {
		return cardheight;
	}

	public void setCardHeight(Integer _cardheight) {
		this.cardheight = _cardheight;
	}

	public String getComboxdesc() {
		return comboxdesc;
	}

	public void setComboxdesc(String comboxdesc) {
		this.comboxdesc = comboxdesc;
	}

	public String getEditformula() {
		return editformula;
	}

	public void setEditformula(String editformula) {
		this.editformula = editformula;
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String isListiseditable() {
		return listiseditable;
	}

	public void setListiseditable(String _listiseditable) {
		this.listiseditable = _listiseditable;
	}

	public Boolean isListisshowable() {
		return listisshowable;
	}

	public void setListisshowable(Boolean listisshowable) {
		this.listisshowable = listisshowable;
	}

	public Integer getListwidth() {
		return listwidth;
	}

	public void setListwidth(Integer listwidth) {
		this.listwidth = listwidth;
	}

	public String getLoadformula() {
		return loadformula;
	}

	public void setLoadformula(String loadformula) {
		this.loadformula = loadformula;
	}

	public String getRefdesc() {
		return refdesc;
	}

	public void setRefdesc(String refdesc) {
		this.refdesc = refdesc;
	}

	public CommUCDefineVO getUCDfVO() {
		return uCDfVO;
	}

	public void setUCDfVO(CommUCDefineVO _dfVO) {
		uCDfVO = _dfVO;
	}

	public Integer getShoworder() {
		return showorder;
	}

	public void setShoworder(Integer showorder) {
		this.showorder = showorder;
	}

	public String getCardiseditable() {
		return cardiseditable == null ? "" : cardiseditable;
	}

	public Boolean getCardisshowable() {
		return cardisshowable;
	}

	public String getCardisexport() {
		return cardisexport;
	}

	public void setCardisexport(String cardisexport) {
		this.cardisexport = cardisexport;
	}

	public String getListiseditable() {
		return listiseditable;
	}

	public Boolean getListisshowable() {
		return listisshowable;
	}

	public ComBoxItemVO[] getComBoxItemVos() {
		return comBoxItemVos;
	}

	public void setComBoxItemVos(ComBoxItemVO[] comBoxItemVos) {
		this.comBoxItemVos = comBoxItemVos;
	}

	public Boolean getIssave() {
		return issave == null ? false : issave;
	}

	public void setIssave(Boolean issave) {
		this.issave = issave;
	}

	public Boolean getIsencrypt() {
		return isencrypt;
	}

	public void setIsencrypt(Boolean isencrypt) {
		this.isencrypt = isencrypt;
	}

	public int getSaveLimit() {
		return saveLimit;
	}

	public void setSaveLimit(int saveLimit) {
		this.saveLimit = saveLimit;
	}

	public Pub_Templet_1VO getPub_Templet_1VO() {
		return pub_Templet_1VO;
	}

	public void setPub_Templet_1VO(Pub_Templet_1VO pub_Templet_1VO) {
		this.pub_Templet_1VO = pub_Templet_1VO;
	}

	//�Ƿ�������
	public boolean isPrimaryKey() {
		String str_okname = getPub_Templet_1VO().getPkname();
		if (getItemkey().equals(str_okname)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNeedSave() {
		return issave == null ? false : issave.booleanValue();
	}

	/**
	 * �Ƿ��ܹ��ɹ�����
	 * @return
	 */
	public boolean isCanSave() {
		String[] str_SavedTableHaveColumns = getPub_Templet_1VO().getRealSavedTableHaveColumns();
		if (str_SavedTableHaveColumns == null || str_SavedTableHaveColumns.length == 0) {
			return false;
		}

		for (int i = 0; i < str_SavedTableHaveColumns.length; i++) {
			if (getItemkey().equalsIgnoreCase(str_SavedTableHaveColumns[i])) {
				return true;
			}
		}

		return false;
	}

	public boolean isViewColumn() {
		String[] str_viewcolumns = getPub_Templet_1VO().getRealViewColumns(); //
		if (str_viewcolumns == null || str_viewcolumns.length == 0) {
			return false;
		}

		for (int i = 0; i < str_viewcolumns.length; i++) {
			if (getItemkey().equalsIgnoreCase(str_viewcolumns[i])) {
				return true;
			}
		}
		return false;
	}

	public void setIsmustinput(boolean _ismustinput) {
		if (_ismustinput) {
			this.ismustinput = "Y";
		} else {
			this.ismustinput = "N";
		}

	}

	public boolean getIsmustinput() {
		if ("Y".equals(ismustinput)) {
			return true;
		}
		return false;
	}

	public void setIsmustinput2(String _ismustinput) {
		this.ismustinput = _ismustinput;
	}

	public String getIsmustinput2() {
		return ismustinput;
	}

	public Boolean getIsRefCanEdit() {
		return isRefCanEdit;
	}

	public void setIsRefCanEdit(Boolean isRefCanEdit) {
		this.isRefCanEdit = isRefCanEdit;
	}

	/**
	 * itemkey
	 * @param _key
	 * @param _value
	 */
	public void setAttributeValue(String _key, Object _value) {
		if (_key.equalsIgnoreCase("itemkey")) {
			setItemkey((String) _value);
		} else if (_key.equalsIgnoreCase("itemname")) {
			setItemname((String) _value);
		} else if (_key.equalsIgnoreCase("itemtype")) {
			setItemtype((String) _value);
		} else if (_key.equalsIgnoreCase("comboxdesc")) {
			setComboxdesc((String) _value);
		} else if (_key.equalsIgnoreCase("refdesc")) {
			setRefdesc((String) _value); //����˵��
		} else if (_key.equalsIgnoreCase("issave")) {
			setIssave((Boolean) _value);
		} else if (_key.equalsIgnoreCase("ismustinput")) {
			setIsmustinput2((String) _value);
		} else if (_key.equalsIgnoreCase("loadformula")) {
			setLoadformula((String) _value);
		} else if (_key.equalsIgnoreCase("editformula")) {
			setEditformula((String) _value); //
		} else if (_key.equalsIgnoreCase("showorder")) {
			setShoworder((Integer) _value);
		} else if (_key.equalsIgnoreCase("listwidth")) {
			setListwidth((Integer) _value);
		} else if (_key.equalsIgnoreCase("cardwidth")) {
			setCardwidth((Integer) _value);
		} else if (_key.equalsIgnoreCase("listisshowable")) {
			setListisshowable((Boolean) _value);
		} else if (_key.equalsIgnoreCase("listiseditable")) {
			setListiseditable((String) _value);
		} else if (_key.equalsIgnoreCase("cardisshowable")) {
			setCardisshowable((Boolean) _value);
		} else if (_key.equalsIgnoreCase("cardiseditable")) {
			setCardiseditable((String) _value);
		} else if (_key.equalsIgnoreCase("defaultvalueformula")) {
			setDefaultvalueformula((String) _value);
		} else if (_key.equalsIgnoreCase("savedcolumndatatype")) {
			setSavedcolumndatatype((String) _value);
		} else if (_key.equalsIgnoreCase("isrefcanedit")) {
			setIsRefCanEdit((Boolean) _value);
		}
	}

	public String getDefaultvalueformula() {
		return defaultvalueformula;
	}

	public void setDefaultvalueformula(String defaultvalueformula) {
		this.defaultvalueformula = defaultvalueformula;
	}

	public String getSavedcolumndatatype() {
		return savedcolumndatatype;
	}

	public void setSavedcolumndatatype(String savedcolumndatatype) {
		this.savedcolumndatatype = savedcolumndatatype;
	}

	public Boolean getIswrap() {
		return iswrap;
	}

	public void setIswrap(Boolean iswrap) {
		this.iswrap = iswrap;
	}

	public String getItemname_e() {
		return itemname_e;
	}

	public void setItemname_e(String itemname_e) {
		this.itemname_e = itemname_e;
	}

	public String getItemtiptext() {
		return itemtiptext;
	}

	public void setItemtiptext(String itemtiptext) {
		this.itemtiptext = itemtiptext;
	}

	public String getGrouptitle() {
		return grouptitle;
	}

	public void setGrouptitle(String grouptitle) {
		this.grouptitle = grouptitle;
	}

	/**
	 * �Ƿ��ǵ�һ��item
	 * @return
	 */
	public boolean isFirstItem() {
		String[] str_keys = getPub_Templet_1VO().getItemKeys();
		if (this.getItemkey().equals(str_keys[0])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �Ƿ������һ��Item
	 * @return
	 */
	public boolean isLastItem() {
		String[] str_keys = getPub_Templet_1VO().getItemKeys();
		if (this.getItemkey().equals(str_keys[str_keys.length - 1])) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getPropisshowable() {
		return propisshowable;
	}

	public void setPropisshowable(Boolean propisshowable) {
		this.propisshowable = propisshowable;
	}

	public Boolean getPropiseditable() {
		return propiseditable;
	}

	public void setPropiseditable(Boolean propiseditable) {
		this.propiseditable = propiseditable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getLabelwidth() {
		return labelwidth;
	}

	public void setLabelwidth(Integer labelwidth) {
		this.labelwidth = labelwidth;
	}

	public String getHyperlinkdesc() {
		return hyperlinkdesc;
	}

	public void setHyperlinkdesc(String hyperlinkdesc) {
		this.hyperlinkdesc = hyperlinkdesc;
	}

	public String getShowbgcolor() {
		return showbgcolor;
	}

	public void setShowbgcolor(String showbgcolor) {
		this.showbgcolor = showbgcolor;
	}

	public Boolean getListishtmlhref() {
		return listishtmlhref == null ? false : listishtmlhref;
	}

	public void setListishtmlhref(Boolean listishtmlhref) {
		this.listishtmlhref = listishtmlhref;
	}
	
	public Boolean getListiscombine() {
		return listiscombine == null ? false : listiscombine;
	}

	public void setListiscombine(Boolean listiscombine) {
		this.listiscombine = listiscombine;
	}

	public Boolean getIsQuickQueryWrap() {
		return isQuickQueryWrap;
	}

	public void setIsQuickQueryWrap(Boolean isQuickQueryWrap) {
		this.isQuickQueryWrap = isQuickQueryWrap;
	}

	public boolean getIsQuickQueryShowable() {
		return isQuickQueryShowable;
	}

	public void setIsQuickQueryShowable(boolean isQuickQueryShowable) {
		this.isQuickQueryShowable = isQuickQueryShowable;
	}

	public boolean getIsQuickQueryEditable() {
		return isQuickQueryEditable;
	}

	public void setIsQuickQueryEditable(boolean isQuickQueryEditable) {
		this.isQuickQueryEditable = isQuickQueryEditable;
	}

	public boolean getIsCommQueryWrap() {
		return isCommQueryWrap;
	}

	public void setIsCommQueryWrap(boolean isCommQueryWrap) {
		this.isCommQueryWrap = isCommQueryWrap;
	}

	public boolean getIsCommQueryShowable() {
		return isCommQueryShowable;
	}

	public void setIsCommQueryShowable(boolean isCommQueryShowable) {
		this.isCommQueryShowable = isCommQueryShowable;
	}

	public boolean getIsCommQueryEditable() {
		return isCommQueryEditable;
	}

	public void setIsCommQueryEditable(boolean isCommQueryEditable) {
		this.isCommQueryEditable = isCommQueryEditable;
	}

	public String getQuerydefaultformula() {
		return querydefaultformula;
	}

	public void setQuerydefaultformula(String querydefaultformula) {
		this.querydefaultformula = querydefaultformula;
	}

	public String getQueryeditformula() {
		return queryeditformula;
	}

	//SQL����������,��Ĭ�ϻ���,In����,Like����,�Զ���SQL,�Զ�����,������...
	public String getQuerycreatetype() {
		return querycreatetype;
	}

	public void setQuerycreatetype(String querycreatetype) {
		this.querycreatetype = querycreatetype;
	}

	public String getQuerycreatecustdef() {
		return querycreatecustdef;
	}

	public void setQuerycreatecustdef(String querycreatecustdef) {
		this.querycreatecustdef = querycreatecustdef;
	}

	public void setQueryeditformula(String queryeditformula) {
		this.queryeditformula = queryeditformula;
	}

	public CommUCDefineVO getQueryUCDfVO() {
		return queryUCDfVO;
	}

	public void setQueryUCDfVO(CommUCDefineVO _queryUCDfVO) {
		this.queryUCDfVO = _queryUCDfVO;
	}

	public Integer getQuerylabelwidth() {
		return querylabelwidth;
	}

	public void setQuerylabelwidth(Integer querylabelwidth) {
		this.querylabelwidth = querylabelwidth;
	}

	public Integer getQuerycompentwidth() {
		return querycompentwidth;
	}

	public void setQuerycompentwidth(Integer querycompentwidth) {
		this.querycompentwidth = querycompentwidth;
	}

	public String getIsQueryMustInput() {
		return isQueryMustInput;
	}

	public void setIsQueryMustInput(String _isQueryMustInput) {
		this.isQueryMustInput = _isQueryMustInput; //
	}

	public Boolean getIskeeptrace() {
		return iskeeptrace == null ? false : iskeeptrace;
	}

	public void setIskeeptrace(Boolean iskeeptrace) {
		this.iskeeptrace = iskeeptrace;
	}

	public Boolean getWorkflowiseditable() {
		return workflowiseditable==null?false:workflowiseditable;
	}

	public void setWorkflowiseditable(Boolean workflowiseditable) {
		this.workflowiseditable = workflowiseditable;
	}

	public String getQueryItemType() {
		return queryitemtype;
	}

	public void setQueryItemType(String queryitemtype) {
		this.queryitemtype = queryitemtype;
	}

	public String getQueryItemDefine() {
		return queryitemdefine;
	}

	public void setQueryItemDefine(String queryitemdefine) {
		this.queryitemdefine = queryitemdefine;
	}

	public ComBoxItemVO[] getQueryComBoxItemVos() {
		return queryComBoxItemVos;
	}

	public void setQueryComBoxItemVos(ComBoxItemVO[] queryComBoxItemVos) {
		this.queryComBoxItemVos = queryComBoxItemVos;
	}

	public boolean getIsuniquecheck() {
		return isuniquecheck;
	}

	public void setIsuniquecheck(boolean isuniquecheck) {
		this.isuniquecheck = isuniquecheck;
	}

	public Integer getQuerycompentheight() {
		return querycompentheight;
	}

	public void setQuerycompentheight(Integer querycompentheight) {
		this.querycompentheight = querycompentheight;
	}

	public Boolean getListisexport() {
		return listisexport;
	}

	public void setListisexport(Boolean listisexport) {
		this.listisexport = listisexport;
	}

	public int getSaveScale() {
		return saveScale;
	}

	public void setSaveScale(int saveScale) {
		this.saveScale = saveScale;
	}

}

/**************************************************************************
 * $RCSfile: Pub_Templet_1_ItemVO.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: Pub_Templet_1_ItemVO.java,v $
 * Revision 1.8  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.3  2012/09/04 11:05:36  Administrator
 * s
 *
 * Revision 1.2  2012/09/04 09:53:26  Administrator
 * s
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.7  2012/02/06 12:01:07  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.6  2011/10/31 13:07:22  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.3  2011/06/14 09:50:02  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2011/01/27 09:55:53  xch123
 * ��ҵ����ǰ����
 *
 * Revision 1.1  2010/05/17 10:23:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/04/20 14:03:46  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.3  2010/04/16 03:18:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/04/12 06:20:16  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.12  2010/03/24 13:11:37  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.11  2010/03/23 12:07:58  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:26  xch
 * *** empty log message ***
 *
 *
**************************************************************************/
