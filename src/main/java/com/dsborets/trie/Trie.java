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

  /**
   * Remove a value from a trie by key
   *
   * @param key   searchable key for the prefix tree
   * @param value value {@link EntryKey}
   */
  private boolean removeFromTrie(String key, EntryKey<I, K> value) {
    TrieNode<K> node = root.get(key.charAt(0));
    if (node == null || node.getSequences() == null)
      return false;

    TrieNode<K> lastNode = searchLastNode(node, key, key.length(), 1);

    if (lastNode == null)
      return false;

    TrieNode<K> firstNode = remove(lastNode, key, key.length() - 1, value);

    if (firstNode.getSequences() != null && firstNode.getSequences().size() == 0) {
      root.remove(key.charAt(0));
      nodeSize--;
    }

    return true;
  }

  /**
   * Remove a nodes from the trie based on se char sequence (trie key)
   *
   * @param node   - root node
   * @param key    - trie key
   * @param offset - current char in the key
   * @param value  - the value related to the key
   * @return first node of the removed sequence
   */
  private TrieNode<K> remove(TrieNode<K> node, String key, int offset, EntryKey value) {

    Set<EntryKey> values = node.getValues();
    if (values != null) {
      values.remove(value);
    }

    TrieNode<K> parent = node.getParent();

    if (parent == null)
      return node;

    Map<Character, TrieNode<K>> children = node.getSequences();
    if ((children == null || children.size() == 0) && (values == null || values.size() == 0)) {
      node.setParent(null);
      parent.getSequences().remove(key.charAt(offset));
      nodeSize--;
    }

    return remove(parent, key, --offset, value);
  }

  /**
   * Search the last node in the trie based on the sequence of chars (key)
   *
   * @param node   - root node
   * @param key    - trie key
   * @param length - key length
   * @param offset - current char in the key
   * @return last node of the sequence in the trie
   */
  private TrieNode<K> searchLastNode(TrieNode<K> node, String key, int length, int offset) {
    if (length == offset)
      return node;
    if (node.getSequences() != null) {
      TrieNode<K> nextNode = node.getSequences().get(key.charAt(offset));
      if (nextNode != null && node.getSequences() != null) {
        return searchLastNode(nextNode, key, length, ++offset);
      }
    }
    return null;
  }
}
