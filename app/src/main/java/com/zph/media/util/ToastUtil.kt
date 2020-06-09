package com.zph.media.util

import android.content.Context
import android.text.TextUtils
import android.widget.Toast

class ToastUtil {
    companion object {
        fun showToast(context: Context?, str: String?) {
            var str = str
            if (TextUtils.isEmpty(str)) {
                str = ""
            }
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }

        fun showToast(context: Context?, stringRes: Int) {
            Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
        }
    }
}