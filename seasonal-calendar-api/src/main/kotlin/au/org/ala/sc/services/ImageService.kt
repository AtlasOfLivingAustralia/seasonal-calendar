package au.org.ala.sc.services

import org.imgscalr.Scalr
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.roundToInt

class ImageService(private val baseDir: File) {

    fun getImageFile(original: File, width: Int?, height: Int?): File {
        val baseName = original.nameWithoutExtension
        val ext = original.extension

        when {
            (width != null) and (height != null) -> {
                val newFile = File(baseDir, "$baseName-w$width-h$height.$ext")
                if (!newFile.exists()) {
                    val img = ImageIO.read(original)
                    val ratio = width!!.toDouble() / height!!.toDouble()
                    val cropped = centerCrop(img, ratio)
                    val scaled = Scalr.resize(cropped, width, height)
                    ImageIO.write(scaled, ext, newFile)
                }
                return newFile
            }
            width != null -> {
                val newFile = File(baseDir, "$baseName-w$width.$ext")
                if (!newFile.exists()) {
                    val img = ImageIO.read(original)
                    val scaledHeight = (width.toDouble() * (img.height.toDouble() / img.width.toDouble())).roundToInt()
                    val scaled = Scalr.resize(img, width, scaledHeight)
                    ImageIO.write(scaled, ext, newFile)
                }
                return newFile
            }
            height != null -> {
                val newFile = File(baseDir, "$baseName-h$height.$ext")
                if (!newFile.exists()) {
                    val img = ImageIO.read(original)
                    val scaledWidth = (height.toDouble() * (img.width.toDouble() / img.height.toDouble())).roundToInt()
                    val scaled = Scalr.resize(img, scaledWidth, height)
                    ImageIO.write(scaled, ext, newFile)
                }
                return newFile
            }
            else -> return original
        }


    }

    private fun centerCrop(img: BufferedImage, ratio: Double): BufferedImage {
        val w = img.width
        val h = img.height

        val aspectH: Double
        val aspectW: Double

        if (ratio == 1.0) {
            aspectH = min(w, h).toDouble()
            aspectW = aspectH
        } else {
            aspectH = w / ratio
            aspectW = h * ratio
        }

        val srcX: Int
        val srcY: Int
        val srcW: Int
        val srcH: Int

        if (aspectH > aspectW) {
            srcX = 0
            srcY = ((h - aspectH) / 2.0).roundToInt()
            srcW = w
            srcH = aspectH.roundToInt()
        } else {
            srcX = ((w - aspectW) / 2.0).roundToInt()
            srcY = 0
            srcW = aspectW.roundToInt()
            srcH = h
        }

        return Scalr.crop(img, srcX, srcY, srcW, srcH)
    }

}