package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

/**
* ��һ�δӷ�������ȡ��ģ������,�Ƿǳ����! Ȼ���ѯʱ�����ϴ�����������,�������ܺ���! �ؼ����д�����Ϣ�ǲ���Ҫ�ٴ��ϴ���,�����б���,����ʾ,�༭��ʽ��!!!
* ������httpЭ�鱾���������������п�! ������Ҫ��һ������ֻ������������Ҫ�Ķ��󴫹�ȥ!!!  ������ģ��Խ�Ǻܴ�,������ܵ�Ӱ��Խ�Ǻ���ʾ!
* һ��ʼ��ֱ�Ӹ���һ��Pub_Templet_1VO��,�����ٷ��ֻ��Ǻܴ�,�����¸���һ����! 
* ʹ��ƽ̨�������,�����156Ŀ¼��,��ʵ��ʾ,ֱ�Ӹ��Ƶ���2���λ16������!! ��ʹ�ø�����ֻ��1.2��λ16������! ������!!
* Ϊ�˱�֤���е�����,����ʹ�ø���!!!
* @author xch
*
*/
public class Pub_Templet_1ParVO implements Serializable {
	private static final long serialVersionUID = -6763959337422972024L;
	private String tablename; // ȡ���ı���
	private String savedtablename; // �������ݵı���
	private String pkname;
	private String pksequencename;
	private String bsdatafilterclass; // �����������ݹ�����!!�ǳ��ؼ�,����Ȩ�޴���Ҫ������
	private Boolean bsdatafilterissql; //�������˹��˷��ص��Ƿ���SQL,�����������,һ����ʹ��JAVA��������ĳ��HashVO�Ƿ����ʾ,һ���Ƿ���SQL,��������ԭ��SQL�������������!Ȼ��֧�ַ�ҳ!!! ��ǰ�Ļ����Ƿ�ҳ������,����Java�������ʱ�Ͳ��ܷ�ҳ!! ����ʹ��SQL��ҳ������������!!! ���Ի�����ҪSQL����!! ��ΪSQL��һ���Ե�!!
	private String tostringkey; //toString��ʾ����
	private String treeviewfield; // ���������ʾ������Ƶ�ģ���ֶ�key

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

	//�ж��Ƿ����!
	public boolean[] getItemIsEncrypt() {
		Pub_Templet_1_ItemParVO[] vos = getItemVos();
		boolean[] isEncrypts = new boolean[vos.length];
		for (int i = 0; i < isEncrypts.length; i++) {
			isEncrypts[i] = vos[i].getIsencrypt();
		}
		return isEncrypts;
	}

}
