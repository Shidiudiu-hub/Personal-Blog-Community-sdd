-- ============================================
-- 个人博客社区系统数据库触发器脚本
-- 用于维护分类和标签的文章数量统计
-- 数据库: MySQL/H2 (MODE=MySQL)
-- 创建日期: 2026-01-26
-- ============================================

-- ============================================
-- 1. 分类文章数统计触发器
-- ============================================

-- 删除已存在的触发器（如果存在）
DROP TRIGGER IF EXISTS `trg_article_insert_category_count`;
DROP TRIGGER IF EXISTS `trg_article_update_category_count`;
DROP TRIGGER IF EXISTS `trg_article_delete_category_count`;

-- 1.1 文章插入后，更新分类文章数（仅统计已发布且未删除的文章）
DELIMITER $$
CREATE TRIGGER `trg_article_insert_category_count`
AFTER INSERT ON `article`
FOR EACH ROW
BEGIN
    IF NEW.category_id IS NOT NULL AND NEW.status = 1 AND NEW.deleted = 0 THEN
        UPDATE `category` 
        SET `article_count` = (
            SELECT COUNT(*) 
            FROM `article` 
            WHERE `category_id` = NEW.category_id 
            AND `status` = 1 
            AND `deleted` = 0
        )
        WHERE `category_id` = NEW.category_id;
    END IF;
END$$
DELIMITER ;

-- 1.2 文章更新后，更新分类文章数
DELIMITER $$
CREATE TRIGGER `trg_article_update_category_count`
AFTER UPDATE ON `article`
FOR EACH ROW
BEGIN
    -- 如果分类ID改变，需要更新旧分类和新分类的文章数
    IF OLD.category_id IS NOT NULL AND (OLD.category_id != NEW.category_id OR OLD.status != NEW.status OR OLD.deleted != NEW.deleted) THEN
        UPDATE `category` 
        SET `article_count` = (
            SELECT COUNT(*) 
            FROM `article` 
            WHERE `category_id` = OLD.category_id 
            AND `status` = 1 
            AND `deleted` = 0
        )
        WHERE `category_id` = OLD.category_id;
    END IF;
    
    -- 如果新分类ID不为空，更新新分类的文章数
    IF NEW.category_id IS NOT NULL AND (OLD.category_id != NEW.category_id OR OLD.status != NEW.status OR OLD.deleted != NEW.deleted) THEN
        UPDATE `category` 
        SET `article_count` = (
            SELECT COUNT(*) 
            FROM `article` 
            WHERE `category_id` = NEW.category_id 
            AND `status` = 1 
            AND `deleted` = 0
        )
        WHERE `category_id` = NEW.category_id;
    END IF;
END$$
DELIMITER ;

-- 1.3 文章删除后，更新分类文章数（已合并到 update 触发器中，此触发器可删除）
-- 注意：文章删除逻辑已包含在 trg_article_update_category_count 中

-- ============================================
-- 2. 标签文章数统计触发器
-- ============================================

-- 删除已存在的触发器（如果存在）
DROP TRIGGER IF EXISTS `trg_article_tag_insert_tag_count`;
DROP TRIGGER IF EXISTS `trg_article_tag_delete_tag_count`;
DROP TRIGGER IF EXISTS `trg_article_update_tag_count`;

-- 2.1 文章标签关联插入后，更新标签文章数（仅统计已发布且未删除的文章）
DELIMITER $$
CREATE TRIGGER `trg_article_tag_insert_tag_count`
AFTER INSERT ON `article_tag`
FOR EACH ROW
BEGIN
    UPDATE `tag` 
    SET `article_count` = (
        SELECT COUNT(DISTINCT at.article_id)
        FROM `article_tag` at
        INNER JOIN `article` a ON at.article_id = a.article_id
        WHERE at.tag_id = NEW.tag_id 
        AND a.status = 1 
        AND a.deleted = 0
    )
    WHERE `tag_id` = NEW.tag_id;
END$$
DELIMITER ;

-- 2.2 文章标签关联删除后，更新标签文章数
DELIMITER $$
CREATE TRIGGER `trg_article_tag_delete_tag_count`
AFTER DELETE ON `article_tag`
FOR EACH ROW
BEGIN
    UPDATE `tag` 
    SET `article_count` = (
        SELECT COUNT(DISTINCT at.article_id)
        FROM `article_tag` at
        INNER JOIN `article` a ON at.article_id = a.article_id
        WHERE at.tag_id = OLD.tag_id 
        AND a.status = 1 
        AND a.deleted = 0
    )
    WHERE `tag_id` = OLD.tag_id;
END$$
DELIMITER ;

-- 2.3 文章状态或删除状态改变时，更新相关标签的文章数
DELIMITER $$
CREATE TRIGGER `trg_article_update_tag_count`
AFTER UPDATE ON `article`
FOR EACH ROW
BEGIN
    -- 如果文章状态或删除状态改变，更新该文章关联的所有标签的文章数
    IF (OLD.status != NEW.status OR OLD.deleted != NEW.deleted) THEN
        UPDATE `tag` t
        SET `article_count` = (
            SELECT COUNT(DISTINCT at.article_id)
            FROM `article_tag` at
            INNER JOIN `article` a ON at.article_id = a.article_id
            WHERE at.tag_id = t.tag_id 
            AND a.status = 1 
            AND a.deleted = 0
        )
        WHERE EXISTS (
            SELECT 1 FROM `article_tag` at WHERE at.tag_id = t.tag_id AND at.article_id = NEW.article_id
        );
    END IF;
END$$
DELIMITER ;

-- ============================================
-- 3. 初始化脚本：更新现有数据的文章数
-- ============================================

-- 3.1 更新所有分类的文章数（仅统计已发布且未删除的文章）
UPDATE `category` c
SET `article_count` = (
    SELECT COALESCE(COUNT(*), 0)
    FROM `article` a
    WHERE a.category_id = c.category_id
    AND a.status = 1
    AND a.deleted = 0
)
WHERE c.deleted = 0;

-- 3.2 更新所有标签的文章数（仅统计已发布且未删除的文章）
UPDATE `tag` t
SET `article_count` = (
    SELECT COALESCE(COUNT(DISTINCT at.article_id), 0)
    FROM `article_tag` at
    INNER JOIN `article` a ON at.article_id = a.article_id
    WHERE at.tag_id = t.tag_id
    AND a.status = 1
    AND a.deleted = 0
)
WHERE t.deleted = 0;

-- ============================================
-- 注意：
-- 1. 触发器仅统计 status=1（已发布）且 deleted=0（未删除）的文章
-- 2. 标签的文章数统计去重，因为一篇文章可能有多个标签
-- 3. 如果使用某些 MySQL 客户端（如 Navicat、DBeaver），可能需要：
--    - 在客户端设置中启用"允许多个语句"
--    - 或者将每个 DELIMITER 块单独执行
-- 4. 如果使用 H2 数据库，DELIMITER 语法不支持，需要移除 DELIMITER 语句
-- ============================================
