package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

/**
* 第一次从服务器端取得模板对象后,是非常大的! 然后查询时总是上传到服务器端,必须性能很慢! 关键是有大量信息是不需要再次上传的,比如列表宽度,是显示,编辑公式等!!!
* 尤其是http协议本身是上行慢而下行快! 所以需要搞一个对象只将服务器端需要的对象传过去!!!  如果这个模板越是很大,则对性能的影响越是很显示!
* 一开始是直接复制一个Pub_Templet_1VO的,但跟踪发现还是很大,所以新搞了一个类! 
* 使用平台输出工具,输出至156目录下,事实显示,直接复制的有2万多位16进制码!! 而使用该类则只有1.2万位16进制码! 相差不少呢!!
* 为了保证上行的性能,必须使用该类!!!
* @author xch
*
*/
public class Pub_Templet_1ParVO implements Serializable {
	private static final long serialVersionUID = -6763959337422972024L;
	private String tablename; // 取数的表名
	private String savedtablename; // 保存数据的表名
	private String pkname;
	private String pksequencename;
	private String bsdatafilterclass; // 服务器端数据过滤器!!非常关键,数据权限大量要依靠他
	private Boolean bsdatafilterissql; //服务器端过滤返回的是否是SQL,即有两种情况,一种是使用JAVA计算设置某个HashVO是否可显示,一种是返回SQL,即还是在原来SQL后面接上新条件!然后支持分页!!! 以前的机制是分页有问题,即有Java计算过滤时就不能分页!! 但不使用SQL分页性能又有问题!!! 所以还是需要SQL过滤!! 因为SQL是一次性的!!
	private String tostringkey; //toString显示的列
	private String treeviewfield; // 树型面板显示结点名称的模板字段key

	private Pub_Templet_1_ItemParVO[] itemVos = null; //

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getSavedtablename() {
		return savedtablename;
	}

	public void setSavedtablename(String savedtablename) {
		this.savedtablename = savedtablename;
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

	public String getTostringkey() {
		return tostringkey;
	}

	public void setTostringkey(String tostringkey) {
		this.tostringkey = tostringkey;
	}

	public String getTreeviewfield() {
		return treeviewfield;
	}

	public void setTreeviewfield(String treeviewfield) {
		this.treeviewfield = treeviewfield;
	}

	public Pub_Templet_1_ItemParVO[] getItemVos() {
		return itemVos;
	}

	public void setItemVos(Pub_Templet_1_ItemParVO[] _itemVos) {
		this.itemVos = _itemVos; //
	}

	public String[] getItemKeys() {
		Pub_Templet_1_ItemParVO[] vos = getItemVos();
		String[] keys = new String[vos.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = vos[i].getItemkey();
		}
		return keys;
	}

	public String[] getItemNames() {
		Pub_Templet_1_ItemParVO[] vos = getItemVos();
		String[] names = new String[vos.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = vos[i].getItemname();
		}
		return names;
	}

	public String[] getItemTypes() {
		Pub_Templet_1_ItemParVO[] vos = getItemVos();
		String[] types = new String[vos.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = vos[i].getItemtype();
		}
		return types;
	}

	//判断是否加密!
	public boolean[] getItemIsEncrypt() {
		Pub_Templet_1_ItemParVO[] vos = getItemVos();
		boolean[] isEncrypts = new boolean[vos.length];
		for (int i = 0; i < isEncrypts.length; i++) {
			isEncrypts[i] = vos[i].getIsencrypt();
		}
		return isEncrypts;
	}

}
