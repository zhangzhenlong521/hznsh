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

	private Object beanInstance = null; //绑定的实例对象,很关键

	private PropertySheetPanel sheet = null;
	private PropertyChangeListener listener;
	private boolean bo_isediteventtrigger = true; //是否是直接在页面上编辑触发变化的!!!因为read数据时也会触发propChange事件,所以需要一个变量来屏蔽此事!!

	private Vector v_editListener = new Vector(); //

	public BillPropPanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
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
			templetItemVOs = templetVO.getItemVos(); //各项..
			str_templetcode = templetVO.getTempletcode();
			initialize(); // 初始化!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void init_2(AbstractTMO _abstractTempletVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			abstractTempletVO = _abstractTempletVO; // 创建方式

			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void initialize() {
		this.setLayout(new BorderLayout()); //
		billPropTable = new BillPropTable(templetVO); //
		billPropTable.setBillPropPanel(this); //在表格中注册面板

		//根据属性模板编码,去数据库中找出对应的数据,应该是一个二维结构.
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

		//使用JavaBean的技术,创建一组属性!!!!
		BaseBeanInfo beanInfo = new BaseBeanInfo(cls); //创建属性对象,其实也就是数据对象!!!
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getPropisshowable().booleanValue()) { //如果定义允许显示!!
				ExtendedPropertyDescriptor propdesc = beanInfo.addProperty(templetItemVOs[i].getItemkey()); //则显示!增加一个属性!!!
				if (templetItemVOs[i].getGrouptitle() != null && !templetItemVOs[i].getGrouptitle().equals("")) {
					propdesc.setCategory(templetItemVOs[i].getGrouptitle()); //组名!!!
				}

				propdesc.setShortDescription("说明:" + templetItemVOs[i].getItemtiptext()); //详细说明!!!
				if (ClientEnvironment.getInstance().isEngligh()) {
					propdesc.setDisplayName(templetItemVOs[i].getItemname_e()); //设置显示的名称
				} else {
					propdesc.setDisplayName(templetItemVOs[i].getItemname()); //设置显示的名称
				}
			}
		}

		sheet.setProperties(beanInfo.getPropertyDescriptors()); //设置属性!!关键!!!就是通过这一行置入数据的!
		//sheet.readFromObject(this.beanInstance); //从实例中读取数据,因为第一次没有赋值,所以总是为空!!!
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Property prop = (Property) evt.getSource();
				try {
					if (bo_isediteventtrigger) {
						prop.writeToObject(beanInstance); //将页面的修改的值保存到对应的实例对象中去!!!
						editChange(prop.getName(), beanInstance); //
						//System.out.println("[" + prop.getName() + "]发生变化(编辑)!!");
					} else {
						//System.out.println("[" + prop.getName() + "]发生变化(赋值)!!");
					}
				} catch (Exception e) {
					e.printStackTrace(); //
				}
			}
		};
		sheet.addPropertySheetChangeListener(listener); //绑定起来!!!!这样只要在页面上修改值后就能将页面上的值自动保存到对应的对象中去!!!
		//		JScrollPane scrollPane = new JScrollPane(); //
		//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //
		//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //
		//		scrollPane.getViewport().add(sheet); //
		this.add(sheet, BorderLayout.CENTER);
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();

		JMenu menu_table_templetmodify = new JMenu("模板编辑");
		//JMenuItem item_table_templetmodify_1 = new JMenuItem("第一种编辑方式");
		JMenuItem item_table_templetmodify_2 = new JMenuItem("第二种编辑方式");
		//menu_table_templetmodify.add(item_table_templetmodify_1);
		menu_table_templetmodify.add(item_table_templetmodify_2);

		menu_table_templetmodify.setPreferredSize(new Dimension(100, 19));
		//item_table_templetmodify_1.setPreferredSize(new Dimension(100, 19));
		item_table_templetmodify_2.setPreferredSize(new Dimension(100, 19));

		//因为目前无法取得某一行的ItemKey,所以暂时先不启用编辑某一项!
		//以后需要重做BillProptable,即完全自己重构一个,然后也同样支持加载公式与编辑公式!!也省去麻烦的一个Bean对象与VO对象的转换! 而也是是直接使用BillVO对象!!! 这样就能与【卡片/列表/树】彻底统一了!!!
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
		int li_selrow = billPropTable.getSelectedRow(); //选中的行!
		if (li_selrow < 0) {
			MessageBox.show(this, "请选择一行进行编辑!"); //
		}
		int li_colcount = billPropTable.getColumnModel().getColumnCount(); //
		MessageBox.show(this, "选中的行号[" + li_selrow + "],总共有[" + li_colcount + "]列!"); //
	}

	/**
	 * 第二种方式编辑元原模板!!!
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
//	 * 判断是否可以配置模板,即如果是Class类型的,则不可配置,如果是XML类型的,则需要复制到DB中进行配置!! 如果是DB的,则分：如果XML中也有,则可以将XML中搞过来比较,也可以直接删除自己而使用XML中的! 如果XML中没有则是以前的逻辑!! 
//	 * @return
//	 */
//	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
//		try {
//			String str_buildFromType = templetVO.getBuildFromType(); //创建的类型!!
//			String str_buildFromInfo = templetVO.getBuildFromInfo(); //创建的信息!!
//			String str_templetCode = templetVO.getTempletcode(); //模板编码
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
	 * 取得Sheet面板..
	 * @return
	 */
	public PropertySheetPanel getSheetPanel() {
		return sheet;
	}

	/**
	 * 取得Sheet面板对应的表格,其实就是JTable的子类!!
	 * @return
	 */
	public PropertySheetTable getSheetTable() {
		return sheet.getTable(); //
	}

	/**
	 * 取得绑定的
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
	 * 重新读取
	 */
	public void reload() {
		bo_isediteventtrigger = false; //在加载数据之前,先屏蔽
		sheet.readFromObject(this.beanInstance);
		bo_isediteventtrigger = true; //恢复开关!!!
	}

	public BillPropTable getBillPropTable() {
		return billPropTable;
	}
}
