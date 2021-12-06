package com.pushworld.ipushgrc.bs.duty.p050;

import java.io.Serializable;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

/**
 * 我的岗位说明书，取html
 * 如果银行岗则没有细化到条目，只有岗位描述。那么需要配置“是否把岗则细化”参数。false：岗位职责会读取岗位描述，true：读取该岗位的所有岗责。
 * 岗位说明书再次修改，岗位基础信息完全可配置，html说明书基于billcellpanel生成。但是目前没有实现相关风险，操作指引，内外规的可配。待修改。[2012-05-22]
 * 系统中增加参数“岗位说明书自定义模板”
 * 默认为N，配置为Y需要在原有的cellpanel中配置需要提取数据的单元格的key(pub_post字段名称)。其它的一定要用默认的key（例如1-D）.如果关联其它表需要配置value为sql。
 * 例如(select name from pub_corp_dept where id ={deptid});{deptid}为宏代码。post表的外键。
 * 
 * @author hm
 * 
 */
public class GetPostAndDutyHtmlUtil implements Serializable {
	CommDMO commdmo = new CommDMO();
	TBUtil tbutil = new TBUtil();
	boolean dutyx = tbutil.getSysOptionBooleanValue("是否把岗责细化", true); // 如果没有岗责，只有岗位的描述。
	String cellTemplet = tbutil.getSysOptionStringValue("岗位说明书自定义模板", "N");
	private int maxspan = 4; // 根据billcellpanel模板第一行得到列合并跨度.默认4
	private String nums[] = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	private int num_no = 0;

