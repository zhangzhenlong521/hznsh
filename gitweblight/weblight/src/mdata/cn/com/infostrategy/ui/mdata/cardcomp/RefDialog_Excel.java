/**************************************************************************
 * $RCSfile: RefDialog_Excel.java,v $  $Revision: 1.14 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.OneSQLBillListConfirmDialog;
import cn.com.infostrategy.ui.mdata.UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.IExcelRefDialogInitIfc;

/**
 * Excel控件,就是弹出仿照Excel那个表格,即可以行与列合并的!!
 * @author xch
 *
 */
public class RefDialog_Excel extends AbstractRefDialog {

	private static final long serialVersionUID = 7240993480143688006L;

	private BillPanel billPanelFrom = null; //
	private UIRefPanel refPanel = null; //
	private CommUCDefineVO dfvo = null; //
	private BillCellPanel billCellPanel = null; //

	private RefItemVO currRefItemVO = null; //
	private String excelcode = null; //Excel模板编码..
	private String afterinitifc = null; //初始化方法
	private String custBtnClassName = null; //按钮自定义面板
	private String returnCellKey = null; //返回的Cell格子的cellKey,即可以直接将Excel中某一个值直接返回参照控件中!
	private String initSql = null;//初始化sql根据这个sql过滤可以选择的模板
	private String returnsCellKey = null;//返回的cellKey,与returnCellKey类似,可以放多个分号分隔。
	private String realValue = null;
	private HashMap paramMap = null;
	private WLTTabbedPane mainTab = null;
	private List cellPanels = new ArrayList();

	//武坤萌添加
	private String[] key = null;
	private List checkList = null;

