# halo-ai-coverimage

Halo AI封面图插件

## 简介

接入`硅基流动`API, 实现发布文章且没有设置封面图时, 插件自动分析文章内容生成并设置封面图。

插件预设了一个提示词, 如果有不同风格要求可在插件设置中修改。

[在线预览 https://blog.3gxk.net/](https://blog.3gxk.net/)

[下载插件](https://github.com/Curtion/plugin-halo-ai-coverimage/releases)

## 下一步

 - [ ] 更多的API接入

预计应该使用`Spring AI`或者其他类似方案来实现大多数API计入, 欢迎PR

## 开发环境

- Java 21+
- Node.js 18+
- pnpm

## 开发

```bash
# 构建插件
./gradlew build

# 开发前端
cd ui
pnpm install
pnpm dev
```

## 构建

```bash
./gradlew build
```

构建完成后，可以在 `build/libs` 目录找到插件 jar 文件。

## 许可证

[GPL-3.0](./LICENSE) © Curtion 

# 赞助

![1](./doc/支付宝.jpg)
![2](./doc/微信.jpg)