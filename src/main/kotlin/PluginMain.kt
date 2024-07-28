package org.longchuanclub.mirai.plugin


import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import org.longchuanclub.mirai.plugin.Command.ImageEvent
import org.longchuanclub.mirai.plugin.Command.List2Image
import org.longchuanclub.mirai.plugin.Service.ImageService
import org.longchuanclub.mirai.plugin.config.LzConfig

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "io.huvz.laizhi",
        name = "laizhiXX",
        version = "0.4.0"
    ) {
        author("Huvz")
        info(
            """
            个人自用
            来只&来点 功能 将群友话语做成可以出发的图
        """.trimIndent()
        )
//        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
    }
) {


    override fun onEnable() {

        LzConfig.reload()

        CommandManager.registerCommand(List2Image)
        globalEventChannel().registerListenerHost(ImageEvent)
//        logger.info { "Plugin loaded" }
//        println(ImageService.selectImageDetail(114514))
    }

    override fun onDisable() {

    }

}


