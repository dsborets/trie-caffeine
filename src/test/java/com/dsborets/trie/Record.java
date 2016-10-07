package com.dsborets.trie;

/**
 * Created by dsborets on 10/2/16.
 */
public class Record implements EntryValueCaseInsensitive {
  private String name;
  private String value;


  public Record(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String setKey() {
    return name;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Record{");
    sb.append("name='").append(name).append('\'');
    sb.append(", value='").append(value).append('\'');
    sb.append('}');
    return sb.toString();
  }
}