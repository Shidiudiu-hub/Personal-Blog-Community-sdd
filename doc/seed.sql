-- ============================================
-- 博客社区演示数据：用户 & 文章
-- 目标：
-- 1) 注入 10 个普通用户 + 2 个管理员
-- 2) 每人发布 2~3 篇文章（Markdown，约 200~300 字，带封面图）
-- 3) 文章关联现有分类/标签（见 doc/init.sql）
-- 说明：
-- - 本项目 PasswordUtil 目前为“明文校验”，因此这里直接写入明文密码
-- - 脚本可重复执行：会先清理本脚本插入的 demo_* 用户及其文章
-- ============================================

START TRANSACTION;

-- ---------- 0) 预查询分类/标签 ID（避免硬编码） ----------
SET @cat_tech := (SELECT category_id FROM `category` WHERE `name` = '技术' AND deleted = 0 LIMIT 1);
SET @cat_life := (SELECT category_id FROM `category` WHERE `name` = '生活' AND deleted = 0 LIMIT 1);
SET @cat_note := (SELECT category_id FROM `category` WHERE `name` = '随笔' AND deleted = 0 LIMIT 1);

SET @tag_java := (SELECT tag_id FROM `tag` WHERE `name` = 'Java' AND deleted = 0 LIMIT 1);
SET @tag_vue := (SELECT tag_id FROM `tag` WHERE `name` = 'Vue' AND deleted = 0 LIMIT 1);
SET @tag_spring := (SELECT tag_id FROM `tag` WHERE `name` = 'Spring Boot' AND deleted = 0 LIMIT 1);
SET @tag_db := (SELECT tag_id FROM `tag` WHERE `name` = '数据库' AND deleted = 0 LIMIT 1);
SET @tag_fe := (SELECT tag_id FROM `tag` WHERE `name` = '前端' AND deleted = 0 LIMIT 1);

-- ---------- 1) 清理旧数据（仅清理 demo_* 用户） ----------
-- 先删文章（article_tag/comment/like/collect 会因外键级联自动清理）
DELETE FROM `article`
WHERE user_id IN (SELECT user_id FROM `user` WHERE username LIKE 'demo_%');

-- 再删用户（follow/like/collect 等引用 user 的表按外键 CASCADE 清理）
DELETE FROM `user`
WHERE username LIKE 'demo_%';

-- ---------- 2) 插入用户（10 普通 + 2 管理员） ----------
INSERT INTO `user` (username, `password`, real_name, phone, email, `role`, avatar, `status`, follow_count, fan_count, create_time, update_time, deleted) VALUES
('demo_user01', '123456', '林远', '13800000001', 'demo_user01@example.com', 0, 'https://picsum.photos/seed/demo_user01/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user02', '123456', '陈奕', '13800000002', 'demo_user02@example.com', 0, 'https://picsum.photos/seed/demo_user02/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user03', '123456', '周宁', '13800000003', 'demo_user03@example.com', 0, 'https://picsum.photos/seed/demo_user03/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user04', '123456', '许言', '13800000004', 'demo_user04@example.com', 0, 'https://picsum.photos/seed/demo_user04/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user05', '123456', '沈乔', '13800000005', 'demo_user05@example.com', 0, 'https://picsum.photos/seed/demo_user05/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user06', '123456', '唐梨', '13800000006', 'demo_user06@example.com', 0, 'https://picsum.photos/seed/demo_user06/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user07', '123456', '顾川', '13800000007', 'demo_user07@example.com', 0, 'https://picsum.photos/seed/demo_user07/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user08', '123456', '罗夏', '13800000008', 'demo_user08@example.com', 0, 'https://picsum.photos/seed/demo_user08/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user09', '123456', '韩树', '13800000009', 'demo_user09@example.com', 0, 'https://picsum.photos/seed/demo_user09/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_user10', '123456', '夏青', '13800000010', 'demo_user10@example.com', 0, 'https://picsum.photos/seed/demo_user10/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_admin01', 'admin123', '站点管理员A', '13900000001', 'demo_admin01@example.com', 1, 'https://picsum.photos/seed/demo_admin01/200/200', 1, 0, 0, NOW(), NOW(), 0),
('demo_admin02', 'admin123', '站点管理员B', '13900000002', 'demo_admin02@example.com', 1, 'https://picsum.photos/seed/demo_admin02/200/200', 1, 0, 0, NOW(), NOW(), 0);

