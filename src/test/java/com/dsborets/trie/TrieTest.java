package com.dsborets.trie;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.function.Function;


/**
 * Created by dsborets on 10/1/16.
 */
public class TrieTest {
  private static final Logger logger = LogManager.getLogger(TrieTest.class);

  private Trie<Integer, Integer, Record> trie;

  @Before
  public void setUp() throws Exception {
    trie = new Trie<>(Trie.DEFAULT_MIN_SEARCHABLE_KEY_LENGTH);

    Caffeine cache = Caffeine.newBuilder();

    trie.addCaffeine(1, cache, mockLoadRecordById());
  }

  @Test
  public void testCache() {
    Record rec1 = new Record("abcd", "value1");
    trie.put(1, 1, rec1);

    Record rec2 = new Record("abce", "value2");
    trie.put(1, 2, rec2);

    Assert.assertEquals(trie.getSize(), 2);
    Assert.assertEquals(trie.getNodeSize(), 5);

    Set set = trie.getSet("abcd");
    Assert.assertNotNull(set);
    Assert.assertArrayEquals(set.toArray(), new Record[]{rec1});

    set = trie.getSet("abce");
    Assert.assertNotNull(set);
    Assert.assertArrayEquals(set.toArray(), new Record[]{rec2});

    set = trie.getSet("abc");
    Assert.assertNotNull(set);
    Assert.assertTrue(set.contains(rec1) && set.contains(rec2));

    trie.remove(1, 1);
    set = trie.getSet("abcd");
    Assert.assertNull(set);

    Assert.assertEquals(trie.getSize(), 1);
    Assert.assertEquals(trie.getNodeSize(), 4);

    set = trie.getSet("abc");
    Assert.assertNotNull(set);
    Assert.assertArrayEquals(set.toArray(), new Record[]{rec2});

    trie.remove(1, 2);
    set = trie.getSet("abce");
    Assert.assertNull(set);

    set = trie.getSet("abc");
    Assert.assertNull(set);

    Assert.assertEquals(trie.getSize(), 0);
    Assert.assertEquals(trie.getNodeSize(), 0);
  }

  private Function<Integer, Record> mockLoadRecordById() {
    return key -> {
      logger.debug("Getting by id: " + key + " ...");
      return new Record("abcd", "value2");
    };
  }
}