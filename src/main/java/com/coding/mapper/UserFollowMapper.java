package com.coding.mapper;

import com.coding.entity.UserFollow;
import com.coding.vo.UserSimpleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 用户关注表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface UserFollowMapper extends Mapper<UserFollow> {

    /**
     * 查询关注关系
     */
    UserFollow selectByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 删除关注关系
     */
    int deleteByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 查询用户的关注列表
     */
    List<UserSimpleVO> selectFollowingList(@Param("userId") Long userId);

    /**
     * 查询用户的粉丝列表
     */
    List<UserSimpleVO> selectFollowerList(@Param("userId") Long userId);

    /**
     * 批量查询是否关注
     */
    List<Long> selectFollowingIdsByFollowerId(@Param("followerId") Long followerId, @Param("followingIds") List<Long> followingIds);
}
