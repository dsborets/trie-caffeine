package com.dsborets.trie;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by dsborets on 10/1/16.
 */
public class LoadTest {
  private static final Logger logger = LogManager.getLogger(LoadTest.class);

  private Trie<Integer, Integer, Record> trie;
  private List<String> dictionary;

  @Before
  public void setUp() throws Exception {
    trie = new Trie<>(Trie.DEFAULT_MIN_SEARCHABLE_KEY_LENGTH);
    Caffeine cache = Caffeine.newBuilder();

    Function<Integer, Record> loadRecordById = key -> new Record("abcd", "value1");

    trie.addCaffeine(1, cache, loadRecordById);

    long t0 = System.nanoTime();

    Scanner dictionaryInput = new Scanner(new GZIPInputStream(new FileInputStream("src/test/dictionary.txt.gz")));

    dictionary = new ArrayList<>();

    while (dictionaryInput.hasNextLine()) {
      dictionary.add(dictionaryInput.nextLine());
    }

    dictionaryInput.close();

    long t1 = System.nanoTime();

    logger.debug("Dictionary of {} words loaded in {} seconds.", dictionary.size(), (t1 - t0) * 0.000000001);

    IntStream.range(0, dictionary.size()).forEach(idx -> {
      Record rec = new Record(dictionary.get(idx), String.valueOf(idx));
      trie.put(1, idx, rec);
    });

    long t2 = System.nanoTime();

    logger.debug("Trie size: {}; node size: {}", trie.getSize(), trie.getNodeSize());
    logger.debug("Trie built in {} seconds.", (t2 - t1) * 0.000000001);
  }

  @Test
  public void test() {
    long t1 = System.nanoTime();
    Set<Record> set = trie.getSet("aaa");
    long t2 = System.nanoTime();

    logger.debug("Getting by key in {} seconds.", (t2 - t1) * 0.000000001);

    Assert.assertNotNull(set);
    Assert.assertTrue(set.size() == 18);

    set = trie.getSet("aaab");
    Assert.assertNull(set);
  }
}
