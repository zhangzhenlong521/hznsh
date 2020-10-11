/**************************************************************************
 * $RCSfile: Pub_Templet_1VO.java,v $  $Revision: 1.24 $  $Date: 2012/11/19 06:17:11 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import cn.com.infostrategy.to.common.TBUtil;

public class Pub_Templet_1VO implements Serializable {

	private static final long serialVersionUID = -5952061193950956426L;

	private String buildFromType = null; //编译生成的类型,有从DB取,有从Class生成,有从XML生成
	private String buildFromInfo = null; //如果是从Class生成的,则说明类名是什么! 如果是从XML生成的则说明是哪个XML文件!

	private String pk_pub_templet_1; // 主键
	private String templetcode; // 模板编码
	private String templetname; // 模板名称
	private String templetname_e; // 模板名称
	private String tablename; // 取数的表名

	private String datasourcename; // 数据源名称,如果为空,则从默认数据源取数!!
	private String dataconstraint; // 数据权限条件(SQL类型)
	private String autoloadconstraint; //自动加载数据时的过滤条件(SQL类型),它与本身的的数据过滤条件可能一样,但也有可不一样(比如自动加载时只查询状态为未处理的或本人录入的,或本月的,但点击查询按钮时却可以查询所有)!!

	private String ordercondition; // 排序条件..
	private int autoLoads = 0; // 页面打开时(列表或树)自动查询数据的量,三种情况,1-为空或0时表示不做查询动作;2-大于0表示查询出对应的记录;3-小于0表示查询出所有数据,无论对于列表或树都是一样!!以前的做法都是在拦截器中实现自动查询,但总感觉不够方便!直接在模板中设定还是有道理的!!!会快速与方便不少!!

	private String datapolicy; //数据权限策略
	private String datapolicymap; //数据权限策略映射
	private String dataSqlAndOrCondition;//sunfujun/20121119/条件与数据权限的拼接方式
	private String bsdatafilterclass; // 服务器端数据过滤器!!非常关键,数据权限大量要依靠他
	private Boolean bsdatafilterissql; //服务器端过滤返回的是否是SQL,即有两种情况,一种是使用JAVA计算设置某个HashVO是否可显示,一种是返回SQL,即还是在原来SQL后面接上新条件!然后支持分页!!! 以前的机制是分页有问题,即有Java计算过滤时就不能分页!! 但不使用SQL分页性能又有问题!!! 所以还是需要SQL过滤!! 因为SQL是一次性的!!

	private String pkname;
	private String pksequencename;
	private String tostringkey; //ToString的key名

	private String savedtablename; // 保存数据的表名
	private String cardlayout; // 卡片布局类型
	private Integer cardwidth; // 卡片宽度
	private String cardBorder; // 卡片控件的边框,即可以是方框或者是下划线

	private Boolean isshowcardborder; // 是否显示卡片边框,为了在调整复杂页面时方便使用!
	private Boolean isshowcardcustbtn; // 卡片是否显示自定义按钮
	private String cardcustbtndesc; // 卡片自定义按钮说明
	private String cardinitformula; // 卡片初始化公式
	private Boolean cardsaveifcheck;// 卡片保存不做必填验证 默认是做即为N
	private String cardsaveselfdesccheck;// 卡片保存自定义验证
	/* private Boolean groupisonlyone; */
	private ButtonDefineVO[] cardcustbtns; //

	private Boolean isshowlistpagebar; // 是否显示列表的分页栏
	private Boolean islistpagebarwrap; //列表中的分页是否换行
	private Boolean isshowlistopebar; // 是否显示列表的操作栏
	private Boolean isshowlistquickquery; // 是否显示列表快速查询
	private Boolean isshowcommquerybtn; // 不显示通用查询
	private Boolean iscollapsequickquery; //是否收起快速查询框!!,即在显示快速查询框的前提下,还可以默认是收起来的,然后点击一个按钮后再展开!!!
	private Boolean isshowlistcustbtn; // 是否显示列表的自定义按钮栏
	private String listcustbtndesc; // 列表自定义按钮栏说明,以分号隔开,拼成一串WLTButton
	private ButtonDefineVO[] listcustbtns; //

	private int listheadheight = 27; // 列表表头的高度,以前是25,但发生换行时会造成第二行字看不见! 而27正好能看见!
	private String listrowheight; // 列表中的行高,可以是一个列表!
	private Boolean islistautorowheight; // 列表是否自动撑高??就像Html的表格一样!!
	private Boolean listheaderisgroup; // 列表的表头是否支持分组
	private String listinitformula; // 列表初始化公式
	private String listweidudesc; // 列表维度定义
	private String[] listbtnorderdesc;

	private String cardcustpanel; // 卡片的自定义面板
	private String listcustpanel; // 列表的自定义面板

	private String treepk; // 树型面板中的主键,只限于构建树型面板时用到
	private String treeparentpk; // 树型面板中的对应父记录的外键,只限于构建树型面板时用到
	private String treeviewfield; // 树型面板显示结点名称的模板字段key
	private String treeseqfield; // 树型面板中用于排序的字段,必须是查询表与保存表中都有的,而且
	private Boolean treeisshowroot; // 树型面板中是否显示根结点
	private Boolean treeisonlyone; // 是否只能同时看一层,即如果展开某一层时,同时把兄弟层收缩!!起到保护数据的作用!
	private Boolean treeIsChecked; // 是否是勾选的树
	private Boolean treeisshowtoolbar; // 树控件是否显示工具条栏
	private String treecustbtndesc; // 树型控件自定义按钮说明
	private ButtonDefineVO[] treecustbtns; //

	private String wfcustexport = null;  //

	private String propbeanclassname; // 属性面板中对应的JavaBean的Class类名,只限于构建属性面板时用到!

	//private String[] itemKeys; //
	private String[] realViewColumns; // 真正的视图的列名,如果它品配上itemKeys中的一项,就给itemKeys这项赋值
	private String[] realSavedTableColumns; // 真正的保存数据库的列名,它肯定是realViewColumns的子集
	private String[] realSavedTableHaveColumns; //

	private String defineRenderer;
	private Pub_Templet_1_ItemVO[] itemVos;

	public Pub_Templet_1VO() {
	}

	public String getBuildFromType() {
		return buildFromType;
	}

	public void setBuildFromType(String _buildFromType) {
		this.buildFromType = _buildFromType;
	}

	public String getBuildFromInfo() {
		return buildFromInfo;
	}

	public void setBuildFromInfo(String _buildFromInfo) {
		this.buildFromInfo = _buildFromInfo;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getTempletname() {
		return templetname;
	}

	public void setTempletname(String templetname) {
		this.templetname = templetname;
	}

	public Pub_Templet_1_ItemVO[] getItemVos() {
		return itemVos;
	}

	public void setItemVos(Pub_Templet_1_ItemVO[] itemVos) {
		this.itemVos = itemVos;
	}

	public String[] getItemKeys() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		String[] keys = new String[vos.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = vos[i].getItemkey();
		}
		return keys;
	}

	public String[] getItemNames() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		String[] names = new String[vos.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = vos[i].getItemname();
		}
		return names;
	}

	public String[] getItemTypes() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		String[] types = new String[vos.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = vos[i].getItemtype();
		}
		return types;
	}

	public int getIsWrapCount() {
		int li_count = 0;
		for (int i = 0; i < itemVos.length; i++) {
			if (itemVos[i].getIswrap().booleanValue()) {
				li_count = li_count + 1;
			}
		}
		return li_count;
	}

	public int[] getItemLengths() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		int[] lengths = new int[vos.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = vos[i].getSaveLimit();
		}
		return lengths;
	}
	//袁江晓 20140127添加 主要 解决输入浮点型位数超出验证的问题
	public int[] getItemLengthsScale() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		int[] lengths = new int[vos.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = vos[i].getSaveScale();
		}
		return lengths;
	}
	public boolean[] getItemIsMustInputs() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		boolean[] bo_isMustInput = new boolean[vos.length];
		for (int i = 0; i < bo_isMustInput.length; i++) {
			if ("Y".equals(vos[i].getIsmustinput2())) {
				bo_isMustInput[i] = true;
			} else {
				bo_isMustInput[i] = false;
			}

		}
		return bo_isMustInput;
	}

	public boolean[] getItemCardShowAble() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		boolean[] bo_cardShowAble = new boolean[vos.length];
		for (int i = 0; i < bo_cardShowAble.length; i++) {
			bo_cardShowAble[i] = vos[i].getCardisshowable();
		}
		return bo_cardShowAble;
	}

	public boolean[] getItemIsWarnInputs() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		boolean[] bo_isWarnInput = new boolean[vos.length];
		for (int i = 0; i < bo_isWarnInput.length; i++) {
			if ("W".equals(vos[i].getIsmustinput2())) {
				bo_isWarnInput[i] = true;
			} else {
				bo_isWarnInput[i] = false;
			}
		}
		return bo_isWarnInput;
	}

	public boolean[] getItemIsUnique() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		boolean[] bo_IsUnique = new boolean[vos.length];
		for (int i = 0; i < bo_IsUnique.length; i++) {
			if (vos[i].getIsuniquecheck()) {
				bo_IsUnique[i] = true;
			} else {
				bo_IsUnique[i] = false;
			}
		}
		return bo_IsUnique;

	}

	public Pub_Templet_1_ItemVO getItemVo(String _itemkey) {
		for (int i = 0; i < this.getItemVos().length; i++) {
			if (this.getItemVos()[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				return this.getItemVos()[i];
			}
		}
		return null;
	}

	public String getItemType(String _itemkey) {
		Pub_Templet_1_ItemVO itemVO = getItemVo(_itemkey); //
		if (itemVO != null) {
			return itemVO.getItemtype(); //
		}
		return null; //
	}

	public String getPkname() {
		return pkname;
	}

	public void setPkname(String pkname) {
		this.pkname = pkname;
	}

	public String getPksequencename() {
		return pksequencename;
	}

	public void setPksequencename(String pksequencename) {
		this.pksequencename = pksequencename;
	}

	public String getTostringkey() {
		return tostringkey;
	}

	public void setTostringkey(String tostringkey) {
		this.tostringkey = tostringkey;
	}

	public String getSavedtablename() {
		return savedtablename;
	}

	public void setSavedtablename(String savedtablename) {
		this.savedtablename = savedtablename;
	}

	public String[] getRealViewColumns() {
		return realViewColumns;
	}

	public Pub_Templet_1_ItemVO[] getRealViewItemVOs() {
		String[] str_views = getRealViewColumns();
		if (str_views == null) {
			return null;
		}
		Vector v_tmp = new Vector();
		for (int i = 0; i < str_views.length; i++) {
			for (int j = 0; j < getItemVos().length; j++) {
				if (getItemVos()[j].getItemkey().equalsIgnoreCase(str_views[i])) {
					v_tmp.add(getItemVos()[j]);
				}
			}
		}

		return (Pub_Templet_1_ItemVO[]) v_tmp.toArray(new Pub_Templet_1_ItemVO[0]); //
	}

	public void setRealViewColumns(String[] realViewColumns) {
		this.realViewColumns = realViewColumns;
	}

	public String[] getRealSavedTableColumns() {
		return realSavedTableColumns;
	}

	public Pub_Templet_1_ItemVO[] getRealSavedTableItemVOs() {
		String[] str_saves = getRealSavedTableColumns();
		Vector v_tmp = new Vector();
		for (int i = 0; i < str_saves.length; i++) {
			for (int j = 0; j < getItemVos().length; j++) {
				if (getItemVos()[j].getItemkey().equalsIgnoreCase(str_saves[i])) {
					v_tmp.add(getItemVos()[j]);
				}
			}
		}
		return (Pub_Templet_1_ItemVO[]) v_tmp.toArray(new Pub_Templet_1_ItemVO[0]); //
	}

	public void setRealSavedTableColumns(String[] realSavedTableColumns) {
		this.realSavedTableColumns = realSavedTableColumns;
	}

	public String[] getRealSavedTableHaveColumns() {
		return realSavedTableHaveColumns;
	}

	public void setRealSavedTableHaveColumns(String[] realSavedTableHaveColumns) {
		this.realSavedTableHaveColumns = realSavedTableHaveColumns;
	}

	public String getCardcustpanel() {
		return cardcustpanel;
	}

	public void setCardcustpanel(String cardcustpanel) {
		this.cardcustpanel = cardcustpanel;
	}

	public String getListcustpanel() {
		return listcustpanel;
	}

	public void setListcustpanel(String listcustpanel) {
		this.listcustpanel = listcustpanel;
	}

	public String getDatasourcename() {
		return datasourcename;
	}

	public void setDatasourcename(String datasourcename) {
		this.datasourcename = datasourcename;
	}

	public String getDataconstraint() {
		return dataconstraint;
	}

	public void setDataconstraint(String dataconstraint) {
		this.dataconstraint = dataconstraint;
	}

	//自动加载数据时的SQL约束条件!!
	public String getAutoloadconstraint() {
		return autoloadconstraint;
	}

	public void setAutoloadconstraint(String autoloadconstraint) {
		this.autoloadconstraint = autoloadconstraint;
	}

	public boolean containsItemKey(String _itemKey) {
		String[] keys = this.getItemKeys();
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equalsIgnoreCase(_itemKey)) {
				return true;
			}
		}
		return false;
	}

	public String getTempletname_e() {
		return templetname_e;
	}

	public void setTempletname_e(String templetname_e) {
		this.templetname_e = templetname_e;
	}

	public String getCardlayout() {
		return cardlayout;
	}

	public void setCardlayout(String cardlayout) {
		this.cardlayout = cardlayout;
	}

	public Integer getCardwidth() {
		return cardwidth;
	}

	public void setCardwidth(Integer cardwidth) {
		this.cardwidth = cardwidth;
	}

	public Boolean getIsshowlistpagebar() {
		return isshowlistpagebar;
	}

	public void setIsshowlistpagebar(Boolean isshowlistpagebar) {
		this.isshowlistpagebar = isshowlistpagebar;
	}

	public Boolean getIslistpagebarwrap() {
		return islistpagebarwrap;
	}

	public void setIslistpagebarwrap(Boolean islistpagebarwrap) {
		this.islistpagebarwrap = islistpagebarwrap;
	}

	public Boolean getIsshowlistopebar() {
		return isshowlistopebar;
	}

	public void setIsshowlistopebar(Boolean isshowlistopebar) {
		this.isshowlistopebar = isshowlistopebar;
	}

	public Boolean getIsshowcardborder() {
		return isshowcardborder;
	}

	public void setIsshowcardborder(Boolean isshowcardborder) {
		this.isshowcardborder = isshowcardborder;
	}

	public String getCardBorder() {
		return cardBorder;
	}

	public void setCardBorder(String cardBorder) {
		this.cardBorder = cardBorder;
	}

	public String getTreepk() {
		return treepk;
	}

	public void setTreepk(String treepk) {
		this.treepk = treepk;
	}

	public String getTreeparentpk() {
		return treeparentpk;
	}

	public void setTreeparentpk(String treeparentpk) {
		this.treeparentpk = treeparentpk;
	}

	public String getTreeviewfield() {
		return treeviewfield;
	}

	public void setTreeviewfield(String treeviewfield) {
		this.treeviewfield = treeviewfield;
	}

	public String getTreeseqfield() {
		return treeseqfield;
	}

	public void setTreeseqfield(String treeseqfield) {
		this.treeseqfield = treeseqfield;
	}

	public Boolean getTreeisshowroot() {
		return treeisshowroot;
	}

	public void setTreeisshowroot(Boolean treeisshowroot) {
		this.treeisshowroot = treeisshowroot;
	}

	public String getPropbeanclassname() {
		return propbeanclassname;
	}

	public void setPropbeanclassname(String propbeanclassname) {
		this.propbeanclassname = propbeanclassname;
	}

	public Boolean getIsshowlistquickquery() {
		return isshowlistquickquery;
	}

	public void setIsshowlistquickquery(Boolean isshowlistquickquery) {
		this.isshowlistquickquery = isshowlistquickquery;
	}

	public Boolean getIscollapsequickquery() {
		return iscollapsequickquery;
	}

	public void setIscollapsequickquery(Boolean iscollapsequickquery) {
		this.iscollapsequickquery = iscollapsequickquery;
	}

	public Boolean getIsshowlistcustbtn() {
		return isshowlistcustbtn;
	}

	public void setIsshowlistcustbtn(Boolean isshowlistcustbtn) {
		this.isshowlistcustbtn = isshowlistcustbtn;
	}

	public String getListcustbtndesc() {
		return listcustbtndesc;
	}

	public void setListcustbtndesc(String listcustbtndesc) {
		this.listcustbtndesc = listcustbtndesc;
	}

	public int getListheadheight() {
		return listheadheight;
	}

	public void setListheadheight(int _height) {
		listheadheight = _height;
	}

	public String getListrowheight() {
		return listrowheight;
	}

	public void setListrowheight(String listrowheight) {
		this.listrowheight = listrowheight;
	}

	public Boolean getListheaderisgroup() {
		return listheaderisgroup;
	}

	public void setListheaderisgroup(Boolean listheaderisgroup) {
		this.listheaderisgroup = listheaderisgroup;
	}

	public Boolean getIsshowcardcustbtn() {
		return isshowcardcustbtn;
	}

	public void setIsshowcardcustbtn(Boolean isshowcardcustbtn) {
		this.isshowcardcustbtn = isshowcardcustbtn;
	}

	public String getOrdercondition() {
		return ordercondition;
	}

	// 排序条件..
	public void setOrdercondition(String ordercondition) {
		this.ordercondition = ordercondition;
	}

	// 自动查询数据的记录数..
	public int getAutoLoads() {
		return autoLoads;
	}

	public void setAutoLoads(int _autoLoads) {
		this.autoLoads = _autoLoads;
	}

	public Boolean getTreeisonlyone() {
		return treeisonlyone;
	}

	public void setTreeisonlyone(Boolean treeisonlyone) {
		this.treeisonlyone = treeisonlyone;
	}

	public Boolean getTreeisshowtoolbar() {
		return treeisshowtoolbar;
	}

	public void setTreeisshowtoolbar(Boolean treeisshowtoolbar) {
		this.treeisshowtoolbar = treeisshowtoolbar;
	}

	public String getPk_pub_templet_1() {
		return pk_pub_templet_1;
	}

	public void setPk_pub_templet_1(String pk_pub_templet_1) {
		this.pk_pub_templet_1 = pk_pub_templet_1;
	}

	public Boolean getIslistautorowheight() {
		return this.islistautorowheight; //
	}

	public void setIslistautorowheight(Boolean islistautorowheight) {
		this.islistautorowheight = islistautorowheight;
	}

	public String getCardcustbtndesc() {
		return cardcustbtndesc;
	}

	public void setCardcustbtndesc(String cardcustbtndesc) {
		this.cardcustbtndesc = cardcustbtndesc;
	}

	public String getTreecustbtndesc() {
		return treecustbtndesc;
	}

	public void setTreecustbtndesc(String treecustbtndesc) {
		this.treecustbtndesc = treecustbtndesc;
	}

	public ButtonDefineVO[] getCardcustbtns() {
		return cardcustbtns;
	}

	public void setCardcustbtns(ButtonDefineVO[] cardcustbtns) {
		this.cardcustbtns = cardcustbtns;
	}

	public ButtonDefineVO[] getListcustbtns() {
		return listcustbtns;
	}

	public void setListcustbtns(ButtonDefineVO[] listcustbtns) {
		this.listcustbtns = listcustbtns;
	}

	public ButtonDefineVO[] getTreecustbtns() {
		return treecustbtns;
	}

	public void setTreecustbtns(ButtonDefineVO[] treecustbtns) {
		this.treecustbtns = treecustbtns;
	}

	public String getDatapolicy() {
		return datapolicy;
	}

	public void setDatapolicy(String datapolicy) {
		this.datapolicy = datapolicy;
	}

	public String getDatapolicymap() {
		return datapolicymap;
	}

	public void setDatapolicymap(String datapolicymap) {
		this.datapolicymap = datapolicymap;
	}

	public String getBsdatafilterclass() {
		return bsdatafilterclass;
	}

	public void setBsdatafilterclass(String bsdatafilterclass) {
		this.bsdatafilterclass = bsdatafilterclass;
	}

	public Boolean getBsdatafilterissql() {
		return bsdatafilterissql;
	}

	public void setBsdatafilterissql(Boolean bsdatafilterissql) {
		this.bsdatafilterissql = bsdatafilterissql;
	}

	public String getCardinitformula() {
		return cardinitformula;
	}

	public void setCardinitformula(String cardinitformula) {
		this.cardinitformula = cardinitformula;
	}

	public String getListinitformula() {
		return listinitformula;
	}

	public void setListinitformula(String listinitformula) {
		this.listinitformula = listinitformula;
	}

	public Boolean getTreeIsChecked() {
		return treeIsChecked;
	}

	public void setTreeIsChecked(Boolean treeIsChecked) {
		this.treeIsChecked = treeIsChecked;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getListweidudesc() {
		return listweidudesc;
	}

	public void setListweidudesc(String listweidudesc) {
		this.listweidudesc = listweidudesc;
	}

	public Boolean getCardsaveifcheck() {
		return cardsaveifcheck;
	}

	public void setCardsaveifcheck(Boolean cardsaveifcheck) {
		this.cardsaveifcheck = cardsaveifcheck;
	}

	public String getCardsaveselfdesccheck() {
		return cardsaveselfdesccheck;
	}

	public void setCardsaveselfdesccheck(String cardsaveselfdesccheck) {
		this.cardsaveselfdesccheck = cardsaveselfdesccheck;
	}

	public Boolean getIsshowcommquerybtn() {
		return isshowcommquerybtn;
	}

	public void setIsshowcommquerybtn(Boolean isshowcommquerybtn) {
		this.isshowcommquerybtn = isshowcommquerybtn;
	}

	public String getDefineRenderer() {
		return defineRenderer;
	}

	public void setDefineRenderer(String defineRenderer) {
		this.defineRenderer = defineRenderer;
	}

	/**
	 * 增加新的字段
	 * @param _newItems
	 */
	public void appendNewItemVOs(String _groupTitle, String[][] _newItems, boolean _isAtFront) {
		String[] str_allKeys = getItemKeys(); //已有的字段!
		HashSet hst = new HashSet(); //
		for (int i = 0; i < str_allKeys.length; i++) {
			hst.add(str_allKeys[i].toUpperCase()); //
		}

		ArrayList al_vos = new ArrayList(); //
		for (int i = 0; i < _newItems.length; i++) {
			if (hst.contains(_newItems[i][0].toUpperCase())) { //如果已有了,则跳过算了!
				continue; //
			}
			Pub_Templet_1_ItemVO itemVO = new Pub_Templet_1_ItemVO(); //
			itemVO.setItemkey(_newItems[i][0]); //
			itemVO.setItemname(_newItems[i][1]); //
			itemVO.setItemtype(_newItems[i][2]); //
			itemVO.setListisshowable(Boolean.TRUE); //
			itemVO.setCardisshowable(Boolean.TRUE); //
			itemVO.setListwidth(Integer.parseInt(_newItems[i][3])); //宽度
			String str_cardwidth = "400";
			String str_cardheight = "20";
			if (_newItems[i][4].indexOf("*") > 0) {
				str_cardwidth = _newItems[i][4].substring(0, _newItems[i][4].indexOf("*")); //
				str_cardheight = _newItems[i][4].substring(_newItems[i][4].indexOf("*") + 1, _newItems[i][4].length()); //
			} else {
				str_cardwidth = _newItems[i][4]; //
			}
			itemVO.setCardwidth(Integer.parseInt(str_cardwidth)); //
			itemVO.setCardHeight(Integer.parseInt(str_cardheight)); //
			if (_newItems[i][5].equals("Y")) {
				itemVO.setIswrap(true);
			} else {
				itemVO.setIswrap(false);
			}
			itemVO.setListiseditable("4"); //全部禁用!
			itemVO.setIsencrypt(Boolean.FALSE); //

			itemVO.setGrouptitle(_groupTitle); //
			al_vos.add(itemVO); //
		}

		if (al_vos.size() > 0) {
			Pub_Templet_1_ItemVO[] appendItemVOs = (Pub_Templet_1_ItemVO[]) al_vos.toArray(new Pub_Templet_1_ItemVO[0]); //
			Pub_Templet_1_ItemVO[] oldItemVOs = getItemVos(); //已有的
			Pub_Templet_1_ItemVO[] newVOs = new Pub_Templet_1_ItemVO[oldItemVOs.length + appendItemVOs.length]; //

			if (_isAtFront) { //如果是加在前面,肯定有客户喜欢加在前面!为是让客户有感觉,要加在前面!
				if (oldItemVOs[0].getGrouptitle() == null || oldItemVOs[0].getGrouptitle().trim().equals("")) {
					for (int k = 0; k < oldItemVOs.length; k++) { //
						if (oldItemVOs[k].getGrouptitle() != null && !oldItemVOs[k].getGrouptitle().trim().equals("")) { //如果有不为空
							break; //
						}
						oldItemVOs[k].setGrouptitle("单据基本信息"); //
					}
				}
				System.arraycopy(appendItemVOs, 0, newVOs, 0, appendItemVOs.length); //拷贝新加的!
				System.arraycopy(oldItemVOs, 0, newVOs, appendItemVOs.length, oldItemVOs.length); //拷贝原来的

			} else {
				System.arraycopy(oldItemVOs, 0, newVOs, 0, oldItemVOs.length); //拷贝原来的
				System.arraycopy(appendItemVOs, 0, newVOs, oldItemVOs.length, appendItemVOs.length); //拷贝新加的!
			}
			for (int i = 0; i < newVOs.length; i++) {
				newVOs[i].setPub_Templet_1VO(this); //
			}
			this.setItemVos(newVOs); //重新设置VO
		}
	}

	//判断是否加密!
	public boolean[] getItemIsEncrypt() {
		Pub_Templet_1_ItemVO[] vos = getItemVos();
		boolean[] isEncrypts = new boolean[vos.length];
		for (int i = 0; i < isEncrypts.length; i++) {
			isEncrypts[i] = vos[i].getIsencrypt();
		}
		return isEncrypts;
	}

	public Pub_Templet_1VO deepClone() {
		try {
			return (Pub_Templet_1VO) new TBUtil().deepClone(this); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 在请求查询时大量数据是不需要的! 只需一些极少的变量,所以必须重构一个!
	 * 这样上行请求时的参数对会很少,从而大大提高性能! 因为网络本身就是上行慢而下行快的!
	 * @return
	 */
	public Pub_Templet_1ParVO getParPub_Templet_1VO() {
		Pub_Templet_1ParVO parVO = new Pub_Templet_1ParVO(); //
		parVO.setTablename(this.getTablename()); //查询表名
		parVO.setSavedtablename(this.getSavedtablename()); //保存的表名
		parVO.setPkname(this.getPkname()); //主键字段名
		parVO.setPksequencename(this.getPksequencename()); //主键序列名
		parVO.setBsdatafilterclass(this.getBsdatafilterclass()); //BS端过滤器名
		parVO.setBsdatafilterissql(this.getBsdatafilterissql()); //BS过滤是否是SQL
		parVO.setTostringkey(this.getTostringkey());  //
		parVO.setTreeviewfield(this.getTreeviewfield()); //树型字面显示名!

		Pub_Templet_1_ItemVO[] thisItemVOs = this.getItemVos(); //我的子表
		Pub_Templet_1_ItemParVO[] parItemVOs = new Pub_Templet_1_ItemParVO[thisItemVOs.length]; //创建参数的子表!
		for (int i = 0; i < parItemVOs.length; i++) {
			parItemVOs[i] = new Pub_Templet_1_ItemParVO(); //
			parItemVOs[i].setItemkey(thisItemVOs[i].getItemkey()); //
			parItemVOs[i].setItemname(thisItemVOs[i].getItemname()); //
			parItemVOs[i].setItemtype(thisItemVOs[i].getItemtype()); //
			parItemVOs[i].setLoadformula(thisItemVOs[i].getLoadformula()); //
			parItemVOs[i].setSavedcolumndatatype(thisItemVOs[i].getSavedcolumndatatype()); //
			parItemVOs[i].setIssave(thisItemVOs[i].getIssave()); //
			parItemVOs[i].setIsencrypt(thisItemVOs[i].getIsencrypt()); //保存时是否加密??
			parItemVOs[i].setComBoxItemVos(thisItemVOs[i].getComBoxItemVos()); //下拉框的内容!! 重要! 容易遗漏!!
		}
		parVO.setItemVos(parItemVOs); //
		return parVO; //
	}

	public String[] getListbtnorderdesc() {
		return listbtnorderdesc;
	}

	public void setListbtnorderdesc(String[] listbtnorderdesc) {
		this.listbtnorderdesc = listbtnorderdesc;
	}

	public String getWfcustexport() {
		return wfcustexport;
	}

	public void setWfcustexport(String wfcustexport) {
		this.wfcustexport = wfcustexport;
	}

	public String getDataSqlAndOrCondition() {
		return dataSqlAndOrCondition;
	}

	public void setDataSqlAndOrCondition(String dataSqlAndOrCondition) {
		this.dataSqlAndOrCondition = dataSqlAndOrCondition;
	}

}
