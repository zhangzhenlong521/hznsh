package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTHrefLabel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * 首页自定义面板类
 * 
 * @author LiGuoli
 * @since 2012-11-16
 */
public class IndexCustPanel extends JPanel {
	private static final long serialVersionUID = 8196356088171005585L;
	private DeskTopPanel deskTopPanel = null; //
	private DeskTopNewGroupVO taskVO = null; //
	private WLTHrefLabel[] labels = null; //
	private JButton btn_refresh, btn_more; //
	private int allWidth = 360; //
	private boolean isMore = false; //
	private boolean isRefresh = true; //

	public IndexCustPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO) {
		this(_deskTopPanel, _taskVO, 360); //
	}

	public IndexCustPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		initialize(null); //
	}

	public IndexCustPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(null); //
	}

	public IndexCustPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore, boolean _isLazyLoad) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(new Boolean(_isLazyLoad)); //
	}

	public void initialize(Boolean boo) {
		this.setBackground(LookAndFeel.defaultShadeColor1);
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); // 透明s
		try {
			// String classStr = (String)
			// taskVO.getDefineVO().getOtherconfig().get("class");// 其他参数中设置
			String classStr = (String) taskVO.getDefineVO().getDatabuildername();// 其他参数中设置
			// class=cn.com.infostrategy.ui.common.DefaultIndexCustPanel
			AbstractWorkPanel workPanel = null;
			if (classStr == null || classStr.trim().equals("")) {
				return;
			}
			workPanel = (AbstractWorkPanel) Class.forName(classStr).newInstance();
			workPanel.setDeskTopPanel(getDeskTopPanel());
			workPanel.initialize();
			this.add(workPanel, BorderLayout.CENTER);

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DeskTopPanel getDeskTopPanel() {
		return deskTopPanel;
	}

}
