module.exports = {
  // 每行最大字符数，超过则换行
  printWidth: 120,
  // 一个tab代表几个空格数
  tabWidth: 2,
  // 是否使用tab进行缩进，false表示用空格进行缩进
  useTabs: false,
  // 字符串是否使用单引号
  singleQuote: true,
  // 行尾是否使用分号
  semi: false,
  // 是否使用尾逗号，有三个可选值"<none|es5|all>"
  trailingComma: 'none',
  // 对象大括号直接是否有空格，效果：{ foo: bar }
  bracketSpacing: true,
  // 箭头函数参数只有一个时是否要有小括号
  arrowParens: 'avoid',
  // 换行符使用 lf
  endOfLine: 'lf',
  // 是否在对象字面量的括号之间打印空格
  bracketSameLine: false,
  // 是否在 Vue 文件中缩进 <script> 和 <style> 标签
  vueIndentScriptAndStyle: false,
  // 解析器
  // parser: 'vue-eslint-parser',
  // 覆盖特定文件的配置
  overrides: [
    {
      files: '*.vue',
      options: {
        parser: 'vue'
      }
    },
    {
      files: '*.js',
      options: {
        parser: 'babel'
      }
    },
    {
      files: '*.ts',
      options: {
        parser: 'typescript'
      }
    }
  ]
}
