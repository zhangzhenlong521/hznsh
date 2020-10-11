package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

/**
 * 直接上传一个图片!!然后在卡片中直接显示该图片,比如HR系统中人员照片等效果!!!
 * @author xch
 *
 */
public class CardCPanel_ImageUpload extends AbstractWLTCompentPanel implements ActionListener, MouseListener {

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String itemKey = null; //
	private String itemName = null; //
	private BillCardPanel billCardPanel = null; //

	private int labelwidth = 120; //
	private int imageLabelWidth = 100; //宽度
	private int imageLabelHeight = 100; //高度

	private JLabel label = null;
	private JLabel imgLabel = null; // 

	private String str_imgbatchno = null; //图片的批号,即存储在平台系统表中的那个唯一批号!!!

	private JPopupMenu popMenu = null; //
	private JMenuItem menuItem_upload, menuItem_download, menuItem_delete, menuItem_maxview; //

	private TBUtil tbUtil = new TBUtil(); //

	private CardCPanel_ImageUpload() {
	}

	public CardCPanel_ImageUpload(Pub_Templet_1_ItemVO _templet_1_ItemVO, BillCardPanel _billCardPanel) {
		this.templetItemVO = _templet_1_ItemVO; //
		this.billCardPanel = _billCardPanel; //

		this.itemKey = _templet_1_ItemVO.getItemkey(); //
		this.itemName = _templet_1_ItemVO.getItemname(); //

		if (templetItemVO.getLabelwidth() != null) {
			labelwidth = templetItemVO.getLabelwidth().intValue(); //
		}
		if (templetItemVO.getCardwidth() != null) {
			imageLabelWidth = templetItemVO.getCardwidth().intValue(); //
		}
		if (templetItemVO.getCardHeight() != null) {
			imageLabelHeight = templetItemVO.getCardHeight().intValue(); //
		}

		this.setLayout(new BorderLayout()); //
		label = createLabel(_templet_1_ItemVO); //
		label.setPreferredSize(new Dimension(labelwidth, imageLabelHeight)); //
		this.add(label, BorderLayout.WEST); //

		imgLabel = new JLabel(); //
		imgLabel.setToolTipText("点击右键上传/删除图片,双击可放大图片"); //
		imgLabel.setVerticalAlignment(SwingConstants.TOP); //
		imgLabel.setBorder(BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor, 1)); //
		imgLabel.addMouseListener(this); //
		this.add(imgLabel, BorderLayout.CENTER); //
		this.setPreferredSize(new Dimension(labelwidth + imageLabelWidth, imageLabelHeight)); //

		//初始化右键的弹出菜单!!!!

