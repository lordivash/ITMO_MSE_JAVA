# Анализ результатов

HM - HashMap<Integer, Integer>\
CHM - ConcurrentHashMap<Integer, Integer>\
CSLM - ConcurrentSkipListMap<Integer, Integer>

## Заполнение хеш таблицы уникальными ключами

```
Benchmark                                 Mode  Cnt     Score     Error  Units
HelloWorld.HMInsertRandom100000          thrpt    5    79,089 ±   3,022  ops/s
HelloWorld.CHMInsertRandom100000         thrpt    5    55,515 ±   1,147  ops/s
HelloWorld.CSLMInsertRandom100000        thrpt    5    26,522 ±   0,112  ops/s
```

### Объяснение вставки

HashMap > ConcurrentHashMap > ConcurrentSkipListMap что является ожидаемым результатом.

ConcurrentHashMap поддерживает многопоточную работу с механизмами блокировки, что вносит свою константу,
поэтому он медленнее, чем HashMap.

ConcurrentSkipListMap же имеет среднюю сложность вставки O(log n), поэтому он просто проигрывает обоим по ассимптотике.

## Поиск элемента

### Поиск существующего элемента

```
Benchmark                                 Mode  Cnt     Score     Error  Units
HelloWorld.HM100000LookupHits100000      thrpt    5  1188,822 ± 624,486  ops/s
HelloWorld.CHM100000LookupHits100000     thrpt    5  1120,354 ± 197,899  ops/s
HelloWorld.CSLM100000LookupHits100000    thrpt    5    30,384 ±   0,912  ops/s
```

### Поиск отсутствующего элемента

```
Benchmark                                 Mode  Cnt     Score     Error  Units
HelloWorld.HM100000LookupMisses100000    thrpt    5  1327,270 ± 283,166  ops/s
HelloWorld.CHM100000LookupMisses100000   thrpt    5  1251,981 ±  94,586  ops/s
HelloWorld.CSLM100000LookupMisses100000  thrpt    5    29,824 ±   1,402  ops/s
```

### Объяснение поиска элемента
HashMap (HM) и ConcurrentHashMap (CHM) - Очень быстрые и близкие по скорости.

Почему CHM почти не уступает HM при поиске? Это ключевой момент. В современных версиях Java ConcurrentHashMap оптимизирован для чтения. Операции чтения (например, get()) не используют блокировок. Вместо этого они используют volatile reads и другие lock-free техники для доступа к данным. Поэтому в однопоточном сценарии или в сценариях с частым чтением и редкой записью производительность CHM при поиске может быть почти такой же высокой, как у HM.

ConcurrentSkipListMap (CSLM) - Значительно медленнее.

Поиск в skip-list требует обхода с самого верхнего уровня списка вниз. В среднем это занимает O(log n) времени. Так что
опять же он проигрывает по ассимптотике.
