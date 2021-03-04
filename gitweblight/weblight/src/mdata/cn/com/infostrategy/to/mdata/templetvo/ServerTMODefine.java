package cn.com.infostrategy.to.mdata.templetvo;

import java.io.Serializable;

/**
 * ��ʱ��Ҫ������TMOֱ�Ӵ����ؼ�,�����Щ�඼���ڿͻ��ˣ��ᵼ�����ص�Class�ǳ���,����������˿ͻ������ص���Ҫ����.
 * ��ʱ�Ҿ�ϣ���ڷ������˶�����Щ����,Ȼ��ÿ�ζ�ȥ��������ȡ������壬ͬʱ�����ڷ�����Ϊ֮����Pub_Templet_1VO������ֻҪ��һ���������������͹��ˣ��������ȷ��䴴��TMO����Ȼ�󴴽�Pub_Templet_1VO.
 * �����ͽ�ServerTMO,��ֻ��һ������������Ƿ�������TMO�����ȫ��,��ΪBillList�ȿؼ���֧��ֱ��ͨ�������ַ������ʹ���ʵ����Ϊ�����֣����봴��һ����������Ǳ����ٸ�������ԭ��
 * ����ڿͻ���ֱ����TMO��������������Pub_Templet_1VO��һ�����͵�����������,��������ʡ��������һ��Զ�̵��õģ���������ֱ�����ڷ����������������ܸ�!! 
 * ��������Ҳ��һ��ȱ�㣬�������ַ�������,������ع�����·��ʱ�����Զ�����.
 * ������Щ��ͳͳ���ڷ������� bs.servertmo. ���������...
 * @author xch
 *
 */
public class ServerTMODefine implements Serializable {
	private static final long serialVersionUID = 6082587737116495177L;
	private String tmoClassName = null; //����
	private String[] constructorPars = null; //�������,��������Ϊ������һ��TMO,�����ж���������!!����:TMO_BOM_B

	/**
	 * ��ֹĬ�Ϲ��췽��
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
