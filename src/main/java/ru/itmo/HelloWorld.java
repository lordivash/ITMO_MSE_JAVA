package ru.itmo;

import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

public class HelloWorld {
	public static final int ELEMS_SEARCH = 100000;
	public static final int MAP_SIZE = 100000;

	@State(Scope.Benchmark)
	public static class BenchState {
		public int[] keys;
		public int[] values;
		public Map<Integer, Integer> HM;
		public Map<Integer, Integer> CHM;
		public Map<Integer, Integer> CSLM;
		public int[] keyHit;
		public int[] keyMiss;

		@Setup(Level.Trial)
		public void setup() {
			Random random = new Random();
			Set<Integer> uniqueKeys = prepareKeysValues(random);
			fillHashMaps();
			pickKeyHits(random);
			pickKeyMisses(uniqueKeys, random);
		}

		private void pickKeyHits(Random random) {
			keyHit = new int[ELEMS_SEARCH];
			for (int i = 0; i < ELEMS_SEARCH; ++i) {
				keyHit[i] = keys[random.nextInt(0, MAP_SIZE)];
			}
		}

		private void pickKeyMisses(Set<Integer> keys, Random random) {
			keyMiss = new int[ELEMS_SEARCH];
			for (int i = 0; i < ELEMS_SEARCH; ++i) {
				keyMiss[i] = getRandomKeyNotInSet(keys, random);
			}
		}

		private void fillHashMaps() {
			HM = fillHashMap(this, new HashMap<>());
			CHM = fillHashMap(this, new ConcurrentHashMap<>());
			CSLM = fillHashMap(this, new ConcurrentSkipListMap<>());
		}

		private Set<Integer> prepareKeysValues(Random random) {
			Set<Integer> uniqueKeys = new HashSet<>();
			keys = new int[MAP_SIZE];
			values = new int[MAP_SIZE];

			for (int i = 0; i < MAP_SIZE; ++i) {
				int randomKey = getRandomKeyNotInSet(uniqueKeys, random);
				keys[i] = randomKey;
				values[i] = random.nextInt();
				uniqueKeys.add(randomKey);
			}

			return uniqueKeys;
		}

		private int getRandomKeyNotInSet(Set<Integer> set, Random random) {
			int randomKey = random.nextInt();
			while (set.contains(randomKey)) {
				randomKey = random.nextInt();
			}
			return randomKey;
		}
	}

	public static Map<Integer, Integer> fillHashMap(BenchState state, Map<Integer, Integer> map) {
		for (int i = 0; i < MAP_SIZE; ++i) {
			map.put(state.keys[i], state.values[i]);
		}
		return map;
	}

	@Fork(value = 1)
	@Benchmark
	public void HMInsertRandom100000(BenchState state) {
		fillHashMap(state, new HashMap<>());
	}

	@Fork(value = 1)
	@Benchmark
	public void CHMInsertRandom100000(BenchState state) {
		fillHashMap(state, new ConcurrentHashMap<>());
	}

	@Fork(value = 1)
	@Benchmark
	public void CSLMInsertRandom100000(BenchState state) {
		fillHashMap(state, new ConcurrentSkipListMap<>());
	}

	static void lookupHashMap(int[] keys, Map<Integer, Integer> map) {
		// DCE DANGER
		for (int key : keys) {
			map.containsKey(key);
		}
	}

	@Fork(value = 1)
	@Benchmark
	public void HM100000LookupHits100000(BenchState state) {
		lookupHashMap(state.keyHit, state.HM);
	}

	@Fork(value = 1)
	@Benchmark
	public void CHM100000LookupHits100000(BenchState state) {
		lookupHashMap(state.keyHit, state.CHM);
	}

	@Fork(value = 1)
	@Benchmark
	public void CSLM100000LookupHits100000(BenchState state) {
		lookupHashMap(state.keyHit, state.CSLM);
	}

	@Fork(value = 1)
	@Benchmark
	public void HM100000LookupMisses100000(BenchState state) {
		lookupHashMap(state.keyMiss, state.HM);
	}

	@Fork(value = 1)
	@Benchmark
	public void CHM100000LookupMisses100000(BenchState state) {
		lookupHashMap(state.keyMiss, state.CHM);
	}

	@Fork(value = 1)
	@Benchmark
	public void CSLM100000LookupMisses100000(BenchState state) {
		lookupHashMap(state.keyMiss, state.CSLM);
	}
}
