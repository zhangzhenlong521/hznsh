package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 历史修改痕迹..
 * @author xch
 *
 */
public class ShowBillTraceDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -471971777355434144L;
	private String str_tablename = null;
	private String str_fieldname = null;
	private String str_pkname = null;
	private String str_pkvalue = null;
	private BillListPanel billlist = null;
	private WLTButton btn_confirm = null; //
	private String str_sql = null; //
	private String[] str_fieldnames = null; //太平老项目需要添加

	/**
	 * 
	 * @param _parent
	 * @param _tablename
	 * @param _fieldname
	 */
	public ShowBillTraceDialog(Container _parent, String _tablename, String _pkname, String _pkvalue, String _fieldname) {
		super(_parent, "查看[" + _tablename + "].[" + _fieldname + "]历史修改痕迹", 500, 300); //
		str_tablename = _tablename;
		str_pkname = _pkname;
		str_pkvalue = _pkvalue; //
		str_fieldname = _fieldname; //
		initialize();
	}

	//太平老项目需要添加
	public ShowBillTraceDialog(Container _parent, String _tablename, String _pkname, String _pkvalue, String[] _fieldnames) {
		super(_parent, "查看历史修改痕迹", 500, 300); //
		str_tablename = _tablename;
		str_pkname = _pkname;
		str_pkvalue = _pkvalue; //
		str_fieldnames = _fieldnames; //
		initialize2();
	}

	private void initialize() {
		billlist = new BillListPanel(new TMO_TraceHist()); //
		str_sql = "select * from pub_bill_keeptrace where tablename='" + this.str_tablename + "' and pkname='" + str_pkname + "' and pkvalue='" + str_pkvalue + "' and fieldname='" + str_fieldname + "' order by tracetime desc";
		billlist.QueryData(str_sql); //
		JPanel panel_south = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_confirm.addActionListener(this); //
		panel_south.add(btn_confirm); //

		this.getContentPane().add(billlist, BorderLayout.CENTER); //
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
	}

	private void initialize2() {
		TMO_TraceHist traceHistTMO = new TMO_TraceHist();
		billlist = new BillListPanel(traceHistTMO); //
		TBUtil tbUtil = new TBUtil();
		str_sql = "select * from pub_bill_keeptrace where " + "tablename='" + this.str_tablename + "' and pkname='" + str_pkname + "' and pkvalue='" + str_pkvalue + "' and fieldname in (" + tbUtil.getInCondition(str_fieldnames) + ") order by fieldname,tracetime desc";
		billlist.QueryData(str_sql); //
		JPanel panel_south = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_confirm.addActionListener(this); //
		panel_south.add(btn_confirm); //

		this.getContentPane().add(billlist, BorderLayout.CENTER); //
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
	}

	public BillListPanel getBilllist() {
		return billlist;
	}

	public void setBilllist(BillListPanel billlist) {
		this.billlist = billlist;
	}

	public WLTButton getBtn_confirm() {
		return btn_confirm;
	}

	public void setBtn_confirm(WLTButton btn_confirm) {
		this.btn_confirm = btn_confirm;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == 17 || e.getModifiers() == 18) {
			MessageBox.showTextArea(this, str_sql); //
		} else {
			this.dispose(); //
		}
	}

	class TMO_TraceHist extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "trace"); //模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "修改痕迹"); //模板名称
			vo.setAttributeValue("templetname_e", ""); //模板名称
			vo.setAttributeValue("tablename", null); //查询数据的表(视图)名		
			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "id"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键"); //显示名称
			itemVO.setAttributeValue("itemname_e", "id"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tablename"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "表名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "tablename"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "pkname"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键字段名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "pkname"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "pkvalue"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键字段值"); //显示名称
			itemVO.setAttributeValue("itemname_e", "pkvalue"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldname"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "保留痕迹字段名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "fieldname"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldvalue"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "历史修改值"); //显示名称
			itemVO.setAttributeValue("itemname_e", "fieldvalue"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "400*75"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tracer"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "修改人"); //显示名称
			itemVO.setAttributeValue("itemname_e", "tracer"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tracetime"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "修改时间"); //显示名称
			itemVO.setAttributeValue("itemname_e", "tracetime"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}

	}

}
