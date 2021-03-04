package cn.com.infostrategy.to.mdata.templetvo;

import java.io.Serializable;

/**
 * 有时需要大量的TMO直接创建控件,如果这些类都放在客户端，会导致下载的Class非常多,最后甚至成了客户端下载的主要内容.
 * 这时我就希望在服务器端定义这些对象,然后每次都去服务器端取这个定义，同时立即在服务器为之生成Pub_Templet_1VO对象，我只要送一个类名给服务器就够了，服务器先反射创建TMO对象，然后创建Pub_Templet_1VO.
 * 这个类就叫ServerTMO,它只有一个类变量，就是服务器端TMO对象的全名,因为BillList等控件已支持直接通过编码字符串类型创建实例，为了区分，必须创建一个对象，这就是必须再搞个对象的原因
 * 如果在客户端直接送TMO给服务器端生成Pub_Templet_1VO，一来是送的数据量还大,二来还是省不掉进行一次远程调用的，反而不如直接由在服务器创建来得性能高!! 
 * 但这样做也有一个缺点，就是送字符串参数,会造成重构对象路径时不能自动更改.
 * 建议这些类统统放在服务器端 bs.servertmo. 这个包名中...
 * @author xch
 *
 */
public class ServerTMODefine implements Serializable {
	private static final long serialVersionUID = 6082587737116495177L;
	private String tmoClassName = null; //类名
	private String[] constructorPars = null; //构造参数,后来遇到为了重用一个TMO,可以有多个构造参数!!比如:TMO_BOM_B

	/**
	 * 禁止默认构造方法
	 */
	private ServerTMODefine() {
	}

	public ServerTMODefine(String _serverTmoClassName) {
		this.tmoClassName = _serverTmoClassName; //
	}

	public ServerTMODefine(String _serverTmoClassName, String[] _pars) {
		this.tmoClassName = _serverTmoClassName; //
		this.constructorPars = _pars; //
	}

	public String getTmoClassName() {
		return tmoClassName;
	}

	public String[] getConstructorPars() {
		return constructorPars;
	}

	public void setConstructorPars(String[] constructorPars) {
		this.constructorPars = constructorPars;
	}

}
