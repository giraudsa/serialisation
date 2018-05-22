package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderEnum extends Header {

	private static final Map<Integer, HeaderEnum> encodageSmallIdToHeaderEnum = new HashMap<>();

	public static HeaderEnum getHeader(final short smallIdType, final boolean typeDevinable) {
		final int encodageSmallIdType = typeDevinable ? 0 : ByteHelper.getMinimumEncodage(smallIdType);
		return encodageSmallIdToHeaderEnum.get(encodageSmallIdType);
	}

	protected static void init() {
		new HeaderEnum(0);// type devinable
		new HeaderEnum(1);
		new HeaderEnum(2);
	}

	private final int encodageSmallIdType;

	public HeaderEnum(final int encodageSmallIdType) {
		super();
		this.encodageSmallIdType = encodageSmallIdType;
		encodageSmallIdToHeaderEnum.put(encodageSmallIdType, this);
	}

	@Override
	public short getSmallIdType(final DataInputStream input) throws IOException, UnmarshallExeption {
		final byte[] tmp = new byte[encodageSmallIdType];
		input.read(tmp);
		final BigInteger bi = new BigInteger(tmp);
		return bi.shortValue();
	}

	@Override
	public boolean isTypeDevinable() {
		return encodageSmallIdType == 0;
	}

	@Override
	public int readSmallId(final DataInputStream input, final int i) throws IOException, UnmarshallExeption {
		return -1;
	}

	public void write(final DataOutput output, final short smallIdType, final Class<?> type, final boolean isDejaVuType)
			throws IOException {
		output.writeByte(headerByte);
		if (encodageSmallIdType > 0) {// type non devinable
			output.write(BigInteger.valueOf(smallIdType).toByteArray());
			if (!isDejaVuType)
				output.writeUTF(type.getName());
		}
	}

}
