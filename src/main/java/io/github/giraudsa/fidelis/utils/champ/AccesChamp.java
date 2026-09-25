package io.github.giraudsa.fidelis.utils.champ;

/**
 * Lecture et écriture d'un champ d'objet. Les implémentations générées ({@link GenerateurAcces}) font un accès direct
 * (getfield/putfield) au lieu de passer par la réflexion ; celle d'un champ primitif redéfinit aussi le setter de son
 * type, qui évite le boxing.
 */
public interface AccesChamp {
	/** Nature du type d'un champ : AUCUNE pour un type objet, sinon le type primitif. */
	int AUCUNE = 0;
	int BOOLEAN = 1;
	int BYTE = 2;
	int SHORT = 3;
	int CHAR = 4;
	int INT = 5;
	int LONG = 6;
	int FLOAT = 7;
	int DOUBLE = 8;
	/** type void (valeur null). */
	int VOID = 9;

	static int nature(final Class<?> type) {
		if (!type.isPrimitive())
			return AUCUNE;
		if (type == int.class)
			return INT;
		if (type == long.class)
			return LONG;
		if (type == double.class)
			return DOUBLE;
		if (type == boolean.class)
			return BOOLEAN;
		if (type == float.class)
			return FLOAT;
		if (type == short.class)
			return SHORT;
		if (type == byte.class)
			return BYTE;
		if (type == char.class)
			return CHAR;
		return VOID;
	}

	Object get(Object objet);

	void set(Object objet, Object valeur);

	default boolean getBoolean(final Object objet) {
		return (Boolean) get(objet);
	}

	default byte getByte(final Object objet) {
		return (Byte) get(objet);
	}

	default char getChar(final Object objet) {
		return (Character) get(objet);
	}

	default double getDouble(final Object objet) {
		return (Double) get(objet);
	}

	default float getFloat(final Object objet) {
		return (Float) get(objet);
	}

	default int getInt(final Object objet) {
		return (Integer) get(objet);
	}

	default long getLong(final Object objet) {
		return (Long) get(objet);
	}

	default short getShort(final Object objet) {
		return (Short) get(objet);
	}

	default void setBoolean(final Object objet, final boolean valeur) {
		set(objet, valeur);
	}

	default void setByte(final Object objet, final byte valeur) {
		set(objet, valeur);
	}

	default void setChar(final Object objet, final char valeur) {
		set(objet, valeur);
	}

	default void setDouble(final Object objet, final double valeur) {
		set(objet, valeur);
	}

	default void setFloat(final Object objet, final float valeur) {
		set(objet, valeur);
	}

	default void setInt(final Object objet, final int valeur) {
		set(objet, valeur);
	}

	default void setLong(final Object objet, final long valeur) {
		set(objet, valeur);
	}

	default void setShort(final Object objet, final short valeur) {
		set(objet, valeur);
	}
}
