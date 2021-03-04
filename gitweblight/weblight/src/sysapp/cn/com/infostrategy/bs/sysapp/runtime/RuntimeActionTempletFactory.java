package cn.com.infostrategy.bs.sysapp.runtime;

import java.util.ArrayList;

import cn.com.infostrategy.to.sysapp.runtime.RtActionTempletVO;

/**
 * 定义动态脚本的模板定义工厂!
 * @author Administrator
 *
 */
public class RuntimeActionTempletFactory {

	private static RuntimeActionTempletFactory instance = null; //

	private RuntimeActionTempletFactory() {
	}

	//取得一个实例!!
	public static RuntimeActionTempletFactory getInstance() {
		if (instance == null) {
			instance = new RuntimeActionTempletFactory(); //
		}
		return instance; //
	}

	//定义所有的动态执行脚本的模板
	public RtActionTempletVO[] getAllTempletVOs() {
		ArrayList al_templets = new ArrayList(); //
		RtActionTempletVO templetVO = null;

		//数据权限过滤
		templetVO = new RtActionTempletVO(); //
		templetVO.setTempletName("cn.com.infostrategy.bs.mdata.AbstractBillDataBSFilter"); //
		templetVO.setSuperClassType("extends"); //
		templetVO.setPackageSubfix("bs"); //是BS端的!!!需要从包名就看出来!!
		templetVO.setClassPrefix("BSFilter_"); //
		templetVO.setImported(new String[] { //
				"cn.com.infostrategy.to.common.HashVO", //
						"cn.com.infostrategy.to.common.TBUtil", //
						"cn.com.infostrategy.bs.common.CommDMO", //
				}); //

		templetVO.setFunctiondoc("数据过滤器之中间件内,实现该方法,然后设置各个HashVO的setVisible,实现过滤效果"); //
		templetVO.setFunctionName("public void filterBillData(HashVO[] _hvs) throws Exception"); //
		templetVO.setFunctionContent(new String[] { //
				"//for (int i=0;i<_hvs.length;i++) {", //
						"//  _hvs[i].setVisible(true);", //
						"//}", //
				}); ///
		al_templets.add(templetVO); //

		//按钮点击事件!!
		templetVO = new RtActionTempletVO(); //
		templetVO.setTempletName("cn.com.infostrategy.ui.mdata.WLTActionListener"); //
		templetVO.setSuperClassType("implements"); //
		templetVO.setPackageSubfix("ui"); //是BS端的!!!需要从包名就看出来!!
		templetVO.setClassPrefix("BtnAL_"); //按钮点击事件
		templetVO.setImported(new String[] { //
				"cn.com.infostrategy.ui.common.UIUtil", //
						"cn.com.infostrategy.to.common.TBUtil", //
						"cn.com.infostrategy.to.mdata.BillVO", //
						"cn.com.infostrategy.to.common.HashVO", //
						"cn.com.infostrategy.ui.common.MessageBox", //
				}); //

		templetVO.setFunctiondoc("按钮点击动作,可以从event中取到面板与数据!!"); //
		templetVO.setFunctionName("public void actionPerformed(cn.com.infostrategy.ui.mdata.WLTActionEvent _event) throws Exception"); //
		templetVO.setFunctionContent(new String[] { //
				"return;", //
				}); ///
		al_templets.add(templetVO); //

		//列表选择变化事件

		//树型选择事件变化

		//卡片初始化时逻辑

		//

		return (RtActionTempletVO[]) al_templets.toArray(new RtActionTempletVO[0]); //
	}

	/**
	 * 取得某一个模板定义
	 * @param _name
	 * @return
	 */
	public RtActionTempletVO getTempletVOByName(String _name) {
		RtActionTempletVO[] allVOs = getAllTempletVOs(); //
		for (int i = 0; i < allVOs.length; i++) {
			if (allVOs[i].getTempletName().equals(_name)) {
				return allVOs[i]; //
			}
		}
		return null; //
	}

	/**
	 * 得到整个模板的Java代码...
	 * @param _name
	 * @return
	 */
	public String getWholeTempletJavaCode(String _name) {
		RtActionTempletVO templetVO = getTempletVOByName(_name); //
		if (templetVO == null) {
			return null;
		}

		StringBuffer sb_code = new StringBuffer(); //
		sb_code.append("package cn.com.weblight.runtime;  \r\n"); //包名
		sb_code.append("\r\n"); //
		for (int i = 0; i < templetVO.getImported().length; i++) {
			sb_code.append("import " + templetVO.getImported()[i] + "; \r\n"); //
		}
		sb_code.append("\r\n"); //

		sb_code.append("public class " + templetVO.getClassPrefix() + "_序号 " + templetVO.getSuperClassType() + " " + templetVO.getTempletName() + " {\r\n"); //
		sb_code.append("\r\n"); //
		sb_code.append("//" + templetVO.getFunctiondoc() + "\r\n"); //

		sb_code.append(templetVO.getFunctionName() + " { \r\n"); //

		for (int i = 0; i < templetVO.getFunctionContent().length; i++) {
			sb_code.append(templetVO.getFunctionContent()[i] + "\r\n"); //
		}

		sb_code.append("}\r\n"); //end function
		sb_code.append("\r\n"); //
		sb_code.append("}\r\n"); //end class
		return sb_code.toString(); //
	}

	//得到所有模板名称清单!!
	public String[] getAllTempletNames() {
		RtActionTempletVO[] allVOs = getAllTempletVOs(); //
		String[] str_names = new String[allVOs.length]; //
		for (int i = 0; i < str_names.length; i++) {
			str_names[i] = allVOs[i].getTempletName(); //
		}
		return str_names; //
	}

	public static void main(String[] _args) {
		System.out.println(RuntimeActionTempletFactory.getInstance().getWholeTempletJavaCode("数据权限过滤")); //

	}
}
