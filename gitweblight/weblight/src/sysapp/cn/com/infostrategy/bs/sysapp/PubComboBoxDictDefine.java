package cn.com.infostrategy.bs.sysapp;

import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 以前下拉框数据都在pub_comboboxdict表中,但有些数据的平台一升级后,发现以前的各个项目库中该表都要维护,很麻烦.
 * 所以还支持直接直接在代码中定义好,下拉框定义也支持从该类中取
 * 该类就是常用的平台下拉框定义
 * @author xch
 *
 */
public class PubComboBoxDictDefine {

	/**
	 * 系统菜单路径类型
	 * @return
	 */
	public String[][] getMenuCommandType() {
		return new String[][] { //
		{ "ST", "ST", "风格模板" }, //
				{ "0A", "0A", "Format公式面板" }, //
				{ "11", "11", "XML注册功能点" }, //
				{ "00", "00", "自定义WorkpPanel" }, //
				{ "99", "99", "自己定义Frame" }, //
		};
	}

	/**
	 * 系统注册按钮类型
	 * @return
	 */
	public String[][] getSysRegButtonType() {
		return WLTButton.getSysRegButtonType(); //
	}

	//查询类型定义!
	public String[][] getQueryItemCreateType() {
		return new String[][] { //
		{ "默认机制", null, "默认机制" },//
				{ "In机制", null, "In机制" },//
				{ "LikeOr机制", null, "LikeOr机制" },//
				{ "SQLServer全文检索", null, "SQLServer全文检索" },//
				{ "SQLServer全文检索2", null, "SQLServer全文检索2" },//
				{ "自定义SQL", null, "自定义SQL" },//
				{ "自定义类", null, "自定义类" },//
				{ "不参与", null, "不参与" },//
		}; //
	}

	public String[][] getTempletItemType() {
		return new String[][] { //
		{ "Label", null, "Label" }, //标签说明
				{ "文本框", null, "文本框" }, //
				{ "数字框", null, "数字框" }, //
				{ "密码框", null, "密码框" }, //
				{ "下拉框", null, "下拉框" }, //
				{ "动态下拉框", null, "动态下拉框" }, //

				{ "参照", null, "参照" }, //直接使用SQL的表型参照!!
				{ "树型参照", null, "树型参照" }, //
				{ "多选参照", null, "多选参照" }, //
				{ "自定义参照", null, "自定义参照" }, //
				{ "列表模板参照", null, "列表模板参照" }, //
				{ "树型模板参照", null, "树型模板参照" }, //
				{ "注册样板参照", null, "注册样板参照" }, //
				{ "注册参照", null, "注册参照" }, //

				{ "勾选框", null, "勾选框" }, //
				{ "多行文本框", null, "多行文本框" }, //
				{ "大文本框", null, "大文本框" }, //
				{ "富文本框", null, "富文本框" }, //即支持粗体,斜体,下划线等效果的文本框!!它存储的是压缩后的64位码!
				{ "按钮", null, "按钮" }, //

				{ "文件选择框", null, "文件选择框" }, //
				{ "引用子表", null, "引用子表" }, //
				{ "导入子表", null, "导入子表" }, //
				{ "Excel控件", null, "Excel控件" }, //
				{ "Office控件", null, "Office控件" }, //在线编辑正文等功能!!

				{ "日历", null, "日历" }, //
				{ "时间", null, "时间" }, //
				{ "图片选择框", null, "图片选择框" }, //
				{ "图片上传", null, "图片上传" }, //就是直接上传一个图片,比如HR系统中的人员照片等! 在卡片状态下就是直接显示该图片,而不是只显示名称,它是将图片存储在另一个系统表中!
				{ "颜色", null, "颜色" }, //
				{ "计算器", null, "计算器" }, //

				{ "自定义控件", null, "自定义控件" }, //自定义控件,高峰后来搞的一个完全自已创建的
				{ "正则表达式控件", null, "正则表达式控件" },//[zzl]
		};

	}

	public String[][] getTempletItemEditable() {
		return new String[][] { //
		{ "1", "001", "全部可编辑" }, //标签说明
				{ "2", "002", "仅新增可编辑" }, //
				{ "3", "003", "仅修改可编辑" }, //
				{ "4", "004", "全部禁用" }, //
		};

	}

	//模板子项是否必输项的分类!
	public String[][] getTempletItemMustInputType() {
		return new String[][] { //
		{ "Y", "Y", "必输项" }, //标签说明
				{ "W", "W", "警告项" }, //
				{ "N", "N", "自由项" }, //
		};

	}

}
