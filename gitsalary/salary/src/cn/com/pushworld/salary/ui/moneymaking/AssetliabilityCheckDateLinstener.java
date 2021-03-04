package cn.com.pushworld.salary.ui.moneymaking;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.jepfunctions.ItemValueListener;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
/**
 * excelģ����У�鹫ʽ�Ľ���ִ��
 * @author ��Ӫ����2014/02/11��
 * */
public class AssetliabilityCheckDateLinstener implements ItemValueListener {

	public synchronized void process(BillPanel panel) {
		if (panel instanceof BillCellPanel) {
			BillCellPanel cellPanel = (BillCellPanel) panel;
			final AssetliabilityWKPanel wkpanel = (AssetliabilityWKPanel) cellPanel.getClientProperty("this"); // �õ�this����
			if (wkpanel == null) {
				MessageBox.show(cellPanel, "����BillListPanel������putClientProperTy(\"this\",class)");
				return;
			}
			BillCellVO billCellVO = cellPanel.getBillCellVO();
			BillCellItemVO[][] vos = billCellVO.getCellItemVOs();
			HashVO[] hashVO = new HashVO[vos.length];

			for (int i = 0; i < vos.length; i++) {
				hashVO[i] = new HashVO();
				for (int j = 0; j < vos[i].length; j++) {
					hashVO[i].setAttributeValue(convertIntColToEn(j + 1) + "", vos[i][j]);
				}
			}
			SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < vos[i].length; j++) {
					String value = vos[i][j].getValidateformula();
					if (!TBUtil.isEmpty(value)) {
						if (value.length() >= 1) {
							util.putDefaultFactorValue("���", hashVO);
							util.putDefaultFactorVO("�ַ���", "excel([" + "���" + "],\"����\",\"" + value + "\")", "���" + i + j, "", "");
							StringBuffer sb = new StringBuffer();
							Object rtvalue;
							try {
								rtvalue = util.onExecute(util.getFoctorHashVO("���" + i + j), hashVO[0], sb);
								System.out.println(rtvalue +" \r\b" + sb.toString());
								if (!(rtvalue == null || String.valueOf(rtvalue).equals("") || String.valueOf(rtvalue).equals("1.0") || String.valueOf(rtvalue).equals("1") || String.valueOf(rtvalue).equalsIgnoreCase("true"))) {
									vos[i][j].setBackground("255,0,0");
								} else {
									vos[i][j].setBackground("255,255,255");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			cellPanel.repaint();
		}
	}

	//������ת����Ӣ�ı���
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { //��������һ��
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}
}
