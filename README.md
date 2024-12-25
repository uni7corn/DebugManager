# DebugManager

#### 介绍
Dosktop端调试车机android系统的软件，采用Compose Desktop。
关于字符串解析采用了大量的特判，不同设备上，可能会有一些问题。

#### 声明
本软件仅用于学习交流，请勿用于非法用途，否则后果自负。

#### 使用的个人开源库
此项目所使用的个人开发者的开源组件有：
1. 模仿微信的weui设计的compose版本，使用了其中的一些写好的组件，拿来改制使用https://gitee.com/chengdongqing/weui
2. adb client, 建立基于adb的socket通信，简化了一些功能的解析流程，例如文件列表的显示。https://github.com/vidstige/jadb
3. 架构设计上稍微模仿了一下adb pad，可惜还是没有模仿到位，越来越忙，时间上也不允许再去优化架构了。https://github.com/kaleidot725/AdbPad