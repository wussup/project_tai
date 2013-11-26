-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Czas wygenerowania: 26 Lis 2013, 00:59
-- Wersja serwera: 5.5.32
-- Wersja PHP: 5.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Baza danych: `tai`
--
CREATE DATABASE IF NOT EXISTS `tai` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `tai`;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `settings`
--

CREATE TABLE IF NOT EXISTS `settings` (
  `settingId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dropbox_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`settingId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Zrzut danych tabeli `settings`
--

INSERT INTO `settings` (`settingId`, `dropbox_token`) VALUES
(1, 'G0jQojXYSYUAAAAAAAAAAX66jXUpnUeAYfR2nAqaFlI5wwyUYjTDas88VV0oW2Vt');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `surname` varchar(40) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `userType` int(10) unsigned NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`userId`, `name`, `login`, `password`, `surname`, `salt`, `userType`) VALUES
(3, 'Tarasik', 'taras', 'JrH0bj/3VRixNS2PbFS1AnFAxz303YD/LJeMjbqNVDM=', 'Melon', '5nibeheEn4u//GgxVGWRZA==', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
