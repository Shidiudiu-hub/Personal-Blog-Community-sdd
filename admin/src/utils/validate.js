/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function isString(str) {
  if (typeof str === 'string' || str instanceof String) {
    return true
  }
  return false
}

/**
 * @param {Array} arg
 * @returns {Boolean}
 */
export function isArray(arg) {
  if (typeof Array.isArray === 'undefined') {
    return Object.prototype.toString.call(arg) === '[object Array]'
  }
  return Array.isArray(arg)
}

/**
 * 身份证号码校验
 * @param {string} idCard 身份证号码
 * @returns {Boolean}
 */
export function validateIdCard(idCard) {
  if (!idCard) return false

  // 18位身份证号码正则表达式
  const reg = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/

  if (!reg.test(idCard)) {
    return false
  }

  // 校验码验证
  const factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  const parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2]
  const code = idCard.substring(17)

  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(idCard[i]) * factor[i]
  }

  return parity[sum % 11] == code.toUpperCase()
}
