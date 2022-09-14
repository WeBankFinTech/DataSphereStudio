
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dss_datamodel_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_dictionary`;
CREATE TABLE `dss_datamodel_dictionary`  (
  `id` int(50) NOT NULL COMMENT '主键id',
  `code` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型code',
  `type` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `created_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `sort` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数模字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dss_datamodel_dictionary
-- ----------------------------
INSERT INTO `dss_datamodel_dictionary` VALUES (10000001, 'Snappy', 'COMPRESS', 'Snappy', '2021-10-09 15:41:23', '2021-10-09 15:55:44', 1);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000002, 'None', 'COMPRESS', '无', '2021-10-09 15:42:17', '2021-10-20 14:48:59', 2);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000003, 'ORC', 'FILE_STORAGE', 'orc', '2021-10-09 15:55:32', '2021-10-20 14:49:00', 1);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000004, 'Parquet', 'FILE_STORAGE', 'Parquet', '2021-10-09 15:55:32', '2021-10-20 14:49:02', 2);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000005, 'None', 'FILE_STORAGE', '无', '2021-10-09 15:55:32', '2021-10-20 14:49:04', 3);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000006, 'Once', 'LIFECYCLE', '一次', '2021-10-09 15:55:32', '2021-10-20 14:49:05', 1);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000007, 'OneDay', 'LIFECYCLE', '一天', '2021-10-09 15:55:32', '2021-10-20 14:49:06', 2);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000008, 'HalfMonth', 'LIFECYCLE', '半月', '2021-10-09 15:55:32', '2021-10-20 14:49:07', 3);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000009, 'OneMonth', 'LIFECYCLE', '一个月', '2021-10-09 15:55:32', '2021-10-20 14:49:08', 4);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000010, 'ThreeMonths', 'LIFECYCLE', '三个月', '2021-10-09 15:55:32', '2021-10-20 14:49:09', 5);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000011, 'SixMonths', 'LIFECYCLE', '六个月', '2021-10-09 15:55:32', '2021-10-20 14:49:10', 6);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000012, 'OneYear', 'LIFECYCLE', '一年', '2021-10-09 15:55:32', '2021-10-20 14:49:11', 7);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000013, 'ThreeYears', 'LIFECYCLE', '三年', '2021-10-09 15:55:32', '2021-10-20 14:49:12', 8);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000014, 'Hive', 'STORAGE_ENGINE', 'hive', '2021-10-09 15:55:32', '2021-10-20 14:49:19', 1);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000015, 'Mysql', 'STORAGE_ENGINE', 'mysql', '2021-10-09 15:55:32', '2021-10-20 14:49:20', 2);
INSERT INTO `dss_datamodel_dictionary` VALUES (10000016, 'ES', 'STORAGE_ENGINE', 'es', '2021-10-09 15:55:32', '2021-10-20 14:49:25', 3);

SET FOREIGN_KEY_CHECKS = 1;
