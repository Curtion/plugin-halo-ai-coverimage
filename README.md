# halo-ai-coverimage

Halo AI封面图插件

## 简介

接入`硅基流动`API, 实现发布文章且没有设置封面图时, 插件自动分析文章内容生成并设置封面图。

插件预设了一个提示词, 如果有不同风格要求可在插件设置中修改。

[在线预览 https://blog.3gxk.net/](https://blog.3gxk.net/)

[下载插件](https://github.com/Curtion/plugin-halo-ai-coverimage/releases)

## 开发环境

- Java 21+
- Node.js 18+
- pnpm

## 开发

```bash

# 前端开发
cd ui
pnpm install
pnpm dev

# 前端依赖升级

npx taze major -w

# 启动Halo
./gradlew haloServer

# 重新加载
./gradlew reload
```

## 构建

```bash
./gradlew build
```

构建完成后，可以在 `build/libs` 目录找到插件 jar 文件。

## 许可证

[GPL-3.0](./LICENSE) © Curtion 
