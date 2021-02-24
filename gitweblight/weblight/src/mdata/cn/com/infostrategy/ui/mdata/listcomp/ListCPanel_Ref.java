/**************************************************************************
 * $RCSfile: ListCPanel_Ref.java,v $  $Revision: 1.7 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

public class ListCPanel_Ref extends UIRefPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * 直接手工创建
	 * @param _key 唯一标识
	 * @param _name  名称
	 * @param _refdesc 参照定义
	 * @param _type 参照类型
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public ListCPanel_Ref(String _key, String _name, String _refdesc, String _type, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_key, _name, _refdesc, _type, _initRefItemVO, _billPanel); ////
		initialize();
	}

	/**
	 * 由元原模板创建
	 * @param _templetVO
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public ListCPanel_Ref(Pub_Templet_1_ItemVO _templetVO, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_templetVO, _initRefItemVO, _billPanel);
		initialize();
	}

	/**
	 * 构建页面
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //在列表中文本框的边框必须是这个!!!
		}
		if (this.type.equals(WLTConstants.COMP_DATE) || this.type.equals(WLTConstants.COMP_DATETIME)) {
			textField.setEnabled(false);
		}
		this.add(textField, BorderLayout.CENTER); //先加入文本框

		JPanel panel_east = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		panel_east.add(btn_ref);
		boolean onlyonebtn = true;
		if (templetItemVO != null) {
			if ((WLTConstants.COMP_OFFICE).equals(templetItemVO.getItemtype())) {
				if (importB != null) {
					panel_east.add(importB); //
					onlyonebtn = false;
				}
			}
		}

		int li_hyperlinkwidth = 0;
		if (hyperlinks != null) { //如果有超连接
			for (int i = 0; i < hyperlinks.length; i++) {
				li_hyperlinkwidth = li_hyperlinkwidth + (int) hyperlinks[i].getPreferredSize().getWidth(); //
				panel_east.add(hyperlinks[i]); //加入超链接面板
				onlyonebtn = false;
			}
		}
		if (onlyonebtn) {
			this.add(btn_ref, BorderLayout.EAST); //如果只有一个按钮，直接加入到面板中，按钮同文本框会同时增加高度。hm 2013-5-31
		} else {
			this.add(panel_east, BorderLayout.EAST); //
		}
	}

	public int getAllWidth() {
		return 0;
	}

}
