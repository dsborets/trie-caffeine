package com.dsborets.trie;

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

  @Before
  public void setUp() throws Exception {
    trie = new Trie<>(Trie.DEFAULT_MIN_SEARCHABLE_KEY_LENGTH);

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