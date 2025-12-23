import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

// 业务请求
const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  // timeout: 5000 // request timeout
})
// request interceptor
request.interceptors.request.use(
  config => {
    // do something before request is sent
    const userStore = useUserStore()
    if (userStore.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = userStore.token
      // config.headers['userId'] = userStore.userInfo.id
    }
    return config
  },
  error => {
    // do something with request error
    return Promise.reject(error)
  }
)

// response interceptor
request.interceptors.response.use(
  response => {
    // console.log('🚀 ~ file: request.js:37 ~ response:', response.data)
    const resData = response.data
    // if the custom status is not 20000, it is judged as an error.
    if (resData.status !== 0) {
      ElMessage({
        message: resData.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if ([50008, 50012, 50014].includes(resData.status)) {
        // to re-login
        ElMessageBox.confirm(
          'You have been logged out, you can cancel to stay on this page, or log in again',
          'Confirm logout',
          {
            confirmButtonText: 'Re-Login',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        ).then(() => {
          const userStore = useUserStore()
          userStore.resetToken.then(() => {
            location.reload()
          })
        })
      } else if (resData.status === 10) {
      } else {
        return Promise.reject(new Error(resData.msg || 'Error'))
      }
    } else {
      return resData
    }
  },
  error => {
    ElMessage({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)
export default request
