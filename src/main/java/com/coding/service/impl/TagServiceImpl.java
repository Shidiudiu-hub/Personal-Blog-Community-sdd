package com.coding.service.impl;

import com.coding.entity.Tag;
import com.coding.mapper.TagMapper;
import com.coding.service.ITagService;
import com.coding.utils.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class TagServiceImpl implements ITagService {

    private final TagMapper tagMapper;

    @Override
    public List<Tag> list() {
        Example example = new Example(Tag.class);
        example.createCriteria().andEqualTo("deleted", 0);
        return tagMapper.selectByExample(example);
    }

    @Override
    public List<Tag> hot() {
        return tagMapper.selectHotTags();
    }

    @Override
    public R<String> add(Tag tag) {
        if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
            return R.createByErrorMessage("标签名称不能为空");
        }
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        if (tag.getDeleted() == null) tag.setDeleted(0);
        if (tag.getArticleCount() == null) tag.setArticleCount(0);
        if (tag.getColor() == null || tag.getColor().isEmpty()) tag.setColor("#409EFF");
        int cnt = tagMapper.insertSelective(tag);
        if (cnt == 0) {
            return R.createByErrorMessage("添加失败");
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> update(Tag tag) {
        if (tag == null || tag.getTagId() == null) {
            return R.createByErrorMessage("参数有误");
        }
        Tag exist = tagMapper.selectByPrimaryKey(tag.getTagId());
        if (exist == null) {
            return R.createByErrorMessage("标签不存在");
        }
        tag.setUpdateTime(LocalDateTime.now());
        int cnt = tagMapper.updateByPrimaryKeySelective(tag);
        if (cnt == 0) {
            return R.createBySuccess();
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> delete(Long tagId) {
        if (tagId == null) {
            return R.createByErrorMessage("参数有误");
        }
        Tag exist = tagMapper.selectByPrimaryKey(tagId);
        if (exist == null) {
            return R.createByErrorMessage("标签不存在");
        }
        Tag u = new Tag();
        u.setTagId(tagId);
        u.setDeleted(1);
        u.setUpdateTime(LocalDateTime.now());
        int cnt = tagMapper.updateByPrimaryKeySelective(u);
        if (cnt == 0) {
            return R.createByErrorMessage("删除失败");
        }
        return R.createBySuccess();
    }
}
