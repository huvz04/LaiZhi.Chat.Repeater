package org.longchuanclub.mirai.plugin


import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.safeLoader
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info
import org.longchuanclub.mirai.plugin.Command.getImgList
import org.longchuanclub.mirai.plugin.Service.myEvent
import org.longchuanclub.mirai.plugin.config.LzConfig
import org.hibernate.*;
import xyz.cssxsh.mirai.hibernate.MiraiHibernateConfiguration
import xyz.cssxsh.mirai.hibernate.MiraiHibernateRecorder


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "io.huvz.laizhi",
        name = "来只XX",
        version = "0.2.6"
    ) {
        author("Huvz")
        info(
            """
            个人自用
            来只&来点 功能 将群友话语做成可以出发的图
        """.trimIndent()
        )
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
    }
) {

    public lateinit var  factory: SessionFactory
    override fun onEnable() {

        LzConfig.reload()
        CommandManager.registerCommand(getImgList)
        globalEventChannel().registerListenerHost(myEvent)
        logger.info { "Plugin loaded" }

    }
    private  fun initDatabase(){
        factory = MiraiHibernateConfiguration(plugin = this).buildSessionFactory()
    }
    override fun onDisable() {
    }

}


