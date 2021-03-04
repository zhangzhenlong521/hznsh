package cn.com.infostrategy.ui.common;

import java.io.Serializable;
import java.util.Hashtable;

import cn.com.infostrategy.to.common.Queue;

public class ClientSequenceFactory implements Serializable {

	private static final long serialVersionUID = -4971518506217700714L;

	private static ClientSequenceFactory factory = new ClientSequenceFactory();

	private static Hashtable ht = new Hashtable();

	private FrameWorkCommServiceIfc service = null;

	private int li_batchnumber = 10; //һ�δ�Զ��ȡ����!��10��

	private ClientSequenceFactory() {
	}

	public final static ClientSequenceFactory getInstance() {
		return factory;
	}

	/**
	 * ȡ��һ�����е�ֵ!!�ͻ���ֻ����һ���������������������һ����Ϊ�ͻ��˵��õģ�һ����Ϊ����������DMO���õ�
	 * @param _sequenceName
	 * @return
	 */
	public synchronized long getSequence(String _sequenceName) throws Exception {

		if (ht.containsKey(_sequenceName.toUpperCase())) { //�����
			Queue queue = (Queue) ht.get(_sequenceName.toUpperCase());//
			if (queue.isEmpty()) { //����ǿյ�,��ȥԶ��ȡ!!
				Long[] ll_newIds = getService().getSequenceNextValByDS(null, _sequenceName, new Integer(li_batchnumber)); //ȥԶ��ȡ��һ������!!!һ����10����!!
				for (int i = 0; i < ll_newIds.length; i++) { //
					queue.push(ll_newIds[i]); //��ƨ�ɺ������!
				}
				return ((Long) queue.pop()).longValue(); //�ٴ�ͷ��ȡһ������
			} else {
				return ((Long) queue.pop()).longValue(); //����ֵ
			}
		} else { //���������û�У���Ҫ��������
			Long[] ll_newIds = getService().getSequenceNextValByDS(null, _sequenceName, new Integer(li_batchnumber)); //ȥԶ��ȡ��һ������!!!
			Queue queue = new Queue(); //����һ������
			for (int i = 0; i < ll_newIds.length; i++) {
				queue.push(ll_newIds[i]); //��ƨ�ɺ������!
			}
			ht.put(_sequenceName.toUpperCase(), queue); //�����д����ϣ����!!!
			return ((Long) queue.pop()).longValue(); //�ٴ�ͷ��ȡһ������!!
		}
	}

	private FrameWorkCommServiceIfc getService() throws Exception {
		if (service == null) {
			service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
		}
		return service;
	}

}
