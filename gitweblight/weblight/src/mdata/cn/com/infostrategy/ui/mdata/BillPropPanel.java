/**
 * L2FProd.com Common Components 7.3 License.
 *
 * Copyright 2005-2007 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;

/**
 * PropertySheetPage. <br>
 *  
 */
public class BillPropPanel extends BillPanel {
	private static final long serialVersionUID = 4072869741004323875L;

	private Pub_Templet_1VO templetVO = null;
	private BillPropTable billPropTable = null;
	private Pub_Templet_1_ItemVO[] templetItemVOs = null;
	private String str_templetcode = null;

	private ServerTMODefine serverTMO = null; //
	private AbstractTMO abstractTempletVO = null;

	private Object beanInstance = null; //�󶨵�ʵ������,�ܹؼ�

	private PropertySheetPanel sheet = null;
	private PropertyChangeListener listener;
	private boolean bo_isediteventtrigger = true; //�Ƿ���ֱ����ҳ���ϱ༭�����仯��!!!��Ϊread����ʱҲ�ᴥ��propChange�¼�,������Ҫһ�����������δ���!!

	private Vector v_editListener = new Vector(); //

	public BillPropPanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	public BillPropPanel(ServerTMODefine _serverTMO) {
		try {
			this.serverTMO = _serverTMO; //
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); //����..
			str_templetcode = templetVO.getTempletcode();
			initialize(); // ��ʼ��!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void init_2(AbstractTMO _abstractTempletVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode();
			abstractTempletVO = _abstractTempletVO; // ������ʽ

			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void initialize() {
		this.setLayout(new BorderLayout()); //
		billPropTable = new BillPropTable(templetVO); //
		billPropTable.setBillPropPanel(this); //�ڱ����ע�����

		//��������ģ�����,ȥ���ݿ����ҳ���Ӧ������,Ӧ����һ����ά�ṹ.
		sheet = new PropertySheetPanel(billPropTable); //
		sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES); //
		sheet.setDescriptionVisible(true);
		//sheet.setSortingCategories(true);
		//sheet.setSortingProperties(true);
		//sheet.setRestoreToggleStates(true);
		sheet.setToolBarVisible(false);
		sheet.getTable().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showPopMenu((JComponent) e.getSource(), e.getX(), e.getY()); //
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showPopMenu((JComponent) e.getSource(), e.getX(), e.getY()); //
				}
			}
		});

		Class cls = null;
		try {
			cls = Class.forName(templetVO.getPropbeanclassname()); //
			this.beanInstance = cls.newInstance(); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		//ʹ��JavaBean�ļ���,����һ������!!!!
		BaseBeanInfo beanInfo = new BaseBeanInfo(cls); //�������Զ���,��ʵҲ�������ݶ���!!!
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getPropisshowable().booleanValue()) { //�������������ʾ!!
				ExtendedPropertyDescriptor propdesc = beanInfo.addProperty(templetItemVOs[i].getItemkey()); //����ʾ!����һ������!!!
				if (templetItemVOs[i].getGrouptitle() != null && !templetItemVOs[i].getGrouptitle().equals("")) {
					propdesc.setCategory(templetItemVOs[i].getGrouptitle()); //����!!!
				}

				propdesc.setShortDescription("˵��:" + templetItemVOs[i].getItemtiptext()); //��ϸ˵��!!!
				if (ClientEnvironment.getInstance().isEngligh()) {
					propdesc.setDisplayName(templetItemVOs[i].getItemname_e()); //������ʾ������
				} else {
					propdesc.setDisplayName(templetItemVOs[i].getItemname()); //������ʾ������
				}
			}
		}

		sheet.setProperties(beanInfo.getPropertyDescriptors()); //��������!!�ؼ�!!!����ͨ����һ���������ݵ�!
		//sheet.readFromObject(this.beanInstance); //��ʵ���ж�ȡ����,��Ϊ��һ��û�и�ֵ,��������Ϊ��!!!
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Property prop = (Property) evt.getSource();
				try {
					if (bo_isediteventtrigger) {
						prop.writeToObject(beanInstance); //��ҳ����޸ĵ�ֵ���浽��Ӧ��ʵ��������ȥ!!!
						editChange(prop.getName(), beanInstance); //
						//System.out.println("[" + prop.getName() + "]�����仯(�༭)!!");
					} else {
						//System.out.println("[" + prop.getName() + "]�����仯(��ֵ)!!");
					}
				} catch (Exception e) {
					e.printStackTrace(); //
				}
			}
		};
		sheet.addPropertySheetChangeListener(listener); //������!!!!����ֻҪ��ҳ�����޸�ֵ����ܽ�ҳ���ϵ�ֵ�Զ����浽��Ӧ�Ķ�����ȥ!!!
		//		JScrollPane scrollPane = new JScrollPane(); //
		//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //
		//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //
		//		scrollPane.getViewport().add(sheet); //
		this.add(sheet, BorderLayout.CENTER);
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();

		JMenu menu_table_templetmodify = new JMenu("ģ��༭");
		//JMenuItem item_table_templetmodify_1 = new JMenuItem("��һ�ֱ༭��ʽ");
		JMenuItem item_table_templetmodify_2 = new JMenuItem("�ڶ��ֱ༭��ʽ");
		//menu_table_templetmodify.add(item_table_templetmodify_1);
		menu_table_templetmodify.add(item_table_templetmodify_2);

		menu_table_templetmodify.setPreferredSize(new Dimension(100, 19));
		//item_table_templetmodify_1.setPreferredSize(new Dimension(100, 19));
		item_table_templetmodify_2.setPreferredSize(new Dimension(100, 19));

		//��ΪĿǰ�޷�ȡ��ĳһ�е�ItemKey,������ʱ�Ȳ����ñ༭ĳһ��!
		//�Ժ���Ҫ����BillProptable,����ȫ�Լ��ع�һ��,Ȼ��Ҳͬ��֧�ּ��ع�ʽ��༭��ʽ!!Ҳʡȥ�鷳��һ��Bean������VO�����ת��! ��Ҳ����ֱ��ʹ��BillVO����!!! ���������롾��Ƭ/�б�/��������ͳһ��!!!
		//		item_table_templetmodify_1.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				modifyTemplet1(); //
		//			}
		//		});

		item_table_templetmodify_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyTemplet2(templetVO.getTempletcode());
			}
		});

		popmenu_header.add(menu_table_templetmodify); //
		popmenu_header.show(_compent, _x, _y); //
	}

	private void modifyTemplet1() {
		int li_selrow = billPropTable.getSelectedRow(); //ѡ�е���!
		if (li_selrow < 0) {
			MessageBox.show(this, "��ѡ��һ�н��б༭!"); //
		}
		int li_colcount = billPropTable.getColumnModel().getColumnCount(); //
		MessageBox.show(this, "ѡ�е��к�[" + li_selrow + "],�ܹ���[" + li_colcount + "]��!"); //
	}

	/**
	 * �ڶ��ַ�ʽ�༭Ԫԭģ��!!!
	 * @param _templetCode
	 */
	private void modifyTemplet2(String _templetCode) {
		try{
			new MetaDataUIUtil().modifyTemplet2(this, this.templetVO.getBuildFromType(), this.templetVO.getBuildFromInfo(), this.templetVO.getTempletcode(), this.templetVO.getTempletname(), false, null);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

//	/**
//	 * �ж��Ƿ��������ģ��,�������Class���͵�,�򲻿�����,�����XML���͵�,����Ҫ���Ƶ�DB�н�������!! �����DB��,��֣����XML��Ҳ��,����Խ�XML�и�����Ƚ�,Ҳ����ֱ��ɾ���Լ���ʹ��XML�е�! ���XML��û��������ǰ���߼�!! 
//	 * @return
//	 */
//	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
//		try {
//			String str_buildFromType = templetVO.getBuildFromType(); //����������!!
//			String str_buildFromInfo = templetVO.getBuildFromInfo(); //��������Ϣ!!
//			String str_templetCode = templetVO.getTempletcode(); //ģ�����
//			String str_templetName = templetVO.getTempletname();
//			return new MetaDataUIUtil().checkTempletIsCanConfig(this, str_buildFromType, str_buildFromInfo, str_templetCode, str_templetName, _isquiet); //
//		} catch (Exception ex) {
//			MessageBox.showException(this, ex); //
//			return false; //
//		}
//	}

	public void stopEditing() {
		billPropTable.stopEditing(); //
	}

	/**
	 * ȡ��Sheet���..
	 * @return
	 */
	public PropertySheetPanel getSheetPanel() {
		return sheet;
	}

	/**
	 * ȡ��Sheet����Ӧ�ı��,��ʵ����JTable������!!
	 * @return
	 */
	public PropertySheetTable getSheetTable() {
		return sheet.getTable(); //
	}

	/**
	 * ȡ�ð󶨵�
	 * @return
	 */
	public Object getBeanInstance() {
		return this.beanInstance;
	}

	private void editChange(String _itemkey, Object _bean) {
		for (int i = 0; i < v_editListener.size(); i++) {
			BillPropEditListener listener = (BillPropEditListener) v_editListener.get(i); //
			listener.onBillPropValueChanged(new BillPropEditEvent(_itemkey, _bean, this)); //
		}
	}

	public void addBillPropEditListener(BillPropEditListener _listener) {
		v_editListener.add(_listener); //
	}

	/**
	 * ���¶�ȡ
	 */
	public void reload() {
		bo_isediteventtrigger = false; //�ڼ�������֮ǰ,������
		sheet.readFromObject(this.beanInstance);
		bo_isediteventtrigger = true; //�ָ�����!!!
	}

	public BillPropTable getBillPropTable() {
		return billPropTable;
	}
}
