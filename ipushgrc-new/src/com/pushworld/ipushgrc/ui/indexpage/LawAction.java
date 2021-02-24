package com.pushworld.ipushgrc.ui.indexpage;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.WLTButton;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;
/**
 * ��ҳ���ɷ������¼������html
 * @author hm
 *
 */
public class LawAction extends AbstractAction {
	private WLTButton btn_html = new WLTButton("Ԥ����ϸ");
	public void actionPerformed(ActionEvent e) {
		//ֱ�Ӵ�����!
		JPanel panel = (JPanel) this.getValue("DeskTopPanel");
		HashVO selectVO = (HashVO) this.getValue("DeskTopNewsDataVO");
		showLawHtml(selectVO.getStringValue("id"), panel);
	}	
	private void showLawHtml(String lawid, Container _parent) {		
		new LawShowHtmlDialog(_parent,lawid);
	}

}
