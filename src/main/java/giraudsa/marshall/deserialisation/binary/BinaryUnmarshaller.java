package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.EntityManager;
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
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;

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
import java.util.IdentityHashMap;
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
import utils.Constants;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderEnum;
import utils.headers.HeaderSimpleType;
import utils.headers.HeaderTypeCourant;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryUnmarshaller.class);
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
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

	private DataInputStream input;

	private Map<Integer, Object> dicoSmallIdToObject = new HashMap<>();
	private Map<Short, Class<?>> dicoSmallIdToClazz = new HashMap<>();
	private short biggestSmallIdType = 0;
	private Set<Class<?>> listeClasseDejaRencontre = new HashSet<>();
	private Map<Integer, UUID> dicoSmallIdToUUID = new HashMap<>();
	private Map<Integer, Date> dicoSmallIdToDate = new HashMap<>();
	private Map<Integer, String> dicoSmallIdToString = new HashMap<>();
	private boolean deserialisationComplete;
	private Set<Integer> isDejaTotalementDeSerialise = new HashSet<>();

	protected BinaryUnmarshaller(DataInputStream input, EntityManager entity) throws ClassNotFoundException, IOException {
		super(entity);
		this.input = input;
		deserialisationComplete = readBoolean();
	}

	public static <U> U fromBinary(InputStream reader, EntityManager entity) throws UnmarshallExeption{
		try(DataInputStream in = new DataInputStream(new BufferedInputStream(reader))){
			BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(in, entity){};
			return w.parse();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NotImplementedSerializeException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption("Impossible de désérialiser", e);
		}
	}

	public static <U> U fromBinary(InputStream reader) throws UnmarshallExeption{
		return fromBinary(reader, null);
	}
	
	private int getMaxId(){
		return dicoSmallIdToObject.size();
	}

	protected boolean isDeserialisationComplete() {
		return deserialisationComplete;
	}
	
	protected boolean isDejaVu(int smallId){
		return dicoSmallIdToObject.containsKey(smallId);
	}
	
	protected boolean isDejaVuDate(int dateId){
		return dicoSmallIdToDate.containsKey(dateId);
	}
	protected boolean isDejaVuString(int stringId){
		return dicoSmallIdToString.containsKey(stringId);
	}
	protected boolean isDejaVuUuid(int uuidId){
		return dicoSmallIdToUUID.containsKey(uuidId);
	}
	protected boolean isDejaVuClazz(short smallIdType){
		return dicoSmallIdToClazz.containsKey(smallIdType);
	}
	protected boolean isDejaVuClazz(Class<?> type){
		return listeClasseDejaRencontre.contains(type);
	}
	private void stockClass(Class<?> type) {
		stockClass(type, ++biggestSmallIdType);
	}
	private void stockClass(Class<?> type, short smallIdType) {
		listeClasseDejaRencontre.add(type);
		dicoSmallIdToClazz.put(smallIdType, type);
		biggestSmallIdType = smallIdType;
	}
	
	
	protected Object getObject(int smallId){
		return dicoSmallIdToObject.get(smallId);
	}
	
	protected void stockObjectSmallId(int smallId, Object obj){
		dicoSmallIdToObject.put(smallId,obj);
	}
	
	private T parse() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, UnmarshallExeption {
		FakeChamp fc = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION);
		litObject(fc);
		while(!pileAction.isEmpty()){
			((ActionBinary<?>)getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}
	
	protected void litObject(FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption {
		if(fieldInformations.getValueType() == byte.class){
			integreObjectDirectement(readByte()); //seul cas ou le header n'est pas nécessaire.
			return;
		}
		byte headerByte = readByte();
		Header<?> header = Header.getHeader(headerByte);
		if (header instanceof HeaderSimpleType<?>)
			litObjectSimple(header);
		else if (header instanceof HeaderTypeCourant<?>)
			litObjectCourant(header);
		else if (header instanceof HeaderEnum<?>)
			litObjectEnum(fieldInformations, header);
		else
			litObjetComplexe(fieldInformations, header);
	}

	private void litObjectEnum(FieldInformations fi, Header<?> header) throws IOException, UnmarshallExeption, ClassNotFoundException, InstantiationException, IllegalAccessException, NotImplementedSerializeException, InvocationTargetException, NoSuchMethodException {
		Class<?> type = fi.getValueType();
		if(header.isTypeDevinable()){
			if(!isDejaVuClazz(type)){
				stockClass(type);
			}
		}else{			
			short smallIdType = header.getSmallIdType(input);
			if(!isDejaVuClazz(smallIdType))
				stockClass(Class.forName(readUTF()), smallIdType);
			type = dicoSmallIdToClazz.get(smallIdType);
		}
		ActionAbstrait<?> action = getAction(type);
		((ActionBinary<?>)action).set(fi, 0);
		pileAction.push(action);
	}

	private void litObjetComplexe(FieldInformations fieldInformations, Header<?> header)
			throws IOException, UnmarshallExeption, ClassNotFoundException, NotImplementedSerializeException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//il faut trouver le type de l'objet
		Class<?> type = fieldInformations.getValueType();
		int smallId = header.readSmallId(input, getMaxId());
		if(isDejaVu(smallId)){
			if(isDejaTotalementDeSerialise(smallId)){
				integreObjectDirectement(getObject(smallId));
				return;
			}
			type = getObject(smallId).getClass();
		}else if(header.isTypeDevinable()){
			if(!isDejaVuClazz(type)){
				stockClass(type);
			}
		}else{
			short smallIdType = header.getSmallIdType(input);
			if(!isDejaVuClazz(smallIdType))
				stockClass(Class.forName(readUTF()), smallIdType);
			type = dicoSmallIdToClazz.get(smallIdType);
		}
		ActionAbstrait<?> action = getAction(type);
		((ActionBinary<?>)action).set(fieldInformations, smallId);
		pileAction.push(action);
	}

	private void litObjectCourant(Header<?> header)
			throws IOException, UnmarshallExeption, IllegalAccessException, InstantiationException {
		HeaderTypeCourant<?> headerTypeCourant = (HeaderTypeCourant<?>)header;
		Class<?> clazz = headerTypeCourant.getTypeCourant();
		int smallId = headerTypeCourant.readSmallId(input, 0); //le 0 n a pas d importance ici
		if(clazz == Date.class){
			if(!isDejaVuDate(smallId)){
				Date date = new Date(readLong());
				stockDateSmallId(date, smallId);
			}
			integreObjectDirectement(dicoSmallIdToDate.get(smallId));
		}else if(clazz == UUID.class){
			if(!isDejaVuUuid(smallId)){
				UUID id = readUUID();
				stockUuidSmallId(id, smallId);
			}
			integreObjectDirectement(dicoSmallIdToUUID.get(smallId));
		}else if(clazz == String.class){
			if(!isDejaVuString(smallId)){
				String string = readUTF();
				stockStringSmallId(string, smallId);
			}
			integreObjectDirectement(dicoSmallIdToString.get(smallId));
		}
	}

	private void litObjectSimple(Header<?> header)
			throws IllegalAccessException, InstantiationException, IOException, UnmarshallExeption {
		HeaderSimpleType<?> headerSimpleType = (HeaderSimpleType<?>)header;
		integreObjectDirectement(headerSimpleType.read(input));
	}
	
	protected boolean readBoolean() throws IOException {
		return input.readBoolean();
	}
	protected byte readByte() throws IOException {
		return input.readByte();
	}
	protected short readShort() throws IOException {
		return input.readShort();
	}
	protected char readChar() throws IOException {
		return input.readChar();
	}
	protected int readInt() throws IOException {
		return input.readInt();
	}
	protected long readLong() throws IOException {
		return input.readLong();
	}
	protected float readFloat() throws IOException {
		return input.readFloat();
	}
	protected double readDouble() throws IOException {
		return input.readDouble();
	}
	protected String readUTF() throws IOException {
		return input.readUTF();
	}
	private UUID readUUID() throws IOException {
		byte[] tmp = new byte[16];
		int r = input.read(tmp);
		if(r != 16)
			throw new IOException("pas possible de lire un UUID");
		return UUID.nameUUIDFromBytes(tmp);
	}

	@SuppressWarnings("unchecked")
	protected void integreObject(Object obj) throws IllegalAccessException, InstantiationException, UnmarshallExeption {
		pileAction.pop();
		integreObjectDirectement(obj);
	}
	
	@SuppressWarnings("unchecked")
	private void integreObjectDirectement(Object obj) throws IllegalAccessException, InstantiationException, UnmarshallExeption {
		ActionAbstrait<?> action = getActionEnCours();
		if(action == null)
			this.obj = (T) obj;
		else integreObjet(action, null, obj);
	}
	
	private void stockDateSmallId(Date date, int smallId) {
		dicoSmallIdToDate.put(smallId, date);
	}
	private void stockUuidSmallId(UUID id, int smallId) {
		dicoSmallIdToUUID.put(smallId, id);
	}
	private void stockStringSmallId(String string, int smallId) {
		dicoSmallIdToString.put(smallId, string);
	}
	
	protected boolean isDejaTotalementDeSerialise(int smallId) {
		return isDejaTotalementDeSerialise.contains(smallId);
	}

	protected void setDejaTotalementDeSerialise(int smallId) {
		isDejaTotalementDeSerialise.add(smallId);
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

}
