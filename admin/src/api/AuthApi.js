import { Axios } from './index'

/**
 * 认证API接口
 */
export default {
  /**
   * 用户注册
   * @param {Object} data - RegisterParam
   * @returns {Promise}
   */
  register(data) {
    return Axios.post('/api/auth/register', data)
  },

  /**
   * 用户登录
   * @param {Object} data - LoginParam {username, password}
   * @returns {Promise}
   */
  login(data) {
    return Axios.post('/api/auth/login', data)
  },

  /**
   * 获取当前用户信息
   * @returns {Promise}
   */
  getCurrentUser() {
    return Axios.get('/api/auth/info')
  },

  /**
   * 检查用户名是否可用
   * @param {String} username
   * @returns {Promise}
   */
  checkUsername(username) {
    return Axios.get('/api/auth/check-username', { params: { username } })
  }
}

