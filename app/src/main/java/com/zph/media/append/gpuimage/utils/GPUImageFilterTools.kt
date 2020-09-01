package com.zph.media.append.gpuimage.utils/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * "GPUImageFastBlurFilter"                               【模糊】
"GPUImageGaussianBlurFilter"                       【高斯模糊】
"GPUImageGaussianSelectiveBlurFilter"        【高斯模糊，选择部分清晰】
"GPUImageBoxBlurFilter"                                【盒状模糊】
"GPUImageTiltShiftFilter"                                【条纹模糊，中间清晰，上下两端模糊】
"GPUImageMedianFilter.h"                             【中间值，有种稍微模糊边缘的效果】
"GPUImageBilateralFilter"                               【双边模糊】
"GPUImageErosionFilter"                                【侵蚀边缘模糊，变黑白】
"GPUImageRGBErosionFilter"                         【RGB侵蚀边缘模糊，有色彩】
"GPUImageDilationFilter"                               【扩展边缘模糊，变黑白】
"GPUImageRGBDilationFilter"                        【RGB扩展边缘模糊，有色彩】
"GPUImageOpeningFilter"                             【黑白色调模糊】
"GPUImageRGBOpeningFilter"                      【彩色模糊】
"GPUImageClosingFilter"                               【黑白色调模糊，暗色会被提亮】
"GPUImageRGBClosingFilter"                        【彩色模糊，暗色会被提亮】
"GPUImageLanczosResamplingFilter"          【Lanczos重取样，模糊效果】
"GPUImageNonMaximumSuppressionFilter"     【非最大抑制，只显示亮度最高的像素，其他为黑】
"GPUImageThresholdedNonMaximumSuppressionFilter" 【与上相比，像素丢失更多】


"GPUImageCrosshairGenerator"              【十字】
"GPUImageLineGenerator"                       【线条】
"GPUImageTransformFilter"                     【形状变化】
"GPUImageCropFilter"                              【剪裁】
"GPUImageSharpenFilter"                        【锐化】
"GPUImageUnsharpMaskFilter"               【反遮罩锐化】


"GPUImageSobelEdgeDetectionFilter"           【Sobel边缘检测算法(白边，黑内容，有点漫画的反色效果)】
"GPUImageCannyEdgeDetectionFilter"          【Canny边缘检测算法（比上更强烈的黑白对比度）】
"GPUImageThresholdEdgeDetectionFilter"    【阈值边缘检测（效果与上差别不大）】
"GPUImagePrewittEdgeDetectionFilter"         【普瑞维特(Prewitt)边缘检测(效果与Sobel差不多，貌似更平滑)】
"GPUImageXYDerivativeFilter"                        【XYDerivative边缘检测，画面以蓝色为主，绿色为边缘，带彩色】
"GPUImageHarrisCornerDetectionFilter"       【Harris角点检测，会有绿色小十字显示在图片角点处】
"GPUImageNobleCornerDetectionFilter"      【Noble角点检测，检测点更多】
"GPUImageShiTomasiFeatureDetectionFilter" 【ShiTomasi角点检测，与上差别不大】
"GPUImageMotionDetector"                             【动作检测】
"GPUImageHoughTransformLineDetector"      【线条检测】
"GPUImageParallelCoordinateLineTransformFilter" 【平行线检测】


"GPUImageLocalBinaryPatternFilter"        【图像黑白化，并有大量噪点】
"GPUImageLowPassFilter"                          【用于图像加亮】
"GPUImageHighPassFilter"                        【图像低于某值时显示为黑】




"GPUImageSketchFilter"                          【素描】
"GPUImageThresholdSketchFilter"         【阀值素描，形成有噪点的素描】
"GPUImageToonFilter"                             【卡通效果（黑色粗线描边）】
"GPUImageSmoothToonFilter"                【相比上面的效果更细腻，上面是粗旷的画风】
"GPUImageKuwaharaFilter"                     【桑原(Kuwahara)滤波,水粉画的模糊效果；处理时间比较长，慎用】


"GPUImageMosaicFilter"                         【黑白马赛克】
"GPUImagePixellateFilter"                       【像素化】
"GPUImagePolarPixellateFilter"              【同心圆像素化】
"GPUImageCrosshatchFilter"                  【交叉线阴影，形成黑白网状画面】
"GPUImageColorPackingFilter"              【色彩丢失，模糊（类似监控摄像效果）】