-- 取回用户 ID
SET @u01 := (SELECT user_id FROM `user` WHERE username='demo_user01' LIMIT 1);
SET @u02 := (SELECT user_id FROM `user` WHERE username='demo_user02' LIMIT 1);
SET @u03 := (SELECT user_id FROM `user` WHERE username='demo_user03' LIMIT 1);
SET @u04 := (SELECT user_id FROM `user` WHERE username='demo_user04' LIMIT 1);
SET @u05 := (SELECT user_id FROM `user` WHERE username='demo_user05' LIMIT 1);
SET @u06 := (SELECT user_id FROM `user` WHERE username='demo_user06' LIMIT 1);
SET @u07 := (SELECT user_id FROM `user` WHERE username='demo_user07' LIMIT 1);
SET @u08 := (SELECT user_id FROM `user` WHERE username='demo_user08' LIMIT 1);
SET @u09 := (SELECT user_id FROM `user` WHERE username='demo_user09' LIMIT 1);
SET @u10 := (SELECT user_id FROM `user` WHERE username='demo_user10' LIMIT 1);
SET @a01 := (SELECT user_id FROM `user` WHERE username='demo_admin01' LIMIT 1);
SET @a02 := (SELECT user_id FROM `user` WHERE username='demo_admin02' LIMIT 1);

-- ---------- 3) 插入文章与标签关联 ----------
-- 写作风格：200~300 字左右 + 封面图（cover_image + Markdown 首图）

-- demo_user01（技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u01,
  '把“能跑”写成“可维护”：我做接口层的三条小规矩',
  '![封面](https://picsum.photos/seed/a_u01_1/800/420)\n\n最近在写接口时，我给自己定了三条规矩：**返回结构统一**、**参数校验前置**、**异常尽量在边界消化**。统一返回能让前端解析更稳定；前置校验能减少“半路失败”的脏数据；边界消化则是把不可控因素（外部调用、空值、越界）限制在最小范围。\n\n这套规矩不华丽，但很实用：当你需要追踪一个“为什么列表为空”的问题时，日志、分页字段、权限判断都能快速对齐；当你需要扩展一个功能时，修改范围也更可控。\n\n结论：先把代码写得清楚，再谈写得聪明。',
  '接口层不追求花活，统一返回、前置校验、边界消化三条规矩，让问题可定位、扩展可控。',
  'https://picsum.photos/seed/a_u01_1/800/420',
  @cat_tech, 1, 32, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_java, NOW()),
(@art, @tag_spring, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u01,
  '数据库里最容易忽略的“软删除”：别只加 deleted 字段',
  '![封面](https://picsum.photos/seed/a_u01_2/800/420)\n\n软删除看似简单：加一列 `deleted=0/1`，查询时带上条件即可。但我踩过的坑是：**关联表与统计字段**。比如文章删除后，评论、点赞、收藏是否级联？分类/标签的文章数是否需要同步？以及“我的文章”“首页列表”对 deleted/status 的过滤是否一致？\n\n建议：\n- 关键查询统一封装（XML/Mapper 或 Service），避免各处拼条件。\n- 统计字段要么做触发器，要么在业务层集中维护；二者同时做也可以当双保险。\n- 别忘了给 `deleted` 建索引。\n\n软删除不是“加字段”，而是一套一致性策略。',
  '软删除不仅是 deleted 字段，还牵扯级联、统计、查询一致性；建议统一封装与集中维护。',
  'https://picsum.photos/seed/a_u01_2/800/420',
  @cat_tech, 1, 41, 2, 0, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_db, NOW()),
(@art, @tag_java, NOW());

