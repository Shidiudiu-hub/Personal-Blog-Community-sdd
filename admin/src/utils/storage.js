import jsCookie from 'js-cookie'

const localStorage = window.localStorage

const sessionStorage = window.sessionStorage

/**
 * @method setLocalStorageItem
 * @param {string} key
 * @param {*} value
 * @returns
 */
export const setLocalStorageItem = (key, value) => localStorage.setItem(key, JSON.stringify(value))

/**
 * @method getLocalStorageItem
 * @param {string} key
 * @param {*} defaultValue
 * @returns
 */
export const getLocalStorageItem = (key, defaultValue = null) => {
  const value = localStorage.getItem(key)

  return value ? JSON.parse(value) : defaultValue
}

/**
 * @method removeLocalStorageItem
 * @param {string} key
 * @returns
 */
export const removeLocalStorageItem = key => localStorage.removeItem(key)

/**
 * @method clearLocalStorageItem
 * @returns
 */
export const clearLocalStorageItem = () => localStorage.clear()

/**
 * @method setSessionStorage
 * @param {string} key
 * @param {*} value
 * @returns
 */
export const setSessionStorage = (key, value) => sessionStorage.setItem(key, JSON.stringify(value))

/**
 * @method getSessionStorage
 * @param {string} key
 * @param {*} defaultValue
 * @returns
 */
export const getSessionStorage = (key, defaultValue = null) => {
  const value = sessionStorage.getItem(key)

  return value ? JSON.parse(value) : defaultValue
}

/**
 * @method removeSessionStorage
 * @param {string} key
 * @returns
 */
export const removeSessionStorage = key => sessionStorage.removeItem(key)

/**
 * @method clearSessionStorage
 * @returns
 */
export const clearSessionStorage = () => sessionStorage.clear()

/**
 * @method setCookieItem
 * @param {string} key
 * @param {*} value
 * @returns
 */
export const setCookieItem = (key, value) => {
  // 确保值不是undefined
  if (value === undefined) {
    value = null
  }
  return jsCookie.set(key, JSON.stringify(value))
}

/**
 * @method getCookieItem
 * @param {string} key
 * @param {*} defaultValue
 * @returns
 */
export const getCookieItem = (key, defaultValue = null) => {
  const value = jsCookie.get(key)

  // 处理值为undefined字符串或空值的情况
  if (!value || value === 'undefined' || value === 'null') {
    return defaultValue
  }

  try {
    return JSON.parse(value)
  } catch (error) {
    // 如果解析失败，返回原始值或默认值
    return defaultValue
  }
}

/**
 * @method removeCookieItem
 * @param {string} key
 * @returns
 */
export const removeCookieItem = key => jsCookie.remove(key)
