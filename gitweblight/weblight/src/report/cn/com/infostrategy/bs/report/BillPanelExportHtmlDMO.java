package cn.com.infostrategy.bs.report;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * ֱ���ڷ�������ʹ��ģ������ֱ�����Html�Ĺ���
 * @author xch
 *
 */
public class BillPanelExportHtmlDMO extends AbstractDMO {

	/**
	 * ��һ����Ƭ���html,֮����ʹ��ģ�����,��Ϊ�˵��������˳��ʱ�����������,���������ģ��Ĺ��ܿ�������
	 * ���ҿ��Գ�����ü��ع�ʽ�Ĺ���!!
	 * ��ǰ��ǰ̨����ķ�����ֱ�Ӵ�UI������ȡ,������ֱ���ں�̨ȡ!!!
	 * @param _templetCode 
	 * @param _sql
	 * @return
	 */
	public String getBillCardHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillCardHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString(); //
	}

	public String getBillListHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillListHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	public String getBillTreeHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillTreeHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	/**
	 * �е�ʱ������Ҫһ����ʾ,����������һ����Ƭ,������һ���б�,����������һ���б�,��һ����!!!
	 * ������ϵ�ļ�,�����˻���,�����˷��յ�,�������ڹ�,���������,�������������!!��...��Ҫһ�����һ������ʾ����!!!
	 * @param _allMulti N��3�еĶ�ά����,��1��������(��Ƭ/�б�/��),�ڶ�����ģ�����,��3����SQL
	 * @return
	 */
	public String getMultiHtml(String[][] _allMulti) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		for (int i = 0; i < _allMulti.length; i++) {
			if (_allMulti[i][0].equalsIgnoreCase("��Ƭ")) {
				sb_html.append(getBillCardHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			} else if (_allMulti[i][0].equalsIgnoreCase("�б�")) {
				sb_html.append(getBillListHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			} else if (_allMulti[i][0].equalsIgnoreCase("��")) {
				sb_html.append(getBillTreeHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			}
		}
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	/**
	 * һ������Ƭ���,����һ�����<table></table>
	 * @param _templetCode
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	private String getBillCardHtmlContent(String _templetCode, String _sql) throws Exception {
		//�ȸ���ģ�����ȡ��TempletVO,

		//Ȼ�����TempletVO��sql����getBillVOsByDS()����,�õ�BillVO[]!!һ��Ҫʹ�ø÷���,��Ϊ��Ҫʹ�ü��ع�ʽ�Զ������name��!!!!!

		//Ȼ�����ģ���ж����Զ�����,��Ƭ�Ƿ���ʾ,Title��������Ի��Ʊ��!!!ͬʱ��BillVO��ȡ��ʵ���������ȥ!!!Ҫע������Լ������,�����ܺ����Զ��ſ�,Ҫ�������Զ��Ÿ�!!(���Բ�����UI�˵�ʵ���߼�!!!)
		//Ҫע������ı����е����ݽ�\r\nת����<br>
		//Ҫע���ļ�����ת����html�ĳ����ӵķ���!!!
		StringBuffer sb_html = new StringBuffer(); //
		for (int i = 0; i < 10; i++) {
			sb_html.append("�����̵���һ�� <br>"); //
		}
		return sb_html.toString(); //
	}

	/***
	 * һ�����б����
	 * @param _templetCode
	 * @param _sql
	 * @return
	 */
	public String getBillListHtmlContent(String _templetCode, String _sql) throws Exception {
		//�ȸ���ģ�����ȡ��TempletVO,

		//Ȼ�����TempletVO��sql����getBillVOsByDS()����,�õ�BillVO[]!!һ��Ҫʹ�ø÷���,��Ϊ��Ҫʹ�ü��ع�ʽ�Զ������name��!!!!!

		//Ȼ�����ģ���ж����б��Ƿ���ʾ,�б���ʾ˳��,��ɫ��ʽ,�б��ȵ����Ի��Ʊ��!!!ͬʱ��BillVO��ȡ��ʵ���������ȥ!!!Ҫע������Լ������,�����ܺ����Զ��ſ�,Ҫ�������Զ��Ÿ�!!(���Բ�����UI�˵�ʵ���߼�!!!)
		//Ҫע������ı����е����ݽ�\r\nת����<br>
		//Ҫע���ļ�����ת����html�ĳ����ӵķ���!!!

		return null; //
	}

	/**
	 * ���Ϳؼ�
	 * @param _templetCode
	 * @param _sql
	 * @return
	 */
	public String getBillTreeHtmlContent(String _templetCode, String _sql) throws Exception {

		return null;
	}

}
