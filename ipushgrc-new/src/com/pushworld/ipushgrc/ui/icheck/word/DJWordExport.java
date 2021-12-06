package com.pushworld.ipushgrc.ui.icheck.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
/**
 * 
 * @author zzl
 *  word ����
 */
public class DJWordExport {

	 private Configuration configure = null;
     public DJWordExport(){
            configure= new Configuration();
            configure.setDefaultEncoding("utf-8");
     }
     /**
      * ����Docģ������word�ļ�
      * @param dataMap Map ��Ҫ����ģ�������
      * @param fileName �ļ�����
      * @param savePath ����·��
      */
     public void createDoc(Map<Object, Object> dataMap, String downloadType, String savePath){
            try{
                   Template template  = null;
                   //����ģ���ļ�
//                   String dirpath = System.getProperty("user.dir");
                   String path=this.getClass().getResource("/").toString();
                   path=path.replace("%20"," ");   
                   path=path.substring(6,path.length()-1);
                   path = path + "/WebRoot/applet";
                   System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+path);
                   configure.setDirectoryForTemplateLoading(new File(path));
                   //���ö����װ��
                   configure.setObjectWrapper(new DefaultObjectWrapper());
                   //�����쳣������
                   configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                   //����Template����,ע��ģ������������downloadTypeҪһ��
                   template= configure.getTemplate(downloadType+".ftl");
                   //����ĵ�
                   File outFile = new File(savePath);
                   Writer out = null;
                   out= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"utf-8"));                                   
                   template.process(dataMap,out);
                   outFile.delete();
                   out.close();
                   System.out.println(">>>>>>>>>>>>>>"+"�����ɹ�");
            }catch (Exception e) {
                   e.printStackTrace();
                   System.out.println("<<<<<<<<<<<<<<<<"+"����ʧ��");
            }finally{
            }
     }

	}  
