package de.nerogar.ocs;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.net.InetSocketAddress;

import org.json.simple.JSONObject;

import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.parse.ParseLogin;
import de.nerogar.ocs.tasks.Task;
import de.nerogar.ocs.user.User;

public class OCSFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private User user;
	private boolean auth;

	public ChannelHandlerContext ctx;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

		synchronized (OCSServer.syncObject) {

			if (!auth) {
				Logger.log(Logger.DEBUG, ctx.name() + " -> " + msg.text());

				String authToken = ParseLogin.parseLogin(msg.text());
				if (authToken != null && !authToken.equals("")) {
					user = OCSServer.userPool.getUser(OCSServer.userPool.getUserIDByToken(authToken));
					if (user != null && user.getPower() < 0) user = null;
				}

				if (user != null) {
					sendLogin();
					user.handleLogin(this);

					auth = true;
				} else {
					sendLogin();
					send(new Alert(Alert.ERROR, false, OCSStrings.getString("login.fail")).send(null));
				}

			} else {
				Logger.log(Logger.DEBUG, user.getUsername() + " -> " + msg.text());

				try {

					user.getDataParser().parse(msg.text());
				} catch (Exception e) {
					new Alert(Alert.ERROR, false, OCSStrings.getString("system.internalError")).sendTo(user);
					e.printStackTrace(Logger.getErrorStream());
				}
			}
		}
		if (user != null) user.refreshTimeout();

		OCSServer.flushUsers();
		
	}

	@SuppressWarnings("unchecked")
	private void sendLogin() {
		//userinfo
		JSONObject loginJSON = new JSONObject();
		loginJSON.put("type", "login");

		if (user != null) {
			loginJSON.put("login", true);
			loginJSON.put("name", user.getUsername());
		} else {
			loginJSON.put("login", false);
			loginJSON.put("name", "");
		}

		send(loginJSON.toJSONString());//login packet
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (user != null) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("system.internalError")).sendTo(user);
		} else {
			ctx.writeAndFlush(new TextWebSocketFrame(new Alert(Alert.ERROR, false, OCSStrings.getString("system.internalError")).send(null)));
		}

		cause.printStackTrace(Logger.getErrorStream());

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		synchronized (OCSServer.syncObject) {
			if (user != null) {

				if (user.isConnected()) {
					user.setDisconnectedFrom(this);
				}

				Task logoutTask = () -> {
					if (!user.isConnected()) {
						user.logout(true);
					}

					OCSServer.flushUsers(); //TODO remove
				};

				OCSServer.scheduler.addTask(logoutTask, OCSServer.getTimestamp() + Config.getValue(Config.LOGOUT_DELAY));

				user.setLogoutTask(logoutTask);
			}

			ctx.disconnect();
			OCSServer.flushUsers();
		}

	}

	public String getIP() {
		return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
	}

	public void send(String text) {
		ctx.writeAndFlush(new TextWebSocketFrame(text));

		if (user == null) {
			Logger.log(Logger.DEBUG, ctx.name() + " <- " + text);
		} else {
			Logger.log(Logger.DEBUG, user.getUsername() + " <- " + text);
		}

	}
}
