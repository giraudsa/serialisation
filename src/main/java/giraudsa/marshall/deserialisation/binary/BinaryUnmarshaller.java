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
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBoolean;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryByte;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryChar;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDate;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDouble;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryFloat;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryShort;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryString;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUUID;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryVoid;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SmallIdTypeException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryUnmarshaller.class);
	private static final Set<Class<?>> simpleEnveloppe = new HashSet<>();
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
	
	private T parse() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, SmallIdTypeException {
		FakeChamp fc = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION);
		litObject(readByte(), fc);
		while(!pileAction.isEmpty()){
			((ActionBinary<?>)getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}
	
	void litObject(byte header, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, SmallIdTypeException {
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
	
	@Override protected void initialiseActions() throws IOException {
		actions.put(Boolean.class, ActionBinaryBoolean.getInstance(this));
		actions.put(Byte.class, ActionBinaryByte.getInstance(this));
		actions.put(Short.class, ActionBinaryShort.getInstance(this));
		actions.put(Integer.class, ActionBinaryInteger.getInstance(this));
		actions.put(Long.class, ActionBinaryLong.getInstance(this));
		actions.put(Float.class, ActionBinaryFloat.getInstance(this));
		actions.put(Double.class, ActionBinaryDouble.getInstance(this));
		actions.put(String.class, ActionBinaryString.getInstance(this));
		actions.put(Date.class, ActionBinaryDate.getInstance(this));
		actions.put(UUID.class, ActionBinaryUUID.getInstance(this));
		actions.put(Void.class, ActionBinaryVoid.getInstance(this));
		actions.put(Character.class, ActionBinaryChar.getInstance(this));
		actions.put(Constants.collectionType, ActionBinaryCollection.getInstance(this));
		actions.put(Constants.arrayType, ActionBinaryArray.getInstance(this));
		actions.put(Constants.dictionaryType, ActionBinaryDictionary.getInstance(this));
		actions.put(Constants.objectType, ActionBinaryObject.getInstance(this));
		actions.put(Constants.enumType, ActionBinaryEnum.getInstance(this));
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

}
