package com.pushworld.ipushgrc.bs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.workflow.msg.SendSMSIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOComparator;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

import com.pushworld.ipushgrc.bs.cmptarget.CmpTargetDMO;
import com.pushworld.ipushgrc.bs.duty.p050.GetPostAndDutyHtmlUtil;
import com.pushworld.ipushgrc.bs.keywordreplace.KeyWordReplaceContext;
import com.pushworld.ipushgrc.bs.law.p010.ExportOrImportLawUtil;
import com.pushworld.ipushgrc.bs.score.ScoreBSUtil;
import com.pushworld.ipushgrc.bs.score.p090.DeptScoreReportBuilderAdapter;
import com.pushworld.ipushgrc.bs.wfrisk.WFRiskBSUtil;
import com.pushworld.ipushgrc.bs.wfrisk.WFRiskDMO;
import com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistHtmlViewWebCallBean;
import com.pushworld.ipushgrc.bs.wfrisk.p010.ImportVisioBsUtil;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushlbs.bs.bargain.BargainDMO;

/**
 * 产品的服务器!
 * @author xch
 *
 */
public class IPushGRCServiceImpl implements IPushGRCServiceIfc {

	public void getAllFns() throws WLTRemoteException {
	}

	/*------------------------------ 指标模块开始(xch) --------------------------------*/
	/**
	 * 根据选中的指标,创建指标实例!! 即一个机构一季度有一批数据!!
	 */
	public String createTargetInstance(HashMap _parMap) throws Exception {
		return new CmpTargetDMO().createTargetInstance(_parMap); //创建指标!
	}

	public String execTargetCompute(String _instId) throws Exception {
		return new CmpTargetDMO().execTargetCompute(_instId); //执行指标演算!
	}

	/*------------------------------ 指标模块结束(xch) --------------------------------*/

	/*----------------流程与风险模块开始---------------------*/
	/**
	 * 根据流程编码、流程文件、流程文件id新增流程，并将流程id返回
	 * @param _code  流程编码
	 * @param _name  流程名称
	 * @param _userdef01  流程所属部门
	 * @param _cmpfileid  流程文件id
	 * @throws Exception
	 */
	public String insertOneWf(String _code, String _name, String _userdef01, String _cmpfileid) throws Exception {
		return new WFRiskDMO().insertOneWf(_code, _name, _userdef01, _cmpfileid);
	}

	/**
	 * 根据流程文件id删除一个流程文件并级联删除流程、流程相关和环节相关的信息
	 * @param _cmpfileid  流程文件id
	 * @throws Exception
	 */
	public void deleteCmpFileById(String _cmpfileid) throws Exception {
		new WFRiskDMO().deleteCmpFileById(_cmpfileid);
	}

	/**
	 * 根据流程id删除一个流程并级联删除流程相关和环节相关的信息
	 * @param _wfid  流程id
	 * @throws Exception
	 */
	public void deleteWfById(String _wfid) throws Exception {
		new WFRiskDMO().deleteWfById(_wfid);
	}

	/**
	 * 根据流程id取得流程相关信息的条数
	 * @param _wfid 流程id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByWfId(String _wfid) throws Exception {
		return new WFRiskDMO().getRelationCountByWfId(_wfid);
	}

	/**
	 * 根据环节id取得环节相关信息的条数
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByActivityId(String _activityid) throws Exception {
		return new WFRiskDMO().getRelationCountByActivityId(_activityid);
	}

	/**
	 * 根据流程id取得所有环节的环节相关信息的条数
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	public HashMap getRelationCountMap(String _processid) throws Exception {
		return new WFRiskDMO().getRelationCountMap(_processid);
	}

	/**
	 * 根据多个环节id删除环节的相关信息
	 * @param _activityids  多个环节id
	 * @return
	 * @throws Exception
	 */
	public void deleteActivityRelationByActivityIds(String _activityids) throws Exception {
		new WFRiskDMO().deleteActivityRelationByActivityIds(_activityids);
	}

	/**
	 * 判断流程文件是否已编辑锁定，如果没有就锁定，返回true（表示锁定成功，可以编辑了），否则返回false（表示锁定失败，原状态就是锁定状态）
	 * @param _cmpfileid 流程文件id
	 * @param _cmpfilename 流程文件名称
	 * @param _username  操作用戶名
	 * @return
	 * @throws Exception
	 */
	public boolean lockCmpFileById(String _cmpfileid, String _cmpfilename, String _username) throws Exception {
		return new WFRiskBSUtil().lockCmpFileById(_cmpfileid, _cmpfilename, _username);
	}

