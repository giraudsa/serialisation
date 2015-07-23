package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCollectionType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDate;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDictionaryType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryObject;
import giraudsa.marshall.serialisation.binary.actions.ActionBinarySimpleComportement;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryVoid;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBoolean;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryByte;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryChar;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryDouble;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryFloat;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryShort;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import utils.Constants;
import utils.TypeExtension;

public class BinaryMarshaller extends Marshaller{
	
	private DataOutputStream output;

	/////METHODES STATICS PUBLICS
	public static <U> void toBinary(U obj, DataOutputStream  output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException  {
		BinaryMarshaller v = new BinaryMarshaller(output);
		v.marshall(obj);
	}

	public static <U> void toCompleteBinary(U obj, DataOutputStream  output) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		BinaryMarshaller v = new BinaryMarshaller(output);
		v.marshallAll(obj);
	}
	
	public BinaryMarshaller(DataOutputStream  output) {
		this.output = output;
		dicoTypeToTypeAction.put(Date.class, ActionBinaryDate.class);
		dicoTypeToTypeAction.put(Boolean.class, ActionBinaryBoolean.class);
		dicoTypeToTypeAction.put(Collection.class, ActionBinaryCollectionType.class);
		dicoTypeToTypeAction.put(Map.class, ActionBinaryDictionaryType.class);
		dicoTypeToTypeAction.put(Object.class, ActionBinaryObject.class);
		dicoTypeToTypeAction.put(void.class, ActionBinaryVoid.class);
		dicoTypeToTypeAction.put(Integer.class, ActionBinaryInteger.class);
		dicoTypeToTypeAction.put(Enum.class, ActionBinarySimpleComportement.class);
		dicoTypeToTypeAction.put(UUID.class, ActionBinarySimpleComportement.class);
		dicoTypeToTypeAction.put(String.class, ActionBinarySimpleComportement.class);
		dicoTypeToTypeAction.put(Byte.class, ActionBinaryByte.class);
		dicoTypeToTypeAction.put(Float.class, ActionBinaryFloat.class);
		dicoTypeToTypeAction.put(Double.class, ActionBinaryDouble.class);
		dicoTypeToTypeAction.put(Long.class, ActionBinaryLong.class);
		dicoTypeToTypeAction.put(Short.class, ActionBinaryShort.class);
		dicoTypeToTypeAction.put(Character.class, ActionBinaryChar.class);
		dicoTypeToTypeAction.put(Enum.class, ActionBinaryEnum.class);
	}


	/////METHODES 
	private <T> void marshall(T obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		if (obj == null){
			writeByte(Constants.IS_NULL);//writeIsNull
			writeByte(Constants.IS_FINI);
			return ;
		}
		marshallSpecialise(obj, TypeRelation.COMPOSITION, true);
		writeByte(Constants.IS_FINI);
	}
	
	private <T> void marshallAll(T obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		if (obj == null){
			writeByte(Constants.IS_NULL);//writeIsNull
			writeByte(Constants.IS_FINI);
			return ;
		}
		aSerialiser = new SetQueue<>();
		marshallSpecialise(obj, TypeRelation.COMPOSITION, true);
		while(!aSerialiser.isEmpty()){
			Object objet = aSerialiser.poll();
			if(!estSerialise.contains(objet))
				marshallSpecialise(aSerialiser.poll(), TypeRelation.COMPOSITION, true);
		}
		writeByte(Constants.IS_FINI);
	}
	
