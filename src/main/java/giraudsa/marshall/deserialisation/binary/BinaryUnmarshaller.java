package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.Unmarshaller;
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
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
	@SuppressWarnings("rawtypes")
	private Class[] dicoSmallIdToClazz = new Class[sizeDicoClazz];
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
		@SuppressWarnings("rawtypes")
		Class[] tmp = new Class[sizeDicoClazz];
		for(int i = 0 ; i < dicoSmallIdToClazz.length ; i++){
			tmp[i] = dicoSmallIdToClazz[i];
		}
		dicoSmallIdToClazz = tmp;
	}
	
	private Class<?> getTypeToUnmarshall(byte header, int smallId, Class<?> typeCandidat) throws ClassNotFoundException, IOException {
		if(TypeExtension.isSimple(typeCandidat)) return typeCandidat;
		boolean typeDevinable = Constants.Type.isTypeDevinable(header);
		if (typeDevinable) return typeCandidat;
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
	
	private T parse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException {
		litObject(readByte(), TypeRelation.COMPOSITION, Object.class);
		while(!pileAction.isEmpty()){
			((ActionBinary<?>)getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}
	
	void litObject(byte header, TypeRelation relation, Class<?> typeProbable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		Class<?> typeCandidat = null;
		
		if(header == Constants.IS_NULL) typeCandidat = Void.class;
		if(header == Constants.BOOL_VALUE.TRUE || header == Constants.BOOL_VALUE.FALSE) typeCandidat = Boolean.class;
		
		if(typeCandidat == null) typeCandidat = Constants.Type.getSimpleType(header, typeProbable);
	
		int smallId = getSmallId(header, typeCandidat);
		Class<?> typeADeserialiser = getTypeToUnmarshall(header, smallId, typeCandidat);
		ActionAbstrait<?> action = getAction(typeADeserialiser);
		if(typeADeserialiser == Boolean.class) ((ActionBinaryBoolean)action).setBool(header);
		((ActionBinary<?>)action).set(relation, smallId);
		pileAction.push(action);
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
		actions.put(Byte.class, ActionBinaryBoolean.getInstance(this));
		actions.put(Byte.class, ActionBinaryByte.getInstance(this));
		actions.put(Short.class, ActionBinaryShort.getInstance(this));
		actions.put(Integer.class, ActionBinaryInteger.getInstance(this));
		actions.put(Long.class, ActionBinaryLong.getInstance(this));
		actions.put(Float.class, ActionBinaryFloat.getInstance(this));
		actions.put(Double.class, ActionBinaryDouble.getInstance(this));
		actions.put(String.class, ActionBinaryString.getInstance(this));
		actions.put(Date.class, ActionBinaryDate.getInstance(this));
		actions.put(UUID.class, ActionBinaryUUID.getInstance(this));
		actions.put(Character.class, ActionBinaryChar.getInstance(this));
		actions.put(Constants.collectionType, ActionBinaryCollection.getInstance(this));
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
	public void integreObject(Object obj) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		pileAction.pop();
		ActionBinary<?> action = (ActionBinary<?>)getActionEnCours();
		if(action == null) this.obj = (T) obj;
		else action.integreObject(obj);
	}
	
	private Set<Object> isDejaTotalementDeSerialise = new HashSet<>();

	boolean isDejaTotalementDeSerialise(Object o) {
		return isDejaTotalementDeSerialise.contains(o);
	}

	void setDejaTotalementDeSerialise(Object o) {
		isDejaTotalementDeSerialise.add(o);
	}

}
