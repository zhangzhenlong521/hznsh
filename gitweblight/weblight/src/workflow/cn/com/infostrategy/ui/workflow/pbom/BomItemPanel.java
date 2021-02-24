package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * 
 * @author xch
 * 
 */
public class BomItemPanel extends JPanel implements ActionListener, DropTargetListener {

	private BillBomPanel billBomPanel; //

	private String bomid = null;
	private String bomcode = null;
	private String imgName = null; //
	private String afterInitCls = null; //

	private DeskTopPanel deskTopPanel = null; //
	private JMenuItem item_seteditstate, item_save, item_lookbom, item_add, item_delete, item_update, item_lookme, item_uploadimg, item_downloadimg; //

	private Vector v_bomitemclicklistener = new Vector(); //
	private Vector v_labels = new Vector(); //
	private String[] bomItemKeys = null; //

	private boolean bo_isEditState = false; //
	private JLabel label_bg = null;

	private boolean editable = true;// ������ǰ�ģ�Ĭ��Ϊ�ɱ༭
	private TBUtil tbUtil = new TBUtil(); //
	private ImageIcon icon = null;

	public boolean isCanSetRiskVO = true; // �����Ƿ�������÷��յ�VO,�����淽��setCanSetRiskVO()�е�ע��!��xch/2012-03-07��
	private boolean canClick = true; //

	private BomItemPanel() {
	}

	protected BomItemPanel(BillBomPanel _bomPanel, String _code) {
		this.billBomPanel = _bomPanel; //
		this.bomcode = _code; //
		new DropTarget(this, this); //
		this.setOpaque(false); // ͸��!!!
		this.setLayout(null); // ����ֵ����
		int li_width = 500; //
		int li_height = 300; //
		try {
			HashVO[] hvs_1 = UIUtil.getHashVoArrayByDS(null, "select * from pub_bom where code='" + _code + "'"); //
			if (hvs_1 != null && hvs_1.length > 0) { //
				this.bomid = hvs_1[0].getStringValue("id"); //
				this.bomcode = hvs_1[0].getStringValue("code"); //
				this.imgName = hvs_1[0].getStringValue("bgimgname"); //
				this.afterInitCls = hvs_1[0].getStringValue("afterinitcls"); //
				if (imgName != null) {
					SysAppServiceIfc sysService = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); // /
					String str_img64code = sysService.getImageUpload64Code(imgName); //
					if (str_img64code != null && !str_img64code.equals("")) {
						byte[] imgBytes = tbUtil.convert64CodeToBytes(str_img64code); //
						icon = new ImageIcon(imgBytes); // ����ͼƬ!!!
					}
				}
				// �ӱ�����!!
				HashVO[] hvs_2 = UIUtil.getHashVoArrayByDS(null, "select * from pub_bom_b where bomid='" + this.bomid + "'"); //
				bomItemKeys = new String[hvs_2.length]; //
				for (int i = 0; i < hvs_2.length; i++) {
					bomItemKeys[i] = hvs_2[i].getStringValue("itemkey"); //
					BomLabel itemLabel = new BomLabel(hvs_2[i].getStringValue("id"), hvs_2[i].getStringValue("itemkey"), hvs_2[i].getStringValue("itemname"), hvs_2[i].getStringValue("bindtype"), //
							hvs_2[i].getStringValue("bindbomcode"), hvs_2[i].getStringValue("bindclassname"), hvs_2[i].getStringValue("bindmenu"), hvs_2[i].getStringValue("loadtype"), //
							hvs_2[i].getIntegerValue("x").intValue(), hvs_2[i].getIntegerValue("y").intValue(), hvs_2[i].getIntegerValue("width").intValue(), hvs_2[i].getIntegerValue("height").intValue());
					v_labels.add(itemLabel); //
					this.add(itemLabel);
				}
			}

			if (icon == null || icon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				label_bg = new JLabel("���ݱ���[" + _code + "]��ͼƬ��ֵ[" + imgName + "]û����ȡ��ͼƬ!"); //
				label_bg.setBounds(0, 0, li_width, li_height); //
			} else {
				label_bg = new JLabel(icon); //
				label_bg.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight()); // ͼƬ��С
				li_width = icon.getIconWidth(); //
				li_height = icon.getIconHeight(); //

			}
			label_bg.setHorizontalAlignment(SwingConstants.LEFT);
			label_bg.setVerticalAlignment(SwingConstants.TOP); //
			this.add(label_bg); //
			this.setPreferredSize(new Dimension(li_width, li_height)); //
			if (ClientEnvironment.isAdmin()) {
				this.setToolTipText("��" + li_width + ",��" + li_height); //
			}