-- demo_user02（前端/技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u02,
  'Vue3 + 列表分页：我如何兼容“奇形怪状”的后端字段',
  '![封面](https://picsum.photos/seed/a_u02_1/800/420)\n\n做后台管理时最常见的痛点不是组件，而是接口返回结构不一致：有时是 `list`，有时是 `records`，甚至 `total` 还是字符串。我的做法是写一个小的解析层：先判断是否套了统一响应 `R`，再从 PageResult 里按优先级取列表字段，最后把 total 统一转成 number。\n\n这一步“看起来多余”，但能让页面逻辑更干净：组件只关心 `items` 和 `total`，调试也更集中。顺手再加上请求/响应拦截器的日志，问题基本都能在控制台定位。\n\n前端稳定性，很多时候靠的是“耐心地统一格式”。',
  '把列表解析做成一层适配：兼容 records/list/data，统一 total 类型，组件逻辑更干净稳定。',
  'https://picsum.photos/seed/a_u02_1/800/420',
  @cat_tech, 1, 55, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_vue, NOW()),
(@art, @tag_fe, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u02,
  '我喜欢用的三种封面图来源：省事但不将就',
  '![封面](https://picsum.photos/seed/a_u02_2/800/420)\n\n写博客时封面图很重要，它决定了读者是否愿意点开。我的“懒人方案”是三种：\n\n1) `picsum.photos/seed/...`：快速、可重复，适合演示环境。\n2) Unsplash：质量高，但要注意尺寸和加载。\n3) 自己截图：对技术文章最友好，能把重点直接放在封面上。\n\n不管用哪种，都建议做两件事：\n- 统一宽高比例（比如 16:9）\n- 配一段 80~120 字的摘要\n\n这样列表页会更整齐，也更像一个“可读”的社区。',
  '封面图不必复杂：picsum / Unsplash / 截图三种方案，统一比例与摘要即可让列表更好看。',
  'https://picsum.photos/seed/a_u02_2/800/420',
  @cat_life, 1, 28, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_fe, NOW());

-- demo_user03（技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u03,
  'Spring Boot 项目里，我为什么喜欢把“统计字段”当一等公民',
  '![封面](https://picsum.photos/seed/a_u03_1/800/420)\n\n点赞数、收藏数、评论数、分类文章数……这些字段会被频繁读取。与其每次 join + count，不如在写入时维护统计字段：读快、列表快、排序也快。当然代价是写复杂，需要考虑事务与一致性。\n\n我的折中做法：\n- 写入路径统一在 service 层；\n- 关键表用触发器做兜底（尤其是历史数据修复）；\n- 对外查询永远只读统计字段，不在列表页临时 count。\n\n当数据量上来时，这些“提前算好”的字段，会让系统的体验差异非常明显。',
  '统计字段是高频读的核心资产：写入时维护、触发器兜底、查询只读统计字段，列表性能更稳。',
  'https://picsum.photos/seed/a_u03_1/800/420',
  @cat_tech, 1, 67, 4, 2, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_spring, NOW()),
(@art, @tag_db, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u03,
  '写给自己看的技术笔记：把日志当成产品的一部分',
  '![封面](https://picsum.photos/seed/a_u03_2/800/420)\n\n我以前把日志当“调试工具”，现在更愿意把它当“产品的一部分”。因为线上排查的成本远高于开发期加几行日志。\n\n实践建议：\n- 接口入口打印关键参数（分页、筛选条件、当前用户）\n- 关键分支打点（权限、草稿判断、删除判断）\n- SQL/Mapper 只保留必要日志，避免刷屏\n\n日志不是越多越好，而是“能还原过程”。当你能从日志复盘一次请求的路径，很多问题都能快速定位。',
  '日志要能还原请求路径：入口参数、关键分支打点、必要 SQL 日志，线上排查会轻松很多。',
  'https://picsum.photos/seed/a_u03_2/800/420',
  @cat_note, 1, 39, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_java, NOW());