	public String getHtml(String wfactiveID, String wfprocessID, String[] post_id) throws Exception {
		HashVO[] hvo_post = commdmo.getHashVoArrayByDS(null, "select *  from  pub_post where id='" + post_id[0] + "'");
		HashVO[] hvo_duty = null;
		StringBuffer postids = new StringBuffer();
		StringBuffer like = new StringBuffer();
		if (dutyx) {
			if (post_id.length >= 1 && post_id[1] != null && !post_id[1].equals("-999999")) { // 添加岗位组,修改后支持岗位组功能
				postids.append(tbutil.getInCondition(post_id));
				like.append(getMultiOrCondition("operaterefpost", post_id));
			} else {
				postids.append(post_id[0]);
				like.append(" operaterefpost like '%;" + post_id[0] + ";%' ");
			}
			hvo_duty = commdmo.getHashVoArrayByDS(null, "select *  from  cmp_postduty where postid in(" + postids + ") order by postid,seq,dutyname");//修改排序字段【李春娟/2014-12-16】
		}
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<html>\r\n");
			sb.append("<style type=\"text/css\">\r\n");
			sb.append("tr { font-size: 14px; color: #666666; line-height: 18px; font-family: 宋体}\r\n");
			sb.append(".style_2 {\r\n");
			sb.append("background-color:#F8F8FF;\r\n");
			sb.append(" font-size: 35px;  color:#CC0000;  font-weight: bold;  line-height: 60px; font-family: 宋体}\r\n");
			sb.append(".style_3 {\r\n");
			sb.append("background-color: #F5F5F5;\r\n");
			sb.append(" font-size: 20px;  color:#0066cc;  font-weight: bold;  line-height: 20px; font-family: 宋体\r\n");
			sb.append("}\r\n\r\n");

			sb.append(".style_4 {\r\n");
			sb.append("background-color: #F5F5FF;\r\n");
			sb.append(" font-size: 14px;  color:#0066cc;  font-weight: bold;  line-height: 18px; font-family: 宋体\r\n");
			sb.append("}\r\n\r\n");

			sb.append("TABLE { \r\n");
			sb.append("margin:0 auto;   \r\n");
			sb.append("border-collapse:collapse; \r\n");
			sb.append("font-family:Arial, Helvetica, sans-serif;\r\n");
			sb.append("background-color:#F5F5F0;\r\n");
			sb.append("margin: 0px auto;\r\n");
			sb.append("margin-bottom:20px;\r\n");
			sb.append("border:#CACAFF solid:1 0 0 1;\r\n");
			sb.append("	border-spacing:0;\r\n");
			sb.append("}\r\n");
			sb.append("body,th,td{margin:5;padding:5}\r\n");
			sb.append("td{border:#CACAFF solid:border-width:1 1 1 1}\r\n\r\n");

			sb.append("</style>\r\n");
			sb.append("<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
			sb.append("<title>" + hvo_post[0].getStringValue("name") + "说明书</title>\r\n");
			sb.append("</head> <body>\r\n");
			sb.append("<table width=100% border=1>\r\n");
			sb.append(getPostBasicTable(hvo_post));

			sb.append("<tr align=left><td  colspan=" + maxspan + " class=style_3>" + nums[num_no++] + "、岗位职责</td></tr>\r\n");
			if (dutyx) {
				StringBuffer dutysb = new StringBuffer(); // 单独定义一个来存放职责
				int rowspan = 1;
				if (hvo_duty.length > 0) { // 仿一个表格头
					sb.append("<tr><td colspan=" + maxspan + ">\r\n");
					sb.append("<table width=100% border=1>\r\n");
					sb.append("<tr bgcolor='#AABBDD'><td colspan=1>&nbsp;&nbsp;职责名称</td>\r\n");
					sb.append("<td colspan=1>职责描述</td>\r\n");
					sb.append("<td colspan=2>工作任务</td>\r\n");
					sb.append("</tr>\r\n");
				}
				for (int i = 0; i < hvo_duty.length; i++) {
					String dutyname = getvalue(hvo_duty[i].getStringValue("dutyname"));
					String strspan = "rowspan=\"" + rowspan + "\"";
					if (i > 0) {
						if (dutyname.equals(getvalue(hvo_duty[i - 1].getStringValue("dutyname")))) { // 如果此行与上一行相同，则改行不输出。
							strspan = "";
							rowspan++;
						} else {
							sb.append(dutysb.toString().replaceAll("rowspan=\"1\"", "rowspan=\"" + rowspan + "\"")); // 如果此行与上一行不同就合并单元格
							rowspan = 1;
							strspan = "rowspan=\"" + rowspan + "\"";// 重置rowspan
							dutysb.delete(0, dutysb.length()); // 清空
						}
					}
					if (strspan != null && !strspan.equals("")) {// 需要
						dutysb.append("<tr><td colspan=1 " + strspan + ">&nbsp;&nbsp;" + getvalue(hvo_duty[i].getStringValue("dutyname")) + "</td>\r\n");
						dutysb.append("<td colspan=1 " + strspan + ">" + getvalue(hvo_duty[i].getStringValue("dutydescr")) + "</td>\r\n");
					}
					dutysb.append("<td colspan=2 >" + getvalue(hvo_duty[i].getStringValue("task")) + "</td>\r\n");
					dutysb.append("</tr>\r\n");
				}
				if (hvo_duty.length > 0) {
					dutysb.append("</table></td></tr>");
				}
				sb.append(dutysb.toString().replaceAll("rowspan=\"1\"", "rowspan=\"" + rowspan + "\""));
			} else {
				sb.append("<tr><td colspan = " + maxspan + " >" + getvalue(hvo_post[0].getStringValue("descr")).replaceAll("\n", "<br>") + " </td></tr>\r\n");
			}
			sb.append("<tr align=left><td  colspan=" + maxspan + " class=style_3>" + nums[num_no++] + "、操作指引</td></tr>\r\n");
			HashVO[] cmp_operate = commdmo.getHashVoArrayByDS(null, " select * from CMP_CMPFILE_WFOPEREQ where operatepost in (" + postids + ") or " + like); // 修改为包含岗位组
			if (cmp_operate.length > 0) {
				sb.append("<tr><td colspan=" + maxspan + ">\r\n");
				sb.append("<table width=100% border=1>\r\n");
				for (int i = 0; i < cmp_operate.length; i++) {
					if (i == 0) {
						sb.append("<tr bgcolor='#AABBDD'><td colspan=1>&nbsp;&nbsp;流程文件名称</td>\r\n");
						sb.append("<td colspan=1 >流程名称</td>\r\n");
						sb.append("<td colspan=1 >职责</td>\r\n");
						sb.append("<td colspan=1 >操作规范</td>\r\n");
						sb.append("</tr>\r\n");
					}
					sb.append("<tr><td colspan=1>&nbsp;&nbsp;" + getvalue(cmp_operate[i].getStringValue("cmpfile_name")) + "</td>\r\n");
					sb.append("<td colspan=1 >" + getvalue(cmp_operate[i].getStringValue("wfprocess_name")) + "</td>\r\n");
					sb.append("<td colspan=1 >" + getvalue(cmp_operate[i].getStringValue("operatedesc")) + "</td>\r\n");
					sb.append("<td colspan=1 >" + getvalue(cmp_operate[i].getStringValue("operatereq")) + "</td>\r\n");
					sb.append("</tr>\r\n");
				}
				sb.append("</table>\r\n");
				sb.append("</td></tr>\r\n");
			}
			HashVO[] cmp_riskVO = commdmo.getHashVoArrayByDS(null, " select * from cmp_risk where wfactivity_id in (" + wfactiveID + ")");
			sb.append("<tr align=left><td  colspan=" + maxspan + " class=style_3>" + nums[num_no++] + "、风险指引</td></tr>\r\n");
			if (cmp_riskVO.length > 0) {
				sb.append("<tr><td colspan=4>\r\n");
				sb.append("<table width=100% border=1>\r\n");
				for (int i = 0; i < cmp_riskVO.length; i++) {
					if (i == 0) {
						sb.append("<tr bgcolor='#AABBDD'><td colspan=1>&nbsp;&nbsp;风险点名称</td>\r\n");
						sb.append("<td>风险点编码</td>\r\n");
						sb.append("<td>风险点描述</td>\r\n");
						sb.append("<td>风险分类</td>\r\n");
						sb.append("<td>发现渠道</td>\r\n");
						sb.append("<td>业务活动分类</td>\r\n");
						sb.append("</tr>\r\n");
					}
					sb.append("<tr><td colspan=1>&nbsp;&nbsp;" + getvalue(cmp_riskVO[i].getStringValue("riskname")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_riskVO[i].getStringValue("riskcode")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_riskVO[i].getStringValue("riskdescr")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_riskVO[i].getStringValue("risktype")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_riskVO[i].getStringValue("findchannel")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_riskVO[i].getStringValue("bsactname")) + "</td>\r\n");
					sb.append("</tr>\r\n");
				}
				sb.append("</table>\r\n");
				sb.append("</td></tr>\r\n");
			}
			sb.append("</td></tr>\r\n");
			HashVO[] cmp_lawVO = commdmo.getHashVoArrayByDS(null, " select * from law_law where  id in ( select law_id from cmp_cmpfile_law where wfactivity_id in(" + wfactiveID + ") or (wfprocess_id in (" + wfprocessID + ") and relationtype='流程') )");
			sb.append("<tr align=left><td  colspan=" + maxspan + " class=style_3>" + nums[num_no++] + "、相关法规</td></tr>\r\n");
			if (cmp_lawVO.length > 0) {
				sb.append("<tr><td colspan=4>\r\n");
				sb.append("<table width=100% border=1>\r\n");
				for (int i = 0; i < cmp_lawVO.length; i++) {
					if (i == 0) {
						sb.append("<tr bgcolor='#AABBDD'><td colspan=2>&nbsp;&nbsp;法规名称</td>\r\n");
						sb.append("<td>发文字号</td>\r\n");
						sb.append("<td>发布机构</td>\r\n");
						sb.append("<td>状态</td>\r\n");
						sb.append("</tr>\r\n");
					}
					sb.append("<tr><td colspan=2>&nbsp;&nbsp;" + getvalue(cmp_lawVO[i].getStringValue("lawname")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_lawVO[i].getStringValue("dispatch_code")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_lawVO[i].getStringValue("issuecorp")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_lawVO[i].getStringValue("state")) + "</td>\r\n");
					sb.append("</tr>\r\n");
				}
				sb.append("</table>\r\n");
				sb.append("</td></tr>\r\n");
			}
			sb.append("<tr align=left><td  colspan=" + maxspan + " class=style_3>" + nums[num_no++] + "、相关制度</td></tr>\r\n");
			HashVO[] cmp_ruleVO = commdmo.getHashVoArrayByDS(null, " select a.rulename,b.name busitype,a.publishdate,a.state from rule_rule a left join BSD_BSACT b on a.busitype = b.id where a.id in ( select rule_id from cmp_cmpfile_rule where wfactivity_id in(" + wfactiveID + ") or (wfprocess_id in (" + wfprocessID + ") and relationtype='流程') )");
			if (cmp_ruleVO.length > 0) {
				sb.append("<tr><td colspan=4>\r\n");
				sb.append("<table width=100% border=1>\r\n");
				for (int i = 0; i < cmp_ruleVO.length; i++) {
					if (i == 0) {
						sb.append("<tr bgcolor='#AABBDD'><td colspan=2>&nbsp;&nbsp;制度名称</td>\r\n");
						sb.append("<td>业务类型</td>\r\n");
						sb.append("<td>发布日期</td>\r\n");
						sb.append("<td>状态</td>\r\n");
						sb.append("</tr>\r\n");
					}
					sb.append("<tr><td colspan=2>&nbsp;&nbsp;" + getvalue(cmp_ruleVO[i].getStringValue("rulename")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_ruleVO[i].getStringValue("busitype")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_ruleVO[i].getStringValue("publishdate")) + "</td>\r\n");
					sb.append("<td>" + getvalue(cmp_ruleVO[i].getStringValue("state")) + "</td>\r\n");
					sb.append("</tr>\r\n");

				}
				sb.append("</table>\r\n");
				sb.append("</td></tr>\r\n");
			}
			sb.append(" </table></body></html>\r\n");
			return sb.toString();
		} catch (Exception e) {
			throw e;
		}
	}

