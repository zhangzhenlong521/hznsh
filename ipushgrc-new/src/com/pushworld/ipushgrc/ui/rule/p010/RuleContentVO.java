package com.pushworld.ipushgrc.ui.rule.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.StringFormat;

public class RuleContentVO {
	private String[] titleid;
	private String title = "";
	private String message = "";
	private String id = null;
	private LinkedList<RuleContentVO> allcontent = new LinkedList<RuleContentVO>();
	private String usercode = ClientEnvironment.getCurrSessionVO().getLoginUserCode();
	private String username = ClientEnvironment.getCurrSessionVO().getLoginUserName();
	private int headerlengths[];
	private String dataSourceType;
	private Container parent;
	private Pub_Templet_1VO templetVO;

	public RuleContentVO(String[] alltitle, String content) {
		content = content.replace('　', ' ');
		titleid = copytitle(alltitle);
		int len = titleid.length;
		int[] alladdress = new int[len];
		int length = content.length();
		int minaddress = length;
		for (int i = 0; i < len; i++) {
			int address = content.indexOf(titleid[i]);
			alladdress[i] = address;
			if (address != -1 && address < minaddress) {
				minaddress = address;
			}
		}
		String start = content.substring(0, minaddress);
		if (!"".equals(start)) {
			int index = start.indexOf('\n');
			if (index == -1) {
				title = start.trim();
			} else {
				title = start.substring(0, index);
				message = start.substring(index + 1).trim();
			}
		}
		for (int i = len - 1; i >= 0; i--) {
			int theaddress = alladdress[i];
			if (theaddress != -1) {
				int min = length;
				for (int j = 0; j < i; j++) {
					int nowaddress = alladdress[j];
					if (nowaddress != -1 && nowaddress < min) {
						min = nowaddress;
					}
				}
				if (theaddress < min) {
					String some = content.substring(theaddress, min);
					String[] all = some.split(getPamrm(titleid[i]));
					for (String type : all) {
						if (!"".equals(type))
							allcontent.add(new RuleContentVO(titleid, type));
					}
				}
			}
		}
	}

	private String[] copytitle(String[] source) {
		int len = source.length;
		String[] result = new String[len];
		for (int i = 0; i < len; i++) {
			result[i] = source[i];
		}
		return result;
	}

	public LinkedList<String> saveToDataBase() throws Exception {
		String currentDate = UIUtil.getServerCurrDate();
		String state = "现行有效"; // 状态
		String con = title + " " + message;
		if (id != null && !id.equals("")) {
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select t1.rulename from rule_rule t1,rule_rule_item t2 where t1.id = t2.ruleid and  t1.id = '" + id + "'"); //按照传入的ID查一下，如果有。那就直接覆盖
			if (vos.length > 0) {
				id = null; //如果选择的制度已经有子表数据，那么需要重新创建ID
			} else {
				//不做
			}
		}
		con = con.trim();// 之前代码会把两行的标题合并为一行，中间加一个空格隔开，但是如果本身就是一行的话，就多一个空格出来。很难看！【郝明 2012-02-27】
		LinkedList<String> sqls = new LinkedList<String>();
		if (id == null || id.equals("")) {
			int index = con.indexOf('\n');
			if (index != -1)
				con = con.substring(0, index);
			con = getSql(con);
			BillVO exsitVOS[] = UIUtil.getBillVOsByDS(null, "select * from rule_rule where rulename  like '%" + con.trim() + "%'", templetVO);
			if (exsitVOS.length > 0 && !showWarDialog(exsitVOS)) {
				return null;
			}
			if ("MYSQL".equalsIgnoreCase(getDataSourceType())) {
				if (con != null && con.length() > headerlengths[0]) {
					throw new Exception("制度标题长度应为【" + headerlengths[0] + "】,实际为【" + con.length() + "】,不能导入！");
				}
			} else {
				if (con != null && con.getBytes().length > headerlengths[0]) {
					throw new Exception("制度标题长度应为【" + headerlengths[0] + "】字符,实际为【" + con.getBytes().length + "】字符,不能导入！");
				}
			}
			id = UIUtil.getSequenceNextValByDS(null, "S_RULE_RULE");
			String sqlMain = "insert into rule_rule(id, rulename,state, publishdate,evalcount,blcorp,blcorp_name,createuser,createdate) " + "values({0}, '{1}', '{2}', '{3}','{4}', '{5}','{6}','{7}','{8}')";
			CurrLoginUserVO userVO = ClientEnvironment.getCurrLoginUserVO();
			String deptID = userVO.getBlDeptId();
			if (deptID == null) {
				deptID = "-1";
			}
			sqlMain = StringFormat.formatSQL(sqlMain, id, con, state, currentDate, "0", deptID, userVO.getBlDeptName() == null ? "" : userVO.getBlDeptName(), usercode + "/" + username, currentDate);
			sqls.add(sqlMain);
		}
		int seq = 1;
		for (RuleContentVO vo : allcontent) {
			sqls.addAll(vo.checksave(null, id, seq, "", headerlengths));
			seq++;
		}
		return sqls;
	}

	private static String getCode(int seq) {
		int num = 4 - String.valueOf(seq).length();
		StringBuilder str = new StringBuilder(4);
		while (num > 0) {
			str.append("0");
			num--;
		}
		str.append(seq);
		return str.toString();
	}

