import { Axios } from './index'

/**
 * 评论API接口
 */
export default {
  /**
   * 发表评论
   * @param {Object} data - { articleId, parentId?, replyUserId?, content }
   * @returns {Promise}
   */
  add(data) {
    return Axios.post('/api/comment/add', data)
  },

  /**
   * 删除评论
   * @param {Number} commentId
   * @returns {Promise}
   */
  delete(commentId) {
    return Axios.post('/api/comment/delete', null, { params: { commentId } })
  },

  /**
   * 获取文章评论列表
   * @param {Number} articleId
   * @returns {Promise}
   */
  list(articleId) {
    return Axios.get('/api/comment/list', { params: { articleId } })
  },

  /**
   * 获取评论的回复列表
   * @param {Number} parentId
   * @returns {Promise}
   */
  replies(parentId) {
    return Axios.get('/api/comment/replies', { params: { parentId } })
  },

  /**
   * 点赞/取消点赞评论
   * @param {Number} commentId
   * @returns {Promise}
   */
  like(commentId) {
    return Axios.post('/api/comment/like', null, { params: { commentId } })
  },

  /**
   * 获取文章评论数
   * @param {Number} articleId
   * @returns {Promise}
   */
  count(articleId) {
    return Axios.get('/api/comment/count', { params: { articleId } })
  }
}
