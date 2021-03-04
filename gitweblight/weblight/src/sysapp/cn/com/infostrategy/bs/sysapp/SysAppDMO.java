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
	 * 得到一个部门的所有子部门的主键
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
		int li_rowprefix = 2;//空几行
		int li_colsrefix = 1;//空几列
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
			cellItemVOs[0][0].setCellvalue("数据权限"); //
			cellItemVOs[0][0].setHalign(2);
			cellItemVOs[0][0].setForeground("0,0,255"); //
			cellItemVOs[0][0].setFonttype("宋体");
			cellItemVOs[0][0].setFontsize("14"); //
			cellItemVOs[0][0].setFontstyle("1"); //
			cellItemVOs[0][0].setRowheight("35"); //
			cellItemVOs[0][0].setIseditable("false");
			cellItemVOs[0][0].setSpan("1," + li_cols); //
			cellItemVOs[1][1] = new BillCellItemVO(); //
			cellItemVOs[1][1].setCellvalue("角色"); //
			cellItemVOs[1][1].setHalign(2);
			cellItemVOs[1][1].setForeground("0,0,255"); //
			cellItemVOs[1][1].setFonttype("宋体");
			cellItemVOs[1][1].setBackground("232,255,255"); //
			cellItemVOs[1][1].setFontsize("14"); //
			cellItemVOs[1][1].setFontstyle("1"); //
			cellItemVOs[1][1].setRowheight("35"); //
			cellItemVOs[1][1].setIseditable("false");
			cellItemVOs[2][0] = new BillCellItemVO(); //
			cellItemVOs[2][0].setCellvalue("资源分类"); //
			cellItemVOs[2][0].setHalign(2);
			cellItemVOs[2][0].setForeground("0,0,255"); //
			cellItemVOs[2][0].setFonttype("宋体");
			cellItemVOs[2][0].setBackground("232,255,255"); //
			cellItemVOs[2][0].setFontsize("14"); //
			cellItemVOs[2][0].setFontstyle("1"); //
			cellItemVOs[2][0].setRowheight("35"); //
			cellItemVOs[2][0].setIseditable("false");
			cellItemVOs[2][1] = new BillCellItemVO(); //
			cellItemVOs[2][1].setCellvalue("资源"); //
			cellItemVOs[2][1].setHalign(2);
			cellItemVOs[2][1].setForeground("0,0,255"); //
			cellItemVOs[2][1].setFonttype("宋体");
			cellItemVOs[2][1].setBackground("232,255,255"); //
			cellItemVOs[2][1].setFontsize("14"); //
			cellItemVOs[2][1].setFontstyle("1"); //
			cellItemVOs[2][1].setRowheight("35"); //
			cellItemVOs[2][1].setIseditable("false");
			if (allCols != null && allRows != null) {
				for (int i = 0; i < allRows.length; i++) { //画行头
					cellItemVOs[i + 3][1].setCustProperty("cellrealtype", "行标题"); //
					cellItemVOs[i + 3][1].setCustProperty("realvalue", allRows[i]); //
					cellItemVOs[i + 3][1].setCellvalue(allRowsName[i]); //
					cellItemVOs[i + 3][1].setHalign(2); //
					cellItemVOs[i + 3][1].setForeground("0,0,255"); //
					cellItemVOs[i + 3][1].setBackground("166,255,166"); //
					cellItemVOs[i + 3][1].setIseditable("false");
				}
				for (int i = 0; i < allCols.length; i++) { //画列头
					cellItemVOs[2][i + 2].setCustProperty("cellrealtype", "列标题"); //
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
	 * 取得登录人员
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
		if (_corpType != null && !_corpType.trim().equals("")) { //如果不为空!!!
			String str_currUserId = getInitContext().getCurrSession().getLoginUserId(); //当前用户ID!!
			HashVO[] hvs_user_corp = commDMO.getHashVoArrayByDS(null, "select userdept from pub_user_post where userid ='" + str_currUserId + "'"); //找到我所属的机构!!以后的用户所属机构应该是从seesion中能取到!!
			if (hvs_user_corp == null || hvs_user_corp.length == 0) {
				return null;
			}
			String str_currcorpid = hvs_user_corp[0].getStringValue("userdept"); //先得到其本身的机构id!!先没考虑兼职的情况!!!
			return getOneCorpParentCorpItemValueByType(str_currcorpid, _corpType, _nvlCorpType, _itemName); //去取某一个列的值!!!
		} else {
			//跨全行去找,如果没找到,则全行去找
			if (_nvlCorpType != null && !_nvlCorpType.trim().equals("")) { //如果同时指定了NVL的类型!!!
				String str_nvlItemValue = commDMO.getStringValueByDS(null, "select " + str_returnItemName + " from pub_corp_dept where corptype='" + _nvlCorpType + "'"); //直接全行去找某类型,找到就算!!!
				return str_nvlItemValue; //
			}
		}

		return null; //
	}

	//取得某一个机构的父亲机构的某一项的值,根据指定的类型
	public String getOneCorpParentCorpItemValueByType(String str_currcorpid, String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_returnItemName = (_itemName == null ? "id" : _itemName); ////
		if (_corpType != null && !_corpType.trim().equals("")) { //如果不为空!!!
			TBUtil tbUtil = new TBUtil(); //
			HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //从缓存中取到所有机构,省支从数据库查了!
			HashMap mapById = new HashMap(); //为了后面频繁的快速查找,先搞个哈希表包装起来!
			for (int i = 0; i < hvs_allCorps.length; i++) {
				System.out.println(">>>>>>>>>>>"+hvs_allCorps[i].getStringValue("id"));
				mapById.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //
			}
			HashVO myHVO = (HashVO) mapById.get(str_currcorpid); //

			if (myHVO == null) {
				return null;
			}
			String str_parentCorpIDs = myHVO.getStringValue("$parentpathids"); //找到所有父亲ID
			if (str_parentCorpIDs == null || str_parentCorpIDs.trim().equals("")) { //如果为空,则立即退出!!!
				return null;
			}
			String[] str_items = tbUtil.split(str_parentCorpIDs, ";"); //分隔!!!

			String str_findedCorpItemValue = null; //
			String[] str_corpTypes = tbUtil.split(_corpType, "/"); //
			for (int rr = 0; rr < str_corpTypes.length; rr++) {
				boolean isfind = false; //
				for (int i = 0; i < str_items.length; i++) {
					if (str_items[i] != null && !str_items[i].trim().equals("")) { //如果不为空!!!
						HashVO tempHVO = (HashVO) mapById.get(str_items[i]);
						if (tempHVO != null) { //如果找到了!!
							if (str_corpTypes[rr].equalsIgnoreCase(tempHVO.getStringValue("corptype"))) { //并且类型对上了!!!!!!
								str_findedCorpItemValue = tempHVO.getStringValue(str_returnItemName, ""); //
								isfind = true; //
								break; //只要找到就立即退出循环
							}
						}
					}
				}
				if (isfind) {
					break; //只要一个找到就退出了!!!!!!
				}
			}

			if (str_findedCorpItemValue != null) { //如果不为空,则立即返回!!
				return str_findedCorpItemValue;
			}
		}

		//跨全行去找,如果没找到,则全行去找
		if (_nvlCorpType != null && !_nvlCorpType.trim().equals("")) { //如果同时指定了NVL的类型!!!
			String str_nvlItemValue = commDMO.getStringValueByDS(null, "select " + str_returnItemName + " from pub_corp_dept where corptype='" + _nvlCorpType + "'"); //直接全行去找某类型,找到就算!!!
			return str_nvlItemValue; //
		}

		return null;
	}

	public HashVO getLoginUserInfo() throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_user where id='" + str_loginUserID + "'"); //
		return hvs[0]; //
	}

	//判断登录人员是否具有某些角色
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return isOneUserContainsRole(str_loginUserID, _roleCodes); //
	}

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return isOneUserContainsRole(str_loginUserID, _roleCodes); //
	}

	//判断登录人员是否具有某些角色
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception {
		String[] str_roles = new TBUtil().split(_roleCodes, "/"); //
		return isOneUserContainsRole(_userid, str_roles); //
	}

	/**
	 * 判断一个人员是否具有某种角色!!
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
			if (al_temp.contains(hvs[i].getStringValue("code"))) { //如果本人的某个角色被包含于定义的角色中,则返回真!!!
				return true;
			}
		}
		return false; //
	}

	//取得当前登录人员的角色列表
	public ArrayList getLoginUserRoleList() throws Exception {
		String loginUserID = getInitContext().getCurrSession().getLoginUserId();
		return getUserRoleList(loginUserID);
	}

	//取得人员的角色列表
	public ArrayList getUserRoleList(String _userID) throws Exception {
		ArrayList roleList = new ArrayList();
		String sql = "select t3.code from pub_user t1,pub_user_role t2,pub_role t3 where t1.id=t2.userid and t2.roleid=t3.id and t1.id='" + _userID + "'";
		HashVO[] roles = new CommDMO().getHashVoArrayByDS(null, sql);
		for (int i = 0, n = roles.length; i < n; i++) {
			roleList.add(roles[i].getStringValue("code")); //角色编码
		}
		return roleList;
	}

	/***
	 * 找出2维字符串数组中匹配的值
	 * @param keyList 匹配的key列表
	 * @param keyValList 2维字符串数组 String[][] {{"key1/key2", valueX},{{"key-a/key-b", valueY}}}
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
	 * 取得某种机构类型的所有机构!!
	 * @param _corpTypes 机构类型  getWFCorp("type=某种类型的机构","类型=总行/总行合规部")
	 * @param _isContainChild
	 * @return
	 */
	public ArrayList getOneCorpTypeAllCorps(String _corpTypes, String _down2CorpType, String _down2ExtCorpType, String _down2CorpName, boolean _isDdown2ContainChildren, HashMap _cacheMap) throws Exception {
		ArrayList al_return = new ArrayList(); //
		if (_corpTypes == null || _corpTypes.trim().equals("")) {
			return al_return;
		}
		CommDMO commDMO = new CommDMO(); //
		String str_corpId = commDMO.getStringValueByDS(null, "select id from pub_corp_dept where corptype ='" + _corpTypes + "'"); //根据机构类型取得所有机构!!!
		if (str_corpId == null) { //如果为空,则直接返回
			return al_return; //
		}
		return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(str_corpId, _corpTypes, _down2CorpType, _down2ExtCorpType, _down2CorpName, false, _isDdown2ContainChildren, "交集", _cacheMap); //
	}

	//取得登录人员的机构条件,根据角色与机构类型公类!
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); ////
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t2.code ");
		sb_sql.append("from pub_user_role t1 ");
		sb_sql.append("left join pub_role t2 on t1.roleid=t2.id ");
		sb_sql.append("where t1.userid='" + str_loginUserID + "' ");
		HashVO[] hvs_myallroles = new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		if (hvs_myallroles == null || hvs_myallroles.length <= 0) { //如果登录人员一个角色没有,则啥都不能看!!即必须有一个角色!
			return null;
		}
		ArrayList al_myallroles = new ArrayList(); //
		for (int i = 0; i < hvs_myallroles.length; i++) {
			al_myallroles.add(hvs_myallroles[i].getStringValue("code")); //加入!!
		}

		TBUtil tbUtil = new TBUtil(); //
		String[] str_roles = tbUtil.split(_formula, "#"); //第一次根据角色分割!!
		String str_otherTypeCorpLids = null; //

		ArrayList al_temp = new ArrayList(); //
		DataPolicyDMO dataPolicyDMO = new DataPolicyDMO(); //
		for (int i = 0; i < str_roles.length; i++) {
			int li_pos = str_roles[i].indexOf("=="); //
			String str_roleitem = str_roles[i].substring(0, li_pos); //
			String str_corpTypeCase = str_roles[i].substring(li_pos + 2, str_roles[i].length()); //机构类型公式!
			if (str_roleitem.equals("*")) { //如果是其他所有角色
				String[] str_corps = dataPolicyDMO.getOnerUserSomeTypeParentCorpID(str_loginUserID, str_corpTypeCase, null, null, null, true, null); //取得所有机构
				str_otherTypeCorpLids = str_corps[2]; //第二位,就是blparentcorpids,即1;8787;的样子!!
			} else {
				String[] str_roleArrays = tbUtil.split(str_roleitem, "/"); //可能有多个角色!!!
				boolean isFinded = false; //
				for (int j = 0; j < str_roleArrays.length; j++) { //遍历各个角色!!!
					if (al_myallroles.contains(str_roleArrays[j])) { //如果本人所有角色列表中具有该角色!!
						isFinded = true; //如果发现了!
						break; //
					}
				}
				if (isFinded) { //如果匹配上了!
					String[] str_corps = dataPolicyDMO.getOnerUserSomeTypeParentCorpID(str_loginUserID, str_corpTypeCase, null, null, null, true, null); //取得所有机构
					al_temp.add(str_corps[2]); //第二位,就是blparentcorpids,即1;8787;的样子!!
				}
			}
		}

		if (al_temp.size() == 0) { //如果没匹配上一个角色!
			if (str_otherTypeCorpLids != null) { //如果有*则表示有其他类型的!!
				return new String[] { str_otherTypeCorpLids }; //
			} else {
				return null; //
			}
		} else {
			return (String[]) al_temp.toArray(new String[0]); //
		}
	}

	//根据公式取得登录人员的机构范围的根结点的主键!! 比如福州分行的这条记录的id值
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return getOneUserCorpAreasRootIDByTypeCase(str_loginUserID, _corpTypeCase); //根据公式
	}

	//根据条件,找到登录人员的某种类型的上级机构!!!
	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception {
		String str_loginUserID = getInitContext().getCurrSession().getLoginUserId(); //
		return getOneUserCorpAreasByTypeCase(str_loginUserID, _corpTypeCase); //
	}

	//根据条件取得某人员某种类型的上级机构id
	public String getOneUserCorpAreasRootIDByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		String[] str_return = new DataPolicyDMO().getOnerUserSomeTypeParentCorpID(_userId, _corpTypeCase, null, null, null, true, null); //
		if (str_return == null) {
			return null;
		}
		return str_return[0];
	}

	/**
	 * 取得某一个人员可以查看的机构范围,根据本人所属机构以及机构类型的case判断!!!
	 * 先取出本人所在机构,然后从机构树中找到父亲机构,然后从从上至下,找出该机构的所有下属机构!!!
	 * @param _userId 人员ID
	 * @param _corpTypeCase  机构类型品配!!!这个非常关键,必须考虑到可扩展性!!! 三级机构/三级机构部门=>三级机构;二级机构/二级机构部门=>二级机构;本部/本部部门=>总部;
	 * @return
	 */
	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		return getOnerUserSomeTypeParentCorp(_userId, _corpTypeCase, null, null, null, true, null); //
	}

	/**
	 * 取得某种机构类型下的所有子机构!!
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
		String[] str_returns = new DataPolicyDMO().getOnerUserSomeTypeParentCorpID(_userId, _up1RootCorpType, _down2CorpType, _down2ExtCorpType, _down2CorpName, _isDdown2ContainChildren, _cacheMap); //取得机构id
		//System.out.println("找到我的机构[" + str_createrCorpId + "]的所有父机构[" + str_parentPathids + "]中成功品配上类型为[" + _up1RootCorpType + "][" + str_realCorpType + "]的机构[" + str_matchedParentCorpId + "]......"); //
		if (str_returns == null) { //如果没找到,则直接返回!
			return al_return; //
		} else {
			String str_matchedParentCorpId = str_returns[0]; //匹配的机构is
			String str_matchedParentCorpName = str_returns[1]; //匹配的机构名称
			//String str_matchedParentCorpBlparentcorpids = str_returns[2]; //机构类型
			String str_realCorpType = str_returns[3]; //机构类型
			if (str_matchedParentCorpId == null) { //如果为空!!
				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆进行首次上溯,但没有找到类型为[" + str_realCorpType + "]的上级机构,所以返回空!<br>"); //
				return al_return;
			} else {
				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆进行首次上溯,找到类型为[" + str_realCorpType + "]的实际上级机构[" + str_matchedParentCorpId + "/" + str_matchedParentCorpName + "]<br>"); //
				return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(str_matchedParentCorpId, str_realCorpType, _down2CorpType, _down2ExtCorpType, _down2CorpName, false, _isDdown2ContainChildren, "交集", _cacheMap); //找到父机构后,再去下探
			}
		}
	}

	public void autoResize(BillCellItemVO[][] cellItemVOs, int lie) {
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font); //
		int li_allowMaxColWidth = 175; //允许的最大列度,即再大也不能大于这个宽度.
		//计算出各列最大的宽度
		for (int j = 0; j < lie; j++) { //遍历各列
			int li_maxwidth = 70; //
			String str_cellValue = null; //
			for (int i = 0; i < cellItemVOs.length; i++) { //遍列该列的各行
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > li_maxwidth) {
						li_maxwidth = li_width; //最大
					}
				}
			}
			li_maxwidth = li_maxwidth + 10; //为了好看向右多5个像素,否则靠的太挤不好看!
			if (li_maxwidth > li_allowMaxColWidth) {
				li_maxwidth = li_allowMaxColWidth; //
			}

			for (int i = 0; i < cellItemVOs.length; i++) { //遍列该列的各行
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > 0) {
						int li_length = (li_width / li_maxwidth) + 1; //有几行
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
	 * 过滤数据!
	 * @param _hvs
	 * @throws Exception
	 */
	public void filterHashVO(HashVO[] _hvs) throws Exception {
		//main.compile(new String[] { "C:/aaa.java" }); //
	}

	/***
	 * 找出1维字符串数组中匹配的值
	 * @param keyList 匹配的key列表
	 * @param keyValList 1维字符串数组 String[]{"本部/本部部门=>总部", "二级机构/二级机构部门=>二级机构, 本部", "三级机构/三级机构部门=>三级机构"};
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

	// 在父亲机构路径中找到要找的机构类型
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
					break; //只要找到这个机构,就退出循环了,提高性能
				}
			}

			if (isFind) {
				deptID = list[i]; //
				break;
			}
		}
		//System.out.println("根据类型[" + searchDeptType + "],找到父亲机构为[" + deptID + "]"); //
		return deptID;

	}

	// 在父亲机构路径中找到全部机构的ID(不要部门)
	private ArrayList getParentCorpIDs(HashVO[] allCorps, String parentPath) {
		TBUtil tbUtil = new TBUtil();
		String[] list = tbUtil.split(parentPath, ";");
		ArrayList idList = new ArrayList();
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < allCorps.length; j++) {
				if (allCorps[j].getStringValue("id").equals(list[i])) {
					if (allCorps[j].getStringValue("corptype") != null && allCorps[j].getStringValue("corptype").endsWith("机构")) {
						idList.add(allCorps[j].getStringValue("id") + "," + allCorps[j].getStringValue("corptype"));
					}
				}
			}
		}
		//System.out.println("找到所有父亲机构为" + idList + "");
		return idList;

	}

	/**
	 * 取得登录人员可以看到的机构范围!
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public ArrayList getLoginUserDeptIDs(String filter[]) throws Exception {
		ArrayList deptIDList = new ArrayList();

		CommDMO commDMO = new CommDMO();
		String tmpSql = "";

		//取出本数据源中系统主键前缀
		String idPrefix = SystemOptions.getStringValue("seq_prefix", "");

		//整个机构树(只取本数据源的， 忽略导入的数据！)
		HashVO[] allCorps = commDMO.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept where id like '" + idPrefix + "%'", "id", "parentid", "seq", null); //找出所有机构,并且返回树型结构!!!		
		//过滤条件为空, 返回所有机构 
		if (filter == null) {
			for (int i = 0; i < allCorps.length; i++) {
				deptIDList.add(allCorps[i].getStringValue("id"));
			}
			return deptIDList; //
		}

		//我的userID
		String myUserID = new WLTInitContext().getCurrSession().getLoginUserId();

		//tmpSql = "select userdept, corptype from pub_user_post u left join pub_corp_dept d on u.userdept = d.id " + "where isdefault='Y' and userid = " + myUserID;
		tmpSql = "Select pk_dept As userdept, corptype From pub_user u  left join pub_corp_dept d on u.pk_dept = d.id Where u.Id = " + myUserID;
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, tmpSql);
		if (hvs == null || hvs.length == 0) {
			//System.out.println("我所在的部门为空!!!");
			return deptIDList;
		}
		//我的机构ID
		String myDeptID = hvs[0].getStringValue("userdept");
		//我的机构类型
		String myDeptType = hvs[0].getStringValue("corptype");

		//System.out.println("我的机构ID=[" + myDeptID + "]");
		//System.out.println("我的机构类型=[" + myDeptType + "]");

		//需要找到的机构类型
		String searchDeptType = this.getKeyMatchList(myDeptType, filter);
		//System.out.println("需要找到的机构类型=[" + searchDeptType + "]");

		//如果需要找到的机构类型=*, 返回所有
		if ("*".equals(searchDeptType)) {
			for (int i = 0; i < allCorps.length; i++) {
				deptIDList.add(allCorps[i].getStringValue("id")); //
			}
			return deptIDList;
		}

		//我的父亲机构路径
		String myParentPath = "";
		for (int i = 0; i < allCorps.length; i++) {
			if (allCorps[i].getStringValue("id").equals(myDeptID)) {
				myParentPath = allCorps[i].getStringValue("$parentpathids"); //
				break;
			}
		}
		//System.out.println("我的父亲机构路径=[" + myParentPath + "]");

		//在父亲机构路径中找到全部机构的ID(不要部门)
		ArrayList<String> parentCorpList = this.getParentCorpIDs(allCorps, myParentPath);

		//查找类型!
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

		//在父亲机构路径中找到要找的机构类型
		String parentDeptID = this.getParentDeptID(allCorps, myParentPath, searchDeptType);
		if ("".equals(includeType)) {
			//System.out.println("只看到自己");	
			if ("本部".equals(searchDeptType)) {
				// 本部
				deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "本部"), "本部"));
			} else {
				// 看到自己
				deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, searchDeptType.substring(0, 2)));
			}
		} else if ("down".equals(includeType)) {
			//System.out.println("看到自己及下级");
			// 看到自己及下级
			deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, ""));
		} else if ("up".equals(includeType)) {
			//System.out.println("看到自己及其上级");			
			// 上级(本部+所有上级)
			// 本部
			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "本部"), "本部"));
			// 自己及所有上级
			String[] tmp = null;
			String corpID, corpType = "";
			for (String corp : parentCorpList) {
				tmp = corp.split(",");
				corpID = tmp[0];
				corpType = tmp[1].substring(0, 2);
				deptIDList.addAll(this.getChildDeptIDs(allCorps, corpID, corpType));
			}
			// 自己			
			//deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, searchDeptType.substring(0, 2)));			
			// 上级
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "二级机构"), "二级"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "三级机构"), "三级"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "四级机构"), "四级"));

		} else if ("all".equals(includeType)) {
			//System.out.println("看到自己及其上级, 下级");
			// 自己及下级
			deptIDList.addAll(this.getChildDeptIDs(allCorps, parentDeptID, ""));
			// 上级(本部+所有上级)
			// 本部
			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getSpecDeptID(allCorps, "本部"), "本部"));

			// 自己及所有上级
			String[] tmp = null;
			String corpID, corpType = "";
			for (String corp : parentCorpList) {
				tmp = corp.split(",");
				corpID = tmp[0];
				corpType = tmp[1].substring(0, 2);
				deptIDList.addAll(this.getChildDeptIDs(allCorps, corpID, corpType));
			}

			// 上级
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "二级机构"), "二级"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "三级机构"), "三级"));
			//			deptIDList.addAll(this.getChildDeptIDs(allCorps, this.getParentDeptID(allCorps, myParentPath, "四级机构"), "四级"));			
		}

		return deptIDList;
	}

	// 得到指定机构的所有子孙ID
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

	// 得到特殊机构的ID
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
	 * 根据批号取得某一个图片的64位编码!!
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
		for (int i = 0; i < hvs.length; i++) { //遍历!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //如果有值
					sb_64code.append(str_item.trim()); //拼接起来!!
				} else { //如果值为空
					break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
				}
			}
		}
		return sb_64code.toString(); //
	}

	/**
	 * 一次取得一些批号的图片,首页滚动图片需要用到这个!!
	 * @param _batchids
	 * @return
	 * @throws Exception
	 */
	public HashMap getImageUpload64Code(String[] _batchids) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		HashMap returnMap = new HashMap(); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid in (" + tbUtil.getInCondition(_batchids) + ") order by batchid,seq"); //

		for (int i = 0; i < _batchids.length; i++) { //遍历所有!!!
			StringBuilder sb_64code = new StringBuilder(); //
			for (int r = 0; r < hvs.length; r++) { //去取得的数据中找!!!
				if (_batchids[i].equals(hvs[r].getStringValue("batchid"))) { //如果就是本批号的!!!
					String str_item = null; //
					for (int j = 0; j < 10; j++) {
						str_item = hvs[r].getStringValue("img" + j); //
						if (str_item != null && !str_item.equals("")) { //如果有值
							sb_64code.append(str_item.trim()); //拼接起来!!
						} else { //如果值为空
							break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
						}
					} //列循环结束!!

				}
			}
			if (sb_64code.length() > 0) {
				returnMap.put(_batchids[i], sb_64code.toString()); //
			}
		}
		return returnMap; //
	}

	//取得某一个节点的父亲机构ID,根据指定的机构类型[倒过来找]
	private HashVO getParentCorpIDByType(String deptID, String findCorpType) throws Exception {
		HashVO corpVO = new HashVO();
		if (findCorpType == null || findCorpType.trim().equals("")) {
			return null;
		}
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //从缓存中取到所有机构,省支从数据库查了!
		HashMap corpMapById = new HashMap(); //为了后面频繁的快速查找,先搞个哈希表包装起来!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			corpMapById.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //
		}

		HashVO myHVO = (HashVO) corpMapById.get(deptID);
		if (myHVO == null) {
			return null;
		}

		String str_parentCorpIDs = myHVO.getStringValue("$parentpathids"); //找到所有父亲ID
		if (str_parentCorpIDs == null || str_parentCorpIDs.trim().equals("")) { //如果为空,则立即退出!!!
			return null;
		}

		String[] corpIDs = tbUtil.split(str_parentCorpIDs, ";"); //分隔!!!
		String[] corpTypes = tbUtil.split(findCorpType, "/");
		String corpType = "";

		//从下往上找父ID, 一找到包含在类型范围中的就返回
		for (int i = corpIDs.length - 1; i >= 0; i--) {
			HashVO tempHVO = (HashVO) corpMapById.get(corpIDs[i]);
			corpType = tempHVO.getStringValue("corptype");
			for (int j = 0, n = corpTypes.length; j < n; j++) {
				if (corpTypes[j].equalsIgnoreCase(corpType)) { //类型对上了
					//return tempHVO.getStringValue("id", ""); //返回
					corpVO.setAttributeValue("id", tempHVO.getStringValue("id", ""));
					corpVO.setAttributeValue("name", tempHVO.getStringValue("name", ""));
					return corpVO;
				}
			}
		}

		return null;
	}

	/***
	 * 取得登录人员的机构ID, 注意不是部门/处室, 是机构!!
	 * @return
	 * @throws Exception 
	 */

	public HashVO getLoginUserCorpVO() throws Exception {
		String deptID = getInitContext().getCurrSession().getLoginUserPKDept();
		return this.getUserCorpVO(deptID);
	}

	public HashVO getUserCorpVO(String deptID) throws Exception {
		HashVO corpVO = null;
		//取下拉框字典中的机构分类定义, 取$本机构=开头的记录, 这样可以知道机构树中哪些类节点是机构!
		CommDMO commDMO = new CommDMO();
		String sql = "select name from pub_comboboxdict where type in ('机构分类','机构类型') and code like '$本机构=%'";
		HashVO[] hvsCorp = commDMO.getHashVoArrayByDS(null, sql);
		if (hvsCorp == null || hvsCorp.length == 0) {
			System.out.println("下拉框字典中的机构分类定义有错: 应该有类似[$本机构=总行]这样的记录! ");
			return null;
		}
		//拼成"总行/分行/事业部"
		String corpType = "";
		for (int i = 0, n = hvsCorp.length; i < n; i++) {
			corpType += hvsCorp[i].getStringValue(0) + "/";
		}
		corpType = corpType.substring(0, corpType.length() - 1);

		//取得某一个机构的父亲机构的某一项的值,根据指定的类型
		corpVO = this.getParentCorpIDByType(deptID, corpType);
		return corpVO;
	}

	/**
	 * 根据机构类型中定义的宏代码类型,比如【$本部门】
	 * @param _type,有1,2,3 三种取值,1-登录人员,这样第二个参数可以为空;2-表示是根据某个人员,这样第二个参数必须是人员id；3-表示是根据机构,这样第二个参数就是机构id
	 * @param _consValue
	 * @param _macroName
	 * @return
	 */
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_userId = null; //
		String str_corpid = null;
		if (_type == 1 || _type == 2) { //如果是根据人员找,根据人员又分两种,一种是直接登录人员, 一种是某个人!
			if (_type == 1) { //如果是类型1
				str_userId = new WLTInitContext().getCurrSession().getLoginUserId(); //
			} else if (_type == 2) { //如果是类型1
				str_userId = _consValue; //
			}
			HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select userid,userdept,isdefault from pub_user_post where userid='" + str_userId + "'"); //
			if (hvs != null && hvs.length > 0) { //
				for (int i = 0; i < hvs.length; i++) {
					if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isdefault"))) { //如果是默认机构
						str_corpid = hvs[i].getStringValue("userdept"); //
						break; //
					}
				}
				if (str_corpid == null) { //如果没找到默认机构,则认为是第一个!
					str_corpid = hvs[0].getStringValue("userdept"); //
				}
			}
		} else if (_type == 3) { //直接输入机构id
			str_corpid = _consValue; //
		}

		//如果机构为空,则啥都干不了,直接退出!
		if (str_corpid == null) {
			return null;
		}

		if (ServerCacheDataFactory.static_vos_corptypedef == null) {  //机构类型一般不会变,且数量小很小! 且频繁使用,所以适合做缓存!
			ServerCacheDataFactory.static_vos_corptypedef = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型')"); //
		}
		HashVO[] hvs_allTypeDef = ServerCacheDataFactory.static_vos_corptypedef; //
		String str_findCotypeType = null; //想找的机构类型
		if (_macroName != null) { //如果需要进行宏代码匹配
			String str_myCorpType = commDMO.getStringValueByDS(null, "select corptype from pub_corp_dept where id='" + str_corpid + "'"); //先找出本机构类型
			if (str_myCorpType != null && !str_myCorpType.equals("")) {
				//HashVO[] hvs_corptypes = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型') and id='" + str_myCorpType + "'"); //找出本人机构类型对应的宏代码公式!!
				HashVO hvs_corptypes = null; //
				for (int i = 0; i < hvs_allTypeDef.length; i++) {
					if (hvs_allTypeDef[i].getStringValue("id", "").equals(str_myCorpType)) {
						hvs_corptypes = hvs_allTypeDef[i]; //
						break; //
					}
				}
				String str_macro = hvs_corptypes.getStringValue("code"); //宏公式
				if (str_macro != null && !str_macro.equals("")) {
					HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_macro, ";", "="); //
					str_findCotypeType = (String) map.get(_macroName); //找到需要寻找的机构类型!!根据宏代码找到实际想找的机构类型！！！
				}
			}
		}

		HashVO[] corpVO = commDMO.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + str_corpid + "'"); //找他的父亲
		if (corpVO != null && corpVO.length > 0) { //如果有机构
			ArrayList list = new ArrayList(); //
			recursionGetDeptVO(commDMO, list, corpVO[0]); //递归找出所有机构类型!
			ArrayList list_rt = new ArrayList(); //用于返回的!
			for (int i = list.size() - 1; i >= 0; i--) { //从屁股往前找
				HashVO itemVO = (HashVO) list.get(i); //
				if (_macroName == null) { //如果没指定,就是找出所有的父亲!
					list_rt.add(itemVO); //直接加入
				} else {
					String str_corpType = itemVO.getStringValue("corptype"); //机构类型
					if (str_corpType != null && str_corpType.equals(str_findCotypeType)) { //如果匹配上了,则直接退出!
						return new HashVO[] { itemVO }; //直接返回
					}
				}
			}

			if (_macroName == null) { //如果没有没有找定
				return (HashVO[]) list_rt.toArray(new HashVO[0]); //返回所有!
			} else { //如果没找到一个,则前面肯定会退出,则返回null
				return null; //
			}
		} else { //机构都没找到,则直接返回!
			return null; //
		}
	}

	/**
	 * 递归取得所有机构
	 * 
	 * @param _al
	 * @param _deptid
	 * @throws Exception
	 */
	private void recursionGetDeptVO(CommDMO _dmo, ArrayList _list, HashVO _vo) throws Exception {
		_list.add(_vo); //先加入..
		String str_parentid = _vo.getStringValue("parentid"); //取得父亲主键
		if (str_parentid != null && !str_parentid.trim().equals("") && !str_parentid.trim().equals("null")) {
			HashVO[] parentVO = _dmo.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + str_parentid + "'"); //找他的父亲
			if (parentVO != null && parentVO.length > 0) {
				recursionGetDeptVO(_dmo, _list, parentVO[0]); //再次递归调用!
			}
		}
	}

}
