package com.pushworld.ipushgrc.bs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

import org.jgraph.JGraph;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.workflow.WorkFlowDesignDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * 大丰农商行数据接口类，客户库是DB2类型【李春娟/2013-07-23】 1.岗位和机构都以编码做唯一性标识，客户库中和合规库中都有数据，其他表都没有数据
 * 2.岗位我们只导入实际岗位，岗位组（deptid is null）不用导入，部门岗位关联也只关联实际岗位 3.流程分组，即流程文件的业务分类
 * 4.流程表中的流程分类指的是根据流程与流程文件的关联，找到流程与业务分类的关联 5.流程表中的层次只可能是“1”表示流程，“2”表示环节
 * 6.流程表中的路径，就是“.processid.”或“.processid.activityid.”
 * 7.流程表中的流程图，就是将合规系统的流程图存成图片的二进制流 8.合规库中的风险和控制措施需要拆分成两个表，并记录关联关系
 * 9.法规分类可以从合规库中取pub_comboboxdict中type='法规类型'的数据
 * 10.法规主子表需要合并，层次为“1”表示主表，此时时效性、法规描述、法规分类、文号、颁布单位、颁布日期需要导入数据，
 * 层次为“2”表示子表记录，不需要导入上面的数据，只导入标题、内容、父节点、层次即可
 * 11.流程法规关联表中的processid和lawid有可能是合规中主表记录也可能是子表记录 12.控制措施表中的版本version
 * 直接设置为“1.0”，删除标识delflag设置为“0”//这条可以先不考虑，以后客户肯定还要统一设置
 * 
 * 
 * @author lcj
 * 
 */
public class ImportDFDataUtil {
	private HashMap postmap = new HashMap();// 旧id和新id的对应关系
	private HashMap processmap = new HashMap();
	private HashMap activitymap = new HashMap();
	private HashMap riskprocessmap = new HashMap();
	private HashMap lawitemmap = new HashMap();
	private HashMap lawmap = new HashMap();
	private HashMap lawtype = new HashMap();
	private HashMap map_dept = new HashMap();
	private HashMap map_dfdept = new HashMap();
	private String df_post = "DB2ADMIN.\"POSITIONS\"";// 大丰数据库中岗位表
	private String df_dept = "DB2ADMIN.\"DEPARTMENT\"";// 大丰数据库中机构表
	private String df_dept_post = "DB2ADMIN.\"DEPPOSLINK\"";// 大丰数据库中部门岗位关联表
	private String df_post_process = "DB2ADMIN.\"LNKPROPOS\"";// 大丰数据库中岗位流程关联表
	private String df_processgroup = "DB2ADMIN.\"PROGROUP\"";// 大丰数据库中流程分组表
	private String df_process = "DB2ADMIN.\"BIZPROCESS\"";// 大丰数据库中流程表
	private String df_risk = "DB2ADMIN.\"RISK\"";// 大丰数据库中风险表
	private String df_ctrl = "DB2ADMIN.\"CONTROLDEF\"";// 大丰数据库中控制措施表
	private String df_lawtype = "DB2ADMIN.\"LAWTYPE\"";// 大丰数据库中法规分类表

	private String df_law = "DB2ADMIN.\"LAWDEF\"";// 大丰数据库中法规表
	private String df_process_law = "DB2ADMIN.\"LNKPROLAW\"";// 大丰数据库中流程法规关联表
	private String df_risk_ctrl = "DB2ADMIN.\"LNKRISCON\"";// 大丰数据库中风险点控制措施关联表
	private String df_process_risk = "DB2ADMIN.\"LNKPRORI\"";// 大丰数据库中流程风险关联表

	private CommDMO commDMO = new CommDMO();

	private String datasource = null;// 这里应该是客户平台系统的数据源名称
	private HashVO[] postVO, dfpostVO = null;// 合规系统岗位表VO,对方系统岗位VO

