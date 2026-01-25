import { Axios } from './index'

/**
 * 文章API接口
 */
export default {
  /**
   * 发布文章
   * @param {Object} data - { title, content, summary?, coverImage?, categoryId?, tagIds?, status }
   * @returns {Promise}
   */
  publish(data) {
    return Axios.post('/api/article/publish', data)
  },

  /**
   * 更新文章
   * @param {Object} data - { articleId, title?, content?, summary?, coverImage?, categoryId?, tagIds?, status? }
   * @returns {Promise}
   */
  update(data) {
    return Axios.post('/api/article/update', data)
  },

  /**
   * 删除文章
   * @param {Number} articleId
   * @returns {Promise}
   */
  delete(articleId) {
    return Axios.post('/api/article/delete', null, { params: { articleId } })
  },

  /**
   * 我的文章列表
   * @param {Object} params - { page, size }
   * @returns {Promise}
   */
  myList(params) {
    return Axios.get('/api/article/my-list', { params })
  },

  /**
   * 文章列表
   * @param {Object} params - { categoryId?, tagId?, keyword?, orderBy?, page, size }
   * @returns {Promise}
   */
  list(params) {
    return Axios.get('/api/article/list', { params })
  },

  /**
   * 文章详情
   * @param {Number} articleId
   * @returns {Promise}
   */
  detail(articleId) {
    return Axios.get('/api/article/detail', { params: { articleId } })
  },

  /**
   * 搜索文章
   * @param {Object} params - { keyword, page, size }
   * @returns {Promise}
   */
  search(params) {
    return Axios.get('/api/article/search', { params })
  },

  /**
   * 点赞/取消点赞文章
   * @param {Number} articleId
   * @returns {Promise}
   */
  like(articleId) {
    return Axios.post('/api/article/like', null, { params: { articleId } })
  },

  /**
   * 获取文章点赞状态
   * @param {Number} articleId
   * @returns {Promise}
   */
  getLikeStatus(articleId) {
    return Axios.get('/api/article/like-status', { params: { articleId } })
  },

  /**
   * 我的点赞列表
   * @param {Object} params - { page, size }
   * @returns {Promise}
   */
  myLikes(params) {
    return Axios.get('/api/article/my-likes', { params })
  },

  /**
   * 收藏/取消收藏文章
   * @param {Number} articleId
   * @returns {Promise}
   */
  collect(articleId) {
    return Axios.post('/api/article/collect', null, { params: { articleId } })
  },

  /**
   * 获取文章收藏状态
   * @param {Number} articleId
   * @returns {Promise}
   */
  getCollectStatus(articleId) {
    return Axios.get('/api/article/collect-status', { params: { articleId } })
  },

  /**
   * 我的收藏列表
   * @param {Object} params - { page, size }
   * @returns {Promise}
   */
  myCollects(params) {
    return Axios.get('/api/article/my-collects', { params })
  }
}
