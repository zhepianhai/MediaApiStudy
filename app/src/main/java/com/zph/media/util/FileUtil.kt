package com.zph.media.util

import android.content.Context
import java.io.*

/**
 * 文件工具类
 * */
class FileUtil {
    companion object {


        private val SEPARATOR = File.separator //路径分隔符


        fun create(filePath: String) {
            val file = File(filePath)
            if (filePath.indexOf(".") != -1) {
                // 说明包含，即使创建文件, 返回值为-1就说明不包含.,即使文件
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                println("创建了文件")
            } else {
                // 创建文件夹
                file.mkdir()
                println("创建了文件夹")
            }
        }


        /**
         * 删除文件或文件夹（包括文件夹下所有文件）
         *
         * @param temFile 文件对象
         * @return
         */
        fun deleteFileOrDir(temFile: File?): Boolean {
            if (temFile == null) {
                return false
            }
            if (!temFile.exists()) {
                return false
            }
            if (temFile.isDirectory) {
                val fileList = temFile.listFiles()
                val count = fileList.size
                for (i in 0 until count) {
                    deleteFileOrDir(fileList[i])
                }
                temFile.delete()
            } else if (temFile.isFile) {
                temFile.delete()
            }
            return true
        }

        /**
         * 删除文件或文件夹（包括文件夹下所有文件）
         *
         * @param path 文件的路径
         * @return
         */
        fun deleteDirContainFile(path: String?): Boolean {
            return if (path == null) {
                false
            } else deleteDirContainFile(File(path))
        }

        /**
         * 删除文件或文件夹（文件夹下所有文件）
         *
         * @return
         */
        fun deleteDirContainFile(file: File?): Boolean {
            if (file == null || !file.exists() || !file.isDirectory) {
                return false
            }
            val fileList = file.listFiles()
            val count = fileList.size
            var flag = true
            for (i in 0 until count) {
                flag = flag && deleteFileOrDir(fileList[i])
            }
            return flag
        }

        /**
         * 复制文件
         *
         * @param source 输入文件
         * @param target 输出文件
         */
        fun copy(source: File?, target: File?) {
            var fileInputStream: FileInputStream? = null
            var fileOutputStream: FileOutputStream? = null
            try {
                fileInputStream = FileInputStream(source)
                fileOutputStream = FileOutputStream(target)
                val buffer = ByteArray(1024)
                while (fileInputStream.read(buffer) > 0) {
                    fileOutputStream.write(buffer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fileInputStream?.close()
                    fileOutputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }






        /**
         * 复制res/raw中的文件到指定目录
         * @param context 上下文
         * @param id 资源ID
         * @param fileName 文件名
         * @param storagePath 目标文件夹的路径
         */
       fun copyFilesFromRaw(context: Context,  id:Int,  fileName:String,  storagePath:String){
            var inputStream=context.resources.openRawResource(id);
            var file =  File(storagePath);
            if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
                file.mkdirs();
            }
            readInputStream(storagePath + SEPARATOR + fileName, inputStream);
        }


        /**
         * 读取输入流中的数据写入输出流
         *
         * @param storagePath 目标文件路径
         * @param inputStream 输入流
         */
        fun readInputStream( storagePath:String,  inputStream: InputStream) {
            var file = File(storagePath);
            try {
                if (!file.exists()) {
                    // 1.建立通道对象
                    var fos = FileOutputStream(file);
                    // 2.定义存储空间
                    var buffer = ByteArray(inputStream.available())
                    // 3.开始读文件
                    while (inputStream.read(buffer) != -1) {// 循环从输入流读取buffer字节
                        // 将Buffer中的数据写到outputStream对象中
                        fos.write(buffer);
                    }
                    fos.flush();// 刷新缓冲区
                    // 4.关闭流
                    fos.close();
                    inputStream.close();
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }

    }
}