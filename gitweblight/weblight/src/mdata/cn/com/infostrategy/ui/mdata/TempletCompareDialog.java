package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1_item;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
/**
 * 数据库中模板数据和Xml中数据进行对比。。
 * @author hm
 *
 */
public class TempletCompareDialog extends BillDialog implements ActionListener{
	private String _templetCode1, _templetCode2;
	private int fromtype1 = 0, fromtype2 = 1;
	private Container _parent ;
	private FrameWorkMetaDataServiceIfc service = null;
	private WLTButton   btn_delrow, btn_moveup, btn_movedown, btn_save, btn_cancel; //
	private WLTPanel btnp = null;
	
	private BillCardPanel cardPanel;
	private BillListPanel listPanel;
	public TempletCompareDialog(Container _parent, String _templetCode){
		this(_parent, _templetCode, 0, _templetCode, 1);
		this.setVisible(true);
	}
	
	public TempletCompareDialog(Container _parent, String _templetCode1, int type1, String _templetCode2, int type2) {
		super(_parent,"模板对比", 1024, 740);
		this._templetCode1 = _templetCode1;
		this._templetCode2 = _templetCode2;
		this.fromtype1 = type1;
		this.fromtype2 = type2;
		this._parent = _parent;
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initialize() throws Exception{
		this.getContentPane().setLayout(new BorderLayout()); //
		try {
			service = UIUtil.getMetaDataService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DefaultTMO databasetmo = service.getDefaultTMOByCode(_templetCode1, fromtype1);
		DefaultTMO xmltmo = service.getDefaultTMOByCode(_templetCode2, fromtype2);
		HashVO[] itemvos_data =	databasetmo.getPub_templet_1_itemData();
		HashVO[] itemvos_xml = xmltmo.getPub_templet_1_itemData();
		HashMap itemmap_data = getMap(itemvos_data);
		HashMap itemmap_xml = getMap(itemvos_xml);
		Vector vc = new Vector();
		HashVO templet_1vo = getCompareHashVO(databasetmo.getPub_templet_1Data(),xmltmo.getPub_templet_1Data()); //主表vo
		/*开始创建主表的BillCardPanel*/
		cardPanel = new BillCardPanel(getPubTempletVO(templet_1vo));
		cardPanel.putClientProperty("currvalue", templet_1vo);
		cardPanel.setEditableByEditInit();
		Object obj [][] = service.getBillListDataByHashVOs(cardPanel.getTempletVO().getParPub_Templet_1VO(), new HashVO[]{templet_1vo}); //直接根据hashvo得到控件内容
	    //把refitemvo的ID转换.getBillListDataByHashVOs得到的refitemvo，id和name一样！
		if(obj.length>0){
			Object[] obj_1 = obj[0];
			String keys[] = cardPanel.getTempletVO().getParPub_Templet_1VO().getItemKeys();
			for (int i = 0; i < obj_1.length; i++) {
				if(obj_1[i] instanceof RefItemVO){
					RefItemVO vo = (RefItemVO) obj_1[i];
					if(i>0){
						String key = keys[i-1].toLowerCase();
						Object userobj = templet_1vo.getUserObject(key);
						if(userobj instanceof String[]){
							String value [] = (String[]) userobj;
							vo.setId(value[0]);
							cardPanel.setItemForeGroundColor(key,"#00DDFF");
						}
					}
				}
			}
		}
		cardPanel.setValue(obj[0]);
		cardPanel.updateCurrRow();
		cardPanel.setEditable("PK_PUB_TEMPLET_1", false); //
		cardPanel.setEditable("TEMPLETCODE", false); //
		/*创建主表的BillCardPanel结束*/
		
		for (int i = 0; i < itemvos_data.length; i++) {
			String itemkey = itemvos_data[i].getStringValue("ITEMKEY"); //主键，确定唯一性
			if(itemmap_xml.containsKey(itemkey)){ //如果包含
				vc.add(new HashVO[]{itemvos_data[i],(HashVO) itemmap_xml.get(itemkey)}); //两者都有
				itemmap_xml.remove(itemkey);//如果有就移除
			}else{
				vc.add(new HashVO[]{itemvos_data[i],null}); //只有数据库中有
			}
		}
		for (Iterator iterator = itemmap_xml.keySet().iterator(); iterator.hasNext();) {  //xml中多出来的
			String itemkey = (String) iterator.next();
			HashVO vo = (HashVO) itemmap_xml.get(itemkey);
			vc.add(new HashVO[]{null,vo});
		}
		Vector newvos = new Vector();  //存放子表VOs
		//设定行标记！
		for (int i = 0; i < vc.size(); i++) {
			HashVO[] vo  = (HashVO[]) vc.get(i);
			if (vo[0]!=null && vo[1]!=null) {
				HashVO newvo =getCompareHashVO(vo[0],vo[1]);
				newvo.setUserObject("$datatype","same"); //相同
				newvos.add(newvo);
			}else if (vo[1] ==null){
				HashVO newvo =getCompareHashVO(vo[0],null);
				newvo.setUserObject("$datatype","delete"); //数据库中有，xml中没有
				newvos.add(newvo);
			}else if(vo[0] == null){
				HashVO newvo =getCompareHashVO(null,vo[1]);
				newvo.setUserObject("$datatype","new");//xml中有，数据库中没有。
				newvos.add(newvo);
			}
		}
		HashVO [] itemvos = (HashVO[]) newvos.toArray(new HashVO[0]);  //子表vo
		new TBUtil().sortHashVOs(itemvos,new String[][]{{"SHOWORDER".toLowerCase(),"N","Y"}});//排序
		listPanel = new BillListPanel(getPubTemplet_itemVO(templet_1vo));
		Object itembillvos[][] =  service.getBillListDataByHashVOs(listPanel.getTempletVO().getParPub_Templet_1VO(),itemvos); //直接根据hashvo得到控件内容
		//把refitemvo的ID转换.getBillListDataByHashVOs得到的refitemvo，id和name一样！
		for (int i = 0; i < itembillvos.length; i++) {
			Object[] obj_1 = itembillvos[i];
			String keys[] = listPanel.getTempletVO().getParPub_Templet_1VO().getItemKeys();
			for (int j= 0; j < obj_1.length; j++) {
				if(obj_1[j] instanceof RefItemVO){
					RefItemVO vo = (RefItemVO) obj_1[j];
					if(j>0){
						String key = keys[j-1].toLowerCase();
						Object userobj = itemvos[i].getUserObject(key);
						if(userobj instanceof String[]){
							String value [] = (String[]) userobj;
							vo.setId(value[0]);
						}
					}
				}
			}
		}
		listPanel.putValue(itembillvos); //塞值
		listPanel.putClientProperty("currvalue", itemvos); //存放包含相对值的hashvo.在getUserObject中
		for (int i = 0; i < itemvos.length; i++) {
			HashVO hvo = itemvos[i];
			String datatype = (String) hvo.getUserObject("$datatype");
			if("delete".equals(datatype)){//只数据库中存在
				String keys[] = itemvos[i].getKeys();
				for (int j = 0; j < keys.length; j++) {
					listPanel.setItemForeGroundColor("DC143C", i, keys[j]);
				}
			}else if("new".equals(datatype)){//只Xml中存在
				String keys[] = itemvos[i].getKeys();
				for (int j = 0; j < keys.length; j++) {
					if(keys[j].equalsIgnoreCase("pk_pub_templet_1")){  //如果是主键
						String newiD = UIUtil.getSequenceNextValByDS(null, "S_PUB_TEMPLET_1_ITEM");
						listPanel.setValueAt(new StringItemVO(newiD), i, "pk_pub_templet_1_item");
					}else if(keys[j].equalsIgnoreCase("pk_pub_templet_1_item")){  //如果是外键
						listPanel.setValueAt(new StringItemVO(cardPanel.getBillVO().getStringValue("pk_pub_templet_1")), i, "pk_pub_templet_1");
					}
					listPanel.setItemForeGroundColor("9370DB", i, keys[j]);
				}
				listPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_INSERT);
			}else{ //相同的
				String keys[] = itemvos[i].getKeys();
				for (int j = 0; j < keys.length; j++) {
					String key_now = keys[j];
					if(!key_now.equalsIgnoreCase("pk_pub_templet_1") && !key_now.equalsIgnoreCase("pk_pub_templet_1_item")&&hvo.getUserObject(key_now.toLowerCase())!=null){
						listPanel.setItemForeGroundColor("0000FF", i, key_now);
					}
				}
			}
		}
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, cardPanel, listPanel); //
		split.setOneTouchExpandable(true);
		split.setDividerLocation(300); //
		split.setDividerSize(10);
		this.getContentPane().add(split, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //加入所有按钮
	}
	public void initdata(){
		try {
			this.getContentPane().removeAll();
			initialize();
			this.getContentPane().validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 所有按钮的面板！
	 */
	public JPanel getSouthPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		String str = "<html><span style=\"float:right\"><font color=\"#0000FF\">■</font>数据不同&nbsp;&nbsp;<font color=\"#9370DB\">■</font>只" + getKey2() + "存在&nbsp;&nbsp;<font color=\"#DC143C\">■</font>只" + getKey1() + "存在&nbsp;&nbsp;</span></html>";
		JLabel label = new JLabel(str);
		btn_delrow = new WLTButton("删除"); //
		btn_moveup = new WLTButton("上移"); //
		btn_movedown = new WLTButton("下移"); //
		btn_save = new WLTButton("保存"); //
		btn_cancel = new WLTButton("取消"); //
		btn_delrow.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		
		btnp = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT,new FlowLayout(FlowLayout.LEFT, 1, 2));
		btnp.add(btn_delrow);
		btnp.add(btn_moveup);
		btnp.add(btn_movedown);
		btnp.add(btn_save);
		btnp.add(btn_cancel);
		WLTPanel pp = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT,new FlowLayout(FlowLayout.LEFT, 1, 2));
		pp.add(label);
		pp.add(btnp);
//		WLTSplitPane sp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,label,btn);
//		sp.setDividerSize(0);
//		sp.setDividerLocation(800);;
		panel.add(pp);
		return panel;
	}
	public AbstractTMO getPubTempletVO(HashVO templet_1vo){
		TMO_Pub_Templet_1 tmp = new TMO_Pub_Templet_1(false);
		HashVO[] itemvo = tmp.getPub_templet_1_itemData();
		for (int i = 0; i < itemvo.length; i++) {
			String itemkey = itemvo[i].getStringValue("ITEMKEY");
			Object obj = templet_1vo.getUserObject(itemkey.toLowerCase()); //如果值不一样！
			if("PK_PUB_TEMPLET_1_ITEM".equalsIgnoreCase(itemkey) ||
					"PK_PUB_TEMPLET_1".equalsIgnoreCase(itemkey)||
					"ITEMKEY".equalsIgnoreCase(itemkey)){
			}else{
				if(obj instanceof String[]){
					itemvo[i].setAttributeValue("itemtype", "自定义参照");
					itemvo[i].setAttributeValue("REFDESC", "getCommUC(\"自定义参照\",\"自定义类名\",\"cn.com.infostrategy.ui.mdata.cardcomp.TempletComPareRefDialog\",\"参数\",\"" + getKey1() + "\",\"参数2\",\"" + getKey2() +"\");");
				}else{
//					itemvo[i].setAttributeValue("itemtype", "文本框");
//					itemvo[i].setAttributeValue("REFDESC", "getCommUC(\"自定义参照\",\"自定义类名\",\"cn.com.infostrategy.ui.mdata.cardcomp.TempletComPareRefDialog\");");
				}
			}
		}
		return new DefaultTMO(tmp.getPub_templet_1Data(),itemvo);
	}
	
