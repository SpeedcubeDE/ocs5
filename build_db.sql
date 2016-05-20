-- Creates a default database for the ocs5. the tables will be prefixed with ocs5_ by default.

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE `ocs5_chat` (
  `id` int AUTO_INCREMENT,
  `userID` int,
  `chatroomID` int,
  `msg` text CHARACTER SET utf8,
  `time` bigint,
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_chatroom` (
  `id` int AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8,
  `ownerID` int,
  `minPower` int,
  `password` varchar(128),
  `salt` varchar(128),
  `closed` int(1),
  PRIMARY KEY (`id`)
);

INSERT INTO `ocs5_chatroom` (`id`, `name`, `ownerID`, `minPower`, `password`, `salt`, `closed`) VALUES
(1, 'main', -1, 0, '', '', 0),
(2, 'staff', -1, 40, '', '', 0);


CREATE TABLE `ocs5_command` (
  `name` varchar(16),
  `minPower` int,
  PRIMARY KEY (`name`)
);

INSERT INTO `ocs5_command` (`name`, `minPower`) VALUES
('alert', 40),
('ban', 40),
('command', -1),
('config', 80),
('help', 0),
('kick', 40),
('log', 40),
('me', 0),
('mute', 40),
('nameColor', 0),
('permission', -1),
('profile', 40),
('saveAll', -1),
('setPower', -1),
('setRank', 40),
('sound', 40),
('status', 0),
('stop', -1),
('userpool', -1);


CREATE TABLE `ocs5_config` (
  `name` varchar(32),
  `value` int,
  PRIMARY KEY (`name`)
);

INSERT INTO `ocs5_config` (`name`, `value`) VALUES
('loginMsgLimit', 50),
('logoutDelay', 20000),
('maxCreatedParties', 1),
('maxPartyRounds', 100),
('maxScrambleCacheSize', 10),
('maxStatusLength', 255),
('maxUserPoolSize', 200),
('minChatMsgDelay', 1000),
('timeoutLimit', 120);


CREATE TABLE `ocs5_log_command` (
  `id` int AUTO_INCREMENT,
  `userID` int,
  `time` bigint,
  `command` varchar(32) CHARACTER SET utf8,
  `arguments` varchar(128) CHARACTER SET utf8,
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_log_login` (
  `id` int AUTO_INCREMENT,
  `userID` int,
  `time` bigint,
  `ip` varchar(64),
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_party` (
  `id` int AUTO_INCREMENT,
  `ownerID` int,
  `cubeType` varchar(32),
  `rounds` int,
  `startTime` bigint,
  `mode` int(2),
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_partyRound` (
  `id` int AUTO_INCREMENT,
  `partyID` int,
  `round` int,
  `scramble` text,
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_partytime` (
  `id` int AUTO_INCREMENT,
  `partyID` int,
  `userID` int,
  `round` int,
  `time` int,
  PRIMARY KEY (`id`)
);


CREATE TABLE `ocs5_permission` (
  `name` varchar(32),
  `minPower` int(11),
  PRIMARY KEY (`name`)
);

INSERT INTO `ocs5_permission` (`name`, `minPower`) VALUES
('createChatRoom', 20),
('createParty', 10),
('createPartyWithMode', 40),
('createUnlimitedParties', 20),
('editAllChatRooms', 40),
('editAllParties', 40),
('maxPower', 100),
('moderateUser', 40),
('seeAllRanks', 40),
('spamAllowed', 40);


CREATE TABLE `ocs5_rank` (
  `power` int,
  `name` varchar(8),
  `show` int(1),
  `short` varchar(3),
  PRIMARY KEY (`power`)
);

INSERT INTO `ocs5_rank` (`power`, `name`, `show`, `short`) VALUES
(-1, 'banned', 1, 'B'),
(10, 'user', 0, 'U'),
(20, 'trusted', 0, 'U+'),
(40, 'mod', 1, 'M'),
(80, 'admin', 1, 'A'),
(100, 'dev', 1, 'Dev');


CREATE TABLE `ocs5_user` (
  `id` int AUTO_INCREMENT,
  `name` varchar(16) CHARACTER SET utf8,
  `loginToken` varchar(32) DEFAULT '',
  `password` varchar(120),
  `power` int DEFAULT '10',
  `status` varchar(255) CHARACTER SET utf8,
  `nameColor` varchar(6) DEFAULT 'FFFFFF',
  `registerDate` bigint(20),
  `muteTime` bigint(20) DEFAULT '-1',
  `banReason` varchar(128) CHARACTER SET utf8 DEFAULT '',
  `onlineTime` bigint(20) DEFAULT '0',
  `loginCount` int DEFAULT '0',
  `chatMsgCount` int DEFAULT '0',
  `wcaID` varchar(16) DEFAULT '',
  `forumID` int DEFAULT '-1',
  `registerToken` varchar(32),
  `email` varchar(128) CHARACTER SET utf8,
  `resetToken` varchar(32) DEFAULT '',
  PRIMARY KEY (`id`)
) CHARSET=utf8 COLLATE=utf8_bin;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
