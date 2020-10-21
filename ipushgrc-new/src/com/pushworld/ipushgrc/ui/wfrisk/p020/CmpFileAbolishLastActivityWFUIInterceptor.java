package com.pushworld.ipushgrc.ui.wfrisk.p020;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept;

public class CmpFileAbolishLastActivityWFUIInterceptor implements WorkFlowUIIntercept {

	public void afterOpenWFProcessPanel(WorkFlowProcessPanel panel) throws Exception {
		BillCardPanel cardpanel = panel.getBillCardPanel();
		cardpanel.setGroupVisiable("���̴���", true);
		CardCPanel_CheckBox approveCheckBox = (CardCPanel_CheckBox) cardpanel.getCompentByKey("isapprove");
		approveCheckBox.getCheckBox().setSelected(true);//�Ƿ�ͬ�� ��ѡ��Ĭ��ѡ��
		BillVO billVO = cardpanel.getBillVO();
		String filestate = billVO.getStringValue("filestate");
		if ("2".equals(filestate)) {//�ļ�״̬��1- �༭��, 2- ����������, 3- ��Ч, 4- ��ֹ������, 5- ʧЧ
			String str_oldversion = billVO.getStringValue("versionno"); //�ɵİ汾��
			CardCPanel_ComboBox newversionBox = (CardCPanel_ComboBox) cardpanel.getCompentByKey("newversionno");
			ComBoxItemVO[] itemValues = null;
			if (str_oldversion == null || "".equals(str_oldversion)) { //���û�а汾�ţ���һ�η���Ĭ����Ϊ��1.0��
				itemValues = new ComBoxItemVO[] { new ComBoxItemVO("1.0", null, "1.0") };
				newversionBox.pushItems(itemValues);//һ��Ҫ�����������setItemVOs()����ˢ��
			} else {
				cardpanel.setEditable("newversionno", true);
				if (!str_oldversion.contains(".")) {
					str_oldversion = str_oldversion + ".0";
				}
				int bigversion = Integer.parseInt(str_oldversion.substring(0, str_oldversion.indexOf(".")));
				int smallversion = Integer.parseInt(str_oldversion.substring(str_oldversion.indexOf(".") + 1, str_oldversion.indexOf(".") + 2)); /*���ݿ������ñ�����λС��ʱ��
													 oracle �б���1.1 ��mysql�б���1.10��������ֻ��Ҫ��ȡһλ������ʹ��mysql���ݿ⣬������ΰ汾ʱ1.10����ȡ����10��
													 ����һ�д�������������ʱ������ĸ���Ϊ10-10=0���ʵ���������Ϊ�ա����/2012-06-12��
													 */
				itemValues = new ComBoxItemVO[10 - smallversion];
				for (int i = 0; i < itemValues.length - 1; i++) {
					String version = bigversion + "." + (smallversion + i + 1);
					itemValues[i] = new ComBoxItemVO(version, null, version); //
				}
				itemValues[10 - smallversion - 1] = new ComBoxItemVO((bigversion + 1) + ".0", null, (bigversion + 1) + ".0");
				newversionBox.pushItems(itemValues);
			}
			newversionBox.getComBox().removeItemAt(0);//����һ���ո�ȥ��
		} else if ("4".equals(filestate)) {//��ֹ������, Ҫ�����°汾�ſؼ�
			cardpanel.setVisiable("newversionno", false);
		}
	}
}
