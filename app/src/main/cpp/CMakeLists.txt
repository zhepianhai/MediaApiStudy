cmake_minimum_required(VERSION 3.4.1)
include_directories(include)
add_library(
        native-lib
        SHARED
        native-lib.cpp)

#添加libavcodec.so
add_library( avcodec
        SHARED
        IMPORTED)
set_target_properties( avcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libavcodec.so)

#添加libavdevice.so
add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties( avdevice
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavdevice.so)

add_library( avfilter
        SHARED
        IMPORTED)
set_target_properties( avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavfilter.so)

add_library( avformat
        SHARED
        IMPORTED)
set_target_properties( avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavformat.so)

add_library( avutil
        SHARED
        IMPORTED)
set_target_properties( avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavutil.so)

add_library( swresample
        SHARED
        IMPORTED)
set_target_properties( swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libswresample.so)

add_library( swscale
        SHARED
        IMPORTED)
set_target_properties( swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libswscale.so)


find_library(
        log-lib
        log )
target_link_libraries(
        native-lib
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        swresample
        swscale
        ${log-lib} )