	/**
	 * 导入所有数据的方法
	 */
	public void importAllData() throws Exception {
		ArrayList arrayList = new ArrayList();
		// 机构和岗位不能删除，岗位与机构关联关系不删除，因为每次都是增加的岗位增量的关联关系
		arrayList.add("delete from " + df_post_process);
		arrayList.add("delete from " + df_processgroup);
		arrayList.add("delete from " + df_process);
		arrayList.add("delete from " + df_risk);
		arrayList.add("delete from " + df_ctrl);
		arrayList.add("delete from " + df_lawtype);
		arrayList.add("delete from " + df_law);
		arrayList.add("delete from " + df_process_law);
		arrayList.add("delete from " + df_risk_ctrl);
		arrayList.add("delete from " + df_process_risk);
		commDMO.executeBatchByDSImmediately(datasource, arrayList);// 执行导入前先删除一下需要清空的记录，这里必须先立即提交一下。

		// 后面的执行肯定有顺序，因为有的关系中需要新id
		commDMO.executeBatchByDS(datasource, importPost());
		commDMO.executeBatchByDS(datasource, importDetpPost());
		commDMO.executeBatchByDS(datasource, importProcessGroup());
		commDMO.executeBatchByDS(datasource, importProcess());
		commDMO.executeBatchByDS(datasource, importPostProcess());
		commDMO.executeBatchByDS(datasource, importRisk());
		commDMO.executeBatchByDS(datasource, importCtrl());
		commDMO.executeBatchByDS(datasource, importLawtype());
		commDMO.executeBatchByDS(datasource, importLaw());
		commDMO.executeBatchByDS(datasource, importProcessLaw());

	}

	/*************************** 杨庆负责 ************************/
	/**
	 * 岗位
	 */
	public ArrayList<String> importPost() throws Exception {
		ArrayList<String> sqList = new ArrayList<String>();
		String sql_post = "select id,code,name,descr,deptid from pub_post where deptid is not null and code is not null";// 排除岗位组
		String sql_dfpost = "select id,posnum,posname,posdesc from " + df_post;
		postVO = commDMO.getHashVoArrayByDS(null, sql_post);// 合规系统岗位
		dfpostVO = commDMO.getHashVoArrayByDS(datasource, sql_dfpost);// 对方系统岗位
		String dfmaxpostid = commDMO.getStringValueByDS(datasource, "select max(id) from " + df_post);// 对方系统岗位最大id
		if (TBUtil.isEmpty(dfmaxpostid)) {// 如果为空，默认为1
			dfmaxpostid = "1";
		}

		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_post);
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// 存放对方岗位名称
		for (int i = 0; i < dfpostVO.length; i++) {
			String dfpostname = dfpostVO[i].getStringValue("posname", "").trim();
			map_dfpost.put(dfpostname, "");
		}

