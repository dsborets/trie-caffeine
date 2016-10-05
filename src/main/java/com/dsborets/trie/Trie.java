package com.dsborets.trie;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Prefix tree (trie) implementation
 *
 * @author dsborets on 10/1/16
 */
public class Trie<I, K, V extends EntryValue> {

  public static final int DEFAULT_MIN_SEARCHABLE_KEY_LENGTH = 3;

  private int minSearchableKeyLength = DEFAULT_MIN_SEARCHABLE_KEY_LENGTH;

  private HashMap<Character, TrieNode<K>> root;

  private int nodeSize;

  /**
   * Put a searchable key with related value to the trie by splitting them into chars
   *
   * @param key searchable key for the prefix tree
   * @param value value {@link EntryKey}
   */
  private void putToTrie(String key, EntryKey<I, K> value) {
    TrieNode<K> node = root.get(key.charAt(0));
    if (node == null) {
      node = new TrieNode<>();
      root.put(key.charAt(0), node);
      nodeSize++;
    }

    add(node, key, key.length(), 1, value);
  }

  private void add(TrieNode<K> node, String sequence, int length, int offset, EntryKey value) {

    if (offset >= minSearchableKeyLength) {
      if (node.getValues() == null) {
        node.setValues(new HashSet<>());
      }
      node.getValues().add(value);
    }

    if (length == offset) {
      //size++;
      return;
    }

    if (node.getSequences() == null) {
      HashMap<Character, TrieNode<K>> children = new HashMap<>();
      node.setSequences(children);
    }

    TrieNode<K> nextNode = node.getSequences().get(sequence.charAt(offset));

    if (nextNode == null) {
      nextNode = new TrieNode<>();
      nodeSize++;
      nextNode.setParent(node);
      node.getSequences().put(sequence.charAt(offset), nextNode);
    }

    add(nextNode, sequence, length, ++offset, value);
  }
}
