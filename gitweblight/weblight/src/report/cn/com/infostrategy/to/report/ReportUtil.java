package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.RefItemVO;

/**
 * ��������.. �ṩһЩ����Ĺ��߷���,��Ϊ�ͻ���,�������˶����õ�,���Է�������.
 * 
 * @author xch
 * 
 */
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * ���ݲ�ѯ���е�����ƴ��in����!! �����շ��ص����Էֺŷָ���ַ���,Ȼ��ƴ��һ��where����.
	 * 
	 * @param _map
	 * @param _queryItemKey
	 * @param _dbItemKey
	 * @return
	 */
	public String getWhereConditionByInType(HashMap _map, String _queryItemKey, String _dbItemKey) {
		String str_queryCondition = (String) _map.get(_queryItemKey); //
		if (str_queryCondition == null) {
			return " 2=2 "; //
		}
		TBUtil tbUtil = new TBUtil(); //
		String[] str_items = tbUtil.split(str_queryCondition, ";"); //
		String str_inCondition = "";
		//		if (str_items.length < 700) {
		//			str_inCondition = tbUtil.getInCondition(str_items); //
		//		} else {
		str_inCondition = this.getInCondition(str_items, _dbItemKey); //
		return str_inCondition;
		//		}
		//		return _dbItemKey + " in (" + str_inCondition + ") "; //
	}

	/**
	 * 
	 * @return
	 */
	public String getInCondition(String[] _sqllist, String _dbItemKey) {
		if (_sqllist == null || _sqllist.length == 0) {
			return "'-99999'"; //
		} else {
			StringBuffer str_return = new StringBuffer();
			// ��һ��ֵ��ʱ��
			if (_sqllist.length == 1) {
				if ("".endsWith(_sqllist[0].trim()))
					return str_return.append("2=2").toString();
				else
					return str_return.append(_dbItemKey + " in ( '" + _sqllist[0].trim() + "' )").toString();
			}
			// ���ʱ��
			str_return.append(_dbItemKey + " in (");
			for (int i = 0; i < _sqllist.length; i++) {
				if (_sqllist[i] != null && !((String) _sqllist[i]).trim().equals("")) {
					if (i == _sqllist.length - 1) {
						str_return.append("'" + (String) _sqllist[i] + "')");
						continue;
					}
					//					if ((i + 1) % 700 == 0)
					//						str_return.append("'" + (String) _sqllist[i] + "') or " + _dbItemKey + " in ( "); // ���
					//					else
					str_return.append("'" + (String) _sqllist[i] + "',"); // ���
				}
			}

			return str_return.toString();
		}
	}

	/**
	 * ����ѯ���е��еĲ�ѯ�����ReplaceAll�ķ�ʽƴ��,����һ�����ڵ�.
	 * �ڲ����е�RefItemVO����һ��HashVO,������һ��querycondition
	 * ,������Լ���õ�itemkey,�����е�itemkey�滻���������ֶ�!
	 * 
	 * @param _map
	 * @param _queryItemKey
	 * @param _dbItemKey
	 * @return
	 */
	public String getWhereConditionByReplaceTYpe(HashMap _map, String _queryItemKey, String _dbItemKey) {
		RefItemVO refItemVO = (RefItemVO) _map.get("obj_" + _queryItemKey); // �Ӳ�ѯ����ȡ������...
		if (refItemVO == null) { // �����ѯ����û�����Ӧ���ֶ�,����ô��?��һ���鲻�����ǲ������?������Ϊ�ǲ������!
			return " 2=2 "; //
		}

		HashVO hvo = refItemVO.getHashVO(); //
		if (hvo == null) {
			return " 2=2 "; //
		}

		String str_queryCondition_macro = hvo.getStringValue("querycondition"); //
		if (str_queryCondition_macro == null) {
			return " 2=2 "; //
		}

		String str_queryCondition = new TBUtil().replaceAll(str_queryCondition_macro, "{itemkey}", _dbItemKey); //
		return " " + str_queryCondition + " "; //
	}

	/**
	 * �з���,��һ���б��е�ĳ��ֵ,����зֺ�,����з���!��ɶ���!
	 * @param _list
	 * @param _colName
	 * @return
	 */
	private ArrayList rowSplit(ArrayList _list, String _colName) {
		ArrayList al_newVOList = new ArrayList(); //�µ��б�,��Խ��Խ�Ӵ󣡣�
		for (int i = 0; i < _list.size(); i++) { //�������н��!
			HashVO itemVO = (HashVO) _list.get(i); //
			String str_value = itemVO.getStringValue(_colName); //
			Set<String> set = new HashSet<String>();//Ԭ����20120911���  ��Ҫȥ���ظ�
			//������µ����⣬Ʃ��һ����¼�� ҵ�����߶�����������������ҵ��;���ÿ�ҵ��;��˾�Ŵ�ҵ��;  Ȼ�������µ�ҵ������һ����Ϊ����ҵ��;����ҵ��;����ҵ��; 
			//����ȡ��ʱ����������ȡ��ʾ��������һ�£�ȥ��֮��ֻ��ʾ����ҵ��
			String[] str = (null != str_value && !str_value.equals("")) ? str_value.split(";") : null;
			for (int j = 0; j < str.length; j++) {
				set.add(str[j]);
			}
			String str_temp = "";
			for (String s : set) {
				str_temp += s + ";";
			}
			String[] str_items = tbUtil.split(str_temp, ";"); //���м�λ!
			for (int j = 0; j < str_items.length; j++) { //������ֵ!
				HashVO cloneHVO = itemVO.clone(); //�ȿ�¡һ��!ÿһ���ָ����Ϳ�¡һ�£�
				cloneHVO.setAttributeValue(_colName, str_items[j]); //������¡�Ķ����ֵ�Ƿ��Ѻ��ֵ!!!
				al_newVOList.add(cloneHVO); //����
			}
		}
		return al_newVOList; //
	}

	/**
	 * @param _hvs
	 * @param _groupFunc ���麯��,���硾count(�����¼�) �����¼�,sum(������ʧ) ������ʧ��
	 * @param _groupField,����ѡ���ά��!��ͷ,��ͷ�ϵ�ά��! ���硾ҵ������,��������,���յȼ���           .
	 * @return ���ط���ͳ�ƺ������
	 */
	public HashVO[] groupHashVOs(HashVO[] _initHvs, String _groupFunc, String _groupFields) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			//System.out.println("_groupFunc=[" + _groupFunc + "],_groupFields[" + _groupFields + "]"); //�����_groupFunc=[count(�����¼�) �����¼�,sum(������ʧ) ������ʧ],_groupFields[ҵ������,���յȼ�]
			// �ȷ���һ�²���������,����:[ҵ������,���յȼ�]
			String[] str_groupFields = tbUtil.split(_groupFields, ","); //
			for (int i = 0; i < str_groupFields.length; i++) {
				str_groupFields[i] = str_groupFields[i].trim(); //ȥ���ո�
			}

			//�������ڷ����ֵ���зֺŵ����,���Դ���һ���з��ѵ����,������ѡ���ά���е�ֵ�Ƿ�����и�ά���е�ֵ�зֺţ������,������з��ѣ����û��,����ԭ���ģ�
			//��������ܵĽ���������ѡ�������ά��,����Ȼ������,ͳ�ƽ��Ҳ��100��,���ѡ�����ά�����ӱ�,����Ȼ����,���Ƕ�̬���ܵ�!!
			//���ѵ�ԭ�������ҳ�������¼�ļ���ά�ȴ��дӸ�ֵ,Ȼ�������Щά��,ʹ��һ��List�����Ĳ������ţ���һ���ȷ��ѵ�һ��ά��,�ڶ��η��ѵڶ���ά��,ÿ�ζ���ԭ����List�ļ�¼���ű�ά������λ�ı�������
			ArrayList al_rowSplitVOs = new ArrayList(); //�з��ѵ�
			if (_initHvs != null) {//������Ҫ�ж�һ�£�����ᱨ��ָ���쳣�����/2014-02-21��
				for (int i = 0; i < _initHvs.length; i++) {
					ArrayList al_needRowSplitGroups = new ArrayList(); //��Ҫ�����з��ѵ���!
					for (int j = 0; j < str_groupFields.length; j++) { //��������ά��
						String str_groupItemValue = _initHvs[i].getStringValue(str_groupFields[j]); //
						if (str_groupItemValue != null) {
							if (tbUtil.split(str_groupItemValue, ";").length > 1) { //�������1��Ҫ���з���
								al_needRowSplitGroups.add(str_groupFields[j]); //��������,��ֻ����Ҫ���ѵĽ��з���!�Ӷ��������!	
							} else { //���û�ж��,��ȥ���ֺ�!��Ϊ��ʱ��ʵ��һ������ǰ���и��ֺţ����硾;����Ժ;����;�������Ժ;��
								_initHvs[i].setAttributeValue(str_groupFields[j], tbUtil.replaceAll(str_groupItemValue, ";", "")); //
							}
						}
					}
					if (al_needRowSplitGroups.size() > 0) { //������ڷ���!
						ArrayList al_tmp = new ArrayList(); //��ʱ��List,�������Ϸ���,�����Ӵ�!!
						al_tmp.add(_initHvs[i]); //�ȼ���!
						for (int k = 0; k < al_needRowSplitGroups.size(); k++) { //��������ά��,һ����ά�Ƚ��з���!
							al_tmp = rowSplit(al_tmp, (String) al_needRowSplitGroups.get(k)); //��VO����ȥ,Ȼ�󷵻��µ�List,Ȼ�������µ�List�ͽ�ȥ!!����ѭ��,��һ�ν���һ��ά�ȷ��Ѵ�����,�ڶ��ν��ڶ���ά�ȴ�����,�����ν�������ά�ȴ�����!
							//System.out.println("��[" + (i + 1) + "]��ά�ȷ��Ѻ�Ľ������С��[" + al_tmp.size() + "]"); //
						}
						//�Ժ�������ڵ��������ӱ����ӱ�֮����Լ����ϵ,���ڷ��Ѻ�,�����﷨����,�޳��������ڵ����,���硾����1<15>,����2<16>��,������Լ����ϵ����һ��Map,Ȼ���ټ�����һ�Σ����ǲ���������Map�еľ��޳���������
						//ʱ���ϵ��ʱ�����������Ժ�����չ����������xch/2012-08-11��

						al_rowSplitVOs.addAll(al_tmp); //���뵽���Ҫ���صĽ������
						al_tmp.clear(); //�����ʱList�е�ֵ!
					} else { //���û�з���!��ֱ�Ӽ��룡
						al_rowSplitVOs.add(_initHvs[i]); //
					}
				}
			}

			HashVO[] detalVOs = (HashVO[]) al_rowSplitVOs.toArray(new HashVO[0]); //��ǿת��VO����!!!��������������Ժ��������!!�����ܱ�ԭ��λ����!
			//this.tbUtil.writeHashToHtmlTableFile(detalVOs, "C:/kkk125.htm"); //����ʱ�������

			//�ȷ���һ�������﷨,���м���α��,���磺count(�����¼�) �����¼�,sum(������ʧ) ������ʧ
			String[] str_items = tbUtil.split(_groupFunc, ","); // ����,�ָ�..
			String[] str_funnames1 = new String[str_items.length]; // ������
			String[] str_funpars1 = new String[str_items.length]; // ��������
			String[] str_boguscolsa = new String[str_items.length]; // α����
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = str_items[i].trim(); // ��ȥ����β�ո�
				int li_pos_1 = str_items[i].indexOf("("); // ��������
				int li_pos_2 = str_items[i].indexOf(")"); // ��������
				str_funnames1[i] = str_items[i].substring(0, li_pos_1).trim(); // ������,���磺count,max,sum,avg
				str_funpars1[i] = str_items[i].substring(li_pos_1 + 1, li_pos_2).trim(); //��������,���磺�����¼�,������ʧ
				str_boguscolsa[i] = str_items[i].substring(li_pos_2 + 1, str_items[i].length()).trim(); //������α������!
				//System.out.println("������[" + str_funnames1[i] + "],��������[" + str_funpars1[i] + "],α��[" + str_boguscolsa[i] + "]"); //
			}

			LinkedHashSet hst_realNumField = new LinkedHashSet(); //��Ϊ��ȫ�п�����ͬһ������ֶν���sum,avg,percentSum����,Ϊ�����Ч��,�������Ψһ�Լ���!
			for (int i = 0; i < str_funpars1.length; i++) {
				hst_realNumField.add(str_funpars1[i]); //
			}
			String[] str_realFieldNumfields = (String[]) hst_realNumField.toArray(new String[0]); //

			//�����ļ���!!
			//��ǰ���߼�����,�µ��߼��ĳ�,��Զ��Count����,����ֻ��һ��!����ͬʱ���ٷֱ��������������ڻ��ڽ���ֶεļ���,�򲻹����߶�ʮһ��ͳͳ��sum,avg,percent,max,min���������
			LinkedHashMap map_rowdata = new LinkedHashMap(); //���������洢����ϲ�������ݵ�Map���󣡣�
			for (int i = 0; i < detalVOs.length; i++) { //����������ϸ����!
				StringBuffer sb_rowvalue = new StringBuffer(); //
				for (int j = 0; j < str_groupFields.length; j++) { //�����������ʵ��ֵ,���磺ҵ������,���յȼ���
					String str_itemvalue = detalVOs[i].getStringValue(str_groupFields[j]); //ʵ��ֵ
					//����зָ�����
					sb_rowvalue.append(str_itemvalue + "#"); //ƴ�ӣ�����
				}
				String real_data = detalVOs[i].getStringValue(str_realFieldNumfields[0]); //Ԭ����  ʵ��ֵ

				if (map_rowdata.containsKey(sb_rowvalue.toString())) { //����Ѿ�����..
					HashVO hvo_group = (HashVO) map_rowdata.get(sb_rowvalue.toString()); //   ����ȡ��֮ǰ��ֵ

					//��������α��,Ҳ���Ǽ�����
					for (int j = 0; j < str_realFieldNumfields.length; j++) {
						//String str_boguscolname = str_boguscols[j]; //α������
						if (detalVOs[i].getAttributeValue("groupextendfields") != null) { //���ָ��������,������ǲ�ѯ�߼���һ�����ӱ������ѯ,�������һЩ�ֶλ��ظ����ֶ��!�������࣡��ʱ����ֻ����һ�Σ�����������ֶζ��������е�,��ֻ��һ�Σ�
							ArrayList al_extendFields = (ArrayList) detalVOs[i].getAttributeValue("groupextendfields"); //����ؼ���ֻ�ڱ���ķ���leftOuterJoinNewHashVO()�в����룡����
							boolean bo_extendcontains_groupfield = true; //�жϷ����ֶ��Ƿ��������ֶ���,Ĭ����Ϊ��!
							for (int q = 0; q < str_groupFields.length; q++) { //
								if (!al_extendFields.contains(str_groupFields[q])) { // ֻҪ��һ�����������ֶ���,��Ͳ�������!!
									bo_extendcontains_groupfield = false; // ��ʾ������
									break; // �����˳�,���Ч��,��Ϊ�Ѿ�֪����������..
								}
							}
							boolean bo_extendcontains_computefield = al_extendFields.contains(str_realFieldNumfields[j]); //�����ֶ����Ƿ���������ֶ�
							if (bo_extendcontains_groupfield) {
								if (bo_extendcontains_computefield) {
									continue; // ���߶��������ֶ���,��ֻ��һ��
								} else {
									// ������������ͳ���ӱ�,�����Ҫ��,������Ǵ���.
								}
							} else {
								if (bo_extendcontains_computefield) {
									// ���ӱ����,ͳ������,���������,
									// �����,���ܺͼ�����������������ܺ�
									// �������,����ӱ��еĲ��ּ�¼����ƽ!��Ϊ��֪��ƾʲô�������ֵ�����ӱ��A��¼�ж�������B��¼��!!
									// Ŀǰ���ǲ��õ�����!! �Ժ���Լ��������������!!!
								} else {
									// ���߶����������ֶ���,�����Ҫ��,������Ǵ���
								}
							}
						}
						if (null != str_funnames1 && str_funnames1[0].trim().equals("init")) { //Ԭ�������  ��ʾ��ʼֵ  20130312
							String str_key_count = str_realFieldNumfields[j] + "��init"; //
							String bdold_count = hvo_group.getStringValue(str_key_count); //ԭ��������
							hvo_group.setAttributeValue(str_key_count, bdold_count + ";" + real_data); //��ֵ����ֵƴ����һ��
							//sum����!   ��������ԭʼֵҲҪ�ϼƵĹ������Ǽ��ϴ˹���  Ԭ����  20130319
							String str_key_sum = str_realFieldNumfields[j] + "��sum"; //
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //��ǰֵ
							BigDecimal bdold_sum = hvo_group.getBigDecimalValue(str_key_sum); //ԭ��������
							hvo_group.setAttributeValue(str_key_sum, bdold_sum.add(bd_thisItem == null ? new BigDecimal(0) : bd_thisItem)); //sum���!!
						} else {
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //��ǰֵ

							//Count����!
							String str_key_count = str_realFieldNumfields[j] + "��count"; //
							BigDecimal bdold_count = hvo_group.getBigDecimalValue(str_key_count); //ԭ��������
							hvo_group.setAttributeValue(str_key_count, bdold_count.add(new BigDecimal(1))); //count��Զ��1

							//sum����!
							String str_key_sum = str_realFieldNumfields[j] + "��sum"; //
							BigDecimal bdold_sum = hvo_group.getBigDecimalValue(str_key_sum); //ԭ��������
							hvo_group.setAttributeValue(str_key_sum, bdold_sum.add(bd_thisItem == null ? new BigDecimal(0) : bd_thisItem)); //sum���!!

							//max����
							String str_key_max = str_realFieldNumfields[j] + "��max"; //
							BigDecimal bdold_max = hvo_group.getBigDecimalValue(str_key_max); //ԭ��������
							if (bdold_max == null) { //���ԭ��Ϊ��
								if (bd_thisItem != null) { //�����ֵ��Ϊ��!
									hvo_group.setAttributeValue(str_key_max, bd_thisItem);
								}
							} else { //���ԭ����Ϊ��
								if (bd_thisItem != null && bd_thisItem.compareTo(bdold_max) > 0) { //�����ֵ
									hvo_group.setAttributeValue(str_key_max, bd_thisItem); //�û�
								}
							}

							//min����!
							String str_key_min = str_realFieldNumfields[j] + "��min"; //
							BigDecimal bdold_min = hvo_group.getBigDecimalValue(str_key_min); //ԭ��������
							if (bdold_min == null) {
								if (bd_thisItem != null) { //�����ֵ��Ϊ��!
									hvo_group.setAttributeValue(str_key_min, bd_thisItem);
								}
							} else { //�����ֵ��Ϊ��,
								if (bd_thisItem != null && bd_thisItem.compareTo(bdold_min) < 0) { // �����ǰ����С�ھ�����,�򽫵�ǰ�����û�..
									hvo_group.setAttributeValue(str_key_min, bd_thisItem); //�û�
								}

							}
						}
					}

					//�����ӵĽ�id�Զ�װ��һ����#value��
					String ids = (String) hvo_group.getAttributeValue("#value");
					if (detalVOs[i].getStringValue("id") != null) {
						if (ids == null) {
							hvo_group.setAttributeValue("#value", detalVOs[i].getStringValue("id"));
						} else {
							hvo_group.setAttributeValue("#value", ids + ";" + detalVOs[i].getStringValue("id"));
						}
					}
				} else { //����ǵ�һ�β��룡��
					//TBUtil.getTBUtil().writeHashToHtmlTableFile(detalVOs, "C:/datas.htm"); //����ļ����´��������ӵ�����ʲô!!
					HashVO hvo_group = new HashVO(); // �������
					for (int j = 0; j < str_groupFields.length; j++) { //
						String str_itemvalue = detalVOs[i].getStringValue(str_groupFields[j]); //
						hvo_group.setAttributeValue(str_groupFields[j], str_itemvalue); // �Ƚ�����ͳ�Ƶ�������
					}

					//��������α��....
					for (int j = 0; j < str_realFieldNumfields.length; j++) {
						if (null != str_funnames1 && str_funnames1[0].trim().equals("init")) { //Ԭ�������  ��ʾ��ʼֵ  20130312
							String bd_thisItem = detalVOs[i].getStringValue(str_realFieldNumfields[j]);
							String str_key_count = str_realFieldNumfields[j] + "��init"; //
							hvo_group.setAttributeValue(str_key_count, bd_thisItem); //
						} else {
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //��ǰֵ

							String str_key_count = str_realFieldNumfields[j] + "��count"; //
							hvo_group.setAttributeValue(str_key_count, new BigDecimal(1)); //

							String str_key_sum = str_realFieldNumfields[j] + "��sum"; //
							hvo_group.setAttributeValue(str_key_sum, bd_thisItem == null ? new BigDecimal(0) : bd_thisItem); //

							String str_key_max = str_realFieldNumfields[j] + "��max"; ///
							hvo_group.setAttributeValue(str_key_max, bd_thisItem); //

							String str_key_min = str_realFieldNumfields[j] + "��min"; ////
							hvo_group.setAttributeValue(str_key_min, bd_thisItem); //
						}

					}

					//�������������ID
					if (detalVOs[i].getStringValue("id") != null) {
						hvo_group.setAttributeValue("#value", detalVOs[i].getStringValue("id")); //�����id�ֶ�,���⴦��!
					}

					map_rowdata.put(sb_rowvalue.toString(), hvo_group); //
					Object[] objs = map_rowdata.values().toArray(); //���н������!!!
					/*for(int k=0;k<objs.length;k++){
						System.out.println("objs====================="+objs[k]);
					}
					
					*/
				}
			}

			Object[] objs = map_rowdata.values().toArray(); //���н������!!!
			HashVO[] returnVOs = new HashVO[objs.length]; //
			for (int i = 0; i < returnVOs.length; i++) {
				returnVOs[i] = (HashVO) objs[i];
			}
			//��ǰ������һ��δ������м����е��߼�,����������������ں�̨û��Ū,��Ϊ����֪��ѡ���ά����ʲô!������ʱ����֪��ά��ֵ����ĸ���,����ֻ����ǰ̨ȡ!
			//���Զ��Ƶ���ǰ̨!��xch/2012-08-24��
			return returnVOs; //
		} catch (java.lang.NumberFormatException ex) {
			throw new WLTAppException("�����������һ���������ֵ�ֵ,������������ͻ���,����һ��ֵȴ�Ǹ�Ӣ����ĸ!"); //
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * ΪhashVO[]���渽��һЩ���ݣ����ڲ���ĳ�������ά�ȣ����޶�˳��
	 * 
	 * @param _hvs
	 * @param _itemkey
	 * @param _types
	 * @return
	 */
	public HashVO[] superadd(HashVO[] _hvs, String _itemkey, String[] _types) {
		ArrayList<HashVO> hashVOList = new ArrayList<HashVO>();

		for (int i = 0; i < _hvs.length; i++) {
			hashVOList.add(_hvs[i]);
		}

		HashVO newHashVO = null;
		for (int i = 0; i < _types.length; i++) {
			newHashVO = new HashVO();
			newHashVO.setAttributeValue(_itemkey, _types[i]);
			hashVOList.add(newHashVO);
		}

		return hashVOList.toArray(new HashVO[hashVOList.size()]);

	}

	/**
	 * ��һ��HashVO����ת����ͼ��VO ���HashVO����ֻ������,ǰ����������ά��,��������ֵ.
	 * 
	 * @param _hvs
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, HashMap sortMap, String[][] computeFunAndFields) {//Ԭ����20121108��ӵ�������������ʾͼ���պ��ַ�ʽ�����㣬֮ǰ��ֻ֧�ְ���count������
		if (_hvs == null || _hvs.length == 0) {
			return null;
		}
		String[] str_key = null;
		for (int i = 0; i < _hvs.length; i++) {
			str_key = _hvs[i].getKeys(); //
			if (_hvs[i].getStringValue(str_key[0]) == null) {
				_hvs[i].setAttributeValue(str_key[0], ""); //
			}

			if (_hvs[i].getStringValue(str_key[1]) == null) {
				_hvs[i].setAttributeValue(str_key[1], ""); //
			}
		}
		if (str_key.length == 2) { // �ҳ�����ά�ȵ�Ψһ��ֵ..
			String[] str_XSerial = null; //
			HashMap map_x = new HashMap();
			for (int i = 0; i < _hvs.length; i++) {
				map_x.put(_hvs[i].getStringValue(0), null); //

			}
			str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
			new TBUtil().sortStrs(str_XSerial); // Ĭ�ϰ���ĸ˳��..
			BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][1]; //
			for (int i = 0; i < str_XSerial.length; i++) {
				for (int k = 0; k < _hvs.length; k++) { // ȥ��������λ��
					if (str_XSerial[i].equals(_hvs[k].getStringValue(0))) { //
						Object cellValue = _hvs[k].getObjectValue(1); //
						if (cellValue instanceof Double) { // �����Double����
							ld_data[i][0] = new BillChartItemVO((Double) cellValue); //
						} else if (cellValue instanceof String) {
							ld_data[i][0] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
						} else if (cellValue instanceof Integer) {
							ld_data[i][0] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
						} else if (cellValue instanceof BillChartItemVO) {
							ld_data[i][0] = (BillChartItemVO) cellValue; //
						} else if (cellValue instanceof BigDecimal) {
							ld_data[i][0] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
						} else {
							ld_data[i][0] = new BillChartItemVO(-77777);
						}
						ld_data[i][0].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
						break;
					}
				}
			}

			BillChartVO chartVO = new BillChartVO(); //
			chartVO.setXSerial(str_XSerial); //
			chartVO.setYSerial(new String[] { " " }); //
			chartVO.setDataVO(ld_data);
			return chartVO; //
		} else if (str_key.length > 2) {
			// ���Ƚ����п�ֵ�ĳɿ��ַ���,�����������׳��ֿ�ָ���쳣!

			// �ҳ�����ά�ȵ�Ψһ��ֵ..
			String[] str_XSerial = null; //
			String[] str_YSerial = null; //
			HashMap map_x = new HashMap();
			HashMap map_y = new HashMap();
			for (int i = 0; i < _hvs.length; i++) {
				map_x.put(_hvs[i].getStringValue(0), null); //
				map_y.put(_hvs[i].getStringValue(1), null); //
			}
			str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
			str_YSerial = (String[]) map_y.keySet().toArray(new String[0]);
			if (sortMap != null && sortMap.size() > 0) {
				String keys[] = _hvs[0].getKeys(); //key1 key2 ������
				String[] sort1 = (String[]) sortMap.get(keys[0]);
				String[] sort2 = (String[]) sortMap.get(keys[1]);
				if (sort1 != null) {
					new TBUtil().sortStrsByOrders(str_XSerial, sort1); //
				} else {
					new TBUtil().sortStrs(str_XSerial); //
				}
				if (sort2 != null) {
					new TBUtil().sortStrsByOrders(str_YSerial, sort2); //	
				} else { //û������ִ��Ĭ������
					new TBUtil().sortStrs(str_YSerial);
				}
			} else {
				// Ĭ������..
				new TBUtil().sortStrs(str_XSerial); //
				new TBUtil().sortStrs(str_YSerial); //	
			}
			BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][str_YSerial.length]; //
			for (int i = 0; i < str_XSerial.length; i++) {
				for (int j = 0; j < str_YSerial.length; j++) {
					for (int k = 0; k < _hvs.length; k++) { // ȥ��������λ��
						if (str_XSerial[i].equals(_hvs[k].getStringValue(0)) && str_YSerial[j].equals(_hvs[k].getStringValue(1))) { //
							Object cellValue = null;
							if (null == computeFunAndFields) {
								cellValue = _hvs[k].getObjectValue(2); // 20121108Ԭ�������ģ�����֮ǰ���߼�
							} else {
								cellValue = _hvs[k].getObjectValue(computeFunAndFields[0][2] + "��" + computeFunAndFields[0][0].toLowerCase()); //Ԭ����20121108��ӵ�������������ʾͼ���պ��ַ�ʽ�����㣬֮ǰ��ֻ֧�ְ���count������
							}
							if (cellValue instanceof Double) { // �����Double����
								ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
							} else if (cellValue instanceof String) {
								ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
							} else if (cellValue instanceof Integer) {
								ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
							} else if (cellValue instanceof BillChartItemVO) {
								ld_data[i][j] = (BillChartItemVO) cellValue; //
							} else if (cellValue instanceof BigDecimal) {
								ld_data[i][j] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
							} else {
								ld_data[i][j] = new BillChartItemVO(-77777);
							}
							ld_data[i][j].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
							break; // �ҵ��ͷ���.
						}
					}
				}
			}

			BillChartVO chartVO = new BillChartVO(); //
			chartVO.setXSerial(str_XSerial); //
			chartVO.setYSerial(str_YSerial); //
			chartVO.setDataVO(ld_data);
			return chartVO; //
		}
		return null;
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs) {
		return this.convertHashVOToChartVO(_hvs, null, null);//Ԭ����20121108��ӵ�������������ʾͼ���պ��ַ�ʽ�����㣬֮ǰ��ֻ֧�ְ���count������
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, String _XSerial, String _YSerial, String _needSelectCol, String[] _xSortItems, String[] _ySortItems) {
		return convertHashVOToChartVO(_hvs, _XSerial, _YSerial, _needSelectCol, _xSortItems, _ySortItems, false, false); //
	}

	/**
	 * ��һ��HashVO����ת����ͼ��VO
	 * 
	 * @param _hvs
	 * @param _XSerial
	 *            ,���������������,���缾��
	 * @param _YSerial
	 *            ,����������������,�������
	 * @param _needSelectCol
	 *            ��Ҫͳ�Ƶ���
	 * @param _isZeroReportType
	 *            �Ƿ���㱨����,��ν��㱨���ƾ��������ֶ��д��ڵĶ���������������Ƿ���ʵ�������д��ڹ��������û�з���ҲҪ���������ķ�ʽ���
	 *            !!
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, String _XSerial, String _YSerial, String _needSelectCol, String[] _xSortItems, String[] _ySortItems, boolean _isXZeroReportType, boolean _isYZeroReportType) {
		if (_hvs == null || _hvs.length == 0) {
			return null;
		}

		// �ҳ�����ά�ȵ�Ψһ��ֵ..
		HashMap map_x = new HashMap();
		HashMap map_y = new HashMap();// ��ʵ����������ά��2
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue(_XSerial) == null) {
				_hvs[i].setAttributeValue(_XSerial, "��ֵ."); //
			}
			if (_hvs[i].getStringValue(_YSerial) == null) {
				_hvs[i].setAttributeValue(_YSerial, "��ֵ."); //
			}
			map_x.put(_hvs[i].getStringValue(_XSerial), ""); //
			map_y.put(_hvs[i].getStringValue(_YSerial), ""); //
		}

		String[] str_XSerial = (String[]) map_x.keySet().toArray(new String[0]); //
		String[] str_YSerial = (String[]) map_y.keySet().toArray(new String[0]); //

		// Ĭ������..
		TBUtil tbUtil = new TBUtil();
		if (_xSortItems == null) { // ���û��ָ����������,��ֻ��ʵ�����ݽ��а���ĸ����
			tbUtil.sortStrs(str_XSerial); //
		} else {
			if (_isXZeroReportType) { // �������㱨����,����кϲ�����
				str_XSerial = spanArrayByZeroReport(str_XSerial, _xSortItems); //
			} else {
				tbUtil.sortStrsByOrders(str_XSerial, _xSortItems); //
			}
		}

		if (_ySortItems == null) {
			tbUtil.sortStrs(str_YSerial); //
		} else {
			if (_isYZeroReportType) { // �������㱨����,����кϲ�����
				str_YSerial = spanArrayByZeroReport(str_YSerial, _ySortItems); //
			} else {
				tbUtil.sortStrsByOrders(str_YSerial, _ySortItems); //
			}
		}

		BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][str_YSerial.length]; //
		for (int i = 0; i < str_XSerial.length; i++) {
			for (int j = 0; j < str_YSerial.length; j++) {
				for (int k = 0; k < _hvs.length; k++) { // ȥ��������λ��
					if (str_XSerial[i].equals(_hvs[k].getStringValue(_XSerial)) && str_YSerial[j].equals(_hvs[k].getStringValue(_YSerial))) { //
						if (_needSelectCol != null && _hvs[k].getObjectValue(_needSelectCol) != null) {
							Object cellValue = _hvs[k].getObjectValue(_needSelectCol); //
							if (cellValue instanceof Double) { // �����Double����
								ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
							} else if (cellValue instanceof String) {
								ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
							} else if (cellValue instanceof Integer) {
								ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
							} else if (cellValue instanceof BillChartItemVO) {
								ld_data[i][j] = (BillChartItemVO) cellValue; //
							} else if (cellValue instanceof BigDecimal) {
								ld_data[i][j] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
							} else {
								ld_data[i][j] = new BillChartItemVO(-77777);
							}
						} else {
							ld_data[i][j] = new BillChartItemVO(0); // ���û�ҵ�,����Ϊ��0
						}
						break; // �ҵ��ͷ�����,�ڿ�ֵ�������,���ܻ�������,��Ϊ��������Ψ��ֵ��ͬ�������ֻ��һ����¼���ǶԵ�!!....
					}
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_XSerial); //
		chartVO.setYSerial(str_YSerial); //
		chartVO.setDataVO(ld_data);
		return chartVO; //
	}

	private String[] spanArrayByZeroReport(String[] _realDatas, String[] _sortDatas) {
		ArrayList al_data = new ArrayList(); //
		for (int i = 0; i < _sortDatas.length; i++) {
			if (_sortDatas[i] != null) {
				al_data.add(_sortDatas[i]); //
			}
		}

		for (int i = 0; i < _realDatas.length; i++) {
			if (_realDatas[i] != null && !isItemExistInArray(_realDatas[i], _sortDatas)) { // ���ʵ�������е�ĳһ�����ָ�����������д��ڹ���,������,��֮�����ȥ,����Span����!!!
				al_data.add(_realDatas[i]); //
			}
		}
		return (String[]) al_data.toArray(new String[0]); //
	}

	private boolean isItemExistInArray(String _item, String[] _array) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null && _array[i].equalsIgnoreCase(_item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��HashVO[]���Ƴ�һЩ��!
	 * 
	 * @param _hvs
	 * @param _itemkey
	 */
	public void removeHashVOItems(HashVO[] _hvs, String _itemkey) {
		new TBUtil().removeHashVOItems(_hvs, _itemkey); //
	}

	/**
	 * ��HashVO[]���Ƴ�һЩ��!
	 * 
	 * @param _hvs
	 * @param _itemkey
	 */
	public void removeHashVOItems(HashVO[] _hvs, String[] _itemkeys) {
		new TBUtil().removeHashVOItems(_hvs, _itemkeys); //
	}

	/**
	 * �滻HashVO[]�е�ĳһ�е�ֵ..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _oldValue
	 *            ��ֵ
	 * @param _newValue
	 *            ��ֵ
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String _oldValue, String _newValue, String _others) {
		new TBUtil().replaceAllHashVOsItemValue(_hvs, _fromItemKey, _oldValue, _newValue, _others); //
	}

	/**
	 * �滻HashVO[]�е�ֵ..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _replaceValue
	 *            ,��n��2�е��ַ�������. ���� new String[][]{{"Y","����"},{"N","δ����"}}
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String[][] _replaceValue) {
		new TBUtil().replaceAllHashVOsItemValue(_hvs, _fromItemKey, _replaceValue); //
	}

	/**
	 * ��������һ��SQL���ɵ�HashVO[],Ȼ���±���н��ں���.. �ǳ�����,������ʱ��Ҫ����ʱ�Ϳ�����
	 * 
	 * @param _oldVOs
	 * @param _sql
	 * @param _oldfield
	 * @param _newfield
	 * @return
	 */
	public HashVO[] leftOuterJoinNewHashVO(HashVO[] _oldVOs, HashVOStruct _newHVO, String _oldfield, String _newfield) {
		ArrayList al_result = new ArrayList(); //
		String[] str_newVOKeys = _newHVO.getHeaderName(); // �õ�����key
		HashVO[] init_newVOs = _newHVO.getHashVOs(); //

		// Ϊ���������,Ҫ��Ŀ��HashVO����һ��,��ֻ��Ҫ����Դ���ݹ��ϵ���Щ��¼,�����ϵ�ͳͳ�ӵ�!!Ϊ��������һ��,�Ƚ�Դ�����е�Distinctһ��,�ҳ�Ψһ�Ե�ֵ,���������ֻ����Щ!
		HashMap map_olddistinct = new HashMap(); //
		for (int i = 0; i < _oldVOs.length; i++) {
			if (_oldVOs[i].getStringValue(_oldfield) != null && !_oldVOs[i].getStringValue(_oldfield).equals("")) {
				map_olddistinct.put(_oldVOs[i].getStringValue(_oldfield), null); //
			}
		}

		// �ٱ�����ֵ,�ҳ����ϵ�,�Բ��ϵ�ͳͳ�ӵ�!
		ArrayList al_filter = new ArrayList(); //
		for (int i = 0; i < init_newVOs.length; i++) {
			String str_newitemvalue = init_newVOs[i].getStringValue(_newfield);
			if (str_newitemvalue != null && map_olddistinct.containsKey(str_newitemvalue)) { // ��������ݵ�Ψһ�Թ����а�������,�����,
				al_filter.add(init_newVOs[i]); //
			}
		}
		HashVO[] filter_newVOs = (HashVO[]) al_filter.toArray(new HashVO[0]); //
		for (int i = 0; i < _oldVOs.length; i++) { // �ӱ����ɵĿ�ʼ
			if (!_oldVOs[i].containsKey("groupextendfields")) {
				_oldVOs[i].setAttributeValue("groupextendfields", null); // ���Ȳ���,��������html����ʱ��������
			}

			String[] str_oldallkeys = _oldVOs[i].getKeys(); // �����ݵ�����key
			String str_oldvalue = _oldVOs[i].getStringValue(_oldfield); //
			if (str_oldvalue == null) {
				str_oldvalue = "";
				_oldVOs[i].setAttributeValue(_oldfield, "");
			}

			boolean isLinkSuccess = false;
			int li_linkedchildcount = 0; //
			for (int j = 0; j < filter_newVOs.length; j++) { // �����µ�VO���˺��,��������������Ĺ����Ѿ�������,���絥�ݱ��еĴ�����ֻ��������,���±���Ȼ����Ա����2������¼,���������˺�����ֻ��������.
				String str_newvalue = filter_newVOs[j].getStringValue(_newfield); //
				if (str_newvalue == null) {
					str_newvalue = ""; //
					filter_newVOs[j].setAttributeValue(_newfield, "");
				}

				if (str_oldvalue.equals(str_newvalue)) { // ����ܹ���
					li_linkedchildcount++; // ����Ʒ������,��������ӱ��϶��,��˵�������ݵ�!!
					isLinkSuccess = true; // ��ʾ�ѹ���...

					// �ȿ�¡һ��,����Ҫ���п�¡����,��ΪJava���ڴ����õ�,��������!!
					HashVO hvo_clone = _oldVOs[i].deepClone(); // �ȿ�¡һ��,��ֻҪ�ҵ�һ���ͽ������¡һ��,�����ͻ���ֽ������¼���ݵ����!!

					// �����ж��Ƿ��ǵ�2����ʼ�����ݵ�,��������ڸü�¼���ȼ�¼���������ݵ��ֶ�
					if (li_linkedchildcount > 1) { // ����ǵڶ������Ϲ����ϵ�,��˵�������ݵ�!!��Ҫ�ڸü�¼��ָ��ǰ��������������ݵ�!!�����������count,sumʱ�Ų������!!��һ������Ҫ!!������ʱ�����ӱ�����ͻ���,��ͳ�������ֶ�!
						ArrayList al_extendKeys = new ArrayList(); //
						al_extendKeys.addAll(Arrays.asList(str_oldallkeys)); // �����о����ݵ�key����
						hvo_clone.setAttributeValue("groupextendfields", al_extendKeys); // ��һ���ֵܹ����Ƽ�¼�����������ֶ�
					}

					for (int k = 0; k < str_newVOKeys.length; k++) {
						hvo_clone.setAttributeValue(str_newVOKeys[k], filter_newVOs[j].getStringValue(str_newVOKeys[k])); // ����
					}
					al_result.add(hvo_clone); //
				}
			}

			if (!isLinkSuccess) { // ������ӱ���ûƷ����,������һȦû���ּ�¼,��ѭ���ھ�VO�ϲ��Ͽյ��µĸ���,�������ܱ�֤,���ص�HashVO��һ�������ı��,����������еļ�¼������!!
				HashVO hvo_clone = _oldVOs[i].deepClone(); // �ȿ�¡һ��,��ֻҪ�ҵ�һ���ͽ������¡һ��,�����ͻ���ֽ������¼���ݵ����!!
				for (int k = 0; k < str_newVOKeys.length; k++) {
					hvo_clone.setAttributeValue(str_newVOKeys[k], ""); // ����
				}
				al_result.add(hvo_clone); // �����¡��ֵ!
			}
		}

		HashVO[] hvs_return = (HashVO[]) al_result.toArray(new HashVO[0]); //
		return hvs_return; //
	}

	/**
	 * ��һ��HashVO��������һ������,�����ǻ���ĳ�����н������ڴ���,���ظ���������,��,��
	 * 
	 * @param _hvs
	 * @param _itemkey
	 * @param _newitemkey
	 * @param _datetype
	 */
	public void leftOuterJoin_YSMDFromDateTime(HashVO[] _hvs, String _newitemkey, String _itemkey, String _datetype) {
		try {
			for (int i = 0; i < _hvs.length; i++) {
				String str_datetime = _hvs[i].getStringValue(_itemkey, ""); //
				if (null == str_datetime || "".equals(str_datetime) || "null".equals(str_datetime)) {//������Ҫ�жϿմ�����ֻ�ǿ�ֵ�����/2012-03-13�� �������жϲ�Ϊnull���Ҳ�����null�ַ���  ��Ԭ����/2012-09-12��
					_hvs[i].setAttributeValue(_newitemkey, ""); //
				} else {

					String str_newdatetypeVaue = str_datetime; //
					if (_datetype.equals("��")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "��"; //
					} else if (_datetype.equals("��")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "��" + getSeason(str_datetime.substring(5, 7)) + "����"; //
					} else if (_datetype.equals("��")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "��" + str_datetime.substring(5, 7) + "��"; //
					} else if (_datetype.equals("��")) {
						str_newdatetypeVaue = str_datetime.substring(0, 10);
					}
					_hvs[i].setAttributeValue(_newitemkey, str_newdatetypeVaue); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��һ��HashVO����һ������,�������ڻ���ԭ���������ϵ��������ڵ��������!!
	 * 
	 * @param _hvs
	 * @param _beginItemKey
	 * @param _endItemKey
	 * @param _newItemName
	 */
	public void leftOuterJoin_DaysBetweenTwoDate(HashVO[] _hvs, String _newItemName, String _beginItemKey, String _endItemKey) {
		try {
			for (int i = 0; i < _hvs.length; i++) {
				Date date_begin = _hvs[i].getDateValue(_beginItemKey); //
				Date date_end = _hvs[i].getDateValue(_endItemKey); //
				if (date_begin == null || date_end == null) {
					_hvs[i].setAttributeValue(_newItemName, ""); //
				} else {
					long ll_betweenDays = (date_end.getTime() - date_begin.getTime()) / (24 * 60 * 60 * 1000);
					_hvs[i].setAttributeValue(_newItemName, "" + ll_betweenDays); //
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����һ������,��������ַ�Χ,����ֵ������3,5,100,300,���Ҫ���������<10,10-100,200-500,>500������
	 * 
	 * @param _hvs
	 *            HashVO[]����
	 * @param _newItemName
	 *            ��������
	 * @param _fromItemName
	 *            ���ĸ���ȡ��
	 * @param _numberArea
	 *            ����
	 */
	public void leftOuterJoin_NumberAreaFromDouble(HashVO[] _hvs, String _newItemName, String _fromItemName, String[] _numberArea) {
		for (int i = 0; i < _hvs.length; i++) {
			Double number = _hvs[i].getDoubleValue(_fromItemName); //
			if (number != null) {
				_hvs[i].setAttributeValue(_newItemName, parseNumber(number, _numberArea));
			} else {
				_hvs[i].setAttributeValue(_newItemName, "��ֵ");
			}
		}
	}

	private String parseNumber(Double number, String[] area) {
		String condition = "";
		for (int i = 0; i < area.length; i++) {
			try {
				if (area[i] == null) {
					continue;
				}
				if (area[i].startsWith("<=")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf("<=") + 1));
					if (number <= scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith("<")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf("<") + 1));
					if (number < scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith(">=")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf(">=") + 1));
					if (number >= scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith(">")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf(">") + 1));
					if (number > scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].indexOf("-") > 0 && !area[i].startsWith("-") && !area[i].endsWith("-")) {
					String[] scopes = area[i].split("-");
					Double begin = Double.parseDouble(scopes[0]);
					Double end = Double.parseDouble(scopes[1]);
					if (number >= begin && number <= end) {
						condition = area[i];
						break;
					}
				}
			} catch (Exception e) {
				WLTLogger.getLogger(ReportUtil.class).error("�����ַ���Ϊ����ʱ��������", e); //
				// e.printStackTrace(); //
				continue;
			}
		}
		return condition;
	}

	/**
	 * ������HashVO�������кϲ�,
	 * 
	 * @param hvs
	 * @param hvs_sum_1
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 */
	public void leftOuterJoin_ColumnSpan(HashVO[] _oldHashVO, String _newItemKey, HashVO[] _newHashVO, String _oldLinkField, String _newLinkField, String _fromItemKey) {
		HashMap map_newData = new HashMap(); //
		for (int i = 0; i < _newHashVO.length; i++) {
			map_newData.put(_newHashVO[i].getStringValue(_newLinkField), _newHashVO[i].getStringValue(_fromItemKey)); // �Թ����ֶε�ֵ��Ϊ����,��������ȡ��ֵ
		}

		String str_oldLinkFieldValue = null; //
		String str_newGetFieldBalue = null; //
		for (int i = 0; i < _oldHashVO.length; i++) {
			str_oldLinkFieldValue = _oldHashVO[i].getStringValue(_oldLinkField); //
			if (str_oldLinkFieldValue == null) {
				_oldHashVO[i].setAttributeValue(_newItemKey, ""); //
			} else {
				str_newGetFieldBalue = (String) map_newData.get(str_oldLinkFieldValue); // ȥ�µı���ȡ,����û��,�����,�������,�����µ�����
				_oldHashVO[i].setAttributeValue(_newItemKey, str_newGetFieldBalue); //
			}
		}
	}

	/**
	 * ��һ��HashVO���������붨����ʾ��
	 */
	public void reOrderAndShowColumns(HashVO[] _oldHashVO, String[] _reOrderColumns) {
		if (_oldHashVO == null) {
			return;
		}

		HashVO[] newHashVOs = new HashVO[_oldHashVO.length]; //
		for (int i = 0; i < newHashVOs.length; i++) { //
			newHashVOs[i] = new HashVO(); //
			for (int j = 0; j < _reOrderColumns.length; j++) {
				newHashVOs[i].setAttributeValue(_reOrderColumns[j], _oldHashVO[i].getObjectValue(_reOrderColumns[j])); //
			}
		}

		// һ��Ҫ���¿�¡һ��,����ûЧ��!!!
		for (int i = 0; i < _oldHashVO.length; i++) { //
			_oldHashVO[i] = newHashVOs[i].deepClone(); //
		}
	}

	private String getSeason(String _month) {
		if (_month.equals("01") || _month.equals("02") || _month.equals("03")) {
			return "1";
		} else if (_month.equals("04") || _month.equals("05") || _month.equals("06")) {
			return "2";
		} else if (_month.equals("07") || _month.equals("08") || _month.equals("09")) {
			return "3";
		} else if (_month.equals("10") || _month.equals("11") || _month.equals("12")) {
			return "4";
		}
		return "";
	}

	/**
	 * ������SQL�еĵ�2�и��µ�1��Ʒ���ϵ�
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @throws Exception
	 */
	public void allOneFieldNameFromOtherTable(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql) throws Exception {
		HashMap custStateMap = getHashMapDataBysql(_sql); // ""
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "��ֵ"); // findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = new TBUtil().split(str_custids, ";"); // �ָ�һ��!
				for (int j = 0; j < str_items.length; j++) {
					String str_itemName = (String) custStateMap.get(str_items[j]); //
					if (str_itemName != null) {
						str_convertname = str_convertname + str_itemName + ";"; //
					} else {
						System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����ֵ��" + str_custids + "�������" + str_items[j] + "����SQL��" + _sql + "����û������ֵ������"); //
					}
				}
				String str_temp = "";
				if (str_convertname.lastIndexOf(";") >= 0) {//Ԭ�����޸�  20120911    �������ж����Ϊ�ջᱨ����Խ��
					str_temp = str_convertname.substring(0, str_convertname.lastIndexOf(";"));
				} else {
					str_temp = "";
				}
				_hvs[i].setAttributeValue(_newItemName, str_temp); // findchannel
			}
		}
	}

	/**
	 * ���ͽṹ,ȡ�ڼ��������
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @param _id
	 * @param _name
	 * @param _parentid
	 * @param _level
	 * @throws Exception
	 */
	public void allOneFieldNameFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level) throws Exception {
		HashMap mapTemp = new ReportDMO().getTreeStruct(_sql); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "��ֵ"); // findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = new TBUtil().split(str_custids, ";"); //
				for (int j = 0; j < str_items.length; j++) {
					String[] str_treePathNames = (String[]) mapTemp.get(str_items[j]); //
					if (str_treePathNames == null || str_treePathNames.length == 0) {
						System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����ֵ��" + str_custids + "�������" + str_items[j] + "����SQL��" + _sql + "����ȡ�ڡ�" + _level + "����ʱû������ֵ������"); //
					} else {
						if (str_treePathNames.length > _level) {
							// if
							// (str_convertname.indexOf(str_treePathNames[_level]
							// + ";") < 0) { //
							str_convertname = str_convertname + str_treePathNames[_level] + ";"; //
							// }
						} else {
							System.err.println("���͹���ʱ,�ֶΡ�" + _fromOldItemName + "����ֵ��" + str_custids + "�������" + str_items[j] + "����SQL��" + _sql + "����ȡ�ڡ�" + _level + "����ʱԽ�磡����"); //
						}
					}
				}
				//�еĲ㼶���ܲ�ͳһ����Ϊ�˲����������ж�һ�¡����/2016-03-21��
				if (str_convertname.contains(";")) {
					_hvs[i].setAttributeValue(_newItemName, str_convertname.substring(0, str_convertname.lastIndexOf(";"))); // 
				} else {
					_hvs[i].setAttributeValue(_newItemName, str_convertname); // 
				}
			}
		}
	}

	private HashMap getHashMapDataBysql(String _sql) throws Exception {
		String[][] str_data = new CommDMO().getStringArrayByDS(null, _sql); //
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < str_data.length; i++) {
			returnMap.put(str_data[i][0], str_data[i][1]); //
		}
		return returnMap;
	}

	/**
	 * �����ڴ�Demo����!������������ʱ����Ҫ�����ڴ�����!�����������������!
	 * @param _valueMap ����������,Ҳ�����ַ���,����:
	 * map.put("ҵ������",new String[]{"����ҵ��","��˾ҵ��","�м�ҵ��"});
	 * map.put("����ʱ��֮����","2012�꼾��;2012��2����");
	 * @param _records �����������¼,����1000
	 * @return
	 */
	public HashVO[] createDemoMemoryData(HashMap _valueMap, int _records) {
		String[] str_keys = (String[]) _valueMap.keySet().toArray(new String[0]); //
		java.util.Random rans = new java.util.Random(); //�����������!
		TBUtil tbUtil = new TBUtil(); //
		ArrayList hvoList = new ArrayList(); //
		for (int i = 0; i < _records; i++) { //�������м�¼!
			HashVO hvoItem = new HashVO(); //��������VO
			hvoItem.setAttributeValue("id", i + 1); //
			for (int j = 0; j < str_keys.length; j++) {
				Object obj = _valueMap.get(str_keys[j]); //ȡ��ֵ,����������,�����ַ���!
				if (obj != null) { //�����Ϊ��!
					String[] str_items = null; //
					if (obj.getClass().isArray()) { //��������Ǹ�����!��ǿת
						str_items = (String[]) obj; //		
					} else { //�����������,������ַ����ָ�
						str_items = tbUtil.split(((String) obj), ";"); //�ָ�
					}
					String str_value = str_items[rans.nextInt(str_items.length)]; //���ֵ!
					hvoItem.setAttributeValue(str_keys[j], str_value); //
				} else {
					hvoItem.setAttributeValue(str_keys[j], "����ֵ��"); //
				}
			}
			hvoList.add(hvoItem); //
		}
		return (HashVO[]) hvoList.toArray(new HashVO[0]); //����
	}

}
