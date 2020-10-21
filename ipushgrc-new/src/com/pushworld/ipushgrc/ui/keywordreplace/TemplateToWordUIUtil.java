package com.pushworld.ipushgrc.ui.keywordreplace;

import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 根据模板生成Word 有两种方式
 * 第一种：要生成的文件只涉及到一个billList模板(此时不涉及子表和自定义字段)，直接调用createWordByOneListPanel方法即可
 * 第二种：涉及子表和自定义字段，此时先调用convertBillVOSToMap封装数据成一个HashMap,然后可以写入自定义字段，最后将HashMap传入createWordByMap即可
 * @author yyb
 */
public class TemplateToWordUIUtil {
	/**
	 * 要生成的文件只涉及到一个billList模板，不涉及子表，最简单的一种
	 * @param billList
	 * @param fileNames 如果fileNames为null或者fileNames长度小于要生成的文件个数，则默认生成的文件名为1,2,3...
	 */
	public  void createWordByOneListPanel(BillListPanel billList, String[] fileNames, Container parent) throws Exception{
		BillVO[] checkItemVOs = billList.getSelectedBillVOs();
		createWordByOneListPanel(billList.getTempletVO().getTempletcode(), checkItemVOs, fileNames, parent);

	}
	/**
	 * 要生成的文件只涉及到一个billList模板，不涉及子表，最简单的一种
	 * @param templateCode
	 * @param checkItemVOs
	 * @param fileNames如果fileNames为null或者fileNames长度小于要生成的文件个数，则默认生成的文件名为1,2,3...
	 * @param parent
	 */	public  void createWordByOneListPanel(String templateCode,BillVO[] checkItemVOs, String[] fileNames, Container parent) throws Exception{
		if (checkItemVOs == null || checkItemVOs.length == 0) {
			MessageBox.showSelectOne(parent);
			return;
		}
		int fileNum = checkItemVOs.length;
		String template_name = null;
		try {
			template_name=getTemplateName(null,templateCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (template_name == null) {
			MessageBox.show(parent, "xml或mht模板不存在！");
			return;
		}
		HashMap[] maps = new HashMap[fileNum];
		for (int i = 0; i < maps.length; i++) {
			maps[i] = convertBillVOToMap(checkItemVOs[i]);
		}
		createWordByMap(maps, fileNames, template_name, parent);

	}
	/**
	 * @param maps  其中每个HashMap应生成一个文件
	 * @param fileNames 如果fileNames为null或者fileNames长度小于要生成的文件个数，则使用默认文件名1,2,3...
	 * @param template_name  模板名称
	 * @param parent
	 */
	public  void createWordByMap(HashMap[] maps, String[] fileNames, String template_name, Container parent) throws Exception{
		if (maps == null || maps.length == 0) {
			MessageBox.show(parent, "传入的数据为空！");
		} else {
			int fileNum = maps.length;
			if (fileNames == null) {//如果传入的文件名称为空，或者文件名个数与内容个数不一致，则初始化fileNames数组为1,2,3。。。
				fileNames = new String[fileNum];
				for (int i = 0; i < fileNum; i++) {
					fileNames[i] = new Integer(i + 1).toString();
				}
			}
			IPushGRCServiceIfc service = null;
			try {
				service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (template_name.contains(";")) {
				int i = template_name.indexOf(";");
				template_name = template_name.substring(0, i);
			}
			String[] contents = service.getWordContents(maps, template_name);
			if (fileNum == 1) {
				saveOneWord(contents[0], fileNames[0], parent);
			} else {
				saveMulWord(contents, fileNames, parent);
			}
		}
	}

	/**
	 * billvo转化为hashmap，key为"表名.ItemKey"
	 * @param billvo
	 * @return
	 */
	public static  HashMap convertBillVOToMap(BillVO billvo) {
		String table_name = billvo.getSaveTableName();
		String[] keys = billvo.getKeys();
		HashMap map = new HashMap();
		for (int i = 0; i < keys.length; i++) {
			String value_str = getStrFromBillVO(billvo, keys[i]);
			map.put(table_name.toLowerCase() + "." + keys[i].toLowerCase(), value_str);
		}
		return map;
	}

	private static String getStrFromBillVO(BillVO billvo, String key) {
		String value_str = "";
		Object obj = billvo.getObject(key);
		if (obj == null)
			value_str = "";
		else {

			if (obj instanceof StringItemVO) {
				value_str = billvo.getStringValue(key);
			} else if (obj instanceof RefItemVO) {
				RefItemVO refVo = (RefItemVO) obj;
				value_str = refVo.getName();
			} else if (obj instanceof ComBoxItemVO) {
				ComBoxItemVO comVo = (ComBoxItemVO) obj;
				value_str = comVo.getName();
			}
		}
		return value_str;

	}

	/**
	 * 生成一个文件所需的所有BillVO统一转化为HashMap,key为"表名.ItemKey",value为相应的值或者值的列表
	 * @param vos 生成一个文件所需的所有BillVO,其中包括主表的一个Billvo，子表的一个或多个Billvo
	 * @return
	 */
	public  HashMap convertBillVOSToMap(BillVO[] vos) {
		if (vos == null || vos.length == 0)
			return null;
		if (vos.length == 1) {
			return convertBillVOToMap(vos[0]);
		} else {
			HashMap map = new HashMap();
			HashMap table_bills_map = getTableBillsMap(vos);
			String[] tables = (String[]) table_bills_map.keySet().toArray(new String[0]);
			for (int i = 0; i < tables.length; i++) {
				HashSet billSet = (HashSet) table_bills_map.get(tables[i]);
				Iterator it = billSet.iterator();
				if (billSet.size() == 1) {
					BillVO vo = (BillVO) it.next();
					String[] keys = vo.getKeys();
					for (int j = 0; j < keys.length; j++) {
						map.put(tables[i].toLowerCase() + "." + keys[j], getStrFromBillVO(vo, keys[j]));
					}
				} else {
					String[] keys = null;
					int m = 1;
					String newLineSig = "<w:p/>";
					while (it.hasNext()) {
						BillVO vo = (BillVO) it.next();
						if (keys == null)
							keys = vo.getKeys();
						for (int j = 0; j < keys.length; j++) {
							String newValue = null;
							if (m == 1) {
								newValue = m + "." + getStrFromBillVO(vo, keys[j]) + newLineSig;
							} else if (m > 1 && m < billSet.size()) {
								String oldValue = (String) map.get(tables[i].toLowerCase() + "." + keys[j]);
								newValue = oldValue + m + "." + getStrFromBillVO(vo, keys[j]) + newLineSig;
							} else if (m == billSet.size()) {
								String oldValue = (String) map.get(tables[i].toLowerCase() + "." + keys[j]);
								newValue = oldValue + m + "." + getStrFromBillVO(vo, keys[j]);
							}
							map.put(tables[i].toLowerCase() + "." + keys[j], (newValue == null) ? "" : newValue);
						}
						m++;
					}
				}
			}
			return map;
		}

	}

	/**
	 * 得到BillVO数组，按表名分类
	 * @param vos
	 * @return key为table名称，value为billvo的HashSet
	 */
	private  HashMap getTableBillsMap(BillVO[] vos) {
		HashMap tepmap = new HashMap();
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getSaveTableName() == null) {
				System.out.println("=========billvo未设置模板信息！导致表名为null===========");
			} else {
				tepmap.put(vos[i].getSaveTableName(), null);
			}
		}
		String[] tables = (String[]) tepmap.keySet().toArray(new String[0]);
		HashMap table_bills_map = new HashMap();
		for (int i = 0; i < tables.length; i++) {
			HashSet set = new HashSet();
			for (int j = 0; j < vos.length; j++) {
				if (tables[i].equals(vos[j].getSaveTableName()))
					set.add(vos[j]);
			}
			table_bills_map.put(tables[i], set);
		}
		return table_bills_map;
	}

	/**
	 * 单个文件保存操作
	 * @param content
	 * @param fileName
	 * @param parent
	 */
	private  void saveOneWord(String content, String fileName, Container parent) {
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setDialogTitle("请选择文件存放目录：");
		file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_chooser.setSelectedFile(new File("c:\\" + fileName + ".doc"));
		int flag = file_chooser.showSaveDialog(parent);
		if (flag == 1 || file_chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = file_chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path);
		FileOutputStream os;
		try {
			os = new FileOutputStream(f);
			new TBUtil().writeBytesToOutputStream(os, content.getBytes("UTF-8"));
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(parent, "文件保存成功！");
	}

	/**
	 * 生成多个文件 
	 * @param contents  文件内容数组
	 * @param fileNames 文件名数组，不带后缀名，如果值为NULL，则默认为1,2,3...
	 * * @param parent
	 */
	private  void saveMulWord(String[] contents, String[] fileNames, Container parent) {
		if (contents != null && contents.length != 0) {
			int fileNum = contents.length;
			if (fileNames == null || fileNames.length < fileNum) {//如果传入的文件名称为空，或者文件名个数小于内容个数不一致，则初始化fileNames数组为1,2,3。。。
				fileNames = new String[fileNum];
				for (int i = 0; i < fileNum; i++) {
					fileNames[i] = new Integer(i + 1).toString();
				}
			}
			JFileChooser file_chooser = new JFileChooser();
			file_chooser.setDialogTitle("共有" + fileNum + "个文件，请选择文件存放目录：");
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int flag = file_chooser.showSaveDialog(parent);
			if (flag == 1) {
				return;
			}
			final String str_path = file_chooser.getSelectedFile().getAbsolutePath();
			if (str_path == null) {
				return;
			}
			try {
				for (int i = 0; i < fileNum; i++) {
					File f = new File(str_path + "\\" + fileNames[i] + ".doc");
					FileOutputStream os = new FileOutputStream(f);
					new TBUtil().writeBytesToOutputStream(os, contents[i].getBytes("UTF-8"));
					os.flush();
					os.close();
				}
				MessageBox.show(parent, "共" + fileNum + "个文件,保存成功！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageBox.show(parent, "文件内容为空！保存失败！");
		}
	}

	/**
	 * 通过编码获得模板文件
	 * @param parent
	 * @param pk_name  xml或mht模板的索引
	 * @return
	 */
	public  static String getTemplateName(Container parent, String pk_name) {
		String template_name = null;
		boolean word_setup=true;
		try {
			if (word_setup) {
				template_name = UIUtil.getStringValueByDS(null, "select xml_name from print_template where pk_name=" + "'" + pk_name.toLowerCase() + "' or pk_name=" + "'" + pk_name.toUpperCase() + "'");
			} else {
				template_name = UIUtil.getStringValueByDS(null, "select mht_name from print_template where pk_name=" + "'" + pk_name.toLowerCase() + "' or pk_name=" + "'" + pk_name.toUpperCase() + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((template_name != null) && (!template_name.trim().equals(""))) {
			return template_name;
		} else {
			if(parent!=null){
			MessageBox.show(parent,"找不到编码为\""+pk_name+"\"的打印模板文件！请检查【打印模板维护】中的模板设置！");
			}
			return null;
		}
	}
}
