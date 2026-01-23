export function downloadFn(flow, filename) {
  if (!flow) return
  const blob = new Blob([flow])
  const blobUrl = window.URL.createObjectURL(blob)

  const a = document.createElement('a')
  a.style.display = 'none'
  a.download = filename // 自定义下载的文件名
  a.href = blobUrl
  a.click()
}
