use shampohoe_api;
-- 接口信息
create table if not exists shampohoe_api.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)',
    `name` varchar(256) not null comment '用户名',
    `description` varchar(256) null comment '描述信息',
    `url` varchar(512) not null comment '接口地址',
    `requestParams` text not null comment '请求参数',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态0关闭1开启',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    ) comment '接口信息';

insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('余昊天', 'www.graciela-durgan.org', '方正豪', '唐天翊', 0, '潘越泽', 6018);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('袁金鑫', 'www.towanda-schimmel.net', '秦鹏涛', '石涛', 0, '田伟宸', 104);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贾明轩', 'www.julio-hessel.co', '陶君浩', '高峻熙', 0, '徐明杰', 647);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('丁鑫鹏', 'www.dennise-schinner.net', '吴晓啸', '龚浩然', 0, '侯文', 8);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('尹鑫磊', 'www.leanna-block.biz', '钟烨伟', '白笑愚', 0, '程烨霖', 168);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('姚立果', 'www.ben-schulist.com', '程浩宇', '谭智宸', 0, '陶伟泽', 870);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('邓文昊', 'www.else-rippin.net', '周琪', '高烨霖', 0, '周智宸', 813513);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贾浩轩', 'www.parker-armstrong.co', '任鹏煊', '曹昊天', 0, '张瑞霖', 61);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('蔡明哲', 'www.gregorio-moen.info', '朱立轩', '汪明杰', 0, '武煜城', 3228);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('沈思源', 'www.keisha-stokes.biz', '钟明辉', '郑涛', 0, '崔智渊', 25427);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('白荣轩', 'www.ka-weissnat.info', '郑鹏飞', '林胤祥', 0, '蔡峻熙', 7);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('严健雄', 'www.theo-herman.co', '毛嘉熙', '钟立诚', 0, '莫雨泽', 3);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('雷昊然', 'www.erasmo-greenholt.net', '吕黎昕', '洪黎昕', 0, '魏弘文', 329);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('熊伟祺', 'www.lincoln-carter.name', '洪浩然', '贺驰', 0, '覃烨霖', 41618);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('周立诚', 'www.eura-windler.co', '傅嘉懿', '陶子骞', 0, '金雨泽', 2041191591);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('谢涛', 'www.napoleon-wolf.org', '赵远航', '史昊强', 0, '蒋旭尧', 19);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('何风华', 'www.vella-medhurst.info', '李荣轩', '钟致远', 0, '丁俊驰', 44);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('薛瑞霖', 'www.shera-heathcote.io', '尹子默', '尹嘉懿', 0, '张雪松', 153585549);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('彭金鑫', 'www.rolland-murazik.biz', '赖修洁', '邹立轩', 0, '萧哲瀚', 210149251);
insert into shampohoe_api.`interface_info` (`name`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('魏鹭洋', 'www.orval-homenick.info', '龚雨泽', '钟靖琪', 0, '洪聪健', 81842291);

-- 用户调用接口关系表
create table if not exists shampohoe_api.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户 id',
    `interfaceInfoId` bigint not null comment '接口 id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常，1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';