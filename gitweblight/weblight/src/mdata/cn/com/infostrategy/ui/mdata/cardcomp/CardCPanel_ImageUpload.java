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
 * ֱ���ϴ�һ��ͼƬ!!Ȼ���ڿ�Ƭ��ֱ����ʾ��ͼƬ,����HRϵͳ����Ա��Ƭ��Ч��!!!
 * @author xch
 *
 */
public class CardCPanel_ImageUpload extends AbstractWLTCompentPanel implements ActionListener, MouseListener {

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String itemKey = null; //
	private String itemName = null; //
	private BillCardPanel billCardPanel = null; //

	private int labelwidth = 120; //
	private int imageLabelWidth = 100; //���
	private int imageLabelHeight = 100; //�߶�

	private JLabel label = null;
	private JLabel imgLabel = null; // 

	private String str_imgbatchno = null; //ͼƬ������,���洢��ƽ̨ϵͳ���е��Ǹ�Ψһ����!!!

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
		imgLabel.setToolTipText("����Ҽ��ϴ�/ɾ��ͼƬ,˫���ɷŴ�ͼƬ"); //
		imgLabel.setVerticalAlignment(SwingConstants.TOP); //
		imgLabel.setBorder(BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor, 1)); //
		imgLabel.addMouseListener(this); //
		this.add(imgLabel, BorderLayout.CENTER); //
		this.setPreferredSize(new Dimension(labelwidth + imageLabelWidth, imageLabelHeight)); //

		//��ʼ���Ҽ��ĵ����˵�!!!!

		menuItem_upload = new JMenuItem("�ϴ�ͼƬ", UIUtil.getImage("office_081.gif")); //
		menuItem_download = new JMenuItem("����ͼƬ", UIUtil.getImage("office_160.gif")); //����ͼƬ
		menuItem_delete = new JMenuItem("���ͼƬ", UIUtil.getImage("office_144.gif")); //
		menuItem_maxview = new JMenuItem("�Ŵ���ʾ", UIUtil.getImage("office_110.gif")); //
		menuItem_upload.addActionListener(this); //
		menuItem_download.addActionListener(this); //
		menuItem_delete.addActionListener(this); //
		menuItem_maxview.addActionListener(this); //
		popMenu = new JPopupMenu(); //
		popMenu.add(menuItem_upload); //
		popMenu.add(menuItem_download); //����
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
		String str_batchNo = itemVO.getStringValue(); //����!!!
		if (str_batchNo == null || str_batchNo.trim().equals("")) {
			reset(); //
			return; //
		}

		this.str_imgbatchno = str_batchNo; //��ֵ����!!!
		//��������Զ�̵���ȡ��ͼƬ64λ����,Ȼ���ڿͻ��˽��й���ͼƬ��Ⱦ��ʵ��ͼ��!!!!!!
		try {
			String str_64code = getImage64Code(str_imgbatchno); //
			byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
			ImageIcon icon = new ImageIcon(bytes, "ͼƬ"); ///����ͼƬ

			int li_width = icon.getIconWidth(); //ԭʼ�Ŀ��
			int li_height = icon.getIconHeight(); //ԭʼ�ĸ߶�
			//����һ��!!
			if (li_width > this.imageLabelWidth || li_height > this.imageLabelHeight) { //���ԭʼͼƬ���ڿؼ���С��Ҫ���Ŵ���һ��!!
				imgLabel.setIcon(new ImageIcon(tbUtil.getImageScale(icon.getImage(), (li_width > imageLabelWidth ? imageLabelWidth : li_width), (li_height > imageLabelHeight ? imageLabelHeight : li_height)))); //����ͼƬ!
			} else {
				imgLabel.setIcon(icon); //����ͼƬ!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	private String getImage64Code(String _batid) throws Exception {
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _batid + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //����!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //�����ֵ
					sb_64code.append(str_item.trim()); //ƴ������!!
				} else { //���ֵΪ��
					break; //�ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
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
			MessageBox.show(this, "û�����ص�ͼƬ!"); //
			return; //
		}
		try {
			JFileChooser fc = new JFileChooser(new File("C:/"));
			File file = new File(new File("C:\\image.jpg").getCanonicalPath());
			fc.setSelectedFile(file);
			int li_rewult = fc.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {//������Ҫ�ж�һ���Ƿ��������˳������/2012-04-13��
				String filename = fc.getSelectedFile().getAbsolutePath();
				String str_64code = getImage64Code(this.str_imgbatchno);
				byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
				//���ͼƬ���Ʋ�����.jpg��.gif��.png ��β�Ļ���Ҫ�Զ���Ӻ�׺���������򿪻ᱨ��!!!�����/2012-04-13��
				if (filename.length() < 4 || (!filename.substring(filename.length() - 4).equalsIgnoreCase(".jpg") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".gif") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".png"))) {
					filename += ".jpg";
				}
				tbUtil.writeBytesToOutputStream(new FileOutputStream(filename), bytes); //д�ļ�!!
				if (MessageBox.confirm(this, "�����ļ�[" + filename + "]�ɹ�,���Ƿ�����������?")) {
					Desktop.open(new File(filename)); //
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * �ϴ�ͼƬ!!
	 */
	private void onUploadImage() {
		try {
			if (imgLabel.getIcon() != null) {
				if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫ����ԭͼƬ��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			JFileChooser fc = new JFileChooser(new File(ClientEnvironment.str_downLoadFileDir));
			int li_result = fc.showOpenDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File selFile = fc.getSelectedFile(); //ȡ���ļ�!!!str_downLoadFileDir
			String str_fileName = selFile.getName(); //
			String str_path = selFile.getCanonicalPath(); //
			if (!(str_fileName.toLowerCase().endsWith(".jpg") || str_fileName.toLowerCase().endsWith(".gif") || str_fileName.toLowerCase().endsWith(".png"))) {
				MessageBox.show(this, "ֻ���ϴ�[jpg/gif/png]���ָ�ʽ��ͼƬ!"); //
				return; //
			}

			TBUtil tbUtil = new TBUtil(); //
			byte[] bytes = tbUtil.readFromInputStreamToBytes(new FileInputStream(selFile)); //ȡ��ͼƬ������!!!
			String str_code64 = tbUtil.convertBytesTo64Code(bytes); //���ֽ�ת��64λ����!

			String str_tabName = null; //
			String str_pkName = null; //
			String str_pkValue = null; //
			if (this.templetItemVO != null) {
				str_tabName = templetItemVO.getPub_Templet_1VO().getSavedtablename(); //
				str_pkName = templetItemVO.getPub_Templet_1VO().getPkname(); //�����ֶ���
				if (billCardPanel != null) {
					str_pkValue = billCardPanel.getRealValueAt(str_pkName); // 
				}
			}

			//�ϴ�����������!!!
			String str_newBatchNO = UIUtil.getMetaDataService().saveImageUploadDocument(this.str_imgbatchno, str_code64, str_tabName, str_pkName, str_pkValue); //
			this.str_imgbatchno = str_newBatchNO; // 
			ImageIcon icon = new ImageIcon(bytes, "ͼƬ"); //
			int li_width = icon.getIconWidth(); //ԭʼ�Ŀ��
			int li_height = icon.getIconHeight(); //ԭʼ�ĸ߶�
			//����һ��!!
			if (li_width > this.imageLabelWidth || li_height > this.imageLabelHeight) { //���ԭʼͼƬ���ڿؼ���С��Ҫ���Ŵ���һ��!!
				imgLabel.setIcon(new ImageIcon(tbUtil.getImageScale(icon.getImage(), (li_width > imageLabelWidth ? imageLabelWidth : li_width), (li_height > imageLabelHeight ? imageLabelHeight : li_height)))); //����ͼƬ!
			} else {
				imgLabel.setIcon(icon); //����ͼƬ!
			}
			ClientEnvironment.str_downLoadFileDir = str_path; //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ɾ��ͼƬ!!!
	 */
	private void onDeleteImage() {
		if (!MessageBox.confirm("���Ƿ���������ͼƬô?")) {
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

	//�Ŵ���ʾ!!
	private void onMaxScreenShow() {
		if (imgLabel.getIcon() == null) { //���û����!!
			return;
		}
		if (this.str_imgbatchno == null || this.str_imgbatchno.trim().equals("")) {
			return;
		}
		//��Ϊ�������������Ŵ���,���ڷŴ�ʱ��������ȡ���ݿ�,������ֱ����ҳ���ϵ�image����Ŵ�!!!
		try {
			String str_64code = getImage64Code(str_imgbatchno); //
			byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
			ImageIcon icon = new ImageIcon(bytes, "ͼƬ"); ///����ͼƬ

			JScrollPane scroll = new JScrollPane(new JLabel(icon)); //
			scroll.setOpaque(false); //
			scroll.getViewport().setOpaque(false); //

			JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			contentPanel.add(scroll, BorderLayout.CENTER); //

			BillDialog dialog = new BillDialog(this, "�Ŵ���ʾ", 800, 500); //
			dialog.getContentPane().add(contentPanel); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void mouseClicked(MouseEvent e) {
		JLabel label = (JLabel) e.getSource(); //
		if (e.getClickCount() == 2) { //˫����ֱ�ӷŴ�!!!
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
