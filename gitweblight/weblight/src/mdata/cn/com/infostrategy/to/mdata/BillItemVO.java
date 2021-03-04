package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * ģ���и�������ݶ�Ӧ������,����ComBoxItemVO,RefItemVO,StringItemVO
 * ����valueChanged,editAble�ȱ���,�����ж��ڱ�����Ƿ�ɲ���!
 * @author xch
 *
 */
public abstract class BillItemVO implements Serializable {

	private static final long serialVersionUID = -2454062944108510368L;
	private boolean valueChanged = false; //�Ƿ�ֵ�����˱仯!!
	private boolean editable = true; //�Ƿ�ɱ༭
	private String foreGroundColor = null; //ǰ����ɫ,"8080FF"�ĸ�ʽ(��ֱ��ʹ��Color������Ϊ���������),������ɫ��ʽ��!��ǰ����ɫ��ʽ����Render����ʱִ�е�,��������ʮ��Ƶ���Ķ����������е�ҳ��ˢ���е�"��",��������!�������ڸĳɽ���ɫ���ɶ����е�ֵ�������ڻ���ʱ�����ܸܺߣ�Ȼ��ʹ�ü��ع�ʽ��༭��ʽ��������ɫ!
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

	//ǰ����ɫ,"8080FF"�ĸ�ʽ
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
