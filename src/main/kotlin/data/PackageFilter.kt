package data

/**
 * 获取apk包名筛选参数
 */
enum class PackageFilter(val param: String) {
    SIMPLE("simple"),
    ALL("all")
}