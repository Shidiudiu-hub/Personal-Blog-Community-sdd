import { Axios } from './index'

/**
 * 分类API接口
 */
export default {
  /**
   * 分类列表
   * @returns {Promise}
   */
  list() {
    return Axios.get('/api/category/list')
  },

  /**
   * 新增分类
   * @param {Object} data - { name, description?, sortOrder? }
   * @returns {Promise}
   */
  add(data) {
    return Axios.post('/api/category/add', data)
  },

  /**
   * 更新分类
   * @param {Object} data - { categoryId, name?, description?, sortOrder? }
   * @returns {Promise}
   */
  update(data) {
    return Axios.post('/api/category/update', data)
  },

  /**
   * 删除分类
   * @param {Number} categoryId
   * @returns {Promise}
   */
  delete(categoryId) {
    return Axios.post('/api/category/delete', null, { params: { categoryId } })
  }
}
