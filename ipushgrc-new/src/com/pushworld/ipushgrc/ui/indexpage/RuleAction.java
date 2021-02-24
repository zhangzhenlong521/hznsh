package com.pushworld.ipushgrc.ui.indexpage;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.WLTButton;

import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;
/**
 * 首页内部制度点击事件。填出html
 * @author hm
 *
 */
public class RuleAction extends AbstractAction {
	private WLTButton btn_html = new WLTButton("预览详细");
	public void actionPerformed(ActionEvent e) {
		//直接打开内容!
		JPanel panel = (JPanel) this.getValue("DeskTopPanel");
		HashVO selectVO = (HashVO) this.getValue("DeskTopNewsDataVO");
		new RuleShowHtmlDialog(panel,selectVO,null);
	}


}
