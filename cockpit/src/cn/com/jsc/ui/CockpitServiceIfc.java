package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

/**
 * zzl
 * ���ݿ��ӻ��ӿ�
 */
public interface CockpitServiceIfc extends WLTRemoteCallServiceIfc {
    /**
     * zzl ��������ͳ��
     * @return
     */
    public String [] [] getCkStatistical();
    /**\
     * zzl ����ͳ��û�жԹ�
     *
     */
    public  String [] [] getCKHsCount();

    /**
     * zzl ���˻��ڴ���������
     * @return
     */
    public  String [] [] getCKGeRenCount();

    /**
     * zzl ���˶��ڴ���������
     * @return
     */
    public  String [] [] getCKGeRenDQCount();

    /**
     * zzl �Թ����ڴ��
     * @return
     */
    public  String [] [] getCKDgHqCount();

    /**
     * zzl �Թ����ڴ��
     * @return
     */
    public  String [] [] getCKDgDqCount();
    /**
     * zpy ��������ǭũ�ƻ���
     * @return
     */
	public int getCurYearQnyhs();
	/**
	 * zpy ���㱾��ǭũ�ƻ�Ծ��
	 * @return
	 */
	public String getCurrYearQnyhyl();
	/**
	 * zpy  ����ǭũe��ǩԼ
	 * @return
	 */
	public int getCurrYearQned();
	/**
	 * zpy ����ǭũE������ռ��
	 * @return
	 */
	public String getCurrYearQnedXszb();

}
