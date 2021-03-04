package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTStylePadPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * 多风格编辑窗口,即支持粗体斜体等效果的编辑窗口!!!
 * @author xch
 *
 */
public class StylePadEditDialog extends BillDialog implements ActionListener {

	private String str_inittext = null; //初始化的值
	private String str_initbatchid = null; //初始化的值
	private String[] str_updatesqls = null; //回写主页面的SQL!!!

	private WLTStylePadPanel stylePadPanel = null; //多风格编辑框!!!
	private WLTButton btn_confirm, btn_cancel; //

	private String returnBatchId = null; //
	private String returnText = null; //

	public StylePadEditDialog(Container _parent, String _title, String _text, String _padid, String[] _sqls) {
		super(_parent, _title, 650, 650);
		this.str_inittext = _text; //
		this.str_initbatchid = _padid; //
		this.str_updatesqls = _sqls; //
		initialize(); //
	}

	/**
	 * 构造页面!!!
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		stylePadPanel = new WLTStylePadPanel(); //
		if (str_initbatchid == null) {
			stylePadPanel.setText(str_inittext); //
		} else {
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_stylepaddoc where batchid='" + str_initbatchid + "' order by seq"); //
				if (hvs == null || hvs.length == 0) {
					stylePadPanel.setText(str_inittext); //如果没找到,则直接使用原来的!即可能迁移时漏掉了!
				} else {
					StringBuilder sb_64code = new StringBuilder(); //
					String str_item = null; //
					for (int i = 0; i < hvs.length; i++) { //遍历!
						for (int j = 0; j < 10; j++) {
							str_item = hvs[i].getStringValue("doc" + j); //
							if (str_item != null && !str_item.equals("")) { //如果有值
								sb_64code.append(str_item.trim()); //拼接起来!!
							} else { //如果值为空
								break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
							}
						}
					}
					stylePadPanel.load64CodeToDocument(sb_64code.toString()); //加载数据!!!!
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		JScrollPane scroll = new JScrollPane(stylePadPanel); //
		this.add(scroll, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel; //
	}
	
	public void setEditble(boolean bo) {
		stylePadPanel.setEditble(bo);
		btn_confirm.setEnabled(bo);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}
	}

	/**
	 * 点击确定,要往平台表中插入数据!!!然后返回一个字符串!!! 
	 * 返回的字符串是980个字,加上特殊符号!!!!
	 */
	private void onConfirm() {
		try {
			String str_text = stylePadPanel.getDocumentText(); //文本
			String str_64code = stylePadPanel.getDocument64Code(); //64位码!!!
			if (str_text == null || str_text.trim().equals("")) { //如果文本实际内容为空,则认为64位编码也为空!!
				str_64code = null;
				str_text = ""; //
			} else {
				str_text = new TBUtil().subStrByGBLength(str_text, 1950, true); //只取前1950个!
			}
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			String str_batchid = service.saveStylePadDocument(this.str_initbatchid, str_64code, str_text, str_updatesqls); //实际插入数据库!!
			returnBatchId = str_batchid; //
			returnText = str_text; //
			this.setCloseType(1); //
			this.dispose(); //关闭!!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onCancel() {
		this.setCloseType(2); //
		this.dispose(); //
	}

	public String getReturnBatchId() {
		return returnBatchId;
	}

	public String getReturnText() {
		return returnText;
	}
}
