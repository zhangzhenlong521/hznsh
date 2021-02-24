/**************************************************************************
 * $RCSfile: AbstractListCustomerButtonBarPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;


import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * �ͻ��Զ��尴ť����,����һ���ط��������û����Լ���һЩ��ť�������ؼ�,һ�㶼�ǰ�ťΪ��!!���Խа�ť����
 * 
 * ���еķ���ж���һ�����ע������һ������,Ȼ�������з��ģ�����������ע���˸���,��ô�ʹ�������ʵ��,
 * Ȼ�����setParentFrame()���������ģ���౾����������,Ȼ����ó��󷽷�initialize();��ʼ������!!
 * 
 * ��initialize()�����пͻ��������ɷ����������Լ���ʲô�ؼ�,���簴ť,�����,Ȼ���������Լ����¼�����,
 * ��ΪֻҪ���ܹ�ͨ��getParentFrame()�����õ�������,��ͨ��������Ӷ��õ�ҳ�����κ�һ���ؼ����,�����ܹ���һ���������ɵ�!!!
 * @author user
 *
 */
public abstract class AbstractListCustomerButtonBarPanel extends JPanel {

	private BillListPanel billListPanel = null;

	public AbstractListCustomerButtonBarPanel() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT)); //Ĭ����ˮƽ����,��Ȼ��������initialize()�н����ֻ���!!
	}

	/**
	 * ��ʼ��ҳ��
	 *
	 */
	public abstract void initialize();

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListPanel billListPanel) {
		this.billListPanel = billListPanel;
	}

}
/**************************************************************************
 * $RCSfile: AbstractListCustomerButtonBarPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: AbstractListCustomerButtonBarPanel.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:45  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:12  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:55  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:39  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:29  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:39  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:07  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:56:14  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/