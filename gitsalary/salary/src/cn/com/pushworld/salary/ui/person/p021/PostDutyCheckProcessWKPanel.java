package cn.com.pushworld.salary.ui.person.p021;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 岗责评价解读查询.
 * @author haoming
 * create by 2014-3-25
 */
public class PostDutyCheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = -2743335801600774045L;

	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "个人考核进度_";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();
		//设置日期默认值为当前考核日期  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//设置导出文件名称 Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				onQuery();
			}
		});
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate = '" + bq.getRealValueAt("checkdate") + "'");
				dr.getBillCellPanel().loadBillCellData(ifc.getPostDutyCheckProcess(logid));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //锁定表头 杨科 2013-10-10
				}

				//设置导出文件名称 Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "发生异常请与管理员联系!");
		}
	}

	private JTextField field = new JTextField();
	private WLTButton confirm = new WLTButton("自动打分");
	private BillListPanel listpanel = new BillListPanel(new MyTMO());
	private BillDialog dialog;

	class MyTMO extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "C"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "员工评议未完成人员"); // 模板名称
			vo.setAttributeValue("templetname_e", ""); // 模板名称
			vo.setAttributeValue("tablename", "WLTDUAL"); // 查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); // 主键名
			vo.setAttributeValue("pksequencename", "S_SAL_PERSON_CHECK_SCORE"); // 序列名
			vo.setAttributeValue("savedtablename", "SAL_PERSON_CHECK_SCORE"); // 保存数据的表名
			vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板

			vo.setAttributeValue("TREEPK", "id"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("TREEPARENTPK", ""); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeviewfield", ""); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowtoolbar", "Y"); // 树型显示工具栏
			vo.setAttributeValue("Treeisonlyone", "N"); // 树型显示工具栏
			vo.setAttributeValue("Treeseqfield", "seq"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowroot", "Y"); // 列表是否显示操作按钮栏
			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			List itemvoList = new ArrayList();

			HashVO itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "userid"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "主键"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "username"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "评分人"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "deptname"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "部门"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "curr"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "进度"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);
			return (HashVO[]) itemvoList.toArray(new HashVO[0]);
		}

	}
}
