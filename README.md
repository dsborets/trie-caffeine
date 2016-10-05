# Trie Caffeine

Trie [Caffeine] is a [prefix tree or trie] implementation to keep a keys, with using one or multiple caches to keep a values.
This is allow to mix a keys from multiple sources (caches) in one prefix tree in order to provide:
  - Quick search by key in case of different types of values associated with those keys
  - Split a key by words (free keyword search)

   [Caffeine]: <https://github.com/ben-manes/caffeine>
   [prefix tree or trie]: <https://en.wikipedia.org/wiki/Trie>