	/**
	 * 某个流程文件编辑完后要解除流程文件的编辑锁，即其他人可以编辑了
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public void unlockCmpFileById(String _cmpfileid) throws Exception {
		new WFRiskBSUtil().unlockCmpFileById(_cmpfileid);
	}

	/**
	 * 判断某个流程文件是否处于锁定状态
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public boolean isCmpFileLocked(String _cmpfileid) throws Exception {
		return new WFRiskBSUtil().isCmpFileLocked(_cmpfileid);
	}

	/**
	 * 得到所有处于锁定状态的流程文件信息
	 * @return
	 * @throws Exception
	 */
	public String getAllLockedCmpFiles() throws Exception {
		return new WFRiskBSUtil().getAllLockedCmpFiles();
	}

	public String getServerCmpfilePath(String _cmpfileid, HashMap _wfmap) throws Exception {
		return new WFRiskDMO().getServerCmpfilePath(_cmpfileid, _wfmap);
	}

	/**
	 * 发布所有状态为【有效】的流程文件为1.0版本，并且清空历史记录
	 * @param _wfmap
	 * @throws Exception
	 */
	public HashMap publishAllCmpFile(HashMap _wfmap) throws Exception {
		return new WFRiskDMO().publishAllCmpFile(_wfmap);
	}

	/**
	 * 发布某个流程文件,发布的同时，更新版本号，并在文件历史表和文件历史内容表中存入当前流程和流程文件的信息。
	 * word格式只存正文（平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="0"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）
	 * 
	 * @param _cmpfileid    流程文件id
	 * @param _cmpfilename  流程文件名称
	 * @param _newversionno 新的版本号
	 * @param _wfmap        流程图二进制流map
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _overwrite) throws Exception {
		new WFRiskDMO().publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, _wfmap, _overwrite);
	}

	/**
	 * 发布某个流程文件,发布的同时，更新版本号，并在文件历史表和文件历史内容表中存入当前流程和流程文件的信息。
	 * 如果参数_showreffile为true，word格式合并正文和流程说明部分，并且是在服务器端合并
	 * （平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="2"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）；
	 * 如果参数_showreffile为false，说明系统不使用正文，则用itext直接由文件子项（目的、适用范围等）写文档
	 * 
	 * @param _cmpfileid    流程文件id
	 * @param _cmpfilename  流程文件名称
	 * @param _newversionno 新的版本号
	 * @param _wfmap        流程图二进制流map
	 * @param _showreffile  是否有正文
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _showreffile, boolean _overwrite) throws Exception {
		new WFRiskDMO().publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, _wfmap, _showreffile, _overwrite);
	}

	/**
	 * 如果系统有正文并且在客户端合并正文和流程说明部分，就得在合并前将当前版本信息添加到历史记录中，这样在合并时修改记录表中才会有当前版本
	 * 这里判断是否需要覆盖同版本历史记录的而不是在发布时判断，因为在客户端发布才会使用这个方法，并且这个方法是产生了新版本号的一条历史记录（不包括内容）
	 * 
	 * @param _cmpfileid    流程文件id
	 * @param _cmpfilename  流程文件名称
	 * @param _newversionno 流程文件要发布的版本号
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @return
	 * @throws Exception
	 */
	public String addCmpfileHist(String _cmpfileid, String _cmpfilename, String _newversionno, boolean _overwrite) throws Exception {
		return new WFRiskDMO().addCmpfileHist(_cmpfileid, _cmpfilename, _newversionno, _overwrite);
	}

	/**
	 * 发布某个流程文件,件历史发布的同时，更新版本号，并在文件历史表和文内容表中存入当前流程和流程文件的信息。
	 * 因itext不能实现正文和流程说明部分合并，故用jacob实现，而jacob必须在装有word的环境下执行，而有时服务器端（Linux系统）是不能满足的，
	 * 故在客户端先合并好，生成要保存的压缩后的64位码，然后传到服务器端进行其他操作。
	 * word格式合并正文和流程说明部分，并且是在客户端合并（平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="1"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）
	 * 
	 * 这是唯一一个不用判断是否需要覆盖同版本历史记录的发布方法，因为在客户端发布才会使用这个方法，而在此之前会调用新增新版本号的历史记录（不包括内容）时，会判断是否需要覆盖同版本号的历史记录（包括内容），
	 * 如果在这里判断的话，会将本版本号的历史记录（不包括内容）给删除掉了
	 * @param _cmpfileid      流程文件id
	 * @param _cmpfilename    流程文件名称
	 * @param _newversionno   新的版本号
	 * @param _cmpfile_histid 历史记录表id，在正文和流程说明部分的合并前要把发布后的版本存入历史记录表中，这样在发布的word文档中的修改记录才会有当前版本
	 * @param _wfmap          流程图二进制流map
	 * @param _doc64code      客户端合并好的word格式的流程文件
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, String _cmpfile_histid, HashMap _wfmap, String _doc64code) throws Exception {
		new WFRiskDMO().publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, _cmpfile_histid, _wfmap, _doc64code);
	}

	/**
	 * 得到某个流程文件的word格式内容
	 * @param _cmpfileid 流程文件id
	 * @param _wfmap     流程图片的内容!!
	 * @param _onlywf    是否只有流程说明部分!!
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(String _cmpfileid, HashMap _wfmap, boolean _onlywf) throws Exception {
		return new WFRiskDMO().getDocContextBytes(_cmpfileid, _wfmap, _onlywf);
	}

	/**
	 * 查看流程后判断是否要记录日志
	 * @param _cmpfileid 流程文件id
	 * @param _userid    用户id
	 * @param _clicktime 查看时间
	 * @return
	 * @throws Exception
	 */
	public boolean clickCmpFile(String _cmpfileid, String _userid, String _clicktime) throws Exception {
		return new WFRiskDMO().clickCmpFile(_cmpfileid, _userid, _clicktime);
	}

