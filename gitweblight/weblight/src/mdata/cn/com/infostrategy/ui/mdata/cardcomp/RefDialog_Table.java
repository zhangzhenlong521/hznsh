/**************************************************************************
 * $RCSfile: RefDialog_Table.java,v $  $Revision: 1.15 $  $Date: 2012/12/11 09:57:54 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 表型参照,一开始平台的设计思路是抽象出若干种控件,比如下拉框,表型参照,树型参照,自定义参照,树型模板参照,文件选择框等,office控件等...
 * 然后每一个都定义一个公式,一个VO,一个控件,然后定义一开始也是约定只有几个! 
 * 但后来发现这种方式与思路扩展起来非常不方便,一是新增控件时非常麻烦,影响一大堆文件,二是控件定义中新增参数时也非常不方便!!特别是那个树型模板参照,后来加了一大堆参数!
 * 所以应该搞成通用的,即所有参数都用一个HashMap来存储,然后就一个公式,一个VO,控制没办法是多个! 这样既会少许多类,又会使扩展更容易! 即一切都是HashMap,这与HashVO的思想如出一撤！！ 
 * 如果代码量少,维护必然简单,而一切都是HashMap,扩展必然方便!! 以后一定要要记住一个原则,除了非不得已需要写多个类的(比如各个控件),尽可能使用通用类,无论是UI,公式,VO,BS端等,一定要做成活的,可扩展的!!!一定要控制类的数量!!!
 * 通用公式就叫getCommUC("表型参照","SQL语句","select * from pub_role"),数据对象叫 CommUCDefineVO[通用UI控件定义对象]
 * 因为需要兼容旧的公式定义,所以以前的公式是去不掉的了,但VO是可以删除的,即统统变成CommUCDefineVO！！！  
 * 但后来一想其实也是可以将所有旧的控件定义公式在JEP中都统一绑定成一个实现函数,即定义时仍然支持多个函数,但实现时统一叫getOldCommUC(),然后在其中将原来的各个函数的逻辑都变成一个个方法!!!比如GetTreeTempletRef()就变成一个方法了! 这样就能省许多类!! 
 * 因为在控件中大量参数都是有默认值的,所以在各个具体控件可以有大量类变量,分别对应各个参数,也分别有默认值！！
 * @author xch
 *
 */
public class RefDialog_Table extends AbstractRefDialog {
	private static final long serialVersionUID = 1L;

	private String str_datasourcename = null;
	private String[] str_sqls = null;
	private TableRefPanel[] refPanel = null; //面板!!
	private TableDataStruct tableDataStruct = null; //

	private String str_ref_pk; //
	private String str_ref_code; //
	private String str_ref_name; //
	private int li_closeType = -1;

	private HashVO returnDataHashVO = null;

	private int li_opentype = -1;