		menuItem_upload = new JMenuItem("上传图片", UIUtil.getImage("office_081.gif")); //
		menuItem_download = new JMenuItem("下载图片", UIUtil.getImage("office_160.gif")); //下载图片
		menuItem_delete = new JMenuItem("清空图片", UIUtil.getImage("office_144.gif")); //
		menuItem_maxview = new JMenuItem("放大显示", UIUtil.getImage("office_110.gif")); //
		menuItem_upload.addActionListener(this); //
		menuItem_download.addActionListener(this); //
		menuItem_delete.addActionListener(this); //
		menuItem_maxview.addActionListener(this); //
		popMenu = new JPopupMenu(); //
		popMenu.add(menuItem_upload); //
		popMenu.add(menuItem_download); //下载
		popMenu.add(menuItem_delete); //
		popMenu.add(menuItem_maxview); //
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
		return labelwidth + imageLabelWidth;
	}

	@Override
	public Object getObject() {
		return new StringItemVO(str_imgbatchno);
	}

	@Override
	public String getValue() {
		return str_imgbatchno;
	}

	@Override
	public void setObject(Object _obj) {
		if (_obj == null) {
			reset(); //
			return; //
		}
		StringItemVO itemVO = (StringItemVO) _obj;
		String str_batchNo = itemVO.getStringValue(); //批号!!!
		if (str_batchNo == null || str_batchNo.trim().equals("")) {
			reset(); //
			return; //
		}

		this.str_imgbatchno = str_batchNo; //赋值批号!!!
		//根据批号远程调用取出图片64位编码,然后在客户端进行构造图片渲染出实际图形!!!!!!
		try {
			String str_64code = getImage64Code(str_imgbatchno); //
			byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
			ImageIcon icon = new ImageIcon(bytes, "图片"); ///创建图片

			int li_width = icon.getIconWidth(); //原始的宽度
			int li_height = icon.getIconHeight(); //原始的高度
			//缩放一下!!
			if (li_width > this.imageLabelWidth || li_height > this.imageLabelHeight) { //如果原始图片大于控件大小则要缩放处理一下!!
				imgLabel.setIcon(new ImageIcon(tbUtil.getImageScale(icon.getImage(), (li_width > imageLabelWidth ? imageLabelWidth : li_width), (li_height > imageLabelHeight ? imageLabelHeight : li_height)))); //设置图片!
			} else {
				imgLabel.setIcon(icon); //设置图片!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	private String getImage64Code(String _batid) throws Exception {
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _batid + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //遍历!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //如果有值
					sb_64code.append(str_item.trim()); //拼接起来!!
				} else { //如果值为空
					break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
				}
			}
		}
		return sb_64code.toString(); //
	}

	@Override
	public void setValue(String _value) {
		setObject(new StringItemVO(_value)); //
	}

	@Override
	public void reset() {
		str_imgbatchno = null; //
		imgLabel.setIcon(null); //
	}

	@Override
	public void setItemEditable(boolean _bo) {
		menuItem_upload.setEnabled(_bo); //
		menuItem_delete.setEnabled(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}
	
	@Override
	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	@Override
	public void focus() {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItem_upload) {
			onUploadImage(); //
		} else if (e.getSource() == menuItem_download) {
			onDownLoadImage(); //
		} else if (e.getSource() == menuItem_delete) {
			onDeleteImage(); //
		} else if (e.getSource() == menuItem_maxview) {
			onMaxScreenShow(); //
		}
	}

	public void onDownLoadImage() {
		if (str_imgbatchno == null) {
			MessageBox.show(this, "没有下载的图片!"); //
			return; //
		}
		try {
			JFileChooser fc = new JFileChooser(new File("C:/"));
			File file = new File(new File("C:\\image.jpg").getCanonicalPath());
			fc.setSelectedFile(file);
			int li_rewult = fc.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {//这里需要判断一下是否点击保存退出【李春娟/2012-04-13】
				String filename = fc.getSelectedFile().getAbsolutePath();
				String str_64code = getImage64Code(this.str_imgbatchno);
				byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
				//如果图片名称不是以.jpg或.gif或.png 结尾的话需要自定添加后缀，否则后面打开会报错!!!【李春娟/2012-04-13】
				if (filename.length() < 4 || (!filename.substring(filename.length() - 4).equalsIgnoreCase(".jpg") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".gif") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".png"))) {
					filename += ".jpg";
				}
				tbUtil.writeBytesToOutputStream(new FileOutputStream(filename), bytes); //写文件!!
				if (MessageBox.confirm(this, "下载文件[" + filename + "]成功,你是否想立即打开它?")) {
					Desktop.open(new File(filename)); //
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * 上传图片!!
	 */
	private void onUploadImage() {
		try {
			if (imgLabel.getIcon() != null) {
				if (MessageBox.showConfirmDialog(this, "您确定要覆盖原图片吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			JFileChooser fc = new JFileChooser(new File(ClientEnvironment.str_downLoadFileDir));
			int li_result = fc.showOpenDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File selFile = fc.getSelectedFile(); //取得文件!!!str_downLoadFileDir
			String str_fileName = selFile.getName(); //
			String str_path = selFile.getCanonicalPath(); //
			if (!(str_fileName.toLowerCase().endsWith(".jpg") || str_fileName.toLowerCase().endsWith(".gif") || str_fileName.toLowerCase().endsWith(".png"))) {
				MessageBox.show(this, "只能上传[jpg/gif/png]三种格式的图片!"); //
				return; //
			}

			TBUtil tbUtil = new TBUtil(); //
			byte[] bytes = tbUtil.readFromInputStreamToBytes(new FileInputStream(selFile)); //取得图片的内容!!!
			String str_code64 = tbUtil.convertBytesTo64Code(bytes); //将字节转成64位编码!

			String str_tabName = null; //
			String str_pkName = null; //
			String str_pkValue = null; //
			if (this.templetItemVO != null) {
				str_tabName = templetItemVO.getPub_Templet_1VO().getSavedtablename(); //
				str_pkName = templetItemVO.getPub_Templet_1VO().getPkname(); //主键字段名
				if (billCardPanel != null) {
					str_pkValue = billCardPanel.getRealValueAt(str_pkName); // 
				}
			}

			//上传到服务器中!!!
			String str_newBatchNO = UIUtil.getMetaDataService().saveImageUploadDocument(this.str_imgbatchno, str_code64, str_tabName, str_pkName, str_pkValue); //
			this.str_imgbatchno = str_newBatchNO; // 
			ImageIcon icon = new ImageIcon(bytes, "图片"); //
			int li_width = icon.getIconWidth(); //原始的宽度
			int li_height = icon.getIconHeight(); //原始的高度
			//缩放一下!!
			if (li_width > this.imageLabelWidth || li_height > this.imageLabelHeight) { //如果原始图片大于控件大小则要缩放处理一下!!
				imgLabel.setIcon(new ImageIcon(tbUtil.getImageScale(icon.getImage(), (li_width > imageLabelWidth ? imageLabelWidth : li_width), (li_height > imageLabelHeight ? imageLabelHeight : li_height)))); //设置图片!
			} else {
				imgLabel.setIcon(icon); //设置图片!
			}
			ClientEnvironment.str_downLoadFileDir = str_path; //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 删除图片!!!
	 */
	private void onDeleteImage() {
		if (!MessageBox.confirm("您是否真的想清空图片么?")) {
			return; //
		}

		try {
			if (this.str_imgbatchno != null) {
				UIUtil.executeUpdateByDS(null, "delete from pub_imgupload where batchid='" + str_imgbatchno + "'"); //
			}
			reset(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//放大显示!!
	private void onMaxScreenShow() {
		if (imgLabel.getIcon() == null) { //如果没数据!!
			return;
		}
		if (this.str_imgbatchno == null || this.str_imgbatchno.trim().equals("")) {
			return;
		}
		//因为后来增加了缩放处理,所在放大时必须重新取数据库,而不能直接拿页面上的image对象放大!!!
		try {
			String str_64code = getImage64Code(str_imgbatchno); //
			byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
			ImageIcon icon = new ImageIcon(bytes, "图片"); ///创建图片

			JScrollPane scroll = new JScrollPane(new JLabel(icon)); //
			scroll.setOpaque(false); //
			scroll.getViewport().setOpaque(false); //

			JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			contentPanel.add(scroll, BorderLayout.CENTER); //

			BillDialog dialog = new BillDialog(this, "放大显示", 800, 500); //
			dialog.getContentPane().add(contentPanel); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void mouseClicked(MouseEvent e) {
		JLabel label = (JLabel) e.getSource(); //
		if (e.getClickCount() == 2) { //双击就直接放大!!!
			onMaxScreenShow(); //
		} else {
			if (e.getButton() == MouseEvent.BUTTON3) {
				menuItem_maxview.setEnabled((imgLabel.getIcon() == null) ? false : true); //
				popMenu.show(label, e.getX(), e.getY()); //
			}
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
