package cn.com.infostrategy.bs.sysapp;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class SysAppDMO extends AbstractDMO {

	private TBUtil thisTBUtil = null; //
	private WLTInitContext thisInitContext = null; //

	public TBUtil getTBUtil() {
		if (thisTBUtil != null) {
			return thisTBUtil;
		}
		thisTBUtil = new TBUtil(); //
		return thisTBUtil;
	}

	private WLTInitContext getInitContext() {
		if (thisInitContext != null) {
			return thisInitContext; //
		}
		thisInitContext = new WLTInitContext();
		return thisInitContext;
	}

	/**
	 * �õ�һ�����ŵ������Ӳ��ŵ�����
	 * @return
	 * @throws Exception
	 */
	public String[] getSubDeptID(String _parentdeptid) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_linkcode = commDMO.getStringValueByDS(null, "select linkcode from pub_corp_dept where id='" + _parentdeptid + "'"); //
		String[][] str_data = commDMO.getStringArrayByDS(null, "select id from pub_corp_dept where linkcode like '" + str_linkcode + "%'"); //
		String[] str_return = new String[str_data.length]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = str_data[i][0]; //
		}
		return str_return; //
	}

	public BillCellVO dataAccessPolicySetBuildCellVO(HashMap condition, CurrLoginUserVO _loginUserVO) throws Exception {
		BillCellVO cellVO = null;
		String[] allRows = null;
		String[] allCols = null;
		String[] allRowsName = null;
		String[] allColsName = null;
		int li_rows = 10;
		int li_cols = 10; //
		int li_rowprefix = 2;//�ռ���
		int li_colsrefix = 1;//�ռ���
		cellVO = new BillCellVO(); //
		if (condition != null && condition.get("roleid") != null && condition.get("datatypeid") != null) {
			allRows = condition.get("roleid").toString().split(";");
			allRowsName = condition.get("obj_roleid").toString().split(";");
			allCols = condition.get("datatypeid").toString().split(";");
			allColsName = condition.get("obj_datatypeid").toString().split(";");
			li_rows = allRows.length + 3; //
			li_cols = allCols.length + 2;

			cellVO.setRowlength(li_rows); //
			cellVO.setCollength(li_cols); //
			BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols]; //
			for (int i = 0; i < cellItemVOs.length; i++) {
				for (int j = 0; j < cellItemVOs[i].length; j++) {
					cellItemVOs[i][j] = new BillCellItemVO(); //
					cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA); //
					cellItemVOs[i][j].setCellrow(i);
					cellItemVOs[i][j].setCellcol(j);
					cellItemVOs[i][j].setRowheight("20");
					cellItemVOs[i][j].setIseditable("false");
					if (j == 0) {
						cellItemVOs[i][j].setColwidth("66"); //
					} else {
						cellItemVOs[i][j].setColwidth("66"); //
					}
					if (i == 0) {
						cellItemVOs[i][j].setSpan("0,0"); //
					} else {
						cellItemVOs[i][j].setSpan("1,1"); //
					}
				}
			}
			cellItemVOs[0][0] = new BillCellItemVO(); //
			cellItemVOs[0][0].setCellvalue("����Ȩ��"); //
			cellItemVOs[0][0].setHalign(2);
			cellItemVOs[0][0].setForeground("0,0,255"); //
			cellItemVOs[0][0].setFonttype("����");
			cellItemVOs[0][0].setFontsize("14"); //
			cellItemVOs[0][0].setFontstyle("1"); //
			cellItemVOs[0][0].setRowheight("35"); //
			cellItemVOs[0][0].setIseditable("false");
			cellItemVOs[0][0].setSpan("1," + li_cols); //
			cellItemVOs[1][1] = new BillCellItemVO(); //
			cellItemVOs[1][1].setCellvalue("��ɫ"); //
			cellItemVOs[1][1].setHalign(2);
			cellItemVOs[1][1].setForeground("0,0,255"); //
			cellItemVOs[1][1].setFonttype("����");
			cellItemVOs[1][1].setBackground("232,255,255"); //
			cellItemVOs[1][1].setFontsize("14"); //
			cellItemVOs[1][1].setFontstyle("1"); //
			cellItemVOs[1][1].setRowheight("35"); //
			cellItemVOs[1][1].setIseditable("false");
			cellItemVOs[2][0] = new BillCellItemVO(); //
			cellItemVOs[2][0].setCellvalue("��Դ����"); //
			cellItemVOs[2][0].setHalign(2);
			cellItemVOs[2][0].setForeground("0,0,255"); //
			cellItemVOs[2][0].setFonttype("����");
			cellItemVOs[2][0].setBackground("232,255,255"); //
			cellItemVOs[2][0].setFontsize("14"); //
			cellItemVOs[2][0].setFontstyle("1"); //
			cellItemVOs[2][0].setRowheight("35"); //
			cellItemVOs[2][0].setIseditable("false");
			cellItemVOs[2][1] = new BillCellItemVO(); //
			cellItemVOs[2][1].setCellvalue("��Դ"); //
			cellItemVOs[2][1].setHalign(2);
			cellItemVOs[2][1].setForeground("0,0,255"); //
			cellItemVOs[2][1].setFonttype("����");
			cellItemVOs[2][1].setBackground("232,255,255"); //
			cellItemVOs[2][1].setFontsize("14"); //
			cellItemVOs[2][1].setFontstyle("1"); //
			cellItemVOs[2][1].setRowheight("35"); //
			cellItemVOs[2][1].setIseditable("false");
			if (allCols != null && allRows != null) {
				for (int i = 0; i < allRows.length; i++) { //����ͷ
					cellItemVOs[i + 3][1].setCustProperty("cellrealtype", "�б���"); //
					cellItemVOs[i + 3][1].setCustProperty("realvalue", allRows[i]); //
					cellItemVOs[i + 3][1].setCellvalue(allRowsName[i]); //
					cellItemVOs[i + 3][1].setHalign(2); //
					cellItemVOs[i + 3][1].setForeground("0,0,255"); //
					cellItemVOs[i + 3][1].setBackground("166,255,166"); //
					cellItemVOs[i + 3][1].setIseditable("false");
				}
				for (int i = 0; i < allCols.length; i++) { //����ͷ
					cellItemVOs[2][i + 2].setCustProperty("cellrealtype", "�б���"); //
					cellItemVOs[2][i + 2].setCustProperty("realvalue", allCols[i]); //
					cellItemVOs[2][i + 2].setCellvalue(allColsName[i]); //
					cellItemVOs[2][i + 2].setHalign(2); //
					cellItemVOs[2][i + 2].setForeground("0,0,255"); //
					cellItemVOs[2][i + 2].setBackground("166,255,166"); //
					cellItemVOs[2][i + 2].setIseditable("false");
				}

			}
			for (int i = 3; i < li_rows; i++) {
				for (int j = 2; j < cellItemVOs[i].length; j++) {
					String param = ifHavePolicy(cellItemVOs[i][1].getCustProperty("realvalue").toString(), cellItemVOs[2][j].getCustProperty("realvalue").toString());
					cellItemVOs[i][j].setIseditable("false");
					cellItemVOs[i][j].setCellvalue(param);
				}
			}
			if (cellItemVOs != null && cellItemVOs[0] != null && cellItemVOs[0].length > 0)
				autoResize(cellItemVOs, cellItemVOs[0].length);
			cellVO.setCellItemVOs(cellItemVOs); //
		}

		return cellVO;
	}

	public String ifHavePolicy(String roleid, String datatypeid) throws Exception {
		StringBuffer returns = new StringBuffer();
		String _sql = "select resname from pub_dataaccess_res where id in ( select resid from pub_dataaccess_role_resmap where roleid = " + roleid + " and restypeid =" + datatypeid + ")";
		String[] param = new CommDMO().getStringArrayFirstColByDS(null, _sql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				returns.append(param[i] + ";");
			}
		}
		return returns.toString();
	}

	/**
	 * ȡ�õ�¼��Ա
	 * @param _corpType
	 * @param _nvlCorpType
	 * @param _itemName
	 * @return
	 * @throws Exception
	 */
	public String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		String str_returnItemName = (_itemName == null ? "id" : _itemName); ////
		if (_corpType != null && !_corpType.trim().equals("")) { //�����Ϊ��!!!
			String str_currUserId = getInitContext().getCurrSession().getLoginUserId(); //��ǰ�û�ID!!
			HashVO[] hvs_user_corp = commDMO.getHashVoArrayByDS(null, "select userdept from pub_user_post where userid ='" + str_currUserId + "'"); //�ҵ��������Ļ���!!�Ժ���û���������Ӧ���Ǵ�seesion����ȡ��!!
			if (hvs_user_corp == null || hvs_user_corp.length == 0) {
				return null;
			}
			String str_currcorpid = hvs_user_corp[0].getStringValue("userdept"); //�ȵõ��䱾��Ļ���id!!��û���Ǽ�ְ�����!!!
			return getOneCorpParentCorpItemValueByType(str_currcorpid, _corpType, _nvlCorpType, _itemName); //ȥȡĳһ���е�ֵ!!!
		} else {
			//��ȫ��ȥ��,���û�ҵ�,��ȫ��ȥ��
			if (_nvlCorpType != null && !_nvlCorpType.trim().equals("")) { //���ͬʱָ����NVL������!!!
				String str_nvlItemValue = commDMO.getStringValueByDS(null, "select " + str_returnItemName + " from pub_corp_dept where corptype='" + _nvlCorpType + "'"); //ֱ��ȫ��ȥ��ĳ����,�ҵ�����!!!
				return str_nvlItemValue; //
			}
		}

		return null; //
	}

	//ȡ��ĳһ�������ĸ��׻�����ĳһ���ֵ,����ָ��������
	public String getOneCorpParentCorpItemValueByType(String str_currcorpid, String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_returnItemName = (_itemName == null ? "id" : _itemName); ////
		if (_corpType != null && !_corpType.trim().equals("")) { //�����Ϊ��!!!
			TBUtil tbUtil = new TBUtil(); //
			HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //�ӻ�����ȡ�����л���,ʡ֧�����ݿ����!
			HashMap mapById = new HashMap(); //Ϊ�˺���Ƶ���Ŀ��ٲ���,�ȸ����ϣ���װ����!
			for (int i = 0; i < hvs_allCorps.length; i++) {
				System.out.println(">>>>>>>>>>>"+hvs_allCorps[i].getStringValue("id"));
				mapById.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //
			}
			HashVO myHVO = (HashVO) mapById.get(str_currcorpid); //

			if (myHVO == null) {
				return null;
			}
			String str_parentCorpIDs = myHVO.getStringValue("$parentpathids"); //�ҵ����и���ID
			if (str_parentCorpIDs == null || str_parentCorpIDs.trim().equals("")) { //���Ϊ��,�������˳�!!!
				return null;
			}
			String[] str_items = tbUtil.split(str_parentCorpIDs, ";"); //�ָ�!!!

			String str_findedCorpItemValue = null; //
			String[] str_corpTypes = tbUtil.split(_corpType, "/"); //
			for (int rr = 0; rr < str_corpTypes.length; rr++) {
				boolean isfind = false; //
				for (int i = 0; i < str_items.length; i++) {
					if (str_items[i] != null && !str_items[i].trim().equals("")) { //�����Ϊ��!!!
						HashVO tempHVO = (HashVO) mapById.get(str_items[i]);
						if (tempHVO != null) { //����ҵ���!!
							if (str_corpTypes[rr].equalsIgnoreCase(tempHVO.getStringValue("corptype"))) { //�������Ͷ�����!!!!!!
								str_findedCorpItemValue = tempHVO.getStringValue(str_returnItemName, ""); //
								isfind = true; //
								break; //ֻҪ�ҵ��������˳�ѭ��
							}
						}
					}
				}
				if (isfind) {
					break; //ֻҪһ���ҵ����˳���!!!!!!
				}
			}

			if (str_findedCorpItemValue != null) { //�����Ϊ��,����������!!
				return str_findedCorpItemValue;
			}
		}

		//��ȫ��ȥ��,���û�ҵ�,��ȫ��ȥ��
		if (_nvlCorpType != null && !_nvlCorpType.trim().equals("")) { //���ͬʱָ����NVL������!!!
			String str_nvlItemValue = commDMO.getStringValueByDS(null, "select " + str_returnItemName + " from pub_corp_dept where corptype='" + _nvlCorpType + "'"); //ֱ��ȫ��ȥ��ĳ����,�ҵ�����!!!
			return str_nvlItemValue; //
		}

		return null;
	}

	public HashVO getLoginUserInfo() throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_user where id='" + str_loginUserID + "'"); //
		return hvs[0]; //
	}

	//�жϵ�¼��Ա�Ƿ����ĳЩ��ɫ
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return isOneUserContainsRole(str_loginUserID, _roleCodes); //
	}

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return isOneUserContainsRole(str_loginUserID, _roleCodes); //
	}

	//�жϵ�¼��Ա�Ƿ����ĳЩ��ɫ
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception {
		String[] str_roles = new TBUtil().split(_roleCodes, "/"); //
		return isOneUserContainsRole(_userid, str_roles); //
	}

	/**
	 * �ж�һ����Ա�Ƿ����ĳ�ֽ�ɫ!!
	 * @param _userid
	 * @param str_roles
	 * @return
	 * @throws Exception
	 */
	public boolean isOneUserContainsRole(String _userid, String[] str_roles) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t2.code ");
		sb_sql.append("from pub_user_role t1 ");
		sb_sql.append("left join pub_role t2 on t1.roleid=t2.id ");
		sb_sql.append("where t1.userid='" + _userid + "' ");
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		if (hvs == null || hvs.length <= 0) {
			return false;
		}
		ArrayList al_temp = new ArrayList(Arrays.asList(str_roles)); //
		for (int i = 0; i < hvs.length; i++) {
			if (al_temp.contains(hvs[i].getStringValue("code"))) { //������˵�ĳ����ɫ�������ڶ���Ľ�ɫ��,�򷵻���!!!
				return true;
			}
		}
		return false; //
	}

	//ȡ�õ�ǰ��¼��Ա�Ľ�ɫ�б�
	public ArrayList getLoginUserRoleList() throws Exception {
		String loginUserID = getInitContext().getCurrSession().getLoginUserId();
		return getUserRoleList(loginUserID);
	}

	//ȡ����Ա�Ľ�ɫ�б�
	public ArrayList getUserRoleList(String _userID) throws Exception {
		ArrayList roleList = new ArrayList();
		String sql = "select t3.code from pub_user t1,pub_user_role t2,pub_role t3 where t1.id=t2.userid and t2.roleid=t3.id and t1.id='" + _userID + "'";
		HashVO[] roles = new CommDMO().getHashVoArrayByDS(null, sql);
		for (int i = 0, n = roles.length; i < n; i++) {
			roleList.add(roles[i].getStringValue("code")); //��ɫ����
		}
		return roleList;
	}

	/***
	 * �ҳ�2ά�ַ���������ƥ���ֵ
	 * @param keyList ƥ���key�б�
	 * @param keyValList 2ά�ַ������� String[][] {{"key1/key2", valueX},{{"key-a/key-b", valueY}}}
	 * @return
	 */
	public static ArrayList getKeyMatchList(ArrayList keyList, String[][] keyValList) {
		ArrayList matchList = new ArrayList();
		String[] tmp = null;
		ArrayList list = null;
		for (int i = 0, n = keyValList.length; i < n; i++) {
			tmp = keyValList[i][0].split("/");
			list = new ArrayList(Arrays.asList(tmp));
			for (int ii = 0, nn = keyList.size(); ii < nn; ii++) {
				if (list.contains(keyList.get(ii))) {
					matchList.add(keyValList[i][1]);
					break;
				}
			}
		}
		return matchList;
	}

	/**
	 * ȡ��ĳ�ֻ������͵����л���!!
	 * @param _corpTypes ��������  getWFCorp("type=ĳ�����͵Ļ���","����=����/���кϹ沿")
	 * @param _isContainChild
	 * @return
	 */
	public ArrayList getOneCorpTypeAllCorps(String _corpTypes, String _down2CorpType, String _down2ExtCorpType, String _down2CorpName, boolean _isDdown2ContainChildren, HashMap _cacheMap) throws Exception {
		ArrayList al_return = new ArrayList(); //
		if (_corpTypes == null || _corpTypes.trim().equals("")) {
			return al_return;
		}
		CommDMO commDMO = new CommDMO(); //
		String str_corpId = commDMO.getStringValueByDS(null, "select id from pub_corp_dept where corptype ='" + _corpTypes + "'"); //���ݻ�������ȡ�����л���!!!
		if (str_corpId == null) { //���Ϊ��,��ֱ�ӷ���
			return al_return; //
		}
		return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(str_corpId, _corpTypes, _down2CorpType, _down2ExtCorpType, _down2CorpName, false, _isDdown2ContainChildren, "����", _cacheMap); //
	}

	//ȡ�õ�¼��Ա�Ļ�������,���ݽ�ɫ��������͹���!
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); ////
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t2.code ");
		sb_sql.append("from pub_user_role t1 ");
		sb_sql.append("left join pub_role t2 on t1.roleid=t2.id ");
		sb_sql.append("where t1.userid='" + str_loginUserID + "' ");
		HashVO[] hvs_myallroles = new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		if (hvs_myallroles == null || hvs_myallroles.length <= 0) { //�����¼��Աһ����ɫû��,��ɶ�����ܿ�!!��������һ����ɫ!
			return null;
		}
		ArrayList al_myallroles = new ArrayList(); //
		for (int i = 0; i < hvs_myallroles.length; i++) {
			al_myallroles.add(hvs_myallroles[i].getStringValue("code")); //����!!
		}

		TBUtil tbUtil = new TBUtil(); //
		String[] str_roles = tbUtil.split(_formula, "#"); //��һ�θ��ݽ�ɫ�ָ�!!
		String str_otherTypeCorpLids = null; //

		ArrayList al_temp = new ArrayList(); //
		DataPolicyDMO dataPolicyDMO = new DataPolicyDMO(); //
		for (int i = 0; i < str_roles.length; i++) {
			int li_pos = str_roles[i].indexOf("=="); //
			String str_roleitem = str_roles[i].substring(0, li_pos); //
			String str_corpTypeCase = str_roles[i].substring(li_pos + 2, str_roles[i].length()); //�������͹�ʽ!
			if (str_roleitem.equals("*")) { //������������н�ɫ
				String[] str_corps = dataPolicyDMO.getOnerUserSomeTypeParentCorpID(str_loginUserID, str_corpTypeCase, null, null, null, true, null); //ȡ�����л���
				str_otherTypeCorpLids = str_corps[2]; //�ڶ�λ,����blparentcorpids,��1;8787;������!!
			} else {
				String[] str_roleArrays = tbUtil.split(str_roleitem, "/"); //�����ж����ɫ!!!
				boolean isFinded = false; //
				for (int j = 0; j < str_roleArrays.length; j++) { //����������ɫ!!!
					if (al_myallroles.contains(str_roleArrays[j])) { //����������н�ɫ�б��о��иý�ɫ!!
						isFinded = true; //���������!
						break; //
					}
				}
				if (isFinded) { //���ƥ������!
					String[] str_corps = dataPolicyDMO.getOnerUserSomeTypeParentCorpID(str_loginUserID, str_corpTypeCase, null, null, null, true, null); //ȡ�����л���
					al_temp.add(str_corps[2]); //�ڶ�λ,����blparentcorpids,��1;8787;������!!
				}
			}
		}

		if (al_temp.size() == 0) { //���ûƥ����һ����ɫ!
			if (str_otherTypeCorpLids != null) { //�����*���ʾ���������͵�!!
				return new String[] { str_otherTypeCorpLids }; //
			} else {
				return null; //
			}
		} else {
			return (String[]) al_temp.toArray(new String[0]); //
		}
	}

	//���ݹ�ʽȡ�õ�¼��Ա�Ļ�����Χ�ĸ���������!! ���縣�ݷ��е�������¼��idֵ
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return getOneUserCorpAreasRootIDByTypeCase(str_loginUserID, _corpTypeCase); //���ݹ�ʽ
	}

	//��������,�ҵ���¼��Ա��ĳ�����͵��ϼ�����!!!
	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return getOneUserCorpAreasByTypeCase(str_loginUserID, _corpTypeCase); //
	}

	//��������ȡ��ĳ��Աĳ�����͵��ϼ�����id
	public String getOneUserCorpAreasRootIDByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		String[] str_return = new DataPolicyDMO().getOnerUserSomeTypeParentCorpID(_userId, _corpTypeCase, null, null, null, true, null); //
		if (str_return == null) {
			return null;
		}
		return str_return[0];
	}

	/**
	 * ȡ��ĳһ����Ա���Բ鿴�Ļ�����Χ,���ݱ������������Լ��������͵�case�ж�!!!
	 * ��ȡ���������ڻ���,Ȼ��ӻ��������ҵ����׻���,Ȼ��Ӵ�������,�ҳ��û�����������������!!!
	 * @param _userId ��ԱID
	 * @param _corpTypeCase  ��������Ʒ��!!!����ǳ��ؼ�,���뿼�ǵ�����չ��!!! ��������/������������=>��������;��������/������������=>��������;����/��������=>�ܲ�;
	 * @return
	 */
	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		return getOnerUserSomeTypeParentCorp(_userId, _corpTypeCase, null, null, null, true, null); //
	}

	/**
	 * ȡ��ĳ�ֻ��������µ������ӻ���!!
	 * @param _userId
	 * @param _up1RootCorpType
	 * @param _down2CorpType
	 * @param _down2ExtCorpType
	 * @param _down2CorpName
	 * @param _isDdown2ContainChildren
	 * @param _cacheMap
	 * @return
	 * @throws Exception
	 */
	public ArrayList getOnerUserSomeTypeParentCorp(String _userId, String _up1RootCorpType, String _down2CorpType, String _down2ExtCorpType, String _down2CorpName, boolean _isDdown2ContainChildren, HashMap _cacheMap) throws Exception {
		ArrayList al_return = new ArrayList(); //
		String[] str_returns = new DataPolicyDMO().getOnerUserSomeTypeParentCorpID(_userId, _up1RootCorpType, _down2CorpType, _down2ExtCorpType, _down2CorpName, _isDdown2ContainChildren, _cacheMap); //ȡ�û���id
		//System.out.println("�ҵ��ҵĻ���[" + str_createrCorpId + "]�����и�����[" + str_parentPathids + "]�гɹ�Ʒ��������Ϊ[" + _up1RootCorpType + "][" + str_realCorpType + "]�Ļ���[" + str_matchedParentCorpId + "]......"); //
		if (str_returns == null) { //���û�ҵ�,��ֱ�ӷ���!
			return al_return; //
		} else {
			String str_matchedParentCorpId = str_returns[0]; //ƥ��Ļ���is
			String str_matchedParentCorpName = str_returns[1]; //ƥ��Ļ�������
			//String str_matchedParentCorpBlparentcorpids = str_returns[2]; //��������
			String str_realCorpType = str_returns[3]; //��������
			if (str_matchedParentCorpId == null) { //���Ϊ��!!
				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�������״�����,��û���ҵ�����Ϊ[" + str_realCorpType + "]���ϼ�����,���Է��ؿ�!<br>"); //
				return al_return;
			} else {
				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�������״�����,�ҵ�����Ϊ[" + str_realCorpType + "]��ʵ���ϼ�����[" + str_matchedParentCorpId + "/" + str_matchedParentCorpName + "]<br>"); //
				return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(str_matchedParentCorpId, str_realCorpType, _down2CorpType, _down2ExtCorpType, _down2CorpName, false, _isDdown2ContainChildren, "����", _cacheMap); //�ҵ���������,��ȥ��̽
			}
		}
	}

	public void autoResize(BillCellItemVO[][] cellItemVOs, int lie) {
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font); //
		int li_allowMaxColWidth = 175; //���������ж�,���ٴ�Ҳ���ܴ���������.
		//������������Ŀ��
		for (int j = 0; j < lie; j++) { //��������
			int li_maxwidth = 70; //
			String str_cellValue = null; //
			for (int i = 0; i < cellItemVOs.length; i++) { //���и��еĸ���
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > li_maxwidth) {
						li_maxwidth = li_width; //���
					}
				}
			}
			li_maxwidth = li_maxwidth + 10; //Ϊ�˺ÿ����Ҷ�5������,���򿿵�̫�����ÿ�!
			if (li_maxwidth > li_allowMaxColWidth) {
				li_maxwidth = li_allowMaxColWidth; //
			}

			for (int i = 0; i < cellItemVOs.length; i++) { //���и��еĸ���
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > 0) {
						int li_length = (li_width / li_maxwidth) + 1; //�м���
						int li_itemRowHeight = li_length * 17 + 5; //
						if (i == 1) {
							if (li_itemRowHeight > 35) {
								cellItemVOs[i][j].setRowheight("" + li_itemRowHeight); //
							} else {
								cellItemVOs[i][j].setRowheight("35"); //
							}
						} else {
							cellItemVOs[i][j].setRowheight("" + li_itemRowHeight); //
						}
						cellItemVOs[i][j].setColwidth("" + li_maxwidth); //
					}
				}
			}
		}
	}

	/**
	 * ��������!
	 * @param _hvs
	 * @throws Exception
	 */
	public void filterHashVO(HashVO[] _hvs) throws Exception {
		//main.compile(new String[] { "C:/aaa.java" }); //
	}

	/***
	 * �ҳ�1ά�ַ���������ƥ���ֵ
	 * @param keyList ƥ���key�б�
	 * @param keyValList 1ά�ַ������� String[]{"����/��������=>�ܲ�", "��������/������������=>��������, ����", "��������/������������=>��������"};
	 * @return
	 */
	private String getKeyMatchList(String key, String[] keyValList) {
		String matchString = "";
		String[] tmp1 = null;
		String[] tmp2 = null;
		ArrayList list = null;
		for (int i = 0, n = keyValList.length; i < n; i++) {
			tmp1 = keyValList[i].split("=>");
			tmp2 = tmp1[0].split("/");
			list = new ArrayList(Arrays.asList(tmp2));
			if (list.contains(key)) {
				matchString = tmp1[1];
				break;
			}
		}
		return matchString;
	}

	// �ڸ��׻���·�����ҵ�Ҫ�ҵĻ�������
	private String getParentDeptID(HashVO[] allCorps, String parentPath, String searchDeptType) {
		TBUtil tbUtil = new TBUtil();
		String[] list = tbUtil.split(parentPath, ";");
		String deptID = null;
		for (int i = 0; i < list.length; i++) {
			boolean isFind = false;
			for (int j = 0; j < allCorps.length; j++) {
				if (allCorps[j].getStringValue("id").equals(list[i])) {
					if (allCorps[j].getStringValue("corptype") != null && allCorps[j].getStringValue("corptype").equals(searchDeptType)) {
						isFind = true;
					}
					break; //ֻҪ�ҵ��������,���˳�ѭ����,�������
				}
			}

			if (isFind) {
				deptID = list[i]; //
				break;
			}
		}
		//System.out.println("��������[" + searchDeptType + "],�ҵ����׻���Ϊ[" + deptID + "]"); //
		return deptID;

	}

	// �ڸ��׻���·�����ҵ�ȫ��������ID(��Ҫ����)
	private ArrayList getParentCorpIDs(HashVO[] allCorps, String parentPath) {
		TBUtil tbUtil = new TBUtil();
		String[] list = tbUtil.split(parentPath, ";");
		ArrayList idList = new ArrayList();
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < allCorps.length; j++) {
				if (allCorps[j].getStringValue("id").equals(list[i])) {
					if (allCorps[j].getStringValue("corptype") != null && allCorps[j].getStringValue("corptype").endsWith("����")) {
						idList.add(allCorps[j].getStringValue("id") + "," + allCorps[j].getStringValue("corptype"));
					}
				}
			}
		}
		//System.out.println("�ҵ����и��׻���Ϊ" + idList + "");
		return idList;

	}

	/**
	 * ȡ�õ�¼��Ա���Կ����Ļ�����Χ!
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public ArrayList getLoginUserDeptIDs(String filter[]) throws Exception {
		ArrayList deptIDList = new ArrayList();

		CommDMO commDMO = new CommDMO();
		String tmpSql = "";

		//ȡ��������Դ��ϵͳ����ǰ׺
		String idPrefix = SystemOptions.getStringValue("seq_prefix", "");

		//����������(ֻȡ������Դ�ģ� ���Ե�������ݣ�)
		HashVO[] allCorps = commDMO.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept where id like '" + idPrefix + "%'", "id", "parentid", "seq", null); //�ҳ����л���,���ҷ������ͽṹ!!!		
		//��������Ϊ��, �������л��� 
		if (filter == null) {
			for (int i = 0; i < allCorps.length; i++) {
				deptIDList.add(allCorps[i].getStringValue("id"));
			}
			return deptIDList; //
		}

		//�ҵ�userID
		String myUserID = new WLTInitContext().getCurrSession().getLoginUserId();

		//tmpSql = "select userdept, corptype from pub_user_post u left join pub_corp_dept d on u.userdept = d.id " + "where isdefault='Y' and userid = " + myUserID;
		tmpSql = "Select pk_dept As userdept, corptype From pub_user u  left join pub_corp_dept d on u.pk_dept = d.id Where u.Id = " + myUserID;
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, tmpSql);
		if (hvs == null || hvs.length == 0) {
			//System.out.println("�����ڵĲ���Ϊ��!!!");
			return deptIDList;
		}
		//�ҵĻ���ID
		String myDeptID = hvs[0].getStringValue("userdept");
		//�ҵĻ�������
		String myDeptType = hvs[0].getStringValue("corptype");

		//System.out.println("�ҵĻ���ID=[" + myDeptID + "]");
		//System.out.println("�ҵĻ�������=[" + myDeptType + "]");

		//��Ҫ�ҵ��Ļ�������
		String searchDeptType = this.getKeyMatchList(myDeptType, filter);
		//System.out.println("��Ҫ�ҵ��Ļ�������=[" + searchDeptType + "]");

		//�����Ҫ�ҵ��Ļ�������=*, ��������
		if ("*".equals(searchDeptType)) {
			for (int i = 0; i < allCorps.length; i++) {
				deptIDList.add(allCorps[i].getStringValue("id")); //
			}
			return deptIDList;
		}

		//�ҵĸ��׻���·��
		String myParentPath = "";
		for (int i = 0; i < allCorps.length; i++) {
			if (allCorps[i].getStringValue("id").equals(myDeptID)) {
				myParentPath = allCorps[i].getStringValue("$parentpathids"); //
				break;
			}
		}
		//System.out.println("�ҵĸ��׻���·��=[" + myParentPath + "]");

		//�ڸ��׻���·�����ҵ�ȫ��������ID(��Ҫ����)
		ArrayList<String> parentCorpList = this.getParentCorpIDs(allCorps, myParentPath);

		//��������!
		String includeType = "";
		if (searchDeptType.startsWith("*") && searchDeptType.endsWith("*")) {
			includeType = "all";
			searchDeptType = searchDeptType.substring(1, searchDeptType.length() - 1);
		} else if (searchDeptType.startsWith("*")) {
			includeType = "up";
			searchDeptType = searchDeptType.substring(1, searchDeptType.length());
		} else if (searchDeptType.endsWith("*")) {
			includeType = "down";
			searchDeptType = searchDeptType.substring(0, searchDeptType.length() - 1);
		}

		//�ڸ��׻���·�����ҵ�Ҫ�ҵĻ�������
		String parentDeptID = this.getParentDeptID(allCorps, myParentPath, searchDeptType);
		if ("".equals(includeType)) {
			//System.out.println("ֻ�����Լ�");	
			if ("����".equals(searchDeptType)) {
				// ����
				deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "����"), "����"));
			} else {
				// �����Լ�
				deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, searchDeptType.substring(0, 2)));
			}
		} else if ("down".equals(includeType)) {
			//System.out.println("�����Լ����¼�");
			// �����Լ����¼�
			deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, ""));
		} else if ("up".equals(includeType)) {
			//System.out.println("�����Լ������ϼ�");			
			// �ϼ�(����+�����ϼ�)
			// ����
			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "����"), "����"));
			// �Լ��������ϼ�
			String[] tmp = null;
			String corpID, corpType = "";
			for (String corp : parentCorpList) {
				tmp = corp.split(",");
				corpID = tmp[0];
				corpType = tmp[1].substring(0, 2);
				deptIDList.addAll(this.getChildDeptIDs(allCorps, corpID, corpType));
			}
			// �Լ�			
			//deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, searchDeptType.substring(0, 2)));			
			// �ϼ�
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "��������"), "����"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "��������"), "����"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "�ļ�����"), "�ļ�"));

		} else if ("all".equals(includeType)) {
			//System.out.println("�����Լ������ϼ�, �¼�");
			// �Լ����¼�
			deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, ""));
			// �ϼ�(����+�����ϼ�)
			// ����
			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "����"), "����"));

			// �Լ��������ϼ�
			String[] tmp = null;
			String corpID, corpType = "";
			for (String corp : parentCorpList) {
				tmp = corp.split(",");
				corpID = tmp[0];
				corpType = tmp[1].substring(0, 2);
				deptIDList.addAll(this.getChildDeptIDs(allCorps, corpID, corpType));
			}

			// �ϼ�
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "��������"), "����"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "��������"), "����"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "�ļ�����"), "�ļ�"));			
		}

		return deptIDList;
	}

	// �õ�ָ����������������ID
	private ArrayList getChildDeptIDs(HashVO[] allCorps, String parentDeptID, String deptType) {
		ArrayList childIDList = new ArrayList();

		for (int i = 0; i < allCorps.length; i++) {
			if (allCorps[i].getStringValue("$parentpathids") != null && allCorps[i].getStringValue("$parentpathids").indexOf(";" + parentDeptID + ";") >= 0) {
				if (deptType.length() > 0) {
					if (allCorps[i].getStringValue("corptype").startsWith(deptType)) {
						childIDList.add(allCorps[i].getStringValue("id"));
					}
				} else {
					childIDList.add(allCorps[i].getStringValue("id"));
				}
			}
		}

		return childIDList;
	}

	// �õ����������ID
	private String getSpecDeptID(HashVO[] allCorps, String deptType) {
		String deptID = "";

		for (int i = 0; i < allCorps.length; i++) {
			if (allCorps[i].getStringValue("corptype") != null && allCorps[i].getStringValue("corptype").equals(deptType)) {
				deptID = allCorps[i].getStringValue("id");
			}
		}
		return deptID;
	}

	/**
	 * ��������ȡ��ĳһ��ͼƬ��64λ����!!
	 * @param _batchid
	 * @return
	 * @throws Exception
	 */
	public String getImageUpload64Code(String _batchid) throws Exception {
		if (_batchid == null) {
			return null;
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _batchid + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //����!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //�����ֵ
					sb_64code.append(str_item.trim()); //ƴ������!!
				} else { //���ֵΪ��
					break; //�ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
				}
			}
		}
		return sb_64code.toString(); //
	}

	/**
	 * һ��ȡ��һЩ���ŵ�ͼƬ,��ҳ����ͼƬ��Ҫ�õ����!!
	 * @param _batchids
	 * @return
	 * @throws Exception
	 */
	public HashMap getImageUpload64Code(String[] _batchids) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		HashMap returnMap = new HashMap(); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid in (" + tbUtil.getInCondition(_batchids) + ") order by batchid,seq"); //

		for (int i = 0; i < _batchids.length; i++) { //��������!!!
			StringBuilder sb_64code = new StringBuilder(); //
			for (int r = 0; r < hvs.length; r++) { //ȥȡ�õ���������!!!
				if (_batchids[i].equals(hvs[r].getStringValue("batchid"))) { //������Ǳ����ŵ�!!!
					String str_item = null; //
					for (int j = 0; j < 10; j++) {
						str_item = hvs[r].getStringValue("img" + j); //
						if (str_item != null && !str_item.equals("")) { //�����ֵ
							sb_64code.append(str_item.trim()); //ƴ������!!
						} else { //���ֵΪ��
							break; //�ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
						}
					} //��ѭ������!!

				}
			}
			if (sb_64code.length() > 0) {
				returnMap.put(_batchids[i], sb_64code.toString()); //
			}
		}
		return returnMap; //
	}

	//ȡ��ĳһ���ڵ�ĸ��׻���ID,����ָ���Ļ�������[��������]
	private HashVO getParentCorpIDByType(String deptID, String findCorpType) throws Exception {
		HashVO corpVO = new HashVO();
		if (findCorpType == null || findCorpType.trim().equals("")) {
			return null;
		}
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //�ӻ�����ȡ�����л���,ʡ֧�����ݿ����!
		HashMap corpMapById = new HashMap(); //Ϊ�˺���Ƶ���Ŀ��ٲ���,�ȸ����ϣ���װ����!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			corpMapById.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //
		}

		HashVO myHVO = (HashVO) corpMapById.get(deptID);
		if (myHVO == null) {
			return null;
		}

		String str_parentCorpIDs = myHVO.getStringValue("$parentpathids"); //�ҵ����и���ID
		if (str_parentCorpIDs == null || str_parentCorpIDs.trim().equals("")) { //���Ϊ��,�������˳�!!!
			return null;
		}

		String[] corpIDs = tbUtil.split(str_parentCorpIDs, ";"); //�ָ�!!!
		String[] corpTypes = tbUtil.split(findCorpType, "/");
		String corpType = "";

		//���������Ҹ�ID, һ�ҵ����������ͷ�Χ�еľͷ���
		for (int i = corpIDs.length - 1; i >= 0; i--) {
			HashVO tempHVO = (HashVO) corpMapById.get(corpIDs[i]);
			corpType = tempHVO.getStringValue("corptype");
			for (int j = 0, n = corpTypes.length; j < n; j++) {
				if (corpTypes[j].equalsIgnoreCase(corpType)) { //���Ͷ�����
					//return tempHVO.getStringValue("id", ""); //����
					corpVO.setAttributeValue("id", tempHVO.getStringValue("id", ""));
					corpVO.setAttributeValue("name", tempHVO.getStringValue("name", ""));
					return corpVO;
				}
			}
		}

		return null;
	}

	/***
	 * ȡ�õ�¼��Ա�Ļ���ID, ע�ⲻ�ǲ���/����, �ǻ���!!
	 * @return
	 * @throws Exception 
	 */

	public HashVO getLoginUserCorpVO() throws Exception {
		String deptID = getInitContext().getCurrSession().getLoginUserPKDept();
		return this.getUserCorpVO(deptID);
	}

	public HashVO getUserCorpVO(String deptID) throws Exception {
		HashVO corpVO = null;
		//ȡ�������ֵ��еĻ������ඨ��, ȡ$������=��ͷ�ļ�¼, ��������֪������������Щ��ڵ��ǻ���!
		CommDMO commDMO = new CommDMO();
		String sql = "select name from pub_comboboxdict where type in ('��������','��������') and code like '$������=%'";
		HashVO[] hvsCorp = commDMO.getHashVoArrayByDS(null, sql);
		if (hvsCorp == null || hvsCorp.length == 0) {
			System.out.println("�������ֵ��еĻ������ඨ���д�: Ӧ��������[$������=����]�����ļ�¼! ");
			return null;
		}
		//ƴ��"����/����/��ҵ��"
		String corpType = "";
		for (int i = 0, n = hvsCorp.length; i < n; i++) {
			corpType += hvsCorp[i].getStringValue(0) + "/";
		}
		corpType = corpType.substring(0, corpType.length() - 1);

		//ȡ��ĳһ�������ĸ��׻�����ĳһ���ֵ,����ָ��������
		corpVO = this.getParentCorpIDByType(deptID, corpType);
		return corpVO;
	}

	/**
	 * ���ݻ��������ж���ĺ��������,���硾$�����š�
	 * @param _type,��1,2,3 ����ȡֵ,1-��¼��Ա,�����ڶ�����������Ϊ��;2-��ʾ�Ǹ���ĳ����Ա,�����ڶ���������������Աid��3-��ʾ�Ǹ��ݻ���,�����ڶ����������ǻ���id
	 * @param _consValue
	 * @param _macroName
	 * @return
	 */
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_userId = null; //
		String str_corpid = null;
		if (_type == 1 || _type == 2) { //����Ǹ�����Ա��,������Ա�ַ�����,һ����ֱ�ӵ�¼��Ա, һ����ĳ����!
			if (_type == 1) { //���������1
				str_userId = new WLTInitContext().getCurrSession().getLoginUserId(); //
			} else if (_type == 2) { //���������1
				str_userId = _consValue; //
			}
			HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select userid,userdept,isdefault from pub_user_post where userid='" + str_userId + "'"); //
			if (hvs != null && hvs.length > 0) { //
				for (int i = 0; i < hvs.length; i++) {
					if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isdefault"))) { //�����Ĭ�ϻ���
						str_corpid = hvs[i].getStringValue("userdept"); //
						break; //
					}
				}
				if (str_corpid == null) { //���û�ҵ�Ĭ�ϻ���,����Ϊ�ǵ�һ��!
					str_corpid = hvs[0].getStringValue("userdept"); //
				}
			}
		} else if (_type == 3) { //ֱ���������id
			str_corpid = _consValue; //
		}

		//�������Ϊ��,��ɶ���ɲ���,ֱ���˳�!
		if (str_corpid == null) {
			return null;
		}

		if (ServerCacheDataFactory.static_vos_corptypedef == null) {  //��������һ�㲻���,������С��С! ��Ƶ��ʹ��,�����ʺ�������!
			ServerCacheDataFactory.static_vos_corptypedef = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������')"); //
		}
		HashVO[] hvs_allTypeDef = ServerCacheDataFactory.static_vos_corptypedef; //
		String str_findCotypeType = null; //���ҵĻ�������
		if (_macroName != null) { //�����Ҫ���к����ƥ��
			String str_myCorpType = commDMO.getStringValueByDS(null, "select corptype from pub_corp_dept where id='" + str_corpid + "'"); //���ҳ�����������
			if (str_myCorpType != null && !str_myCorpType.equals("")) {
				//HashVO[] hvs_corptypes = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������') and id='" + str_myCorpType + "'"); //�ҳ����˻������Ͷ�Ӧ�ĺ���빫ʽ!!
				HashVO hvs_corptypes = null; //
				for (int i = 0; i < hvs_allTypeDef.length; i++) {
					if (hvs_allTypeDef[i].getStringValue("id", "").equals(str_myCorpType)) {
						hvs_corptypes = hvs_allTypeDef[i]; //
						break; //
					}
				}
				String str_macro = hvs_corptypes.getStringValue("code"); //�깫ʽ
				if (str_macro != null && !str_macro.equals("")) {
					HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_macro, ";", "="); //
					str_findCotypeType = (String) map.get(_macroName); //�ҵ���ҪѰ�ҵĻ�������!!���ݺ�����ҵ�ʵ�����ҵĻ������ͣ�����
				}
			}
		}

		HashVO[] corpVO = commDMO.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + str_corpid + "'"); //�����ĸ���
		if (corpVO != null && corpVO.length > 0) { //����л���
			ArrayList list = new ArrayList(); //
			recursionGetDeptVO(commDMO, list, corpVO[0]); //�ݹ��ҳ����л�������!
			ArrayList list_rt = new ArrayList(); //���ڷ��ص�!
			for (int i = list.size() - 1; i >= 0; i--) { //��ƨ����ǰ��
				HashVO itemVO = (HashVO) list.get(i); //
				if (_macroName == null) { //���ûָ��,�����ҳ����еĸ���!
					list_rt.add(itemVO); //ֱ�Ӽ���
				} else {
					String str_corpType = itemVO.getStringValue("corptype"); //��������
					if (str_corpType != null && str_corpType.equals(str_findCotypeType)) { //���ƥ������,��ֱ���˳�!
						return new HashVO[] { itemVO }; //ֱ�ӷ���
					}
				}
			}

			if (_macroName == null) { //���û��û���Ҷ�
				return (HashVO[]) list_rt.toArray(new HashVO[0]); //��������!
			} else { //���û�ҵ�һ��,��ǰ��϶����˳�,�򷵻�null
				return null; //
			}
		} else { //������û�ҵ�,��ֱ�ӷ���!
			return null; //
		}
	}

	/**
	 * �ݹ�ȡ�����л���
	 * 
	 * @param _al
	 * @param _deptid
	 * @throws Exception
	 */
	private void recursionGetDeptVO(CommDMO _dmo, ArrayList _list, HashVO _vo) throws Exception {
		_list.add(_vo); //�ȼ���..
		String str_parentid = _vo.getStringValue("parentid"); //ȡ�ø�������
		if (str_parentid != null && !str_parentid.trim().equals("") && !str_parentid.trim().equals("null")) {
			HashVO[] parentVO = _dmo.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + str_parentid + "'"); //�����ĸ���
			if (parentVO != null && parentVO.length > 0) {
				recursionGetDeptVO(_dmo, _list, parentVO[0]); //�ٴεݹ����!
			}
		}
	}

}
