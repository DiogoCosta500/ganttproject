package net.sourceforge.ganttproject.resource;


import java.util.*;

public class HumanResourceGroup extends HumanResource{
  private HumanResource leader;
  private List<HumanResource> subordinates;



  public HumanResourceGroup(HumanResourceManager manager) {
    super("",-1,manager);
    this.subordinates = new LinkedList<>();
  }
  public HumanResourceGroup(String name, HumanResourceManager manager) {
    super(name,manager);
    this.subordinates = new LinkedList<>();
  }

  public HumanResourceGroup(String name, HumanResource leader, HumanResourceManager manager) {
    super(name,manager);
    this.leader = leader;
    this.subordinates = new LinkedList<>();
    leader.setGroup(this);
  }

  public HumanResourceGroup(String name, int id,HumanResource leader,HumanResourceManager manager){
    super(name,id,manager);
    this.leader = leader;
    this.subordinates = new LinkedList<>();
  }

  public HumanResourceGroup(String name, int id,HumanResourceManager manager){
    super(name,id,manager);
    this.subordinates = new LinkedList<>();
  }

  public void addSubordinate(HumanResource subordinate){
    if( !this.subordinates.contains(subordinate) )
      this.subordinates.add(subordinate);
  }

  public void removeSubordinate(HumanResource subordinate){
      this.subordinates.remove(subordinate);
  }

  public void removeElement(HumanResource element){
    if(leader == element){
      unsetLeader();
    }else{
      removeSubordinate(element);
    }
  }


  /**
   * If the group has no leader, it passes the leadership to the first added subordinate
   * if there are any
   */
  private void getLeaderSubstitution(){
    if( leader == null && subordinates.size() > 0 ){
      leader = subordinates.get(0);
    }
  }

  /**
   * Unsets the group leader and passes the leadership.
   */
  public void unsetLeader(){
    leader = null;
    getLeaderSubstitution();
  }

  public void setLeader(HumanResource leader){
    this.leader = leader;
  }

  public HumanResource getLeader(){
    return leader;
  }

  public Iterator<HumanResource> getSubordinates(){
    return subordinates.iterator();
  }

  // Returns leader and subordinates
  public Iterator<HumanResource> getGroupElementsIt(){
    return getGroupElements().iterator();
  }

  public List<HumanResource> getGroupElements(){
    if(leader == null) return subordinates;

    List newList = new LinkedList<HumanResource>(subordinates);
    newList.add(0, leader);
    return newList;
  }

  public HumanResourceGroup getGroup(){
    return this;
  }

}