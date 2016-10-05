package com.dsborets.trie;

import java.util.HashSet;
import java.util.Map;

/**
 * Trie node contains a map of sequences, link to the parent node ad a list of
 * entry keys {@link EntryKey}
 *
 * @author dsborets on 10/1/16
 */
class TrieNode<V> {
  private TrieNode<V> parent;
  private Map<Character, TrieNode<V>> sequences;
  private HashSet<EntryKey> values;

  TrieNode<V> getParent() {
    return parent;
  }

  void setParent(TrieNode<V> parent) {
    this.parent = parent;
  }

  Map<Character, TrieNode<V>> getSequences() {
    return sequences;
  }

  void setSequences(Map<Character, TrieNode<V>> sequences) {
    this.sequences = sequences;
  }

  HashSet<EntryKey> getValues() {
    return values;
  }

  void setValues(HashSet<EntryKey> values) {
    this.values = values;
  }
}