			// �����������������!��Ӵ�һ��,��һ����Ŀ�������ͻ��������ͼ,����һ��,���������������Ŀ����Աά������͸㶨��!!��xch/2012-03-07��
			if (this.afterInitCls != null && !this.afterInitCls.trim().equals("")) { //
				afterInitIntercept(hvs_1[0].getStringValue("afterinitcls")); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		setEditable(true);// ������ǰ�ģ�Ĭ��Ϊ�ɱ༭
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean _editable) {
		this.editable = _editable;
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && editable) {
					showPopMenu(e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * ��ñ�bomͼ���е�itemkeyֵ ���ڻ�����ͼ�У���һ�����л������������ڣ�ֻ��Ҫ��������ȵ��itemkeyֵ��Ӧ�Ļ������ɣ�
	 */
	public String[] getAllItemKey() {
		BomLabel[] labels = getLabels(); //
		String[] itemkeys = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			itemkeys[i] = labels[i].getItemkey();
		}
		return itemkeys;
	}

	/**
	 * ��ʼ���������������,��Ϊ����������Bomͼ�������÷��յ�,ֻ��
	 */
	private void afterInitIntercept(String _clasName) {
		try {
			_clasName = _clasName.trim(); //
			int li_pos = _clasName.indexOf("("); //
			String str_realClsName = null; // ʵ�ʵ�����,��Ϊ��������в���!
			String[] str_pars = null; // �������!!!
			if (li_pos > 0) { // ����в���!!
				str_realClsName = _clasName.substring(0, li_pos); //
				String str_par = _clasName.substring(li_pos + 1, _clasName.lastIndexOf(")")); //
				str_pars = this.tbUtil.split(str_par, ","); // �ָ�,���м�������!!!
				for (int i = 0; i < str_pars.length; i++) {
					if (str_pars[i].startsWith("\"")) { //
						str_pars[i] = str_pars[i].substring(1, str_pars[i].length()); // ȥ��ǰ���˫����!
					}
					if (str_pars[i].endsWith("\"")) { //
						str_pars[i] = str_pars[i].substring(0, str_pars[i].length() - 1); // ȥ�������˫����!
					}
				}
			} else {
				str_realClsName = _clasName; // ���û�в���!!
			}

			Class cls = Class.forName(str_realClsName); // ���䴴��
			Constructor cst = cls.getConstructor(BillBomPanel.class, BomItemPanel.class, String[].class); // ȡ�ù��췽��!!
			Object obj = cst.newInstance(this.billBomPanel, this, str_pars); // �������췽��!!!
		} catch (Exception ex) {
			ex.printStackTrace(); // ���ܻᷢ��ClassNotFound,�Ҳ�����Ӧ�Ĺ��췽�����쳣!!
		}
	}

	public void addBomItemClickListener(BomItemClickListener _listener) {
		v_bomitemclicklistener.add(_listener); //
	}

	private BomLabel[] getLabels() {
		return (BomLabel[]) v_labels.toArray(new BomLabel[0]);
	}

	private BomLabel getBomLabel(String _itemkey) {
		BomLabel[] labels = getLabels(); //
		for (int i = 0; i < labels.length; i++) {
			if (labels[i].getItemkey().equals(_itemkey)) {
				return labels[i]; //
			}
		}
		return null;
	}

	public void setAllRiskVO(RiskVO _riskVO) {
		if (!isCanSetRiskVO) {
			return;
		}
		BomLabel[] labels = getLabels(); //
		for (int i = 0; i < labels.length; i++) {
			labels[i].setRiskVO(_riskVO); //
		}
	}

	public void setRiskVO(Hashtable _htRiskVO) {
		if (!isCanSetRiskVO) {
			return;
		}
		String[] str_keys = (String[]) _htRiskVO.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) {
			setRiskVO(str_keys[i], (RiskVO) _htRiskVO.get(str_keys[i])); //
		}
	}

