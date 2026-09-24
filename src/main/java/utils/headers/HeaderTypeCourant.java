package utils.headers;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.io.EntreeBinaire;
import utils.io.SortieBinaire;

public class HeaderTypeCourant extends Header {

	/**
	 * headers indexés par la taille de codage du smallId (1 à 4) pour une référence, 0 pour une première apparition
	 * (smallId implicite, attribué séquentiellement à la lecture).
	 */
	private static final HeaderTypeCourant[] headersDate = new HeaderTypeCourant[5];
	private static final HeaderTypeCourant[] headersString = new HeaderTypeCourant[5];
	private static final HeaderTypeCourant[] headersUuid = new HeaderTypeCourant[5];

	public static HeaderTypeCourant getHeader(final Object o, final int smallId, final boolean isDejaVu) {
		Registre.init();
		return headers(o.getClass())[isDejaVu ? ByteHelper.getMinimumEncodage(smallId) : 0];
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
		new HeaderTypeCourant(String.class, 0);
		new HeaderTypeCourant(String.class, 1);
		new HeaderTypeCourant(String.class, 2);
		new HeaderTypeCourant(String.class, 3);
		new HeaderTypeCourant(String.class, 4);
		new HeaderTypeCourant(UUID.class, 0);
		new HeaderTypeCourant(UUID.class, 1);
		new HeaderTypeCourant(UUID.class, 2);
		new HeaderTypeCourant(UUID.class, 3);
		new HeaderTypeCourant(UUID.class, 4);
		new HeaderTypeCourant(Date.class, 0);
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

	@Override
	public boolean isNouveau() {
		return encodageSmallId == 0;
	}

	@Override
	protected int categorie() {
		return COURANT;
	}

	public Class<?> getTypeCourant() {
		return typeCourant;
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int maxId) throws IOException, UnmarshallExeption {
		return (int) ByteHelper.read(input, encodageSmallId);
	}

	public void write(final SortieBinaire output, final int smallId) throws IOException {
		output.writeByte(headerByte);
		if (encodageSmallId > 0)
			ByteHelper.write(output, smallId);
	}

}
