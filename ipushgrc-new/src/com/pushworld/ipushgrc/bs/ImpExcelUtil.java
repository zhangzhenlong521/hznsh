package com.pushworld.ipushgrc.bs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ����excel�Ĺ�����
 * @author hm
 *
 */
public class ImpExcelUtil {
	/**
	 * ��������-wdl
	 */
		public String[][] InputInfo(String[][] str_content, String remarks,String template_id, String type) {
			Map map = new HashMap();// �洢KEY
			List list_error = new LinkedList();
			List list = new LinkedList();
			String tablename = null;
			List new_list = new LinkedList();
			Connection conn = null;
			PreparedStatement psmt = null;
			PreparedStatement psmt_check = null;
			ResultSet rs = null;
			String check_dept = null;//���Ŷ�ӦID
			boolean check_tree = false;//�Ƿ����parentid
			String tree_namekey=null;//���νṹ�����ֶ���
			String tree_codekey=null;//���νṹ�����ֶ���
			String[][] str_error = null;
			Map map_oldkey = new HashMap();// �洢KEY
			/**
			 * ��EXCEL����ȡ�����ݽ������ƴ���LIST
			 */
			try {
				conn = new WLTDBConnection().getJDBCConnection();
				psmt = conn.prepareStatement("select itemkey from  pub_templet_1_item where itemname=? and pk_pub_templet_1=?");
				for (int j = 0; j < str_content[0].length; j++) {
					if (str_content[0][j] != null && !"".equals(str_content[0][j])) {// ���в�Ϊ��ʱ
						psmt.setString(1, str_content[0][j]);
						psmt.setInt(2, Integer.parseInt(template_id));
						rs = psmt.executeQuery();
						while (rs.next()) {
							if (rs.getString(1).equals("parentid")) {// �ж���Ҫ����������Ƿ������νṹ
								check_tree = true;
							}
							if(type.equals("tree")){ //�������νṹʱ��¼���������ֶ���
								if(str_content[0][j].indexOf("����")!=-1){
									tree_namekey=rs.getString(1);
								}
								if(str_content[0][j].indexOf("����")!=-1){
									tree_codekey=rs.getString(1);
								}
							}else if(type.equals("list")){//���Ǳ��ͽṹʱ������ڲ��ż�¼�����ֶ���
								if (str_content[0][j].indexOf("����") != -1) {
									check_dept = rs.getString(1);
								}
							}
							map_oldkey.put(rs.getString(1), str_content[0][j]);// ��¼��ǰ��ͷ������
							str_content[0][j] = rs.getString(1);// ����ͷ���Ƹ�Ϊ���ݿ����ֶ���
						}
					}
				}
				for (int i = 0; i < str_content.length; i++) {
					HashVO ho = null;
					if (i != 0) {
						ho = new HashVO();
					}
					for (int j = 0; j < str_content[i].length; j++) {
						if (str_content[0][j] != null && !"".equals(str_content[0][j])) {// ���в�Ϊ��ʱ
							if (i == 0) {// ��Ϊ��һ��
								System.out.println(str_content[0][j] + " ");
								map.put(str_content[0][j], "?");
							} else {
								ho.setAttributeValue(str_content[0][j],str_content[i][j]);// �洢VALUE
							}
						}
					}
					if (ho != null) {// ���������ݲ�Ϊ���Ǽ���LIST
						list.add(ho);
					}
				}
				if (remarks != null && !"".equals(remarks)) {// ����ע������Ϊ��
					Map map_remarks = new HashMap();
					String[] str = remarks.split(";;;");
					for (int i = 0; i < str.length; i++) {
						String key = str[i].substring(0, str[i].indexOf('='));
						String value = str[i].substring(str[i].indexOf('=') + 1);
						map.put(key, "?");// ����KEY
						map_remarks.put(key, value);
					}
					String[] keySet = (String[]) map_remarks.keySet().toArray(
							new String[0]);
					for (int j = 0; j < list.size(); j++) {// ѭ������LIST�洢��VALUE
						HashVO ho = (HashVO) list.get(j);
						for (int n = 0; n < keySet.length; n++) {
							ho.setAttributeValue(keySet[n], map_remarks
									.get(keySet[n]));
						}
					}
				}
				psmt = conn.prepareStatement("select savedtablename from  pub_templet_1 where pk_pub_templet_1=?");
				psmt.setInt(1, Integer.parseInt(template_id));
				rs = psmt.executeQuery();
				while (rs.next()) {
					tablename = rs.getString(1);
				}

				/**
				 * ����LIST
				 */
				if (type.equals("tree")&&check_tree) {// ��������νṹ
					Map map_parentid=new HashMap();
					int num=1;
					HashVO[] vos=(HashVO[]) list.toArray(new HashVO[0]);
					String rootname=vos[0].getStringValue(tree_namekey);
					for(int i=0;i<vos.length;i++){
						if(rootname.equals(vos[i].getStringValue("parentid"))){
							num++;
						}
					}
					List[] allList=new LinkedList[num];
					List parentidList=new LinkedList();
					CommDMO comm = new CommDMO();
					int m=0;
					String rootid="";
					for(int i=0,j=0;i<vos.length&&j<allList.length;i++){
						if(rootname.equals(vos[i].getStringValue("parentid"))){
							List newList=new LinkedList();
							HashMap parentidMap=new HashMap();
							for(int k=m;k<i;k++){
								if(vos[k].getStringValue(tree_namekey)!=null&&!"".equals(vos[k].getStringValue(tree_namekey))){
									newList.add(vos[k]);
									String str_countsid = comm.getSequenceNextValByDS(null, "s_pub_corp_dept");
									vos[k].setAttributeValue(tree_codekey, str_countsid);
									if(m==0){
										rootid=str_countsid;
									}else{
								        parentidMap.put(rootname,rootid);
									}
									parentidMap.put(vos[k].getStringValue(tree_namekey), str_countsid);
								}
							}
							parentidList.add(parentidMap);
							allList[j]=newList;
							m=i;
							j++;
							
						}	
						
					}
					List last_newList=new LinkedList();
					HashMap last_parentidMap=new HashMap();
					for(int k=m;k<vos.length;k++){
						if(vos[k].getStringValue(tree_namekey)!=null&&!"".equals(vos[k].getStringValue(tree_namekey))){
							last_newList.add(vos[k]);
							String str_countsid = comm.getSequenceNextValByDS(null, "s_pub_corp_dept");
							vos[k].setAttributeValue(tree_codekey, str_countsid);
							if(m==0){
								rootid=str_countsid;
							}else{
								last_parentidMap.put(rootname,rootid);
							}
							last_parentidMap.put(vos[k].getStringValue(tree_namekey), str_countsid);
						}
					}
					parentidList.add(last_parentidMap);
					allList[allList.length-1]=last_newList;
					for(int i=0;i<allList.length;i++){
						HashMap _map=(HashMap) parentidList.get(i);
						for(int j=0;j<allList[i].size();j++){
							HashVO ho=(HashVO)(allList[i].get(j));
							if("".equals(ho.getStringValue("parentid"))){
								ho.setAttributeValue("parentid", null);
							}else{
								ho.setAttributeValue("parentid", _map.get(ho.getAttributeValue("parentid")));
							}
						}
						Insert_InputInfo(map, allList[i], tablename, tree_codekey);
					}
				} else if(type.equals("list")&&!check_tree) {
					/**
					 * У��LIST��Ϣ
					 */
					if (check_dept != null && !"".equals(check_dept)) {// ���в����ֶ�ʱ
						psmt_check = conn.prepareStatement("select id from  pub_corp_dept where name=?  ");
						for (int i = 0; i < list.size(); i++) {
							int dept_id = -1;
							HashVO ho = (HashVO) list.get(i);
							String deptname = (String) ho
									.getAttributeValue(check_dept);
							psmt_check.setString(1, deptname);
							rs = psmt_check.executeQuery();
							while (rs.next()) {
								dept_id = rs.getInt(1);
							}
							if (dept_id != -1) {
								ho.setAttributeValue(check_dept, dept_id);// ���������Ƹ�ΪID
								new_list.add(ho);
							} else {
								list_error.add(ho);
							}
						}
					}
					if (new_list.size() != 0) {
						Insert_InputInfo(map, new_list, tablename,tree_codekey);
					} else {
						Insert_InputInfo(map, list, tablename,tree_codekey);
					}
				}else{
					str_error=new String[1][1];
					str_error[0][0]="error";
					return str_error;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (psmt != null) {
					try {
						psmt.close();
					} catch (final SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (final SQLException e) {
						e.printStackTrace();
					}
				}
			}
			/**
			 * ��δ������Ϣת���ɶ�λ����
			 */
			if (list_error.size() != 0) {
				String[] oldkey = (String[]) map_oldkey.keySet().toArray(new String[0]);
				str_error = new String[list_error.size() + 1][oldkey.length];
				for (int i = 0; i < str_error.length; i++) {
					for (int j = 0; j < str_error[i].length; j++) {
						if (i == 0) {
							str_error[i][j] = (String) map_oldkey.get(oldkey[j]);
						} else {
							str_error[i][j] = (String) ((HashVO) list_error.get(i - 1)).getAttributeValue(oldkey[j]);
						}
					//	System.out.println(i+" "+j+"||"+str_error[i][j]);
					}
				}
				return str_error;
			} else {
				return null;
			}
		}

		/**
		 * ������������-wdl
		 */
		public void Insert_InputInfo(Map map, List new_list, String tablename, String tree_codekey) {
			Connection conn = null;
			PreparedStatement psmt = null;
			String keys = "";
			String values = "";
			try {
				conn = new WLTDBConnection().getJDBCConnection();
				String[] keySet_all = (String[]) map.keySet().toArray(new String[0]);
				for (int i = 0; i < keySet_all.length; i++) {
					keys += keySet_all[i] + ",";
					values += map.get(keySet_all[i]) + ",";
				}
				keys = keys.substring(0, keys.length() - 1);
				values = values.substring(0, values.length() - 1);
				CommDMO comm = new CommDMO();
				String sql = " insert into " + tablename + " (id," + keys+ ") values (?," + values + ") ";
				psmt = conn.prepareStatement(sql);
				for (int i = 0; i < new_list.size(); i++) {
					String str_countsid=null;
					if(tree_codekey!=null&&!"".equals(tree_codekey)){
						str_countsid = ((HashVO)(new_list.get(i))).getStringValue(tree_codekey);
					}else {
						str_countsid= comm.getSequenceNextValByDS(null, "s_"+ tablename);
					}
					psmt.setObject(1, str_countsid);
					HashVO ho = (HashVO) new_list.get(i);
					for (int j = 0; j < keySet_all.length; j++) {
						psmt.setObject(j + 2, ho.getAttributeValue(keySet_all[j]));
					}
					psmt.addBatch();
				}
				psmt.executeBatch();
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (psmt != null) {
					try {
						psmt.close();
					} catch (final SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (final SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
}
