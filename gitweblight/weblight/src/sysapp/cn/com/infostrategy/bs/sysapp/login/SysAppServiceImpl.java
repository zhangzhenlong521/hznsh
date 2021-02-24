package cn.com.infostrategy.bs.sysapp.login;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.RolesAndMenuRelationDMO;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.bs.sysapp.database.compare.WLTDictDMO;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.install.database.DataBaseUtilDMO;
import cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO;
import cn.com.infostrategy.bs.sysapp.runtime.RunTimeActionBSUtil;
import cn.com.infostrategy.bs.sysapp.systemfile.SystemFileDMO;
import cn.com.infostrategy.bs.sysapp.transferdb.ExportDBDMO;
import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOComparator;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupDefineVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.to.sysapp.login.PostVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

public class SysAppServiceImpl implements SysAppServiceIfc {

	private HashMap tableColsMap = new HashMap(); // ��¼pub_user��pub_user_role�ȱ���ֶδ���

	// public static HashVO[] static_hvos_post = null; //
	// public static HashVO[] static_hvos_role = null; //
	// public static String[][] static_str_commrole_menus = null; //
	// public static String[][] static_str_userrole_menus = null; //
	// public static String[] static_str_user_menus = null; //

	/**
	 * һ���Ե�¼,����ԭ��������Զ�̷��ʲ���һ��!!!!
	 * @param _usercode
	 * @param _pwd
	 * @param _adminpwd
	 * @param isAdmin
	 * @param _isquicklogin
	 * @return
	 * @throws Exception
	 */
	public LoginOneOffVO loginOneOff(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, Properties _clientProp, String checkcode) throws Exception {
		// String[] str_keys = _clientProp.keySet().toArray(new String[0]); //
		// for (int i = 0; i < str_keys.length; i++) {
		// if (str_keys[i].startsWith("REQ_")) {
		// System.out.println("Clint prop[" + str_keys[i] + "]=[" +
		// _clientProp.getProperty(str_keys[i]) + "]"); //
		// if (str_keys[i].equals("REQ_username")) {
		// System.out.println("16���Ʒ�ת" + new
		// TBUtil().convertHexStringToStr(_clientProp.getProperty(str_keys[i])));
		// //
		// }
		// }
		// }

		String userName = "";
		// ����ǵ����¼, ������һЩ���⴦��
		if (_isquicklogin) {
			// �ƹ�ϵͳ�е�¼��(���֤��)+�û���(����)����ȷ��Ψһ�û�(��Ϊ���֤�����ظ���!)
			// login?logintype=single&usercode=zxb&username=ղ�˰�
			if (!TBUtil.isEmpty(_clientProp.getProperty("REQ_username"))) {
				// ��������ʱ����16�����ֻ�, ����Ҫ16���Ʒ�ת!
				userName = new TBUtil().convertHexStringToStr(_clientProp.getProperty("REQ_username"));
			}
		}

		LoginOneOffVO loginOneOffVO = new LoginOneOffVO(); // һ���Ե�¼����VO

		Log4jConfigVO log4jConfigVO = ServerEnvironment.getInstance().getLog4jConfigVO(); // log4j������ϢVO
		DataSourceVO[] dataSourceVOs = ServerEnvironment.getInstance().getDataSourceVOs(); // ����Դ��ϢVO
		CurrLoginUserVO loginUserVO = null;
		if (userName.equals("")) {
			loginUserVO = login(_usercode, _pwd, _adminpwd, isAdmin, _isquicklogin, checkcode); // ȡ�õ�¼��Ա����Ϣ..
		} else {
			loginUserVO = login(_usercode + "$" + userName + "$", _pwd, _adminpwd, isAdmin, _isquicklogin, checkcode); // ȡ�õ�¼��Ա����Ϣ(��¼)
		}

		DeskTopVO deskTopVO = getDeskTopVO(loginUserVO.getId(), _usercode, loginUserVO.isFunfilter(), loginUserVO.getAllRoleIds()); //

		loginOneOffVO.setLog4jConfigVO(log4jConfigVO); //
		loginOneOffVO.setDataSourceVOs(dataSourceVOs); //
		loginOneOffVO.setCurrLoginUserVO(loginUserVO); //
		loginOneOffVO.setDeskTopVO(deskTopVO); //
		deskTopVO.setLoginTime(new TBUtil().getCurrTime()); //
		System.gc(); //

		// ����ǰSession��ע���¼�û�����Ϣ!!!
		// String str_sessionId = new
		// WLTInitContext().getCurrSession().getHttpsessionid(); //
		// System.out.println("��ǰ��¼�û���SessionID[" + str_sessionId +
		// "],����Ӧ��ע��һ�¸��û��ĵ�ǰ������ɫ,��ǰ��������,��������������,���������ı���!!!"); //

		return loginOneOffVO; //
	}

