package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

/**
 * 富文本框,即支持粗体,斜体,下划线等效果的控件!!!
 * @author xch
 *
 */
public class CardCPanel_StylePadArea extends AbstractWLTCompentPanel implements ActionListener, MouseListener {

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String itemKey = null; //
	private String itemName = null; //
	private BillCardPanel billCardPanel = null; //

	private int labelwidth = 120; //
	private int textareawidth = 400; //
	private int textareaheight = 150; //

	private JLabel label = null;
	private JTextArea textArea = null; //

	private String str_stylepadid = null; //存储在平台表中的主键!!
	private WLTButton btn_btnbar; // btn_edit; //按钮栏,编辑!!!
	private boolean editble = true;

	private CardCPanel_StylePadArea() {
	}

	/**
	 * 构造方法!!!
	 * @param _templet_1_ItemVO
	 * @param _billCardPanel
	 */
	public CardCPanel_StylePadArea(Pub_Templet_1_ItemVO _templet_1_ItemVO, BillCardPanel _billCardPanel) {
		this.templetItemVO = _templet_1_ItemVO; //
		this.billCardPanel = _billCardPanel; //

		this.itemKey = _templet_1_ItemVO.getItemkey(); //
		this.itemName = _templet_1_ItemVO.getItemname(); //

		if (templetItemVO.getLabelwidth() != null) {
			labelwidth = templetItemVO.getLabelwidth().intValue(); //
		}
		if (templetItemVO.getCardwidth() != null) {
			textareawidth = templetItemVO.getCardwidth().intValue(); //
		}
		if (templetItemVO.getCardHeight() != null) {
			textareaheight = templetItemVO.getCardHeight().intValue(); //
		}

		this.setLayout(null); //
		this.setBackground(LookAndFeel.cardbgcolor); //
		label = createLabel(_templet_1_ItemVO); //
		label.setBounds(0, 0, labelwidth, textareaheight); //

		btn_btnbar = new WLTButton(UIUtil.getImage("stylepadbtns.gif")); //
		btn_btnbar.setToolTipText("点击进入编辑模式"); //
		btn_btnbar.setBounds(labelwidth, 0, 257, 18); //
		btn_btnbar.addActionListener(this); //

		textArea = new JTextArea(10, 10); //

		textArea.setLineWrap(true); //
		textArea.setToolTipText("双击进入编辑模式"); //
		textArea.setEditable(false); //永远不可编辑
		JScrollPane scroll = new JScrollPane(textArea); //
		scroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(labelwidth, 22, textareawidth, textareaheight); //
		textArea.addMouseListener(this); //

		this.add(label); //
		this.add(btn_btnbar); //
		//this.add(btn_edit); //
		this.add(scroll); //

		this.setPreferredSize(new Dimension(labelwidth + textareawidth, textareaheight + 22)); //

	}

	@Override
	public String getItemKey() {
		return itemKey;
	}

	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public JLabel getLabel() {
		return label;
	}

	@Override
	public int getAllWidth() {
		return labelwidth + textareawidth;
	}

	@Override
	public Object getObject() {
		String str_text = textArea.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			return null; //
		}
		if (str_stylepadid != null) {
			str_text = str_text + "#@$" + str_stylepadid + "$@#"; //如果有后辍,则加上!
		}
		return new StringItemVO(str_text);
	}

	@Override
	public String getValue() {
		Object obj = getObject(); //
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	@Override
	public void setObject(Object _obj) {
		if (_obj == null) {
			textArea.setText(""); //
			this.str_stylepadid = null; //
			return; //
		}
		StringItemVO sto = (StringItemVO) _obj; //
		String str_realtext = sto.getStringValue(); //
		int li_pos_1 = str_realtext.lastIndexOf("#@$"); //
		int li_pos_2 = str_realtext.lastIndexOf("$@#"); // 
		if (str_realtext.endsWith("$@#") && li_pos_1 > 0 && li_pos_2 > 0) { //如果是以【$@#】结尾的! #@$12586$@#
			textArea.setText(str_realtext.substring(0, li_pos_1)); ////
			this.str_stylepadid = str_realtext.substring(li_pos_1 + 3, li_pos_2); ////主键!!
		} else {
			textArea.setText(str_realtext); //
			this.str_stylepadid = null; //
		}
		textArea.setSelectionStart(0); //
		textArea.setSelectionEnd(0); //
	}

	@Override
	public void setValue(String _value) {
		if (_value == null) {
			setObject(null); ////
		} else {
			setObject(new StringItemVO(_value)); ////
		}
	}

	@Override
	public void reset() {
		textArea.setText(""); //
		this.str_stylepadid = null; //
	}

	@Override
	public void setItemEditable(boolean _bo) {
		if (_bo) {
			textArea.setBackground(Color.WHITE);
			editble = _bo;
		} else {
			textArea.setBackground(new Color(240, 240, 240));
			editble = false;
		}
	}

	@Override
	public boolean isItemEditable() {
		return btn_btnbar.isEnabled();
	}

	@Override
	public void setItemVisiable(boolean _bo) {
		this.setItemVisiable(_bo); //
	}

	@Override
	public void focus() {
		textArea.requestFocus(); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_btnbar) {
			onEdit(); //
		}
	}

	/**
	 * 进入编辑模式!
	 */
	private void onEdit() {
		String[] str_updatesqls = null; //
		String str_tableName = templetItemVO.getPub_Templet_1VO().getSavedtablename(); //
		String str_itemkey = templetItemVO.getItemkey(); //
		boolean isNeedSave = templetItemVO.isNeedSave(); //
		boolean isCanSave = templetItemVO.isCanSave(); //
		String str_pkname = templetItemVO.getPub_Templet_1VO().getPkname();
		String str_pkvalue = billCardPanel.getRealValueAt(str_pkname); //
		if (str_tableName != null && str_itemkey != null && str_pkname != null && str_pkvalue != null && isNeedSave && isCanSave) {
			str_updatesqls = new String[] { str_tableName, str_itemkey, str_pkname, str_pkvalue }; //
		}

		StylePadEditDialog dialog = new StylePadEditDialog(this, "编辑[" + this.getItemName() + "]", textArea.getText(), str_stylepadid, str_updatesqls); //
		dialog.setEditble(editble);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_batchid = dialog.getReturnBatchId(); //返回的批号!!
			String str_text = dialog.getReturnText(); //返回的文本框的值!!
			str_stylepadid = str_batchid; //设置值!!
			textArea.setText(str_text); //设置值
		}
		textArea.setSelectionStart(0); //
		textArea.setSelectionEnd(0); //
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			onEdit(); //
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {

	}

}
