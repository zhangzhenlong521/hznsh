package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.pushworld.salary.to.SalaryReportVO;

/**
 * ��ҳ����: ��ѯͳ��--����ͳ��
 * @author yangke,haoming
 * create by 2013-10-30
 * 
 */
public class PersonOtherrReportWKPanel extends AbstractWorkPanel {
	private WLTTabbedPane tabbedPane = null;

	public void initialize() {
		this.setLayout(new BorderLayout());
		tabbedPane = new WLTTabbedPane();
		tabbedPane.setFocusable(false);

		/*		SalaryReportVO srvo_0 = new SalaryReportVO();
				srvo_0.setTitle(new String[] { "���", "����", "������", "��λ����" });
				srvo_0.setField(new String[] { "���", "username", "corpname", "stationkind" });
				srvo_0.setField_len(new String[] { "���", "80", "150", "100" });
				srvo_0.setTypes(new String[] { "�������" });
				srvo_0.setTypes_nocou(new String[] { });
				srvo_0.setTypes_field(new String[] { "C" });
				srvo_0.setTypes_field_nocou(new String[] { "D" });
				srvo_0.setV_count_type(new String[] { "�ϼ�" });
				srvo_0.setH_count_type(new String[] { "�ϼ�" });
				srvo_0.setTitlerows(2);
				srvo_0.setExcel_code("B");
				srvo_0.setReportname("����֧��");

				PersonStyleeReportWKPanel PersonStyleeReportWKPanel_0 = new PersonStyleeReportWKPanel(srvo_0);
				PersonStyleeReportWKPanel_0.initialize();
				tabbedPane.addTab("����֧��", PersonStyleeReportWKPanel_0);*/

		/*		SalaryReportVO srvo_0 = new SalaryReportVO();
				srvo_0.setTitle(new String[] { "���", "����" });
				srvo_0.setField(new String[] { "���", "username" });
				srvo_0.setField_len(new String[] { "���", "100", "100" });
				srvo_0.setTypes(new String[] {});
				srvo_0.setTypes_nocou(new String[] { "ʣ����" });
				srvo_0.setTypes_field(new String[] {});
				srvo_0.setTypes_field_nocou(new String[] { "money" });
				srvo_0.setH_count_type(new String[] {});
				srvo_0.setTitlerows(1);
				srvo_0.setReportname("����֧������");
				PersonStyleReportWKPanel PersonStyleReportWKPanel_0 = new PersonStyleReportWKPanel(srvo_0);
				PersonStyleReportWKPanel_0.initialize();
				BillQueryPanel querypanel = PersonStyleReportWKPanel_0.getBillQueryPanel();
				if (querypanel != null) {
					querypanel.setVisible(false);
				}
				tabbedPane.addTab("����֧������", PersonStyleReportWKPanel_0);*/

		PersonYqzfReportWKPanel PersonYqzfReportWKPanel_0 = new PersonYqzfReportWKPanel("����֧������");
		PersonYqzfReportWKPanel_0.initialize();
		tabbedPane.addTab("����֧����", PersonYqzfReportWKPanel_0);

		SalaryReportVO srvo_1 = new SalaryReportVO();
		srvo_1.setTitle(new String[] { "���", "����", "������", "��λ����" });
		srvo_1.setField(new String[] { "���", "username", "corpname", "stationkind" });
		srvo_1.setField_len(new String[] { "���", "80", "150", "100" });
		srvo_1.setTypes(new String[] { "�������" });
		srvo_1.setTypes_nocou(new String[] {});
		srvo_1.setTypes_field(new String[] { "B" });
		srvo_1.setTypes_field_nocou(new String[] {});
		srvo_1.setV_count_type(new String[] {});
		srvo_1.setH_count_type(new String[] { "�ϼ�" });
		srvo_1.setTitlerows(2);
		srvo_1.setExcel_code("A");
		srvo_1.setReportname("�������");

		PersonStyleeReportWKPanel PersonStyleeReportWKPanel_1 = new PersonStyleeReportWKPanel(srvo_1);
		PersonStyleeReportWKPanel_1.initialize();
		tabbedPane.addTab("���������", PersonStyleeReportWKPanel_1);

		PersonGqjlReportWKPanel PersonGqjlReportWKPanel_2 = new PersonGqjlReportWKPanel("��Ȩ��������");
		PersonGqjlReportWKPanel_2.addMenuConfMap(getMenuConfMap());
		PersonGqjlReportWKPanel_2.initialize();
		tabbedPane.addTab("��Ȩ����", PersonGqjlReportWKPanel_2);

		this.add(tabbedPane, BorderLayout.CENTER);

		//PersonStyleReportWKPanel_0.query("", ""); //�Զ���ѯ
	}

}
