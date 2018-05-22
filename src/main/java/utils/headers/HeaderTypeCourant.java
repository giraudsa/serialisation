package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.BiHashMap;

public class HeaderTypeCourant extends Header {

	private static final BiHashMap<Class<?>, Integer, HeaderTypeCourant> typeCourantAndEncodageSmallIdToHeader = new BiHashMap<>();

	public static HeaderTypeCourant getHeader(final Object o, final int smallId) {
		final Class<?> type = o.getClass();
		int encodageSmallId = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(smallId);
		return typeCourantAndEncodageSmallIdToHeader.get(type, encodageSmallId);
	}

	protected static void init() {
		new HeaderTypeCourant(String.class, 1);
		new HeaderTypeCourant(String.class, 2);
		new HeaderTypeCourant(String.class, 3);
		new HeaderTypeCourant(String.class, 4);
		new HeaderTypeCourant(UUID.class, 1);
		new HeaderTypeCourant(UUID.class, 2);
		new HeaderTypeCourant(UUID.class, 3);
		new HeaderTypeCourant(UUID.class, 4);
		new HeaderTypeCourant(Date.class, 1);
		new HeaderTypeCourant(Date.class, 2);
		new HeaderTypeCourant(Date.class, 3);
		new HeaderTypeCourant(Date.class, 4);
	}

	private final int encodageSmallId;
	private final Class<?> typeCourant;

	private HeaderTypeCourant(final Class<?> typeCourant, final int encodageSmallId) {
		super();
		this.typeCourant = typeCourant;
		this.encodageSmallId = encodageSmallId;
		typeCourantAndEncodageSmallIdToHeader.put(typeCourant, encodageSmallId, this);
	}

	public Class<?> getTypeCourant() {
		return typeCourant;
	}

	@Override
	public int readSmallId(final DataInputStream input, final int maxId) throws IOException, UnmarshallExeption {
		final byte[] tmp = new byte[encodageSmallId];
		input.readFully(tmp);
		final BigInteger bi = new BigInteger(tmp);
		return bi.intValue();
	}

	public void write(final DataOutput output, final int smallId) throws IOException {
		output.writeByte(headerByte);
		output.write(BigInteger.valueOf(smallId).toByteArray());
	}

}
