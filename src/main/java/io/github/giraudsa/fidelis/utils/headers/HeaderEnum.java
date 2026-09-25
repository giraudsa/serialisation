package io.github.giraudsa.fidelis.utils.headers;

import java.io.IOException;

import io.github.giraudsa.fidelis.exception.UnmarshallExeption;
import io.github.giraudsa.fidelis.utils.io.EntreeBinaire;
import io.github.giraudsa.fidelis.utils.io.SortieBinaire;

public class HeaderEnum extends Header {

	/** headers indexés par la taille de codage du smallIdType (0 = type devinable). */
	private static final HeaderEnum[] encodageSmallIdToHeaderEnum = new HeaderEnum[3];

	public static HeaderEnum getHeader(final short smallIdType, final boolean typeDevinable) {
		Registre.init();
		final int encodageSmallIdType = typeDevinable ? 0 : ByteHelper.getMinimumEncodage(smallIdType);
		return encodageSmallIdToHeaderEnum[encodageSmallIdType];
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
		encodageSmallIdToHeaderEnum[encodageSmallIdType] = this;
	}

	@Override
	protected int categorie() {
		return ENUM;
	}

	@Override
	public short getSmallIdType(final EntreeBinaire input) throws IOException, UnmarshallExeption {
		return (short) ByteHelper.read(input, encodageSmallIdType);
	}

	@Override
	public boolean isTypeDevinable() {
		return encodageSmallIdType == 0;
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int i) throws IOException, UnmarshallExeption {
		return -1;
	}

	public void write(final SortieBinaire output, final short smallIdType, final Class<?> type, final boolean isDejaVuType)
			throws IOException {
		output.writeByte(headerByte);
		if (encodageSmallIdType > 0) {// type non devinable
			ByteHelper.write(output, smallIdType);
			if (!isDejaVuType)
				output.writeString(type.getName());
		}
	}

}
