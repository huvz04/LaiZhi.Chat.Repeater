package util.skia.impl

import org.jetbrains.skia.Canvas
import org.longchuanclub.mirai.plugin.entity.ImageData
import org.longchuanclub.mirai.plugin.util.skia.ImageDrawer

class ImagePreviewDrawer(
    private val fileList: List<ImageData>,
    private val outputWidth: Int,
    private val outputHeight: Int,
    private val numImagesPerRow: Int,
    // 其他参数
) : ImageDrawer {
    override fun draw(canvas: Canvas) {
        // 绘制图片预览
    }
}