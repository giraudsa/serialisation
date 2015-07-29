package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionBinary<T> extends ActionAbstrait<T>{
	
	protected boolean deserialisationComplete;

	public ActionBinary(Class<? extends T> type, Unmarshaller<?> unmarshaller){
		super(type, unmarshaller);
		deserialisationComplete = getBinaryUnmarshaller().isDeserialisationComplete();
	}
	
	
	protected BinaryUnmarshaller<?> getBinaryUnmarshaller() {
		return (BinaryUnmarshaller<?>)unmarshaller;
	}
	
	
	@SuppressWarnings("unchecked")
	T unmarshall(Class<?> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		return readObject((Class<? extends T>) typeADeserialiser, typeRelation, smallId);
	}
	
	protected abstract T readObject(Class<? extends T> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException;
	
	protected boolean readBoolean() throws IOException {
		return getBinaryUnmarshaller().readBoolean();
	}
	protected byte readByte() throws IOException {
		return getBinaryUnmarshaller().readByte();
	}
	protected short readShort() throws IOException {
		return getBinaryUnmarshaller().readShort();
	}
	protected char readChar() throws IOException {
		return getBinaryUnmarshaller().readChar();
	}
	protected int readInt() throws IOException {
		return getBinaryUnmarshaller().readInt();
	}
	protected long readLong() throws IOException {
		return getBinaryUnmarshaller().readLong();
	}
	protected float readFloat() throws IOException {
		return getBinaryUnmarshaller().readFloat();
	}
	protected double readDouble() throws IOException {
		return getBinaryUnmarshaller().readDouble();
	}
	protected String readUTF() throws IOException {
		return getBinaryUnmarshaller().readUTF();
	}

	protected Object litObject(TypeRelation relation, Class<?> typeProbable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		return getBinaryUnmarshaller().litObject(readByte(), relation, typeProbable);
	}
	
	protected boolean isDejaVu(int smallId) {
		return getBinaryUnmarshaller().isDejaVu(smallId);
	}
	
	protected Object getObjet(int smallId) {
		return getBinaryUnmarshaller().getObject(smallId);
	}

	protected void stockeObjetId(int smallId, Object objetADeserialiser) {
		getBinaryUnmarshaller().stockObjectSmallId(smallId, objetADeserialiser);
	}


	protected void pushComportement(Object objetADeserialiser, TypeRelation typeRelation) {
		getBinaryUnmarshaller().pushComportement(new Comportement(objetADeserialiser, typeRelation));
	}
	

	public void traiteChampsRestant(Object objetADserialiser, TypeRelation typeRelation) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {}
	
	class Comportement{
		private Object objetADserialiser;
		private TypeRelation typeRelation;
		Comportement(Object objetADserialiser, TypeRelation typeRelation) {
			super();
			this.objetADserialiser = objetADserialiser;
			this.typeRelation = typeRelation;
		}
		void finiDeserialisation() throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
			traiteChampsRestant(objetADserialiser, typeRelation);
		}
	}
}
