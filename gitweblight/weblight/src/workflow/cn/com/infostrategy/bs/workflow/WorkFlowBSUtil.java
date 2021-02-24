package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.workflow.msg.MessageBSUtil;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;

public class WorkFlowBSUtil {

	private TBUtil tbUtil = null; //
	private CommDMO commDMO = null; //
	private HashMap allCorpsCacheMap = null; //

	public void dealHiddenMsg(HashVO[] hvs, String _prinstanceId) throws Exception {
		dealHiddenMsg(hvs, _prinstanceId, null); //
	}

	/**
	 * 处理是否隐藏/屏蔽处理意见,即用*号表示!
	 * @param hvs
	 * @param _prinstanceId
	 * @throws Exception
	 */
	public void dealHiddenMsg(HashVO[] hvs, String _prinstanceId, String _loginUserId) throws Exception {
		if (new TBUtil().getSysOptionBooleanValue("流程意见永不加密", false)) { //如果定义了流程意见永不加密=Y,则直接返回,即不做加密处理了
			for (int i = 0; i < hvs.length; i++) { //
				hvs[i].setAttributeValue("submitmessage_viewreason", "因为系统有总开关参数[流程意见永不加密=Y],所以能看"); //
			}
			return; //
		}
		CommDMO commDMO = new CommDMO(); //
		//然后如果定义消息屏蔽,则还要进行处理
		String str_loginUserid = null;
		if (_loginUserId != null) {
			str_loginUserid = _loginUserId;
		} else {
			str_loginUserid = new WLTInitContext().getCurrSession().getLoginUserId(); //
		}
		String str_rootInstId = commDMO.getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceId; //
		}

		//先算出我在该流程中走过的
		String str_1 = "select t1.prinstanceid,t1.processid,t1.createbyid,t1.curractivity,t2.fromparentactivity from pub_wf_dealpool t1 left join pub_wf_prinstance t2 on t1.prinstanceid=t2.id where (t1.prinstanceid='" + _prinstanceId + "' or t1.rootinstanceid='" + str_rootInstId + "') and (t1.participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "')"; //

		HashVO[] hvs_myWalkedCode = commDMO.getHashVoArrayByDS(null, str_1); //

		//		HashVO[] hvs_myWalkedCode = commDMO.getHashVoArrayByDS(null, "select t1.prinstanceid,t1.processid,t1.createbyid,t2.code activitycode,t2.belongdeptgroup activitybelongdeptgroup " + // 
		//				"from pub_wf_dealpool t1 left join pub_wf_activity t2 on t1.curractivity=t2.id where (t1.prinstanceid='" + _prinstanceId + "' or t1.rootinstanceid='" + str_rootInstId + "') " + // 
		//				"and (t1.participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "')"); //我走过的所有环节!!
		//		
		String[] str_allCreateByIds = new String[hvs_myWalkedCode.length]; //创建者,即
		String[] str_myparinstids = new String[hvs_myWalkedCode.length]; //我人父亲流程
		HashSet hst_thisActids = new HashSet(); //
		HashSet hst_parentActids = new HashSet(); //
		for (int i = 0; i < hvs_myWalkedCode.length; i++) {
			str_allCreateByIds[i] = hvs_myWalkedCode[i].getStringValue("createbyid"); // 
			str_myparinstids[i] = hvs_myWalkedCode[i].getStringValue("prinstanceid"); // 
			if (hvs_myWalkedCode[i].getStringValue("curractivity") != null) {
				hst_thisActids.add(hvs_myWalkedCode[i].getStringValue("curractivity")); //
			}
			if (hvs_myWalkedCode[i].getStringValue("fromparentactivity") != null) { //如果本流程是子流程,则这就是本子流程所在父亲流程中的组名!!
				hst_parentActids.add(hvs_myWalkedCode[i].getStringValue("fromparentactivity")); //
			}
		}

		//计算我经过的环节的组名
		String[] str_thisActids = (String[]) hst_thisActids.toArray(new String[0]); //
		String[] str_parentActids = (String[]) hst_parentActids.toArray(new String[0]); //

		String str_2 = "";
		str_2 = str_2 + "select id,code,wfname,belongdeptgroup,'本流程环节' c1 from pub_wf_activity where id in (" + getTBUtil().getInCondition(str_thisActids) + ")  union all "; //
		str_2 = str_2 + "select id,code,wfname,belongdeptgroup,'父流程环节' c1 from pub_wf_activity where id in (" + getTBUtil().getInCondition(str_parentActids) + ")"; //
		HashVO[] hvs_allActs = commDMO.getHashVoArrayByDS(null, str_2); //
		HashSet hst_thisWFActGroups = new HashSet();
		HashSet hst_parentWFActGroups = new HashSet();
		for (int i = 0; i < hvs_allActs.length; i++) {
			String str_groupName = hvs_allActs[i].getStringValue("belongdeptgroup"); //
			if (str_groupName != null) { //可能没有定义组!即直接画框
				String str_type = hvs_allActs[i].getStringValue("c1"); //
				if (str_type.equals("本流程环节")) { //如果是本流程环节
					hst_thisWFActGroups.add(str_groupName); //
				} else if (str_type.equals("父流程环节")) {
					hst_parentWFActGroups.add(str_groupName); //
					System.out.println("成功发现这是子流程,且算出它对应的父流程中的环节名[" + hvs_allActs[i].getStringValue("wfname") + "],组名[" + hvs_allActs[i].getStringValue("belongdeptgroup") + "]"); //
				}
			}
		}
		String[] str_myWalkedGroup = (String[]) hst_thisWFActGroups.toArray(new String[0]); //我在本流程中处理经过的组,如果我是父流程,则自然
		String[] str_myWalkedParentGroup = (String[]) hst_parentWFActGroups.toArray(new String[0]); //我所经过的父流程组!只有本人在子流程中,这个才会有值!

		//str_myWalkedGroup[i] = hvs_myWalkedCode[i].getStringValue("activitybelongdeptgroup"); //所属什么组

		//		HashSet hst_walkedgroup = new HashSet(); //
		//		for (int i = 0; i < str_myWalkedGroup.length; i++) {
		//			if (str_myWalkedGroup[i] != null && !str_myWalkedGroup[i].trim().equals("")) {
		//				hst_walkedgroup.add(str_myWalkedGroup[i]); //
		//			}
		//		}
		//		str_myWalkedGroup = (String[]) hst_walkedgroup.toArray(new String[0]); //唯一性过滤!!

