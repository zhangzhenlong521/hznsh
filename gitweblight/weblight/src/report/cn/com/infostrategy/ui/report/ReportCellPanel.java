package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * ��ά����ĸ�������,Ҳ���ǽ�ԭ���Ĵ����߼����ᵽ������� ��Ϊ��Ҫ��ȡ,����ʵ������Grid����ϴ洢����һ���ĸ����
 * ����Grid����д洢��һ����ջ,�øö�ջ���洢��ȡ���һ������,������ʱ,�Ӷ�ջ������ȥ�ɵ�,��ʾ�µĶ�ջ����
 * ����ȡʱ,����һ���µ�,�������ջ����,Ȼ����ʾ֮!!
 * @author xch
 * 
 */
public class ReportCellPanel extends JPanel implements BillCellHtmlHrefListener, ActionListener {

	private String str_builderClassName = null; //

	private WLTButton btn_xy, btn_exportHtml, btn_exportExcel; //�����һ�Ű�ť!
	private BillCellPanel cellPanel = null; // ����ؼ�,����ҪΨһ�Ŀؼ�!
	private boolean isConvertXY = false; //

	private String[] allCanGroupFields = null; // ���пɲ���������
	private String[] allCanComputeFields = null; // ���пɲ���������

	private String[] chooseGroupRowFields = null; // ѡ�е���ͷ����,����:new String[] {
	// "����", "��Ʒ" }; //
	private String[] chooseGroupColFields = null; // ѡ�е���ͷ����,����:new String[] {
	// "���", "Ʒ��", "�ͺ�" }; //

	private String[][] chooseComputeFunAndField = null; // �������ĺ����������!!
	private boolean isRowGroupTiled = false; //��ͷ�Ƿ�ƽ��?

	private boolean isTotal = true; //�Ƿ����ܼ�
	private boolean isRowGroupSubTotal = true; //��ͷ�Ƿ���С��?
	private boolean isColGroupTiled = false; //��ͷ�Ƿ�ƽ��?
	private boolean isColGroupSubTotal = true; //��ͷ�Ƿ���С��?
	private boolean isSortByCpValue = false; //�Ƿ񰴽������?

	private HashMap queryConditionMap = null; // ��ѯ����
	private HashMap[] drillConditionMap = null; // ��������
	private HashVO[] hvs_data = null; // ��ȡ�������
	private HashVO[] hvs_data_beforefilter = null; // ����ǰ������,��һ����ʱ�Ὣ������.
	private HashMap filterConditionMap = null; // ��������!!!
	private HashMap filterGroupValueMap = null; // ��������!!!

	private HashMap groupfieldorder = null; // ����ͷ���� key=�ֶ�����
	// value=�����˳�����飬����key=�ļ�״̬
	// value=[��д��][���뷢��][��Ч]..
	private DefaultMutableTreeNode rowHeaderRootNode = null;
	private DefaultMutableTreeNode colHeaderRootNode = null;
	private HashMap beforeBuilderDataMap = null; //

	private String[] headBgColors_row = new String[] { "255,215,255", "200,255,255", "222,222,188", "235,235,235" }; //
	private String[] headBgColors_col = new String[] { "200,255,230", "230,225,255", "255,200,200", "237,220,220" }; //
	private String[] totalBgColors = new String[] { "221,221,50", "255,255,100", "255,255,180", "255,255,220" }; //
	private String[] computeBgColors = new String[] { "255,128,64", "0,128,128", "0,0,255", "0,128,0" }; //�������еĵڼ���λ��
	private String[] warnBgColors = new String[] { "237,23,0", "255,128,0", "255,255,0", "128,255,128" }; //����ֵ�ı�����ɫ,�ӡ���ɫ->��ɫ->��ɫ->��ɫ��
	private JepFormulaParseAtUI jepParse = null; //��ʽ������!

	private JPopupMenu rightPopMenu = null;
	private JMenuItem menuItem_drillNextGroup, menuItem_drillDetail, menuItem_filter, menuItem_barChart, menuItem_lineChart, menuItem_pieChart, menuItem_warnValue, menuItem_viewInfo, menuItem_lock, menuItem_unlock; //

	private String str_drillActionClass = null; //�ͻ���ʵ����ȥ��ʵ���ݵ���·����[����2012-06-08]
	private String str_drillTempletCode = null; //�ͻ�����ȡ��ϸʱ����һ������ģ��??
	private HashMap drillgroupbindMap = null; //��ȡ��Ԥ�ð󶨵Ķ���!
	private HashMap zeroReportConfMap = null; //��㱨���Ƶ�Map����!
	private HashMap dateGroupDefineMap = null; //ʱ���ֶ�ά��!
	private HashMap secondHashVOComputeMap = null; //��HashVO[]���ж��μ����Map
	private HashMap rowHeaderFormulaGroupMap = null; //��ͷ�Ͽ��Զ��������µĹ�ʽ��!
	private boolean isCtrlDown = false; //

	private TBUtil tbutil = new TBUtil();
	private String[] str_notCanTotalTypes = new String[] { "max", "min" }; //

	private MultiLevelChooseDrillDialog drillNextGroupDialog = null;

	/**
	 * Ĭ�Ϲ��췽��������!
	 */
	private ReportCellPanel() {
	}

	/**
	 * ���췽��..
	 * 
	 * @param _builderClassName
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _queryConditionMap, HashMap[] _drillConditionMap) {
		this(_builderClassName, _allCanGroupFields, _allCanComputeFields, _chooseGroupRowFields, _chooseGroupColFields, _chooseComputeFunAndField, null, null, null, true, false, true, false, true, false, _queryConditionMap, _drillConditionMap, null, null, null, false);
	}

	/**
	 * �������groupfieldorder ���÷�ʽ key=�ֶ����� value=�����˳�����飬����key=�ļ�״̬
	 * value=[��д��][���뷢��][��Ч]..
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _queryConditionMap, HashMap[] _drillConditionMap, HashMap _groupfieldorder) {
		this(_builderClassName, _allCanGroupFields, _allCanComputeFields, _chooseGroupRowFields, _chooseGroupColFields, _chooseComputeFunAndField, null, null, null, true, false, true, false, true, false, _queryConditionMap, _drillConditionMap, _groupfieldorder, null, null, false);
	}

	/**
	 * �������groupfieldorder ���÷�ʽ key=�ֶ����� value=�����˳�����飬����key=�ļ�״̬
	 * value=[��д��][���뷢��][��Ч]..
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _filterGroupValueMap, HashMap _secondHashVOComputeMap, HashMap _rowHeaderFormulaGroupMap, boolean _isTotal, boolean _isRowGroupTiled, boolean _isRowGroupSubTotal,
			boolean _isColGroupTiled, boolean _isColGroupSubTotal, boolean _isSortByCpValue, HashMap _queryConditionMap, HashMap[] _drillConditionMap, HashMap _groupfieldorder, HashMap _zeroReportConfMap, HashMap _dateGroupDefineMap, boolean _isCtrlDown) {
		super(); //
		str_builderClassName = _builderClassName; //
		this.allCanGroupFields = _allCanGroupFields; //
		this.allCanComputeFields = _allCanComputeFields; //
		this.chooseGroupRowFields = _chooseGroupRowFields; //
		this.chooseGroupColFields = _chooseGroupColFields; //
		this.chooseComputeFunAndField = _chooseComputeFunAndField; //
		this.filterGroupValueMap = _filterGroupValueMap; //
		this.rowHeaderFormulaGroupMap = _rowHeaderFormulaGroupMap; //��ʽ��!

		this.isTotal = _isTotal; //
		this.isRowGroupTiled = _isRowGroupTiled; //
		this.isRowGroupSubTotal = _isRowGroupSubTotal; //  
		this.isColGroupTiled = _isColGroupTiled; //
		this.isColGroupSubTotal = _isColGroupSubTotal; //
		this.isSortByCpValue = _isSortByCpValue; //�Ƿ񰴽������?

		this.queryConditionMap = _queryConditionMap; //
		this.drillConditionMap = _drillConditionMap; //
		this.groupfieldorder = _groupfieldorder;
		this.zeroReportConfMap = _zeroReportConfMap; //
		this.dateGroupDefineMap = _dateGroupDefineMap; //
		this.secondHashVOComputeMap = _secondHashVOComputeMap; //

		this.isCtrlDown = _isCtrlDown; //�Ƿ���Ctrl��??

		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashVO[] hvs_initdata = service.queryMultiLevelReportData(queryConditionMap, this.str_builderClassName, getAllChooseGroupFields(), chooseComputeFunAndField, isCtrlDown); //��������Զ�̷���,����ͳ��!���ﷵ�ص��Ѿ��ǻ��ܵ�����,�����������ٵ�!!
			if (hvs_initdata.length == 0) {
				this.add(new JLabel("û�в�ѯ��һ������!!!")); //
				return; //
			}

			//�ȴ�������Ϊ�յ�����Ϊ���ַ���!!��һ���������Ǻܱ�Ҫ��,��Ϊ���������Ϊ��!
			String[] itemKey = hvs_initdata[0].getKeys(); //
			for (int i = 0; i < hvs_initdata.length; i++) {
				for (int j = 0; j < itemKey.length; j++) {
					if (hvs_initdata[i].getStringValue(itemKey[j]) == null) { //
						hvs_initdata[i].setAttributeValue(itemKey[j], ""); //
					}
				}
			}

			if (drillConditionMap == null || drillConditionMap.length == 0) { //���ֱ�Ӳ�ѯ��,������ȡ��,��ֱ�ӵ���!
				this.hvs_data = hvs_initdata; //���û�н���������ȡ
			} else { //�������������ȡ,�������Ĵ���,��Ҫ������ȡ��ά�Ƚ����ٴι���!!!����ȡ���߼�����ǰ̨���е�! Ϊʲô���ں�̨�����أ���Ϊ��ȡ����,��������Ҫ����������ϸHashVO[],����һ��,��ǰ̨���Ǻ�̨�������ܶ����!!
				ArrayList al_filteredhvs = new ArrayList();
				String str_a1, str_a2;//
				for (int i = 0; i < hvs_initdata.length; i++) { // ��������
					boolean bo_isonematch = false; //
					for (int j = 0; j < drillConditionMap.length; j++) {
						boolean bo_ismatch = true;
						String[] str_secondFilterGroupFields = (String[]) drillConditionMap[j].keySet().toArray(new String[0]); //
						for (int k = 0; k < str_secondFilterGroupFields.length; k++) { // ÿһ������,���Ʒ�䲻�Ͼ������˳�!
							str_a1 = hvs_initdata[i].getStringValue(str_secondFilterGroupFields[k]); //
							str_a2 = (String) drillConditionMap[j].get(str_secondFilterGroupFields[k]); //
							if (str_a1 == null || str_a1.trim().equals("")) { //���뿼�ǿ�ֵ��ƥ��!
								str_a1 = "����ֵ��";
							}
							if (!str_a1.equals(str_a2)) { //ֻҪ��һ������,�������˳�!
								bo_ismatch = false;
								break;
							}
						}
						if (bo_ismatch) {
							bo_isonematch = true; // ������˵�����,һ��������������,�����������Ͳ�Ҫ����!!!��������!
							break;
						}
					}
					if (bo_isonematch) {
						al_filteredhvs.add(hvs_initdata[i]); //
					}
				}
				this.hvs_data = (HashVO[]) al_filteredhvs.toArray(new HashVO[0]); // ���ù��˺��ֵ
			}
			hvs_data_beforefilter = null; // ÿ����ȡ�������²�ѯʱ,���ǽ����ÿ�!
			filterConditionMap = null; //

			buildUI2(); // ��ҳ����!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, ex.getMessage()); //
		}
	}

	/**
	 * �������������ڲ����ã������ؼ����ǿ������������㶨һ�еģ�����
	 */
	private void buildTree() {
		boolean isOnlyRowGroup = (chooseGroupColFields.length == 0 ? true : false); //�Ƿ�ֻ����ά��?�������ά������Ϊ0,���ʾֻ����ά�ȣ�����
		boolean isOnlyColGroup = (chooseGroupRowFields.length == 0 ? true : false); //�Ƿ�ֻ����ά��?�������ά������Ϊ0,���ʾֻ����ά�ȣ�����
		boolean isCanTotal = isCaneTotlal(chooseComputeFunAndField); //�Ƿ���Ժϼ�?��ֻҪ��һ���ֶ��ǿ��ԺϼƵ�,�����Ϊ�ǿ��ԺϼƵ�!
		rowHeaderRootNode = getGroupHeaderNode(this.chooseGroupRowFields, isRowGroupTiled, isRowGroupSubTotal, isCanTotal, true, isOnlyRowGroup, chooseComputeFunAndField, groupfieldorder); // ������ͷ
		colHeaderRootNode = getGroupHeaderNode(this.chooseGroupColFields, isColGroupTiled, isColGroupSubTotal, isCanTotal, false, isOnlyColGroup, chooseComputeFunAndField, groupfieldorder); // ������ͷ

		JTree tree1 = new JTree(rowHeaderRootNode); //
		JTree tree2 = new JTree(colHeaderRootNode); //

		JTabbedPane tabbPane = new JTabbedPane(); //
		tabbPane.addTab("��ͷ��", new JScrollPane(tree1)); //
		tabbPane.addTab("��ͷ��", new JScrollPane(tree2)); //

		this.removeAll();
		this.setLayout(new BorderLayout(0, 0)); //
		this.add(tabbPane); //

		this.updateUI(); //
	}

