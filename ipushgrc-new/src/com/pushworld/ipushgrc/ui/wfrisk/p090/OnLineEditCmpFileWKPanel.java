package com.pushworld.ipushgrc.ui.wfrisk.p090;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 编辑中的文件，一方面用于统计查看，另一方面可以解锁由非正常操作导致未处于编辑状态但仍处于锁定状态的文件
 * @author lcj
 *
 */
public class OnLineEditCmpFileWKPanel extends AbstractWorkPanel implements ActionListener {
	private IPushGRCServiceIfc service;//产品服务接口
	private BillListPanel billList_cmpfile; // 流程文件列表!
	WLTButton btn_unlock;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		try {
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		billList_cmpfile = new BillListPanel(new TMO_CMPFILE());
		btn_unlock = new WLTButton("取消编辑");
		btn_unlock.addActionListener(this);
		billList_cmpfile.addBillListButton(btn_unlock);
		billList_cmpfile.repaintBillListButton();
		billList_cmpfile.getQuickQueryPanel().addBillQuickActionListener(this);		
		this.add(billList_cmpfile);
		//页面打开时自动查询
		billList_cmpfile.getQuickQueryPanel().getQuickQueryButton().doClick();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_unlock) {
			BillVO billVO = billList_cmpfile.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			try {
				service.unlockCmpFileById(billVO.getStringValue("cmpfileid"));
				MessageBox.show(this, "取消编辑成功!");
				billList_cmpfile.removeSelectedRow();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		} else {
			try {
				billList_cmpfile.removeAllRows();
				String cmpfiles = service.getAllLockedCmpFiles();
				if (cmpfiles == null || "".equals(cmpfiles.trim())) {
					return;
				}
				String[] str_cmpfiles = new TBUtil().split(cmpfiles, ";");
				for (int i = 0; i < str_cmpfiles.length; i++) {
					billList_cmpfile.addEmptyRow();
					String[] str_cmpfile = str_cmpfiles[i].split("&");
					billList_cmpfile.setRealValueAt(str_cmpfile[0], i, "cmpfileid");
					billList_cmpfile.setRealValueAt(str_cmpfile[1], i, "cmpfilename");
					billList_cmpfile.setRealValueAt(str_cmpfile[2], i, "username");
					billList_cmpfile.setRealValueAt(str_cmpfile[3], i, "time");
				}
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	class TMO_CMPFILE extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "cmp_cmpfile"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "编辑中的文件"); // 模板名称
			vo.setAttributeValue("Isshowlistquickquery", "N"); //列表是否显示快速查询
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cmpfileid"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "cmpfileid"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cmpfilename"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "文件名称"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "itemkey"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "username"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "用户名"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "itemname"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "time"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "开始编辑的时间"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "time"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}
}
