package net.sourceforge.ganttproject.gui;



import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import biz.ganttproject.core.option.GPOption;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.option.StringOption;
import biz.ganttproject.core.option.DefaultStringOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;



public class GanttDialogGroup extends JPanel {

    private final GPAction myCreateAction;
    private final GPAction myDeleteAction;

    private final GPOptionGroup addGroupForm;
    private final GPOptionGroup removeGroupForm;

    private final StringOption newGroupNameField = new DefaultStringOption("colGroupName");

    private final StringOption deleteGroupNameField = new DefaultStringOption("colGroupName");

    public GanttDialogGroup() {
        //super(new BorderLayout()); // TAVA NO CODIGO N SEI O QUE FAZ

        myCreateAction = new GPAction("create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        };

        myDeleteAction = new GPAction("delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        };

        addGroupForm = new GPOptionGroup("createGroup", new GPOption[] { newGroupNameField });
        addGroupForm.setTitled(true);
        
        removeGroupForm = new GPOptionGroup("deleteGroup", new GPOption[] { deleteGroupNameField });
        removeGroupForm.setTitled(true);
    }


    public Component createGroupPanel(){
        OptionsPageBuilder builder = new OptionsPageBuilder();
        OptionsPageBuilder.I18N i18n = new OptionsPageBuilder.I18N() {
            @Override
            public String getOptionLabel(GPOptionGroup group, GPOption<?> option) {
                return getValue(option.getID());
            }
        };
        builder.setI18N(i18n);

        final JComponent groupPage = builder.buildPlanePage(new GPOptionGroup[] {addGroupForm, removeGroupForm});
        groupPage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        return groupPage;
    }

}

   /* Box buttonBox = Box.createHorizontalBox();
    Box buttonBox2 = Box.createHorizontalBox();
    buttonBox.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
    buttonBox2.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
    buttonBox.add(new JButton(myCreateAction));
    buttonBox.add(Box.createHorizontalStrut(5));
    buttonBox2.add(new JButton(myDeleteAction));
    buttonBox2.add(Box.createHorizontalStrut(5));
    groupPage.add(buttonBox, BorderLayout.SOUTH);
    groupPage.add(buttonBox2, BorderLayout.SOUTH);*/