	/**
	 * �����Ĺ������ݣ����µ��߼�����
	 */
	private void buildUI2() {
		boolean isOnlyRowGroup = (chooseGroupColFields.length == 0 ? true : false); //�Ƿ�ֻ����ά��?�������ά������Ϊ0,���ʾֻ����ά�ȣ�����
		boolean isOnlyColGroup = (chooseGroupRowFields.length == 0 ? true : false); //�Ƿ�ֻ����ά��?�������ά������Ϊ0,���ʾֻ����ά�ȣ�����
		boolean isCanTotal = isCaneTotlal(chooseComputeFunAndField); //�Ƿ���Ժϼ�?��ֻҪ��һ���ֶ��ǿ��ԺϼƵ�,�����Ϊ�ǿ��ԺϼƵ�!
		boolean isINIT = false;// �����ж��Ƿ�Ϊinitģʽ������ǵĻ�����Ҫ������������ø��ӵĿ�ȣ���Ϊ��Initģʽ�£�������Ҫ�ٿ�һЩ    20130308   Ԭ����

		//�������и���֮ǰ,��Ҫ�����ϣ������!����Ϊ�����ƥ��ά��ֵʱ������!
		//��ǰ�Ƿ��ں�������,�����������а�����!!������ͳ��ֵ��������!������Ҫ�ȼ����ֵ,���Էŵ�ǰ������!
		beforeBuilderDataMap = new LinkedHashMap(); //��ϣ������
		HashSet hst_cpfield = new HashSet(); //�洢Ψһ�Եļ����е�����,��Ϊһ������ȫ�п���ͬʱ��sum,avg,count,���Ա�����Ψһ�Թ���,������ظ�����,���������������bug
		for (int k = 0; k < chooseComputeFunAndField.length; k++) { //��������
			String str_fieldName = chooseComputeFunAndField[k][2]; //
			if (str_fieldName.indexOf("-") > 0) {
				str_fieldName = str_fieldName.substring(0, str_fieldName.indexOf("-")); //��ȡǰ���!!!
			}
			hst_cpfield.add(str_fieldName); //
		}
		String[] str_dist_fields = (String[]) hst_cpfield.toArray(new String[0]); //ת��������
		boolean havecount = false;
		for (int i = 0; i < chooseComputeFunAndField.length; i++) {
			if ("count".equalsIgnoreCase(chooseComputeFunAndField[i][0])) {
				havecount = true;
				break;
			}
		}
		for (int r = 0; r < hvs_data.length; r++) { //������������!!
			//if()
			//ֱ�ӵ���������ά�ȣ�����
			for (int i = 0; i < chooseGroupRowFields.length; i++) { //����������ά�ȣ����û����ά��,Ϊ�˱�֤�������ܳ�����Ҳ����һ��ѭ����!
				//�ȴ�0����ǰλ�����ȫ��,�ֱ���A,AB,ABC,ABCD
				StringBuilder sb_key_1 = new StringBuilder(); //
				if (isRowGroupTiled) { //��ͷƽ��
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i], "����ֵ��", true); //ά��ֵ��
					sb_key_1.append(chooseGroupRowFields[i] + "=" + str_levelValue + "��"); //ʵ��ֵ��
				} else {
					for (int i2 = 0; i2 <= i; i2++) {
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i2], "����ֵ��", true); //ά��ֵ��
						sb_key_1.append(chooseGroupRowFields[i2] + "=" + str_levelValue + "��"); //ʵ��ֵ��
					}
				}

				for (int k = 0; k < str_dist_fields.length; k++) { //�������м�����!
					String str_cpField = str_dist_fields[k]; //�ֶ���!!
					if (str_cpField.endsWith("����") || havecount) { //Ԭ������� ͳ����ʾ��ʼֵ  20130312   ���Ϊ����ͳ��
						BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "��count"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(count)", ld_value_count); //�ж�����о��ۼ�,���û�оͼ���!!
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //

					} else {//���Ϊinit   Ԭ������� ͳ����ʾ��ʼֵ  20130312
						isINIT = true;// �����һ��Ϊinit��˴ζ�Ϊinit�����治�����ж�
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						/*String ld_value_init= hvs_data[r].getStringValue(str_cpField+"��init", ";");   //Ԭ�������init
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(init)", ld_value_init);  //Ԭ�������init
						*/}
				}

				String str_value = hvs_data[r].getStringValue("#value"); //����û��?
				String str_realKey_1_values = sb_key_1.toString() + "#value"; //
				appendMapData2(beforeBuilderDataMap, str_realKey_1_values, str_value); //
			}

			//����������ά�ȣ���
			for (int j = 0; j < chooseGroupColFields.length; j++) { //����������ά��
				StringBuilder sb_key_2 = new StringBuilder(); //
				if (isColGroupTiled) { //��ͷƽ��
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j], "����ֵ��", true); //ά��ֵ��
					sb_key_2.append(chooseGroupColFields[j] + "=" + str_levelValue + "��"); //ʵ��ֵ��
				} else {
					for (int j2 = 0; j2 <= j; j2++) { //
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j2], "����ֵ��", true); //ά��ֵ��
						sb_key_2.append(chooseGroupColFields[j2] + "=" + str_levelValue + "��"); //ʵ��ֵ��
					}
				}

				for (int k = 0; k < str_dist_fields.length; k++) { //�������м�����!
					String str_cpField = str_dist_fields[k]; //�ֶ���!!
					if (str_cpField.endsWith("����") || havecount) { //���Ϊ����ͳ��
						BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "��count"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(count)", ld_value_count); //�ж�����о��ۼ�,���û�оͼ���!!
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
					} else {//��Ϊ����ͳ����Ϊinit
						//����������־��Ŀ��ʱͳ��ʱ��Ҫ��Ӻϼƹ��ܣ����ǳ�ʼ��Ҳ���Ϻϼƹ���  20130319   Ԭ����
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						/*String ld_value_init= hvs_data[r].getStringValue(str_cpField+"��init", ";");   //Ԭ�������init
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(init)", ld_value_init);  //Ԭ�������init
						*/}
				}

				String str_value = hvs_data[r].getStringValue("#value"); //����û��?
				String str_realKey_2_values = sb_key_2.toString() + "#value"; //
				appendMapData2(beforeBuilderDataMap, str_realKey_2_values, str_value); //
			}

			//�����н��棡�������ֻ���л��У��򶼲������У����������������Ѵ����ˣ�����
			for (int i = 0; i < chooseGroupRowFields.length; i++) { //����������ά�ȣ����û����ά��,Ϊ�˱�֤�������ܳ�����Ҳ����һ��ѭ����!
				//�ȴ�0����ǰλ�����ȫ��,�ֱ���A,AB,ABC,ABCD
				StringBuilder sb_key_1 = new StringBuilder(); //
				if (isRowGroupTiled) { //��ͷƽ��
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i], "����ֵ��", true); //ά��ֵ��
					sb_key_1.append(chooseGroupRowFields[i] + "=" + str_levelValue + "��"); //ʵ��ֵ��
				} else {
					for (int i2 = 0; i2 <= i; i2++) {
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i2], "����ֵ��", true); //ά��ֵ��
						sb_key_1.append(chooseGroupRowFields[i2] + "=" + str_levelValue + "��"); //ʵ��ֵ��
					}
				}

				//����������ά��!!
				for (int j = 0; j < chooseGroupColFields.length; j++) { //����������ά��
					StringBuilder sb_key_2 = new StringBuilder(); //
					if (isColGroupTiled) { //��ͷƽ��
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j], "����ֵ��", true); //ά��ֵ��
						sb_key_2.append(chooseGroupColFields[j] + "=" + str_levelValue + "��"); //ʵ��ֵ��
					} else {
						for (int j2 = 0; j2 <= j; j2++) { //
							String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j2], "����ֵ��", true); //ά��ֵ��
							sb_key_2.append(chooseGroupColFields[j2] + "=" + str_levelValue + "��"); //ʵ��ֵ��
						}
					}

					for (int k = 0; k < str_dist_fields.length; k++) { //�������м�����!
						String str_cpField = str_dist_fields[k]; //�ֶ���!!
						if (str_cpField.endsWith("����") || havecount) { //���Ϊ����ͳ��
							BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "��count"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(count)", ld_value_count); //�ж�����о��ۼ�,���û�оͼ���!!
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						} else {//��Ϊ����ͳ����Ϊinit
							String ld_value_init = hvs_data[r].getStringValue(str_cpField + "��init", ";"); //Ԭ�������init
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(init)", ld_value_init); //Ԭ�������init
							//����������־��Ŀ��ʱͳ��ʱ��Ҫ��Ӻϼƹ��ܣ����ǳ�ʼ��Ҳ���Ϻϼƹ���  20130319   Ԭ����
							BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						}
					}

					String str_value = hvs_data[r].getStringValue("#value"); //����û��?
					String str_realKey_3_values = sb_key_1.toString() + sb_key_2.toString() + "#value"; //
					//System.out.println("��" + str_realKey_3_values + "��=[" + str_value + "]"); //
					appendMapData2(beforeBuilderDataMap, str_realKey_3_values, str_value); //
				}
			} //

			//�����½��и��ܼ�
			for (int k = 0; k < str_dist_fields.length; k++) { //�������м�����!
				String str_cpField = str_dist_fields[k]; //�ֶ���!!
				if (str_cpField.endsWith("����") || havecount) { //���Ϊ����ͳ��
					BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "��count"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
					BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum");
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(count)", ld_value_count); //�ж�����о��ۼ�,���û�оͼ���!!
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(sum)", ld_value_sum); //
				} else {//��Ϊ����ͳ����Ϊinit   �������ܼ�
					//����������־��Ŀ��ʱͳ��ʱ��Ҫ��Ӻϼƹ��ܣ����ǳ�ʼ��Ҳ���Ϻϼƹ���  20130319   Ԭ����
					BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "��sum"); //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(sum)", ld_value_sum); //
					/*	String ld_value_init= hvs_data[r].getStringValue(str_cpField+"��init", ";");   //Ԭ�������init
						appendMapData(beforeBuilderDataMap, str_cpField.toString() + "@" + str_cpField + "(init)", ld_value_init);  //Ԭ�������init
					*/}
			}

		}
		/*		for(Iterator iterator =beforeBuilderDataMap.keySet().iterator();iterator.hasNext();){
					Object name= iterator.next();
					System.out.println("����beforeBuilderDataMap===="+name+"=====================>"+beforeBuilderDataMap.get(name));
				}*/

		//����ƽ����,ռ��,ͬ��,����,����������...
		//��������Map����������
		rowHeaderRootNode = getGroupHeaderNode(this.chooseGroupRowFields, isRowGroupTiled, isRowGroupSubTotal, isCanTotal, true, isOnlyRowGroup, chooseComputeFunAndField, groupfieldorder); // ������ͷ
		colHeaderRootNode = getGroupHeaderNode(this.chooseGroupColFields, isColGroupTiled, isColGroupSubTotal, isCanTotal, false, isOnlyColGroup, chooseComputeFunAndField, groupfieldorder); // ������ͷ

		int[] li_rowLeafCountAndLevel = getLeafNodeCountAndLevel(rowHeaderRootNode); //��ͷ��Ҷ�ӽ�������������
		int[] li_colLeafCountAndLevel = getLeafNodeCountAndLevel(colHeaderRootNode); //��ͷ��Ҷ�ӽ�������������

		int li_row_leafCount = li_rowLeafCountAndLevel[0]; //������Ҷ�ӽ�������������
		int li_row_level = li_rowLeafCountAndLevel[1]; //���

		int li_col_leafCount = li_colLeafCountAndLevel[0]; //������Ҷ�ӽ�������������
		int li_col_level = li_colLeafCountAndLevel[1]; //���

		int li_all_rows = li_row_level + li_col_leafCount; //����������
		int li_all_cols = li_col_level + li_row_leafCount; //����������

		//System.out.println("��ά�ȵĲ��[" + li_row_prefix + "],��ά�ȵĲ��[" + li_col_prefix + "]"); //

		//�������ݶ���!!
		BillCellVO billCellVO = new BillCellVO(); //
		billCellVO.setTempletcode("");
		billCellVO.setTempletname("");
		billCellVO.setBillNo(""); //
		billCellVO.setRowlength(li_all_rows); //����
		billCellVO.setCollength(li_all_cols); //����

		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_all_rows][li_all_cols]; //
		for (int i = 0; i < cellItemVOs.length; i++) {
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				cellItemVOs[i][j] = new BillCellItemVO(); //
				cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA); //
				cellItemVOs[i][j].setCellrow(i);
				cellItemVOs[i][j].setCellcol(j);
				if (i == 0 && j == 0 && li_row_level <= 1) {
					cellItemVOs[i][j].setRowheight("40");//�и�!!
				} else {
					cellItemVOs[i][j].setRowheight("25");//�и�!!
				}
				if (j == 0 && li_col_level == 1) {
					cellItemVOs[i][j].setColwidth("125"); //�п�!!
				} else {
					if (!isINIT) {
						if (j < li_col_level) {
							cellItemVOs[i][j].setColwidth("80"); //�п�!!
						} else {
							cellItemVOs[i][j].setColwidth("65"); //�п�!!
						}
					} else { //initģʽ  ������Ҫ��һЩ  �ÿ�   Ԭ����  20130308
						if (j < li_col_level) {
							cellItemVOs[i][j].setColwidth("100"); //�п�!!
						} else {
							cellItemVOs[i][j].setColwidth("140"); //�п�!!
						}
					}
				}
				cellItemVOs[i][j].setSpan(null); //�ϲ�!!
				cellItemVOs[i][j].setCellvalue(""); //
			}
		}

		//�������Ͻǵı���!!!
		cellItemVOs[0][0].setSpan(li_row_level + "," + li_col_level); //���¿�������϶�����ͷ���Ĳ��!������������ͷ���Ĳ�Σ�
		cellItemVOs[0][0].setCustProperty("itemtype", "TopLeftTitle"); //����!!
		cellItemVOs[0][0].setCellvalue(getTopLeftTitle()); //���ϽǱ�������
		cellItemVOs[0][0].setBackground("255,255,160"); //
		cellItemVOs[0][0].setHalign(3); //
		cellItemVOs[0][0].setValign(3); //

		//������ͷ,���߽����ͷ!!!
		DefaultMutableTreeNode[] allLeafNodes_col = getAllLeafNodes(colHeaderRootNode); //���н��!
		HashSet hstParent_col = new HashSet(); //
		for (int i = 0; i < allLeafNodes_col.length; i++) { //��������Ҷ�ӽ��!
			int li_itemLevel = allLeafNodes_col[i].getLevel() - 1; //�㼶
			//System.out.println("��[" + i + "]��[" + li_itemLevel + "]�н�������[" + allNodes[i].getUserObject() + "]"); //
			HashVO hvo_node = (HashVO) allLeafNodes_col[i].getUserObject(); //
			String str_itemType = hvo_node.getStringValue("ItemType"); //
			cellItemVOs[li_row_level + i][li_itemLevel].setCellvalue(hvo_node.toString()); //
			cellItemVOs[li_row_level + i][li_itemLevel].setCustProperty(hvo_node.getM_hData()); //�����
			if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) { //�����С��/�ϼ�,�򱳾���ɫ�ǻ�ɫ��
				cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getTotalColor(allLeafNodes_col[i].getLevel() - 1)); //
				cellItemVOs[li_row_level + i][li_itemLevel].setSpan("1," + (li_col_level - li_itemLevel)); //����Ҫ�ϲ�
				cellItemVOs[li_row_level + i][li_itemLevel].setHalign(3); //
				cellItemVOs[li_row_level + i][li_itemLevel].setValign(2); //
			} else {
				if (str_itemType.equals("SplitCP")) { //����Ǽ�����,��ʹ�ü����е���ɫ!�������ͷ��Ϊ��,��ʱ�����л��Զ�ת�Ƶ���ͷ�ϣ�
					int li_myIndexOfParent = allLeafNodes_col[i].getParent().getIndex(allLeafNodes_col[i]); //���Ǹ��׽���еĵڼ���!!
					cellItemVOs[li_row_level + i][li_itemLevel].setBackground("245,245,245"); //
					cellItemVOs[li_row_level + i][li_itemLevel].setForeground(getComputeHeaderColor(li_myIndexOfParent)); //
				} else { //�����Level
					if (isColGroupTiled) { //�������ƽ�̵����,�򱳾���ɫȨ��һ���!
						DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_col[i].getParent(); // 
						int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //���Ǹ��׽���еĵڼ���!!
						cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getColHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getColHeaderColor(li_itemLevel)); //
					}
				}
				cellItemVOs[li_row_level + i][li_itemLevel].setHalign(2);
				cellItemVOs[li_row_level + i][li_itemLevel].setValign(2);

				//�����Ҷ�ӽ��,���ֶ�����ԤԼ��ȡά��,���г�����!
				cellItemVOs[li_row_level + i][li_itemLevel].setIshtmlhref("Y"); //
			}

			TreeNode[] parentNodes = allLeafNodes_col[i].getPath(); //ȡ�����ĸ���!
			for (int j = 1; j < parentNodes.length - 1; j++) { //�������и���
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes[j]; //
				HashVO hvo_parent = (HashVO) parentNode.getUserObject();
				cellItemVOs[li_row_level + i][j - 1].setCellvalue(hvo_parent.toString()); //����ֵ����
				cellItemVOs[li_row_level + i][j - 1].setCustProperty(hvo_parent.getM_hData()); //
				if (!hstParent_col.contains(parentNode)) { //�����û��������,��˵���ǵ�һ��,����кϲ�����!!!
					int li_thisAllLeafCount = getLeafNodeCount(parentNode); //
					cellItemVOs[li_row_level + i][j - 1].setSpan(li_thisAllLeafCount + ",1"); //
					cellItemVOs[li_row_level + i][j - 1].setHalign(2); //
					cellItemVOs[li_row_level + i][j - 1].setValign(2); //
					if (isColGroupTiled) { //�������ƽ�̵����
						int li_myIndexOfParent = parentNode.getParent().getIndex(parentNode); //���Ǹ��׽���еĵڼ���!!
						cellItemVOs[li_row_level + i][j - 1].setBackground(getColHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[li_row_level + i][j - 1].setBackground(getColHeaderColor(j - 1)); //
					}
					hstParent_col.add(parentNode); //
				}
			}
		}

		//������ͷ��Ӧ��������ͷ������������
		DefaultMutableTreeNode[] allLeafNodes_row = getAllLeafNodes(rowHeaderRootNode); //���н��!
		HashSet hstParent_row = new HashSet(); //
		for (int i = 0; i < allLeafNodes_row.length; i++) { //��������Ҷ�ӽ��!
			int li_itemLevel = allLeafNodes_row[i].getLevel() - 1; //�㼶
			HashVO hvo_node = (HashVO) allLeafNodes_row[i].getUserObject(); //
			String str_itemType = hvo_node.getStringValue("ItemType"); //
			cellItemVOs[li_itemLevel][li_col_level + i].setCellvalue(hvo_node.toString()); //
			cellItemVOs[li_itemLevel][li_col_level + i].setCustProperty(hvo_node.getM_hData()); //
			if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) { //
				cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getTotalColor(allLeafNodes_row[i].getLevel() - 1)); //
				cellItemVOs[li_itemLevel][li_col_level + i].setSpan((li_row_level - li_itemLevel) + ",1"); //
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(3);
			} else if (str_itemType.equals("FormulaLevel")) { //
				cellItemVOs[li_itemLevel][li_col_level + i].setSpan((li_row_level - li_itemLevel) + ",1"); //
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(2);
				if (isRowGroupTiled) { //�������ƽ�̵����
					DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_row[i].getParent(); // 
					int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //���Ǹ��׽���еĵڼ���!!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
				} else { //����ǲ㼶!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_itemLevel)); //
				}
				cellItemVOs[li_itemLevel][li_col_level + i].setForeground("0,0,255"); //
			} else { //�����Level����!
				if (str_itemType.equals("SplitCP")) { //����Ǽ�����,��ʹ�ü����е���ɫ!
					int li_myIndexOfParent = allLeafNodes_row[i].getParent().getIndex(allLeafNodes_row[i]); //���Ǹ��׽���еĵڼ���!!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground("245,245,245"); //
					cellItemVOs[li_itemLevel][li_col_level + i].setForeground(getComputeHeaderColor(li_myIndexOfParent)); //
				} else { //������Ǽ�����?����׼ά��!
					if (isRowGroupTiled) { //�������ƽ�̵����
						DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_row[i].getParent(); // 
						int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //���Ǹ��׽���еĵڼ���!!
						cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
					} else { //����ǲ㼶!
						cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_itemLevel)); //
					}
				}
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(2);
				//�����Ҷ�ӽ��,���ֶ�����Ԥ����ȡά��,���г�����
				cellItemVOs[li_itemLevel][li_col_level + i].setIshtmlhref("Y"); //
			}

			TreeNode[] parentNodes = allLeafNodes_row[i].getPath(); //ȡ�����ĸ���
			for (int j = 1; j < parentNodes.length - 1; j++) { //�������и���
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes[j]; //
				HashVO hvo_parent = (HashVO) parentNode.getUserObject();
				cellItemVOs[j - 1][li_col_level + i].setCellvalue(hvo_parent.toString()); //����ֵ����
				cellItemVOs[j - 1][li_col_level + i].setCustProperty(hvo_parent.getM_hData()); //
				if (!hstParent_row.contains(parentNode)) { //�����û��������,��˵���ǵ�һ��,����кϲ�����!!!
					int li_thisAllLeafCount = getLeafNodeCount(parentNode); //
					cellItemVOs[j - 1][li_col_level + i].setSpan("1," + li_thisAllLeafCount); //
					cellItemVOs[j - 1][li_col_level + i].setHalign(2); //
					cellItemVOs[j - 1][li_col_level + i].setValign(2); //

					if (isRowGroupTiled) { //�������ƽ�̵����
						int li_myIndexOfParent = 0; //
						if (parentNode.getLevel() == 2) {
							DefaultMutableTreeNode parentNode1 = (DefaultMutableTreeNode) parentNode.getParent(); //
							li_myIndexOfParent = parentNode1.getParent().getIndex(parentNode1); //���Ǹ��׽���еĵڼ���!!
						} else {
							li_myIndexOfParent = parentNode.getParent().getIndex(parentNode); //���Ǹ��׽���еĵڼ���!!
						}
						cellItemVOs[j - 1][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[j - 1][li_col_level + i].setBackground(getRowHeaderColor(j - 1)); //
					}
					hstParent_row.add(parentNode); //
				}
			}
		}

		HashMap formulaCpMap = new HashMap(); //�����洢T1,T2,T3�ȼ����е�map,��Ϊ���湫ʽ������Ҫ����
		//ʵ�ʸ�ÿ�����ݸ��Ӹ�ֵ������ʵ���Ͼ��ǽ���ͷ����ͷ������Ҷ�ӽ����������Ͽ������˻�������
		ArrayList al_hvos = new ArrayList(); //�洢����ʵ�����ݵ�HashVO
		for (int i = 0; i < allLeafNodes_col.length; i++) { //����������ͷ��Ҷ�ӽ�㣡������
			HashVO hvo_colItem = (HashVO) allLeafNodes_col[i].getUserObject(); //
			String str_realLevelNameValue_col = hvo_colItem.getStringValue("RealLevelNameValue"); //ʵ��ά��ֵ
			String str_itemType_col = hvo_colItem.getStringValue("ItemType"); //����,������Ҫ����

			HashVO hvo_realRowData = null; //ĳһʵ���е�����
			if (!(str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total"))) { //������Ǻϼ�
				hvo_realRowData = new HashVO(); //
				HashMap colGroupValueMap = tbutil.convertStrToMapByExpress(str_realLevelNameValue_col, "��", "="); //
				for (int w = 0; w < chooseGroupColFields.length; w++) { //�Ƚ�������ά����
					String str_levelValue = (String) colGroupValueMap.get(chooseGroupColFields[w]); //
					hvo_realRowData.setAttributeValue(chooseGroupColFields[w], str_levelValue == null ? "" : str_levelValue); //
					hvo_realRowData.setUserObject("bgcolor@" + chooseGroupColFields[w], getColHeaderColor(w)); //���ñ�����ɫ!
				}
			}
			for (int j = 0; j < allLeafNodes_row.length; j++) { //����������ͷ��Ҷ�ӽ�㣡������
				HashVO hvo_rowItem = (HashVO) allLeafNodes_row[j].getUserObject(); //
				String str_realLevelNameValue_row = hvo_rowItem.getStringValue("RealLevelNameValue"); //ʵ��ά��ֵ
				String str_itemType_row = hvo_rowItem.getStringValue("ItemType"); //����,������Ҫ����
				String str_realComputeField = null, str_realComputeType = null, str_realComputeConfig = null; //
				if (isOnlyRowGroup) { //���ֻ����ά��,��˵����ʱ�������Զ��Ƶ�������,��ʱ�Ͳ��ܴ��е�Ҷ�ӽ����ȡ��������,��Ӧ���Ǵ���ͷ��ȡ��!
					str_realComputeField = hvo_colItem.getStringValue("RealCPField"); //
					str_realComputeType = hvo_colItem.getStringValue("RealCPType"); //����ʹ����ͷ�ļ���,��Ϊ��ͷ���ܷ������Ѽ�������!
					str_realComputeConfig = hvo_colItem.getStringValue("RealCPConfig"); //������Ϣ 
				} else { //������Զǿ�д���ͷ��ȡ�����������ֶ�����
					str_realComputeField = hvo_rowItem.getStringValue("RealCPField"); //
					str_realComputeType = hvo_rowItem.getStringValue("RealCPType"); //����ʹ����ͷ�ļ���,��Ϊ��ͷ���ܷ������Ѽ�������!
					str_realComputeConfig = hvo_rowItem.getStringValue("RealCPConfig"); //������Ϣ
				}
				HashMap confMap = tbutil.convertStrToMapByExpress(str_realComputeConfig, ";", "="); //����������Ϣ,����Ԥ������,�Ƿ���ٷֺŵ�...

				String str_mapDataKey = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_realComputeField + "(" + str_realComputeType + ")"; //
				String str_mapDataKey2 = str_realLevelNameValue_row + str_realLevelNameValue_col + "#value"; //�洢id����ġ�����
				String real_value = "";
				BigDecimal ld_value = new BigDecimal("0");//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
				if (str_realComputeType != null && str_realComputeType.equals("init")) { // Ԭ������� ͳ����ʾ��ʼֵ  20130312
					real_value = (String) beforeBuilderDataMap.get(str_mapDataKey); //
				} else {
					ld_value = (BigDecimal) beforeBuilderDataMap.get(str_mapDataKey); //
				}
				String str_values2 = (String) beforeBuilderDataMap.get(str_mapDataKey2); //ȡ�����а󶨵�Values

				BillCellItemVO cellItemVO = new BillCellItemVO(); //��������VO
				cellItemVO.setCellvalue(""); //�ȸ���մ�
				boolean isTotalCell = (str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total") || str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total")) ? true : false; //�ж��Ƿ��ǺϼƵĸ���!
				if ((str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total")) && !isCaneTotlal(str_realComputeType)) { //������ͷ���Ǻϼ���,��ʵ������������ֶ����ǲ��ܺϼƵ�����(����avg)
					cellItemVO.setCellvalue(""); //Ϊ��  
					//��ƽ����,
				} else {
					if (str_itemType_row.equals("FormulaLevel")) { //����ǹ�ʽ��!����и��ӵĹ�ʽ����!���������ͬһ�У�
						String[] str_containGroups = (String[]) hvo_rowItem.getObjectValue("containgroups"); //
						String str_LevelFormula = hvo_rowItem.getStringValue("LevelFormula"); //
						String str_LevelConf = hvo_rowItem.getStringValue("LevelConfig"); //ά�ȵĲ���
						cellItemVO.setCustProperty("ContainGroups", str_containGroups); //������Щ�飡��
						cellItemVO.setCustProperty("LevelFormula", str_LevelFormula); //������Щ�飡��

						//�ȷֽ���ͷ��,Ȼ����һ�����ά���滻��һ�����İ�����,Ȼ��һ����ȡֵ(��������Map�е�ֵ),����һ��HashMap��!(Key�����ֵ,ֵ����ʵ��ֵ!)
						//ȡ�����㹫ʽ!Ȼ�󽫹�ʽ�е�{}�а����������滻����һ��Map�еõ�������!!ȡ�����ĵ��㴦��!
						String[] str_rowLevels = tbutil.split(str_realLevelNameValue_row, "��"); //
						String str_lastLevel = str_rowLevels[str_rowLevels.length - 1]; //
						HashMap allOtherGroupValueMap = new HashMap(); //�����������ֵ!
						for (int k = 0; k < str_containGroups.length; k++) { //������������ά��!
							String str_oneGroupKey = tbutil.replaceAll(str_mapDataKey, str_lastLevel, hvo_rowItem.getStringValue("levelname") + "=" + str_containGroups[k]); //�滻!!!��mapkey�еġ����յȼ�=����1���滻�ɡ����յȼ�=�߷��ա�
							String str_oneGroupValue = trimDoubleToStr((Double) beforeBuilderDataMap.get(str_oneGroupKey)); //ȡ��ĳ�����ֵ!
							if (str_oneGroupValue.equals("")) {
								str_oneGroupValue = "0"; //
							}
							allOtherGroupValueMap.put(str_containGroups[k], str_oneGroupValue); //���������ֵ�Ž���!��ʵ�����е�ֵ!���߷��ա�=��30��
						}

						//����ʽ�滻!
						try {
							String str_LevelFormula_convert = str_LevelFormula; //
							String[] str_formulaKeys = tbutil.getFormulaMacPars(str_LevelFormula_convert, "{", "}"); //
							for (int k = 0; k < str_formulaKeys.length; k++) {
								String str_mapItemValue = (String) allOtherGroupValueMap.get(str_formulaKeys[k]); //
								if (str_mapItemValue == null || str_mapItemValue.trim().equals("")) { //���Ϊ�ջ�մ�,���㴦��!
									str_mapItemValue = "0";
								}
								str_LevelFormula_convert = tbutil.replaceAll(str_LevelFormula_convert, "{" + str_formulaKeys[k] + "}", str_mapItemValue); //�滻!!
							}

							//Ȼ��ʹ�ù�ʽ������м���!! �����������ֵ������������У�
							Double jepValue = (Double) getJepParse().execFormula(str_LevelFormula_convert); //��(0*100)/(0+0+0+0)����(0*100)/(0+1+0+0)��18+22
							if (!"NaN".equalsIgnoreCase("" + jepValue)) { //��������Ϊ����򲻸�ֵ!
								cellItemVO.setCellvalue(trimDoubleToStr(jepValue, null, tbutil.convertStrToMapByExpress(str_LevelConf, ";", "="))); //�����飡��
							}
							cellItemVO.setCustProperty("LevelFormulaParse", "��" + str_LevelFormula + "��=>��" + str_LevelFormula_convert + "��=>��" + jepValue + "��"); //��ʽ�����������!�����뵱ǰ�е�������֤!
						} catch (Exception ex) {
							String str_exmsg = "���㹫ʽ��[" + str_LevelFormula + "]�����쳣:<" + ex.getClass().getName() + ">" + ex.getMessage();
							System.err.println(str_exmsg); //
							cellItemVO.setCustProperty("LevelFormulaParse", str_exmsg); //
						}
					} else { //�����׼����!�����ȡ��!
						//��ǰ��ƽ����,��¼��ռ��,���ռ��,���߲��,���߱���,ͬ��,����,�����ں�̨�����,���������ں�����������ӵ��߼��ǲ��е�(����С�Ƶ�ƽ����,�ٷֱ��Ǻ���֮��ļ���!),
						if (str_realComputeType != null & str_realComputeType.equals("init")) { // Ԭ������� ͳ����ʾ��ʼֵ  20130312
							cellItemVO.setCellvalue(real_value == null ? "" : real_value.toString()); //������������������ֵ�ĵط��������   //20120909  Ԭ�����޸ģ�����Ҫ��Ϊ�յĵط�ͳ�ƵĽ������ʾ0
						} else if (str_realComputeType.equals("count") || str_realComputeType.equals("sum")) {
							//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							cellItemVO.setCellvalue(ld_value == null ? "0" : trimBigDecimalToStr(ld_value, str_realComputeType, confMap)); //������������������ֵ�ĵط��������   //20120909  Ԭ�����޸ģ�����Ҫ��Ϊ�յĵط�ͳ�ƵĽ������ʾ0
						} else if (str_realComputeType.equals("avg")) { //ƽ����!   // ƽ����ʱ������ �Ժ���޸ĸù���  20130312
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //ʵ�ʼ�����ֶ���,��Ϊ��ǰ�ļ������ǡ���¼��-ռ�ȡ�,ʵ������Ҫȡǰ��ġ���¼����������
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							String str_key_thissum = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //

							//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_thissum = (BigDecimal) beforeBuilderDataMap.get(str_key_thissum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_thissum == null) {
								ld_value_thissum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("ƽ�������㹫ʽ", "" + ld_value_thissum + "/" + ld_value_thiscount); //
							if (ld_value_thiscount.compareTo(new BigDecimal("0")) != 0) { //���ܷ�������Ϊ������!!
								ld_value = ld_value_thissum.divide(ld_value_thiscount, 2, BigDecimal.ROUND_HALF_UP); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_value, str_realComputeType, confMap)); //�õ�ǰֵ���������ұߵĺϲ�ά�ȼ���!
							}
						} else if (str_realComputeType.equals("PercentCount")) { //��¼��ռ��!
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //ʵ�ʼ�����ֶ���,��Ϊ��ǰ�ļ������ǡ���¼��-ռ�ȡ�,ʵ������Ҫȡǰ��ġ���¼����������
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							String str_key_countsum = str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							if ((str_realLevelNameValue_row.equals("") && !isTotalCell) || chooseGroupRowFields.length == 0) { //���û����ͷά��,����ʾֱ�Ӽ��ŵ�,��ʱӦǿ���Զ�ȡ����!���ϼƸ��Ӳ���!
								str_key_countsum = "@" + str_trim_cpfield + "(count)"; //
							}
							//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_countsum = (BigDecimal) beforeBuilderDataMap.get(str_key_countsum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_countsum == null) {
								ld_value_countsum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("��¼��ռ�ȵ�ʵ�ʼ������", "" + ld_value_thiscount + "/" + ld_value_countsum); //
							if (ld_value_countsum.compareTo(new BigDecimal("0")) != 0) { //���ܷ�������Ϊ������!!
								BigDecimal ld_percent = (ld_value_thiscount.multiply(new BigDecimal("100")).divide(ld_value_countsum, 2, BigDecimal.ROUND_HALF_UP)); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_percent, str_realComputeType, confMap)); //�õ�ǰֵ���������ұߵĺϲ�ά�ȼ���!
							}
						} else if (str_realComputeType.equals("PercentSum")) { //���ռ��!!
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //ʵ�ʼ�����ֶ���,��Ϊ��ǰ�ļ������ǡ���¼��-ռ�ȡ�,ʵ������Ҫȡǰ��ġ���¼����������
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //
							String str_key_countsum = str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //
							if ((str_realLevelNameValue_row.equals("") && !isTotalCell) || chooseGroupRowFields.length == 0) { //���û����ͷά��,����ʾֱ�Ӽ��ŵ�,��ʱӦǿ���Զ�ȡ����!���ϼƸ��Ӳ���!
								str_key_countsum = "@" + str_trim_cpfield + "(sum)"; //
							}
							//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_countsum = (BigDecimal) beforeBuilderDataMap.get(str_key_countsum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_countsum == null) {
								ld_value_countsum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("���ռ�ȵ�ʵ�ʼ������", "" + ld_value_thiscount + "/" + ld_value_countsum); //
							if (ld_value_countsum.compareTo(new BigDecimal("0")) != 0) { //���ܷ�������Ϊ������!!
								BigDecimal ld_percent = (ld_value_thiscount.multiply(new BigDecimal("100")).divide(ld_value_countsum, 2, BigDecimal.ROUND_HALF_UP)); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_percent, str_realComputeType, confMap)); //�õ�ǰֵ���������ұߵĺϲ�ά�ȼ���!
							}
						} else if (str_realComputeType.equals("FormulaCompute")) { //��ʽ���㣡������{T1}+{T2}
							String str_formula_cp = (String) confMap.get("��ʽ"); //�����еĹ�ʽ!
							if (str_formula_cp != null && !str_formula_cp.trim().equals("")) { //��������˼����й�ʽ!
								cellItemVO.setCustProperty("FormulaCompute", str_formula_cp); //��ʽ�����������!�����뵱ǰ�е�������֤!
								try {
									String str_formula_cp_convert = str_formula_cp; //
									String str_level_prefix = str_realLevelNameValue_row + str_realLevelNameValue_col; //Ϊ�˹�ʽ����,����Ѱ��ά��������ͬ�ĸ���,��ǰ���һ��!
									String[] str_formulaKeys = tbutil.getFormulaMacPars(str_formula_cp_convert, "{", "}"); //�ھ�����Ű����Ķ��䣡����
									for (int k = 0; k < str_formulaKeys.length; k++) { //�������ж���
										String str_mapItemValue = (String) formulaCpMap.get(str_level_prefix + "@" + str_formulaKeys[k]); //��Ѱ�ҡ�ҵ������=����ҵ��@T1��
										if (str_mapItemValue == null || str_mapItemValue.trim().equals("")) { //���Ϊ�ջ�մ�,���㴦��!
											str_mapItemValue = "0";
										}
										if (str_mapItemValue.endsWith("%")) {
											str_mapItemValue = str_mapItemValue.substring(0, str_mapItemValue.length() - 1); //����ǰٷֱȵ���ֵ,��õ��ٷֺ�,����ʽ����϶�����!!
										}
										str_formula_cp_convert = tbutil.replaceAll(str_formula_cp_convert, "{" + str_formulaKeys[k] + "}", str_mapItemValue); //�滻!!������{T1}+{T2}���滻�ɡ�25+86��
									}
									//Ȼ��ʹ�ù�ʽ������м���!! �����������ֵ������������У�
									Double jepValue = (Double) getJepParse().execFormula(str_formula_cp_convert); //��(0*100)/(0+0+0+0)����(0*100)/(0+1+0+0)��18+22
									if (!"NaN".equalsIgnoreCase("" + jepValue)) { //��������Ϊ����򲻸�ֵ!
										cellItemVO.setCellvalue(trimDoubleToStr(jepValue, null, confMap)); //�����飡��
									}
									cellItemVO.setCustProperty("FormulaComputeParse", "��" + str_formula_cp + "��=>��" + str_formula_cp_convert + "��=>��" + jepValue + "��"); //��ʽ�����������!�����뵱ǰ�е�������֤!
								} catch (Exception ex) {
									String str_exmsg = "���㹫ʽ�����쳣:" + ex.getClass().getName() + "," + ex.getMessage(); //
									System.err.println(str_exmsg); //
									cellItemVO.setCustProperty("FormulaComputeParse", str_exmsg); //
								}
							} else {
								cellItemVO.setCustProperty("FormulaComputeParse", "��չ������û�ж���\"��ʽ\",���Բ������κμ���!"); //
							}
						} else if (str_realComputeType.endsWith("ChainIncrease") || str_realComputeType.endsWith("PeriodIncrease")) { //���С�ͬ��/���ȡ������ʣ�����
							ld_value = setCellItemVOByIncrese(cellItemVO, str_realComputeField, str_realLevelNameValue_row, str_realLevelNameValue_col, str_realComputeType, confMap); //
						} else if (str_realComputeType.endsWith("ChainSeries") || str_realComputeType.endsWith("PeriodSeries")) { //���С�ͬ��/���ȡ���������������
							ld_value = setCellItemVOByPeriodSeries(cellItemVO, str_realComputeField, str_realLevelNameValue_row, str_realLevelNameValue_col, str_realComputeType, confMap); //
						} else {
							cellItemVO.setCustProperty("FormulaComputeParse", "δ֪�ļ�������:" + str_realComputeType); //
						}
					}
				}

				//Ϊ��֧�ֹ�ʽ����,�����ٸ�һ����ϣ��,Ȼ��ר��ʹ��T1,T1,T3��Ϊkey���洢!!!һ��Ҫ�ڼ����ִ��!������Ŀ�����ǰ��ģ���ǰ��Ĳ����ú���ģ�����T4�еĹ�ʽ����ʹ��T1,T2,T3,��T2����ʹ��T4
				if (str_itemType_row.equals("SplitCP")) { //����Ƿ��ѵ�!
					String str_cpindexno = hvo_rowItem.getStringValue("CpIndexNo"); //�����е������ţ�����
					String str_formulaKey = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + "T" + str_cpindexno; //����ҵ������=����ҵ��@T1��
					formulaCpMap.put(str_formulaKey, cellItemVO.getCellvalue()); //ֱ�ӵ�ֵ,�����ǰٷֱ�,����35%,��ʱ��ʽ����ʱ����Ҫ�Ƚ��ٷֺŽص�������
					cellItemVO.setCustProperty("FormulaMapKey", str_formulaKey); //Ϊ�˵��Է���,���������������Ҳ���������Key
				}
				cellItemVO.setHalign(3); //
				cellItemVO.setCustProperty("MapKey", str_mapDataKey); //
				cellItemVO.setCustProperty("#value", str_values2); //
				if (isTotalCell) { //�����С�ƻ�ϼ���??
					int li_colPathDepth = 0; //
					int li_rowPathDepth = 0; //
					if (str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total")) {
						li_colPathDepth = allLeafNodes_col[i].getLevel() - 1; //	
					}
					if (str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total")) {
						li_rowPathDepth = allLeafNodes_row[j].getLevel() - 1; //
					}
					int li_pathDepth = (li_colPathDepth < li_rowPathDepth ? li_rowPathDepth : li_colPathDepth); //ȡС�Ĳ㼶!!!
					cellItemVO.setBackground(getTotalColor(li_pathDepth)); //����ϼƵĸ���,���ɫ!
					cellItemVO.setCustProperty("itemtype", "ComputeTotal"); //������ӵĺϼ�!
				} else {
					cellItemVO.setCustProperty("itemtype", "ComputeCell"); //������ӵĺϼƣ���
					if (!str_itemType_row.equals("FormulaLevel")) { //����ǹ�ʽ������,���޷����г�����
						cellItemVO.setIshtmlhref("Y"); //�г�������	
					}

					//�����޷���ȡ��ϸ���ӳ��� �����/2013-06-20��
					/*if (str_values2==null||str_values2.equals("")) { 
						cellItemVO.setIshtmlhref("N"); 
					}*/

					//������ֵ,������������Ŀ��Ԥ������,��ֱ�Ӳ�ѯʱ����ʾ������ֵ!
					if (ld_value != null && confMap.containsKey("�������")) { //����о������������ԣ���
						String str_warnRule = (String) confMap.get("�������"); //����Ҫ���һ���ǳ���׳�׶��ĵ�Ԥ�������﷨,Ȼ�����һ�£���
						if (str_warnRule != null) { //��������˾���ֵ!
							String[] str_warnValues = tbutil.split(str_warnRule, ","); //ʹ�÷ֺ����!
							for (int w = 0; w < str_warnValues.length; w++) {
								String str_warnItem = str_warnValues[w]; //����֧�ֹ�ʽ������,�����ǡ�{ƽ����}*1.5��
								double ld_warnValue = getJepFormulaValue(str_warnItem); //ת��double
								//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
								if (ld_value.compareTo(new BigDecimal(ld_warnValue)) > 0) { //�����ǰֵ���ھ���ֵ,�򾯸�ɫ��ʾ!�Ժ�������ܸ������֧��С�ڼ��㷽��������Ĭ�����Ǵ��ڼ��㣡
									cellItemVO.setBackground(getWarnColor(w)); //��ɫ��ʾ!!!
									break; //����ƥ��ǰ��ľ���ֵ,��������߾�����ֵ��,����Ĳ��ټ����ˣ�
								}
							}
						}
					}
				}

				cellItemVO.setCustProperty("RowHeaderLevelNameValue", str_realLevelNameValue_row); //��ͷά����ֵ!
				cellItemVO.setCustProperty("ColHeaderLevelNameValue", str_realLevelNameValue_col); //��ͷά����ֵ!
				cellItemVO.setCustProperty("RealCPField", str_realComputeField); //�����ֶΣ���
				cellItemVO.setCustProperty("RealCPType", str_realComputeType); //�������ͣ���
				cellItemVO.setCustProperty("RealCPConfig", str_realComputeConfig); //�������ͣ���

				if (hvo_realRowData != null && !(str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total"))) { //������Ǻϼ�!
					String str_hvoitemKey = getPathName(allLeafNodes_row[j]); //
					hvo_realRowData.setAttributeValue(str_hvoitemKey, cellItemVO.getCellvalue()); //����ֵ!
					hvo_realRowData.setUserObject("bgcolor@" + str_hvoitemKey, cellItemVO.getBackground()); //���ñ�����ɫ!
				}

				cellItemVOs[li_row_level + i][li_col_level + j] = cellItemVO; ////
			}

			if (hvo_realRowData != null) {
				al_hvos.add(hvo_realRowData); //
			}
		}
		billCellVO.setCellItemVOs(cellItemVOs); //
		HashVO[] hvs_realData = (HashVO[]) al_hvos.toArray(new HashVO[0]); //

		if (secondHashVOComputeMap != null) { //�����Ƕ����˵�
			String str_orderindex = (String) secondHashVOComputeMap.get("���ڼ�λ����������"); //
			if (str_orderindex != null && !str_orderindex.equals("")) { //�Ƿ�ָ������������??
				int li_cpIndex = Integer.parseInt(str_orderindex); //�������ݼ���,����HashVO��������ֶ�!
				if (hvs_realData.length > 0) { //����
					String[] str_hvkeys = hvs_realData[0].getKeys(); //
					if (str_hvkeys.length >= chooseGroupColFields.length + li_cpIndex) {
						String str_sortKey = str_hvkeys[chooseGroupColFields.length + li_cpIndex - 1]; //
						tbutil.sortHashVOs(hvs_realData, new String[][] { { str_sortKey, "Y", "Y" } }); //��һ��!
					}
				}
			}
			String[] str_hiddcols = (String[]) secondHashVOComputeMap.get("���ʱ���ص���"); ////
			if (str_hiddcols != null && str_hiddcols.length > 0) {
				tbutil.removeHashVOItems(hvs_realData, str_hiddcols); //��ȥһЩ��!�����ʴ������о�ǿ��Ҫ���·ݲ�Ҫ��ʾ!
			}
		}

		//���ݹ���õ����ݶ��󴴽����!
		cellPanel = new BillCellPanel(billCellVO); //
		cellPanel.setEditable(false); //���ɱ༭
		cellPanel.addBillCellHtmlHrefListener(this); //
		cellPanel.setAllowShowPopMenu(false); //�Ƚ��õ�ԭ�����Ҽ�����!
		cellPanel.getTable().addMouseListener(new MouseAdapter() { //�����Լ����Ҽ�����!
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON3) {
							popShowRightMenu((JComponent) e.getSource(), e.getPoint()); //�Ҽ�������ʾ�˵�!
						}
					}
				}); //
		cellPanel.getTable().setToolTipText("��ʾ:�Ҽ��С���ȡ�������ˡ��ȸ��๦��"); //
		cellPanel.setCrystal(true); //͸��!!!

		this.removeAll();
		this.setOpaque(false); // ͸��!!
		this.setLayout(new BorderLayout(0, 0)); //

		if (secondHashVOComputeMap == null || secondHashVOComputeMap.size() <= 0) { //���û�ж�����ʾHashVO��Map,��ԭ���Ĵ���! ���Ǿ���������!
			this.add(cellPanel, BorderLayout.CENTER); //���м����ݼ���!!
		} else { //���������,�����Ҫʹ��˫ҳǩ!
			BillCellPanel hvoCell = new BillCellPanel(hvs_realData); //
			hvoCell.setEditable(false); //���ɱ༭
			hvoCell.setAllowShowPopMenu(false); //�Ƚ��õ�ԭ�����Ҽ�����!
			hvoCell.setCrystal(true); //͸��!
			if (ClientEnvironment.isAdmin()) { //����ǹ���Ա���,��ʹ������ҳǩ,����Ϊ��Debugʱ����
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM); //
				JPanel tmpPanel_1 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				tmpPanel_1.add(hvoCell); //
				JPanel tmpPanel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				tmpPanel_2.add(cellPanel); //
				tabbedPane.addTab("HashVO����", tmpPanel_1); //
				tabbedPane.addTab("Cell����", tmpPanel_2); //
				this.add(tabbedPane, BorderLayout.CENTER); //���м����ݼ���!!
			} else { //������ǹ���Ա,��ֱ�������HashVO
				this.add(hvoCell, BorderLayout.CENTER); //���м����ݼ���!!
			}
		}
		this.add(getNorthBtnPanel(), BorderLayout.NORTH); //���Ϸ����밴ť���!!

		this.updateUI(); //
	}

	/**
	 * ��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
	 * ������¼��,���,ƽ�������ġ�����,ͬ�ȡ�����
	 * @param cellItemVO
	 * @param str_realComputeField
	 * @param str_realLevelNameValue_row
	 * @param str_realLevelNameValue_col
	 * @param _computeType
	 */
	private BigDecimal setCellItemVOByIncrese(BillCellItemVO cellItemVO, String str_realComputeField, String str_realLevelNameValue_row, String str_realLevelNameValue_col, String _computeType, HashMap _confMap) {
		String[] str_thidDatePeriodValue = getThisDatePeriodValue(chooseGroupRowFields, chooseGroupColFields, str_realLevelNameValue_row, str_realLevelNameValue_col); //����ȡ��ʱ��ά��!���Ƿ����ʱ��ά��!�������,��ſ��Խ���
		if (str_thidDatePeriodValue[0] == null) {
			cellItemVO.setCustProperty("���㵱ǰά��ֵʱʧ��", "" + str_thidDatePeriodValue[1]);
			return null; //ֱ�ӷ���!
		}

		String str_dateGroupName = str_thidDatePeriodValue[0]; //
		String str_thisDatePeriodValue = str_thidDatePeriodValue[1];
		String str_thisDateType = str_thidDatePeriodValue[2]; //����/�¶�
		String str_fromRowCol = str_thidDatePeriodValue[3]; //row/col   

		String str_frontPeriod = getPeriodDateValue(str_thisDatePeriodValue, str_thisDateType, _computeType); //ȡ����һ��ʱ������
		String str_newMapKey = null; //
		if (str_fromRowCol.equals("row")) { //���������ͷ�Ϸ��ֵ�!
			str_newMapKey = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod) + str_realLevelNameValue_col; //
		} else if (str_fromRowCol.equals("col")) { //
			str_newMapKey = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod); //
		}

		String[] str_cpValue = getOneDatePeriodValue(str_newMapKey, str_realComputeField, str_realLevelNameValue_row + str_realLevelNameValue_col, _computeType); //
		cellItemVO.setCustProperty("��ʽ������ʵ�ʽ��", "" + str_cpValue[0]);
		cellItemVO.setCustProperty("��һʱ������{" + str_frontPeriod + "}�ļ��㹫ʽ", str_cpValue[1]); //ʵ�ʹ�ʽ
		cellItemVO.setCustProperty("��һʱ������{" + str_frontPeriod + "}��ȡ��MapKey", str_cpValue[2]); //
		if (str_cpValue[0] != null && !"NaN".equalsIgnoreCase(str_cpValue[0]) && !"Infinity".equalsIgnoreCase(str_cpValue[0])) { //������ǳ���Ϊ��������
			cellItemVO.setCellvalue(trimBigDecimalToStr(new BigDecimal(str_cpValue[0]), _computeType, _confMap)); //��ֵ����
			return new BigDecimal(str_cpValue[0]); //
		} else {
			return null;
		}
	}

	/**
	 * //��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
	 * ����������������!! ���鷳����,����Ҫ������ǰ����,�����Ҳ�������,��ֹͣ!
	 * @param cellItemVO
	 * @param str_realComputeField
	 * @param str_realLevelNameValue_row
	 * @param str_realLevelNameValue_col
	 * @param _computeType
	 * @param _confMap
	 */
	private BigDecimal setCellItemVOByPeriodSeries(BillCellItemVO cellItemVO, String str_realComputeField, String str_realLevelNameValue_row, String str_realLevelNameValue_col, String _computeType, HashMap _confMap) {
		String[] str_thidDatePeriodValue = getThisDatePeriodValue(chooseGroupRowFields, chooseGroupColFields, str_realLevelNameValue_row, str_realLevelNameValue_col); //����ȡ��ʱ��ά��!���Ƿ����ʱ��ά��!�������,��ſ��Խ���
		if (str_thidDatePeriodValue[0] == null) {
			cellItemVO.setCustProperty("���㵱ǰά��ֵʱʧ��", "" + str_thidDatePeriodValue[1]);
			return null; //ֱ�ӷ���!
		}

		String str_dateGroupName = str_thidDatePeriodValue[0]; //
		String str_thisDatePeriodValue = str_thidDatePeriodValue[1];
		String str_thisDateType = str_thidDatePeriodValue[2]; //����/�¶�
		String str_fromRowCol = str_thidDatePeriodValue[3]; //row/col   
		String str_cycleDatePeriodValue = str_thisDatePeriodValue; //ѭ��ʹ�õ����ڱ���,������ѭ���в��ϸ������ֵ������

		int li_count = 0;
		//��ѭ��!,��һ������������ǰ��,ֱ���ҵ�һ�������ڵ�ʱ��ά��,����ά��ֵС��0,���˳��������������򲻶��ۼ�!!
		//�Ժ�Ϊ���������,Ӧ�ø��map,��¼��ǰ��������!��������3���ȵ���������ʱ,��ʵǰ���2�����Ѿ��������! ���Ҹ�������������ͬʱ����������������������! ��ʱ����������ܵ�Ч�������ԣ�����Ϊʱ���ϵ,�˹����Ժ���Ū������
		StringBuilder sb_allPriods = new StringBuilder(); //��¼���м������ά��! 
		while (1 == 1) {
			//���ҳ���ǰ��!
			String str_newMapKey1 = null; //
			if (str_fromRowCol.equals("row")) { //���������ͷ�Ϸ��ֵ�!
				str_newMapKey1 = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_cycleDatePeriodValue) + str_realLevelNameValue_col; //
			} else if (str_fromRowCol.equals("col")) { //
				str_newMapKey1 = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_cycleDatePeriodValue); //
			}

			//���ҳ�ǰһ���ڼ��!
			String str_frontPeriod = getPeriodDateValue(str_cycleDatePeriodValue, str_thisDateType, _computeType); //ȡ�ø�ʱ�����һ��ʱ������!����Զ����ȡ����!
			String str_newMapKey2 = null; //
			if (str_fromRowCol.equals("row")) { //���������ͷ�Ϸ��ֵ�!
				str_newMapKey2 = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod) + str_realLevelNameValue_col; //
			} else if (str_fromRowCol.equals("col")) { //
				str_newMapKey2 = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod); //
			}

			//����!!!
			String[] str_cpValue = getOneDatePeriodValue(str_newMapKey2, str_realComputeField, str_newMapKey1, _computeType); //ȡ������һ���ڵ�ʵ�ʱ���,�����¼������!
			sb_allPriods.append("��" + str_cycleDatePeriodValue + "��=��" + trimDoubleToStr(str_cpValue[0]) + "��"); ////

			if (str_cpValue[0] == null || str_cpValue[0].trim().equals("") || "NaN".equalsIgnoreCase(str_cpValue[0]) || "Infinity".equalsIgnoreCase(str_cpValue[0])) { //���Ϊ��,���˳�ѭ��!
				break; //
			}

			if (Double.parseDouble(str_cpValue[0]) <= 0) { //������С��0���˳�
				break; //
			}

			li_count = li_count + 1; //��1
			str_cycleDatePeriodValue = str_frontPeriod; //���¸�ֵ,��ɵݹ��ٴ�С�ݵ�Ч��,����������ѭ��������
		}

		cellItemVO.setCellvalue("" + li_count); //
		cellItemVO.setCustProperty("���м������ά��", sb_allPriods.toString()); //
		return new BigDecimal(li_count); //
	}

	//ȡ��ʱ��ά��,ֻ�ҵ���һ������!
	private String[] getThisDatePeriodValue(String[] _rowGroups, String[] _colGroups, String _realLevelNameValue_row, String _realLevelNameValue_col) {
		if (this.dateGroupDefineMap != null) { //���������
			if (_rowGroups != null && _rowGroups.length > 0) {
				for (int i = _rowGroups.length - 1; i >= 0; i--) { //��������!
					if (dateGroupDefineMap.containsKey(_rowGroups[i])) {
						HashMap map = tbutil.convertStrToMapByExpress(_realLevelNameValue_row, "��", "="); //�ָ�!
						String str_value = (String) map.get(_rowGroups[i]); //ά��ֵ!����:2012��1����!!!
						if (str_value == null || str_value.trim().equals("") || str_value.equals("����ֵ��")) { //�ر�Ҫע��ʱ��ά��ֵ���ܴ��ڡ���ֵ�������(���������û��¼�롰����ʱ�䡱),�������϶�����!
							return new String[] { null, "ȡ�õı�ʱ��ά��ֵΪ��,�����޷���������" }; //
						} else {
							return new String[] { _rowGroups[i], str_value, (String) dateGroupDefineMap.get(_rowGroups[i]), "row" }; //������ͷ���ҵ���!
						}
					}
				}
			}

			if (_colGroups != null && _colGroups.length > 0) {
				for (int i = _colGroups.length - 1; i >= 0; i--) { //��������!
					if (dateGroupDefineMap.containsKey(_colGroups[i])) {
						HashMap map = tbutil.convertStrToMapByExpress(_realLevelNameValue_col, "��", "="); //�ָ�!
						String str_value = (String) map.get(_colGroups[i]); //ά��ֵ!����:2012��1����
						if (str_value == null || str_value.trim().equals("") || str_value.equals("����ֵ��")) { //�ر�Ҫע��ʱ��ά��ֵ���ܴ��ڡ���ֵ�������(���������û��¼�롰����ʱ�䡱),�������϶�����!
							return new String[] { null, "ȡ�õı�ʱ��ά��ֵΪ��,�����޷���������" }; //
						} else {
							return new String[] { _colGroups[i], str_value, (String) dateGroupDefineMap.get(_colGroups[i]), "col" }; //������ͷ���ҵ���
						}
					}
				}
			}
			return new String[] { null, "��ǰά����û��һ����ʱ��ά��,Ҫô���ڹ������ķ���getDateGroupDefineMap()��û����,Ҫô����ѡ��/�������ά����û��" };
		} else {
			return new String[] { null, "��������û�ж����ļ���ά����ʱ��ά��,�붨��getDateGroupDefineMap()" };
		}
	}

	/**
	 * ȡ��ĳһ��ʱ��ά�ȵ�ֵ,�ڽ���ͬ��,����,������������ʱ����Ҫ�õ�!
	 * @return
	 */
	private String[] getOneDatePeriodValue(String _datePeriodMapKey, String str_realComputeField, String _thisMapKey, String _computeType) {
		String str_trim_cpfield = str_realComputeField;
		if (str_realComputeField.indexOf("-") > 0) {
			str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //ʵ�ʼ�����ֶ���,��Ϊ��ǰ�ļ������ǡ���¼��-ռ�ȡ�,ʵ������Ҫȡǰ��ġ���¼����������
		}

		//��ʱ�������ڵ�count��sum
		String str_key_this_count = _thisMapKey + "@" + str_trim_cpfield + "(count)"; //
		String str_key_this_sum = _thisMapKey + "@" + str_trim_cpfield + "(sum)"; //

		//��һʱ�������ڵ�count��sum
		String str_key_frontPeriod_count = _datePeriodMapKey + "@" + str_trim_cpfield + "(count)"; //��һ���ڵ�count���㣡��
		String str_key_frontPeriod_sum = _datePeriodMapKey + "@" + str_trim_cpfield + "(sum)"; //��һ���ڵ�sum���㣡��
		String str_key_frontPeriod_avg = _datePeriodMapKey + "@" + str_trim_cpfield + "(avg)"; //��һ���ڵ�sum���㣡��

		if (!beforeBuilderDataMap.containsKey(str_key_frontPeriod_count)) { //����������������key,��˵�����ʱ��ά���¸���û��ֵ!
			return new String[] { null, "��û��ȡ��ֵ���޹�ʽ", "����û�����MapKey��" + str_key_frontPeriod_count + "��ֵ" }; //
		}

		//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
		//ȡֵ!
		BigDecimal ld_value_this_count = (BigDecimal) beforeBuilderDataMap.get(str_key_this_count); //
		BigDecimal ld_value_this_sum = (BigDecimal) beforeBuilderDataMap.get(str_key_this_sum); //
		BigDecimal ld_value_frontPeriod_count = (BigDecimal) beforeBuilderDataMap.get(str_key_frontPeriod_count); //
		BigDecimal ld_value_frontPeriod_sum = (BigDecimal) beforeBuilderDataMap.get(str_key_frontPeriod_sum); //
		ld_value_this_count = (ld_value_this_count == null ? new BigDecimal("0") : ld_value_this_count);
		ld_value_this_sum = (ld_value_this_sum == null ? new BigDecimal("0") : ld_value_this_sum);
		ld_value_frontPeriod_count = (ld_value_frontPeriod_count == null ? new BigDecimal("0") : ld_value_frontPeriod_count);
		ld_value_frontPeriod_sum = (ld_value_frontPeriod_sum == null ? new BigDecimal("0") : ld_value_frontPeriod_sum);

		String str_formula = null; //
		String str_mapkeyInfo = null; //

		BigDecimal value = new BigDecimal("0");
		if (_computeType.startsWith("Count")) { //�����Count���͵Ļ��Ȼ�ͬ��!
			str_formula = "((" + ld_value_this_count + "-" + ld_value_frontPeriod_count + ")*100)/" + ld_value_frontPeriod_count; //
			value = ld_value_this_count.subtract(ld_value_frontPeriod_count).multiply(new BigDecimal("100")).divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_count; //
		} else if (_computeType.startsWith("Sum")) { //�����Sum���͵Ļ��Ȼ�ͬ��
			str_formula = "((" + ld_value_this_sum + "-" + ld_value_frontPeriod_sum + ")*100)/" + ld_value_frontPeriod_sum; //
			value = ld_value_this_sum.subtract(ld_value_frontPeriod_sum).multiply(new BigDecimal("100")).divide(ld_value_frontPeriod_sum, 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_sum; //
		} else if (_computeType.startsWith("Avg")) { //�����ƽ�������͵Ļ��Ȼ�ͬ��!
			str_formula = "((" + ld_value_this_sum + "/" + ld_value_this_count + "-" + ld_value_frontPeriod_sum + "/" + ld_value_frontPeriod_count + ")*100)/(" + ld_value_frontPeriod_sum + "/" + ld_value_frontPeriod_count + ")"; //
			value = ld_value_this_sum.divide(ld_value_this_count, 2, BigDecimal.ROUND_HALF_UP).subtract((ld_value_frontPeriod_sum.divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal("100")).divide((ld_value_frontPeriod_sum.divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP)), 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_avg; //
		}
		//Double jepValue = (Double) getJepParse().execFormula(str_formula); //��(0*100)/(0+0+0+0)����(0*100)/(0+1+0+0)��18+22
		if (value.compareTo(new BigDecimal(0)) == 0) {
			value = new BigDecimal("0");
		} else {
			value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return new String[] { "" + value, str_formula, str_mapkeyInfo }; //
	}

	//ȡ����һ���ڼ�,���統ǰ�ڼ���2012��06��,��һ�ھ���2012��05��
	private String getPeriodDateValue(String _thisPeriod, String _type, String _tbhb) {
		if (_thisPeriod == null || _thisPeriod.trim().equals("")) {
			return _thisPeriod;
		}
		int li_year = Integer.parseInt(_thisPeriod.substring(0, 4)); //
		if (_tbhb.endsWith("ChainIncrease") || _tbhb.endsWith("ChainSeries")) { //����ǻ���,�鷳һ��
			if (_type.equals("����") || _type.equals("��")) { //����Ǽ���
				int li_season = Integer.parseInt(_thisPeriod.substring(5, 6)); //
				li_season = li_season - 1; //
				if (li_season == 0) {
					li_season = 4; //
					li_year = li_year - 1; //
				}
				return "" + li_year + "��" + li_season + "����"; //
			} else if (_type.equals("�¶�") || _type.equals("��")) { //������¶�
				int li_month = Integer.parseInt(_thisPeriod.substring(5, 7)); //
				li_month = li_month - 1; //
				if (li_month == 0) {
					li_month = 12; //
					li_year = li_year - 1; //
				}
				return "" + li_year + "��" + ("" + (100 + li_month)).substring(1, 3) + "��"; //
			} else if (_type.equals("���") || _type.equals("��")) {
				return "" + (li_year - 1) + _thisPeriod.substring(4, _thisPeriod.length()); //����ȼ�1,Ȼ�����ԭ���ģ�����
			} else {
				return _thisPeriod; //
			}
		} else if (_tbhb.endsWith("PeriodIncrease") || _tbhb.endsWith("PeriodSeries")) { //ͬ�Ⱥܼ�,������ݼ�һ
			return "" + (li_year - 1) + _thisPeriod.substring(4, _thisPeriod.length()); //����ȼ�1,Ȼ�����ԭ���ģ�����
		} else {
			return _thisPeriod; //
		}
	}

	//�Ծ��湫ʽ���м���!!
	private double getJepFormulaValue(String _formula) {
		if (_formula.indexOf("+") > 0 || _formula.indexOf("-") > 0 || _formula.indexOf("*") > 0 || _formula.indexOf("/") > 0 || _formula.indexOf("}") > 0) { //����мӼ��˳�,��{}������Ϊ�ǹ�ʽ,������ʽ����,����ֱ�ӵ������ִ���,����Ϊ���������!
			String str_newFormula = _formula; //
			if (_formula.indexOf("{��ƽ����}") >= 0) { //����������ַ���,������滻������
				str_newFormula = tbutil.replaceAll(_formula, "{��ƽ����}", "10"); //�������ַ����滻
			}

			Double jepValue = (Double) getJepParse().execFormula(str_newFormula); //
			//System.out.println("��ʽ[" + _formula + "]�ļ���ֵ��[" + jepValue + "]"); //
			return jepValue; //
		} else {
			return Double.parseDouble(_formula); //
		}
	}

	private JepFormulaParseAtUI getJepParse() {
		if (jepParse == null) {
			jepParse = new JepFormulaParseAtUI(true);
		}
		return jepParse; //
	}

	/**
	 * ��ƽ����,���ֵ,������ʲô����û�кϼ������!������Ҫ�ж��Ƿ���Ŀ��Ժϼ�??
	 * @param chooseComputeFunAndField2
	 * @return
	 */
	private boolean isCaneTotlal(String[][] _chooseComputeFunAndField) {
		boolean canTotal = false; //
		for (int i = 0; i < _chooseComputeFunAndField.length; i++) {
			canTotal = isCaneTotlal(_chooseComputeFunAndField[i][0]); //
			if (canTotal) { //ֻҪ��һ���ܺϼƾ����ǿ��Ժϼƣ���
				break;
			}
		}
		return canTotal;
	}

	/**
	 * �ж�ĳһ���Ƿ���Ժϼ�??
	 * @param _cmType
	 * @return
	 */
	private boolean isCaneTotlal(String _cmType) {
		if (_cmType.endsWith("Increase") || _cmType.endsWith("Series") || tbutil.isExistInArray(_cmType, str_notCanTotalTypes)) {
			return false; //����������ʻ�����ָ���ļ��㷽ʽSeries
		} else {
			return true; //
		}
	}

	/**
	 * �����ϱ�ͷ,���ͷ������ֵ����
	 * @return
	 */
	private String getTopLeftTitle() {
		StringBuilder sb_text = new StringBuilder(); //
		String str_row = ""; //
		if (chooseGroupRowFields.length > 0) {
			for (int i = 0; i < chooseGroupRowFields.length; i++) { // ���ǴӲ㿪ʼ��,��������
				str_row = str_row + chooseGroupRowFields[i];
				if (i != chooseGroupRowFields.length - 1) {
					str_row = str_row + "��"; //
				}
			}
			str_row = "��" + str_row + "��" + (isConvertXY ? "��" : "��") + "\r\n"; //
		}

		String str_col = ""; //
		if (chooseGroupColFields.length > 0) {
			for (int i = 0; i < chooseGroupColFields.length; i++) { // ���ǴӲ㿪ʼ��,��������
				str_col = str_col + chooseGroupColFields[i];
				if (i != chooseGroupColFields.length - 1) {
					str_col = str_col + "��"; //
				}
			}
			str_col = "��" + str_col + "��" + (isConvertXY ? "��" : "��") + "\r\n"; //
		}

		String str_cp = ""; //
		//������
		if (chooseComputeFunAndField.length == 1) { //���ֻ��һ��������
			str_cp = "��" + chooseComputeFunAndField[0][2] + "���K\r\n"; //
		}

		if (!isConvertXY) { //���û��ת��
			sb_text.append(str_row); //
			sb_text.append(str_cp); //
			sb_text.append(str_col); //
		} else {
			sb_text.append(str_col); //
			sb_text.append(str_cp); //
			sb_text.append(str_row); //
		}
		String str_rt = sb_text.toString(); //
		if (str_rt.endsWith("\r\n")) {
			str_rt = str_rt.substring(0, str_rt.length() - 2); //
		}
		return str_rt; //
	}

	//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
	private void appendMapData(HashMap _map, String _key, BigDecimal _newValue) {
		if (_map.containsKey(_key)) { //�����ֵ,���ۼӣ�
			BigDecimal ld_oldValue = (BigDecimal) _map.get(_key); //
			_newValue = ld_oldValue.add(_newValue);
			_map.put(_key, _newValue); //
		} else { //���ûֵ,��ֱ�Ӽ��룡
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData(HashMap _map, String _key, Double _newValue) {
		if (_map.containsKey(_key)) { //�����ֵ,���ۼӣ�
			double ld_oldValue = (Double) _map.get(_key); //
			_map.put(_key, ld_oldValue + _newValue); //
		} else { //���ûֵ,��ֱ�Ӽ��룡
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData(HashMap _map, String _key, String _newValue) { //Ԭ�������  ��ʾ��ʼֵ  20130312
		/*֮ǰ��д������Ҫ����ʼֵ���ַ���һ��ƴ������
		 * if (_map.containsKey(_key)) { //�����ֵ,���ۼӣ�
			String ld_oldValue = (String) _map.get(_key); //
			_map.put(_key, ld_oldValue + _newValue); //
		} else { //���ûֵ,��ֱ�Ӽ��룡
			_map.put(_key, _newValue); //
		}*/
		//����Ϊ��������������ģ������ȫΪ���֣������ֵĽ�����
		if (_map.containsKey(_key)) { //�����ֵ,���ۼӣ�
			String ld_oldValue = (String) _map.get(_key); //
			Pattern pattern = Pattern.compile("[0-9]");//�ж��Ƿ�Ϊ�����֡����ǣ�תΪdouble��������ӣ��ó�С��
			if (pattern.matcher(ld_oldValue).find() && pattern.matcher(_newValue).find()) {
				Double oldvalue = Double.parseDouble(ld_oldValue);
				Double newvalue = Double.parseDouble(_newValue);
				_map.put(_key, (oldvalue + newvalue) + ""); //
			} else {
				_map.put(_key, ld_oldValue + _newValue); //
			}

		} else { //���ûֵ,��ֱ�Ӽ��룡
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData2(HashMap _map, String _key, String _values) {
		if (_values == null || _values.equals("")) {
			return;
		}
		if (_map.containsKey(_key)) { //�����ֵ,���ۼӣ�
			String str_oldValue = (String) _map.get(_key); //
			if (str_oldValue.equals(";")) {
				_map.put(_key, str_oldValue + _values); //
			} else {
				_map.put(_key, str_oldValue + ";" + _values); //
			}
		} else { //���ûֵ,��ֱ�Ӽ��룡
			_map.put(_key, _values); //
		}
	}

	private JPanel getNorthBtnPanel() {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btnPanel.setOpaque(false); //͸��

		btn_xy = new WLTButton("X/Y��ת��", UIUtil.getImage("office_169.gif"));
		btn_exportHtml = new WLTButton("����Html", UIUtil.getImage("office_192.gif")); //
		btn_exportExcel = new WLTButton("����Excel", UIUtil.getImage("office_170.gif")); //
		btn_xy.addActionListener(this); //
		btn_exportHtml.addActionListener(this); //
		btn_exportExcel.addActionListener(this); //

		btnPanel.add(btn_xy); //
		btnPanel.add(btn_exportHtml); //
		btnPanel.add(btn_exportExcel); //
		return btnPanel; //
	}

	//�Ҽ�����!!
	protected void popShowRightMenu(JComponent _component, Point _point) {
		if (rightPopMenu == null) { //�����û����,���һ���ȴ���!
			rightPopMenu = new JPopupMenu(); //

			menuItem_drillNextGroup = new JMenuItem("��ȡ��ά��", UIUtil.getImage("office_059.gif")); //
			menuItem_drillDetail = new JMenuItem("��ȡ��ϸ", UIUtil.getImage("office_070.gif")); //
			menuItem_filter = new JMenuItem("��  ��", UIUtil.getImage("office_115.gif")); //
			menuItem_barChart = new JMenuItem("ת��������ͼ", UIUtil.getImage("office_153.gif")); //
			menuItem_lineChart = new JMenuItem("ת��������ͼ", UIUtil.getImage("office_152.gif")); //
			menuItem_pieChart = new JMenuItem("ת���ɱ�ͼ", UIUtil.getImage("office_005.gif")); //
			menuItem_warnValue = new JMenuItem("����ֵ����", UIUtil.getImage("office_053.gif")); //
			menuItem_viewInfo = new JMenuItem("������ϸ��Ϣ", UIUtil.getImage("savedata.gif")); //
			menuItem_lock = new JMenuItem("���ᴰ��", UIUtil.getImage("savedata.gif")); //
			menuItem_unlock = new JMenuItem("ȡ�����ᴰ��", UIUtil.getImage("savedata.gif")); //
			menuItem_drillDetail.setToolTipText("���ص�HashVO[]�б�����id�ֶ�,Ȼ���嵯����ϸ���б�ģ�����,����������!"); //

			menuItem_drillNextGroup.setPreferredSize(new Dimension(105, 20)); //
			menuItem_drillDetail.setPreferredSize(new Dimension(105, 20)); //
			menuItem_filter.setPreferredSize(new Dimension(105, 20)); //
			menuItem_barChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_lineChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_pieChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_warnValue.setPreferredSize(new Dimension(105, 20)); //
			menuItem_viewInfo.setPreferredSize(new Dimension(105, 20)); //
			menuItem_lock.setPreferredSize(new Dimension(105, 20)); //
			menuItem_unlock.setPreferredSize(new Dimension(105, 20)); //

			menuItem_drillNextGroup.addActionListener(this); //
			menuItem_drillDetail.addActionListener(this); //
			menuItem_filter.addActionListener(this); //
			menuItem_barChart.addActionListener(this); //
			menuItem_lineChart.addActionListener(this); //
			menuItem_pieChart.addActionListener(this); //
			menuItem_warnValue.addActionListener(this); //
			menuItem_viewInfo.addActionListener(this); //
			menuItem_lock.addActionListener(this); //
			menuItem_unlock.addActionListener(this); //

			rightPopMenu.add(menuItem_drillNextGroup); //
			rightPopMenu.add(menuItem_drillDetail); //
			rightPopMenu.add(menuItem_filter); //
			rightPopMenu.add(menuItem_barChart); //
			rightPopMenu.add(menuItem_lineChart); //
			rightPopMenu.add(menuItem_pieChart); //
			rightPopMenu.add(menuItem_warnValue); //
			if (ClientEnvironment.isAdmin()) {
				rightPopMenu.add(menuItem_viewInfo); //ֻ���ǹ���Ա��¼��ʽ���ܿ������!
			}
			rightPopMenu.add(menuItem_lock); //
			rightPopMenu.add(menuItem_unlock); //
		}
		if (cellPanel != null) {
			menuItem_lock.setVisible(!cellPanel.isLocked());
			menuItem_unlock.setVisible(cellPanel.isLocked());
		}
		menuItem_drillNextGroup.setVisible(false); //
		menuItem_drillDetail.setVisible(false); //
		menuItem_filter.setVisible(false); //
		menuItem_barChart.setVisible(false); //
		menuItem_lineChart.setVisible(false); //
		menuItem_pieChart.setVisible(false); //

		int li_selRow = cellPanel.getTable().getSelectedRow(); // ѡ�е���
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // ѡ�е���
		if (li_selRow < 0 || li_selCol < 0) {
			return; //
		}
		boolean isEnbleMenuItem = true; //
		boolean isShowMenu = true; //
		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		if (cellItemVO.getCellvalue() == null || cellItemVO.getCellvalue().trim().equals("")) {
			isEnbleMenuItem = false; //
		}
		menuItem_drillNextGroup.setEnabled(isEnbleMenuItem); //
		menuItem_drillDetail.setEnabled(isEnbleMenuItem); //
		menuItem_barChart.setEnabled(isEnbleMenuItem); //
		menuItem_lineChart.setEnabled(isEnbleMenuItem); //
		menuItem_pieChart.setEnabled(isEnbleMenuItem); //

		String str_cellType = (String) cellItemVO.getCustProperty("itemtype"); //
		if ("Level".equals(str_cellType)) { //ֻ������ͷ������ͷ���ϲ���ʾ�����ˡ��˵�
			menuItem_drillNextGroup.setVisible(true); //
			menuItem_filter.setVisible(true); //
		} else if ("ComputeCell".equals(str_cellType) || "ComputeTotal".equals(str_cellType)) { //ֻ���ڼ�����в���ʾ�Ĳ˵�,�����˲���ʾ��!
			menuItem_drillNextGroup.setVisible(true); //
			menuItem_drillDetail.setVisible(true); //
			menuItem_barChart.setVisible(true); //
			menuItem_lineChart.setVisible(true); //
			menuItem_pieChart.setVisible(true); //
		} else {
			isShowMenu = false;
		}

		if (isShowMenu || ClientEnvironment.isAdmin()) {
			rightPopMenu.show(_component, _point.x, _point.y); //�����˵�!!!
		}
	}

	//�����ӵ��!
	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent _event) {
		if (!_event.isCtrlDown() && !_event.isShiftDown() && !_event.isAltDown()) { //�����סCtrl��
			onDrillDetail(); //��ȡ��ϸ!
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_xy) { //X/Yת��
			onConvertXY(); //
		} else if (_event.getSource() == btn_exportHtml) { //����Html
			cellPanel.exportHtml((this.getClientProperty("templetname") == null || "".equals(this.getClientProperty("templetname").toString().trim())) ? "�������ݵ���" : (this.getClientProperty("templetname") + "")); //
		} else if (_event.getSource() == btn_exportExcel) { //����Excel
			cellPanel.exportExcel((this.getClientProperty("templetname") == null || "".equals(this.getClientProperty("templetname").toString().trim())) ? "�������ݵ���" : (this.getClientProperty("templetname") + ""), "BillReportPanel"); //20130911  Ԭ����������Ҫ���ڽ���񱨱�ĵ���excel�ֿ�
		} else if (_event.getSource() == menuItem_drillNextGroup) { //�������ȡ��һά��
			onDrillNextGroup(null); //
		} else if (_event.getSource() == menuItem_drillDetail) { //�������ȡ����
			onDrillDetail(); //
		} else if (_event.getSource() == menuItem_filter) { //����
			onFilterData(); //
		} else if (_event.getSource() == menuItem_barChart) { //ת��������ͼ
			onConvertBarChart(); //
		} else if (_event.getSource() == menuItem_lineChart) { //ת��������ͼ
			onConvertLineChart(); //
		} else if (_event.getSource() == menuItem_pieChart) { //��ͼ��ʾ!
			onConvertPieChart(); //
		} else if (_event.getSource() == menuItem_warnValue) { //��ʾ����ֵ
			onSetWarnValue(); //���þ���ֵ!
		} else if (_event.getSource() == menuItem_viewInfo) { //��ʾ����ֵ
			onViewInfo(); //
		} else if (_event.getSource() == menuItem_lock) { //��ʾ����ֵ
			onLock(); //
		} else if (_event.getSource() == menuItem_unlock) { //��ʾ����ֵ
			onUnLock(); //
		}
	}

	/**
	 * X/Yת��!!
	 */
	private void onConvertXY() {
		isConvertXY = !isConvertXY; //����һ��!
		cellPanel.convertxy(false); //һת���������¹�����,������Ҫ���¼����¼�
		BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(0, 0);
		itemVO.setCellvalue(getTopLeftTitle()); //
		itemVO.setRowheight("40"); //
		cellPanel.getTable().addMouseListener(new MouseAdapter() { //�����Լ����Ҽ�����!
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON3) {
							popShowRightMenu((JComponent) e.getSource(), e.getPoint()); //�Ҽ�������ʾ�˵�!
						}
					}
				}); //
		cellPanel.getTable().setToolTipText("��ʾ:�Ҽ��С���ȡ�������ˡ��ȸ��๦��"); //
		cellPanel.setEditable(false);
		cellPanel.setCrystal(true); //͸��!!!
	}

	//��ȡ��ϸ
	public void onDrillDetail() {
		try {
			int li_selRow = cellPanel.getTable().getSelectedRow(); // ѡ�е���
			int li_selCol = cellPanel.getTable().getSelectedColumn(); // ѡ�е���
			if (li_selRow < 0 || li_selCol < 0) {
				MessageBox.show(this, "��ѡ�����ݲ��ܽ�����ȡ����!"); //
				return; //
			}
			BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
			if (cellItemVO.getCellvalue() == null || cellItemVO.getCellvalue().trim().equals("")) { //
				return;
			}
			String str_cellType = (String) cellItemVO.getCustProperty("itemtype"); //
			if ("ComputeCell".equals(str_cellType)) { //����Ǽ����,��ֱ�ӽ�����ȡ!
				String str_ids = (String) cellItemVO.getCustProperty("#value"); // �õ�ѡ��Ԫ����ʵID����
				new ReportUIUtil().onDrillDetail(this.cellPanel, str_ids, cellItemVO, this.str_builderClassName, this.queryConditionMap, this.allCanGroupFields, this.str_drillActionClass, this.str_drillTempletCode); //
			} else if ("Level".equals(str_cellType)) { //�����ͷ�黹����ͷ��,�������һά����ȡ!!
				if (this.drillgroupbindMap == null) {
					onDrillNextGroup(null); //
				} else {
					String str_grouptype = (String) cellItemVO.getCustProperty("levelname"); //
					if (drillgroupbindMap.containsKey(str_grouptype)) { //������ά��Ԥ�Ȱ�����һ��ά��,���ڵ��ʱֱ����ȡ����һά��
						onDrillNextGroup((String) drillgroupbindMap.get(str_grouptype)); //
					} else {
						onDrillNextGroup(null); //
					}
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//ȡ����ȡ����
	private void onDrillNextGroup(String _nextGroupName) {
		DrillDefineVO drillVO = getDrillDefine(_nextGroupName); //������ȡ��һά�ȵĴ���,ѡ���������ȡ��ά��!!
		if (drillVO == null) {
			return;
		}

		String[] str_newRowGroupFields = chooseGroupRowFields; //��Ϊ�п�����ȡʱ�ڡ����ϡ������ˡ��µ���ȡ��,��ʱ����ά�ȡ��ǲ����,�����ȸ�ֵΪ��ǰ����ά�ȣ���
		if (drillVO.getDrillRowGroupName() != null && drillVO.getDrillRowGroupName().length > 0) { //����������µ���ά���ϵ���ȡ!!
			str_newRowGroupFields = new String[chooseGroupRowFields.length + drillVO.getDrillRowGroupName().length]; //
			System.arraycopy(chooseGroupRowFields, 0, str_newRowGroupFields, 0, chooseGroupRowFields.length); //����ԭ���Ŀ�������
			System.arraycopy(drillVO.getDrillRowGroupName(), 0, str_newRowGroupFields, chooseGroupRowFields.length, drillVO.getDrillRowGroupName().length); //����ԭ���Ŀ�������
		}

		String[] str_newColGroupFields = chooseGroupColFields; //��Ϊ�п�����ȡʱ�ڡ����ϡ������ˡ��µ���ȡ��,��ʱ����ά�ȡ��ǲ����,�����ȸ�ֵΪ��ǰ����ά�ȣ���
		if (drillVO.getDrillColGroupName() != null && drillVO.getDrillColGroupName().length > 0) { //����������µ���ά���ϵ���ȡ!!
			str_newColGroupFields = new String[chooseGroupColFields.length + drillVO.getDrillColGroupName().length]; //
			System.arraycopy(chooseGroupColFields, 0, str_newColGroupFields, 0, chooseGroupColFields.length); //
			System.arraycopy(drillVO.getDrillColGroupName(), 0, str_newColGroupFields, chooseGroupColFields.length, drillVO.getDrillColGroupName().length); //����ԭ���Ŀ�������
		}

		ReportCellPanel temp_ReportCellPanel = null;
		if (drillVO.getDrillType() == DrillDefineVO.ROWGROUP || drillVO.getDrillType() == DrillDefineVO.COLGROUP) { //���������ͷ�Ϸ���
			String[][] str_newComputeFunandFields = getChooseComputeFunAndField();
			temp_ReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanComputeFields, str_newRowGroupFields, str_newColGroupFields, str_newComputeFunandFields, null, null, null, isTotal, false, isRowGroupSubTotal, false, isColGroupSubTotal, isSortByCpValue, getQueryConditionMap(), drillVO.getDrillConditionMap(), groupfieldorder, zeroReportConfMap,
					dateGroupDefineMap, this.isCtrlDown); //
		} else if (drillVO.getDrillType() == DrillDefineVO.CELL) {
			String[][] str_newComputeFunandFields = drillVO.getComputeFunAndFields();
			temp_ReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanComputeFields, str_newRowGroupFields, str_newColGroupFields, str_newComputeFunandFields, null, null, null, isTotal, false, isRowGroupSubTotal, false, isColGroupSubTotal, isSortByCpValue, getQueryConditionMap(), drillVO.getDrillConditionMap(), groupfieldorder, zeroReportConfMap,
					dateGroupDefineMap, this.isCtrlDown); //
		}
		temp_ReportCellPanel.setStr_drillActionClass(this.str_drillActionClass); //
		temp_ReportCellPanel.setStr_drillTempletCode(this.str_drillTempletCode); //
		temp_ReportCellPanel.setDrillgroupbindMap(this.drillgroupbindMap); //������ȡ�󶨵�ά��

		Window frame = BillDialog.getWindowForComponent(this); //
		BillDialog dialog = new BillDialog(this, "��ȡ��ά��", 650, 550, (int) frame.getLocation().getX() + 150, (int) frame.getLocation().getY() + 120); //�������λ!!

		JPanel conttentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		conttentPanel.add(temp_ReportCellPanel); //
		dialog.getContentPane().add(conttentPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	/**
	 * ȡ����һ����Ҫ������ȡ��ά��!!!
	 * ѡ�м����Ҳ������ȡ,ѡ����ͷ/��ͷҲ�ǿ�����ȡ�ģ���
	 * ѡ����ͷ����ͷ�ȼ���ѡ�ж�Ӧ���������ϵ����и��ӵ�ֵ����
	 */
	public DrillDefineVO getDrillDefine(String _groupName) {
		if (cellPanel == null) {
			MessageBox.show(this, "û������,���ܽ��д˲���!"); //
			return null;
		}

		int li_selRow = cellPanel.getTable().getSelectedRow(); // ѡ�е���
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // ѡ�е���
		if (li_selRow < 0 || li_selCol < 0) {
			MessageBox.show(this, "��ѡ�����ݲ��ܽ�����ȡ����!"); //
			return null; //
		}

		int[] li_selrows = cellPanel.getTable().getSelectedRows(); //
		int[] li_selcols = cellPanel.getTable().getSelectedColumns(); //

		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		String str_cellrealtype = (String) cellItemVO.getCustProperty("itemtype"); // �ж����л�����
		String str_rowColType = (String) cellItemVO.getCustProperty("rowcoltype"); //�������ͣ�
		if (!(str_cellrealtype != null && (str_cellrealtype.equals("Level") || str_cellrealtype.equals("ComputeCell")))) {
			MessageBox.show(this, "ѡ���е��������Ͳ���,ֻ��ѡ��[�ϱ�ͷ],[���ͷ],[�����]�������Ͳ��ܽ�����ȡ����!"); //
			return null; //
		}

		if (str_cellrealtype.equals("ComputeCell")) { // ���ѡ�е��Ǽ����
			ArrayList al_drillNextItemVOs = new ArrayList(); //
			String str_firstcellLevel = null; //
			for (int i = 0; i < li_selrows.length; i++) { // ������
				for (int j = 0; j < li_selcols.length; j++) { // ������
					BillCellItemVO cellItemVO_item = cellPanel.getBillCellItemVOAt(li_selrows[i], li_selcols[j]); //
					String str_cellrealtype_item = (String) cellItemVO_item.getCustProperty("itemtype"); // �ж����л�����
					if ("ComputeCell".equals(str_cellrealtype_item)) {
						if (str_firstcellLevel == null) {
							str_firstcellLevel = getCellLevelName(cellItemVO_item); //
							al_drillNextItemVOs.add(cellItemVO_item); //
						} else { //
							String str_thiscellLevel = getCellLevelName(cellItemVO_item); //
							if (str_thiscellLevel.equals(str_firstcellLevel)) { //��Ϊ��������ƽ�̵����,�������ѡ����ƽ�̷�Χ�е�����ʱ�����޷���ȡ�ģ����Ա�����ͬһ��ά�����͵ĲŴ���
								al_drillNextItemVOs.add(cellItemVO_item); //
							}
						}
					}
				}
			}

			Object[] drillConsObjs = getDrillNextGroupCons((BillCellItemVO[]) al_drillNextItemVOs.toArray(new BillCellItemVO[0])); //
			HashMap[] prefixGroupTypeValues = (HashMap[]) drillConsObjs[0]; //
			String[][] str_newComputeFunandFields = (String[][]) drillConsObjs[1]; //

			//�����Ի���,ѡ��һ��ά��!!
			if (drillNextGroupDialog == null) {
				drillNextGroupDialog = new MultiLevelChooseDrillDialog(this, getRemainGroupFields()); //������ȡ�Ի���!!
			}
			drillNextGroupDialog.setVisible(true); //
			if (drillNextGroupDialog.getCloseType() == 1) {
				DrillDefineVO drillVO = new DrillDefineVO();
				drillVO.setDrillType(DrillDefineVO.CELL); //
				drillVO.setDrillRowGroupName(drillNextGroupDialog.getRowGroupnames()); // �µ�Ҫ��ȡ����
				drillVO.setDrillColGroupName(drillNextGroupDialog.getColGroupnames()); // �µ�Ҫ��ȡ����
				drillVO.setDrillConditionMap(prefixGroupTypeValues); //
				drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
				return drillVO;
			} else {
				return null;
			}
		} else { //���ѡ�е�����ͷ����ͷ��!!!
			if (li_selrows.length > 1 && li_selcols.length > 1) {
				MessageBox.show(this, "ֻ�ܶ�ͬһ�л�ͬһ�е����ݽ���ѡ����ȡ!!"); //
				return null; //
			}

			ArrayList al_drillNextItemVOs = new ArrayList(); //
			if (str_rowColType.equals("Row")) { //���ѡ�е�����ͷ��
				for (int i = 0; i < li_selcols.length; i++) { //������
					for (int j = li_selRow + 1; j < cellPanel.getRowCount(); j++) { //����������,�൱�ڰ�ѡ�����ϵ������еġ�����񡱶�����������!
						BillCellItemVO preFixCellItemVO = cellPanel.getBillCellItemVOAt(j, li_selcols[i]); // �ҵ�ǰ���
						if ("ComputeCell".equals(preFixCellItemVO.getCustProperty("itemtype"))) { //�����Ǽ����Ų������!!
							al_drillNextItemVOs.add(preFixCellItemVO); //
						}
					}
				}
			} else if (str_rowColType.equals("Col")) { // ���ѡ�е�����ͷ��
				for (int i = 0; i < li_selrows.length; i++) { // ��������
					for (int j = li_selCol + 1; j < cellPanel.getColumnCount(); j++) { //����������,�൱�ڰ�ѡ�����ϵ������еġ�����񡱶�����������!
						BillCellItemVO preFixCellItemVO = cellPanel.getBillCellItemVOAt(li_selrows[i], j); //�ҵ�ǰ��ĸ�ά��
						if ("ComputeCell".equals(preFixCellItemVO.getCustProperty("itemtype"))) { //�����Ǽ����Ų������!!
							al_drillNextItemVOs.add(preFixCellItemVO); //
						}
					}
				}
			}

			Object[] drillConsObjs = getDrillNextGroupCons((BillCellItemVO[]) al_drillNextItemVOs.toArray(new BillCellItemVO[0])); //
			HashMap[] prefixGroupTypeValues = (HashMap[]) drillConsObjs[0]; //
			String[][] str_newComputeFunandFields = (String[][]) drillConsObjs[1]; //

			//�����Ի���,ѡ��һ��ά��!!
			if (_groupName == null) { //
				String[] str_remainGroups = getRemainGroupFields(); //
				if (null == str_remainGroups) { //Ԭ����20121109��ӣ���Ҫ����ϵı�����ֵ�����
					return null;
				}
				MultiLevelChooseDrillDialog dialog = new MultiLevelChooseDrillDialog(this, str_remainGroups);
				dialog.setVisible(true); //
				if (dialog.getCloseType() == 1) {
					DrillDefineVO drillVO = new DrillDefineVO();
					drillVO.setDrillRowGroupName(dialog.getRowGroupnames()); //�µ�Ҫ��ȡ����
					drillVO.setDrillColGroupName(dialog.getColGroupnames()); //�µ�Ҫ��ȡ����
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					if ("Row".equals(str_rowColType)) {
						drillVO.setDrillType(DrillDefineVO.ROWGROUP); //
					} else if ("Col".equals(str_rowColType)) {
						drillVO.setDrillType(DrillDefineVO.COLGROUP); //
					}
					return drillVO;
				} else {
					return null;
				}
			} else { //���ֱ�Ӷ���������ȡ������!
				DrillDefineVO drillVO = new DrillDefineVO();
				if ("Row".equals(str_rowColType)) {
					drillVO.setDrillRowGroupName(new String[] { _groupName }); //�µ�Ҫ��ȡ����
					drillVO.setDrillColGroupName(null); //�µ�Ҫ��ȡ����
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					drillVO.setDrillType(DrillDefineVO.ROWGROUP); //
				} else if ("Col".equals(str_rowColType)) {
					drillVO.setDrillRowGroupName(null); //�µ�Ҫ��ȡ����
					drillVO.setDrillColGroupName(new String[] { _groupName }); //�µ�Ҫ��ȡ����
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					drillVO.setDrillType(DrillDefineVO.COLGROUP); //
				}
				return drillVO;
			}
		}
	}

	private String getCellLevelName(BillCellItemVO cellItemVO_item) {
		StringBuilder sb_text = new StringBuilder(); //
		String str_rowLevel = (String) cellItemVO_item.getCustProperty("RowHeaderLevelNameValue"); //
		if (!str_rowLevel.equals("")) {
			String[] str_keys = (String[]) TBUtil.getTBUtil().convertStrToMapByExpress(str_rowLevel, "��", "=").keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append(str_keys[i] + "��"); //
			}
		}

		String str_colLevel = (String) cellItemVO_item.getCustProperty("ColHeaderLevelNameValue"); //
		if (!str_colLevel.equals("")) {
			String[] str_keys = (String[]) TBUtil.getTBUtil().convertStrToMapByExpress(str_colLevel, "��", "=").keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append(str_keys[i] + "��"); //
			}
		}
		return sb_text.toString();
	}

	//��һ������е����ݵĸ�ά�ȵ�ֵ���м���!!������ȡ��!
	private Object[] getDrillNextGroupCons(BillCellItemVO[] _itemVOs) {
		ArrayList al_prefixGroupTypeValues = new ArrayList(); //
		ArrayList al_computeFunAndFields = new ArrayList(); //
		for (int i = 0; i < _itemVOs.length; i++) {
			String str_cpField = (String) _itemVOs[i].getCustProperty("RealCPField"); //
			String str_cpType = (String) _itemVOs[i].getCustProperty("RealCPType"); //
			String[] str_computeFunAndField = new String[] { str_cpType, "", str_cpField }; //
			if (!containComputeFunandField(al_computeFunAndFields, str_computeFunAndField)) { // ���������,�����
				al_computeFunAndFields.add(str_computeFunAndField); //
			}

			HashMap map_prefixGroupTypeValues = new HashMap(); //
			String str_RowHeaderLevelNameValue = (String) _itemVOs[i].getCustProperty("RowHeaderLevelNameValue"); //��ͷ��
			if (!str_RowHeaderLevelNameValue.equals("")) {
				HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_RowHeaderLevelNameValue, "��", "="); //
				String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
				for (int k = 0; k < str_keys.length; k++) {
					map_prefixGroupTypeValues.put(str_keys[k], map.get(str_keys[k])); //��¼����������е�ֵ�ڸ���ά���ϵ�ʵ��ά��ֵ!!
				}
			}

			String str_ColHeaderLevelNameValue = (String) _itemVOs[i].getCustProperty("ColHeaderLevelNameValue"); //��ͷ��
			if (!str_ColHeaderLevelNameValue.equals("")) {
				HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_ColHeaderLevelNameValue, "��", "="); //
				String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
				for (int k = 0; k < str_keys.length; k++) {
					map_prefixGroupTypeValues.put(str_keys[k], map.get(str_keys[k])); //��¼����������е�ֵ�ڸ���ά���ϵ�ʵ��ά��ֵ!!
				}
			}

			al_prefixGroupTypeValues.add(map_prefixGroupTypeValues); //
		}

		HashMap[] prefixGroupTypeValues = (HashMap[]) al_prefixGroupTypeValues.toArray(new HashMap[0]); //
		String[][] str_newComputeFunandFields = getComputeFunAndFields(al_computeFunAndFields); //

		return new Object[] { prefixGroupTypeValues, str_newComputeFunandFields }; //
	}

	/**
	 * ���˵�ǰ������!!! ����ʱ����̫��,ֻ�뿴����һ����!!! ����ѡ������һ����,���й���,ֻ��ͬʱѡ��һ����
	 * ����ʱ,�ǲ����к�̨ȡ����!ֱ����ǰ̨����!!!
	 */
	public void onFilterData() {
		if (cellPanel == null) {
			MessageBox.show(this, "û������,���ܽ��д˲���!"); //
			return;
		}

		int li_selRow = cellPanel.getTable().getSelectedRow(); // ѡ�е���
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // ѡ�е���
		if (li_selRow < 0 || li_selCol < 0) {
			MessageBox.show(this, "��ѡ�����ݲ��ܽ��й��˲���!"); // ��ʾ�������/2012-03-15��
			return; //
		}

		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		String str_celltype = (String) cellItemVO.getCustProperty("itemtype"); //
		if (!"Level".equals(str_celltype)) { //���������ʽ!
			MessageBox.show(this, "ѡ�е����ݸ�ʽ����,��ѡ���з�����з���!"); //
			return; //
		}

		String str_grouptype = (String) cellItemVO.getCustProperty("levelname"); // ʲô��
		if (hvs_data_beforefilter == null) {
			hvs_data_beforefilter = new HashVO[hvs_data.length]; //
			for (int i = 0; i < hvs_data.length; i++) {
				hvs_data_beforefilter[i] = hvs_data[i].deepClone(); // �ȿ�¡һ��
			}
		}

		if (filterConditionMap == null) {
			filterConditionMap = new HashMap(); //
		}

		String[][] str_filterData = null; //���Ǽ����ĳһ��ά�ȵ�ʵ�ʵ�Ψһֵ����
		if (filterConditionMap.containsKey(str_grouptype)) {
			str_filterData = (String[][]) filterConditionMap.get(str_grouptype); //
		} else {
			LinkedHashSet hst_distinct = new LinkedHashSet(); //
			for (int i = 0; i < hvs_data_beforefilter.length; i++) { //���������Ǵ�ʵ�������й��˵�!
				hst_distinct.add(hvs_data_beforefilter[i].getStringValue(str_grouptype, "����ֵ��", true)); //
			}
			String[] str_filterValues = (String[]) hst_distinct.toArray(new String[0]); //
			TBUtil.getTBUtil().sortStrs(str_filterValues); //
			str_filterData = new String[str_filterValues.length][2]; //
			for (int i = 0; i < str_filterValues.length; i++) { //
				str_filterData[i][0] = str_filterValues[i];
				str_filterData[i][1] = "0"; //
			}
		}

		MultiLevelChooseFilterDialog dialog = new MultiLevelChooseFilterDialog(this, "ѡ��һ������ [" + str_grouptype + "] ���й���", str_filterData); //
		dialog.setVisible(true); // ��ʾ
		if (dialog.getCloseType() != 1) { //�������ȷ�����ص�,��ֱ���˳�����
			return;
		}

		filterConditionMap.put(str_grouptype, str_filterData); //�ȼ��룡����
		String[][] str_choosefilter = dialog.getReturnFilterField(); // ����
		filterConditionMap.put(str_grouptype, str_choosefilter); // ��������!!!

		//������������,������������Ĺ���ҲҪ����!!!
		String[] str_allFilterGroupTypes = (String[]) filterConditionMap.keySet().toArray(new String[0]); //
		HashMap[] allFilterGroupMap = new HashMap[str_allFilterGroupTypes.length];
		for (int i = 0; i < str_allFilterGroupTypes.length; i++) {
			allFilterGroupMap[i] = new HashMap(); //
			String[][] str_chooseFilterDetail = (String[][]) filterConditionMap.get(str_allFilterGroupTypes[i]); //
			for (int j = 0; j < str_chooseFilterDetail.length; j++) {
				if (str_chooseFilterDetail[j][1].equals("1")) {
					allFilterGroupMap[i].put(str_chooseFilterDetail[j][0], null); //
				}
			}
		}

		ArrayList al_filtered = new ArrayList(); //
		String str_a1; //
		for (int i = 0; i < hvs_data_beforefilter.length; i++) {
			boolean ismatch = true;
			for (int j = 0; j < allFilterGroupMap.length; j++) { //
				str_a1 = hvs_data_beforefilter[i].getStringValue(str_allFilterGroupTypes[j], "����ֵ��", true); //����Ҫ�����ֵ������
				if (!allFilterGroupMap[j].containsKey(str_a1)) { // �������һ�ؾ͹�����,�������˳�
					ismatch = false;
					break;
				}
			}

			if (ismatch) {
				al_filtered.add(hvs_data_beforefilter[i]); //
			}
		}

		this.hvs_data = (HashVO[]) al_filtered.toArray(new HashVO[0]); //
		buildUI2(); // ��������ҳ��
	}

	//��ѡ�еĸ���ת��������ͼ��ʾ!
	private void onConvertBarChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}

		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createBarChart("����ͼ", chartVO.getXHeadName(), chartVO.getYHeadName(), chartVO.getXSerial(), chartVO.getYSerial(), getChartData(chartVO.getDataVO()), true, null); //
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "��������ͼ", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	//��ѡ�еĸ���ת��������ͼ��ʾ!
	private void onConvertLineChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}
		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createLineChart("����ͼ", chartVO.getXHeadName(), chartVO.getYHeadName(), chartVO.getXSerial(), chartVO.getYSerial(), getChartData(chartVO.getDataVO()), false, null); //jfreechart-1.0.14����׷������ͼ3άЧ�� �����/2013-06-13��
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "��������ͼ", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	//��ѡ�еĸ���ת���ɱ�ͼ��ʾ!
	private void onConvertPieChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}
		BillChartItemVO[][] chartItemVOs = chartVO.getDataVO(); //
		String[] str_serial = new String[chartVO.getXSerial().length * chartVO.getYSerial().length]; //
		double[] ld_values = new double[chartVO.getXSerial().length * chartVO.getYSerial().length]; //
		for (int i = 0; i < chartVO.getXSerial().length; i++) {
			for (int j = 0; j < chartVO.getYSerial().length; j++) {
				int li_pos = i * chartVO.getYSerial().length + j; //
				String str_groupName = chartVO.getXSerial()[i] + "-" + chartVO.getYSerial()[j]; //
				str_serial[li_pos] = str_groupName; //
				ld_values[li_pos] = (chartItemVOs[i][j] == null ? 0 : chartItemVOs[i][j].getValue()); //
			}
		}
		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createPieChart("����ͼ", str_serial, ld_values, 2, null); //
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "��������ͼ", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	private double[][] getChartData(BillChartItemVO[][] _vos) {
		double[][] ld_value = new double[_vos.length][_vos[0].length]; //
		for (int i = 0; i < ld_value.length; i++) {
			for (int j = 0; j < ld_value[0].length; j++) {
				Double ld_itemvalue = (_vos[i][j] == null ? 0 : _vos[i][j].getValue()); //
				if (ld_itemvalue == null) {
					ld_itemvalue = 0d; //
				}
				ld_value[i][j] = ld_itemvalue; //
			}
		}
		return ld_value; //
	}

	//�����ѡ�и��ӵ�����!
	private BillChartVO getChartVO() {
		int[] li_rows = cellPanel.getTable().getSelectedRows(); //ѡ�е�������?
		int[] li_cols = cellPanel.getTable().getSelectedColumns(); //ѡ�е�������?

		String[] str_xSerial = new String[li_rows.length]; //
		String[] str_ySerial = new String[li_cols.length]; //
		BillChartItemVO[][] chartItemVOs = new BillChartItemVO[li_rows.length][li_cols.length]; //

		String str_cellValue = null; //
		for (int i = 0; i < li_rows.length; i++) { //��������ѡ�е���!
			for (int j = 0; j < li_cols.length; j++) { //��������ѡ�е���!
				BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_rows[i], li_cols[j]); //
				String str_realType = (String) cellItemVO.getCustProperty("itemtype"); //
				if (!("ComputeCell".equals(str_realType) || "ComputeTotal".equals(str_realType))) { //
					MessageBox.show(this, "ֻ��ѡ�м�������ת��!\r\n��ע�����Ƿ����ѡ���˺ϼ���?"); //
					return null; //
				}

				if (i == 0) {
					String str_rowLevel = (String) cellItemVO.getCustProperty(isConvertXY ? "ColHeaderLevelNameValue" : "RowHeaderLevelNameValue"); //
					str_ySerial[j] = getSplitValue(str_rowLevel, isConvertXY ? "��" : "��"); //
					if (chooseGroupRowFields.length == 0 || (chooseComputeFunAndField.length > 1 && chooseGroupColFields.length > 0)) { //�����ά��Ϊ��,�����ж��������ʱ,����
						str_ySerial[j] = str_ySerial[j] + "<" + (String) cellItemVO.getCustProperty("RealCPField") + ">"; //
					}
				}

				if (j == 0) { //����ǵ�һ��!
					String str_colLevel = (String) cellItemVO.getCustProperty(isConvertXY ? "RowHeaderLevelNameValue" : "ColHeaderLevelNameValue"); //
					str_xSerial[i] = getSplitValue(str_colLevel, isConvertXY ? "��" : "��"); //
					if (chooseGroupColFields.length == 0) {
						str_xSerial[i] = str_xSerial[i] + "<" + (String) cellItemVO.getCustProperty("RealCPField") + ">"; //
					}
				}
				str_cellValue = cellItemVO.getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					if (str_cellValue.endsWith("%")) {
						str_cellValue = str_cellValue.substring(0, str_cellValue.length() - 1); //
					}
					chartItemVOs[i][j] = new BillChartItemVO(Double.parseDouble(str_cellValue)); //�������VO
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setTitle("ͼ��"); //
		chartVO.setXHeadName(chooseGroupRowFields.length > 0 ? chooseGroupRowFields[0] : chooseGroupColFields[0]); //this.chooseGroupRowFields[0]
		chartVO.setYHeadName(this.chooseComputeFunAndField[0][2]); //
		chartVO.setXSerial(str_xSerial); //
		chartVO.setYSerial(str_ySerial); //
		chartVO.setDataVO(chartItemVOs); //��������!!
		return chartVO; //
	}

	private String getSplitValue(String _text, String _split) {
		if (_text.equals("")) {
			return "";
		}
		HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(_text, _split, "="); //
		Iterator its = map.values().iterator(); //
		StringBuilder sb_text = new StringBuilder(); //
		while (its.hasNext()) {
			sb_text.append("" + its.next() + "_"); //
		}
		String str_return = sb_text.toString(); //
		str_return = str_return.substring(0, str_return.length() - 1); //
		return str_return; //
	}

	//���þ���ֵ!!!
	private void onSetWarnValue() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //
		JLabel label = new JLabel("����һ��������,���غ�ͻὫ�����������ֵ�ĸ��Ӻ�ɫ��ʾ!");
		JComboBox comBox = new JComboBox(); //
		comBox.addItem("����"); //
		comBox.addItem("С��"); //
		comBox.setFocusable(false); //

		JFormattedTextField textField = new JFormattedTextField("100"); //��ʽ�������ֿ�
		textField.setHorizontalAlignment(SwingConstants.RIGHT); //
		textField.setDocument(new NumberFormatdocument()); //�������ֿ�ֻ����������,������ĸ���ü���!!!!

		label.setBounds(25, 15, 350, 20); //
		comBox.setBounds(100, 40, 100, 20); //
		textField.setBounds(205, 40, 100, 20); //

		panel.add(label); //
		panel.add(comBox); //
		panel.add(textField); //

		if (!MessageBox.confirm(this, panel, 400, 150)) { //
			return;
		}

		String str_text = textField.getText(); //
		if (str_text.equals("")) {
			MessageBox.show(this, "������һ����ֵ!"); //
			return; //
		}
		double ld_warnValue = Double.parseDouble(str_text); ////
		int li_rows = cellPanel.getTable().getRowCount(); ////
		int li_cols = cellPanel.getTable().getColumnCount(); ////
		for (int i = 0; i < li_rows; i++) {
			for (int j = 0; j < li_cols; j++) {
				BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j); //
				if (itemVO != null && itemVO.getCellvalue() != null) {
					String str_realType = (String) itemVO.getCustProperty("itemtype"); //
					if ("ComputeCell".equals(str_realType)) { //
						double ld_value = Double.parseDouble(itemVO.getCellvalue()); //
						boolean isWarn = false; //
						if (comBox.getSelectedIndex() == 0) {
							isWarn = (ld_value > ld_warnValue); //
						} else {
							isWarn = (ld_value < ld_warnValue); //
						}
						if (isWarn) {
							itemVO.setBackground("255,0,0"); //
							itemVO.setForeground("255,255,255"); //
						} else {
							itemVO.setBackground("255,255,255"); //
							itemVO.setForeground("0,0,0"); //
						}
					}
				}
			}
		}
		cellPanel.getTable().clearSelection(); //
	}

	/**
	 * ��ʾ��������!!
	 */
	private void onViewInfo() {
		int li_result = MessageBox.showOptionDialog(this, "��ѡ����Ҫ�鿴����Ϣ���ͣ���", "��ʾ", new String[] { "��ʾ������Ϣ", "��ʾ����ά������Ϣ", "ȡ��" }, 450, 150); //
		if (li_result == 0) {
			cellPanel.showCellAllProp(); //ֱ����ʾ����ֵ
		} else if (li_result == 1) { //��ʾά��������!!
			onViewInfo2(); //
		}
	}

	private void onLock() {
		cellPanel.onLock();
	}

	private void onUnLock() {
		cellPanel.onUnLock();
	}

	private void onViewInfo2() {
		JTree tree_rowHeader = new JTree(rowHeaderRootNode); //
		JTree tree_colHeader = new JTree(colHeaderRootNode); //

		JTabbedPane tabbed = new JTabbedPane(); //
		tabbed.addTab("��ͷά��", new JScrollPane(tree_rowHeader)); //
		tabbed.addTab("��ͷά��", new JScrollPane(tree_colHeader)); //

		//���ݣ�����
		HashVOStruct hvst = new HashVOStruct(); //
		hvst.setHeaderName(this.hvs_data[0].getKeys()); //
		hvst.setHashVOs(this.hvs_data); //
		BillListPanel billList = new BillListPanel(hvst); //
		tabbed.addTab("����ǰ̨�Ļ�������", billList); //

		//��ϣ������
		String[] str_keys = (String[]) beforeBuilderDataMap.keySet().toArray(new String[0]); //
		HashVO[] hvs_mapData = new HashVO[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) { //
			hvs_mapData[i] = new HashVO(); //
			hvs_mapData[i].setAttributeValue("Key", str_keys[i]); //
			hvs_mapData[i].setAttributeValue("Value", beforeBuilderDataMap.get(str_keys[i])); //
		}
		HashVOStruct hvst_map = new HashVOStruct(); //
		hvst_map.setHeaderName(new String[] { "Key", "Value" }); //
		hvst_map.setHashVOs(hvs_mapData); //
		BillListPanel billList_map = new BillListPanel(hvst_map); //
		tabbed.addTab("��������ת����Map", billList_map); //

		BillDialog dialog = new BillDialog(this, "��ʾ��������", 500, 500);
		dialog.getContentPane().add(tabbed); //
		dialog.addConfirmButtonPanel(); //
		dialog.setVisible(true);
	}

	/**
	 * ָ��ĳ���������,���º�ƴ���м���! �����ڿ�ʼ�ϲ�����������,����������,���ҵĺϲ��������ø���������!
	 * 
	 * @param itemVOS
	 * @param _beginX
	 * @param _beginY
	 * @param _spanDown
	 *            ���²�����
	 * @param _spanRight
	 *            ���Ҳ�����
	 */
	private void span(BillCellItemVO[][] itemVOS, int _beginX, int _beginY, int _spanDown, int _spanRight, String[][] _custsype) {
		itemVOS[_beginX][_beginY].setSpan("" + _spanDown + "," + _spanRight + ""); // cellrealtype
		for (int k = 0; k < _custsype.length; k++) {
			itemVOS[_beginX][_beginY].setCustProperty(_custsype[k][0], _custsype[k][1]);
		}

		for (int i = 0; i < _spanDown; i++) { // ���¼���,���������1,��ֻ��һ��ѭ��
			for (int j = 0; j < _spanRight; j++) { // ���Ҽ���
				if (i == 0 && j == 0) {
				} else {
					itemVOS[_beginX + i][_beginY + j].setSpan("" + (0 - j) + "," + (0 - i)); // ָ�����и��бȿ�ʼ�ϲ����������ټ��м���!!!
					for (int k = 0; k < _custsype.length; k++) {
						itemVOS[_beginX + i][_beginY + j].setCustProperty(_custsype[k][0], _custsype[k][1]);
					}
				}
			}
		}
	}

	private String getRowHeaderColor(int _row) {
		if (_row >= headBgColors_row.length) {
			return "231,231,232";
		} else {
			return headBgColors_row[_row]; //
		}

	}

	private String getColHeaderColor(int _col) {
		if (_col >= headBgColors_col.length) {
			return "231,231,232";
		} else {
			return headBgColors_col[_col]; //
		}
	}

	//�ϼ��е���ɫ������
	private String getTotalColor(int _depth) {
		if (_depth >= totalBgColors.length) {
			return "255,255,142";
		} else {
			return totalBgColors[_depth]; //
		}
	}

	//��þ���ֵ�ı�����ɫ����
	private String getWarnColor(int _len) {
		if (_len >= warnBgColors.length) {
			return warnBgColors[warnBgColors.length - 1];
		} else {
			return warnBgColors[_len]; //
		}
	}

	private String getComputeHeaderColor(int _index) {
		int index = 0; //
		if (_index < computeBgColors.length) {
			index = _index; //
		}
		return computeBgColors[index]; // 
	}

	/**
	 * ά���ϵ�ֵ��һ�����ͽṹ!
	 * �ڶ������������еķ���!
	 * _orderConsMap �����ֶζ���
	 */
	private DefaultMutableTreeNode getGroupHeaderNode(String[] _chooseGroupFields, boolean _isTiled, boolean _isSubTotal, boolean _isCanTotal, boolean _isRowHeader, boolean _isOnlyThisGroup, String[][] _computeFields, HashMap _orderConsMap) {
		boolean isDoZeroAppend = true;//this.tbutil.getSysOptionBooleanValue("�����Ƿ������㱨����", true); //Ĭ��������,����ʱ�м�ʱ����Ҫǿ��ȥ���������!  20120911Ԭ�����޸�    Ŀǰ�е��ֶ���Ҫ���㣬���Լ�������㱨����
		HashVO hvo_root = new HashVO(); //
		hvo_root.setAttributeValue("ItemType", "ROOT"); //
		hvo_root.setAttributeValue("ItemValue", "ROOT"); //
		hvo_root.setToStringFieldName("ItemValue"); //
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(hvo_root); //�����
		HashMap[] extConfMaps = new HashMap[_computeFields.length]; //
		for (int i = 0; i < _computeFields.length; i++) { //��������������
			if (_computeFields[i][1] != null && !_computeFields[i][1].trim().equals("")) { //�����Ϊ��!!
				extConfMaps[i] = tbutil.convertStrToMapByExpress(_computeFields[i][1], ";", "="); //
			}
		}
		if (_chooseGroupFields.length == 0) { //���ά��Ϊ��,���Զ��������г��ڹ�����
			for (int r = 0; r < _computeFields.length; r++) { //���һ����ݼ����н��з��ѣ�����
				HashVO hvo_splitCp = new HashVO(); //�����з���
				hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
				hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
				hvo_splitCp.setAttributeValue("CpType", _computeFields[r][0]); //���������,����count,sum
				hvo_splitCp.setAttributeValue("CpConfig", _computeFields[r][1]); //����������Ϣ
				hvo_splitCp.setAttributeValue("CpField", _computeFields[r][2]); //������ֶ�����
				hvo_splitCp.setAttributeValue("CpIndexNo", "" + (r + 1)); //�����е�˳����
				if (extConfMaps[r] != null && extConfMaps[r].containsKey("��ʾ����")) { //���ǿ��ָ����
					hvo_splitCp.setAttributeValue("ViewText", extConfMaps[r].get("��ʾ����")); // + "(" + _computeFields[r][0] + ")"
				} else {
					hvo_splitCp.setAttributeValue("ViewText", _computeFields[r][2]); // + "(" + _computeFields[r][0] + ")"
				}
				hvo_splitCp.setToStringFieldName("ViewText"); ////
				DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); //
				rootNode.add(computeNode); //��������н�㣡��
			}
		} else {
			if (_isTiled) { //�����ƽ�̵�,���߼�������һ��,��ƽ��ʱ��Զֻ�����㣡����һ����ά�ȵ�����,�ڶ�����ά�ȵ�ֵ!
				for (int i = 0; i < _chooseGroupFields.length; i++) { //����ά��ֵ����
					HashVO hvo_tiled = new HashVO(); //ƽ�̵�VO
					hvo_tiled.setAttributeValue("ItemType", "Tiled"); //ƽ�̵�����
					hvo_tiled.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
					hvo_tiled.setAttributeValue("LevelName", _chooseGroupFields[i]); //ά������
					hvo_tiled.setToStringFieldName("LevelName"); //��ʾ��ֵ����ά������
					DefaultMutableTreeNode itemNode1 = new DefaultMutableTreeNode(hvo_tiled); //�����ӽ��!
					rootNode.add(itemNode1); //�����ӽ�㣡��

					String[] thisLevelValue = findOneLevelDistinctValue(_chooseGroupFields[i]); //�������һ��ά���µ�ʵ��ֵ,�п��ܳ��֡���ֵ�����������

					//�������,����㱨���ƣ�������Щά��������ݿ���û��,����Ҫ��ʾά�ȱ���,ֵ���㴦��!!
					//��Ҫ�ر�ע�����,����ά�ȵĲ�����ƱȽ��ر�,����Ҫǿ�в�ȫ�Ļ���!�������ʱ��ά�ȵ���ǰ���ʱ��ά��֮��Ҫ����!�������ѯ��������1����,�����ݿ�����Сֵ��2����,1����Ҳ������ô? �����Զǿ�д�1���Ȳ�ȫ,�������ѯ����ֻ��3����,�᲻����Ϊ�Ǵ��?,
					if (isDoZeroAppend && !isContainDrillGroup(_chooseGroupFields[i])) { //������Ǳ���ȡ��.
						if (this.zeroReportConfMap != null && zeroReportConfMap.containsKey(_chooseGroupFields[i])) { //������ά�ȶ����˱��벹�������!
							String[] str_zerodef = (String[]) zeroReportConfMap.get(_chooseGroupFields[i]); //value������һ��һά���飡
							thisLevelValue = this.tbutil.getSpanFromTwoArray(thisLevelValue, str_zerodef); //����������ϲ�!
						}
					}
					//��������!!���ͬʱ���ڲ������������,�����Ȳ���������!ʵ���ϴ����(����ĳ��������)�������벹��Ķ�����һ���ģ� ��Ϊʲô�����һ����?��Ϊ��ʱ��ʱ���ܼܺ򵥣��������,��ֻҪ����������ǰ��Ϳ�����!������ʡ�ԡ���
					if (!_isRowHeader && isSortByCpValue) { //ֻ�����ϵ�ά�Ƚ������а���!
						//��������ά��,Ȼ���Map��ȡ�ø���ά�ȵ�ʵ��ֵ!Ȼ�󹹽�һ��HashVO[],��һ��������,�ڶ����ǽ��,Ȼ����а�����С����! Ȼ��������������ȡ����!
						HashVO[] sortVOs = new HashVO[thisLevelValue.length]; //
						for (int s = 0; s < thisLevelValue.length; s++) { //������s,��ʾsort֮��!
							String str_mapKey = _chooseGroupFields[i] + "=" + thisLevelValue[s] + "��" + "@" + chooseComputeFunAndField[0][2] + "(" + chooseComputeFunAndField[0][0] + ")";
							Double ld_sortValue = (Double) beforeBuilderDataMap.get(str_mapKey); //
							if (ld_sortValue == null) {
								ld_sortValue = 0D;
							}
							sortVOs[s] = new HashVO(); //
							sortVOs[s].setAttributeValue("name", thisLevelValue[s]); //
							sortVOs[s].setAttributeValue("value", ld_sortValue); //
							//System.out.println("��" + str_mapKey + "��=��" + ld_sortValue + "��"); //
						}
						String[] str_sortedNames = new String[sortVOs.length]; //
						tbutil.sortHashVOs(sortVOs, new String[][] { { "value", "Y", "Y" } }); //��������,���ݽ������!!�����ǵ���!
						for (int s = 0; s < str_sortedNames.length; s++) { //������s,��ʾsort֮��!
							str_sortedNames[s] = sortVOs[s].getStringValue("name"); //
						}
						thisLevelValue = str_sortedNames; //
					} else {
						if (_orderConsMap != null && _orderConsMap.containsKey(_chooseGroupFields[i])) { // ���û�����÷������򣬰������ݿ�鴦��˳����ʾ�������˾�ִ��������߼���
							TBUtil.getTBUtil().sortStrsByOrders(thisLevelValue, (String[]) _orderConsMap.get(_chooseGroupFields[i])); //ָ������!
						} else {
							TBUtil.getTBUtil().sortStrs(thisLevelValue); //��ACSAII������
						}
					}
					//Ԭ���� 20130723 �޸�  �������������  ����ֵ����ʾ�����
					if (null != thisLevelValue && TBUtil.getTBUtil().isExistInArray("����ֵ��", thisLevelValue)) {
						MoveToEnd("����ֵ��", thisLevelValue);
					}
					//�����к��ٴ������!!
					if (filterGroupValueMap != null && filterGroupValueMap.containsKey(_chooseGroupFields[i])) { //��������˹�������
						String[] str_filterDatas = (String[]) filterGroupValueMap.get(_chooseGroupFields[i]); //�����ά�ȱ�������!
						if (str_filterDatas != null && str_filterDatas.length > 0) {
							thisLevelValue = tbutil.getInterSectionFromTwoArray(thisLevelValue, str_filterDatas); //ȡ����
						}
					}

					DefaultMutableTreeNode[] itemNodes2 = new DefaultMutableTreeNode[thisLevelValue.length]; //
					for (int j = 0; j < itemNodes2.length; j++) { //�������н��
						HashVO hvo_level = new HashVO(); //������ά��ֵ���͵�����!���ʵ�����Ǹ����������͵�������ƥ�����ģ���
						hvo_level.setAttributeValue("ItemType", "Level"); //�����Ĳ㼶
						hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
						hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //���ά�ȵ����ƣ���
						hvo_level.setAttributeValue("LevelValue", thisLevelValue[j]); //���ά�ȵ�ʵ��ֵ����
						hvo_level.setToStringFieldName("LevelValue"); //��ʾ����ά��ֵ
						itemNodes2[j] = new DefaultMutableTreeNode(hvo_level); //����
						itemNode1.add(itemNodes2[j]); //��Ϊ�ӽ����룡��

						//�ж��Ƿ�ѡ���˶�������У��Ƿ�Ҫ�ٴν��з��ѣ���ֻ������ͷ���з��ѣ�����
						if (_isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //����ж�������У�����Ҫ�ٴν��з���,ֻ����ͷ�Ͻ��з��ѣ������������ά��Ϊ��ʱ(��ֻ����ά��)��Ҳ�ǲ����ѵ�,��Ϊ��ʱ�������Զ��Ƶ�������
							for (int k = 0; k < _computeFields.length; k++) { //���һ����ݼ����н��з��ѣ�����
								HashVO hvo_splitCp = new HashVO(); //�����з���
								hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
								hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_splitCp.setAttributeValue("CpType", _computeFields[k][0]); //���������,����count,sum
								hvo_splitCp.setAttributeValue("CpConfig", _computeFields[k][1]); //����������Ϣ
								hvo_splitCp.setAttributeValue("CpField", _computeFields[k][2]); //������ֶ�����
								hvo_splitCp.setAttributeValue("CpIndexNo", "" + (k + 1)); //�����е�˳����
								if (extConfMaps[k] != null && extConfMaps[k].containsKey("��ʾ����")) { //���ǿ��ָ����
									hvo_splitCp.setAttributeValue("ViewText", extConfMaps[k].get("��ʾ����")); // + "(" + _computeFields[r][0] + ")"
								} else {
									hvo_splitCp.setAttributeValue("ViewText", _computeFields[k][2]); //+ "(" + _computeFields[k][0] + ")"
								}
								hvo_splitCp.setToStringFieldName("ViewText"); //
								DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); ////
								itemNodes2[j].add(computeNode); //��������н�㣡��
							}
						}
					}

					//����ʽ�飡��������һЩ���ǹ�ʽ��,���硾({�߷���}+{�������})/({�ͷ���}+{��С����})��....
					if (_isRowHeader && rowHeaderFormulaGroupMap != null && rowHeaderFormulaGroupMap.containsKey(_chooseGroupFields[i])) { //
						String[][] str_appendFormulaGroups = (String[][]) rowHeaderFormulaGroupMap.get(_chooseGroupFields[i]); //new String[][] { { "���и߷���", "{�߷���}+{�������}", "�Ƿ���ٷֺ�=N" }, { "�߷���/�ͷ���", "({�߷���}*100)/{��С����}", "�Ƿ���ٷֺ�=Y" } }; //...
						for (int k = 0; k < str_appendFormulaGroups.length; k++) {
							HashVO hvo_level = new HashVO(); //������ά��ֵ���͵�����!���ʵ�����Ǹ����������͵�������ƥ�����ģ���
							hvo_level.setAttributeValue("ItemType", "FormulaLevel"); //��ʽ��
							hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
							hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //���ά�ȵ����ƣ���
							hvo_level.setAttributeValue("LevelValue", str_appendFormulaGroups[k][0]); //���ά�ȵ�ʵ��ֵ����
							hvo_level.setAttributeValue("ContainGroups", thisLevelValue); //������Щ��!
							hvo_level.setAttributeValue("LevelFormula", str_appendFormulaGroups[k][1]); //�����Ĺ�ʽ
							hvo_level.setAttributeValue("LevelConfig", str_appendFormulaGroups[k][2]); //����������Ϣ
							hvo_level.setToStringFieldName("LevelValue"); //��ʾ����ά��ֵ...

							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
							itemNode1.add(childNode); //�����������
						}
					}

					//�ж��Ƿ���Ҫ����С�ƣ�����ƽ����û�кϼƵĸ���ģ�����Ϊ��Ȼ��ƽ��,��˵���ܹ���һ��,��νС����ʵ�ǵȼ��ںϼƣ���
					if (isTotal && _isCanTotal && _isSubTotal) { //�����С��,���ҵ�ȷ���ܹ��ϼƼ����(����avg�ǲ����ԺϼƵ�)!!
						if (_computeFields.length > 1 && _isRowHeader && !_isOnlyThisGroup) { //����ж�������У�����Ҫ�ٴν��з���!
							for (int k = 0; k < _computeFields.length; k++) { //���һ����ݼ����н��з��ѣ������ں���Ҫ��
								if (isCaneTotlal(_computeFields[k][0])) { //�����һ�������е�ȷ�ǡ��������򡰽����ֿ��ԺϼƵģ���������������У�Ҳ����˵��������������У�һ��������,һ���ǰٷֱȣ���ֻ��������һ���ϼ��У�
									HashVO hvo_subtotal = new HashVO(); //С����
									hvo_subtotal.setAttributeValue("ItemType", "SubTotal"); //С�ƣ���
									hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
									hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[k][0]); //
									hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[k][1]); //����������Ϣ
									hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[k][2]); //
									if (extConfMaps[k] != null && extConfMaps[k].containsKey("��ʾ����")) { //���ǿ��ָ����
										hvo_subtotal.setAttributeValue("ViewText", extConfMaps[k].get("��ʾ����") + "&С��"); // + "(" + _computeFields[r][0] + ")"
									} else {
										hvo_subtotal.setAttributeValue("ViewText", _computeFields[k][2] + "&С��"); //
									}
									hvo_subtotal.setToStringFieldName("ViewText"); //
									DefaultMutableTreeNode subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
									itemNode1.add(subTotalNode); //
								}
							}
						} else { //�������ͷ,��϶�ֻ��һ���ϼ�!��ֻ��һ����������϶�ֻ��һ��С�ƣ�
							HashVO hvo_subtotal = new HashVO(); //С����
							hvo_subtotal.setAttributeValue("ItemType", "SubTotal"); //С�ƣ���
							hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
							hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[0][0]); //
							hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[0][1]); //����������Ϣ����
							hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[0][2]); //
							hvo_subtotal.setAttributeValue("ViewText", "С��"); //
							hvo_subtotal.setToStringFieldName("ViewText"); //
							DefaultMutableTreeNode subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
							itemNode1.add(subTotalNode); //
						}
					}
				}
			} else { //����ǲ㼶
				for (int i = 0; i < _chooseGroupFields.length; i++) { //ѭ������!!
					DefaultMutableTreeNode[] thisLevelNodes = getSomeLevelAllNodes(rootNode, i); //ȡ��ĳһ��������ӽ��!��һ��ֻ�᷵�ظ����!
					for (int j = 0; j < thisLevelNodes.length; j++) { //������һ������н��!
						HashVO hvo_thisNodeVO = (HashVO) thisLevelNodes[j].getUserObject(); ////
						String str_thisNodeType = hvo_thisNodeVO.getStringValue("ItemType"); //

						String[] str_pathLevelName = new String[i];
						String[] str_pathLevelValue = new String[i]; //
						String str_pathMapkey = ""; //
						TreeNode[] pathNodes = thisLevelNodes[j].getPath(); // ��ȡ�ý�㵽�ò�����н��
						for (int k = 0; k < str_pathLevelName.length; k++) {
							str_pathLevelName[k] = _chooseGroupFields[k]; //ά������!���硾ҵ�����͡�������������
							HashVO hvo_item = (HashVO) ((DefaultMutableTreeNode) pathNodes[k + 1]).getUserObject(); //
							str_pathLevelValue[k] = hvo_item.toString(); //ά�ȵ�ֵ,���硾����ҵ��
							str_pathMapkey = str_pathMapkey + str_pathLevelName[k] + "=" + str_pathLevelValue[k] + "��"; //
						}

						//�ؼ������߼��������ҵ���������������Ѫ���Ķ���,����һ��֧�¿����������ӡ���Ϊ��ͬ�㼶�����в�ͬ��ֵ
						String[] str_newchildNames = findNewChildName(str_pathLevelName, str_pathLevelValue, _chooseGroupFields[i]); //���ܻ���֡���ֵ��

						//�������,����㱨���ƣ�������Щά��������ݿ���û��,����Ҫ��ʾά�ȱ���,ֵ���㴦��!
						//ʱ��ά����Ҫ�ر�ע�⣡����Ӧ�����Զ�ƴ��!
						if (isDoZeroAppend && !isContainDrillGroup(_chooseGroupFields[i])) { //������Ǳ���ȡ��.
							if (this.zeroReportConfMap != null && zeroReportConfMap.containsKey(_chooseGroupFields[i])) { //������ά�ȶ����˱��벹�������!
								String[] str_zerodef = (String[]) zeroReportConfMap.get(_chooseGroupFields[i]); //value������һ��һά���飡
								str_newchildNames = this.tbutil.getSpanFromTwoArray(str_newchildNames, str_zerodef); //����������ϲ�!
							}
						}
						//���ʴ���Ŀ������,����Ԥ��ʱ,ֻ��ʾ���¼�¼,Ȼ���ٵ�������!!Ҳ����˵���˺����ŷ���
						if (!_isRowHeader && isSortByCpValue) { //�������ά��(ֻ����ά�Ƚ������д���),��ָ����Ҫ��������!!
							//��������ά��,Ȼ���Map��ȡ�ø���ά�ȵ�ʵ��ֵ!Ȼ�󹹽�һ��HashVO[],��һ��������,�ڶ����ǽ��,Ȼ����а�����С����! Ȼ��������������ȡ����!
							HashVO[] sortVOs = new HashVO[str_newchildNames.length]; //
							for (int s = 0; s < str_newchildNames.length; s++) { //������s,��ʾsort֮��!
								String str_mapKey = str_pathMapkey + _chooseGroupFields[i] + "=" + str_newchildNames[s] + "��" + "@" + chooseComputeFunAndField[0][2] + "(" + chooseComputeFunAndField[0][0] + ")"; //ȡ��Map�е�Key,����Ӧ�ÿ��Ը��ݵ��������е�ֵȡ����
								//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
								BigDecimal ld_sortValue = (BigDecimal) beforeBuilderDataMap.get(str_mapKey); //ȡ��
								if (ld_sortValue == null) { //
									ld_sortValue = new BigDecimal(0);
								}
								sortVOs[s] = new HashVO(); //
								sortVOs[s].setAttributeValue("name", str_newchildNames[s]); //
								sortVOs[s].setAttributeValue("value", ld_sortValue); //
								//System.out.println("��" + str_mapKey + "��=��" + ld_sortValue + "��"); //
							}
							String[] str_sortedNames = new String[sortVOs.length]; //
							tbutil.sortHashVOs(sortVOs, new String[][] { { "value", "Y", "Y" } }); //��������,���ݽ������!!�����ǵ���!
							for (int s = 0; s < str_sortedNames.length; s++) { //������s,��ʾsort֮��!
								str_sortedNames[s] = sortVOs[s].getStringValue("name"); //
							}
							str_newchildNames = str_sortedNames; //
						} else {
							if (_orderConsMap != null && _orderConsMap.containsKey(_chooseGroupFields[i])) {// ���û�����÷������򣬰������ݿ�鴦��˳����ʾ�������˾�ִ��������߼���
								TBUtil.getTBUtil().sortStrsByOrders(str_newchildNames, (String[]) _orderConsMap.get(_chooseGroupFields[i])); //��Ϊ���������б�������,���Ա���ʹ��TBUtil�е����򷽷�!!
							} else {
								//	TBUtil.getTBUtil().sortStrs(str_newchildNames); //��ACSAII������  20130206  Ԭ��������  Ĭ�ϵ�Ӧ��Ϊ���ݿ�����˳�򣬲�����ACSAII������
							}
						}
						//Ԭ���� 20130723 �޸�  �������������  ����ֵ����ʾ�����
						if (null != str_newchildNames && TBUtil.getTBUtil().isExistInArray("����ֵ��", str_newchildNames)) {
							MoveToEnd("����ֵ��", str_newchildNames);
						}
						//����������!!!��ֻ����ʾ��Щ����!���˱����������!
						if (filterGroupValueMap != null && filterGroupValueMap.containsKey(_chooseGroupFields[i])) { //��������˹�������
							String[] str_filterDatas = (String[]) filterGroupValueMap.get(_chooseGroupFields[i]); //
							if (str_filterDatas != null && str_filterDatas.length > 0) {
								str_newchildNames = tbutil.getInterSectionFromTwoArray(str_newchildNames, str_filterDatas); //ȡ����
							}
						}

						//ѭ�����������ӽ��!!!
						if (!str_thisNodeType.endsWith("Total")) { //�����С�ƻ�ϼ���,����Զ�����ӽ��!
							for (int k = 0; k < str_newchildNames.length; k++) {
								HashVO hvo_level = new HashVO(); //������ά��ֵ���͵�����!���ʵ�����Ǹ����������͵�������ƥ�����ģ���
								hvo_level.setAttributeValue("ItemType", "Level"); //�����Ĳ㼶
								hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //���ά�ȵ����ƣ���
								hvo_level.setAttributeValue("LevelValue", str_newchildNames[k]); //���ά�ȵ�ʵ��ֵ����
								hvo_level.setToStringFieldName("LevelValue"); //��ʾ����ά��ֵ...
								DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
								thisLevelNodes[j].add(childNode); //�����������

								//�ټ����ϼ�����!
								if (i == _chooseGroupFields.length - 1 && _isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //�����ĩ���,���ж�������У�����Ҫ�ٴν��з���!! ֻ����ͷ�Ͻ��з��ѣ������������ά��Ϊ��ʱ(��ֻ����ά��)��Ҳ�ǲ����ѵ�,��Ϊ��ʱ�������Զ��Ƶ�������
									for (int r = 0; r < _computeFields.length; r++) { //���һ����ݼ����н��з��ѣ�����
										HashVO hvo_splitCp = new HashVO(); //�����з���
										hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
										hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
										hvo_splitCp.setAttributeValue("CpType", _computeFields[r][0]); //���������,����count,sum
										hvo_splitCp.setAttributeValue("CpConfig", _computeFields[r][1]); //����������Ϣ
										hvo_splitCp.setAttributeValue("CpField", _computeFields[r][2]); //������ֶ�����
										hvo_splitCp.setAttributeValue("CpIndexNo", "" + (r + 1)); //�����е�˳����
										if (extConfMaps[r] != null && extConfMaps[r].containsKey("��ʾ����")) { //���ǿ��ָ����
											hvo_splitCp.setAttributeValue("ViewText", extConfMaps[r].get("��ʾ����")); // + "(" + _computeFields[r][0] + ")"
										} else {
											hvo_splitCp.setAttributeValue("ViewText", _computeFields[r][2]); // + "(" + _computeFields[r][0] + ")"
										}
										hvo_splitCp.setToStringFieldName("ViewText"); ////
										DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); //
										childNode.add(computeNode); //��������н�㣡��
									}
								}
							}
						}

						//����ʽ�飡��������һЩ���ǹ�ʽ��,���硾({�߷���}+{�������})/({�ͷ���}+{��С����})��....
						if (!str_thisNodeType.endsWith("Total") && _isRowHeader && (i == _chooseGroupFields.length - 1) && rowHeaderFormulaGroupMap != null && rowHeaderFormulaGroupMap.containsKey(_chooseGroupFields[i])) { //
							String[][] str_appendFormulaGroups = (String[][]) rowHeaderFormulaGroupMap.get(_chooseGroupFields[i]); //new String[][] { { "���и߷���", "{�߷���}+{�������}", "�Ƿ���ٷֺ�=N" }, { "�߷���/�ͷ���", "({�߷���}*100)/{��С����}", "�Ƿ���ٷֺ�=Y" } }; //...
							for (int k = 0; k < str_appendFormulaGroups.length; k++) {
								HashVO hvo_level = new HashVO(); //������ά��ֵ���͵�����!���ʵ�����Ǹ����������͵�������ƥ�����ģ���
								hvo_level.setAttributeValue("ItemType", "FormulaLevel"); //��ʽ��
								hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //���ά�ȵ����ƣ���
								hvo_level.setAttributeValue("LevelValue", str_appendFormulaGroups[k][0]); //���ά�ȵ�ʵ��ֵ����
								hvo_level.setAttributeValue("ContainGroups", str_newchildNames); //������Щ��!
								hvo_level.setAttributeValue("LevelFormula", str_appendFormulaGroups[k][1]); //�����Ĺ�ʽ
								hvo_level.setAttributeValue("LevelConfig", str_appendFormulaGroups[k][2]); //����������Ϣ
								hvo_level.setToStringFieldName("LevelValue"); //��ʾ����ά��ֵ...

								DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
								thisLevelNodes[j].add(childNode); //�����������
							}
						}

						//�ж��Ƿ���Ҫ����С�ƣ�����Ҳ����˵ÿһ�㶼��С�ƣ�����
						if (isTotal && _isCanTotal && (_isSubTotal || i == 0)) { //�����С��,�ҿ��ԺϼƼ���(����avg�Ͳ��ܺϼƼ���)�������û��ָ��С��,��Ĭ�����кϼƵ�(��i==0ʱ���Ǻϼ�)
							if (!str_thisNodeType.equals("SubTotal") && !str_thisNodeType.equals("Total")) { //������˾���С��,���������!��С�ƽ����Զ��Ҷ�ӽ�㣡��
								if (_isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //��������ͷ�Ÿ���С��,�������ͷ�Ǽ�����ʱ������
									for (int k = 0; k < _computeFields.length; k++) { //���һ����ݼ����н��з��ѣ�����
										if (isCaneTotlal(_computeFields[k][0])) { //�������ܹ��ϼƵ�����!!!����avg��ʵ���ϲ��ܺϼ�!
											HashVO hvo_subtotal = new HashVO(); //С����
											hvo_subtotal.setAttributeValue("ItemType", i == 0 ? "" : "SubTotal"); //С�ƣ���
											hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
											hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[k][0]); //����ж��������,����Ӧ��Ҳ�Ƕ��С��,���硾�����¼�-С�ơ�����ʧ���-С�ơ�!!
											hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[k][1]); //
											hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[k][2]); //
											if (extConfMaps[k] != null && extConfMaps[k].containsKey("��ʾ����")) { //���ǿ��ָ����
												hvo_subtotal.setAttributeValue("ViewText", extConfMaps[k].get("��ʾ����") + (i == 0 ? "" : "&С��")); // + "(" + _computeFields[r][0] + ")"
											} else {
												hvo_subtotal.setAttributeValue("ViewText", i == 0 ? (_computeFields[k][2] + "") : (_computeFields[k][2] + "&С��")); //
											}
											hvo_subtotal.setToStringFieldName("ViewText"); //
											DefaultMutableTreeNode subTotalNode = null;
											if (hvo_subtotal.getAttributeValue("ItemType") == null || hvo_subtotal.getAttributeValue("ItemType").equals("")) {
											} else {
												subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
												thisLevelNodes[j].add(subTotalNode); //
											}

										}
									}
								} else { //��������ϵ�С��/�ϼ�,����Զֻ��һ��������˵�ܹ���һ�������У�
									HashVO hvo_subtotal = new HashVO(); //С����
									hvo_subtotal.setAttributeValue("ItemType", i == 0 ? "" : "SubTotal"); //С�ƣ���
									hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
									hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[0][0]); //����ж��������,����Ӧ��Ҳ�Ƕ��С��,���硾�����¼�-С�ơ�����ʧ���-С�ơ�!!
									hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[0][1]); //
									hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[0][2]); //
									hvo_subtotal.setAttributeValue("ViewText", i == 0 ? "" : "С��"); //
									hvo_subtotal.setToStringFieldName("ViewText"); //
									DefaultMutableTreeNode subTotalNode = null;
									if (hvo_subtotal.getAttributeValue("ItemType") == null || hvo_subtotal.getAttributeValue("ItemType").equals("")) {
									} else {
										subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
										thisLevelNodes[j].add(subTotalNode); //
									}

								}
							}
						}
					}

				}
			} //����ǲ㼶����!!!
		} //�����ά��

		//��������Ҷ�ӽ��,һ����Ϊ����Ҷ�ӽ��������У�ά�����ƣ�ά��ֵ��������ֵ��������������ȫ��Ϊ���������!
		DefaultMutableTreeNode[] allLeafNodes = getAllLeafNodes(rootNode); //ȡ������Ҷ�ӽ�㣡����
		for (int i = 0; i < allLeafNodes.length; i++) { //�����������ӽ��!
			HashVO hvo_leaf = (HashVO) allLeafNodes[i].getUserObject(); ////

			StringBuilder sb_levelNameValue = new StringBuilder(); //
			TreeNode[] parentNodes = allLeafNodes[i].getPath(); //ȡ������������и��׽�㣡
			for (int j = 1; j < parentNodes.length; j++) { //�������и��׽�㣡��
				HashVO hvo_nodeItem = (HashVO) ((DefaultMutableTreeNode) parentNodes[j]).getUserObject(); //ĳ�����׽��!!
				if (hvo_nodeItem.getStringValue("ItemType").equals("Level") || hvo_nodeItem.getStringValue("ItemType").equals("FormulaLevel")) { //������Level��
					sb_levelNameValue.append(hvo_nodeItem.getStringValue("LevelName") + "=" + hvo_nodeItem.getStringValue("LevelValue") + (_isRowHeader ? "��" : "��")); ////
				}
			}
			hvo_leaf.setAttributeValue("RealLevelNameValue", sb_levelNameValue.toString()); //
			String str_itemType = hvo_leaf.getStringValue("ItemType"); //
			if (str_itemType.equals("SplitCP")) { //���������Ƿ��ѵ�
				hvo_leaf.setAttributeValue("RealCPType", hvo_leaf.getStringValue("CpType")); //
				hvo_leaf.setAttributeValue("RealCPField", hvo_leaf.getStringValue("CpField")); //
				hvo_leaf.setAttributeValue("RealCPConfig", hvo_leaf.getStringValue("CpConfig")); //
			} else if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) {
				hvo_leaf.setAttributeValue("RealCPType", hvo_leaf.getStringValue("SubTotalType")); //
				hvo_leaf.setAttributeValue("RealCPField", hvo_leaf.getStringValue("SubTotalField")); //
				hvo_leaf.setAttributeValue("RealCPConfig", hvo_leaf.getStringValue("SubTotalConfig")); //
			} else { //���Ҷ�ӽ��Ȳ��Ƿ��ѵ�,Ҳ���ǺϼƵ�,�������ֻ��һ��������,����ֱ��ʹ�õ�һλ�����У�
				hvo_leaf.setAttributeValue("RealCPType", _computeFields[0][0]); //
				hvo_leaf.setAttributeValue("RealCPConfig", _computeFields[0][1]); //
				hvo_leaf.setAttributeValue("RealCPField", _computeFields[0][2]); //
			}
		}

		return rootNode; //
	}

	//Ԭ���� 20130723 ��� ��һ�����е�һ��Ԫ���ƶ���ĩβ ���ȸ�����Ӧ��ֻ����һ��str
	public void MoveToEnd(String str, String[] thisLevelValue) {
		int iStr = 0;
		for (int i = 0; i <= thisLevelValue.length - 1 - iStr; i++) {
			if (!thisLevelValue[i].equals(str)) {
				continue;
			} else {
				for (int j = i + 1; j <= thisLevelValue.length - 1; j++) {
					thisLevelValue[j - 1] = thisLevelValue[j];
				}
				thisLevelValue[thisLevelValue.length - 1] = str;
				i--;
				iStr++;
			}
		}
	}

	//ȡ��ĳһ����������·����ȫ�ƣ���
	private String getPathName(DefaultMutableTreeNode _node) {
		TreeNode[] parentNodes = _node.getPath(); //
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 1; i < parentNodes.length; i++) {
			sb_text.append(((DefaultMutableTreeNode) parentNodes[i]).getUserObject() + "-"); //
		}
		String str_return = sb_text.toString(); //
		if (str_return.endsWith("-")) {
			str_return = str_return.substring(0, str_return.length() - 1); //
		}
		return str_return; //
	}

	/**
	 * �����ĳһά�ȵ�ʵ��ֵ,Ҫ��Ψһ�Թ���!
	 * @return
	 */
	private String[] findOneLevelDistinctValue(String _groupLevelName) {
		HashSet hst = new HashSet(); //
		for (int i = 0; i < hvs_data.length; i++) { //��������ȡ��ĳ��ά�ȵ�Ψһ�Ե�����!!!
			String str_value = hvs_data[i].getStringValue(_groupLevelName, "����ֵ��", true); //
			hst.add(str_value); //
		}

		return (String[]) hst.toArray(new String[0]); //
	}

	/**
	 * �ҳ�ĳһ֧������һ������ж���
	 * 
	 * @param str_pathlevelname
	 *            Ѫϵ���������,���� ���,����,��Ʒ. ��Ҳ�������� ����,�游,����,
	 * @param str_pathvalue
	 *            Ѫϵ�����ֵ ���� 2008,����,���� ��Ҳ�������� ����,Ӻ��,Ǭ¡
	 * @param string
	 *            �ҳ���һ�������ֵ,���� ��Ʒ����, ��Ҳ�������� ����, ����ֵӦ���� Ǭ¡���еĶ���, ������4��
	 * @return
	 */
	private String[] findNewChildName(String[] str_pathlevelname, String[] str_pathvalue, String _newlevelname) {
		ArrayList al = new ArrayList();
		//HashSet lhs = new HashSet(); //   �ɵĴ���  Ԭ���� 2013-02-26����  ���ղ�ѯ˳����ʾ
		for (int i = 0; i < hvs_data.length; i++) { // ��������ȡ��ĳ��ά�ȵ�Ψһ�Ե�����!!!
			boolean bo_ismatch = true;
			for (int j = 0; j < str_pathlevelname.length; j++) { //������������ά���ϵ�ֵ,����ȫ��ƥ��,����Ϊ�Ƕ�����!
				if (!hvs_data[i].getStringValue(str_pathlevelname[j]).equals(str_pathvalue[j])) { //�����һ���Բ�������Ϊ�϶Բ���
					bo_ismatch = false; //
					break;
				}
			}
			if (bo_ismatch) { // �������������,��˳��������,�����
				//  lhs.add(hvs_data[i].getStringValue(_newlevelname, "����ֵ��", true)); // �ҳ�Ψһ�Ե�ֵ! //   �ɵĴ���  Ԭ���� 2013-02-26����  ���ղ�ѯ˳����ʾ
				String temp = hvs_data[i].getStringValue(_newlevelname, "����ֵ��", true);
				if (al.indexOf(temp) == -1) { //���ж���û���ظ�
					al.add(temp);//����˵����ƹ�ϵͳ������ظ������Ǽ���һ���ж�   �ɵĴ���  Ԭ���� 2013-02-23����  ���ղ�ѯ˳����ʾ
				}
			}
		}
		return (String[]) al.toArray(new String[0]); //
	}

	private boolean isContainDrillGroup(String _groupName) {
		if (drillConditionMap != null && drillConditionMap.length > 0) {
			for (int i = 0; i < drillConditionMap.length; i++) {
				if (drillConditionMap[i].containsKey(_groupName)) { //��������������,��˵���Ǳ���ȡ��!��ͺ��Բ������
					return true; //
				}
			}
		}

		return false; //
	}

	/**
	 * ȡ��ĳһ������н��!
	 * 
	 * @return
	 */
	private DefaultMutableTreeNode[] getSomeLevelAllNodes(DefaultMutableTreeNode _node, int _level) {
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		Vector v_return = new Vector(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getLevel() == _level) {
				v_return.add(allNodes[i]); //
			}
		}
		return (DefaultMutableTreeNode[]) v_return.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * ȡ��һ�����в�ε���_level�����н��
	 * 
	 * @param _nodes
	 * @param _level
	 * @return
	 */
	private DefaultMutableTreeNode[] getLevelNodes(DefaultMutableTreeNode[] _nodes, int _level) {
		ArrayList al_nodes = new ArrayList();
		for (int i = 0; i < _nodes.length; i++) {
			if (_nodes[i].getLevel() == _level) {
				al_nodes.add(_nodes[i]); //
			}
		}
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]);
	}

	private DefaultMutableTreeNode[] getAllNodes(DefaultMutableTreeNode _node) {
		Vector vector = new Vector(); //
		visitAllNodes(vector, _node); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
		return allNodes; //
	}

	/**
	 * ȡ��ĳ����������ĩ��������
	 * @param _node
	 * @return
	 */
	private int getLeafNodeCount(DefaultMutableTreeNode _node) {
		return getLeafNodeCountAndLevel(_node)[0]; //
	}

	private int[] getLeafNodeCountAndLevel(DefaultMutableTreeNode _node) {
		int li_count = 0;
		int li_level = 0; //
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) {
				li_count++;
				if (allNodes[i].getLevel() > li_level) { //�����������!
					li_level = allNodes[i].getLevel(); //
				}
			}
		}
		return new int[] { li_count, li_level };
	}

	//�����һ������µ�����Ҷ�ӽ�㣡����
	private DefaultMutableTreeNode[] getAllLeafNodes(DefaultMutableTreeNode _node) {
		ArrayList al_nodes = new ArrayList(); //
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) {
				al_nodes.add(allNodes[i]); //
			}
		}

		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]);
	}

	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // ����ý��
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
			}
		}
	}

	private String[] getAllChooseGroupFields() {
		String[] str_allGroupFields = new String[chooseGroupRowFields.length + chooseGroupColFields.length];
		System.arraycopy(chooseGroupRowFields, 0, str_allGroupFields, 0, chooseGroupRowFields.length); // �����ֶ�
		System.arraycopy(chooseGroupColFields, 0, str_allGroupFields, chooseGroupRowFields.length, chooseGroupColFields.length);
		return str_allGroupFields; //
	}

	private String[][] getComputeFunAndFields(ArrayList _al) {
		String[][] str_returns = new String[_al.size()][3];
		for (int i = 0; i < _al.size(); i++) {
			str_returns[i] = (String[]) _al.get(i);
		}
		return str_returns;
	}

	private boolean containComputeFunandField(ArrayList _al, String[] _oneComputeDefine) {
		for (int i = 0; i < _al.size(); i++) {
			String[] str_items = (String[]) _al.get(i); //
			if (str_items[0].equals(_oneComputeDefine[0]) && str_items[2].equals(_oneComputeDefine[2])) {
				return true;
			}
		}
		return false; //
	}

	//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
	private String trimBigDecimalToStr(BigDecimal _bigDecimal, String _cpType, HashMap _configMap) {
		String str_text = trimBigDecimalToStr(_bigDecimal); //
		if (!str_text.equals("")) {
			if (_cpType != null && (_cpType.indexOf("Percent") >= 0 || _cpType.indexOf("Increase") >= 0)) {
				str_text = str_text + "%"; //
			} else {
				if ("Y".equalsIgnoreCase((String) _configMap.get("�Ƿ���ٷֺ�"))) {
					str_text = str_text + "%"; //
				}
			}
		}
		return str_text; //
	}

	//��ǰΪdouble���ͣ�����ǧ�����ݻᱨ�����޸�֮�����/2019-03-21��
	private String trimBigDecimalToStr(BigDecimal _value) {
		if (_value == null) {
			return "";
		}
		if (_value.compareTo(new BigDecimal("0")) == 0) {
			return "0";
		}
		_value = _value.setScale(2, BigDecimal.ROUND_HALF_UP);
		String str_value = _value.toString();
		if (str_value.endsWith(".00")) {
			str_value = str_value.substring(0, str_value.indexOf(".00"));
		}
		return str_value;
	}

	private String trimDoubleToStr(Double _double, String _cpType, HashMap _configMap) {
		String str_text = trimDoubleToStr(_double); //
		if (!str_text.equals("")) {
			if (_cpType != null && (_cpType.indexOf("Percent") >= 0 || _cpType.indexOf("Increase") >= 0)) {
				str_text = str_text + "%"; //
			} else {
				if ("Y".equalsIgnoreCase((String) _configMap.get("�Ƿ���ٷֺ�"))) {
					str_text = str_text + "%"; //
				}
			}
		}
		return str_text; //
	}

	private String trimDoubleToStr(Double _double) {
		if (_double == null) {
			return "";
		}
		if (_double == 0) {
			return "0";
		}
		String str_value = "" + _double; //
		int li_pos = str_value.indexOf("."); //
		if (li_pos > 0) {
			String str_prefix = str_value.substring(0, li_pos); //
			String str_subfix = str_value.substring(li_pos + 1, str_value.length()); //���
			if (str_subfix.length() > 2) { //�������λС��
				str_subfix = str_subfix.substring(0, 2); //
			}
			if (str_subfix.equals("0") || str_subfix.equals("00")) {
				return str_prefix; //
			} else {
				return str_prefix + "." + str_subfix; //
			}
		} else {
			return str_value;
		}
	}

	private String trimDoubleToStr(String _str) {
		if (_str == null || _str.equals("")) {
			return "";
		}
		int li_pos = _str.indexOf("."); //
		if (li_pos > 0) {
			String str_prefix = _str.substring(0, li_pos); //
			String str_subfix = _str.substring(li_pos + 1, _str.length()); //���
			if (str_subfix.length() > 2) { //�������λС��
				str_subfix = str_subfix.substring(0, 2); //
			}
			return str_prefix + "." + str_subfix; //
		} else {
			return _str;
		}

	}

	/**
	 * ȡ��ʣ��Ŀɽ�����ȡ��ά��
	 * 
	 * @return
	 */
	private String[] getRemainGroupFields() {
		ArrayList al_remains = new ArrayList(); //
		if (null == allCanGroupFields) {
			return null;
		}
		for (int i = 0; i < allCanGroupFields.length; i++) {
			if (!isInArray(allCanGroupFields[i], chooseGroupRowFields) && !isInArray(allCanGroupFields[i], chooseGroupColFields)) {
				al_remains.add(allCanGroupFields[i]); //
			}
		}
		return (String[]) al_remains.toArray(new String[0]); //
	}

	private boolean isInArray(String _item, String[] _arrays) {
		for (int i = 0; i < _arrays.length; i++) {
			if (_arrays[i].equals(_item)) {
				return true;
			}
		}
		return false;
	}

	public String[] getAllCanGroupFields() {
		return allCanGroupFields;
	}

	public void setAllCanGroupFields(String[] allCanGroupFields) {
		this.allCanGroupFields = allCanGroupFields;
	}

	public String[] getAllCanComputeFields() {
		return allCanComputeFields;
	}

	public void setAllCanComputeFields(String[] allCanComputeFields) {
		this.allCanComputeFields = allCanComputeFields;
	}

	public BillCellPanel getCellPanel() {
		return cellPanel;
	}

	public String[] getChooseGroupRowFields() {
		return chooseGroupRowFields;
	}

	public String[] getChooseGroupColFields() {
		return chooseGroupColFields;
	}

	public String[][] getChooseComputeFunAndField() {
		return chooseComputeFunAndField;
	}

	public HashMap getQueryConditionMap() {
		return queryConditionMap;
	}

	public String getStr_drillActionClass() {
		return str_drillActionClass;
	}

	public void setStr_drillActionClass(String str_drillActionClass) {
		this.str_drillActionClass = str_drillActionClass;
	}

	public String getStr_drillTempletCode() {
		return str_drillTempletCode;
	}

	public void setStr_drillTempletCode(String str_drillTempletCode) {
		this.str_drillTempletCode = str_drillTempletCode;
	}

	public void setDrillgroupbindMap(HashMap drillgroupbindMap) {
		this.drillgroupbindMap = drillgroupbindMap;
	}

}
