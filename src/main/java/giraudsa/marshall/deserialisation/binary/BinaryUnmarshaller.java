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
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBoolean;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryByte;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCalendar;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryChar;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCurrency;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDate;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDouble;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryFloat;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryInetAddress;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryLocale;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryShort;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryString;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuffer;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuilder;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUUID;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUri;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUrl;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryVoid;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SmallIdTypeException;
import giraudsa.marshall.exception.UnmarshallExeption;

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
import utils.Constants;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryUnmarshaller.class);
	private static final Set<Class<?>> simpleEnveloppe = new HashSet<>();
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	static {
		dicoTypeToAction.put(Boolean.class, ActionBinaryBoolean.getInstance());
		dicoTypeToAction.put(Byte.class, ActionBinaryByte.getInstance());
		dicoTypeToAction.put(Short.class, ActionBinaryShort.getInstance());
		dicoTypeToAction.put(Integer.class, ActionBinaryInteger.getInstance());
		dicoTypeToAction.put(Long.class, ActionBinaryLong.getInstance());
		dicoTypeToAction.put(Float.class, ActionBinaryFloat.getInstance());
		dicoTypeToAction.put(Double.class, ActionBinaryDouble.getInstance());
		dicoTypeToAction.put(String.class, ActionBinaryString.getInstance());
		dicoTypeToAction.put(Date.class, ActionBinaryDate.getInstance());
		dicoTypeToAction.put(UUID.class, ActionBinaryUUID.getInstance());
		dicoTypeToAction.put(Void.class, ActionBinaryVoid.getInstance());
		dicoTypeToAction.put(Character.class, ActionBinaryChar.getInstance());
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
	private Map<Integer, Class<?>> dicoSmallIdToClazz = new HashMap<>();
	private Map<Class<?>, Integer> dicoClassToSmallId = new HashMap<>();
	private boolean deserialisationComplete;
	private Set<Object> isDejaTotalementDeSerialise = new HashSet<>();


	protected BinaryUnmarshaller(DataInputStream input, EntityManager entity) throws ClassNotFoundException, IOException {
		super(entity);
		this.input = input;
		deserialisationComplete = readBoolean();
	}

	public static <U> U fromBinary(InputStream reader, EntityManager entity) throws UnmarshallExeption{
		try(DataInputStream in = new DataInputStream(reader)){
			BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(in, entity){};
			return w.parse();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NotImplementedSerializeException | SmallIdTypeException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption(e);
		}
	}

	public static <U> U fromBinary(InputStream reader) throws UnmarshallExeption{
		return fromBinary(reader, null);
	}

	boolean isDeserialisationComplete() {
		return deserialisationComplete;
	}
	
	boolean isDejaVu(int smallId){
		return dicoSmallIdToObject.containsKey(smallId);
	}
	
	
	private Class<?> getTypeToUnmarshall(byte header, int smallId, Class<?> typeCandidat) throws ClassNotFoundException, IOException {
		if(TypeExtension.isSimple(typeCandidat))
			return typeCandidat;
		boolean typeDevinable = Constants.Type.isTypeDevinable(header);
		if (typeDevinable){
			if(!dicoClassToSmallId.containsKey(typeCandidat)){
				int smallIdType = dicoClassToSmallId.size() +1;
				dicoClassToSmallId.put(typeCandidat, smallIdType);
				dicoSmallIdToClazz.put(smallIdType, typeCandidat);
			}			
			return typeCandidat;
		}
		return isDejaVu(smallId) ? getObject(smallId).getClass() : readType(header, typeCandidat);
	}
	
	private Class<?> readType(byte header, Class<?> typeProbable) throws IOException, ClassNotFoundException{
		byte b = Constants.Type.getLongueurCodageType(header);
		int smallId = 0;
		switch (b) {
		case Constants.Type.CODAGE_BYTE:
			smallId = readByte() & 0x000000FF;
			break;
		case Constants.Type.CODAGE_SHORT:
			smallId = readShort() & 0x0000FFFF;
			break;
		case Constants.Type.CODAGE_INT:
			smallId = readInt();
			break;
		default :
			LOGGER.error("codage longueur id erroné");
		}
		if(smallId == 0) 
			return typeProbable;
		Class<?> ret = dicoSmallIdToClazz.get(smallId);
		if(ret == null){
			String classeString = readUTF();
			ret = Class.forName(classeString);
			dicoSmallIdToClazz.put(smallId,ret);
			dicoClassToSmallId.put(ret, smallId);
		}
		return ret;
	}
	
	Object getObject(int smallId){
		return dicoSmallIdToObject.get(smallId);
	}
	
	void stockObjectSmallId(int smallId, Object obj){
		dicoSmallIdToObject.put(smallId,obj);
	}
	
	private T parse() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, SmallIdTypeException, UnmarshallExeption {
		FakeChamp fc = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION);
		litObject(readByte(), fc);
		while(!pileAction.isEmpty()){
			((ActionBinary<?>)getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}
	
	void litObject(byte header, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, SmallIdTypeException, UnmarshallExeption {
		Class<?> typeProbable = fieldInformations.getValueType();
		Class<?> typeCandidat = null;
		
		if(header == Constants.IS_NULL)
			typeCandidat = Void.class;
		if(header == Constants.BoolValue.TRUE || header == Constants.BoolValue.FALSE) 
			typeCandidat = Boolean.class;
		if(typeCandidat == null)
			typeCandidat = Constants.Type.getSimpleType(header, typeProbable);
	
		int smallId = getSmallId(header, typeCandidat);
		Class<?> typeADeserialiser = getTypeToUnmarshall(header, smallId, typeCandidat);
		ActionAbstrait<?> action = getAction(typeADeserialiser);
		if(typeADeserialiser == Boolean.class) 
			((ActionBinaryBoolean)action).setBool(header);
		((ActionBinary<?>)action).set(fieldInformations, smallId);
		pileAction.push(action);
	}

	
	static{
		simpleEnveloppe.add(Boolean.class);
		simpleEnveloppe.add(Byte.class);
		simpleEnveloppe.add(Character.class);
		simpleEnveloppe.add(Short.class);
		simpleEnveloppe.add(Integer.class);
		simpleEnveloppe.add(Long.class);
		simpleEnveloppe.add(Void.class);
	}
	private int getSmallId(byte header, Class<?> t) throws IOException, SmallIdTypeException {
		if(simpleEnveloppe.contains(t))
			return 0;
		int smallId;
		Byte smallIdType = Constants.SmallIdType.getSmallId(header);
		switch (smallIdType){
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_BYTE:
			smallId = readByte() & 0x000000FF;
			break;
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_SHORT:
			smallId = readShort() & 0x0000FFFF;
			break; 
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_INT:
			smallId = readInt();
			break;
		default :
			LOGGER.error("bad small id type");
			throw new SmallIdTypeException();
		}
		return smallId;	
	}
	
	boolean readBoolean() throws IOException {
		return input.readBoolean();
	}
	byte readByte() throws IOException {
		return input.readByte();
	}
	short readShort() throws IOException {
		return input.readShort();
	}
	char readChar() throws IOException {
		return input.readChar();
	}
	int readInt() throws IOException {
		return input.readInt();
	}
	long readLong() throws IOException {
		return input.readLong();
	}
	float readFloat() throws IOException {
		return input.readFloat();
	}
	double readDouble() throws IOException {
		return input.readDouble();
	}
	String readUTF() throws IOException {
		return input.readUTF();
	}

	@SuppressWarnings("unchecked")
	protected void integreObject(Object obj) throws IllegalAccessException, InstantiationException {
		pileAction.pop();
		ActionAbstrait<?> action = getActionEnCours();
		if(action == null)
			this.obj = (T) obj;
		else integreObjet(action, null, obj);
	}
	
	boolean isDejaTotalementDeSerialise(Object o) {
		return isDejaTotalementDeSerialise.contains(o);
	}

	void setDejaTotalementDeSerialise(Object o) {
		isDejaTotalementDeSerialise.add(o);
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

}
