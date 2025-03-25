package view.component.panel;

import model.MultimediaList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CollapsablePanel extends JPanel {

    private boolean isCollapsed = true;
    final private MultimediaList MULTIMEDIA_LIST;

    public CollapsablePanel(MultimediaList multimediaList){
        this.MULTIMEDIA_LIST = multimediaList;

        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "fill, flowy, gap 0 0, ins 0",
                "[fill]",
                "[fill]"
        ));

        //Components
        JButton btnExpand = new JButton(MULTIMEDIA_LIST.toString());
        btnExpand.setContentAreaFilled(false);
        btnExpand.setFocusable(false);
        btnExpand.setHorizontalAlignment(SwingConstants.LEFT);
        Border borderButton = BorderFactory.createCompoundBorder(LineBorder.createBlackLineBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnExpand.setBorder(borderButton);


        JPanel pnlContent = new JPanel();
        pnlContent.setPreferredSize(new Dimension(400, 500));
        pnlContent.setBackground(Color.PINK);

        //Listener
        btnExpand.addActionListener(_ ->{
            if (isCollapsed){
                CollapsablePanel.this.add(pnlContent);
            } else {
                CollapsablePanel.this.remove(pnlContent);
            }

            isCollapsed = !isCollapsed;
            CollapsablePanel.this.revalidate();
            CollapsablePanel.this.repaint();
        });

        add(btnExpand, "hmin 100");
    }
}
