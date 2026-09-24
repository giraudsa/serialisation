package utils.headers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.TypeExtension;
import utils.champ.AccesChamp;
import utils.io.EntreeBinaire;
import utils.io.SortieBinaire;

public class HeaderSimpleType<T> extends Header {
	private static HeaderSimpleType<Boolean> booleanFalse;
	private static HeaderSimpleType<Boolean> booleanTrue;
	/** type (primitif ou enveloppe) -> header indexé par la taille de codage. */
	private static final Map<Class<?>, HeaderSimpleType<?>[]> classAndEncodageMiniToHeader = new HashMap<>();
	private static HeaderSimpleType<Void> nullHeader;
	private static HeaderSimpleType<?>[] headersDouble;
	private static HeaderSimpleType<?>[] headersInteger;
	private static HeaderSimpleType<?>[] headersLong;

	public static Header getHeader(final Object o) {
		Registre.init();
		if (o == null)
			return nullHeader;
		if (o instanceof Boolean)
			return ((Boolean) o).booleanValue() ? booleanTrue : booleanFalse;
		final int encodage;
		if (o instanceof Character)
			encodage = (Character) o == 0 ? 0 : 2;
		else
			encodage = ByteHelper.getMinimumEncodage((Number) o);
		return headersDuType(o.getClass())[encodage];
	}

	/** Évite la HashMap sur le chemin courant : les types enveloppes sont testés directement. */
	private static HeaderSimpleType<?>[] headersDuType(final Class<?> type) {
		if (type == Integer.class)
			return headersInteger;
		if (type == Long.class)
			return headersLong;
		if (type == Double.class)
			return headersDouble;
		return classAndEncodageMiniToHeader.get(type);
	}

	protected static void init() {
		booleanFalse = new HeaderSimpleType<>(false, boolean.class);
		booleanTrue = new HeaderSimpleType<>(true, boolean.class);
		nullHeader = new HeaderSimpleType<>(void.class, 0);
		new HeaderSimpleType<>((byte) 0, byte.class);
		new HeaderSimpleType<>(byte.class, 1);
		new HeaderSimpleType<>((char) 0, char.class);
		new HeaderSimpleType<>(char.class, 2);
		new HeaderSimpleType<>((short) 0, short.class);
		new HeaderSimpleType<>(short.class, 1);
		new HeaderSimpleType<>(short.class, 2);
		new HeaderSimpleType<>(0, int.class);
		new HeaderSimpleType<>(int.class, 1);
		new HeaderSimpleType<>(int.class, 2);
		new HeaderSimpleType<>(int.class, 3);
		new HeaderSimpleType<>(int.class, 4);
		new HeaderSimpleType<>((long) 0, long.class);
		new HeaderSimpleType<>(long.class, 1);
		new HeaderSimpleType<>(long.class, 2);
		new HeaderSimpleType<>(long.class, 3);
		new HeaderSimpleType<>(long.class, 4);
		new HeaderSimpleType<>(long.class, 5);
		new HeaderSimpleType<>(long.class, 6);
		new HeaderSimpleType<>(long.class, 7);
		new HeaderSimpleType<>(long.class, 8);
		new HeaderSimpleType<>((float) 0.0, float.class);
		new HeaderSimpleType<>(float.class, 4);
		new HeaderSimpleType<>(0.0, double.class);
		new HeaderSimpleType<>(double.class, 8);
		headersInteger = classAndEncodageMiniToHeader.get(Integer.class);
		headersLong = classAndEncodageMiniToHeader.get(Long.class);
		headersDouble = classAndEncodageMiniToHeader.get(Double.class);
	}

	private static void enregistre(final Class<?> type, final int tailleCodageValeur, final HeaderSimpleType<?> header) {
		classAndEncodageMiniToHeader.computeIfAbsent(type, t -> new HeaderSimpleType<?>[9])[tailleCodageValeur] = header;
	}

	private T defautValue;
	/** nature du type (voir AccesChamp) : aiguillage de lecture par switch. */
	private final int nature;
	private final Class<T> simpleType;

	private final int tailleCodageValeur;

	private HeaderSimpleType(final Class<T> simpleType, final int tailleCodageValeur) {
		super();
		this.simpleType = simpleType;
		this.tailleCodageValeur = tailleCodageValeur;
		nature = AccesChamp.nature(simpleType);
		enregistre(simpleType, tailleCodageValeur, this);
		enregistre(TypeExtension.getTypeEnveloppe(simpleType), tailleCodageValeur, this);
	}

	private HeaderSimpleType(final T value, final Class<T> simpleType) {
		super();
		this.simpleType = simpleType;
		this.defautValue = value;
		tailleCodageValeur = 0;
		nature = AccesChamp.nature(simpleType);
		enregistre(simpleType, tailleCodageValeur, this);
		enregistre(TypeExtension.getTypeEnveloppe(simpleType), tailleCodageValeur, this);
	}

	@Override
	protected int categorie() {
		return SIMPLE;
	}

	public Object read(final EntreeBinaire input) throws IOException, UnmarshallExeption {
		if (tailleCodageValeur == 0)
			return defautValue; // 0 ou true ou false ou null
		switch (nature) {
		case AccesChamp.INT:
			return (int) ByteHelper.read(input, tailleCodageValeur);
		case AccesChamp.LONG:
			return ByteHelper.read(input, tailleCodageValeur);
		case AccesChamp.DOUBLE:
			return input.readDouble();
		case AccesChamp.FLOAT:
			return input.readFloat();
		case AccesChamp.CHAR:
			return input.readChar();
		case AccesChamp.SHORT:
			return (short) ByteHelper.read(input, tailleCodageValeur);
		case AccesChamp.BYTE:
			return (byte) ByteHelper.read(input, tailleCodageValeur);
		default:
			return ByteHelper.getObject(simpleType, ByteHelper.read(input, tailleCodageValeur));
		}
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int maxId) {
		return 0;
	}

	@Override
	public void writeValue(final SortieBinaire output, final Object o) throws IOException {
		output.writeByte(headerByte);
		if (tailleCodageValeur == 0)
			return;// rien à ecrire
		if (o instanceof Float)
			output.writeFloat((float) o);
		else if (o instanceof Double)
			output.writeDouble((double) o);
		else if (o instanceof Character)
			output.writeChar((char) o);
		else
			ByteHelper.write(output, ((Number) o).longValue());
	}

}
