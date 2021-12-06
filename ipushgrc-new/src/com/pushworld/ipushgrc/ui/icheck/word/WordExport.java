package com.pushworld.ipushgrc.ui.icheck.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import cn.com.infostrategy.to.common.TBUtil;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
/**
 * 
 * @author zzl
 *  word ����
 */
public class WordExport {
	private String filePath=null;
	private String fileName=null;

	 private Configuration configure = null;
     public WordExport(){
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
    	 this.filePath=savePath;
    	 this.fileName=downloadType;
            try{
            	String path=downloadFile();
                   Template template  = null;
                   //����ģ���ļ�
//                   String dirpath = System.getProperty("user.dir");
//                   String path=this.getClass().getResource("/").toString();                   
//                   path=path.replace("%20"," ");
//                   path=path.substring(6,path.length()-1);
//                   path = path + "/WebRoot/applet";
                   System.out.println(">>>>>>>>>>>>����word·��>>>>>>>>>>>>>"+path);
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
     private String downloadFile(){
    	 String strurl=null;
    	 try{
        String osUser=System.getProperty("user.name");  
        String UserHome=System.getProperty("user.home");  
//      System.out.println(">>>>>>>>>>>>>>"+System.getProperty("PUSH.WEBPORT"));
        System.out.println(">>>>>>>>>>>�û���>>>>>>>>>>>..."+UserHome);
//      System.out.println(System.getProperties());
//      System.out.println(">>>>>>>>>>>�û���>>>>>>>>>>>..."+osUser);
     	TBUtil tb=new TBUtil();
    	String theURL=tb.getSysOptionStringValue("������wordģ��·��",null);
    	StringBuilder sb=new StringBuilder();
    	sb.append("http://");
    	sb.append(theURL);
    	sb.append("/"+fileName+".ftl");
    	theURL=sb.toString();
    	System.out.println("!!!!!!!!������·��!!!!!!!!!"+theURL);
    	URL url = new URL(theURL);  
         File dirFile = new File(UserHome+"/word");
	        if(!dirFile.exists()){//�ļ�·��������ʱ���Զ�����Ŀ¼
	          dirFile.mkdir();
	        }
	      URLConnection  connection = url.openConnection();
	      InputStream in = connection.getInputStream();  
	      FileOutputStream os = new FileOutputStream(dirFile+"/"+fileName+".ftl"); 
	      byte[] buffer = new byte[4 * 1024];  
	      int read;  
	      while ((read = in.read(buffer)) > 0) {  
	          os.write(buffer, 0, read);  
	           }  
	        os.close();  
	        strurl=dirFile.getPath();
    	 }catch(Exception  e){
    		 e.printStackTrace();
    	 }
		return strurl;
	   }   

	}  
