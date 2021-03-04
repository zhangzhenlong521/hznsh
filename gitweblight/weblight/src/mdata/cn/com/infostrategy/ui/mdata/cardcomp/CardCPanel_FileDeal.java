package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 卡片状态下的文件上传与下载框
 * 以前是没有这个类的,以前卡片上也是一个参照的类型,即点击后再弹出附件列表,要多点一次..
 * 但后来无数用户第一次不能接受这种方式，最大的原因是找不到数据，与OA使用习惯极不一样！！
 * 所以还是依照引用子表的效果搞在主页面上,因为这个抱怨太大，为了减少麻烦，觉得还是有价值搞一下的..
 * 但原来的弹出框还是要的，因为列表上点击弹出还是需要的！！！
 * @author xch
 *
 */
public class CardCPanel_FileDeal extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1L;
	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String itemKey = null; //
	private String itemName = null; //
	private JLabel label = null;

	private int li_width_all = 445; //

	private RefFileDealPanel fileDealPanel = null; //直接处理文件的弄表框
	private BillPanel billCardPanel = null; //

	private CardCPanel_FileDeal() {
	}

	/**
	 * 构造方法.
	 * @param pub_Templet_1_ItemVO
	 * @param billCardPanel
	 */
	public CardCPanel_FileDeal(Pub_Templet_1_ItemVO _templet_1_ItemVO, BillPanel _billCardPanel) {
		this.setLayout(new BorderLayout()); //
		this.setBackground(LookAndFeel.cardbgcolor); //

		this.templetItemVO = _templet_1_ItemVO; //
		this.billCardPanel = _billCardPanel; //

		this.itemKey = _templet_1_ItemVO.getItemkey(); //
		this.itemName = _templet_1_ItemVO.getItemname(); //

		int li_tablewidth = 300; //
		if (templetItemVO.getCardwidth() != null) {
			li_tablewidth = templetItemVO.getCardwidth().intValue(); //
		}

		int li_tableheight = 55; //
		label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); //

		fileDealPanel = new RefFileDealPanel(this, billCardPanel, templetItemVO.getUCDfVO()); //
		fileDealPanel.getBillListPanel().setItemWidth("filename", li_tablewidth - 130);//以前li_tablewidth - 150 宽度减去的太多了【李春娟/2012-03-26】
		fileDealPanel.getBillListPanel().getToolbarPanel().setOpaque(false); //
		this.add(label, BorderLayout.WEST); //
		this.add(fileDealPanel, BorderLayout.CENTER); //

		this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
	}

	public RefFileDealPanel getRefFileDealPanel() {

		return fileDealPanel;
	}

	public BillPanel getBillCardPanel() {
		return billCardPanel;
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
	public Object getObject() {
		return fileDealPanel.getAllFileRefItemVOs();
	}

	@Override
	public String getValue() {
		RefItemVO refVO = (RefItemVO) getObject();
		if (refVO == null) {
			return null;
		}
		return refVO.getId();
	}

	@Override
	public void setObject(Object _obj) {
		try {
			RefItemVO refVO = (RefItemVO) _obj; //
			fileDealPanel.setRefItemVO(refVO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void setValue(String _value) {
		setObject(new RefItemVO(_value, _value, _value));
	}

	@Override
	public void setItemEditable(boolean _bo) {
		fileDealPanel.setEditabled(_bo); //
		//System.out.println("再次设置是否可编辑=[" + _bo + "]"); //
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}

	@Override
	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo);
	}

	@Override
	public int getAllWidth() {
		return li_width_all;
	}

	@Override
	public void reset() {
		fileDealPanel.clearAllFile(); //
	}

	@Override
	public void focus() {

	}

	public Pub_Templet_1_ItemVO getTempletItemVO() {
		return templetItemVO;
	}

	public void setTempletItemVO(Pub_Templet_1_ItemVO templetItemVO) {
		this.templetItemVO = templetItemVO;
	}

}
