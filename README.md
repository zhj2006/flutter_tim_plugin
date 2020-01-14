# flutter dim

基于腾讯云im 封装的一个 flutter im库

## 前期准备
[融云官网](https://developer.rongcloud.cn/signup/?utm_source=IMfluttergithub&utm_term=Imsign) 申请开发者账号

通过管理后台的 "基本信息"->"App Key" 获取 AppKey

通过管理后台的 "IM 服务"—>"API 调用"->"用户服务"->"获取 Token"，通过用户 id 获取 IMToken


因为这个库是基于腾讯云im的，因此需要去云im申请一个应用，阅读这篇[文章](https://github.com/tencentyun/TIMSDK/tree/master/Android)可以获得以下知识：

1、`appid`怎么来的

2、`账号`及其对应的`sig`如何来的，已经推荐的sig的生成方式（当然这个是后台同学关注的）。



## 使用 dim
dim的使用非常简单，只需引入这个库就可以使用了。

```dart
dependencies:
  dim: ^0.2.6
```

## 已有的功能

1. 初始化
2. 登录
3. 登出
4. 获取会话列表
5. 删除一个会话
6. 获取漫游和本地消息
7. 发送图片消息
8. 发送文本消息
9. 发送地理位置消息
10. 发送音频消息
11. 发送视频消息
12. 发送小文件消息
13. 发送自定义消息
14. 设置消息已读上报
15. 回撤消息
16. 创建群组
17. 邀请入群
18. 申请入群
19. 获取用户资料
20. 设置用户资料
21. 监听新消息回调
22. 监听用户状态回调
    
