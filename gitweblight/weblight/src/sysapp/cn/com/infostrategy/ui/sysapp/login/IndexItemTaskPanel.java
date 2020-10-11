package cn.com.infostrategy.ui.sysapp.login;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTHrefLabel;
import cn.com.infostrategy.ui.common.WLTHtmlButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;

/**
 * 首页某一个具体任务的面板!!!
 * 宽400*205
 * @author xch
 *
 */
public class IndexItemTaskPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private DeskTopPanel deskTopPanel = null; //
	private DeskTopNewGroupVO taskVO = null; //
	private WLTHrefLabel[] labels = null; //
	private JButton btn_refresh, btn_more; //
	private int allWidth = 360; //
	private boolean isMore = false; //
	private boolean isRefresh = true; //
	private TBUtil tbUtil = new TBUtil(); //

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO) {
		this(_deskTopPanel, _taskVO, 360); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		initialize(null); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(null); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore, boolean _isLazyLoad) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(new Boolean(_isLazyLoad)); //
	}

	/**
	 * 构造页面!!!
	 */
	private void initialize(Boolean _isLazyLoad) {
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
		boolean isLazyLoad = false; //
		if (_isLazyLoad == null) { //如果没有明确指定,则使用参数定义!!
			isLazyLoad = "Y".equalsIgnoreCase(taskVO.getDefineVO().getIsLazyLoad()); //是否懒装入??
		} else { //如果明确指定,则使用明确指定的!!
			isLazyLoad = _isLazyLoad.booleanValue(); //
		}
		if (isLazyLoad) { //如果是懒装入,则另开线程!
			this.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //透明
			this.add(new JLabel("正在加载数据....", UIUtil.getImage("office_006.gif"), SwingConstants.CENTER), BorderLayout.CENTER); //先显示正在加载数据
			this.add(getTitlePanel(taskVO.getDefineVO().getTitle(), taskVO.getDefineVO().getImgicon(), taskVO.getDefineVO().getTitlecolor(), false), BorderLayout.NORTH); //
			new Timer().schedule(new TimerTask() { //另开一个线程,去懒加载数据!!
						@Override
						public void run() { //新开一个线程加载页面!
							lazyLoad(); //懒装入!!
						}
					}, 0); //过多长时间后开始执行!! 
		} else {
			loadRealPanel(); //
		}
	}

	/**
	 * 懒装入逻辑!即先要取得数据,然后画页面!!
	 */
	private void lazyLoad() {
		try {
			String str_clasName = taskVO.getDefineVO().getDatabuildername(); //数据加载类名!

			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			HashVO[] hvs = service.getDeskTopNewGroupVOData(str_clasName, ClientEnvironment.getCurrLoginUserVO().getCode(), taskVO.getDefineVO()); //这个可能耗时!!

			//追加数据有效期限判断【杨科/2012-07-30】
			if ((taskVO.getDefineVO().getDatatype() == null || taskVO.getDefineVO().getDatatype().equals("文字")) && (hvs != null)) {
				String expfield = (String) taskVO.getDefineVO().getOtherconfig().get("有效期限匹配字段");
				int expdate = 0;
				ArrayList al_hvs = new ArrayList();

				try {
					expdate = Integer.parseInt((String) taskVO.getDefineVO().getOtherconfig().get("有效期限"));
				} catch (RuntimeException e) {
					expdate = 0;
				}

				if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
					CommonDate cd = new CommonDate(new Date());
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs[i].getDateValue(expfield))) < expdate) {
							al_hvs.add(hvs[i]);
						}
					}
					hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]);
				}
			} //yangke			

			this.taskVO.setDataVOs(hvs);
			this.removeAll(); //先擦除页面上所有数据!!
			loadRealPanel(); //实际加载!
			this.updateUI(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	/**
	 * 加载实际页面!!
	 */
	private void loadRealPanel() {
		isRefresh = tbUtil.getSysOptionBooleanValue("首页各分组是否有刷新按钮", true); //中铁王部长竟然不喜欢刷新
		String str_title = taskVO.getDefineVO().getTitle(); //
		String str_imgName = taskVO.getDefineVO().getImgicon(); //
		String str_logoimg = taskVO.getDefineVO().getLogoimg(); //
		String str_titleColor = taskVO.getDefineVO().getTitlecolor(); //
		//str_imgName = (str_imgName == null ? "office_066.gif" : str_imgName);
		//str_logoimg = (str_logoimg == null ? "90_60_01.gif" : str_logoimg);

		this.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
		this.setLayout(null); //
		this.setOpaque(false); //透明

		//标题栏图片....
		int li_y = 0; //
		JPanel titlePanel = getTitlePanel(str_title, str_imgName, str_titleColor, true); //标题面板!!
		titlePanel.setBounds(1, 1, allWidth - 2, 30); //
		this.add(titlePanel); //
		li_y = li_y + 33; //

		//大图片...
		if (str_logoimg != null && !str_logoimg.trim().equals("")) {
			JLabel img1 = new JLabel(UIUtil.getImage(str_logoimg)); //
			img1.setBounds(5, li_y, 90, 60); //
			this.add(img1); //
		}

		HashVO[] hvs = taskVO.getDataVOs(); //数据对象!!
		if (isMore) { //如果是更多!!
			labels = new WLTHrefLabel[hvs == null ? 0 : hvs.length]; //
		} else {
			labels = new WLTHrefLabel[7]; //?
		}

		//加入所有内容项
		Color color1 = LookAndFeel.desktop_Foreground; //new Color(2, 68, 152); //字体颜色,娄平说中铁项目中客户也要可配置!!
		for (int i = 0; i < labels.length; i++) {
			if (hvs != null && i < hvs.length) { //遍历多少条数据
				labels[i] = new WLTHrefLabel("·" + hvs[i] == null ? "" : hvs[i].toString()); //
				labels[i].setToolTipText("·" + hvs[i] == null ? "" : hvs[i].toString()); //

				//配置首页new图片显示天数 【杨科/2012-11-05】
				String newfield = (String) taskVO.getDefineVO().getOtherconfig().get("新消息显示天数匹配字段");
				int newday = 10;
				try {
					newday = Integer.parseInt((String) taskVO.getDefineVO().getOtherconfig().get("新消息显示天数"));
				} catch (RuntimeException e) {
					newday = 10;
				}

				if (!(newfield == null || newfield.trim().equals(""))) {
					CommonDate cd = new CommonDate(new Date());
					if (hvs[i].getDateValue(newfield) != null && cd.getDaysAfter(new CommonDate(hvs[i].getDateValue(newfield))) < newday) {
						labels[i].setIcon(UIUtil.getImage("new.gif"));
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING);
					}
				} else {
					if (i < taskVO.getDefineVO().getNewcount()) { //前两条后面有个new图标!!表示是最新的消息!!!
						labels[i].setIcon(UIUtil.getImage("new.gif")); //
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING); //
					}
				}

				labels[i].putClientProperty("HashVO", hvs[i]); //
				labels[i].putClientProperty("index", i); //
			} else {
				labels[i] = new WLTHrefLabel("·"); //
			}
			labels[i].setOpaque(false); //
			labels[i].setFont(LookAndFeel.font); //
			labels[i].setForeground(color1); //
			if (str_logoimg != null && !str_logoimg.trim().equals("")) { //如果有大图标,则前三行是在右边的!
				labels[i].setBounds(((i < 3) ? 100 : 8), li_y, ((i < 3) ? (allWidth - 90) : allWidth) - 15, 25); //
			} else { //如果没有大图片!
				labels[i].setBounds(8, li_y, allWidth - 15, 25); //
			}
			//			labels[i].addActionListener(this); //
			labels[i].addMouseListener(this); //右键可以查看HashVO数据!!
			this.add(labels[i]); //
			li_y = li_y + 25;
		}
		this.setPreferredSize(new Dimension(allWidth, li_y)); //
	}

	//创建标题面板!
	private JPanel getTitlePanel(String _title, String _imgName, String _titleColor, boolean _isHaveRightBtn) {
		_imgName = (_imgName == null ? "office_066.gif" : _imgName);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		panel.setOpaque(false);
		JLabel label_title = new JLabel(" " + _title, UIUtil.getImage(_imgName), JLabel.LEFT); //
		label_title.setOpaque(false);
		label_title.setFont(LookAndFeel.desktop_GroupTitleFont); //中铁王部长要求首页标题字体很粗大!来了不与其他字体影响,又搞了个参数!!
		if (_titleColor != null && !_titleColor.trim().equals("")) { //如果定义了颜色!!
			int li_pos = _titleColor.indexOf("@"); //看有没有@这个特殊符号?因为中铁项目中有的组的背景颜色与其他不一样!
			if (_titleColor.indexOf("@") > 0) { //如果有@符号
				label_title.setForeground(tbUtil.getColor(_titleColor.substring(0, li_pos), Color.BLACK)); //前景颜色!!
				panel.setBackground(tbUtil.getColor(_titleColor.substring(li_pos + 1, _titleColor.length()), Color.BLACK)); //背景颜色!!因为label是透明的,所以要设在panel上!!
			} else { //如果没有定义,则默认使用黑色!
				label_title.setForeground(tbUtil.getColor(_titleColor, Color.BLACK)); //只设前景!
			}
		}
		panel.add(label_title, BorderLayout.CENTER); //
		if (_isHaveRightBtn && !isMore) {
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //
			btnPanel.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 25));
			btnPanel.setOpaque(false); //
			btn_refresh = new WLTHtmlButton("刷新"); //
			btn_more = new WLTHtmlButton("更多"); //
			btn_refresh.addActionListener(this); //
			btn_more.addActionListener(this); //
			if (isRefresh) {
				btnPanel.add(btn_refresh); //
			}
			btnPanel.add(btn_more); //
			panel.add(btnPanel, BorderLayout.EAST); //
		}
		panel.setPreferredSize(new Dimension(-1, 30)); //
		return panel; //
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_refresh) {
			onRefreshGroup(false); //刷新!!
		} else if (_event.getSource() == btn_more) {
			onMore(); //
		}
	}

	/**
	 * 点击某个条目!!
	 * @param _hrefLabel
	 */
	private void onClicked(WLTHrefLabel _hrefLabel) {
		try {
			_hrefLabel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			HashVO dataVO = (HashVO) _hrefLabel.getClientProperty("HashVO"); //
			if (dataVO == null) {
				return;
			}

			int li_index = (Integer) _hrefLabel.getClientProperty("index"); //
			String str_actionName = taskVO.getDefineVO().getActionname(); //
			if (str_actionName != null && !str_actionName.trim().equals("")) { // 如果定义了自定义的Action,自己的逻辑弹出来,比如工作流.
				AbstractAction action = (AbstractAction) Class.forName(str_actionName).newInstance(); //
				action.putValue("DeskTopNewsDefineVO", taskVO.getDefineVO()); //
				action.putValue("DeskTopNewsDataVO", dataVO); //
				action.putValue("Index", new Integer(li_index)); //
				action.putValue("DeskTopPanel", this.deskTopPanel); //
				action.putValue("IndexItemTaskPanel", this); //
				action.actionPerformed(new ActionEvent(this, 1, "ClickTaskGroup")); // 执行!!!!
			} else { //
				String str_templetCode = taskVO.getDefineVO().getTempletcode(); //模板编码!
				if (str_templetCode != null && !str_templetCode.equals("")) {
					BillCardPanel cardPanel = new BillCardPanel(str_templetCode); //
					if (dataVO.getStringValue("id") != null) {
						System.out.println("主键值是=[" + dataVO.getStringValue("id") + "]"); //
						cardPanel.queryDataByCondition("id='" + dataVO.getStringValue("id") + "'"); //
					}
					BillCardDialog dialog = new BillCardDialog(this, taskVO.getDefineVO().getTitle(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); //
					dialog.setVisible(true); // 
				} else {
					System.out.println("没有定义自定义Actiob,也没有定义模板编码!");
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			_hrefLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	/**
	 * 刷新
	 */
	public void onRefreshGroup(boolean _isQuiet) {
		try {
			if (btn_refresh != null) {//点击更多弹出来时不加判断会有错误
				btn_refresh.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			}
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			if (isMore) {
				this.taskVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), this.taskVO.getDefineVO().getTitle(), true); //重置!!
			} else {
				this.taskVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), this.taskVO.getDefineVO().getTitle(), false); //重置!!
			}//BUG
			this.removeAll(); //
			this.setLayout(null); //
			this.loadRealPanel(); //
			this.updateUI(); //
		} catch (Exception ex) {
			if (_isQuiet) {
				ex.printStackTrace();
			} else {
				MessageBox.showException(this, ex); //
			}
		} finally {
			if (btn_refresh != null) {
				btn_refresh.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			}
		}
	}

	/**
	 * 点击更多!!!
	 */
	private void onMore() {
		try {
			btn_more.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			DeskTopNewGroupVO newGroupVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), taskVO.getDefineVO().getTitle(), true); //这个是不带懒装入机制的!因为没必要!!
			IndexItemTaskPanel newTaskPanel = new IndexItemTaskPanel(this.deskTopPanel, newGroupVO, 600, true, false); //

			JScrollPane scroll = new JScrollPane(newTaskPanel); //
			JScrollBar scrollbar = scroll.getVerticalScrollBar();
			scrollbar.setUnitIncrement(10); //以前首页更多上下滚动的太慢。[郝明2012-05-31]
			scroll.setOpaque(false); ////

			scroll.getViewport().setOpaque(false); ////
			JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			contentPanel.add(scroll); //
			BillDialog dialog = new BillDialog(this, "更多内容", 700, 600); //
			dialog.getContentPane().add(contentPanel); //
			dialog.setVisible(true); //
			onRefreshGroup(true); //刷新!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			btn_more.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	/**
	 * 右键记录可以查看绑定的实际数据,在项目过程中方便调试与跟踪!!!
	 */
	public void mouseClicked(MouseEvent _event) {
		WLTHrefLabel label = (WLTHrefLabel) _event.getSource(); //
		String str_actionName = taskVO.getDefineVO().getActionname(); //
		String str_dataBuilderName = taskVO.getDefineVO().getDatabuildername(); //

		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("数据构造器名称=【" + str_dataBuilderName + "】\r\n"); //
		sb_text.append("客户端点击监听器名称=【" + str_actionName + "】\r\n"); //
		sb_text.append("该记录绑定的HashVO实际值：\r\n"); //
		if (_event.getButton() == MouseEvent.BUTTON3) {
			HashVO dataVO = (HashVO) label.getClientProperty("HashVO"); //
			if (dataVO == null) {
				sb_text.append("没有绑定一个HashVO对象!!\r\n"); //
				MessageBox.show(this, sb_text.toString()); //
			} else {
				String[] str_keys = dataVO.getKeys(); //
				for (int i = 0; i < str_keys.length; i++) {
					sb_text.append("【" + str_keys[i] + "】=【" + dataVO.getStringValue(str_keys[i], "") + "】\r\n"); //
				}
				MessageBox.show(this, sb_text.toString()); //
			}
		} else {
			onClicked(label);
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

	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		IconFactory.getInstance().getPanelQueryItem_BG().draw(g, 0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
}
