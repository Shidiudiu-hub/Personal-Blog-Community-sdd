import { Axios } from './index'

/**
 * 用户API接口
 */
export default {
  /**
   * 获取用户信息
   * @param {Number} userId - 用户ID，不传则获取当前用户
   * @returns {Promise}
   */
  getInfo(userId) {
    return Axios.get('/api/user/info', { params: { userId } })
  },

  /**
   * 获取个人资料
   * @returns {Promise}
   */
  getProfile() {
    return Axios.get('/api/user/profile')
  },

  /**
   * 更新个人资料
   * @param {Object} data - { username?, avatar?, email?, phone?, bio? }
   * @returns {Promise}
   */
  updateProfile(data) {
    return Axios.post('/api/user/update-profile', data)
  },

  /**
   * 更新头像
   * @param {String} avatar - 头像URL
   * @returns {Promise}
   */
  updateAvatar(avatar) {
    return Axios.post('/api/user/update-avatar', { avatar })
  },

  /**
   * 修改密码
   * @param {Object} data - { oldPassword, newPassword }
   * @returns {Promise}
   */
  changePassword(data) {
    return Axios.post('/api/user/change-password', data)
  },

  /**
   * 获取用户统计数据
   * @param {Number} userId - 用户ID，不传则获取当前用户
   * @returns {Promise}
   */
  getStatistics(userId) {
    return Axios.get('/api/user/statistics', { params: { userId } })
  },

  /**
   * 关注/取消关注用户
   * @param {Number} userId - 被关注用户ID
   * @returns {Promise}
   */
  follow(userId) {
    return Axios.post('/api/user/follow', null, { params: { userId } })
  },

  /**
   * 获取关注状态
   * @param {Number} userId - 被关注用户ID
   * @returns {Promise}
   */
  getFollowStatus(userId) {
    return Axios.get('/api/user/follow-status', { params: { userId } })
  },

  /**
   * 获取关注列表
   * @param {Object} params - { userId?, page, size }
   * @returns {Promise}
   */
  getFollowingList(params) {
    return Axios.get('/api/user/following-list', { params })
  },

  /**
   * 获取粉丝列表
   * @param {Object} params - { userId?, page, size }
   * @returns {Promise}
   */
  getFollowerList(params) {
    return Axios.get('/api/user/follower-list', { params })
  }
}
