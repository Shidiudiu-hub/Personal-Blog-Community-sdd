// 使用 Node.js 内置的 https 模块
const https = require('https')
const querystring = require('querystring')
const fs = require('fs')
const path = require('path')

const params = {
  // baseUrl: 'https://genius-api.tulan.wang',
  baseUrl: 'https://bisai-api.tulan.wang/',
  version: 'openapi2',
  pathFilter: '/admin/**'
}

console.log('请求参数:', params)
// 使用内置 https 模块发起请求
function makeRequestWithHttps() {
  return new Promise((resolve, reject) => {
    const queryString = querystring.stringify(params)
    const url = `https://tools.tulan.wang/openapi/api?${queryString}`
    console.log('请求URL:', url)
    const req = https.get(url, (res) => {
      let data = ''

      res.on('data', (chunk) => {
        data += chunk
      })

      res.on('end', () => {
        try {
          resolve(JSON.parse(data))
        } catch (error) {
          resolve(data)
        }
      })
    })

    req.on('error', (error) => {
      console.error('请求失败:', error.message)
      reject(error)
    })

    req.setTimeout(10000, () => {
      req.destroy()
      reject(new Error('请求超时'))
    })
  })
}

// 执行请求
makeRequestWithHttps()
  .then((result) => {
    console.log('请求成功完成')

    // 写入文件功能
    const outputDir = 'src/api'

    // 生成文件名
    const fileName = 'AdminApi.js'
    const outputPath = path.join(outputDir, fileName)

    // 确保输出目录存在
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true })
      console.log(`📁 创建输出目录: ${outputDir}`)
    }

    // 将结果写入文件
    try {
      fs.writeFileSync(outputPath, result, 'utf8')
      console.log(`✅ 结果已成功写入文件: ${outputPath}`)
      console.log(`📊 文件大小: ${(result.length / 1024).toFixed(2)} KB`)
    } catch (writeError) {
      console.error('❌ 写入文件失败:', writeError.message)
    }
  })
  .catch((error) => {
    console.error('程序执行失败:', error.message)
  })
