package utils.io;

import java.io.IOException;

import utils.champ.AccesChamp;

/**
 * Codage binaire d'une valeur dont le type déclaré est primitif (champ d'objet, élément d'un tableau de primitifs) :
 * le lecteur connaît le type, aucun en-tête n'est écrit. Entiers en varint zigzag, flottants sur 4 ou 8 octets,
 * booléen et byte sur un octet, char en varint.
 */
public final class Primitifs {

	/** Écrit le champ primitif de l'objet, sans boxing. */
	public static void ecrit(final SortieBinaire out, final int nature, final AccesChamp acces, final Object objet)
			throws IOException {
		switch (nature) {
		case AccesChamp.INT:
			out.writeVarInt(zigzag(acces.getInt(objet)));
			break;
		case AccesChamp.LONG:
			out.writeVarLong(zigzag(acces.getLong(objet)));
			break;
		case AccesChamp.DOUBLE:
			out.writeDouble(acces.getDouble(objet));
			break;
		case AccesChamp.BOOLEAN:
			out.write(acces.getBoolean(objet) ? 1 : 0);
			break;
		case AccesChamp.FLOAT:
			out.writeFloat(acces.getFloat(objet));
			break;
		case AccesChamp.SHORT:
			out.writeVarInt(zigzag(acces.getShort(objet)));
			break;
		case AccesChamp.BYTE:
			out.write(acces.getByte(objet));
			break;
		default: // CHAR
			out.writeVarInt(acces.getChar(objet));
			break;
		}
	}

	/** Écrit une valeur enveloppée (Integer, Double...) sous le codage de la nature primitive donnée. */
	public static void ecritValeur(final SortieBinaire out, final int nature, final Object valeur) throws IOException {
		switch (nature) {
		case AccesChamp.INT:
			out.writeVarInt(zigzag(((Number) valeur).intValue()));
			break;
		case AccesChamp.LONG:
			out.writeVarLong(zigzag(((Number) valeur).longValue()));
			break;
		case AccesChamp.DOUBLE:
			out.writeDouble(((Number) valeur).doubleValue());
			break;
		case AccesChamp.BOOLEAN:
			out.write((Boolean) valeur ? 1 : 0);
			break;
		case AccesChamp.FLOAT:
			out.writeFloat(((Number) valeur).floatValue());
			break;
		case AccesChamp.SHORT:
			out.writeVarInt(zigzag(((Number) valeur).shortValue()));
			break;
		case AccesChamp.BYTE:
			out.write(((Number) valeur).byteValue());
			break;
		default: // CHAR
			out.writeVarInt((Character) valeur);
			break;
		}
	}

	/** Lit une valeur de nature primitive, enveloppée. */
	public static Object lit(final EntreeBinaire in, final int nature) throws IOException {
		switch (nature) {
		case AccesChamp.INT:
			return unzigzag(in.readVarInt());
		case AccesChamp.LONG:
			return unzigzag(in.readVarLong());
		case AccesChamp.DOUBLE:
			return in.readDouble();
		case AccesChamp.BOOLEAN:
			return in.readByte() != 0;
		case AccesChamp.FLOAT:
			return in.readFloat();
		case AccesChamp.SHORT:
			return (short) unzigzag(in.readVarInt());
		case AccesChamp.BYTE:
			return in.readByte();
		default: // CHAR
			return (char) in.readVarInt();
		}
	}

	/** Lit la valeur d'un champ primitif et l'écrit dans l'objet, sans boxing. */
	public static void litEtAffecte(final EntreeBinaire in, final int nature, final AccesChamp acces,
			final Object objet) throws IOException {
		switch (nature) {
		case AccesChamp.INT:
			acces.setInt(objet, unzigzag(in.readVarInt()));
			break;
		case AccesChamp.LONG:
			acces.setLong(objet, unzigzag(in.readVarLong()));
			break;
		case AccesChamp.DOUBLE:
			acces.setDouble(objet, in.readDouble());
			break;
		case AccesChamp.BOOLEAN:
			acces.setBoolean(objet, in.readByte() != 0);
			break;
		case AccesChamp.FLOAT:
			acces.setFloat(objet, in.readFloat());
			break;
		case AccesChamp.SHORT:
			acces.setShort(objet, (short) unzigzag(in.readVarInt()));
			break;
		case AccesChamp.BYTE:
			acces.setByte(objet, in.readByte());
			break;
		default: // CHAR
			acces.setChar(objet, (char) in.readVarInt());
			break;
		}
	}

	public static int zigzag(final int v) {
		return v << 1 ^ v >> 31;
	}

	public static long zigzag(final long v) {
		return v << 1 ^ v >> 63;
	}

	public static int unzigzag(final int v) {
		return v >>> 1 ^ -(v & 1);
	}

	public static long unzigzag(final long v) {
		return v >>> 1 ^ -(v & 1);
	}

	private Primitifs() {
	}
}
