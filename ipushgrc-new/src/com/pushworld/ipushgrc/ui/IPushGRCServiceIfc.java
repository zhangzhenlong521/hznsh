package com.pushworld.ipushgrc.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

/**
 * 产品的服务接口!
 * @author xch
 *
 */
public interface IPushGRCServiceIfc extends WLTRemoteCallServiceIfc {

	public void getAllFns() throws WLTRemoteException; //

	/*------------------------------ 指标模块开始(xch) --------------------------------*/
	/**
	 * 根据选中的指标,创建指标实例!! 即一个机构一季度有一批数据!!
	 */
	public String createTargetInstance(HashMap _parMap) throws Exception;

	/**
	 * 执行指标演算计算!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public String execTargetCompute(String _instId) throws Exception; //

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
	public String insertOneWf(String _code, String _name, String _userdef01, String _cmpfileid) throws Exception; //

	/**
	 * 根据流程文件id删除一个流程文件并级联删除流程、流程相关和环节相关的信息
	 * @param _cmpfileid  流程文件id
	 * @throws Exception
	 */
	public void deleteCmpFileById(String _cmpfileid) throws Exception; //

	/**
	 * 根据流程主键删除一个流程并级联删除流程相关和环节相关的信息
	 * @param _wfid  流程id
	 * @throws Exception
	 */
	public void deleteWfById(String _wfid) throws Exception; //

	/**
	 * 根据流程id取得流程相关信息的条数
	 * @param _wfid 流程id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByWfId(String _wfid) throws Exception;

	/**
	 * 根据环节id取得环节相关信息的条数
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByActivityId(String _activityid) throws Exception;

	/**
	 * 根据流程id取得所有环节的环节相关信息的条数
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	public HashMap getRelationCountMap(String _processid) throws Exception;

	/**
	 * 根据多个环节id删除环节的相关信息
	 * @param _activityids  多个环节id
	 * @return
	 * @throws Exception
	 */
	public void deleteActivityRelationByActivityIds(String _activityids) throws Exception;

	/**
	 * 判断流程文件是否已编辑锁定，如果没有就锁定，返回true（表示锁定成功，可以编辑了），否则返回false（表示锁定失败，原状态就是锁定状态）
	 * @param _cmpfileid 流程文件id
	 * @param _cmpfilename 流程文件名称
	 * @param _username  操作用户名
	 * @return
	 * @throws Exception
	 */
	public boolean lockCmpFileById(String _cmpfileid, String _cmpfilename, String _username) throws Exception;

	/**
	 * 某个流程文件编辑完后要解除流程文件的编辑锁，即其他人可以编辑了
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public void unlockCmpFileById(String _cmpfileid) throws Exception;

	/**
	 * 判断某个流程文件是否处于锁定状态
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public boolean isCmpFileLocked(String _cmpfileid) throws Exception;

	/**
	 * 得到所有处于锁定状态的流程文件信息
	 * @return
	 * @throws Exception
	 */
	public String getAllLockedCmpFiles() throws Exception;

	public String getServerCmpfilePath(String _cmpfileid, HashMap _wfmap) throws Exception;

