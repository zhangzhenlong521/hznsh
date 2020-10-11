package cn.com.infostrategy.ui.sysapp.other;

import cn.com.infostrategy.bs.common.RemoteCallServlet;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.com.infostrategy.to.common.TBUtil.getTBUtil;


/**
 * zzl ���ı���ϸ�ϴ�
 * Ҳ���Է�װ��ͨ�õĴ��ı��ϴ���ʽ
 *
 */
public class BigFileUpload extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
    WLTButton excel_btn = new WLTButton("���ݵ���");
    //zzl tablename=���� fileType=�ļ���ʽ
    private String tableName,fileType;
    private String selectDate = "";
    private String colLength = "";//�е�ֵ
    private String strFg="";//�ı��ָ�����
    private String xcCount="";//�߳���
    private String xxtype="";//�������
    private ExecutorService executor;
    private int countjv=0;
    private Logger logger = WLTLogger.getLogger(RemoteCallServlet.class);
    public void initialize() {
        this.setLayout(new FlowLayout(0));
        this.excel_btn.setPreferredSize(new Dimension(100, 50));
        this.excel_btn.addActionListener(this);
        this.add(this.excel_btn);
    }

    @Override
    public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==this.excel_btn) {
            if (fileUpload() == 1) {
                fileChoose();
            }
        }
    }
    public int fileUpload() {
        try {
            BillCardDialog cardDialog = new BillCardDialog(this, "�����ϴ�����","WN_BIGFILE_CODE1",600,400);
            cardDialog.setSaveBtnVisiable(false);
            cardDialog.getBillcardPanel().setEditable("tableName",true);
            cardDialog.getBillcardPanel().setRealValueAt("fileType", "csv");
            cardDialog.getBillcardPanel().setRealValueAt("colLength", "200");
            cardDialog.getBillcardPanel().setRealValueAt("strFg", "&");
            cardDialog.getBillcardPanel().setRealValueAt("xcCount", "8");
            cardDialog.getBillcardPanel().setEditable("fileType",false);
            cardDialog.getBillcardPanel().setEditable("colLength",true);
            cardDialog.getBillcardPanel().setEditable("strFg",false);
            cardDialog.getBillcardPanel().setEditable("fileType",true);
            cardDialog.getBillcardPanel().setRealValueAt("xxtype", "ȫ��");
            cardDialog.setVisible(true);
            if (cardDialog.getCloseType() == 1) {
                tableName = cardDialog.getBillcardPanel().getRealValueAt("tableName");
                tableName = UIUtil.getStringValueByDS(null, "select code from PUB_COMBOBOXDICT where 1=1  and (id='"+tableName+"' and type='н��_���ļ������ϴ�')  ");
                fileType = cardDialog.getBillcardPanel().getRealValueAt("fileType");
                colLength = cardDialog.getBillcardPanel().getRealValueAt("colLength");
                strFg = cardDialog.getBillcardPanel().getRealValueAt("strFg");
                xcCount = cardDialog.getBillcardPanel().getRealValueAt("xcCount");
                xxtype = cardDialog.getBillcardPanel().getRealValueAt("xxtype");
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void fileChoose(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("��ѡ��һ��"+fileType+"�ļ�");
        chooser.setApproveButtonText("ѡ��");

        FileFilter filter = new FileNameExtensionFilter(""+fileType+"������", fileType);
        chooser.setFileFilter(filter);
        int flag = chooser.showOpenDialog(this);
        if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return;
        }
        final String str_path = chooser.getSelectedFile().getAbsolutePath();

        if (!(str_path.toLowerCase().endsWith("."+fileType) || str_path.toLowerCase().endsWith("."+fileType))) {
            MessageBox.show(this, "��ѡ��һ��"+fileType+"�ļ�!");
            return;
        }
        long startTime = System.currentTimeMillis(); // ��ȡ��ʼʱ��
        try{
            String [] datas=getDate(this);
            selectDate=datas[0].toString();
            if(datas.toString().equals("1") || Integer.parseInt(datas[1].toString())==1){
                new SplashWindow(this, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        bigFileRead(str_path,selectDate);
                    }
                });
            }else{
                return;
            }

        }catch (Exception e){

        }
        long endTime = System.currentTimeMillis(); // ��ȡ����ʱ��
        System.out.println("��������ʱ�䣺 " + (endTime - startTime) + "ms");
    }
    public void bigFileRead(String str_path,String data){
        try{
            File file = new File(str_path);
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// ��5M�Ļ����ȡ�ı��ļ�
            String line;
            int counter = 0;
            final List<String> list=new ArrayList<String>();
            final BIgFileUploadIfc service = (BIgFileUploadIfc) UIUtil
                    .lookUpRemoteService(BIgFileUploadIfc.class);
            String[] colnum=null;
            String newtableName=tableName+"_"+data.replace("-","");
            if(xxtype.equals("ȫ��")){
                HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from dba_tables where TABLE_NAME='"+newtableName.toUpperCase()+"' and TABLESPACE_NAME='"+ ClientEnvironment.getInstance().getDataSourceVOs()[0].getUser().toUpperCase()+"'");
                if(vos.length>0){
                    int count=MessageBox.showConfirmDialog(this,"����"+data+"�������Ѵ��ڣ����ȷ�����ǣ�����ر�ȡ��");
                    if(count==0){
                        UIUtil.executeBatchByDS(null,new String [] {"drop table "+newtableName});
                    }else{
                        return;
                    }
                }
            }
            executor = Executors.newFixedThreadPool(Integer.parseInt(xcCount));
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                if (counter == 0) {
                    colnum = line.split(strFg);
                    if(xxtype.equals("ȫ��")){
                        String sql=creatTable(colnum,newtableName);
                        list.add(sql);
                        countjv=service.upLoad(list);
                        if(countjv==0){
                            MessageBox.show(this,"����ʧ��");
                            return;
                        }
                        list.clear();
                    }
                }else{
                    if(xxtype.equals("ȫ��")){
                        list.add(insertSql(colnum,line,newtableName));
                    }else{
                        String xj=UIUtil.getStringValueByDS(null,"select count(*) from user_tab_columns where table_name=upper('"+newtableName+"')");
                        String [] xjstr=line.split(strFg);
                        if(Integer.parseInt(xj)==xjstr.length){
                            list.add(insertSql(colnum,line,newtableName));
                        }else{
                            MessageBox.show(this,"�����ļ������������ݿ��е�������һ,��˶�");
                            return;
                        }
                    }
                }
                if(list.size()==2000){
                    final List<String> newList=new ArrayList<String>();
                    newList.addAll(list);
                    list.clear();
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            countjv=service.upLoad(newList);
                            newList.clear();
                        }
                    });
                }
                counter++;
            }

        if(list.size()>0){
            final List<String> newList=new ArrayList<String>();
            newList.addAll(list);
            list.clear();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    countjv=service.upLoad(newList);
                    newList.clear();
                }
            });
        }
            executor.shutdown();
            while (true) {
                if (executor.isTerminated() && countjv>0) {
                    if(xxtype.equals("ȫ��")){
                        String count=UIUtil.getStringValueByDS(null,"select count(*) from "+newtableName+"");
                        promptHyperlinks(this,"�ɹ������ˡ�"+count+"������������뵼���ļ������ݲ�һ,��Ҫ�滻�����Ļ��Ļ��з����ο�:","https://jingyan.baidu.com/article/c85b7a64a1a773003bac9508.html");
                    }else{
                        promptHyperlinks(this,"�ɹ������ˡ�"+(counter-1)+"������������뵼���ļ������ݲ�һ,��Ҫ�滻�����Ļ��Ļ��з����ο�:","https://jingyan.baidu.com/article/c85b7a64a1a773003bac9508.html");
                    }
                    reader.close();
                    MessageBox.show(this,"����ɹ�");
                    break;
                }
                Thread.sleep(200);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * �������
     * @param strs
     * @param tablename
     * @return
     */
    private String creatTable(String[] strs,String tablename) {
        StringBuffer sb = new StringBuffer();
        String dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType();
        sb.append("create table " + tablename + " (");
        for (int i = 0; i < strs.length; i++) {
            String l = "" + getColumnName(i);
            if ("Oracle".equalsIgnoreCase(dbtype)) {
                if ((i + 1) == strs.length) {
                    if (l.equals("AT") || l.equals("AS") || l.equals("BY") || l.equals("OF") || l.equals("ON")) {
                        sb.append("\"" + l + "\"" + " varchar(" + Integer.parseInt(colLength) + ") ");
                    } else {
                        sb.append(l + " varchar(" + Integer.parseInt(colLength) + ") ");
                    }
                } else {
                    if (l.equals("AT") || l.equals("AS") || l.equals("BY") || l.equals("OF") || l.equals("ON")) {
                        sb.append("\"" + l + "\"" + " varchar(" + Integer.parseInt(colLength) + "), ");
                    } else {
                        sb.append(l + " varchar(" + Integer.parseInt(colLength) + "), ");
                    }
                }
            }else{
                if ((i + 1) == strs.length) {
                    sb.append(l + " varchar(" + Integer.parseInt(colLength) + ") ");
                }else{
                    sb.append(l + " varchar(" + Integer.parseInt(colLength) + "), ");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     *   //�õ����е��в���ת����A B C.......
     * @param index
     * @return
     */
      private String getColumnName(int index) {
        String colCode = "";
        char key = 'A';
        int loop = index / 26;
        if (loop > 0) {
            colCode += getColumnName(loop - 1);
        }
        key = (char) (key + index % 26);
        colCode += key;
        return colCode;
    }

    /**
     * ʱ��
     * @param _parent
     * @return
     */
    private String [] getDate(Container _parent) {
        String [] str=null;
        int a=0;
        try {
            RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "��ѡ���ϴ����ݵ��·�", new RefItemVO(selectDate, "", selectDate), null);
            chooseMonth.initialize();
            chooseMonth.setVisible(true);
            selectDate = chooseMonth.getReturnRefItemVO().getName();
            a=chooseMonth.getCloseType();
            str=new String[]{selectDate,String.valueOf(a)};
            return str;
        } catch (Exception e) {
            WLTLogger.getLogger(BigFileUpload.class).error(e);
        }
        return new String[]{"2013-08",String.valueOf(a)};
    }



    /**
     * ��װinsertSQL���
     * @param tablename
     * @param colnum
     * @param str
     */
    private String insertSql(String [] colnum,String str,String tablename){
        InsertSQLBuilder insert=new InsertSQLBuilder(tablename);
        String [] strValue=str.split(strFg);
        try{
            for(int i=0;i<strValue.length;i++){
                if(getColumnName(i).equals("AT") || getColumnName(i).equals("AS") || getColumnName(i).equals("BY") || getColumnName(i).equals("OF") || getColumnName(i).equals("ON")){
                    insert.putFieldValue("\""+getColumnName(i).toString()+"\"",strValue[i].toString());
                }else{
                    insert.putFieldValue(getColumnName(i).toString(),strValue[i].toString());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return insert.getSQL();
    }

    /**
     * zzl
     * ��ʾ������
     */
    private void promptHyperlinks(Container _parent, String str, final String url){
        BillDialog billDialog=new BillDialog(_parent);
        JLabel website = new JLabel();
        website.setText("<html> "+str+":<a href=''>https://jingyan.baidu.com/article/c85b7a64a1a773003bac9508.html</a></html>");
        website.setCursor(new Cursor(Cursor.HAND_CURSOR));
        website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                }
            }
        });
        billDialog.add(website);
        billDialog.setTitle("��ʾ");
        billDialog.setSize(getTextWidth(str),getTextHeight(str));
        billDialog.setLocationRelativeTo(null);//���������ʾ
        billDialog.setVisible(true);
    }

    /**
     * ȡ�ÿ��
     * @return
     */
    private int getTextWidth(String str_content) {
        if (str_content == null) {
            return 275;
        }
        String[] str_rowtexts = str_content.split("\n"); //
        int li_maxlength = 25; //
        for (int i = 0; i < str_rowtexts.length; i++) {
            int li_words = getTBUtil().getStrWidth(LookAndFeel.font, str_rowtexts[i]) + 70; //
            //System.out.println("����["+li_words+"]");  //
            if (li_words > li_maxlength) {
                li_maxlength = li_words; //�ҳ����һ���ֵĸ���
            }
        }

        int li_width = li_maxlength * 1; //

        if (li_width < 275) {
            li_width = 275;
        }

        if (li_width > 900) {
            li_width = 900;
        }
        return li_width;
    }

    /**
     * ȡ�ø߶�
     * @return
     */
    private int getTextHeight(String str_content) {
        if (str_content == null) {
            return 120;
        }

        int li_width = getTextWidth(str_content ); //
        String[] str_rowtexts = str_content.split("\n"); //
        int li_rows = 0;
        for (int i = 0; i < str_rowtexts.length; i++) {
            int li_words = getTBUtil().getStrWidth(LookAndFeel.font, str_rowtexts[i]); //
            li_rows = li_rows + ((li_words / li_width) + 1); //
        }
        int li_height = li_rows * 22 + 70; //

        if (li_height < 120) {
            li_height = 120;
        }

        if (li_height > 600) {
            li_height = 600;
        }
        return li_height; //
    }
}
