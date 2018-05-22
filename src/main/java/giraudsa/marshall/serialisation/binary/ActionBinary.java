package giraudsa.marshall.serialisation.binary;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;
import utils.headers.Header;

public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	protected class ComportementDiminueProfondeur extends Comportement {

		@Override
		protected void evalue(final Marshaller marshaller) {
			diminueProfondeur(marshaller);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinary.class);

	protected ActionBinary() {
		super();
	}

	@Override
	protected <V> boolean aTraiter(final Marshaller marshaller, final V value, final FieldInformations f) {
		return true;
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformation,
			boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;

	protected BinaryMarshaller getBinaryMarshaller(final Marshaller marshaller) {
		return (BinaryMarshaller) marshaller;
	}

	protected DataOutput getOutput(final Marshaller marshaller) {
		return getBinaryMarshaller(marshaller).output;
	}

	protected int getSmallIdAndStockObj(final Marshaller marshaller, final Object o) {
		return getBinaryMarshaller(marshaller).getSmallIdAndStockObj(o);
	}

	protected int getSmallIdDateAndStockDate(final Marshaller marshaller, final Date date) {
		return getBinaryMarshaller(marshaller).getSmallIdAndStockDate(date);
	}

	protected int getSmallIdStringAndStockString(final Marshaller marshaller, final String string) {
		return getBinaryMarshaller(marshaller).getSmallIdAndStockString(string);
	}

	protected short getSmallIdTypeAndStockType(final Marshaller marshaller, final Class<?> typeObj) {
		return getBinaryMarshaller(marshaller).getSmallIdTypeAndStockType(typeObj);
	}

	protected int getSmallIdUUIDAndStockUUID(final Marshaller marshaller, final UUID id) {
		return getBinaryMarshaller(marshaller).getSmallIdAndStockUUID(id);
	}

	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		if (object == null)
			return void.class;
		return object.getClass();
	}

	@Override
	protected <U> boolean isDejaVu(final Marshaller marshaller, final U objet) {
		return getBinaryMarshaller(marshaller).isSmallIdDefined(objet);
	}

	protected boolean isDejaVuDate(final Marshaller marshaller, final Date date) {
		return getBinaryMarshaller(marshaller).isDejaVuDate(date);
	}

	protected boolean isDejaVuString(final Marshaller marshaller, final String string) {
		return getBinaryMarshaller(marshaller).isDejaVuString(string);
	}

	protected boolean isDejaVuType(final Marshaller marshaller, final Class<?> typeObj) {
		return getBinaryMarshaller(marshaller).isDejaVuType(typeObj);
	}

	protected boolean isDejaVuUUID(final Marshaller marshaller, final UUID id) {
		return getBinaryMarshaller(marshaller).isDejaVuUUID(id);
	}

	@Override
	protected boolean isTypeDevinable(final Marshaller marshaller, final Object value,
			final FieldInformations fieldInformations) {
		return fieldInformations.isTypeDevinable(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformation) throws MarshallExeption {
		try {
			final boolean isDejaVu = writeHeaders(marshaller, (T) objetASerialiser, fieldInformation);
			augmenteProdondeur(marshaller);
			pushComportement(marshaller, new ComportementDiminueProfondeur());
			ecritValeur(marshaller, (T) objetASerialiser, fieldInformation, isDejaVu);
		} catch (MarshallExeption | IOException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("problème à la sérialisation de l'objet " + objetASerialiser.toString(), e);
			throw new MarshallExeption(e);
		}

	}

	protected void writeBoolean(final Marshaller marshaller, final boolean v) throws IOException {
		getBinaryMarshaller(marshaller).writeBoolean(v);
	}

	protected void writeByte(final Marshaller marshaller, final byte v) throws IOException {
		getBinaryMarshaller(marshaller).writeByte(v);
	}

	protected void writeByteArray(final Marshaller marshaller, final byte[] v) throws IOException {
		getBinaryMarshaller(marshaller).writeByteArray(v);
	}

	protected void writeChar(final Marshaller marshaller, final char v) throws IOException {
		getBinaryMarshaller(marshaller).writeChar(v);
	}

	protected void writeDouble(final Marshaller marshaller, final double v) throws IOException {
		getBinaryMarshaller(marshaller).writeDouble(v);
	}

	protected void writeFloat(final Marshaller marshaller, final float v) throws IOException {
		getBinaryMarshaller(marshaller).writeFloat(v);
	}

	protected boolean writeHeaders(final Marshaller marshaller, final T objetASerialiser,
			final FieldInformations fieldInformations) throws MarshallExeption, IOException {
		final Class<?> typeObj = getTypeObjProblemeHibernate(objetASerialiser);
		final boolean isDejaVu = isDejaVu(marshaller, objetASerialiser);
		final boolean isTypeDevinable = isTypeDevinable(marshaller, objetASerialiser, fieldInformations);
		final boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		final int smallId = getSmallIdAndStockObj(marshaller, objetASerialiser);
		final short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		final Header<?> header = Header.getHeader(isDejaVu, isTypeDevinable, smallId, smallIdType);
		header.write(getOutput(marshaller), smallId, smallIdType, isDejaVuType, typeObj);
		return isDejaVu;
	}

	protected void writeInt(final Marshaller marshaller, final int v) throws IOException {
		getBinaryMarshaller(marshaller).writeInt(v);
	}

	protected void writeLong(final Marshaller marshaller, final long v) throws IOException {
		getBinaryMarshaller(marshaller).writeLong(v);
	}

	protected void writeNull(final Marshaller marshaller) throws IOException {
		writeByte(marshaller, (byte) 0);
	}

	protected void writeShort(final Marshaller marshaller, final short v) throws IOException {
		getBinaryMarshaller(marshaller).writeShort(v);
	}

	protected void writeUTF(final Marshaller marshaller, final String s) throws IOException {
		getBinaryMarshaller(marshaller).writeUTF(s);
	}
}
