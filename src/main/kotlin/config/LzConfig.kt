package org.longchuanclub.mirai.plugin.config


import net.mamoe.mirai.console.data.*


@PublishedApi
internal object LzConfig : AutoSavePluginConfig("LaiZhiConfig"){
    /**
     * 指令列表
     * 默认为"[来点]","[来只]"
     */

    @ValueDescription("触发-添加图库-指令")
    val AddcommandList:List<String> by value(listOf("add","添加"))

    @ValueDescription("开关欢迎")
    val openWelcome: Boolean by value(false)
    @ValueDescription("存储目录 *不可用")
    var archiveDirectory: String by value()
    @ValueDescription("管理员QQ")
    var adminQQid:Long by value()

    @ValueDescription("受保护的图库")
    var ProtectImageList:List<String?> by value()

    @ValueDescription("消息发送延迟")
    var messageIntervalTime :Long by value(500L);

    @ValueDescription("黑名单")
    var Blacklist:List<Long> by value()

    @ValueDescription("获取图库指令列表")
    var Graphicslist:List<String> by value(listOf("#获取图库","#图库","本群图库"))



}