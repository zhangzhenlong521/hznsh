/**************************************************************************
 * $RCSfile: CardCPanel_TextArea.java,v $  $Revision: 1.12 $  $Date: 2013/02/28 06:14:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.mdata.WLTHyperLinkLabel;

/**
 * 卡片中的多行文本框
 * @author xch
 *
 */
public class CardCPanel_TextArea extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1523290372757996226L;

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private JLabel label = null;

	private JTextArea textArea = null;
	private JScrollPane jsp = null;
	private String key = null;
	private String name = null;

	protected String hyperlinkdesc = null; //超链接定义
	private StringItemVO initItemVO = null; //初始数据
	private BillPanel billPanel = null;

	private int row = 1; // 7改为3, 这样默认不出现滚动框 Gwang 2013/4/9   hm/2013-5-30改为1
	private int col = 10;
	private int li_label_width = 120;
	private int li_textarea_width = 150; //
	private int li_height = 75; // 高80,即可视4行数据

	private int li_width_all = 0;

	public CardCPanel_TextArea(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.li_height = templetItemVO.getCardHeight().intValue(); //高度  
		this.li_textarea_width = templetItemVO.getCardwidth().intValue(); // 设置宽度
		initialize();

	}

	public CardCPanel_TextArea(Pub_Templet_1_ItemVO _templetVO, StringItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.initItemVO = _initItemVO; //
		this.billPanel = _billPanel; //
		this.li_height = templetItemVO.getCardHeight().intValue(); //高度  
		this.li_textarea_width = templetItemVO.getCardwidth().intValue(); // 设置宽度
		initialize();

	}

	public CardCPanel_TextArea(String key, String name) {
		super();
		this.key = key;
		this.name = name;
		initialize();
	}

	public CardCPanel_TextArea(String key, String name, int r, int c) {
		super();
		this.key = key;
		this.name = name;
		this.row = r;
		this.col = c;
		initialize();
	}

	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.cardbgcolor); //

		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setPreferredSize(new Dimension(li_label_width, 20)); //设置宽度
		}
		CommUCDefineVO ucvo = templetItemVO.getUCDfVO();
		textArea = new WLTTextArea(row, col); //
		textArea.setLineWrap(true);
		textArea.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
		textArea.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色
		if (ucvo != null && "自定义校验".equals(ucvo.getConfValue("类型"))) {
			textArea.setDocument(new NumberFormatdocument(ucvo)); //多行文本也需要校验 by haoming 2016-05-09
		}
		//		area.addMouseListener(new MouseAdapter() {
		//			@Override
		//			public void mouseClicked(MouseEvent e) {
		//				if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
		//					onMaxScreenView(); //最大化
		//				}
		//			}
		//		}); //
		//addComponentUndAndRedoFunc(area);
		jsp = new JScrollPane();
		//		jsp.setBorder(BorderFactory.createEmptyBorder());
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			jsp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		jsp.setPreferredSize(new Dimension(li_textarea_width, li_height));
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.getViewport().add(textArea);

		label.setPreferredSize(new Dimension((int) label.getPreferredSize().getWidth(), li_height)); //TextArea与其他不一样,它的高度要重设一下!
		this.add(label);
		this.add(jsp);

		//加入超链接!!!,目前文本框,只有在卡片中才有超链接出现,列表中不出现!!
		int li_hyperlinkwidth = 0;
		if (hyperlinkdesc != null) {
			WLTHyperLinkLabel[] linkLabels = this.createHyperLinkLabel(this.key, this.name, hyperlinkdesc, this.billPanel); //
			if (linkLabels != null) {
				if (linkLabels != null) { //如果有超连接
					for (int i = 0; i < linkLabels.length; i++) {
						li_hyperlinkwidth = li_hyperlinkwidth + (int) linkLabels[i].getPreferredSize().getWidth(); //
						linkLabels[i].setPreferredSize(new Dimension((int) linkLabels[i].getPreferredSize().getWidth(), li_height));
						linkLabels[i].setVerticalAlignment(SwingConstants.BOTTOM); //
						this.add(linkLabels[i]); //
					}
				}
			}
		}

		li_width_all = (int) (label.getPreferredSize().getWidth() + jsp.getPreferredSize().getWidth() + li_hyperlinkwidth); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_height)); //
	}

	/**
	 *满屏显示
	 */
	//	protected void onMaxScreenView() {
	//		Dimension dimenSion = Toolkit.getDefaultToolkit().getScreenSize(); //
	//		if (area.isEditable()) {
	//			if (SplashWindow.window != null) {
	//				SplashWindow.window.closeWindow(); //
	//			}
	//			TextAreaDialog dialog = new TextAreaDialog(this, this.name, area.getText(), 1000, 618, Color.WHITE); //
	//			JTextArea jta_context = dialog.getJta_context();
	//			jta_context.setOpaque(true);
	//			jta_context.setEditable(true);
	//			jta_context.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
	//			jta_context.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色
	//			jta_context.setText(jta_context.getText());
	//			dialog.setVisible(true); //
	//			if (dialog.getCloseType() == 1) {
	//				area.setText(jta_context.getText());
	//			}
	//		} else {
	//			if (SplashWindow.window != null) {
	//				SplashWindow.window.closeWindow(); //
	//			}
	//			TextAreaDialog dialog = new TextAreaDialog(this, this.name, area.getText(), 1000, 618, new Color(240, 240, 240)); //
	//			JTextArea jta_context = dialog.getJta_context();
	//			jta_context.setOpaque(true);
	//			jta_context.setEditable(false);
	//			jta_context.setForeground(LookAndFeel.inputforecolor_disable); //无效时的前景色
	//			jta_context.setBackground(LookAndFeel.inputbgcolor_disable); //无效时的背景色
	//			jta_context.setText(jta_context.getText());
	//			dialog.setVisible(true); //
	//		}
	//	}
	public String getDataType() {
		return null;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getValue() {
		return textArea.getText();
	}

	public void setText(String str) {
		this.textArea.setText(str);
	}

	public void setValue(String _value) {
		textArea.setText(_value);
		textArea.select(0, 0); //
	}

	public void reset() {
		textArea.setText(""); //
	}

	public void setItemEditable(boolean _bo) {
		textArea.setEditable(_bo);
		if (_bo) {
			textArea.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
			textArea.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色
		} else {
			textArea.setForeground(LookAndFeel.inputforecolor_disable); //无效时的前景色
			textArea.setBackground(LookAndFeel.inputbgcolor_disable); //无效时的背景色
		}
	}

	@Override
	public boolean isItemEditable() {
		return textArea.isEditable();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		return new StringItemVO(getValue());
	}

	public void setObject(Object _obj) {
		if (_obj != null) {
			if (_obj instanceof RefItemVO) {
				setValue(((RefItemVO) _obj).getId()); //
			} else if (_obj instanceof StringItemVO) {
				setValue(((StringItemVO) _obj).getStringValue()); //
			} else {
				setValue(_obj.toString()); //
			}
		} else {
			setValue(null); //
		}
	}

	public JLabel getLabel() {
		return label;
	}

	public void focus() {
		textArea.requestFocus();
		textArea.requestFocusInWindow();
	}

	public int getAllWidth() {
		return this.li_width_all;
	}

	public JTextArea getArea() {
		return textArea;
	}

	@Override
	/**
	 * 重构,以文本框为准! 在stopediting时需要
	 */
	public boolean isFocusOwner() {
		return textArea.isFocusOwner(); //
	}

}
/**************************************************************************
 * $RCSfile: CardCPanel_TextArea.java,v $  $Revision: 1.12 $  $Date: 2013/02/28 06:14:48 $
 *
 * $Log: CardCPanel_TextArea.java,v $
 * Revision 1.12  2013/02/28 06:14:48  wanggang
 * *** empty log message ***
 *
 * Revision 1.10  2012/10/08 02:22:48  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.8  2012/05/04 05:50:19  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/04/18 03:14:48  My Dream
 *
 * Committed on the Free edition of March Hare Software CVSNT Server.
 * Upgrade to CVS Suite for more features and support:
 * http://march-hare.com/cvsnt/
 *
 * Revision 1.7  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.5  2011/04/20 10:21:37  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2011/01/27 09:55:53  xch123
 * 兴业春节前回来
 *
 * Revision 1.3  2010/12/28 10:30:11  xch123
 * 12月28日提交
 *
 * Revision 1.2  2010/08/20 06:45:27  xuchanghua
 * *** empty log message ***
 *
 *
**************************************************************************/
