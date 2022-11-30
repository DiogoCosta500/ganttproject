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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class GanttDialogGroup extends JPanel {

    private final GPAction myManageAction;
    private final GPAction mySelectAction;
    private final GPAction myAddResourceAction;
    private final GPAction myRemoveResourceAction;
    private final GPAction myMakeLeaderAction;

    private final GPAction myCreateAction;
    private final GPAction myDeleteAction;


    private final GPOptionGroup manageCheckGroupForm;

    private final GPOptionGroup managedGroupForm;
    private final GPOptionGroup addGroupForm;
    private final GPOptionGroup deleteGroupForm;
    private final StringOption newGroupNameField = new DefaultStringOption("colGroupName");

    private final EnumerationOption manageGroupNameField;

    private final DefaultEnumerationOption resourceToAddField;
    private final DefaultEnumerationOption resourceToRemoveField;
    private final DefaultEnumerationOption resourceToSetLeaderField;
    private ResourcesGroupTableModel myModel;
    private final EnumerationOption newLeaderNameField;
    private final EnumerationOption deleteGroupNameField;
    private HumanResourceManager manager;
    private JComponent checkGroupPage;
    private JComponent manageGroupPage;
    private JTable myTable;
    private JScrollPane myScroll;

    public GanttDialogGroup(HumanResourceManager manager, EnumerationOption personDialogGroupField,
                            GPOptionGroup personDialogForm, HumanResource editingPerson) {
        super(new BorderLayout());
        this.manager = manager;
        final HumanResourceManager innerManager = manager;
        final DefaultEnumerationOption personGroupField = (DefaultEnumerationOption) personDialogGroupField;
        final GPOptionGroup pdForm = personDialogForm;
        final HumanResource pdEditingPerson = editingPerson;




        mySelectAction = new GPAction("Select"){
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = manageGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup managedGroup = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));

                updateBoxes(managedGroup);
            }
        };

        myAddResourceAction = new GPAction("Add Resource"){
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = manageGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup managedGroup = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));

                String resourceId = ((EnumerationOption) resourceToAddField).getValue().split("\\s+")[0];
                HumanResource resource = innerManager.getById(Integer.parseInt(resourceId));

                resource.getGroup().removeElement(resource);
                resource.setGroup(managedGroup);
                managedGroup.addSubordinate(resource);

                if( resourceId.equals(Integer.toString(pdEditingPerson.getId())) ) {
                    personGroupField.setValue(managedGroup.getId() + " - " + managedGroup.getName());
                }

                updateBoxes(managedGroup);
            }
        };

        myRemoveResourceAction = new GPAction("Remove Resource"){
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = manageGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup managedGroup = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));

                String resourceId = ((EnumerationOption) resourceToRemoveField).getValue().split("\\s+")[0];
                HumanResource resource = innerManager.getById(Integer.parseInt(resourceId));

                managedGroup.removeElement(resource);
                innerManager.getDefaultGroup().addSubordinate(resource);
                resource.setGroup(innerManager.getDefaultGroup());

                updateBoxes(managedGroup);
            }
        };

        myMakeLeaderAction = new GPAction("Make Leader"){
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId =  manageGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup managedGroup = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));

                String resourceId = ((EnumerationOption) resourceToSetLeaderField).getValue().split("\\s+")[0];
                HumanResource resource = innerManager.getById(Integer.parseInt(resourceId));

                HumanResource oldLeader = managedGroup.getLeader();
                managedGroup.unsetLeader();
                if(oldLeader != null)
                    managedGroup.addSubordinate(oldLeader);
                managedGroup.setLeader(resource);
                managedGroup.removeSubordinate(resource);

                updateBoxes(managedGroup);
            }
        };

        myManageAction = new GPAction("check") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = manageGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup managedGroup = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));

                // Creating table and updating UI
                myModel = new ResourcesGroupTableModel(managedGroup);
                myTable.setModel(myModel);
                myTable.updateUI();
                checkGroupPage.updateUI();
            }
        };

        myCreateAction = new GPAction("create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HumanResourceGroup newGroup;
                if( newLeaderNameField.getValue() != null ) {
                    String leaderId = newLeaderNameField.getValue().split("\\s+")[0];
                    HumanResource leader = innerManager.getById(Integer.parseInt(leaderId));

                    // remove our new group leader from his old group
                    HumanResourceGroup oldLeaderGroup = leader.getGroup();
                    oldLeaderGroup.removeElement(leader);

                    newGroup = new HumanResourceGroup(newGroupNameField.getValue(), leader, innerManager);

                    // UI
                    List<String> newValue = new LinkedList<String>();
                    newValue.add(newGroupNameField.getValue());

                    String oldGroupValue = (String) pdForm.getOption("colGroup").getValue();

                    personGroupField.updateValues(newValue);
                    if( leaderId.equals(Integer.toString(pdEditingPerson.getId())) ) {
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
                String groupId = deleteGroupNameField.getValue().split("\\s+")[0];
                HumanResourceGroup group = (HumanResourceGroup) innerManager.getById(Integer.parseInt(groupId));
                Iterator<HumanResource> it = group.getGroupElementsIt();
                while(it.hasNext()){
                    HumanResource p = it.next();
                    p.setGroup(innerManager.getDefaultGroup());
                }
                if( pdEditingPerson.getGroup() == group)
                    pdForm.getOption("colGroup").setValue("0 - none");
                innerManager.removeGroupById(Integer.parseInt(groupId));
            }
        };


        manageGroupNameField = (EnumerationOption) getGroupEnumerator(innerManager.getGroupsIt());
        manageCheckGroupForm = new GPOptionGroup("manageCheckGroup", new GPOption[] { manageGroupNameField });
        manageCheckGroupForm.setTitled(true);

        resourceToAddField = getEmptyEnumerator("colResourceToAddField");
        resourceToRemoveField = getEmptyEnumerator("colResourceToRemoveField");
        resourceToSetLeaderField = getEmptyEnumerator("colResourceToSetLeaderField");
        managedGroupForm = new GPOptionGroup("manageGroup", new GPOption[] { manageGroupNameField, resourceToAddField , resourceToRemoveField , resourceToSetLeaderField });
        managedGroupForm.setTitled(true);

        newLeaderNameField = (EnumerationOption) getResourceEnumerator(innerManager.getResourcesIt());
        addGroupForm = new GPOptionGroup("createGroup", new GPOption[] { newGroupNameField , newLeaderNameField});
        addGroupForm.setTitled(true);

        deleteGroupNameField = (EnumerationOption) getGroupEnumerator(innerManager.getGroupsIt());
        deleteGroupForm = new GPOptionGroup("deleteGroup", new GPOption[] { deleteGroupNameField });
        deleteGroupForm.setTitled(true);
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
        tabbedPane.addTab("Check group", buildCheckGroupTab(builder));
        tabbedPane.addTab("Manage group",buildManageGroupTab(builder));
        tabbedPane.addTab("Add group"   ,buildAddGroupTab(builder));
        tabbedPane.addTab("Delete group",buildDeleteGroupTab(builder));
        return tabbedPane;
    }

    private JComponent buildManageGroupTab(OptionsPageBuilder builder){
        manageGroupPage = builder.createGroupComponent(managedGroupForm);

        Box btnBoxManage = Box.createHorizontalBox();
        btnBoxManage.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        btnBoxManage.add(new JButton(mySelectAction));
        btnBoxManage.add(new JButton(myAddResourceAction));
        btnBoxManage.add(new JButton(myRemoveResourceAction));
        btnBoxManage.add(new JButton(myMakeLeaderAction));

        manageGroupPage.add(btnBoxManage,BorderLayout.AFTER_LAST_LINE);
        return manageGroupPage;
    }

    private JComponent buildCheckGroupTab(OptionsPageBuilder builder){
        checkGroupPage = builder.createGroupComponent(manageCheckGroupForm);

        Box btnBoxManage = Box.createHorizontalBox();
        btnBoxManage.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        btnBoxManage.add(new JButton(myManageAction));

        myTable = new JTable();
        myScroll = new JScrollPane(myTable);
        checkGroupPage.add(myScroll);

        checkGroupPage.add(btnBoxManage,BorderLayout.AFTER_LAST_LINE);
        return checkGroupPage;
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

    private DefaultEnumerationOption<Object> getEmptyEnumerator(String colName){
        String[] empty = {"-"};

        return new DefaultEnumerationOption<Object>(colName,empty);
    }

    private DefaultEnumerationOption<Object> getResourceEnumerator(Iterator<HumanResource> it){
        String[] resourceValues = new String[manager.getNumResources()];

        int i = 0;
        while(it.hasNext()){
            HumanResource next = it.next();
            resourceValues[i++] = next.getId() + " - " + next.getName();
        }

        return new DefaultEnumerationOption<Object>("colLeaderName",resourceValues);
    }

    private DefaultEnumerationOption<Object> getGroupEnumerator(Iterator<HumanResourceGroup> it){
        String[] groupValues = new String[manager.getNumGroups()];

        int i = 0;
        while(it.hasNext()){
            HumanResourceGroup next = it.next();
            groupValues[i++] = next.getId() + " - " + next.getName();
        }

        return new DefaultEnumerationOption<Object>("colDeleteGroup", groupValues);
    }

    private List<String> getResourceValuesEnumerator(Iterator<HumanResource> it){
        List<String> resourceValues = new LinkedList<String>();

        while(it.hasNext()){
            HumanResource next = it.next();
            resourceValues.add(next.getId() + " - " + next.getName());
        }

        return resourceValues;
    }

    private void updateBoxes(HumanResourceGroup managedGroup){
        if(managedGroup == manager.getDefaultGroup()) {
            resourceToAddField.reloadValues(new LinkedList<String>());
            resourceToSetLeaderField.reloadValues(new LinkedList<String>());
            resourceToRemoveField.reloadValues(new LinkedList<String>());
        }else {
            resourceToAddField.reloadValues(getResourceValuesEnumerator(manager.getDefaultGroup().getSubordinates()));
            resourceToSetLeaderField.reloadValues(getResourceValuesEnumerator(managedGroup.getSubordinates()));
            resourceToRemoveField.reloadValues(getResourceValuesEnumerator(managedGroup.getGroupElementsIt()));
        }
    }



}
