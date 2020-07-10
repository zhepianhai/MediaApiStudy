package com.zph.media.util

import org.json.JSONTokener
import org.json.JSONArray;
import org.json.JSONObject;

object JsonParser {
    fun parseIatResult(json: String?): String? {
        val ret = StringBuffer()
        try {
            val tokener = JSONTokener(json)
            val joResult = JSONObject(tokener)
            val words: JSONArray = joResult.getJSONArray("ws")
            for (i in 0 until words.length()) {
                // 转写结果词，默认使用第一个结果
                val items: JSONArray = words.getJSONObject(i).getJSONArray("cw")
                val obj: JSONObject = items.getJSONObject(0)
                ret.append(obj.getString("w"))
                //				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret.toString()
    }

    fun parseGrammarResult(json: String?, engType: String): String? {
        val ret = StringBuffer()
        try {
            val tokener = JSONTokener(json)
            val joResult = JSONObject(tokener)
            val words: JSONArray = joResult.getJSONArray("ws")
            // 云端和本地结果分情况解析
            if ("cloud" == engType) {
                for (i in 0 until words.length()) {
                    val items: JSONArray = words.getJSONObject(i).getJSONArray("cw")
                    for (j in 0 until items.length()) {
                        val obj: JSONObject = items.getJSONObject(j)
                        if (obj.getString("w").contains("nomatch")) {
                            ret.append("没有匹配结果.")
                            return ret.toString()
                        }
                        ret.append("【结果】" + obj.getString("w"))
                        ret.append("【置信度】" + obj.getInt("sc"))
                        ret.append("\n")
                    }
                }
            } else if ("local" == engType) {
                ret.append("【结果】")
                for (i in 0 until words.length()) {
                    val wsItem: JSONObject = words.getJSONObject(i)
                    val items: JSONArray = wsItem.getJSONArray("cw")
                    if ("<contact>" == wsItem.getString("slot")) {
                        // 可能会有多个联系人供选择，用中括号括起来，这些候选项具有相同的置信度
                        ret.append("【")
                        for (j in 0 until items.length()) {
                            val obj: JSONObject = items.getJSONObject(j)
                            if (obj.getString("w").contains("nomatch")) {
                                ret.append("没有匹配结果.")
                                return ret.toString()
                            }
                            ret.append(obj.getString("w")).append("|")
                        }
                        ret.setCharAt(ret.length - 1, '】')
                    } else {
                        //本地多候选按照置信度高低排序，一般选取第一个结果即可
                        val obj: JSONObject = items.getJSONObject(0)
                        if (obj.getString("w").contains("nomatch")) {
                            ret.append("没有匹配结果.")
                            return ret.toString()
                        }
                        ret.append(obj.getString("w"))
                    }
                }
                ret.append("【置信度】" + joResult.getInt("sc"))
                ret.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret.append("没有匹配结果.")
        }
        return ret.toString()
    }

    fun parseGrammarResult(json: String?): String? {
        val ret = StringBuffer()
        try {
            val tokener = JSONTokener(json)
            val joResult = JSONObject(tokener)
            val words: JSONArray = joResult.getJSONArray("ws")
            for (i in 0 until words.length()) {
                val items: JSONArray = words.getJSONObject(i).getJSONArray("cw")
                for (j in 0 until items.length()) {
                    val obj: JSONObject = items.getJSONObject(j)
                    if (obj.getString("w").contains("nomatch")) {
                        ret.append("没有匹配结果.")
                        return ret.toString()
                    }
                    ret.append("【结果】" + obj.getString("w"))
                    ret.append("【置信度】" + obj.getInt("sc"))
                    ret.append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret.append("没有匹配结果.")
        }
        return ret.toString()
    }

    fun parseTransResult(json: String?, key: String?): String? {
        val ret = StringBuffer()
        try {
            val tokener = JSONTokener(json)
            val joResult = JSONObject(tokener)
            val errorCode: String = joResult.optString("ret")
            if (errorCode != "0") {
                return joResult.optString("errmsg")
            }
            val transResult: JSONObject = joResult.optJSONObject("trans_result")
            ret.append(transResult.optString(key))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret.toString()
    }
}