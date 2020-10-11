package cn.com.infostrategy.bs.sysapp.transferdb;

/**
 * ת����������,ͨ��������������ת��������
 * @author xch
 *
 */
public class ConvertFactory {

	private static ConvertFactory factory = new ConvertFactory(); //

	private ConvertFactory() {
	}

	public static ConvertFactory getInstance() {
		return factory; //
	}

	public ConvertAdapterIfc createConvertAdapter(String _sourceDBType, String _destDBType) {
		try {
			ConvertAdapterIfc convert = (ConvertAdapterIfc) Class.forName("cn.com.infostrategy.bs.sysapp.transferdb." + _sourceDBType + "To" + _destDBType + "Converter").newInstance();
			return convert;
		} catch (Exception e) {
			e.printStackTrace(); //
			return null;
		}
	}
}
