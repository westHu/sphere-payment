CREATE TABLE `ext_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_type` varchar(50) NOT NULL COMMENT '配置类型',
  `name` varchar(100) NOT NULL COMMENT '键',
  `module` varchar(50) NOT NULL COMMENT '模块',
  `value` text COMMENT '值',
  `attribute` text COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_module` (`module`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扩展配置表';



CREATE TABLE `merchant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `merchant_type` tinyint(4) NOT NULL COMMENT '商户性质(1:个人 2:企业 3:机构)',
  `merchant_level` tinyint(4) NOT NULL COMMENT '商户等级',
  `brand_name` varchar(100) DEFAULT NULL COMMENT '品牌名称',
  `api_mode` tinyint(4) NOT NULL COMMENT 'API对接模式(1:API 2:收银台 3:都有)',
  `area_list` json DEFAULT NULL COMMENT '地区代码',
  `digital_list` json DEFAULT NULL COMMENT '数字货币支持',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '商户状态',
  `attribute` text COMMENT '扩展属性',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`),
  KEY `idx_merchant_type` (`merchant_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';


CREATE TABLE `merchant_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `merchant_secret` varchar(128) NOT NULL COMMENT '商户秘钥',
  `finish_payment_url` varchar(255) DEFAULT NULL COMMENT '商户收款完成回调地址',
  `finish_payout_url` varchar(255) DEFAULT NULL COMMENT '商户出款完成回调地址',
  `finish_refund_url` varchar(255) DEFAULT NULL COMMENT '商户退款完成回调地址',
  `finish_redirect_url` varchar(255) DEFAULT NULL COMMENT '商户支付完成跳转地址',
  `public_key` text DEFAULT NULL COMMENT '商户公钥',
  `ip_white_list` text DEFAULT NULL COMMENT '商户ip白名单',
  `payment_link_setting` json DEFAULT NULL COMMENT '支付链接设置',
  `receipt_setting` json DEFAULT NULL COMMENT '凭证设置',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户基本配置表';


CREATE TABLE `merchant_operator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `last_password_update_time` datetime DEFAULT NULL COMMENT '最近密码更新时间',
  `last_trade_password_update_time` datetime DEFAULT NULL COMMENT '最近交易密码更新时间',
  `google_code` varchar(32) DEFAULT NULL COMMENT '验证码',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:启用 0:禁用)',
  `attribute` text DEFAULT NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户操作员表';

