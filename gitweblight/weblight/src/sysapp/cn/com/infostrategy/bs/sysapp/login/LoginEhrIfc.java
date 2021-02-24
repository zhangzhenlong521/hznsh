package cn.com.infostrategy.bs.sysapp.login;

/**
 * 用于从第三方系统去校验用户名与密码
 * 一般来说都是从用户的Ehr系统中校验,所以这个类干脆就叫LoginEhrIfc,方法也干脆叫checkInEhr(),其实并不一定是非要到hr系统中校验的,你可以做任何逻辑的校验!
 * @author xch
 *
 */
public interface LoginEhrIfc {

	/**
	 * 送入用户名与密码去校验,如果校验不对则抛异常!!
	 * @param _usercode
	 * @param _pwd
	 * @throws Exception
	 */
	public void checkInEhr(String _usercode, String _pwd) throws Exception;
}
