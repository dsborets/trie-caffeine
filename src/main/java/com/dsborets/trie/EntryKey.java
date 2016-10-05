package com.dsborets.trie;

/**
 * Prefix tree (trie) entry key to keep cache's id and entity's id within a cache
 *
 * @author dsborets on 10/1/16
 */
class EntryKey<I, K> {
  private I cacheId;
  private K key;

  EntryKey(I cacheId, K key) {
    this.cacheId = cacheId;
    this.key = key;
  }

  I getCacheId() {
    return cacheId;
  }

  K getKey() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EntryKey<?, ?> entryKey = (EntryKey<?, ?>) o;

    if (!cacheId.equals(entryKey.cacheId)) return false;
    return key.equals(entryKey.key);

  }

  @Override
  public int hashCode() {
    int result = cacheId.hashCode();
    result = 31 * result + key.hashCode();
    return result;
  }
}