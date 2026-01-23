<template>
  <div class="precise-stopwatch">
    <div class="time-display">
      <span class="time-unit">{{ formatTime(hours) }}</span>
      <span class="separator">:</span>
      <span class="time-unit">{{ formatTime(minutes) }}</span>
      <span class="separator">:</span>
      <span class="time-unit">{{ formatTime(seconds) }}</span>
      <span class="separator">.</span>
      <span class="milliseconds">{{ formatMilliseconds(milliseconds) }}</span>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, watch, onUnmounted } from 'vue'

// 定义组件属性
const props = defineProps({
  // 开始时间的时间戳
  startTime: {
    type: [Number, String],
    default: null
  },
  // 结束时间的时间戳
  endTime: {
    type: [Number, String],
    default: null
  },
  // 当前系统时间差值
  nowTimeDiff: {
    type: [Number, String],
    default: null
  },
  // 效率倍率
  percent: {
    type: Number,
    default: 100
  }
})
// 定时器ID
let intervalId = null
// 时间状态
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
const milliseconds = ref(0)

// 格式化时间（补零）
const formatTime = value => {
  return value.toString().padStart(2, '0')
}

// 格式化毫秒（显示两位数）
const formatMilliseconds = value => {
  // 只显示前两位毫秒数
  return Math.floor(value / 10)
    .toString()
    .padStart(2, '0')
}

// 开始计时
const startTimer = () => {
  // 清除现有的定时器
  if (intervalId) {
    clearInterval(intervalId)
  }

  // 重置时间
  hours.value = 0
  minutes.value = 0
  seconds.value = 0
  milliseconds.value = 0

  // 获取开始时间
  const startTime = props.startTime || Date.now()
  const endTime = props.endTime

  // 如果有结束时间，直接计算并显示最终时间
  if (endTime) {
    const elapsed = (endTime - startTime) * (props.percent / 100);
    if (elapsed > 0) {
      // 更新毫秒
      milliseconds.value = elapsed % 1000
      // 更新秒
      seconds.value = Math.floor(elapsed / 1000) % 60
      // 更新分钟
      minutes.value = Math.floor(elapsed / (1000 * 60)) % 60
      // 更新小时
      hours.value = Math.floor(elapsed / (1000 * 60 * 60))
    }
    return // 不启动定时器
  }

  // 记录开始时间
  const startTimestamp = startTime

  // 启动定时器，每10毫秒更新一次
  intervalId = setInterval(() => {
    // 计算经过的时间
    // const elapsed = Date.now() - props.nowTimeDiff - startTimestamp
    const elapsed = (Date.now() - startTimestamp) * (props.percent / 100)
    // 更新毫秒
    milliseconds.value = elapsed % 1000

    // 更新秒
    seconds.value = Math.floor(elapsed / 1000) % 60

    // 更新分钟
    minutes.value = Math.floor(elapsed / (1000 * 60)) % 60

    // 更新小时
    hours.value = Math.floor(elapsed / (1000 * 60 * 60))
  }, 10)
}

// 停止计时
const stopTimer = () => {
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
}

// 监听开始时间和结束时间变化
watch(
  [() => props.startTime, () => props.endTime],
  ([newStartTime, newEndTime]) => {
    if (newStartTime) {
      startTimer()
    } else {
      stopTimer()
      // 重置时间
      hours.value = 0
      minutes.value = 0
      seconds.value = 0
      milliseconds.value = 0
    }
  },
  { immediate: true }
)
// watch(
//   () => props.nowTimeDiff,
//   (newNowTimeDiff) => {
//     if (newNowTimeDiff && props.startTime && !props.endTime) {
//       startTimer()
//     }
//   }
// )
// 组件卸载时清理定时器
onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped lang="scss">
.precise-stopwatch {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;

  .time-display {
    display: flex;
    font-size: 28px;
    font-weight: bold;
    font-family: 'Courier New', monospace;
    color: #303133;

    .time-unit {
      display: inline-block;
      min-width: 2ch;
      text-align: center;
    }

    .milliseconds {
      display: inline-block;
      min-width: 2ch;
      text-align: center;
    }

    .separator {
      margin: 0 5px;
    }
  }
}
</style>
