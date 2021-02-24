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

	public static int LOGINUSER_DEPT_BLZHBM = 11; //��������֮�������в���
	public static int LOGINUSER_DEPT_BLZHBM_NAME = 12; //��������֮�������в�������

	public static int LOGINUSER_DEPT_BLFENGH = 13; //��������֮��������
	public static int LOGINUSER_DEPT_BLFENGH_NAME = 14; //��������֮��������

	public static int LOGINUSER_DEPT_BLFENGHBM = 15; //��������֮�������в���
	public static int LOGINUSER_DEPT_BLFENGHBM_NAME = 16; //��������֮�������в�������

	public static int LOGINUSER_DEPT_BLZHIH = 17; //��������֮����֧��
	public static int LOGINUSER_DEPT_BLZHIH_NAME = 18; //��������֮����֧��

	public static int LOGINUSER_DEPT_BLSHIYB = 19; //��������֮������ҵ��
	public static int LOGINUSER_DEPT_BLSHIYB_NAME = 20; //��������֮������ҵ��

	public static int LOGINUSER_DEPT_BLSHIYBFB = 21; //��������֮������ҵ���ֲ�
	public static int LOGINUSER_DEPT_BLSHIYBFB_NAME = 22; //��������֮������ҵ���ֲ�

	public static int LOGINUSER_CORPID = 23;//��������֮�������������/2012-06-06��
	public static int LOGINUSER_CORPNAME = 24;//��������֮������������ Gwang 2016-08-30

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
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTID) { //��������ID
				str_value = getLoginUserDeptId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTCODE) { //��������CODE
				str_value = getLoginUserDeptCode(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPTNAME) { //������������
				str_value = getLoginUserDeptName(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTID) { //��λId
				str_value = getLoginUserPostId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTCODE) { //��λ����
				str_value = getLoginUserPostCode(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_POSTNAME) { //��λ����
				str_value = getLoginUserPostName(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE) { //��������
				str_value = getLoginUserDeptCorpType(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM) { //����֮�������в���
				str_value = getLoginUserDept_BL_ZHBM(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM_NAME) { //����֮�������в���
				str_value = getLoginUserDept_BL_ZHBM_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH) { //����֮��������
				str_value = getLoginUserDept_BL_FENGH(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH_NAME) { //����֮��������
				str_value = getLoginUserDept_BL_FENGH_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM) { //����֮�������в���
				str_value = getLoginUserDept_BL_FENGHBM(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM_NAME) { //����֮�������в�������
				str_value = getLoginUserDept_BL_FENGHBM_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH) { //����֮����֧��
				str_value = getLoginUserDept_BL_ZHIH(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH_NAME) { //����֮����֧��
				str_value = getLoginUserDept_BL_ZHIH_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB) { //����֮������ҵ��
				str_value = getLoginUserDept_BL_SHIYB(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB_NAME) { //����֮������ҵ��
				str_value = getLoginUserDept_BL_SHIYB_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB) { //����֮������ҵ���ֲ�
				str_value = getLoginUserDept_BL_SHIYBFB(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB_NAME) { //����֮������ҵ���ֲ�
				str_value = getLoginUserDept_BL_SHIYBFB_NAME(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_CORPID) { //��������֮�������������/2012-06-06��
				str_value = getLoginUserCorpId(); //
			} else if (li_infotype == GetLoginUserInfo.LOGINUSER_CORPNAME) { //��������֮������������ Gwang 2016-08-30
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

	//ȡ�õ�¼��ԱID
	private String getLoginUserID() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserId(); //
		} else {
			return "";
		}
	}

	//ȡ�õ�¼��Ա����
	private String getLoginUserCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserCode(); //
		} else {
			return "";
		}
	}

	//��¼��Ա����
	private String getLoginUserName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return new WLTInitContext().getCurrSession().getLoginUserName(); //
		} else {
			return "";
		}
	}

	//��¼��Ա��������
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

	//��¼��Ա��������֮��������,ȡ�������ֵ��еĻ������ඨ��, ȡ$������=��ͷ�ļ�¼, ��������֪������������Щ��ڵ��ǻ���!"select name from pub_comboboxdict where type='��������' and code like '$������=%'";�����/2012-06-06��
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

	//��¼��Ա������������
	private String getLoginUserDeptCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDeptCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա������������
	private String getLoginUserDeptName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDeptName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��ǰ��λ
	private String getLoginUserPostId() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostId(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��ǰ��λ
	private String getLoginUserPostCode() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostCode(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��ǰ��λ����
	private String getLoginUserPostName() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlPostName(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա������������
	private String getLoginUserDeptCorpType() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_corptype(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮�������в���
	private String getLoginUserDept_BL_ZHBM() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhonghbm(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮�������в���
	private String getLoginUserDept_BL_ZHBM_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhonghbm_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮��������
	private String getLoginUserDept_BL_FENGH() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fengh(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮��������
	private String getLoginUserDept_BL_FENGH_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fengh_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮�������в���
	private String getLoginUserDept_BL_FENGHBM() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fenghbm(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮�������в���
	private String getLoginUserDept_BL_FENGHBM_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_fenghbm_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮����֧��
	private String getLoginUserDept_BL_ZHIH() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhih(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮����֧��
	private String getLoginUserDept_BL_ZHIH_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_zhih_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮������ҵ��
	private String getLoginUserDept_BL_SHIYB() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiyb(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮������ҵ��
	private String getLoginUserDept_BL_SHIYB_NAME() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiyb_name(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮������ҵ���ֲ�
	private String getLoginUserDept_BL_SHIYBFB() {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			return ClientEnvironment.getCurrLoginUserVO().getBlDept_bl_shiybfb(); //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			return ""; //
		} else {
			return "";
		}
	}

	//��¼��Ա��������֮������ҵ���ֲ�
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
	 * ȡ����Ա������Ϣ.
	 * @param _attrName
	 * @return
	 * @throws Exception
	 */
	private String getAttributeValue(String _attrName) throws Exception {
		if (li_type == WLTConstants.JEPTYPE_UI) {
			String str_result = (String) ClientEnvironment.getInstance().getCurrLoginUserVO().getAttributeValue(_attrName); //
			return str_result; //
		} else if (li_type == WLTConstants.JEPTYPE_BS) {
			cn.com.infostrategy.bs.common.CommDMO dmo = ServerEnvironment.getCommDMO(); //����DMO..
			String str_loginUserId = dmo.getCurrSession().getLoginUserId(); //
			HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select " + _attrName + "from pub_user where id='" + str_loginUserId + "'"); //����ȥ���ݿ���ȡһ��!��Ϊ����һ���Ƿ������˵���,����һ���Ǽ��ع�ʽ,������Ҫ������!
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
 * �ʴ��ֳ�����ͳһ�޸�
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
