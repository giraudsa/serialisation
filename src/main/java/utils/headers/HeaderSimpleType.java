package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.BiHashMap;
import utils.TypeExtension;

public class HeaderSimpleType<T> extends Header<T> {
	private static final BiHashMap<Class<?>, Integer, Header<?>> classAndEncodageMiniToHeader = new BiHashMap<>();
	private static final HeaderSimpleType<Boolean> booleanTrue = new HeaderSimpleType<>(true, boolean.class);
	private static final HeaderSimpleType<Boolean> booleanFalse = new HeaderSimpleType<>(false, boolean.class);
	private static final HeaderSimpleType<Void> nullHeader = new HeaderSimpleType<>(void.class, 0);
	protected static void init(){
		new HeaderSimpleType<>((byte)0, byte.class);
		new HeaderSimpleType<>(byte.class, 1);
		new HeaderSimpleType<>((char)0, char.class);
		new HeaderSimpleType<>(char.class, 2);
		new HeaderSimpleType<>((short)0, short.class);
		new HeaderSimpleType<>(short.class, 1);
		new HeaderSimpleType<>(short.class, 2);
		new HeaderSimpleType<>((int)0, int.class);
		new HeaderSimpleType<>(int.class, 1);
		new HeaderSimpleType<>(int.class, 2);
		new HeaderSimpleType<>(int.class, 3);
		new HeaderSimpleType<>(int.class, 4);
		new HeaderSimpleType<>((long)0, long.class);
		new HeaderSimpleType<>(long.class, 1);
		new HeaderSimpleType<>(long.class, 2);
		new HeaderSimpleType<>(long.class, 3);
		new HeaderSimpleType<>(long.class, 4);
		new HeaderSimpleType<>(long.class, 5);
		new HeaderSimpleType<>(long.class, 6);
		new HeaderSimpleType<>(long.class, 7);
		new HeaderSimpleType<>(long.class, 8);
		new HeaderSimpleType<>((float)0.0, float.class);
		new HeaderSimpleType<>(float.class, 4);
		new HeaderSimpleType<>((double)0.0, double.class);
		new HeaderSimpleType<>(double.class, 8);
	}
	private Class<T> simpleType;
	private T defautValue;
	private int tailleCodageValeur;
	
	private HeaderSimpleType(Class<T> simpleType, int tailleCodageValeur) {
		super();
		this.simpleType = simpleType;
		this.tailleCodageValeur = tailleCodageValeur;
		classAndEncodageMiniToHeader.put(simpleType, tailleCodageValeur, this);
		classAndEncodageMiniToHeader.put(TypeExtension.getTypeEnveloppe(simpleType), tailleCodageValeur, this);
	}
	
	private HeaderSimpleType(T value, Class<T> simpleType) {
		super();
		this.simpleType = simpleType;
		this.defautValue = value;
		tailleCodageValeur = 0;
		classAndEncodageMiniToHeader.put(simpleType, tailleCodageValeur, this);
		classAndEncodageMiniToHeader.put(TypeExtension.getTypeEnveloppe(simpleType), tailleCodageValeur, this);
	}
	
	@SuppressWarnings("unchecked")
	public static <U> Header<U> getHeader(U o) throws MarshallExeption{
		if(o == null)
			return (Header<U>) nullHeader;
		if(Boolean.class.isInstance(o))
			return (Header<U>) ((boolean)(Object)o ? booleanTrue : booleanFalse);
		Class<U> classeO = (Class<U>) o.getClass();
		int encodage = ByteHelper.getMinimumEncodage((Number)o);
		return (Header<U>) classAndEncodageMiniToHeader.get(TypeExtension.getTypeEnveloppe(classeO), encodage);
	}
	
	@Override
	public void writeValue(DataOutput output, Object o) throws IOException {
		output.writeByte(headerByte);
		if(tailleCodageValeur == 0)
			return;//rien à ecrire
		if(Float.class.isInstance(o))
			output.writeFloat((float)o);
		else if(Double.class.isInstance(o))
			output.writeDouble((double)o);
		else{
			output.write(BigInteger.valueOf(((Number)o).longValue()).toByteArray());
		}
	}

	public Object read(DataInputStream input) throws IOException, UnmarshallExeption {
		if(tailleCodageValeur == 0)
			return defautValue; //0 ou true ou false ou null
		if(simpleType == float.class)
			return input.readFloat();
		if(simpleType == double.class)
			return input.readDouble();
		byte[] tmp = new byte[tailleCodageValeur];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return ByteHelper.getObject(simpleType, bi);
	}

	@Override
	public int readSmallId(DataInputStream input, int maxId) {
		return 0;
	}

}
