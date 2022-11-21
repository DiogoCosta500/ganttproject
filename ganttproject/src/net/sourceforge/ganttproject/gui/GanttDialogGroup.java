package net.sourceforge.ganttproject.gui;



import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import biz.ganttproject.core.option.GPOption;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.option.StringOption;
import biz.ganttproject.core.option.DefaultStringOption;
import biz.ganttproject.core.option.DefaultEnumerationOption;
import biz.ganttproject.core.option.EnumerationOption;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceGroup;
import net.sourceforge.ganttproject.resource.HumanResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;


public class GanttDialogGroup extends JPanel {

    private final GPAction myCreateAction;
    private final GPAction myDeleteAction;

    private final GPOptionGroup addGroupForm;
    private final GPOptionGroup deleteGroupForm;
    private final StringOption newGroupNameField = new DefaultStringOption("colGroupName");
    private final EnumerationOption newLeaderNameField;
    private final EnumerationOption deleteGroupNameField;

    private HumanResourceManager manager;

    // FALTA FAZER
    // - Atualizar os dropdowns no create e delete
    // - Mudar o index


    public GanttDialogGroup(HumanResourceManager manager) {
        super(new BorderLayout());
        this.manager = manager;
        final HumanResourceManager innerManager = manager;

        myCreateAction = new GPAction("create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HumanResourceGroup newGroup;
                if( newLeaderNameField.getValue() != null ) {
                    String leaderName = newLeaderNameField.getValue().split("\\s+")[2];
                    HumanResource leader = innerManager.getResource(leaderName);
                    newGroup = new HumanResourceGroup(newGroupNameField.getValue(), leader, innerManager);
                }
                else {
                    newGroup = new HumanResourceGroup(newGroupNameField.getValue(), innerManager);
                }
                innerManager.addGroup(newGroup);
            }
        };

        myDeleteAction = new GPAction("delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = deleteGroupNameField.getValue().split("\\s+")[2];
                innerManager.removeGroup(groupName);
            }
        };

        newLeaderNameField = updateResourceEnumerator(manager.getResourcesIt());
        addGroupForm = new GPOptionGroup("createGroup", new GPOption[] { newGroupNameField , newLeaderNameField});
        addGroupForm.setTitled(true);

        deleteGroupNameField = updateGroupEnumerator(manager.getGroupsIt());
        deleteGroupForm = new GPOptionGroup("deleteGroup", new GPOption[] { deleteGroupNameField });
        deleteGroupForm.setTitled(true);
    }

    private DefaultEnumerationOption<Object> updateResourceEnumerator(Iterator<HumanResource> it){
        String[] resourceValues = new String[manager.getNumResources()];

        int i = 0;
        while(it.hasNext()){
            HumanResource next = it.next();
            resourceValues[i++] = next.getId() + " - " + next.getName();
        }

        return new DefaultEnumerationOption<Object>("colLeaderName",resourceValues);
    }

    private DefaultEnumerationOption<Object> updateGroupEnumerator(Iterator<HumanResourceGroup> it){
        String[] groupValues = new String[manager.getNumGroups()];

        int i = 0;
        while(it.hasNext()){
            HumanResourceGroup next = it.next();
            groupValues[i++] = next.getId() + " - " + next.getName();
        }

        return new DefaultEnumerationOption<Object>("colDeleteGroup", groupValues);
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


        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("AddGroup"   ,buildAddGroupTab(builder));
        tabbedPane.addTab("DeleteGroup",buildDeleteGroupTab(builder));
        return tabbedPane;
    }

    private JComponent buildAddGroupTab(OptionsPageBuilder builder){
        JComponent addGroupPage = builder.createGroupComponent(addGroupForm);

        Box btnBoxCreate = Box.createHorizontalBox();
        btnBoxCreate.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        btnBoxCreate.add(new JButton(myCreateAction));

        addGroupPage.add(btnBoxCreate,BorderLayout.AFTER_LAST_LINE);
        return addGroupPage;
    }

    private JComponent buildDeleteGroupTab(OptionsPageBuilder builder){
        JComponent deleteGroupPage = builder.createGroupComponent(deleteGroupForm);

        Box btnBoxDelete = Box.createHorizontalBox();
        btnBoxDelete.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        btnBoxDelete.add(new JButton(myDeleteAction));

        deleteGroupPage.add(btnBoxDelete,BorderLayout.AFTER_LAST_LINE);
        return deleteGroupPage;
    }


}
