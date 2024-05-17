package util.skia.impl

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.toImage
import org.longchuanclub.mirai.plugin.util.skia.ImageDrawer
import javax.imageio.ImageIO

class BackgroundDrawer(
    private val outputWidth: Int,
    private val outputHeight: Int,
    private val titleText: String
) : ImageDrawer {
    override fun draw(canvas: Canvas) {
        val imagePaint = Paint().apply {
            isAntiAlias = true
        }

        val inputStream = this::class.java.getResourceAsStream("/image/01.jpg")
        val originalImage = ImageIO.read(inputStream).toImage()

        // 计算缩放比例
        val aspectRatio = originalImage.width.toFloat() / originalImage.height.toFloat()
        val targetWidth = outputWidth.toFloat()
        val targetHeight = targetWidth / aspectRatio
        // 缩放原始图像并绘制到新的图像对象上
        canvas.drawImageRect(
            originalImage,
            Rect.makeXYWH(0f, 0f, targetWidth, targetHeight),
            imagePaint
        )
    }
}