	public String getKey1() {
		String rtn = null;
		if(this.fromtype1 == 0) {
			rtn = "数据库(" + this._templetCode1 + ")中";
		}else {
			rtn = "XML(" + this._templetCode1 + ")中";
		}
		return rtn;
	}
	
	public String getKey2() {
		String rtn = null;
		if(this.fromtype2 == 0) {
			rtn = "数据库(" + this._templetCode2 + ")中";
		}else {
			rtn = "XML(" + this._templetCode2 + ")中";
		}
		return rtn;
	}
	
	public AbstractTMO getPubTemplet_itemVO(HashVO templet_1vo){
		TMO_Pub_Templet_1_item tmp = new TMO_Pub_Templet_1_item();
		HashVO[] itemvo = tmp.getPub_templet_1_itemData();
		for (int i = 0; i < itemvo.length; i++) {
			if("PK_PUB_TEMPLET_1_ITEM".equalsIgnoreCase(itemvo[i].getStringValue("itemkey")) ||
					"PK_PUB_TEMPLET_1".equalsIgnoreCase(itemvo[i].getStringValue("itemkey"))||
					"ITEMKEY".equalsIgnoreCase(itemvo[i].getStringValue("itemkey"))){
			}else{
				itemvo[i].setAttributeValue("itemtype", "自定义参照");
				itemvo[i].setAttributeValue("REFDESC", "getCommUC(\"自定义参照\",\"自定义类名\",\"cn.com.infostrategy.ui.mdata.cardcomp.TempletComPareRefDialog\",\"参数\",\"" + getKey1() + "\",\"参数2\",\"" + getKey2() +"\");");
			}
		}
		return new DefaultTMO(tmp.getPub_templet_1Data(),itemvo);
	}
	/*
	 * 对比数据库中和xml中每个字段的的子表配置数据。
	 * 把数据全部变为String
	 */
	public HashVO getCompareHashVO(HashVO datahashvo , HashVO xmlhashvo){
		HashVO  currvo = new HashVO();
		if(datahashvo!=null && xmlhashvo != null){ //是 数据库和xml的对比
			String [] keys_data = datahashvo.getKeys();
			String [] keys_xml = xmlhashvo.getKeys();//
			HashMap map2 = getMap(keys_xml, xmlhashvo); //按说两个数组应该是一致的,把模板中的搞成hashmap。
			//主表只有值对不对的问题。
			for (int i = 0; i < keys_data.length; i++) {
				String item = keys_data[i].toLowerCase();
				String datavalue = datahashvo.getStringValue(item,""); //数据库中的值
				if("PK_PUB_TEMPLET_1_ITEM".equalsIgnoreCase(item) ||
						"PK_PUB_TEMPLET_1".equalsIgnoreCase(item)||
						"ITEMKEY".equalsIgnoreCase(item)){
					currvo.setAttributeValue(item, datavalue);
					continue;  //这三个字段不需要改。是唯一的
				}
				if(map2.containsKey(item)){ //
					String xmlvalue = (String) map2.get(item);
					if(xmlvalue ==null){
						xmlvalue = "";
					}
					if(datavalue.trim().equals(xmlvalue.trim())){
						currvo.setAttributeValue(item, datavalue);
						continue;
					}else{
						if("SHOWORDER".equalsIgnoreCase(item)){
							currvo.setAttributeValue(item,datavalue );
							currvo.setUserObject(item, new String[]{datavalue,xmlvalue});
						}else{
							StringBuffer sb = new StringBuffer();
							sb.append(getKey1() + "【" + datavalue +"】,\r\n");
							sb.append(getKey2() + "【" + xmlvalue +"】.\r\n");
							currvo.setAttributeValue(item,sb.toString() );
							currvo.setUserObject(item, new String[]{datavalue,xmlvalue});
						}
					}
				}else{
					currvo.setAttributeValue(item, datavalue);
				}
			}
			return currvo;
		}else if(datahashvo!=null){ //数据库中的不为空（XMl没有字段子表数据）。
			String [] keys_data = datahashvo.getKeys();
			for (int i = 0; i < keys_data.length; i++) {
				String item = keys_data[i];
				String datavalue = datahashvo.getStringValue(item,""); //数据库中的值
				currvo.setAttributeValue(item, datavalue);
			}
		}else if(xmlhashvo!=null){//Xml中的不为空(数据库没有字段子表数据)。
			String [] keys_data = xmlhashvo.getKeys();
			for (int i = 0; i < keys_data.length; i++) {
				String item = keys_data[i];
				String datavalue = xmlhashvo.getStringValue(item,""); //数据库中的值
				currvo.setAttributeValue(item, datavalue);
			}
			
		}
		return currvo;
	}
	