	public RefDialog_Excel(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, value, panel);
		this.billPanelFrom = panel; //
		if (value != null) {
			realValue = value.getId();
		}
		this.refPanel = (UIRefPanel) _parent; //
		this.dfvo = _dfvo; //
	}

	public void initialize() {
		if (ClientEnvironment.getInstance().isAdmin()) {
			this.setTitle(this.getTitle() + " [强烈提醒:请千万注意在需要保存的格子中设置CellKey!!!!!(本信息只对admin=Y提醒)]"); //
		}
		//模板编码,初始化方法,按钮自定义面板,返回显示值
		if (dfvo != null) {
			this.excelcode = dfvo.getConfValue("模板编码"); //
			this.afterinitifc = dfvo.getConfValue("初始化方法"); //初始化方法
			this.custBtnClassName = dfvo.getConfValue("按钮自定义面板"); //按钮自定义面板
			this.returnCellKey = dfvo.getConfValue("返回显示值"); //返回显示值
			this.returnsCellKey = dfvo.getConfValue("返回隐藏值"); //返回隐藏值
			this.initSql = dfvo.getConfValue("初始化sql"); //
		}

		if (excelcode == null || "".equals(excelcode)) {
			if (realValue != null && realValue.indexOf("*") > 0) {
				excelcode = getRealModelNO(realValue);
			}
		}

		if (excelcode == null || "".equals(excelcode)) {
			StringBuffer qsql = new StringBuffer(" select templetcode 编码,templetname 名称 from PUB_BILLCELLTEMPLET_H where 1=1  ");
			if (initSql != null && !"null".equals(initSql) && !"".equals(initSql)) {
				qsql.append(" and " + initSql);
			}
			OneSQLBillListConfirmDialog bd = new OneSQLBillListConfirmDialog(this, qsql.toString(), "请选择需要的EXCEL模板", 600, 400, true, true);
			bd.setVisible(true);
			if (bd.getCloseType() == BillDialog.CONFIRM) {
				BillVO[] re = bd.getSelectedBillVOs();
				if (re != null && re.length > 0) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < re.length; i++) {
						sb.append(re[i].getRealValue("编码") + ";");
					}
					excelcode = sb.toString();
				}
			} else {
				this.setShowAfterInitialize(false);
			}
		}

		if (excelcode != null && !"".equals(excelcode)) {
			this.getContentPane().setLayout(new BorderLayout()); ////
			String[] codes = excelcode.split(";");
			mainTab = new WLTTabbedPane();
			billCellPanel = new BillCellPanel(codes[0]);
			int li_allwidth = 0; //
			int li_allbtnwidth = 20;
			int li_allhight = 0;
			for (int i = 0; i < codes.length; i++) {
				BillCellPanel billCellPanel_ = new BillCellPanel(codes[i]); ////
				billCellPanel_.setToolBarVisiable(false); ////
				mainTab.addTab(codes[i], billCellPanel_);
				li_allbtnwidth = li_allbtnwidth + (int) mainTab.getJButtonAt(i).getPreferredSize().getWidth();
				cellPanels.add(billCellPanel_);
				li_allwidth = li_allwidth > ((int) billCellPanel_.getTableAllWidth() + 100) ? li_allwidth : ((int) billCellPanel_.getTableAllWidth() + 100); //
				li_allhight = li_allhight > ((int) billCellPanel_.getTable().getPreferredSize().getHeight() + 120) ? li_allhight : ((int) billCellPanel_.getTable().getPreferredSize().getHeight() + 120);
			}
			this.add(mainTab, BorderLayout.CENTER); //
			this.add(getSouthBtnPanel(), BorderLayout.SOUTH); //

			if (li_allbtnwidth > li_allwidth) {
				li_allwidth = li_allbtnwidth;
			}
			int screen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(); //
			int screen_hight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			if (li_allwidth >= screen_width) {
				li_allwidth = screen_width;
			}
			if (li_allhight >= (screen_hight - 60)) {
				li_allhight = screen_hight - 60;
			}
			this.setSize(li_allwidth, li_allhight);
			this.setLocation((screen_width - li_allwidth) / 2, (screen_hight - li_allhight) / 2);

			RefItemVO initRefItemVO = getInitRefItemVO(); //

			if (initRefItemVO != null && initRefItemVO.getId() != null && !initRefItemVO.getId().trim().equals("")) {
				try {
					String str_billno = initRefItemVO.getId(); //
					String realbillno = getRealBillNO(str_billno);
					String[] realbillnos = realbillno.split(";");
					if (realbillnos != null && realbillnos.length > 0) {
						for (int i = 0; i < realbillnos.length; i++) {
							if (i < cellPanels.size()) {
								((BillCellPanel) cellPanels.get(i)).loadBillData(realbillnos[i]); ////
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}

			//初始化上方按钮栏..
			if (custBtnClassName != null && !custBtnClassName.trim().equals("")) {
				try {
					AbstractRefDialogCustBtnPanel custPanel = (AbstractRefDialogCustBtnPanel) Class.forName(custBtnClassName).newInstance(); //
					custPanel.setBillPanel(billPanelFrom); //
					custPanel.setRefDiaglog(this);
					custPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); //
					custPanel.initialize(); //
					this.add(custPanel, BorderLayout.NORTH); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}

			//执行初始化拦截器
			if (afterinitifc != null && !afterinitifc.trim().equals("")) {
				try {
					IExcelRefDialogInitIfc ifc = (IExcelRefDialogInitIfc) Class.forName(afterinitifc).newInstance(); //
					ifc.afterInitExcelRefDialog(billCellPanel, getBillPanel()); //
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (billPanelFrom instanceof BillCardPanel) {
				if (((BillCardPanel) billPanelFrom).getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
					if (cellPanels != null && cellPanels.size() > 0) {
						for (int j = 0; j < cellPanels.size(); j++) {
							((BillCellPanel) cellPanels.get(j)).setEditable(false);
						}
					}
				}
			}
		} else {
			this.getContentPane().setLayout(new BorderLayout()); ////
			WLTPanel wp = new WLTPanel();
			WLTLabel j = new WLTLabel("请选择EXCEL模板！");
			j.setOpaque(false);
			wp.add(j);
			wp.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //查询面板底色
			wp.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, false)); //
			this.add(wp);
		}

	}

	private JPanel getSouthBtnPanel() {
		JButton btn_ok = new JButton("确定");
		//JButton btn_save = new JButton("保存");
		JButton btn_cancel = new JButton("取消");

		btn_ok.setPreferredSize(new Dimension(75, 20));
		//btn_save.setPreferredSize(new Dimension(75, 20));
		btn_cancel.setPreferredSize(new Dimension(75, 20));

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		//		btn_save.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				onSave();
		//			}
		//		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		JPanel p_btn = new JPanel();
		p_btn.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2)); //
		p_btn.add(btn_ok);
		//p_btn.add(btn_save);
		p_btn.add(btn_cancel);

		if (billPanelFrom instanceof BillCardPanel) {
			if (((BillCardPanel) billPanelFrom).getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
				btn_ok.setEnabled(false);
				//btn_save.setEnabled(false);
			}
		}

		return p_btn;
	}

	public int getInitWidth() {
		return 900;
	}

	public int getInitHeight() {
		return 555;
	}

	/**
	 * 
	 */
	protected void onConfirm() {
		if (cellPanels != null && cellPanels.size() > 0) {
			for (int j = 0; j < cellPanels.size(); j++) {
				((BillCellPanel) cellPanels.get(j)).stopEditing();
			}
		}
		try {
			RefItemVO initRefItemVO = getInitRefItemVO(); //
			String str_billbo = null; //
			if (initRefItemVO == null || initRefItemVO.getId() == null || initRefItemVO.getId().trim().equals("")) { //如果值为空，则要创建
				str_billbo = getBillNO(); //
			} else {
				str_billbo = initRefItemVO.getId(); //
			}

			if (str_billbo == null) {
				throw new WLTAppException("取得BillNo为空!"); //
			}

			if (this.excelcode == null || this.excelcode.trim().equals("")) { //没有定义模板则需要存储选择的模板
				if (str_billbo.indexOf("*") > 0) {
					String str_prefix = str_billbo.substring(0, str_billbo.indexOf("*")); //
					str_billbo = str_prefix + "*" + excelcode; //
				} else {
					str_billbo = str_billbo + "*" + excelcode; //
				}
			}

			if (returnCellKey != null) {
				String str_cellValue = "";
				if (cellPanels != null && cellPanels.size() > 0) {
					for (int j = 0; j < cellPanels.size(); j++) {
						String str_cellValues = ((BillCellPanel) cellPanels.get(j)).getValueAt(this.returnCellKey);
						if (str_cellValues != null) {
							str_cellValue = str_cellValues;
						}
					}
				}
				if (str_billbo.indexOf("#") > 0) {
					String str_prefix = str_billbo.substring(0, str_billbo.indexOf("#")); //
					str_billbo = str_prefix + "#" + (str_cellValue == null ? "" : str_cellValue); //
				} else {
					str_billbo = str_billbo + "#" + (str_cellValue == null ? "" : str_cellValue); //
				}
			}

			String realbillno = getRealBillNO(str_billbo);
			String[] realbillnos = realbillno.split(";");
			if (realbillnos != null && realbillnos.length > 0) {
				for (int i = 0; i < realbillnos.length; i++) {
					if (i < cellPanels.size()) {
						((BillCellPanel) cellPanels.get(i)).SaveBillData(realbillnos[i]); ////
					}
				}
			}
			HashVO hsvo = new HashVO();
			hsvo.setAttributeValue("id", str_billbo);
			hsvo.setAttributeValue("code", null);
			hsvo.setAttributeValue("name", getName(str_billbo));
			if (returnsCellKey != null) {
				String[] keys = returnsCellKey.split(",");
				String str_cellValue = null;
				for (int i = 0; i < keys.length; i++) {
					if (cellPanels != null && cellPanels.size() > 0) {
						for (int j = 0; j < cellPanels.size(); j++) {
							str_cellValue = ((BillCellPanel) cellPanels.get(j)).getValueAt(keys[i]);
							if (str_cellValue != null) {
								hsvo.setAttributeValue(keys[i], str_cellValue);
							}
						}
					}
				}
			}
			currRefItemVO = new RefItemVO(hsvo); //
			if (ClientEnvironment.getInstance().isAdmin()) {
				MessageBox.show(this, "保存数据成功,但不一定你编辑的数据全部会保存,只有设置了CellKey的值才会真正保存!!请千万注意这一点!!(本信息只对admin=Y提醒)");
			}

			setCloseType(1);
			this.dispose();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 
	 */
	protected void onSave() {
		if (cellPanels != null && cellPanels.size() > 0) {
			for (int j = 0; j < cellPanels.size(); j++) {
				((BillCellPanel) cellPanels.get(j)).stopEditing();
			}
		}
		try {
			RefItemVO initRefItemVO = getInitRefItemVO(); //
			String str_billbo = null; //
			if (initRefItemVO == null || initRefItemVO.getId() == null || initRefItemVO.getId().trim().equals("")) { //如果值为空，则要创建
				str_billbo = getBillNO(); //
			} else {
				str_billbo = initRefItemVO.getId(); //
			}

			if (str_billbo == null) {
				throw new WLTAppException("取得BillNo为空!"); //
			}

			if (this.excelcode == null || excelcode.trim().equals("")) {//没有定义模板则需要存储选择的模板
				if (str_billbo.indexOf("*") > 0) {
					String str_prefix = str_billbo.substring(0, str_billbo.indexOf("*")); //
					str_billbo = str_prefix + "*" + excelcode; //
				} else {
					str_billbo = str_billbo + "*" + excelcode; //
				}
			}

			if (this.returnCellKey != null) {
				String str_cellValue = "";
				if (cellPanels != null && cellPanels.size() > 0) {
					for (int j = 0; j < cellPanels.size(); j++) {
						String str_cellValues = ((BillCellPanel) cellPanels.get(j)).getValueAt(this.returnCellKey);
						if (str_cellValues != null) {
							str_cellValue = str_cellValues;
						}
					}
				}
				if (str_billbo.indexOf("#") > 0) {
					String str_prefix = str_billbo.substring(0, str_billbo.indexOf("#")); //
					str_billbo = str_prefix + "#" + (str_cellValue == null ? "" : str_cellValue); //
				} else {
					str_billbo = str_billbo + "#" + (str_cellValue == null ? "" : str_cellValue); //
				}
			}

			String realbillno = getRealBillNO(str_billbo);
			String[] realbillnos = realbillno.split(";");
			if (realbillnos != null && realbillnos.length > 0) {
				for (int i = 0; i < realbillnos.length; i++) {
					if (i < cellPanels.size()) {
						((BillCellPanel) cellPanels.get(i)).SaveBillData(realbillnos[i]); ////
					}
				}
			}
			HashVO hsvo = new HashVO();
			hsvo.setAttributeValue("id", str_billbo);
			hsvo.setAttributeValue("code", null);
			hsvo.setAttributeValue("name", getName(str_billbo));
			if (returnsCellKey != null) {
				String[] keys = returnsCellKey.split(",");
				String str_cellValue = null;
				for (int i = 0; i < keys.length; i++) {
					if (cellPanels != null && cellPanels.size() > 0) {
						for (int j = 0; j < cellPanels.size(); j++) {
							str_cellValue = ((BillCellPanel) cellPanels.get(j)).getValueAt(keys[i]);
							if (str_cellValue != null) {
								hsvo.setAttributeValue(keys[i], str_cellValue);
							}
						}
					}
				}
			}
			currRefItemVO = new RefItemVO(hsvo); //

			if (ClientEnvironment.getInstance().isAdmin()) {
				MessageBox.show(this, "保存数据成功,但不一定你编辑的数据全部会保存,只有设置了CellKey的值才会真正保存!!请千万注意这一点!!(本信息只对admin=Y提醒)");
			}

			setCloseType(1);
			this.dispose();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 武坤萌
	 * 反洗钱表格填写检查
	 * @param String[] key
	 */
	public void setCheckKeys(String[] key) {
		this.key = key;
	}

	public void setCheckKeysOrder(List list) {
		this.checkList = list;
	}

	private String getRealBillNO(String billno) {
		String str_Rt = null;
		if (billno.indexOf("*") > 0) {
			str_Rt = billno.substring(0, billno.indexOf("*")); //
		} else if (billno.indexOf("#") > 0) {
			str_Rt = billno.substring(0, billno.indexOf("#")); //
		} else {
			str_Rt = billno;
		}
		return str_Rt;
	}

	private String getRealModelNO(String billno) {
		String str_Rt = null;
		if (billno.indexOf("*") > 0) {
			str_Rt = billno.substring(billno.indexOf("*") + 1, billno.length()); //
			if (str_Rt.indexOf("#") > 0) {
				str_Rt = str_Rt.substring(0, str_Rt.indexOf("#")); //
			}
		} else {
		}
		return str_Rt;
	}

	private String getName(String billno) {
		String str_Rt = null;
		if (billno.indexOf("#") > 0) {
			str_Rt = billno.substring(billno.indexOf("#") + 1, billno.length()); //
			if (str_Rt.indexOf("*") > 0) {
				str_Rt = str_Rt.substring(0, str_Rt.indexOf("*")); //
			}
		} else {
			str_Rt = "点击按钮查看数据";
		}
		return str_Rt;
	}

	private String getBillNO() {
		BillPanel billPanel = getBillPanel(); //
		String begin = "";
		if (billPanel instanceof BillCardPanel) {
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			begin = cardPanel.getTempletVO().getSavedtablename() + "_" + refPanel.getItemKey() + "_" + cardPanel.getRealValueAt(cardPanel.getTempletVO().getPkname()) + "_";

		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel; //
			begin = listPanel.getTempletVO().getSavedtablename() + "_" + refPanel.getItemKey() + "_" + listPanel.getValueAt(listPanel.getSelectedRow(), listPanel.getTempletVO().getPkname()) + "_";
		}

		try {
			if (cellPanels != null && cellPanels.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < cellPanels.size(); i++) {
					sb.append(begin + UIUtil.getSequenceNextValByDS(null, "S_PUB_BILLCELLTEMPLET_H") + ";");
				}
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onCancel() {
		currRefItemVO = null;
		setCloseType(2);
		this.dispose();
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO;
	}

	public BillCellPanel getBillCellPanel() {
		return billCellPanel;
	}

}
