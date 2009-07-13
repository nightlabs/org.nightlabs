/**
 *
 */
package org.nightlabs.util;

import java.util.Comparator;

/**
 * Abstract base class for {@link Comparator} which already performs a null check {@link #comparatorNullCheck(Object, Object)}
 * and only if both elements to compare are not null, the given comparator passed in the constructor is used.
 *
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class BaseComparator<T>
implements Comparator<T>
{
	public static int COMPARE_RESULT_NOT_NULL = Integer.MAX_VALUE - 1;

	/**
	 * Check both elements for null.
	 * This method can be used inside implementations of {@link Comparator}
	 * to check for null occurrences.
	 *
	 * @param o1 the first element to compare
	 * @param o2 the second element to compare
	 * @return 0 if both elements are null, 1 if the first element is null and the second is not null,
	 * -1 if the first element is not null and the second is null. If both elements are not null
	 * {@link #COMPARE_RESULT_NOT_NULL} is returned.
	 *
	 */
	public static int comparatorNullCheck(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
			return 0;

		if (o1 == null && o2 != null)
			return 1;

		if (o1 != null && o2 == null)
			return -1;

		return COMPARE_RESULT_NOT_NULL;
	}

	private Comparator<T> comparator;

	/**
	 * Creates an BaseComparator.
	 * @param comparator the {@link Comparator} to use after null check is done.
	 */
	public BaseComparator(Comparator<T> comparator)
	{
		if (comparator == null)
			throw new IllegalArgumentException("Param comparator must not be null!");

		this.comparator = comparator;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(T o1, T o2)
	{
		int result = comparatorNullCheck(o1, o2);
		if (result == COMPARE_RESULT_NOT_NULL) {
			return comparator.compare(o1, o2);
		}
		return result;
	}

}