CREATE TABLE `merchant_payment_channel_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `single_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `single_rate` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '费率',
  `min_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '最少交易费用',
  `amount_limit_min` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最小',
  `amount_limit_max` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最大',
  `settle_type` varchar(50) DEFAULT NULL COMMENT '结算配置',
  `settle_time` varchar(50) DEFAULT NULL COMMENT '结算时间',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:启用 0:禁用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_channel` (`merchant_id`, `payment_method`, `channel_code`),
  KEY `idx_merchant_name` (`merchant_name`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户支付渠道配置表';


CREATE TABLE `merchant_payment_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `review` tinyint(1) NOT NULL DEFAULT '0' COMMENT '收款人工审核开关',
  `deduction_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '扣款方式：0内扣 1外扣',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户支付配置表';


CREATE TABLE `merchant_payout_channel_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式编码',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `single_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `single_rate` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '费率',
  `min_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '最少商户手续费',
  `amount_limit_min` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最小',
  `amount_limit_max` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最大',
  `settle_type` varchar(50) DEFAULT NULL COMMENT '结算配置',
  `settle_time` varchar(50) DEFAULT NULL COMMENT '结算时间',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_channel` (`merchant_id`, `payment_method`, `channel_code`),
  KEY `idx_merchant_name` (`merchant_name`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户代付渠道配置表';

CREATE TABLE `merchant_payout_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `review` tinyint(1) NOT NULL DEFAULT '0' COMMENT '代付人工审核开关',
  `deduction_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '扣款方式：0内扣 1外扣',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户代付配置表';

CREATE TABLE `merchant_withdraw_channel_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式编码',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `single_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `single_rate` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '费率',
  `min_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '最少交易费用',
  `amount_limit_min` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最小',
  `amount_limit_max` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单笔最大',
  `settle_type` varchar(50) DEFAULT NULL COMMENT '结算配置',
  `settle_time` varchar(50) DEFAULT NULL COMMENT '结算时间',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_channel` (`merchant_id`, `payment_method`, `channel_code`),
  KEY `idx_merchant_name` (`merchant_name`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户提现渠道配置表';


CREATE TABLE `merchant_withdraw_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(64) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `review` tinyint(1) NOT NULL DEFAULT '0' COMMENT '提现人工审核开关',
  `deduction_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '扣款方式：0内扣 1外扣',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户提现配置表';


CREATE TABLE `payment_callback_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `trade_no` varchar(64) NOT NULL COMMENT '交易流水号',
  `channel_order_no` varchar(64) NOT NULL COMMENT '渠道流水号',
  `message` text NOT NULL COMMENT '消息体',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_trade_no` (`trade_no`),
  KEY `idx_channel_order_no` (`channel_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付回调消息表';


CREATE TABLE `payment_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `channel_type` varchar(50) NOT NULL COMMENT '渠道类型(银行、三方、四方等)',
  `return_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '返回模式(0:收银台、1:收款号、4:都有)',
  `url` varchar(255) DEFAULT NULL COMMENT 'API接口地址',
  `license` text DEFAULT NULL COMMENT '渠道授权信息',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '渠道状态',
  `attribute` text DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_code` (`channel_code`),
  KEY `idx_channel_type` (`channel_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道表';

CREATE TABLE `payment_channel_balance_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '余额',
  `available_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '可用余额',
  `pending_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '处理中金额',
  `attribute` text DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_channel_currency` (`channel_code`, `currency`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道余额统计表';


CREATE TABLE `payment_channel_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` varchar(50) NOT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式编码',
  `payment_direction` int(11) NOT NULL COMMENT '交易方向',
  `payment_attribute` text DEFAULT NULL COMMENT '支付属性',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `settle_type` varchar(20) DEFAULT NULL COMMENT '结算周期',
  `settle_time` varchar(20) DEFAULT NULL COMMENT '结算时间',
  `single_fee` decimal(20,8) DEFAULT '0.00000000' COMMENT '单笔固定费用',
  `single_rate` decimal(10,6) DEFAULT '0.000000' COMMENT '单笔费率',
  `amount_limit_min` decimal(20,8) DEFAULT '0.00000000' COMMENT '单笔最小金额',
  `amount_limit_max` decimal(20,8) DEFAULT '0.00000000' COMMENT '单笔最大金额',
  `times_limit_min` int(11) DEFAULT '0' COMMENT '每日最小交易次数',
  `times_limit_max` int(11) DEFAULT '0' COMMENT '每日最大交易次数',
  `success_rate` decimal(10,6) DEFAULT '1.000000' COMMENT '支付成功率',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '渠道方法状态',
  `attribute` text DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_payment` (`channel_code`, `payment_method`, `payment_direction`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_payment_direction` (`payment_direction`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道方法表';


CREATE TABLE `payment_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式编码',
  `payment_direction` int(11) NOT NULL COMMENT '支付方向(二进制状态法)',
  `payment_name` varchar(100) NOT NULL COMMENT '支付方式名称',
  `payment_type` int(11) NOT NULL COMMENT '支付方式类型(信用卡、虚拟号、二维码等)',
  `payment_icon` varchar(255) DEFAULT NULL COMMENT '支付方式图标',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付方式状态',
  `area` int(11) DEFAULT NULL COMMENT '支持的地区',
  `attribute` text DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_method` (`payment_method`),
  KEY `idx_payment_type` (`payment_type`),
  KEY `idx_payment_direction` (`payment_direction`),
  KEY `idx_status` (`status`),
  KEY `idx_area` (`area`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式表';

CREATE TABLE `sandbox_merchant_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `merchant_secret` varchar(255) NOT NULL COMMENT '商户秘钥',
  `finish_payment_url` varchar(255) DEFAULT NULL COMMENT '商户收款完成回调地址',
  `finish_payout_url` varchar(255) DEFAULT NULL COMMENT '商户出款完成回调地址',
  `finish_refund_url` varchar(255) DEFAULT NULL COMMENT '商户退款完成回调地址',
  `finish_redirect_url` varchar(255) DEFAULT NULL COMMENT '商户支付完成跳转地址',
  `public_key` varchar(400) DEFAULT NULL COMMENT '商户公钥',
  `ip_white_list` text DEFAULT NULL COMMENT '商户ip白名单',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_id` (`merchant_id`),
  KEY `idx_merchant_name` (`merchant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户沙箱基本配置表';

CREATE TABLE `sandbox_trade_payment_link_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `link_no` varchar(64) NOT NULL COMMENT '支付链接单号',
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '金额',
  `link_status` int(11) NOT NULL COMMENT '状态',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `payment_link` varchar(255) NOT NULL COMMENT '支付链接',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_link_no` (`link_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_link_status` (`link_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='沙箱支付链接订单表';




CREATE TABLE `sandbox_trade_payment_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '收款单号',
  `order_no` varchar(64) NOT NULL COMMENT '外部订单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '交易目的',
  `product_detail` varchar(255) DEFAULT NULL COMMENT '商户详情',

  -- 支付渠道信息
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) DEFAULT NULL COMMENT '商户账户号',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '收款金额',
  `merchant_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) DEFAULT '0.00000000' COMMENT '到账金额',
  `channel_cost` decimal(20,8) DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '平台利润',

  -- 交易参与方信息
  `payer_info` text DEFAULT NULL COMMENT '付款方信息',
  `receiver_info` text DEFAULT NULL COMMENT '收款方信息',

  -- 交易状态信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` varchar(255) DEFAULT NULL COMMENT '交易结果',
  `payment_status` int(11) DEFAULT NULL COMMENT '支付状态',
  `payment_result` varchar(255) DEFAULT NULL COMMENT '支付结果',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',

  -- 结算信息
  `settle_status` int(11) DEFAULT NULL COMMENT '结算状态',
  `settle_result` varchar(255) DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 通知信息
  `call_back_status` int(11) DEFAULT NULL COMMENT '回调状态',

  -- 其他信息
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `source` int(11) DEFAULT NULL COMMENT '来源',
  `version` int(11) DEFAULT '1' COMMENT '版本号',
  `attribute` text DEFAULT NULL COMMENT '备注',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='沙箱收款订单表';



CREATE TABLE `sandbox_trade_payout_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '代付单号',
  `order_no` varchar(64) NOT NULL COMMENT '外部单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '交易目的',
  `product_detail` varchar(255) DEFAULT NULL COMMENT '商品详情',

  -- 支付渠道信息
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',
  `bank_code` varchar(50) DEFAULT NULL COMMENT '银行联行号',
  `bank_account` varchar(50) NOT NULL COMMENT '出款账号',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) DEFAULT NULL COMMENT '商户账户号',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '代付金额',
  `actual_amount` decimal(20,8) NOT NULL COMMENT '实扣金额',
  `merchant_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) DEFAULT '0.00000000' COMMENT '到账金额',
  `channel_cost` decimal(20,8) DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '平台利润',

  -- 交易状态信息
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `payment_status` int(11) DEFAULT NULL COMMENT '支付状态',
  `settle_status` int(11) DEFAULT NULL COMMENT '结算状态',
  `call_back_status` int(11) DEFAULT NULL COMMENT '回调状态',

  -- 交易参与方信息
  `payer_info` text DEFAULT NULL COMMENT '付款方信息',
  `receiver_info` text DEFAULT NULL COMMENT '收款方信息',

  -- 时间&结果信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_result` varchar(255) DEFAULT NULL COMMENT '交易结果',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',
  `payment_result` varchar(255) DEFAULT NULL COMMENT '支付结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',
  `settle_result` varchar(255) DEFAULT NULL COMMENT '结算结果',

  -- 其他信息
  `source` int(11) DEFAULT NULL COMMENT '来源',
  `version` int(11) DEFAULT '1' COMMENT '版本号',
  `area` int(11) DEFAULT NULL COMMENT '区域',
  `refund_no` varchar(64) DEFAULT NULL COMMENT '退款单号',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `attribute` text DEFAULT NULL COMMENT '备注',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_bank_account` (`bank_account`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='沙箱代付订单表';


CREATE TABLE `settle_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',

  -- 账户信息
  `account_type` int(11) NOT NULL COMMENT '账户类型',
  `account_no` varchar(50) NOT NULL COMMENT '账户号',
  `account_name` varchar(100) NOT NULL COMMENT '账户名称',

  -- 余额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `available_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '可用余额',
  `frozen_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结余额',
  `to_settle_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '待结算余额',

  -- 状态信息
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `attribute` text DEFAULT NULL COMMENT '属性',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_account` (`merchant_id`, `account_no`),
  KEY `idx_account_type` (`account_type`),
  KEY `idx_currency` (`currency`),
  KEY `idx_status` (`status`),
  KEY `idx_area` (`area`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户账户余额表';

CREATE TABLE `settle_account_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 交易信息
  `trade_no` varchar(64) NOT NULL COMMENT '关联交易单号',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '账户号',

  -- 资金信息
  `account_direction` int(11) NOT NULL COMMENT '账户资金方向 1 收入 -1 支出',
  `account_direction_desc` varchar(100) NOT NULL COMMENT '账户资金方向描述',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '变动金额',

  -- 时间信息
  `flow_time` bigint(20) NOT NULL COMMENT '流水记录时间',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  KEY `idx_trade_no` (`trade_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_account_direction` (`account_direction`),
  KEY `idx_flow_time` (`flow_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户资金流水表';

CREATE TABLE `settle_account_snapshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 账户信息
  `account_date` date NOT NULL COMMENT '账务日期',
  `account_no` varchar(50) NOT NULL COMMENT '账户号',
  `account_name` varchar(100) NOT NULL COMMENT '账户名称',
  `account_type` int(11) NOT NULL COMMENT '账户类型',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',

  -- 余额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `available_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '可用余额',
  `frozen_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结余额',
  `to_settle_balance` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '待结算余额',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_date` (`account_date`, `account_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_type` (`account_type`),
  KEY `idx_currency` (`currency`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户每日余额快照表';


CREATE TABLE `settle_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '订单单号',
  `trade_type` int(11) NOT NULL COMMENT '交易类型',
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '商户账户号',

  -- 渠道信息
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `deduction_type` int(11) DEFAULT NULL COMMENT '扣费方式',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '订单金额',
  `merchant_fee` decimal(20,8) DEFAULT '0.00000000' COMMENT '结算商户手续费',
  `merchant_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '结算商户分润',
  `channel_cost` decimal(20,8) DEFAULT '0.00000000' COMMENT '结算商户通道成本',
  `platform_profit` decimal(20,8) DEFAULT '0.00000000' COMMENT '平台盈利',
  `account_amount` decimal(20,8) DEFAULT '0.00000000' COMMENT '到账金额',

  -- 结算信息
  `settle_type` varchar(10) DEFAULT NULL COMMENT '结算类型 D0/D1',
  `settle_time` varchar(20) DEFAULT NULL COMMENT '结算时间(商户配置，非D0使用)',
  `actual_settle_time` bigint(20) DEFAULT NULL COMMENT '结算时间',
  `settle_status` int(11) DEFAULT NULL COMMENT '结算状态',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_trade_type` (`trade_type`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算订单表';


CREATE TABLE `trade_payment_callback_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单信息
  `trade_no` varchar(64) NOT NULL COMMENT '收款单号',

  -- 回调信息
  `call_back_time` bigint(20) NOT NULL COMMENT '回调时间',
  `call_back_status` int(11) NOT NULL COMMENT '回调状态',
  `call_back_result` text NOT NULL COMMENT '回调结果',

  -- 扩展信息
  `attribute` text DEFAULT NULL COMMENT '扩展字段',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  KEY `idx_trade_no` (`trade_no`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_call_back_time` (`call_back_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款订单回调结果表';


CREATE TABLE `trade_payment_link_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单信息
  `link_no` varchar(64) NOT NULL COMMENT '支付链接单号',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',

  -- 支付信息
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '金额',

  -- 状态信息
  `link_status` int(11) NOT NULL COMMENT '链接状态',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `payment_link` varchar(255) NOT NULL COMMENT '支付链接',

  -- 其他信息
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_link_no` (`link_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_link_status` (`link_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付链接订单表';


CREATE TABLE `trade_payment_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '收款单号',
  `order_no` varchar(64) NOT NULL COMMENT '外部订单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '交易目的',
  `product_detail` varchar(255) DEFAULT NULL COMMENT '商品详情',

  -- 支付渠道信息
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '商户账户号',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '收款金额',
  `actual_amount` decimal(20,8) NOT NULL COMMENT '实扣金额',
  `merchant_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL COMMENT '到账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',

  -- 交易参与方信息
  `payer_info` text DEFAULT NULL COMMENT '付款方信息',
  `receiver_info` text DEFAULT NULL COMMENT '收款方信息',

  -- 交易状态信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` text DEFAULT NULL COMMENT '交易结果',

  -- 支付状态信息
  `payment_status` int(11) NOT NULL COMMENT '支付状态',
  `payment_result` text DEFAULT NULL COMMENT '支付结果',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',

  -- 结算信息
  `settle_status` int(11) NOT NULL COMMENT '结算状态',
  `settle_result` text DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 通知信息
  `call_back_status` int(11) NOT NULL COMMENT '回调状态',

  -- 其他信息
  `source` int(11) DEFAULT NULL COMMENT '来源',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款订单表';

CREATE TABLE `trade_payout_callback_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单信息
  `trade_no` varchar(64) NOT NULL COMMENT '代付单号',

  -- 回调信息
  `call_back_time` bigint(20) NOT NULL COMMENT '回调时间',
  `call_back_status` int(11) NOT NULL COMMENT '回调状态',
  `call_back_result` text DEFAULT NULL COMMENT '回调结果',

  -- 扩展信息
  `attribute` text DEFAULT NULL COMMENT '扩展字段',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  KEY `idx_trade_no` (`trade_no`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_call_back_time` (`call_back_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代付订单回调结果表';

CREATE TABLE `trade_payout_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '代付单号',
  `outer_no` varchar(64) NOT NULL COMMENT '外部单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '交易目的',
  `product_detail` varchar(255) DEFAULT NULL COMMENT '商品详情',

  -- 支付渠道信息
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `bank_code` varchar(50) DEFAULT NULL COMMENT '银行联行号',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '出款账号',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '商户账户号',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL COMMENT '代付金额',
  `actual_amount` decimal(20,8) NOT NULL COMMENT '实扣金额',
  `merchant_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL COMMENT '到账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',

  -- 交易状态信息
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` text DEFAULT NULL COMMENT '交易结果',
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',

  -- 支付状态信息
  `payment_status` int(11) NOT NULL COMMENT '支付状态',
  `payment_result` text DEFAULT NULL COMMENT '支付结果',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',

  -- 结算状态信息
  `settle_status` int(11) NOT NULL COMMENT '结算状态',
  `settle_result` text DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 通知信息
  `call_back_status` int(11) NOT NULL COMMENT '回调状态',

  -- 其他信息
  `source` int(11) DEFAULT NULL COMMENT '来源',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `area` int(11) DEFAULT NULL COMMENT '区域',
  `refund_no` varchar(64) DEFAULT NULL COMMENT '退款单号',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `attribute` text DEFAULT NULL COMMENT '扩展信息',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_outer_no` (`outer_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_call_back_status` (`call_back_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代付订单表';


CREATE TABLE `trade_recharge_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '充值单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '充值目的',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '充值商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '充值商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '充值账户',

  -- 金额信息
  `recharge_currency` varchar(10) NOT NULL COMMENT '充值币种',
  `recharge_amount` decimal(20,8) NOT NULL COMMENT '充值金额',
  `exchange_rate` decimal(20,8) NOT NULL DEFAULT '1.00000000' COMMENT '兑换率',
  `currency` varchar(10) NOT NULL COMMENT '入账币种',
  `amount` decimal(20,8) NOT NULL COMMENT '入账金额',
  `merchant_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL COMMENT '到账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',

  -- 支付信息
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '充值账户',

  -- 交易状态信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` text DEFAULT NULL COMMENT '交易结果',

  -- 结算信息
  `settle_status` int(11) NOT NULL COMMENT '结算状态',
  `settle_result` text DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 其他信息
  `attribute` text DEFAULT NULL COMMENT '扩展信息',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值订单表';


CREATE TABLE `snapshot_trade_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 统计维度信息
  `trade_date` date NOT NULL COMMENT '交易日期',
  `trade_type` int(11) NOT NULL COMMENT '交易类型(1:收款 2:代付 3:充值 4:转账 5:提现)',
  `transfer_direction` int(11) DEFAULT NULL COMMENT '转账方向(-1:转出 1:转入)',
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '商户账户号',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编号',
  `channel_name` varchar(100) DEFAULT NULL COMMENT '渠道名称',

  -- 订单统计信息
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单笔数',
  `order_success_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单成功笔数',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `order_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '订单金额',
  `order_success_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '订单成功金额',

  -- 费用统计信息
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户入账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',

  -- 其他信息
  `attribute` text DEFAULT NULL COMMENT '扩展字段',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_date_type_merchant` (`trade_date`, `trade_type`, `merchant_id`, `account_no`, `payment_method`, `channel_code`),
  KEY `idx_trade_date` (`trade_date`),
  KEY `idx_trade_type` (`trade_type`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易数据分析快照表';

CREATE TABLE `trade_transfer_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '转账单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '转账目的',
  `direction` int(11) NOT NULL COMMENT '转账方向(-1:转出 1:转入)',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '转账商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '转账商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '转账账户',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '转账金额',
  `merchant_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '到账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',

  -- 交易状态信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` text DEFAULT NULL COMMENT '交易结果',

  -- 结算信息
  `settle_status` int(11) NOT NULL COMMENT '结算状态',
  `settle_result` text DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 其他信息
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `area` int(11) DEFAULT NULL COMMENT '地区',
  `attribute` text DEFAULT NULL COMMENT '扩展字段',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转账订单表';


CREATE TABLE `trade_withdraw_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 订单基础信息
  `trade_no` varchar(64) NOT NULL COMMENT '提现单号',
  `purpose` varchar(255) DEFAULT NULL COMMENT '提现目的',

  -- 支付渠道信息
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道编码',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `withdraw_account` varchar(50) NOT NULL COMMENT '出款账号',
  `withdraw_account_name` varchar(100) NOT NULL COMMENT '出款账号名称',

  -- 商户信息
  `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
  `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
  `account_no` varchar(50) NOT NULL COMMENT '商户账户号',

  -- 金额信息
  `currency` varchar(10) NOT NULL COMMENT '提现币种',
  `amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '提现金额',
  `actual_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '实扣金额',
  `merchant_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户分润',
  `merchant_fee` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '商户手续费',
  `account_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '到账金额',
  `channel_cost` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '通道成本',
  `platform_profit` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台利润',
  `withdraw_target_currency` varchar(10) DEFAULT NULL COMMENT '提现目标币种',
  `withdraw_target_amount` decimal(20,8) DEFAULT NULL COMMENT '提现目标金额',
  `exchange_rate` decimal(20,8) NOT NULL DEFAULT '1.00000000' COMMENT '兑换率',

  -- 交易参与方信息
  `payer_info` text DEFAULT NULL COMMENT '付款方信息',
  `receiver_info` text DEFAULT NULL COMMENT '收款方信息',

  -- 交易状态信息
  `trade_time` bigint(20) NOT NULL COMMENT '交易时间',
  `trade_status` int(11) NOT NULL COMMENT '交易状态',
  `trade_result` text DEFAULT NULL COMMENT '交易结果',

  -- 支付状态信息
  `payment_status` int(11) NOT NULL COMMENT '支付状态',
  `payment_result` text DEFAULT NULL COMMENT '支付结果',
  `payment_finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',

  -- 结算信息
  `settle_status` int(11) NOT NULL COMMENT '结算状态',
  `settle_result` text DEFAULT NULL COMMENT '结算结果',
  `settle_finish_time` bigint(20) DEFAULT NULL COMMENT '结算完成时间',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_withdraw_account` (`withdraw_account`),
  KEY `idx_trade_time` (`trade_time`),
  KEY `idx_trade_status` (`trade_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_settle_status` (`settle_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现订单表';