"GPUImageVignetteFilter"                        【晕影，形成黑色圆形边缘，突出中间图像的效果】
"GPUImageSwirlFilter"                               【漩涡，中间形成卷曲的画面】
"GPUImageBulgeDistortionFilter"            【凸起失真，鱼眼效果】
"GPUImagePinchDistortionFilter"            【收缩失真，凹面镜】
"GPUImageStretchDistortionFilter"         【伸展失真，哈哈镜】
"GPUImageGlassSphereFilter"                  【水晶球效果】
"GPUImageSphereRefractionFilter"         【球形折射，图形倒立】

"GPUImagePosterizeFilter"                 【色调分离，形成噪点效果】
"GPUImageCGAColorspaceFilter"      【CGA色彩滤镜，形成黑、浅蓝、紫色块的画面】
"GPUImagePerlinNoiseFilter"              【柏林噪点，花边噪点】
"GPUImage3x3ConvolutionFilter"      【3x3卷积，高亮大色块变黑，加亮边缘、线条等】
"GPUImageEmbossFilter"                   【浮雕效果，带有点3d的感觉】
"GPUImagePolkaDotFilter"                 【像素圆点花样】
"GPUImageHalftoneFilter"                  【点染,图像黑白化，由黑点构成原图的大致图形】


混合模式 Blend

"GPUImageMultiplyBlendFilter"            【通常用于创建阴影和深度效果】
"GPUImageNormalBlendFilter"               【正常】
"GPUImageAlphaBlendFilter"                 【透明混合,通常用于在背景上应用前景的透明度】
"GPUImageDissolveBlendFilter"             【溶解】
"GPUImageOverlayBlendFilter"              【叠加,通常用于创建阴影效果】
"GPUImageDarkenBlendFilter"               【加深混合,通常用于重叠类型】
"GPUImageLightenBlendFilter"              【减淡混合,通常用于重叠类型】
"GPUImageSourceOverBlendFilter"       【源混合】
"GPUImageColorBurnBlendFilter"          【色彩加深混合】
"GPUImageColorDodgeBlendFilter"      【色彩减淡混合】
"GPUImageScreenBlendFilter"                【屏幕包裹,通常用于创建亮点和镜头眩光】
"GPUImageExclusionBlendFilter"            【排除混合】
"GPUImageDifferenceBlendFilter"          【差异混合,通常用于创建更多变动的颜色】
"GPUImageSubtractBlendFilter"            【差值混合,通常用于创建两个图像之间的动画变暗模糊效果】
"GPUImageHardLightBlendFilter"         【强光混合,通常用于创建阴影效果】
"GPUImageSoftLightBlendFilter"           【柔光混合】
"GPUImageChromaKeyBlendFilter"       【色度键混合】
"GPUImageMaskFilter"                           【遮罩混合】
"GPUImageHazeFilter"                            【朦胧加暗】
"GPUImageLuminanceThresholdFilter" 【亮度阈】
"GPUImageAdaptiveThresholdFilter"     【自适应阈值】
"GPUImageAddBlendFilter"                    【通常用于创建两个图像之间的动画变亮模糊效果】
"GPUImageDivideBlendFilter"                 【通常用于创建两个图像之间的动画变暗模糊效果】
 * */
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.opengl.Matrix
import com.zph.media.R
import jp.co.cyberagent.android.gpuimage.filter.*
import java.util.*

