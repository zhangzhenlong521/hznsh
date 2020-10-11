package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 当前登录用户的数据VO
 * @author xch
 *
 */
public class CurrLoginUserVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id = null; //主键
	private String code = null; //编码
	private String code1 = null; //编码
	private String code2 = null; //编码
	private String code3 = null; //编码

	private String name = null; //名称
	private String creator = null;
	private Date createdate = null;

	private String telephone = null;
	private String mobile = null;
	private String email = null;
	private boolean isfunfilter = false; //默认是否,即所有菜单都出来!

	private String deskTopStyle = null; //桌面风格!!!
	private String menubomimg = null; //菜单Bom图片名称!!!
	private String status = null;
	private String userdef01 = null;
	private String userdef02 = null;
	private String userdef03 = null;
	private String userdef04 = null;
	private String userdef05 = null;
	private String userdef06 = null;
	private String userdef07 = null;
	private String userdef08 = null;
	private String userdef09 = null;
	private String userdef10 = null;
	private HashVO hashVO = null;

	//部门
	private String pKDept = null; //所属部门主键
	private String deptcode = null; //部门编码
	private String deptname = null; //部门名称
	private String deptlinkcode = null; //所属部门关联编码
	private String deptCorpType = null; //所属部门机构类型

	//机构
	private String corpID = null;
	private String corpName = null;

	private PostVO[] postVOs = null; //该人员所有的岗位与机构
	private String blPostId; //该人员所属岗位主键
	private String blPostCode; //该人员所属岗位编码
	private String blPostName; //该人员所属岗位名称
	private String blDeptId; //该人员所属岗位所属机构一个键
	private String blDeptCode; //该人员所属岗位所属机构编码
	private String blDeptName; //该人员所属岗位所属机构名称
	private String blDept_corptype; //该人员所属岗位所属机构之机构类型
	private String blDept_bl_zhonghbm; //该人员所属岗位所属机构之所属总行部门
	private String blDept_bl_zhonghbm_name; //该人员所属岗位所属机构之所属总行部门名称
	private String blDept_bl_fengh; //该人员所属岗位所属机构之所属分行
	private String blDept_bl_fengh_name; //该人员所属岗位所属机构之所属分行名称
	private String blDept_bl_fenghbm; //该人员所属岗位所属机构之所属分行部门
	private String blDept_bl_fenghbm_name; //该人员所属岗位所属机构之所属分行部门名称
	private String blDept_bl_zhih; //该人员所属岗位所属机构之所属支行
	private String blDept_bl_zhih_name; //该人员所属岗位所属机构之所属支行名称
	private String blDept_bl_shiyb; //该人员所属岗位所属机构之所属事业部
	private String blDept_bl_shiyb_name; //该人员所属岗位所属机构之所属事业部名称
	private String blDept_bl_shiybfb; //该人员所属岗位所属机构之所属事业部分部.
	private String blDept_bl_shiybfb_name; //该人员所属岗位所属机构之所属事业部分部名称.
	private int securitylevel;//该人员的密级

	private String corpdistinct;//zzl[2020-5-19] 添加机构是否涉农类型
	private String corpclass;//zzl[2020-5-19] 添加机构涉农的分类

	private boolean isCorpDeptAdmin = false; //是否是机构管理员
	private int lookAndFeelType = 0; //

	private boolean isLoadCorpFromDB = false; //王钢以前在客户端加了一个想取得人员机构id的方法getCorpID!

	//角色
	private RoleVO[] roleVOs = null; //所拥有的所有角色..

	//所有风格,即LookAndFeel
	private String[][] allLookAndFeels = null; //
	
	private int fontrevise  = 0 ; //字体放大号数
	
	private Map<String, String> roleMap;//所拥有的角色

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

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public String getCode3() {
		return code3;
	}

	public void setCode3(String code3) {
		this.code3 = code3;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMenubomimg() {
		return menubomimg;
	}

	public void setMenubomimg(String menubomimg) {
		this.menubomimg = menubomimg;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public boolean isRootUser() {
		if (this.getCode().equalsIgnoreCase("root")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到所有字段
	 * @return
	 */
	public String[] getAllAttributeNames() {
		return getHashVO().getKeys(); //
	}

	/**
	 * 得到某个字段的值
	 * @param _key
	 * @return
	 */
	public String getAttributeValue(String _key) {
		return getHashVO().getStringValue(_key);
	}

	/**
	 * 取得HashVO
	 * @return
	 */
	public HashVO getHashVO() {
		return hashVO;
	}

	/**
	 * 设置HashVO
	 * @param hashVO
	 */
	public void setHashVO(HashVO hashVO) {
		this.hashVO = hashVO;
	}

	public String getDeskTopStyle() {
		return deskTopStyle;
	}

	public void setDeskTopStyle(String deskTopStyle) {
		this.deskTopStyle = deskTopStyle;
	}

	public RoleVO[] getRoleVOs() {
		return roleVOs;
	}

	public void setRoleVOs(RoleVO[] roleVOs) {
		this.roleVOs = roleVOs;
	}

	public String getPKDept() {
		return pKDept;
	}

	public void setPKDept(String _pkdept) {
		this.pKDept = _pkdept;
	}

	public String getDeptlinkcode() {
		return deptlinkcode;
	}

	public void setDeptlinkcode(String _deptlinkcode) {
		this.deptlinkcode = _deptlinkcode;
	}

	public String getDeptcode() {
		return deptcode;
	}

	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}

	public PostVO[] getPostVOs() {
		return postVOs;
	}

	public void setPostVOs(PostVO[] postVOs) {
		this.postVOs = postVOs;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getBlPostId() {
		return blPostId;
	}

	public void setBlPostId(String blPostId) {
		this.blPostId = blPostId;
	}

	public String getBlPostCode() {
		return blPostCode;
	}

	public void setBlPostCode(String blPostCode) {
		this.blPostCode = blPostCode;
	}

	public String getBlPostName() {
		return blPostName;
	}

	public void setBlPostName(String blPostName) {
		this.blPostName = blPostName;
	}

	public String getBlDeptId() {
		return blDeptId;
	}

	public void setBlDeptId(String blDeptId) {
		this.blDeptId = blDeptId;
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

	//人员密级
	public int getSecuritylevel() {
		return securitylevel;
	}

	public void setSecuritylevel(int securitylevel) {
		this.securitylevel = securitylevel;
	}

	/**
	 * 是否是机构管理员
	 * @return
	 */
	public boolean isCorpDeptAdmin() {
		return isCorpDeptAdmin;
	}

	public void setCorpDeptAdmin(boolean isCorpDeptAdmin) {
		this.isCorpDeptAdmin = isCorpDeptAdmin;
	}

	public String getUserdef01() {
		return userdef01;
	}

	public void setUserdef01(String userdef01) {
		this.userdef01 = userdef01;
	}

	public String getUserdef02() {
		return userdef02;
	}

	public void setUserdef02(String userdef02) {
		this.userdef02 = userdef02;
	}

	public String getUserdef03() {
		return userdef03;
	}

	public void setUserdef03(String userdef03) {
		this.userdef03 = userdef03;
	}

	public String getUserdef04() {
		return userdef04;
	}

	public void setUserdef04(String userdef04) {
		this.userdef04 = userdef04;
	}

	public String getUserdef05() {
		return userdef05;
	}

	public void setUserdef05(String userdef05) {
		this.userdef05 = userdef05;
	}

	public String getUserdef06() {
		return userdef06;
	}

	public void setUserdef06(String userdef06) {
		this.userdef06 = userdef06;
	}

	public String getUserdef07() {
		return userdef07;
	}

	public void setUserdef07(String userdef07) {
		this.userdef07 = userdef07;
	}

	public String getUserdef08() {
		return userdef08;
	}

	public void setUserdef08(String userdef08) {
		this.userdef08 = userdef08;
	}

	public String getUserdef09() {
		return userdef09;
	}

	public void setUserdef09(String userdef09) {
		this.userdef09 = userdef09;
	}

	public String getUserdef10() {
		return userdef10;
	}

	public void setUserdef10(String userdef10) {
		this.userdef10 = userdef10;
	}

	/**
	 * 取得登录人员所有角色编码
	 * @return
	 */
	public String[] getAllRoleCodes() {
		if (this.roleVOs == null) {
			return null;
		}
		String[] roleCodes = new String[roleVOs.length];//
		for (int i = 0; i < roleCodes.length; i++) {
			roleCodes[i] = roleVOs[i].getCode(); //
		}
		return roleCodes; //
	}

	/**
	 * 取得登录人员所有角色编码
	 * @return
	 */
	public String[] getAllRoleNames() {
		if (this.roleVOs == null) {
			return null;
		}
		String[] roleNames = new String[roleVOs.length];//
		for (int i = 0; i < roleNames.length; i++) {
			roleNames[i] = roleVOs[i].getName(); //
		}
		return roleNames; //
	}

	/**
	 * 取得登录人员所有角色编码
	 * @return
	 */
	public String[][] getAllRoleCodeNames() {
		if (this.roleVOs == null) {
			return null;
		}
		String[][] roleCodeNames = new String[roleVOs.length][2];//
		for (int i = 0; i < roleCodeNames.length; i++) {
			roleCodeNames[i] = new String[] { roleVOs[i].getCode(), roleVOs[i].getName() }; //
		}
		return roleCodeNames; //
	}

	/**
	 * 取得登录人员所有角色主键
	 * @return
	 */
	public String[] getAllRoleIds() {
		if (this.roleVOs == null) {
			return null;
		}

		String[] roleIds = new String[roleVOs.length];//
		for (int i = 0; i < roleIds.length; i++) {
			roleIds[i] = roleVOs[i].getId(); //
		}
		return roleIds; //
	}

	/**
	 * 取得登录人员所有岗位编码
	 * @return
	 */
	public String[] getAllPostCodes() {
		ArrayList al_posts = new ArrayList(); //

		if (this.postVOs != null) {
			for (int i = 0; i < postVOs.length; i++) {
				al_posts.add(postVOs[i].getCode()); //
			}
		}

		return (String[]) al_posts.toArray(new String[0]); //
	}

	/**
	 * 取得登录人员所有岗位名称
	 * @return
	 */
	public String[] getAllPostNames() {
		ArrayList al_posts = new ArrayList(); //

		if (this.postVOs != null) {
			for (int i = 0; i < postVOs.length; i++) {
				al_posts.add(postVOs[i].getName()); //
			}
		}
		return (String[]) al_posts.toArray(new String[0]); //
	}

	public int getLookAndFeelType() {
		return lookAndFeelType;
	}

	public void setLookAndFeelType(int lookAndFeelType) {
		this.lookAndFeelType = lookAndFeelType;
	}

	public String[][] getAllLookAndFeels() {
		return allLookAndFeels;
	}

	public void setAllLookAndFeels(String[][] allLookAndFeels) {
		this.allLookAndFeels = allLookAndFeels;
	}

	@Override
	public String toString() {
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("人员ID:[" + this.getId() + "]\r\n");//
		sb_text.append("人员编码:[" + this.getCode() + "]\r\n");//
		sb_text.append("人员名称:[" + this.getName() + "]\r\n");//
		sb_text.append("是否机构管理员:[" + this.isCorpDeptAdmin() + "]\r\n");//

		sb_text.append("\r\n");
		sb_text.append("当前机构ID:[" + this.getBlDeptId() + "]\r\n");
		sb_text.append("当前机构编码:[" + this.getBlDeptCode() + "]\r\n");
		sb_text.append("当前机构名称:[" + this.getBlDeptName() + "]\r\n"); //

		sb_text.append("\r\n");
		sb_text.append("当前岗位ID:[" + this.getBlPostId() + "]\r\n");
		sb_text.append("当前岗位编码:[" + this.getBlPostCode() + "]\r\n");
		sb_text.append("当前岗位名称:[" + this.getBlPostName() + "]\r\n");

		sb_text.append("\r\n");
		sb_text.append("登录人员所有角色:\r\n");
		RoleVO[] allRoles = getRoleVOs(); //
		if (allRoles != null) {
			for (int i = 0; i < allRoles.length; i++) {
				sb_text.append("角色编码=[" + allRoles[i].getCode() + "],角色名称=[" + allRoles[i].getName() + "],该角色的所属机构[" + allRoles[i].getUserdeptpk() + "," + allRoles[i].getUserdeptcode() + "," + allRoles[i].getUserdeptname() + "]\r\n"); //
			}
		}

		sb_text.append("\r\n");
		sb_text.append("登录人员所有岗位:\r\n");
		PostVO[] allPosts = getPostVOs(); //
		if (allPosts != null) {
			for (int i = 0; i < allPosts.length; i++) {
				sb_text.append("岗位编码=[" + allPosts[i].getCode() + "],岗位名称=[" + allPosts[i].getName() + "],所属机构=[" + allPosts[i].getBlDeptCode() + "," + allPosts[i].getBlDeptName() + "]\r\n"); //
			}
		}

		return sb_text.toString();
	}

	public boolean isFunfilter() {
		return isfunfilter;
	}

	public void setFunfilter(boolean isfunfilter) {
		this.isfunfilter = isfunfilter;
	}

	/**
	 * @param corpID the corpID to set
	 */
	public void setCorpID(String corpID) {
		this.corpID = corpID;
	}

	/**
	 * 王钢当初在登录时一口气计算出登录人员的所在机构(即$机构),但实际上在登录后并不是马上使用!
	 * 结果就造成登录逻辑过于复杂! 造成在邮储项目中性能测试不行! 一般都要15秒,所以不得已放在这里!
	 * 即只在第一次使用时才去数据库查询!!
	 * 按道理不应该放在VO里,而应该放在UIUtil中,但因为考虑到别人已经使用了,所以先放在这里!
	 * @return
	 */
	public String getCorpID() {
		if (isLoadCorpFromDB) { //如果已经加载过数据,则直接返回
			return corpID; //
		}
		isLoadCorpFromDB = true; //

		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {
			//老徐搞的方法有问题, 根本取不到数据, 还是用王钢之前写的方法
			//			String[] str_corpIdName = cn.com.infostrategy.ui.common.UIUtil.getLoginUserCorpIdName(getPKDept(), getDeptlinkcode(), getDeptCorpType()); //
			//			if (str_corpIdName != null) {
			//				corpID = str_corpIdName[0]; //
			//				corpName = str_corpIdName[1]; //
			//			}
			try {
				cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
				HashVO vo = sysService.getLoginUserCorpVO();
				corpID = vo.getStringValue("id");
				corpName = vo.getStringValue("name");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return corpID;
	}

	/**
	 * 王钢当初在登录时一口气计算出登录人员的所在机构(即$机构),但实际上在登录后并不是马上使用!
	 * 结果就造成登录逻辑过于复杂! 造成在邮储项目中性能测试不行! 一般都要15秒,所以不得已放在这里!
	 * 即只在第一次使用时才去数据库查询!!
	 * 按道理不应该放在VO里,而应该放在UIUtil中,但因为考虑到别人已经使用了,所以先放在这里!
	 * @return
	 */
	public String getCorpName() {
		if (isLoadCorpFromDB) { //如果已经加载过数据,则直接返回
			return corpName; //
		}
		isLoadCorpFromDB = true; //
		//老徐搞的方法有问题, 根本取不到数据, 还是用王钢之前写的方法
//		String[] str_corpIdName = cn.com.infostrategy.ui.common.UIUtil.getLoginUserCorpIdName(getPKDept(), getDeptlinkcode(), getDeptCorpType()); //
//		if (str_corpIdName != null) {
//			corpID = str_corpIdName[0]; //
//			corpName = str_corpIdName[1]; //
//		}
		try {
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
			HashVO vo = sysService.getLoginUserCorpVO();
			corpID = vo.getStringValue("id");
			corpName = vo.getStringValue("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpName;
	}

	/**
	 * @param corpName the corpName to set
	 */
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getDeptCorpType() {
		return deptCorpType;
	}

	public void setDeptCorpType(String deptCorpType) {
		this.deptCorpType = deptCorpType;
	}

	public int getFontrevise() {
		return fontrevise;
	}

	public void setFontrevise(int fontrevise) {
		this.fontrevise = fontrevise;
	}
	public Map<String, String> getRoleMap() {
		return roleMap;
	}
	
	public void setRoleMap(Map<String, String> roleMap) {
		this.roleMap = roleMap;
	}

}
