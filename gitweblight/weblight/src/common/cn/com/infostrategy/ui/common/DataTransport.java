package cn.com.infostrategy.ui.common;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * ����ֱ����ͬһ���ͻ���ִ��Ǩ�Ƶ�ǰ���ݵ���ͬ������Դ
 * @author chendu 
 * @since  2009-05-07
 */
public class DataTransport {
    private Container parent = null;
    
    private DataTransport(Container _parent) {
        this.parent = _parent;
    }
    
    public static DataTransport createDataTransport(Container _parent) {
        return new DataTransport(_parent);
    }
    
    public void transportXMLTempletDialog(String[] _templetCodes) throws WLTRemoteException, Exception {
        ArrayList<String> dataSources = getCurrentSystemDataSourceList();
        
        //create a panel which include 
        WLTTabbedPane message_pane = new WLTTabbedPane();
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "��ѡ��һ������Դ��", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // �����߿�
        
        //a srcDataSource choosing JCombox 
        JComboBox sourceDataSource_choose = new JComboBox(dataSources.toArray());
        JPanel srcChoosePanel = new JPanel();
        srcChoosePanel.setBorder(border);
        srcChoosePanel.add(sourceDataSource_choose);
        
        //and a destDataSource choosing JCombox;
        JComboBox destDataSource_choose = new JComboBox(dataSources.toArray());
        JPanel destChoosePanel = new JPanel();

        destChoosePanel.setBorder(border);
        destChoosePanel.add(destDataSource_choose);
        
        message_pane.addTab("����", srcChoosePanel);
        message_pane.addTab("����", destChoosePanel);
        message_pane.setPreferredSize(new Dimension(300, 400));
        
        //create a JOptionPane using this panel;
        int i_return = JOptionPane.showConfirmDialog(parent, message_pane, "ѡ������Դ", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        //when the return value is confirm
        if(i_return == JOptionPane.YES_OPTION) {
            if(message_pane.getSelectedIndex()==0){
                //import
                transportXMLTempletBetweenDataSource(
                      _templetCodes, 
                      sourceDataSource_choose.getSelectedItem().toString(), 
                      new String[]{UIUtil.getCommonService().getDeaultDataSource()}
                      );
                MessageBox.show("�ɹ�����ģ�棡");
            } else if (message_pane.getSelectedIndex()==1){
                //export
                transportXMLTempletBetweenDataSource(
                      _templetCodes, 
                      UIUtil.getCommonService().getDeaultDataSource(), 
                      new String[]{destDataSource_choose.getSelectedItem().toString()}
                      );
                MessageBox.show("�ɹ�����ģ�棡");
            } else {
                MessageBox.show("����Դѡ�����");
            }
//            //do transport between srcDataSource and destDataSourceList
//            String str_source = null;//for test, default data source
//            //sourceDataSource_choose.getSelectedItem().toString();
//            
//            transportXMLTempletBetweenDataSource(
//                    _templetCodes, 
//                    str_source, 
//                    destDataSource_choose.getSelectedValues()
//                    );
        }
    }
    /**
     * 
     * @param _templetcode
     * @param _source
     * @param _destinations
     */
    public void transportXMLTempletBetweenDataSource(
            String[] _templetcodes, 
            String _source, 
            Object[] _destinations
            ) throws WLTRemoteException, Exception {
        FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc.class);
        service.transportTempletToDataSource(_templetcodes, _source, _destinations);
    }
    
    private ArrayList<String> getCurrentSystemDataSourceList() throws WLTRemoteException, Exception {
        DataSourceVO[] dataSourceVOs = UIUtil.getCommonService().getDataSourceVOs();
        if (dataSourceVOs.length == 1) {
            MessageBox.show(parent, "��ǰû����������������Դ������weblight.xml�е�����Դ��");
        }
        ArrayList<String> dataSourceList = new ArrayList<String>();
        for (int i = 0; i < dataSourceVOs.length; i++) {
            String str_dataSourceName = dataSourceVOs[i].getName();
            if (UIUtil.getCommonService().getDeaultDataSource().equals(str_dataSourceName)) {
                //������Դ�ǵ�ǰϵͳ����Դ
                continue;
            }
            dataSourceList.add(str_dataSourceName);
        }
        return dataSourceList;      
    }
}
