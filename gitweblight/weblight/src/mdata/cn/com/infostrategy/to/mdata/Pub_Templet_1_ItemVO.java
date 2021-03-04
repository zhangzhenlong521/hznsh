/**************************************************************************
 * $RCSfile: Pub_Templet_1_ItemVO.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

public class Pub_Templet_1_ItemVO implements Serializable {

	private static final long serialVersionUID = -9091409633160181848L;

	private Pub_Templet_1VO pub_Templet_1VO = null;

	private String id; //主键
	private String itemkey;

	private String itemname;
	private String itemname_e; //英文名称
	private String itemtiptext; //标签帮助说明

	private String itemtype; //
	private String savedcolumndatatype; //保存表中列的真正数据类型,比如varchar,number,date

	//下拉框定义的所有数据!!!!!
	private String comboxdesc; //下拉框的下义
	private ComBoxItemVO[] comBoxItemVos = null; //下拉框对应的所有数据项.下拉框的数据是在取查询模板时就直接取出来的!!!,而参照是点击按钮时才取的!!

	private String queryitemtype; //查询框中的控件类型,即有许多控件在编辑与查询状态下时是不一样的
	private String queryitemdefine; //查询框中的控件定义,比如下拉框,参照定义等,除了下拉框是在BS端执行,其他的都在UI端执行!!!!
	private ComBoxItemVO[] queryComBoxItemVos = null; //查询框中,当是下拉框时的下拉框定义.  

	//参照定义的所有数据!!!!
	private String refdesc; //参照对应的SQL,参数定义的VO,即CommUCDefineVO
	private CommUCDefineVO uCDfVO = null; //控件定义VO,即后来将所有控件的定义都统一成这个对象了!!

	private String hyperlinkdesc; //超链接定义
	private Boolean issave; //是否参与保存
	private Boolean isencrypt; //保存时是否加密?
	private int saveLimit = -1; //保存时的上限,在构建模板VO时自动赋值
	private int saveScale = -1; //袁江晓 20140127添加  表示字符小数点后保留的位数
	private boolean isuniquecheck;//是否校验唯一性

	private String ismustinput;
	private Boolean iskeeptrace; //是否保留痕迹
	private String showbgcolor;
	private String loadformula;
	private String editformula;
	private Integer showorder;
	private Integer listwidth;
	private Integer labelwidth; //label的宽度
	private Integer cardwidth; //
	private Integer cardheight; //卡时显示时高度

	private Boolean listisshowable;
	private String listiseditable; //列表是否可编辑1都可以编辑/2只有新增可以编辑/3新增不能编辑修改可以编辑/4全部不能编辑
	private Boolean listishtmlhref; //列表是否是Html超链接方式,即可以点击弹出窗口
	private Boolean listiscombine; //列表是否合并 【杨科/2013-07-26】 
	private Boolean cardisshowable;
	private String cardisexport; //卡片中是否参与导出!0-不导出/1-半列导出/2-全列导出
	private Boolean listisexport; //列表中是否参与导出!Y/N
	private String cardiseditable; //卡片是否可编辑
	private Boolean propisshowable; //属性框中是否显示
	private Boolean propiseditable; //属性框中是否可编辑
	private String defaultvalueformula;
	private Boolean iswrap;
	private String grouptitle; //分组名,只对卡片与列表有用!

	//查询框的相关配置
	private CommUCDefineVO queryUCDfVO = null; //查询控件的VO定义对象!!!

	private Integer querylabelwidth; //查询label的宽度..
	private Integer querycompentwidth; //查询控件的宽度..
	private Integer querycompentheight; //查询控件的高度..

	private String queryeditformula; //查询的编辑公式
	private String querycreatetype; //查询创建器类型
	private String querycreatecustdef; //查询创建器自定义
	private Boolean isQuickQueryWrap; //快速查询时是否换行..
	private boolean isQuickQueryShowable; //快速查询是否显示..
	private boolean isQuickQueryEditable; //快速查询是否可编辑..
	private boolean isCommQueryWrap; //通用查询是否换行
	private boolean isCommQueryShowable; //通用查询是否换行
	private boolean isCommQueryEditable; //通用查询是否可编辑
	private String isQueryMustInput; //查询框是否必输
	private String querydefaultformula; //查询的默认值公式.

	private Boolean workflowiseditable; //
	private Boolean isRefCanEdit; //参照时是否允许编辑

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

	//是否是主键
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
	 * 是否能够成功保存
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
			setRefdesc((String) _value); //参照说明
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
	 * 是否是第一个item
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
	 * 是否是最后一个Item
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

	//SQL创建器类型,有默认机制,In机构,Like机制,自定义SQL,自定义类,不参与...
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
 * 邮储现场回来统一修改
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
 * 兴业春节前回来
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
