package view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MySearchTextField extends JTextField {
    public MySearchTextField(){
        super();
        setInitialValues();

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getForeground() == Color.GRAY){
                    setForeground(Color.BLACK);
                    setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()){
                    setInitialValues();
                }
            }
        });
    }

    private void setInitialValues(){
        setText("Search...");
        setForeground(Color.GRAY);
    }
}
