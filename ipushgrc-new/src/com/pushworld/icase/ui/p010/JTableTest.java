package com.pushworld.icase.ui.p010;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class JTableTest extends JFrame  
{  
  
    public JTableTest()  
    {  
        intiComponent();  
    }  
  
    /** 
     * ��ʼ��������� 
     */  
    private void intiComponent()  
    {  
        /* 
         * ����JTable������ 
         */  
        String[] columnNames =  
        { "����", "ѧ��", "�Ա�", "����", "ѧԺ", "ѧУ" };  
  
        /* 
         * ��ʼ��JTable��������ֵ����������һģһ����ʵ��"�Կ���"ѧ���� 
         */  
        Object[][] obj = new Object[2][6];  
        for (int i = 0; i < 2; i++)  
        {  
            for (int j = 0; j < 6; j++)  
            {  
                switch (j)  
                {  
                case 0:  
                    obj[i][j] = "�Կ���";  
                    break;  
                case 1:  
                    obj[i][j] = "123215";  
                    break;  
                case 2:  
                    obj[i][j] = "��";  
                    break;  
                case 3:  
                    obj[i][j] = "����";  
                    break;  
                case 4:  
                    obj[i][j] = "���������ѧԺ";  
                    break;  
                case 5:  
                    obj[i][j] = "��������ѧ";  
                    break;  
                }  
            }  
        }  
          
          
        /* 
         * JTable������һ�ֹ��췽�� 
         */  
        JTable table = new JTable(obj, columnNames);  
        /* 
         * ����JTable����Ĭ�ϵĿ�Ⱥ͸߶� 
         */  
        TableColumn column = null;  
        int colunms = table.getColumnCount();  
        for(int i = 0; i < colunms; i++)  
        {  
            column = table.getColumnModel().getColumn(i);  
            /*��ÿһ�е�Ĭ�Ͽ������Ϊ100*/  
            column.setPreferredWidth(100);  
        }  
        /* 
         * ����JTable�Զ������б��״̬���˴�����Ϊ�ر� 
         */  
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  
          
        /*��JScrollPaneװ��JTable������������Χ���оͿ���ͨ�����������鿴*/  
        JScrollPane scroll = new JScrollPane(table);  
        scroll.setSize(300, 200);  
          
          
        add(scroll);  
        this.setVisible(true);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.pack();  
    }  
  
    public static void main(String[] args)  
    {  
        new JTableTest();  
    }  
}  