-- demo_user04（生活）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u04,
  '用番茄钟写博客：我如何把“想写”变成“写完”',
  '![封面](https://picsum.photos/seed/a_u04_1/800/420)\n\n写博客最大的阻力不是技术，而是注意力。我试过很多方法，最后留下的是番茄钟：25 分钟只做一件事，5 分钟休息。\n\n写作时我会拆成三步：\n- 先写标题和三段小结（像目录）\n- 再补例子和截图\n- 最后统一润色\n\n每次只要求自己完成一个番茄钟，累积下来反而更稳定。写作不是冲刺，是长期的低强度坚持。',
  '番茄钟 + 拆分写作步骤，让“想写”变成“写完”。写作不是冲刺，而是长期的低强度坚持。',
  'https://picsum.photos/seed/a_u04_1/800/420',
  @cat_life, 1, 24, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_fe, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u04,
  '生活里的“版本管理”：每周一次小复盘',
  '![封面](https://picsum.photos/seed/a_u04_2/800/420)\n\n做开发久了，会下意识做版本管理：记录变更、能回滚、可追溯。我把这套习惯搬到生活里：每周一次小复盘，三件事就够——\n\n- 这周最有价值的一次投入是什么？\n- 哪件事让我明显分心？\n- 下周只改一个小动作，会改什么？\n\n复盘不是自责，而是把“随机波动”变成“可控迭代”。当你愿意持续做一点点小优化，生活也会像代码一样越来越顺。',
  '每周一次小复盘：价值投入、分心源、下周一个小改动。把随机波动变成可控迭代。',
  'https://picsum.photos/seed/a_u04_2/800/420',
  @cat_note, 1, 31, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 0
);
SET @art := LAST_INSERT_ID();

-- demo_user05（技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u05,
  '写 SQL 之前先想清楚：你要的是“列表”还是“视图”',
  '![封面](https://picsum.photos/seed/a_u05_1/800/420)\n\n很多列表查询越写越复杂，本质原因是把“展示视图”当成“原始列表”。比如文章列表里要带作者名、头像、分类名、标签数组、统计字段……这其实更像一个视图。\n\n我现在更倾向：\n- SQL 里一次 join 出必要字段（作者/分类）\n- 标签用子查询/collection 取（避免重复行）\n- 列表只返回展示需要的 VO\n\n当你承认自己在做“视图”，很多取舍就更自然：字段要稳定、结构要统一、性能要可预期。',
  '文章列表更像“展示视图”：join 作者/分类，标签用子查询，VO 结构稳定，性能更可预期。',
  'https://picsum.photos/seed/a_u05_1/800/420',
  @cat_tech, 1, 44, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_db, NOW()),
(@art, @tag_java, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u05,
  '前端路由权限：不要只靠“菜单隐藏”',
  '![封面](https://picsum.photos/seed/a_u05_2/800/420)\n\n很多系统把权限理解成“菜单不显示”，但用户依然可以手动输入 URL。正确做法是多层：\n\n- 菜单层：根据角色过滤路由树\n- 路由守卫：进入页面前校验 meta.roles\n- 页面兜底：没有权限时给出友好提示与跳转\n\n三层一起做，体验和安全性都更稳。隐藏菜单是 UX，拦截路由是安全，页面兜底是最后的保险。',
  '权限别只做菜单隐藏：菜单过滤 + 路由守卫 + 页面兜底三层，安全与体验都更稳。',
  'https://picsum.photos/seed/a_u05_2/800/420',
  @cat_tech, 1, 52, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_vue, NOW()),
(@art, @tag_fe, NOW());

-- demo_user06（生活）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u06,
  '一张清单解决“下班后不知道做什么”',
  '![封面](https://picsum.photos/seed/a_u06_1/800/420)\n\n我以前下班后容易刷手机，明明很累又停不下来。后来我写了一个“低门槛清单”：运动 10 分钟、洗个热水澡、读 10 页书、写 5 行日记、整理桌面。\n\n核心是：每一项都足够小，小到不需要意志力。做完任何一项就算胜利，剩下的时间想休息也不会内疚。\n\n当你能稳定地把一天收尾，第二天的精力会更像“可复用的缓存”。',
  '下班后用低门槛清单收尾：每项都小到不费意志力，做完任何一项就算胜利。',
  'https://picsum.photos/seed/a_u06_1/800/420',
  @cat_life, 1, 19, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u06,
  '随笔：把“写不出来”当作信号',
  '![封面](https://picsum.photos/seed/a_u06_2/800/420)\n\n有时候坐在电脑前，发现自己写不出来。我以前会强行逼自己继续，后来发现“写不出来”往往是一个信号：要么输入不足，要么目标太大。\n\n我的应对是两步：\n- 把题目缩小一半（从“如何做架构”变成“我怎么组织一个接口”）\n- 先写一个例子（真实 bug、真实日志、真实数据）\n\n当你从具体开始，文字会自己长出来。写作不需要灵感，写作需要路径。',
  '写不出来是信号：缩小题目、先写一个真实例子，从具体开始，文字会自己长出来。',
  'https://picsum.photos/seed/a_u06_2/800/420',
  @cat_note, 1, 27, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 0
);
SET @art := LAST_INSERT_ID();

