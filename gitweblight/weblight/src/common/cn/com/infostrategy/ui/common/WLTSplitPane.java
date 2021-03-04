package cn.com.infostrategy.ui.common;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

/**
 * 继承于JSplitPane,主要是修改了一个风格
 * @author xch
 *
 */
public class WLTSplitPane extends JSplitPane {

	private static final long serialVersionUID = 456857956063744595L;

	public WLTSplitPane() {
		super();
		myResetStyle();
	}

	public WLTSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
		myResetStyle();
	}

	public WLTSplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
		myResetStyle();
	}

	public WLTSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
		myResetStyle();
	}

	public WLTSplitPane(int newOrientation) {
		super(newOrientation);
		myResetStyle();
	}

	/**
	 * 重置一下风格
	 */
	private void myResetStyle() {
		this.setDividerSize(4); //
		this.setOpaque(true); //
		this.setDividerLocation(300); //
		this.setOneTouchExpandable(false); //
		this.setBorder(BorderFactory.createEmptyBorder()); //
		//this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //
	}

}
