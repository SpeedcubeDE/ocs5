package de.nerogar.ocs.command;

import java.util.*;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.sql.DatabaseCommand;
import de.nerogar.ocs.user.User;

public abstract class Command implements Comparable<Command> {

	private static DatabaseCommand databaseCommands;

	private static HashMap<String, Command> commands;
	private static Command[] commandArray;

	private int minPower;

	public Command() {
		minPower = -1;
	}

	public abstract boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom);

	protected boolean executeOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, String targetUsername) {
		User targetUser = OCSServer.userPool.getUser(OCSServer.userPool.getUserIDByName(targetUsername));

		if (targetUser == null) {
			user.sendMessage(chatRoom, OCSStrings.getString("command.userNotFound", targetUsername));
			return true;
		} else if (!checkUserPower(user, targetUser)) {
			user.sendMessage(chatRoom, OCSStrings.getString("command.insufficientPower"));
			return true;
		} else {
			return onExecuteOnUser(arguments, argumentString, user, chatRoom, targetUser);
		}
	}

	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		return false;
	}

	public int getMinPower() {
		return minPower >= 0 ? minPower : User.permissions.get(User.MAX_POWER);
	}

	public int getMinPowerReal() {
		return minPower;
	}

	public void setMinPower(int minPower) {
		this.minPower = minPower;
	}

	public abstract String getName();

	public abstract String getHelp();

	public boolean checkUserPower(User user, User targetUser) {
		return user.getPower() > targetUser.getPower() || user.hasPermission(User.MAX_POWER);
	}

	public static void init() {
		databaseCommands = new DatabaseCommand(OCSServer.databaseNew);
		databaseCommands.loadCommands(commands);
	}

	public static Command getCommand(String name) {
		return commands.get(name);
	}

	public static Command[] getCommands() {
		return commandArray;
	}

	public String getArgumentString(String argumentString, int firstIndex) {
		String[] split = argumentString.split(" ", firstIndex + 1);
		if (split.length == firstIndex + 1) {
			return split[firstIndex];
		} else {
			return "";
		}

	}

	@Override
	public int compareTo(Command c) {
		return getName().compareTo(c.getName());
	}

	private static void registerCommand(Command command) {
		commands.put(command.getName().toLowerCase(), command);
	}

	public static void saveAll() {
		for (Command command : commandArray) {
			databaseCommands.saveCommand(command);
		}
	}

	static {
		commands = new HashMap<String, Command>();

		//chat
		registerCommand(new HelpCommand());
		registerCommand(new MeCommand());
		registerCommand(new StatusCommand());
		registerCommand(new NameColorCommand());

		//mod
		registerCommand(new KickCommand());
		registerCommand(new SetRankCommand());
		registerCommand(new AlertCommand());
		registerCommand(new ConfigCommand());
		registerCommand(new MuteCommand());
		registerCommand(new BanCommand());
		registerCommand(new SoundCommand());
		registerCommand(new LogCommand());

		//dev
		registerCommand(new SetPowerCommand());
		registerCommand(new SaveAllCommand());
		registerCommand(new StopCommand());
		registerCommand(new RestartCommand());
		registerCommand(new PermissionCommand());
		registerCommand(new CommandCommand());
		registerCommand(new ProfileCommand());
		registerCommand(new UserpoolCommand());
		registerCommand(new HistoryCommand());

		//build commandList
		commandArray = new Command[commands.size()];
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.addAll(commands.values());

		for (int i = 0; i < commandArray.length; i++) {
			commandArray[i] = commandList.get(i);
		}

		Arrays.sort(commandArray);

	}

}
