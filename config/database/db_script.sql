USE [adoration]
GO
/****** Object:  Table [dbo].[translator]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP TABLE [dbo].[translator]
GO
/****** Object:  Table [dbo].[social]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP TABLE [dbo].[social]
GO
/****** Object:  Table [dbo].[person]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP TABLE [dbo].[person]
GO
/****** Object:  Table [dbo].[link]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP TABLE [dbo].[link]
GO
/****** Object:  Table [dbo].[auditTrail]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP TABLE [dbo].[auditTrail]
GO
USE [adoration]
GO
/****** Object:  Sequence [dbo].[AdorationUniqueNumber]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP SEQUENCE [dbo].[AdorationUniqueNumber]
GO
/****** Object:  User [adorApp]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP USER [adorApp]
GO
USE [master]
GO
/****** Object:  Database [adoration]    Script Date: 2019. 09. 29. 20:15:06 ******/
DROP DATABASE [adoration]
GO
/****** Object:  Database [adoration]    Script Date: 2019. 09. 29. 20:15:06 ******/
CREATE DATABASE [adoration]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'adoration', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\adoration.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'adoration_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\adoration_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [adoration] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [adoration].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [adoration] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [adoration] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [adoration] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [adoration] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [adoration] SET ARITHABORT OFF 
GO
ALTER DATABASE [adoration] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [adoration] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [adoration] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [adoration] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [adoration] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [adoration] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [adoration] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [adoration] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [adoration] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [adoration] SET  DISABLE_BROKER 
GO
ALTER DATABASE [adoration] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [adoration] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [adoration] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [adoration] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [adoration] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [adoration] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [adoration] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [adoration] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [adoration] SET  MULTI_USER 
GO
ALTER DATABASE [adoration] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [adoration] SET DB_CHAINING OFF 
GO
ALTER DATABASE [adoration] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [adoration] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [adoration] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [adoration] SET QUERY_STORE = OFF
GO
USE [adoration]
GO
/****** Object:  User [adorApp]    Script Date: 2019. 09. 29. 20:15:06 ******/
CREATE USER [adorApp] FOR LOGIN [adorApp] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [adorApp]
GO
USE [adoration]
GO
/****** Object:  Sequence [dbo].[AdorationUniqueNumber]    Script Date: 2019. 09. 29. 20:15:06 ******/
CREATE SEQUENCE [dbo].[AdorationUniqueNumber] 
 AS [bigint]
 START WITH 1000
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
/****** Object:  Table [dbo].[auditTrail]    Script Date: 2019. 09. 29. 20:15:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[auditTrail](
	[id] [bigint] NOT NULL,
	[refId] [bigint] NOT NULL,
	[atWhen] [ntext] NOT NULL,
	[byWho] [ntext] NOT NULL,
	[actvityType] [ntext] NOT NULL,
	[description] [ntext] NOT NULL,
	[data] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[link]    Script Date: 2019. 09. 29. 20:15:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[link](
	[id] [bigint] NOT NULL,
	[personId] [bigint] NOT NULL,
	[hourId] [int] NOT NULL,
	[priority] [int] NOT NULL,
	[adminComment] [ntext] NULL,
	[publicComment] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[person]    Script Date: 2019. 09. 29. 20:15:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[person](
	[id] [bigint] NOT NULL,
	[name] [ntext] NOT NULL,
	[adorationStatus] [int] NOT NULL,
	[webStatus] [int] NOT NULL,
	[mobile] [ntext] NULL,
	[mobileVisible] [bit] NOT NULL,
	[email] [ntext] NULL,
	[emailVisible] [bit] NOT NULL,
	[adminComment] [ntext] NULL,
	[dhcSigned] [bit] NOT NULL,
	[dhcSignedDate] [date] NULL,
	[coordinatorComment] [ntext] NULL,
	[visibleComment] [ntext] NULL,
	[languageCode] [ntext] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[social]    Script Date: 2019. 09. 29. 20:15:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[social](
	[id] [bigint] NOT NULL,
	[personId] [bigint] NULL,
	[gEmail] [ntext] NULL,
	[gUserName] [ntext] NULL,
	[gUserId] [ntext] NULL,
	[gUserPicture] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[translator]    Script Date: 2019. 09. 29. 20:15:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[translator](
	[textId] [ntext] NOT NULL,
	[languageCode] [ntext] NOT NULL,
	[text] [ntext] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
INSERT [dbo].[link] ([id], [personId], [hourId], [priority], [adminComment], [publicComment]) VALUES (1000, 0, 0, 0, N'', N'')
INSERT [dbo].[link] ([id], [personId], [hourId], [priority], [adminComment], [publicComment]) VALUES (1001, 0, 0, 1, N'', N'')
INSERT [dbo].[link] ([id], [personId], [hourId], [priority], [adminComment], [publicComment]) VALUES (1002, 0, 1, 0, N'', N'')
INSERT [dbo].[link] ([id], [personId], [hourId], [priority], [adminComment], [publicComment]) VALUES (1003, 0, 107, 0, N'', N'')
INSERT [dbo].[link] ([id], [personId], [hourId], [priority], [adminComment], [publicComment]) VALUES (1004, 0, 107, 1, N'', N'')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'sunday', N'hu', N'vasárnap')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'monday', N'hu', N'hétfő')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'tuesday', N'hu', N'kedd')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'wednesday', N'hu', N'szerda')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'thursday', N'hu', N'csütörtök')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'friday', N'hu', N'péntek')
INSERT [dbo].[translator] ([textId], [languageCode], [text]) VALUES (N'saturday', N'hu', N'szombat')
USE [master]
GO
ALTER DATABASE [adoration] SET  READ_WRITE 
GO
