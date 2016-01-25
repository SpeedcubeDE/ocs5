package de.nerogar.ocs.tasks;

import java.util.ArrayList;

import de.nerogar.ocs.Logger;
import de.nerogar.ocs.OCSServer;

public class Scheduler extends Thread {

	private class TaskContainer {
		public Task task;
		public long timestamp;

		/**0 = don't repeat, -1 = infinite*/
		public int repeat;
		public long interval;

		public TaskContainer(Task task, long timestamp, int repeat, long interval) {
			this.task = task;
			this.timestamp = timestamp;
			this.repeat = repeat;
			this.interval = interval;
		}
	}

	private ArrayList<TaskContainer> tasks;

	private boolean running;

	private Object lock = new Object();
	private boolean earlyWake;

	public Scheduler() {
		tasks = new ArrayList<TaskContainer>();
		running = true;

		setDaemon(true);
	}

	public void addTask(Task task, long timestamp) {
		addTask(new TaskContainer(task, timestamp, 0, 0));
	}

	/**
	 * @param task the task
	 * @param timestamp initial execution
	 * @param repeat 0 = don't repeat, -1 = infinite repeat, x = x repeats
	 * @param interval = wait time between executions
	 */
	public void addRepeatingTask(Task task, long timestamp, int repeat, long interval) {
		addTask(new TaskContainer(task, timestamp, repeat, interval));
	}

	public void cancelTask(Task task) {
		synchronized (tasks) {
			for (int i = tasks.size() - 1; i >= 0; i--) {
				if (tasks.get(i).task == task) {
					tasks.remove(i);
					Logger.log(Logger.DEBUG, "scheduler cancel task: " + task);
				}
			}
		}

		wake();
	}

	private void addTask(TaskContainer task) {
		synchronized (tasks) {
			tasks.add(task);

			Logger.log(Logger.DEBUG, "scheduler add task: " + task.task);
			
			tasks.sort((a, b) -> Long.signum(a.timestamp - b.timestamp));
			wake();
		}
	}

	private void wake() {
		synchronized (lock) {
			earlyWake = true;
			lock.notify();
		}
	}

	public void stopScheduler() {
		running = false;
		wake();
	}

	@Override
	public void run() {

		taskLoop: while (running) {

			if (!tasks.isEmpty()) {

				long nextTimestamp = tasks.get(0).timestamp;
				int waitTime = (int) (nextTimestamp - OCSServer.getTimestamp());

				if (waitTime > 0) {
					synchronized (lock) {
						try {
							lock.wait(waitTime);
						} catch (InterruptedException e) {
							//thread was woken up
							e.printStackTrace(Logger.getErrorWriter());
						}
					}
				}

				if (earlyWake) {
					earlyWake = false;
					continue taskLoop;
				}

				TaskContainer nextTask = tasks.get(0);
				tasks.remove(0);

				synchronized (OCSServer.syncObject) {
					try {
						nextTask.task.run();
					} catch (Exception e) {
						e.printStackTrace(Logger.getErrorWriter());
					}

					Logger.log(Logger.DEBUG, "scheduler run: " + nextTask.task);
				}

				if (nextTask.repeat != 0) {
					nextTask.timestamp = OCSServer.getTimestamp() + nextTask.interval;
					nextTask.repeat--;
					if (nextTask.repeat < 0) nextTask.repeat = -1;

					addTask(nextTask);

				}
			} else {

				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						//thread was woken up
					}
				}

			}
		}
	}

}
