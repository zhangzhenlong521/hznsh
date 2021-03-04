package cn.com.infostrategy.ui.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ReportUIUtil {

	public synchronized static void openBillCardHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillCardHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openBillListHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillListHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openBillTreeHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillTreeHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openMultiHtmlFrame(String _title, String[][] _multiSQLs) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getMultiHtml"); //
			parMap.put("multisqls", _multiSQLs);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * ��ȡ����,��Ϊ��ά������ͼ���ж�������ȡ��ϸ���߼�,Ϊ�˹���ͬһ�ݴ���,���Գ��ڵ�������!
	 * @param _parent
	 * @param str_ids
	 * @param _objItemVO
	 * @param str_builderClassName
	 * @param queryConditionMap
	 * @param _defaultDetailColNames
	 * @param str_drillActionClass
	 * @param str_drillTempletCode
	 * @throws Exception
	 * 
	 */
	public void onDrillDetail(java.awt.Container _parent, String str_ids, Object _objItemVO, String str_builderClassName, HashMap queryConditionMap, String[] _defaultDetailColNames, String str_drillActionClass, String str_drillTempletCode) throws Exception {
		//����˵��������ֽ�����ȡ���������ͬ��ģ�壬�����޷�������ʾ  [Ԭ����2012-08-21]
		//Ԭ����  2012-08-21 �����߼�
		//�����߼������sql�����û��id��ֱ���˳��������������߼�(ע����#��ʾ�ж������϶��ɵ����ݣ���ģ�岻һ��)
		//���ȼ��Ӹߵ��ͣ����û�з�����Ҳû��ģ�岢��idҲû��#����Ĭ�ϵ�ά�ȣ���������з��������߷����࣬����Ƕ������϶��ɵ����߲�ͬ��ģ�壬�������ֻ��һ��ģ������һ��ģ�壬
		//���������Ҫ��Ʃ��˵����������֮��Ŀ��Բ���Ҫid��˽�str_drillActionClass�ᵽ������
		if(str_drillActionClass != null){   //
			 //��������Է�����,��ֱ�ӵ��÷�����!
			Object obj = Class.forName(str_drillActionClass).newInstance(); // ʵ������ȡ��ʵ���ࡣ
			BillReportDrillActionIfc action = (BillReportDrillActionIfc) obj;
			if (obj instanceof BillReportDrillActionIfc) {
				action.drillAction(str_ids, _objItemVO, _parent,queryConditionMap); // ִ��
			}
		}else if (str_ids == null) { //Ԭ�������ģ������ж��ᵽ����   ������ʾΪ0�򲻽�����ȡ
			//MessageBox.show(_parent, "û��id�ֶ�,���#valueΪ��!�����޷�����ȡ����!"); //��û�ж��巴����,Ҳû�ж���ģ��,
			return;
		}else if(str_drillTempletCode == null&& str_ids.indexOf("#")==-1) {//���ʲô��û������Ĭ�ϵ�
			 //������߶�Ϊ��!��ֱ�ӵ���ids,Ȼ��ӷ��������ٴβ�ѯ����,�õ�HashVO[],������!!!Ȼ��ֱ��ʹ�ö�̬����BillListPanel�ķ�ʽ����
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashVO[] hvsDetail = service.queryMultiLevelReportDataDrillDetail(queryConditionMap, str_builderClassName, str_ids); //
			if (hvsDetail.length <= 0) {
				MessageBox.show(_parent, "����ids=[" + str_ids + "]��ȡ��ϸ�Ľ����ȻΪ��,���������ݷ����˱仯!�����²�ѯ����!"); //
				return; //
			}
			String[] str_keys = hvsDetail[0].getKeys(); //ʵ�ʵ�������!
			TBUtil.getTBUtil().sortStrsByOrders(str_keys, _defaultDetailColNames); //����һ��
			String[][] str_tmodefine = new String[str_keys.length][2]; //
			for (int i = 0; i < str_keys.length; i++) {
				if (TBUtil.getTBUtil().isExistInArray(str_keys[i], _defaultDetailColNames)) { //ֻ��ʾ����ά�ȼ������!
					str_tmodefine[i] = new String[] { str_keys[i], str_keys[i], "105", "Y" }; //
				} else {
					str_tmodefine[i] = new String[] { str_keys[i], str_keys[i], "105", "N" }; //
				}
			}
			DefaultTMO tmo = new DefaultTMO("��ȡ��ϸ", str_tmodefine); //
			BillListPanel list = new BillListPanel(tmo); //
			list.putValue(hvsDetail); //
			list.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //��������״̬ΪInit�����������鶼����ɫ��,������
			list.setTitleLabelText("��ȡ��ϸ,һ��[" + hvsDetail.length + "]����¼"); //
			String str_title = "��ȡ��ϸ"; //
			if (ClientEnvironment.isAdmin()) {
				str_title = str_title + " ���ڹ��������ع�getDrillTempletCode(),�Ϳ�����ָ��ģ����ʾ��ϸ,��ǰ������" + str_builderClassName + "��";
			}
			BillDialog dialog = new BillDialog(_parent, str_title, 900, 500);
			dialog.getContentPane().add(list); //
			dialog.addConfirmButtonPanel(2); //
			dialog.setVisible(true); //
		
		}else{
			if(str_ids.indexOf("#")>0){  //Ԭ������ӣ�����������������һ��ͳ�ƽ���ǴӶ����õ������ݣ���ͬ�����ݿ��ܶ��ڲ�ͬ��ģ�壬��ǰ̨��װ����ʱ��ID+'#'+ģ�����������ķ�ʽ����װIDS������ǰ̨����Ҫ����дgetDrillTempletCode����
				/*
				 * �˲����ж���Ҫ���һ�������ж�����ģ��  [Ԭ����2012-08-21���]
				 */
				String []str_id=new TBUtil().split(str_ids, ";");
				String template="";
				List <String>listId=new ArrayList<String>();
				//��ȡģ��
				if(null!=str_id[0]&&!str_id[0].equals("")&&str_id[0].indexOf("#")>0){//��һ����Ϊnull���Ҳ�Ϊ���ַ���������#��
					template=str_id[0].substring( str_id[0].indexOf("#")+1);
				}
				//StringBuffer strids=new StringBuffer();
				//������Ҫ��ids���н�ȡ��ȥ����#����ƴ�ӳ�ids
				for(int i=0;i<str_id.length;i++){  //20121217Ԭ�������ģ�֮ǰΪʲô��str_id-1�أ��벻����
					String str_temp=str_id[i].substring(0, str_id[i].indexOf("#"));
					listId.add(str_temp);
					//strids.append(str_temp).append(",");//����ƴ�ӣ�ȥ����"#"���ٽ���idƴ��
				}
				//strids.append(str_id[str_id.length-1].substring(0, str_id[str_id.length-1].indexOf("#")));
				Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(template);
				templetVO.setAutoLoads(0); //Ĭ�ϲ�����
				templetVO.setDatapolicy(""); //ȥ�����ݲ���
				templetVO.setDatapolicymap("");
				templetVO.setDataconstraint("");//ȥ��Ĭ�ϵ�Ȩ�޲��ԣ���Ϊ��ͳ�Ƶ�ʱ���Ѿ�������Ȩ�޲��ԣ�������ﲻȥ����������ʾ����Ŀ��ǰ̨ͳ�Ƶ���Ŀ��һ��     [Ԭ����2012-08-20]
				templetVO.setAutoloadconstraint("");//ȥ��Ĭ�ϵ�Ȩ�޲��ԣ���Ϊ��ͳ�Ƶ�ʱ���Ѿ�������Ȩ�޲��ԣ�������ﲻȥ����������ʾ����Ŀ��ǰ̨ͳ�Ƶ���Ŀ��һ��    [Ԭ����2012-08-20]
				templetVO.setIsshowlistpagebar(true);//����ҳ������ǰ̨��ʾ�����ݺ�̨һ��ҳ�����ֻ����ʾ20��    [Ԭ����2012-08-20]
				BillListPanel drillList = new BillListPanel(templetVO);
				//drillList.setBillQueryPanelVisible(true); //���ز�ѯ
				drillList.setPagePanelVisible(true); //��ҳ
				drillList.setItemEditable(false); //���ɱ༭!!
				drillList.setQuickQueryPanelVisiable(false);//�����Բ�ѯֻ����ʾ
				drillList.setAllBillListBtnVisiable(false);//�������õİ�ťֻ�������ť���� [Ԭ����2012-08-20]
				drillList.setIsRefreshParent("1");//���ò�ˢ��ģ�� Ԭ���� 20130423���
				WLTButton btn_lookupresult;
				btn_lookupresult=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);//��������ť
				drillList.addBillListButton(btn_lookupresult);
				drillList.repaintBillListButton();
				//��ȡ����
				StringBuffer str_col=new StringBuffer();
				if(drillList.getColumnModel().getColumnCount()>0){
					for(int k=0;k<drillList.getColumnModel().getColumnCount();k++ ){
						str_col.append(drillList.getColumnModel().getColumn(k).getHeaderValue().toString());
					}
				}
				String tempstr=TBUtil.getTBUtil().getInCondition(listId);
				drillList.QueryDataByCondition(drillList.getTempletVO().getPkname() +" in(" + tempstr+")"); //���������Ա�Զ���ģ�壬������Ҫ��������ID��һ�顣[����2012-08-02]  20121126Ԭ�������� ��Ҫ��ֹsql��䳬��1000�����bug
				drillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //��������״̬ΪInit�����������鶼����ɫ��,������
				String str_title = "��ȡ��ϸ"; //
				if (ClientEnvironment.isAdmin()) {
					str_title = str_title + ",ʵ�ʷ������ݵ����ǡ�" + str_col.toString() + "��"; //ʵʩ��Ա����ֱ�ӿ���������ģ����������Щ��!!
				}
				drillList.setTitleLabelText("��ȡ��ϸ,һ����[" + str_id.length + "]����¼"); //
				BillDialog dialog = new BillDialog(_parent, str_title, 800, 600); //
				dialog.getContentPane().add(drillList);
				dialog.addConfirmButtonPanel(2);
				dialog.setVisible(true);
			}else if (str_drillTempletCode != null) { //���û�ж��巴����,����ʹ��ģ��������ȡ����!
				Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(str_drillTempletCode);
				templetVO.setAutoLoads(0); //Ĭ�ϲ�����
				templetVO.setDatapolicy(""); //ȥ�����ݲ���
				templetVO.setDatapolicymap("");
				templetVO.setDataconstraint("");//ȥ��Ĭ�ϵ�Ȩ�޲��ԣ���Ϊ��ͳ�Ƶ�ʱ���Ѿ�������Ȩ�޲��ԣ�������ﲻȥ����������ʾ����Ŀ��ǰ̨ͳ�Ƶ���Ŀ��һ��     [Ԭ����2012-08-20]
				templetVO.setAutoloadconstraint("");//ȥ��Ĭ�ϵ�Ȩ�޲��ԣ���Ϊ��ͳ�Ƶ�ʱ���Ѿ�������Ȩ�޲��ԣ�������ﲻȥ����������ʾ����Ŀ��ǰ̨ͳ�Ƶ���Ŀ��һ��    [Ԭ����2012-08-20]
				templetVO.setIsshowlistpagebar(true);//����ҳ������ǰ̨��ʾ�����ݺ�̨һ��ҳ�����ֻ����ʾ20��    [Ԭ����2012-08-20]
				BillListPanel drillList = new BillListPanel(templetVO);
				//drillList.setBillQueryPanelVisible(true); //���ز�ѯ
				drillList.setPagePanelVisible(true); //��ҳ
				drillList.setItemEditable(false); //���ɱ༭!!
				drillList.setQuickQueryPanelVisiable(false);//�����Բ�ѯֻ����ʾ
				drillList.setAllBillListBtnVisiable(false);//�������õİ�ťֻ�������ť���� [Ԭ����2012-08-20]
				drillList.setIsRefreshParent("1");//���ò�ˢ��ģ��  Ԭ���� 20130423���
				WLTButton btn_lookupresult;
				btn_lookupresult=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);//��������ť
				drillList.addBillListButton(btn_lookupresult);
				drillList.repaintBillListButton();
				//Զ��ȡ��,�������!!!
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvsDetail = service.queryMultiLevelReportDataDrillDetail(queryConditionMap, str_builderClassName, str_ids); //
				//				drillList.putValue(hvsDetail); //��������!!
			/*	StringBuilder incondition = new StringBuilder("-999999"); //id��
				for (int i = 0; i < hvsDetail.length; i++) {
					String id = hvsDetail[i].getStringValue("id");
					if (id != null && !id.equals("")) {
						incondition.append("," + id);
					}
				}*/
				List <String>listId=new ArrayList<String>();
				for (int i = 0; i < hvsDetail.length; i++) {
					String id = hvsDetail[i].getStringValue("id");
					if (id != null && !id.equals("")) {
						listId.add(id);
					}
				}
				String tempstr=TBUtil.getTBUtil().getInCondition(listId);
				StringBuilder sb_info = new StringBuilder(); //
				if (hvsDetail.length > 0) {
					String[] str_keys = hvsDetail[0].getKeys(); //ʵ�ʵ�������!
					for (int i = 0; i < str_keys.length; i++) {
						sb_info.append(str_keys[i] + ","); //
					}
				}
				drillList.QueryDataByCondition(drillList.getTempletVO().getPkname() +" in(" + tempstr+")"); //���������Ա�Զ���ģ�壬������Ҫ��������ID��һ�顣[����2012-08-02]  20121126Ԭ�������� ��Ҫ��ֹsql��䳬��1000�����bug
				drillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //��������״̬ΪInit�����������鶼����ɫ��,������
				String str_title = "��ȡ��ϸ"; //
				if (ClientEnvironment.isAdmin()) {
					str_title = str_title + ",ʵ�ʷ������ݵ����ǡ�" + sb_info.toString() + "��"; //ʵʩ��Ա����ֱ�ӿ���������ģ����������Щ��!!
				}
				drillList.setTitleLabelText("��ȡ��ϸ,һ����[" + hvsDetail.length + "]����¼"); //
				BillDialog dialog = new BillDialog(_parent, str_title, 800, 600); //
				dialog.getContentPane().add(drillList);
				dialog.addConfirmButtonPanel(2);
				dialog.setVisible(true);
			}
		}
	}

}
