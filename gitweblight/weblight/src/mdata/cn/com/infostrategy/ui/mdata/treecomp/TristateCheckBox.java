package cn.com.infostrategy.ui.mdata.treecomp;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.event.*;

/**
 * ÈýÌ¬¹´Ñ¡¿ò
 * @author haoming
 * create by 2015-7-16
 */
public class TristateCheckBox extends JCheckBox {

	private final TristateDecorator decorator;

	public TristateCheckBox(String text, Icon icon, Boolean initial) {
		super(text, icon);
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				grabFocus();
				decorator.nextState();
			}
		});
		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() { //NOI18N
					public void actionPerformed(ActionEvent e) {
						grabFocus();
						decorator.nextState();
					}
				});
		map.put("released", null); //NOI18N
		SwingUtilities.replaceUIActionMap(this, map);
		decorator = new TristateDecorator(getModel());
		setModel(decorator);
		setState(initial);
	}

	public TristateCheckBox(String text, Boolean initial) {
		this(text, null, initial);
	}

	public TristateCheckBox(String text) {
		this(text, null);
	}

	public TristateCheckBox() {
		this(null);
	}

	public void addMouseListener(MouseListener l) {
	}

	public void setState(Boolean state) {
		decorator.setState(state);
	}

	public Boolean getState() {
		return decorator.getState();
	}

	private class TristateDecorator implements ButtonModel {

		private final ButtonModel other;

		private TristateDecorator(ButtonModel other) {
			this.other = other;
		}

		private void setState(Boolean state) {
			if (state == Boolean.FALSE) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == Boolean.TRUE) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else {
				other.setArmed(true);
				setPressed(true);
				setSelected(true);
			}
		}

		private Boolean getState() {
			if (isSelected() && !isArmed()) {
				// normal black tick
				return Boolean.TRUE;
			} else if (isSelected() && isArmed()) {
				// don't care grey tick
				return null;
			} else {
				// normal deselected
				return Boolean.FALSE;
			}
		}

		private void nextState() {
			Boolean current = getState();
			if (current == Boolean.FALSE) {
				setState(Boolean.TRUE);
			} else if (current == Boolean.TRUE) {
				setState(null);
			} else if (current == null) {
				setState(Boolean.FALSE);
			}
		}

		public void setArmed(boolean b) {
		}

		public boolean isFocusTraversable() {
			return isEnabled();
		}

		public void setEnabled(boolean b) {
			other.setEnabled(b);
		}

		public boolean isArmed() {
			return other.isArmed();
		}

		public boolean isSelected() {
			return other.isSelected();
		}

		public boolean isEnabled() {
			return other.isEnabled();
		}

		public boolean isPressed() {
			return other.isPressed();
		}

		public boolean isRollover() {
			return other.isRollover();
		}

		public void setSelected(boolean b) {
			other.setSelected(b);
		}

		public void setPressed(boolean b) {
			other.setPressed(b);
		}

		public void setRollover(boolean b) {
			other.setRollover(b);
		}

		public void setMnemonic(int key) {
			other.setMnemonic(key);
		}

		public int getMnemonic() {
			return other.getMnemonic();
		}

		public void setActionCommand(String s) {
			other.setActionCommand(s);
		}

		public String getActionCommand() {
			return other.getActionCommand();
		}

		public void setGroup(ButtonGroup group) {
			other.setGroup(group);
		}

		public void addActionListener(ActionListener l) {
			other.addActionListener(l);
		}

		public void removeActionListener(ActionListener l) {
			other.removeActionListener(l);
		}

		public void addItemListener(ItemListener l) {
			other.addItemListener(l);
		}

		public void removeItemListener(ItemListener l) {
			other.removeItemListener(l);
		}

		public void addChangeListener(ChangeListener l) {
			other.addChangeListener(l);
		}

		public void removeChangeListener(ChangeListener l) {
			other.removeChangeListener(l);
		}

		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}
	}
}