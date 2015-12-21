package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionBinary<T> extends ActionAbstrait<T>{
	protected TypeRelation relation;
	protected int smallId;
	
	protected boolean isDeserialisationComplete(){
		return getBinaryUnmarshaller().isDeserialisationComplete();
	}

	protected ActionBinary(Class<T> type, BinaryUnmarshaller<?> unmarshaller){
		super(type, unmarshaller);
	}
	
	
	protected BinaryUnmarshaller<?> getBinaryUnmarshaller() {
		return (BinaryUnmarshaller<?>)unmarshaller;
	}
	
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

	protected void litObject(TypeRelation relation, Class<?> typeProbable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		getBinaryUnmarshaller().litObject(readByte(), relation, typeProbable);
	}
	
	protected boolean isDejaVu() {
		return getBinaryUnmarshaller().isDejaVu(smallId);
	}
	
	protected Object getObjetDejaVu() {
		return getBinaryUnmarshaller().getObject(smallId);
	}

	protected void stockeObjetId() {
		getBinaryUnmarshaller().stockObjectSmallId(smallId, obj);
	}
	
	protected void exporteObject() throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		getBinaryUnmarshaller().integreObject(obj);
	}

	void set(TypeRelation relation, int smallId) throws IOException, InstantiationException, IllegalAccessException {
		this.relation = relation;
		this.smallId = smallId;
		initialise();
	}

	protected abstract void initialise() throws IOException, InstantiationException, IllegalAccessException;

	public abstract void integreObject(Object obj) throws IllegalArgumentException, IllegalAccessException, InstantiationException;

	public abstract void deserialisePariellement() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException;
	
	protected boolean isDejaTotalementDeSerialise() {
		return getBinaryUnmarshaller().isDejaTotalementDeSerialise(obj);
	}
	protected void setDejaTotalementDeSerialise() {
		getBinaryUnmarshaller().setDejaTotalementDeSerialise(obj);
	}
	
	//methode inutiles en binary
	@Override
	protected void rempliData(String donnees){}
	@Override
	protected void construitObjet(){}
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {}
}
