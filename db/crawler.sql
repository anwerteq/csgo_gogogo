/*
 Navicat Premium Data Transfer

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 50709
 Source Host           : localhost:3306
 Source Schema         : crawler

 Target Server Type    : MySQL
 Target Server Version : 50709
 File Encoding         : 65001

 Date: 20/04/2020 20:08:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for proxy_api
-- ----------------------------
DROP TABLE IF EXISTS `proxy_api`;
CREATE TABLE `proxy_api`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip_api` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of proxy_api
-- ----------------------------
INSERT INTO `proxy_api` VALUES (1, 'http://dps.kdlapi.com/api/getdps/?orderid=948736843604899&num=10&pt=1&sep=1', 'txt');

-- ----------------------------
-- Table structure for proxy_config
-- ----------------------------
DROP TABLE IF EXISTS `proxy_config`;
CREATE TABLE `proxy_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `validate_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '验证网址',
  `validate_count` int(6) NOT NULL COMMENT '验证次数（默认为3次验证），如果代理IP存活时间较短 可该小验证次数',
  `delay_time` int(6) NOT NULL COMMENT '如果为0 则开启随机',
  `private_username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '私有接口-认证用户名',
  `private_password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '私有接口-认证用户密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of proxy_config
-- ----------------------------
INSERT INTO `proxy_config` VALUES (1, 'http://www.blyuan.com/', 1, 0, 'meet.parker', 'ca0ngogx');

-- ----------------------------
-- Table structure for proxyip
-- ----------------------------
DROP TABLE IF EXISTS `proxyip`;
CREATE TABLE `proxyip`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `anonymity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `available` bit(1) NOT NULL,
  `availableCount` int(11) NOT NULL,
  `availableRate` double NULL DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lastValidateTime` datetime(0) NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `port` int(11) NOT NULL,
  `requestTime` bigint(20) NOT NULL,
  `responseTime` bigint(20) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unAvailableCount` int(11) NOT NULL,
  `useTime` bigint(20) NOT NULL,
  `validateCount` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4080 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
