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
		cardpanel.setGroupVisiable("流程处理", true);
		CardCPanel_CheckBox approveCheckBox = (CardCPanel_CheckBox) cardpanel.getCompentByKey("isapprove");
		approveCheckBox.getCheckBox().setSelected(true);//是否同意 勾选框默认选中
		BillVO billVO = cardpanel.getBillVO();
		String filestate = billVO.getStringValue("filestate");
		if ("2".equals(filestate)) {//文件状态：1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
			String str_oldversion = billVO.getStringValue("versionno"); //旧的版本号
			CardCPanel_ComboBox newversionBox = (CardCPanel_ComboBox) cardpanel.getCompentByKey("newversionno");
			ComBoxItemVO[] itemValues = null;
			if (str_oldversion == null || "".equals(str_oldversion)) { //如果没有版本号，第一次发布默认设为“1.0”
				itemValues = new ComBoxItemVO[] { new ComBoxItemVO("1.0", null, "1.0") };
				newversionBox.pushItems(itemValues);//一定要用这个方法，setItemVOs()不能刷新
			} else {
				cardpanel.setEditable("newversionno", true);
				if (!str_oldversion.contains(".")) {
					str_oldversion = str_oldversion + ".0";
				}
				int bigversion = Integer.parseInt(str_oldversion.substring(0, str_oldversion.indexOf(".")));
				int smallversion = Integer.parseInt(str_oldversion.substring(str_oldversion.indexOf(".") + 1, str_oldversion.indexOf(".") + 2)); /*数据库中设置保留两位小数时，
													 oracle 中保存1.1 但mysql中保存1.10，故这里只需要截取一位，否则使用mysql数据库，如果本次版本时1.10，截取后是10，
													 到下一行创建下拉框数组时，数组的个数为10-10=0，故导致下拉框为空【李春娟/2012-06-12】
													 */
				itemValues = new ComBoxItemVO[10 - smallversion];
				for (int i = 0; i < itemValues.length - 1; i++) {
					String version = bigversion + "." + (smallversion + i + 1);
					itemValues[i] = new ComBoxItemVO(version, null, version); //
				}
				itemValues[10 - smallversion - 1] = new ComBoxItemVO((bigversion + 1) + ".0", null, (bigversion + 1) + ".0");
				newversionBox.pushItems(itemValues);
			}
			newversionBox.getComBox().removeItemAt(0);//将第一条空格去掉
		} else if ("4".equals(filestate)) {//废止申请中, 要隐藏新版本号控件
			cardpanel.setVisiable("newversionno", false);
		}
	}
}
