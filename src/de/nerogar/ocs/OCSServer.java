package de.nerogar.ocs;

import de.nerogar.ocs.chat.ChatRoomManager;
import de.nerogar.ocs.command.Command;
import de.nerogar.ocs.party.PartyContainer;
import de.nerogar.ocs.scramble.ScrambleProvider;
import de.nerogar.ocs.sql.*;
import de.nerogar.ocs.tasks.Scheduler;
import de.nerogar.ocs.user.User;
import de.nerogar.ocs.user.UserPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class OCSServer {

	public static final int EXIT_STOP = 0;
	public static final int EXIT_RESTART = 1;

	private int port;
	public static String name;

	public static final Object syncObject = new Object();

	public static ChatRoomManager chatRoomManager;
	public static PartyContainer  partyContainer;
	public static Userlist        userlist;
	public static UserPool        userPool;
	//public static OCSDatabase database;
	public static Database        databaseNew;
	public static DatabaseLog     databaseLog;
	public static DatabaseUser    databaseUser;
	public static DatabaseProfile databaseProfile;
	public static DatabaseParty   databaseParty;

	public static String onlinefilePath;

	public static boolean useCertFile;
	public static String  certPath;
	public static String  keyPath;

	public static Scheduler scheduler;

	public OCSServer() throws Exception {

		long startTime = OCSServer.getTimestamp();

		// Load Database connection info
		Properties properties = new Properties();
		Logger.log(Logger.INFO, "trying to load properties...");
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream("ocs.properties"));
			properties.load(stream);
			stream.close();

			name = properties.getProperty("name");

			onlinefilePath = properties.getProperty("onlinelist");
			port = Integer.parseInt(properties.getProperty("port"));

			useCertFile = Boolean.parseBoolean(properties.getProperty("useCertFile"));
			certPath = properties.getProperty("certPath");
			keyPath = properties.getProperty("keyPath");

			PrintStream logStream = new PrintStream(new FileOutputStream(new File(properties.getProperty("log")), true));
			PrintStream errorlogStream = new PrintStream(new FileOutputStream(new File(properties.getProperty("errorlog")), true));
			Logger.addStream(Logger.INFO, Logger.INFO, logStream);
			Logger.addStream(Logger.WARNING, errorlogStream);
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "could not find properties file. stopping.");
			throw new Exception("server could not start");
		}

		// Try database connection
		Logger.log(Logger.INFO, "trying to load database...");
		try {
			//database = new OCSDatabase(properties.getProperty("host"), properties.getProperty("username"), properties.getProperty("password"), properties.getProperty("database"), properties.getProperty("dbPrefix"));
			databaseNew = new Database(properties.getProperty("host"), properties.getProperty("username"), properties.getProperty("password"), properties.getProperty("database"), properties.getProperty("dbPrefix"));

			databaseLog = new DatabaseLog(databaseNew);
			databaseUser = new DatabaseUser(databaseNew);
			databaseProfile = new DatabaseProfile(databaseNew);
			databaseParty = new DatabaseParty(databaseNew);
		} catch (SQLException e) {
			Logger.log(Logger.ERROR, "database connection failed. stopping");
			throw new Exception("server could not start");
		}

		// init
		Config.init();

		// create objects
		userlist = new Userlist();
		chatRoomManager = new ChatRoomManager();
		partyContainer = new PartyContainer();
		userPool = new UserPool();
		scheduler = new Scheduler();

		scheduler.start();

		// init
		Command.init();
		Rank.init();
		User.initPermissions();
		ScrambleProvider.initScrambler();

		//init scheduler events
		scheduler.addRepeatingTask(OCSServer::saveAll, getTimestamp(), -1, get1SecondTimestamp() * 60);

		startTime = OCSServer.getTimestamp() - startTime;
		Logger.log(Logger.INFO, "startup in " + Time.asStringDelta(startTime));
	}

	public void run() throws Exception {
		SslContext sslCtx;

		if (useCertFile) {
			sslCtx = SslContextBuilder.forServer(new File(certPath), new File(keyPath)).build();
		} else {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		}

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			final ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(final SocketChannel ch) throws Exception {

							OCSFrameHandler ocsFrameHandler = new OCSFrameHandler();
							ch.pipeline().addLast(
									sslCtx.newHandler(ch.alloc()),
									new HttpServerCodec(),
									new HttpObjectAggregator(65536),
									//new WebSocketServerCompressionHandler(),
									new WebSocketServerProtocolHandler("/websocket"),
									ocsFrameHandler
							                     );
						}
					});

			final Channel ch = sb.bind(port).sync().channel();
			Logger.log(Logger.INFO, "OCS websocket server started at port " + port);

			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	/**
	 * flushes users and outdated data
	 */
	public static void flushUsers() {
		synchronized (syncObject) {
			if (userlist.flushUsers()) saveOnlineFile(true);
			chatRoomManager.flushUserlists();
			partyContainer.flushUsers();
		}
	}

	public static long saveAll() {

		long t1 = OCSServer.getTimestamp();
		userlist.saveAll();
		userPool.saveAll();
		chatRoomManager.saveAll();
		User.savePermissions();
		Config.saveAll();
		Command.saveAll();
		long t2 = OCSServer.getTimestamp();

		long time = t2 - t1;

		Logger.log(Logger.DEBUG, "saved all: " + Time.asStringDelta(time));
		return time;
	}

	@SuppressWarnings("unchecked")
	public static void saveOnlineFile(boolean online) {
		JSONObject onlineObject = new JSONObject();

		JSONArray usersArray = new JSONArray();
		for (User user : userlist.getUsers()) {
			JSONObject userObject = new JSONObject();
			userObject.put("username", user.getUsername());
			userObject.put("namecolor", user.getNameColor());
			usersArray.add(userObject);
		}

		onlineObject.put("users", usersArray);
		onlineObject.put("online", online);

		String saveString = onlineObject.toJSONString();

		try {

			File onlineFile = new File(onlinefilePath);
			if (onlineFile.exists()) onlineFile.delete();
			onlineFile.createNewFile();

			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(onlinefilePath), "UTF-8"));

			out.print(saveString);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace(Logger.getErrorStream());
		}

	}

	public static long get1SecondTimestamp() {
		return 1000L;
	}

	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {
		Logger.setPrintTimestamp(true);

		int logLevel = Logger.DEBUG;

		if (args.length > 0) {
			logLevel = Integer.parseInt(args[0]);
		}

		Logger.addStream(logLevel, Logger.WARNING, System.out);
		Logger.addStream(Logger.ERROR, System.err);

		try {
			OCSServer server = new OCSServer();
			server.run();
		} catch (Exception e) {
			e.printStackTrace(Logger.getErrorStream());
			return;
		}
	}

}