	private HashMap getMap(String[] key,HashVO hashvo){
		HashMap map = new HashMap();
		for (int i = 0; i < key.length; i++) {
			map.put(key[i],hashvo.getStringValue(key[i]));
		}
		return map;
	}
	/*
	 * 把hashvo的主键建map。便于查找
	 */
	private HashMap<String,HashVO> getMap(HashVO [] vos){
		HashMap map = new HashMap();
		for (int i = 0; i < vos.length; i++) {
			String itemkey = vos[i].getStringValue("ITEMKEY");
			map.put(itemkey, vos[i]);
		}
		return map;
	}
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj== btn_cancel){
			this.dispose();
		}else if(obj == btn_moveup){
			onMoveup();
		}else if(obj == btn_movedown){
			onMovedown();
		}else if(obj == btn_delrow){
			onDelete();
		}else if(obj == btn_save){
			onSave();
		}
	}
	/*
	 * 排序
	 */
	private void resetShowOrder() {
		int li_rowcount = listPanel.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (listPanel.getValueAt(i, "SHOWORDER") != null && Integer.parseInt("" + listPanel.getValueAt(i, "SHOWORDER")) != (i + 1)) {
				String seq = "" + (i + 1);
				listPanel.setValueAt(new RefItemVO(seq,null,seq), i, "SHOWORDER"); //
				if(!WLTConstants.BILLDATAEDITSTATE_INSERT.equalsIgnoreCase(listPanel.getRowNumberEditState(i))){
					listPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //设置为修改状态.
				}
			}
		}
	}
	private void onDelete(){
		listPanel.removeSelectedRow();
		resetShowOrder();
	}
	private void onMoveup() {
		if (listPanel.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void onMovedown() {
		if (listPanel.moveDownRow()) {
			resetShowOrder(); //	
		}
	}
	private boolean onSave() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			String state  = listPanel.getRowNumberEditState(i);
			if(WLTConstants.BILLDATAEDITSTATE_INSERT.equalsIgnoreCase(state)){ //如果是insert状态。
				sb.append("["+listPanel.getValueAt(i, "ItemName")+"]");
			}
		}
		int index = -1;
		if(sb.length()>0){
			index = MessageBox.showOptionDialog(this, "字段"+sb.toString()+"为新增字段，只存在Xml文件中，数据库中缺少。\r\n如果保存新增字段请选择是，只保存修改字段请选择否，不做操作选择取消！", "提示", new String[]{"是","否","取消"});
			if(index == 2 || index == -1){
				return false;
			}
		}
		resetShowOrder(); //重新更新排序
		stopEditing();
		String str_sql_1 = cardPanel.getUpdateSQL();
		String[] str_sqls_2 = null;
		if(index == 0 || index == -1){ //如果是保存新增字段
			str_sqls_2 = listPanel.getOperatorSQLs();
		}else if(index == 1){ //如果不保存新增的
			str_sqls_2 = getUpdateSqls();
		}
		ArrayList list = new ArrayList();
		list.add(str_sql_1);
		list.addAll(Arrays.asList(str_sqls_2)); //
		try {
			UIUtil.executeBatchByDS(null, list); //
			listPanel.setAllRowStatusAs("INIT");
			JOptionPane.showMessageDialog(this, "保存数据成功!!!"); //
			initdata();
			return true;
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
			return false;
		}
	}
	public String[] getUpdateSqls(){
		Vector v_all = new Vector();
		v_all.addAll(listPanel.getDeleteSQLVector());
		for (int i = 0; i < listPanel.getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO)listPanel. getTableModel().getValueAt(i, listPanel.str_rownumberMark)).getState(); //
			 if (str_type.equals("UPDATE")) { //如果是修改状态!
				v_all.add(listPanel.getUpdateSQL(i));
			 }
		}
		return (String[]) v_all.toArray(new String[0]); //
	}
	//停止编辑!!!
	private void stopEditing() {
		try {
			if (listPanel.getTable().getCellEditor() != null) {
				listPanel.getTable().getCellEditor().stopCellEditing(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public WLTPanel getBtnp() {
		return btnp;
	}

	public void setBtnp(WLTPanel btnp) {
		this.btnp = btnp;
	}
}