	/**
	 * 删除流程文件的一个历史版本的所有记录
	 * @param _cmpfile_histid 历史版本id
	 * @throws Exception
	 */
	public void deleteCmpFileHistById(String _cmpfile_histid) throws Exception {
		new WFRiskDMO().deleteCmpFileHistById(_cmpfile_histid);
	}

	/**
	 * 删除流程文件的所有历史版本的所有记录，并且更新文件发布日期和版本号为空,状态为“编辑中”
	 * @param _cmpfileid 流程文件id
	 * @throws Exception
	 */
	public void deleteAllCmpFileHistByCmpfileId(String _cmpfileid) throws Exception {
		new WFRiskDMO().deleteAllCmpFileHistByCmpfileId(_cmpfileid);
	}

	/**
	 * 一个BOM图中的所有热点的RiskVO
	 * @param _bomtype  "RISK"、"PROCESS"、"CMPFILE"
	 * @param _datatype  "BLCORPNAME"、"ICTYPENAME"
	 * @param _alldatas  BOM图所有热点值，只有机构才需要设置
	 * @param _isSelfCorp  是否查询本机构
	 * @return
	 * @throws Exception
	 */
	public Hashtable getHashtableRiskVO(String _bomtype, String _datatype, ArrayList _alldatas, boolean _isSelfCorp) throws Exception {
		return new WFRiskDMO().getHashtableRiskVO(_bomtype, _datatype, _alldatas, _isSelfCorp);
	}

	/*----------------流程与风险模块结束---------------------*/

	/*-------------------  法规导出开始   ---------------------*/
	public HashMap getXMlFromTable1000Records(String _dsName, String table, int _beginNo) throws Exception {
		return new ExportOrImportLawUtil().getXMlFromTable1000Records(_dsName, table, _beginNo);
	}

	public String importXmlToTable1000Records(String _dsName, String path, HashMap _compareTable, HashMap conditionMap) throws Exception {
		return new ExportOrImportLawUtil().importXmlToTable1000Records(_dsName, path, _compareTable, conditionMap); //
	}

	/*
	 * 获取服务器端法规更新状态[郝明2014-11-21]
	 */
	public String getUpdataLawDataSchedule() throws Exception {
		return ExportOrImportLawUtil.getSchedule();
	}

	public boolean createFolder(String folderName) {
		String path = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
		File file = new File(path + File.separator + folderName);
		if (file.exists()) {
			return true;
		} else {
			file.mkdir();
		}
		return true;
	}

	/*-------------------  法规导出结束   ---------------------*/
	public String[] getWordContents(HashMap[] maps, String template_name) throws Exception {
		KeyWordReplaceContext replace_context = new KeyWordReplaceContext(maps, template_name);
		return replace_context.getContents();
	}

	public String[][] InputInfo(String[][] str_content, String remarks, String template_id, String type) {
		return new ImpExcelUtil().InputInfo(str_content, remarks, template_id, type);
	}

	public void Insert_InputInfo(Map map, List new_list, String tablename, String tree_codekey) {
		new ImpExcelUtil().Insert_InputInfo(map, new_list, tablename, tree_codekey);

	}

	public String[] ImportVisioToProcess(String cmpfileid, String cmpfilecode, String str) throws Exception {
		return new ImportVisioBsUtil().init(cmpfileid, cmpfilecode, str);
	}

	//原来传一个岗位ID，现在多一个标准岗位，传入数组，第一个值为实际岗位，第二个为标准岗位（可为空）
	public String getPostAndDutyHtmlStr(String wfactiveID, String wfprocessID, String[] post_id) throws Exception {
		return new GetPostAndDutyHtmlUtil().getHtml(wfactiveID, wfprocessID, post_id);
	}

