package utils

object DoubleClickUtils {

    private var lastClickTime = 0L

    private const val CLICK_INTERVAL_TIME = 300L

    /**
     *  快速双击
     */
    fun isFastDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        val timeDelta = time - lastClickTime
        lastClickTime = time
        return (timeDelta in 1..<CLICK_INTERVAL_TIME)
    }

    fun isFastDoubleClick(limitTime: Long): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime;
        val isFastClick = (timeD in 1..<limitTime)
        lastClickTime = time;
        return isFastClick
    }
}