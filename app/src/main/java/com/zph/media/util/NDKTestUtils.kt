package com.zph.media.util


class NDKTestUtils {
    companion object {
        external fun operateString(s:String) :String
        external fun sumArray(array: IntArray): Int
        external fun init2DArray(size: Int): Array<IntArray>
    }
}