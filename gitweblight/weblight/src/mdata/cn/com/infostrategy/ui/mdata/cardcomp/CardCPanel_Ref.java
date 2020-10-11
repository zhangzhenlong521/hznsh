/**************************************************************************
 * $RCSfile: CardCPanel_Ref.java,v $  $Revision: 1.13 $  $Date: 2013/02/28 06:14:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

/**
 * 
 * @author xch
 * 
 */
public class CardCPanel_Ref extends UIRefPanel {
	private static final long serialVersionUID = 1L;

	private Vector v_Listeners = new Vector(); //
	protected int li_label_width = 120; // 标签宽度说明
	protected int li_width_all = 0; // 只有卡片中有总宽度中的概念,列表中则是BorderLayout
	private int li_cardheight = 20; //
	private boolean isRefCanEdit = false;

	/**
	 * 直接手工创建
	 * 
	 * @param _key
	 * @param _name
	 * @param _refdesc
	 *            参照定义
	 * @param _type
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public CardCPanel_Ref(String _key, String _name, String _refdesc, String _type, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_key, _name, _refdesc, _type, _initRefItemVO, _billPanel); // //
		initialize(); //
	}

	public CardCPanel_Ref(String _key, String _name, String _refdesc, String _type, int _labelwidth, int _textfieldwidth, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_key, _name, _refdesc, _type, _initRefItemVO, _billPanel); // //
		li_label_width = _labelwidth;
		li_textfield_width = _textfieldwidth;
		initialize(); //
	}

	/**
	 * 由元原模板创建
	 * 
	 * @param _templetVO
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public CardCPanel_Ref(Pub_Templet_1_ItemVO _templetVO, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_templetVO, _initRefItemVO, _billPanel); // //
		this.li_cardheight = _templetVO.getCardHeight().intValue(); //
		initialize();
	}

	/**
	 * 初始化页面布局,卡片与列静听的参照面板其实主要就是这儿不一样!!!! 卡片中的布局是有两个东东,一个是label标签,一个是文本框,而列表中则没有Label标签!
	 */
	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setOpaque(false); //

		if (templetItemVO != null) {
			label = createLabel(templetItemVO); // 采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setHorizontalAlignment(SwingConstants.RIGHT); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); // 设置宽度
		}

		if (templetItemVO != null && templetItemVO.getIsRefCanEdit() != null) {
			if (templetItemVO.getIsRefCanEdit()) {
				isRefCanEdit = true;
			}
		}

		// 图片选择框不可编辑 【杨科/2012-12-19】
		if (this.type.equals(WLTConstants.COMP_PICTURE) || this.type.equals(WLTConstants.COMP_DATE) || this.type.equals(WLTConstants.COMP_DATETIME)
				|| (((this.type.indexOf(WLTConstants.COMP_REFPANEL) > 0) || this.type.equals(WLTConstants.COMP_REFPANEL)) && !isRefCanEdit)) {
			textField.setEditable(false);
		}
		// 处理颜色
		if (this.type.equals(WLTConstants.COMP_OFFICE) || this.type.equals(WLTConstants.COMP_BIGAREA) || this.type.equals(WLTConstants.COMP_EXCEL)) {
			textField.setForeground(Color.BLUE); // 蓝色
		} else {
			textField.setForeground(LookAndFeel.inputforecolor_enable); // 有效时的前景色
		}
		textField.setBackground(LookAndFeel.inputbgcolor_enable); // 有效时的背景色

		// 处理边框
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { // 自定义UI中已经去掉border。
			if (templetItemVO != null) {
				if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("BORDER")) {
					textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				} else if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("LINE")) {
					textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				}
			} else {
				textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); // LookAndFeel.compBorderLineColor
			}
		}
		if (textField instanceof JTextArea) {
			textField.putClientProperty("JTextArea.Insets", new Insets(2, 3, 2, 0));
			textField.setForeground(LookAndFeel.inputforecolor_enable); // 有效时的前景色
			textField.setBackground(LookAndFeel.inputbgcolor_enable); // 有效时的背景色
		}
		this.add(label); //
		JScrollPane jsp = new JScrollPane();
		if (textComponentWrap) {
			jsp.setPreferredSize(new Dimension(li_textfield_width, li_cardheight)); //
			jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jsp.getViewport().add(textField);
			this.add(jsp); //
		} else {
			textField.setPreferredSize(new Dimension(li_textfield_width, li_cardheight)); //
			this.add(textField); //
		}

		this.add(btn_ref); //

		// JPanel tempPanel_flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		// tempPanel_flow.add(textField); //
		// tempPanel_flow.add(btn_ref); //
		// JPanel tempPanel_border = new JPanel(new BorderLayout());
		// tempPanel_border.add(tempPanel_flow, BorderLayout.NORTH); //
		// tempPanel_border.setPreferredSize(new Dimension(li_textfield_width+20, li_cardheight)); //
		// this.add(tempPanel_border); //

		int li_hyperlinkwidth = 0;
		if (hyperlinks != null) { // 如果有超连接
			for (int i = 0; i < hyperlinks.length; i++) {
				li_hyperlinkwidth = li_hyperlinkwidth + (int) hyperlinks[i].getPreferredSize().getWidth(); //
				this.add(hyperlinks[i]); //
			}
		}
		btn_ref.setPreferredSize(new Dimension(20, li_cardheight));
		if (textComponentWrap) {
			li_width_all = (int) (label.getPreferredSize().getWidth() + jsp.getPreferredSize().getWidth() + btn_ref.getPreferredSize().getWidth() + li_hyperlinkwidth); // 总宽度
		} else {
			li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth() + btn_ref.getPreferredSize().getWidth() + li_hyperlinkwidth); // 总宽度
		}

		if (templetItemVO != null) {
			if ((WLTConstants.COMP_OFFICE).equals(templetItemVO.getItemtype())) {
				if (importB != null) {
					this.add(importB); //
					li_width_all = (int) (li_width_all + importB.getPreferredSize().getWidth());
				}
			}
		}
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
	}

	public void addBillCardEditListener(BillCardEditListener _listener) {
		v_Listeners.add(_listener);
	}

	@Override
	public void setItemEditable(boolean _bo) {
		super.setItemEditable(_bo);
		if (this.type.equals(WLTConstants.COMP_EXCEL)) { // this.type.equals(WLTConstants.COMP_OFFICE) ||
		} else {
			if (_bo) {
				textField.setForeground(LookAndFeel.inputforecolor_enable); // 有效时的前景色
				textField.setBackground(LookAndFeel.inputbgcolor_enable); // 有效时的背景色
			} else {
				textField.setForeground(LookAndFeel.inputforecolor_disable); // 无效时的前景色
				textField.setBackground(LookAndFeel.inputbgcolor_disable); // 无效时的背景色
			}
		}

	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		for (int i = 0; i < v_Listeners.size(); i++) {
			BillCardEditListener listener = (BillCardEditListener) v_Listeners.get(i);
			listener.onBillCardValueChanged(_evt); //
		}
	}

	public int getAllWidth() {
		return li_width_all;
	}

}
