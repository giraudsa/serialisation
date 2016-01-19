package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryArrayType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryAtomicIntegerArray;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryAtomicLongArray;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryBitSet;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCalendar;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCollectionType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCurrency;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDate;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDictionaryType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryInetAddress;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryObject;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryString;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryStringBuffer;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryStringBuilder;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUrl;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUri;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUUID;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicBoolean;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBigDecimal;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBigInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBoolean;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryByte;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryChar;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryDouble;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryFloat;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryShort;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinarySimple;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryVoid;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Constants;
import utils.champ.FakeChamp;

public class BinaryMarshaller extends Marshaller{
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryMarshaller.class);
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	static {
		dicoTypeToAction.put(void.class, new ActionBinaryVoid());
		dicoTypeToAction.put(Boolean.class, new ActionBinaryBoolean());
		dicoTypeToAction.put(Integer.class, new ActionBinaryInteger());
		dicoTypeToAction.put(Byte.class, new ActionBinaryByte());
		dicoTypeToAction.put(Float.class, new ActionBinaryFloat());
		dicoTypeToAction.put(Double.class, new ActionBinaryDouble());
		dicoTypeToAction.put(Long.class, new ActionBinaryLong());
		dicoTypeToAction.put(Short.class, new ActionBinaryShort());
		dicoTypeToAction.put(Character.class, new ActionBinaryChar());
		dicoTypeToAction.put(UUID.class, new ActionBinaryUUID());
		dicoTypeToAction.put(String.class, new ActionBinaryString());
		dicoTypeToAction.put(Date.class, new ActionBinaryDate());
		dicoTypeToAction.put(Enum.class, new ActionBinaryEnum());
		dicoTypeToAction.put(Collection.class, new ActionBinaryCollectionType());
		dicoTypeToAction.put(Array.class, new ActionBinaryArrayType());
		dicoTypeToAction.put(Map.class, new ActionBinaryDictionaryType());
		dicoTypeToAction.put(Object.class, new ActionBinaryObject());
		
		dicoTypeToAction.put(AtomicBoolean.class, new ActionBinaryAtomicBoolean());
		dicoTypeToAction.put(AtomicInteger.class, new ActionBinaryAtomicInteger());
		dicoTypeToAction.put(AtomicLong.class, new ActionBinaryAtomicLong());
		dicoTypeToAction.put(AtomicIntegerArray.class, new ActionBinaryAtomicIntegerArray());
		dicoTypeToAction.put(AtomicLongArray.class, new ActionBinaryAtomicLongArray());
		dicoTypeToAction.put(BigDecimal.class, new ActionBinaryBigDecimal());
		dicoTypeToAction.put(BigInteger.class, new ActionBinaryBigInteger());
		dicoTypeToAction.put(URI.class, new ActionBinaryUri());
		dicoTypeToAction.put(URL.class, new ActionBinaryUrl());
		dicoTypeToAction.put(Currency.class, new ActionBinaryCurrency());
		dicoTypeToAction.put(Locale.class, new ActionBinarySimple<Locale>());
		dicoTypeToAction.put(InetAddress.class, new ActionBinaryInetAddress());
		dicoTypeToAction.put(BitSet.class, new ActionBinaryBitSet());
		dicoTypeToAction.put(Calendar.class, new ActionBinaryCalendar());
		dicoTypeToAction.put(StringBuilder.class, new ActionBinaryStringBuilder());
		dicoTypeToAction.put(StringBuffer.class, new ActionBinaryStringBuffer());
	}
	private DataOutputStream output;
	private Map<Object, Integer> smallIds = new HashMap<>();
	private Map<Class<?>, Integer> dejaVuType = new HashMap<>();
	private int compteur = 0;
	private int compteurType = 1;	
	private BinaryMarshaller(DataOutputStream  output, boolean isCompleteSerialisation) throws IOException {
		super(isCompleteSerialisation);
		this.output = output;
		output.writeBoolean(isCompleteSerialisation);
	}

	/////METHODES STATICS PUBLICS
	public static <U> void toBinary(U obj, OutputStream  output) throws MarshallExeption{
		try(DataOutputStream stream = new DataOutputStream(output)){
			BinaryMarshaller v = new BinaryMarshaller(stream, false);
			v.marshall(obj);
			stream.flush();
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("Problème lors de la sérialisation binaire", e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> void toCompleteBinary(U obj, OutputStream  output) throws MarshallExeption{
		try(DataOutputStream stream = new DataOutputStream(output)){
			BinaryMarshaller v = new BinaryMarshaller(stream,true);
			v.marshall(obj);
			stream.flush();
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("Problème lors de la sérialisation binaire complète", e);
			throw new MarshallExeption(e);
		}
	}
	
	/////METHODES 
	private <T> void marshall(T obj) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption{
		FakeChamp fieldsInfo = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION);
		marshallSpecialise(obj, fieldsInfo);
		while(!aFaire.isEmpty()){
			deserialisePile();
		}
	}

	private static byte getTypeOfSmallId(int smallId) {
		if( ((int)((byte)smallId) & 0x000000FF) == smallId)
			return Constants.SmallIdType.NEXT_IS_SMALL_ID_BYTE;
		if( ((int)((short)smallId) & 0x0000FFFF) == smallId)
			return Constants.SmallIdType.NEXT_IS_SMALL_ID_SHORT;
		return Constants.SmallIdType.NEXT_IS_SMALL_ID_INT;
	}
	
	private static byte getTypeOfSmallIdTypeObj(int smallId) {
		if( ((int)((byte)smallId) & 0x000000FF) == smallId)
			return Constants.Type.CODAGE_BYTE;
		if( ((int)((short)smallId) & 0x0000FFFF) == smallId) 
			return Constants.Type.CODAGE_SHORT;
		return Constants.Type.CODAGE_INT;
	}
	
	private int getSmallIdAndStockObj(Object obj){
		if(!smallIds.containsKey(obj)){
			smallIds.put(obj, compteur++);
		}
		 return smallIds.get(obj);
	}
	
	private int getSmallIdTypeAndStockType(Class<?> typeObj) {
		if(!isDejaVuType(typeObj)){
			dejaVuType.put(typeObj, compteurType++);
		}
		return dejaVuType.get(typeObj);
	}

	protected byte[] calculHeader(Object o, byte debutHeader, boolean estDejaVu) throws IOException, MarshallExeption{
		Class<?> typeObj = o.getClass();
		byte debut = debutHeader;
		boolean isTypeAutre = debutHeader == Constants.Type.AUTRE || debutHeader== Constants.Type.DEVINABLE;
		boolean typeDevinable = debutHeader== Constants.Type.DEVINABLE;
		int smallId = getSmallIdAndStockObj(o);
		byte typeOfSmallId = getTypeOfSmallId(smallId);
		debut |= typeOfSmallId;
		boolean isDejaVuTypeObj = true;
		int smallIdTypeObj = 0;
		byte typeOfSmallIdTypeObj = 0;
		if(isTypeAutre && !estDejaVu){
				typeObj = problemeHibernate(typeObj);
				isDejaVuTypeObj = isDejaVuType(typeObj);
				smallIdTypeObj = getSmallIdTypeAndStockType(typeObj);
				typeOfSmallIdTypeObj = getTypeOfSmallIdTypeObj(smallIdTypeObj);
				debut |= typeOfSmallIdTypeObj;
		}
		try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				DataOutputStream dataOut = new DataOutputStream(byteOut)){
			dataOut.writeByte(debut);
			writeSmallId(smallId, typeOfSmallId, dataOut);
			if(!estDejaVu)
				ecritTypeSiNecessaire(typeObj, isTypeAutre, typeDevinable, isDejaVuTypeObj, smallIdTypeObj,
					typeOfSmallIdTypeObj, dataOut);
			return byteOut.toByteArray();
		}
	}
	
	private Class<?> problemeHibernate(Class<?> typeObj) {
		Class<?> ret = typeObj;
		if(typeObj.getName().toLowerCase().indexOf("org.hibernate.collection.PersistentBag") != -1) 
			ret = ArrayList.class;
		if(typeObj.getName().toLowerCase().indexOf("org.hibernate.collection.PersistentSet") != -1) 
			ret = HashSet.class;
		if(typeObj.getName().toLowerCase().indexOf("org.hibernate.collection.PersistentMap") != -1) 
			ret = HashMap.class;
		if(typeObj.getName().toLowerCase().indexOf("org.hibernate.collection.PersistentSortedSet") != -1) 
			ret = TreeSet.class;
		if(typeObj.getName().toLowerCase().indexOf("org.hibernate.collection.PersistentSortedMap") != -1) 
			ret = TreeMap.class;
		return ret;
	}

	private static void writeSmallId(int smallId, byte typeOfSmallId, DataOutputStream dataOut)
			throws IOException, MarshallExeption {
		switch (typeOfSmallId) {
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_BYTE:
			dataOut.writeByte((byte)smallId);
			break;
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_SHORT:
			dataOut.writeShort((short)smallId);
			break;
		case Constants.SmallIdType.NEXT_IS_SMALL_ID_INT:
			dataOut.writeInt(smallId);
			break;
		default :
			throw new MarshallExeption("trop d'objets");
		}
	}

	private static void ecritTypeSiNecessaire(Class<?> typeObj, boolean isTypeAutre, boolean typeDevinable,
			boolean isDejaVuTypeObj, int smallIdTypeObj, byte typeOfSmallIdTypeObj, DataOutputStream dataOut)
					throws IOException, MarshallExeption {
		if(isTypeAutre && !typeDevinable){
			writeSmallIdType(smallIdTypeObj, typeOfSmallIdTypeObj, dataOut);
			if(!isDejaVuTypeObj){
				dataOut.writeUTF(typeObj.getName());
				
			}
		}
	}

	private static void writeSmallIdType(int smallIdTypeObj, byte typeOfSmallIdTypeObj, DataOutputStream dataOut)
			throws IOException, MarshallExeption {
		switch (typeOfSmallIdTypeObj) {
		case Constants.Type.CODAGE_BYTE:
			dataOut.writeByte((byte)smallIdTypeObj);
			break;
		case Constants.Type.CODAGE_SHORT:
			dataOut.writeShort((short)smallIdTypeObj);
			break;
		case Constants.Type.CODAGE_INT:
			dataOut.writeInt(smallIdTypeObj);
			break;
		default :
			throw new MarshallExeption("trop de type");
		}
	}

	protected boolean isDejaVuType(Class<?> typeObj) {
		return dejaVuType.containsKey(typeObj);
	}
	
	//////////
	protected boolean writeBoolean(boolean v) throws IOException {
		output.writeBoolean(v);
		return v;
	}
	protected void writeByte(byte v) throws IOException {
		output.writeByte((int)v);
	}
	protected void writeByteArray(byte[] v) throws IOException{
		output.write(v);
	}
	protected void writeShort(short v) throws IOException {
		output.writeShort((int)v);
	}
	protected void writeChar(char v) throws IOException {
		output.writeChar((int)v);
	}
	protected void writeInt(int v) throws IOException {
		output.writeInt(v);
	}
	protected void writeLong(long v) throws IOException {
		output.writeLong(v);
	}
	protected void writeFloat(float v) throws IOException {
		output.writeFloat(v);
	}
	protected void writeDouble(double v) throws IOException {
		output.writeDouble(v);
	}
	protected void writeUTF(String s) throws IOException {
		output.writeUTF(s);
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}
}
