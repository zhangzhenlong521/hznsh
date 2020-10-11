package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public class CreateHtmlForMenuCompare implements WebCallBeanIfc {

	private HashVO[] oldMenu = null;
	private HashVO[] newMenu = null;
	private DatabaseCompareBean commentBean = null;
	private DatabaseCompareBean commentBeanThis = null;
	private DatabaseCompareBean commentBeanThat = null;
	private List<DatabaseCompareBean> list = null;
	private String type = null;
	private DataSourceVO[] dataSource = null;
	private String dataSource1 = null;
	private String dataSource2 = null;
	
	@SuppressWarnings("unchecked")
	public String getHtmlContent(HashMap map) throws Exception {
		type = (String)map.get("type");
		dataSource1 = (String)map.get("datasource1");
		dataSource2 = (String)map.get("datasource2");
		oldMenu = new CommDMO().getHashVoArrayByDS(map.get("datasource2").toString(), "select * from pub_menu");
		newMenu = new CommDMO().getHashVoArrayByDS(map.get("datasource1").toString(), "select * from pub_menu");
		
		//输出数据库中不同的数据。
		if(type.equals("0")){
			list = new ArrayList<DatabaseCompareBean>();
			for(int i = 0;i < newMenu.length;i++){
				int j = 0;
				for(;j < oldMenu.length;j++){
					if(newMenu[i].getStringValue("name").trim().equals(oldMenu[j].getStringValue("name").trim())){
						break;
					}
				}
				if(j >= oldMenu.length){
					createBean(newMenu[i],null);
				}
			}
		}
		
		if(type.equals("1")){
			list = new ArrayList<DatabaseCompareBean>();
			for(int i = 0;i < oldMenu.length;i++){
				int j = 0;
				for(;j < newMenu.length;j++){
					if(oldMenu[i].getStringValue("name").trim().equals(newMenu[j].getStringValue("name").trim())){
						break;
					}
				}
				if(j >= newMenu.length){
					createBean(null,oldMenu[i]);
				}
			}
		}
		if(type.equals("2")){
			list = new ArrayList<DatabaseCompareBean>();
			for(int i = 0;i < newMenu.length;i++){
				for(int j = 0;j < oldMenu.length;j++){
					//必须名称相同的加以处理
					if(newMenu[i].getStringValue("name").equals(oldMenu[j].getStringValue("name"))){
						if(isSamePath(newMenu[i],oldMenu[j])){
							if(newMenu[i].getStringValue("usecmdtype") != null && oldMenu[j].getStringValue("usecmdtype") != null){
								if(newMenu[i].getStringValue("usecmdtype").equals("1")){
									if(isSameFirstPathType(newMenu[i],oldMenu[j])){
										if(!isSameFirstPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}else{
										createBean(newMenu[i],oldMenu[j]);
									}
								}else if(newMenu[i].getStringValue("usecmdtype").equals("2")){
									if(isSameSecondPathType(newMenu[i],oldMenu[j])){
										if(!isSameSecondPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}else{
										createBean(newMenu[i],oldMenu[j]);
									}
								}else if(newMenu[i].getStringValue("usecmdtype").equals("3")){
									if(isSameThirdPathType(newMenu[i],oldMenu[j])){
										if(!isSameThirdPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}else{
										createBean(newMenu[i],oldMenu[j]);
									}
								}
							}
						}else{
							createBean(newMenu[i],oldMenu[j]);
						}	
					}
				}
			}
		}
		if(type.equals("3")){
			list = new ArrayList<DatabaseCompareBean>();
			for(int i = 0;i < newMenu.length;i++){
				for(int j = 0;j < oldMenu.length;j++){
					//必须名称相同的加以处理
					if(newMenu[i].getStringValue("name").equals(oldMenu[j].getStringValue("name"))){
						if(isSamePath(newMenu[i],oldMenu[j])){
							if(newMenu[i].getStringValue("usecmdtype") != null && oldMenu[j].getStringValue("usecmdtype") != null){
								if(newMenu[i].getStringValue("usecmdtype").equals("1")){
									if(isSameFirstPathType(newMenu[i],oldMenu[j])){
										if(isSameFirstPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}
								}else if(newMenu[i].getStringValue("usecmdtype").equals("2")){
									if(isSameSecondPathType(newMenu[i],oldMenu[j])){
										if(isSameSecondPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}
								}else if(newMenu[i].getStringValue("usecmdtype").equals("3")){
									if(isSameThirdPathType(newMenu[i],oldMenu[j])){
										if(isSameThirdPathContent(newMenu[i],oldMenu[j])){
											createBean(newMenu[i],oldMenu[j]);
										}
									}
								}
							}
							if(newMenu[i].getStringValue("usecmdtype") == null && oldMenu[j].getStringValue("usecmdtype") == null){
								createBean(newMenu[i],oldMenu[j]);
							}
						}	
					}
				}
			}
		}

		return new TableTransform(createBillVOs(list)).getHtmlFile(map.get("title").toString());
	}
	
	private BillCellVO[] createBillVOs(List<DatabaseCompareBean> tempList){
		BillCellVO[] tempBillVO = new BillCellVO[1];
		BillCellItemVO[][] tempCellItem = new BillCellItemVO[tempList.size()+ 1][6];
		tempBillVO[0] = new BillCellVO();
		tempBillVO[0].setCollength(6);
		tempBillVO[0].setRowlength(tempList.size() + 1);
		tempCellItem[0][0] = new BillCellItemVO();
		tempCellItem[0][0].setSpan("1,1");
		tempCellItem[0][0].setColwidth("100");
		tempCellItem[0][0].setRowheight("20");
		tempCellItem[0][0].setCellvalue("数据库编码");
		
		tempCellItem[0][1] = new BillCellItemVO();
		tempCellItem[0][1].setSpan("1,1");
		tempCellItem[0][1].setColwidth("100");
		tempCellItem[0][1].setRowheight("20");
		tempCellItem[0][1].setCellvalue("数据库所在地");
		
		tempCellItem[0][2] = new BillCellItemVO();
		tempCellItem[0][2].setSpan("1,1");
		tempCellItem[0][2].setColwidth("100");
		tempCellItem[0][2].setRowheight("20");
		tempCellItem[0][2].setCellvalue("菜单名");
		
		tempCellItem[0][3] = new BillCellItemVO();
		tempCellItem[0][3].setSpan("1,1");
		tempCellItem[0][3].setColwidth("100");
		tempCellItem[0][3].setRowheight("20");
		tempCellItem[0][3].setCellvalue("采用路径");
		
		tempCellItem[0][4] = new BillCellItemVO();
		tempCellItem[0][4].setSpan("1,1");
		tempCellItem[0][4].setColwidth("100");
		tempCellItem[0][4].setRowheight("20");
		tempCellItem[0][4].setCellvalue("路径类型");
		
		tempCellItem[0][5] = new BillCellItemVO();
		tempCellItem[0][5].setSpan("1,1");
		tempCellItem[0][5].setColwidth("100");
		tempCellItem[0][5].setRowheight("20");
		tempCellItem[0][5].setCellvalue("路径内容");
		for(int j = 0,i = 1;j < tempList.size();j++,i++){
			commentBean = (DatabaseCompareBean)tempList.get(j);
			tempCellItem[i][0] = new BillCellItemVO();
			tempCellItem[i][0].setSpan("1,1");
			tempCellItem[i][0].setColwidth("100");
			tempCellItem[i][0].setRowheight("20");
			tempCellItem[i][0].setCellvalue(commentBean.getCode());
			
			tempCellItem[i][1] = new BillCellItemVO();
			tempCellItem[i][1].setSpan("1,1");
			tempCellItem[i][1].setColwidth("100");
			tempCellItem[i][1].setRowheight("20");
			tempCellItem[i][1].setCellvalue(commentBean.getTitle());
			
			tempCellItem[i][2] = new BillCellItemVO();
			tempCellItem[i][2].setSpan("1,1");
			tempCellItem[i][2].setColwidth("100");
			tempCellItem[i][2].setRowheight("20");
			tempCellItem[i][2].setCellvalue(commentBean.getName());
			
			tempCellItem[i][3] = new BillCellItemVO();
			tempCellItem[i][3].setSpan("1,1");
			tempCellItem[i][3].setColwidth("100");
			tempCellItem[i][3].setRowheight("20");
			tempCellItem[i][3].setCellvalue(commentBean.getPath());
			
			tempCellItem[i][4] = new BillCellItemVO();
			tempCellItem[i][4].setSpan("1,1");
			tempCellItem[i][4].setColwidth("100");
			tempCellItem[i][4].setRowheight("20");
			tempCellItem[i][4].setCellvalue(commentBean.getPathTyep());
			
			tempCellItem[i][5] = new BillCellItemVO();
			tempCellItem[i][5].setSpan("1,1");
			tempCellItem[i][5].setColwidth("100");
			tempCellItem[i][5].setRowheight("20");
			tempCellItem[i][5].setCellvalue(commentBean.getPathContent());
			
		}
		tempBillVO[0].setCellItemVOs(tempCellItem);
		return tempBillVO;
	}
	private boolean isSameFirstPathContent(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("command") == null && oldBillVO.getStringValue("command") == null){
			return true;
		}
		if(newBillVO.getStringValue("command") != null && oldBillVO.getStringValue("command") != null && newBillVO.getStringValue("command").equals(oldBillVO.getStringValue("command"))){
			return true;
		}
		return false;
	}
	private boolean isSameSecondPathContent(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("command") == null && oldBillVO.getStringValue("command") == null){
			return true;
		}
		if(newBillVO.getStringValue("command") != null && oldBillVO.getStringValue("command") != null && newBillVO.getStringValue("command").equals(oldBillVO.getStringValue("command"))){
			return true;
		}
		return false;
	}
	
	private boolean isSameThirdPathContent(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("command") == null && oldBillVO.getStringValue("command") == null){
			return true;
		}
		if(newBillVO.getStringValue("command") != null && oldBillVO.getStringValue("command") != null && newBillVO.getStringValue("command").equals(oldBillVO.getStringValue("command"))){
			return true;
		}
		return false;
	}
	
	private boolean isSameFirstPathType(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype") == null && oldBillVO.getStringValue("commandtype") == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype") != null && oldBillVO.getStringValue("commandtype") != null && newBillVO.getStringValue("commandtype").equals(oldBillVO.getStringValue("commandtype"))){
			return true;
		}
		
		return false;
	}
	private boolean isSameSecondPathType(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype2") == null && oldBillVO.getStringValue("commandtype2") == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype2") != null && oldBillVO.getStringValue("commandtype2") != null && newBillVO.getStringValue("commandtype2").equals(oldBillVO.getStringValue("commandtype2"))){
			return true;
		}
		
		return false;
	}
	private boolean isSameThirdPathType(HashVO newBillVO,HashVO oldBillVO){
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype3") == null && oldBillVO.getStringValue("commandtype3") == null){
			return true;
		}
		if(newBillVO.getStringValue("commandtype3") != null && oldBillVO.getStringValue("commandtype3") != null && newBillVO.getStringValue("commandtype3").equals(oldBillVO.getStringValue("commandtype3"))){
			return true;
		}
		
		return false;
	}
	private boolean isSamePath(HashVO newBillVO,HashVO oldBillVO){
		
		if(newBillVO == null && oldBillVO == null){
			return true;
		}
		if(newBillVO.getStringValue("usecmdtype") == null && oldBillVO.getStringValue("usecmdtype") == null){
			return true;
		}
		if(newBillVO.getStringValue("usecmdtype") != null && oldBillVO.getStringValue("usecmdtype") != null && newBillVO.getStringValue("usecmdtype").equals(oldBillVO.getStringValue("usecmdtype"))){
			return true;
		}
		return false;
	}
	
	private void createBean(HashVO newBillVO,HashVO oldBillVO){
		dataSource = ServerEnvironment.getInstance().getDataSourceVOs();
		try {
			if(newBillVO != null){
				commentBeanThis = new DatabaseCompareBean();
				commentBeanThis.setCode("1");
				for(int i = 0;i < dataSource.length;i++){
					if(dataSource[i].getName().equals(dataSource1)){
						commentBeanThis.setTitle(dataSource[i].getDescr());
					}
				}
				initBean(commentBeanThis,newBillVO,"1");
				list.add(commentBeanThis);
			}
			if(oldBillVO != null){
				commentBeanThat = new DatabaseCompareBean();
				commentBeanThat.setCode("2");
				for(int i = 0;i < dataSource.length;i++){
					if(dataSource[i].getName().equals(dataSource2)){
						commentBeanThis.setTitle(dataSource[i].getDescr());
					}
				}
				initBean(commentBeanThat,oldBillVO,"2");
				list.add(commentBeanThat);
			}
		} catch (Exception e) {
			System.out.println("创建数据记录bean"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void initBean(DatabaseCompareBean tempBean,HashVO tempHash,String dataType) throws Exception{
		String tempName = null;
		if(dataType.equals("1")){
			HashVO[] typeName = new CommDMO().getHashVoArrayByDS(dataSource1,"select name from pub_menu where id =" + tempHash.getStringValue("parentmenuid"));
			if(typeName == null || typeName.length == 0){
				tempName = "根目录";
			}else{
				tempName = typeName[0].getStringValue("name");
			}
		}else if(dataType.equals("2")){
			HashVO[] typeName = new CommDMO().getHashVoArrayByDS(dataSource2,"select name from pub_menu where id =" + tempHash.getStringValue("parentmenuid"));
			if(typeName == null || typeName.length == 0){
				tempName = "根目录";
			}else{
				tempName = typeName[0].getStringValue("name");
			}
		}
		tempBean.setName(tempHash.getStringValue("name")+"("+tempName+")");
		if(tempHash.getStringValue("usecmdtype") == null){
			tempBean.setPath("无路径");
			return ;
		}
		if(tempHash.getStringValue("usecmdtype").equals("1")){
			tempBean.setPath("第一种路径");
			if(tempHash.getStringValue("commandtype") == null){
				tempBean.setPathTyep("无路径类型");
				return ;
			}else{
				HashVO[] typeName = null;
				if(dataType.equals("1")){
					typeName = new CommDMO().getHashVoArrayByDS(dataSource1, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}else{
					typeName = new CommDMO().getHashVoArrayByDS(dataSource2, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}
				tempBean.setPathTyep(typeName != null ? typeName[0].getStringValue("Name"):"无路径类型");
				if(typeName != null){
					tempBean.setPathContent(tempHash.getStringValue("command"));
				}else{
					tempBean.setPathContent("无路径内容");
				}
				return ;
			}
			
		}
		if(tempHash.getStringValue("usecmdtype").equals("2")){
			tempBean.setPath("第二种路径");
			if(tempHash.getStringValue("commandtype2") == null){
				tempBean.setPathTyep("无路径类型");
				return ;
			}else{
				HashVO[] typeName = null;
				if(dataType.equals("1")){
					typeName = new CommDMO().getHashVoArrayByDS(dataSource1, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}else{
					typeName = new CommDMO().getHashVoArrayByDS(dataSource2, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}
				tempBean.setPathTyep(typeName != null ?typeName[0].getStringValue("Name"):"无路径类型");
				if(typeName != null){
					tempBean.setPathContent(tempHash.getStringValue("command2"));
				}else{
					tempBean.setPathContent("无路径内容");
				}
				return ;
			}
		}
		if(tempHash.getStringValue("usecmdtype").equals("3")){
			tempBean.setPath("第三种路径");
			if(tempHash.getStringValue("commandtype3") == null){
				tempBean.setPathTyep("无路径类型");
				return ;
			}else{
				HashVO[] typeName = null;
				if(dataType.equals("1")){
					typeName = new CommDMO().getHashVoArrayByDS(dataSource1, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}else{
					typeName = new CommDMO().getHashVoArrayByDS(dataSource2, "Select Name From pub_comboboxdict Where type='菜单路径' and code='"+tempHash.getStringValue("commandtype")+"'");
				}
				tempBean.setPathTyep(typeName != null ? typeName[0].getStringValue("Name"):"无路径类型");
				if(typeName != null){
					tempBean.setPathContent(tempHash.getStringValue("command3"));
				}else{
					tempBean.setPathContent("无路径内容");
				}
				return ;
			}
		}
		
		
	}
}
