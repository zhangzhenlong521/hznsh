package cn.com.infostrategy.bs.mdata;

import java.util.HashMap;

import cn.com.infostrategy.to.mdata.ButtonDefineVO;

/**
 * 按钮校验的拦截器
 * @author Administrator
 *
 */
public interface WLTButtonInitAllowValidateIfc {

	/**
	 * 有效校验的成功与否!!
	 * 一个按钮是否有效一般分两种情况:
	 * 一种情况与某一条记录密切相关!!!另一种情况是不与记录相关,而是直接与角色或人员相关!!!
	 * 不与记录相关时，可以在初始化时完成校验逻辑,也可在点击时完成校验逻辑!!一般会要求在初始化时校验逻辑!!!
	 * 与记录相关时,初始化时不会做权限过滤!!
	 * @param  _btndfo 按钮定义的说明类!!!!
	 * @param _loginUserId 登录人员id,自动传进去
	 * @param _initSharePoolMap 为了提高性能初始化时大家共享数据用的!!
	 * @return  是否有效,如果是true，则按钮有效,如果是false,则按钮无效!!
	 */
	public boolean allowValieDate(ButtonDefineVO _btndfo, String _loginUserId, HashMap _initSharePoolMap); //

	/**
	 * 校验失败时的原因说明,不一定非要实现,如果实现了,则能更有效的从页面上通过点击右键查看到原因!!!
	 * @return
	 */
	public String getAllowInfo(); //
}
