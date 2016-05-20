package de.nerogar.ocs.scramble;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.nerogar.ocs.Config;
import de.nerogar.ocs.Logger;

public class ScrambleProviderCache extends ScrambleProvider {

	private class CacheThread extends Thread {

		private ScrambleProvider base;

		private BlockingQueue<String> scrambles;

		public CacheThread(ScrambleProvider base) {
			this.base = base;

			scrambles = new LinkedBlockingQueue<>(Config.getValue(Config.MAX_SCRAMBLE_CACHE_SIZE));

			setDaemon(true);
			setName("scrambleThread-" + base.toString());

			start();
		}

		@Override
		public void run() {
			while (true) {
				String scramble = base.genNextScramble();

				try {
					scrambles.put(scramble);
					Logger.log(Logger.DEBUG, "scramble generated in cache: " + base.toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public String nextScramble() {
			try {
				return scrambles.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return "error while retrieving scrambles from cache";
		}

	}

	private CacheThread thread;

	public ScrambleProviderCache(ScrambleProvider base) {
		this.thread = new CacheThread(base);
	}

	@Override
	public String genNextScramble() {
		return thread.nextScramble();
	}

	@Override
	public String toString() {
		return "scramble chache of " + thread.base.toString();
	}

}