-- demo_user07（技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u07,
  '分页到底要不要从 0 开始？我选择“对用户友好”',
  '![封面](https://picsum.photos/seed/a_u07_1/800/420)\n\n分页有两派：0-based 和 1-based。后端开发更喜欢 0-based，前端 UI 更喜欢 1-based。我的实践是：对外接口统一 1-based（用户看到的就是第 1 页），内部如果用 PageHelper/ORM 再做转换。\n\n这看似是细节，但能减少很多沟通成本：UI、日志、接口文档三者一致；排查问题时也不会出现“我传 0 还是 1”的来回确认。\n\n细节统一，就是团队效率。',
  '分页对外统一 1-based：UI、日志、文档一致，减少沟通成本；内部需要再做转换也更清晰。',
  'https://picsum.photos/seed/a_u07_1/800/420',
  @cat_tech, 1, 36, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_java, NOW()),
(@art, @tag_vue, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u07,
  'Tag 设计小结：颜色只是开始，关键是“可读性”',
  '![封面](https://picsum.photos/seed/a_u07_2/800/420)\n\n标签上色会让界面更活泼，但更重要的是可读性：颜色要对比足够、边框/文字要统一、标签数量要适度。\n\n我给自己定了三条规则：\n- 标签最多显示 3~5 个，多了就折叠\n- 颜色用固定色板，不让用户随意输入奇怪颜色\n- 列表页标签只做定位，不做信息堆叠\n\n标签的目的不是装饰，而是帮助读者快速判断“值不值得点开”。',
  '标签的关键是可读性：固定色板、控制数量、列表页只做定位。让读者更快判断文章价值。',
  'https://picsum.photos/seed/a_u07_2/800/420',
  @cat_tech, 1, 22, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_fe, NOW());

-- demo_user08（随笔/技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u08,
  '随笔：当我开始在代码里写“为什么”',
  '![封面](https://picsum.photos/seed/a_u08_1/800/420)\n\n以前我写注释只写“做了什么”，后来发现真正有价值的是“为什么”。比如为什么草稿只能作者看、为什么白名单接口也要透传 userId、为什么 PageResult 的 total 要做字符串兼容。\n\n当你把“为什么”写下来，半年后回头看就不会再踩同一个坑；新人接手也能少走弯路。\n\n代码是写给人看的，机器只是顺便执行。',
  '注释写“为什么”更有价值：记录权衡与边界，避免半年后再踩坑，也方便新人快速理解。',
  'https://picsum.photos/seed/a_u08_1/800/420',
  @cat_note, 1, 18, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u08,
  '在一个小项目里练“工程化”：从目录到接口约定',
  '![封面](https://picsum.photos/seed/a_u08_2/800/420)\n\n很多人只有在大项目里才谈工程化，但我觉得小项目更适合练基本功：目录结构清晰、接口约定统一、错误处理一致、日志能追踪。\n\n例如：前端统一 Axios 拦截器、后端统一 R 返回、分页统一字段；这些看起来“啰嗦”的约定，会在需求增长时帮你顶住混乱。\n\n工程化不是仪式，是减少未来的痛苦。',
  '小项目更适合练工程化：目录清晰、接口统一、错误处理一致。工程化不是仪式，是减少未来痛苦。',
  'https://picsum.photos/seed/a_u08_2/800/420',
  @cat_tech, 1, 29, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_vue, NOW()),
(@art, @tag_spring, NOW());

