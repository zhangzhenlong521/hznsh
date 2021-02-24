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
 * ���Ͳ���,һ��ʼƽ̨�����˼·�ǳ���������ֿؼ�,����������,���Ͳ���,���Ͳ���,�Զ������,����ģ�����,�ļ�ѡ����,office�ؼ���...
 * Ȼ��ÿһ��������һ����ʽ,һ��VO,һ���ؼ�,Ȼ����һ��ʼҲ��Լ��ֻ�м���! 
 * �������������ַ�ʽ��˼·��չ�����ǳ�������,һ�������ؼ�ʱ�ǳ��鷳,Ӱ��һ����ļ�,���ǿؼ���������������ʱҲ�ǳ�������!!�ر����Ǹ�����ģ�����,��������һ��Ѳ���!
 * ����Ӧ�ø��ͨ�õ�,�����в�������һ��HashMap���洢,Ȼ���һ����ʽ,һ��VO,����û�취�Ƕ��! �����Ȼ��������,�ֻ�ʹ��չ������! ��һ�ж���HashMap,����HashVO��˼�����һ������ 
 * �����������,ά����Ȼ��,��һ�ж���HashMap,��չ��Ȼ����!! �Ժ�һ��ҪҪ��סһ��ԭ��,���˷ǲ�������Ҫд������(��������ؼ�),������ʹ��ͨ����,������UI,��ʽ,VO,BS�˵�,һ��Ҫ���ɻ��,����չ��!!!һ��Ҫ�����������!!!
 * ͨ�ù�ʽ�ͽ�getCommUC("���Ͳ���","SQL���","select * from pub_role"),���ݶ���� CommUCDefineVO[ͨ��UI�ؼ��������]
 * ��Ϊ��Ҫ���ݾɵĹ�ʽ����,������ǰ�Ĺ�ʽ��ȥ��������,��VO�ǿ���ɾ����,��ͳͳ���CommUCDefineVO������  
 * ������һ����ʵҲ�ǿ��Խ����оɵĿؼ����幫ʽ��JEP�ж�ͳһ�󶨳�һ��ʵ�ֺ���,������ʱ��Ȼ֧�ֶ������,��ʵ��ʱͳһ��getOldCommUC(),Ȼ�������н�ԭ���ĸ����������߼������һ��������!!!����GetTreeTempletRef()�ͱ��һ��������! ��������ʡ�����!! 
 * ��Ϊ�ڿؼ��д�������������Ĭ��ֵ��,�����ڸ�������ؼ������д��������,�ֱ��Ӧ��������,Ҳ�ֱ���Ĭ��ֵ����
 * @author xch
 *
 */
public class RefDialog_Table extends AbstractRefDialog {
	private static final long serialVersionUID = 1L;

	private String str_datasourcename = null;
	private String[] str_sqls = null;
	private TableRefPanel[] refPanel = null; //���!!
	private TableDataStruct tableDataStruct = null; //

	private String str_ref_pk; //
	private String str_ref_code; //
	private String str_ref_name; //
	private int li_closeType = -1;

	private HashVO returnDataHashVO = null;

	private int li_opentype = -1;

	/**
	 * ��һ�ַ�ʽ,ͨ��һ��SQL����
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _dsname
	 * @param _sql
	 */
	public RefDialog_Table(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, CommUCDefineVO _definevo) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		this.str_datasourcename = _definevo.getConfValue("����Դ"); //����Դ����
		this.str_sqls = getSQLFromConf(_definevo); //����֧�ֶ��SQL!���Թ�ʽ����ʱ��ʹ�á�SQL��䡿��ǰ�,Ȼ���ж��!!!
		li_opentype = 1; //��һ�ִ򿪷�ʽ,��ֱ�ӹ���
	}

	/**
	 * �ڶ��ַ�ʽ,ֱ����������!!
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _vos
	 */
	public RefDialog_Table(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, HashVO[] _vos) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		li_opentype = 2; //��2�ִ򿪷�ʽ,��Ԫԭģ�幹��!!
	}

	public RefDialog_Table(Container _parent, String _title, RefItemVO _refinitvalue, BillPanel _billPanel, TableDataStruct _struct) {
		super(_parent, _title, _refinitvalue, _billPanel); //
		tableDataStruct = _struct; //
		li_opentype = 3; //��2�ִ򿪷�ʽ,��Ԫԭģ�幹��!!
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initialize() {
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " ���ֶ�����$��β���Զ����ء�"); //
		}

		if (li_opentype == 1) {
			init_1(); //��һ�ִ򿪷�ʽ!!
		} else if (li_opentype == 2) {
			init_2(); //�ڶ��ִ򿪷�ʽ!!
		} else if (li_opentype == 3) {
			init_3(); //�ڶ��ִ򿪷�ʽ!!
		}
	}

	/**
	 * ��һ�ִ򿪷�ʽ
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
			tabedPanel.setOpaque(false); //͸��!!
			for (int i = 0; i < str_sqls.length; i++) {
				tabedPanel.addTab(this.getTitle() + "[" + (i + 1) + "]", UIUtil.getImage("office_157.gif"), refPanel[i]); //
			}
			contentPanel.add(tabedPanel, BorderLayout.CENTER); //
		}
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH); //

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(contentPanel); //
		li_maxWidth = li_maxWidth + 50; //���϶����,��Ϊ�ұ��жοհײź�
		if (li_maxWidth < 450) { //������Сֵ
			li_maxWidth = 450;
		}
		if (li_maxWidth > 1000) { //�������ֵ
			li_maxWidth = 1000;
		}

		this.setSize(li_maxWidth, getInitHeight()); //�����´�С!!
		this.locationToCenterPosition(); //����
	}

	/**
	 * �ڶ��ִ򿪷�ʽ
	 */
	private void init_2() {

	}

	/**
	 * ��һ�ִ򿪷�ʽ
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
		li_maxWidth = li_maxWidth + 50; //���϶����,��Ϊ�ұ��жοհײź�
		if (li_maxWidth < 450) { //������Сֵ
			li_maxWidth = 450;
		}
		if (li_maxWidth > 1000) { //�������ֵ
			li_maxWidth = 1000;
		}
		this.setSize(li_maxWidth, getInitHeight()); //�����´�С!!
		this.locationToCenterPosition(); //����
	}

	/**
	 * �ҳ���SQL���Ϊǰ�
	 * @param _definevo
	 * @return
	 */
	private String[] getSQLFromConf(CommUCDefineVO _definevo) {
		String[] str_keys = _definevo.getAllConfKeys("SQL���", true); //��SQL��俪�ص�!!
		String[] str_values = new String[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) {
			str_values[i] = _definevo.getConfValue(str_keys[i]); // 
		}
		return str_values; //
	}

	/**
	 * ��ʼ���
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
		panel.setOpaque(false); //͸��!!!

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onShowSQL();
				}
			}
		});

		JButton btn_1 = new WLTButton(UIUtil.getLanguage("ȷ��"));
		btn_1.setPreferredSize(new Dimension(85, 20));
		btn_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		JButton btn_3 = new WLTButton(UIUtil.getLanguage("ȡ��"));
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
	 * ȷ��
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
					str_ref_pk = (str_ref_pk == null ? "" : str_ref_pk) + "��" + vos[i].getId() + "��"; //
					str_ref_code = (str_ref_code == null ? "" : str_ref_code) + "��" + vos[i].getCode() + "��"; //
					str_ref_name = (str_ref_name == null ? "" : str_ref_name) + "��" + vos[i].getName() + "��"; //
				}
			}
		}
		setCloseType(1); //
		this.dispose();
	}

	/**
	 * �ر�
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
