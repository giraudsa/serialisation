/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.generic;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a generic type {@code T}. Java doesn't yet provide a way to
 * represent generic types, so this class does. Forces clients to create a
 * subclass of this class which enables retrieval the type information even at
 * runtime.
 *
 * <p>
 * For example, to create a type literal for {@code List<String>}, you can
 * create an empty anonymous inner class:
 *
 * <p>
 * {@code TypeToken<List<String>> list = new TypeToken<List<String>>() {};}
 *
 * <p>
 * This syntax cannot be used to create type literals that have wildcard
 * parameters, such as {@code Class<?>} or {@code List<? extends CharSequence>}.
 *
 * @author Bob Lee
 * @author Sven Mawson
 * @author Jesse Wilson
 */
public class TypeToken<T> {
	private static AssertionError buildUnexpectedTypeError(final Type token, final Class<?>... expected) {

		// Build exception message
		final StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");
		for (final Class<?> clazz : expected)
			exceptionMessage.append(clazz.getName()).append(", ");
		exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ")
				.append(token.toString()).append('.');

		return new AssertionError(exceptionMessage.toString());
	}

	/**
	 * Gets type literal for the given {@code Class} instance.
	 */
	public static <T> TypeToken<T> get(final Class<T> type) {
		return new TypeToken<>(type);
	}

	/**
	 * Gets type literal for the given {@code Type} instance.
	 */
	public static TypeToken<?> get(final Type type) {
		return new TypeToken<>(type);
	}

