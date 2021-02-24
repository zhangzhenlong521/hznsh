package com.pushworld.ipushgrc.ui.risk.p120;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Random;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.UIUtil;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * 风险3D统计面板!!
 * @author xch
 *
 */
public class Risk3DPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;

	private SimpleUniverse universe = null; //

	/**
	 * 构造方法!!
	 */
	public Risk3DPanel() {
		//创建一个场景,并加入一个画布! Create a simple scene and attach it to the virtual universe
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()); //创建画布!!

		universe = new SimpleUniverse(canvas); //创建场景,universe.removeAllLocales();可以移去所有内容

		//设置可放大缩小,旋转等!! Add some view related things to view branch side of scene graph,add mouse interaction to the ViewingPlatform
		OrbitBehavior orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL | OrbitBehavior.STOP_ZOOM);
		orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform(); //This will move the ViewPlatform back a bit so the objects in the scene can be viewed.
		viewingPlatform.setViewPlatformBehavior(orbit);

		universe.addBranchGraph(get3DContentGraph()); //实际上的构造内容都在这!!!
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
	}

	/**
	 * 实际内容
	 */
	private BranchGroup get3DContentGraph() {
		//创建两个基变量!!
		Transform3D rotate = new Transform3D(); //旋转的!
		Transform3D translate = new Transform3D(); //关键思想,一个移位置总是对应于另一个位置而言的! 

		BranchGroup objRoot = new BranchGroup();
		rotate.rotX(Math.toRadians(90.0)); //设置旋转,转多少度

		String[] str_riskLevel = new String[] { "极大风险", "高风险", "中等风险", "低风险", "极小风险" }; //
		String[] str_controlLevel = new String[] { "控制不足", "控制过度", "控制基本有效", "控制有效" }; //

		//赋数据!!这里应该从数据库取!!! 先取出风险点总数,然后分别取出【极大风险-控制过度】所点百分比,比如45%转换成4.5,如果是0的,则变成0.1
		Random random = new Random(); //
		float[] lf_heights = new float[] { 0.2f, 0.3f, 0.3f, 0.6f, 0.7f, 0.9f, 1.2f }; //
		String isDemoSystem = new TBUtil().getSysOptionStringValue("是否为演示系统", "弃权");
		//参数值可以为[强制启动]:强制设置本系统为演示系统，功能最全面，如一图两表界面的【相关罚则】、【相关案苑】和【检查要点】按钮肯定会显示出来;
		//参数值为[强制禁用]:强制系统中某些功能不显示，如上面说到的三个按钮;
		//参数值为[弃权]:表示该参数不干预本系统功能的显示;
		//默认为[弃权]！
		boolean isRandomData = false;////指定是否是随机取数!! 即在演示时为了效果很好，使用随机数据,图表更漂亮!!但在实际运行时要去掉!
		if ("强制启动".equals(isDemoSystem)) {
			isRandomData = true;
		}

		String[][] counts = null;
		int allcount = 0;
		if (!isRandomData) {
			try {
				counts = UIUtil.getStringArrayByDS(null, "select risk_rank,ctrlfneffect ,count(risk_id) from v_risk_process_file where filestate='3' and ctrlfneffect is not null group by risk_rank,ctrlfneffect");
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int m = 0; m < counts.length; m++) {
				allcount += Float.parseFloat(counts[m][2]);
			}
		}
		HashMap dataMap = new HashMap(); //
		boolean iffind = false;
		for (int i = 0; i < str_riskLevel.length; i++) {
			for (int j = 0; j < str_controlLevel.length; j++) {
				if (isRandomData) { //如果是随机取数,为了演示时使用!!!
					dataMap.put(str_riskLevel[i] + "-" + str_controlLevel[j], lf_heights[random.nextInt(lf_heights.length)]);
				} else {
					for (int m = 0; m < counts.length; m++) {
						if (str_riskLevel[i].equals(counts[m][0]) && str_controlLevel[j].equals(counts[m][1])) {
							dataMap.put(str_riskLevel[i] + "-" + str_controlLevel[j], Float.parseFloat(counts[m][2]) / allcount * 10f); ////
							iffind = true;
							break;
						}
					}
					if (iffind) {
						iffind = false;
					} else {
						dataMap.put(str_riskLevel[i] + "-" + str_controlLevel[j], 0.01f); //设置一个默认值
					}
				}
			}
		}

		//画所有柱子!!!
		float lf_start_x = -2.0f; //
		float lf_start_y = -2.0f; //
		int[] li_cunetype = new int[] { 2, 1, 1, 3 }; //控制有效性只有四项
		Color3f[] colors = new Color3f[] { new Color3f(0.9f, 0.2f, 0.2f), new Color3f(0.7f, 0.4f, 0.2f), new Color3f(0.0f, 1.0f, 0.0f), new Color3f(0.3f, 0.3f, 0.9f), new Color3f(0.5f, 0.2f, 0.8f) }; //随机的颜色!!
		float lf_downsubfix = 0.65f; //下沉的高度,否则旋转时效果不佳!!!即旋转时总是翘到后面去,不能形成360度转着看的感觉!!
		for (int i = 0; i < str_riskLevel.length; i++) { //5个
			for (int j = 0; j < str_controlLevel.length; j++) { //4个
				float lf_height = (Float) dataMap.get(str_riskLevel[i] + "-" + str_controlLevel[j]); //取高度
				int li_type = li_cunetype[j]; //
				Color3f thisColor = colors[i]; //
				if (li_type == 3) { //长方体与其他不一样!是两倍宽度!!
					translate.setTranslation(new Vector3f(lf_start_x + i, lf_start_y + j, (lf_height / 2) - lf_downsubfix)); //移位置!!
					objRoot.addChild(getConeGroup(li_type, rotate, translate, thisColor, 0.2f, 0.2f, lf_height / 2, null)); //加入一个圆椎!!
				} else {
					translate.setTranslation(new Vector3f(lf_start_x + i, lf_start_y + j, (lf_height / 2) - lf_downsubfix)); //移位置!!
					objRoot.addChild(getConeGroup(li_type, rotate, translate, thisColor, 0.2f, 0.0f, lf_height, null)); //加入一个圆椎!!
				}
			}
		}

		//加上文字!!
		for (int i = 0; i < str_riskLevel.length; i++) {
			translate.setTranslation(new Vector3f(-2.2f + i, -2.5f, 0.0f - lf_downsubfix)); //移位置!!
			objRoot.addChild(getConeGroup(4, rotate, translate, colors[i], 0.2f, 0.0f, 0.0f, str_riskLevel[i])); //加入一个文字
		}
		for (int i = 0; i < str_controlLevel.length; i++) {
			translate.setTranslation(new Vector3f(-2.85f, -2.0f + i, 0.0f - lf_downsubfix)); //移位置!!
			objRoot.addChild(getConeGroup(4, rotate, translate, new Color3f(0.0f, 0.0f, 1.0f), 0.2f, 0.0f, 0.0f, str_controlLevel[i])); //加入一个文字
		}

		//加上底板
		translate.setTranslation(new Vector3f(0.0f, -0.5f, -0.06f - lf_downsubfix)); //移位置!!
		objRoot.addChild(getConeGroup(3, rotate, translate, new Color3f(0.1f, 0.2f, 0.1f), 3.0f, 2.5f, 0.06f, null)); //加入一个圆椎!!

		//throw in some light so we aren't stumbling,around in the dark
		Color3f lightColor = new Color3f(0.7f, 0.6f, 0.7f); //打光的渐变颜色!!!
		AmbientLight ambientLight = new AmbientLight(lightColor);
		ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
		objRoot.addChild(ambientLight);
		DirectionalLight directionalLight = new DirectionalLight(); //有光感!!
		directionalLight.setColor(new Color3f(0.7f, 0.6f, 0.7f));
		directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
		objRoot.addChild(directionalLight); //加入对象!!!
		BranchGroup root = new BranchGroup();
		Transform3D _rotate = new Transform3D(); //旋转的!
		_rotate.rotX(Math.toRadians(-70.0)); //设置旋转,转多少度
		_rotate.setScale(new Vector3d(0.25f, 0.23f, 0.23f));//_rotate.setScale(0.23f);
		TransformGroup objRotate = new TransformGroup(_rotate); //
		objRotate.addChild(objRoot);
		root.addChild(objRotate); //加入
		return root;
	}

	//创建一个元素!!包括圆柱,圆锥长方体等等
	private Group getConeGroup(int _type, Transform3D _rotate, Transform3D _translate, Color3f _color, float _xwidth, float _ywidth, float _height, String _text) {
		Group root = new Group();
		TransformGroup objRotate = new TransformGroup(_rotate); //
		TransformGroup objTranslate = new TransformGroup(_translate);

		//创建圆椎!!!
		Material material = new Material();
		material.setAmbientColor(_color); //yellow cone1.0f, 1.0f, 0.0f
		Appearance coneApper = new Appearance(); //
		coneApper.setMaterial(material); //
		if (_type == 1) { //如果是1则是圆柱
			Cylinder cyliner = new Cylinder(_xwidth, _height, Primitive.GENERATE_NORMALS, coneApper); //
			objRotate.addChild(cyliner); //tack on yellow cone
		} else if (_type == 2) { //是圆椎!
			Cone cone = new Cone(_xwidth, _height, Primitive.GENERATE_NORMALS, coneApper); //第一个圆椎的底座的大小,第二个参数是圆椎的高度!!
			objRotate.addChild(cone); //tack on yellow cone
		} else if (_type == 3) { //是长方体!
			Box box = new Box(_xwidth, _height, _ywidth, coneApper); //第一个圆椎的底座的大小,第二个参数是圆椎的高度!!
			objRotate.addChild(box); //tack on yellow cone
		} else if (_type == 4) { //是文字
			Transform3D txt_rotate = new Transform3D(); //旋转的!
			txt_rotate.rotX(Math.toRadians(80.0)); //设置旋转,转多少度
			if (_text.getBytes().length > 5) {
				txt_rotate.setScale(0.1f);//必须在rotX()后面
			} else {
				txt_rotate.setScale(0.15f);//必须在rotX()后面
			}
			Font3D font3d = new Font3D(new Font("黑体", Font.PLAIN, 1), new FontExtrusion());
			Text3D txt3d = new Text3D(font3d, _text, new Point3f(-0.1f, 0.1f, 0.1f));
			Shape3D shape3d = new Shape3D();
			shape3d.setGeometry(txt3d);
			shape3d.setAppearance(coneApper);
			objRotate.setTransform(txt_rotate);
			objRotate.addChild(shape3d);
		}
		objTranslate.addChild(objRotate);
		root.addChild(objTranslate); //加入
		return root;
	}

	public void removeAllContent() {
		universe.removeAllLocales(); //
	}

	public static void main(String[] args) {
		javax.swing.JFrame frame = new javax.swing.JFrame("Java3D");
		frame.setSize(1200, 800);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE); //
		frame.getContentPane().add(new Risk3DPanel());
		frame.setVisible(true); //
	}
}
