package cn.com.infostrategy.to.report;

import java.io.Serializable;

import cn.com.infostrategy.to.mdata.BillCellVO;

/**
 * ʹ��Excel�ؼ�չʾ�ı����еĴ洢�����ݶ���!!�ؼ�!!
 * ������ֱ��ʹ��HashVO[]��BillCellVO��Ϊ�������������࣬�������������ǲ����������ͷ�����ͷ�ϲ���Ҫ�����Ըо��б�Ҫ���¹���һ���µ�ר�ŵı��������࣡
 * �ö�����Ҫ�ɱ���������ͷ,��ͷ,���ݱ���Ĳ�����ɡ�������ͷ����ͷ�����Ƕ�㣬���ݱ������������,������ͬ���ݺϲ���
 * 
 * @author xch
 *
 */
public class ReportCellVO implements Serializable {

	private String title = null; //������,��Զ�ڵ�һ����ʾ.

	private String[][] rowHeaders = null; //��ͷ,������ɫ�Զ���Ϊǳ��ɫ
	private String[][] colHeaders = null; //��ͷ,������ɫ�Զ���Ϊǳ��ɫ

	private BillCellVO contentCellVO = null; //������������
}