	/**
	 * Returns the type from super class's type parameter in
	 * {@link $Gson$Types#canonicalize canonical form}.
	 */
	static Type getSuperclassTypeParameter(final Class<?> subclass) {
		final Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class)
			throw new RuntimeException("Missing type parameter.");
		final ParameterizedType parameterized = (ParameterizedType) superclass;
		return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
	}

	/**
	 * Private helper function that performs some assignability checks for the
	 * provided GenericArrayType.
	 */
	private static boolean isAssignableFrom(final Type from, final GenericArrayType to) {
		final Type toGenericComponentType = to.getGenericComponentType();
		if (toGenericComponentType instanceof ParameterizedType) {
			Type t = from;
			if (from instanceof GenericArrayType)
				t = ((GenericArrayType) from).getGenericComponentType();
			else if (from instanceof Class<?>) {
				Class<?> classType = (Class<?>) from;
				while (classType.isArray())
					classType = classType.getComponentType();
				t = classType;
			}
			return isAssignableFrom(t, (ParameterizedType) toGenericComponentType, new HashMap<String, Type>());
		}
		// No generic defined on "to"; therefore, return true and let other
		// checks determine assignability
		return true;
	}

	/**
	 * Private recursive helper function to actually do the type-safe checking of
	 * assignability.
	 */
	private static boolean isAssignableFrom(final Type from, final ParameterizedType to,
			final Map<String, Type> typeVarMap) {

		if (from == null)
			return false;

		if (to.equals(from))
			return true;

		// First figure out the class and any type information.
		final Class<?> clazz = $Gson$Types.getRawType(from);
		ParameterizedType ptype = null;
		if (from instanceof ParameterizedType)
			ptype = (ParameterizedType) from;

		// Load up parameterized variable info if it was parameterized.
		if (ptype != null) {
			final Type[] tArgs = ptype.getActualTypeArguments();
			final TypeVariable<?>[] tParams = clazz.getTypeParameters();
			for (int i = 0; i < tArgs.length; i++) {
				Type arg = tArgs[i];
				final TypeVariable<?> var = tParams[i];
				while (arg instanceof TypeVariable<?>) {
					final TypeVariable<?> v = (TypeVariable<?>) arg;
					arg = typeVarMap.get(v.getName());
				}
				typeVarMap.put(var.getName(), arg);
			}

			// check if they are equivalent under our current mapping.
			if (typeEquals(ptype, to, typeVarMap))
				return true;
		}

		for (final Type itype : clazz.getGenericInterfaces())
			if (isAssignableFrom(itype, to, new HashMap<>(typeVarMap)))
				return true;

		// Interfaces didn't work, try the superclass.
		final Type sType = clazz.getGenericSuperclass();
		return isAssignableFrom(sType, to, new HashMap<>(typeVarMap));
	}

	/**
	 * Checks if two types are the same or are equivalent under a variable mapping
	 * given in the type map that was provided.
	 */
	private static boolean matches(final Type from, final Type to, final Map<String, Type> typeMap) {
		return to.equals(from)
				|| from instanceof TypeVariable && to.equals(typeMap.get(((TypeVariable<?>) from).getName()));

	}

	/**
	 * Checks if two parameterized types are exactly equal, under the variable
	 * replacement described in the typeVarMap.
	 */
	private static boolean typeEquals(final ParameterizedType from, final ParameterizedType to,
			final Map<String, Type> typeVarMap) {
		if (from.getRawType().equals(to.getRawType())) {
			final Type[] fromArgs = from.getActualTypeArguments();
			final Type[] toArgs = to.getActualTypeArguments();
			for (int i = 0; i < fromArgs.length; i++)
				if (!matches(fromArgs[i], toArgs[i], typeVarMap))
					return false;
			return true;
		}
		return false;
	}

	final int hashCode;

	final Class<? super T> rawType;

	final Type type;

	/**
	 * Constructs a new type literal. Derives represented class from type parameter.
	 *
	 * <p>
	 * Clients create an empty anonymous subclass. Doing so embeds the type
	 * parameter in the anonymous class's type hierarchy so we can reconstitute it
	 * at runtime despite erasure.
	 */
	@SuppressWarnings("unchecked")
	protected TypeToken() {
		this.type = getSuperclassTypeParameter(getClass());
		this.rawType = (Class<? super T>) $Gson$Types.getRawType(type);
		this.hashCode = type.hashCode();
	}

	/**
	 * Unsafe. Constructs a type literal manually.
	 */
	@SuppressWarnings("unchecked")
	TypeToken(final Type type) {
		this.type = $Gson$Types.canonicalize($Gson$Preconditions.checkNotNull(type));
		this.rawType = (Class<? super T>) $Gson$Types.getRawType(this.type);
		this.hashCode = this.type.hashCode();
	}

	@Override
	public final boolean equals(final Object o) {
		return o instanceof TypeToken<?> && $Gson$Types.equals(type, ((TypeToken<?>) o).type);
	}

	/**
	 * Returns the raw (non-generic) type for this type.
	 */
	public final Class<? super T> getRawType() {
		return rawType;
	}

	/**
	 * Gets underlying {@code Type} instance.
	 */
	public final Type getType() {
		return type;
	}

	@Override
	public final int hashCode() {
		return this.hashCode;
	}

	/**
	 * Check if this type is assignable from the given class object.
	 *
	 * @deprecated this implementation may be inconsistent with javac for types with
	 *             wildcards.
	 */
	@Deprecated
	public boolean isAssignableFrom(final Class<?> cls) {
		return isAssignableFrom((Type) cls);
	}

	/**
	 * Check if this type is assignable from the given Type.
	 *
	 * @deprecated this implementation may be inconsistent with javac for types with
	 *             wildcards.
	 */
	@Deprecated
	public boolean isAssignableFrom(final Type from) {
		if (from == null)
			return false;

		if (type.equals(from))
			return true;

		if (type instanceof Class<?>)
			return rawType.isAssignableFrom($Gson$Types.getRawType(from));
		else if (type instanceof ParameterizedType)
			return isAssignableFrom(from, (ParameterizedType) type, new HashMap<String, Type>());
		else if (type instanceof GenericArrayType)
			return rawType.isAssignableFrom($Gson$Types.getRawType(from))
					&& isAssignableFrom(from, (GenericArrayType) type);
		else
			throw buildUnexpectedTypeError(type, Class.class, ParameterizedType.class, GenericArrayType.class);
	}

	/**
	 * Check if this type is assignable from the given type token.
	 *
	 * @deprecated this implementation may be inconsistent with javac for types with
	 *             wildcards.
	 */
	@Deprecated
	public boolean isAssignableFrom(final TypeToken<?> token) {
		return isAssignableFrom(token.getType());
	}

	@Override
	public final String toString() {
		return $Gson$Types.typeToString(type);
	}
}
