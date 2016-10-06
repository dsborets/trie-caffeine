package com.dsborets.trie;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Prefix tree (trie) implementation
 *
 * @author dsborets on 10/1/16
 */
public class Trie<I, K, V extends EntryValue> {

  public static final int DEFAULT_MIN_SEARCHABLE_KEY_LENGTH = 3;

  private int minSearchableKeyLength = DEFAULT_MIN_SEARCHABLE_KEY_LENGTH;

  private HashMap<Character, TrieNode<K>> root;

  private HashMap<I, LoadingCache<K, V>> cacheList;

  private int nodeSize;

  public Trie(int minSearchableKeyLength) {
    this.minSearchableKeyLength = minSearchableKeyLength;
    root = new HashMap<>();
    cacheList = new HashMap<>();
  }

  public void addCaffeine(I cacheId, Caffeine<K, V> caffeine, Function<K, V> buildFunction) {
    addCaffeine(cacheId, caffeine, buildFunction, null);
  }

  /**
   * Add a cache to the list of caches
   *
   * @param cacheId       - cache id (must be unique)
   * @param caffeine      - Caffeine object
   * @param buildFunction - the function to get an entry by id in case of the entry expiration in the cache (see Caffeine doc)
   * @param delimiter     - delimiter char if case of free search key
   */
  public void addCaffeine(I cacheId, Caffeine<K, V> caffeine, Function<K, V> buildFunction, String delimiter) {
    Cache currentCache = cacheList.get(cacheId);

    if (currentCache != null)
      throw new RuntimeException(String.format("The cache with provided id {%s} already exists", cacheId));

    LoadingCache<K, V> cache = caffeine.writer(new CacheWriter<K, V>() {
      @Override

      public void write(@Nonnull K key, @Nonnull V value) {
        putToTrie(value.getKey(), new EntryKey<>(cacheId, key));
      }

      @Override
      public void delete(@Nonnull K key, V value, @Nonnull RemovalCause cause) {
        removeFromTrie(value.getKey(), new EntryKey<>(cacheId, key));
      }
    }).build(buildFunction::apply);

    cacheList.put(cacheId, cache);
  }

  /**
   * Put a searchable key with related value to the trie by splitting them into chars
   *
   * @param key   searchable key for the prefix tree
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
