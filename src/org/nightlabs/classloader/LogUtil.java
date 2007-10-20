/**
 * 
 */
package org.nightlabs.classloader;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 * @author marco schulze - marco at nightlabs dot de
 */
public class LogUtil {

	public static boolean debugEnabled = false;

	public static void log_debug(Class<?> clazz, String method, String message)
	{
		if (!debugEnabled)
			return;
		System.out.println(System.currentTimeMillis() + " DEBUG [" + Thread.currentThread().getName() + "] " + message);
	}
	public static void log_info(Class<?> clazz, String method, String message)
	{
		System.out.println(System.currentTimeMillis() + " INFO [" + Thread.currentThread().getName() + "] " + message);
	}
	public static void log_warn(Class<?> clazz, String method, String message)
	{
		log_warn(clazz, method, message, null);
	}
	public static void log_warn(Class<?> clazz, String method, String message, Throwable x)
	{
		System.err.println(System.currentTimeMillis() + " WARNING [" + Thread.currentThread().getName() + "] " + message);
		if (x != null)
			x.printStackTrace();
	}
	public static void log_error(Class<?> clazz, String method, String message)
	{
		log_error(clazz, method, message, null);
	}
	public static void log_error(Class<?> clazz, String method, String message, Throwable x)
	{
		System.err.println(System.currentTimeMillis() + " ERROR [" + Thread.currentThread().getName() + "] " + message);
		if (x != null)
			x.printStackTrace();
	}
}
