package cn.com.infostrategy.ui.common;

import java.io.Serializable;
import java.util.Hashtable;

import cn.com.infostrategy.to.common.Queue;

public class ClientSequenceFactory implements Serializable {

	private static final long serialVersionUID = -4971518506217700714L;

	private static ClientSequenceFactory factory = new ClientSequenceFactory();

	private static Hashtable ht = new Hashtable();

	private FrameWorkCommServiceIfc service = null;

	private int li_batchnumber = 10; //一次从远程取多少!是10个

	private ClientSequenceFactory() {
	}

	public final static ClientSequenceFactory getInstance() {
		return factory;
	}

	/**
	 * 取得一个序列的值!!客户端只有这一个方法，服务端有两个，一个是为客户端调用的，一个是为服务器其他DMO调用的
	 * @param _sequenceName
	 * @return
	 */
	public synchronized long getSequence(String _sequenceName) throws Exception {

		if (ht.containsKey(_sequenceName.toUpperCase())) { //如果有
			Queue queue = (Queue) ht.get(_sequenceName.toUpperCase());//
			if (queue.isEmpty()) { //如果是空的,则去远程取!!
				Long[] ll_newIds = getService().getSequenceNextValByDS(null, _sequenceName, new Integer(li_batchnumber)); //去远程取到一批数据!!!一般是10左右!!
				for (int i = 0; i < ll_newIds.length; i++) { //
					queue.push(ll_newIds[i]); //从屁股后面插入!
				}
				return ((Long) queue.pop()).longValue(); //再从头部取一个返回
			} else {
				return ((Long) queue.pop()).longValue(); //返回值
			}
		} else { //如果队列中没有，则要创建队列
			Long[] ll_newIds = getService().getSequenceNextValByDS(null, _sequenceName, new Integer(li_batchnumber)); //去远程取到一批数据!!!
			Queue queue = new Queue(); //创建一个队列
			for (int i = 0; i < ll_newIds.length; i++) {
				queue.push(ll_newIds[i]); //从屁股后面插入!
			}
			ht.put(_sequenceName.toUpperCase(), queue); //将队列存入哈希缓存!!!
			return ((Long) queue.pop()).longValue(); //再从头部取一个返回!!
		}
	}

	private FrameWorkCommServiceIfc getService() throws Exception {
		if (service == null) {
			service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
		}
		return service;
	}

}