	public void setRiskVO(Hashtable _htRiskVO, String type) {
		if (!isCanSetRiskVO) {
			return;
		}
		String[] str_keys = (String[]) _htRiskVO.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) {
			setRiskVO(str_keys[i], (RiskVO) _htRiskVO.get(str_keys[i]), type); //
		}
	}

	private void setRiskVO(String _itemkey, RiskVO _riskVO, String type) {
		if (!isCanSetRiskVO) { // ������������÷��յ�,���˳�!
			return;
		}
		BomLabel label = getBomLabel(_itemkey);
		if (label != null) {
			label.setRiskVO(_riskVO, type); //
		}

	}

	public void setRiskVO(String _itemkey, RiskVO _riskVO) {
		if (!isCanSetRiskVO) { // ������������÷��յ�,���˳�!
			return;
		}
		BomLabel label = getBomLabel(_itemkey);
		if (label != null) {
			label.setRiskVO(_riskVO); //
		}
	}

	/**
	 * ������������ȵ�,��ʱ����Demoʱ��Ҫ���ٳ�Ч��,һ��������̫����!!��������һ������,Ϊ������һ�д������Ϊ�����ȵ������Ϸ��յ�!!
	 * ��Ϊ����ʾ���õġ�xch/2012-03-07��
	 */
	public void setRiskVOAsRandom() {
		if (!isCanSetRiskVO) { // ������������÷��յ�,���˳�!
			return;
		}
		if (bomItemKeys == null || bomItemKeys.length <= 0) {
			return;
		}
		Random random = new Random(); //
		for (int i = 0; i < bomItemKeys.length; i++) {
			int li_1 = random.nextInt(50) - 15; //
			int li_2 = random.nextInt(50) - 15; //
			int li_3 = random.nextInt(50) - 15; //
			setRiskVO(bomItemKeys[i], new RiskVO(li_1, li_2, li_3)); //
		}
	}

	/**
	 * �����Ƿ�������÷��յ�VO,��ǰbomͼ�����÷��յ㶼��д����,������һ����Ŀ����������,���ÿ�����pub_bom��ע��һ����ʼ������
	 * ��������п������÷��յ�
	 * ,��Ϊ�˸���ԭ�����߼�,��Ҫ��һ������,����ע�����������˷��յ��֪ͨ��������þ�ûЧ����!!���Լ�������һ������!!��xch
	 * /2012-03-07��
	 * 
	 * @param isCanSetRiskVO
	 */
	public void setCanSetRiskVO(boolean isCanSetRiskVO) {
		this.isCanSetRiskVO = isCanSetRiskVO;
	}

	/**
	 * ȡ�������ȵ��itemkey
	 * 
	 * @return
	 */
	public String[] getAllBomItemKeys() {
		return this.bomItemKeys; //
	}

	protected void showPopMenu(final int _x, final int _y) {
		if (bomid == null) {
			MessageBox.show(this, "pub_bomû�ж�Ӧ������!");
			return;
		}

		JPopupMenu popmenu_header = new JPopupMenu();
		if (bo_isEditState) {
			item_seteditstate = new JMenuItem("ȡ���༭״̬"); // չ�����н��
		} else {
			item_seteditstate = new JMenuItem("�༭״̬"); // չ�����н��
		}

		item_save = new JMenuItem("����λ�����С"); // չ�����н��
		item_add = new JMenuItem("�����ȵ�"); // չ�����н��
		item_lookbom = new JMenuItem("�鿴BOMͼ��Ϣ"); // չ�����н��
		item_uploadimg = new JMenuItem("�ϴ�BOMͼƬ");
		item_downloadimg = new JMenuItem("����BOMͼƬ");

		item_seteditstate.addActionListener(this); //
		item_save.addActionListener(this); //
		item_lookbom.addActionListener(this); //
		item_uploadimg.addActionListener(this);
		item_downloadimg.addActionListener(this);
		item_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAdd(_x, _y);
			}
		}); //

		popmenu_header.add(item_seteditstate); //
		popmenu_header.add(item_add); //
		popmenu_header.add(item_save); //
		popmenu_header.add(item_lookbom); //
		popmenu_header.add(item_uploadimg); //
		popmenu_header.add(item_downloadimg); //
		if (bo_isEditState) {
			item_add.setEnabled(true);
			item_save.setEnabled(true);
		} else {
			item_add.setEnabled(false);
			item_save.setEnabled(false);
		}
		popmenu_header.show(this, _x, _y); //
	}

	protected void showLabelPopMenu(final JLabel _label, final int _x, final int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();
		item_delete = new JMenuItem("ɾ���ȵ�");
		item_update = new JMenuItem("�޸��ȵ�");
		item_lookme = new JMenuItem("�鿴�ȵ���Ϣ");

		item_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelete(_label); //
			}
		});

		item_update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUpdate(_label); //
			}
		});

		item_lookme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLookMe(_label); //
			}
		});

		popmenu_header.add(item_delete); //
		popmenu_header.add(item_update); //
		popmenu_header.add(item_lookme); //

		if (bo_isEditState) {
			item_delete.setEnabled(true); //
			item_update.setEnabled(true); //

		} else {
			item_delete.setEnabled(false);
			item_update.setEnabled(false); //

		}

		popmenu_header.show(_label, _x, _y); //
	}

	private void setAllLabelBorderRed(int _linewidth) {
		for (int i = 0; i < getLabels().length; i++) {
			getLabels()[i].setBorder(BorderFactory.createLineBorder(Color.RED, _linewidth)); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == item_seteditstate) {
			if (bo_isEditState) {
				onUnSetEditState(); //
			} else {
				onSetEditState(); //
			}
		} else if (e.getSource() == item_save) {
			onSave();
		} else if (e.getSource() == item_lookbom) {
			onLookBomInfo();
		} else if (e.getSource() == item_uploadimg) {
			onUploadImage();
		} else if (e.getSource() == item_downloadimg) {
			onDownLoadImage();
		}
	}

	// BomPanel����Ҫ�����������,����public
	public void onSetEditState() {
		bo_isEditState = true; //
		setAllLabelBorderRed(2); //
		for (int i = 0; i < getLabels().length; i++) {
			getLabels()[i].setText(getLabels()[i].getItemname()); //
			getLabels()[i].addDrop();
		}
		label_bg.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //
	}

	// BomPanel����Ҫ�����������,����public
	public void onUnSetEditState() {
		bo_isEditState = false; //
		setAllLabelBorderRed(0); //
		for (int i = 0; i < getLabels().length; i++) {
			getLabels()[i].setText(""); //
		}
		bo_isEditState = false; //
		for (int i = 0; i < getLabels().length; i++) {
			getLabels()[i].removeDrop();
		}
		label_bg.setBorder(BorderFactory.createLineBorder(Color.RED, 0)); //
	}

	private void onAdd(int _x, int _y) {
		try {
			BillCardPanel cardPanel = new BillCardPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_BOM_B", new String[] { bomid })); // Ϊ�˼���UI�����صĴ�����,�Ƶ���������
			cardPanel.insertRow(); //
			cardPanel.setValueAt("bomid", new StringItemVO(bomid)); //
			cardPanel.setValueAt("x", new StringItemVO(_x + "")); //
			cardPanel.setValueAt("y", new StringItemVO(_y + "")); //
			cardPanel.setValueAt("width", new StringItemVO(75 + "")); //
			cardPanel.setValueAt("height", new StringItemVO(75 + "")); //

			BillCardDialog dialog = new BillCardDialog(this, "BOM", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				BillVO billVO = dialog.getBillVO(); //
				String str_id = billVO.getStringValue("id"); //
				String str_itemkey = billVO.getStringValue("itemkey"); //
				String str_itemname = billVO.getStringValue("itemname"); //
				String str_bindtype = billVO.getStringValue("bindtype"); //
				String str_bindbomcode = billVO.getStringValue("bindbomcode"); //
				String str_bindclassname = billVO.getStringValue("bindclassname"); //
				String str_bindmenu = billVO.getStringValue("bindmenu"); //
				String str_loadtype = billVO.getStringValue("loadtype"); //
				BomLabel label = new BomLabel(str_id, str_itemkey, str_itemname, str_bindtype, str_bindbomcode, str_bindclassname, str_bindmenu, str_loadtype, _x, _y, 75, 75); //
				label.setText(str_itemname); //
				label.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); //
				label.addDrop(); // ��������
				this.add(label, this.getComponentCount() - 1); //
				this.updateUI(); //
				v_labels.add(label); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	private void onDelete(JLabel _label) {
		try {
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ�����ȵ���?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			String str_itemid = ((BomLabel) _label).getItemid();
			UIUtil.executeUpdateByDS(null, "delete from pub_bom_b where id='" + str_itemid + "'"); //
			this.remove(_label); //
			this.updateUI(); //
			v_labels.remove(_label); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private void onUpdate(JLabel _label) {
		try {
			BomLabel blabel = (BomLabel) _label; //
			BillCardPanel cardPanel = new BillCardPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_BOM_B", new String[] { bomid })); //
			cardPanel.queryDataByCondition("id=" + blabel.getItemid()); //
			BillCardDialog dialog = new BillCardDialog(this, "BOM", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				BillVO billVO = dialog.getBillVO(); //
				String str_itemkey = billVO.getStringValue("itemkey"); //
				String str_itemname = billVO.getStringValue("itemname"); //
				String str_bindtype = billVO.getStringValue("bindtype"); //
				String str_bindbomcode = billVO.getStringValue("bindbomcode"); //
				String str_bindclass = billVO.getStringValue("bindclassname"); //
				String str_bindmenu = billVO.getStringValue("bindmenu"); //
				blabel.setItemkey(str_itemkey);
				blabel.setItemname(str_itemname); //
				blabel.setBindtype(str_bindtype); //
				blabel.setBindbomcode(str_bindbomcode); //
				blabel.setBindclass(str_bindclass); //
				blabel.setBindmenu(str_bindmenu); //
				blabel.setText(str_itemname); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private void onLookBomInfo() {
		String str_text = "code:[" + bomcode + "]\r\n";
		str_text = str_text + "����ͼ:[" + imgName + "]\r\n";
		str_text = str_text + "��������:[" + (afterInitCls == null ? "" : afterInitCls) + "]\r\n";
		MessageBox.showTextArea(this, str_text); //
	}

	/**
	 * �ϴ�ͼƬ!!
	 */
	private void onUploadImage() {
		try {
			if (label_bg.getIcon() != null) {
				if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫ����ԭͼƬ��?\r\n��������:���ڳߴ�ԭ���п��ܵ���ԭ�ȵ��޷��༭!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			JFileChooser fc = new JFileChooser(new File(ClientEnvironment.str_downLoadFileDir));
			int li_result = fc.showOpenDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File selFile = fc.getSelectedFile(); // ȡ���ļ�!!!str_downLoadFileDir
			String str_fileName = selFile.getName(); //
			String str_path = selFile.getCanonicalPath(); //
			if (!(str_fileName.toLowerCase().endsWith(".jpg") || str_fileName.toLowerCase().endsWith(".gif") || str_fileName.toLowerCase().endsWith(".png"))) {
				MessageBox.show(this, "ֻ���ϴ�[jpg/gif/png]���ָ�ʽ��ͼƬ!"); //
				return; //
			}

			TBUtil tbUtil = new TBUtil(); //
			byte[] bytes = tbUtil.readFromInputStreamToBytes(new FileInputStream(selFile)); // ȡ��ͼƬ������!!!
			String str_code64 = tbUtil.convertBytesTo64Code(bytes); // ���ֽ�ת��64λ����!

			// �ϴ�����������!!!
			UIUtil.getMetaDataService().saveImageUploadDocument(this.imgName, str_code64, "pub_bom", "id", this.bomid); //
			icon = new ImageIcon(bytes, "ͼƬ"); //
			label_bg.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight()); //
			label_bg.setIcon(icon);
			this.setPreferredSize(new Dimension(icon.getIconWidth() + 10, icon.getIconHeight() + 10)); //
			ClientEnvironment.str_downLoadFileDir = str_path; //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ����ͼƬ!!
	 */
	public void onDownLoadImage() {
		if (this.imgName == null) {
			MessageBox.show(this, "û�����ص�ͼƬ!"); //
			return; //
		}
		try {
			JFileChooser fc = new JFileChooser(new File("C:/"));
			File file = new File(new File("C:\\image.jpg").getCanonicalPath());
			fc.setSelectedFile(file);
			int li_rewult = fc.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {// ������Ҫ�ж�һ���Ƿ��������˳������/2012-04-13��
				String filename = fc.getSelectedFile().getAbsolutePath();
				String str_64code = getImage64Code(this.imgName);
				byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
				// ���ͼƬ���Ʋ�����.jpg��.gif��.png
				// ��β�Ļ���Ҫ�Զ���Ӻ�׺���������򿪻ᱨ��!!!�����/2012-04-13��
				if (filename.length() < 4 || (!filename.substring(filename.length() - 4).equalsIgnoreCase(".jpg") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".gif") && !filename.substring(filename.length() - 4).equalsIgnoreCase(".png"))) {
					filename += ".jpg";
				}
				tbUtil.writeBytesToOutputStream(new FileOutputStream(filename), bytes); // д�ļ�!!
				if (MessageBox.confirm(this, "�����ļ�[" + filename + "]�ɹ�,���Ƿ�����������?")) {
					Desktop.open(new File(filename)); //
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private String getImage64Code(String _batid) throws Exception {
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _batid + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { // ����!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { // �����ֵ
					sb_64code.append(str_item.trim()); // ƴ������!!
				} else { // ���ֵΪ��
					break; // �ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
				}
			}
		}
		return sb_64code.toString(); //
	}

	private void onLookMe(JLabel _label) {
		BomLabel blabel = (BomLabel) _label; //
		String str_text = "code:[" + bomcode + "]\r\n";
		str_text = str_text + "����ͼ:[" + imgName + "]\r\n";
		str_text = str_text + "id:[" + blabel.getItemid() + "]\r\n";
		str_text = str_text + "ItmKey:[" + blabel.getItemkey() + "]\r\n";
		str_text = str_text + "ItmName:[" + blabel.getItemname() + "]\r\n";
		str_text = str_text + "BindBomCode:[" + blabel.getBindbomcode() + "]\r\n";
		MessageBox.showTextArea(this, str_text); //
	}

	public void onSave() {
		try {
			Vector v_sqls = new Vector(); //
			for (int i = 0; i < getLabels().length; i++) {
				Rectangle rec = getLabels()[i].getBounds();
				int li_x = (int) rec.getX(); //
				int li_y = (int) rec.getY(); //
				int li_width = (int) rec.getWidth(); //
				int li_height = (int) rec.getHeight(); //
				v_sqls.add(getSQL(getLabels()[i].getItemid(), getLabels()[i].getItemkey(), getLabels()[i].getItemname(), li_x, li_y, li_width, li_height)); //
			}

			UIUtil.executeBatchByDS(null, v_sqls); //
			onUnSetEditState();
			MessageBox.show(this, "���浱ǰͼ�γɹ�!!");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private String getSQL(String _itemid, String _itemkey, String _imgname, int li_x, int li_y, int li_width, int li_height) throws Exception {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("update pub_bom_b set ");
		sb_sql.append("itemkey='" + _itemkey + "',");
		sb_sql.append("itemname='" + _imgname + "',");
		sb_sql.append("x='" + li_x + "',");
		sb_sql.append("y='" + li_y + "',");
		sb_sql.append("width='" + li_width + "',");
		sb_sql.append("height='" + li_height + "' ");
		sb_sql.append("where id='" + _itemid + "'");
		return sb_sql.toString(); //
	}

	public void dragEnter(DropTargetDragEvent evt) {
		// Called when the user is dragging and enters this drop target.
	}

	public void dragOver(DropTargetDragEvent evt) {
		// Called when the user is dragging and moves over this drop target.
		// System.out.println("dragOver");
	}

	public void dragExit(DropTargetEvent evt) {
		// Called when the user is dragging and leaves this drop target.
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
		// Called when the user changes the drag action between copy or move.
	}

	public void drop(DropTargetDropEvent evt) {
		// Called when the user finishes or cancels the drag operation.

		// this.setBounds((int) pt.getX(), (int) pt.getY(), this.getWidth(),
		// this.getHeight());
	}

	public String getBomcode() {
		return bomcode;
	}

	public void setBomcode(String bomcode) {
		this.bomcode = bomcode;
	}

	public DeskTopPanel getDeskTopPanel() {
		return deskTopPanel;
	}

	public void setDeskTopPanel(DeskTopPanel deskTopPanel) {
		this.deskTopPanel = deskTopPanel;
	}

	public void setCanClicked(boolean _canClick) {
		this.canClick = _canClick; //
	}

	class BomLabel extends JLabel implements DragGestureListener, DragSourceListener {
		DragSource dragSource;
		private RiskVO riskvo = null;

		String type = null;
		String itemid = null; //
		String itemkey = null; //
		String itemname = null; //
		String bindtype = null;
		String bindbomcode = null;
		String bindclass = null;
		String bindmenu = null; //
		String loadtype = null; // ��������,��������������������Ŀ������˵,ϲ��������!!������Ū�˸�����!��xch/2012-03-09��

		int clickedX = 0, clickedY = 0, shapeWidth = 15, shapeHeight = 15;
		boolean bo_isresize = false; //

		public BomLabel(String _itemid, String _itemkey, String _itemname, String _bindtype, String _bindbomcode, String _bindclass, String _bindmenu, String _loadtype, int _x, int _y, int _width, int _height) {
			super(); //

			this.itemid = _itemid; //
			this.itemkey = _itemkey; //
			this.itemname = _itemname; //
			this.bindtype = _bindtype; //
			this.bindbomcode = _bindbomcode; //
			this.bindclass = _bindclass;
			this.bindmenu = _bindmenu;
			this.loadtype = _loadtype; //
			this.setOpaque(false); //
			this.setToolTipText(itemname); //
			this.setBorder(BorderFactory.createLineBorder(Color.RED, 0)); //
			this.setBounds(_x, _y, _width, _height); //
			dragSource = new DragSource();

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					clickedX = e.getX(); //
					clickedY = e.getY(); //
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (!canClick) {
						return; //
					}
					onMouseEnter(e); //
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (!canClick) {
						return; //
					}
					onMouseExit(); //
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (!canClick) {
						return; //
					}
					if (!e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
						onBomCellClicked((BomLabel) e.getSource()); //
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						showLabelPopMenu((JLabel) e.getSource(), e.getX(), e.getY());
					}

				}
			}); //
		}

		private void filterMenu() {
			getDeskTopPanel().closeAllTab(); // �ر�����ҳǩ!
		}

		public void Menu() {
			new SplashWindow(this, new AbstractAction() {
				private static final long serialVersionUID = -287905438900197436L;

				public void actionPerformed(ActionEvent e) {
					filterMenu(); //
				}
			}, 366, 366);
		}

		protected void onBomCellClicked(BomLabel _label) {
			try {
				_label.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
				for (int i = 0; i < v_bomitemclicklistener.size(); i++) {
					BomItemClickListener listener = (BomItemClickListener) v_bomitemclicklistener.get(i); //
					BomItemClickEvent event = new BomItemClickEvent(bomcode, _label.getItemkey(), _label.getItemname(), _label.getBindtype(), _label.getBindbomcode(), _label.getBindclass(), _label.getBindmenu(), _label.getLoadtype(), BomItemPanel.this); //
					listener.onBomItemClicked(event); //
				}
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex); //
			} finally {
				_label.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			}
		}

		public void addDrop() {
			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		}

		public void removeDrop() {
			if (dragSource != null) {
				dragSource.removeDragSourceListener(this);
				dragSource = null; //
			}
		}

		private void onMouseEnter(MouseEvent e) {
			BomLabel itemLabel = (BomLabel) e.getSource();
			if (bo_isEditState) {
				itemLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); //
			} else {
				itemLabel.setBorder(BorderFactory.createRaisedBevelBorder()); //
			}
			Cursor thisCurSor = new Cursor(Cursor.HAND_CURSOR); //
			boolean isResize = false; //
			if (e.isControlDown()) { // ����ǰ�סCtrl����!!���ж�
				int li_width = (int) itemLabel.getBounds().getWidth(); //
				int li_height = (int) itemLabel.getBounds().getHeight(); //
				if ((e.getX() > li_width - 15 && e.getY() > li_height - 15) || (e.getX() < li_width + 15 && e.getY() < li_height + 15)) { //
					if (bo_isEditState) {
						thisCurSor = new Cursor(Cursor.SE_RESIZE_CURSOR); //
						isResize = true;
					}
				}
			}
			itemLabel.setCursor(thisCurSor); //
			bo_isresize = isResize; //
		}

		private void onMouseExit() {
			if (bo_isEditState) {
				this.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); //
			} else {
				this.setBorder(BorderFactory.createLineBorder(Color.RED, 0)); //
			}
			if (bo_isresize) { // ����Ǹı��С״̬!!�����κθı�!!
			} else {
				bo_isresize = false; //
				this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			if (dragSource != null) {
				try {
					Transferable t = new StringSelection("aString");
					dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

		public void dragEnter(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and enters
			// the drop target.
		}

		public void dragOver(DragSourceDragEvent _evt) {
			if (!bo_isEditState) { // ���Ǳ༭״̬��ֱ���˳�!!!
				return;
			}
			BomLabel label = (BomLabel) _evt.getDragSourceContext().getComponent(); // ȡ�ÿؼ�
			Point pt_1 = _evt.getLocation();
			SwingUtilities.convertPointFromScreen(pt_1, BomItemPanel.this); //
			if (label.isBo_isresize()) { // �������ı��С״̬,����Ǹı��С״̬
				int li_oldX = (int) label.getBounds().getX();
				int li_oldY = (int) label.getBounds().getY();
				int li_new_width = (int) (pt_1.getX() - li_oldX);
				int li_new_height = (int) (pt_1.getY() - li_oldY);
				if (li_new_width < 20) {
					li_new_width = 20;
				}

				if (li_new_height < 1) {
					li_new_height = 1;
				}

				label.setBounds(li_oldX, li_oldY, li_new_width, li_new_height); //
			} else {
				label.setBounds((int) (pt_1.getX() - label.getClickedX()), (int) (pt_1.getY() - label.getClickedY()), label.getWidth(), label.getHeight());
			}
		}

		public void dragExit(DragSourceEvent evt) {
			// Called when the user is dragging this drag source and leaves
			// the drop target.
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
			// Called when the user changes the drag action between copy or
			// move.
		}

		public void dragDropEnd(DragSourceDropEvent _evt) {
			// Point pt = _evt.getLocation();
			// SwingUtilities.convertPointFromScreen(pt, PBomPanle2.this); //
			// this.setBounds((int) pt.getX(), (int) pt.getY(), this.getWidth(),
			// this.getHeight());
		}

		public int getClickedX() {
			return clickedX;
		}

		public void setClickedX(int clickedX) {
			this.clickedX = clickedX;
		}

		public int getClickedY() {
			return clickedY;
		}

		public void setClickedY(int clickedY) {
			this.clickedY = clickedY;
		}

		public boolean isBo_isresize() {
			return bo_isresize;
		}

		public void setBo_isresize(boolean bo_isresize) {
			this.bo_isresize = bo_isresize;
		}

		public String getItemid() {
			return itemid;
		}

		public void setItemid(String itemid) {
			this.itemid = itemid;
		}

		public String getItemkey() {
			return itemkey;
		}

		public void setItemkey(String itemkey) {
			this.itemkey = itemkey;
		}

		public String getItemname() {
			return itemname;
		}

		public void setItemname(String itemname) {
			this.itemname = itemname;
		}

		public String getBindbomcode() {
			return bindbomcode;
		}

		public void setBindbomcode(String bindbomcode) {
			this.bindbomcode = bindbomcode;
		}

		public String getBindtype() {
			return bindtype;
		}

		public void setBindtype(String bindtype) {
			this.bindtype = bindtype;
		}

		public String getBindclass() {
			return bindclass;
		}

		public void setBindclass(String bindclass) {
			this.bindclass = bindclass;
		}

		public String getBindmenu() {
			return bindmenu;
		}

		public void setBindmenu(String bindmenuid) {
			this.bindmenu = bindmenuid;
		}

		public void setRiskVO(RiskVO _riskvo) {
			this.riskvo = _riskvo; //
			repaint(); // �ػ�һ��
		}

		public void setRiskVO(RiskVO _riskvo, String type) {
			this.riskvo = _riskvo; //
			this.type = type;
			repaint(); // �ػ�һ��
		}

		@Override
		public void paint(Graphics g) {// ��sunfujun/20120507/Gw����riskvo���õ���ɫ�Լ���״������ͬʱ�Ż���һ�����꡿
			super.paint(g);
			if (riskvo == null) {
				return;
			}
			// �����յ�...
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// ��Ȧ��Բ
			Dimension d = getSize();
			int li_width = (int) d.getWidth();
			Shape a = null;
			Font font_new = g2.getFont().deriveFont(Font.BOLD); //
			g2.setFont(font_new);// sunfujun/20120607/���ֿ��������ԼӴ�һ��
			// �Ȼ�Ȧ

			if (riskvo.getLevel1RiskCount() > 0) { // ���ص�,����Ȧ
				g2.setColor(riskvo.getColor1_()); //
				// g2.fillOval(li_width - 17, 1, 17, 17); // //
				if ("KRI".equals(type)) {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, 1, shapeWidth, shapeWidth, riskvo.getShape1());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					//g2.drawString("A", TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel1RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel1RiskCount())); //
					String value1 = riskvo.getR_value();

					g2.drawString(riskvo.getR_value(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel1RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel1RiskCount())); //
				} else {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, 1, shapeWidth, shapeWidth, riskvo.getShape1());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					g2.drawString("" + riskvo.getLevel1RiskCount(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel1RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel1RiskCount())); //
				}

			}

			if (riskvo.getLevel2RiskCount() > 0) { // ���ص�,����ɫ��
				g2.setColor(riskvo.getColor2_()); //
				if ("KRI".equals(type)) {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, 1, shapeWidth, shapeWidth, riskvo.getShape1());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					g2.drawString(riskvo.getY_value(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel2RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel2RiskCount())); //
				} else {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, 1, shapeWidth, shapeHeight, riskvo.getShape2());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					g2.drawString("" + riskvo.getLevel2RiskCount(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel2RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel2RiskCount())); //
				}
			}

			if (riskvo.getLevel3RiskCount() > 0) {// �����ǲ���ҲӦ���ж�һ���ǲ����е�1�֣�
				g2.setColor(riskvo.getColor3_()); //
				if ("KRI".equals(type)) {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, 1, shapeWidth, shapeWidth, riskvo.getShape1());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					g2.drawString(riskvo.getG_value(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel3RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel3RiskCount())); //
				} else {
					a = TBUtil.getTBUtil().getShape(li_width - shapeWidth, shapeWidth + 1, shapeWidth, shapeHeight, riskvo.getShape3());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					g2.drawString("" + riskvo.getLevel3RiskCount(), TBUtil.getTBUtil().getStrX(g2, a, "" + riskvo.getLevel3RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskvo.getLevel3RiskCount())); //
				}
			}

		}

		public String getLoadtype() {
			return loadtype;
		}

		public void setLoadtype(String loadtype) {
			this.loadtype = loadtype;
		}

	}

}
