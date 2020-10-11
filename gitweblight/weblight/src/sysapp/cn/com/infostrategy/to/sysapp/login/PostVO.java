package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

/**
 * 一个人员所有的岗位信息,其中包括机构信息!!
 * @author xch
 *
 */
public class PostVO implements Serializable {
	private static final long serialVersionUID = 5468856041510973251L;

	private String id; //岗位ID
	private String code; //岗位编码
	private String name; //岗位名称

	private String blDeptId; //该岗位所属机构一个键
	private String blDeptCode; //该岗位所属机构编码
	private String blDeptName; //该岗位所属机构名称

	private String blDept_corptype; //所属机构之机构类型

	private String blDept_bl_zhonghbm; //所属机构之所属总行部门
	private String blDept_bl_zhonghbm_name; //所属机构之所属总行部门名称

	private String blDept_bl_fengh; //所属机构之所属分行
	private String blDept_bl_fengh_name; //所属机构之所属分行名称

	private String blDept_bl_fenghbm; //所属机构之所属分行部门
	private String blDept_bl_fenghbm_name; //所属机构之所属分行部门名称

	private String blDept_bl_zhih; //所属机构之所属支行
	private String blDept_bl_zhih_name; //所属机构之所属支行名称

	private String blDept_bl_shiyb; //所属机构之所属事业部
	private String blDept_bl_shiyb_name; //所属机构之所属事业部名称

	private String blDept_bl_shiybfb; //所属机构之所属事业部分部.
	private String blDept_bl_shiybfb_name; //所属机构之所属事业部分部名称.

	private String corpdistinct;//zzl[2020-5-19] 添加机构是否涉农类型
	private String corpclass;//zzl[2020-5-19] 添加机构涉农的分类

	private boolean isDefault; //是否是默认岗位,即在一个人所属的多个岗位中,只有一个岗位是默认岗位!!然后在所有处理业务功能点时,都是按默认岗位进行的!也就意味着需要二次登录,切换岗位!!这个功能非常关键与重要!!

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
