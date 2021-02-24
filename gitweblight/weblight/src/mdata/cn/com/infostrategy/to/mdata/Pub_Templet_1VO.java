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

	private String buildFromType = null; //�������ɵ�����,�д�DBȡ,�д�Class����,�д�XML����
	private String buildFromInfo = null; //����Ǵ�Class���ɵ�,��˵��������ʲô! ����Ǵ�XML���ɵ���˵�����ĸ�XML�ļ�!

	private String pk_pub_templet_1; // ����
	private String templetcode; // ģ�����
	private String templetname; // ģ������
	private String templetname_e; // ģ������
	private String tablename; // ȡ���ı���

	private String datasourcename; // ����Դ����,���Ϊ��,���Ĭ������Դȡ��!!
	private String dataconstraint; // ����Ȩ������(SQL����)
	private String autoloadconstraint; //�Զ���������ʱ�Ĺ�������(SQL����),���뱾��ĵ����ݹ�����������һ��,��Ҳ�пɲ�һ��(�����Զ�����ʱֻ��ѯ״̬Ϊδ����Ļ���¼���,���µ�,�������ѯ��ťʱȴ���Բ�ѯ����)!!

	private String ordercondition; // ��������..
	private int autoLoads = 0; // ҳ���ʱ(�б����)�Զ���ѯ���ݵ���,�������,1-Ϊ�ջ�0ʱ��ʾ������ѯ����;2-����0��ʾ��ѯ����Ӧ�ļ�¼;3-С��0��ʾ��ѯ����������,���۶����б��������һ��!!��ǰ��������������������ʵ���Զ���ѯ,���ܸо���������!ֱ����ģ�����趨�����е����!!!������뷽�㲻��!!

	private String datapolicy; //����Ȩ�޲���
	private String datapolicymap; //����Ȩ�޲���ӳ��
	private String dataSqlAndOrCondition;//sunfujun/20121119/����������Ȩ�޵�ƴ�ӷ�ʽ
	private String bsdatafilterclass; // �����������ݹ�����!!�ǳ��ؼ�,����Ȩ�޴���Ҫ������
	private Boolean bsdatafilterissql; //�������˹��˷��ص��Ƿ���SQL,�����������,һ����ʹ��JAVA��������ĳ��HashVO�Ƿ����ʾ,һ���Ƿ���SQL,��������ԭ��SQL�������������!Ȼ��֧�ַ�ҳ!!! ��ǰ�Ļ����Ƿ�ҳ������,����Java�������ʱ�Ͳ��ܷ�ҳ!! ����ʹ��SQL��ҳ������������!!! ���Ի�����ҪSQL����!! ��ΪSQL��һ���Ե�!!

	private String pkname;
	private String pksequencename;
	private String tostringkey; //ToString��key��

	private String savedtablename; // �������ݵı���
	private String cardlayout; // ��Ƭ��������
	private Integer cardwidth; // ��Ƭ���
	private String cardBorder; // ��Ƭ�ؼ��ı߿�,�������Ƿ���������»���

	private Boolean isshowcardborder; // �Ƿ���ʾ��Ƭ�߿�,Ϊ���ڵ�������ҳ��ʱ����ʹ��!
	private Boolean isshowcardcustbtn; // ��Ƭ�Ƿ���ʾ�Զ��尴ť
	private String cardcustbtndesc; // ��Ƭ�Զ��尴ť˵��
	private String cardinitformula; // ��Ƭ��ʼ����ʽ
	private Boolean cardsaveifcheck;// ��Ƭ���治��������֤ Ĭ��������ΪN
	private String cardsaveselfdesccheck;// ��Ƭ�����Զ�����֤
	/* private Boolean groupisonlyone; */
	private ButtonDefineVO[] cardcustbtns; //

	private Boolean isshowlistpagebar; // �Ƿ���ʾ�б�ķ�ҳ��
	private Boolean islistpagebarwrap; //�б��еķ�ҳ�Ƿ���
	private Boolean isshowlistopebar; // �Ƿ���ʾ�б�Ĳ�����
	private Boolean isshowlistquickquery; // �Ƿ���ʾ�б���ٲ�ѯ
	private Boolean isshowcommquerybtn; // ����ʾͨ�ò�ѯ
	private Boolean iscollapsequickquery; //�Ƿ�������ٲ�ѯ��!!,������ʾ���ٲ�ѯ���ǰ����,������Ĭ������������,Ȼ����һ����ť����չ��!!!
	private Boolean isshowlistcustbtn; // �Ƿ���ʾ�б���Զ��尴ť��
	private String listcustbtndesc; // �б��Զ��尴ť��˵��,�ԷֺŸ���,ƴ��һ��WLTButton
	private ButtonDefineVO[] listcustbtns; //

	private int listheadheight = 27; // �б��ͷ�ĸ߶�,��ǰ��25,����������ʱ����ɵڶ����ֿ�����! ��27�����ܿ���!
	private String listrowheight; // �б��е��и�,������һ���б�!
	private Boolean islistautorowheight; // �б��Ƿ��Զ��Ÿ�??����Html�ı��һ��!!
	private Boolean listheaderisgroup; // �б�ı�ͷ�Ƿ�֧�ַ���
	private String listinitformula; // �б��ʼ����ʽ
	private String listweidudesc; // �б�ά�ȶ���
	private String[] listbtnorderdesc;

	private String cardcustpanel; // ��Ƭ���Զ������
	private String listcustpanel; // �б���Զ������

	private String treepk; // ��������е�����,ֻ���ڹ����������ʱ�õ�
	private String treeparentpk; // ��������еĶ�Ӧ����¼�����,ֻ���ڹ����������ʱ�õ�
	private String treeviewfield; // ���������ʾ������Ƶ�ģ���ֶ�key
	private String treeseqfield; // �������������������ֶ�,�����ǲ�ѯ���뱣����ж��е�,����
	private Boolean treeisshowroot; // ����������Ƿ���ʾ�����
	private Boolean treeisonlyone; // �Ƿ�ֻ��ͬʱ��һ��,�����չ��ĳһ��ʱ,ͬʱ���ֵܲ�����!!�𵽱������ݵ�����!
	private Boolean treeIsChecked; // �Ƿ��ǹ�ѡ����
	private Boolean treeisshowtoolbar; // ���ؼ��Ƿ���ʾ��������
	private String treecustbtndesc; // ���Ϳؼ��Զ��尴ť˵��
	private ButtonDefineVO[] treecustbtns; //

	private String wfcustexport = null;  //

	private String propbeanclassname; // ��������ж�Ӧ��JavaBean��Class����,ֻ���ڹ����������ʱ�õ�!

	//private String[] itemKeys; //
	private String[] realViewColumns; // ��������ͼ������,�����Ʒ����itemKeys�е�һ��,�͸�itemKeys���ֵ
	private String[] realSavedTableColumns; // �����ı������ݿ������,���϶���realViewColumns���Ӽ�
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
	//Ԭ���� 20140127��� ��Ҫ ������븡����λ��������֤������
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

	//�Զ���������ʱ��SQLԼ������!!
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

	// ��������..
	public void setOrdercondition(String ordercondition) {
		this.ordercondition = ordercondition;
	}

	// �Զ���ѯ���ݵļ�¼��..
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
	 * �����µ��ֶ�
	 * @param _newItems
	 */
	public void appendNewItemVOs(String _groupTitle, String[][] _newItems, boolean _isAtFront) {
		String[] str_allKeys = getItemKeys(); //���е��ֶ�!
		HashSet hst = new HashSet(); //
		for (int i = 0; i < str_allKeys.length; i++) {
			hst.add(str_allKeys[i].toUpperCase()); //
		}

		ArrayList al_vos = new ArrayList(); //
		for (int i = 0; i < _newItems.length; i++) {
			if (hst.contains(_newItems[i][0].toUpperCase())) { //���������,����������!
				continue; //
			}
			Pub_Templet_1_ItemVO itemVO = new Pub_Templet_1_ItemVO(); //
			itemVO.setItemkey(_newItems[i][0]); //
			itemVO.setItemname(_newItems[i][1]); //
			itemVO.setItemtype(_newItems[i][2]); //
			itemVO.setListisshowable(Boolean.TRUE); //
			itemVO.setCardisshowable(Boolean.TRUE); //
			itemVO.setListwidth(Integer.parseInt(_newItems[i][3])); //���
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
			itemVO.setListiseditable("4"); //ȫ������!
			itemVO.setIsencrypt(Boolean.FALSE); //

			itemVO.setGrouptitle(_groupTitle); //
			al_vos.add(itemVO); //
		}

		if (al_vos.size() > 0) {
			Pub_Templet_1_ItemVO[] appendItemVOs = (Pub_Templet_1_ItemVO[]) al_vos.toArray(new Pub_Templet_1_ItemVO[0]); //
			Pub_Templet_1_ItemVO[] oldItemVOs = getItemVos(); //���е�
			Pub_Templet_1_ItemVO[] newVOs = new Pub_Templet_1_ItemVO[oldItemVOs.length + appendItemVOs.length]; //

			if (_isAtFront) { //����Ǽ���ǰ��,�϶��пͻ�ϲ������ǰ��!Ϊ���ÿͻ��ио�,Ҫ����ǰ��!
				if (oldItemVOs[0].getGrouptitle() == null || oldItemVOs[0].getGrouptitle().trim().equals("")) {
					for (int k = 0; k < oldItemVOs.length; k++) { //
						if (oldItemVOs[k].getGrouptitle() != null && !oldItemVOs[k].getGrouptitle().trim().equals("")) { //����в�Ϊ��
							break; //
						}
						oldItemVOs[k].setGrouptitle("���ݻ�����Ϣ"); //
					}
				}
				System.arraycopy(appendItemVOs, 0, newVOs, 0, appendItemVOs.length); //�����¼ӵ�!
				System.arraycopy(oldItemVOs, 0, newVOs, appendItemVOs.length, oldItemVOs.length); //����ԭ����

			} else {
				System.arraycopy(oldItemVOs, 0, newVOs, 0, oldItemVOs.length); //����ԭ����
				System.arraycopy(appendItemVOs, 0, newVOs, oldItemVOs.length, appendItemVOs.length); //�����¼ӵ�!
			}
			for (int i = 0; i < newVOs.length; i++) {
				newVOs[i].setPub_Templet_1VO(this); //
			}
			this.setItemVos(newVOs); //��������VO
		}
	}

	//�ж��Ƿ����!
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
	 * �������ѯʱ���������ǲ���Ҫ��! ֻ��һЩ���ٵı���,���Ա����ع�һ��!
	 * ������������ʱ�Ĳ����Ի����,�Ӷ�����������! ��Ϊ���籾����������������п��!
	 * @return
	 */
	public Pub_Templet_1ParVO getParPub_Templet_1VO() {
		Pub_Templet_1ParVO parVO = new Pub_Templet_1ParVO(); //
		parVO.setTablename(this.getTablename()); //��ѯ����
		parVO.setSavedtablename(this.getSavedtablename()); //����ı���
		parVO.setPkname(this.getPkname()); //�����ֶ���
		parVO.setPksequencename(this.getPksequencename()); //����������
		parVO.setBsdatafilterclass(this.getBsdatafilterclass()); //BS�˹�������
		parVO.setBsdatafilterissql(this.getBsdatafilterissql()); //BS�����Ƿ���SQL
		parVO.setTostringkey(this.getTostringkey());  //
		parVO.setTreeviewfield(this.getTreeviewfield()); //����������ʾ��!

		Pub_Templet_1_ItemVO[] thisItemVOs = this.getItemVos(); //�ҵ��ӱ�
		Pub_Templet_1_ItemParVO[] parItemVOs = new Pub_Templet_1_ItemParVO[thisItemVOs.length]; //�����������ӱ�!
		for (int i = 0; i < parItemVOs.length; i++) {
			parItemVOs[i] = new Pub_Templet_1_ItemParVO(); //
			parItemVOs[i].setItemkey(thisItemVOs[i].getItemkey()); //
			parItemVOs[i].setItemname(thisItemVOs[i].getItemname()); //
			parItemVOs[i].setItemtype(thisItemVOs[i].getItemtype()); //
			parItemVOs[i].setLoadformula(thisItemVOs[i].getLoadformula()); //
			parItemVOs[i].setSavedcolumndatatype(thisItemVOs[i].getSavedcolumndatatype()); //
			parItemVOs[i].setIssave(thisItemVOs[i].getIssave()); //
			parItemVOs[i].setIsencrypt(thisItemVOs[i].getIsencrypt()); //����ʱ�Ƿ����??
			parItemVOs[i].setComBoxItemVos(thisItemVOs[i].getComBoxItemVos()); //�����������!! ��Ҫ! ������©!!
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