-- demo_user09（生活）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u09,
  '我给自己做的“信息饮食”：只订阅 5 个来源',
  '![封面](https://picsum.photos/seed/a_u09_1/800/420)\n\n信息过载时，最难的是判断什么值得看。我现在只订阅 5 个来源：一个技术周刊、一个数据库专栏、一个前端作者、一个生活类播客、一个随笔作者。剩下的全部通过“搜索”获取，而不是被动推送。\n\n这样做的好处是：注意力更稳定，阅读更像“按需加载”。当你愿意控制输入源，输出（写作、思考、产出）也会更轻松。',
  '控制信息源：只订阅少量高质量来源，其他内容按需搜索。注意力更稳定，产出更轻松。',
  'https://picsum.photos/seed/a_u09_1/800/420',
  @cat_life, 1, 16, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u09,
  '技术写作的一个小技巧：用“问题”开头',
  '![封面](https://picsum.photos/seed/a_u09_2/800/420)\n\n我发现技术文章最容易写成“流水账”，读者看两段就退出。一个简单的改进是：用问题开头。\n\n比如：\n- 为什么文章列表为空？\n- 为什么草稿无权限？\n- 为什么点赞收藏不显示？\n\n当你从问题出发，文章会自然形成结构：现象、原因、验证、修复、复盘。读者也更容易跟着你的路径走到结论。',
  '用问题开头能让文章自然成型：现象→原因→验证→修复→复盘，读者更容易读到结论。',
  'https://picsum.photos/seed/a_u09_2/800/420',
  @cat_note, 1, 21, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY), 0
);
SET @art := LAST_INSERT_ID();

-- demo_user10（技术）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u10,
  '从 0 到 1 做一个“可互动”的文章详情页',
  '![封面](https://picsum.photos/seed/a_u10_1/800/420)\n\n文章详情页不只是展示正文，还应该支持互动：评论、点赞、收藏、关注作者。实现时我把它拆成四块：\n\n1) 详情数据：标题、作者、分类、标签、统计字段。\n2) 互动状态：当前用户是否点赞/收藏。\n3) 评论区：顶级评论 + 回复列表。\n4) 友好跳转：无权限/不存在时回到“我的文章”。\n\n拆分之后每块都能独立调试，用户体验也更完整。',
  '把详情页拆成：详情数据、互动状态、评论区、友好跳转四块，调试更独立，体验也更完整。',
  'https://picsum.photos/seed/a_u10_1/800/420',
  @cat_tech, 1, 58, 4, 2, 0, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_vue, NOW()),
(@art, @tag_fe, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @u10,
  '我理解的“社区感”：让用户愿意留下来',
  '![封面](https://picsum.photos/seed/a_u10_2/800/420)\n\n一个社区最宝贵的不是功能，而是“愿意留下来的人”。产品上我会优先做三件事：\n\n- 让内容更容易被发现（分类/标签/搜索/排序）\n- 让互动更低摩擦（点赞收藏、评论回复、关注）\n- 让个人主页更真实（文章、关注、粉丝、统计）\n\n当你把这些链路串起来，用户会从“读完就走”变成“逛一会儿”。社区感不是口号，是连续的体验。',
  '社区感来自连续体验：内容发现、低摩擦互动、真实个人主页。链路串起来，用户才会从读完就走变成逛一会儿。',
  'https://picsum.photos/seed/a_u10_2/800/420',
  @cat_note, 1, 25, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 0
);
SET @art := LAST_INSERT_ID();

-- demo_admin01（3 篇，技术为主）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a01,
  '【站点公告】关于“分类/标签文章数”的统计口径说明',
  '![封面](https://picsum.photos/seed/a_admin01_1/800/420)\n\n为了保证列表性能，本系统对分类与标签的文章数采用“统计字段 + 触发器/业务层维护”的方式。\n\n统计口径：\n- 仅统计 **已发布（status=1）且未删除（deleted=0）** 的文章。\n- 草稿不会计入文章数。\n\n如果你发现文章数不一致，可以执行修复脚本对历史数据重新计算。后续新增/编辑文章时，系统会自动更新统计字段。\n\n感谢你的反馈与使用。',
  '公告：分类/标签文章数仅统计已发布且未删除文章，草稿不计入；可通过脚本修复历史数据统计。',
  'https://picsum.photos/seed/a_admin01_1/800/420',
  @cat_note, 1, 88, 6, 3, 0, DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a01,
  '【管理员笔记】为什么“拖拽排序”比“填数字”更可靠',
  '![封面](https://picsum.photos/seed/a_admin01_2/800/420)\n\n分类排序最容易出现的问题是重复、空值、不连续。让用户手填排序数字，必然会产生冲突；而拖拽排序天然表达了“我想把它放到第几位”。\n\n实现时我们做了两层保证：\n- 前端拖拽结束后只提交一个目标位置；\n- 后端用“移动排序”算法挤开区间，并在每次变更前归一化旧数据。\n\n结果是：用户操作更直观，数据也能长期保持一致。',
  '拖拽排序更直观且更不易出错：前端提交目标位置，后端区间挤开+归一化保证排序长期一致。',
  'https://picsum.photos/seed/a_admin01_2/800/420',
  @cat_tech, 1, 64, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_fe, NOW()),
