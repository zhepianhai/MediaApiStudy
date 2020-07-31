package com.zph.media.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

val Any.TAG: String
    get() = this.javaClass.simpleName

inline val isOverN: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

//inline val isOverQ: Boolean
//    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

inline val isOverM: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun Date.format(format: String): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

inline val EditText.exTextString: String
    get() = this.text.toString().trim()

inline val EditText.hasContent: Boolean
    get() = exTextString.isEmpty()

fun showSnackBar(
    view: View?,
    content: String,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
    showAction: Boolean = false
): Snackbar? {
    if (view == null) return null
    val snack = Snackbar.make(view, content, duration)
    if (showAction) snack.setAction("好的") { snack.dismiss() }
    snack.show()
    return snack
}

fun showSnackBar(
    view: View?,
    @StringRes strId: Int,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
    showAction: Boolean = false
): Snackbar? {
    if (view == null) return null
    val snack = Snackbar.make(view, strId, duration)
    if (showAction) snack.setAction("好的") { snack.dismiss() }
    snack.show()
    return snack
}

//数值类型相关的拓展函数
/**
 * 将某个数值当作dp值,并将其转成当前分辨率的px值
 * e.g.
 *      val pxValue = 10.dp //将10dp转换成px
 */
inline val Float.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

inline val Float.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()
inline val Int.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

/**
 * 将某个数值当作dp值,并将其转成当前分辨率的px值
 * e.g.
 *      val pxValue = 10.asDp2Px //将10dp转换成px
 */
inline val Float.asPx2dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
inline val Int.asPx2dp: Int
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()


//字符串 的拓展方法

fun String.md5(): String {
    try {
        val instance = MessageDigest.getInstance("MD5")
        val digest = instance.digest("${this}goldenwatersoft433".toByteArray())
        val sb: StringBuffer = StringBuffer()
        digest.forEach {
            val i = it.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }
        return sb.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}





fun Long.formatTime(format: String): String {
    return SimpleDateFormat(format, Locale.CHINA).format(Date(this))
}

//Activity 的拓展属性

inline val Activity.exScreenWidth: Int
    get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width

inline val Activity.exScreenHeight: Int
    get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height

// [Kotlin Exception] 的拓展属性

inline val Throwable.nMessage: String
    get() = this.message ?: "出小差了"

