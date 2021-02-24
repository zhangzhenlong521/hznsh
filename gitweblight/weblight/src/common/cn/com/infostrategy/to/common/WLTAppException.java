package cn.com.infostrategy.to.common;

/**
 * 平台业务异常,继承于RuntimeException,即运行异常,而不是编译异常,这样就不会要求在代码中捕获了
 * @author xch
 *
 */
public class WLTAppException extends RuntimeException {

	private static final long serialVersionUID = -5553372557234353936L;

	public WLTAppException(String _message) {
		super(_message); //
	}

}