	public HashVO[] getMyfavoriteTreeBillVO(String userid) throws Exception {
		HashVO vos[] = new CommDMO().getHashVoArrayByDS(null, " select * from  my_favorites where  creater = '" + userid + "' and (isfolder='Y' or (parentid is null and isfolder is null))");
		HashMap treeMap = new HashMap();
		List list_1 = new ArrayList();
		LinkedHashMap oldTypeMap = new LinkedHashMap();
		for (int i = 0; i < vos.length; i++) {
			if ("Y".equals(vos[i].getStringValue("isfolder"))) {
				vos[i].setAttributeValue("iconname", "office_057.gif"); //图标
				list_1.add(vos[i]);
			} else {
				oldTypeMap.put(vos[i].getStringValue("ITEMTYPE"), vos[i]);
			}
		}
		Iterator it = oldTypeMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry en = (Entry) it.next();
			HashVO oldVO = (HashVO) en.getValue();
			list_1.add(oldVO);
		}
		HashVO vo_1[] = (HashVO[]) list_1.toArray(new HashVO[0]);
		if (vo_1 != null && vo_1.length > 0) {
			Arrays.sort(vo_1, new HashVOComparator(new String[][] { { "seq", "N", "Y" } })); //重新排序!!
		}
		HashMap treeHash = new HashMap();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		DefaultMutableTreeNode[] allnodes = new DefaultMutableTreeNode[vo_1.length];
		for (int i = 0; i < vo_1.length; i++) {
			allnodes[i] = new DefaultMutableTreeNode(vo_1[i]);
			treeHash.put(vo_1[i].getStringValue("id"), allnodes[i]);
		}
		for (int i = 0; i < vo_1.length; i++) {
			String parentid = vo_1[i].getStringValue("parentid");
			if (parentid == null || parentid.equals("")) {
				rootNode.add(allnodes[i]);
				continue;
			}
			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) treeHash.get(parentid); //
			if (parentnode != null) { //如果找到爸爸了..
				try {
					parentnode.add(allnodes[i]); //在爸爸下面加入我..
				} catch (Exception ex) {
					WLTLogger.getLogger(this).error("在[" + parentnode + "]上创建子结点[" + allnodes[i] + "]失败!!", ex); //
				}
			} else {
				rootNode.add(allnodes[i]); //如果没有找到父亲，也加到根节点下。
			}
		}
		if (rootNode.getChildCount() > 0) {
			Vector vc = new Vector();
			for (Enumeration e = rootNode.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				getAllNode(vc, childNode);
			}
			return (HashVO[]) vc.toArray(new HashVO[0]);
		}
		return new HashVO[0];
	}

	private void getAllNode(Vector vector, DefaultMutableTreeNode node) {
		vector.add(node.getUserObject());
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				getAllNode(vector, childNode); // 继续查找该儿子
			}
		}
	}

	public HashMap bargainCopyFile(HashMap fileMap) throws Exception {
		BargainDMO dmo = new BargainDMO();
		return dmo.bargainCopyFile(fileMap);
	}

	/**
	 * 将制度主表中附件迁移到子表中，只能正常使用一次
	 */
	public void removeRuleAttachfileToRuleitem() throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO[] hashvos = dmo.getHashVoArrayByDS(null, "select id,attachfile from rule_rule where attachfile is not null");
		if (hashvos == null || hashvos.length == 0) {
			return;
		}

		InsertSQLBuilder insertSQl = new InsertSQLBuilder("rule_rule_item");
		ArrayList sqllist = new ArrayList();
		for (int i = 0; i < hashvos.length; i++) {
			String itemtitles[] = hashvos[i].getStringValue("attachfile").split(";");//将附件分成一条一条的
			if (itemtitles.length > 1) {//判断有多少附件，如果有多个附件就将这些附件一条一条的挂到条目中的附件节点下面
				String id = dmo.getSequenceNextValByDS(null, "S_RULE_RULE_ITEM").toString();//得到附件节点的id
				insertSQl.putFieldValue("id", id);
				insertSQl.putFieldValue("ruleid", hashvos[i].getStringValue("id"));
				insertSQl.putFieldValue("parentid", "");
				insertSQl.putFieldValue("itemtitle", "附件");
				insertSQl.putFieldValue("seq", 100);
				insertSQl.putFieldValue("attachfile", "");
				sqllist.add(insertSQl.getSQL());
				for (int j = 0; j < itemtitles.length; j++) {//将附件挂到条目中附件节点的子节点下面，依次命名为：‘附件1’、‘附件2’
					insertSQl.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_RULE_RULE_ITEM"));
					insertSQl.putFieldValue("parentid", id);
					insertSQl.putFieldValue("attachfile", itemtitles[j]);
					insertSQl.putFieldValue("itemtitle", "附件" + (j + 1));
					insertSQl.putFieldValue("seq", 1 + j);
					sqllist.add(insertSQl.getSQL());
				}
			} else {//如果该制度在主表中只有一个附件，就将这个附件直接挂到条目中的附件节点上。
				insertSQl.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_RULE_RULE_ITEM"));
				insertSQl.putFieldValue("ruleid", hashvos[i].getStringValue("id"));
				insertSQl.putFieldValue("parentid", "");
				insertSQl.putFieldValue("itemtitle", "附件");
				insertSQl.putFieldValue("seq", 100);
				insertSQl.putFieldValue("attachfile", hashvos[i].getStringValue("attachfile"));
				sqllist.add(insertSQl.getSQL());
			}

		}

		sqllist.add("update rule_rule set attachfile=null");//清除制度主表上所有的附件
		dmo.executeBatchByDS(null, sqllist);//执行sql语句
	}

	public HashMap getXMlFromTable500Records(String name, String table, int _beginno, String joinSql, String _condition) throws Exception {
		return new ExportOrImportLawUtil().getXMlFromTable500RecordsByCondition(name, table, _beginno, joinSql, _condition);
	}

	//将数据库中的DOC内容拼接, 存为DOC文件
	public void exportCMPFileAsDocFile(String path) throws Exception {
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		String sql = "select id, cmpfilename, versionno from cmp_cmpfile where versionno is not null";
		CommDMO dmo = new CommDMO();
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, sql);
		HashVO vo;
		for (int i = 0; i < hvs.length; i++) {
			vo = hvs[i];
			this.exportCMPFileAsDocFile(vo.getStringValue("id"), vo.getStringValue("cmpfilename"), vo.getStringValue("versionno"), path);
		}

	}

	private void exportCMPFileAsDocFile(String cmpFileID, String cmpFileName, String version, String path) throws Exception {
		TBUtil tbUtil = new TBUtil();
		CommDMO dmo = new CommDMO();
		String sql = "select * from cmp_cmpfile_histcontent " + "where cmpfile_id='" + cmpFileID + "' " + "and cmpfile_versionno = '" + version + "' " + "and contentname='DOC' " + "order by seq";

		HashVO[] hvs = dmo.getHashVoArrayByDS(null, sql);
		if (hvs.length < 1) {
			return;
		}
		StringBuilder sb_doc = new StringBuilder();
		String str_itemValue = null;
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j);
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break;
				} else {
					sb_doc.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		String str_64code = sb_doc.toString();
		byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!这就是Word的实际内容,用于输出!!!

		String fileName = path + cmpFileName + "_V" + version + ".doc";
		new TBUtil().writeBytesToOutputStream(new FileOutputStream(new File(fileName), false), unZipedBytes);
	}

	//将数据库中的HTML内容拼接, 存为HTML文件, 但图片没有出来, 仍然是一个servlet链接, 所以不能离线使用 (暂时没用)
	private void exportCMPFileAsHtmlFile() throws Exception {
		String cmpFileName = "【普信参照示范流程】内部信息传递（含部门分工）.html";
		String cmpFileHisID = "162";

		HashMap map = new HashMap(1);
		map.put("cmpfilehistid", cmpFileHisID);
		WFRiskHistHtmlViewWebCallBean html = new WFRiskHistHtmlViewWebCallBean();
		String body = html.getHtmlContent(map);
		String fileName = "c:\\data\\" + cmpFileName;
		new TBUtil().writeStrToOutputStream(new FileOutputStream(new File(fileName), false), body);
	}

	//导出流程图, 图片格式. (暂时没用)
	//this.exportCMPHisImg(cmpFileHisID, "IMAGE_2761.jpg");	
	private void exportCMPHisImg(String cmpFileHisID, String ImgName) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + cmpFileHisID + "' and contentname='" + ImgName + "' order by seq");
		StringBuilder sb_image = new StringBuilder(); //
		String str_itemValue = null; //
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j); //
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break; //
				} else {
					sb_image.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		TBUtil tbUtil = new TBUtil(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(sb_image.toString());
		byte[] unzipedBytes = tbUtil.decompressBytes(bytes); //解压!!!

		new TBUtil().writeBytesToOutputStream(new FileOutputStream(new File("c:\\data\\IMG.jpg"), false), unzipedBytes);
	}

	/*
	 * 得到某个流程下的流程图数据XML,用于流程复制[YangQing/2013-08-01]
	 */
	public String getXMLProcess(String _processid) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //

		HashVO[] hashvo_activity = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_activity where processid ='" + _processid + "'");
		if (hashvo_activity.length > 0) {
			String[] str_allparentcolumns_activity = hashvo_activity[0].getKeys(); //
			for (int j = 0; j < hashvo_activity.length; j++) {
				sb_xml.append("<!-- ****************************(" + (j + 1) + ")" + (hashvo_activity[j].getStringValue("wfname") == null ? "" : hashvo_activity[j].getStringValue("wfname")) + "******************************** -->\r\n"); // 环节
				sb_xml.append("<activity>\r\n"); //				
				for (int q = 0; q < str_allparentcolumns_activity.length; q++) {
					String parent_content = hashvo_activity[j].getStringValue(str_allparentcolumns_activity[q]);
					if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
						sb_xml.append("  <" + str_allparentcolumns_activity[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_activity[q].toLowerCase() + ">\r\n"); //
					} else {
						sb_xml.append("  <" + str_allparentcolumns_activity[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_activity[q].toLowerCase() + ">\r\n"); //
					}
				}
				sb_xml.append("</activity>"); //
				sb_xml.append("\r\n"); //
			}
		}
		HashVO[] hashvo_group = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_group where processid ='" + _processid + "'");
		if (hashvo_group.length > 0) {
			String[] str_allparentcolumns_group = hashvo_group[0].getKeys(); //
			for (int j = 0; j < hashvo_group.length; j++) {
				sb_xml.append("<!-- ****************************(" + (j + 1) + ")" + (hashvo_group[j].getStringValue("wfname") == null ? "" : hashvo_group[j].getStringValue("wfname")) + "******************************** -->\r\n"); // 部门组
				sb_xml.append("<group>\r\n"); //				
				for (int q = 0; q < str_allparentcolumns_group.length; q++) {
					String parent_content = hashvo_group[j].getStringValue(str_allparentcolumns_group[q]);
					if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
						sb_xml.append("  <" + str_allparentcolumns_group[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_group[q].toLowerCase() + ">\r\n"); //

					} else {
						sb_xml.append("  <" + str_allparentcolumns_group[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_group[q].toLowerCase() + ">\r\n"); //
					}
				}
				sb_xml.append("</group>"); //
				sb_xml.append("\r\n"); //
			}
		}
		HashVO[] hashvo_line = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_transition where processid ='" + _processid + "'");
		if (hashvo_line.length > 0) {
			String[] str_allparentcolumns_line = hashvo_line[0].getKeys(); //
			for (int j = 0; j < hashvo_line.length; j++) {
				sb_xml.append("<!-- ****************************(" + (j + 1) + ")" + (hashvo_line[j].getStringValue("wfname") == null ? "" : hashvo_line[j].getStringValue("wfname")) + "******************************** -->\r\n"); // 连线
				sb_xml.append("<transition>\r\n"); //				
				for (int q = 0; q < str_allparentcolumns_line.length; q++) {
					String parent_content = hashvo_line[j].getStringValue(str_allparentcolumns_line[q]);
					if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
						sb_xml.append("  <" + str_allparentcolumns_line[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_line[q].toLowerCase() + ">\r\n"); //
					} else {
						sb_xml.append("  <" + str_allparentcolumns_line[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_line[q].toLowerCase() + ">\r\n"); //
					}
				}
				sb_xml.append("</transition>"); //
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 复制流程图到该流程下(替换)[YangQing/2013-08-02]
	 * @param xmlcontent XML内容
	 * @param currProcessid 当前流程ID
	 * @throws Exception
	 */
	public void importXMLProcess(String xmlcontent, String currProcessid) throws Exception {
		TBUtil tbUtil = new TBUtil();
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(xmlcontent.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets_activity = doc.getRootElement().getChildren("activity"); // 遍历所有环节
		java.util.List al_billtemplets_group = doc.getRootElement().getChildren("group"); // 遍历所有组
		java.util.List al_billtemplets_transition = doc.getRootElement().getChildren("transition"); // 遍历所有线
		HashMap activitymap = new HashMap();
		al.add("delete from pub_wf_activity where processid=" + currProcessid);
		al.add("delete from pub_wf_group where processid=" + currProcessid);
		al.add("delete from pub_wf_transition where processid=" + currProcessid);
		commdmo.executeBatchByDSImmediately(null, al);//必须立即提交一下，以前未删除旧的数据【李春娟/2014-09-22】
		al.clear();

		for (int j = 0; j < al_billtemplets_activity.size(); j++) {
			org.jdom.Element node_billtemplet_activity = (org.jdom.Element) al_billtemplets_activity.get(j); // 环节
			StringBuffer sb_sql_acitivity = new StringBuffer();
			String pub_acitivity_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			activitymap.put(node_billtemplet_activity.getChild("id").getText(), pub_acitivity_id);
			sb_sql_acitivity.append("insert into pub_wf_activity");
			sb_sql_acitivity.append("(");
			sb_sql_acitivity.append("id,");
			sb_sql_acitivity.append("processid,");
			sb_sql_acitivity.append("code,");
			sb_sql_acitivity.append("wfname,");
			sb_sql_acitivity.append("uiname,");
			sb_sql_acitivity.append("x,");
			sb_sql_acitivity.append("y,");
			sb_sql_acitivity.append("autocommit,");
			sb_sql_acitivity.append("isassignapprover,");
			sb_sql_acitivity.append("approvemodel,");
			sb_sql_acitivity.append("approvenumber,");
			sb_sql_acitivity.append("participate_user,");
			sb_sql_acitivity.append("participate_group,");
			sb_sql_acitivity.append("participate_dynamic,");
			sb_sql_acitivity.append("messageformat,");
			sb_sql_acitivity.append("messagereceiver,");
			sb_sql_acitivity.append("descr,");
			sb_sql_acitivity.append("activitytype,");
			sb_sql_acitivity.append("iscanback,");
			sb_sql_acitivity.append("isneedmsg,");
			sb_sql_acitivity.append("intercept1,");
			sb_sql_acitivity.append("intercept2,");
			sb_sql_acitivity.append("checkuserpanel,");
			sb_sql_acitivity.append("width,");
			sb_sql_acitivity.append("height,");
			sb_sql_acitivity.append("viewtype,");
			sb_sql_acitivity.append("belongdeptgroup,");
			sb_sql_acitivity.append("belongstationgroup,");
			sb_sql_acitivity.append("canhalfstart,");
			sb_sql_acitivity.append("halfstartrole,");
			sb_sql_acitivity.append("canhalfend,");
			sb_sql_acitivity.append("canselfaddparticipate,");
			sb_sql_acitivity.append("showparticimode,");
			sb_sql_acitivity.append("isneedreport,");
			sb_sql_acitivity.append("fonttype,");
			sb_sql_acitivity.append("fontsize,");
			sb_sql_acitivity.append("foreground,");
			sb_sql_acitivity.append("background,");
			sb_sql_acitivity.append("imgstr");
			sb_sql_acitivity.append(")");
			sb_sql_acitivity.append(" values ");
			sb_sql_acitivity.append("(");
			sb_sql_acitivity.append(pub_acitivity_id + ",");
			sb_sql_acitivity.append(currProcessid + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("code").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("wfname").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("uiname").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("x").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("y").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("isassignapprover").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("isassignapprover").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("approvemodel").getText()) + ",");
			String textString = node_billtemplet_activity.getChild("approvenumber").getText();
			if (textString == null || "".equals(textString.trim())) {
				textString = "0";
			}
			sb_sql_acitivity.append(textString + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("participate_user").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("participate_group").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("participate_dynamic").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("messageformat").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("messagereceiver").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("descr").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("activitytype").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("iscanback").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("isneedmsg").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("intercept1").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("intercept2").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("checkuserpanel").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("width").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("height").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("viewtype").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("belongdeptgroup").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("belongstationgroup").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("canhalfstart").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("halfstartrole").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("canhalfend").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("canselfaddparticipate").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("showparticimode").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("isneedreport").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("fonttype").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("fontsize").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("foreground").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("background").getText()) + ",");
			sb_sql_acitivity.append(tbUtil.convertSQLValue(node_billtemplet_activity.getChild("imgstr").getText()));
			sb_sql_acitivity.append(")");
			al.add(sb_sql_acitivity.toString());
		}
		for (int j = 0; j < al_billtemplets_group.size(); j++) {
			org.jdom.Element node_billtemplet_group = (org.jdom.Element) al_billtemplets_group.get(j); // 部门组
			StringBuffer sb_sql_group = new StringBuffer();
			String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			sb_sql_group.append("insert into pub_wf_group");
			sb_sql_group.append("(");
			sb_sql_group.append("id,");
			sb_sql_group.append("processid,");
			sb_sql_group.append("grouptype,");
			sb_sql_group.append("code,");
			sb_sql_group.append("wfname,");
			sb_sql_group.append("uiname,");
			sb_sql_group.append("x,");
			sb_sql_group.append("y,");
			sb_sql_group.append("width,");
			sb_sql_group.append("height,");
			sb_sql_group.append("descr,");
			sb_sql_group.append("fonttype,");
			sb_sql_group.append("fontsize,");
			sb_sql_group.append("foreground,");
			sb_sql_group.append("background,");
			sb_sql_group.append("posts");
			sb_sql_group.append(")");
			sb_sql_group.append(" values ");
			sb_sql_group.append("(");
			sb_sql_group.append(pub_group_id + ",");
			sb_sql_group.append(currProcessid + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("grouptype").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("code").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("wfname").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("uiname").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("x").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("y").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("width").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("height").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("descr").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("fonttype").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("fontsize").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("foreground").getText()) + ",");
			sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("background").getText()) + ",");
			if (node_billtemplet_group.getChild("posts") != null) {
				sb_sql_group.append(tbUtil.convertSQLValue(node_billtemplet_group.getChild("posts").getText()));
			} else {
				sb_sql_group.append("null");
			}
			sb_sql_group.append(")");
			al.add(sb_sql_group.toString());
		}
		for (int j = 0; j < al_billtemplets_transition.size(); j++) {
			org.jdom.Element node_billtemplet_transition = (org.jdom.Element) al_billtemplets_transition.get(j); // 连线
			StringBuffer sb_sql_transition = new StringBuffer();
			String pub_transition_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
			sb_sql_transition.append("insert into pub_wf_transition");
			sb_sql_transition.append("(");
			sb_sql_transition.append("id,");
			sb_sql_transition.append("processid,");
			sb_sql_transition.append("code,");
			sb_sql_transition.append("wfname,");
			sb_sql_transition.append("uiname,");
			sb_sql_transition.append("fromactivity,");
			sb_sql_transition.append("toactivity,");
			sb_sql_transition.append("conditions,");
			sb_sql_transition.append("points,");
			sb_sql_transition.append("dealtype,");
			sb_sql_transition.append("reasoncodesql,");
			sb_sql_transition.append("mailsubject,");
			sb_sql_transition.append("mailcontent,");
			sb_sql_transition.append("intercept,");
			sb_sql_transition.append("fonttype,");
			sb_sql_transition.append("fontsize,");
			sb_sql_transition.append("foreground,");
			sb_sql_transition.append("background,");
			sb_sql_transition.append("linetype,");
			sb_sql_transition.append("issingle");
			sb_sql_transition.append(")");
			sb_sql_transition.append(" values ");
			sb_sql_transition.append("(");
			sb_sql_transition.append(pub_transition_id + ",");
			sb_sql_transition.append(currProcessid + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("code").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("wfname").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("uiname").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(activitymap.get(node_billtemplet_transition.getChild("fromactivity").getText()).toString()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(activitymap.get(node_billtemplet_transition.getChild("toactivity").getText()).toString()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("conditions").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("points").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("dealtype").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("reasoncodesql").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("mailsubject").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("mailcontent").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("intercept").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("fonttype").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("fontsize").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("foreground").getText()) + ",");
			sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("background").getText()) + ",");
			if (node_billtemplet_transition.getChild("linetype") != null) {
				sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("linetype").getText()) + ",");
			} else {
				sb_sql_transition.append("null,");
			}
			if (node_billtemplet_transition.getChild("issingle") != null) {
				sb_sql_transition.append(tbUtil.convertSQLValue(node_billtemplet_transition.getChild("issingle").getText()));
			} else {
				sb_sql_transition.append("null");
			}
			sb_sql_transition.append(")");
			al.add(sb_sql_transition.toString());
		}
		commdmo.executeBatchByDS(null, al);
	}

	private String getSequence(String _itemKey, int _digit) throws WLTRemoteException, Exception {
		CommDMO commdmo = new CommDMO();
		String str_value = commdmo.getSequenceNextValByDS(null, _itemKey);
		if (_digit > str_value.length()) {
			if (_digit - str_value.length() == 0) {
			} else if (_digit - str_value.length() == 1) {
				str_value = "0" + str_value;
			} else if (_digit - str_value.length() == 2) {
				str_value = "00" + str_value;
			} else if (_digit - str_value.length() == 3) {
				str_value = "000" + str_value;
			} else if (_digit - str_value.length() == 4) {
				str_value = "0000" + str_value;
			} else if (_digit - str_value.length() == 5) {
				str_value = "00000" + str_value;
			} else if (_digit - str_value.length() == 6) {
				str_value = "000000" + str_value;
			}
		}
		return str_value;
	}

	/**
	 * 大丰农商行数据接口【李春娟/2013-07-23】
	 * 以后可能要删掉该方法
	 * @param path
	 * @throws Exception
	 */
	public void importDFDatas() throws Exception {
		ImportDFDataUtil util = new ImportDFDataUtil();
		util.importAllData();
	}

	public BillCellVO getDeptScoreCellVO(HashMap _condition) throws Exception {
		return new DeptScoreReportBuilderAdapter().buildReportData(_condition);
	}

	/**
	 * 新的违规积分直接生效功能【李春娟/2014-11-04】
	 * @param _id 积分登记主表（SCORE_REGISTER）主键
	 * @throws Exception
	 */
	public void effectScoreByRegisterId(String _id) throws Exception {
		new ScoreBSUtil().effectScoreByRegisterId(_id);

	}

	public HashMap<String, String> publishEffectScore(HashMap hashmap) throws Exception {
		return new ScoreBSUtil().publishEffectScore(hashmap);
	}

	/**
	 * 新的违规积分我的积分--积分确认 Gwang 2014-11-26
	 * @param _id （score_user）主键
	 * @throws Exception
	 */
	public void effectScoreById(String _id) throws Exception {
		String sql = "id = " + _id;
		new ScoreBSUtil().effectScoreBySqlCondition(sql);

	}

	public void sendSMS(List tell_msg) throws Exception {
		String smsclass = TBUtil.getTBUtil().getSysOptionStringValue("通用短信接口", null);
		if (!TBUtil.isEmpty(smsclass)) {
			Object obj = Class.forName(smsclass).newInstance();
			if (obj instanceof SendSMSIFC) {
				SendSMSIFC ifc = (SendSMSIFC) obj;
				ifc.scoreSend(tell_msg);
			}
		}
	}

	/**
	 * 在服务器端创建流程文件最新版本的word文档，在上传office控件下的word目录中，以历史版本主表id为文档名称【李春娟/2015-02-11】
	 * @param _cmpfileid
	 * @return				返回文件名称，如123.doc
	 * @throws Exception
	 */
	public String createCmpfileByHistWord(String _cmpfileid) throws Exception {
		return new WFRiskDMO().createCmpfileByHistWord(_cmpfileid);
	}
	
	public boolean outputAllCmpFileByHisWord() throws Exception {
		return new WFRiskDMO().outputAllCmpFileByHisWord();
	}
}
