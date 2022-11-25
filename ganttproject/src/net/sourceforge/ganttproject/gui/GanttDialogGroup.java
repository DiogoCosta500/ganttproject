package net.sourceforge.ganttproject.gui;



import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import biz.ganttproject.core.option.GPOption;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.option.StringOption;
import biz.ganttproject.core.option.DefaultStringOption;
import biz.ganttproject.core.option.DefaultEnumerationOption;
import biz.ganttproject.core.option.EnumerationOption;
import net.sourceforge.ganttproject.resource.ResourcesGroupTableModel;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceGroup;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLOutput;
import java.util.Iterator;
import java.util.LinkedList;


public class GanttDialogGroup extends JPanel {

    private final GPAction myManageAction;
    private final GPAction myCreateAction;
    private final GPAction myDeleteAction;

    private final GPOptionGroup manageGroupForm;
    private final GPOptionGroup addGroupForm;
    private final GPOptionGroup deleteGroupForm;
    private final StringOption newGroupNameField = new DefaultStringOption("colGroupName");

    private final EnumerationOption manageGroupNameField;

    private ResourcesGroupTableModel myModel;
    private final EnumerationOption newLeaderNameField;
    private final EnumerationOption deleteGroupNameField;
    private HumanResourceManager manager;
    private JComponent manageGroupPage;

    private JComponent myTable;

    public GanttDialogGroup(HumanResourceManager manager, EnumerationOption personDialogGroupField, GPOptionGroup personDialogForm) {
        super(new BorderLayout());
        this.manager = manager;
        final HumanResourceManager innerManager = manager;
        final DefaultEnumerationOption personGroupField = (DefaultEnumerationOption) personDialogGroupField;
        final GPOptionGroup pdForm = personDialogForm;

        myManageAction = new GPAction("check") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = manageGroupNameField.getValue().split("\\s+")[2];
                HumanResourceGroup managedGroup = innerManager.getGroup(groupName);

                manageGroupPage.remove(myTable);
                myModel = new ResourcesGroupTableModel(managedGroup);
                myTable = new JTable(myModel);
                manageGroupPage.add(myTable);
                manageGroupPage.updateUI();
            }
        };

        myCreateAction = new GPAction("create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HumanResourceGroup newGroup;
                if( newLeaderNameField.getValue() != null ) {
                    String leaderName = newLeaderNameField.getValue().split("\\s+")[2];
                    HumanResource leader = innerManager.getResource(leaderName);

                    // remove our new group leader from his old group
                    HumanResourceGroup oldLeaderGroup = leader.getGroup();
                    if(oldLeaderGroup.getLeader() != null && oldLeaderGroup.getLeader() == leader) {
                        oldLeaderGroup.unsetLeader();
                    }
                    else {
                        oldLeaderGroup.deleteSubordinate(leader);
                    }

                    newGroup = new HumanResourceGroup(newGroupNameField.getValue(), leader, innerManager);

                    // UI
                    java.util.List<String> newValue = new java.util.LinkedList<String>();
                    newValue.add(newGroupNameField.getValue());

                    String resourceNameValue = (String) pdForm.getOption("name").getValue();
                    String oldGroupValue = (String) pdForm.getOption("colGroup").getValue();

                    personGroupField.updateValues(newValue);
                    if(resourceNameValue.equals(leaderName)) {
                        personGroupField.setValue(newGroup.getName());
                    }
                    else {
                        personGroupField.setValue(oldGroupValue);
                    }
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
                HumanResourceGroup group = innerManager.getGroup(groupName);
                Iterator<HumanResource> it = group.getGroupElementsIt();
                while(it.hasNext()){
                    HumanResource p = it.next();
                    p.setGroup(innerManager.getDefaultGroup());
                }
                innerManager.removeGroup(groupName);
            }


        };

        manageGroupNameField = updateGroupEnumerator(innerManager.getGroupsIt());
        manageGroupForm = new GPOptionGroup("manageGroup", new GPOption[] { manageGroupNameField });
        manageGroupForm.setTitled(true);

        newLeaderNameField = updateResourceEnumerator(innerManager.getResourcesIt());
        addGroupForm = new GPOptionGroup("createGroup", new GPOption[] { newGroupNameField , newLeaderNameField});
        addGroupForm.setTitled(true);

        deleteGroupNameField = updateGroupEnumerator(innerManager.getGroupsIt());
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
        tabbedPane.addTab("Manage group",buildManageGroupTab(builder));
        tabbedPane.addTab("Add group"   ,buildAddGroupTab(builder));
        tabbedPane.addTab("Delete group",buildDeleteGroupTab(builder));
        return tabbedPane;
    }

    private JComponent buildManageGroupTab(OptionsPageBuilder builder){
        manageGroupPage = builder.createGroupComponent(manageGroupForm);

        Box btnBoxManage = Box.createHorizontalBox();
        btnBoxManage.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        btnBoxManage.add(new JButton(myManageAction));

        myTable = new JTable();
        manageGroupPage.add(myTable);

        manageGroupPage.add(btnBoxManage,BorderLayout.AFTER_LAST_LINE);
        return manageGroupPage;
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
