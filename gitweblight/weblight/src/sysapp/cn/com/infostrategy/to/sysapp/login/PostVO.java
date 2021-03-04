package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

/**
 * һ����Ա���еĸ�λ��Ϣ,���а���������Ϣ!!
 * @author xch
 *
 */
public class PostVO implements Serializable {
	private static final long serialVersionUID = 5468856041510973251L;

	private String id; //��λID
	private String code; //��λ����
	private String name; //��λ����

	private String blDeptId; //�ø�λ��������һ����
	private String blDeptCode; //�ø�λ������������
	private String blDeptName; //�ø�λ������������

	private String blDept_corptype; //��������֮��������

	private String blDept_bl_zhonghbm; //��������֮�������в���
	private String blDept_bl_zhonghbm_name; //��������֮�������в�������

	private String blDept_bl_fengh; //��������֮��������
	private String blDept_bl_fengh_name; //��������֮������������

	private String blDept_bl_fenghbm; //��������֮�������в���
	private String blDept_bl_fenghbm_name; //��������֮�������в�������

	private String blDept_bl_zhih; //��������֮����֧��
	private String blDept_bl_zhih_name; //��������֮����֧������

	private String blDept_bl_shiyb; //��������֮������ҵ��
	private String blDept_bl_shiyb_name; //��������֮������ҵ������

	private String blDept_bl_shiybfb; //��������֮������ҵ���ֲ�.
	private String blDept_bl_shiybfb_name; //��������֮������ҵ���ֲ�����.

	private String corpdistinct;//zzl[2020-5-19] ��ӻ����Ƿ���ũ����
	private String corpclass;//zzl[2020-5-19] ��ӻ�����ũ�ķ���

	private boolean isDefault; //�Ƿ���Ĭ�ϸ�λ,����һ���������Ķ����λ��,ֻ��һ����λ��Ĭ�ϸ�λ!!Ȼ�������д���ҵ���ܵ�ʱ,���ǰ�Ĭ�ϸ�λ���е�!Ҳ����ζ����Ҫ���ε�¼,�л���λ!!������ܷǳ��ؼ�����Ҫ!!

	public String getCorpdistinct() {
		return corpdistinct;
	}

	public void setCorpdistinct(String corpdistinct) {
		this.corpdistinct = corpdistinct;
	}

	public String getCorpclass() {
		return corpclass;
	}

	public void setCorpclass(String corpclass) {
		this.corpclass = corpclass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBlDeptCode() {
		return blDeptCode;
	}

	public void setBlDeptCode(String blDeptCode) {
		this.blDeptCode = blDeptCode;
	}

	public String getBlDeptName() {
		return blDeptName;
	}

	public void setBlDeptName(String blDeptName) {
		this.blDeptName = blDeptName;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getBlDept_corptype() {
		return blDept_corptype;
	}

	public void setBlDept_corptype(String blDept_corptype) {
		this.blDept_corptype = blDept_corptype;
	}

	public String getBlDept_bl_zhonghbm() {
		return blDept_bl_zhonghbm;
	}

	public void setBlDept_bl_zhonghbm(String blDept_bl_zhonghbm) {
		this.blDept_bl_zhonghbm = blDept_bl_zhonghbm;
	}

	public String getBlDept_bl_zhonghbm_name() {
		return blDept_bl_zhonghbm_name;
	}

	public void setBlDept_bl_zhonghbm_name(String blDept_bl_zhonghbm_name) {
		this.blDept_bl_zhonghbm_name = blDept_bl_zhonghbm_name;
	}

	public String getBlDept_bl_fengh() {
		return blDept_bl_fengh;
	}

	public void setBlDept_bl_fengh(String blDept_bl_fengh) {
		this.blDept_bl_fengh = blDept_bl_fengh;
	}

	public String getBlDept_bl_fengh_name() {
		return blDept_bl_fengh_name;
	}

	public void setBlDept_bl_fengh_name(String blDept_bl_fengh_name) {
		this.blDept_bl_fengh_name = blDept_bl_fengh_name;
	}

	public String getBlDept_bl_fenghbm() {
		return blDept_bl_fenghbm;
	}

	public void setBlDept_bl_fenghbm(String blDept_bl_fenghbm) {
		this.blDept_bl_fenghbm = blDept_bl_fenghbm;
	}

	public String getBlDept_bl_fenghbm_name() {
		return blDept_bl_fenghbm_name;
	}

	public void setBlDept_bl_fenghbm_name(String blDept_bl_fenghbm_name) {
		this.blDept_bl_fenghbm_name = blDept_bl_fenghbm_name;
	}

	public String getBlDept_bl_zhih() {
		return blDept_bl_zhih;
	}

	public void setBlDept_bl_zhih(String blDept_bl_zhih) {
		this.blDept_bl_zhih = blDept_bl_zhih;
	}

	public String getBlDept_bl_zhih_name() {
		return blDept_bl_zhih_name;
	}

	public void setBlDept_bl_zhih_name(String blDept_bl_zhih_name) {
		this.blDept_bl_zhih_name = blDept_bl_zhih_name;
	}

	public String getBlDept_bl_shiyb() {
		return blDept_bl_shiyb;
	}

	public void setBlDept_bl_shiyb(String blDept_bl_shiyb) {
		this.blDept_bl_shiyb = blDept_bl_shiyb;
	}

	public String getBlDept_bl_shiyb_name() {
		return blDept_bl_shiyb_name;
	}

	public void setBlDept_bl_shiyb_name(String blDept_bl_shiyb_name) {
		this.blDept_bl_shiyb_name = blDept_bl_shiyb_name;
	}

	public String getBlDept_bl_shiybfb() {
		return blDept_bl_shiybfb;
	}

	public void setBlDept_bl_shiybfb(String blDept_bl_shiybfb) {
		this.blDept_bl_shiybfb = blDept_bl_shiybfb;
	}

	public String getBlDept_bl_shiybfb_name() {
		return blDept_bl_shiybfb_name;
	}

	public void setBlDept_bl_shiybfb_name(String blDept_bl_shiybfb_name) {
		this.blDept_bl_shiybfb_name = blDept_bl_shiybfb_name;
	}

	public String getBlDeptId() {
		return blDeptId;
	}

	public void setBlDeptId(String blDeptId) {
		this.blDeptId = blDeptId;
	}

}
