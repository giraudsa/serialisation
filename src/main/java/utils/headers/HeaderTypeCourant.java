package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderTypeCourant extends Header {

	/** headers indexés par la taille de codage du smallId (1 à 4). */
	private static final HeaderTypeCourant[] headersDate = new HeaderTypeCourant[5];
	private static final HeaderTypeCourant[] headersString = new HeaderTypeCourant[5];
	private static final HeaderTypeCourant[] headersUuid = new HeaderTypeCourant[5];

	public static HeaderTypeCourant getHeader(final Object o, final int smallId) {
		Registre.init();
		return headers(o.getClass())[ByteHelper.getMinimumEncodage(smallId)];
	}

	private static HeaderTypeCourant[] headers(final Class<?> type) {
		if (type == String.class)
			return headersString;
		if (type == UUID.class)
			return headersUuid;
		if (type == Date.class)
			return headersDate;
		throw new IllegalArgumentException("pas de header courant pour le type " + type);
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
		headers(typeCourant)[encodageSmallId] = this;
	}

	public Class<?> getTypeCourant() {
		return typeCourant;
	}

	@Override
	public int readSmallId(final DataInputStream input, final int maxId) throws IOException, UnmarshallExeption {
		return (int) ByteHelper.read(input, encodageSmallId);
	}

	public void write(final DataOutput output, final int smallId) throws IOException {
		output.writeByte(headerByte);
		ByteHelper.write(output, smallId);
	}

}
