package cn.com.infostrategy.bs.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * �������ݴ�����
 * �ؼ�,Ҫʵ��BI��һЩ����,���������϶�ά��
 * ��Ҫʵ��һ����������ӵĹ��ߣ�����һ����һ��Ĵ���HashVO.
 * @author xch
 *
 */
public class ReportDMO extends AbstractDMO {

	org.apache.log4j.Logger logger = WLTLogger.getLogger(ReportDMO.class); //

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * ����ͳ�� ����groupHashVOs(hvs, "sum(c1) s1", "code,name"); //
	 * @param _hvs
	 * @param _groupFunc ���麯��,Ŀǰ����sum(account),count(*)����,Max(account),Min(account),�Ժ�֧��Avg(account)��
	 * @param _groupField.
	 * @return code,name,s1
	 */
	public HashVO[] groupHashVOs(HashVO[] _hvs, String _groupFunc, String _groupFields) {
		//�ȷ���һ�²���������
		String[] str_groupFields = tbUtil.split(_groupFields, ","); //
		for (int i = 0; i < str_groupFields.length; i++) {
			str_groupFields[i] = str_groupFields[i].trim(); //ȥ���ո�
		}

		//�ȷ���һ�������﷨,���м���α��...
		String[] str_items = tbUtil.split(_groupFunc, ","); //����,�ָ�..
		String[] str_funnames = new String[str_items.length]; //������
		String[] str_funpars = new String[str_items.length]; //��������
		String[] str_boguscols = new String[str_items.length]; //α����
		for (int i = 0; i < str_items.length; i++) {
			str_items[i] = str_items[i].trim(); //��ȥ����β�ո�
			int li_pos_1 = str_items[i].indexOf("("); //��������
			int li_pos_2 = str_items[i].indexOf(")"); //��������
			str_funnames[i] = str_items[i].substring(0, li_pos_1).trim(); //������
			str_funpars[i] = str_items[i].substring(li_pos_1 + 1, li_pos_2).trim(); //��������
			str_boguscols[i] = str_items[i].substring(li_pos_2 + 1, str_items[i].length()).trim(); //��������
			//System.out.println("������[" + str_funnames[i] + "],��������[" + str_funpars[i] + "],����[" + str_boguscols[i] + "]"); //
		}

		//��������
		VectorMap map_rowdata = new VectorMap(); //
		for (int i = 0; i < _hvs.length; i++) { //
			StringBuffer sb_rowvalue = new StringBuffer(); //
			for (int j = 0; j < str_groupFields.length; j++) { //
				String str_itemvalue = _hvs[i].getStringValue(str_groupFields[j]); //
				sb_rowvalue.append(str_itemvalue + "#"); //
			}

			if (map_rowdata.containsKey(sb_rowvalue.toString())) { //����Ѿ�����..
				HashVO hvo_group = (HashVO) map_rowdata.get(sb_rowvalue.toString()); //
				//��������α��
				for (int j = 0; j < str_boguscols.length; j++) {
					BigDecimal bigDecimal_olddata = hvo_group.getBigDecimalValue(str_boguscols[j]); //ԭ��������
					BigDecimal bigDecimal_newdata = null;
					if (str_funnames[j].equalsIgnoreCase("sum")) { //��ԭ�������ϼ��ϸ�������..
						bigDecimal_newdata = bigDecimal_olddata.add(_hvs[i].getBigDecimalValue(str_funpars[j]) == null ? new BigDecimal(0) : _hvs[i].getBigDecimalValue(str_funpars[j])); //
					} else if (str_funnames[j].equalsIgnoreCase("count")) {
						bigDecimal_newdata = bigDecimal_olddata.add(new BigDecimal(1)); //
					} else if (str_funnames[j].equalsIgnoreCase("max")) {
						if (bigDecimal_olddata == null) {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]);
							}
						} else {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								if (_hvs[i].getBigDecimalValue(str_funpars[j]).compareTo(bigDecimal_olddata) > 0) { //�����ǰ���ݴ��ھ�����,�򽫵�ǰ�����û�..
									bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
								}
							}
						}
					} else if (str_funnames[j].equalsIgnoreCase("min")) {
						if (bigDecimal_olddata == null) {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]);
							}
						} else {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
							} else {
								if (_hvs[i].getBigDecimalValue(str_funpars[j]).compareTo(bigDecimal_olddata) < 0) { //�����ǰ����С�ھ�����,�򽫵�ǰ�����û�..
									bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
								}
							}
						}
					} else if (str_funnames[j].equalsIgnoreCase("avg")) { //ƽ���Ƚ��鷳,�ȸ������
						bigDecimal_newdata = new BigDecimal(5); //
					}
					hvo_group.setAttributeValue(str_boguscols[j], bigDecimal_newdata); //������������
				}
			} else { //�����û��
				HashVO hvo_group = new HashVO(); //�������
				for (int j = 0; j < str_groupFields.length; j++) { //
					String str_itemvalue = _hvs[i].getStringValue(str_groupFields[j]); //
					hvo_group.setAttributeValue(str_groupFields[j], str_itemvalue); //�Ƚ�����ͳ�Ƶ�������
				}

				//�ټ�������α��....
				for (int j = 0; j < str_boguscols.length; j++) {
					if (str_funnames[j].equalsIgnoreCase("sum")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j]) == null ? new BigDecimal(0) : _hvs[i].getBigDecimalValue(str_funpars[j])); //�����sum���,�򽫵�һ��ֱֵ������	
					} else if (str_funnames[j].equalsIgnoreCase("count")) {
						hvo_group.setAttributeValue(str_boguscols[j], new BigDecimal(1)); //	
					} else if (str_funnames[j].equalsIgnoreCase("max")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j])); //	
					} else if (str_funnames[j].equalsIgnoreCase("min")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j])); //	
					} else if (str_funnames[j].equalsIgnoreCase("avg")) { //ƽ���Ƚ��鷳,�ȸ������
						hvo_group.setAttributeValue(str_boguscols[j], new BigDecimal(5));
					}
				}

				map_rowdata.put(sb_rowvalue.toString(), hvo_group); //
			}
		}

		Object[] objs = map_rowdata.getValues(); //
		HashVO[] returnVOs = new HashVO[objs.length]; //
		for (int i = 0; i < returnVOs.length; i++) {
			returnVOs[i] = (HashVO) objs[i];
		}
		return returnVOs; //
	}

	/**
	 * ȡ�����ͽṹ,���ع�ϣ��,key��Ψһ��id,value�ǴӸ��������ǰ�������Ƶ�һά����
	 * @param _sql
	 * @param _idName
	 * @param _nameName
	 * @param _parentIdName
	 * @return
	 * @throws Exception
	 */
	public HashMap getTreeStruct(String _sql) throws Exception {
		//String str_sql = "select " + _idName + "," + _nameName + "," + _parentIdName + " from " + _tableName;
		HashVO[] hvsData = new CommDMO().getHashVoArrayByDS(null, _sql); //

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new RefItemVO("-99999", null, "�����")); //
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvsData.length]; //�������
		HashMap map_parent = new HashMap();
		for (int i = 0; i < hvsData.length; i++) {
			node_level_1[i] = new BillTreeDefaultMutableTreeNode(new RefItemVO(hvsData[i].getStringValue(0), hvsData[i].getStringValue(2), hvsData[i].getStringValue(1))); //SQL����еĵ�һ����id,��2��������,��������parentid
			map_parent.put(hvsData[i].getStringValue(0), node_level_1[i]); //
			rootNode.add(node_level_1[i]); // ��������
		}

		//������,����һ���Ұְֵ���Ϸ!!!
		RefItemVO nodeVO = null; //
		String str_pk_parentPK = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (RefItemVO) node_level_1[i].getUserObject();
			str_pk_parentPK = nodeVO.getCode(); // ��������
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //����ҵ��ְ���..
				try {
					parentnode.add(node_level_1[i]); //�ڰְ����������..
				} catch (Exception ex) {
					logger.error("��[" + parentnode + "]�ϴ����ӽ��[" + node_level_1[i] + "]ʧ��!!");
					ex.printStackTrace(); //
				}
			}
		}

		HashMap mapReturn = new HashMap(); //
		for (int i = 0; i < node_level_1.length; i++) {
			RefItemVO nodeItemVO = (RefItemVO) node_level_1[i].getUserObject();
			String str_id = nodeItemVO.getId(); //
			TreeNode[] pathNodes = node_level_1[i].getPath(); //
			String[] str_pathNames = new String[pathNodes.length]; //
			for (int j = 0; j < pathNodes.length; j++) {
				str_pathNames[j] = ((RefItemVO) ((DefaultMutableTreeNode) pathNodes[j]).getUserObject()).getName(); //
			}
			mapReturn.put(str_id, str_pathNames); //
		}
		return mapReturn;
	}

	private void myReport(String[] _deptid) throws Exception {
		String str_incondition = new TBUtil().getInCondition(_deptid);
		HashMap map_id_linkcode = new CommDMO().getHashMapBySQLByDS(null, "select id,linkcode from pub_corp_dept"); //
		HashMap map_linkcode_name = new CommDMO().getHashMapBySQLByDS(null, "select linkcode,name from pub_corp_dept"); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from lbs_cases where deptid in (" + str_incondition + ")"); //
		for (int i = 0; i < hvs.length; i++) {
			String str_deptid = hvs[i].getStringValue("createdeptid"); //
			String str_linkcode = (String) map_id_linkcode.get(str_deptid); //
			hvs[i].setAttributeValue("linkcode", str_linkcode); //�����µ���
			hvs[i].setAttributeValue("linkcode_8", str_linkcode.substring(0, 8)); //�����µ���\
			String str_fnname = (String) map_linkcode_name.get(str_linkcode.substring(0, 8));
			hvs[i].setAttributeValue("linkcode_8_name", str_fnname); //�����µ���
		}

		HashVO[] hvsreturn = groupHashVOs(hvs, "sum(account) s1,count(*) c1,max(a2)", "linkcode_8_name,busitype");
		//���ݷ��� ��˾ҵ�� 80  5  6
		//���ݷ��� ����ҵ�� 80  5  6
		//���ݷ��� 90  6
		//�Ϻ����� 100

	}

	/**
	 * ��һ��HashVO[]����һ������_newItemName,���е���ֵ�ļ����߼��Ǵ�һ������(����sql����),�þ��е�ֵƷ��sql���ؽ�����еĵ�1�У����ص�2��
	 * ˵���˾��Ǹ���id��������,����addOneFieldFromOtherTable(hvs,"username","userid","select id,name from pub_user");
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTable(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql) throws Exception {
		HashMap custStateMap = getHashMapDataBysql(_sql); //""
		int li_errorcount = 0; //
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "����ֵ��"); //findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = tbUtil.split(str_custids, ";"); //�ָ�һ��!
				for (int j = 0; j < str_items.length; j++) {
					String str_itemName = (String) custStateMap.get(str_items[j]); //
					if (str_itemName != null) {
						str_convertname = str_convertname + str_itemName + ";"; //
					} else {
						li_errorcount++;
						if (li_errorcount < 9) {
							System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����ֵ��" + str_custids + "�������" + str_items[j] + "����SQL��" + _sql + "����û������ֵ������"); //
						}
					}
				}
				if (str_convertname.endsWith(";")) {
					str_convertname = str_convertname.substring(0, str_convertname.length() - 1); //
				}
				_hvs[i].setAttributeValue(_newItemName, str_convertname); //�����µ�ֵ
			}
		}

		if (li_errorcount > 0) {
			System.err.println("���͹���ʱһ������[" + li_errorcount + "]�ι�������,�ֶΡ�" + _fromOldItemName + "����" + _sql + "��������"); //
		}
	}

	/**
	 * 
	 * @param _hvs ԭʼvos
	 * @param _fromOldItemName vo�еıȽ��ֶ�
	 * @param newItems ����ӵ��� 
	 * @param _sql ���
	 * @param _leftjoinTableKey ���еıȽϼ���
	 */
	public void addMoreFieldFromOtherTable(HashVO[] _hvs, String _fromOldItemName, String[] newItems, String _sql, String _leftjoinTableKey) throws Exception {
		HashVO t_vos[] = new CommDMO().getHashVoArrayByDS(null, _sql); //�������ȡֵ�����vos
		if (t_vos.length == 0) {
			System.err.println("HashVO��չ��,SQL��" + _sql + "��û�в鵽�κ����ݣ�����"); //
			return;
		}
		HashMap<String, HashVO> map = new HashMap<String, HashVO>();
		for (int i = 0; i < t_vos.length; i++) {
			map.put(t_vos[i].getStringValue(_leftjoinTableKey), t_vos[i]);
		}
		for (int i = 0; i < _hvs.length; i++) {
			String value = _hvs[i].getStringValue(_fromOldItemName);
			if (value == null) {
				for (int j = 0; j < newItems.length; j++) {
					_hvs[i].setAttributeValue(newItems[j], "����ֵ��");
				}
			} else {
				HashVO q_hashvo = map.get(value);
				if (q_hashvo == null) {
					System.err.println("HashVO��չ��ʱ,�����ֶΡ�" + _fromOldItemName + "����ֵ��SQL��" + _sql + "�������ƥ���" + _leftjoinTableKey + "����û������ֵ������"); //
					for (int j = 0; j < newItems.length; j++) {
						_hvs[i].setAttributeValue(newItems[j], "����ֵ��");
					}
				} else {
					for (int j = 0; j < newItems.length; j++) {
						_hvs[i].setAttributeValue(newItems[j], q_hashvo.getStringValue(newItems[j]));
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql ����[select id,name,parentid from bsd_bsact]���SQLҪ������,�ҵ�һ��������,�ڶ�����������ʾ������,�������ǹ���������parentid,������������û���ر�Ҫ��!ֻҪ�еĺ����ǶԵ�!
	 * @param _level
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level) throws Exception {
		addOneFieldFromOtherTree(_hvs, _newItemName, _fromOldItemName, _sql, _level, false, 1); //
	}

	/**
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql ����[select id,name,parentid from bsd_bsact]���SQLҪ������,�ҵ�һ��������,�ڶ�����������ʾ������,�������ǹ���������parentid,������������û���ر�Ҫ��!ֻҪ�еĺ����ǶԵ�!
	 * @param _level
	 * @param _isLinkName �Ƿ�·����ƴ����һ��?? ���硾�Ϻ�����-�ֶ�֧�С�    
	 * @param _linkName_StartLevel ������Ҫ������ƴ������,����Ϊ�е����ĵ�һ����Զ��һ����,���о���ϻ�,������ʱ��Ҫ�ӵڶ����ĳһ�㿪ʼƴ��!
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level, boolean _isLinkName, int _linkName_StartLevel) throws Exception {
		HashMap mapTemp = getTreeStruct(_sql); //
		int li_linkfailCount = 0; //
		int li_arrayoutCount = 0;//
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "����ֵ��"); // findchannel
			} else {
				StringBuilder sb_convertname = new StringBuilder(); //
				String[] str_items = tbUtil.split(str_custids, ";");
				for (int j = 0; j < str_items.length; j++) {
					String[] str_treePathNames = (String[]) mapTemp.get(str_items[j]); //
					if (str_treePathNames == null || str_treePathNames.length == 0) {
						li_linkfailCount++;
					} else {
						//Gwang 2016-05-06 �޸�Ϊ�� ���·���������� �ж��ٷ��ض࣡ ԭ������ֱ�ӷ��ؿգ�
						if (_isLinkName && _linkName_StartLevel <= _level) {
							StringBuilder sb_kk = new StringBuilder(); //
							for (int k = _linkName_StartLevel; k <= _level; k++) {
								if (str_treePathNames.length == k) {
									break;
								}
								sb_kk.append(str_treePathNames[k]); //
								if (k < _level) {
									sb_kk.append("-"); //
								}
							}
							sb_convertname.append(sb_kk.toString()); //
						} else {
							if (_level >= str_treePathNames.length) {
								sb_convertname.append(str_treePathNames[str_treePathNames.length - 1]); // ֱ�Ӽ��롾���/2016-12-15��
							} else {
								sb_convertname.append(str_treePathNames[_level]); // ֱ�Ӽ���
							}

						}
						if (j != str_items.length - 1) { // ����������һ����ӷֺ�!
							sb_convertname.append(";"); //
						}

					}
				}

				String sb = sb_convertname.toString();
				if (sb.endsWith("-")) {
					sb = sb.substring(0, sb.length() - 1);
				}
				_hvs[i].setAttributeValue(_newItemName, sb); // ��������
			}
		}
		System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����SQL��" + _sql + "����ȡ�ڡ�" + _level + "����ʱ������" + li_linkfailCount + "����û������ֵ������"); //
		System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����SQL��" + _sql + "����ȡ�ڡ�" + _level + "����ʱ������" + li_arrayoutCount + "����Խ�磡����"); //
	}

	/**
	 * ����SQL����ѯ���ݣ���������ĵ�1��Ϊkey,��2����ΪValue������һ����ϣ��
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	private HashMap getHashMapDataBysql(String _sql) throws Exception {
		String[][] str_data = new CommDMO().getStringArrayByDS(null, _sql); //
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < str_data.length; i++) {
			returnMap.put(str_data[i][0], str_data[i][1]); //
		}
		return returnMap;
	}

	/**
	 * ��һ��HashVO[]�������н��м���,��������зֺ����,���Զ����������,�����Ͳ���Ҫʹ�ñ��������ֱ�����һ������д���!
	 * Ȼ���Ǹ������еĶ�ѡ����Զ���ּ���ɶ�����൱��һ�Զ����ʱ���ݳɶ�����¼!!
	 * @param _hvs
	 * @param _GroupLevel_1
	 * @param _GroupLevel_2
	 * @return
	 * @throws Exception
	 */
	public HashVO[] computeHashVOTwoFieldRecords(HashVO[] _hvs, String _GroupLevel_1, String _GroupLevel_2) throws Exception {
		HashMap map_compute = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) { //
			String str_1 = _hvs[i].getStringValue(_GroupLevel_1); //
			String str_2 = _hvs[i].getStringValue(_GroupLevel_2); //
			if (str_1 == null || str_1.trim().equals("")) {
				str_1 = "����ֵ��";
			}
			if (str_2 == null || str_2.trim().equals("")) {
				str_2 = "����ֵ��";
			}
			String[] str_oneRecordInfo = getOneRecordCompute(str_1, str_2); //һ�м�¼�и��ݷֺż���ĸ������,�������ֻ��һ��!����˼������!!
			for (int j = 0; j < str_oneRecordInfo.length; j++) {
				if (map_compute.containsKey(str_oneRecordInfo[j])) { //
					Integer oldInteger = (Integer) map_compute.get(str_oneRecordInfo[j]); //
					map_compute.put(str_oneRecordInfo[j], new Integer(oldInteger.intValue() + 1)); //�ۼ�
				} else {
					map_compute.put(str_oneRecordInfo[j], new Integer(1)); //
				}
			}
		}
		String[] str_allKeys = (String[]) map_compute.keySet().toArray(new String[0]);
		HashVO[] returnVOs = new HashVO[str_allKeys.length]; //
		for (int i = 0; i < str_allKeys.length; i++) {
			returnVOs[i] = new HashVO(); //
			String[] str_keyItems = tbUtil.split(str_allKeys[i], "#"); //
			returnVOs[i].setAttributeValue(_GroupLevel_1, str_keyItems[0]); //
			returnVOs[i].setAttributeValue(_GroupLevel_2, str_keyItems[1]); //
			returnVOs[i].setAttributeValue("��¼��", map_compute.get(str_allKeys[i])); //
		}

		return returnVOs;
	}

	private String[] getOneRecordCompute(String _str1, String _str2) {
		String[] str_array1 = tbUtil.split(_str1, ";");
		String[] str_array2 = tbUtil.split(_str2, ";");

		ArrayList al_1 = new ArrayList(); //
		for (int i = 0; i < str_array1.length; i++) {
			if (str_array1[i] != null && !str_array1[i].trim().equals("")) {
				al_1.add(str_array1[i]); //
			}
		}
		str_array1 = (String[]) al_1.toArray(new String[0]); //

		ArrayList al_2 = new ArrayList(); //
		for (int i = 0; i < str_array2.length; i++) {
			if (str_array2[i] != null && !str_array2[i].trim().equals("")) {
				al_2.add(str_array2[i]); //
			}
		}
		str_array2 = (String[]) al_2.toArray(new String[0]); //

		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < str_array1.length; i++) {
			for (int j = 0; j < str_array2.length; j++) {
				String str_key = str_array1[i] + "#" + str_array2[j]; //
				//if (!al_temp.contains(str_key)) { //���������,������,���ظ���ֻ��һ��!!�о��ڲ�ͬʱ����Ҫ��ͬ����!! �����Ա���Ա����ܹ���,�����ҵ�����
				al_temp.add(str_key); //
				//}
			}
		}
		return (String[]) al_temp.toArray(new String[0]); //
	}

	/**
	 * 
	 * @param _oldVOs
	 * @param _newHVO
	 * @param _oldfield
	 * @param _newfield
	 * @return
	 */
	public HashVO[] leftOuterJoinNewHashVO(HashVO[] _oldVOs, String _sql, String _oldfield, String _newfield) throws Exception {
		ReportUtil util = new ReportUtil(); //
		HashVOStruct hvs = new CommDMO().getHashVoStructByDS(null, _sql); //
		return util.leftOuterJoinNewHashVO(_oldVOs, hvs, _oldfield, _newfield); //
	}

	/**
	 * ��һ�������,ȡ���Ǹ����е�ĳ���ֶη���ԭ����HashVO[]��,
	 * @param _oldVOs ������
	 * @param _newItemName ����������
	 * @param _tableName �������
	 * @param _dbfieldName ��ȡ������
	 * @param _whereDBFielKey ���й������ֶ���
	 * @param _fromItemKey ��ԭ��HashVO[]�й������ֶ���
	 * @throws Exception
	 */
	public void leftOuterJoin_TableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey) throws Exception {
		String str_sql = "select " + _whereDBFielKey + "," + _joinedFieldName + " from " + _tableName; //
		HashVO[] hvs_leftTable = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		HashMap map_leftTable = new HashMap(); //
		for (int i = 0; i < hvs_leftTable.length; i++) {
			map_leftTable.put(hvs_leftTable[i].getStringValue(_whereDBFielKey), hvs_leftTable[i].getStringValue(_joinedFieldName)); //
		}

		String str_fromValue = null; //
		String str_leftTableFieldName = null; //
		for (int i = 0; i < _oldVOs.length; i++) {
			str_fromValue = _oldVOs[i].getStringValue(_fromItemKey); //
			if (str_fromValue == null) {
				_oldVOs[i].setAttributeValue(_newItemName, ""); //
			} else {
				str_leftTableFieldName = (String) map_leftTable.get(str_fromValue); //
				_oldVOs[i].setAttributeValue(_newItemName, str_leftTableFieldName); //
				//System.out.println("����[" + _newItemName + "]=[" + str_leftTableFieldName + "]"); //
			}
		}
	}

	public void leftOuterJoin_TreeTableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey, String _treeLinkedIdField, String _treeLinkedParentIdField, int _getLevel) throws Exception {
		leftOuterJoin_TreeTableFieldName(_oldVOs, _newItemName, _tableName, _joinedFieldName, _whereDBFielKey, _fromItemKey, _treeLinkedIdField, _treeLinkedParentIdField, _getLevel, false);
	}

	/**
	 * ��һ�����α����,ȥȡ���Ǹ����еĵڼ����ĳ���ֶε�ֵ!!
	 * @param _oldVOs ԭ��������
	 * @param _newItemName �������ֶ���
	 * @param _tableName  ��ѯ���������
	 * @param _joinedFieldName ��Ҫ������ֶ���
	 * @param _whereDBFielKey ���ݿ��й������ֶ���
	 * @param _fromItemKey ��ԭ����HashVO�е��ĸ��ֶ�����
	 * @param _treeLinkedIdField �����������ӵ������ֶ���
	 * @param _treeLinkedParentIdField �����������ӵ�����ֶ���
	 * @param _getLevel ��ȡ�ڼ���
	 * @param isReturnFullPath �Ƿ񷵻�����ȫ·�� Gwang 2012-3-15 ����
	 * @throws Exception 
	 */
	public void leftOuterJoin_TreeTableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey, String _treeLinkedIdField, String _treeLinkedParentIdField, int _getLevel, boolean isReturnFullPath) throws Exception {
		//��Ҫ����һ����!!!��Ϊ�����ҪҪȡ��ĳ�����������ϵĵڼ���!!!!���Ա�����Ҫ����һ����!!!
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root"); //
		String str_sql = "select " + _whereDBFielKey + " id," + _joinedFieldName + " name," + _treeLinkedIdField + " linkid," + _treeLinkedParentIdField + " linkparentid from " + _tableName; //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvs.length]; // �������н������
		HashMap map_assign = new HashMap(); //
		HashMap map_parent = new HashMap();
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("name"); //
			node_level_1[i] = new DefaultMutableTreeNode(hvs[i]); // �����������
			rootNode.add(node_level_1[i]); // ��������
			map_assign.put(hvs[i].getStringValue("id"), node_level_1[i]); //
			map_parent.put(hvs[i].getStringValue("linkid"), node_level_1[i]); //��¼���Ĺ�ϵ
		}

		HashVO nodeVO = null; //
		String str_pk_parentPK = null; //
		HashVO nodeVO_2 = null; //
		String str_pk_2 = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (HashVO) node_level_1[i].getUserObject();
			str_pk_parentPK = nodeVO.getStringValue("linkparentid"); // ��������
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) {
				try {
					parentnode.add(node_level_1[i]); //
				} catch (Exception ex) {
					logger.error("��[" + parentnode + "]�ϴ����ӽ��[" + node_level_1[i] + "]ʧ��!!");
					ex.printStackTrace(); //
				}
			}
		}

		String str_fromItemValue = null; //
		//�������������������������!!!
		for (int i = 0; i < _oldVOs.length; i++) {
			str_fromItemValue = _oldVOs[i].getStringValue(_fromItemKey); //
			if (str_fromItemValue == null) {
				_oldVOs[i].setAttributeValue(_newItemName, ""); //by haoming 2015-12-29��ԭ����hvo[i].setAttributeValue...
			} else {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) map_assign.get(str_fromItemValue); //
				if (node != null && _getLevel > 0 && node.getLevel() >= _getLevel) { //����ҵ��Ľ���δ��ڵ�����ȡ�Ĳ��!!
					TreeNode[] parentNodes = node.getPath();
					//========Gwang 2012-3-15 ���� =============>/
					//��������ȫ·��
					String nodeName = "";
					if (isReturnFullPath) {
						for (int ii = _getLevel, n = parentNodes.length; ii < n; ii++) {
							nodeName += parentNodes[ii].toString() + "-";
						}
						nodeName = nodeName.substring(0, nodeName.length() - 1);
					} else {
						nodeName = parentNodes[_getLevel].toString();
					}
					_oldVOs[i].setAttributeValue(_newItemName, nodeName); //
					//<=========Gwang 2012-3-15 ���� =============/

					//HashVO findHVO = (HashVO) (((DefaultMutableTreeNode) parentNodes[_getLevel]).getUserObject()); //
					//_oldVOs[i].setAttributeValue(_newItemName, findHVO.getStringValue("name")); //
				} else {
					_oldVOs[i].setAttributeValue(_newItemName, ""); //
				}
			}
		}
	}

	/**
	 * Ϊÿ�����������������������
	 * @param _oldVOs
	 * @param _newItem
	 * @param _fromItemKey 
	 * @param _newCorpType ����������ȡֵ:
	 * bl_zhonghbm �������в���,һ������
	 * bl_fengh ��������,һ������
	 * bl_fenghbm �������в���
	 * bl_zhih ����֧��
	 * bl_shiyb ������ҵ��,һ������
	 * bl_shiybfb ������ҵ���ֲ�
	 * @throws Exception
	 */
	public void leftOuterJoin_CorpBelongCorpName(HashVO[] _oldVOs, String _newItem, String _fromItemKey, String _belongCorpType) throws Exception {
		leftOuterJoin_CorpBelongCorpName(_oldVOs, new String[] { _newItem }, _fromItemKey, new String[] { _belongCorpType }); //
	}

	/**
	 * Ϊÿ��HashVO�����µĻ�������,������������,������ҵ����,�������в��ŵ�,���и���������ʱ��Щ����ֻ��ͬʱ����һ��,��ʱ�ͻ���ּ�¼����Ϊ�յ����!!
	 * @param _oldVOs
	 * @param _newItems
	 * @param _fromItemKey
	 * @param _newCorpType ����������ȡֵ:
	 * bl_zhonghbm �������в���,һ������
	 * bl_fengh ��������,һ������
	 * bl_fenghbm �������в���
	 * bl_zhih ����֧��
	 * bl_shiyb ������ҵ��,һ������
	 * bl_shiybfb ������ҵ���ֲ�
	 */
	public void leftOuterJoin_CorpBelongCorpName(HashVO[] _oldVOs, String[] _newItems, String _fromItemKey, String[] _belongCorpTypes) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_corp_dept"); //���ҳ����л���,���뻺��,Ϊ���������
		HashMap map_allCorp = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			map_allCorp.put(hvs[i].getStringValue("id"), hvs[i]); //
		}

		String str_bl_corp_id = null; //
		for (int j = 0; j < _oldVOs.length; j++) {
			String str_corpId = _oldVOs[j].getStringValue(_fromItemKey); //����ID
			if (str_corpId == null || str_corpId.trim().equals("")) {
				for (int k = 0; k < _newItems.length; k++) {
					_oldVOs[j].setAttributeValue(_newItems[k], ""); //
				}
			} else {
				HashVO hvo_corp = (HashVO) map_allCorp.get(str_corpId); //ȥ��������!!!
				if (hvo_corp == null) { //���û�ҵ�
					for (int k = 0; k < _newItems.length; k++) {
						_oldVOs[j].setAttributeValue(_newItems[k], ""); //
					}
				} else { //����ҵ�!!
					for (int k = 0; k < _newItems.length; k++) {
						str_bl_corp_id = hvo_corp.getStringValue(getCorpDBFileName(_belongCorpTypes[k])); //ȡ�������������͵�IDֵ!!!
						if (str_bl_corp_id == null || str_bl_corp_id.trim().equals("")) {
							_oldVOs[j].setAttributeValue(_newItems[k], ""); //
						} else {
							HashVO hvo_bl_corp = (HashVO) map_allCorp.get(str_bl_corp_id); //
							_oldVOs[j].setAttributeValue(_newItems[k], hvo_bl_corp.getStringValue("name", "")); //
						}
					}
				}
			}
		}
	}

	/**
	 * ���ݻ������ͷ������ݿ���ʵ������
	 * @param _corpType
	 * @return
	 */
	private String getCorpDBFileName(String _corpType) {
		return _corpType; //
	}

	/**
	 * ��һ��HashVO����ת����ͼ��VO
	 * @param _hvs
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] hvs) {
		String[] str_prodtype = null;
		String[] str_months = null;
		String[][] str_initdata = new String[hvs.length][3]; //
		HashMap hname = new HashMap();
		HashMap hyear = new HashMap();
		for (int i = 0; i < hvs.length; i++) {
			str_initdata[i][1] = hvs[i].getStringValue(0); //
			str_initdata[i][0] = hvs[i].getStringValue(1); //
			str_initdata[i][2] = hvs[i].getStringValue(2); //
			hyear.put(hvs[i].getStringValue(0), null);
			hname.put(hvs[i].getStringValue(1), null);
		}

		str_prodtype = (String[]) hname.keySet().toArray(new String[0]);
		str_months = (String[]) hyear.keySet().toArray(new String[0]);

		new TBUtil().sortStrs(str_prodtype); //����һ��
		new TBUtil().sortStrs(str_months); //����һ��,�����1����,��2����,��3����...

		BillChartItemVO[][] ld_data = new BillChartItemVO[str_prodtype.length][str_months.length]; //
		for (int i = 0; i < str_prodtype.length; i++) {
			for (int j = 0; j < str_months.length; j++) {
				for (int k = 0; k < str_initdata.length; k++) {
					if (str_prodtype[i].equals(str_initdata[k][0]) && str_months[j].equals(str_initdata[k][1])) {
						Object cellValue = hvs[k].getObjectValue(2); //
						if (cellValue instanceof Double) { //�����Double����
							ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
						} else if (cellValue instanceof String) {
							ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
						} else if (cellValue instanceof Integer) {
							ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
						} else if (cellValue instanceof BillChartItemVO) {
							ld_data[i][j] = (BillChartItemVO) cellValue; //
						} else {
							ld_data[i][j] = new BillChartItemVO(-77777);
						}
						break; //
					}
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_prodtype);
		chartVO.setYSerial(str_months);
		chartVO.setDataVO(ld_data);
		return chartVO; //
	}

	public String getOracleLongInSqlCondition(String[] itemvalues, String itemkey) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //��ǰ���û�Session...
		// 'id1','id2','id3'
		///ƴ��insert���
		List<String> sqlList = new ArrayList<String>();
		if (itemvalues != null) {
			for (int i = 0; i < itemvalues.length; i++) {
				StringBuilder sbsql = new StringBuilder("insert into pub_sqlincons (sessionid,itemkey,incolumn) values (");
				sbsql.append("'");
				sbsql.append(sessionid);
				sbsql.append("','");
				sbsql.append(itemkey);
				sbsql.append("','");
				sbsql.append(itemvalues[i]);
				sbsql.append("')");
				sqlList.add(sbsql.toString());
			}
			System.out.println("�� [" + sqlList.size() + " ]");
			commDMO.executeBatchByDS(null, sqlList); //�����ύ,����ᷢ���ȴ�...
		}
		return "select incolumn from pub_sqlincons where sessionid='" + sessionid + "' and itemkey='" + itemkey + "'";
	}

	/**
	 * 
	 * @param _map
	 * @param itemkey
	 * @param _dbItemKey
	 * @return
	 * @throws Exception
	 */
	public String getOracleLongInSqlCondition(HashMap _map, String itemkey, String _dbItemKey) throws Exception {

		String str_queryCondition = (String) _map.get(itemkey); //
		if (str_queryCondition == null) {
			return " 2=2 "; //
		}
		TBUtil tbUtil = new TBUtil(); //
		String[] str_items = tbUtil.split(str_queryCondition, ";"); //
		CommDMO commDMO = new CommDMO(); //
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //��ǰ���û�Session...
		// 'id1','id2','id3'
		///ƴ��insert���
		List<String> sqlList = new ArrayList<String>();
		if (str_items != null) {
			for (int i = 0; i < str_items.length; i++) {
				StringBuilder sbsql = new StringBuilder("insert into pub_sqlincons (sessionid,itemkey,incolumn) values (");
				sbsql.append("'");
				sbsql.append(sessionid);
				sbsql.append("','");
				sbsql.append(itemkey);
				sbsql.append("','");
				sbsql.append(str_items[i]);
				sbsql.append("')");
				sqlList.add(sbsql.toString());
			}
			System.out.println("�� [" + sqlList.size() + " ]");
			commDMO.executeBatchByDS(null, sqlList); //�����ύ,����ᷢ���ȴ�...
		}
		return _dbItemKey + " in (select incolumn from pub_sqlincons where sessionid='" + sessionid + "' and itemkey='" + itemkey + "')";
	}

	/**
	 * ȡ�ô�ӡģ��
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception {
		String str_sql_1 = "select * from pub_printtemplet where templetcode='" + _templetCode + "'";
		HashVO[] hvs_1 = new CommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1 == null || hvs_1.length == 0) {
			throw new WLTAppException("û���ҵ���ӡģ��[" + _templetCode + "]"); //
		}

		PubPrintTempletVO templetVO = new PubPrintTempletVO();
		templetVO.setId(hvs_1[0].getLognValue("id")); //
		templetVO.setTempletcode(hvs_1[0].getStringValue("templetcode")); //
		templetVO.setTempletname(hvs_1[0].getStringValue("templetname")); //

		String str_sql_2 = "select * from pub_printtemplet_itembands where parentid='" + hvs_1[0].getLognValue("id") + "'";
		HashVO[] hvs_2 = new CommDMO().getHashVoArrayByDS(null, str_sql_2); //
		PubPrintItemBandVO[] itemBandVOs = new PubPrintItemBandVO[hvs_2.length]; //
		for (int i = 0; i < itemBandVOs.length; i++) {
			itemBandVOs[i] = new PubPrintItemBandVO(); //
			itemBandVOs[i].setItemkey(hvs_2[i].getStringValue("itemkey")); //
			itemBandVOs[i].setItemname(hvs_2[i].getStringValue("itemname")); //
			itemBandVOs[i].setX(hvs_2[i].getDoubleValue("x").doubleValue()); //
			itemBandVOs[i].setY(hvs_2[i].getDoubleValue("y").doubleValue()); //
			itemBandVOs[i].setWidth(hvs_2[i].getIntegerValue("width").intValue()); //
			itemBandVOs[i].setHeight(hvs_2[i].getIntegerValue("height").intValue()); //

			itemBandVOs[i].setFonttype(hvs_2[i].getStringValue("fonttype") == null ? "System" : hvs_2[i].getStringValue("fonttype")); //
			itemBandVOs[i].setFontsize(hvs_2[i].getIntegerValue("fontsize") == null ? 10 : hvs_2[i].getIntegerValue("fontsize").intValue()); //
			itemBandVOs[i].setFontstyle(hvs_2[i].getIntegerValue("fontstyle") == null ? 0 : hvs_2[i].getIntegerValue("fontstyle").intValue()); //

			itemBandVOs[i].setHalign(hvs_2[i].getIntegerValue("halign") == null ? 1 : hvs_2[i].getIntegerValue("halign").intValue()); //����λ��
			itemBandVOs[i].setValign(hvs_2[i].getIntegerValue("valign") == null ? 1 : hvs_2[i].getIntegerValue("valign").intValue()); //����λ��
			itemBandVOs[i].setLayer(hvs_2[i].getIntegerValue("layer") == null ? 100 : hvs_2[i].getIntegerValue("layer").intValue()); //���

			itemBandVOs[i].setForeground(hvs_2[i].getStringValue("foreground") == null ? "000000" : hvs_2[i].getStringValue("foreground")); //ǰ����ɫ
			itemBandVOs[i].setBackground(hvs_2[i].getStringValue("background") == null ? "FFFFFF" : hvs_2[i].getStringValue("background")); //������ɫ

			itemBandVOs[i].setShowBorder(hvs_2[i].getBooleanValue("isshowborder") == null ? false : hvs_2[i].getBooleanValue("isshowborder").booleanValue()); //
			itemBandVOs[i].setShowBaseline(hvs_2[i].getBooleanValue("isshowbaseline") == null ? false : hvs_2[i].getBooleanValue("isshowbaseline").booleanValue()); //�Ƿ���ʾ����
		}

		templetVO.setItemBandVOs(itemBandVOs); //

		return templetVO; //����ģ��VO..
	}

	/**
	 * ����ģ��
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception {
		String str_sql = "select itemkey,itemname from pub_templet_1_item where pk_pub_templet_1 =(select pk_pub_templet_1 from pub_templet_1 where templetcode='" + _billtempletCode + "') and cardisshowable='Y' order by showorder"; //
		CommDMO commdmo = new CommDMO();
		HashVO[] hvs = commdmo.getHashVoArrayByDS(null, str_sql); //
		if (hvs == null || hvs.length == 0) {
			throw new WLTAppException("û���ҵ�����ģ��[" + _billtempletCode + "]"); //
		}
		String templetid = commdmo.getStringValueByDS(null, "select id from pub_printtemplet where templetcode='" + _templetCode + "'");
		Vector v_sqls = new Vector(); //
		v_sqls.add("delete from pub_printtemplet_itembands where parentid =" + templetid);

		for (int i = 0; i < hvs.length; i++) {
			String str_newid = new CommDMO().getSequenceNextValByDS(null, "s_pub_printtemplet_itembands"); //

			int li_row = i / 4; //
			int li_pos = i % 4; //

			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_printtemplet_itembands");
			sb_sql.append("(");
			sb_sql.append("id,");
			sb_sql.append("parentid,");
			sb_sql.append("itemkey,");
			sb_sql.append("itemname,");
			sb_sql.append("x,");
			sb_sql.append("y,");
			sb_sql.append("width,");
			sb_sql.append("height,");
			sb_sql.append("fonttype,");
			sb_sql.append("fontsize,");
			sb_sql.append("fontstyle,");
			sb_sql.append("halign,");
			sb_sql.append("valign,");
			sb_sql.append("layer,");
			sb_sql.append("isshowborder,");
			sb_sql.append("isshowbaseline");
			sb_sql.append(")");
			sb_sql.append(" values ");
			sb_sql.append("(");
			sb_sql.append(str_newid + ",");
			sb_sql.append(templetid + ",");
			sb_sql.append("'" + hvs[i].getStringValue("itemkey") + "',");
			sb_sql.append(hvs[i].getStringValue("itemname") == null ? "null," : ("'" + hvs[i].getStringValue("itemname") + "',"));
			sb_sql.append((li_pos * 200) + ",");
			sb_sql.append((li_row * 20) + ",");
			sb_sql.append("200,");
			sb_sql.append("20,");
			sb_sql.append("'System',");
			sb_sql.append("'10',");
			sb_sql.append("'0',");
			sb_sql.append("2,");
			sb_sql.append("1,");
			sb_sql.append("100,");
			sb_sql.append("'N',");
			sb_sql.append("'N'");
			sb_sql.append(")");
			v_sqls.add(sb_sql.toString()); //
		}

		new CommDMO().executeBatchByDS(null, v_sqls); //
	}

	/**
	 * �����ӡģ�嶨��
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception {
		String str_sql_1 = "select id from pub_printtemplet where templetcode='" + _templetcode + "'";
		HashVO[] hvs_1 = new CommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1 != null && hvs_1.length > 0) {
			String str_parentid = hvs_1[0].getStringValue("id"); //
			Vector v_sqls = new Vector();
			v_sqls.add("delete  from pub_printtemplet_itembands where parentid='" + str_parentid + "'");

			for (int i = 0; i < _itemBandVOs.length; i++) {
				String str_newid = new CommDMO().getSequenceNextValByDS(null, "s_pub_printtemplet_itembands"); //

				StringBuffer sb_sql = new StringBuffer(); //
				sb_sql.append("insert into pub_printtemplet_itembands");
				sb_sql.append("(");
				sb_sql.append("id,");
				sb_sql.append("parentid,");
				sb_sql.append("itemkey,");
				sb_sql.append("itemname,");
				sb_sql.append("x,");
				sb_sql.append("y,");
				sb_sql.append("width,");
				sb_sql.append("height,");
				sb_sql.append("fonttype,");
				sb_sql.append("fontsize,");
				sb_sql.append("fontstyle,");
				sb_sql.append("halign,");
				sb_sql.append("valign,");
				sb_sql.append("layer,");
				sb_sql.append("foreground,"); //ǰ����ɫ
				sb_sql.append("background,"); //������ɫ
				sb_sql.append("isshowborder,");
				sb_sql.append("isshowbaseline");
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				sb_sql.append(str_newid + ",");
				sb_sql.append(str_parentid + ",");
				sb_sql.append("'" + _itemBandVOs[i].getItemkey() + "',");
				sb_sql.append(_itemBandVOs[i].getItemname() == null ? "null," : "'" + _itemBandVOs[i].getItemname() + "',");
				sb_sql.append(_itemBandVOs[i].getX() + ",");
				sb_sql.append(_itemBandVOs[i].getY() + ",");
				sb_sql.append(_itemBandVOs[i].getWidth() + ",");
				sb_sql.append(_itemBandVOs[i].getHeight() + ",");
				sb_sql.append("'" + _itemBandVOs[i].getFonttype() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getFontsize() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getFontstyle() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getHalign() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getValign() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getLayer() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getForeground() + "',"); //ǰ����ɫ
				sb_sql.append("'" + _itemBandVOs[i].getBackground() + "',"); //������ɫ
				if (_itemBandVOs[i].isShowBorder()) {
					sb_sql.append("'Y',"); //
				} else {
					sb_sql.append("'N',"); //
				}

				if (_itemBandVOs[i].isShowBaseline()) {
					sb_sql.append("'Y'"); //
				} else {
					sb_sql.append("'N'"); //
				}

				sb_sql.append(")");
				v_sqls.add(sb_sql.toString()); //
			}
			new CommDMO().executeBatchByDS(null, v_sqls); //
		}
	}

	/**
	 * ����ģ������ɾ����ӡģ��
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOnePrintTemplet(String _id) throws Exception {
		new CommDMO().executeBatchByDS(null, new String[] { "delete from pub_printtemplet where id=" + _id, "delete  from pub_printtemplet_itembands where parentid=" + _id });
	}
}
