package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

public class SystemFile extends AbstractWorkPanel implements TreeWillExpandListener,TreeSelectionListener{
	/**
	 * Ψһ��ʾ
	 */
	private static final long serialVersionUID = -2880884572483039769L;
	private JPanel centerPanel = null;
	private DefaultMutableTreeNode root = null;
	private DefaultMutableTreeNode node = null;
	private MutableTreeNode tempNode = null;
	private MutableTreeNode[] nodes = null;
	private JTree tree = null;
	private JScrollPane jScrollPaneForTree = null;
	private JScrollPane jScrollPaneForList = null;
	private File[] file = null;
	private TreePath treePath = null;
	private File tempFile = null;
	private JList jList = null;
	private JSplitPane jSplit = null;
	private SysAppServiceIfc systemServer = null;
	private DefaultTreeCellRenderer defaultCell = null;
	private JTable fileTable = null;
	private JLabel statu = null;

	public void initialize() {
		/**
		 * ��������ͼƬ....
		 */
		defaultCell = new DefaultTreeCellRenderer();
		ImageIcon imageIcon = new ImageIcon("D:/image/foldericon.png");
		defaultCell.setOpenIcon(imageIcon);
		defaultCell.setClosedIcon(imageIcon);
		defaultCell.setLeafIcon(imageIcon); 
		//��ʼ����
		initTreeRoot();

		this.setLayout(new BorderLayout());
		centerPanel = new JPanel(new BorderLayout());
		//��ʼ���ļ��б�
		jList = new JList();
		statu = new JLabel();
		fileTable = new JTable();
		jScrollPaneForTree = new JScrollPane();
		jScrollPaneForList = new JScrollPane();
		//��ʼ�����Լ��ļ��б�
		jScrollPaneForTree.setViewportView(tree);
		jScrollPaneForList.setViewportView(fileTable);
		//��ʼ���ָ���
		jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jScrollPaneForTree,jScrollPaneForList);
		jSplit.setDividerLocation(200);
		centerPanel.add(jSplit,BorderLayout.CENTER);
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(statu,BorderLayout.SOUTH);
	}

	/**
	 * ��ʼ�����ĸ�Ŀ¼
	 */
	private void initTreeRoot(){
		try {
			systemServer = (SysAppServiceIfc)UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			file = systemServer.getSystemFiles(null);
			if(file == null || file.length == 0){
				return ;
			}
			if(file.length > 1){
				root = new DefaultMutableTreeNode("�ҵĵ���");
				for(int i = 0;i < file.length;i++){
					node = new DefaultMutableTreeNode(UIUtil.replaceAll(file[i].getPath(), "\\", ""));
					if(systemServer.hasDirectory(file[i])){
						node.add(new DefaultMutableTreeNode(""));
					}
					root.add(node);
				}
			}else{
				root = new DefaultMutableTreeNode(file[0].getName());
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tree = new JTree(root);
		tree.collapsePath(tree.getPathForRow(0));
//		tree.addMouseListener(this);
		tree.addTreeWillExpandListener(this);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(defaultCell);
		tree.revalidate();
	}
	/**
	 * ���첢�����ӽڵ�
	 * @param myFiles
	 * @return
	 */
	private MutableTreeNode[] getNodes(File[] myFiles){
		if(myFiles == null || myFiles.length == 0){
			return null;
		}
		MutableTreeNode[] returnNodes = new DefaultMutableTreeNode[myFiles.length];
		try {
			for(int i = 0;i < myFiles.length;i++){
				returnNodes[i] = new DefaultMutableTreeNode(myFiles[i].getName());
				if(systemServer.hasDirectory(myFiles[i])){
					returnNodes[i].insert(new DefaultMutableTreeNode(""), 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return returnNodes;
	}

	/**
	 * �ж�ϵͳ����
	 * @param path
	 * @return boolean
	 */
	private boolean isWindows(String[] path){
		if(path.length > 1 && path[1].indexOf(":") > 0){
			return true;
		}
		return false;
	}
	/**
	 * �����ļ����ļ��У����û�еĻ����򷵻س���Ϊ0�����飡
	 * @param myFiles
	 * @return
	 */
	private File[] getDirectory(File[] myFiles){
		File[] returnFiles = null;
		FileTable myTable = null;
		Vector vector = new Vector();
		Object[][] input = null;
		String[] columnName = {"�ļ���","��С","����"};
		ArrayList list = new ArrayList();
		if(myFiles != null && myFiles.length != 0){
			for(int i = 0;i < myFiles.length;i++){
				if(myFiles[i].isDirectory()){
					list.add(myFiles[i]);
				}else{
						vector.add(myFiles[i]);
				}
			}
		}
		returnFiles = new File[list.size()];
		for(int i = 0;i < list.size();i++){
			returnFiles[i] = (File)list.get(i);
		}
		input = new Object[vector.size()][3];
		ImageIcon fileImage = new ImageIcon("D:/image/file.png");
		JLabel imageLabel = null;
		JLabel fileNameLabel = null;
		JPanel filePanel = null;
		for(int i = 0;i < vector.size();i++){
			input[i][0] = ((File)vector.get(i)).getName();
			input[i][1] = getFileSize((File)vector.get(i));
			input[i][2] = getFileDate((File)vector.get(i));
		}
		imageLabel = new JLabel();
		imageLabel.setIcon(fileImage);
		imageLabel.setPreferredSize(new Dimension(20,20));
		fileNameLabel = new JLabel("����",JLabel.CENTER);
		filePanel = new JPanel(new BorderLayout());
		filePanel.add(imageLabel,BorderLayout.WEST);
		filePanel.add(fileNameLabel,BorderLayout.CENTER);
		fileTable.removeAll();
		myTable = new FileTable(input,columnName);
		fileTable.setModel(myTable);
		fileTable.setShowHorizontalLines(Boolean.FALSE);
		fileTable.setShowVerticalLines(Boolean.FALSE);
		fileTable.revalidate();
		jList.removeAll();
//		jList.setListData(vector);
		jList.revalidate();
		return returnFiles;
	}
	//////////////////////////////////////��ʱ����Ҫ�ļ����¼�
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {}
	///////////////////////////////////////////////////////
	/**
	 * ��������չ���¼�....
	 */
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		if(tree.isExpanded(tree.getSelectionPath())){
			return ;
		}
		if(((MutableTreeNode)tree.getSelectionPath().getLastPathComponent()).getChildCount() > 1){
			return ;
		}
		try{
			StringBuffer filePath = new StringBuffer();
			treePath = event.getPath();
			String[] tempPath = new String[treePath.getPath().length];
			for(int i = 0;i < treePath.getPath().length;i++){
				tempPath[i] = treePath.getPath()[i].toString();
			}
			if(isWindows(tempPath)){
				for(int i = 1;i < tempPath.length;i++){
					if(i == 1){
						filePath.append(tempPath[i]);
					}else{
						filePath.append("/" + tempPath[i]);
					}
					if(tempPath.length - 1 == 1){
						filePath.append("/");
					}
				}
			}else{
				for(int i = 0;i < tempPath.length;i++){
					if(i == tempPath.length - 1){
						filePath.append(tempPath[i]);
					}else{
						filePath.append(tempPath[i] + "/");
					}
				}
			}
			tempFile = new File(filePath.toString());
			showStatu(tempFile.getPath());
			file = systemServer.getSystemFiles(tempFile);
			nodes = getNodes(getDirectory(file));
			if(nodes != null){
				tempNode = (MutableTreeNode)treePath.getLastPathComponent();
				if(!tempNode.getChildAt(0).toString().equals("")){
					return;
				}
				tempNode.remove(tempNode.getChildCount() - 1);
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				for(int i = 0;i < nodes.length;i++){
					model.insertNodeInto(nodes[i], tempNode, tempNode.getChildCount());
				}
			}
			tree.revalidate();
		} catch (WLTRemoteException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	/**
//	 * ���˫���¼�
//	 * �����ļ����ڵ������ļ��б�
//	 */
//	public void mouseClicked(MouseEvent e) {
//		if(e.getClickCount() == 2){
//				try{
//					StringBuffer filePath = new StringBuffer();
//					treePath = tree.getSelectionPath();
//					if(!((MutableTreeNode)treePath.getLastPathComponent()).isLeaf()){
//						return;
//					}
//					String[] tempPath = new String[treePath.getPath().length];
//					for(int i = 0;i < treePath.getPath().length;i++){
//						tempPath[i] = treePath.getPath()[i].toString();
//					}
//					if(isWindows(tempPath)){
//						for(int i = 1;i < tempPath.length;i++){
//							if(i == 1){
//								filePath.append(tempPath[i]);
//							}else{
//								filePath.append("/" + tempPath[i]);
//							}
//							if(tempPath.length - 1 == 1){
//								filePath.append("/");
//							}
//						}
//					}else{
//						for(int i = 0;i < tempPath.length;i++){
//							if(i == tempPath.length - 1){
//								filePath.append(tempPath[i]);
//							}else{
//								filePath.append(tempPath[i] + "/");
//							}
//						}
//					}
//					tempFile = new File(filePath.toString());
//					file = systemServer.getSystemFiles(tempFile);
//					getDirectory(file);
//					tree.revalidate();
//				} catch (WLTRemoteException ex) {
//					ex.printStackTrace();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//		}
//	}
	/**
	 * �õ�ѡ�е��ļ�
	 * @return File[]
	 */
	public File[] getSelectedFile(){
		File[] files = null;
		ArrayList list = new ArrayList();
		if(jList.getSelectedValues() != null && jList.getSelectedValues().length > 0){
			files = new File[jList.getSelectedValues().length];
			for(int i = 0;i < jList.getSelectedValues().length;i++){
				for(int j = 0;j < file.length;j++){
					if(file[i].isFile() && file[i].getName().equals(jList.getSelectedValues()[i])){
						list.add(file[i]);
					}
				}
			}
		}
		files = new File[list.size()];
		for(int i = 0;i < list.size();i++){
			files[i] = (File)list.get(i);
		}
		return files;
	}

	/**
	 * ����ѡ���¼�....
	 */
	public void valueChanged(TreeSelectionEvent e) {
		TreePath changedTreePath = null;
		try{
			StringBuffer filePath = new StringBuffer();
			changedTreePath = e.getPath();
			String[] tempPath = new String[changedTreePath.getPath().length];
			for(int i = 0;i < changedTreePath.getPath().length;i++){
				tempPath[i] = changedTreePath.getPath()[i].toString();
			}
			if(isWindows(tempPath)){
				for(int i = 1;i < tempPath.length;i++){
					if(i == 1){
						filePath.append(tempPath[i]);
					}else{
						filePath.append("/" + tempPath[i]);
					}
					if(tempPath.length - 1 == 1){
						filePath.append("/");
					}
				}
			}else{
				for(int i = 0;i < tempPath.length;i++){
					if(i == tempPath.length - 1){
						filePath.append(tempPath[i]);
					}else{
						filePath.append(tempPath[i] + "/");
					}
				}
			}
			tempFile = new File(filePath.toString());
			showStatu(tempFile.getPath());
			file = systemServer.getSystemFiles(tempFile);
			getDirectory(file);
			tree.revalidate();
		} catch (WLTRemoteException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * �����ʾ�ļ��Ĵ�С
	 * @param file
	 * @return String
	 */
	private String getFileSize(File file){
		if(file == null){
			return "0b";
		}
		long size = file.length();
		if(size/1024 == 0){
			return size+"B";
		}else if(size/1024 >= 1024){
			return (size/1024)/1024+"M";
		}else{
			return size/1024+"K";
		}
	}
	
	/**
	 * �����ʾ�ļ�������
	 * @param file
	 * @return String
	 */
	private String getFileDate(File file){
		if(file == null){
			return null;
		}
		Date fileDate = new Date(file.lastModified());
		String[] time = fileDate.toLocaleString().split(" ");
		return time[0];
	}
	
	private void showStatu(String message){
		if(message == null){
			message = "";
		}
		statu.setText("��ǰ�鿴Ŀ¼:"+message);
		statu.setHorizontalAlignment(JLabel.LEFT);
		statu.repaint();
	}
}
