package com.dsborets.trie;

/**
 * Cache entry value interface.
 * "getKey" should return a real prefix tree searching key
 *
 * @author dsborets on 10/1/16
 */
public interface EntryValue {
  String getKey();
}
