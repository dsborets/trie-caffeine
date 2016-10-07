package com.dsborets.trie;

import com.google.common.testing.FakeTicker;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * Created by dsborets on 10/1/16.
 */
public class TrieTest {
  private static final Logger logger = LogManager.getLogger(TrieTest.class);

  private Trie<Integer, Integer, Record> trie;

  private FakeTicker ticker;

  @Before
  public void setUp() throws Exception {
    trie = new Trie<>(Trie.DEFAULT_MIN_SEARCHABLE_KEY_LENGTH);

    ticker = new FakeTicker();

    Caffeine cache1 = Caffeine.newBuilder()
            .refreshAfterWrite(5, TimeUnit.SECONDS)
            .executor(Runnable::run)
            .ticker(ticker::read);

    trie.addCaffeine(1, cache1, mockLoadRecordById());
    Caffeine cache2 = Caffeine.newBuilder();
    trie.addCaffeine(2, cache2, mockLoadRecordById());
  }

  @Test
  public void timeExpirationTest() {
    Record rec1 = new Record("ABCD", "value1");

    logger.debug("Adding one element");
    trie.put(1, 1, rec1);

    logger.debug("Getting it back");
    Set<Record> set = trie.getSet("abcd");
    Assert.assertTrue(set.size() == 1);
    Assert.assertTrue(set.size() == 1);
    Record[] records = set.toArray(new Record[set.size()]);
    Assert.assertTrue("value1".equals(records[0].getValue()));

    logger.debug("Moving time");
    ticker.advance(30, TimeUnit.MINUTES);

    logger.debug("Getting it back again...");
    set = trie.getSet("abcd");

    Assert.assertTrue(set.size() == 1);
    records = set.toArray(new Record[set.size()]);
    Assert.assertTrue("value1".equals(records[0].getValue()));

    set = trie.getSet("abcd");
    Assert.assertTrue(set.size() == 1);
    records = set.toArray(new Record[set.size()]);
    Assert.assertTrue("value2".equals(records[0].getValue()));
  }

  @Test
  public void testOneCache() {
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


  @Test
  public void testTwoCaches() {
    Record rec1 = new Record("abcd", "value1");
    trie.put(1, 1, rec1);

    Record rec2 = new Record("abce", "value2");
    trie.put(2, 1, rec2);

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

    trie.remove(2, 1);
    set = trie.getSet("abce");
    Assert.assertNull(set);

    set = trie.getSet("abc");
    Assert.assertNull(set);

    Assert.assertEquals(trie.getSize(), 0);
    Assert.assertEquals(trie.getNodeSize(), 0);
  }

  @Test(expected = RuntimeException.class)
  public void testNoCacheById() {
    Record rec = new Record("abcd", "value1");
    trie.put(3, 1, rec);
  }

  @Test(expected = RuntimeException.class)
  public void testNullKey() {
    Record rec = new Record("abcd", "value1");
    trie.put(1, null, rec);
  }

  @Test(expected = RuntimeException.class)
  public void testNullValue() {
    trie.put(1, 1, null);
  }

  @Test(expected = RuntimeException.class)
  public void testRemoveCacheWithNullCacheId() {
    trie.put(null, 1, null);
  }

  @Test(expected = RuntimeException.class)
  public void testGetSetByNullKey() {
    trie.getSet(null);
  }

  @Test(expected = RuntimeException.class)
  public void testAddCacheWithExistingKey() {
    Caffeine cache = Caffeine.newBuilder();

    trie.addCaffeine(1, cache, mockLoadRecordById());
  }

  private Function<Integer, Record> mockLoadRecordById() {
    return key -> {
      logger.debug("Getting by id: " + key + " ...");
      return new Record("abcd", "value2");
    };
  }
}