	/**
	 * 第一种方式,通过一个SQL带入
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _dsname
	 * @param _sql
	 */
	public RefDialog_Table(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, CommUCDefineVO _definevo) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		this.str_datasourcename = _definevo.getConfValue("数据源"); //数据源名称
		this.str_sqls = getSQLFromConf(_definevo); //后来支持多个SQL!所以公式定义时是使用【SQL语句】做前辍,然后有多个!!!
		li_opentype = 1; //第一种打开方式,即直接构造
	}

	/**
	 * 第二种方式,直接送入数据!!
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _vos
	 */
	public RefDialog_Table(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, HashVO[] _vos) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		li_opentype = 2; //第2种打开方式,从元原模板构造!!
	}

	public RefDialog_Table(Container _parent, String _title, RefItemVO _refinitvalue, BillPanel _billPanel, TableDataStruct _struct) {
		super(_parent, _title, _refinitvalue, _billPanel); //
		tableDataStruct = _struct; //
		li_opentype = 3; //第2种打开方式,从元原模板构造!!
	}

	/**
	 * 初始化页面
	 */
	public void initialize() {
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " 【字段名以$结尾则自动隐藏】"); //
		}

		if (li_opentype == 1) {
			init_1(); //第一种打开方式!!
		} else if (li_opentype == 2) {
			init_2(); //第二种打开方式!!
		} else if (li_opentype == 3) {
			init_3(); //第二种打开方式!!
		}
	}

	/**
	 * 第一种打开方式
	 */
	private void init_1() {
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		refPanel = new TableRefPanel[str_sqls.length]; //
		int li_maxWidth = 0; //
		for (int i = 0; i < str_sqls.length; i++) {
			refPanel[i] = new TableRefPanel(this.str_datasourcename, str_sqls[i], getInitRefItemVO());
			if (refPanel[i].getAllWidth() > li_maxWidth) {
				li_maxWidth = refPanel[i].getAllWidth(); //
			}
		}

		if (str_sqls.length == 1) {
			contentPanel.add(refPanel[0], BorderLayout.CENTER); //
		} else {
			JTabbedPane tabedPanel = new JTabbedPane(); //
			tabedPanel.setOpaque(false); //透明!!
			for (int i = 0; i < str_sqls.length; i++) {
				tabedPanel.addTab(this.getTitle() + "[" + (i + 1) + "]", UIUtil.getImage("office_157.gif"), refPanel[i]); //
			}
			contentPanel.add(tabedPanel, BorderLayout.CENTER); //
		}
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH); //

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(contentPanel); //
		li_maxWidth = li_maxWidth + 50; //加上多余的,因为右边有段空白才好
		if (li_maxWidth < 450) { //限制最小值
			li_maxWidth = 450;
		}
		if (li_maxWidth > 1000) { //限制最大值
			li_maxWidth = 1000;
		}

		this.setSize(li_maxWidth, getInitHeight()); //重置下大小!!
		this.locationToCenterPosition(); //居中
	}

	/**
	 * 第二种打开方式
	 */
	private void init_2() {

	}

	/**
	 * 第一种打开方式
	 */
	private void init_3() {
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		refPanel = new TableRefPanel[1]; //
		refPanel[0] = new TableRefPanel(this.str_datasourcename, tableDataStruct, getInitRefItemVO());
		contentPanel.add(refPanel[0], BorderLayout.CENTER); //
		int li_maxWidth = refPanel[0].getAllWidth(); //
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(contentPanel); //
		li_maxWidth = li_maxWidth + 50; //加上多余的,因为右边有段空白才好
		if (li_maxWidth < 450) { //限制最小值
			li_maxWidth = 450;
		}
		if (li_maxWidth > 1000) { //限制最大值
			li_maxWidth = 1000;
		}
		this.setSize(li_maxWidth, getInitHeight()); //重置下大小!!
		this.locationToCenterPosition(); //居中
	}

	/**
	 * 找出以SQL语句为前辍
	 * @param _definevo
	 * @return
	 */
	private String[] getSQLFromConf(CommUCDefineVO _definevo) {
		String[] str_keys = _definevo.getAllConfKeys("SQL语句", true); //以SQL语句开关的!!
		String[] str_values = new String[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) {
			str_values[i] = _definevo.getConfValue(str_keys[i]); // 
		}
		return str_values; //
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 400;
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setOpaque(false); //透明!!!

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onShowSQL();
				}
			}
		});

		JButton btn_1 = new WLTButton(UIUtil.getLanguage("确定"));
		btn_1.setPreferredSize(new Dimension(85, 20));
		btn_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		JButton btn_3 = new WLTButton(UIUtil.getLanguage("取消"));
		btn_3.setPreferredSize(new Dimension(85, 20));
		btn_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});

		panel.add(btn_1); //
		panel.add(btn_3); //
		return panel;
	}

	/**
	 * 确定
	 * 
	 */
	public void onConfirm() {
		RefItemVO[] vos = new RefItemVO[refPanel.length]; //
		for (int i = 0; i < refPanel.length; i++) {
			vos[i] = refPanel[i].getSelectVO(); //
		}

		if (vos[0] == null) {
			MessageBox.showSelectOne(this); //
			return;
		}

		if (vos.length == 1) { //
			str_ref_pk = vos[0].getId(); //
			str_ref_code = vos[0].getCode(); //
			str_ref_name = vos[0].getName(); //
			returnDataHashVO = vos[0].getHashVO(); //
		} else { //
			for (int i = 0; i < vos.length; i++) {
				if (vos[i] != null) {
					str_ref_pk = (str_ref_pk == null ? "" : str_ref_pk) + "【" + vos[i].getId() + "】"; //
					str_ref_code = (str_ref_code == null ? "" : str_ref_code) + "【" + vos[i].getCode() + "】"; //
					str_ref_name = (str_ref_name == null ? "" : str_ref_name) + "【" + vos[i].getName() + "】"; //
				}
			}
		}
		setCloseType(1); //
		this.dispose();
	}

	/**
	 * 关闭
	 */
	protected void onClose() {
		setCloseType(2);
		this.dispose();
	}

	private void onShowSQL() {
		//System.out.println(this.str_sql);
		//MessageBox.showTextArea(this, this.str_sql);
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_ref_pk, str_ref_code, str_ref_name, returnDataHashVO);
	}

}
