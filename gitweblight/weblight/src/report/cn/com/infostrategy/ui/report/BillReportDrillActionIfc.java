package cn.com.infostrategy.ui.report;

import java.awt.Container;
import java.util.HashMap;

/**
 * ��ά����ʵ����ȥ��ʵ���ݵĽӿڡ��ͻ���ʵ�ָýӿ��еķ�����
 * ��д�������˱������ݹ�����ʱ ������дMultiLevelReportDataBuilderAdapter�е�getDrillActionClassPath()����������һ�������ʵ����,������д����ʱ��ʵ�ָýӿ�
 * Ҫ���ڷ�������д��������Hashvo������ ������id����ֶΡ�
 * ʵ�ָýӿڵ����·����Ҫ��MultiLevelReportDataBuilderAdapter�д��ؿͻ��ˡ�Ϊʲô��ô�㣿���ڱ������ܶ������BillReportPanel,���������ò��ܵ�ReportCellPanel����ϣ�
 * ���嶼�ӷ������߹��췽���������㡣���ҿ��Ը��ݷ����������޷���ֵ�����Ƿ���ʾ�Ҽ���
 * @author hm
 * 
 * zzl[2018-02-01]
 * query
 * �����ѯmap  �����Ժ��ѯ
 *
 */
public interface BillReportDrillActionIfc {
	/*
	 * ids��cell���е�id����
	 */
	public void drillAction(String ids, Object _itemVO, Container _parent,HashMap<String, String> query);
	
	
}
