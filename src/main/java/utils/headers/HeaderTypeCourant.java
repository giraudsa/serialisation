package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.BiHashMap;

public class HeaderTypeCourant<T> extends Header<T> {

	private static final BiHashMap<Class<?>, Integer, HeaderTypeCourant<?>> typeCourantAndEncodageSmallIdToHeader = new BiHashMap<>();
	protected static void init(){
		new HeaderTypeCourant<>(String.class, 1);
		new HeaderTypeCourant<>(String.class, 2);
		new HeaderTypeCourant<>(String.class, 3);
		new HeaderTypeCourant<>(String.class, 4);
		new HeaderTypeCourant<>(UUID.class, 1);
		new HeaderTypeCourant<>(UUID.class, 2);
		new HeaderTypeCourant<>(UUID.class, 3);
		new HeaderTypeCourant<>(UUID.class, 4);
		new HeaderTypeCourant<>(Date.class, 1);
		new HeaderTypeCourant<>(Date.class, 2);
		new HeaderTypeCourant<>(Date.class, 3);
		new HeaderTypeCourant<>(Date.class, 4);
	}
	
	private Class<?> typeCourant;
	private int encodageSmallId;
	private HeaderTypeCourant(Class<?> typeCourant, int encodageSmallId) {
		super();
		this.typeCourant = typeCourant;
		this.encodageSmallId = encodageSmallId;
		typeCourantAndEncodageSmallIdToHeader.put(typeCourant, encodageSmallId, this);
	}
	
	public static  HeaderTypeCourant<?> getHeader(Object o, int smallId){
		Class<?> type = o.getClass();
		int encodageSmallId = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(smallId);
		return typeCourantAndEncodageSmallIdToHeader.get(type, encodageSmallId);
	}
	
	public void write(DataOutput output, int smallId) throws IOException {
		output.writeByte(headerByte);
		output.write(BigInteger.valueOf(smallId).toByteArray());
	}

	public Class<?> getTypeCourant() {
		return typeCourant;
	}

	@Override
	public int readSmallId(DataInputStream input, int maxId) throws IOException, UnmarshallExeption {
		byte[] tmp = new byte[encodageSmallId];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return bi.intValue();
	}
	
}