	/**
	 * ʵ�ʵ�¼�߼�!!!
	 */

	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin) throws Exception {
		return login(_usercode, _pwd, _adminpwd, isAdmin, _isquicklogin, null);
	}

	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, String checkcode) throws Exception {
		if (_usercode.equalsIgnoreCase("pushroot") && "pushworld123".equals(_pwd)) { // ����ǳ����û�,��ֹû���û�ʱ���ܵ�¼!!��ǰ��root,��root̫������,�±���ײ��,���Ը�Ϊpushroot
			CurrLoginUserVO userVO = new CurrLoginUserVO(); //
			userVO.setId("-999"); //
			userVO.setCode("pushroot"); //
			userVO.setName("pushroot"); //
			userVO.setDeskTopStyle("A"); //
			return userVO;
		}
		// Ԭ����20161202�޸� 20161228 ��Ҫ����Ϊuuid�������֣����ڲ������ֵ���Ҫ��ά����
		// ��ʵ����ط������⣬������Ӧ��ֻ�е����¼ʱ���жϣ������������ǵ����¼ʱ��_isquickloginҲΪfalse�����Բ����ж�
		String uuid = "";// ���ں���Ҫ����֤���������ȱ�����
		CommDMO dmo = new CommDMO();
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("�����¼̫ƽר��", false) && (_usercode != null && _usercode.matches("[0-9]+"))) {
			_usercode = dmo.getStringValueByDS(null, "select code from pub_user where uuid='" + _usercode + "'");
			System.out.println("�����¼ת�����usercode=================" + _usercode);
		} else if (TBUtil.getTBUtil().getSysOptionBooleanValue("�����¼̫ƽר��", false) && (_usercode != null && !_usercode.matches("[0-9]+"))) {// ��ʾ�ӿͻ��˵�¼
			uuid = dmo.getStringValueByDS(null, "select uuid from pub_user where code='" + _usercode + "'");
		}
		TBUtil tbutil = new TBUtil();
		// ���������Ŀ����һ�����������������������������ϵͳ������ʵ��ʹ��ʱ���Ჿ�������ϣ��ܶ�����һ��ʹ�ã���ʵ�����£��ʸ���session���˸����������������߼������/2012-05-17��
		if (!ServerEnvironment.isLoadRunderCall) { // �����LV��������
			String str_currtime = tbutil.getCurrTime(); // ��ǰʱ��
			String maxNumber = ServerEnvironment.getProperty("PROJECT_MU");// ���ϵͳ���ͬʱ��¼������������Ϊ�˲��ÿͻ�֪�����������ƣ�������ȡ�˸�����"PROJECT_MU"�����/2012-05-17��
			int waitMinute = 3;// ���÷�����ʱ�䣨���ӣ���Ĭ��Ϊ3����
			if (maxNumber == null || "".equals(maxNumber)) {
				maxNumber = "500";// Ĭ��Ϊ500�ˣ��ʴ���Ŀ�з�����ǰ���õ�50��̫С�ˣ�����Ĳ������ͻ�����Ҫ���Ÿģ���������ʦ�������ﻹ�ǸĴ�һЩ�ɡ����/2013-10-23��
			} else {
				maxNumber = tbutil.convertHexStringToStr(maxNumber);// ��һ��16���Ƹ�ʽ���ַ���ת����ԭʼ���ַ�����ʽ
				// �������ֵ�а������ţ���ʽΪ"100,5"�����ʾϵͳ���ͬʱ��¼100�ˣ����ҷ���ʱ��Ϊ5���ӣ���������в��������ţ���ʽΪ��100�������ʾϵͳ���ͬʱ��¼100�ˣ�Ĭ�Ϸ���ʱ��Ϊ3����
				if (maxNumber.contains(",")) {
					try {
						waitMinute = Integer.parseInt(maxNumber.substring(maxNumber.indexOf(",") + 1));
					} catch (Exception e) {
						throw new WLTAppException("���������Ѵ�����,���Ժ��¼!!");
					}
					maxNumber = maxNumber.substring(0, maxNumber.indexOf(","));
				}
			}
			int count_user = 1;// ��ǰ��¼�������������ʱ��¼����Ϣ��û�м��뵽session���ʳ�ʼΪ1
			HashMap mapUser = ServerEnvironment.getLoginUserMap(); //
			String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // ��ǰ���߿ͻ��˵��嵥!!!
			for (int i = 0; i < str_sesions.length; i++) {
				String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // ĳһ���û�����ϸʱ��
				long betweenSecond = tbutil.betweenTwoTimeSecond(str_onlineusers[5], str_currtime);// ��һ�����������һ�η���ʱ��,�ڶ��������ǵ�ǰʱ��
				if (betweenSecond < 60 * waitMinute) {
					count_user++;
				}
			}
			int int_maxnumber = 500;
			try {
				int_maxnumber = Integer.parseInt(maxNumber);
			} catch (Exception e) {
				throw new WLTAppException("���������Ѵ�����,���Ժ��¼!!");
			}
			if (count_user > int_maxnumber) {// �����ǰ��¼��������
				throw new WLTAppException("���������Ѵ�����,���Ժ��¼!");
			}
		}

		boolean isHaveEhrCheck = false; //
		if (ServerEnvironment.getProperty("LOGINEHRCHECK") != null && !ServerEnvironment.getProperty("LOGINEHRCHECK").trim().equals("")) {
			isHaveEhrCheck = true; //
		}
		// �����Ehr��֤���Ƚ���EHR��֤!!!
		if (!_isquicklogin && isHaveEhrCheck) {
			LoginEhrIfc ehrcheck = (LoginEhrIfc) Class.forName(ServerEnvironment.getProperty("LOGINEHRCHECK")).newInstance(); //
			ehrcheck.checkInEhr(_usercode, _pwd); // ȥ�û�HR�ӿ���У��!
		}

		// ��ʹEHR��֤�ɹ���,��Ȼ��Ҫ�ڱ�ϵͳ�в����Ƿ����û�? ��У���û��Ƿ����!
		// �ƹ������е�½�˺���ͬ����Ա������Ҫ��CODE��NAMEһͬ��У�顣���������_usercode = code$username$
		boolean codeAndName = false; // �ƹ��д����˺ţ����֤����ͬ����Ա���Ƿ����$xxx$
		String userName = null; // �洢�ָ�������
		if (_usercode.contains("$") && _usercode.endsWith("$")) {
			codeAndName = true;
			userName = _usercode.substring(_usercode.indexOf("$") + 1, _usercode.length() - 1);
			_usercode = _usercode.substring(0, _usercode.indexOf("$"));
		}

		HashVO[] hvos_user = null;
		// ����������������������Ҫ�ĵط�������������������������
		// ��ǰ��¼����֮���Ժ���,��Ҫԭ���������,��Ϊԭ�����߼���code='admin' or code1='admin' or
		// code2='admin'...
		// �⽫���ȫ��ɨ��!!����Ա��ǡǡ�����Ǽ���������!������������ǳ�����!
		// ���ڵķ����Ƿֳɶ��,��һ����code,�ڶ�����code1,���������ͻ���Ч!! �����Ϻ�����ѯ�����ݿ�,ʵ�������ܸ���!
		// ��Ϊȫ��ɨ����ڴӼ�����������в���
		// ���Ҵ󲿷�����ǵ�һ�־ͷ��سɹ�!!!
		for (int i = 0; i <= 3; i++) { // �ܹ�4��
			String str_colName = "code"; //
			if (i > 0) { //
				str_colName = str_colName + i; // ���ֱ�ƴ�� code1,code2,code3....
			}
			String str_sql = null; //
			if (codeAndName) { // �︻Ⱥ���ʴ�����Ϊ�ֻ���֤�ӵ�!!!
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("��¼�Ƿ�����STATUS��֤", false)) {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and name='" + userName + "' and  status='0'"; //
				} else {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and name='" + userName + "' "; //
				}

			} else {
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("��¼�Ƿ�����STATUS��֤", false)) {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and  status='0'"; //
				} else {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "'  "; //
				}

			}

			HashVO[] hvos_item = getDMO().getHashVoArrayByDS(null, str_sql); // �����û�!!!
			if (hvos_item != null && hvos_item.length > 0) { // ����ҵ���ֱ�ӷ���
				hvos_user = hvos_item; //
				break; // ����ҵ�����������!!�����codeƥ����,��ֱ�ӷ���,���û��,�����code1,code2,code3��!!!
			}
		}

		if (hvos_user == null || hvos_user.length <= 0) {
			if (isHaveEhrCheck) {
				throw new WLTAppException("ȥOA/HRϵͳ��֤�ɹ���,����ϵͳ��û�з����û�[" + _usercode + "].");//
			} else {
				throw new WLTAppException("��ϵͳ��û�з����û�[" + _usercode + "].");//
			}
		}
		if (hvos_user.length > 1) { // ������ֶ���û�,����ʾ�ⱨ��! �������ֽ�������!!
			StringBuilder sb_multiUser = new StringBuilder(); //
			for (int i = 0; i < hvos_user.length; i++) {
				sb_multiUser.append("��¼��[" + hvos_user[i].getStringValue("code", ""));
				if (!hvos_user[i].getStringValue("code1", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code1", "")); //
				}
				if (!hvos_user[i].getStringValue("code2", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code2", "")); //
				}
				if (!hvos_user[i].getStringValue("code3", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code3", "")); //
				}
				sb_multiUser.append("],�û���[" + hvos_user[i].getStringValue("name", "") + "]\r\n"); //
			}
			throw new WLTAppException("ϵͳ�з��ֶ�����ʺ��û�:\r\n" + sb_multiUser.toString() + "���ǲ������,����ϵͳ����Ա��ϵ!"); // ��ʾ�ҵ����!
		}

		// ������û���������,�������������ԭ��,Ȼ����ʾ!!
		if (!ServerEnvironment.isLoadRunderCall && "Y".equals(hvos_user[0].getStringValue("islock"))) { // LV����ʱ����
			if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
				// ����90��δ��¼���� ���ȼ��� �����/2013-04-27��
				if (SystemOptions.getIntegerValue("δ��¼��������", 0) > 0) {
					String dealtype = getDMO().getStringValueByDS(null, "select dealtype from (select dealtype from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum = 1");
					if (dealtype != null && dealtype.equals("��ʱ��δ��¼����")) {
						throw new WLTAppException("�û�[" + _usercode + "]����[" + SystemOptions.getIntegerValue("δ��¼��������", 90) + "]��δ��¼���ѱ�����,�������Ա��ϵ!");
					}
				}

				// ������¼ʧ��5������ �Զ����� �����/2013-04-27��
				if (SystemOptions.getIntegerValue("��¼ʧ����������", 0) > 0) {
					HashVO[] hvos_login = getDMO().getHashVoArrayByDS(null, "select dealtype,dealtime from (select dealtype,dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum = 1");
					String dealtype = hvos_login[0].getStringValue("dealtype");
					if (dealtype != null && dealtype.equals("�����������")) {
						boolean mark = true;
						if (SystemOptions.getBooleanValue("�����Ƿ��Զ�����", false)) {
							String dealtime = hvos_login[0].getStringValue("dealtime");
							if (dealtime != null && !dealtime.equals("")) {
								long days = 60 * 1000; // һ���ӵĺ�����
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date d1 = df.parse(tbutil.getCurrTime());
								Date d2 = df.parse(dealtime);
								if ((d1.getTime() - d2.getTime()) / days >= SystemOptions.getIntegerValue("�Զ�����ʱ��", 30)) {
									getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='' where code='" + _usercode + "'");
									mark = false;
								}
							}
						}

						if (mark) {
							if (SystemOptions.getBooleanValue("�����Ƿ��Զ�����", false)) {
								throw new WLTAppException("�û�[" + _usercode + "]������¼ʧ��" + SystemOptions.getIntegerValue("��¼ʧ����������", 5) + "��,�ѱ�����," + SystemOptions.getIntegerValue("�Զ�����ʱ��", 30) + "����֮���Զ�����!");
							} else {
								throw new WLTAppException("�û�[" + _usercode + "]������¼ʧ��" + SystemOptions.getIntegerValue("��¼ʧ����������", 5) + "��,�ѱ�����,�������Ա��ϵ!");
							}

						}
					}
				}
			} else {
				String[] codes = getDMO().getStringArrayFirstColByDS(null, "select code  from pub_role where id in (select roleid from pub_user_role where userid=" + hvos_user[0].getStringValue("id") + " )");
				boolean isadmin = false;
				int i = 0;
				for (; i < codes.length; i++) {
					if ("ϵͳ����Ա".equals(codes[i]) || "��ȫ����Ա".equals(codes[i])) {
						isadmin = true;
						break;
					}
				}
				if (isadmin) {
					throw new WLTAppException(codes[i] + "[" + _usercode + "]�ѱ�����,������ƹ���Ա��ϵ!");//  
				} else {
					throw new WLTAppException("�û�[" + _usercode + "]������¼ʧ��5�Σ��ѱ�����,���밲ȫ����Ա��ϵ!");//  
				}
			}
		}

		// ����90��δ��¼���� �����/2013-04-27��
		if (!ServerEnvironment.isLoadRunderCall && SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false) && SystemOptions.getIntegerValue("δ��¼��������", 0) > 0) {
			String lastLoginTime = getDMO().getStringValueByDS(null, "select dealtime from (select dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " and dealtype in('��¼ϵͳ','��ʱ��δ��¼����') order by dealtime desc) where rownum = 1");
			if (lastLoginTime != null && !lastLoginTime.equals("")) {
				int dayslenght = SystemOptions.getIntegerValue("δ��¼��������", 90);
				long days = 24 * 60 * 60 * 1000; // һ��ĺ�����
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d1 = df.parse(tbutil.getCurrTime());
				Date d2 = df.parse(lastLoginTime);
				if ((d1.getTime() - d2.getTime()) / days >= dayslenght) {
					insertSysDealLog("��ʱ��δ��¼����", hvos_user[0].getStringValue("id"), "", "", "");
					getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='Y' where code='" + _usercode + "'");
					throw new WLTAppException("�û�[" + _usercode + "]����[" + dayslenght + "]��δ��¼���ѱ�����,�������Ա��ϵ!");
				}
			}
		}

		// ������¼ʧ��5������ �����/2013-04-27��
		if (!ServerEnvironment.isLoadRunderCall && SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false) && SystemOptions.getIntegerValue("��¼ʧ����������", 0) > 0) {
			int failurecount = SystemOptions.getIntegerValue("��¼ʧ����������", 5);
			HashVO[] hvos_login = getDMO().getHashVoArrayByDS(null, "select dealtype,dealtime from (select dealtype,dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum <= " + failurecount);
			if (hvos_login.length == failurecount) {
				int count = 0;
				for (int i = 0; i < hvos_login.length; i++) {
					String dealtype = hvos_login[i].getStringValue("dealtype");
					if (dealtype != null && dealtype.equals("�������")) {
						count++;
					}
				}
				if (count == failurecount) {
					boolean mark = true;
					if (SystemOptions.getIntegerValue("��¼ʧ���ӳ�ʱ��", 0) > 0) {
						String dealtime_0 = hvos_login[0].getStringValue("dealtime");
						String dealtime_failurecount = hvos_login[failurecount - 1].getStringValue("dealtime");
						if (dealtime_0 != null && !"".equals(dealtime_0) && dealtime_failurecount != null && !"".equals(dealtime_failurecount)) {
							long days = 60 * 1000; // һ���ӵĺ�����
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date d1 = df.parse(dealtime_0);
							Date d2 = df.parse(dealtime_failurecount);
							if ((d1.getTime() - d2.getTime()) / days >= SystemOptions.getIntegerValue("��¼ʧ���ӳ�ʱ��", 30)) {
								mark = false;
							}
						}
					}

					if (mark) {
						insertSysDealLog("�����������", hvos_user[0].getStringValue("id"), "", "", "");
						getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='Y' where code='" + _usercode + "'");
						if (SystemOptions.getBooleanValue("�����Ƿ��Զ�����", false)) {
							throw new WLTAppException("�û�[" + _usercode + "]������¼ʧ��" + failurecount + "��,�ѱ�����," + SystemOptions.getIntegerValue("�Զ�����ʱ��", 30) + "����֮���Զ�����!");
						} else {
							throw new WLTAppException("�û�[" + _usercode + "]������¼ʧ��" + failurecount + "��,�ѱ�����,�������Ա��ϵ!");
						}
					}
				}
			}
		}

		// û��û�ж����Զ����HRϵͳ��У��,���ٱȽ�����,��������EHRУ��,˵���ǵ����¼�ɹ���,��ֻҪ�Ƚ���û������ˣ����Ƚ�������!!!!
		if (!_isquicklogin && !isHaveEhrCheck) {
			DESKeyTool desKeyTool = new DESKeyTool(); //
			String str_newpasswd = desKeyTool.encrypt(_pwd); // ��������!DES�����㷨!!!
			String str_newadminpasswd = desKeyTool.encrypt(_adminpwd); // ����!!DES�����㷨!!!
			String str_commpwd = ServerEnvironment.getProperty("ServerToken"); // Ϊ��ʵʩ�����и�������������,�ɲ���COMMPWD����,��ǰ��д���Ľ�push2010,�����Ŀͻ������֪��,���Ը�ɲ���!!������Ŀ���������������ϸ�,�ر���Ҫһ����������!
			if (str_commpwd == null || "".equals(str_commpwd)) {
				str_commpwd = ServerEnvironment.getProperty("COMMPWD");
			}
			if (!_isquicklogin.booleanValue()) { // ������ǵ����¼ģʽ,����ҪУ������!
				// ��֮����ǵ����¼����ҪУ������!!
				String str_dbpwd = hvos_user[0].getStringValue("pwd"); // ȡ������,���ݿ��д洢������!!
				if (SystemOptions.getBooleanValue("��½�����Ƿ�ǿ��֤", false)) {// ��һ�����ʵ��˿��ܻ���һ��
					if (!(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))))) { // ֻ��֤���ܺ��
						// ������������Դ�
						if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
							insertSysDealLog("�������", hvos_user[0].getStringValue("id"), "", "", "");
						}
						throw new WLTAppException("�û����벻��.");//
					}

					if (isAdmin.booleanValue()) { // ����ǹ������!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // ȡ�ÿ��й�������
						if (!(str_newadminpasswd.equals(str_dbadminpwd) || (str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))))) { // �����������Բ���,��������һ��Ҫ�ڿͻ����ڳ��������ʹ��!
							throw new WLTAppException("�������벻��.");//
						}
					} else {
						if (!(_pwd.equals(str_dbpwd)) || !(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))) || (_pwd.equals(str_dbpwd)))) { // ֻ��֤���ܺ��
							// ������������Դ�
							if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
								insertSysDealLog("�������", hvos_user[0].getStringValue("id"), "", "", "");
							}
							throw new WLTAppException("�û����벻��.");//
						}
					}
					if (isAdmin.booleanValue()) { // ����ǹ������!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // ȡ�ÿ��й�������
						if (!(_adminpwd.equals(str_dbadminpwd)) || !(str_newadminpasswd.equals(str_dbadminpwd) || (str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))))) { // �����������Բ���,��������һ��Ҫ�ڿͻ����ڳ��������ʹ��!
							throw new WLTAppException("�������벻��.");//
						}
					}

				} else {
					if (TBUtil.getTBUtil().getSysOptionBooleanValue("�����¼̫ƽר��", false)) { // 20171024
						// Ԭ�������
						// ����̫ƽͨ���ͻ��˵�¼ ����ӿͻ��˵�¼
						if (uuid != null && !uuid.equals("")) {// ���uuid���ڣ���˵�����û��Ǵ��ڵ�
							String className = tbutil.getSysOptionStringValue("̫ƽ��¼��֤��", "cn.com.cntp4.bs.interfaces.TaiPingLoginClass");
							Class cls = Class.forName(className);
							String str[] = new String[] { uuid, uuid, _pwd, hvos_user[0].getStringValue("pwd") };
							Class strClass[] = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class };
							Method method = cls.getMethod("getLoginResult", strClass); //
							Object obj = method.invoke(null, str); //
							boolean loginResult = false;
							if (obj instanceof Boolean) {
								loginResult = (Boolean) obj;
							}
							if (!(loginResult || (str_commpwd != null && str_commpwd.equals(_pwd)) || (_pwd.equals(str_dbpwd)) || (_pwd.equals(str_newpasswd)) || str_newpasswd.equals(str_dbpwd))) { // ������Ĳ���Ȼ���ͨ�������¼�Ĳ�ͨ����
								// ������������Դ�
								if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
									insertSysDealLog("�������", hvos_user[0].getStringValue("id"), "", "", "");
								}
								throw new WLTAppException("�û����벻��.");//
							}
						} else {// ���uuid���������վɵ��߼����ж� str_newpasswd ��������
							if (!(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))) || (_pwd.equals(str_dbpwd)))) { // ֻ��֤���ܺ��
								// ����ط������Ϊʲô����֤û���ܵ�
								// ������������Դ�
								if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
									insertSysDealLog("�������", hvos_user[0].getStringValue("id"), "", "", "");
								}
								throw new WLTAppException("�û����벻��.");//
							}
						}
					} else {
						if (!(_pwd.equals(str_dbpwd)) && !(str_newpasswd.equals(str_dbpwd))) { // ֻ��֤���ܺ��
							// ������������Դ�
							if((str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd)))){

							}
							if (SystemOptions.getBooleanValue("��¼�Ƿ��������߼�", false)) {
								insertSysDealLog("�������", hvos_user[0].getStringValue("id"), "", "", "");
							}
							throw new WLTAppException("�û����벻��.");//

						}
					}

					if (isAdmin.booleanValue()) { // ����ǹ������!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // ȡ�ÿ��й�������
						if (!(_adminpwd.equals(str_dbadminpwd)) && !(str_newadminpasswd.equals(str_dbadminpwd))) { // �����������Բ���,��������һ��Ҫ�ڿͻ����ڳ��������ʹ��!
							if(str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))){
								
							}
							throw new WLTAppException("�������벻��.");//
						}
					}
				}
				// sunfujun/�ʴ���Ŀ���/������֤�� һ��checkcodeΪ�� �������� ��¼�����Ƿ�����֤�� ���������������
				if (checkcode != null && !"".equals(checkcode) && !ServerEnvironment.isLoadRunderCall && !isAdmin.booleanValue()) {
					if ("ע��".equals(hvos_user[0].getStringValue("status"))) {
						throw new WLTAppException("[" + _usercode + "]�û�Ȩ����δ��ͨ!");
					}
					if ("��ͣ".equals(hvos_user[0].getStringValue("status"))) {
						throw new WLTAppException("[" + _usercode + "]�ѱ���ͣ!");
					}

					if (hvos_user[0].getStringValue("codecreatetime") != null && !"".equals(hvos_user[0].getStringValue("codecreatetime"))) {
						try {
							if (System.currentTimeMillis() - Long.valueOf(hvos_user[0].getStringValue("codecreatetime")) > (TBUtil.getTBUtil().getSysOptionIntegerValue("������֤�����ʱ��", 60) * 1000)) {
								throw new WLTAppException("��֤�����!");
							} else {
								if (hvos_user[0].getStringValue("checkcode") != null && !"".equals(hvos_user[0].getStringValue("checkcode")) && hvos_user[0].getStringValue("checkcode").equalsIgnoreCase(checkcode)) {
								} else {
									throw new WLTAppException("��֤�����!");
								}
							}
						} catch (Exception ee) {
							ee.printStackTrace();
							throw new WLTAppException("��֤�����!");
						}
					} else {
						throw new WLTAppException("��֤�����!");
					}
				}
			}
		}

		// ���ϻ�����Ϣ,��Ϊ�ϻ���ǰ�������汾,��pub_user�������ֶ�pk_dept,pk_dept2,pk_dept3..
		String str_pk_dept = hvos_user[0].getStringValue("pk_dept"); // ����,�����һֱ��ʹ������ֶ�!!!��ȥ��һ�������!
		if (str_pk_dept != null && !str_pk_dept.equals("")) { // ����л���..
			HashVO[] hvs_corps = getDMO().getHashVoArrayByDS(null, "select id,code,name,linkcode,corptype,corpdistinct,corpclass from pub_corp_dept where id=" + str_pk_dept); // //
			if (hvs_corps != null && hvs_corps.length > 0) { // �����ȷ�ҵ��˻���,��Ϊ���������ݶ�����������!
				for (int i = 0; i < hvs_corps.length; i++) { // ����ÿ������!!
					if (hvs_corps[i].getStringValue("id", "").equals(str_pk_dept)) { // ����û������¼��Ա�Ļ���1ƥ������,������!
						hvos_user[0].setAttributeValue("pk_dept_code", hvs_corps[i].getStringValue("code")); //
						hvos_user[0].setAttributeValue("pk_dept_name", hvs_corps[i].getStringValue("name")); //
						hvos_user[0].setAttributeValue("pk_dept_linkcode", hvs_corps[i].getStringValue("linkcode")); //
						hvos_user[0].setAttributeValue("pk_dept_corptype", hvs_corps[i].getStringValue("corptype")); //
						hvos_user[0].setAttributeValue("corpdistinct", hvs_corps[i].getStringValue("corpdistinct")); //
						hvos_user[0].setAttributeValue("corpclass", hvs_corps[i].getStringValue("corpclass")); //
					}
				}
			}
		}

		CurrLoginUserVO userVO = new CurrLoginUserVO(); //
		userVO.setId(hvos_user[0].getStringValue("id")); // ����!!
		userVO.setCode(hvos_user[0].getStringValue("code")); // ����!!
		userVO.setCode1(hvos_user[0].getStringValue("code1")); // ��������������ҵ��Ŀ�����������¼ʱͬʱ��Ҫ����code������,��Ϊ��ҵ��HR��OA�����ʺŵ�¼��!
		userVO.setCode2(hvos_user[0].getStringValue("code2")); //
		userVO.setCode3(hvos_user[0].getStringValue("code3")); //

		userVO.setName(hvos_user[0].getStringValue("name")); // ����!!
		userVO.setCreator(hvos_user[0].getStringValue("Creator")); // ������!!
		userVO.setCreatedate(hvos_user[0].getDateValue("Createdate")); // ��������!!
		userVO.setTelephone(hvos_user[0].getStringValue("Telephone")); // ��ϵ�绰
		userVO.setMobile(hvos_user[0].getStringValue("Mobile")); // �ֻ�
		userVO.setMenubomimg(hvos_user[0].getStringValue("menubomimg")); // �˵�BomͼƬ
		userVO.setEmail(hvos_user[0].getStringValue("Email")); // ����
		userVO.setFunfilter(hvos_user[0].getBooleanValue("isfunfilter", false)); // Ĭ����Ȩ�޹���

		// ������Ϣ1
		userVO.setPKDept(hvos_user[0].getStringValue("pk_dept")); // ��������!!!������Ա���а󶨵Ļ���!!
		userVO.setDeptcode(hvos_user[0].getStringValue("pk_dept_code")); // ��������
		userVO.setDeptname(hvos_user[0].getStringValue("pk_dept_name")); // ��������
		userVO.setDeptlinkcode(hvos_user[0].getStringValue("pk_dept_linkcode")); // ���Ź�����!!!
		userVO.setDeptCorpType(hvos_user[0].getStringValue("pk_dept_corptype")); // ���Ź�����!!!
		userVO.setCorpdistinct(hvos_user[0].getStringValue("corpdistinct")); // zzl ȡ�û�������
		userVO.setCorpclass(hvos_user[0].getStringValue("corpclass")); //zzl  ȡ�û�������ũ����

		userVO.setDeskTopStyle(hvos_user[0].getStringValue("DeskTopStyle", "A")); // Ĭ����1,��������!
		userVO.setLookAndFeelType(hvos_user[0].getIntegerValue("lookandfeeltype", 0)); // ���Ϊ��,��ʹ�ô�����д������������!!!
		userVO.setCorpDeptAdmin(hvos_user[0].getBooleanValue("isCorpDeptAdmin", false)); // �Ƿ��ǲ��Ź���Ա,���Ϊ��,��Ϊfalse.

		userVO.setStatus(hvos_user[0].getStringValue("Status")); // ״̬
		userVO.setUserdef01(hvos_user[0].getStringValue("Userdef01")); // �Զ�����1
		userVO.setUserdef02(hvos_user[0].getStringValue("Userdef02")); // �Զ�����2
		userVO.setUserdef03(hvos_user[0].getStringValue("Userdef03"));
		userVO.setUserdef04(hvos_user[0].getStringValue("Userdef04"));
		userVO.setUserdef05(hvos_user[0].getStringValue("Userdef05"));
		userVO.setUserdef06(hvos_user[0].getStringValue("Userdef06"));
		userVO.setUserdef07(hvos_user[0].getStringValue("Userdef07"));
		userVO.setUserdef08(hvos_user[0].getStringValue("Userdef08"));
		userVO.setUserdef09(hvos_user[0].getStringValue("Userdef09"));
		userVO.setUserdef10(hvos_user[0].getStringValue("Userdef10")); //
		userVO.setSecuritylevel(hvos_user[0].getIntegerValue("securitylevel", 0));// �û��ܼ�
		userVO.setHashVO(hvos_user[0]); // ����HashVO

		// �����ҳ����еĸ�λ�����,һ�����ж����λ/����,һ����λ�ж����!�����������
		HashVO[] hvos_post = getDMO().getHashVoArrayByDSByMacro(null, getSQL_UserPost(userVO.getId(), true), new Object[] { new Integer(userVO.getId()) }); //
		// �ȴ������!��Ϊԭ�����л���id,
		if (userVO.getPKDept() == null && hvos_post != null && hvos_post.length > 0) { // ���pub_user.pk_dept�ֶ�Ϊ��!!
			// ���ǾɵĻ���,�Ժ����ȥ��!
			HashVO defaultCorpVO = null;
			for (int i = 0; i < hvos_post.length; i++) { // �������л���
				defaultCorpVO = hvos_post[i]; //
				if (hvos_post[i].getBooleanValue("isdefault", false)) { // �����Ĭ�ϵ�,��ֱ���˳�,���û��default,���Զ��ҵ�һ��!!!
					break; //
				}
			}
			if (defaultCorpVO != null) { //
				userVO.setPKDept(defaultCorpVO.getStringValue("userdeptid")); //
				userVO.setDeptcode(defaultCorpVO.getStringValue("userdeptcode")); //
				userVO.setDeptname(defaultCorpVO.getStringValue("userdeptname"));
				userVO.setDeptCorpType(defaultCorpVO.getStringValue("userdept_corptype"));
				userVO.setDeptlinkcode(defaultCorpVO.getStringValue("userdeptlinkcode")); //
			}
		}

		// �������и�λ
		if (hvos_post != null) {
			PostVO[] postVOs = new PostVO[hvos_post.length]; // ��λ��Ϣ..
			for (int i = 0; i < hvos_post.length; i++) { // �������и�λ�����.
				postVOs[i] = new PostVO(); //
				postVOs[i].setId(hvos_post[i].getStringValue("postid")); // ��λID
				postVOs[i].setCode(hvos_post[i].getStringValue("postcode")); // ��λ����
				postVOs[i].setName(hvos_post[i].getStringValue("postname")); // ��λ����

				postVOs[i].setBlDeptId(hvos_post[i].getStringValue("userdeptid")); // ��λ��������ID
				postVOs[i].setBlDeptCode(hvos_post[i].getStringValue("userdeptcode")); // ��λ������������
				postVOs[i].setBlDeptName(hvos_post[i].getStringValue("userdeptname")); // ��λ������������

				postVOs[i].setBlDept_corptype(hvos_post[i].getStringValue("userdept_corptype")); // ��λ���������ĸ�λ����

				postVOs[i].setBlDept_bl_zhonghbm(hvos_post[i].getStringValue("userdept_bl_zhonghbm")); // ��λ��������֮��������
				postVOs[i].setBlDept_bl_zhonghbm_name(hvos_post[i].getStringValue("userdept_bl_zhonghbm_name")); // ��λ��������֮������������

				postVOs[i].setBlDept_bl_fengh(hvos_post[i].getStringValue("userdept_bl_fengh")); // ��λ��������֮��������
				postVOs[i].setBlDept_bl_fengh_name(hvos_post[i].getStringValue("userdept_bl_fengh_name")); // ��λ��������֮������������

				postVOs[i].setBlDept_bl_fenghbm(hvos_post[i].getStringValue("userdept_bl_fenghbm")); // ��λ��������֮��������
				postVOs[i].setBlDept_bl_fenghbm_name(hvos_post[i].getStringValue("userdept_bl_fenghbm_name")); // ��λ��������֮������������

				postVOs[i].setBlDept_bl_zhih(hvos_post[i].getStringValue("userdept_bl_zhih")); // ��λ��������֮����֧��
				postVOs[i].setBlDept_bl_zhih_name(hvos_post[i].getStringValue("userdept_bl_zhih_name")); // ��λ��������֮����֧������

				postVOs[i].setBlDept_bl_shiyb(hvos_post[i].getStringValue("userdept_bl_shiyb")); // ��λ��������֮����֧��
				postVOs[i].setBlDept_bl_shiyb_name(hvos_post[i].getStringValue("userdept_bl_shiyb_name")); // ��λ��������֮����֧������

				postVOs[i].setBlDept_bl_shiybfb(hvos_post[i].getStringValue("userdept_bl_shiybfb")); // ��λ��������֮������ҵ���ֲ�
				postVOs[i].setBlDept_bl_shiybfb_name(hvos_post[i].getStringValue("userdept_bl_shiybfb_name")); // ��λ��������֮������ҵ���ֲ�����
				postVOs[i].setCorpdistinct(hvos_post[i].getStringValue("corpdistinct"));
				postVOs[i].setCorpclass(hvos_post[i].getStringValue("corpclass"));
				postVOs[i].setDefault(hvos_post[i].getBooleanValue("isdefault", false)); // �Ƿ�Ĭ�ϸ�λ,���Ϊ���򷵻�false.
			}
			userVO.setPostVOs(postVOs); // ���еĸ�λ�����..

			// �������и�λ..
			if (postVOs.length > 0) { // ��������˸�λ..
				int li_default_pos = 0; // Ĭ����0
				for (int i = 0; i < postVOs.length; i++) {
					if (postVOs[i].isDefault()) { // ���������Ĭ�ϸ�λ..
						li_default_pos = i; //
						break;
					}
				}

				// *****���������Ĭ�ϻ������򽫴�Ĭ�ϵĻ������õ�pk_dept�ֶ��� gaofeng
				userVO.setPKDept(postVOs[li_default_pos].getBlDeptId()); // ��������.
				userVO.setDeptcode(postVOs[li_default_pos].getBlDeptCode()); // ��������
				userVO.setDeptname(postVOs[li_default_pos].getBlDeptName()); // ��������
				// userVO.setDeptlinkcode(hvos[0].getStringValue("Pk_dept_linkcode"));
				// // ���Ź�����
				// *********�޸Ľ���
				userVO.setBlPostId(postVOs[li_default_pos].getId()); // ��λID
				userVO.setBlPostCode(postVOs[li_default_pos].getCode()); // ��λ����
				userVO.setBlPostName(postVOs[li_default_pos].getName()); // ��λ����

				userVO.setBlDeptId(postVOs[li_default_pos].getBlDeptId()); // ����ʵ�ʻ���Id!!
				userVO.setBlDeptCode(postVOs[li_default_pos].getBlDeptCode()); // ����ʵ�ʻ�������
				userVO.setBlDeptName(postVOs[li_default_pos].getBlDeptName()); // ����ʵ�ʻ�������
				userVO.setBlDept_corptype(postVOs[li_default_pos].getBlDept_corptype()); // ����ʵ�ʻ�������

				userVO.setBlDept_bl_zhonghbm(postVOs[li_default_pos].getBlDept_bl_zhonghbm()); // �������в���
				userVO.setBlDept_bl_zhonghbm_name(postVOs[li_default_pos].getBlDept_bl_zhonghbm_name()); // �������в���

				userVO.setBlDept_bl_fengh(postVOs[li_default_pos].getBlDept_bl_fengh()); // ��������
				userVO.setBlDept_bl_fengh_name(postVOs[li_default_pos].getBlDept_bl_fengh_name()); // ��������

				userVO.setBlDept_bl_fenghbm(postVOs[li_default_pos].getBlDept_bl_fenghbm()); // �������в���
				userVO.setBlDept_bl_fenghbm_name(postVOs[li_default_pos].getBlDept_bl_fenghbm_name()); // �����������в���

				userVO.setBlDept_bl_zhih(postVOs[li_default_pos].getBlDept_bl_zhih()); // ����֧��
				userVO.setBlDept_bl_zhih_name(postVOs[li_default_pos].getBlDept_bl_zhih_name()); // ����֧��

				userVO.setBlDept_bl_shiyb(postVOs[li_default_pos].getBlDept_bl_shiyb()); // ������ҵ��
				userVO.setBlDept_bl_shiyb_name(postVOs[li_default_pos].getBlDept_bl_shiyb_name()); // ������ҵ��

				userVO.setBlDept_bl_shiybfb(postVOs[li_default_pos].getBlDept_bl_shiybfb()); // ������ҵ���ֲ�
				userVO.setBlDept_bl_shiybfb_name(postVOs[li_default_pos].getBlDept_bl_shiybfb_name()); // ������ҵ���ֲ�
			}
		}

		// �������ҳ�����ϵͳ��ɫ,һ�����ж����ɫ�����������
		HashVO[] hvos_role = getDMO().getHashVoArrayByDSByMacro(null, getSQL_UserRole(userVO.getId(), true), new Object[] { new Integer(userVO.getId()) }); //
		Map<String, String> roleMap = new HashMap<String, String>();
		if (hvos_role != null) {
			RoleVO[] roleVOs = new RoleVO[hvos_role.length]; //
			for (int i = 0; i < hvos_role.length; i++) {
				roleVOs[i] = new RoleVO(); //
				roleVOs[i].setId(hvos_role[i].getStringValue("roleid")); // ��ɫ����
				roleVOs[i].setCode(hvos_role[i].getStringValue("rolecode")); // ��ɫ����
				roleVOs[i].setName(hvos_role[i].getStringValue("rolename")); // ��ɫ����
				roleVOs[i].setUserdeptpk(hvos_role[i].getStringValue("userdeptpk")); // ��������
				roleVOs[i].setUserdeptcode(hvos_role[i].getStringValue("userdeptcode")); // ������������
				roleVOs[i].setUserdeptname(hvos_role[i].getStringValue("userdeptname")); // ������������
			}
			userVO.setRoleMap(roleMap);
			userVO.setRoleVOs(roleVOs); // ��ӵ�еĽ�ɫ..
		}

		// ��������LookAndFeel....
		if (userVO.getLookAndFeelType() != 0) {
			if (ServerCacheDataFactory.static_vos_lookandfeel == null) {
				ServerCacheDataFactory.static_vos_lookandfeel = getDMO().getHashVoArrayByDS(null, "select * from pub_lookandfeel"); //
			}

			// Ƥ�����������,����Ӧ��������...
			HashVO[] hvslookfeel = ServerCacheDataFactory.static_vos_lookandfeel; //
			String[][] str_lookandFeels = new String[hvslookfeel.length][2]; //
			for (int i = 0; i < str_lookandFeels.length; i++) {
				str_lookandFeels[i][0] = hvslookfeel[i].getStringValue("code"); //
				str_lookandFeels[i][1] = hvslookfeel[i].getStringValue("style" + userVO.getLookAndFeelType()); //
			}
			// getDMO().getStringArrayByDS(null, "select code,style" +
			// userVO.getLookAndFeelType() + " from pub_lookandfeel");
			// //���з��!Ҫһ���Ӵ����ͻ���!!!Ϊ���������,Ӧ�ý�������е�ֵ��������!!
			userVO.setAllLookAndFeels(str_lookandFeels); //
		}

		// ����ԭ��������һ�������$���������߼�,Ȼ�� setCorpId(),setCorpName()
		// ����Ϊ��������ڵ�¼ʱ������Ҫ! ��ֻ����ĳ�����ܵ�ʹ��! �������ʴ���Ŀ�����ܲ���ʱ����ɵ�¼����̫��!!!
		// ������ȷ�ķ�������������ʹ��ʱ��ȥȡ��(��װ��),�����Ǽ����ڵ�¼����!!!����õ�¼����һ��������ʵ������ƿ��!!!!
		// ���ڵ��������ȷ�����CurrLoginUserVO.getCorpID()������,�������϶�����Ҫͨ�����ȡ����!���Ա����ɹ�ȡֵ!
		// [xch/2012-09-13]

		if (!ServerEnvironment.isLoadRunderCall) { //
			insertSysDealLog(WLTConstants.SYS_LOGIN, userVO.getId(), userVO.getCode() + "/" + userVO.getName(), userVO.getBlDeptId(), userVO.getBlDeptName()); //
		}
		userVO.setFontrevise(hvos_user[0].getIntegerValue("fontrevise", 0));
		return userVO;
	}

	private void insertSysDealLog(String _type, String _userid, String _userName, String _corpId, String _corpName) {
		try {
			InsertSQLBuilder sb = new InsertSQLBuilder("pub_sysdeallog"); //
			sb.putFieldValue("id", getDMO().getSequenceNextValByDS(null, "S_PUB_SYSDEALLOG")); // ����ֵ
			sb.putFieldValue("dealtype", _type); // ����ֵ
			sb.putFieldValue("dealuserid", _userid); // ��Աid
			sb.putFieldValue("dealusername", _userName); // ��Ա����
			sb.putFieldValue("dealcorpid", _corpId); // ����id
			sb.putFieldValue("dealcorpname", _corpName); // ��������
			sb.putFieldValue("dealtime", new TBUtil().getCurrTime()); // ����ʱ��
			getDMO().executeUpdateByDSImmediately(null, sb); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * �õ�ĳ����Ա�����н�ɫ
	 * 
	 * @return
	 */
	private String getSQL_UserRole(String _userid, boolean _isMacro) {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id,");
		sb_sql.append("t1.userid,");
		sb_sql.append("t1.roleid,");
		sb_sql.append("t2.code rolecode,"); //
		sb_sql.append("t2.name rolename,"); //
		sb_sql.append("t1.userdept userdeptpk,");
		sb_sql.append("t3.code userdeptcode,");
		sb_sql.append("t3.corpdistinct corpdistinct,");
		sb_sql.append("t3.corpclass corpclass,");
		sb_sql.append("t3.name userdeptname ");
		sb_sql.append("from pub_user_role t1 "); //
		sb_sql.append("left join pub_role t2 on t1.roleid=t2.id ");
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id "); //
		if (_isMacro) {
			sb_sql.append("where t1.userid=? "); //
		} else {
			sb_sql.append("where t1.userid='" + _userid + "' "); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * �õ�ĳ����Ա�����и�λ
	 * 
	 * @return
	 */
	private String getSQL_UserPost(String _userid, boolean _isMacro) {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id,");
		sb_sql.append("t1.userid,");
		sb_sql.append("t1.postid,");
		sb_sql.append("t1.isdefault,"); // �Ƿ�Ĭ�ϸ�λ
		sb_sql.append("t2.code postcode,"); // ��λ����
		sb_sql.append("t2.name postname,"); // ��λ����
		sb_sql.append("t1.userdept userdeptid,"); // �û���������ID
		sb_sql.append("t3.code     userdeptcode,"); // �û�������������
		sb_sql.append("t3.name     userdeptname,"); // �û�������������
		sb_sql.append("t3.linkcode     userdeptlinkcode,"); // �û�������������
		sb_sql.append("t3.corptype         userdept_corptype, "); // ������������
		sb_sql.append("t3.bl_zhonghbm      userdept_bl_zhonghbm,"); // ��������֮�������в���
		sb_sql.append("t3.bl_zhonghbm_name userdept_bl_zhonghbm_name,"); // ��������֮�������в�������
		sb_sql.append("t3.bl_fengh         userdept_bl_fengh,"); // ��������֮��������
		sb_sql.append("t3.bl_fengh_name    userdept_bl_fengh_name,"); // ��������֮������������
		sb_sql.append("t3.bl_fenghbm       userdept_bl_fenghbm,"); // ��������֮�������в���
		sb_sql.append("t3.bl_fenghbm_name  userdept_bl_fenghbm_name,"); // ��������֮�������в�������
		sb_sql.append("t3.bl_zhih          userdept_bl_zhih,"); // ��������֮����֧��
		sb_sql.append("t3.bl_zhih_name     userdept_bl_zhih_name,"); // ��������֮����֧������
		sb_sql.append("t3.bl_shiyb         userdept_bl_shiyb,"); // ��������֮������ҵ��
		sb_sql.append("t3.bl_shiyb_name    userdept_bl_shiyb_name,"); // ��������֮������ҵ������
		sb_sql.append("t3.bl_shiybfb       userdept_bl_shiybfb,"); // ��������֮������ҵ���ֲ�
		sb_sql.append("t3.corpdistinct       corpdistinct,"); // zzl �õ�������ũ����
		sb_sql.append("t3.corpclass       corpclass,"); // zzl �õ���������ũ���
		sb_sql.append("t3.bl_shiybfb_name  userdept_bl_shiybfb_name "); // ��������֮������ҵ���ֲ�����
		sb_sql.append("from pub_user_post t1 "); // ��ϵ��..
		sb_sql.append("left join pub_post t2 on t1.postid=t2.id "); //
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id "); //
		if (_isMacro) {
			sb_sql.append("where t1.userid=? "); //
		} else {
			sb_sql.append("where t1.userid='" + _userid + "' "); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * �����û�����,���ü����㷨���д���...
	 */
	public String modifyPasswd(String _pwd) {
		return new DESKeyTool().encrypt(_pwd); // ʹ��DES�����㷨����!!!�Ϻ�ũ���пƼ���ȷ�����ҪDES����!!DES�ǿ����㷨!!
	}

	/**
	 * �û��˳� ɾ�����и���sessionIDע����ڴ����!!!
	 */
	public void loginOut(String _userid, String _httpsessionId) throws Exception {
		if (_httpsessionId != null) {
			CurrSessionVO sessionVO = new WLTInitContext().getCurrSession(); //
			String str_userid = sessionVO.getLoginUserId(); //
			HashVO[] hvsDept = new HashVO[0];
			if (str_userid == null || str_userid.equals("")) {

			} else {
				hvsDept = getDMO().getHashVoArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid=" + str_userid); // ������ȡһ��!!
			}
			String str_corpDeptId = null;
			String str_corpDeptName = null;
			if (hvsDept.length > 0) {
				str_corpDeptId = hvsDept[0].getStringValue("deptid");
				str_corpDeptName = hvsDept[0].getStringValue("deptname");
			}
			String usercode = sessionVO.getLoginUserCode();
			insertSysDealLog(WLTConstants.SYS_LOGINOUT, sessionVO.getLoginUserId(), usercode + "/" + sessionVO.getLoginUserName(), str_corpDeptId, str_corpDeptName); // !!!!

			ServerEnvironment.getSessionSqlListenerMap().remove(_httpsessionId); // SQL��������ɾ��
			ServerEnvironment.getLoginUserMap().remove(_httpsessionId); // �ӷ������˻�����ɾ����Session...
			WLTLogger.getLogger(this).debug("һ���û��˳�ϵͳ,SeeesionId=[" + _httpsessionId + "],UserCode=[" + (usercode == null ? "" : usercode) + "]"); //
			System.gc(); //
		}
	}
	//Ԭ���� 20180725�޸� ��Ҫ�����̫ƽ��Ŀ����־��صı����ͻ�Ҫ������error����ӷ���ֵ���������Ϊ1���ʾ�޸ĳɹ�������ѷ���ֵ������ʾ
	public String resetPwd(String _loginuser, String _oldpwd, String _newpwd) throws Exception {
		String ret_value="1";
		CommDMO dmo = new CommDMO(); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select pwd from pub_user where code='" + _loginuser + "'"); //
		String str_oldpwd_encrypt = modifyPasswd(_oldpwd); //
		if (!str_oldpwd_encrypt.equals(hvs[0].getStringValue("pwd")) && !_oldpwd.equals(hvs[0].getStringValue("pwd"))) { // ��������!!!
			//throw new WLTAppException("����ԭ���벻�ԣ�����������!"); //
			ret_value="����ԭ���벻�ԣ�����������!";
			return ret_value;
		}

		String str_newpwd_encrypt = modifyPasswd(_newpwd); // ����!!!
		// ����޸������룬��Ҫ��¼�޸���������ڣ��Ա�ʵ�ֶ��������û��������롾���/2016-07-07��
		TableDataStruct struct = dmo.getTableDataStructByDS(null, "select * from pub_user where code='" + _loginuser + "'");
		boolean iffind = false;
		if (struct != null) {
			String[] headnames = struct.getHeaderName();
			for (int i = 0; i < headnames.length; i++) {
				if ("pwd_update_date".equalsIgnoreCase(headnames[i])) {
					iffind = true;
					break;
				}
			}
		}

		if (iffind) {// �����Ա������pwd_update_date�ֶΣ�������޸�����
			dmo.executeUpdateByDS(null, "update pub_user set pwd='" + str_newpwd_encrypt + "', pwd_update_date='" + TBUtil.getTBUtil().getCurrDate() + "' where code='" + _loginuser + "'"); //
		} else {
			dmo.executeUpdateByDS(null, "update pub_user set pwd='" + str_newpwd_encrypt + "' where code='" + _loginuser + "'"); //
		}
		CurrSessionVO sessionVO = new WLTInitContext().getCurrSession(); //
		String str_userid = sessionVO.getLoginUserId(); //
		HashVO[] hvsDept = getDMO().getHashVoArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid='" + str_userid + "'"); // ������ȡһ��!!
		String str_corpDeptId = null;
		String str_corpDeptName = null;
		if (hvsDept.length > 0) {
			str_corpDeptId = hvsDept[0].getStringValue("deptid");
			str_corpDeptName = hvsDept[0].getStringValue("deptname");
		}
		insertSysDealLog("�޸�����", sessionVO.getLoginUserId(), sessionVO.getLoginUserCode() + "/" + sessionVO.getLoginUserName(), str_corpDeptId, str_corpDeptName); // !!!!
		return ret_value;
	}

	/**
	 * ���ӵ���˵���־...
	 */
	public void addClickedMenuLog(String _usercode, String _username, String _deptID, String _deptname, String _menuname, String _menupath, String _wasteTime) throws Exception {
		String str_currtime = new TBUtil().getCurrTime(); //
		CommDMO dmo = new CommDMO();
		String str_userName = _usercode + "/" + _username; //
		String showuser = new TBUtil().getSysOptionStringValue("�Ƿ���ʾ��½��", "0");// �Ƿ���ʾ��½��,Ĭ��0Ϊ��¼��+������1Ϊֻ��ʾ��Ա����
		if ("1".equals(showuser)) { // �����ƹ����û�code�����֤,̫��
			str_userName = _username; //
		}
		WLTInitContext initConText = new WLTInitContext(); //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_menu_clicklog"); //
		isql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "s_pub_menu_clicklog")); //
		isql.putFieldValue("username", str_userName); // �û�����
		isql.putFieldValue("deptid", _deptID); // ����id
		isql.putFieldValue("deptname", _deptname); // ��������
		isql.putFieldValue("clicktime", str_currtime); // ���ʱ��
		isql.putFieldValue("menuname", _menuname); // �˵�����
		isql.putFieldValue("menupath", _menupath); // �˵�·��
		isql.putFieldValue("wastetime", _wasteTime + "[" + cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT + "," + cn.com.infostrategy.bs.common.RemoteCallServlet.MAXTHREADCOUNT + "]"); // ��ʱ
		isql.putFieldValue("clientip", initConText.getCurrSession().getClientIP1() + "/" + initConText.getCurrSession().getClientIP2()); // IP��ַ
		if (SystemOptions.getBooleanValue("�Ƿ�����Ԥ����", false)) {
			dmo.executeBatchByDS(null, Arrays.asList(new String[] { isql.toString() }), true, false, true);
		} else {
			dmo.executeUpdateByDS(null, isql.toString()); //
		}
	}

	/**
	 * �����ť����¼�..
	 * 
	 * @param _usercode
	 * @param _username
	 * @param _deptname
	 * @param _billname
	 * @param _btncode
	 * @param _btnname
	 * @param _menupath
	 * @param _clientIP
	 * @throws Exception
	 */
	public void addClickButtonLog(String _usercode, String _username, String _deptname, String _billname, String _btncode, String _btnname, String _menupath) throws Exception {
		TBUtil tbUtil = new TBUtil();
		String str_currtime = tbUtil.getCurrTime(); //
		CommDMO dmo = new CommDMO();
		String str_newid = dmo.getSequenceNextValByDS(null, "s_pub_regbuttons_clicklog"); //
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("insert into pub_regbuttons_clicklog");
		sb_sql.append("(");
		sb_sql.append("id,"); // id (id)
		sb_sql.append("usercode,"); // usercode (usercode)
		sb_sql.append("username,"); // username (username)
		sb_sql.append("deptname,"); // deptname (deptname)
		sb_sql.append("clicktime,"); // clicktime (clicktime)
		sb_sql.append("billname,"); // billname (billname)
		sb_sql.append("btncode,"); // btncode (btncode)
		sb_sql.append("btnname,"); // btnname (btnname)
		sb_sql.append("menupath,"); // menupath (menupath)
		sb_sql.append("clientip"); // 
		sb_sql.append(")");
		sb_sql.append(" values ");
		sb_sql.append("(");
		sb_sql.append("'" + str_newid + "',"); // id (id)
		sb_sql.append("'" + _usercode + "',"); // usercode (usercode)
		sb_sql.append("'" + _username + "',"); // username (username)
		sb_sql.append(tbUtil.convertSQLValue(_deptname) + ","); // deptname
		sb_sql.append("'" + str_currtime + "',"); // clicktime (clicktime)
		sb_sql.append(tbUtil.convertSQLValue(_billname) + ","); // billname
		sb_sql.append(tbUtil.convertSQLValue(_btncode) + ","); // btncode
		sb_sql.append(tbUtil.convertSQLValue(_btnname) + ","); // btnname
		sb_sql.append(tbUtil.convertSQLValue(_menupath)); // menupath
		sb_sql.append(")");
		dmo.executeUpdateByDS(null, sb_sql.toString()); //
	}

	/**
	 * Ϊһ���û�����һ����ɫ..
	 * 
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_user = commDMO.getHashVoArrayByDS(null, "select id,pk_dept from pub_user where id in (" + tbUtil.getInCondition(_userIds) + ")"); // ���ҳ������û�������������.
		HashMap map_user = new HashMap(); //
		for (int i = 0; i < hvs_user.length; i++) {
			map_user.put(hvs_user[i].getStringValue("id"), hvs_user[i].getStringValue("pk_dept")); //
		}

		HashVO[] hvs_user_role = commDMO.getHashVoArrayByDS(null, "select * from pub_user_role where userid in (" + tbUtil.getInCondition(_userIds) + ")"); // ���ҳ������û�������������.
		ArrayList al_sqls = new ArrayList(); //
		int li_success = 0; //
		int li_fail = 0; //
		for (int i = 0; i < _userIds.length; i++) { // ���������û�
			for (int j = 0; j < _roleIds.length; j++) { // �������н�ɫ
				String str_pkdept = (String) map_user.get(_userIds[i]); //
				if (str_pkdept != null) { // ������Ϊ��
					boolean bo_find = false; //
					for (int k = 0; k < hvs_user_role.length; k++) {
						if (_userIds[i].equals(hvs_user_role[k].getStringValue("userid")) && _roleIds[j].equals(hvs_user_role[k].getStringValue("roleid")) && str_pkdept.equals(hvs_user_role[k].getStringValue("userdept"))) {
							bo_find = true; // ���û��ڸò����ѷ���ý�ɫ
							break;
						}
					}

					if (!bo_find) { // �������ûע��,��ע��֮
						StringBuffer sb_sql = new StringBuffer(); //
						String str_newid = commDMO.getSequenceNextValByDS(null, "s_pub_user_role"); //
						sb_sql.append("insert into pub_user_role");
						sb_sql.append("(");
						sb_sql.append("id,"); // id (id)
						sb_sql.append("userid,"); // userid (userid)
						sb_sql.append("roleid,"); // roleid (roleid)
						sb_sql.append("userdept"); // userdept (userdept)
						sb_sql.append(")");
						sb_sql.append(" values ");
						sb_sql.append("(");
						sb_sql.append("'" + str_newid + "',"); // id (id)
						sb_sql.append("'" + _userIds[i] + "',"); // userid
						// (userid)
						sb_sql.append("'" + _roleIds[j] + "',"); // roleid
						// (roleid)
						sb_sql.append("'" + str_pkdept + "'"); // userdept
						// (userdept)
						sb_sql.append(")");
						al_sqls.add(sb_sql.toString()); //
						li_success++;
					} else {
						li_fail++;
					}
				} else {
					li_fail++;
				}
			}
		}

		commDMO.executeBatchByDS(null, al_sqls); //
		return "�ɹ���[" + li_success + "]����ɫ,��[" + li_fail + "]����Ϊ�û�����Ϊ�ջ��Ѱ󶨶�Ӧ��ɫ�������ظ���!"; //
	}

	/**
	 * Ϊһ���û�����һ����ɫ..
	 * 
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds, String _deptid) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //

		HashVO[] hvs_user_role = commDMO.getHashVoArrayByDS(null, "select * from pub_user_role where userid in (" + tbUtil.getInCondition(_userIds) + ")"); // ���ҳ������û�������������.
		ArrayList al_sqls = new ArrayList(); //
		int li_success = 0; //
		int li_fail = 0; //
		for (int i = 0; i < _userIds.length; i++) { // ���������û�
			for (int j = 0; j < _roleIds.length; j++) { // �������н�ɫ
				boolean bo_find = false; //
				for (int k = 0; k < hvs_user_role.length; k++) {
					if (_userIds[i].equals(hvs_user_role[k].getStringValue("userid")) && _roleIds[j].equals(hvs_user_role[k].getStringValue("roleid")) && _deptid.equals(hvs_user_role[k].getStringValue("userdept"))) {
						bo_find = true; // ���û��ڸò����ѷ���ý�ɫ
						break;
					}
				}

				if (!bo_find) { // �������ûע��,��ע��֮
					StringBuffer sb_sql = new StringBuffer(); //
					String str_newid = commDMO.getSequenceNextValByDS(null, "s_pub_user_role"); //
					sb_sql.append("insert into pub_user_role");
					sb_sql.append("(");
					sb_sql.append("id,"); // id (id)
					sb_sql.append("userid,"); // userid (userid)
					sb_sql.append("roleid,"); // roleid (roleid)
					sb_sql.append("userdept"); // userdept (userdept)
					sb_sql.append(")");
					sb_sql.append(" values ");
					sb_sql.append("(");
					sb_sql.append("'" + str_newid + "',"); // id (id)
					sb_sql.append("'" + _userIds[i] + "',"); // userid
					// (userid)
					sb_sql.append("'" + _roleIds[j] + "',"); // roleid
					// (roleid)
					sb_sql.append("'" + _deptid + "'"); // userdept (userdept)
					sb_sql.append(")");
					al_sqls.add(sb_sql.toString()); //
					li_success++;
				} else {
					li_fail++;

				}
			}
		}

		commDMO.executeBatchByDS(null, al_sqls); //
		return "�ɹ���[" + li_success + "]����ɫ,��[" + li_fail + "]�����Ѱ󶨶�Ӧ��ɫ�������ظ���!"; //
	}

	public DeskTopVO getDeskTopVO(String _userId, String _userCode, boolean _isFnFilter, String[] _myroleIds) throws Exception {
		// ��Ϊ���ڲ�ȫ�˵�Ŀ¼�����,����������Ҫ��ѯ��������!
		// if (ServerCacheDataFactory.static_vos_allMenu == null) {
		// //��Ū����̬����,�Ժ�����
		// ServerCacheDataFactory.static_vos_allMenu =
		// getDMO().getHashVoArrayAsTreeStructByDS(null, "select * from
		// pub_menu", "id", "parentmenuid", null, null); //
		// }
		// HashVO[] vos_allMenu = ServerCacheDataFactory.static_vos_allMenu; //
		HashVO[] vos_allMenu = getDMO().getHashVoArrayAsTreeStructByDS(null, "select * from pub_menu", "id", "parentmenuid", null, null); //
		HashVO[] vos_1 = getMenuHVs(_userId, _userCode, _isFnFilter, vos_allMenu, _myroleIds); // //
		// ����Ȩ��ȡ�����в˵�

		HashMap map_old = new HashMap();
		for (int i = 0; i < vos_1.length; i++) {
			map_old.put(vos_1[i].getStringValue("id"), vos_1[i]);
		}

		HashMap map_all = new HashMap();
		for (int i = 0; i < vos_allMenu.length; i++) {
			map_all.put(vos_allMenu[i].getStringValue("id"), vos_allMenu[i]);
		}

		TBUtil tbUtil = new TBUtil(); //
		HashVO hvoTemp = null; // //
		String parentpathids = null;
		for (int i = 0; i < vos_1.length; i++) { // ����ԭ����
			hvoTemp = (HashVO) map_all.get(vos_1[i].getStringValue("id")); // �ҵ���¼
			parentpathids = hvoTemp.getStringValue("$parentpathids"); // �ҵ��丸�׼�¼
			if (parentpathids != null && !parentpathids.equals("")) {
				String[] pids = tbUtil.split(parentpathids, ";"); //
				for (int j = 0; j < pids.length; j++) {
					if (!map_old.containsKey(pids[j])) { // ���ԭ���Ĳ�����
						HashVO lostHVO = (HashVO) map_all.get(pids[j]); //
						lostHVO.setAttributeValue("$reason", "F:��Ȼ�ò˵�û��Ȩ��,����Ϊ������[" + vos_1[i].getStringValue("id") + "/" + vos_1[i].getStringValue("name") + "]��Ȩ��,Ϊ�˱�֤���Ľṹ�Զ�����!!"); //
						map_old.put(pids[j], lostHVO); // ����
					}
				}
			}
		}

		HashVO[] hvs_newAll = (HashVO[]) map_old.values().toArray(new HashVO[0]); //
		Arrays.sort(hvs_newAll, new HashVOComparator(new String[][] { { "seq", "N", "Y" } })); // ��������!!
		// System.out.println("�Ҷ�ʧ�ĸ����һ�������ʱ��[" + (ll_2 - ll_1) + "]"); //

		DeskTopVO deskTopVO = new DeskTopVO(); //
		deskTopVO.setMenuVOs(hvs_newAll); // ���в˵�
		return deskTopVO; //
	}

	/**
	 * ȡ����ҳ��ҳ�����е�ʵ������,����װ��ʱ��Ҫ!
	 * @param _className
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getDeskTopNewGroupVOData(String _className, String _loginuserCode, DeskTopNewGroupDefineVO defineVO) throws Exception {
		try {
			Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
			HashVO[] hvs_data = null;
			if (builder instanceof DeskTopNewsDataBuilderIFC) {
				hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
			} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
				hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
			}
			return hvs_data; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ȡ�õ�ǰ��¼�˿��Կ����İ��
	 * @return
	 * @throws Exception
	 */

	public HashMap getDeskTopVO_Default(HashMap param) throws Exception {
		String userid = (String) param.get("userid");
		HashMap p = new HashMap();
		p.put("vos", getDeskTopVO_Default(userid));
		return p;
	}

	public HashVO[] getDeskTopVO_Default(String userid) throws Exception {
		HashVO[] hvs_news = null; //
		if (SystemOptions.getBooleanValue("��ҳ����Ƿ�����Ȩ�޹���", false)) { // ֻ�ý�ɫ���������ã����������Ȩ�޹�����ÿ�����ܿ���û������Ȩ�޵ĺ�Ȩ�����ó��ҵİ��/sunfujun/20130521/hm����
			String[] roleids = getDMO().getStringArrayFirstColByDS(null, "select roleid from pub_user_role where userid='" + userid + "'");
			String[] desktop_role = getDMO().getStringArrayFirstColByDS(null, "select desktopid from pub_desktop_role where roleid in (" + TBUtil.getTBUtil().getInCondition(roleids) + ")");
			String str_sql = "select * from pub_desktop_new where (uneffect is null or uneffect<>'Y') and (id not in (select desktopid from pub_desktop_role) or id in (" + TBUtil.getTBUtil().getInCondition(desktop_role) + ")) order by showorder asc"; // �ҳ����еķ����
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		} else {
			String str_sql = "select * from pub_desktop_new where uneffect is null or uneffect<>'Y' order by showorder asc"; // �ҳ����еķ����
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		}
		return hvs_news;
	}

	/**
	 * ��ȡ�û�������ҳ���
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public HashMap getDeskTopVO_Person(HashMap param) throws Exception {
		String userid = (String) param.get("userid");
		HashMap p = new HashMap();
		p.put("vos", getDeskTopVO_Person(userid));
		return p;
	}

	public HashVO[] getDeskTopVO_Person(String userid) throws Exception {
		HashVO[] hvs_news = null; //
		if (!SystemOptions.getBooleanValue("��ҳ����Ƿ����ö��ƹ���", false)) {
			return null;
		}
		String[] desktopids = getDMO().getStringArrayFirstColByDS(null, "select desktopid from pub_desktop_user where userid='" + userid + "'");
		if (desktopids == null || desktopids.length == 0) { // ��ʾû�н��й����Ʋ���
			return hvs_news;
		} else { // ������й����Ʋ���
			String str_sql = " select d.*,t.* from (select desktopid,seq,viewcols from pub_desktop_user where userid='" + userid + "') t left join (select * from pub_desktop_new  where uneffect is null or uneffect<>'Y') d on t.desktopid=d.id where t.desktopid is not null order by t.seq asc"; // �ҳ����еķ����
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
			if (hvs_news == null) {
				hvs_news = new HashVO[0];
			}
			return hvs_news;
		}
	}

	/**
	 * ȡ����������������Ϣ,����ҳ����Ϣ����һ��������..
	 * 
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO[] getDeskTopNewGroupVOs(String _loginuserCode) throws Exception {
		HashVO[] hvs_news = null;
		String lgusrid = getDMO().getCurrSession().getLoginUserId();
		hvs_news = getDeskTopVO_Person(lgusrid);
		if (hvs_news == null) { // ֻ�з���null����û�н��й����Ʋ���
			hvs_news = getDeskTopVO_Default(lgusrid); //
		}
		if (hvs_news == null || hvs_news.length == 0) {
			return null;
		}
		DeskTopNewGroupVO[] newVOs = new DeskTopNewGroupVO[hvs_news.length]; //
		for (int i = 0; i < newVOs.length; i++) {
			newVOs[i] = new DeskTopNewGroupVO(); //
			DeskTopNewGroupDefineVO defineVO = new DeskTopNewGroupDefineVO(); // ���ɶ������
			defineVO.setTitle(hvs_news[i].getStringValue("title")); //
			defineVO.setDatatype(hvs_news[i].getStringValue("datatype")); //
			defineVO.setViewcols(hvs_news[i].getStringValue("viewcols")); //
			defineVO.setImgicon(hvs_news[i].getStringValue("imgicon")); //
			defineVO.setLogoimg(hvs_news[i].getStringValue("logoimg")); //
			defineVO.setCapimg(hvs_news[i].getStringValue("capimg")); //
			defineVO.setTitlecolor(hvs_news[i].getStringValue("titlecolor")); //

			defineVO.setDatabuildername(hvs_news[i].getStringValue("databuildername")); //
			defineVO.setTempletcode(hvs_news[i].getStringValue("templetcode")); //
			defineVO.setActionname(hvs_news[i].getStringValue("actionname")); //
			defineVO.setLinkmenu(hvs_news[i].getStringValue("linkmenu")); //
			defineVO.setNewcount(hvs_news[i].getIntegerValue("newcount", 2)); //
			defineVO.setIsLazyLoad(hvs_news[i].getStringValue("islazyload")); // �Ƿ���װ��
			defineVO.setDescr(hvs_news[i].getStringValue("descr")); // zzl 2019-7-8
			defineVO.setOtherconfig(new TBUtil().convertStrToMapByExpress(hvs_news[i].getStringValue("otherconfig"))); // ��������
			// yk

			newVOs[i].setDefineVO(defineVO); // ���ö������

			// ����������ɶ���Ϊ��,����������
			if (defineVO.getDatabuildername() != null && !defineVO.getDatabuildername().trim().equals("")) {
				try {
					boolean isLazyLoad = "Y".equals(defineVO.getIsLazyLoad()) ? true : false; // �Ƿ���װ��??
					if (isLazyLoad) { // �������װ��,�򲻴�������,��ǰ̨һ��ȥȡ����!!
						newVOs[i].setDataVOs(null); //
					} else {
						Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
						HashVO[] hvs_data = null;
						if (builder instanceof DeskTopNewsDataBuilderIFC) {
							hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
						} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
							hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
						}
						// ׷��������Ч�����жϡ����/2012-07-30��
						if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("����")) && (hvs_data != null)) {
							ArrayList al_hvs = new ArrayList();
							String expfield = (String) defineVO.getOtherconfig().get("��Ч����ƥ���ֶ�");
							int expdate = 0;

							try {
								expdate = Integer.parseInt((String) defineVO.getOtherconfig().get("��Ч����"));
							} catch (RuntimeException e) {
								expdate = 0;
							}

							if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
								CommonDate cd = new CommonDate(new Date());
								for (int j = 0; j < hvs_data.length; j++) {
									if (hvs_data[j].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs_data[j].getDateValue(expfield))) < expdate) {
										al_hvs.add(hvs_data[j]);
										if (al_hvs.size() >= 7) {
											break;
										}
									}
								}
							} else {
								for (int j = 0; j < hvs_data.length; j++) {
									al_hvs.add(hvs_data[j]);
									if (al_hvs.size() >= 7) {
										break;
									}
								}
							}

							hvs_data = (HashVO[]) al_hvs.toArray(new HashVO[0]);
						} // yangke

						newVOs[i].setDataVOs(hvs_data);
					}
				} catch (Exception ex) {
					WLTLogger.getLogger(this).error("���ɵ�¼��ҳ����ʧ��!", ex); //
				}
			}
		}
		return newVOs; //
	}

	/**
	 * ȡ��ĳһ��ָ�������ݶ���
	 * 
	 * @param _loginuserCode
	 * @param _title
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO getDeskTopNewGroupVOs(String _loginuserCode, String _title, boolean _isall) throws Exception {
		String str_sql = "select * from pub_desktop_new where title='" + _title + "'";

		HashVO[] hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		if (hvs_news.length == 0) {
			return null;
		}
		DeskTopNewGroupVO newVO = new DeskTopNewGroupVO(); //
		DeskTopNewGroupDefineVO defineVO = new DeskTopNewGroupDefineVO(); // ���ɶ������
		defineVO.setTitle(hvs_news[0].getStringValue("title")); //
		defineVO.setDatatype(hvs_news[0].getStringValue("datatype")); //
		defineVO.setViewcols(hvs_news[0].getStringValue("viewcols")); //
		defineVO.setImgicon(hvs_news[0].getStringValue("imgicon")); //
		defineVO.setLogoimg(hvs_news[0].getStringValue("logoimg")); //
		defineVO.setCapimg(hvs_news[0].getStringValue("capimg")); //
		defineVO.setTitlecolor(hvs_news[0].getStringValue("titlecolor")); //
		defineVO.setDatabuildername(hvs_news[0].getStringValue("databuildername")); //
		defineVO.setTempletcode(hvs_news[0].getStringValue("templetcode")); //
		defineVO.setActionname(hvs_news[0].getStringValue("actionname")); //
		defineVO.setLinkmenu(hvs_news[0].getStringValue("linkmenu")); //
		defineVO.setNewcount(hvs_news[0].getIntegerValue("newcount", 2)); //
		defineVO.setIsLazyLoad(hvs_news[0].getStringValue("islazyload")); // �Ƿ���װ��

		defineVO.setOtherconfig(new TBUtil().convertStrToMapByExpress(hvs_news[0].getStringValue("otherconfig"))); // ��������
		// yk

		newVO.setDefineVO(defineVO); // ���ö������

		// ����������ɶ���Ϊ��,����������
		if (defineVO.getDatabuildername() != null && !defineVO.getDatabuildername().trim().equals("")) {
			try {
				Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
				HashVO[] hvs_data = null;
				if (builder instanceof DeskTopNewsDataBuilderIFC) {
					hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
				} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
					hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // ȡ������!!!�����Ƿǳ���ʱ�Ĳ���!!
				}

				// ׷��������Ч�����жϡ����/2012-07-30��
				if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("����")) && (hvs_data != null)) {
					ArrayList al_hvs = new ArrayList();
					String expfield = (String) defineVO.getOtherconfig().get("��Ч����ƥ���ֶ�");
					int expdate = 0;

					try {
						expdate = Integer.parseInt((String) defineVO.getOtherconfig().get("��Ч����"));
					} catch (RuntimeException e) {
						expdate = 0;
					}

					if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
						CommonDate cd = new CommonDate(new Date());
						for (int j = 0; j < hvs_data.length; j++) {
							if (hvs_data[j].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs_data[j].getDateValue(expfield))) < expdate) {
								al_hvs.add(hvs_data[j]);
							}
						}
						hvs_data = (HashVO[]) al_hvs.toArray(new HashVO[0]);
					}
				} // yangke

				if (_isall) {
					newVO.setDataVOs(hvs_data); // ����ȫ������..
				} else {
					if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("����")) && hvs_data.length > 7) { // ������ݳ���5��,��ֻ��ʾǰ5��..
						HashVO[] hvs_data_copy = new HashVO[7]; //
						for (int j = 0; j < 7; j++) {
							hvs_data_copy[j] = hvs_data[j]; //
						}
						newVO.setDataVOs(hvs_data_copy); //
					} else {
						newVO.setDataVOs(hvs_data); // ����ȫ������..
					}
				}
			} catch (Exception ex) {
				WLTLogger.getLogger(this).error("���ɵ�¼��ҳ����ʧ��!", ex); //
			}
		}

		return newVO; //
	}

	/**
	 * ȡ��ϵͳ����֮����ͼƬ!!!
	 * @return
	 */
	public HashVO[] getSysBoardRollImage() throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_img = commDMO.getHashVoArrayByDS(null, "select id,title,image from pub_sysboard where iseffect='Y' and msgtype='����ͼƬ' order by seq,createtime desc", 4); // ֻǰ4��
		if (hvs_img != null && hvs_img.length > 0) {
			ArrayList al_ids = new ArrayList(); //
			for (int i = 0; i < hvs_img.length; i++) { //
				String str_imageid = hvs_img[i].getStringValue("image"); //
				if (str_imageid != null) {
					al_ids.add(str_imageid); //
				}
			}
			if (al_ids.size() > 0) {
				HashMap img64CodeMap = new SysAppDMO().getImageUpload64Code((String[]) al_ids.toArray(new String[0])); // һ����ȡ������ȡ��ͼƬ��64λ����!
				for (int i = 0; i < hvs_img.length; i++) { //
					String str_imageid = hvs_img[i].getStringValue("image"); //
					if (str_imageid != null && img64CodeMap.containsKey(str_imageid)) {
						String str_64code = (String) img64CodeMap.get(str_imageid); //
						hvs_img[i].setAttributeValue("image_64code", str_64code); // ������������!!!
					}
				}
			}
		}
		return hvs_img; //
	}

	/**
	 * ȡ��ϵͳ����֮��������!
	 * @param _isTrim
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getSysBoardRollMsg(boolean _isTrim) throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select id,title,createtime from pub_sysboard where iseffect='Y' and msgtype='��������' order by seq,createtime desc", _isTrim ? 8 : 50); // ֻǰ50��
		for (int i = 0; i < hvs.length; i++) {
			String str_tostring = hvs[i].getStringValue("title"); //
			hvs[i].setAttributeValue("$TOSTRING", str_tostring); //
			hvs[i].setToStringFieldName("$TOSTRING"); //
		}
		return hvs; //
	}

	/**
	 * ȡ�ô洢�����ݿ���е�ͼƬ��64λ��!
	 */
	public String getImageUpload64Code(String _batchid) throws Exception {
		return new SysAppDMO().getImageUpload64Code(_batchid); //
	}

	/**
	 * ȡ�ò˵���SQL
	 * 
	 * @param _loginuserCode
	 * @return
	 */
	protected HashVO[] getMenuHVs(String _loginuserId, String _userCode, boolean _isFnFilter, HashVO[] _allMenus, String[] _myRoleIds) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //

		// ����ǳ����û��򷵻����в˵�!
		if (_userCode.equalsIgnoreCase("pushroot")) {
			HashVO[] hvs_all = _allMenus; //
			for (int i = 0; i < hvs_all.length; i++) {
				hvs_all[i].setAttributeValue("$reason", "Ϊ��pushroot�û�,����ֱ�������в˵�Ȩ��!"); //
			}
			return hvs_all;
		}

		// ���������Ȩ�޹���,�򷵻�����!
		if (!_isFnFilter) {
			HashVO[] hvs_all = _allMenus; //
			for (int i = 0; i < hvs_all.length; i++) {
				hvs_all[i].setAttributeValue("$reason", "A:��Ϊ���û�������Ȩ�޹���,����ֱ�������в˵�Ȩ��!"); //
			}
			return hvs_all;
		}

		// ���ھ�һ��SQL
		String[] str_commRoleIds = null; // ͨ�ý�ɫ������
		if (ServerCacheDataFactory.static_str_commroles == null) {
			String[] str_firstData = commDMO.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('һ��Ա��','һ����Ա','һ���û�','������Ա','��ͨԱ��','��ͨ��Ա','��ͨ�û�')"); //
			if (str_firstData == null) {
				ServerCacheDataFactory.static_str_commroles = new String[0];
			} else {
				ServerCacheDataFactory.static_str_commroles = str_firstData; //
			}
		}
		str_commRoleIds = ServerCacheDataFactory.static_str_commroles; //

		HashSet hst_roleIds = new HashSet(); // ��ͨ�ý�ɫ(һ���û�,��ͨԱ����)��id�뱾��ֱ�ӹ����Ľ�ɫid,����Ψһ�Ժϲ�...
		if ((str_commRoleIds != null && str_commRoleIds.length > 0) || (_myRoleIds != null && _myRoleIds.length > 0)) { //
			if (str_commRoleIds != null && str_commRoleIds.length > 0) {
				for (int i = 0; i < str_commRoleIds.length; i++) {
					hst_roleIds.add(str_commRoleIds[i]); //
				}
			}
			if (_myRoleIds != null && _myRoleIds.length > 0) {
				for (int i = 0; i < _myRoleIds.length; i++) {
					hst_roleIds.add(_myRoleIds[i]); //
				}
			}
		}

		// ǿת����������
		String[] str_allRoleIds = (String[]) hst_roleIds.toArray(new String[0]); // ��ת������!!
		String str_sql_all = "select menuid,userid,'usercode' c1,'username' c2, 'usermenu' dtype from pub_user_menu where userid=" + _loginuserId; //
		if (str_allRoleIds != null && str_allRoleIds.length > 0) {
			StringBuilder sb_sql_1 = new StringBuilder(); //
			sb_sql_1.append("select t1.menuid,t1.roleid,t2.code rolecode,t2.name rolename,'rolemenu' dtype "); //
			sb_sql_1.append("from pub_role_menu t1,pub_role t2 "); //
			sb_sql_1.append("where t1.roleid=t2.id ");
			sb_sql_1.append("and t1.roleid in (" + tbUtil.getInCondition(str_allRoleIds) + ") "); //			
			str_sql_all = str_sql_all + " union all " + sb_sql_1.toString(); //
		}

		// ������SQLƴ�Ӻ��ѯ���ݿ�!!!
		String[][] str_userMenuArray = commDMO.getStringArrayByDS(null, str_sql_all); //
		if (str_userMenuArray == null || str_userMenuArray.length <= 0) {
			return new HashVO[0]; //
		}

		// �û���ɫֱ�Ӱ󶨵Ĳ˵�!!
		HashSet hst_menuIds = new HashSet(); // Ψһ�Թ���
		HashSet hst_roleNames = new HashSet(); //
		boolean isBecauceUser = false; // 
		for (int i = 0; i < str_userMenuArray.length; i++) {
			hst_menuIds.add(str_userMenuArray[i][0]); //
			if (str_userMenuArray[i][4].equals("usermenu")) { // �������Ϊ"��ɫ"ֱ����"�˵�"�󶨵�
				isBecauceUser = true;
			} else if (str_userMenuArray[i][4].equals("rolemenu")) { // �������Ϊ"��ɫ"ֱ����"�˵�"�󶨵�
				hst_roleNames.add("��" + str_userMenuArray[i][2] + "/" + str_userMenuArray[i][2] + "��"); //
			}
		}
		// ƴ��һ���ַ���...
		String[] str_roleNames = (String[]) hst_roleNames.toArray(new String[0]); //
		StringBuilder sb_roleNames = new StringBuilder(); //
		for (int i = 0; i < str_roleNames.length; i++) {
			sb_roleNames.append(str_roleNames[i]); //
		}

		// �����в˵���Ѱ��!
		ArrayList al_list = new ArrayList(); //
		for (int i = 0; i < _allMenus.length; i++) { //
			if ("Y".equalsIgnoreCase(_allMenus[i].getStringValue("isalwaysopen")) || hst_menuIds.contains(_allMenus[i].getStringValue("id"))) { // ������Ǵ�,�������ҵķ�Χ��
				al_list.add(_allMenus[i]); //
			}
		}
		HashVO[] hvs = (HashVO[]) al_list.toArray(new HashVO[0]); //
		for (int i = 0; i < hvs.length; i++) {
			StringBuilder sb_reason = new StringBuilder(); //
			if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isalwaysopen"))) { //
				sb_reason.append("B:��Ϊ�ò˵����ܵ�����Զ���ŵ�;\r\n"); //
			}

			if (isBecauceUser) {
				sb_reason.append("C:��Ϊ�ò˵����˱��û�;\r\n"); //
			}

			// ������ҵĽ�ɫ!!
			if (sb_roleNames.length() > 0) { //
				sb_reason.append("D:��Ϊ�ò˵������ҵ���Щ��ɫ(Ҳ������ͨ�ý�ɫ):[" + sb_roleNames.toString() + "];\r\n"); //
			}
			hvs[i].setAttributeValue("$reason", sb_reason.toString()); //
		}
		return hvs; //

	}

	protected String getTaskdealSQL(String[] workposition) {
		return "select * from pub_task_deal"; //
	}

	/**
	 * ȡ�����а�ť
	 * 
	 * @return
	 */
	protected String getButtonSQL() {
		return "select * from pub_menu where id not in (select parentmenuid from pub_menu where parentmenuid is not null) and showintoolbar ='y' order by id";
	}

	protected String getNewsSQL(String _usercode) {
		return "";
	}

	/**
	 * ���ݽ�ɫ��������͹�ʽȡ�õ�¼��Ա�����ڲ��ŵ�
	 * @param _formula
	 * @return
	 * @throws Exception
	 */
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasByRoleAndCorpTypeFormula(_formula);
	}

	// ���ݹ�ʽȡ�õ�¼��Ա�Ļ�����Χ�ĸ���������!! ���縣�ݷ��е�������¼��idֵ
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasRootIDByTypeCase(_corpTypeCase); //
	}

	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasByTypeCase(_corpTypeCase); //
	}

	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		return new SysAppDMO().getOneUserCorpAreasByTypeCase(_userId, _corpTypeCase); //
	}

	/**
	 * �жϵ�¼��Ա�Ƿ����ĳЩ��ɫ
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception {
		return new SysAppDMO().isLoginUserContainsRole(_roleCodes); //
	}

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception {
		return new SysAppDMO().isLoginUserContainsRole(_roleCodes); //
	}

	/**
	 * �ж�ĳ����Ա�Ƿ����ĳЩ��ɫ
	 * @param _userid
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception {
		return new SysAppDMO().isOneUserContainsRole(_userid, _roleCodes); //
	}

	private CommDMO getDMO() {
		return new CommDMO(); //
	}

	/**
	 * ����һ����ݷ�ʽ!
	 */
	public void addShortCut(String _userId, String _menuId) throws Exception {
		String str_sql_find = "select * from pub_user_shortcut where userid='" + _userId + "' and menuid='" + _menuId + "'";
		HashVO[] vos = getDMO().getHashVoArrayByDS(null, str_sql_find); //
		if (vos != null && vos.length > 0) {
			throw new WLTAppException("�ò˵���ע��Ϊ�˿�ݷ�ʽ!!!"); //
		}

		String str_newid = getDMO().getSequenceNextValByDS(null, "s_pub_user_shortcut");
		String str_sql_1 = "delete from pub_user_shortcut where userid='" + _userId + "' and menuid='" + _menuId + "'"; //
		String str_sql_2 = "insert into pub_user_shortcut (id,userid,menuid) values (" + str_newid + ",'" + _userId + "','" + _menuId + "')";
		getDMO().executeBatchByDS(null, new String[] { str_sql_1, str_sql_2 }); //
	}

	/**
	 * �õ���¼ҳ��������ȵ�
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[][] getLoginHrefs() throws Exception {
		return ServerEnvironment.getLoginHref(); //
	}

	/**
	 * �����ݹ��ܵĴ���ģ��������
	 */
	public void transferDB_CreateColdata(String[] _transfernames) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		for (int i = 0; i < _transfernames.length; i++) {
			HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb where transfername='" + _transfernames[i] + "'"); //
			if (hvs_1 != null && hvs_1.length > 0) {
				String str_headid = hvs_1[0].getStringValue("id"); // ��������
				String str_dsname = hvs_1[0].getStringValue("target_dsname"); //
				if (str_dsname == null || str_dsname.trim().equals("")) {
					str_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
				}

				String str_tablename = hvs_1[0].getStringValue("target_tablename"); //
				TableDataStruct tds = commDMO.getTableDataStructByDS(str_dsname, "select * from " + str_tablename + " where 1=2"); //
				String[] str_colNames = tds.getHeaderName();

				Vector v_sqls = new Vector(); //
				v_sqls.add("delete from wlt_transferdb_b where headid='" + str_headid + "'"); //
				for (int j = 0; j < str_colNames.length; j++) {
					String str_newId = commDMO.getSequenceNextValByDS(null, "s_wlt_transferdb_b"); //
					StringBuffer sb_sql = new StringBuffer(); //
					sb_sql.append("insert into wlt_transferdb_b "); //
					sb_sql.append("("); //
					sb_sql.append("id,"); //
					sb_sql.append("headid,"); //
					sb_sql.append("transtype,"); //
					sb_sql.append("source_colname,"); //
					sb_sql.append("target_colname"); //
					sb_sql.append(")"); //
					sb_sql.append(" values "); //
					sb_sql.append("("); //
					sb_sql.append("'" + str_newId + "',"); //
					sb_sql.append("'" + str_headid + "',"); //
					sb_sql.append("'������',"); //
					sb_sql.append("'" + str_colNames[j] + "',"); //
					sb_sql.append("'" + str_colNames[j] + "'"); //
					sb_sql.append(")"); //
					v_sqls.add(sb_sql.toString()); //
				}
				commDMO.executeBatchByDS(null, v_sqls); //
			}
		}
	}

	/**
	 * ת������_����������
	 * 
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_import(String[] _transfernames) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		for (int i = 0; i < _transfernames.length; i++) {
			HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb where transfername='" + _transfernames[i] + "'"); //
			String str_headid = hvs_1[0].getStringValue("id"); // ��������
			String str_source_dsname = hvs_1[0].getStringValue("source_dsname"); //
			String str_source_tablename = hvs_1[0].getStringValue("source_tablename"); //
			String str_source_filtersql = hvs_1[0].getStringValue("source_filtersql"); //

			String str_target_dsname = hvs_1[0].getStringValue("target_dsname"); //
			String str_target_tablename = hvs_1[0].getStringValue("target_tablename"); //
			String str_target_filtersql = hvs_1[0].getStringValue("target_filtersql"); //

			if (str_source_dsname == null || str_source_dsname.trim().equals("")) {
				str_source_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
			}

			if (str_target_dsname == null || str_target_dsname.trim().equals("")) {
				str_target_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
			}

			String str_selectsql = "select * from " + str_source_tablename + " where 1=1"; //
			if (str_source_filtersql != null && !str_source_filtersql.trim().equals("")) {
				str_selectsql = str_selectsql + " and (" + str_source_filtersql + ")";
			}

			String str_deletesql = "delete from " + str_target_tablename + " where 1=1 "; //
			if (str_target_filtersql != null && !str_target_filtersql.trim().equals("")) {
				str_deletesql = str_deletesql + " and (" + str_target_filtersql + ")";
			}

			HashVO[] hvs_2 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb_b where headid='" + str_headid + "'"); //

			HashVO[] hvs_3 = commDMO.getHashVoArrayByDS(str_source_dsname, str_selectsql); //
			Vector v_sqls = new Vector(); //
			v_sqls.add(str_deletesql); //

			for (int j = 0; j < hvs_3.length; j++) {
				StringBuffer sb_sql = new StringBuffer(); //
				sb_sql.append("insert into " + str_target_tablename + " "); //
				sb_sql.append("(");
				for (int k = 0; k < hvs_2.length; k++) {
					String str_scn = hvs_2[k].getStringValue("target_colname"); //
					if (k != hvs_2.length - 1) {
						str_scn = str_scn + ","; //
					}
					sb_sql.append(str_scn); // Ŀ����
				}
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				for (int k = 0; k < hvs_2.length; k++) {
					String str_tvalue = null; //
					if (hvs_2[k].getStringValue("transtype").equals("����")) {
						str_tvalue = "'" + hvs_2[k].getStringValue("source_colname") + "'"; //
					} else {
						str_tvalue = hvs_3[j].getStringValue(hvs_2[k].getStringValue("source_colname")); //
						if (str_tvalue == null) {
							str_tvalue = "null";
						} else {
							str_tvalue = "'" + str_tvalue + "'"; //
						}
					}

					if (k != hvs_2.length - 1) {
						str_tvalue = str_tvalue + ","; //
					}

					sb_sql.append(str_tvalue); //
				}
				sb_sql.append(")");

				v_sqls.add(sb_sql.toString()); //
			}

			commDMO.executeBatchByDS(str_target_dsname, v_sqls); //
		}
	}

	/**
	 * ����һ������,ȡ��������ֱϵ������...
	 * 
	 * @param _deptid
	 * @return
	 */
	public String[] getOneDeptDirtDepts(String _deptid) throws Exception {
		CommDMO dmo = new CommDMO();
		ArrayList al_dept = new ArrayList(); //
		recursionGetDeptId(dmo, al_dept, _deptid); //
		return (String[]) al_dept.toArray(new String[0]); //
	}

	public String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		return new SysAppDMO().getLoginUserParentCorpItemValueByType(_corpType, _nvlCorpType, _itemName); //
	}

	/**
	 * ���ݺ�����ҳ�ĳ����Ա/������ĳ�����׻���,��������Ϊ��,���ҳ����и��׼�¼!
	 * @param _type
	 * @param _consValue
	 * @param _macroName
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception {
		return new SysAppDMO().getParentCorpVOByMacro(_type, _consValue, _macroName); //
	}

	/**
	 * �ݹ�ȡ�����л���
	 * 
	 * @param _al
	 * @param _deptid
	 * @throws Exception
	 */
	private void recursionGetDeptId(CommDMO _dmo, ArrayList _al, String _deptid) throws Exception {
		_al.add(_deptid); //
		String str_parentid = _dmo.getStringValueByDS(null, "select parentid from pub_corp_dept where id='" + _deptid + "'"); //
		if (str_parentid != null && !str_parentid.trim().equals("") && !str_parentid.trim().equals("null")) {
			recursionGetDeptId(_dmo, _al, str_parentid); // �ݹ����!
		}
	}

	/**
	 * �õ�һ�����������Ӳ��ŵ������б�
	 */
	public String[] getSubDeptID(String _parentdeptid) throws Exception {
		return new SysAppDMO().getSubDeptID(_parentdeptid);
	}

	// �������ж���ı���
	public String[][] getAllTableDefineNames() throws Exception {
		return new WLTDictDMO().getAllTableDefineNames(); //
	}

	/**
	 * ���ݱ���ģ����ѯ
	 */
	public String[][] getAllTableDefineNames(String tableName) throws Exception {
		return new WLTDictDMO().getAllTableDefineNames(tableName); //
	}

	/**
	 * �õ�����ֻ�ڶ������еı�
	 */
	public String[][] getAllTableOnlyDFhave() throws Exception {
		return new WLTDictDMO().getAllTableOnlyDFhave(); //
	}

	/**
	 * �õ�����ƽ̨������Դ���еı�
	 * @return
	 */
	public String[][] getAllTableBHhave() throws Exception {
		return new WLTDictDMO().getAllTableBHhave(); //
	}

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception {
		return new WLTDictDMO().getCompareLISTByTabName(_dataSourceName, _tabName); //
	}

	/**
	 * �õ�����ֻ����Դ�еı�
	 * @return
	 */
	public String[][] getAllTableOnlyDBhave() throws Exception {
		return new WLTDictDMO().getAllTableOnlyDBhave(); //
	}

	// ���ݱ����õ�����
	public String[][] getAllColumnsDefineNames(String _tabName) throws Exception {
		return new WLTDictDMO().getAllColumnsDefineNames(_tabName);
	}

	// ���ݱ��ȡ�ö����Create�ű�
	public String getCreateSQLByTabDefineName(String _tabName) throws Exception {
		return new WLTDictDMO().getCreateSQLByTabDefineName(_tabName); //
	}

	// �������ݿ����ͺͱ��ֶ��������͡�����ȡ�ö����alter�ű�
	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception {
		return new WLTDictDMO().getAlterSQLByTabDefineName(_dbtype, _tabName, _cName, _cType, _cLength); //
	}

	// ȡ�����ж��еı��alter���
	public String getAllAlterSQLByTabDefineName() throws Exception {
		return new WLTDictDMO().getAllAlterSQLByTabDefineName();
	}

	// ����ĳһ����ıȽ���Ϣ
	public String getCompareSQLByTabName(String _tabName) throws Exception {
		return new WLTDictDMO().getCompareSQLByTabName(_tabName); //
	}

	// ����ʵ�ʱ�����������Java����
	public String reverseCreateJavaCode(String _tableName) throws Exception {
		return new WLTDictDMO().reverseCreateJavaCode(_tableName);
	}

	public String exportTableSchema(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		return new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getTablesSchema(_sourceDB.getName());
	}

	public String exportTableView(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		return new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getViewDefines(_sourceDB.getName());
	}

	public void transferDBDataByds(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getData(_sourceDB.getName(), _destDB.getName());
	}

	public Hashtable exportTableDataAsText(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception {
		return new ExportDBDMO(_sourcedb.getDbtype(), _destdb.getDbtype()).getDataAsText();
	}

	public Vector updateSequence(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception {
		return new ExportDBDMO(_sourcedb.getDbtype(), _destdb.getDbtype()).updateSequence();
	}

	public File[] getSystemFiles(File file) throws Exception {
		return new SystemFileDMO().getSystemFiles(file);
	}

	public boolean hasDirectory(File file) throws Exception {
		return new SystemFileDMO().hasDirectory(file);
	}

	public void xchTest() throws Exception {
		CommDMO dmo = new CommDMO();
		HashMap map = dmo.getHashMapBySQLByDS(null, "select concat(userid,'_',userdept) c1, '1' c2 from pub_user_post"); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select id,pk_dept from pub_user where pk_dept is not null"); //
		for (int i = 0; i < hvs.length; i++) {
			String str_key = hvs[i].getStringValue("id") + "_" + hvs[i].getStringValue("pk_dept"); //
			if (!map.containsKey(str_key)) {
				String strPnewpk = dmo.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				dmo.executeUpdateByDSImmediately(null, "insert into pub_user_post (id,userid,userdept,isdefault) values ('" + strPnewpk + "','" + hvs[i].getStringValue("id") + "','" + hvs[i].getStringValue("pk_dept") + "','Y')"); // //
			}
		}
	}

	public void resetAllCorpBlParentCorpIds() throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs = commDMO.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept", "id", "parentid", "seq", null); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs.length; i++) {
			if (hvs[i].getStringValue("$parentpathids") != null) {
				al_sqls.add("update pub_corp_dept set blparentcorpids='" + hvs[i].getStringValue("$parentpathids") + "' where id='" + hvs[i].getStringValue("id") + "'"); //
			}
		}
		commDMO.executeBatchByDS(null, al_sqls); // ִ��һ��!!���ܴ�!!
		ServerCacheDataFactory.getInstance().registeCorpCacheData(); // ����ע��һ�»���!!
	}

	public String getSystemOptions(String key, String dvl) throws Exception {
		// TODO Auto-generated method stub
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + key + "'"); //
		if (str_strvalue == null) {
			return dvl;
		} else {
			return str_strvalue;
		}
	}

	public String setTotalLoginCount() throws Exception {
		String value = null;
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='totallogincount_'"); //
		if (str_strvalue == null) {
			value = "1";
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'totallogincount_','1','')" });
		} else {
			value = (new BigDecimal(str_strvalue).add(new BigDecimal(1))).toString();
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + value + "' where envkey = 'totallogincount_'"); //
		}
		return value;
	}

	public String setUserLoginCount() throws Exception {
		CommDMO dmo = new CommDMO();
		String userkey = "userlogincount_" + dmo.getCurrSession().getLoginUserId();
		String uservalue = null;
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + userkey + "'"); //
		if (str_strvalue == null) {
			uservalue = "1";
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'" + userkey + "','1','')" });
		} else {
			uservalue = (new BigDecimal(str_strvalue).add(new BigDecimal(1))).toString();
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + uservalue + "' where envkey = '" + userkey + "'"); //
		}
		return uservalue;
	}

	public void setSystemOptions(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + key + "'"); //
		if (str_strvalue == null) {
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'" + key + "','" + value + "','')" });
		} else {
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + value + "' where envkey = '" + key + "'"); //
		}

	}

	public HashMap getServerLoginUserMap() throws Exception {
		return ServerEnvironment.getInstance().getLoginUserMap();
	}

	public BillCellVO dataAccessPolicySetBuildCellVO(HashMap condition, CurrLoginUserVO _loginUserVO) throws Exception {
		return new SysAppDMO().dataAccessPolicySetBuildCellVO(condition, _loginUserVO);
	}

	public void registeTableCacheData(String _keyName, String _tableName) {
		ServerCacheDataFactory.getInstance().registeTableCacheData(_keyName, _tableName); //
	}

	public void registeTreeCacheData(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		ServerCacheDataFactory.getInstance().registeTreeCacheData(_keyName, _tableName, _idField, _parentId, _seqField); //
	}

	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName) {
		return ServerCacheDataFactory.getInstance().getCacheTableDataByAutoCreate(_keyName, _tableName);
	}

	public HashVO[] getCacheTreeDataByAutoCreate(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		return ServerCacheDataFactory.getInstance().getCacheTreeDataByAutoCreate(_keyName, _tableName, _idField, _parentId, _seqField); //
	}

	public HashVO[] getCorpCacheDataByAutoCreate() {
		return ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate();
	}

	public void registeCorpCacheData() {
		ServerCacheDataFactory.getInstance().registeCorpCacheData();
	}

	public HashVO[] getUserCacheDataByAutoCreate() {
		return ServerCacheDataFactory.getInstance().getUserCacheDataByAutoCreate();
	}

	public void registeUserCacheData() {
		ServerCacheDataFactory.getInstance().registeUserCacheData();
	}

	// public RtActionTempletVO getRunTimeActionTempletVO(String _templetName)
	// throws Exception {
	// return
	// RuntimeActionTempletFactory.getInstance().getTempletVOByName(_templetName);//
	// }

	public String compileRunTimeActionCode(String _actionName, String _codeText, boolean _isSave) throws Exception {
		return new RunTimeActionBSUtil().compileRunTimeActionCode(_actionName, _codeText, _isSave); //
	}

	// ����Java��̬����
	public HashMap loadRuntimeActionCode(String _actionName) throws Exception {
		return new RunTimeActionBSUtil().loadRuntimeActionCode(_actionName); //
	}

	public String[][] compareDictByDB() throws Exception {
		// TODO Auto-generated method stub
		return new DataBaseUtilDMO().compareDictByDB(null, null);
	}

	public String[][] compareMenuDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareMenuDateByDB();
	}

	public void dealOneMenuCommit(String codeValue) throws Exception {
		new DataBaseUtilDMO().dealOneMenuCommit(codeValue);
	}

	public String[][] compareRegbuttonDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegbuttonDateByDB();
	}

	public String[][] compareRegformatPanelDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegformatPanelDateByDB();
	}

	public String[][] compareRegregisterDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegregisterDateByDB();
	}

	public String[][] compareComboboxdictDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareComboboxdictDateByDB();
	}

	public String[][] compareOptionDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareOptionDateByDB();
	}

	public String[][] compareLookandfeelDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareLookandfeelDateByDB();
	}

	public String[][] compareUserDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareUserDateByDB();
	}

	public String[][] comparetempletDateByDB() throws Exception {
		return new DataBaseUtilDMO().comparetempletDateByDB();
	}

	public String[][] compareViewByDB() throws Exception {
		return new DataBaseUtilDMO().compareViewByDB();
	}

	public String[][] getAllInstallPackages(String _subdir) throws Exception {
		return new BSUtil().getAllInstallPackages(_subdir); //
	}

	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception {
		return new InstallDMO().getAllIntallTablesByPackagePrefix(_package_prefix); //
	}

	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception {
		return new InstallDMO().createTableByPackagePrefix(_package_prefix, _tabName); //
	}

	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception {
		return new InstallDMO().getAllIntallViewsByPackagePrefix(_package_prefix); //
	}

	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception {
		return new InstallDMO().createViewByPackagePrefix(_package_prefix, _viewName); //
	}

	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception {
		return new InstallDMO().getAllIntallInitDataByPackagePrefix(_package_prefix, _xtdatadir); //
	}

	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _tabName) throws Exception {
		return new InstallDMO().InsertInitDataByPackagePrefix(_package_prefix, _xtdatadir, _tabName); //
	}

	// ȡ����չ���ݰ�װ�ļ��嵥
	public String[][] getExt3DataXmlFiles(String _package_prefix) throws Exception {
		return new InstallDMO().getExt3DataXmlFiles(_package_prefix); //
	}

	// ��װ��չ����֮ĳ��XML�ļ�
	public String installExt3Data(String _xmlFileName) throws Exception {
		return new InstallDMO().installExt3Data(_xmlFileName); //
	}

	// ��������ע��˵�!!!
	public ArrayList getAllRegistMenu() throws Exception {
		return new InstallDMO().getAllRegistMenu(); //
	}

	// ȡ��ĳ��ע��˵���ʵ������!
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception {
		return new InstallDMO().getOneRegMenuCommand(_xmlFile, _menuName); //
	}

	// ȡ�ü���ɾ����SQL
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception {
		return new InstallDMO().getCascadeDeleteSQL(_table, _field, _value); //
	}

	// ����ɾ��SQL,��������!
	public String[] doCascadeDeleteSQL(String _table, String[][] _fieldValues, String _sql, boolean _isAutoExec) throws Exception {
		InstallDMO installDMO = new InstallDMO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < _fieldValues.length; i++) {
			String[] str_sqls = installDMO.getCascadeDeleteSQL(_table, _fieldValues[i][0], _fieldValues[i][1]); // //
			if (str_sqls != null && str_sqls.length > 0) {
				al_sqls.addAll(Arrays.asList(str_sqls)); //
			}
		}
		if (_sql != null) {
			al_sqls.add(_sql); //
		}
		String[] str_sqls = (String[]) al_sqls.toArray(new String[0]); //
		if (_isAutoExec) {
			boolean isps = SystemOptions.getBooleanValue("�Ƿ�����Ԥ����", false);
			if (isps) {
				new CommDMO().executeBatchByDS(null, Arrays.asList(new String[] { _sql }), true, true, true);
			} else {
				new CommDMO().executeBatchByDS(null, str_sqls); //
			}
		}
		return str_sqls; //
	}

	// �����޸����е�SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception {
		return new InstallDMO().getCascadeUpdateSQL(_table, _field, _oldvalue, _newValue); //
	}

	// ��������!!!
	public String[] doCascadeUpdateSQL(String _table, String[][] _changedValues, String _sql, boolean _isAutoExec) throws Exception {
		InstallDMO installDMO = new InstallDMO(); //
		ArrayList al_sqls = new ArrayList(); //
		if (_sql != null) {
			al_sqls.add(_sql); //
		}
		if (_changedValues != null && _changedValues.length > 0) {
			for (int i = 0; i < _changedValues.length; i++) {
				String[] str_sqls = installDMO.getCascadeUpdateSQL(_table, _changedValues[i][0], _changedValues[i][1], _changedValues[i][2]); //
				if (str_sqls != null && str_sqls.length > 0) {
					al_sqls.addAll(Arrays.asList(str_sqls)); //
				}
			}
		}
		String[] str_sqls = (String[]) al_sqls.toArray(new String[0]); //
		if (_isAutoExec) {
			boolean isps = SystemOptions.getBooleanValue("�Ƿ�����Ԥ����", false);
			if (isps) {
				new CommDMO().executeBatchByDS(null, Arrays.asList(new String[] { _sql }), true, true, true);
			} else {
				new CommDMO().executeBatchByDS(null, str_sqls); //
			}
		}
		return str_sqls;
	}

	// ȡ�ü��������SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception {
		return new InstallDMO().getCascadeWarnSQL(_isPreSelect); //
	}

	/**
	 * ȡ�����п��԰�װ��SQL�ļ�
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getAllInstallSQLTexts() throws Exception {
		return new DataBaseUtilDMO().getAllInstallSQLTexts(null, null); //
	}

	public ArrayList getLoginUserDeptIDs(String[] _filter) throws Exception {
		return new SysAppDMO().getLoginUserDeptIDs(_filter);
	}

	public HashVO getLoginUserInfo() throws Exception {
		return new SysAppDMO().getLoginUserInfo();
	}

	private String getTableColsMap(String tableName) throws Exception {
		String renStr = null;
		if (tableColsMap.containsKey(tableName)) {
			renStr = (String) tableColsMap.get(tableName);
		} else {
			renStr = new TBUtil().getTableAllCols(tableName);
			tableColsMap.put(tableName, renStr);
		}
		return renStr;
	}

	/**
	 * ȡ�����б��ļ�¼���Ľ��,������װ�̻�ʵʩ�����ж���Ҫ���!!! ��xch/2012-02-23��
	 */
	public String getAllTableRecordCountInfo(String[] _tables) throws Exception {
		String[][] str_allRegTables = new InstallDMO().getAllIntallTablesDescr(); // ����ע��������!!
		String[][] str_tables = null; //
		if (_tables == null) { // ������Ϊ��
			str_tables = str_allRegTables; // ֱ��ʹ������ע���!
		} else { // ���ָ���ı�!
			str_tables = new String[_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				str_tables[i][0] = _tables[0]; // ����!
				for (int j = 0; j < str_allRegTables.length; j++) { // ���Դ�ע������ҵ���Ӧ��˵��!
					if (str_allRegTables[j][0].equalsIgnoreCase(str_tables[i][0])) {
						str_tables[i][1] = str_allRegTables[j][1]; // ˵������ע����˵��!!
						break; //
					}
				}
			}
		}
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_info = new StringBuilder(); //
		int li_allTablecount = 0; //
		long ll_allRecordcount = 0; //
		for (int i = 0; i < str_tables.length; i++) {
			String str_count = commDMO.getStringValueByDS(null, "select count(*) from " + str_tables[i][0]); //
			int li_count = Integer.parseInt(str_count); //
			if (li_count > 0) { // �����¼������0
				li_allTablecount = li_allTablecount + 1; //
				ll_allRecordcount = ll_allRecordcount + li_count; //
				sb_info.append("(" + li_allTablecount + ")�� ��" + str_tables[i][0] + "��(" + str_tables[i][1] + ") �ļ�¼��=[" + li_count + "]\r\n"); //
			}
		}
		sb_info.append("һ��[" + str_tables.length + "]�ű�,����[" + li_allTablecount + "]�������ݼ�¼,�ܼ�¼��[" + ll_allRecordcount + "]"); //
		return sb_info.toString();
	}

	/**
	 * �����������б��е�ֵ!!��ʱ�����µ���ĳ���������ΪXML,���ŵ���װĿ¼��ʱ,�������ǵ�����Ӧ������ֵ(��ʹ����Ҳ���鷳),�����ͻ���ɰ�װ����¼������ʱ,��������ͻ!!! ��xch/2012-02-23��
	 * ��ʵ���ϴ������װ����������ǡ�S_������������Ҫһ�ֻ��ƿ��Է�����������,Ȼ���ڰ�װ������һ��������һ��!!! �����Ἣ��Ľ�������װ�̵Ĺ�����!! ����װ������û��pub_sequence_10001.xml��,�����ڰ�װ�����һ��ʱ���·�������֮!!
	 * @return
	 */
	public String reverseSetSequenceValue(String _packageName) throws Exception {
		String[][] str_allRegTables = new InstallDMO().getAllIntallTablesDescr(_packageName); // ������ע��������!!
		// ������������ע�����߼�,�Ժ�Ӧ�ø������,ֻ����ָ���������ֵ,������ǿ��Щ!
		ArrayList al_sqls = new ArrayList(); //
		CommDMO commdmo = new CommDMO(); //
		for (int i = 0; i < str_allRegTables.length; i++) { // ����!
			String str_table = str_allRegTables[i][0]; // ����
			String str_pkname = str_allRegTables[i][2]; // �����ֶ���
			if (str_pkname == null || str_pkname.trim().equals("")) {
				continue;
			}
			String str_maxvalue = commdmo.getStringValueByDS(null, "select max(" + str_pkname + ") from " + str_table); //
			if (str_maxvalue != null && !str_maxvalue.trim().equals("")) { // ������ֵ��Ϊ��!!
				al_sqls.add("insert into pub_sequence (sename,currvalue) values ('S_" + str_table.toUpperCase() + "'," + ((Integer.parseInt(str_maxvalue)) + 1) + ")"); // �����б��в���һ����¼!!!
			}
		}
		commdmo.executeBatchByDS(null, al_sqls); // //��������!
		return "һ�������б��в�����[" + al_sqls.size() + "]����¼!";
	}

	/**
	 * ȡ��ĳһ�����ܵ�����߰���,���߰�����ǰֻ��һ��,���ڲ˵����ý������ϴ�word����,Ȼ����ν�鿴�������Ǵ����word����!!
	 * ����������,Word������ʱ����û���ϴ�,����Word�ļ�����̫�࣬�ͻ���ʵʩ��Ա������������,�������Բ���Ϊ��!!������Ҫһ��������˼·�ļ���ı��ļ�˵��!!!
	 * �������ڵ�˼·��,����а�������˵��,��Ĭ�ϴ�֮,Ȼ������ť�ٴ�Word����!!  
	 * ��xch/2012-02-27��
	 * @param _menuId �˵�id,��������Ѱ��word������!
	 * @param _clasName ����,��������Ѱ��help�ı�!
	 * @return �ַ�������,��һλ��help�ı�,�ڶ�λ��word������!!!
	 * @throws Exception
	 */
	public String[] getMenuHelpInfo(String _menuId, String _clasName) throws Exception {
		String str_returnHelpText = null; //
		if (_clasName != null) { // ���������,Ӧ�ÿ϶�������!!!
			String str_menu_help = null; //
			String str_helppackage = null; // �����ļ���Ӧ�İ���!!
			String[][] str_installs = new BSUtil().getAllInstallPackages(null); //
			for (int i = 0; i < str_installs.length; i++) {
				InputStream ins_menuxml = this.getClass().getResourceAsStream(str_installs[i][0] + "RegisterMenu.xml"); //
				if (ins_menuxml != null) {
					org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins_menuxml); // ����XML
					java.util.List list_menu = doc.getRootElement().getChildren("menu"); // Ѱ������menu�ӽ��!
					for (int j = 0; j < list_menu.size(); j++) { // �������в˵����
						org.jdom.Element menuNode = (org.jdom.Element) list_menu.get(j); //
						// String str_menu_name =
						// menuNode.getAttributeValue("name"); //�˵�����!!
						String str_menu_cmd = menuNode.getAttributeValue("command"); // ·��!!!
						if (_clasName.equals(str_menu_cmd)) { // ���·��
							str_menu_help = menuNode.getAttributeValue("help"); // ��������,����Ϊ��,����ַ���!!!
							str_helppackage = str_installs[i][0]; //
							break; // �ж�ѭ��
						}
					}
					ins_menuxml.close(); // �ر���!!
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			// System.out.println("��������[" + _clasName + "]�ҵ�����˵��[" +
			// str_menu_help + "]"); //
			if (str_menu_help != null && !str_menu_help.trim().equals("")) { // ��������ж���!!
				InputStream ins_help = this.getClass().getResourceAsStream(str_helppackage + "help.txt"); // Ѱ��help�ļ�!!
				if (ins_help != null) { // �������help�ļ�!!
					String str_helpfileText = tbUtil.readFromInputStreamToStr(ins_help); // ��ȡ����������!!!
					StringBuilder sb_textAppend = new StringBuilder(); //
					String[] str_helpItems = tbUtil.split(str_menu_help, ";"); // �����ļ�������!!
					for (int i = 0; i < str_helpItems.length; i++) { // �������а�������!!!
						// System.out.println("��Ӧ�����ļ�����������:" +
						// str_helpfileText); ////
						int li_pos = str_helpfileText.indexOf(str_helpItems[i] + "\r\n"); // ����û�иð����ı�ǩλ��!!
						if (li_pos >= 0) { // ����ҵ�������һ��!
							String str_remain = str_helpfileText.substring(li_pos + str_helpItems[i].length(), str_helpfileText.length()); //
							int li_pos_2 = str_remain.indexOf("\r\n#"); // Ѱ����һ����#��ͷ�ļ�¼,��ֹͣ����!!!
							if (li_pos_2 > 0) {
								str_remain = str_remain.substring(0, li_pos_2); //
							}
							sb_textAppend.append("��" + str_helpItems[i] + "��֮���ٰ���:"); //
							sb_textAppend.append(str_remain + "\r\n\r\n"); //
						} else {
							sb_textAppend.append("��" + str_helpItems[i] + "�������������ļ���" + str_helppackage + "help.txt" + "����û�ж���!\r\n\r\n"); //
						}
					}
					str_returnHelpText = sb_textAppend.toString(); //
				} else {
					str_returnHelpText = "û�ж�������ļ�[" + str_helppackage + "help.txt" + "]"; //
				}
			}
		}
		String str_wordfileName = new CommDMO().getStringValueByDS(null, "select helpfile from pub_menu where id=" + _menuId); //
		return new String[] { str_returnHelpText, str_wordfileName }; //
	}

	/**
	 *�ж�ĳ����Ա�Ƿ������Ϊ��������Ա����ĳЩ����!!!
	*��������Ա(��ɫͨ��)��ƽ̨����������һ���ش����!!��������򻯾����Ȩ������,��������ϵͳ��ʹ�ù��̳�һ������Ȩ�޿��ƹ��ϡ�ʱ�޷�����ʱ!!����鿴�������������!
	*����һ�����ſ���ִ������!��
	 */
	public HashMap isCanDoAsSuperAdmin(String _loginUserId, String _queryTableName, String _savedTableName) throws Exception {
		HashMap rtMap = new HashMap(); // ���ص����ݶ���!!
		CommDMO dmo = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from pub_role where code like '%��������Ա'"); // �ҳ������ԡ���������Ա��Ϊ��꡵Ľ�ɫ�����Ƿǳ��ؼ���Լ��!!
		if (hvs.length <= 0) {
			rtMap.put("ReturnType", "1"); //
			rtMap.put("ReturnMsg", "û�ж���һ����\"��������Ա\"��β�Ľ�ɫ,���Ʋ�����,������Ȩ����!"); //
			return rtMap; //
		}

		ArrayList al_matchVos = new ArrayList(); // �洢��ָ�������ݶ���ƥ���ϵ����г�������Ա!!
		for (int i = 0; i < hvs.length; i++) {
			String str_dos = hvs[i].getStringValue("spdataobjs"); // ��Ӧ�����ݶ���!!!
			if (str_dos != null && !str_dos.trim().equals("")) { // ����ж���,�ϰ汾����û����ֶ�!
				if (str_dos.trim().equals("*")) { // �����*,���ʾ�κζ���֧��,����һ�����޳�������Ա!
					al_matchVos.add(hvs[i]); //
				} else {
					String[] str_items = tbUtil.split(str_dos, ";"); // �ָ�
					boolean isMatch = false; //
					for (int j = 0; j < str_items.length; j++) { // �������ж���Ķ�������!!
						if (_queryTableName != null) { // �����ѯ�����а����Ҷ�����,��˵��ƥ������,�����ѯ�����ǡ�v_cmp_cmpfile_1��,�Ҷ�����ǡ�cmp_��,�Ǿ�ƥ������!
							if (_queryTableName.trim().toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) {
								isMatch = true; //
								break; //
							}
						}

						if (_savedTableName != null) { // �����ѯ�����а����Ҷ�����,��˵��ƥ������,�����ѯ�����ǡ�v_cmp_cmpfile_1��,�Ҷ�����ǡ�cmp_��,�Ǿ�ƥ������!
							if (_savedTableName.trim().toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) {
								isMatch = true; //
								break; //
							}
						}
					}
					if (isMatch) { // ֻҪƥ��ɹ���!��ó�������Ա�Ƿ��������ĳ�������Ա!!
						al_matchVos.add(hvs[i]); //
					}
				}
			}
			// �Ժ�Ҫ���ݻ��������жϵ�¼��Ա�Ƿ�����ָ���Ļ�������,�����,��ż���!!��Ϊʱ���ϵ������Ū!�Ժ���Ū�ɣ���xch/2012-04-27
			// 17:52��
		}
		if (al_matchVos.size() <= 0) {
			rtMap.put("ReturnType", "2"); //
			rtMap.put("ReturnMsg", "��Ȼ������" + hvs.length + "����������Ա,��û��һ��ƥ���ϱ�ҵ�����[" + _queryTableName + "/" + _savedTableName + "],������Ȩ����!"); //
			return rtMap; //
		}

		// ���ƥ�����˳�������Ա!
		HashVO[] hvs_superRoles = (HashVO[]) al_matchVos.toArray(new HashVO[0]); // �����뱾��������ɹ���,���������ĳ�������Ա!!
		String[] str_superRoleCodes = new String[hvs_superRoles.length]; //
		for (int i = 0; i < str_superRoleCodes.length; i++) {
			str_superRoleCodes[i] = hvs_superRoles[i].getStringValue("code"); // ��ɫ����
		}
		// �����Ƿ�ֱ�ӹ�����ĳ����������Ա,���ֱ�ӹ�����,��ͻ��˲������Ի���,ֱ�ӷ���true
		String str_count = dmo.getStringValueByDS(null, "select count(*) from v_pub_user_role_1 where userid='" + _loginUserId + "' and rolecode in (" + tbUtil.getInCondition(str_superRoleCodes) + ")"); //
		if (Integer.parseInt(str_count) > 0) {
			rtMap.put("ReturnType", "3"); //
			rtMap.put("ReturnMsg", "����ֱ�Ӿ���ĳ����������Ա,�������赯�������,ֱ�Ӳ���!"); //
			return rtMap; //
		} else {
			rtMap.put("ReturnType", "4"); //
			rtMap.put("ReturnMsg", "����û��ֱ���볬������Ա����,������Ҫ�����Ի���,�ڿͻ�����������в���!"); //
			rtMap.put("AllSuperRoleVOs", hvs_superRoles); // ֱ�ӽ����з��������ĳ�������Ա���󷵻�!��Ϊ�ͻ����õ���,Ҫ��̬�����Ի���!!!Ȼ����������ʱ,ֱ���ڿͻ���У��!����Ҫ�������������,��Ϊ�����Ѿ�����������!!
			return rtMap; //
		}
	}

	// ȡ�õ�¼��Ա�Ļ���ID, ע�ⲻ�ǲ���/����, �ǻ���!!
	public HashVO getLoginUserCorpVO() throws Exception {
		return new SysAppDMO().getLoginUserCorpVO();
	}

	public int resetAllSequence() throws Exception {
		int ret = 0;
		ArrayList sqlList = new ArrayList();
		CommDMO commdmo = new CommDMO();
		String[][] str_allTables = commdmo.getAllSysTableAndDescr(null, null, false, true);// ��ǰ��ȡ��tables.xml�еı�����Ŀ����Ŀ����п���û�е���tables.xml
		// ���ȫ������Ҫȡ���ݿ��е����б����/2012-09-25��
		ArrayList senameList = new ArrayList();
		WLTDBConnection conn = new WLTInitContext().getConn(null); //
		String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // ���ݿ�����
		String str_schema = null;
		if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { // �����ORACLE��MYSQL��Ҫָ��Schema���ƣ�SQLSERVSER����ָ��!
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getUser();
		} else if (str_dbtype.equalsIgnoreCase("DB2")) {// ����DB2���ݿ���жϡ����/2015-12-24��
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getDburl();
			if (str_schema != null) {
				str_schema = str_schema.toLowerCase().trim();
				if (str_schema.contains("currentschema=")) {
					str_schema = str_schema.substring(str_schema.indexOf("currentschema=") + 14);
					if (str_schema.endsWith(";")) {
						str_schema = str_schema.substring(0, str_schema.length() - 1);
					}
				} else {
					str_schema = null;
				}
			}
		}
		DatabaseMetaData dbmd = conn.getMetaData(); // ȡ���������ݿ��ԭ���ݶ���!
		String defaultcode = commdmo.getStringValueByDS(null, "select parvalue from pub_option where parkey='seq_prefix'");// �����Ƿ���ǰ׺���ƹ���Ŀ���õ�
		for (String[] table : str_allTables) {
			String tabName = table[0]; // ����
			ResultSet resultset = dbmd.getPrimaryKeys(null, (str_schema == null ? null : str_schema.toUpperCase()), tabName);
			if (!resultset.next()) {
				continue;
			}
			String pkName = (String) resultset.getObject(4); // �����ֶ���
			if (pkName == null || pkName.trim().equals("")) {
				continue;
			}
			String maxID = null; //
			if (defaultcode == null || "".equals(defaultcode.trim())) {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName); //
			} else {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName + " where " + pkName + " like '" + defaultcode + "%'"); //
				if (maxID == null || maxID.trim().equals("")) {
					continue;
				}
				maxID = maxID.substring(defaultcode.length());
			}

			if (maxID != null && !maxID.trim().equals("")) { // ������ֵ��Ϊ��,����ֻ��������
				char[] charofs = maxID.toCharArray();// �鿴�ַ�����ÿ���ַ��Ƿ�������
				if ("-0123456789.".indexOf(charofs[0]) < 0) {// ���ǵ���ʱ�������Ǹ�ֵ�����/2012-09-25��
					continue;
				}
				boolean isNum = true;
				for (int i = 1; i < charofs.length; i++) {
					if ("0123456789.".indexOf(charofs[i]) < 0) {
						isNum = false;
						break;
					}
				}
				if (isNum) {
					senameList.add("S_" + tabName.toUpperCase());
					long val = (Long.parseLong(maxID) / 10 + 1) * 10; // ȡ��
					sqlList.add("insert into pub_sequence (sename,currvalue) values ('S_" + tabName.toUpperCase() + "'," + val + ")"); // �����б��в���һ����¼!!!
				}
			}
		}
		// ���ﲻ�ܽ��������м�¼ɾ������Ϊ�еĲ��Ǹ��ݱ��е��������ֵ���и��µģ������ϴ��ļ�ʱ���ǰ������ţ���ȡs_pub_fileupload����ʵ�ʲ����ڱ�pub_fileupload���ǵ���ͼƬע��ʱҲ�õ���
		// �����/2012-07-24��
		commdmo.executeUpdateByDS(null, "delete from pub_sequence where sename in(" + new TBUtil().getInCondition(senameList) + ")");
		commdmo.executeBatchByDS(null, sqlList); // //��������!
		ret = sqlList.size();// ��ǰret = sqlList.size()-1
		// ������Ҫ��1�����޸�֮�����/2015-12-25��
		System.out.println("��������Sequence, ���޸���pub_sequence����[" + ret + "]����¼!");
		return ret;
	}

	/***
	 * �����/2014-02-28��
	 * ���ò���Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetSequence(ArrayList _tablenames) throws Exception {
		int ret = 0;
		ArrayList sqlList = new ArrayList();
		CommDMO commdmo = new CommDMO();
		String[][] str_allTables = commdmo.getAllSysTableAndDescr(null, null, false, true);// ��ǰ��ȡ��tables.xml�еı�����Ŀ����Ŀ����п���û�е���tables.xml
		// ���ȫ������Ҫȡ���ݿ��е����б����/2012-09-25��
		ArrayList senameList = new ArrayList();
		WLTDBConnection conn = new WLTInitContext().getConn(null); //
		String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // ���ݿ�����
		String str_schema = null;
		if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { // �����ORACLE��MYSQL��Ҫָ��Schema���ƣ�SQLSERVSER����ָ��!
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getUser();
		} else if (str_dbtype.equalsIgnoreCase("DB2")) {// ����DB2���ݵ��жϡ����/2015-12-24��
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getDburl();
			if (str_schema != null) {
				str_schema = str_schema.toLowerCase().trim();
				if (str_schema.contains("currentschema=")) {
					str_schema = str_schema.substring(str_schema.indexOf("currentschema=") + 14);
					if (str_schema.endsWith(";")) {
						str_schema = str_schema.substring(0, str_schema.length() - 1);
					}
				} else {
					str_schema = null;
				}
			}
		}
		DatabaseMetaData dbmd = conn.getMetaData(); // ȡ���������ݿ��ԭ���ݶ���!
		String defaultcode = commdmo.getStringValueByDS(null, "select parvalue from pub_option where parkey='seq_prefix'");// �����Ƿ���ǰ׺���ƹ���Ŀ���õ�
		for (String[] table : str_allTables) {
			String tabName = table[0].toUpperCase(); // ����
			if (!_tablenames.contains(tabName)) {// ֻ������Ҫ���µı�sequence�����/2014-02-28��
				continue;
			}
			ResultSet resultset = dbmd.getPrimaryKeys(null, (str_schema == null ? null : str_schema.toUpperCase()), tabName);
			if (!resultset.next()) {
				continue;
			}
			String pkName = (String) resultset.getObject(4); // �����ֶ���
			if (pkName == null || pkName.trim().equals("")) {
				continue;
			}
			String maxID = null; //
			if (defaultcode == null || "".equals(defaultcode.trim())) {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName); //
			} else {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName + " where " + pkName + " like '" + defaultcode + "%'"); //
				if (maxID == null || maxID.trim().equals("")) {
					continue;
				}
				maxID = maxID.substring(defaultcode.length());
			}

			if (maxID != null && !maxID.trim().equals("")) { // ������ֵ��Ϊ��,����ֻ��������
				char[] charofs = maxID.toCharArray();// �鿴�ַ�����ÿ���ַ��Ƿ�������
				if ("-0123456789.".indexOf(charofs[0]) < 0) {// ���ǵ���ʱ�������Ǹ�ֵ�����/2012-09-25��
					continue;
				}
				boolean isNum = true;
				for (int i = 1; i < charofs.length; i++) {
					if ("0123456789.".indexOf(charofs[i]) < 0) {
						isNum = false;
						break;
					}
				}
				if (isNum) {
					senameList.add("S_" + tabName.toUpperCase());
					long val = (Long.parseLong(maxID) / 10 + 1) * 10; // ȡ��
					sqlList.add("insert into pub_sequence (sename,currvalue) values ('S_" + tabName.toUpperCase() + "'," + val + ")"); // �����б��в���һ����¼!!!
				}
			}
		}
		// ���ﲻ�ܽ��������м�¼ɾ������Ϊ�еĲ��Ǹ��ݱ��е��������ֵ���и��µģ������ϴ��ļ�ʱ���ǰ������ţ���ȡs_pub_fileupload����ʵ�ʲ����ڱ�pub_fileupload���ǵ���ͼƬע��ʱҲ�õ���
		// �����/2012-07-24��
		commdmo.executeUpdateByDS(null, "delete from pub_sequence where sename in(" + new TBUtil().getInCondition(senameList) + ")");
		commdmo.executeBatchByDS(null, sqlList); // //��������!
		ret = sqlList.size();// ��ǰret = sqlList.size()-1
		// ������Ҫ��1�����޸�֮�����/2015-12-25��
		System.out.println("��������Sequence, ���޸���pub_sequence����[" + ret + "]����¼!");
		return ret;

	}

	/*�����������������������������µİ�װģ��Զ�̵��÷�������������������������������������������������������*/
	public HashVO[] getAllInstallModuleStatus() throws Exception {
		return new QuickInstallDMO().getAllInstallModuleStatus();
	}

	public String installOrUpdateOperateModule(HashVO _install_updateConfig, String _operateType) throws Exception {
		return new QuickInstallDMO().installOrUpdateOperateAction(_install_updateConfig, _operateType);
	}

	public List getInstallOrUpdateSchedule() throws Exception {
		return QuickInstallDMO.getCurrSchedule();
	}

	public void refreshModuleOn_OffByIds(List _onoffids) throws Exception {
		new QuickInstallDMO().refreshModuleOn_OffByIds(_onoffids);
	}

	/*���������������������������µİ�װģ��Զ�̵��÷�������������������������������������������������������*/

	// ��ȡ��ҳ�������� �����/2013-06-05��
	public ArrayList getRemindDatas(String _loginUserId) throws Exception {
		ArrayList al = new ArrayList();
		if (SystemOptions.getBooleanValue("��ҳ�������Ƿ�̬����", false)) {
			if (getTaskCount(_loginUserId) > 0) {
				al.add("����������!  ");
			}
		}

		String classname = SystemOptions.getStringValue("��ҳ��̬�����Զ�����", "");
		if (!classname.equals("")) {
			// ʵ��cn.com.infostrategy.bs.sysapp.login.RemindIfc�ӿ� �����/2013-06-05��
			RemindIfc ri = (RemindIfc) Class.forName(classname).newInstance();
			ArrayList al_ = ri.getRemindDatas(_loginUserId);
			if (al_ != null && al_.size() > 0) {
				for (int i = 0; i < al_.size(); i++) {
					al.add(al_.get(i));
				}
			}
		}

		// ���ȱ�Ϊ30,��֤��ͬ����������ʾһ��, һ���Ӻ��ٴ����������
		ArrayList al_new = new ArrayList();
		if (al.size() > 0 && al.size() < 30) {
			while (al_new.size() < 30) {
				for (int i = 0; i < al.size(); i++) {
					al_new.add(al.get(i));
				}
			}
		}

		return al_new;
	}

	// ��ȡ������������ �����/2013-06-05��
	private int getTaskCount(String _loginUserId) {
		int count = 0;
		try {
			HashVO[] hvs = getDMO().getHashVoArrayByDS(null, "select count(*) cou from pub_task_deal where (dealuser='" + _loginUserId + "' or accruserid='" + _loginUserId + "')");
			count = hvs[0].getIntegerValue("cou", 0);
		} catch (Exception e) {
		}
		return count;
	}

	public BillCellVO getRolesAndMenuRelation() throws Exception {
		return new RolesAndMenuRelationDMO().getRolesAndMenuRelation();
	}
}
