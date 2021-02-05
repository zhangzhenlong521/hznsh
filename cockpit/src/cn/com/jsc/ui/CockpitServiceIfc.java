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
	/**
	 * zzl �ͻ�����Ӫ���������
	 */
	public  String [] [] getCKRanking();
	/**
	 * zzl ȫ�д������Ŀ��������
	 */
    public String [] [] getQhCkCompletion();
	/**
	 * zzl ȫ�д������Ŀ��������
	 */
	public String [] [] getQhCkCompletion2();
	/**
	 * zzl ��֧�д��������
	 */
	public String [] [] getgzhWcqingkuang();
	/**
	 * zzl ȫ�д���������
	 *
	 */
	public String [] [] getqhDkWcCount();
	/**
	 * zzl ȫ�д���������
	 *
	 */
	public String [] [] getDKgzhWcqingkuang();
	/**
	 * zzl ȫ��ũ���������
	 *
	 */
	public String [] [] getNhFgMian();
	/**
	 * zzl ���������ͳ��
	 * @return
	 */
	public String [] [] getDkStatistical();
	/**
	 * zzl �����ܻ���ͳ��
	 * @return
	 */
	public String [] [] getDkStatisticalHs();
	/**
	 * zzl �������������ͳ��
	 * @return
	 */
	public String [] [] getBmDkStatisticalHs();
	/**
	 * zzl ������幤�̻���ͳ��
	 * @return
	 */
	public String [] [] getGtDkStatisticalHs();
	/**
	 * zzl �¶���ͳ�� ������������
	 * @return
	 */
	public String [] [] getXdlDkStatisticalHs();
	/**
	 * zzl ũ������
	 * @return
	 */
	public String [] [] getnhDkStatisticalHs();
	/**
	 * zzl �ͻ�����Ӫ����������
	 */
	public  String [] [] getDKRanking();
	/**
	 * zzl 60���������ܶ�
	 */
	public  String [] [] getBlDKCount();
	/**
	 * zzl 90���������ܶ�
	 */
	public  String [] [] getBlDKCount2();
	/**
	 * zzl ���������ܶ�
	 */
	public  String [] [] getBlDKLvCount();
	/**
	 * zzl �ջز�������
	 */
	public  String [] [] getShBlDKLvCount();
	/**
	 * zzl ����������������
	 */
	public  String [] [] getxzBlDKLvCount();
	/**
	 * zzl �����ջر��ⲻ������
	 */
	public  String [] [] getShBwBlDKLvCount();
	/**
	 * zzl �����������ⲻ������
	 */
	public  String [] [] getxzBwBlDKLvCount();
	/**
	 * zzl ���¿ͻ��������ղ�����������
	 */
	public  String [] [] getqsBlDKLvCount();
	/**
	 * zzl ȫ�в����������
	 */
	public  String [] [] getqhBlDKLvCount();
	/**
	 * zzl ��֧�в�������������
	 */
	public  String [] [] getgzhBlDKLvCount();
	/**
	 * zzl Ӫ��ǭũ������
	 */
	public  String [] [] getQnyDKLvCount();
	/**
	 * zzl ǭũE������
	 */
	public  String [] [] getQnyEDKLvCount();
}
