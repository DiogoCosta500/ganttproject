/*
GanttProject is an opensource project management tool. License: GPL3
Copyright (C) 2010 Dmitry Barashev

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.resource;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.roles.Role;

import javax.annotation.Resource;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ResourcesGroupTableModel extends AbstractTableModel {

  static enum Column {
    ID("id", String.class), NAME("resourcename", String.class),
    ROLE("role", String.class), GROUP("group", String.class);

    private final String myName;
    private final Class<?> myClass;

    Column(String key, Class<?> clazz) {
      myName = GanttLanguage.getInstance().getText(key);
      myClass = clazz;
    }

    String getName() {
      return myName;
    }

    Class<?> getColumnClass() {
      return myClass;
    }
  }

  private final List<HumanResource> myAssignments;

  private final HumanResourceGroup myGroup;

  private boolean isChanged = false;

  public ResourcesGroupTableModel(HumanResourceGroup group) {
    myGroup = group;
    myAssignments = group.getGroupElements();
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return Column.values()[columnIndex].getColumnClass();
  }

  @Override
  public int getColumnCount() {
    return Column.values().length;
  }

  @Override
  public int getRowCount() {
    return myAssignments.size() + 1;
  }

  @Override
  public String getColumnName(int col) {
    return Column.values()[col].getName();
  }

  @Override
  public Object getValueAt(int row, int col) {
    Object result;
    if (row >= 0) {
      if (row < myAssignments.size()) {
        HumanResource resource = myAssignments.get(row);
        switch (col) {
        case 0:
          result = String.valueOf(resource.getId());
          break;
        case 1:
          result = resource.getName();
          break;
        case 2:
          result = resource.getRole();
          break;
        case 3:
          result = resource.getGroup().getName();
          break;
        default:
          result = "";
        }
      } else {
        result = null;
      }
    } else {
      throw new IllegalArgumentException("I can't return data in row=" + row);
    }
    return result;
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    boolean result = col > 0;
    if (result) {
      result = (col == 2 ? row < myAssignments.size() : row <= myAssignments.size()) || col == 3 || col == 4;
    }
    return result;
  }


  /*public void setValueAt(HumanResource resource, int row, int col) {
    if (row >= 0) {
      if (row >= myAssignments.size())
        createGroupAssigment(resource);
    } else {
      throw new IllegalArgumentException("I can't set data in row=" + row);
    }
    isChanged = true;
  }*/

  private void createGroupAssigment(HumanResource resource) {
    myAssignments.add(resource);
    fireTableRowsInserted(myAssignments.size(),myAssignments.size());

    /*if (value instanceof HumanResource) {
      ResourceAssignment newAssignment = myMutator.addAssignment((HumanResource) value);

      boolean coord = false;          // FAZER PARA LEADER DO GROUP
      if (myAssignments.isEmpty())
        coord = true;
      newAssignment.setCoordinator(coord);

      newAssignment.setRoleForAssignment(newAssignment.getResource().getRole());
      myAssignments.add(newAssignment);
      fireTableRowsInserted(myAssignments.size(), myAssignments.size());
    }*/
  }

  public List<HumanResource> getResourcesGroupAssignments() {
    return Collections.unmodifiableList(myAssignments);
  }

  /*public boolean isChanged() {
    return isChanged;
  }*/

  /*public void delete(int[] selectedRows) {
    List<ResourceAssignment> selected = new ArrayList<ResourceAssignment>();
    for (int row : selectedRows) {
      if (row < myAssignments.size()) {
        selected.add(myAssignments.get(row));
      }
    }
    for (ResourceAssignment ra : selected) {
      ra.delete();
    }
    myAssignments.removeAll(selected);
    fireTableDataChanged();
  }*/

}