		for (int i = 0; i < postVO.length; i++) {
			String postname = postVO[i].getStringValue("name", "").trim();// 合规岗位名称
			if (map_dfpost.containsKey(postname)) {// 如果两个名称相同，则不导入该条数据;此处认为岗位名称是唯一的且两个系统一致
				continue;
			}
			// 没有重复相同的name，则导入

			int postid = Integer.parseInt(dfmaxpostid) + (i + 1);// 因为对方系统岗位表有数据，所以id从岗位表最大ID加(i+1)开始
			String postcode = postVO[i].getStringValue("code", "");// 岗位名称
			String postdescr = postVO[i].getStringValue("descr", "");// 岗位描述
			insertSQL.putFieldValue("id", postid);
			insertSQL.putFieldValue("posnum", postcode);
			insertSQL.putFieldValue("posname", postname);
			insertSQL.putFieldValue("posdesc", postdescr);
			insertSQL.putFieldValue("delflag", "0");
			sqList.add(insertSQL.getSQL());
			postmap.put(postVO[i].getStringValue("id", ""), postid + "");// 存入ID，旧ID-新ID
		}
		return sqList;
	}

	/**
	 * 部门岗位关联
	 */
	public ArrayList<String> importDetpPost() throws Exception {
		ArrayList<String> sqList = new ArrayList<String>();
		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_dept_post);
		HashMap<String, String> map_post = new HashMap<String, String>();// 合规系统岗位表MAP
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// 对方系统岗位表MAP
		for (int i = 0; i < postVO.length; i++) {
			map_post.put(postVO[i].getStringValue("id", ""), postVO[i].getStringValue("name", ""));
		}
		for (int i = 0; i < dfpostVO.length; i++) {
			map_dfpost.put(dfpostVO[i].getStringValue("posname", ""), dfpostVO[i].getStringValue("id", ""));
		}
		String sql_dept = "select id,name from pub_corp_dept";// 合规系统机构表
		String sql_dfdept = "select depname,id from " + df_dept;// 查对方系统机构表，name标示
		map_dept = commDMO.getHashMapBySQLByDS(null, sql_dept);// 合规系统机构表MAP
		map_dfdept = commDMO.getHashMapBySQLByDS(datasource, sql_dfdept);// 对方系统机构部门map
		for (int i = 0; i < postVO.length; i++) {
			String deptid = postVO[i].getStringValue("deptid", "");
			if (!map_dfdept.containsKey(map_dept.get(deptid))) {// 如果对方系统中无此机构的code，不导入；此处认为机构CODE是唯一的且两个系统一致
				continue;
			}
			String postid = postVO[i].getStringValue("id", "");// 合规系统岗位id
			if (TBUtil.isEmpty(postid)) {
				continue;
			}
			String positionid = "";// 对方系统关联表岗位ID
			if (postmap.containsKey(postid)) {// 如果postmap里包含该岗位ID，说明是新增入的岗位，故加入相应关联数据
				positionid = (String) postmap.get(postid);
			} else {// 新增岗位记录MAP里如果没有，则不加入相应关联数据
				continue;
			}
			String departmentid = map_dfdept.get(map_dept.get(deptid)).toString();// 得到对方系统相应机构ID
			insertSQL.putFieldValue("id", postid);
			insertSQL.putFieldValue("departmentid", departmentid);
			insertSQL.putFieldValue("positionid", positionid);
			sqList.add(insertSQL.getSQL());
		}
		return sqList;
	}

	/**
	 * 岗位流程关联
	 */
	public ArrayList importPostProcess() throws Exception {
		ArrayList sqList = new ArrayList();
		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_post_process);
		HashMap<String, String> map_post = new HashMap<String, String>();// 合规系统岗位表MAP
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// 对方系统岗位表MAP
		for (int i = 0; i < postVO.length; i++) {
			map_post.put(postVO[i].getStringValue("id", ""), postVO[i].getStringValue("name", ""));
		}
		for (int i = 0; i < dfpostVO.length; i++) {
			map_dfpost.put(dfpostVO[i].getStringValue("posname", ""), dfpostVO[i].getStringValue("id", ""));
		}
		String sql_wfpost = "select id,wfactivity_id,operatepost from cmp_cmpfile_wfopereq where wfactivity_id in(select id from pub_wf_activity where processid is not null) and operatepost is not null";// 合规系统岗位流程关联表
		HashVO[] wfpostVO = commDMO.getHashVoArrayByDS(null, sql_wfpost);// 合规系统岗位流程关联表VO
		for (int i = 0; i < wfpostVO.length; i++) {
			String processid = wfpostVO[i].getStringValue("wfactivity_id", "");// 流程ID
			if (activitymap.containsKey(processid)) {// 如果流程MAP里包含该流程ID，说明ID已改，得到新ID
				processid = activitymap.get(processid).toString();// 得到相应流程新ID
			}
			String wfpostid = wfpostVO[i].getStringValue("id", "");// 岗位流程关联表ID
			if (TBUtil.isEmpty(wfpostid)) {
				continue;
			}
			String postid = wfpostVO[i].getStringValue("operatepost", "");// 合规系统关联表操作岗位id
			String positionid = "";// 对方系统关联表岗位ID

			if (!postmap.containsKey(postid)) {// postmap中没有记录该岗位ID替换情况,说明对方岗位中有该岗位，就得用对方的岗位ID
				positionid = map_dfpost.get(map_post.get(postid));
			} else {// 用导过去的新岗位ID
				positionid = (String) postmap.get(postid);
			}
			if (positionid == null || positionid.equals("")) {
				continue;
			}
			insertSQL.putFieldValue("id", wfpostid);
			insertSQL.putFieldValue("processid", processid);
			insertSQL.putFieldValue("posid", positionid);
			sqList.add(insertSQL.getSQL());
		}
		return sqList;
	}

	/*************************** 李春娟负责 ************************/

	/**
	 * 流程
	 */
	public ArrayList importProcess() throws Exception {
		ArrayList sqList = new ArrayList();
		// 先创建所有流程记录，该表中id不变
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select wfpro.id,wfpro.code num,wfpro.name,cmpfile.bsactid progroupid,cmpfile.blcorpid ownerdepid from pub_wf_process wfpro left join cmp_cmpfile cmpfile on wfpro.cmpfileid= cmpfile.id ");
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				StringBuffer sb_sql = new StringBuffer("insert into ");
				sb_sql.append(df_process);
				sb_sql.append(" (id,num,name,progroupid,ownerdepid,prolevel,bizpath,propicture,delflag) values (");
				sb_sql.append(vos[i].getStringValue("id") + ",");
				sb_sql.append("'" + vos[i].getStringValue("num") + "',");
				sb_sql.append("'" + vos[i].getStringValue("name") + "',");
				if (vos[i].getStringValue("progroupid") == null || vos[i].getStringValue("progroupid").trim().equals("")) {
					sb_sql.append("null,");
				} else {
					sb_sql.append(vos[i].getStringValue("progroupid") + ",");
				}
				if (vos[i].getStringValue("ownerdepid") == null || vos[i].getStringValue("ownerdepid").trim().equals("")) {
					sb_sql.append("null,");
				} else {
					sb_sql.append(map_dfdept.get(map_dept.get(vos[i].getStringValue("ownerdepid"))) + ",");
				}
				sb_sql.append("1,");// 层次
				sb_sql.append("'." + vos[i].getStringValue("id") + ".',");// 路径
				sb_sql.append("empty_blob()");// 流程图
				sb_sql.append(",'0')");
				sqList.add(sb_sql.toString());
			}
			commDMO.executeBatchByDSImmediately(this.datasource, sqList);
			sqList.clear();
			// 更新图片字段
			WLTDBConnection conn = new WLTInitContext().getConn(datasource);
			for (int i = 0; i < vos.length; i++) {
				byte[] aString = getProPicture(vos[i].getStringValue("id"));
				String sqlchange = "select propicture from " + df_process + " where id = " + vos[i].getStringValue("id") + " for update";
				PreparedStatement statement = conn.prepareStatement(sqlchange);
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					com.ibm.db2.jcc.a.be blob = (com.ibm.db2.jcc.a.be) resultSet.getBlob("propicture");
					OutputStream outputStream = blob.setBinaryStream(1);
					outputStream.write(aString, 0, aString.length);
					outputStream.flush();
					outputStream.close();

					statement = conn.prepareStatement("update " + df_process + " set propicture =? where id=?");
					statement.setBlob(1, blob);
					statement.setInt(2, Integer.parseInt(vos[i].getStringValue("id")));
					statement.executeUpdate();
					statement.close();
				}
			}
			new WLTInitContext().commitAllTrans();
		}
		// 再创建与流程主键不冲突的环节记录
		HashVO[] vos_activity = commDMO.getHashVoArrayByDS(null, "select id,code num,wfname name,processid parentid from pub_wf_activity where processid in (select id from pub_wf_process) and id not in (select id from pub_wf_process)");
		if (vos_activity != null && vos_activity.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_process);
			String[] keys = vos_activity[0].getKeys();
			for (int i = 0; i < vos_activity.length; i++) {
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos_activity[i].getStringValue(keys[j]));

				}
				sqlBuilder.putFieldValue("delflag", "0");// 层次
				sqlBuilder.putFieldValue("prolevel", 2);// 层次
				sqlBuilder.putFieldValue("bizpath", "." + vos_activity[i].getStringValue("id") + "." + vos_activity[i].getStringValue("parentid") + ".");// 路径
				sqList.add(sqlBuilder.getSQL());
			}

		}
		// 最后创建与流程主键冲突的环节记录
		HashVO[] vos_activity2 = commDMO.getHashVoArrayByDS(null, "select id,code num,wfname name,processid parentid from pub_wf_activity where processid in (select id from pub_wf_process) and id in (select id from pub_wf_process)");
		if (vos_activity2 != null && vos_activity2.length > 0) {
			String maxid = commDMO.getStringValueByDS(null, "select max(id) id from pub_wf_process union all select max(id) id from pub_wf_activity order by id desc");
			int int_maxid = Integer.parseInt(maxid);

			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_process);
			String[] keys = vos_activity2[0].getKeys();
			for (int i = 0; i < vos_activity2.length; i++) {
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos_activity2[i].getStringValue(keys[j]));

				}
				int_maxid++;
				activitymap.put(vos_activity2[i].getStringValue("id"), "" + int_maxid);// 环节的旧id作为key，新id作为value
				sqlBuilder.putFieldValue("id", int_maxid);
				sqlBuilder.putFieldValue("prolevel", 2);// 层次
				sqlBuilder.putFieldValue("delflag", "0");// 层次
				sqlBuilder.putFieldValue("bizpath", "." + vos_activity2[i].getStringValue("id") + "." + vos_activity2[i].getStringValue("parentid") + ".");// 路径
				sqList.add(sqlBuilder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * 获得某个流程的流程图片
	 * 
	 * @param _processid
	 * @return
	 * @throws Exception
	 */
	public byte[] getProPicture(String _processid) throws Exception {
		// 第二步，远程访问一下子查出所有风险点
		HashVO[] hvs_risk = commDMO.getHashVoArrayByDS(null, "select wfprocess_id,wfactivity_id,rank from cmp_risk where wfprocess_id='" + _processid + "'"); //

		// 第三步，创建流程图!
		// 支持排序,即每次输出的顺序都一样!!!
		WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel(false); // 创建一个工作流!不显示工具箱【李春娟/2012-03-08】

		ProcessVO currentProcessVO = new WorkFlowDesignDMO().getWFProcessByWFID(_processid);// 因为本类在服务器端执行，故需要用dmo直接获得ProcessVO，不能用远程调用，否则会报错
		if (currentProcessVO == null) {
			return "".getBytes();
		}
		wfPanel.openMainGraph(currentProcessVO, 0); // 根据一个对象VO,打开一个图形!!
		wfPanel.setGridVisible(false);// 设置不显示背景点点【李春娟/2012-11-16】
		//		wfPanel.showStaff(false);// 设置不显示标尺
		boolean isExportTitle = SystemOptions.getInstance().getBooleanValue("工作流导出是否有标题", true); // 内控系统遇到一些客户要求导出时不要标题!
		if (!isExportTitle) {
			wfPanel.setTitleCellForeground(Color.WHITE);// 设置标题颜色为白色，即不显示
		}
		wfPanel.reSetAllLayer(false);// 必须设置一下,否则阶段左边向下的箭头有可能不显示[李春娟/2012-11-19]
		// 加风险点
		HashMap riskMap = new HashMap(); //
		for (int j = 0; j < hvs_risk.length; j++) {
			if (hvs_risk[j].getStringValue("wfprocess_id") != null && hvs_risk[j].getStringValue("wfprocess_id").equals(_processid)) { // 属于本流程的
				String str_activity_id = hvs_risk[j].getStringValue("wfactivity_id"); //
				if (str_activity_id != null) {
					int li_1 = 0, li_2 = 0, li_3 = 0;
					if (riskMap.containsKey(str_activity_id)) { // 如果已经有了风险点
						RiskVO rvo = (RiskVO) riskMap.get(str_activity_id); // //
						li_1 = rvo.getLevel1RiskCount();
						li_2 = rvo.getLevel2RiskCount();
						li_3 = rvo.getLevel3RiskCount();
					}
					String str_rank = hvs_risk[j].getStringValue("rank"); // 风险等级
					if (str_rank != null) {
						if (str_rank.equals("高风险") || str_rank.equals("极大风险")) {
							li_1++;
						} else if (str_rank.equals("低风险") || str_rank.equals("极小风险")) {
							li_3++;
						} else {
							li_2++; // 中等风险
						}
					} else {
						li_2++; // 中等风险
					}
					RiskVO rsvo = new RiskVO(li_1, li_2, li_3); //
					riskMap.put(str_activity_id, rsvo); // 重新置入!!!
				}
			}
		}
		String[] str_keys = (String[]) riskMap.keySet().toArray(new String[0]); //
		for (int k = 0; k < str_keys.length; k++) {
			RiskVO rvo = (RiskVO) riskMap.get(str_keys[k]); // //
			if (rvo != null) {
				wfPanel.setCellAddRisk(str_keys[k], rvo); // //
			}
		}

		JGraph graph = wfPanel.getGraph(); // //
		int li_width = (int) graph.getPreferredSize().getWidth(); //
		int li_height = (int) graph.getPreferredSize().getHeight(); //

		JWindow win = new JWindow(); // 创建一个窗口,不知道为什么一定要弄一个窗口显示出来,才能把图画上去!!!
		win.setSize(0, 0); //
		win.getContentPane().add(wfPanel); // 
		win.toBack(); //
		win.setVisible(true); //
		if (li_width == 0 || li_height == 0) {// 如果流程没有环节
			li_width = 1;
			li_height = 1;
		}
		BufferedImage image = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB); // 创建一个空白的图片!!
		Graphics g = image.createGraphics(); // 为图片创建一个新的画笔!!
		graph.paint(g); // 将控件的图画写入到这个新的画板中
		g.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", out);
		byte[] imgBytes = out.toByteArray(); // 生成二进制内容!!!
		win.dispose();
		win = null; // 内存释放
		// return str_64code; //返回!!
		return imgBytes;
	}

	/**
	 * 风险，流程与风险关联
	 */
	public ArrayList importRisk() throws Exception {
		ArrayList sqList = new ArrayList();
		// 先创建所有风险，id不变
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select id,riskcode,riskname,riskdescr riskdesc,wfprocess_id,wfactivity_id from cmp_risk");
		if (vos != null && vos.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_risk);

			InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder(df_process_risk);
			int processriskID = 0;
			for (int i = 0; i < vos.length; i++) {
				sqlBuilder.putFieldValue("id", vos[i].getStringValue("id"));
				sqlBuilder.putFieldValue("riskcode", vos[i].getStringValue("riskcode"));
				sqlBuilder.putFieldValue("riskname", vos[i].getStringValue("riskname"));
				sqlBuilder.putFieldValue("riskdesc", vos[i].getStringValue("riskdesc"));
				sqlBuilder.putFieldValue("delflag", "0");
				sqList.add(sqlBuilder.getSQL());// 风险的所有记录

				// 流程与风险关联
				String activityid = vos[i].getStringValue("wfactivity_id", "");
				String processid = vos[i].getStringValue("wfprocess_id", "");
				if (activityid.equals("")) {// 判断风险是否属于环节，创建风险与流程/环节的关系
					if (!processid.equals("")) {
						processriskID++;
						riskprocessmap.put(vos[i].getStringValue("id"), processid);// 记录风险属于哪个流程
						sqlBuilder2.putFieldValue("id", processriskID);
						sqlBuilder2.putFieldValue("processid", processid);
						sqlBuilder2.putFieldValue("riskid", vos[i].getStringValue("id"));
						sqList.add(sqlBuilder2.getSQL());
					}
				} else {
					processriskID++;
					sqlBuilder2.putFieldValue("id", processriskID);
					sqlBuilder2.putFieldValue("riskid", vos[i].getStringValue("id"));
					String realprocessid = activityid;
					if (activitymap.containsKey(activityid)) {
						realprocessid = (String) activitymap.get(activityid);
					}
					sqlBuilder2.putFieldValue("processid", realprocessid);// 记录风险属于哪个流程
					riskprocessmap.put(vos[i].getStringValue("id"), realprocessid);// 记录风险属于哪个流程
					sqList.add(sqlBuilder2.getSQL());
				}
			}
		}
		return sqList;
	}

	/**
	 * 控制措施，风险与控制措施关联
	 */
	public ArrayList importCtrl() throws Exception {
		ArrayList sqList = new ArrayList();
		// 先创建所有控制措施，id不变
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select id,ctrltarget conname,ctrlfn3 condesc from cmp_risk where ctrltarget is not null or ctrlfn3 is not null");
		if (vos != null && vos.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_ctrl);
			InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder(df_risk_ctrl);
			String[] keys = vos[0].getKeys();
			int riskctrlID = 0;
			for (int i = 0; i < vos.length; i++) {
				if ((vos[i].getStringValue("conname") == null || vos[i].getStringValue("conname").equals("")) && (vos[i].getStringValue("condesc") == null || vos[i].getStringValue("condesc").equals(""))) {
					continue;
				}
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos[i].getStringValue(keys[j]));
				}
				sqlBuilder.putFieldValue("version", "1.0.0");
				sqlBuilder.putFieldValue("delflag", "0");
				sqList.add(sqlBuilder.getSQL());

				String processid = "";
				String riskid = vos[i].getStringValue("id");
				if (riskprocessmap.containsKey(riskid)) {
					processid = (String) riskprocessmap.get(riskid);
				}
				riskctrlID++;
				sqlBuilder2.putFieldValue("id", riskctrlID);
				sqlBuilder2.putFieldValue("riskid", riskid);
				sqlBuilder2.putFieldValue("conid", riskid);// 这里将风险表拆分为风险和控制，故两条记录id一样。
				sqlBuilder2.putFieldValue("proid", processid);

				sqList.add(sqlBuilder2.getSQL());
			}
		}
		return sqList;
	}

	/*************************** 张营闯负责 ************************/

	/**
	 * 法规分类
	 */
	public ArrayList importLawtype() throws Exception {
		ArrayList sqList = new ArrayList();
		String sql = "select id ,name from pub_comboboxdict where type='法规类型'";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql);
		if (hashVOs != null) {
			InsertSQLBuilder builder = new InsertSQLBuilder(df_lawtype);
			for (int i = 0; i < hashVOs.length; i++) {
				builder.putFieldValue("id", i);
				builder.putFieldValue("delflag", "0");
				builder.putFieldValue("name", hashVOs[i].getStringValue("name"));
				lawtype.put(hashVOs[i].getStringValue("name"), i);
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * 法规
	 */

	public ArrayList importLaw() throws Exception {
		ArrayList sqList = new ArrayList();
		String maxids = "select max(id) id from law_law union all select max(id) id from law_law_item order by id desc";
		String maxlawid = commDMO.getStringValueByDS(null, maxids);
		int maxid = Integer.parseInt(maxlawid);
		// 将法规主表全部加入到流程银行数据库中
		String lawsql = "select id ,lawname title,state status,remark descrp,lawtype lawtypeid,dispatch_code filenum,issuecorp publishorg,issue_date publishdate from law_law";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, lawsql);
		if (hashVOs != null && hashVOs.length > 0) {
			String[] keys = hashVOs[0].getKeys();
			for (int i = 0; i < hashVOs.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_law);
				for (int j = 0; j < keys.length; j++) {
					if (keys[j].equals("lawtypeid")) {
						builder.putFieldValue(keys[j], lawtype.get(hashVOs[i].getStringValue(keys[j])).toString());
					} else if (keys[j].equals("publishorg")) {
						if (postmap.get(hashVOs[i].getStringValue(keys[j])) != null) {
							builder.putFieldValue(keys[j], postmap.get(hashVOs[i].getStringValue(keys[j])).toString());
						} else {
							builder.putFieldValue(keys[j], hashVOs[i].getStringValue(keys[j]));
						}
					} else if (keys[j].equals("publishdate")) {
						String date = hashVOs[i].getStringValue(keys[j]);// 我们库中时间都是字符串，格式为2013-05-10，而客户数据库中时间就是时间类型，格式为2013-5-10，故这里需要处理一下
						if (date == null || date.equals("") || date.length() != 10) {
							builder.putFieldValue(keys[j], date);
						} else if ("0".equals(date.substring(5, 6))) {
							date = date.substring(0, 5) + date.substring(6, 10);
							if ("0".equals(date.substring(7, 8))) {// 已经变成了2013-5-10的格式，故长度也变了
								date = date.substring(0, 7) + date.substring(8, 9);
							}
						} else if ("0".equals(date.substring(8, 9))) {
							date = date.substring(0, 8) + date.substring(9, 10);
						}
						builder.putFieldValue(keys[j], date);
					} else {
						builder.putFieldValue(keys[j], hashVOs[i].getStringValue(keys[j]));
					}
				}
				builder.putFieldValue("lawlevel", "1");
				builder.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder.getSQL());
			}
		}

		// 加入子表中的数据（子表id与主表中的冲突）
		String lawsql0 = "select id,itemtitle title,itemcontent content,parentid ,lawid ,seq orderseq from law_law_item where id in(select id from law_law)";
		HashVO[] itemhashVO = commDMO.getHashVoArrayByDS(null, lawsql0);
		if (itemhashVO != null) {
			String[] itemkeys = itemhashVO[0].getKeys();
			for (int i = 0; i < itemhashVO.length; i++) {
				maxid++;
				InsertSQLBuilder builder01 = new InsertSQLBuilder(df_law);
				lawitemmap.put(itemhashVO[i].getStringValue("id"), maxid);
				for (int j = 0; j < itemkeys.length; j++) {
					if (itemkeys[j].equals("id")) {
						builder01.putFieldValue(itemkeys[j], maxid);
					} else if (itemkeys[j].equals("parentid")) {
						if (lawitemmap.get(itemkeys[j]) != null && !lawitemmap.get(itemkeys[j]).equals("")) {
							builder01.putFieldValue(itemkeys[j], lawitemmap.get(itemhashVO[i].getStringValue(itemkeys[j])).toString());
						} else if (itemhashVO[i].getStringValue(itemkeys[j]) != null && !itemhashVO[i].getStringValue(itemkeys[j]).equals("")) {
							builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue(itemkeys[j]));
						} else {
							builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue("lawid"));
						}
					} else if (!itemkeys[j].equals("lawid")) {
						builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue(itemkeys[j]));
					}
				}
				builder01.putFieldValue("lawlevel", "2");
				builder01.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder01.getSQL());
			}
		}

		// 加入子表中的数据（子表id不与主表中的冲突）
		String lawsql1 = "select id,itemtitle title,itemcontent content,parentid ,lawid from law_law_item where id not in(select id from law_law)";
		HashVO[] itemhashVO1 = commDMO.getHashVoArrayByDS(null, lawsql1);
		if (itemhashVO1 != null) {
			String[] itemkeys = itemhashVO1[0].getKeys();
			for (int i = 0; i < itemhashVO1.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_law);
				for (int j = 0; j < itemkeys.length; j++) {
					if (itemkeys[j].equals("parentid")) {
						if (lawitemmap.get(itemkeys[j]) != null && !lawitemmap.get(itemkeys[j]).equals("")) {
							builder.putFieldValue(itemkeys[j], lawitemmap.get(itemhashVO1[i].getStringValue(itemkeys[j])).toString());
						} else if (itemhashVO1[i].getStringValue(itemkeys[j]) != null && !itemhashVO1[i].getStringValue(itemkeys[j]).equals("")) {
							builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue(itemkeys[j]));
						} else {
							builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue("lawid"));
						}
					} else if (!itemkeys[j].equals("lawid")) {
						builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue(itemkeys[j]));
					}
				}
				builder.putFieldValue("lawlevel", "2");
				builder.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * 流程法规关联
	 */

	public ArrayList importProcessLaw() throws Exception {
		ArrayList sqList = new ArrayList();
		int idindex = 0;
		// 流程关联的法规
		String sql1 = "select wfprocess_id processid,law_id lawid,lawitem_id from cmp_cmpfile_law where relationtype='流程' ";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql1);
		if (hashVOs != null) {
			for (int i = 0; i < hashVOs.length; i++) {
				idindex++;
				InsertSQLBuilder builder = new InsertSQLBuilder(df_process_law);
				builder.putFieldValue("id", idindex);
				builder.putFieldValue("processid", hashVOs[i].getStringValue("processid"));
				if (hashVOs[i].getStringValue("lawitem_id") == null || hashVOs[i].getStringValue("lawitem_id").equals("")) {
					builder.putFieldValue("lawid", hashVOs[i].getStringValue("lawid"));
				} else if (lawitemmap.containsKey(hashVOs[i].getStringValue("lawitem_id"))) {
					builder.putFieldValue("lawid", lawitemmap.get(hashVOs[i].getStringValue("lawitem_id")).toString());
				} else {
					builder.putFieldValue("lawid", hashVOs[i].getStringValue("lawitem_id"));
				}
				sqList.add(builder.getSQL());
			}
		}
		// 环节关联的法规
		String sql2 = "select wfactivity_id processid,law_id lawid,lawitem_id from cmp_cmpfile_law where relationtype='环节'";
		HashVO[] hashVOs2 = commDMO.getHashVoArrayByDS(null, sql2);
		if (hashVOs2 != null) {
			for (int i = 0; i < hashVOs2.length; i++) {
				idindex++;
				InsertSQLBuilder builder = new InsertSQLBuilder(df_process_law);
				builder.putFieldValue("id", idindex);
				if (processmap.containsKey(hashVOs2[i].getStringValue("processid"))) {
					builder.putFieldValue("processid", processmap.get(hashVOs2[i].getStringValue("processid")).toString());
				} else {
					builder.putFieldValue("processid", hashVOs2[i].getStringValue("processid"));
				}
				if (hashVOs2[i].getStringValue("lawitem_id") == null || hashVOs2[i].getStringValue("lawitem_id").equals("")) {
					builder.putFieldValue("lawid", hashVOs2[i].getStringValue("lawid"));
				} else if (lawitemmap.containsKey(hashVOs2[i].getStringValue("lawitem_id"))) {
					builder.putFieldValue("lawid", lawitemmap.get(hashVOs2[i].getStringValue("lawitem_id")).toString());
				} else {
					builder.putFieldValue("lawid", hashVOs2[i].getStringValue("lawitem_id"));
				}
				sqList.add(builder.getSQL());
			}
		}

		return sqList;
	}

	/**
	 * 流程分组
	 */
	public ArrayList importProcessGroup() throws Exception {
		ArrayList sqList = new ArrayList();
		String sql = "select id ,name from bsd_bsact";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql);
		if (hashVOs != null) {
			for (int i = 0; i < hashVOs.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_processgroup);
				builder.putFieldValue("id", hashVOs[i].getStringValue("id"));
				builder.putFieldValue("delflag", "0");
				builder.putFieldValue("pgname", hashVOs[i].getStringValue("name"));
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}
}
