/**************************************************************************
 * $RCSfile: GetLoginUserInfo.java,v $  $Revision: 1.7 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.ClientEnvironment;

public class GetLoginUserInfo extends PostfixMathCommand {

	public static int LOGINUSER_ID = 1; //
	public static int LOGINUSER_CODE = 2; //
	public static int LOGINUSER_NAME = 3; //

	public static int LOGINUSER_DEPTID = 4; //
	public static int LOGINUSER_DEPTCODE = 5; //
	public static int LOGINUSER_DEPTNAME = 6; //

	public static int LOGINUSER_POSTID = 7; //
	public static int LOGINUSER_POSTCODE = 8; //
	public static int LOGINUSER_POSTNAME = 9; //

	public static int LOGINUSER_DEPT_CORPTYPE = 10; //

	public static int LOGINUSER_DEPT_BLZHBM = 11; //所属部门之所属总行部门
	public static int LOGINUSER_DEPT_BLZHBM_NAME = 12; //所属部门之所属总行部门名称

	public static int LOGINUSER_DEPT_BLFENGH = 13; //所属部门之所属分行
	public static int LOGINUSER_DEPT_BLFENGH_NAME = 14; //所属部门之所属分行

	public static int LOGINUSER_DEPT_BLFENGHBM = 15; //所属部门之所属分行部门
	public static int LOGINUSER_DEPT_BLFENGHBM_NAME = 16; //所属部门之所属分行部门名称

	public static int LOGINUSER_DEPT_BLZHIH = 17; //所属部门之所属支行
	public static int LOGINUSER_DEPT_BLZHIH_NAME = 18; //所属部门之所属支行

	public static int LOGINUSER_DEPT_BLSHIYB = 19; //所属部门之所属事业部
	public static int LOGINUSER_DEPT_BLSHIYB_NAME = 20; //所属部门之所属事业部

	public static int LOGINUSER_DEPT_BLSHIYBFB = 21; //所属部门之所属事业部分部
	public static int LOGINUSER_DEPT_BLSHIYBFB_NAME = 22; //所属部门之所属事业部分部

	public static int LOGINUSER_CORPID = 23;//所属部门之所属机构【李春娟/2012-06-06】
	public static int LOGINUSER_CORPNAME = 24;//所属部门之所属机构名称 Gwang 2016-08-30

	private int li_type = -1; //
	private int li_infotype = -1; //

	private TBUtil tbUtil = new TBUtil(); //

	public GetLoginUserInfo() {
		numberOfParameters = 0; //
	}

	public GetLoginUserInfo(int _type, int _infoType) {
		numberOfParameters = 0; //
		li_type = _type;
		li_infotype = _infoType; //
	}

	/**
	 * 
	 */
	public void run(Stack _inStack) throws ParseException {
		try {
			String str_value = null; //
			if (li_infotype == GetLoginUserInfo.LOGINUSER_ID) {
				str_value = getLoginUserID(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_CODE) {
				str_value = getLoginUserCode(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_NAME) {
				str_value = getLoginUserName(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTID) { //所属机构ID
				str_value = getLoginUserDeptId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTCODE) { //所属机构CODE
				str_value = getLoginUserDeptCode(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTNAME) { //所属机构名称
				str_value = getLoginUserDeptName(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTID) { //岗位Id
				str_value = getLoginUserPostId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTCODE) { //岗位编码
				str_value = getLoginUserPostCode(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTNAME) { //岗位名称
				str_value = getLoginUserPostName(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE) { //机构类型
				str_value = getLoginUserDeptCorpType(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM) { //机构之所属总行部门
				str_value = getLoginUserDept_BL_ZHBM(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM_NAME) { //机构之所属总行部门
				str_value = getLoginUserDept_BL_ZHBM_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH) { //机构之所属分行
				str_value = getLoginUserDept_BL_FENGH(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH_NAME) { //机构之所属分行
				str_value = getLoginUserDept_BL_FENGH_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM) { //机构之所属分行部门
				str_value = getLoginUserDept_BL_FENGHBM(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM_NAME) { //机构之所属分行部门名称
				str_value = getLoginUserDept_BL_FENGHBM_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH) { //机构之所属支行
				str_value = getLoginUserDept_BL_ZHIH(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH_NAME) { //机构之所属支行
				str_value = getLoginUserDept_BL_ZHIH_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB) { //机构之所属事业部
				str_value = getLoginUserDept_BL_SHIYB(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB_NAME) { //机构之所属事业部
				str_value = getLoginUserDept_BL_SHIYB_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB) { //机构之所属事业部分部
				str_value = getLoginUserDept_BL_SHIYBFB(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB_NAME) { //机构之所属事业部分部
				str_value = getLoginUserDept_BL_SHIYBFB_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_CORPID) { //所属部门之所属机构【李春娟/2012-06-06】
				str_value = getLoginUserCorpId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_CORPNAME) { //所属部门之所属机构名称 Gwang 2016-08-30
				str_value = getLoginUserCorpName(); //
			}

			if (str_value == null) {
				str_value = ""; //
			}
			_inStack.push(str_value); //
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	//取得登录人员ID
	private String getLoginUserID() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserId(); //
		} else {
			return "";
		}
	}

	//取得登录人员编码
	private String getLoginUserCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserCode(); //
		} else {
			return "";
		}
	}

	//登录人员名称
	private String getLoginUserName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserName(); //
		} else {
			return "";
		}
	}

	//登录人员所属机构
	private String getLoginUserDeptId() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			try {
				String str_curruserid = new WLTInitContext().getCurrSession().getLoginUserId(); //
				String str_deptid = new CommDMO().getStringValueByDS(null, "select userdept from pub_user_post where userid='" + str_curruserid + "' and isdefault='Y'"); //
				return str_deptid; //
			} catch (Exception ex) {
				tbUtil.printStackTrace(ex); //
				return "-99999"; //
			}
		} else {
			return "-99999";
		}
	}

	//登录人员所属部门之所属机构,取下拉框字典中的机构分类定义, 取$本机构=开头的记录, 这样可以知道机构树中哪些类节点是机构!"select name from pub_comboboxdict where type='机构分类' and code like '$本机构=%'";【李春娟/2012-06-06】
	private String getLoginUserCorpId() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getInstance().getLoginUserCorpId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			try {
				String pkdept = new WLTInitContext().getCurrSession().getLoginUserPKDept(); //
				if (pkdept == null || pkdept.trim().equals("")) {
					return "-99999"; //
				} else {
					HashVO corpVO = new SysAppDMO().getUserCorpVO(pkdept);
					if (corpVO == null) {
						return "-99999"; //
					} else {
						return corpVO.getStringValue("id");
					}
				}
			} catch (Exception ex) {
				tbUtil.printStackTrace(ex); //
				return "-99999"; //
			}
		} else {
			return "-99999";
		}
	}
	
	private String getLoginUserCorpName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getInstance().getLoginUserCorpName();
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			try {
				String pkdept = new WLTInitContext().getCurrSession().getLoginUserPKDept(); //
				if (pkdept == null || pkdept.trim().equals("")) {
					return "-99999"; //
				} else {
					HashVO corpVO = new SysAppDMO().getUserCorpVO(pkdept);
					if (corpVO == null) {
						return "-99999"; //
					} else {
						return corpVO.getStringValue("name");
					}
				}
			} catch (Exception ex) {
				tbUtil.printStackTrace(ex); //
				return "-99999"; //
			}
		} else {
			return "-99999";
		}
	}

	//登录人员所属机构编码
	private String getLoginUserDeptCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDeptCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构名称
	private String getLoginUserDeptName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDeptName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员当前岗位
	private String getLoginUserPostId() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员当前岗位
	private String getLoginUserPostCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员当前岗位名称
	private String getLoginUserPostName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构类型
	private String getLoginUserDeptCorpType() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_corptype(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属总行部门
	private String getLoginUserDept_BL_ZHBM() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhonghbm(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属总行部门
	private String getLoginUserDept_BL_ZHBM_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhonghbm_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属分行
	private String getLoginUserDept_BL_FENGH() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fengh(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属分行
	private String getLoginUserDept_BL_FENGH_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fengh_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属分行部门
	private String getLoginUserDept_BL_FENGHBM() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fenghbm(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属分行部门
	private String getLoginUserDept_BL_FENGHBM_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fenghbm_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属支行
	private String getLoginUserDept_BL_ZHIH() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhih(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属支行
	private String getLoginUserDept_BL_ZHIH_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhih_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属事业部
	private String getLoginUserDept_BL_SHIYB() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiyb(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属事业部
	private String getLoginUserDept_BL_SHIYB_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiyb_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属事业部分部
	private String getLoginUserDept_BL_SHIYBFB() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiybfb(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//登录人员所属机构之所属事业部分部
	private String getLoginUserDept_BL_SHIYBFB_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiybfb_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	/**
	 * 取得人员基本信息.
	 * @param _attrName
	 * @return
	 * @throws Exception
	 */
	private String getAttributeValue(String _attrName) throws Exception {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			String str_result = (String) ClientEnvironment.getInstance().getCurrLoginUserVO().getAttributeValue(_attrName); //
			return str_result; //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			cn.com.infostrategy.bs.common.CommDMO dmo = ServerEnvironment.getCommDMO(); //创建DMO..
			String str_loginUserId = dmo.getCurrSession().getLoginUserId(); //
			HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select " + _attrName + "from pub_user where id='" + str_loginUserId + "'"); //重新去数据库中取一下!因为这里一般是服务器端调用,而且一般是加载公式,所以需要做缓存!
			String str_result = hvs[0].getStringValue(0); //
			return str_result;
		} else {
			return "";
		}
	}
}

/**************************************************************************
 * $RCSfile: GetLoginUserInfo.java,v $  $Revision: 1.7 $  $Date: 2012/10/08 02:22:49 $
 *
 * $Log: GetLoginUserInfo.java,v $
 * Revision 1.7  2012/10/08 02:22:49  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2012/06/06 08:00:41  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.2  2011/05/17 06:55:37  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