	/**
	 * 发布所有状态为【有效】的流程文件为1.0版本，并且清空历史记录
	 * @param _wfmap
	 * @throws Exception
	 */
	public HashMap publishAllCmpFile(HashMap _wfmap) throws Exception;

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
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _overwrite) throws Exception;

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
	 * @param _showreffile  是否有正文，如果有正文，则在服务器端合并正文和流程说明部分
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _showreffile, boolean _overwrite) throws Exception;

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
	public String addCmpfileHist(String _cmpfileid, String _cmpfilename, String _newversionno, boolean _overwrite) throws Exception;

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
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, String _cmpfile_histid, HashMap _wfmap, String _doc64code) throws Exception;

	/**
	 * 得到某个流程文件的word格式内容
	 * @param _cmpfileid 流程文件id
	 * @param _wfmap     流程图片的内容!!
	 * @param _onlywf    是否只有流程说明部分!!
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(String _cmpfileid, HashMap _wfmap, boolean _onlywf) throws Exception;

	/**
	 * 查看流程后判断是否要记录日志
	 * @param _cmpfileid 流程文件id
	 * @param _userid    用户id
	 * @param _clicktime 查看时间
	 * @return
	 * @throws Exception
	 */
	public boolean clickCmpFile(String _cmpfileid, String _userid, String _clicktime) throws Exception;

	/**
	 * 删除流程文件的一个历史版本的所有记录
	 * @param _cmpfile_histid 历史版本id
	 * @throws Exception
	 */
	public void deleteCmpFileHistById(String _cmpfile_histid) throws Exception;

	/**
	 * 删除流程文件的所有历史版本的所有记录，并且更新文件发布日期和版本号为空,状态为“编辑中”
	 * @param _cmpfileid 流程文件id
	 * @throws Exception
	 */
	public void deleteAllCmpFileHistByCmpfileId(String _cmpfileid) throws Exception;

	/**
	 * 一个BOM图中的所有热点的RiskVO
	 * @param _bomtype  "RISK"、"PROCESS"、"CMPFILE"
	 * @param _datatype  "BLCORPNAME"、"ICTYPENAME"
	 * @param _alldatas  BOM图所有热点值，只有机构才需要设置
	 * @param _isSelfCorp  是否查询本机构
	 * @return
	 * @throws Exception
	 */
	public Hashtable getHashtableRiskVO(String _bomtype, String _datatype, ArrayList _alldatas, boolean _isSelfCorp) throws Exception;

	/*----------------流程与风险模块结束---------------------*/
	/*-------------------  法规导出开始   ---------------------*/
	public HashMap getXMlFromTable1000Records(String _dsName, String table, int _beginNo) throws Exception;

	public HashMap getXMlFromTable500Records(String _dsName, String table, int _beginNO, String joinSql, String _condition) throws Exception;

	public String importXmlToTable1000Records(String _dsName, String path, HashMap _compareTable, HashMap conditionMap) throws Exception;

	public String getUpdataLawDataSchedule() throws Exception;

	public boolean createFolder(String folderName) throws Exception;

	/*-------------------  法规导出结束   ---------------------*/
	/**
	 * 将制度主表中附件迁移到子表中，只能正常使用一次
	 */
	public void removeRuleAttachfileToRuleitem() throws Exception;

	/*-----------------------根据xml模板生成word----------------------------*/
	public String[] getWordContents(HashMap[] maps, String template_name) throws Exception;

	/**
	 * 根据不同类型导入数据-wdl
	 * @param str_content 
	 * @param remarks
	 * @param template_id
	 * @param type
	 * @return
	 */
	public String[][] InputInfo(String[][] str_content, String remarks, String template_id, String type);

	/**
	 * 插入数据-wdl
	 * @param map
	 * @param new_list
	 * @param tablename
	 * @param tree_codekey
	 */
	public void Insert_InputInfo(Map map, List new_list, String tablename, String tree_codekey);

	public String[] ImportVisioToProcess(String cmpfileid, String cmpfilecode, String str) throws Exception;

	public String getPostAndDutyHtmlStr(String wfactiveID, String wfprocessID, String post_id[]) throws Exception;

	public HashVO[] getMyfavoriteTreeBillVO(String userID) throws Exception;

	/*---------------------- 合同范本-->起草文档复制 -----------------------*/
	public HashMap bargainCopyFile(HashMap fileMap) throws Exception;

	public void exportCMPFileAsDocFile(String path) throws Exception;

	public String getXMLProcess(String _processid) throws Exception;

	public void importXMLProcess(String xmlcontent, String currProcessid) throws Exception;

	/**
	 * 大丰农商行数据接口【李春娟/2013-07-23】
	 * 以后可能要删掉该方法
	 * @param path
	 * @throws Exception
	 */
	public void importDFDatas() throws Exception;

	/************************/
	public BillCellVO getDeptScoreCellVO(HashMap _condition) throws Exception;

	/**
	 * 新的违规积分直接生效功能【李春娟/2014-11-04】
	 * @param _id 积分登记主表（SCORE_REGISTER）主键
	 * @throws Exception
	 */
	public void effectScoreByRegisterId(String _id) throws Exception;

	//发布违规积分认定通知书
	public HashMap<String, String> publishEffectScore(HashMap hashmap) throws Exception;

	/**
	 * 新的违规积分我的积分--积分确认 Gwang 2014-11-26
	 * @param _id （score_user）主键
	 * @throws Exception
	 */
	public void effectScoreById(String _id) throws Exception;

	public void sendSMS(List tell_msg) throws Exception;

	/**
	 * 在服务器端创建流程文件最新版本的word文档，在上传office控件下的word目录中，以历史版本主表id为文档名称【李春娟/2015-02-11】
	 * @param _cmpfileid
	 * @return				返回文件名称，如123.doc
	 * @throws Exception
	 */
	public String createCmpfileByHistWord(String _cmpfileid) throws Exception;
	
	public boolean outputAllCmpFileByHisWord() throws Exception;
}
