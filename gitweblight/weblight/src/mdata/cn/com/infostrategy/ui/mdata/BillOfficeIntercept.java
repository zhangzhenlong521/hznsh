package cn.com.infostrategy.ui.mdata;

/**
 * ��Щ������Ҫ�ڵ����ĳ��ť��ִ��һЩ�߼�
 * ��ֻ�Ӵ�ӡ��ť
 * @author Administrator
 *
 */
public abstract class BillOfficeIntercept {
	/**
	 * ��������
	 */
	public abstract void afterSave(BillOfficePanel bf);
	/**
	 * ��ӡ�����
	 */
	public abstract void print(BillOfficePanel bf);

	/**
	 * ȫ������
	 */
	public abstract void printAll(BillOfficePanel bf);

	/**
	 * �״�����
	 */
	public abstract void printTao(BillOfficePanel bf);

	/**
	 * �ִ�����
	 */
	public abstract void printFen(BillOfficePanel bf);
	
	/**
	 * �ĵ��򿪺�
	 * @param bf
	 */
	public abstract void afterDocumenComplet(BillOfficePanel bf);

}
