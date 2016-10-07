package com.dsborets.trie;

import org.apache.commons.lang.StringUtils;

/**
 * Cache entry value interface.
 * "getKey" should return a real prefix tree searching key
 *
 * @author dsborets on 10/1/16
 */
public interface EntryValueCaseInsensitive extends EntryValue{

  @Override
  default String getKey() {
    return StringUtils.isNotEmpty(setKey()) ? setKey().toLowerCase() : null;
  }

  String setKey();
}