	private LinkedList<String> checksave(String parentid, String lawid, int seq, String code, int headerlengths[]) throws Exception {
		LinkedList<String> sqls = new LinkedList<String>();
		// String currentDate = UIUtil.getCurrDate();
		String pk = UIUtil.getSequenceNextValByDS(null, "S_RULE_RULE_ITEM");
		// String loginUserID = ClientEnvironment.getCurrLoginUserVO().getId();
		String linkcode = code + getCode(seq);
		String itemtitle = title;
		String itemcontent = message;
		if ("MYSQL".equalsIgnoreCase(getDataSourceType())) {
			if (itemtitle != null && itemtitle.length() > headerlengths[1]) {
				throw new Exception("条目【" + itemtitle + "】名称长度最大为：" + headerlengths[1] + "实际值：" + itemtitle.length());
			}
			if (itemcontent != null && itemcontent.length() > headerlengths[2]) {
				throw new Exception("条目【" + itemtitle + "】的内容长度最大为：" + headerlengths[2] + "实际值：" + itemcontent.length());
			}
		} else {
			if (itemtitle != null && itemtitle.getBytes().length > headerlengths[1]) {
				throw new Exception("条目【" + itemtitle + "】名称长度最大为：" + headerlengths[1] + "字符，实际值：" + itemtitle.getBytes().length + "字符");
			}
			if (itemcontent != null && itemcontent.getBytes().length > headerlengths[2]) {
				throw new Exception("条目【" + itemtitle + "】的内容长度最大为：" + headerlengths[2] + "字符，实际值：" + itemcontent.getBytes().length + "字符");
			}
		}
		InsertSQLBuilder insertSql = new InsertSQLBuilder("rule_rule_item");
		insertSql.putFieldValue("id", pk);
		insertSql.putFieldValue("ruleid", lawid);
		insertSql.putFieldValue("itemtitle", itemtitle);
		insertSql.putFieldValue("itemcontent", itemcontent);
		insertSql.putFieldValue("parentid", parentid);
		insertSql.putFieldValue("linkcode", linkcode);
		insertSql.putFieldValue("seq", seq);
		sqls.add(insertSql.getSQL());
		// sqls.add("insert into
		// rule_rule_item(id,ruleid,itemtitle,itemcontent,parentid,linkcode,seq)
		// values(" + pk + ",'" + lawid + "','" + itemtitle + "','" +
		// itemcontent + "'," + parentid + ",'" + linkcode + "','" + seq +
		// "')");
		seq = 1;
		for (RuleContentVO vo : allcontent) {
			sqls.addAll(vo.checksave(pk, lawid, seq, linkcode, headerlengths));
			seq++;
		}
		return sqls;
	}

	private static String getSql(String source) {
		char[] message = source.toCharArray();
		StringBuilder result = new StringBuilder(message.length);
		boolean mysql = "MYSQL".equalsIgnoreCase(ClientEnvironment.getInstance().getDefaultDataSourceType());
		for (char now : message) {
			if (now == '\'')
				result.append("''");
			else if (mysql && now == '\\')
				result.append("\\\\");
			else
				result.append(now);
		}
		return result.toString();
	}

	private static String getPamrm(String source) {
		char[] some = new char[] { '\\', '.', '*', '+', '?', '|', '(', ')', '{', '}', '^', '$' };
		char[] message = source.toCharArray();
		StringBuilder result = new StringBuilder(message.length);
		for (char now : message) {
			for (char the : some) {
				if (the == now) {
					result.append('\\');
					break;
				}
			}
			result.append(now);
		}
		return result.toString();
	}

	public String getTitle() {
		return title;
	}

	public LinkedList<RuleContentVO> getContent() {
		LinkedList<RuleContentVO> result = new LinkedList<RuleContentVO>();
		for (int i = 0, len = allcontent.size(); i < len; i++) {
			result.add(allcontent.get(i));
		}
		return allcontent;
	}

	public String getMessage() {
		return message;
	}

	public void setItemlength(int[] headerlengths) {
		this.headerlengths = headerlengths;
	}

	public String getDataSourceType() {
		if (dataSourceType == null) {
			dataSourceType = new TBUtil().getDefaultDataSourceType();
		}
		return dataSourceType;
	}

	public String getID() {
		return id;
	}

	/*
	 * 传入制度id，如果外面的制度仅仅是有制度主表数据的数据，那么默认认为把子表内容导入到选择的制度中。
	 */
	public void setID(String _ruleID) {
		this.id = _ruleID;
	}

	public void setPub_Templet_1VO(Pub_Templet_1VO _templetVO) {
		this.templetVO = _templetVO.deepClone();
		this.templetVO.setAutoLoads(0);
	}

	public void setParentContainer(Container _parent) {
		this.parent = _parent;
	}

	/*
	 * 如果制度有已存在的或者类似的。就先提示。
	 */
	public boolean showWarDialog(BillVO vos[]) {
		final BillDialog dialog = new BillDialog(parent, "发现[" + vos.length + "]条相同或相近制度，是否继续？");
		WLTButton btn_continue = new WLTButton("继续导入");
		btn_continue.addActionListener(new ActionListener() {
			WLTButton btn_continue = new WLTButton("继续导入");

			public void actionPerformed(ActionEvent e) {
				dialog.setCloseType(1);
				dialog.dispose();
			}
		});
		WLTButton btn_back = new WLTButton("返回");
		btn_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setCloseType(-1);
				dialog.dispose();
			}
		});
		BillListPanel listPanel = new BillListPanel(templetVO);
		listPanel.setItemVisible("showdetail", false);
		listPanel.setItemVisible("evalcount", false);
		listPanel.getQuickQueryPanel().setVisible(false);
		listPanel.addBillVOs(vos);
		listPanel.addBillListHtmlHrefListener(new BillListHtmlHrefListener() {
			public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
				new RuleShowHtmlDialog(_event.getBillListPanel());
			}
		});
		dialog.getContentPane().add(listPanel);
		WLTPanel btnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout());
		btnPanel.add(btn_continue);
		btnPanel.add(btn_back);
		dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		dialog.setSize(700, 300);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			return true;
		}
		return false;
	}
}
