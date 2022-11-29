package net.sourceforge.ganttproject.resource;


import java.util.*;

public class HumanResourceGroup {

  private String name;
  private int id;
  private HumanResource leader;
  private List<HumanResource> subordinates;
  private HumanResourceManager myManager;

  public HumanResourceGroup(HumanResourceManager manager) {
    this.name = "";
    this.id = -1;
    this.myManager = manager;
    this.subordinates = new LinkedList<>();
  }
  public HumanResourceGroup(String name, HumanResourceManager manager) {
    this.name = name;
    this.myManager = manager;
    this.subordinates = new LinkedList<>();
  }

  public HumanResourceGroup(String name, HumanResource leader, HumanResourceManager manager) {
    this.name = name;
    this.leader = leader;
    this.myManager = manager;
    this.subordinates = new LinkedList<>();
    leader.setGroup(this);
  }

  public HumanResourceGroup(String name, int id,HumanResource leader,HumanResourceManager manager){
    this.name = name;
    this.id = id;
    this.leader = leader;
    this.subordinates = new LinkedList<>();
    this.myManager = manager;
  }

  public HumanResourceGroup(String name, int id,HumanResourceManager manager){
    this.name = name;
    this.id = id;
    this.subordinates = new LinkedList<>();
    this.myManager = manager;
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

  public int getId(){
    return this.id;
  }

  public void setId(int id){
    this.id = id;
  }

  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

}