object GPUImageFilterTools {
    fun showDialog(
        context: Context,
        listener: (filter: GPUImageFilter) -> Unit
    ) {
        val filters = FilterList()
            .apply {
            addFilter("Contrast",
                FilterType.CONTRAST
            )
            addFilter("Invert",
                FilterType.INVERT
            )
            addFilter("Pixelation",
                FilterType.PIXELATION
            )
            addFilter("Hue",
                FilterType.HUE
            )
            addFilter("Gamma",
                FilterType.GAMMA
            )
            addFilter("Brightness",
                FilterType.BRIGHTNESS
            )
            addFilter("Sepia",
                FilterType.SEPIA
            )
            addFilter("Grayscale",
                FilterType.GRAYSCALE
            )
            addFilter("Sharpness",
                FilterType.SHARPEN
            )
            addFilter("Sobel Edge Detection",
                FilterType.SOBEL_EDGE_DETECTION
            )
            addFilter("Threshold Edge Detection",
                FilterType.THRESHOLD_EDGE_DETECTION
            )
            addFilter("3x3 Convolution",
                FilterType.THREE_X_THREE_CONVOLUTION
            )
            addFilter("Emboss",
                FilterType.EMBOSS
            )
            addFilter("Posterize",
                FilterType.POSTERIZE
            )
            addFilter("Grouped filters",
                FilterType.FILTER_GROUP
            )
            addFilter("Saturation",
                FilterType.SATURATION
            )
            addFilter("Exposure",
                FilterType.EXPOSURE
            )
            addFilter("Highlight Shadow",
                FilterType.HIGHLIGHT_SHADOW
            )
            addFilter("Monochrome",
                FilterType.MONOCHROME
            )
            addFilter("Opacity",
                FilterType.OPACITY
            )
            addFilter("RGB",
                FilterType.RGB
            )
            addFilter("White Balance",
                FilterType.WHITE_BALANCE
            )
            addFilter("Vignette",
                FilterType.VIGNETTE
            )
            addFilter("ToneCurve",
                FilterType.TONE_CURVE
            )

            addFilter("Luminance",
                FilterType.LUMINANCE
            )
            addFilter("Luminance Threshold",
                FilterType.LUMINANCE_THRESHSOLD
            )

            addFilter("Blend (Difference)",
                FilterType.BLEND_DIFFERENCE
            )
            addFilter("Blend (Source Over)",
                FilterType.BLEND_SOURCE_OVER
            )
            addFilter("Blend (Color Burn)",
                FilterType.BLEND_COLOR_BURN
            )
            addFilter("Blend (Color Dodge)",
                FilterType.BLEND_COLOR_DODGE
            )
            addFilter("Blend (Darken)",
                FilterType.BLEND_DARKEN
            )
            addFilter("Blend (Dissolve)",
                FilterType.BLEND_DISSOLVE
            )
            addFilter("Blend (Exclusion)",
                FilterType.BLEND_EXCLUSION
            )
            addFilter("Blend (Hard Light)",
                FilterType.BLEND_HARD_LIGHT
            )
            addFilter("Blend (Lighten)",
                FilterType.BLEND_LIGHTEN
            )
            addFilter("Blend (Add)",
                FilterType.BLEND_ADD
            )
            addFilter("Blend (Divide)",
                FilterType.BLEND_DIVIDE
            )
            addFilter("Blend (Multiply)",
                FilterType.BLEND_MULTIPLY
            )
            addFilter("Blend (Overlay)",
                FilterType.BLEND_OVERLAY
            )
            addFilter("Blend (Screen)",
                FilterType.BLEND_SCREEN
            )
            addFilter("Blend (Alpha)",
                FilterType.BLEND_ALPHA
            )
            addFilter("Blend (Color)",
                FilterType.BLEND_COLOR
            )
            addFilter("Blend (Hue)",
                FilterType.BLEND_HUE
            )
            addFilter("Blend (Saturation)",
                FilterType.BLEND_SATURATION
            )
            addFilter("Blend (Luminosity)",
                FilterType.BLEND_LUMINOSITY
            )
            addFilter("Blend (Linear Burn)",
                FilterType.BLEND_LINEAR_BURN
            )
            addFilter("Blend (Soft Light)",
                FilterType.BLEND_SOFT_LIGHT
            )
            addFilter("Blend (Subtract)",
                FilterType.BLEND_SUBTRACT
            )
            addFilter("Blend (Chroma Key)",
                FilterType.BLEND_CHROMA_KEY
            )
            addFilter("Blend (Normal)",
                FilterType.BLEND_NORMAL
            )

            addFilter("Lookup (Amatorka)",
                FilterType.LOOKUP_AMATORKA
            )
            addFilter("Gaussian Blur",
                FilterType.GAUSSIAN_BLUR
            )
            addFilter("Crosshatch",
                FilterType.CROSSHATCH
            )

            addFilter("Box Blur",
                FilterType.BOX_BLUR
            )
            addFilter("CGA Color Space",
                FilterType.CGA_COLORSPACE
            )
            addFilter("Dilation",
                FilterType.DILATION
            )
            addFilter("Kuwahara",
                FilterType.KUWAHARA
            )
            addFilter("RGB Dilation",
                FilterType.RGB_DILATION
            )
            addFilter("Sketch",
                FilterType.SKETCH
            )
            addFilter("Toon",
                FilterType.TOON
            )
            addFilter("Smooth Toon",
                FilterType.SMOOTH_TOON
            )
            addFilter("Halftone",
                FilterType.HALFTONE
            )

            addFilter("Bulge Distortion",
                FilterType.BULGE_DISTORTION
            )
            addFilter("Glass Sphere",
                FilterType.GLASS_SPHERE
            )
            addFilter("Haze",
                FilterType.HAZE
            )
            addFilter("Laplacian",
                FilterType.LAPLACIAN
            )
            addFilter("Non Maximum Suppression",
                FilterType.NON_MAXIMUM_SUPPRESSION
            )
            addFilter("Sphere Refraction",
                FilterType.SPHERE_REFRACTION
            )
            addFilter("Swirl",
                FilterType.SWIRL
            )
            addFilter("Weak Pixel Inclusion",
                FilterType.WEAK_PIXEL_INCLUSION
            )
            addFilter("False Color",
                FilterType.FALSE_COLOR
            )

            addFilter("Color Balance",
                FilterType.COLOR_BALANCE
            )

            addFilter("Levels Min (Mid Adjust)",
                FilterType.LEVELS_FILTER_MIN
            )

            addFilter("Bilateral Blur",
                FilterType.BILATERAL_BLUR
            )

            addFilter("Zoom Blur",
                FilterType.ZOOM_BLUR
            )

            addFilter("Transform (2-D)",
                FilterType.TRANSFORM2D
            )

            addFilter("Solarize",
                FilterType.SOLARIZE
            )

            addFilter("Vibrance",
                FilterType.VIBRANCE
            )
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
        builder.setItems(filters.names.toTypedArray()) { _, item ->
            listener(
                createFilterForType(
                    context,
                    filters.filters[item]
                )
            )
        }
        builder.create().show()
    }

    private fun createFilterForType(context: Context, type: FilterType): GPUImageFilter {
        return when (type) {
            FilterType.CONTRAST -> GPUImageContrastFilter(2.0f)
            FilterType.GAMMA -> GPUImageGammaFilter(2.0f)
            FilterType.INVERT -> GPUImageColorInvertFilter()
            FilterType.PIXELATION -> GPUImagePixelationFilter()
            FilterType.HUE -> GPUImageHueFilter(90.0f)
            FilterType.BRIGHTNESS -> GPUImageBrightnessFilter(1.5f)
            FilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
            FilterType.SEPIA -> GPUImageSepiaToneFilter()
            FilterType.SHARPEN -> GPUImageSharpenFilter()
            FilterType.SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetectionFilter()
            FilterType.THRESHOLD_EDGE_DETECTION -> GPUImageThresholdEdgeDetectionFilter()
            FilterType.THREE_X_THREE_CONVOLUTION -> GPUImage3x3ConvolutionFilter()
            FilterType.EMBOSS -> GPUImageEmbossFilter()
            FilterType.POSTERIZE -> GPUImagePosterizeFilter()
            FilterType.FILTER_GROUP -> GPUImageFilterGroup(
                listOf(
                    GPUImageContrastFilter(),
                    GPUImageDirectionalSobelEdgeDetectionFilter(),
                    GPUImageGrayscaleFilter()
                )
            )
            FilterType.SATURATION -> GPUImageSaturationFilter(1.0f)
            FilterType.EXPOSURE -> GPUImageExposureFilter(0.0f)
            FilterType.HIGHLIGHT_SHADOW -> GPUImageHighlightShadowFilter(
                0.0f,
                1.0f
            )
            FilterType.MONOCHROME -> GPUImageMonochromeFilter(
                1.0f, floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
            )
            FilterType.OPACITY -> GPUImageOpacityFilter(1.0f)
            FilterType.RGB -> GPUImageRGBFilter(1.0f, 1.0f, 1.0f)
            FilterType.WHITE_BALANCE -> GPUImageWhiteBalanceFilter(
                5000.0f,
                0.0f
            )
            FilterType.VIGNETTE -> GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.0f, 0.0f, 0.0f),
                0.3f,
                0.75f
            )
            FilterType.TONE_CURVE -> GPUImageToneCurveFilter().apply {
                setFromCurveFileInputStream(context.resources.openRawResource(R.raw.tone_cuver_sample))
            }
            FilterType.LUMINANCE -> GPUImageLuminanceFilter()
            FilterType.LUMINANCE_THRESHSOLD -> GPUImageLuminanceThresholdFilter(0.5f)
            FilterType.BLEND_DIFFERENCE -> createBlendFilter(
                context,
                GPUImageDifferenceBlendFilter::class.java
            )
            FilterType.BLEND_SOURCE_OVER -> createBlendFilter(
                context,
                GPUImageSourceOverBlendFilter::class.java
            )
            FilterType.BLEND_COLOR_BURN -> createBlendFilter(
                context,
                GPUImageColorBurnBlendFilter::class.java
            )
            FilterType.BLEND_COLOR_DODGE -> createBlendFilter(
                context,
                GPUImageColorDodgeBlendFilter::class.java
            )
            FilterType.BLEND_DARKEN -> createBlendFilter(
                context,
                GPUImageDarkenBlendFilter::class.java
            )
            FilterType.BLEND_DISSOLVE -> createBlendFilter(
                context,
                GPUImageDissolveBlendFilter::class.java
            )
            FilterType.BLEND_EXCLUSION -> createBlendFilter(
                context,
                GPUImageExclusionBlendFilter::class.java
            )

            FilterType.BLEND_HARD_LIGHT -> createBlendFilter(
                context,
                GPUImageHardLightBlendFilter::class.java
            )
            FilterType.BLEND_LIGHTEN -> createBlendFilter(
                context,
                GPUImageLightenBlendFilter::class.java
            )
            FilterType.BLEND_ADD -> createBlendFilter(
                context,
                GPUImageAddBlendFilter::class.java
            )
            FilterType.BLEND_DIVIDE -> createBlendFilter(
                context,
                GPUImageDivideBlendFilter::class.java
            )
            FilterType.BLEND_MULTIPLY -> createBlendFilter(
                context,
                GPUImageMultiplyBlendFilter::class.java
            )
            FilterType.BLEND_OVERLAY -> createBlendFilter(
                context,
                GPUImageOverlayBlendFilter::class.java
            )
            FilterType.BLEND_SCREEN -> createBlendFilter(
                context,
                GPUImageScreenBlendFilter::class.java
            )
            FilterType.BLEND_ALPHA -> createBlendFilter(
                context,
                GPUImageAlphaBlendFilter::class.java
            )
            FilterType.BLEND_COLOR -> createBlendFilter(
                context,
                GPUImageColorBlendFilter::class.java
            )
            FilterType.BLEND_HUE -> createBlendFilter(
                context,
                GPUImageHueBlendFilter::class.java
            )
            FilterType.BLEND_SATURATION -> createBlendFilter(
                context,
                GPUImageSaturationBlendFilter::class.java
            )
            FilterType.BLEND_LUMINOSITY -> createBlendFilter(
                context,
                GPUImageLuminosityBlendFilter::class.java
            )
            FilterType.BLEND_LINEAR_BURN -> createBlendFilter(
                context,
                GPUImageLinearBurnBlendFilter::class.java
            )
            FilterType.BLEND_SOFT_LIGHT -> createBlendFilter(
                context,
                GPUImageSoftLightBlendFilter::class.java
            )
            FilterType.BLEND_SUBTRACT -> createBlendFilter(
                context,
                GPUImageSubtractBlendFilter::class.java
            )
            FilterType.BLEND_CHROMA_KEY -> createBlendFilter(
                context,
                GPUImageChromaKeyBlendFilter::class.java
            )
            FilterType.BLEND_NORMAL -> createBlendFilter(
                context,
                GPUImageNormalBlendFilter::class.java
            )

            FilterType.LOOKUP_AMATORKA -> GPUImageLookupFilter().apply {
                bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lookup_amatorka)
            }
            FilterType.GAUSSIAN_BLUR -> GPUImageGaussianBlurFilter()
            FilterType.CROSSHATCH -> GPUImageCrosshatchFilter()
            FilterType.BOX_BLUR -> GPUImageBoxBlurFilter()
            FilterType.CGA_COLORSPACE -> GPUImageCGAColorspaceFilter()
            FilterType.DILATION -> GPUImageDilationFilter()
            FilterType.KUWAHARA -> GPUImageKuwaharaFilter()
            FilterType.RGB_DILATION -> GPUImageRGBDilationFilter()
            FilterType.SKETCH -> GPUImageSketchFilter()
            FilterType.TOON -> GPUImageToonFilter()
            FilterType.SMOOTH_TOON -> GPUImageSmoothToonFilter()
            FilterType.BULGE_DISTORTION -> GPUImageBulgeDistortionFilter()
            FilterType.GLASS_SPHERE -> GPUImageGlassSphereFilter()
            FilterType.HAZE -> GPUImageHazeFilter()
            FilterType.LAPLACIAN -> GPUImageLaplacianFilter()
            FilterType.NON_MAXIMUM_SUPPRESSION -> GPUImageNonMaximumSuppressionFilter()
            FilterType.SPHERE_REFRACTION -> GPUImageSphereRefractionFilter()
            FilterType.SWIRL -> GPUImageSwirlFilter()
            FilterType.WEAK_PIXEL_INCLUSION -> GPUImageWeakPixelInclusionFilter()
            FilterType.FALSE_COLOR -> GPUImageFalseColorFilter()
            FilterType.COLOR_BALANCE -> GPUImageColorBalanceFilter()
            FilterType.LEVELS_FILTER_MIN -> GPUImageLevelsFilter()
            FilterType.HALFTONE -> GPUImageHalftoneFilter()
            FilterType.BILATERAL_BLUR -> GPUImageBilateralBlurFilter()
            FilterType.ZOOM_BLUR -> GPUImageZoomBlurFilter()
            FilterType.TRANSFORM2D -> GPUImageTransformFilter()
            FilterType.SOLARIZE -> GPUImageSolarizeFilter()
            FilterType.VIBRANCE -> GPUImageVibranceFilter()