		if (hvs_myWalkedCode.length <= 0) { //如果我从没参与过该流程,则统统不能看!
			for (int i = 0; i < hvs.length; i++) {
				hvs[i].setAttributeValue("submitmessage_viewreason", "因为我从来没有参与过该流程,所以加密"); //
				if (hvs[i].getStringValue("submitmessage") != null) {
					hvs[i].setAttributeValue("submitmessage", "*****"); //如果我从来参与过该流程,则统统不让看
				}
				if (hvs[i].getStringValue("submitmessagefile") != null) {
					hvs[i].setAttributeValue("submitmessagefile", "*****"); //如果我从来参与过该流程,则统统不让看
				}
			}
		} else { //我参与了该流程!!!
			for (int i = 0; i < hvs.length; i++) { //遍历每一条记录!
				StringBuilder sb_reason = new StringBuilder(); //为了更友好的在前台知道为什么这条意见能被看到或被隐藏,把原因也计算出来!!
				if (str_loginUserid.equals(hvs[i].getStringValue("participant_user")) || str_loginUserid.equals(hvs[i].getStringValue("participant_accruserid"))) { //如果该条任务的处理就是登录人员,则根本不做屏蔽处理,因为这条任务就是我的,而且意见就是我本人填写的!!
					sb_reason.append("因为我就是这条任务的待办者,所以能看"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue;
				}
				if (getTBUtil().isExistInArray(hvs[i].getStringValue("id"), str_allCreateByIds)) { //如果这条任务是本人任务的创建者,即我是本人是B,A提交给我的意见,我肯定能看到!
					sb_reason.append("因为这条任务就是提交给我的,所以能看"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				String str_taskid = hvs[i].getStringValue("id"); //任务id
				String str_prinstid = hvs[i].getStringValue("prinstanceid"); //这条任务的流程实例id
				String str_processid = hvs[i].getStringValue("processid"); //流程图id
				String str_lifecycle = hvs[i].getStringValue("lifecycle"); //生命同期
				String str_hidActivitys = hvs[i].getStringValue("curractivity_iscanlookidea"); //指定本环节可以被哪些环节查看!!

				//System.out.println("什么内容:\r\n" + hvs[i].getSBStr());  //

				if ("E".equals(str_lifecycle)) { //如果是主流程的结束点,则都能看,即所谓的流程最终意见!!
					sb_reason.append("因为这条任务就是流程结束点,最终意见所有人都能看,所以能看"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				if (str_hidActivitys != null && str_hidActivitys.trim().equals("*")) {
					sb_reason.append("因为环节中有特定过滤约束是[*],即直接对所有人开放,所以能看"); ////
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				if (getTBUtil().isExistInArray(str_prinstid, str_myparinstids)) { //如果我参与了该流程,在没有产生子流程时肯定都是这种情况,即旧版本的机制!!
					if (str_hidActivitys != null && !str_hidActivitys.trim().equals("")) { //如果定义了屏蔽,才处理,没定义则表示该环节上的意见可以被任何人看
						boolean isMineWalkedThisProcess = false; //我是否走过这个流程???
						for (int j = 0; j < hvs_myWalkedCode.length; j++) { //
							if (str_processid.equals(hvs_myWalkedCode[j].getStringValue("processid"))) { //如果我走过了该流程!
								isMineWalkedThisProcess = true; //
								break;
							}
						}
						if (isMineWalkedThisProcess) { //如果这条记录所在的流程正是我我走过的流程!!即如果我都没在该流程中,则不计算,因为可能不同的流程中有相同编码的环节!!
							boolean isBlackList = false; //是否是黑白单
							if (str_hidActivitys.startsWith("-(")) { //如果是黑名单
								isBlackList = true; //
								str_hidActivitys = str_hidActivitys.substring(2, str_hidActivitys.length() - 1); //去掉第一个"-"号,后面才是真正的环节列表!
							}
							String[] str_hids = getTBUtil().split(str_hidActivitys, ";"); //用分号隔开!
							if (!isRealShow(str_hids, hvs_myWalkedCode, isBlackList)) { //如果不允许被看,则置*号
								sb_reason.append("因为环节中有特定过滤约束【" + str_hidActivitys + "】（指定该环节上的意见(只/不)能被哪些环节查看）,\r\n本人参与校验失败,所以加密！"); ////
								if (hvs[i].getStringValue("submitmessage") != null) {
									hvs[i].setAttributeValue("submitmessage", "*****"); //如果我从来参与过该流程,则统统不让看
								}
								if (hvs[i].getStringValue("submitmessagefile") != null) {
									hvs[i].setAttributeValue("submitmessagefile", "*****"); //如果我从来参与过该流程,则统统不让看
								}
							} else {
								sb_reason.append("环节中有特定过滤约束【" + str_hidActivitys + "】（指定该环节上的意见(只/不)能被哪些环节查看）,\r\n本人参与校验成功,所以能看！"); ////
							}
						} else { //
							sb_reason.append("环节中有特定过滤约束【" + str_hidActivitys + "】（指定该环节上的意见(只/不)能被哪些环节查看）,\r\n但我没走过该流程,所以忽略条件,直接加密！"); ////
							if (hvs[i].getStringValue("submitmessage") != null) {
								hvs[i].setAttributeValue("submitmessage", "*****"); //如果我从来参与过该流程,则统统不让看
							}
							if (hvs[i].getStringValue("submitmessagefile") != null) {
								hvs[i].setAttributeValue("submitmessagefile", "*****"); //如果我从来参与过该流程,则统统不让看
							}
						}
					} else { //如果需要进行隔组处理
						boolean isHiddenByDiffGroup = true; //要从系统参数取!!
						if (isHiddenByDiffGroup) { //
							String str_thistask_bldeptgroup = hvs[i].getStringValue("curractivity_belongdeptgroup", ""); //这条于什么组!!
							if (str_thistask_bldeptgroup == null || str_thistask_bldeptgroup.trim().equals("")) {
								sb_reason.append("因为这是与本人在同一个流程中的,没有特定过滤约束,但要求组分隔,但本记录所属组为空,所以能看"); ////
							} else {
								String str_mychildren_task_bldeprGroup = getMyChildrenTaskBlDeprname(hvs, str_taskid); //
								if (!str_thistask_bldeptgroup.equals(str_mychildren_task_bldeprGroup)) { //这条任务与他的儿子不是同一组,即是出去的意见!!
									sb_reason.append("因为这是与本人在同一个流程中的,没有特定过滤约束,但要求组分隔,且不是本组内的,\r\n但提交的是另外一组(从组[" + str_thistask_bldeptgroup + "]提交了组[" + str_mychildren_task_bldeprGroup + "]),即是部门出去的的意见,部门出去的意见大家都能看(临界处),所以能看"); ////
								} else {
									if (getTBUtil().isExistInArray(str_thistask_bldeptgroup, str_myWalkedGroup)) { //如果
										sb_reason.append("因为这是与本人在同一个流程中的,没有特定过滤约束,但要求组分隔,且出去的也是同一组内,\r\n这条任务的组是[" + str_thistask_bldeptgroup + "],本人走过的组有[" + getSBStr(str_myWalkedGroup) + "],是匹配成功的,即是一家里面的事,所以能看"); ////
									} else {
										sb_reason.append("因为这是与本人在同一个流程中的,没有特定过滤约束,但要求组分隔,且出去的也是同一组内,\r\n这条任务的组是[" + str_thistask_bldeptgroup + "],本人走过的组有[" + getSBStr(str_myWalkedGroup) + "],匹配失败,即这是别人家里的事,所以加密"); ////
										if (hvs[i].getStringValue("submitmessage") != null) {
											hvs[i].setAttributeValue("submitmessage", "*****"); //如果我从来参与过该流程,则统统不让看
										}
										if (hvs[i].getStringValue("submitmessagefile") != null) {
											hvs[i].setAttributeValue("submitmessagefile", "*****"); //如果我从来参与过该流程,则统统不让看
										}
									}
								}
							}
						} else {
							sb_reason.append("因为这是与本人在同一个流程中的,且没有特定过滤约束,且又没有要求分组相隔,所以能看"); ////
						}
					}
				} else { //如果我没有参与该流程,比如我在主流程但该任务是子流程,或者我在子流程但该任务在主流程!
					//邮储项目中中的需求是,会签时不需要部门之间屏蔽,而是只要在父流程中属于同一个"机构通道",则所有人都不屏蔽!!!
					//即父流程中的人能看会签内部的,同样会签内部的也能看外面的!但在父流程的机构通道中跨行时就不能看了....
					//流程实例不同还有一种情况是两个子流程之间的情况!!!
					boolean isChildWFCanLookAll = true;
					if (isChildWFCanLookAll) { //
						String str_parentid = hvs[i].getStringValue("parentinstanceid"); //
						if (str_parentid != null && !str_parentid.equals("")) { //如果不为空,则说明是子流程!!而的流程与他不同,说明我肯定是主流程中的参与者
							String str_blcorpName = hvs[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup"); //这条任务在父亲亲流程中的环节名称	
							if (getTBUtil().isExistInArray(str_blcorpName, str_myWalkedGroup) || getTBUtil().isExistInArray(str_blcorpName, str_myWalkedParentGroup)) { //
								sb_reason.append("这条任务与在不同流程实例,而且该任务是子流程,即我在父流程或者是另一个子流程分支中,\r\n且发现这个子流程所对应的父流程环节组[" + str_blcorpName + "]与我的本流程组[" + getSBStr(str_myWalkedGroup) + "]或父流程组[" + getSBStr(str_myWalkedParentGroup) + "]在同一范围,所以能看!"); //
							} else {
								sb_reason.append("这条任务与在不同流程实例,而且该任务是子流程,即我在父流程或者是另一个子流程分支中,\r\n且发现这个子流程所对应的父流程环节组[" + str_blcorpName + "]与我的本流程组[" + getSBStr(str_myWalkedGroup) + "]或父流程组[" + getSBStr(str_myWalkedParentGroup) + "]不在同一范围,所以加密!"); //
								hvs[i].setAttributeValue("submitmessage", "*****"); //
							}
						} else { //如果这条任务是父亲流程,这样一来,我应该就是子流程中的!
							String str_blcorpName = hvs[i].getStringValue("curractivity_belongdeptgroup"); //二分/一分
							if (getTBUtil().isExistInArray(str_blcorpName, str_myWalkedParentGroup)) { //我所经过的流程所属的父流程中的环节组名!
								sb_reason.append("这条任务与在不同流程实例,而且该任务是父流程,即我在子流程,\r\n且发现这个父流程所对应的环节组[" + str_blcorpName + "]与我所对应的父流程环节[" + getSBStr(str_myWalkedParentGroup) + "]相同,所以能看"); //
							} else {
								sb_reason.append("这条任务与在不同流程实例,而且该任务是父流程,即我在子流程,\r\n且发现这个父流程所对应的环节组[" + str_blcorpName + "]与我所对应的父流程环节[" + getSBStr(str_myWalkedParentGroup) + "]不相同,所以加密!");
								hvs[i].setAttributeValue("submitmessage", "*****");
							}
						}
					} else {
						if ("CC".equals(str_lifecycle) || "EC".equals(str_lifecycle)) { //如果是进入子流程或子流程结束的意见,则大家都都看!!
							sb_reason.append("因为这条任务是进入子流程或子流程结束的,所以能看"); ////
						} else {
							sb_reason.append("这个任务与本人不在同一流程中,所以加密"); ////
							if (hvs[i].getStringValue("submitmessage") != null) {
								hvs[i].setAttributeValue("submitmessage", "*****"); //如果我从来参与过该流程,则统统不让看
							}
							if (hvs[i].getStringValue("submitmessagefile") != null) {
								hvs[i].setAttributeValue("submitmessagefile", "*****"); //如果我从来参与过该流程,则统统不让看
							}
						}
					}
				}
				hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
			}
		}
	}

	private String getSBStr(String[] _array) {
		StringBuilder sb_str = new StringBuilder(); //
		for (int i = 0; i < _array.length; i++) {
			sb_str.append(_array[i] + ";"); //
		}
		return sb_str.toString(); //
	}

	private String getMyChildrenTaskBlDeprname(HashVO[] hvs, String str_taskid) {
		for (int i = 0; i < hvs.length; i++) {
			String str_createbyid = hvs[i].getStringValue("createbyid"); //创建的
			if (str_taskid.equals(str_createbyid)) { //这是我的儿子!!!但可能有多个儿子!!!但这多个儿子肯定都是一个组,所以只要一个就够了!
				return hvs[i].getStringValue("curractivity_belongdeptgroup"); //
			}
		}
		return null;
	}

	private String[] getMySameGroupTaskCreateByIds(HashVO[] _hvs, String[] _myWalkedGroup) {
		HashSet hst_temp = new HashSet(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_thisgroup = _hvs[i].getStringValue("curractivity_belongdeptgroup"); //
			for (int j = 0; j < _myWalkedGroup.length; j++) {
				if (_myWalkedGroup[j] != null && _myWalkedGroup[j].equals(str_thisgroup)) { //与我同一组!
					if (!_hvs[i].getStringValue("createbyid", "").equals("")) { //
						hst_temp.add(_hvs[i].getStringValue("createbyid")); //记录这条记录的来源!加入!!
					}
					break; //
				}
			}
		}
		return (String[]) hst_temp.toArray(new String[0]);
	}

	//是否真正隐藏..
	private boolean isRealShow(String[] str_hidCodes, HashVO[] _myWalkedHVS, boolean isBlackList) {
		String[] str_myWalkedCode = new String[_myWalkedHVS.length]; //
		for (int i = 0; i < _myWalkedHVS.length; i++) {
			str_myWalkedCode[i] = _myWalkedHVS[i].getStringValue("activitycode"); //取得环节编码
		}

		if (!isBlackList) { //如果是白名单!即
			return getTBUtil().containTwoArrayCompare(str_hidCodes, str_myWalkedCode); //如果我走过的环节中有一项品配上了定义的,则说明可以查看!!
		} else { //如果是黑名单
			for (int i = 0; i < str_myWalkedCode.length; i++) {
				if (!getTBUtil().isExistInArray(str_myWalkedCode[i], str_hidCodes)) { //我只要有一个环节不在定义的黑名单中,则就对我开放!
					return true; //
				}
			}
			return false; //如果很不巧,我都在被列的黑名单中,则不显示
		}
	}

	/**
	 * 取得待办任务与已办任务的结果数!!
	 * 以模板编码分类!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getTaskClassCountMap(HashMap _parMap) throws Exception {
		boolean isSuperShow = (Boolean) _parMap.get("isSuperShow"); //是否是超级查看??
		Object templetcode = _parMap.get("templetcode");
		Object model = _parMap.get("model");
		String str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId(); //
		StringBuffer sb1 = new StringBuffer("select templetcode,count(*) c1 from pub_task_deal " + (isSuperShow ? "" : "where (dealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "')") + " ");
		StringBuffer sb2 = new StringBuffer("select templetcode,count(*) c1 from pub_task_off  " + (isSuperShow ? "" : "where (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "')") + " ");
		if (templetcode != null && !"".equals((String) templetcode)) {
			sb1.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
			sb2.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
		}
		sb1.append(" group by templetcode ");
		sb2.append(" group by templetcode ");
		HashMap retrunMap = new HashMap(); //
		if (model == null || "dealTask".equals(model)) {
			retrunMap.put("待办任务", getClassCountBySQL(sb1.toString())); //待办人或授权人等于我的
		}

		if (model == null || "offTask".equals(model)) {
			if (getTBUtil().getSysOptionBooleanValue("已办任务同一任务是否只显示最后一条", false)) {
				//已办任务相同任务合并 【杨科/2013-03-08】
				StringBuffer sb22 = new StringBuffer(" select a.templetcode,count(*) c1 from pub_task_off a ");
				sb22.append(" inner join ( select pkvalue, max(dealtime) dealtime from pub_task_off where 1=1 ");
				sb22.append((isSuperShow ? "" : " and (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "));
				sb22.append(" group by pkvalue ) b on a.pkvalue = b.pkvalue and a.dealtime = b.dealtime ");
				sb22.append((isSuperShow ? " where 1=1 " : " where (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "));
				if (templetcode != null && !"".equals((String) templetcode)) {
					sb22.append(" and a.templetcode='" + templetcode + "' ");
				}
				sb22.append(" group by a.templetcode ");
				retrunMap.put("已办任务", getClassCountBySQL(sb22.toString())); //实际已办人等于我的!
			} else {
				retrunMap.put("已办任务", getClassCountBySQL(sb2.toString())); //实际已办人等于我的!
			}
		}
		//retrunMap.put("已办任务", getClassCountBySQL(sb2.toString())); //实际已办人等于我的!
		if (model == null || "msg".equals(model)) {
			retrunMap.put("消息中心", new cn.com.infostrategy.bs.workflow.msg.MsgBsUtil().getMsgCountMap(new HashMap(), -1)); //
		}
		if (model == null || "bd".equals(model)) {
			retrunMap.put("意见补登", getBDCount(_parMap, str_loginUserId));//sunfujun/意见补登
		}

		if (model == null || "pr".equals(model)) {
			retrunMap.put("传阅", new MessageBSUtil().getPassReadClassCountMap(_parMap)); //传阅 【杨科/2012-12-14】
		}
		return retrunMap; //
	}

	//取得某一个分类的总数!!!
	private HashMap getClassCountBySQL(String _sql) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, _sql); //
		if (hvs != null && hvs.length > 0) {
			ArrayList al_templetCodes = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				al_templetCodes.add(hvs[i].getStringValue("templetcode")); //加入模板编码!
			}
			//从数据库中找!!!
			HashVO[] templetHVs = commDMO.getHashVoArrayByDS(null, "select templetcode,templetname,tablename,savedtablename,pkname from pub_templet_1 where templetcode in (" + getTBUtil().getInCondition(al_templetCodes) + ")"); //找到模板编码与名称的映射的哈希表!!

			//如果没找到,则从XML中找!! 因为新的机制是可以从XML中找!!! 结束导致发布时因为数据库中是没有数据的,所以工作流任务中心报错!!
			ArrayList al_notFindFromDB = new ArrayList(); //没有从数据库中发现的清单!!!
			for (int i = 0; i < al_templetCodes.size(); i++) {
				String str_itemcode = (String) al_templetCodes.get(i); //
				boolean isFinded = false; //是否发现标记!!
				for (int j = 0; j < templetHVs.length; j++) {
					if (str_itemcode != null && str_itemcode.equals(templetHVs[j].getStringValue("templetcode"))) { //如果数据库中有!!
						isFinded = true; //
						break; //
					}
				}
				if (!isFinded) { //如果在数据库中没发现,则加入清单!!
					al_notFindFromDB.add(str_itemcode); //加入!
				}
			}

			if (al_notFindFromDB.size() > 0) { //如果找到!!//去XML中找出对应的模板!!!
				HashVO[] hvs_xml = new InstallDMO().getInstallTempletsAsHashVO((String[]) al_notFindFromDB.toArray(new String[0])); //
				if (hvs_xml != null && hvs_xml.length > 0) { //如何找到
					HashVO[] spanVOs = new HashVO[templetHVs.length + hvs_xml.length]; //	
					System.arraycopy(templetHVs, 0, spanVOs, 0, templetHVs.length); //拷贝!
					System.arraycopy(hvs_xml, 0, spanVOs, templetHVs.length, hvs_xml.length); //拷贝!
					templetHVs = spanVOs; //重置!!
				}
			}

			LinkedHashMap returnMap = new LinkedHashMap(); //返回的哈希表!!带排序的,省得每次刷新时还不一样!
			for (int i = 0; i < hvs.length; i++) {
				String str_itemCode = hvs[i].getStringValue("templetcode"); //模板编码!
				String str_templetName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "templetname"); //
				String str_tableName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "tablename"); //
				String str_savedTableName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "savedtablename"); //
				String str_pkName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "pkname"); //
				returnMap.put(str_itemCode, new String[] { str_templetName, str_tableName, str_savedTableName, str_pkName, hvs[i].getStringValue("c1") }); //返回key=模板编码,Value={"模板名称","数量"}的哈希表!!
			}
			return returnMap; //
		}
		return null; //
	}

	/**
	 * 手工添加接收人员时,第一次打开时取出登录人所在机构,以及这个机构的所有人员!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsers(HashMap _parMap) throws Exception {
		String str_accrModel = null; // 
		if (_parMap != null) {
			String str_billType = (String) _parMap.get("billtype"); //单据类型
			String str_busiType = (String) _parMap.get("busitype"); //业务类型
			str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType); //
		}
		String str_loginuserId = new WLTInitContext().getCurrSession().getLoginUserId(); //登录人员id
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvsCorp = commDMO.getHashVoArrayByDS(null, "select userid,userdept,isdefault from pub_user_post where userid='" + str_loginuserId + "'"); //
		String str_corpId = null; //先找出机构!!!
		if (hvsCorp.length > 0) {
			boolean isFinded = false; //
			for (int i = 0; i < hvsCorp.length; i++) {
				if ("Y".equals(hvsCorp[i].getStringValue("isdefault"))) { //如果是默认机构
					str_corpId = hvsCorp[i].getStringValue("userdept"); //用户机构!!!
					isFinded = true; //
					break; //
				}
			}
			if (!isFinded) { //如果没发现默认机构!
				str_corpId = hvsCorp[0].getStringValue("userdept"); //取第一个
			}
		}
		if (str_corpId == null) {
			return null; //
		}
		//找出该机构的所有人员,同时计算出其所有角色与机构!!
		return getCorpAndUserByCorpId(str_corpId, str_accrModel); //
	}

	/**
	 * 手工添加接收人员时,选择左边机构树刷新右边的员!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsersBycorpId(HashMap _parMap) throws Exception {
		String str_corpId = (String) _parMap.get("corpid"); //
		if (_parMap.containsKey("isaddaccr") && !(Boolean) _parMap.get("isaddaccr")) { //是否对人做授权,即如果某个人做了授权设置,是否计算出被授权人?
			return getCorpAndUserByCorpId(str_corpId, null, false);
		}
		String str_billType = (String) _parMap.get("billtype"); //单据类型
		String str_busiType = (String) _parMap.get("busitype"); //业务类型
		String str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType); //
		return getCorpAndUserByCorpId(str_corpId, str_accrModel); //
	}

	private String getAccrModelByBillTypeAndBusitype(String str_billType, String str_busiType) throws Exception {
		String[][] str_accrModels = getCommDMO().getStringArrayByDS(null, "select accrmodel from pub_workflowassign where billtypecode='" + str_billType + "' and busitypecode='" + str_busiType + "'"); //审批模式!!!
		if (str_accrModels.length == 0) { //
			throw new WLTAppException("做授权模块计算时发现,根据单据类型[" + str_billType + "],业务类型[" + str_busiType + "]在流程分配表中没有找到一条分配记录,这是不对的,请与管理员联系!"); //
		} else if (str_accrModels.length > 1) {
			throw new WLTAppException("做授权模块计算时发现,根据单据类型[" + str_billType + "],业务类型[" + str_busiType + "]在流程分配表中找到两条以上的配置信息!\r\n这将会导致得到随机的授权模块,是不允许的,请与管理员联系!!"); //
		}
		String str_accrModel = str_accrModels[0][0]; //赋值
		return str_accrModel;
	}

	/**
	 * 根据机构id找出这个机构下的所有人员!! 同时根据指定的授权模块对人员进行授权计算!!
	 * @param _corpId
	 * @return
	 * @throws Exception
	 */

	private HashMap getCorpAndUserByCorpId(String _corpId, String _accrModel) throws Exception {
		return getCorpAndUserByCorpId(_corpId, _accrModel, true);
	}

	/**
	 * 根据机构类型和角色来过滤
	 * 高级查询用
	 * @param param
	 * @return
	 */
	public HashMap getUserCorpAndUsersBycorpTypeAndRole(HashMap param) throws Exception {
		DataPolicyDMO ddmo = new DataPolicyDMO();
		HashMap map = ddmo.getDataPolicyTargetCorpsByUserId(new WLTInitContext().getCurrSession().getLoginUserId(), "工作流选人机构数据权限", "id");
		String userCondition = getTBUtil().getSysOptionStringValue("工作流人员状态过滤条件", null);//太平集团有效人员状态为0，故增加参数【李春娟/2017-10-19】
		String[] ids = (String[]) map.get("AllCorpIDs");
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select ");
		sb_sql.append("t1.userid,");
		sb_sql.append("t2.code usercode,");
		sb_sql.append("t2.name username,");
		sb_sql.append("t1.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename ");
		sb_sql.append("from pub_user_post t1 ");
		sb_sql.append("left join pub_user      t2 on t1.userid=t2.id "); //
		sb_sql.append("left join pub_user_role t3 on t2.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		sb_sql.append("where 1=1 "); //
		if (userCondition != null && !userCondition.trim().equals("")) {
			sb_sql.append(" and (t2.status is null or t2." + userCondition + ") ");
		}
		sb_sql.append("and t1.userdept in (select id from pub_corp_dept where id in(" + getTBUtil().getInCondition(ids) + ") " + (((List) param.get("corptype")).size() > 0 ? " and corptype in (" + getTBUtil().getInCondition((List) param.get("corptype")) + ")" : "") + ")"); //机构等于登录人员所在的机构!!
		sb_sql.append("and t4.name in (" + getTBUtil().getInCondition((List) param.get("role")) + ")");
		sb_sql.append("order by t1.seq desc");
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //如果已有!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //取得
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //重置角色id
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //重置角色name
				userMap.put(str_userId, hvoItem); //重新塞入!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //人员编码
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //人员名称
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //角色id
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //角色名称
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////做了合并的用户清单!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname");
		//补上人机构的名称
		HashMap returnMap = new HashMap(); //
		returnMap.put("users", hvsUserSpan); //用一机构下的所有人员!!!
		return returnMap; //

	}

	private HashMap getCorpAndUserByCorpId(String _corpId, String _accrModel, boolean isaddaccr) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_sql = new StringBuilder(); //
		String userCondition = getTBUtil().getSysOptionStringValue("工作流人员状态过滤条件", null);//太平集团有效人员状态为0，故增加参数【李春娟/2017-10-19】
		sb_sql.append("select ");
		sb_sql.append("t1.userid,");
		sb_sql.append("t2.code usercode,");
		sb_sql.append("t2.name username,");
		sb_sql.append("t1.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename ");
		sb_sql.append("from pub_user_post t1 ");
		sb_sql.append("left join pub_user      t2 on t1.userid=t2.id "); //
		sb_sql.append("left join pub_user_role t3 on t2.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		sb_sql.append("where 1=1 "); //
		if (userCondition != null && !userCondition.trim().equals("")) {
			sb_sql.append(" and (t2.status is null or t2." + userCondition + ") ");
		}
		sb_sql.append("and t1.userdept='" + _corpId + "' "); //机构等于登录人员所在的机构!!
		sb_sql.append("order by t1.seq desc"); //排序一下,保证每次出来的人员顺序都一样!这里增加了人员排序，后面逻辑给倒序了一把，所以这里也按seq倒序排，李春娟修改
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //如果已有!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //取得
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //重置角色id
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //重置角色name
				userMap.put(str_userId, hvoItem); //重新塞入!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //人员编码
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //人员名称
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //角色id
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //角色名称
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////做了合并的用户清单!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname"); //补上人机构的名称

		if (isaddaccr) {//进行授权人计算,即如果某个用户授权给了另外一个人,则要显示授权人的名称!!! 比如华总授权给了李克振,则如果人员是华总的话,则要将显示改成“李克振(华总授权)”!!!!
			HashMap allAccreditDefinemap = getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //取得授权定义的记录!!
			if (allAccreditDefinemap.size() > 0) { //如果有授权定义才做处理
				ArrayList al_temp = new ArrayList(); //
				for (int i = 0; i < hvsUserSpan.length; i++) { //遍历所有人员!!!
					al_temp.add(hvsUserSpan[i]); //先加入!!!
					String str_oldUserId = hvsUserSpan[i].getStringValue("userid"); //
					HashVO proxyUserVO = getAccredProxyUserVO(allAccreditDefinemap, _accrModel, str_oldUserId); //找到代理人的VO
					if (proxyUserVO != null) { //如果找到了代理人,则增加代理人的信息
						HashVO cloneProxyUserHVO = hvsUserSpan[i].clone(); //
						String str_username1 = hvsUserSpan[i].getStringValue("username") + "(授权" + proxyUserVO.getStringValue("proxyusername") + ")"; //
						String str_username2 = proxyUserVO.getStringValue("proxyusername") + "(" + hvsUserSpan[i].getStringValue("username") + "授权)"; //这个以后可能需要参数配置!
						cloneProxyUserHVO.setAttributeValue("username", str_username1);//和workflowenginedmo搞成一样 永远显示 汪总( 授权孙富君) 这种形式

						//设置被授权人
						cloneProxyUserHVO.setAttributeValue("accruserid", hvsUserSpan[i].getStringValue("proxyuserid")); //
						cloneProxyUserHVO.setAttributeValue("accrusercode", hvsUserSpan[i].getStringValue("proxyusercode")); //
						cloneProxyUserHVO.setAttributeValue("accrusername", hvsUserSpan[i].getStringValue("proxyusername")); //

						al_temp.add(cloneProxyUserHVO); //先加入!!!
					}
				}
				hvsUserSpan = (HashVO[]) al_temp.toArray(new HashVO[0]); //重新赋值!!
			}
		}
		HashMap returnMap = new HashMap(); //
		returnMap.put("userCorp", _corpId); //用户机构
		returnMap.put("sameCorpUsers", hvsUserSpan); //用一机构下的所有人员!!!
		return returnMap; //
	}

	//重置相关信息
	private void resetHashVOItem(HashVO _hvoItem, HashVO _rowHVO, String _itemName, boolean _isfirst) {
		if (_hvoItem.getStringValue(_itemName) != null && !_hvoItem.getStringValue(_itemName).equals("")) {
			String str_oldRoleid = _hvoItem.getStringValue(_itemName); //取得旧的
			if (str_oldRoleid == null || str_oldRoleid.equals("")) { //如果旧的角色id为空,则直接设新的
				_hvoItem.setAttributeValue(_itemName, _rowHVO.getStringValue(_itemName)); //直接设新的
			} else {
				if (str_oldRoleid.indexOf(";") >= 0) { //如果已有分号!!
					_hvoItem.setAttributeValue(_itemName, str_oldRoleid + _rowHVO.getStringValue(_itemName) + ";"); //
				} else { //如果没有分号
					String str_newRoleId = (_isfirst ? ";" : "") + str_oldRoleid + ";" + _rowHVO.getStringValue(_itemName) + ";"; //新的id
					_hvoItem.setAttributeValue(_itemName, str_newRoleId); //
				}
			}
		}
	}

	/**
	 * 取得授权的信息,因为在流程引擎的参与者与手工添加新的接收人员时,都需要计算授权! 为了重用,所在放在这里!!!
	 * 以前手工添加时不计算授权,实际上是有问题的!!!
	 * @return
	 * @throws Exception
	 */
	public HashMap getAccreditAndProxyUsersMap(HashMap _allCorpCacheDataMap) throws Exception {
		HashMap returnMap = new HashMap(); //
		String str_currtime = getTBUtil().getCurrTime(); //取得当前时间(即是服务器端的时间)!!!
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select ");
		sb_sql.append("t1.id,"); //主键
		sb_sql.append("t1.accruserid,"); //授权人id,
		sb_sql.append("t2.code accrusercode,"); //授权人编码
		sb_sql.append("t2.name accrusername,"); //授权人名称
		sb_sql.append("t1.proxyuserid,"); //代理人主键
		sb_sql.append("t1.accrmodel,"); //授权模块!
		sb_sql.append("t3.code proxyusercode,"); //代理人编码
		sb_sql.append("t3.name proxyusername "); //代理人名称
		sb_sql.append("from pub_workflowaccrproxy t1 ");
		sb_sql.append("left join pub_user       t2 on t1.accruserid=t2.id "); //授权人关联
		sb_sql.append("left join pub_user       t3 on t1.proxyuserid=t3.id "); //代理人关联proxyuserdeptid,proxyuserdeptname
		sb_sql.append("where 1=1 "); //
		sb_sql.append("and t1.accrbegintime<'" + str_currtime + "' and (t1.accrendtime is null or t1.accrendtime>'" + str_currtime + "') "); //授权期限在有效期内!!!
		HashVO[] hvs_allAccrditAndProxy = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //找出所有授权的数据!!
		if (hvs_allAccrditAndProxy.length <= 0) {
			return returnMap; //
		}

		HashSet hst = new HashSet(); //
		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) {
			if (hvs_allAccrditAndProxy[i].getStringValue("proxyuserid") != null) {
				hst.add(hvs_allAccrditAndProxy[i].getStringValue("proxyuserid")); //
			}
		}
		String[] str_allProxyUserIds = (String[]) hst.toArray(new String[0]); //代理人!!!
		String str_proxyUserInCondition = getTBUtil().getInCondition(str_allProxyUserIds); //
		HashVO[] hvs_allProxyUserRoles = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.roleid,t2.code rolecode,t2.name rolename   from pub_user_role t1 left join pub_role t2 on t1.roleid=t2.id where t1.userid in (" + str_proxyUserInCondition + ")"); //
		HashVO[] hvs_allProxyUserCorps = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t2.code userdeptcode,t2.name userdeptname from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid in (" + str_proxyUserInCondition + ") and t1.isdefault='Y'"); //

		HashMap map_userRoles = spellHashVOsByField(hvs_allProxyUserRoles, "userid", new String[] { "roleid", "rolecode", "rolename" }, new boolean[] { true, true, false }, true); //合并角色
		HashMap map_userCorps = spellHashVOsByField(hvs_allProxyUserCorps, "userid", new String[] { "userdept", "userdeptcode", "userdeptname" }, new boolean[] { true, true, false }, false); //合并机构!!!

		//补上代理人的角色与机构!!
		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) {
			String str_proxyUserid = hvs_allAccrditAndProxy[i].getStringValue("proxyuserid"); //
			String[] str_proxyUserAllRoles = (String[]) map_userRoles.get(str_proxyUserid); //
			if (str_proxyUserAllRoles != null) { //因为代理人可能没设角色,所以要做非空判断!!!
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserroleid", str_proxyUserAllRoles[0]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserrolecode", str_proxyUserAllRoles[1]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserrolename", str_proxyUserAllRoles[2]); //
			}

			String[] str_proxyUserAllCorps = (String[]) map_userCorps.get(str_proxyUserid); //
			if (str_proxyUserAllCorps != null) { //因为代理人可能没设机构,所以要做非空判断!!!
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptid", str_proxyUserAllCorps[0]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptcode", str_proxyUserAllCorps[1]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptname", str_proxyUserAllCorps[2]); //
			}
		}

		//补上机构全称!
		appendWholeCorpNameByHashVOs(_allCorpCacheDataMap, hvs_allAccrditAndProxy, "proxyuserdeptid", "proxyuserdeptname"); //补上代理人的机构名称!! 比如华总授权给了李克振,则显示李克振的机构名称!!

		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) { //遍历!!
			String str_accruserid = hvs_allAccrditAndProxy[i].getStringValue("accruserid"); //授权人
			if (!returnMap.containsKey(str_accruserid)) { //以前是直接送入,但后来有了分模块授权,则是一个向量!!
				ArrayList al_temp = new ArrayList(); //
				al_temp.add(hvs_allAccrditAndProxy[i]); //
				returnMap.put(str_accruserid, al_temp); //
			} else {
				ArrayList al_temp = (ArrayList) returnMap.get(str_accruserid); //
				al_temp.add(hvs_allAccrditAndProxy[i]); //
				returnMap.put(str_accruserid, al_temp); //
			}
		}
		return returnMap; //
	}

	private HashMap spellHashVOsByField(HashVO[] _hvs, String _mapKeyField, String[] str_spancols, boolean[] _isFirstAddFH, boolean _isAppend) {
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_mapKeyValue = _hvs[i].getStringValue(_mapKeyField); //
			if (!returnMap.containsKey(str_mapKeyValue)) { //如果没有!!
				String[] str_rowValues = new String[str_spancols.length]; //
				for (int j = 0; j < str_rowValues.length; j++) {
					if (_isAppend) {
						str_rowValues[j] = (_isFirstAddFH[j] ? ";" : "") + _hvs[i].getStringValue(str_spancols[j], "") + ";"; //
					} else {
						str_rowValues[j] = _hvs[i].getStringValue(str_spancols[j], ""); //
					}
				}
				returnMap.put(str_mapKeyValue, str_rowValues); //
			} else {
				String[] str_rowValues = (String[]) returnMap.get(str_mapKeyValue); //
				for (int j = 0; j < str_rowValues.length; j++) {
					if (_isAppend) {
						str_rowValues[j] = str_rowValues[j] + _hvs[i].getStringValue(str_spancols[j], "") + ";"; //
					} else {
						str_rowValues[j] = _hvs[i].getStringValue(str_spancols[j], ""); //
					}
				}
				returnMap.put(str_mapKeyValue, str_rowValues); //重新塞入!!
			}
		}
		return returnMap; //
	}

	//对一个HashVO数组中的某个机构id的字段进行计算,找出其机构名称的全路径名!!
	public void appendWholeCorpNameByHashVOs(HashMap _allCorpCacheDataMap, HashVO[] _hvs, String _fromDeptidField, String _toDeptNameField) throws Exception {
		HashMap mapAllCorps = _allCorpCacheDataMap; //取得所有机构的缓存!!
		if (mapAllCorps == null) { //如果没有,则从本类的缓存中取,这就是因为该方法有可能被WorkFlowEngineDMO调用,也可能被本类调用,即手工添加接收人员时调用!! 所以需要这种判断!!
			mapAllCorps = getAllCorpsCacheMap(); //
		}
		HashMap mapIdHVO = (HashMap) mapAllCorps.get("AllCorpHashMap"); //机构Map
		for (int i = 0; i < _hvs.length; i++) {
			String str_corpId = _hvs[i].getStringValue(_fromDeptidField); //
			if (str_corpId != null && !str_corpId.trim().equals("")) {
				Object obj = mapIdHVO.get(str_corpId); //找到这个机构
				if (obj == null) {//找不到该机构，可能被删除了【李春娟/2015-07-29】
					_hvs[i].setAttributeValue(_toDeptNameField, ""); //设置空串，防止取值时未判断是否为空而报错
				} else {
					HashVO hvoCorpItem = (HashVO) obj;
					String str_nameLink = hvoCorpItem.getStringValue("$parentpathnamelink1"); //因为有了这个,所有很快!
					_hvs[i].setAttributeValue(_toDeptNameField, str_nameLink); //重置机构名称
				}
			}
		}
	}

	//取得被授权人(代理人)的信息,根据已算出的所有授权人信息经及当前表单与流程绑定的授权模块!
	public HashVO getAccredProxyUserVO(HashMap _allAccrditAndProxyMap, String _accrdModel, String _olduserid) {
		ArrayList al_proxyUsers = (ArrayList) _allAccrditAndProxyMap.get(_olduserid); //
		if (al_proxyUsers == null) {
			return null; //
		}
		HashVO[] hvs_AccrUsers = (HashVO[]) al_proxyUsers.toArray(new HashVO[0]); ////
		HashVO hvo_accr = null; //
		if (_accrdModel == null || _accrdModel.trim().equals("")) { //如果流程分配定义的模式为空,则取默认的授权模式!!!
			for (int k = 0; k < hvs_AccrUsers.length; k++) {
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals("") || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf("默认授权") >= 0) {
					hvo_accr = hvs_AccrUsers[k]; //
					break; //
				}
			}
		} else { //如果指定了模式,则必须找指定的模式,如果没找到品配的,但却有一个是默认的,则仍然使用默认的!! 总之,必须有个是默认的!!
			HashVO hvo_accrDefault = null; //
			for (int k = 0; k < hvs_AccrUsers.length; k++) {
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals("") || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf("默认授权") >= 0) { //后来搞成了多选!
					hvo_accrDefault = hvs_AccrUsers[k]; //如果为空!!则说明是默认的
				}
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals(_accrdModel) || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf(";" + _accrdModel + ";") >= 0) { //如果品配上是 sunfujun/20121102/授权模块可以改成多选
					hvo_accr = hvs_AccrUsers[k]; //
					break; //
				}
			}
			if (hvo_accr == null) { //如果没品配上,则直接使用默认的,但可能默认也没有!!
				hvo_accr = hvo_accrDefault; //
			}
		}
		return hvo_accr; //
	}

