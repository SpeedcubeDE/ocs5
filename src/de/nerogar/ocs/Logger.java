package de.nerogar.ocs;

import java.io.*;
import java.util.*;

public class Logger {

	/**
	 * Information to find bugs during development.
	 */
	public static final int DEBUG = 0;
	private static final PrintWriter DEBUG_WRITER = (new LogWriter(DEBUG));

	/**
	 * More important than debug information. 
	 */
	public static final int INFO = 1;
	private static final PrintWriter INFO_WRITER = (new LogWriter(INFO));

	/**
	 * Warnings about unexpected behavior.
	 */
	public static final int WARNING = 2;
	private static final PrintWriter WARNING_WRITER = (new LogWriter(WARNING));

	/**
	 * Problems that can cause a crash.
	 */
	public static final int ERROR = 4;
	private static final PrintWriter ERROR_WRITER = (new LogWriter(ERROR));

	private static List<LogOutStream> logStreams;

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream 
	 * @param stream the printStream for message output
	 */
	public static void addStream(int minLogLevel, PrintStream stream) {
		logStreams.add(new LogOutStream(minLogLevel, ERROR, stream));
	}

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream
	 * @param maxLogLevel the maximum loglevel to print on this stream
	 * @param stream the printStream for message output
	 */
	public static void addStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
		logStreams.add(new LogOutStream(minLogLevel, maxLogLevel, stream));
	}

	/**
	 * removes any stream that is equal to the specified stream.
	 * @param stream the stream to remove
	 * @return true, if a stream was removed, false otherwise
	 */
	public static boolean removeStream(PrintStream stream) {
		return logStreams.removeIf((a) -> a.stream.equals(stream));
	}

	/**
	 * returns a {@link PrintWriter} for easy debug logging
	 * @return the debug writer
	 */
	public static PrintWriter getDebugWriter() {
		return DEBUG_WRITER;
	}

	/**
	 * returns a {@link PrintWriter} for easy info logging
	 * @return the info writer
	 */
	public static PrintWriter getInfoWriter() {
		return INFO_WRITER;
	}

	/**
	 * returns a {@link PrintWriter} for easy warning logging
	 * @return the warning writer
	 */
	public static PrintWriter getWarningWriter() {
		return WARNING_WRITER;
	}

	/**
	 * returns a {@link PrintWriter} for easy error logging
	 * @return the error writer
	 */
	public static PrintWriter getErrorWriter() {
		return ERROR_WRITER;
	}

	/**
	 * prints the message to all attached streams with the correct log level
	 * 
	 * @param logLevel the loglevel for this message
	 * @param msg the message as a String
	 */
	public static void log(int logLevel, String msg) {
		logStreams.forEach((logStream) -> {
			if (logLevel >= logStream.minLogLevel && logLevel <= logStream.maxLogLevel) {
				print(logStream.stream, logLevel, msg);
			}
		});
	}

	/**
	 * calls </code>.toString()</code> on msg and logs it
	 * 
	 * @param logLevel the loglevel for this message
	 * @param msg the Object to log
	 */
	public static void log(int logLevel, Object msg) {
		logStreams.forEach((logStream) -> {
			if (logLevel >= logStream.minLogLevel && logLevel <= logStream.maxLogLevel) {
				if (msg instanceof Object[]) {
					print(logStream.stream, logLevel, Arrays.deepToString((Object[]) msg));
				} else {
					print(logStream.stream, logLevel, msg.toString());
				}
			}
		});
	}

	private static void print(PrintStream stream, int logLevel, String msg) {
		stream.println("[" + logLevel + "] " + msg);
	}

	static {
		logStreams = new ArrayList<LogOutStream>();
	}

	/**
	 * A class to store the output streams
	 */
	private static class LogOutStream {
		public int minLogLevel;
		public int maxLogLevel;
		public PrintStream stream;

		public LogOutStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
			this.minLogLevel = minLogLevel;
			this.maxLogLevel = maxLogLevel;
			this.stream = stream;
		}
	}

	/**
	 * a writer that does nothing
	 */
	private static class NullWriter extends Writer {

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			//do nothing
		}

		@Override
		public void flush() throws IOException {
			//do nothing
		}

		@Override
		public void close() throws IOException {
			//do nothing
		}

	}

	/**
	 * A class to write to for writer based interfaces
	 */
	private static class LogWriter extends PrintWriter {

		private int logLevel;

		public LogWriter(int logLevel) {
			super(new NullWriter());
			this.logLevel = logLevel;
		}

		@Override
		public void println() {
			log(logLevel, "\n");
		}

		@Override
		public void print(boolean b) {
			log(logLevel, String.valueOf(b));
		}

		@Override
		public void print(char c) {
			log(logLevel, String.valueOf(c));
		}

		@Override
		public void print(int i) {
			log(logLevel, String.valueOf(i));
		}

		@Override
		public void print(long l) {
			log(logLevel, String.valueOf(l));
		}

		@Override
		public void print(float f) {
			log(logLevel, String.valueOf(f));
		}

		@Override
		public void print(double d) {
			log(logLevel, String.valueOf(d));
		}

		@Override
		public void print(char[] s) {
			log(logLevel, String.valueOf(s));
		}

		@Override
		public void print(String s) {
			log(logLevel, s);
		}

		@Override
		public void print(Object obj) {
			log(logLevel, obj);
		}

		@Override
		public void println(boolean b) {
			print(b);
		}

		@Override
		public void println(char c) {
			print(c);
		}

		@Override
		public void println(int i) {
			print(i);
		}

		@Override
		public void println(long l) {
			println(l);
		}

		@Override
		public void println(float f) {
			print(f);
		}

		@Override
		public void println(double d) {
			print(d);
		}

		@Override
		public void println(char[] s) {
			print(s);
		}

		@Override
		public void println(String s) {
			print(s);
		}

		@Override
		public void println(Object obj) {
			print(obj);
		}

		@Override
		public PrintWriter printf(String format, Object... args) {
			return format(format, args);
		}

		@Override
		public PrintWriter printf(Locale l, String format, Object... args) {
			return format(l, format, args);
		}

		@Override
		public PrintWriter append(CharSequence csq) {
			write(csq.toString());
			return this;
		}

		@Override
		public PrintWriter format(String format, Object... args) {
			log(logLevel, String.format(format, args));
			return this;
		}

		@Override
		public PrintWriter format(Locale l, String format, Object... args) {
			log(logLevel, String.format(l, format, args));
			return this;
		}

		@Override
		public PrintWriter append(CharSequence csq, int start, int end) {
			write(csq.subSequence(start, end).toString());
			return this;
		}

		@Override
		public PrintWriter append(char c) {
			write(c);
			return this;
		}

		@Override
		public void write(int c) {
			log(c, String.valueOf(c));
		}

		@Override
		public void write(char[] buf) {
			log(logLevel, String.valueOf(buf));
		}

		@Override
		public void write(String s) {
			log(logLevel, s);
		}

		@Override
		public void write(char[] cbuf, int off, int len) {
			log(logLevel, String.valueOf(cbuf, off, len));
		}

		@Override
		public void write(String s, int off, int len) {
			log(logLevel, s.substring(off, off + len));
		}

		@Override
		public void flush() {
			//do nothing
		}

		@Override
		public void close() {
			//do nothing
		}

	}

}
