package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

object ImageUtils {
    private const val TAG = "ImageUtils"

    fun createCameraImageUri(context: Context): Uri {
        val dir = File(context.cacheDir, "camera").apply {
            if (!exists()) mkdirs()
        }
        val imageFile = File(dir, "rabbit_cam_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    suspend fun decodeSampledRotatedBitmap(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1280
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }

            val rawWidth = boundsOptions.outWidth
            val rawHeight = boundsOptions.outHeight
            if (rawWidth <= 0 || rawHeight <= 0) return@withContext null

            var inSampleSize = 1
            while ((rawWidth / inSampleSize) > maxDimension * 2 ||
                (rawHeight / inSampleSize) > maxDimension * 2
            ) {
                inSampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@withContext null

            val orientation = try {
                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    ExifInterface(exifStream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: ExifInterface.ORIENTATION_NORMAL
            } catch (_: Exception) {
                ExifInterface.ORIENTATION_NORMAL
            }

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            }

            val maxSide = max(decoded.width, decoded.height)
            if (maxSide > maxDimension) {
                val scale = maxDimension.toFloat() / maxSide.toFloat()
                matrix.postScale(scale, scale)
            }

            val transformed = if (!matrix.isIdentity) {
                val out = Bitmap.createBitmap(
                    decoded,
                    0,
                    0,
                    decoded.width,
                    decoded.height,
                    matrix,
                    true
                )
                if (out !== decoded) {
                    decoded.recycle()
                }
                out
            } else {
                decoded
            }

            // Ensure strictly bounded final dimensions for smooth UI display and < 20MB AI upload
            val finalMax = max(transformed.width, transformed.height)
            if (finalMax > maxDimension) {
                val ratio = maxDimension.toFloat() / finalMax.toFloat()
                val scaled = Bitmap.createScaledBitmap(
                    transformed,
                    (transformed.width * ratio).roundToInt().coerceAtLeast(1),
                    (transformed.height * ratio).roundToInt().coerceAtLeast(1),
                    true
                )
                if (scaled !== transformed) {
                    transformed.recycle()
                }
                scaled
            } else {
                transformed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode image from $uri", e)
            null
        }
    }

    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        fileNamePrefix: String = "RabbitAI"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/RabbitAI")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext Result.failure(IllegalStateException("Unable to create gallery entry"))

            resolver.openOutputStream(imageUri)?.use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    throw IllegalStateException("Failed to encode PNG bitmap")
                }
            } ?: return@withContext Result.failure(IllegalStateException("Unable to open gallery stream"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val clearPending = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                resolver.update(imageUri, clearPending, null, null)
            }
            Result.success(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving bitmap to gallery", e)
            Result.failure(e)
        }
    }

    suspend fun getShareableImageUri(
        context: Context,
        bitmap: Bitmap
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.cacheDir, "shared_ai_images").apply {
                if (!exists()) mkdirs()
            }
            val file = File(dir, "rabbit_ai_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed creating shareable image URI", e)
            null
        }
    }
}
