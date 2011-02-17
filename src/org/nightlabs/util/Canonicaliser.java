package org.nightlabs.util;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.nightlabs.util.reflect.ReflectUtil;

/**
 * <p>
 * Cache to replace non-identical but equal objects by canonical (identical) objects.
 * </p>
 * <p>
 * In one Java VM, multiple {@link String}s (or other immutable objects) might exist separately
 * with equal content, thus unnecessarily allocating multiple times memory for the same data.
 * For example, imagine you read 10 times the identifier "MY_IDENTIFIER_0" from a database,
 * a TCP socket or another input. This way, you end up with 10 instances of this same <code>String</code>,
 * each allocating 15 chars (i.e. 30 bytes). This means, you allocate 300 bytes of (redundant) data
 * plus 10 times another 64 bits object references, i.e. in total about 380 bytes.
 * </p>
 * <p>
 * Since the 10 <code>String</code>s actually contain the same content, you might as well have your
 * 10 object references point to the very same String instance. This can be achieved by using
 * a {@link Canonicaliser}: It stores object instances in a {@link WeakHashMap} and replaces
 * all objects that are equal to one already cached object by this cached instance.
 * </p>
 * <p>
 * Example 1:
 * </p>
 * <pre>
 * String fieldValue = input.readField();
 * fieldValue = canoncialiser.canonicalise(fieldValue);
 * complexObjectGraph.setSomeFieldValue(fieldValue);
 * </pre>
 * <p>
 * In contrast to a simple {@link WeakHashMap}, this Canonicaliser inspects an object graph recursively (using
 * reflection) and replaces all <code>String</code>s (and other {@link #getReplaceableTypes() replaceable types})
 * within the whole object graph.
 * </p>
 * <p>
 * Example 2:
 * </p>
 * <pre>
 * public class MyIdentifier {
 * 	public String namespace;
 * 	public String localID;
 *
 * 	// hashCode and equals methods are based on these 2 fields.
 * }
 *
 * // within some method
 * MyIdentifier id = input.readIdentifier();
 * id = canonicaliser.canonicalise(id);
 * resultList.add(id);
 * </pre>
 * <p>
 * Depending on whether your {@link Canonicaliser} is {@link #setReplaceableTypes(Class...) configured to replace}
 * <code>MyIdentifier</code> instances, either the <code>id</code> itself would already be
 * replaced (if the same id occurs multiple times) or at least the strings inside the ids are replaced;
 * the same namespace is very likely occuring multiple times in our example (that's the nature of a namespace).
 * </p>
 *
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class Canonicaliser {

	private Map<Object, Object> replacementMap = new WeakHashMap<Object, Object>();

	/**
	 * <p>
	 * Canonicalise the given <code>object</code>. The object graph will be recursively
	 * inspected and object instances are replaced according to the
	 * {@link #getReplaceableTypes() replaceable types} as well as the
	 * {@link #getNeverReplaceableTypes() never-replaceable types}.
	 * </p>
	 * <p>
	 * The result of this method is either the given <code>object</code> itself or another
	 * instance that is equal to <code>object</code> (depending on the type of <code>object</code>
	 * and the replaceable/never-replaceable types).
	 * </p>
	 *
	 * @param <T> the type of the object.
	 * @param object the root of the object-graph to be canonicalised. Note, that this object might be manipulated using reflection!
	 * @return the canonicalised object - either the very same instance as the given <code>object</code> or another instance.
	 */
	public <T> T canonicalise(T object)
	{
		return internalCanonicalise(object, new IdentityHashMap<Object, Object>());
	}

	private Map<Class<?>, Boolean> class2replace = Collections.synchronizedMap(new HashMap<Class<?>, Boolean>());

	private List<Class<?>> replaceableTypes;
	{
		// set default values
		setReplaceableTypes((List<Class<?>>)null);
	}

	/**
	 * Convenience method delegating to {@link #setReplaceableTypes(Collection)}.
	 * @param replaceableTypes the replaceable types (classes or interfaces).
	 * Can be <code>null</code> for setting default values.
	 * @see #getReplaceableTypes()
	 */
	public void setReplaceableTypes(Class<?> ... replaceableTypes) {
		setReplaceableTypes(replaceableTypes == null ? null : Arrays.asList(replaceableTypes));
	}

	/**
	 * <p>
	 * Set the replaceable types. See {@link #getReplaceableTypes()} for more details.
	 * </p>
	 * <p>
	 * If you pass <code>null</code> to this method, it will set meaningful default values,
	 * which currently means to replace instances of {@link String}, {@link Number} and {@link UUID}.
	 * </p>
	 * <p>
	 * This list of default types might be extended without notice.
	 * </p>
	 *
	 * @param replaceableTypes the replaceable types (classes or interfaces).
	 * Can be <code>null</code> for setting default values.
	 * @see #getReplaceableTypes()
	 */
	public void setReplaceableTypes(Collection<Class<?>> replaceableTypes) {
		if (replaceableTypes == null)
		{
			replaceableTypes = new LinkedList<Class<?>>();
			replaceableTypes.add(String.class);
			replaceableTypes.add(Number.class);
			replaceableTypes.add(UUID.class);
		}

		this.replaceableTypes = Collections.unmodifiableList(
				new ArrayList<Class<?>>(replaceableTypes)
		);
		class2replace.clear();
	}

	/**
	 * <p>
	 * Get the types (classes or interfaces) that are considered replaceable. All objects
	 * inside the object-graph passed to {@link #canonicalise(Object)} that are an instance of
	 * one of these classes/interfaces will be replaced by previously cached equal objects.
	 * </p>
	 * <p>
	 * Usually, all those classes should be replaceable, which have immutable object instances
	 * (either by code or by convention) and a proper {@link Object#equals(Object)} and {@link Object#hashCode()}
	 * implementation.
	 * </p>
	 * <p>
	 * Note, that the {@link #getNeverReplaceableTypes() never-replaceable} types have a higher priority
	 * and overrule the replaceable types. For example, you could set <code>java.lang.Object</code> as the only
	 * replaceable type and then exclude {@link Collection} and {@link Map} causing all objects that
	 * do not implement one of these interfaces (directly or indirectly) to be replaced.
	 * </p>
	 *
	 * @return the replaceable types - never <code>null</code>.
	 * @see #setReplaceableTypes(Class...)
	 * @see #setReplaceableTypes(Collection)
	 * @see #getNeverReplaceableTypes()
	 */
	public Collection<Class<?>> getReplaceableTypes() {
		return replaceableTypes;
	}


	private Map<Class<?>, Boolean> class2neverReplace = Collections.synchronizedMap(new HashMap<Class<?>, Boolean>());

	private List<Class<?>> neverReplaceableTypes;
	{
		// set default values
		setNeverReplaceableTypes((List<Class<?>>)null);
	}

	/**
	 * Convenience method delegating to {@link #setNeverReplaceableTypes(Collection)}.
	 * @param neverReplaceableTypes the never-replaceable types (classes or interfaces).
	 * Can be <code>null</code> for setting default values.
	 * @see #getNeverReplaceableTypes()
	 */
	public void setNeverReplaceableTypes(Class<?> ... neverReplaceableTypes) {
		setNeverReplaceableTypes(neverReplaceableTypes == null ? null : Arrays.asList(neverReplaceableTypes));
	}

	/**
	 * <p>
	 * Set the never-replaceable types. See {@link #getNeverReplaceableTypes()} for more details.
	 * </p>
	 * <p>
	 * If you pass <code>null</code> to this method, it will set meaningful default values,
	 * which currently means to exclude instances of {@link Collection}, {@link Map} and {@link Object[]}.
	 * This is of course only relevant, if you set non-default
	 * {@link #setReplaceableTypes(Collection) replaceable types}, e.g. <code>java.lang.Object</code>.
	 * The reason to exclude collections and maps by default is that their <code>equals(...)</code> method [which is
	 * used by the replacement code] is extremely slow with lots of entries and replacing them makes no sense
	 * in most situations, anyway.
	 * </p>
	 * <p>
	 * This list of default types might be extended without notice.
	 * </p>
	 *
	 * @param neverReplaceableTypes the never-replaceable types (classes or interfaces).
	 * Can be <code>null</code> for setting default values.
	 * @see #getNeverReplaceableTypes()
	 */
	public void setNeverReplaceableTypes(Collection<Class<?>> neverReplaceableTypes) {
		if (neverReplaceableTypes == null)
		{
			neverReplaceableTypes = new LinkedList<Class<?>>();
			neverReplaceableTypes.add(Collection.class);
			neverReplaceableTypes.add(Map.class);
			neverReplaceableTypes.add(Object[].class);
		}

		this.neverReplaceableTypes = Collections.unmodifiableList(
				new ArrayList<Class<?>>(neverReplaceableTypes)
		);
		class2neverReplace.clear();
	}

	/**
	 * <p>
	 * Get the types (classes or interfaces) whose instances should never be replaced.
	 * </p>
	 * <p>
	 * This list overrules the {@link #getReplaceableTypes() replaceable types}.
	 * </p>
	 *
	 * @return the never-replaceable types - never <code>null</code>.
	 * @see #setNeverReplaceableTypes(Class...)
	 * @see #setNeverReplaceableTypes(Collection)
	 * @see #getReplaceableTypes()
	 */
	public Collection<Class<?>> getNeverReplaceableTypes() {
		return neverReplaceableTypes;
	}


	protected <T> T internalCanonicalise(T object, IdentityHashMap<Object, Object> processed)
	{
		if (object == null)
			return null;

		if (processed.containsKey(object))
			return object;

		processed.put(object, object);


		Class<? extends Object> type = object.getClass();

		Boolean neverReplace = class2neverReplace.get(type);
		if (neverReplace == null) {
			neverReplace = Boolean.FALSE;
			for (Class<?> clazz : neverReplaceableTypes) {
				if (clazz.isInstance(object)) {
					neverReplace = Boolean.TRUE;
					break;
				}
			}
			class2neverReplace.put(type, neverReplace);
		}

		Boolean replace = neverReplace ? Boolean.FALSE : class2replace.get(type);
		if (replace == null) {
			replace = Boolean.FALSE;
			for (Class<?> clazz : replaceableTypes) {
				if (clazz.isInstance(object)) {
					replace = Boolean.TRUE;
					break;
				}
			}
			class2replace.put(type, replace);
		}

		T result = null;

		if (replace) {
			synchronized (replacementMap) {
				@SuppressWarnings("unchecked")
				WeakReference<T> w = (WeakReference<T>) replacementMap.get(object);
				result = w == null ? null : w.get();
				if (result == null)
					replacementMap.put(object, new WeakReference<T>(object));
			}
		}

		if (result == null) {
			result = object;

			if (type.isArray()) {
				Object[] array = (Object[]) object;
				for (int i = 0; i < array.length; ++i)
					array[i] = internalCanonicalise(array[i], processed);
			}
			else {
//				stopwatch.start("50.80.collectAllFields");
				List<Field> fields = ReflectUtil.collectAllFields(type, true);
//				stopwatch.stop("50.80.collectAllFields");

				try {
					for (Field field : fields) {
						if (field.getType().isPrimitive())
							continue;

						field.setAccessible(true);
						Object value = field.get(object);
						Object valueDedup = internalCanonicalise(value, processed);

						if (value != valueDedup)
							field.set(object, valueDedup);
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return result;
	}

//	private static Stopwatch stopwatch = new Stopwatch();
//
//	public static void main(String[] args)
//	throws Throwable
//	{
////		stopwatch.start("00.total");
////
////		Canonicaliser cache = new Canonicaliser();
////		List<String> list = new ArrayList<String>();
////
////		for (int i = 0; i < 1000000; ++i) {
////			StringBuilder sb = new StringBuilder();
////
////			for (int b = 0; b < 36; ++b)
////				sb.appendCodePoint(65 + b);
////
////			String s1 = sb.toString();
////			String s2 = s1;
////			stopwatch.start("50.deduplicate");
////			s2 = cache.deduplicate(s1);
////			stopwatch.stop("50.deduplicate");
////
////			stopwatch.start("60.list.add");
////			list.add(s2);
////			stopwatch.stop("60.list.add");
////		}
////		stopwatch.start("80.writeFile");
////		ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("/tmp/test.ser"));
////		objOut.writeObject(list);
////		objOut.close();
////		stopwatch.stop("80.writeFile");
////
////		stopwatch.stop("00.total");
////		System.out.println(stopwatch.createHumanReport(true));
//
//
//		stopwatch.start("00.total");
//
//		Canonicaliser canonicaliser = new Canonicaliser();
//		canonicaliser.setReplaceableTypes(Object.class);
//		List<TestObjectID> list = new ArrayList<TestObjectID>();
//
//		for (int i = 0; i < 5000000; ++i) {
//			StringBuilder sb = new StringBuilder();
//
//			for (int b = 0; b < 45; ++b)
//				sb.appendCodePoint(65 + b);
//
//			TestObjectID oid = new TestObjectID();
//			oid.organisationID = sb.toString();
//			oid.localID = i % 10;
//
//			stopwatch.start("50.canonicalise");
//			oid = canonicaliser.canonicalise(oid);
//			stopwatch.stop("50.canonicalise");
//
//			stopwatch.start("60.list.add");
//			list.add(oid);
//			stopwatch.stop("60.list.add");
//		}
//
////		stopwatch.start("75.canonicalise");
////		list = canonicaliser.canonicalise(list);
////		stopwatch.stop("75.canonicalise");
//
//		stopwatch.start("80.writeFile");
//		ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("/tmp/test.ser"));
//		objOut.writeObject(list);
//		objOut.close();
//		stopwatch.stop("80.writeFile");
//
//		stopwatch.stop("00.total");
//		System.out.println(stopwatch.createHumanReport(true));
//	}
}
