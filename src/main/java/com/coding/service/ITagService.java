package com.coding.service;

import com.coding.entity.Tag;
import com.coding.utils.R;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface ITagService {

    List<Tag> list();

    List<Tag> hot();

    R<String> add(Tag tag);

    R<String> update(Tag tag);

    R<String> delete(Long tagId);
}
