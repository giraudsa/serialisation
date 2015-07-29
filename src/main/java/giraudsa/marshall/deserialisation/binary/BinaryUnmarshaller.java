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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.zip.InflaterInputStream;

import org.xml.sax.SAXException;

import utils.Constants;
import utils.TypeExtension;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	
	//////METHODES STATICS PUBLICS
	public static <U> U fromBinary(InputStream reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		try(DataInputStream in = new DataInputStream(reader)){
			BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(in, entity){};
			return w.parse();
		}
	}
	
	public static <U> U fromBinary(InputStream reader) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		return fromBinary(reader, null);
	}
	
	/////////FIN METHODES STATICS
	DataInputStream input;
	private int sizeDicoObject = 16;
	private int sizeDicoClazz = 16;
	private Object[] dicoSmallIdToObject = new Object[sizeDicoObject];
	private Class[] dicoSmallIdToClazz = new Class[sizeDicoClazz];
	private Deque<ActionBinary<?>.Comportement> resteADeserialiser = new LinkedList<>();
	boolean deserialisationComplete;
	

	boolean isDeserialisationComplete() {
		return deserialisationComplete;
	}
	
	boolean isDejaVu(int smallId){
		grossiDicoObjectSiNeccessaire(smallId);
		return dicoSmallIdToObject[smallId] != null;
	}
	
	private void grossiDicoObjectSiNeccessaire(int smallId) {
		if(smallId < sizeDicoObject) return;
		sizeDicoObject = sizeDicoObject * 2;
		Object[] tmp = new Object[sizeDicoObject];
		for(int i = 0 ; i < dicoSmallIdToObject.length ; i++){
			tmp[i] = dicoSmallIdToObject[i];
		}
		dicoSmallIdToObject = tmp;
	}

	private void grossiDicoClazzSiNeccessaire(int smallId) {
		if(smallId < sizeDicoClazz) return;
		sizeDicoClazz = sizeDicoClazz * 2;
		Class[] tmp = new Class[sizeDicoClazz];
		for(int i = 0 ; i < dicoSmallIdToClazz.length ; i++){
			tmp[i] = dicoSmallIdToClazz[i];
		}
		dicoSmallIdToClazz = tmp;
	}
	
	public void pushComportement(ActionBinary<?>.Comportement comportement) {
		resteADeserialiser.push(comportement);
	}
	
	private Class<?> getTypeToUnmarshall(byte header, int smallId, Class<?> typeCandidat) throws ClassNotFoundException, IOException {
		if(TypeExtension.isSimple(typeCandidat)) return typeCandidat;
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
		}
		if(smallId == 0) return typeProbable;
		grossiDicoClazzSiNeccessaire(smallId);
		Class<?> ret = dicoSmallIdToClazz[smallId];
		if(ret == null){
			String classeString = readUTF();
			ret = Class.forName(classeString);
			dicoSmallIdToClazz[smallId] = ret;
		}
		return ret;
	}
	
	Object getObject(int smallId){
		grossiDicoObjectSiNeccessaire(smallId);
		return dicoSmallIdToObject[smallId];
	}
	
	void stockObjectSmallId(int smallId, Object obj){
		grossiDicoObjectSiNeccessaire(smallId);
		dicoSmallIdToObject[smallId] =  obj;
	}
	
	@SuppressWarnings("unchecked")
	private T parse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException {
		long debut = System.nanoTime();
		T ret = (T) litObject(readByte(), TypeRelation.COMPOSITION, Object.class);
		while(!resteADeserialiser.isEmpty()){
			resteADeserialiser.pop().finiDeserialisation();
		}
		long fin = System.nanoTime();
		System.out.println("temps de désérialisation = " + (fin - debut)/1e9 + " secondes" );
		return ret;
	}
	
	Object litObject(byte header, TypeRelation relation, Class<?> typeProbable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		if(header == Constants.IS_NULL) return null;
		if(header == Constants.BOOL_VALUE.TRUE) return true;
		if(header == Constants.BOOL_VALUE.FALSE) return false;
		
		Class<?> typeCandidat = Constants.Type.getSimpleType(header, typeProbable);
		///bool
		if(typeCandidat == Boolean.class){
			System.out.println("problème dans le header");
			return false;
		}
		
		int smallId = getSmallId(header, typeCandidat);
		Class<?> typeADeserialiser = getTypeToUnmarshall(header, smallId, typeCandidat);
		return ((ActionBinary<?>)getAction(typeADeserialiser)).unmarshall(typeADeserialiser, relation, smallId);
	}

	
	
	private int getSmallId(byte header, Class<?> t) throws IOException {
		if(t == Boolean.class || t == Byte.class || t == Character.class || t == Short.class
				|| t == Integer.class || t == Long.class) return 0;
		int smallId = 0;
		Byte smallIdType = Constants.SMALL_ID_TYPE.getSmallId(header);
		switch (smallIdType){
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_BYTE:
			smallId = readByte() & 0x000000FF;
			break;
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_SHORT:
			smallId = readShort() & 0x0000FFFF;
			break; 
		case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_INT:
			smallId = readInt();
		}
		return smallId;	
	}
	
	protected BinaryUnmarshaller(DataInputStream input, EntityManager entity) throws ClassNotFoundException, IOException {
		super(entity);
		this.input = input;
		initialiseActions();		
	}

	private void initialiseActions() throws IOException {
		deserialisationComplete = readBoolean();
		actions.put(Byte.class, new ActionBinaryByte(Byte.class, this));
		actions.put(Short.class, new ActionBinaryShort(Short.class, this));
		actions.put(Integer.class, new ActionBinaryInteger(Integer.class, this));
		actions.put(Long.class, new ActionBinaryLong(Long.class, this));
		actions.put(Float.class, new ActionBinaryFloat(Float.class, this));
		actions.put(Double.class, new ActionBinaryDouble(Double.class, this));
		actions.put(String.class, new ActionBinaryString(String.class, this));
		actions.put(Date.class, new ActionBinaryDate(Date.class, this));
		actions.put(UUID.class, new ActionBinaryUUID(UUID.class, this));
		actions.put(Character.class, new ActionBinaryChar(Character.class, this));
		actions.put(Constants.collectionType, new ActionBinaryCollection(Collection.class, this));
		actions.put(Constants.dictionaryType, new ActionBinaryDictionary(Map.class, this));
		actions.put(Constants.objectType, new ActionBinaryObject(Object.class, this));
		actions.put(Constants.enumType, new ActionBinaryEnum(Enum.class, this));
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