	private String getItemValueFromTempletVOs(HashVO[] _hvs, String _templetCode, String _returnItemName) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue("templetcode").equals(_templetCode)) {
				return _hvs[i].getStringValue(_returnItemName); //
			}
		}
		return null;
	}

	//因为在计算公式时随时遇到机构计算!为了提高性能,在这时就计算好,然后传进去!!!
	private HashMap getAllCorpsCacheMap() throws Exception {
		if (this.allCorpsCacheMap != null) {
			return this.allCorpsCacheMap; //
		}
		allCorpsCacheMap = createAllCorpsCacheMap(); //创建!!!
		return allCorpsCacheMap; //
	}

	//创建所有机构的缓存哈希表
	public HashMap createAllCorpsCacheMap() {
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //getCommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //找出所有机构,并且返回树型结构!!!
		HashMap allCorpsMap = new HashMap(); //将机构用哈希表存起来,因为下面要多次搜索!!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			allCorpsMap.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //先注册一下!为了下面根据id找机构时要非常快!!
		}
		HashMap returnMap = new HashMap(); //
		returnMap.put("AllCorpHashVOs", hvs_allCorps); //送入缓存!
		returnMap.put("AllCorpHashMap", allCorpsMap); //送入缓存!
		return returnMap; //
	}

	/**
	 * 复制流程图
	 */
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception {
		CommDMO commdmo = new CommDMO();
		HashVOStruct hvos_process = commdmo.getHashVoStructByDS(null, "select * from pub_wf_process where id =" + _processid);
		if (hvos_process == null || hvos_process.getHashVOs() == null || hvos_process.getHashVOs().length == 0) {
			return null;
		}
		HashVOStruct hvos_group = commdmo.getHashVoStructByDS(null, "select * from pub_wf_group where processid =" + _processid);// 遍历所有组
		HashVOStruct hvos_activity = commdmo.getHashVoStructByDS(null, "select * from pub_wf_activity where processid =" + _processid);// 遍历所有环节
		HashVOStruct hvos_transition = commdmo.getHashVoStructByDS(null, "select * from pub_wf_transition where processid =" + _processid);// 遍历所有线

		// 处理主表信息
		String processid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_process");

		HashVO[] hvo = hvos_process.getHashVOs();
		hvo[0].setAttributeValue("id", processid);
		if (_processcode == null) {//如果编码为空
			hvo[0].setAttributeValue("code", hvo[0].getAttributeValue("code") + "_copy");
			hvo[0].setAttributeValue("name", hvo[0].getAttributeValue("name") + "_copy");
		} else {
			hvo[0].setAttributeValue("code", _processcode);
			hvo[0].setAttributeValue("name", _processname);
		}
		String str_curruserid = new WLTInitContext().getCurrSession().getLoginUserId(); //
		String str_deptid = new CommDMO().getStringValueByDS(null, "select userdept from pub_user_post where userid='" + str_curruserid + "' and isdefault='Y'"); //

		hvo[0].setAttributeValue("userdef01", str_deptid);//创建机构id
		hvo[0].setAttributeValue("userdef02", "1");//流程状态，初始状态是“1-有效”。“1-有效；2-启用；3-废止”，如果跟业务类型、单据类型关联了就变为【启用】状态，可以手动废止，【废止】的流程下级不可以看到
		hvo[0].setAttributeValue("userdef03", _processid);//从哪个流程复制的流程id

		hvos_process.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_process", hvos_process);

		hvo = hvos_group.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String groupid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			hvo[j].setAttributeValue("id", groupid);
			hvo[j].setAttributeValue("processid", processid);
		}
		hvos_group.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_group", hvos_group);

		HashMap hashmap = new HashMap();
		hvo = hvos_activity.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String acitivityid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			hvo[j].setAttributeValue("userdef03", hvo[j].getStringValue("id"));//sunfujun/20120615/gaofeng需要
			hashmap.put(hvo[j].getStringValue("id"), acitivityid);
			hvo[j].setAttributeValue("id", acitivityid);
			hvo[j].setAttributeValue("processid", processid);

		}
		hvos_activity.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_activity", hvos_activity);

		hvo = hvos_transition.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String transitionid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
			hvo[j].setAttributeValue("id", transitionid);
			hvo[j].setAttributeValue("processid", processid);
			hvo[j].setAttributeValue("fromactivity", hashmap.get(hvo[j].getStringValue("fromactivity")));
			hvo[j].setAttributeValue("toactivity", hashmap.get(hvo[j].getStringValue("toactivity")));
		}
		hvos_transition.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_transition", hvos_transition);
		return processid;
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO;
		}

		commDMO = new CommDMO(); //
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil;
	}

	private HashMap getBDCount(HashMap _parMap, String str_loginUserId) throws Exception {
		DataPolicyDMO dpdmo = new DataPolicyDMO();
		boolean isSuperShow = (Boolean) _parMap.get("isSuperShow");
		HashMap users = dpdmo.getDataPolicyTargetUsersByUserId(str_loginUserId, "意见补登数据权限", "id");
		if (users != null && users.containsKey("AllUserIDs")) {
			StringBuffer sb1 = new StringBuffer("select templetcode,count(*) c1 from pub_task_deal " + (isSuperShow ? "" : "where (dealuser in (" + getTBUtil().getInCondition((String[]) users.get("AllUserIDs")) + ") ) ") + " ");//or accruserid in (" + getTBUtil().getInCondition((String[])users.get("AllUserIDs")) + ")
			if (_parMap.get("templetcode") != null && !"".equals((String) _parMap.get("templetcode"))) {
				sb1.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
			}
			sb1.append(" group by templetcode ");
			return getClassCountBySQL(sb1.toString());
		}
		return null;
	}

	public HashMap getUserIdsByDataPolicy(HashMap param) throws Exception {
		DataPolicyDMO dpdmo = new DataPolicyDMO();
		String str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		return dpdmo.getDataPolicyTargetUsersByUserId(str_loginUserId, "意见补登数据权限", "id");
	}

	/**
	 * 工作流在自由添加人时,需要一个按群组选择的界面!!! 这是sfj做的,它被UI端的ChooseUserByGroupPanel类调用了
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsersByGroup(HashMap _parMap) throws Exception {
		BillVO groupVO = (BillVO) _parMap.get("groupVO");
		if (_parMap.containsKey("isaddaccr") && !(Boolean) _parMap.get("isaddaccr")) {
			return getCorpAndUserByGroup(groupVO, null, false);
		}
		String str_billType = (String) _parMap.get("billtype");
		String str_busiType = (String) _parMap.get("busitype");
		String str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType);
		return getCorpAndUserByGroup(groupVO, str_accrModel);
	}

	private HashMap getCorpAndUserByGroup(BillVO _groupVO, String _accrModel) throws Exception {
		return this.getCorpAndUserByGroup(_groupVO, _accrModel, true);
	}

	private HashMap getCorpAndUserByGroup(BillVO _groupVO, String _accrModel, boolean isaddaccr) throws Exception {
		CommDMO commDMO = new CommDMO();
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id userid,");
		sb_sql.append("t1.code usercode,");
		sb_sql.append("t1.name username,");
		sb_sql.append("t2.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename, ");
		sb_sql.append("t2.isdefault isdefault ");
		sb_sql.append("from (select * from pub_user where id in(" + getTBUtil().getInCondition(_groupVO.getStringValue("USERIDS")) + ")) t1 ");
		sb_sql.append(" left join pub_user_post t2 on t1.id= t2.userid ");
		sb_sql.append("left join pub_user_role t3 on t1.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //如果已有!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //取得
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //重置角色id
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //重置角色name
				if (!"Y".equals(hvoItem.getStringValue("isdefault", ""))) {
					if ("Y".equals(hvsUsers[i].getStringValue("isdefault", ""))) {
						hvoItem.setAttributeValue("userdept", hvsUsers[i].getStringValue("userdept", ""));
						hvoItem.setAttributeValue("isdefault", "Y");
					}
				}
				userMap.put(str_userId, hvoItem); //重新塞入!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //人员编码
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //人员名称
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //角色id
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //角色名称
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				hvoItem.setAttributeValue("isdefault", hvsUsers[i].getAttributeValue("isdefault"));
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////做了合并的用户清单!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname"); //补上人机构的名称
		StringBuffer sb_helpmsg = new StringBuffer(); //返回到客户端的帮助信息!
		if (isaddaccr) {
			//进行授权人计算,即如果某个用户授权给了另外一个人,则要显示授权人的名称!!! 比如华总授权给了李克振,则如果人员是华总的话,则要将显示改成“李克振(华总授权)”!!!!
			HashMap allAccreditDefinemap = getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //取得授权定义的记录!!
			if (allAccreditDefinemap.size() > 0) { //如果有授权定义才做处理
				ArrayList al_temp = new ArrayList(); //
				for (int i = 0; i < hvsUserSpan.length; i++) { //遍历所有人员!!!
					al_temp.add(hvsUserSpan[i]); //先加入!!!
					String str_oldUserId = hvsUserSpan[i].getStringValue("userid"); //
					HashVO proxyUserVO = getAccredProxyUserVO(allAccreditDefinemap, _accrModel, str_oldUserId); //找到代理人的VO
					if (proxyUserVO != null) { //如果找到了代理人,则增加代理人的信息
						HashVO cloneProxyUserHVO = hvsUserSpan[i].clone(); //
						String str_username1 = hvsUserSpan[i].getStringValue("username") + "(授权" + proxyUserVO.getStringValue("proxyusername") + ")"; //
						String str_username2 = proxyUserVO.getStringValue("proxyusername") + "(" + hvsUserSpan[i].getStringValue("username") + "授权)"; //这个以后可能需要参数配置!				
						cloneProxyUserHVO.setAttributeValue("username", str_username1);//和workflowenginedmo搞成一样就是名字始终显示授权人

						//设置被授权人...
						cloneProxyUserHVO.setAttributeValue("accruserid", hvsUserSpan[i].getStringValue("proxyuserid")); //
						cloneProxyUserHVO.setAttributeValue("accrusercode", hvsUserSpan[i].getStringValue("proxyusercode")); //
						cloneProxyUserHVO.setAttributeValue("accrusername", hvsUserSpan[i].getStringValue("proxyusername")); //

						al_temp.add(cloneProxyUserHVO); //先加入!!!
						sb_helpmsg.append("[" + hvsUserSpan[i].getStringValue("username") + "]授权给了[" + proxyUserVO.getStringValue("proxyusername") + "]\r\n");
					}
				}
				hvsUserSpan = (HashVO[]) al_temp.toArray(new HashVO[0]); //重新赋值!!
			}
		}
		HashMap returnMap = new HashMap();
		returnMap.put("sqinf", sb_helpmsg.toString());//是否存在授权的情况
		returnMap.put("users", hvsUserSpan);
		return returnMap; //

	}

	public HashMap getBDObj(HashMap param) throws Exception {
		DataPolicyDMO ddmo = new DataPolicyDMO();
		HashMap map = ddmo.getDataPolicyTargetUsersByUserId(new WLTInitContext().getCurrSession().getLoginUserId(), "意见补登数据权限", "id");
		String[] ids = (String[]) map.get("AllUserIDs");
		HashMap rtnmap = new HashMap();
		if (ids != null && ids.length > 0) {
			CommDMO commDMO = new CommDMO();
			StringBuilder sb_sql = new StringBuilder();
			sb_sql.append("select ");
			sb_sql.append("t1.id userid,");
			sb_sql.append("t1.code usercode,");
			sb_sql.append("t1.name username,");
			sb_sql.append("t2.userdept,");
			sb_sql.append("t3.roleid userroleid,");
			sb_sql.append("t4.name userrolename, ");
			sb_sql.append("t2.isdefault isdefault ");
			sb_sql.append("from (select * from pub_user where id in(" + getTBUtil().getInCondition(ids) + ")) t1 ");
			sb_sql.append(" left join pub_user_post t2 on t1.id= t2.userid ");
			sb_sql.append("left join pub_user_role t3 on t1.id =t3.userid ");
			sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
			HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString());
			LinkedHashMap userMap = new LinkedHashMap();
			for (int i = 0; i < hvsUsers.length; i++) {
				String str_userId = hvsUsers[i].getStringValue("userid");
				if (userMap.containsKey(str_userId)) {
					HashVO hvoItem = (HashVO) userMap.get(str_userId);
					resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true);
					resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false);
					if (!"Y".equals(hvoItem.getStringValue("isdefault", ""))) {
						if ("Y".equals(hvsUsers[i].getStringValue("isdefault", ""))) {
							hvoItem.setAttributeValue("userdept", hvsUsers[i].getStringValue("userdept", ""));
							hvoItem.setAttributeValue("isdefault", "Y");
						}
					}
					userMap.put(str_userId, hvoItem);
				} else {
					HashVO hvoItem = new HashVO();
					hvoItem.setAttributeValue("userid", str_userId);
					hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode"));
					hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username"));
					hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid"));
					hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename"));
					hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept"));
					hvoItem.setAttributeValue("isdefault", hvsUsers[i].getAttributeValue("isdefault"));
					userMap.put(str_userId, hvoItem);
				}
			}
			HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]);
			appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname");
			rtnmap.put("vos", hvsUserSpan);
		}
		return rtnmap;
	}
}
