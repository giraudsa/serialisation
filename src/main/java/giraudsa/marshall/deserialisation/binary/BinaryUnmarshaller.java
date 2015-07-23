package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryCollection;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryDictionary;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryObject;
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
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.xml.sax.SAXException;

import utils.Constants;
import utils.TypeExtension;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	
	//////METHODES STATICS PUBLICS
	public static <U> U fromBinary(InputStream reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(new DataInputStream(reader), entity){};
		return w.parse();
	}
	
	public static <U> U fromBinary(InputStream reader) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		return fromBinary(reader, null);
	}
	
	/////////FIN METHODES STATICS
	
	private Map<Class<?>, ActionBinary<?>> dicoSimpleTypeToAction = new HashMap<>();
	private boolean isFirstLoop = true;
	DataInputStream input;
	private Map<Integer, Object> dicoSmallIdToObject = new HashMap<>();
	private Map<Integer, Class<?>> dicoSmallIdToClazz = new HashMap<>();
	
	private boolean isDejaVu(int smallId){
		return dicoSmallIdToObject.containsKey(smallId);
	}
	
	private Class<?> readType(byte header, Class<?> typeProbable) throws IOException, ClassNotFoundException{
		byte b = Constants.Type.getLongueurCodageType(header);
		int smallId = 0;
		switch (b) {
		case Constants.Type.CODAGE_BYTE:
			smallId = (int)readByte();
			break;
		case Constants.Type.CODAGE_SHORT:
			smallId = (int)readShort();
			break;
		case Constants.Type.CODAGE_INT:
			smallId = (int)readInt();
			break;
		}
		if(smallId == 0) return typeProbable;
		Class<?> ret = dicoSmallIdToClazz.get(smallId);
		if(ret == null){
			String classeString = readUTF();
			ret = Class.forName(classeString);
			dicoSmallIdToClazz.put(smallId, ret);
		}
		return ret;
	}
	
	Object getObject(int smallId){
		return dicoSmallIdToObject.get(smallId);
	}
	
	void stockObjectSmallId(int smallId, Object obj){
		dicoSmallIdToObject.put(smallId, obj);
	}
	
	@SuppressWarnings("unchecked")
	private T parse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException {
		byte header;
		while((header = readByte()) != Constants.IS_FINI){
			Object objet = litObject(header, TypeRelation.COMPOSITION, Object.class);
			if(isFirstLoop){
				obj = (T) objet;
			}
		}
		return obj;
	}
	
	Object litObject(byte header, TypeRelation relation, Class<?> typeProbable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		////null
		if(header == Constants.IS_NULL) return null;
		
		Class<?> typeCandidat = Constants.Type.getSimpleType(header, typeProbable);
		///bool
		if(typeCandidat == Boolean.class) return Constants.BOOL_VALUE.getBool(header);
		//DATE, UUID, STRING
		if(typeCandidat == Date.class || typeCandidat == String.class || typeCandidat == UUID.class){
			int smallId = getSmallId(header);
			if(isDejaVu(smallId)){
				return dicoSmallIdToObject.get(smallId);
			}
			Object objetResultat = dicoSimpleTypeToAction.get(typeCandidat).unmarshall();
			dicoSmallIdToObject.put(smallId, objetResultat);
			return objetResultat;
		}
		///autres types simples
		if(TypeExtension.isSimple(typeCandidat)) return dicoSimpleTypeToAction.get(typeCandidat).unmarshall();
		//le type complexe
		int smallId = getSmallId(header);
		Object objToUnmarshall = isDejaVu(smallId) ? dicoSmallIdToObject.get(smallId) : readType(header, typeProbable).newInstance();
		return executeAction(objToUnmarshall, relation);
	}

	@SuppressWarnings("unchecked")
	private Object executeAction(Object objToUnmarshall, TypeRelation relation) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		return getObjet(getTypeAction(objToUnmarshall.getClass())
				.getConstructor(Class.class, TypeRelation.class, objToUnmarshall.getClass(), Unmarshaller.class)
				.newInstance(objToUnmarshall.getClass(), relation, objToUnmarshall, this));
	}

	private int getSmallId(byte header) throws IOException {
		int smallId = 0;
		Byte smallIdType = Constants.SMALL_ID_TYPE.getSmallId(header);
		switch (smallIdType){
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_BYTE:
			smallId = (int)readByte();
			break;
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_SHORT:
			smallId = (int)readShort();
			break; 
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_INT:
			smallId = readInt();
		}
		return smallId;	
	}

	void putObject(int smallId, Object obj){
		dicoSmallIdToObject.put(smallId, obj);
	}
	
	protected BinaryUnmarshaller(DataInputStream input) throws ClassNotFoundException {
		super();
		this.input = input;
		dicoSimpleTypeToAction.put(Byte.class, new ActionBinaryByte(Byte.class, this));
		dicoSimpleTypeToAction.put(Short.class, new ActionBinaryShort(Short.class, this));
		dicoSimpleTypeToAction.put(Integer.class, new ActionBinaryInteger(Integer.class, this));
		dicoSimpleTypeToAction.put(Long.class, new ActionBinaryLong(Long.class, this));
		dicoSimpleTypeToAction.put(Float.class, new ActionBinaryFloat(Float.class, this));
		dicoSimpleTypeToAction.put(Double.class, new ActionBinaryDouble(Double.class, this));
		dicoSimpleTypeToAction.put(String.class, new ActionBinaryString(String.class, this));
		dicoSimpleTypeToAction.put(Date.class, new ActionBinaryDate<Date>(Date.class, this));
		dicoSimpleTypeToAction.put(UUID.class, new ActionBinaryUUID(UUID.class, this));
		dicoSimpleTypeToAction.put(Character.class, new ActionBinaryChar(Character.class, this));
		typesAction.put(Collection.class, ActionBinaryCollection.class);
		typesAction.put(Map.class, ActionBinaryDictionary.class);
		typesAction.put(Object.class, ActionBinaryObject.class);
		typesAction.put(Enum.class, ActionBinaryEnum.class);
	}

	protected BinaryUnmarshaller(DataInputStream input, EntityManager entity) throws ClassNotFoundException {
		super(entity);
		this.input = input;
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
}
