package ch.hsr.uebung04.aufgabe01;

import java.util.concurrent.CountDownLatch;

public class BrokenCyclicLatch {
	private static final int NOF_ROUNDS = 10;
	private static final int NOF_THREADS = 10;

	private static CountDownLatch latch = new CountDownLatch(NOF_THREADS);

	private static void multiRounds(int threadId) throws InterruptedException {
		for (int round = 0; round < NOF_ROUNDS; round++) {
			latch.countDown();
			latch.await();
			// Dadurch, dass der Thread mit ID 0 nicht garantiert als erstes ausgeführt wird,
			// kann er überholt werden. Sobald dieser den neuen Latch setzt, wartet der neue Latch auf
			// NOF_THREADS. Dadurch das ein oder mehrere schon durchgelaufen sind, wird der neue Latch nie auf 0
			// gesetzt.
			if (threadId == 0) {
				latch = new CountDownLatch(NOF_THREADS); // new latch for new round
			}
			System.out.println("Round " + round + " thread " + threadId);
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < NOF_THREADS; i++) {
			final int threadId = i;
			new Thread(() -> {
				try {
					multiRounds(threadId);
				} catch (InterruptedException e) {
					throw new AssertionError();
				}
			}).start();
		}
	}
}
