package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditFrame;
import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * 流程与风险编辑!! 核心界面!
 * A.主界面就是体系文件列表(cmp_cmpfile)
 * B.如果是编辑状态上面的按钮有【新建文件】【编辑文件】【删除文件】【编辑流程】【查看文件】【流程导出html】【流程导出word】 【历史版本】
 * C.如果是查看状态上面的按钮有【浏览】【查看正文】【查看流程】【流程导出html】【流程导出word】 【历史版本】【加入收藏】【查看违规事件】
 * D.如果是走工作流发布的,则还需要两个按钮【提交发布】【流程监控】,如果不是走工作流,则有两个按钮【发布】【废止】
 * E.如果显示正文就不显示目的、适用范围等子项（流程导出html和word也不显示），但要显示【查看正文】按钮，逻辑为直接用IE打开正文；【流程导出word】按钮直接导出流程说明。
 * 如果不显示正文，则显示目的、适用范围等子项（流程导出html和word也要显示），但不显示【查看正文】按钮，【流程导出word】按钮逻辑为子项内容。
 * 
 * @author xch
 * 
 */

public class WFAndRiskEditWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private IPushGRCServiceIfc service;//产品服务接口
	private BillListPanel billList_cmpfile; // 流程文件列表!
	private WLTButton btn_add;// 按钮【新建文件】
	private WLTButton btn_edit;// 按钮【编辑文件】
	private WLTButton btn_delete;// 按钮【删除文件】
	private WLTButton btn_editwf;// 按钮【编辑流程】
	//private WLTButton btn_lookfile;// 按钮【浏览】
	private WLTButton btn_looktempfile;//按钮【Word预览】
	private WLTButton btn_lookreffile;// 按钮【Word浏览】
	//private WLTButton btn_lookwf;//按钮【查看流程】
	private WLTButton btn_lookword;// 按钮【合规手册】
	private WLTButton btn_looktempfile2;//按钮【Html预览】
	private WLTButton btn_lookhtml;// 按钮【Html浏览】
	//	private WLTButton btn_version;// 按钮【历史版本】
	private WLTButton btn_lookevent;// 按钮【查看违规事件】
	private WLTButton btn_bigVerWord;//按钮【红头文件】 导出最新的发布文件，比如（1.0,2.0), 没什么太大用处
	private WLTButton btn_exportWords;//按钮【批量导出】
	private boolean editable = true;//是否可编辑
	private boolean showreffile;//是否显示正文
	private boolean stateeditable;//文件状态是否可编辑
	private int htmlStyle;
	private TBUtil tbutil;
	private String templetcode = null;
	private String name = ClientEnvironment.getInstance().getLoginUserName();
	public BillListPanel getBillList_cmpfile() {
		return billList_cmpfile;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public boolean isShowreffile() {
		return showreffile;
	}

	public boolean isStateeditable() {
		return stateeditable;
	}

	/**
	 * 工作面板初始化方法
	 */
	public void initialize() {
		//取得菜单参数 生成html的风格: htmlStyle
		if (getMenuConfMapValueAsStr("htmlStyle") != null) {
			this.htmlStyle = Integer.parseInt(getMenuConfMapValueAsStr("htmlStyle"));
		}
		if (getMenuConfMapValueAsStr("列表模板") != null) {
			templetcode = getMenuConfMapValueAsStr("列表模板");
		}
		this.setLayout(new BorderLayout()); //
		try {
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		tbutil = new TBUtil();
		showreffile = tbutil.getSysOptionBooleanValue("流程文件是否由正文生成word", true);//默认有正文，由正文生成word
		stateeditable = tbutil.getSysOptionBooleanValue("流程文件维护时文件状态是否可编辑", true);//默认在新增和编辑文件时文件状态是可编辑的

		btn_lookword = new WLTButton("合规手册");
		btn_lookword.addActionListener(this);
		boolean showlookword = tbutil.getSysOptionBooleanValue("流程文件查看是否显示合规手册按钮", true);//一汽项目中需要将合规手册隐藏，故增加参数配置【李春娟/2012-05-30】
		boolean btntype = tbutil.getSysOptionBooleanValue("流程文件查看是否简化按钮", false);//宜兴项目中提出流程文件查询列表上按钮太多，故增加参数配置【李春娟/2015-01-28】
		if (this.editable) {//如果可编辑，则添加按钮
			if (templetcode == null) {
				/**
				 * 
				 * 因内控产品中去掉了业务类型,将体系和管理对象/产品进行关联,故所有有关流程文件的模板都要改,
				 * 很多都是直接用到合规系统中的功能菜单,在菜单参数配置中配置不太方便,故弄了个平台参数配置。
				 * 
				 * 类型有 维护、查看、发布、版本管理、文件/流程查看、流程地图查看、风险地图查看、风险地图浏览 八种,
				 * 
				 * 分别表示流程文件的维护权限的模板(本部门+本人),
				 * 查看权限的模板(一般是本机构,可根据流程文件查看策略配置),
				 * 流程文件发布或废止权限的模板,
				 * 流程文件版本管理的模板,
				 * 流程文件名称连接点击后打开的窗口中文件的模板(模板布局进行了调整),
				 * 流程地图点击热点打开的列表模板,
				 * 风险地图点击热点打开的列表模板
				 * 风险地图点击热点打开的列表上的浏览按钮使用的模板
				 * 
				 * 查看  CMP_CMPFILE_CODE1
				 * 维护 CMP_CMPFILE_CODE2
				 * 发布 CMP_CMPFILE_CODE3
				 * 版本管理 CMP_CMPFILE_CODE3
				 * 风险地图查看 V_RISK_PROCESS_FILE_CODE1
				 * 流程地图查看 V_FILE_PROCESS_CODE1
				 * 风险地图浏览  CMP_RISK_CODE3
				 * 文件/流程查看 CMP_CMPFILE_CODE5

				 */
				templetcode = tbutil.getSysOptionHashItemStringValue("各功能中流程文件的模板", "维护", "CMP_CMPFILE_CODE2");//只能维护本部门的流程文件【李春娟/2012-07-13】
			}
			billList_cmpfile = new BillListPanel(templetcode); //
			btn_add = new WLTButton("新建");
			btn_edit = new WLTButton("编辑文件");
			btn_delete = new WLTButton("删除");
			btn_editwf = new WLTButton("编辑流程");
			btn_looktempfile = new WLTButton("Word预览");
			btn_looktempfile.setToolTipText("临时版本Word预览");
			btn_looktempfile2 = new WLTButton("Html预览");
			btn_looktempfile2.setToolTipText("临时版本Html预览");

			btn_add.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_editwf.addActionListener(this);
			btn_looktempfile.addActionListener(this);
			btn_looktempfile2.addActionListener(this);

			if (showreffile) {//如果有正文，则增加查看文件按钮
				if (showlookword) {
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_edit, btn_editwf, btn_looktempfile, btn_looktempfile2, btn_lookword });// 流程文件添加按钮
				} else {
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_edit, btn_editwf, btn_looktempfile, btn_looktempfile2 });// 流程文件添加按钮
				}
			} else {
				billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_edit, btn_editwf, btn_looktempfile, btn_looktempfile2 });// 流程文件添加按钮
			}
			//在流程文件维护时可批量导出word正文，方便线下评审【李春娟/2015-07-13】
			btn_exportWords = new WLTButton("批量导出");
			btn_exportWords.addActionListener(this);
			billList_cmpfile.addBillListButton(btn_exportWords);
			//在系统参数里配置Y全部显示  N只有admin显示[张珍龙/2016-01-15]
			boolean expOne = false;//批量导出word按钮是否显示
			String expAllowRole = TBUtil.getTBUtil().getSysOptionStringValue("流程维护批量导出按钮开放角色", "none");		
			if (expAllowRole.equals("none")) {
				//不开放...
			}else {
				//对某些角色开放
				String[] userRole = ClientEnvironment.getInstance().getLoginUserRoleCodes();
				String[] allowRoles = expAllowRole.split(";");
				for (String allowRole : allowRoles) {
					for (String role : userRole) {
						if (allowRole.equals(role)) {
							expOne = true;
							break;
						}
					}
					if (expOne) break;
				}
		
			}
			if (ClientEnvironment.isAdmin() || expOne) {
				btn_exportWords.setVisible(true);
			}else{
				btn_exportWords.setVisible(false);
			}
			
		} else {
			if (templetcode == null) {
				templetcode = tbutil.getSysOptionHashItemStringValue("各功能中流程文件的模板", "查看", "CMP_CMPFILE_CODE1");//流程文件的查看模板（本机构），权限由流程文件的查看策略配置【李春娟/2012-07-13】
			}
			billList_cmpfile = new BillListPanel(templetcode); //
			billList_cmpfile.setDataFilterCustCondition("versionno is not null");

			btn_lookreffile = new WLTButton("Word浏览");
			btn_lookreffile.setToolTipText("最新发布的版本Word浏览");
			btn_lookreffile.addActionListener(this);

			btn_lookhtml = new WLTButton("Html浏览");
			btn_lookhtml.setToolTipText("最新发布的版本Html浏览");
			btn_lookhtml.addActionListener(this);

			btn_lookevent = new WLTButton("查看违规事件");
			btn_lookevent.addActionListener(this);
			WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("流程文件", this.getClass().getName(), "cmpfilename");//加入收藏夹按钮

//			btn_bigVerWord = new WLTButton("红头文件");
//			btn_bigVerWord.addActionListener(this);

			if (showreffile) {//如果有正文，则增加查看文件按钮
				if (showlookword) {
					if (btntype) {//如果设置简化按钮【李春娟/2015-01-28】
						billList_cmpfile.getBillListBtnPanel().removeAllButtons();
						billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookreffile, btn_lookword, btn_joinFavority });
					} else {
						billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookreffile, btn_lookword, btn_lookhtml, btn_joinFavority, btn_lookevent });// 流程文件添加按钮
					}
				} else {
					if (btntype) {//如果设置简化按钮【李春娟/2015-01-28】
						billList_cmpfile.getBillListBtnPanel().removeAllButtons();
						billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookreffile, btn_joinFavority });
					} else {
						billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookreffile, btn_lookhtml, btn_joinFavority, btn_lookevent });// 流程文件添加按钮
					}
				}
			} else if (showlookword) {
				if (btntype) {//如果设置简化按钮【李春娟/2015-01-28】
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookword, btn_joinFavority });
					billList_cmpfile.getBillListBtnPanel().removeAllButtons();
				} else {
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookword, btn_lookhtml, btn_joinFavority, btn_lookevent });// 流程文件添加按钮
				}
			} else {
				if (btntype) {//如果设置简化按钮【李春娟/2015-01-28】
					billList_cmpfile.getBillListBtnPanel().removeAllButtons();
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_joinFavority });
				} else {
					billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_lookhtml, btn_joinFavority, btn_lookevent });// 流程文件添加按钮
				}
			}
		}
		billList_cmpfile.repaintBillListButton();// 必须重新绘制按钮
		billList_cmpfile.addBillListHtmlHrefListener(this);
		this.add(billList_cmpfile); //	
	}

	/**
	 * 列表按钮的点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAddFile();
		} else if (e.getSource() == btn_edit) {
			onEditFile();
		} else if (e.getSource() == btn_delete) {
			onDeleteFile();
			//		} else if (e.getSource() == btn_lookwf) {
			//			onLookWf(true);
		} else if (e.getSource() == btn_editwf) {
			onEditWf();
		} else if (e.getSource() == btn_lookreffile) {
			onLookReffileByHist();
		} else if (e.getSource() == btn_lookword) {
			onWordLook();
		} else if (e.getSource() == btn_lookhtml) {
			onHtmlLook();
		} else if (e.getSource() == btn_lookevent) {
			onLookEvent();
		} else if (e.getSource() == btn_looktempfile) {
			onLookTempFileByWord();
		} else if (e.getSource() == btn_looktempfile2) {
			onLookTempFileByHtml();
		} else if (e.getSource() == btn_bigVerWord) {
			onLookBigVerByWord();
		} else if (e.getSource() == btn_exportWords) {//增加批量导出word【李春娟/2015-07-13】
			//批量导出有些客户导出的路径有空格导致导出的文件$中的文件替换不了
			MessageBox.show(this, "温馨提示：您在导出文件的时候！请确定您导出文件的路径没有空格！按住Ctrl可多选");
			onExportFilesAsWord();
		}
	}

	/**
	 * 查看大版本号的红头文件
	 */
	private void onLookBigVerByWord() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsWordLByHist(billList_cmpfile, str_cmpfileID, true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 列表链接事件，一个是文件名称的链接，一个是历史版本的链接，因该页面按钮太多，所以将文件名称和历史版本设成链接查看，
	 * 为了和后面流程文件地图等功能点一致，文件名称链接打开是查看文件和流程的dialog，而不采用查看正文的方式。
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if ("cmpfilename".equals(_event.getItemkey())) {
			onLookFileAndWf();
		} else {
			onLookVersion();
		}
	}

	/**
	 * 按钮【新建】的逻辑,因正文有保存，卡片也有保存，客户经常混淆，认为正文里保存了，该流程文件记录也就保存了，就直接关闭窗口了，故将正文隐藏，在编辑文件时才显示
	 */
	private void onAddFile() {
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.insertRow(); // 卡片新增一行!
		cardPanel.setEditableByInsertInit(); // 设置卡片编辑状态为新增时的设置
		cardPanel.setVisiable("btn_temp", false);//新增时隐藏按钮【下载模板】
		cardPanel.setVisiable("reffile", false);//新增时隐藏正文
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); // 弹出卡片新增框
		cardPanel.setEditable("filestate", stateeditable);//设置文件状态是否可编辑,必须放在上句后面
		dialog.setVisible(true); // 显示卡片窗口
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billList_cmpfile.newRow(false); //
			billList_cmpfile.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_cmpfile.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // 设置列表该行的数据为初始化状态.
			billList_cmpfile.setSelectedRow(li_newrow); //
			billList_cmpfile.refreshCurrSelectedRow();//需要刷新一下，否则文件状态会变为黑色【李春娟/2012-03-19】
		}
	}

	/**
	 * 按钮【编辑文件】的逻辑
	 */
	private void onEditFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//刷新一下，防止发生被别人修改了文件状态而不同步的问题
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String filestate = billVO.getStringValue("filestate");
		String view_filestate = billVO.getStringViewValue("filestate");
		if ((("2".equals(filestate) || "4".equals(filestate)) && !this.hasReject(billVO)) || "5".equals(filestate)) {//1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
			if (MessageBox.showConfirmDialog(this, "该文件的状态为[" + view_filestate + "],不能进行编辑,是否需要查看？") != JOptionPane.YES_OPTION) {
				return;
			} else {
				onLookFile();
				return;
			}
		} else if ("3".equals(filestate)) {
			int li_result = MessageBox.showOptionDialog(this, "该流程文件已经[发布], 如果选择[编辑]状态将变为[编辑中],\r\n则需要重新发布!你可以有如下操作:", "提示", new String[] { "查看", "编辑", "取消" }, 450, 150); //
			if (li_result == 0) { //
				onLookFile();
				return;
			} else if (li_result == 1) { //
				try {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + cmpfileid);
					billList_cmpfile.refreshCurrSelectedRow();
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			} else {
				return;
			}
		}
		billVO = billList_cmpfile.getSelectedBillVO();//必须要设置一下
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.setBillVO(billVO); //
		if (!showreffile) {
			cardPanel.setVisiable("btn_temp", false);//下载模板的按钮
		}
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); // 弹出卡片编辑框
		cardPanel.setEditable("filestate", stateeditable);//设置文件状态是否可编辑,必须放在上句后面
		dialog.setVisible(true); // 显示卡片窗口
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回!将则卡片中的数据赋给列表!
			BillVO return_BillVO = dialog.getBillVO();
			return_BillVO.setObject("blcorpname", new StringItemVO(return_BillVO.getStringViewValue("blcorpid")));
			return_BillVO.setObject("bsactname", new StringItemVO(return_BillVO.getStringViewValue("bsactid")));
			return_BillVO.setObject("ictypename", new StringItemVO(return_BillVO.getStringViewValue("ictypeid")));

			billList_cmpfile.setBillVOAt(billList_cmpfile.getSelectedRow(), return_BillVO, false); //
			billList_cmpfile.setRowStatusAs(billList_cmpfile.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			String return_blcorpid = return_BillVO.getStringValue("blcorpid");
			String return_blcorpname = return_BillVO.getStringViewValue("blcorpid");

			//这里需要判断一下如果机构名称变了，也需要更新一下！否则在风险地图里显示个数有问题！【李春娟/2012-03-14】
			try {
				UpdateSQLBuilder sb_update = new UpdateSQLBuilder("cmp_risk", "cmpfile_id = " + cmpfileid);
				if (!billList_cmpfile.getTempletItemVO("bsactid").isCardisshowable() && billList_cmpfile.getTempletItemVO("ictypeid").isCardisshowable()) {//如果模板中不显示业务活动但显示内控体系，则将风险点中原记录业务活动字段记录内控体系，这样可在风险表少加一个字段，内控产品中用到【李春娟/2012-07-17】
					String return_ictypeid = return_BillVO.getStringValue("ictypeid");
					String return_ictypename = return_BillVO.getStringViewValue("ictypeid");
					//					UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + tbutil.convertSQLValue(return_blcorpid) + ",blcorpname=" + tbutil.convertSQLValue(return_blcorpname) + " ,bsactid=" + tbutil.convertSQLValue(return_ictypeid) + ",bsactname="
					//							+ tbutil.convertSQLValue(return_ictypename) + " where cmpfile_id = " + cmpfileid);//如果blcorpid、bsactid为空串时以前sql会报错，故修改之【李春娟/2014-12-23】
					sb_update.putFieldValue("blcorpid", return_blcorpid);
					sb_update.putFieldValue("blcorpname", return_blcorpname);
					sb_update.putFieldValue("bsactid", return_ictypeid);
					sb_update.putFieldValue("bsactname", return_ictypename);
				} else {
					String return_bsactid = return_BillVO.getStringValue("bsactid");
					String return_bsactname = return_BillVO.getStringViewValue("bsactid");
					//更改文件的所属机构和业务活动要同步更新风险点上的所属机构和业务活动
					//UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + tbutil.convertSQLValue(return_blcorpid) + ",blcorpname=" + tbutil.convertSQLValue(return_blcorpname) + ",bsactid=" + tbutil.convertSQLValue(return_bsactid) + ",bsactname="
					//		+ tbutil.convertSQLValue(return_bsactname) + " where cmpfile_id = " + cmpfileid);//如果blcorpid、bsactid为空串时以前sql会报错，故修改之【李春娟/2014-12-23】
					sb_update.putFieldValue("blcorpid", return_blcorpid);
					sb_update.putFieldValue("blcorpname", return_blcorpname);
					sb_update.putFieldValue("bsactid", return_bsactid);
					sb_update.putFieldValue("bsactname", return_bsactname);
				}
				UIUtil.executeUpdateByDS(null, sb_update.getSQL());
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
			billList_cmpfile.refreshCurrSelectedRow();//需要刷新一下，否则文件状态会变为黑色【李春娟/2012-03-19】
		}
	}

	/**
	 * 按钮【删除文件】的逻辑，只是对没有版本号的流程文件进行删除，并级联删除流程及相关信息
	 */
	private void onDeleteFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//重新查一下，因为页面的数据可能是十分钟或很久以前的数据。
		billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO.getStringValue("versionno") != null && !"".equals(billVO.getStringValue("versionno"))) {
			MessageBox.show(this, "该文件已有版本不能删除!"); //
			return;
		}
		if ("2".equals(billVO.getStringValue("filestate"))) {//没有版本，但是已在[发布申请中]的文件也不允许删除
			MessageBox.show(this, "该文件的状态为[" + billVO.getStringViewValue("filestate") + "],不能删除!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "此操作会删除所有相关的流程,是否删除?") != JOptionPane.YES_OPTION) {
			return; //
		}
		String cmpfileid = billVO.getStringValue("id");
		// 删除流程文件要记录日志,后台处理，先查出所有流程id，不用子查询
		try {
			service.deleteCmpFileById(cmpfileid);
			billList_cmpfile.removeSelectedRow();// 页面删除
			for (int i = 0; i < billList_cmpfile.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList_cmpfile.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent("删除", btn_delete, billList_cmpfile); //
				action.onBillListDeleteButtonClicked(event); //点击后执行.....
			}
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 按钮【查看流程】的逻辑
	 * @param _showlog 是否显示流程文件查看日志的弹出框
	 */
	private void onLookWf(boolean _showlog) {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			String cmpfileid = billVO.getStringValue("id");
			String cmpfilename = billVO.getStringValue("cmpfilename");
			String[][] processes = UIUtil.getStringArrayByDS(null, "select id,code,name from pub_wf_process where cmpfileid =" + cmpfileid + " order by userdef04,id");// 流程文件的所有流程
			if (processes == null || processes.length == 0) {// 判断流程文件是否有流程
				MessageBox.show(this, "该文件没有流程!");// 如果该流程文件没有流程就直接返回
				return;
			}
			WFGraphEditDialog editDialog = new WFGraphEditDialog(this, "流程查看", 1000, 700, cmpfileid, cmpfilename, processes, false); //
			editDialog.setMaxWindowMenuBar();
			editDialog.setVisible(true);
			if (_showlog) {
				String clicktime = tbutil.getCurrDate();
				CurrLoginUserVO uservo = ClientEnvironment.getInstance().getCurrLoginUserVO();
				//			boolean showIdea = service.clickCmpFile(billVO.getStringValue("id"), uservo.getId(), clicktime);//判断是否需要添加日志记录，一个小时内的就不添加了
				//			if (showIdea) {
				BillCardPanel cardPanel = new BillCardPanel("CMP_CMPFILE_CLICKLOG_CODE1"); // 创建一个卡片面板
				cardPanel.insertRow(); //
				BillCardDialog dialog = new BillCardDialog(billList_cmpfile, "流程文件查看日志", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel.setRealValueAt("cmpfile_id", cmpfileid);
				cardPanel.setRealValueAt("cmpfile_name", cmpfilename);

				cardPanel.setRealValueAt("userid", uservo.getId());
				cardPanel.setRealValueAt("usercode", uservo.getCode());
				cardPanel.setRealValueAt("username", uservo.getName());
				cardPanel.setRealValueAt("userblcorpid", uservo.getBlDeptId());
				cardPanel.setRealValueAt("userblcorpname", uservo.getBlDeptName());
				cardPanel.setRealValueAt("clicktime", clicktime);
				dialog.setVisible(true); // 显示卡片窗口	
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 按钮【编辑流程】的逻辑
	 */
	private void onEditWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//刷新一下，防止发生被别人修改了文件状态而不同步的问题
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String cmpfilename = billVO.getStringValue("cmpfilename");
		String filestate = billVO.getStringValue("filestate");
		String view_filestate = billVO.getStringViewValue("filestate");
		if ((("2".equals(filestate) || "4".equals(filestate)) && !this.hasReject(billVO)) || "5".equals(filestate)) {//1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
			if (MessageBox.showConfirmDialog(this, "该文件的状态为[" + view_filestate + "],不能进行编辑,是否需要查看？") != JOptionPane.YES_OPTION) {
				return;
			} else {
				onLookWf(false);
				return;
			}
		} else if ("3".equals(filestate)) {
			int li_result = MessageBox.showOptionDialog(this, "该流程文件已经[发布], 如果选择[编辑]状态将变为[编辑中],\r\n则需要重新发布!你可以有如下操作:", "提示", new String[] { "查看", "编辑", "取消" }, 450, 150); //
			if (li_result == 0) { //
				onLookWf(false);
				return;
			} else if (li_result == 1) { //
				try {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + cmpfileid);
					billList_cmpfile.refreshCurrSelectedRow();
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			} else {
				return;
			}
		}

		String[][] processes = null;// 流程文件的所有流程
		try {
			processes = UIUtil.getStringArrayByDS(null, "select id,code,name,userdef01 from pub_wf_process where cmpfileid =" + cmpfileid + " order by userdef04,id");
			if (processes == null || processes.length == 0) {// 判断流程文件是否有流程
				if (MessageBox.showConfirmDialog(this, "该文件没有流程,是否创建流程?") == JOptionPane.YES_OPTION) {// 该文件没有流程，提示是否创建
					try {
						String wfname = cmpfilename + "_流程1";
						String wfid = service.insertOneWf(wfname, wfname, billVO.getStringValue("blcorpid"), cmpfileid);//创建流程
						processes = new String[][] { { wfid, wfname, wfname } };
					} catch (Exception e) {
						MessageBox.showException(this, e);
					}
				} else {// 如果选择取消，以前是不创建流程则直接返回，但邵春芸和钢哥提出，这里直接打开空的面板，以便可以visio导入【李春娟/2014-08-21】
					WFGraphEditDialog editDialog = new WFGraphEditDialog(this, "流程编辑", 1000, 700, cmpfileid, cmpfilename, null, true); //
					editDialog.setMaxWindowMenuBar();
					editDialog.setVisible(true);
					return;
				}
			}
			//			boolean iseditable = service.lockCmpFileById(cmpfileid, cmpfilename, ClientEnvironment.getCurrLoginUserVO().getName());
			//			if (!iseditable) {//如果不可以编辑
			//				MessageBox.show(this, "该流程文件正在被他人编辑，请稍候编辑!");
			//				return;
			//			}
			//一汽项目王雷提出流程图编辑时要参考标准流程图，故设置菜单参数可配置流程图编辑窗口是否为模式窗口，"editWindowStyle"取值为0-非模式窗口，1-模式窗口，即可同时显示多个编辑窗口【李春娟/2012-04-01】
			if ("1".equals(this.getMenuConfMapValueAsStr("editWindowStyle", "0"))) {
				WFGraphEditFrame editFrame = new WFGraphEditFrame(this, "流程编辑", 1000, 700, cmpfileid, cmpfilename, processes, true); //
				editFrame.setVisible(true);
			} else {
				WFGraphEditDialog editDialog = new WFGraphEditDialog(this, "流程编辑", 1000, 700, cmpfileid, cmpfilename, processes, true); //
				editDialog.setMaxWindowMenuBar();
				editDialog.setVisible(true);
			}
			//service.unlockCmpFileById(cmpfileid);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}

	/**
	 * 按钮【浏览】是直接由WLTButton创建的，组显示和隐藏等逻辑是在卡片初始化时处理的，故不用这段逻辑，但编辑文件，如果该文件的状态是不可编辑的，则需要调用这段逻辑来浏览文件
	 */
	private void onLookFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.setBillVO(billVO);
		cardPanel.setVisiable("btn_temp", false);//浏览时隐藏按钮【下载模板】
		cardPanel.setEditable(false);
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); // 弹出卡片浏览框
		dialog.setVisible(true); // 显示卡片窗口
	}

	/**
	 * 按钮【查看正文】的逻辑,查看正文时可以直接在编辑或者浏览中查看，所以暂不显示该按钮
	 */
	private void onLookReffile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String str_filename = billVO.getStringValue("reffile", "");
		if ("".equals(str_filename)) {
			MessageBox.show(this, "该文件没有正文,不能浏览!"); //
			return;
		}
		UIUtil.openRemoteServerFile("office", str_filename);
	}

	/**
	 * 按钮【word浏览】的逻辑
	 */
	private void onLookReffileByHist() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsWordLByHist(billList_cmpfile, str_cmpfileID, false); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 按钮【合规手册】的逻辑
	 */
	private void onWordLook() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String cmpfiletype_code = billVO.getComBoxItemVOValue("cmpfiletype").getCode();
		if (!"有流程".equals(cmpfiletype_code)) {
			MessageBox.show(this, "该文件为非流程类文件，不能导出合规手册!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().ExportHandBook(billList_cmpfile, str_cmpfileID); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 按钮【html浏览】的逻辑
	 */
	private void onHtmlLook() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsHTMLByHist(billList_cmpfile, str_cmpfileID); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 按钮【查看违规事件】的逻辑
	 */
	private void onLookEvent() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String cmpfileid = billVO.getStringValue("id");
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from cmp_event where cmp_cmpfile_id=" + cmpfileid);
			if ("0".equals(count)) {
				MessageBox.show(this, "该流程文件没有违规事件!"); //
				return;
			}
			BillListDialog listdialog = new BillListDialog(billList_cmpfile, "违规事件", "CMP_EVENT_CODE1");
			BillListPanel listPanel = listdialog.getBilllistPanel();
			listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "浏览"));
			listPanel.repaintBillListButton();
			listPanel.setQuickQueryPanelVisiable(false);
			listPanel.QueryDataByCondition("cmp_cmpfile_id=" + cmpfileid);
			listdialog.getBtn_confirm().setVisible(false);
			listdialog.getBtn_cancel().setText("关闭");
			listdialog.getBtn_cancel().setToolTipText("关闭");
			listdialog.setVisible(true); // 显示卡片窗口
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * 文件名称-链接，查看流程文件及其所有流程，这里看最新的记录包括文件属性和一图两表
	 */
	private void onLookFileAndWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();
		billVO = billList_cmpfile.getSelectedBillVO();
		String tabcount = this.getMenuConfMapValueAsStr("文件查看页签数", "0");//流程维护和发布废止两个功能可配置文件查看页签数量，比平台参数的优先级要高。默认值为0，表示该菜单参数弃权，根据平台参数判断【李春娟/2015-02-11】
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billVO.getStringValue("id"), tabcount);
		dialog.setVisible(true);
	}

	/**
	 * 历史版本-链接的逻辑
	 */
	private void onLookVersion() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String versionno = billVO.getStringValue("versionno");//只根据版本号是否为空判断即可，因为如果发布过的文件肯定有版本号，并且如果可以删除历史版本的话，当前版本肯定是要保留的
		if (versionno == null) {
			MessageBox.show(this, "该文件未发布过，没有历史版本!"); //
			return;
		}
		String cmpfileid = billVO.getStringValue("id");
		CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "文件[" + billVO.getStringValue("cmpfilename") + "]的历史版本", cmpfileid, false); //
		dialog.setVisible(true); //
	}

	/**
	 * 【Word预览】的逻辑
	 */
	private void onLookTempFileByWord() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsWord(billList_cmpfile, str_cmpfileID); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 【Html预览】的逻辑
	 */
	private void onLookTempFileByHtml() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsHTML(billList_cmpfile, str_cmpfileID, this.htmlStyle); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private boolean hasReject(BillVO _billvo) {
		String processid = _billvo.getStringValue("WFPRINSTANCEID");
		try {
			if (processid == null || "".equals(processid.trim())) {//由于文件发布和废止如果走流程，第一步点提交了，但没选下一环节的接收人，就直接取消提交了，这时文件状态已变成“申请发布中”或“申请废止中”，编辑时再重置回来
				if ("2".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + _billvo.getStringValue("id"));
				} else if ("4".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='3' where id=" + _billvo.getStringValue("id"));
				}
				billList_cmpfile.refreshCurrSelectedRow();
				return true;
			}

			String count = UIUtil.getStringValueByDS(null, "select count(id) from pub_wf_dealpool where rootinstanceid='" + processid + "' and submitisapprove='N'");//是否有退回
			if ("0".equals(count)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return false;
		}
	}

	/**
	 * 【批量导出】的逻辑
	 * 在编写流程文件时，可能需要线下评审，所以现场咨询师建议增加批量导出word功能
	 */
	private void onExportFilesAsWord() {
		final BillVO[] billVOs = billList_cmpfile.getSelectedBillVOs();
		if (billVOs == null || billVOs.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			new SplashWindow(null, new AbstractAction() {//加了一个假的进度条要不一些客户提出会出现黑屏现象其实后台还在运行【张珍龙/2016-01-15】
				public void actionPerformed(ActionEvent arg0) {	
					try {
						new WFRiskUIUtil().exportFilesAsWord(billList_cmpfile, billVOs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //
				}	
				});
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
}

