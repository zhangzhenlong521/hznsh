package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;

public class ReloginDialog extends BillDialog implements BillCardEditListener {

	private LoginAppletLoader loader = null;
	private BillCardPanel cardPanel = null;

	public ReloginDialog(Container _parent, LoginAppletLoader _loader) {
		super(_parent, "重新登录", 350, 253);
		this.loader = _loader; //
		this.setResizable(false); //
		initialize();
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		cardPanel = new BillCardPanel(new LoginPanelVO()); //
		cardPanel.setScrollable(false); //
		cardPanel.setTitleable(false);
		cardPanel.setEditable(true); //
		cardPanel.setRealValueAt("ISADMIN", "N");
		cardPanel.setEditable("ADMINPWD", false); //
		if (ClientEnvironment.getInstance().isMultiLanguage()) {
			cardPanel.setVisiable("LANGUAGE", true);
		} else {
			cardPanel.setVisiable("LANGUAGE", false);
		}
		boolean flag = new TBUtil().getSysOptionBooleanValue("注销时是否显示输入管理密码", true);
		if (!flag) {
			cardPanel.setVisiable("ADMINPWD", false);
			cardPanel.setVisiable("ISADMIN", false);
		}

		((CardCPanel_TextField) cardPanel.getCompentByKey("PWD")).getTextField().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					onConfirm();
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyTyped(KeyEvent e) {

			}
		});
		((CardCPanel_TextField) cardPanel.getCompentByKey("ADMINPWD")).getTextField().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					onConfirm();
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyTyped(KeyEvent e) {

			}
		});

		cardPanel.addBillCardEditListener(this);
		((CardCPanel_ComboBox) cardPanel.getCompentByKey("LANGUAGE")).getComBox().setSelectedIndex(1); //
		this.getContentPane().add(cardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());  ////
		JButton btn_1 = new WLTButton(UIUtil.getLanguage("确定")); //
		JButton btn_2 = new WLTButton(UIUtil.getLanguage("取消")); //
		btn_1.setPreferredSize(new Dimension(75, 20));
		btn_2.setPreferredSize(new Dimension(75, 20));
		btn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		btn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_1);
		panel.add(btn_2);
		return panel;
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		BillCardPanel card = (BillCardPanel) _evt.getSource();
		if (_evt.getItemKey().equals("ISADMIN")) {
			StringItemVO obj = (StringItemVO) _evt.getNewObject(); //
			if (obj != null && obj.getStringValue().equals("Y")) {
				card.getCompentByKey("ADMINPWD").setItemEditable(true);
				((CardCPanel_TextField) cardPanel.getCompentByKey("ADMINPWD")).getTextField().requestFocus(); //
			} else {
				card.getCompentByKey("ADMINPWD").setItemEditable(false);
			}
		}
	}

	private void onConfirm() {
		String user_code = cardPanel.getRealValueAt("USERCODE"); //
		String pwd = cardPanel.getRealValueAt("PWD"); //

		String adminpwd = null;
		ComBoxItemVO boxItemVO = (ComBoxItemVO) cardPanel.getCompentObjectValue("LANGUAGE");
		String str_language = WLTConstants.SIMPLECHINESE;
		if (boxItemVO != null) {
			str_language = boxItemVO.getId();
		}

		boolean bo_isAdmin = false;
		if (1 == 1) {
			String isAdmin = cardPanel.getRealValueAt("ISADMIN"); //
			if (isAdmin != null && isAdmin.equals("Y")) {
				adminpwd = cardPanel.getRealValueAt("ADMINPWD"); //
				bo_isAdmin = true;
				if(adminpwd == null || adminpwd.equals("")){ //管理密码必须输入[郝明2012-12-05]
					MessageBox.show(this, "管理密码不能为空!"); //
					return;
				}
			} else {
				bo_isAdmin = false;
			}
		}

		this.dispose();
		this.loader.dealLogin(true, user_code, pwd, adminpwd, ClientEnvironment.chooseISys, bo_isAdmin, false, str_language, null); //
	}

	private void onCancel() {
		this.dispose(); //
	}

	class LoginPanelVO extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "RELOGIN"); //模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "用户信息"); //模板名称
			vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); //主键名
			vo.setAttributeValue("pksequencename", "S_PUB_MENU"); //序列名
			vo.setAttributeValue("savedtablename", null); //保存数据的表名
			vo.setAttributeValue("cardwidth", "200"); //保存数据的表名
			vo.setAttributeValue("listcustpanel", null); //列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "USERCODE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "用户名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "UserCode"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PWD"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "密码"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Password"); //显示名称
			itemVO.setAttributeValue("itemtype", "密码框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ADMINPWD"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "管理密码"); //显示名称
			itemVO.setAttributeValue("itemname_e", "SupportPassword"); //显示名称
			itemVO.setAttributeValue("itemtype", "密码框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISADMIN"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否管理身份"); //显示名称
			itemVO.setAttributeValue("itemname_e", "IsSupport"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LANGUAGE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "Language"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Language"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "select 'SIMPLECHINESE' id,'SIMPLECHINESE' code,'简体中文' name from wltdual union all select 'ENGLISH' id,'ENGLISH' code,'English' name from wltdual union all select 'TRADITIONALCHINESE' id,'TRADITIONALCHINESE' code,'繁体中文' name from wltdual"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
