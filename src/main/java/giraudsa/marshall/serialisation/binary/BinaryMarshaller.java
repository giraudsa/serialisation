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
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLocale;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryShort;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryVoid;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;

import java.io.BufferedOutputStream;
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
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
		dicoTypeToAction.put(Locale.class, new ActionBinaryLocale());
		dicoTypeToAction.put(InetAddress.class, new ActionBinaryInetAddress());
		dicoTypeToAction.put(BitSet.class, new ActionBinaryBitSet());
		dicoTypeToAction.put(Calendar.class, new ActionBinaryCalendar());
		dicoTypeToAction.put(StringBuilder.class, new ActionBinaryStringBuilder());
		dicoTypeToAction.put(StringBuffer.class, new ActionBinaryStringBuffer());
	}
	protected DataOutputStream output;
	private Map<Object, Integer> smallIds = new HashMap<>();
	private Map<Class<?>, Short> dejaVuType = new HashMap<>();
	private Map<UUID, Integer> dejaVuUuid = new HashMap<>();
	private Map<Date, Integer> dejaVuDate = new HashMap<>();
	private Map<String, Integer> dejaVuString = new HashMap<>();
	private int compteur = 1;
	private short compteurType = 1;
	private int compteurUuid = 1;
	private int compteurDate = 1;
	private int compteurString = 1;
	private BinaryMarshaller(DataOutputStream  output, StrategieDeSerialisation strategie) throws IOException, MarshallExeption {
		super(strategie);
		this.output = output;
		writeSpecialisation();
	}
	private void writeSpecialisation() throws IOException, MarshallExeption {
		byte firstByte = Constants.getFirstByte(strategie);
		writeByte(firstByte);
		if(firstByte == Constants.STRATEGIE_INCONNUE){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			toCompleteBinary(strategie, out);
			writeByteArray(out.toByteArray());
		}
	}
	public static <U> void toBinary(U obj, OutputStream  output) throws MarshallExeption{
		toBinary(obj, output, new StrategieParComposition());
	}

	/////METHODES STATICS PUBLICS
	public static <U> void toBinary(U obj, OutputStream  output, StrategieDeSerialisation strategie) throws MarshallExeption{
		try(DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(output))){
			BinaryMarshaller v = new BinaryMarshaller(stream, strategie);
			v.marshall(obj);
			stream.flush();
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("Problème lors de la sérialisation binaire", e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> void toCompleteBinary(U obj, OutputStream  output) throws MarshallExeption{
		try(DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(output))){
			BinaryMarshaller v = new BinaryMarshaller(stream, new StrategieParComposition());
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
		marshall(obj, fieldsInfo);
		while(!aFaire.isEmpty()){
			deserialisePile();
		}
	}
	
	protected boolean isSmallIdDefined(Object obj){
		return smallIds.containsKey(obj);
	}

	protected int getSmallIdAndStockObj(Object obj){
		if(!isSmallIdDefined(obj)){
			int smallid = TypeExtension.isSimpleBinary(obj.getClass()) ? -1 : compteur++;
			smallIds.put(obj, smallid);
		}
		return smallIds.get(obj);
	}
	
	protected short getSmallIdTypeAndStockType(Class<?> typeObj) {
		if(!isDejaVuType(typeObj)){
			dejaVuType.put(typeObj, compteurType++);
		}
		return dejaVuType.get(typeObj);
	}

	protected boolean isDejaVuType(Class<?> typeObj) {
		return dejaVuType.containsKey(typeObj);
	}
	
	protected int getSmallIdAndStockUUID(UUID id) {
		if(!isDejaVuUUID(id)){
			dejaVuUuid .put(id, compteurUuid++);
		}
		return dejaVuUuid.get(id);
	}

	protected boolean isDejaVuUUID(UUID id) {
		return dejaVuUuid.containsKey(id);
	}
	
	protected int getSmallIdAndStockDate(Date date) {
		if(!isDejaVuDate(date)){
			dejaVuDate.put(date, compteurDate++);
		}
		return dejaVuDate.get(date);
	}

	protected boolean isDejaVuDate(Date date) {
		return dejaVuDate.containsKey(date);
	}
	
	protected int getSmallIdAndStockString(String string) {
		if(!isDejaVuString(string)){
			dejaVuString.put(string, compteurString++);
		}
		return dejaVuString.get(string);
	}

	protected boolean isDejaVuString(String string) {
		return dejaVuString.containsKey(string);
	}
	
	//////////
	protected void writeBoolean(boolean v) throws IOException {
		output.writeBoolean(v);
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