	private String getvalue(String str) {
		if (str == null || str.equals("")) {
			return "未填写";
		} else {
			return str.trim();
		}
	}

	private String getPostBasicTable(HashVO hvo_post[]) throws Exception {
		StringBuffer sb = new StringBuffer();
		if ("N".equals(cellTemplet)) {
			sb.append("<tr align=center height=60><td  colspan=4 class=style_2>《" + hvo_post[0].getStringValue("name", "没有定义岗位名称") + "》岗位说明书</td></tr>\r\n");
			sb.append("<tr align=left><td  colspan=4 class=style_3 >" + nums[num_no++] + "、基本信息</td></tr>\r\n");
			sb.append("<tr><td width=\"24%\" class=style_4>岗位名称</td><td width=\"26%\">" + getvalue(hvo_post[0].getStringValue("Name")) + "</td><td width=\"23%\" class=style_4>岗位编码</td><td width=\"25%\">" + getvalue(hvo_post[0].getStringValue("code")) + "</td></tr>\r\n");
			String deptname = commdmo.getStringValueByDS(null, "select name  from  pub_corp_dept where id=" + hvo_post[0].getStringValue("deptid"));
			if (deptname == null) {
				deptname = "";
			}
			sb.append("<tr><td width=\"24%\" class=style_4>所属机构</td><td width=\"26%\">" + deptname + "</td><td width=\"23%\" class=style_4>岗位级别</td><td width=\"25%\">" + getvalue(hvo_post[0].getStringValue("postlevel")) + "</td></tr>\r\n");

			sb.append("<tr align=left><td  colspan=4 class=style_3>" + nums[num_no++] + "、岗位设置目的</td></tr>\r\n");
			sb.append("<tr align=left><td  colspan=4>&nbsp;&nbsp;" + getvalue(hvo_post[0].getStringValue("intent")) + "</td> </tr>\r\n");

			sb.append("<tr align=left><td  colspan=4 class=style_3>" + nums[num_no++] + "、工作联系</td></tr>\r\n");
			sb.append("<tr align=left><td width=\"24%\" class=style_4>内部联系</td><td colspan=3>" + getvalue(hvo_post[0].getStringValue("innercontact")) + "</td></tr>\r\n");
			sb.append("<tr align=left><td width=\"24%\" class=style_4>外部联系</td><td colspan=3>" + getvalue(hvo_post[0].getStringValue("outcontact")) + "</td></tr>\r\n");
			sb.append("<tr align=left><td  colspan=4 class=style_3>" + nums[num_no++] + "、任职资格要求</td></tr>\r\n");
			sb.append("<tr align=left><td width=\"24%\" class=style_4>教育程度/经验</td><td colspan=3>" + getvalue(hvo_post[0].getStringValue("education")) + "</td></tr>\r\n");
			sb.append("<tr align=left><td width=\"24%\" class=style_4>知识/技能</td><td colspan=3>" + getvalue(hvo_post[0].getStringValue("skill")) + "</td></tr>\r\n");
		} else {
			String cellcode = "mypostandduty";
			if ("Y".equals(cellTemplet)) {
				cellcode = "mypostandduty";
			} else {
				cellcode = cellTemplet; // 得到模板编码
			}
			try {
				BillCellVO cell = new MetaDataDMO().getBillCellVO(cellcode, null, null);
				BillCellItemVO itemVOS[][] = cell.getCellItemVOs();
				String colnum = itemVOS[0][0].getSpan();
				int maxspan_1[] = toSpan(colnum);
				if (maxspan_1.length > 0 && maxspan_1[1] > 2) {
					maxspan = maxspan_1[1];
				}
				sb.append("<tr align=center height=60><td  colspan=" + maxspan + " class=style_2>《" + hvo_post[0].getStringValue("name", "没有定义岗位名称") + "》岗位说明书</td></tr>\r\n");
				int tableSpan[] = null;
				for (int i = 1; i < itemVOS.length; i++) {
					sb.append("<tr align=left>\r\n");
					for (int j = 0; j < itemVOS[i].length; j++) {
						String span = itemVOS[i][j].getSpan();
						tableSpan = toSpan(span);
						if (tableSpan == null || tableSpan[0] < 1 || tableSpan[1] < 1) {
							continue;
						}
						String key = itemVOS[i][j].getCellkey();
						String value = itemVOS[i][j].getCellvalue();
						if (key != null && !key.contains("-")) {
							if (value != null && value.contains("select") && value.contains("from")) {
								String[] condition = tbutil.getFormulaMacPars(value);
								for (int k = 0; k < condition.length; k++) {
									String out_id = hvo_post[0].getStringValue(condition[k]);
									if (out_id.contains(";")) {
										out_id = tbutil.getInCondition(out_id);
									}
									value = tbutil.replaceAll(value, "{" + condition[k] + "}", out_id);
								}
								String values[] = commdmo.getStringArrayFirstColByDS(null, value);
								value = "";
								if (values.length == 1) {
									value += values[0];
								} else {
									for (int k = 0; k < values.length; k++) {
										value += values[k] + ";";
									}
								}
							} else {
								value = hvo_post[0].getStringValue(key);
							}

							value = getvalue(value);
							sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + ">" + value + "</td>");
						} else {
							if (value != null && value.contains("select") && value.contains("from")) {
								String[] condition = tbutil.getFormulaMacPars(value);
								for (int k = 0; k < condition.length; k++) {
									String out_id = hvo_post[0].getStringValue(condition[k]);
									if (out_id.contains(";")) {
										out_id = tbutil.getInCondition(out_id);
									}
									value = tbutil.replaceAll(value, "{" + condition[k] + "}", out_id);
								}
								String values[] = commdmo.getStringArrayFirstColByDS(null, value);
								value = "";
								if (values.length == 1) {
									value += values[0];
								} else {
									for (int k = 0; k < values.length; k++) {
										value += values[k] + ";";
									}
								}
								sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + ">" + value + "</td>");
								continue;
							}
							if (tableSpan[1] == maxspan) {
								if (value == null) {
									value = "&nbsp";
									sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + " class=style_3 >" + value + "</td>");
								} else {
									if (value.contains(nums[0]) || value.contains(nums[1]) || value.contains(nums[2]) || value.contains(nums[3]) || value.contains(nums[4]) || value.contains(nums[5])) {
										sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + " class=style_3 >" + value + "</td>");
										num_no++;
									} else {
										sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + " class=style_3 >" + nums[num_no++] + "、" + value + "</td>");
									}

								}
							} else {
								if (value == null) {
									value = "&nbsp";
								}
								sb.append("<td rowspan=" + tableSpan[0] + " colspan=" + tableSpan[1] + " class=style_4 >" + value + "</td>");
							}
						}
					}
					sb.append("</tr>");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} //
		}
		return sb.toString();
	}

	/**
	 * 合并单元格字符串转换为一维数组
	 * 
	 * @param span
	 *            String
	 * @return 整形[2]
	 */
	private int[] toSpan(String span) {
		String[] splitString = null;
		int[] returnResult = new int[] { 1, 1 };
		if (span != null) {
			splitString = span.split(",");
			returnResult[0] = Integer.parseInt(splitString[0].toString());
			returnResult[1] = Integer.parseInt(splitString[1].toString());
		}
		return returnResult;
	}

	private String getMultiOrCondition(String key, String[] tempid) {
		StringBuffer sb_sql = new StringBuffer();
		if (tempid != null && tempid.length > 0) {
			sb_sql.append(" (");
			for (int j = 0; j < tempid.length; j++) {
				sb_sql.append(key + " like '%;" + tempid[j] + ";%'"); // 
				if (j != tempid.length - 1) { //
					sb_sql.append(" or ");
				}
			}
			sb_sql.append(") "); //
		}
		return sb_sql.toString();
	}

}