	protected <T> void marshallSpecialise(T obj, TypeRelation relation, Boolean couldTypeBeLessSpecifique ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		Class<?> typeObj = TypeExtension.getTypeEnveloppe(obj == null ? void.class : obj.getClass());
		if(obj == null){
			writeByte(Constants.IS_NULL);
		}else{
			boolean isDejaVu = false;
			
			byte header = Constants.Type.getByteHeader(typeObj);
			switch (header) {
			case Constants.Type.BOOL:
				header |= Constants.BOOL_VALUE.getByte((Boolean) obj);
				writeByte(header);
				break;
			case Constants.Type.AUTRE:
				isDejaVu = dejaVu.containsKey(obj);
				int smallId = _getSmallId(obj);
				byte typeOfSmallId = getTypeOfSmallId(smallId);
				header |= typeOfSmallId;
				
				boolean isDejaVuTypeObj = true;
				int smallIdTypeObj = 0;
				byte typeOfSmallIdTypeObj = 0;
				
				if(!isDejaVu){
					if(typeObj.getName().toLowerCase().indexOf("hibernate") != -1) typeObj = ArrayList.class;
					isDejaVuTypeObj = dejaVu.containsKey(typeObj);
					smallIdTypeObj = _getSmallId(typeObj);
					typeOfSmallIdTypeObj = getTypeOfSmallIdTypeObj(smallIdTypeObj);
					header |= typeOfSmallIdTypeObj;
				}
				
				///////////write header
				writeByte(header);
				
				
				/////////write obj small id
				switch (typeOfSmallId) {
				case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_BYTE:
					writeByte((byte)smallId);
					break;
				case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_SHORT:
					writeShort((short)smallId);
					break;
				case Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_INT:
					writeInt(smallId);
					break;
				}
				
				
				///////write type if necessary
				if(!isDejaVu && !couldTypeBeLessSpecifique){
					switch (typeOfSmallIdTypeObj) {
					case Constants.Type.CODAGE_BYTE:
						writeByte((byte)smallIdTypeObj);
						break;
					case Constants.Type.CODAGE_SHORT:
						writeShort((short)smallIdTypeObj);
						break;
					case Constants.Type.CODAGE_INT:
						writeInt(smallIdTypeObj);
						break;
					}
					if(!isDejaVuTypeObj) writeUTF(typeObj.getName());
				}
				break;
				
			case Constants.Type.BYTE:
			case Constants.Type.SHORT:
			case Constants.Type.INT:
			case Constants.Type.LONG:
			case Constants.Type.FLOAT:
			case Constants.Type.DOUBLE:
			case Constants.Type.UUID:
			case Constants.Type.STRING:
			case Constants.Type.DATE:
			case Constants.Type.CHAR:
				writeByte(header);
			}
		///////write value of obj
			getBehavior(obj).getConstructor(Class.class, obj.getClass(), TypeRelation.class, Boolean.class, BinaryMarshaller.class).newInstance(typeObj, obj, relation, isDejaVu, this);
		}
	}
	

	private byte getTypeOfSmallId(int smallId) {
		if( (int)((byte)smallId) == smallId) return Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_BYTE;
		if( (int)((short)smallId) == smallId) return Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_SHORT;
		return Constants.SMALL_ID_TYPE.NEXT_IS_SMALL_ID_INT;
	}
	
	private byte getTypeOfSmallIdTypeObj(int smallId) {
		if( (int)((byte)smallId) == smallId) return Constants.Type.CODAGE_BYTE;
		if( (int)((short)smallId) == smallId) return Constants.Type.CODAGE_SHORT;
		return Constants.Type.CODAGE_INT;
	}
	
	//////////
	boolean writeBoolean(boolean v) throws IOException {
		output.writeBoolean(v);
		return v;
	}
	void writeByte(byte v) throws IOException {
		output.writeByte((int)v);
	}
	void writeShort(short v) throws IOException {
		output.writeShort((int)v);
	}
	void writeChar(char v) throws IOException {
		output.writeChar((int)v);
	}
	void writeInt(int v) throws IOException {
		output.writeInt(v);
	}
	void writeLong(long v) throws IOException {
		output.writeLong(v);
	}
	void writeFloat(float v) throws IOException {
		output.writeFloat(v);
	}
	void writeDouble(double v) throws IOException {
		output.writeDouble(v);
	}
	void writeUTF(String s) throws IOException {
		output.writeUTF(s);
	}
}