            else -> GPUImageContrastFilter(2.0f)
        }
    }

    private fun createBlendFilter(
        context: Context,
        filterClass: Class<out GPUImageTwoInputFilter>
    ): GPUImageFilter {
        return try {
            filterClass.newInstance().apply {
                bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            GPUImageFilter()
        }
    }

    private enum class FilterType {
        CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THRESHOLD_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, TONE_CURVE, LUMINANCE, LUMINANCE_THRESHSOLD, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN,
        BLEND_DIFFERENCE, BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
        BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE, DILATION, KUWAHARA, RGB_DILATION, SKETCH, TOON, SMOOTH_TOON, BULGE_DISTORTION, GLASS_SPHERE, HAZE, LAPLACIAN, NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION, FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN, BILATERAL_BLUR, ZOOM_BLUR, HALFTONE, TRANSFORM2D, SOLARIZE, VIBRANCE
    }

    private class FilterList {
        val names: MutableList<String> = LinkedList()
        val filters: MutableList<FilterType> = LinkedList()

        fun addFilter(name: String, filter: FilterType) {
            names.add(name)
            filters.add(filter)
        }
    }

    class FilterAdjuster(filter: GPUImageFilter) {
        private val adjuster: Adjuster<out GPUImageFilter>?

        init {
            adjuster = when (filter) {
                is GPUImageSharpenFilter -> SharpnessAdjuster(filter)
                is GPUImageSepiaToneFilter -> SepiaAdjuster(filter)
                is GPUImageContrastFilter -> ContrastAdjuster(filter)
                is GPUImageGammaFilter -> GammaAdjuster(filter)
                is GPUImageBrightnessFilter -> BrightnessAdjuster(filter)
                is GPUImageSobelEdgeDetectionFilter -> SobelAdjuster(filter)
                is GPUImageThresholdEdgeDetectionFilter -> ThresholdAdjuster(filter)
                is GPUImage3x3ConvolutionFilter -> ThreeXThreeConvolutionAjuster(filter)
                is GPUImageEmbossFilter -> EmbossAdjuster(filter)
                is GPUImage3x3TextureSamplingFilter -> GPU3x3TextureAdjuster(filter)
                is GPUImageHueFilter -> HueAdjuster(filter)
                is GPUImagePosterizeFilter -> PosterizeAdjuster(filter)
                is GPUImagePixelationFilter -> PixelationAdjuster(filter)
                is GPUImageSaturationFilter -> SaturationAdjuster(filter)
                is GPUImageExposureFilter -> ExposureAdjuster(filter)
                is GPUImageHighlightShadowFilter -> HighlightShadowAdjuster(filter)
                is GPUImageMonochromeFilter -> MonochromeAdjuster(filter)
                is GPUImageOpacityFilter -> OpacityAdjuster(filter)
                is GPUImageRGBFilter -> RGBAdjuster(filter)
                is GPUImageWhiteBalanceFilter -> WhiteBalanceAdjuster(filter)
                is GPUImageVignetteFilter -> VignetteAdjuster(filter)
                is GPUImageLuminanceThresholdFilter -> LuminanceThresholdAdjuster(filter)
                is GPUImageDissolveBlendFilter -> DissolveBlendAdjuster(filter)
                is GPUImageGaussianBlurFilter -> GaussianBlurAdjuster(filter)
                is GPUImageCrosshatchFilter -> CrosshatchBlurAdjuster(filter)
                is GPUImageBulgeDistortionFilter -> BulgeDistortionAdjuster(filter)
                is GPUImageGlassSphereFilter -> GlassSphereAdjuster(filter)
                is GPUImageHazeFilter -> HazeAdjuster(filter)
                is GPUImageSphereRefractionFilter -> SphereRefractionAdjuster(filter)
                is GPUImageSwirlFilter -> SwirlAdjuster(filter)
                is GPUImageColorBalanceFilter -> ColorBalanceAdjuster(filter)
                is GPUImageLevelsFilter -> LevelsMinMidAdjuster(filter)
                is GPUImageBilateralBlurFilter -> BilateralAdjuster(filter)
                is GPUImageTransformFilter -> RotateAdjuster(filter)
                is GPUImageSolarizeFilter -> SolarizeAdjuster(filter)
                is GPUImageVibranceFilter -> VibranceAdjuster(filter)
                else -> null
            }
        }

        fun canAdjust(): Boolean {
            return adjuster != null
        }

        fun adjust(percentage: Int) {
            adjuster?.adjust(percentage)
        }

        private abstract inner class Adjuster<T : GPUImageFilter>(protected val filter: T) {

            abstract fun adjust(percentage: Int)

            protected fun range(percentage: Int, start: Float, end: Float): Float {
                return (end - start) * percentage / 100.0f + start
            }

            protected fun range(percentage: Int, start: Int, end: Int): Int {
                return (end - start) * percentage / 100 + start
            }
        }

        private inner class SharpnessAdjuster(filter: GPUImageSharpenFilter) :
            Adjuster<GPUImageSharpenFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSharpness(range(percentage, -4.0f, 4.0f))
            }
        }

        private inner class PixelationAdjuster(filter: GPUImagePixelationFilter) :
            Adjuster<GPUImagePixelationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setPixel(range(percentage, 1.0f, 100.0f))
            }
        }

        private inner class HueAdjuster(filter: GPUImageHueFilter) :
            Adjuster<GPUImageHueFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setHue(range(percentage, 0.0f, 360.0f))
            }
        }

        private inner class ContrastAdjuster(filter: GPUImageContrastFilter) :
            Adjuster<GPUImageContrastFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setContrast(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class GammaAdjuster(filter: GPUImageGammaFilter) :
            Adjuster<GPUImageGammaFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setGamma(range(percentage, 0.0f, 3.0f))
            }
        }

        private inner class BrightnessAdjuster(filter: GPUImageBrightnessFilter) :
            Adjuster<GPUImageBrightnessFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBrightness(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class SepiaAdjuster(filter: GPUImageSepiaToneFilter) :
            Adjuster<GPUImageSepiaToneFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class SobelAdjuster(filter: GPUImageSobelEdgeDetectionFilter) :
            Adjuster<GPUImageSobelEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class ThresholdAdjuster(filter: GPUImageThresholdEdgeDetectionFilter) :
            Adjuster<GPUImageThresholdEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
                filter.setThreshold(0.9f)
            }
        }

        private inner class ThreeXThreeConvolutionAjuster(filter: GPUImage3x3ConvolutionFilter) :
            Adjuster<GPUImage3x3ConvolutionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setConvolutionKernel(
                    floatArrayOf(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
                )
            }
        }

        private inner class EmbossAdjuster(filter: GPUImageEmbossFilter) :
            Adjuster<GPUImageEmbossFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.intensity = range(percentage, 0.0f, 4.0f)
            }
        }

        private inner class PosterizeAdjuster(filter: GPUImagePosterizeFilter) :
            Adjuster<GPUImagePosterizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                // In theorie to 256, but only first 50 are interesting
                filter.setColorLevels(range(percentage, 1, 50))
            }
        }

        private inner class GPU3x3TextureAdjuster(filter: GPUImage3x3TextureSamplingFilter) :
            Adjuster<GPUImage3x3TextureSamplingFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class SaturationAdjuster(filter: GPUImageSaturationFilter) :
            Adjuster<GPUImageSaturationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSaturation(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ExposureAdjuster(filter: GPUImageExposureFilter) :
            Adjuster<GPUImageExposureFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setExposure(range(percentage, -10.0f, 10.0f))
            }
        }

        private inner class HighlightShadowAdjuster(filter: GPUImageHighlightShadowFilter) :
            Adjuster<GPUImageHighlightShadowFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setShadows(range(percentage, 0.0f, 1.0f))
                filter.setHighlights(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class MonochromeAdjuster(filter: GPUImageMonochromeFilter) :
            Adjuster<GPUImageMonochromeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class OpacityAdjuster(filter: GPUImageOpacityFilter) :
            Adjuster<GPUImageOpacityFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setOpacity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class RGBAdjuster(filter: GPUImageRGBFilter) :
            Adjuster<GPUImageRGBFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRed(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class WhiteBalanceAdjuster(filter: GPUImageWhiteBalanceFilter) :
            Adjuster<GPUImageWhiteBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setTemperature(range(percentage, 2000.0f, 8000.0f))
            }
        }

        private inner class VignetteAdjuster(filter: GPUImageVignetteFilter) :
            Adjuster<GPUImageVignetteFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVignetteStart(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class LuminanceThresholdAdjuster(filter: GPUImageLuminanceThresholdFilter) :
            Adjuster<GPUImageLuminanceThresholdFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class DissolveBlendAdjuster(filter: GPUImageDissolveBlendFilter) :
            Adjuster<GPUImageDissolveBlendFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMix(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class GaussianBlurAdjuster(filter: GPUImageGaussianBlurFilter) :
            Adjuster<GPUImageGaussianBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBlurSize(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class CrosshatchBlurAdjuster(filter: GPUImageCrosshatchFilter) :
            Adjuster<GPUImageCrosshatchFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setCrossHatchSpacing(range(percentage, 0.0f, 0.06f))
                filter.setLineWidth(range(percentage, 0.0f, 0.006f))
            }
        }

        private inner class BulgeDistortionAdjuster(filter: GPUImageBulgeDistortionFilter) :
            Adjuster<GPUImageBulgeDistortionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
                filter.setScale(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class GlassSphereAdjuster(filter: GPUImageGlassSphereFilter) :
            Adjuster<GPUImageGlassSphereFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class HazeAdjuster(filter: GPUImageHazeFilter) :
            Adjuster<GPUImageHazeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistance(range(percentage, -0.3f, 0.3f))
                filter.setSlope(range(percentage, -0.3f, 0.3f))
            }
        }

        private inner class SphereRefractionAdjuster(filter: GPUImageSphereRefractionFilter) :
            Adjuster<GPUImageSphereRefractionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class SwirlAdjuster(filter: GPUImageSwirlFilter) :
            Adjuster<GPUImageSwirlFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setAngle(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ColorBalanceAdjuster(filter: GPUImageColorBalanceFilter) :
            Adjuster<GPUImageColorBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMidtones(
                    floatArrayOf(
                        range(percentage, 0.0f, 1.0f),
                        range(percentage / 2, 0.0f, 1.0f),
                        range(percentage / 3, 0.0f, 1.0f)
                    )
                )
            }
        }

        private inner class LevelsMinMidAdjuster(filter: GPUImageLevelsFilter) :
            Adjuster<GPUImageLevelsFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f)
            }
        }

        private inner class BilateralAdjuster(filter: GPUImageBilateralBlurFilter) :
            Adjuster<GPUImageBilateralBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f))
            }
        }

        private inner class RotateAdjuster(filter: GPUImageTransformFilter) :
            Adjuster<GPUImageTransformFilter>(filter) {
            override fun adjust(percentage: Int) {
                val transform = FloatArray(16)
                Matrix.setRotateM(transform, 0, (360 * percentage / 100).toFloat(), 0f, 0f, 1.0f)
                filter.transform3D = transform
            }
        }

        private inner class SolarizeAdjuster(filter: GPUImageSolarizeFilter) :
            Adjuster<GPUImageSolarizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class VibranceAdjuster(filter: GPUImageVibranceFilter) :
            Adjuster<GPUImageVibranceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVibrance(range(percentage, -1.2f, 1.2f))
            }
        }
    }
}
