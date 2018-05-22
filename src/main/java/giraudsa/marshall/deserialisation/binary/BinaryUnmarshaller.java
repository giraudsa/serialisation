package giraudsa.marshall.deserialisation.binary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryArray;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryCollection;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryDictionary;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryObject;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicBoolean;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicIntegerArray;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicLong;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicLongArray;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBigDecimal;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBigInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBitSet;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCalendar;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCurrency;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDate;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryInetAddress;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryLocale;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuffer;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuilder;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUri;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUrl;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.Constants;
import utils.EntityManager;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderEnum;
import utils.headers.HeaderSimpleType;
import utils.headers.HeaderTypeCourant;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections
			.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryUnmarshaller.class);
	static {
		dicoTypeToAction.put(Constants.dateType, ActionBinaryDate.getInstance());
		dicoTypeToAction.put(Constants.collectionType, ActionBinaryCollection.getInstance());
		dicoTypeToAction.put(Constants.arrayType, ActionBinaryArray.getInstance());
		dicoTypeToAction.put(Constants.dictionaryType, ActionBinaryDictionary.getInstance());
		dicoTypeToAction.put(Constants.objectType, ActionBinaryObject.getInstance());
		dicoTypeToAction.put(Constants.enumType, ActionBinaryEnum.getInstance());
		dicoTypeToAction.put(AtomicBoolean.class, ActionBinaryAtomicBoolean.getInstance());
		dicoTypeToAction.put(AtomicInteger.class, ActionBinaryAtomicInteger.getInstance());
		dicoTypeToAction.put(AtomicLong.class, ActionBinaryAtomicLong.getInstance());
		dicoTypeToAction.put(AtomicIntegerArray.class, ActionBinaryAtomicIntegerArray.getInstance());
		dicoTypeToAction.put(AtomicLongArray.class, ActionBinaryAtomicLongArray.getInstance());
		dicoTypeToAction.put(BigDecimal.class, ActionBinaryBigDecimal.getInstance());
		dicoTypeToAction.put(BigInteger.class, ActionBinaryBigInteger.getInstance());
		dicoTypeToAction.put(URI.class, ActionBinaryUri.getInstance());
		dicoTypeToAction.put(URL.class, ActionBinaryUrl.getInstance());
		dicoTypeToAction.put(Currency.class, ActionBinaryCurrency.getInstance());
		dicoTypeToAction.put(Locale.class, ActionBinaryLocale.getInstance());
		dicoTypeToAction.put(InetAddress.class, ActionBinaryInetAddress.getInstance());
		dicoTypeToAction.put(BitSet.class, ActionBinaryBitSet.getInstance());
		dicoTypeToAction.put(Calendar.class, ActionBinaryCalendar.getInstance());
		dicoTypeToAction.put(StringBuilder.class, ActionBinaryStringBuilder.getInstance());
		dicoTypeToAction.put(StringBuffer.class, ActionBinaryStringBuffer.getInstance());
	}

	public static <U> U fromBinary(final InputStream reader) throws UnmarshallExeption {
		return fromBinary(reader, null);
	}

	/**
	 * Désérialise un objet à partir d'un InputStream L'entity Manager permet
	 * 
	 * @param reader
	 * @param entity
	 * @return
	 * @throws UnmarshallExeption
	 */
	public static <U> U fromBinary(final InputStream reader, final EntityManager entity) throws UnmarshallExeption {
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(reader))) {
			final BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(in, entity) {
			};
			return w.parse();
		} catch (UnmarshallExeption | FabriqueInstantiationException | IOException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException | ClassNotFoundException
				| NotImplementedSerializeException | InstanciationException | EntityManagerImplementationException
				| SetValueException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption("Impossible de désérialiser", e);
		}
	}

	private short biggestSmallIdType = 0;
	private final Map<Short, Class<?>> dicoSmallIdToClazz = new HashMap<>();
	private final Map<Integer, Date> dicoSmallIdToDate = new HashMap<>();
	private final Map<Integer, Object> dicoSmallIdToObject = new HashMap<>();
	private final Map<Integer, String> dicoSmallIdToString = new HashMap<>();
	private final Map<Integer, UUID> dicoSmallIdToUUID = new HashMap<>();
	private final DataInputStream input;
	private final Set<Integer> isDejaTotalementDeSerialise = new HashSet<>();
	private final Set<Class<?>> listeClasseDejaRencontre = new HashSet<>();

	protected int profondeur = 0;

	private final StrategieDeSerialisation strategie;

	protected BinaryUnmarshaller(final DataInputStream input, final EntityManager entity)
			throws FabriqueInstantiationException, IOException, UnmarshallExeption {
		super(entity);
		this.input = input;
		strategie = readStrategie();
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

	private int getMaxId() {
		return dicoSmallIdToObject.size();
	}

	protected Object getObject(final int smallId) {
		return dicoSmallIdToObject.get(smallId);
	}

	int getProfondeur() {
		return profondeur;
	}

	StrategieDeSerialisation getStrategie() {
		return strategie;
	}

	protected void integreObject(final Object obj) throws IllegalAccessException, EntityManagerImplementationException,
			InstanciationException, SetValueException {
		pileAction.pop();
		integreObjectDirectement(obj);
	}

	@SuppressWarnings("unchecked")
	private void integreObjectDirectement(final Object obj) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		final ActionAbstrait<?> action = getActionEnCours();
		if (action == null)
			this.obj = (T) obj;
		else
			integreObjet(action, null, obj);
	}

	protected boolean isDejaTotalementDeSerialise(final int smallId) {
		return isDejaTotalementDeSerialise.contains(smallId);
	}

	protected boolean isDejaVu(final int smallId) {
		return dicoSmallIdToObject.containsKey(smallId);
	}

	protected boolean isDejaVuClazz(final Class<?> type) {
		return listeClasseDejaRencontre.contains(type);
	}

	protected boolean isDejaVuClazz(final short smallIdType) {
		return dicoSmallIdToClazz.containsKey(smallIdType);
	}

	protected boolean isDejaVuDate(final int dateId) {
		return dicoSmallIdToDate.containsKey(dateId);
	}

	protected boolean isDejaVuString(final int stringId) {
		return dicoSmallIdToString.containsKey(stringId);
	}

	protected boolean isDejaVuUuid(final int uuidId) {
		return dicoSmallIdToUUID.containsKey(uuidId);
	}

	protected void litObject(final FieldInformations fieldInformations)
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		if (fieldInformations.getValueType() == byte.class) {
			integreObjectDirectement(readByte()); // seul cas ou le header n'est pas nécessaire.
			return;
		}
		final byte headerByte = readByte();
		final Header<?> header = Header.getHeader(headerByte);
		if (header instanceof HeaderSimpleType<?>)
			litObjectSimple(header);
		else if (header instanceof HeaderTypeCourant<?>)
			litObjectCourant(header);
		else if (header instanceof HeaderEnum<?>)
			litObjectEnum(fieldInformations, header);
		else
			litObjetComplexe(fieldInformations, header);
	}

	private void litObjectCourant(final Header<?> header) throws IOException, UnmarshallExeption,
			IllegalAccessException, EntityManagerImplementationException, InstanciationException, SetValueException {
		final HeaderTypeCourant<?> headerTypeCourant = (HeaderTypeCourant<?>) header;
		final Class<?> clazz = headerTypeCourant.getTypeCourant();
		final int smallId = headerTypeCourant.readSmallId(input, 0); // le 0 n a pas d importance ici
		if (clazz == Date.class) {
			if (!isDejaVuDate(smallId)) {
				final Date date = new Date(readLong());
				stockDateSmallId(date, smallId);
			}
			integreObjectDirectement(dicoSmallIdToDate.get(smallId));
		} else if (clazz == UUID.class) {
			if (!isDejaVuUuid(smallId)) {
				final UUID id = readUUID();
				stockUuidSmallId(id, smallId);
			}
			integreObjectDirectement(dicoSmallIdToUUID.get(smallId));
		} else if (clazz == String.class) {
			if (!isDejaVuString(smallId)) {
				final String string = readUTF();
				stockStringSmallId(string, smallId);
			}
			integreObjectDirectement(dicoSmallIdToString.get(smallId));
		}
	}

	private void litObjectEnum(final FieldInformations fi, final Header<?> header)
			throws NotImplementedSerializeException, ClassNotFoundException, IOException, UnmarshallExeption,
			InstanciationException {
		Class<?> type = fi.getValueType();
		if (header.isTypeDevinable()) {
			if (!isDejaVuClazz(type))
				stockClass(type);
		} else {
			final short smallIdType = header.getSmallIdType(input);
			if (!isDejaVuClazz(smallIdType))
				stockClass(Class.forName(readUTF()), smallIdType);
			type = dicoSmallIdToClazz.get(smallIdType);
		}
		final ActionAbstrait<?> action = getAction(type);
		((ActionBinary<?>) action).set(fi, 0);
		pileAction.push(action);
	}

	private void litObjectSimple(final Header<?> header) throws IOException, UnmarshallExeption, IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		final HeaderSimpleType<?> headerSimpleType = (HeaderSimpleType<?>) header;
		integreObjectDirectement(headerSimpleType.read(input));
	}

	private void litObjetComplexe(final FieldInformations fieldInformations, final Header<?> header)
			throws NotImplementedSerializeException, ClassNotFoundException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		// il faut trouver le type de l'objet
		Class<?> type = fieldInformations.getValueType();
		final int smallId = header.readSmallId(input, getMaxId());
		if (isDejaVu(smallId)) {
			if (isDejaTotalementDeSerialise(smallId)) {
				integreObjectDirectement(getObject(smallId));
				return;
			}
			type = getObject(smallId).getClass();
		} else if (header.isTypeDevinable()) {
			if (!isDejaVuClazz(type))
				stockClass(type);
		} else {
			final short smallIdType = header.getSmallIdType(input);
			if (!isDejaVuClazz(smallIdType))
				stockClass(Class.forName(readUTF()), smallIdType);
			type = dicoSmallIdToClazz.get(smallIdType);
		}
		final ActionAbstrait<?> action = getAction(type);
		((ActionBinary<?>) action).set(fieldInformations, smallId);
		pileAction.push(action);
	}

	private T parse() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption,
			InstanciationException, EntityManagerImplementationException, SetValueException {
		final FakeChamp fc = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
		litObject(fc);
		while (!pileAction.isEmpty()) {
			final ActionBinary<?> actionEnCours = (ActionBinary<?>) getActionEnCours();
			profondeur = actionEnCours.getProfondeur();
			((ActionBinary<?>) getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}

	protected boolean readBoolean() throws IOException {
		return input.readBoolean();
	}

	protected byte readByte() throws IOException {
		return input.readByte();
	}

	protected char readChar() throws IOException {
		return input.readChar();
	}

	protected double readDouble() throws IOException {
		return input.readDouble();
	}

	protected float readFloat() throws IOException {
		return input.readFloat();
	}

	protected int readInt() throws IOException {
		return input.readInt();
	}

	protected long readLong() throws IOException {
		return input.readLong();
	}

	protected short readShort() throws IOException {
		return input.readShort();
	}

	private StrategieDeSerialisation readStrategie() throws IOException, UnmarshallExeption {
		final byte firstByte = readByte();
		final StrategieDeSerialisation strat = Constants.getStrategie(firstByte);
		if (strat != null)
			return strat;
		return fromBinary(input); // TODO : verifier que le input n'est pas fermé apres lecture de la strategie
	}

	protected String readUTF() throws IOException {
		return input.readUTF();
	}

	private UUID readUUID() throws IOException {
		final byte[] tmp = new byte[16];
		final int r = input.read(tmp);
		if (r != 16)
			throw new IOException("pas possible de lire un UUID");
		return UUID.nameUUIDFromBytes(tmp);
	}

	protected void setDejaTotalementDeSerialise(final int smallId) {
		isDejaTotalementDeSerialise.add(smallId);
	}

	private void stockClass(final Class<?> type) {
		stockClass(type, ++biggestSmallIdType);
	}

	private void stockClass(final Class<?> type, final short smallIdType) {
		listeClasseDejaRencontre.add(type);
		dicoSmallIdToClazz.put(smallIdType, type);
		biggestSmallIdType = smallIdType;
	}

	private void stockDateSmallId(final Date date, final int smallId) {
		dicoSmallIdToDate.put(smallId, date);
	}

	protected void stockObjectSmallId(final int smallId, final Object obj) {
		dicoSmallIdToObject.put(smallId, obj);
	}

	private void stockStringSmallId(final String string, final int smallId) {
		dicoSmallIdToString.put(smallId, string);
	}

	private void stockUuidSmallId(final UUID id, final int smallId) {
		dicoSmallIdToUUID.put(smallId, id);
	}

}
