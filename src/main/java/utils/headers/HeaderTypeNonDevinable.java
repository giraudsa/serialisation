package utils.headers;

import java.io.IOException;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.io.EntreeBinaire;
import utils.io.SortieBinaire;

/**
 * Premier passage d'un objet dont le type doit être écrit (smallIdType, suivi du nom de la classe à sa première
 * apparition). Le smallId de l'objet est implicite, comme pour {@link HeaderTypeDevinable}.
 */
public class HeaderTypeNonDevinable extends Header {
	/** headers indexés par la taille de codage du smallIdType (1 à 2). */
	private static final HeaderTypeNonDevinable[] encodageSmallIdTypeToHeader = new HeaderTypeNonDevinable[3];

	protected static Header getHeader(final short smallIdType) {
		return encodageSmallIdTypeToHeader[ByteHelper.getMinimumEncodage(smallIdType)];
	}

	protected static void init() {
		new HeaderTypeNonDevinable(1);
		new HeaderTypeNonDevinable(2);
	}

	private final int encodageSmallIdType;

	private HeaderTypeNonDevinable(final int encodageSmallIdType) {
		super();
		this.encodageSmallIdType = encodageSmallIdType;
		encodageSmallIdTypeToHeader[encodageSmallIdType] = this;
	}

	@Override
	public short getSmallIdType(final EntreeBinaire input) throws IOException, UnmarshallExeption {
		return (short) ByteHelper.read(input, encodageSmallIdType);
	}

	@Override
	public boolean isNouveau() {
		return true;
	}

	@Override
	public boolean isTypeDevinable() {
		return false;
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int maxId) {
		return 0;
	}

	@Override
	public void write(final SortieBinaire output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
		ByteHelper.write(output, smallIdType);
		if (!isDejaVuType)
			output.writeString(type.getName());
	}

}
