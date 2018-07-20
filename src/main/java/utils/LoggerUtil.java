package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Asus- on 2018/7/20.
 */
public class LoggerUtil {

    /**
     * 查找堆栈信息,查找下一个非日志类
     *
     * @return
     */
    private static StackTraceElement findElement() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements == null)
            return null;
        // 最原始被调用的堆栈信息
        StackTraceElement element = null;
        // 日志类名称
        String logClassName = LoggerUtil.class.getName();
        // 循环遍历到日志类标识
        boolean isLogClass = false;
        for (StackTraceElement ele : elements) {
            if (logClassName.equals(ele.getClassName())) {
                isLogClass = true;
            }
            //下一个非日志类
            if (isLogClass) {
                if (!logClassName.equals(ele.getClassName())) {
                    isLogClass = false;
                    element = ele;
                    break;
                }
            }
        }
        return element;
    }

    /**
     * 自动匹配请求类名，生成logger对象
     *
     * @return
     */
    private static Logger logger() {
        // 最原始被调用的堆栈对象
        StackTraceElement caller = findElement();
        if (null == caller) return LoggerFactory.getLogger(LoggerUtil.class);
        Logger log = LoggerFactory.getLogger(caller.getClassName() + "."
                + caller.getMethodName()
                + "() Line: "
                + caller.getLineNumber());
        return log;
    }

    /**
     * 分别对应5个等级的log
     *
     * @param msg
     */
    public static void error(String msg) {
        logger().error(msg);
    }

    public static void wran(String msg) {
        logger().warn(msg);
    }

    public static void info(String msg) {
        logger().info(msg);
    }

    public static void debug(String msg) {
        logger().debug(msg);
    }

    public static void trance(String msg) {
        logger().trace(msg);
    }
}
