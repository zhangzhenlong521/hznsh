package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * 以前的各种控件都是一个个的公式与VO,后来统一变成新的getCommUC了,但为了支持与兼容旧的公式,所以做了这样一个旧的公式,然后将所有旧的公式都统一执行在这里面!
 * 然后通过参数UCType来决定是什么类型!! 然后返回的数据对象永远都是CommUCDefineVO,然后在控件的实现代码中将原来的各种判断改成一个个的方法,然后都从CommUCDefineVO中取!
 * @author xch
 *
 */
public class GetOldCommUCJEP extends PostfixMathCommand {

	private String UCType = null; //控件类型

	/**
	 *构造方法
	 */
	public GetOldCommUCJEP(String _UCType) {
		numberOfParameters = -1; //不确定的参数!!
		this.UCType = _UCType; //控件类型!!
	}

	/**
	 * 运行时执行的方法!!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		if (this.UCType.equals("参照")) { //一共12种主要控件,以后还要增加文件选择框等,即理论上任何一种控件都需要参数!! 而且参数扩展非常方便!!!
			getTableRef(inStack);
		} else if (this.UCType.equals("树型参照")) {
			getTreeRef(inStack);
		} else if (this.UCType.equals("多选参照")) {
			getMultiRef(inStack);
		} else if (this.UCType.equals("自定义参照")) {
			getCustRef(inStack);
		} else if (this.UCType.equals("列表模板参照")) {
			getListTempletRef(inStack);
		} else if (this.UCType.equals("树型模板参照")) {
			getTreeTempletRef(inStack);
		} else if (this.UCType.equals("注册样板参照")) {
			getRegFormatRef(inStack);
		} else if (this.UCType.equals("注册参照")) {
			getRegisterRef(inStack);
		} else if (this.UCType.equals("Excel控件")) {
			getExcelRef(inStack);
		} else if (this.UCType.equals("Office控件")) {
			getOfficeRef(inStack);
		} else if (this.UCType.equals("引用子表")) {
			getChildTable(inStack);
		} else if (this.UCType.equals("导入子表")) {
			getChildTableImport(inStack);
		}
	}

	/**
	 * 表型参照!!
	 * @param inStack
	 */
	private void getTableRef(Stack inStack) {
		String[] str_sqls = new String[curNumberOfParameters];
		for (int i = 0; i < str_sqls.length; i++) {
			str_sqls[str_sqls.length - 1 - i] = (String) inStack.pop(); //
		}
		CommUCDefineVO ucDfVO = new CommUCDefineVO("参照"); //以前写成了表型参照,是不对的!!!
		for (int i = 0; i < str_sqls.length; i++) {
			ucDfVO.setConfValue("SQL语句" + (i + 1), str_sqls[i]); //
		}
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 树型参照
	 * @param inStack
	 */
	private void getTreeRef(Stack inStack) {
		Object param_1 = inStack.pop(); //ParentField
		Object param_2 = inStack.pop(); //ID
		Object param_3 = inStack.pop(); //SQL

		String str_sql = (String) param_3; //
		String str_pkField = (String) param_2; //
		String str_parentPKfield = (String) param_1; //

		CommUCDefineVO ucDfVO = new CommUCDefineVO("树型参照"); //
		ucDfVO.setConfValue("SQL语句", str_sql); //
		ucDfVO.setConfValue("PKField", str_pkField); //
		ucDfVO.setConfValue("ParentPKField", str_parentPKfield); //

		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 多选参照
	 */
	private void getMultiRef(Stack inStack) {
		Object param_1 = inStack.pop();
		String str_sql = (String) param_1; //
		CommUCDefineVO ucDfVO = new CommUCDefineVO("多选参照"); //
		ucDfVO.setConfValue("SQL语句", str_sql); //
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 自定义参照
	 * @param inStack
	 */
	private void getCustRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters - 1];
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}
		HashMap sysRefMap = new HashMap(); //
		sysRefMap.put("机构选择", "cn.com.infostrategy.ui.sysapp.refdialog.CommonCorpDeptRefDialog"); //
		sysRefMap.put("登录人员直属机构", "cn.com.infostrategy.ui.sysapp.corpdept.LoginUserDirtDeptRefDialog"); //
		sysRefMap.put("万能日期范围选择", "cn.com.infostrategy.ui.sysapp.refdialog.CommonDateTimeRefDialog"); //
		sysRefMap.put("静态与动态脚本定义", "cn.com.infostrategy.ui.sysapp.runtime.RunTimeActionEditRefDialog"); //

		String str_classname = (String) inStack.pop();//第一个是类名,
		if (str_classname.indexOf(".") < 0) { //如果没有逗号,即不是类名,而可能是特定的参照
			str_classname = (String) sysRefMap.get(str_classname); //转换一下
		}
		CommUCDefineVO ucDfVO = new CommUCDefineVO("自定义参照"); //
		ucDfVO.setConfValue("自定义类名", str_classname); //
		for (int i = 0; i < str_pa.length; i++) { //
			ucDfVO.setConfValue("参数" + (i + 1), str_pa[i]); //
		}
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 列表模板参照!!!
	 * @param inStack
	 */
	private void getListTempletRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters]; //创建报有参数列表
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}

		CommUCDefineVO ucDfVO = new CommUCDefineVO("列表模板参照"); //
		if (str_pa.length == 1) { //如果只有一个参数
			String str_templetcode = str_pa[0]; //
			ucDfVO.setConfValue("模板编码", str_templetcode); //
			inStack.push(ucDfVO); //置入堆栈!!
		} else if (str_pa.length == 5) { //如果是5个参数!!
			String str_templetCode = str_pa[0]; //模板编码
			String str_idField = str_pa[1]; //参照ID
			String str_nameField = str_pa[2]; //参照Name
			String str_refCustCondition = str_pa[3]; //参照自定义条件
			String str_parameters = str_pa[4]; //设置参数项
			ucDfVO.setConfValue("模板编码", str_templetCode); //
			if (str_idField != null && !str_idField.trim().equals("")) {
				ucDfVO.setConfValue("ID字段", str_idField); //
			}
			if (str_nameField != null && !str_nameField.trim().equals("")) {
				ucDfVO.setConfValue("NAME字段", str_nameField); //
			}
			if (str_refCustCondition != null && !str_refCustCondition.trim().equals("")) {
				ucDfVO.setConfValue("附加SQL条件", str_refCustCondition); //	
			}
			if (str_parameters != null && !str_parameters.trim().equals("")) { //最后还可能有参数!!
				String[] parameters = new TBUtil().split(str_parameters, ";"); //以分号分割!!
				for (int i = 0; i < parameters.length; i++) {
					try {
						int li_pos = parameters[i].indexOf("="); //
						String str_key = parameters[i].substring(0, li_pos); //
						String str_value = parameters[i].substring(li_pos + 1, parameters[i].length()); //
						ucDfVO.setConfValue(str_key, str_value); //置入!!比如有:可以多选=Y,自动查询=Y
					} catch (Exception ex) {
						System.err.println("解析列表模板参照公式参数发生异常:" + ex.getClass().getName() + ":" + ex.getMessage()); //
					}
				}
			}
			inStack.push(ucDfVO); //置入堆栈!!
		} else {
			System.err.println("执行表型参照定义公式时发生错误，参数的个数不对！只能接收1个或5个,现在是" + str_pa.length + "个参数"); //
		}
	}

	/**
	 * 树型模板参照,参数最多最复杂,也是最需要优化的参照!!
	 * @param inStack
	 */
	private void getTreeTempletRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters]; //创建报有参数列表
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}

		CommUCDefineVO ucDfVO = new CommUCDefineVO("树型模板参照"); //
		if (str_pa.length == 1) { //如果只有一个参数!!
			ucDfVO.setConfValue("模板编码", (String) str_pa[0]); //
			inStack.push(ucDfVO); //置入堆栈!!
		} else if (str_pa.length == 5) { //如果是5个参数!
			String str_templetcode = (String) str_pa[0]; //模板编码
			String str_idField = (String) str_pa[1]; //返回id的字段名
			String str_nameField = (String) str_pa[2]; //返回名称的字段名
			String str_queryCondition = (String) str_pa[3]; //
			String str_propdefine = (String) str_pa[4]; //所有属性，比如："可以多选=N;只能选叶子=N;只留前几层=0;"

			ucDfVO.setConfValue("模板编码", str_templetcode); //
			if (str_idField != null && !str_idField.trim().equals("")) {
				ucDfVO.setConfValue("ID字段", str_idField); //
			}
			if (str_nameField != null && !str_nameField.trim().equals("")) {
				ucDfVO.setConfValue("NAME字段", str_nameField); //
			}
			if (str_queryCondition != null && !str_queryCondition.trim().equals("")) {
				ucDfVO.setConfValue("附加SQL条件", str_queryCondition); //
			}

			//其他你属性定义
			if (str_propdefine != null && !str_propdefine.trim().equals("")) {
				String[] str_items = new TBUtil().split(str_propdefine, ";"); //先按分号拆开
				for (int i = 0; i < str_items.length; i++) {
					int li_pos = str_items[i].indexOf("="); //
					if (li_pos > 0) { //如果有等号分隔,
						String str_key = str_items[i].substring(0, li_pos).trim(); //
						String str_value = str_items[i].substring(li_pos + 1, str_items[i].length()).trim(); //
						ucDfVO.setConfValue(str_key, str_value); //设置附加属性,比如:可以多选=N;只能选叶子=N;只留前几层=0;
					}
				}
			}
			inStack.push(ucDfVO); //置入堆栈!!
		} else {
			System.err.println("执行树型参照定义公式时发生错误，参数的个数不对！只能接收1个或5个,现在是" + str_pa.length + "个参数"); //
		}

	}

	/**
	 * 注册样板参照!!
	 * @param inStack
	 */
	private void getRegFormatRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("注册样板参照"); // 
		if (curNumberOfParameters == 1) {
			ucDfVO.setConfValue("注册编码", (String) inStack.pop()); //
			ucDfVO.setConfValue("风格", "0"); //
		} else if (curNumberOfParameters == 2) {
			Object param_2 = inStack.pop();
			Object param_1 = inStack.pop();
			ucDfVO.setConfValue("注册编码", (String) param_1); //
			ucDfVO.setConfValue("数据生成器", (String) param_2); //
			ucDfVO.setConfValue("风格", "0"); //
		} else if (curNumberOfParameters == 3) {
			Object param_3 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_1 = inStack.pop(); //
			ucDfVO.setConfValue("注册编码", (String) param_1); //
			ucDfVO.setConfValue("数据生成器", (String) param_2); //
			ucDfVO.setConfValue("风格", (String) param_3); //一共有四种风格,即上下/左右/多页签!!
		}
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 注册参照!!
	 * @param inStack
	 */
	private void getRegisterRef(Stack inStack) {
		Object param_1 = inStack.pop();
		String str_regrefname = (String) param_1; //
		CommUCDefineVO ucDfVO = new CommUCDefineVO("注册参照"); // 
		ucDfVO.setConfValue("注册名称", str_regrefname); //
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * Excel参照!!
	 * @param inStack
	 */
	private void getExcelRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("Excel控件"); //
		if (curNumberOfParameters == 1) {
			Object str_excelcode = inStack.pop(); //
			if (str_excelcode instanceof HashMap) {
				ucDfVO.setConfValueAll((HashMap) str_excelcode); //
			} else {
				ucDfVO.setConfValue("模板编码", (String) str_excelcode); //
			}
		} else if (curNumberOfParameters == 2) {
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("模板编码", str_excelcode); //
			ucDfVO.setConfValue("初始化方法", str_ifc); //
		} else if (curNumberOfParameters == 3) {
			String str_custbtnPaenl = (String) inStack.pop(); //
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("模板编码", str_excelcode); //
			ucDfVO.setConfValue("初始化方法", str_ifc); //
			ucDfVO.setConfValue("按钮自定义面板", str_custbtnPaenl); //
		} else if (curNumberOfParameters == 4) {
			String str_returnCellKey = (String) inStack.pop(); //
			String str_custbtnPaenl = (String) inStack.pop(); //
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("模板编码", str_excelcode); //
			ucDfVO.setConfValue("初始化方法", str_ifc); //
			ucDfVO.setConfValue("按钮自定义面板", str_custbtnPaenl); //
			ucDfVO.setConfValue("返回显示值", str_returnCellKey); //
		} else {
			System.err.println("执行树型参照定义公式时发生错误，参数的个数不对！只能接收1-4个,现在是" + curNumberOfParameters + "个参数"); //
		}
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * Office控件!!!
	 * @param inStack
	 */
	private void getOfficeRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("Office控件"); //
		if (this.curNumberOfParameters == 1) { //如果是一个参数【就指定是doc还是xls】
			String str_filetype = (String) inStack.pop(); //
			ucDfVO.setConfValue("文件类型", str_filetype); //
		} else if (this.curNumberOfParameters == 2) { //如果是两个参数【第一个是doc/xls,第二个是模板文件】
			String str_filetempletname = (String) inStack.pop(); //
			String str_filetype = (String) inStack.pop(); //
			ucDfVO.setConfValue("文件类型", str_filetype); //
			ucDfVO.setConfValue("模板文件名", str_filetempletname); //如果没有找到文件,或第一次加载,则自动打开模板,默认是blank.doc
		} else if (this.curNumberOfParameters == 3) { //如果是三个参数【第一个是doc/xls,第二个是模板文件,第三个书签生成器的类名】
			String str_bookmarcreater = (String) inStack.pop(); //得到某个菜单的名字,主要为了法律审查用OFFICE控件生成编码用
			String str_filetempletname = (String) inStack.pop(); //
			String str_filetype = (String) inStack.pop(); //			
			ucDfVO.setConfValue("文件类型", str_filetype); //
			ucDfVO.setConfValue("模板文件名", str_filetempletname); //如果没有找到文件,或第一次加载,则自动打开模板,默认是blank.doc
			ucDfVO.setConfValue("书签生成器", str_bookmarcreater); //
		} else if (this.curNumberOfParameters == 4) { //如果是4个参数【第一个是doc/xls,第二个是模板文件,第三个书签生成器的类名,第四个控件厂家,比如金格/千航】
			String str_officeActivex = (String) inStack.pop(); //office控件
			String str_bookmarcreater = (String) inStack.pop(); //用来生成bookMark的的构建类!!
			String str_filetempletname = (String) inStack.pop(); //模板文件名称
			String str_filetype = (String) inStack.pop(); //文件类型
			ucDfVO.setConfValue("文件类型", str_filetype); //
			ucDfVO.setConfValue("模板文件名", str_filetempletname); //如果没有找到文件,或第一次加载,则自动打开模板,默认是blank.doc
			ucDfVO.setConfValue("书签生成器", str_bookmarcreater); //
			ucDfVO.setConfValue("控件厂家", str_officeActivex); //有金格/千航,以后可能还有其他!
		} else if (this.curNumberOfParameters == 5) { //如果是5个参数【第一个是doc/xls,第二个是模板文件,第三个书签生成器的类名,第四个控件厂家,比如金格/千航,第5个是指定是否显示导入按钮】
			Object obj_importBVisible = inStack.pop(); //是否显示导入按钮
			String str_officeActivex = (String) inStack.pop(); //office控件
			String str_bookmarcreater = (String) inStack.pop(); //用来生成bookMark的的构建类!!
			String str_filetempletname = (String) inStack.pop(); //模板文件名称
			String str_filetype = (String) inStack.pop(); //文件类型
			ucDfVO.setConfValue("文件类型", str_filetype); //
			ucDfVO.setConfValue("模板文件名", str_filetempletname); //如果没有找到文件,或第一次加载,则自动打开模板,默认是blank.doc
			ucDfVO.setConfValue("书签生成器", str_bookmarcreater); //
			ucDfVO.setConfValue("控件厂家", str_officeActivex);
			if (obj_importBVisible instanceof String) { //如果是字符串!
				String str_importBVisible = (String) obj_importBVisible; //
				ucDfVO.setConfValue("是否显示导入按钮", str_importBVisible); //是true/false
			} else if (obj_importBVisible instanceof java.util.HashMap) { //后来为了扩展方便,干脆直接搞了个getParAsMap()方法可以直接返回哈希表!!还没不费事,还且别扭!
				HashMap parMap = (HashMap) obj_importBVisible; //
				ucDfVO.setConfValueAll(parMap); //全部加入!!!比如:【存储目录,是否显示导入按钮】等参数!!
			}
		}
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 引用子表
	 * @param inStack
	 */
	private void getChildTable(Stack inStack) {
		String param_foreignkey = (String) inStack.pop(); //外键
		String param_primarykey = (String) inStack.pop(); //主键
		String param_billtempletcode = (String) inStack.pop(); //模板编码
		CommUCDefineVO ucDfVO = new CommUCDefineVO("引用子表"); //
		ucDfVO.setConfValue("模板编码", param_billtempletcode); //
		ucDfVO.setConfValue("主键字段名", param_primarykey); //
		ucDfVO.setConfValue("关联外键字段名", param_foreignkey); //
		inStack.push(ucDfVO); //置入堆栈!!
	}

	/**
	 * 导入子表,孙富君搞的,个人感觉完全可以与引用子表合在一起! 只需搞个参数区分一下就够了,根本没必要又弄成一种新的控件!!
	 * @param inStack
	 */
	private void getChildTableImport(Stack inStack) {
		String param_foreignkey = (String) inStack.pop(); //A的外键;分隔
		String param_primarykey = (String) inStack.pop(); //B的主键
		String param_primaryname = (String) inStack.pop(); //B的名称
		String param_billtempletcode = (String) inStack.pop(); //B模板编码

		CommUCDefineVO ucDfVO = new CommUCDefineVO("导入子表"); //
		ucDfVO.setConfValue("模板编码", param_billtempletcode); //
		ucDfVO.setConfValue("主键字段名", param_primarykey); //
		ucDfVO.setConfValue("主键字段显示名", param_primaryname); //
		ucDfVO.setConfValue("关联外键字段名", param_foreignkey); //
		inStack.push(ucDfVO); //置入堆栈!!
	}

}
