package mod.wurmonline.serverlauncher.gui;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
// Adjusted version of FormattedFloatEditor from Wurm Unlimited by Code Club AB
//

import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.PropertyEditor;

public class FormattedFloatEditor implements PropertyEditor<Float> {
    Item item;
    TextField textField;

    public FormattedFloatEditor(final Item item) {
        this.item = item;
        this.textField = new TextField();
        this.textField.setOnAction((ae) -> {
            item.setValue(this.getValue());
            this.textField.textProperty().setValue(this.getValue().toString());
        });
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0 && !newValue.matches("(^\\d*\\.?\\d*[0-9]+\\d*$)|(^[0-9]+\\d*\\.\\d*$)")) {
                FormattedFloatEditor.this.textField.textProperty().setValue(oldValue);
            }

        });
        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                item.setValue(FormattedFloatEditor.this.getValue());
                FormattedFloatEditor.this.textField.textProperty().setValue(FormattedFloatEditor.this.getValue().toString());
            }

        });
    }

    public Node getEditor() {
        return this.textField;
    }

    public Float getValue() {
        return this.stringToObj(this.textField.getText(), this.item.getType());
    }

    public void setValue(Float t) {
        this.textField.setText(objToString(t));
    }

    public static String objToString(Object value) {
        return value.toString();
    }

    private Float stringToObj(String str, Class<?> cls) {
        try {
            if (str == null) {
                return null;
            } else {
                String t = cls.getName();
                Float min = null;
                Float max = null;
                if (this.item instanceof MinMax) {
                    min = ((MinMax) this.item).getMinValue();
                    max = ((MinMax) this.item).getMaxValue();
                }

                if (!t.equals("float") && !cls.equals(Float.class)) {
                    return null;
                } else {
                    if (str.length() == 0 || str.length() == 1 && str.contains(".")) {
                        return min != null ? min : new java.lang.Float(0.0F);
                    } else {
                        java.lang.Float val = new java.lang.Float(str);
                        return min != null && val < min ? min : (max != null && val > max ? max : val);
                    }
                }
            }
        } catch (Throwable var9) {
            return null;
        }
    }
}