(@art, @tag_vue, NOW());

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a01,
  '【实践】接口白名单 + 登录态透传：既开放又不丢上下文',
  '![封面](https://picsum.photos/seed/a_admin01_3/800/420)\n\n很多接口需要公开访问（如文章列表、标签热榜），但当用户携带 token 时，我们又希望后端能拿到 userId 做差异化处理（比如草稿权限、互动状态）。\n\n因此白名单不等于“忽略 token”。正确做法是：\n- 白名单接口无 token 直接放行；\n- 有 token 则解析并透传 userId（包装 request 继续传递）。\n\n这样既能保持接口可访问性，也能保留登录上下文，功能更完整。',
  '白名单接口也要支持登录态透传：无 token 放行，有 token 解析并透传 userId，既开放又保留上下文。',
  'https://picsum.photos/seed/a_admin01_3/800/420',
  @cat_tech, 1, 73, 5, 2, 0, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_spring, NOW());

-- demo_admin02（3 篇）
INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a02,
  '【站点公告】演示账号与默认密码说明',
  '![封面](https://picsum.photos/seed/a_admin02_1/800/420)\n\n为方便本地体验，系统提供了一组演示账号：10 个普通用户与 2 个管理员。\n\n- 普通用户：用户名 `demo_user01` ~ `demo_user10`，密码 `123456`\n- 管理员：用户名 `demo_admin01` / `demo_admin02`，密码 `admin123`\n\n你可以用这些账号体验发布文章、点赞收藏、关注、评论与后台管理等功能。若你用于线上环境，请务必禁用或删除演示账号。',
  '公告：提供 10 个普通用户与 2 个管理员演示账号（普通 123456 / 管理员 admin123），线上请务必移除。',
  'https://picsum.photos/seed/a_admin02_1/800/420',
  @cat_note, 1, 90, 8, 4, 0, DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a02,
  '【运营】给新作者的三条建议：从第一篇开始写',
  '![封面](https://picsum.photos/seed/a_admin02_2/800/420)\n\n如果你刚开始写博客，不必等“准备充分”。给你三条建议：\n\n1) 从小主题开始：一段代码、一次排错、一个小技巧。\n2) 写清楚过程：现象→原因→验证→修复→总结。\n3) 配一张图：封面或截图都行，让列表更可读。\n\n社区最需要的是持续的真实内容。你的第一篇可能不完美，但它会让第二篇更容易。',
  '运营建议：小主题起步、写清过程、配一张图。第一篇不必完美，但会让第二篇更容易。',
  'https://picsum.photos/seed/a_admin02_2/800/420',
  @cat_life, 1, 47, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 0
);
SET @art := LAST_INSERT_ID();

INSERT INTO `article` (user_id, title, content, summary, cover_image, category_id, status, view_count, like_count, collect_count, comment_count, create_time, update_time, deleted)
VALUES (
  @a02,
  '【技术提示】分类排序的最佳实践：用位置表达意图',
  '![封面](https://picsum.photos/seed/a_admin02_3/800/420)\n\n排序的本质是“位置”。当你想把“技术”放在最前面，不是因为它的排序数字是 1，而是因为它应该在列表第一位。\n\n因此我们建议：\n- 使用拖拽排序来表达位置；\n- 后端用区间移动算法维护唯一且连续的排序；\n- 如需批量调整，可以先归一化再移动。\n\n当排序能稳定表达意图，分类体系也会更清晰。',
  '排序本质是位置：推荐拖拽表达意图，后端区间移动维护唯一连续排序，分类体系更清晰稳定。',
  'https://picsum.photos/seed/a_admin02_3/800/420',
  @cat_tech, 1, 53, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 0
);
SET @art := LAST_INSERT_ID();
INSERT INTO `article_tag` (article_id, tag_id, create_time) VALUES
(@art, @tag_db, NOW()),
(@art, @tag_fe, NOW());

COMMIT;

