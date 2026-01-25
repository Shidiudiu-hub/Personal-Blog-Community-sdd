import { Axios } from './index'

/**
 * 标签API接口
 */
export default {
  /**
   * 标签列表
   * @returns {Promise}
   */
  list() {
    return Axios.get('/api/tag/list')
  },

  /**
   * 热门标签
   * @returns {Promise}
   */
  hot() {
    return Axios.get('/api/tag/hot')
  },

  /**
   * 新增标签
   * @param {Object} data - { name, color? }
   * @returns {Promise}
   */
  add(data) {
    return Axios.post('/api/tag/add', data)
  },

  /**
   * 更新标签
   * @param {Object} data - { tagId, name?, color? }
   * @returns {Promise}
   */
  update(data) {
    return Axios.post('/api/tag/update', data)
  },

  /**
   * 删除标签
   * @param {Number} tagId
   * @returns {Promise}
   */
  delete(tagId) {
    return Axios.post('/api/tag/delete', null, { params: { tagId } })
  }
}
