package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 模板中各项的数据对应的子类,比如ComBoxItemVO,RefItemVO,StringItemVO
 * 它有valueChanged,editAble等变量,用来判断在表格中是否可操作!
 * @author xch
 *
 */
public abstract class BillItemVO implements Serializable {

	private static final long serialVersionUID = -2454062944108510368L;
	private boolean valueChanged = false; //是否值发生了变化!!
	private boolean editable = true; //是否可编辑
	private String foreGroundColor = null; //前景颜色,"8080FF"的格式(不直接使用Color对象是为了提高性能),用于颜色公式用!以前的颜色公式是在Render绘制时执行的,而绘制是十分频繁的动作，所以有点页面刷新有点"呆",即性能慢!所以现在改成将颜色定成对象中的值，这样在绘制时就性能很高，然后使用加载公式或编辑公式来设置颜色!
	private String backGroundColor = null;

	public boolean isValueChanged() {
		return valueChanged;
	}

	public void setValueChanged(boolean valueChanged) {
		this.valueChanged = valueChanged;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getForeGroundColor() {
		return foreGroundColor;
	}

	//前景颜色,"8080FF"的格式
	public void setForeGroundColor(String foreGroundColor) {
		this.foreGroundColor = foreGroundColor;
	}

	public String getBackGroundColor() {
		return backGroundColor;
	}

	public void setBackGroundColor(String backGroundColor) {
		this.backGroundColor = backGroundColor;
	}

	public BillItemVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillItemVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
