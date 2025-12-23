<template>
  <div class="vue-cropper-container" v-loading="cropperLoading" element-loading-text="裁剪上传中...">
    <div ref="cropperWrapperRef" class="cropper-wrapper" v-if="cropperDataInit.imageUrl">
      <vue-cropper
        ref="cropperRef"
        :img="cropperDataInit.imageUrl"
        :outputSize="1"
        :outputType="outputType"
        :info="true"
        :autoCrop="true"
        :auto-crop-width="cropperDataInit.width"
        :auto-crop-height="cropperDataInit.height"
        :canScale="true"
        :centerBox="true"
        :fixed="cropperDataInit.fixed"
        :fixedBox="true"
        :fixedNumber="cropperDataInit.fixedNumber"
        :canMove="false"
        :canMoveBox="true"
        :original="false"
        :infoTrue="false"
        :enlarge="1"
        :full="true"
        :mode="cropperDataInit.mode"
      />
    </div>
    <div class="cropper-actions" v-if="cropperDataInit.imageUrl">
      <el-button @click="resetCropper" type="default">重置</el-button>
      <el-button @click="rotateLeft" type="default">向左旋转</el-button>
      <el-button @click="rotateRight" type="default">向右旋转</el-button>
      <el-button @click="postCropper" type="success">确定</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, toRefs } from 'vue'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'

// Props定义
const props = defineProps({
  cropperData: {
    type: Object
  },
  // 输出图片类型
  outputType: {
    type: String,
    default: 'png'
  }
})
// Emits定义
const emit = defineEmits(['crop-blob'])
const { cropperData, outputType } = toRefs(props)
// 组件引用
const cropperRef = ref(null)
const cropperWrapperRef = ref(null)
const cropperDataInit = ref({
  mode: '',
  fixed: false,
  width: 0,
  height: 0,
  imageUrl: '',
  fixedNumber: [1, 1]
})
const cropperLoading = ref(false)
// 重置裁剪器
const resetCropper = () => {
  cropperRef.value?.refresh()
}
// 向左旋转
const rotateLeft = () => {
  cropperRef.value?.rotateLeft()
}
// 向右旋转
const rotateRight = () => {
  cropperRef.value?.rotateRight()
}
// blob转file
const blobToFile = (blob, fileName) => {
  return new Promise((resolve, reject) => {
    const file = new File([blob], fileName, { type: blob.type })
    resolve(file)
  })
}
// file转blob
const fileToBlob = file => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      resolve(reader.result)
    }
  })
}
// 转换为blob
fileToBlob(props.cropperData.imageSrc).then(file => {
  let newImg = new Image()
  newImg.src = file
  newImg.onload = () => {
    let setW = props.cropperData.width ? props.cropperData.width : newImg.width
    let setH = props.cropperData.height ? props.cropperData.height : newImg.height
    let fixedNum = setW / setH
    cropperDataInit.value = {
      newImgWidth: newImg.width,
      newImgHeight: newImg.height,
      mode: props.cropperData.width ? 'auto' : 'contain',
      fixed: fixedNum == 1 ? true : false,
      width: setW,
      height: setH,
      fixedNumber: [fixedNum, 1],
      imageUrl: file
    }
  }
})
// 获取裁剪图片
const postCropper = () => {
  cropperLoading.value = true
  cropperRef.value?.getCropBlob(blob => {
    resizeBlob(blob, cropperDataInit.value.width, cropperDataInit.value.height).then(resizedBlob => {
      emit('crop-blob', resizedBlob)
    })
    setTimeout(() => {
      cropperLoading.value = false
    }, 500)
  })
}
// 缩放图片
const resizeBlob = (blob, desiredWidth, desiredHeight) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')

      // 计算缩放比例
      const scaleX = desiredWidth / img.width
      const scaleY = desiredHeight / img.height
      const scale = Math.min(scaleX, scaleY)

      // 设置 Canvas 的宽度和高度
      canvas.width = desiredWidth
      canvas.height = desiredHeight

      // 绘制图片到 Canvas 上，并进行缩放
      ctx.drawImage(img, 0, 0, img.width * scale, img.height * scale)

      // 将 Canvas 中的图像转换为 Blob 对象
      canvas.toBlob(resizedBlob => {
        resolve(resizedBlob)
      }, blob.type)
    }

    img.onerror = error => {
      reject(error)
    }
    img.src = window.URL.createObjectURL(blob)
  })
}
</script>

<style scoped lang="scss">
.vue-cropper-container {
  width: 100%;
  margin: 0 auto;
}

.vue-cropper-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  margin-bottom: 10px;

  .vue-cropper-header-item {
    display: flex;
    align-items: center;
    gap: 10px;
  }
}

.cropper-wrapper {
  width: 100%;
  height: 65vh;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.cropper-actions {
  margin: 20px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.cropper-actions .el-button {
  margin: 0;
}

.preview-container {
  margin-top: 20px;
  text-align: center;
}

.preview-container h4 {
  margin-bottom: 10px;
  color: #606266;
}

.preview-image {
  display: inline-block;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}
</style>
