package org.nightlabs.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * {@link List}-implementation that can be used to parse comma-separated UUID-Strings
 * in REST-URLs. For example, in the URL
 * http://host/resource/0734afce-421b-4bb3-aafa-f6674df2d170,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,45024399-06a5-4a67-8c95-e6e737ee4514
 * the part after the last slash ("/") could be passed directly to the constructor {@link #UUIDList(String)}.
 *
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class UUIDList extends ArrayList<UUID>
{
	/**
	 * Create a new {@link UUIDList} with all elements from the specified collection <code>c</code>.
	 * @param c the collection from which to copy the elements.
	 * @see ArrayList#ArrayList(Collection)
	 */
	public UUIDList(Collection<? extends UUID> c) {
		super(c);
	}

	/**
	 * Create a new {@link UUIDList} with the specified initial capacity.
	 * @param initialCapacity the initial capacity.
	 * @see ArrayList#ArrayList(int)
	 */
	public UUIDList(int initialCapacity) {
		super(initialCapacity);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new empty {@link UUIDList} with default settings.
	 * @see ArrayList#ArrayList()
	 */
	public UUIDList() { }

	/**
	 * <p>
	 * Creates a new {@link UUIDList} with elements parsed from the specified <code>uuidListString</code>.
	 * The string argument can optionally start and stop with '[' and ']' in order to make this constructor
	 * compatible with the {@link #toString()} result. The brackets are, however, not necessary.
	 * Empty elements, i.e. multiple commas directly following each other, are silently ignored. Spaces
	 * are silently ignored, if they occur around commas. They must not occur inbetween the UUID-String-representations!
	 * </p>
	 * <p>
	 * The String-value "null" is detected as <code>null</code> and no attempt is made to parse this into a {@link UUID} (which again
	 * is compatible with the {@link #toString()} method, which writes "null" for each <code>null</code>-element).
	 * </p>
	 * <p>
	 * The following Strings can all be parsed by this constructor and lead to the same result:
	 * </p>
	 * <ul>
	 * <li>[0734afce-421b-4bb3-aafa-f6674df2d170, null, 7fdcbd8f-f4af-4d37-82ff-56fe3a892489, 45024399-06a5-4a67-8c95-e6e737ee4514]</li>
	 * <li>0734afce-421b-4bb3-aafa-f6674df2d170, null, 7fdcbd8f-f4af-4d37-82ff-56fe3a892489, 45024399-06a5-4a67-8c95-e6e737ee4514</li>
	 * <li>0734afce-421b-4bb3-aafa-f6674df2d170,null,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,45024399-06a5-4a67-8c95-e6e737ee4514</li>
	 * <li>0734afce-421b-4bb3-aafa-f6674df2d170,null,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,45024399-06a5-4a67-8c95-e6e737ee4514,</li>
	 * <li>,0734afce-421b-4bb3-aafa-f6674df2d170,null,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,45024399-06a5-4a67-8c95-e6e737ee4514</li>
	 * <li>,0734afce-421b-4bb3-aafa-f6674df2d170,,null,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,,,45024399-06a5-4a67-8c95-e6e737ee4514,</li>
	 * </ul>
	 * @param uuidListString a comma-separated String-representation of multiple UUIDs - e.g. the result of a previous invocation of {@link #toString()}.
	 */
	public UUIDList(String uuidListString) {
		if (uuidListString != null) {
			StringTokenizer st = new StringTokenizer(uuidListString, ", []", true);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (" ".equals(token))
					;// ignore
				else if (",".equals(token))
					;// ignore
				else if ("[".equals(token))
					;// ignore
				else if ("]".equals(token))
					;// ignore
				else if ("null".equals(token))
					this.add(null);
				else {
					UUID uuid = UUID.fromString(token);
					this.add(uuid);
				}
			}

//			if (uuidListString.startsWith("["))
//				uuidListString = uuidListString.substring(1);
//
//			if (uuidListString.endsWith("]"))
//				uuidListString = uuidListString.substring(0, uuidListString.length() - 1);
//
//			String[] uuidStrings = uuidListString.split(",");
//			for (String uuidString : uuidStrings) {
//				uuidString = uuidString.trim();
//				if ("null".equals(uuidString))
//					this.add(null);
//				else {
//					UUID uuid = UUID.fromString(uuidString);
//					this.add(uuid);
//				}
//			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The result of this method can be passed to the constructor {@link #UUIDList(String)} and both lists will be equal.
	 * In other words, the following code will always write "true" to standard out:
	 * <pre>
	 * UUIDList l1 = new UUIDList();
	 * l1.add(null);
	 * l1.add(UUID.randomUUID());
	 * l1.add(UUID.randomUUID());
	 * l1.add(null);
	 * l1.add(UUID.randomUUID());
	 * String s1 = l1.toString();
	 * UUIDList l2 = new UUIDList(s1);
	 * System.out.println(l1.equals(l2));
	 * </pre>
	 * </p>
	 */
	@Override
	public String toString() {
		return super.toString();
	}

//	public static void main(String[] args) {
//		UUIDList l;
//		l = new UUIDList();
//		l.add(null);
//		l.add(UUID.randomUUID());
//		l.add(null);
//		l.add(null);
//		l.add(UUID.randomUUID());
//		String s = l.toString();
//		UUIDList l2 = new UUIDList(s);
//		System.out.println(l);
//		System.out.println(l.equals(l2));
//
//		l = new UUIDList(",0734afce-421b-4bb3-aafa-f6674df2d170,,7fdcbd8f-f4af-4d37-82ff-56fe3a892489,,,45024399-06a5-4a67-8c95-e6e737ee4514,");
//		System.out.println(l);
//	}
}
