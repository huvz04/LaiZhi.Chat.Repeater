package org.longchuanclub.mirai.plugin.entity

import jakarta.persistence.*

@Entity
@Table(name="image_file")
data class ImageFile (
    @Id
    @GeneratedValue
    /**
     * 主键
     */
    var id: Long= 1,
    /**
     * md5 唯一标识
     */
    var md5: String?="2",
    /**
     * 绑定群聊：群qq号
     */
    var qq: String?="你群",
    /**
     * 发送次数
     */
    var count :Long = 0L,
    /**
     * 关键字
     */
    var about:String="",
    /**
     * 图片类型
     */
    var type:String="",
    )