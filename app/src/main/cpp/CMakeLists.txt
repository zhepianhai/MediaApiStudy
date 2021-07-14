# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
#指定cmake的最小版本号
cmake_minimum_required(VERSION 3.4.1)
#定义项目名称,可以不定义
#project(demo)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#设置so库的输出路径
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})


#设置编译类型
#add_executable(demo demo.cpp) # 生成可执行文件
#生成动态共享库
add_library( # 设置编译成so库的名称
        lame-lib

        # 生成动态库或共享库，此处如果SHARED改为STATIC，其含义是生成静态库
        SHARED

        # 提供一个需要编译的源文件的相对路径()，native-lib.cpp就是需要编译的源文件
        native-lib.cpp
        lame/bitstream.c
        lame/encoder.c
        lame/fft.c
        lame/gain_analysis.c
        lame/id3tag.c
        lame/lame.c
        lame/mpglib_interface.c
        lame/newmdct.c
        lame/presets.c
        lame/psymodel.c
        lame/quantize.c
        lame/quantize_pvt.c
        lame/reservoir.c
        lame/set_get.c
        lame/tables.c
        lame/takehiro.c
        lame/util.c
        lame/vbrquantize.c
        lame/VbrTag.c
        lame/version.c
        )

#明确指定编译时需要编译哪些源文件
#add_library(demo demo.cpp test.cpp util.cpp)

#aux_source_directory(dir VAR) 发现一个目录下所有的源代码文件并将列表存储在一个变量中。
#例如：aux_source_directory(. SRC_LIST) # 搜索当前目录下的所有.cpp文件
#add_library(demo ${SRC_LIST})


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
#查找到指定的预编译库，并将它的路径存储在变量中
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#设置target需要链接的库
target_link_libraries( # Specifies the target library.目标库
        lame-lib

        # Links the target library to the log library 目标库需要链接的库，log-lib是上面find_library指定的变量名
        # included in the NDK.
        ${log-lib})
