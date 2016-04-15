package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionBinary<T> extends ActionAbstrait<T>{
	protected int smallId;
	
	protected ActionBinary(Class<T> type, BinaryUnmarshaller<?> unmarshaller){
		super(type, unmarshaller);
	}

	protected boolean isDeserialisationComplete(){
		return getBinaryUnmarshaller().isDeserialisationComplete();
	}

	@SuppressWarnings("unchecked")
	protected <U> BinaryUnmarshaller<U> getBinaryUnmarshaller() {
		return (BinaryUnmarshaller<U>)unmarshaller;
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

	protected void litObject(FieldInformations f) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption{
		getBinaryUnmarshaller().litObject(f);
	}
	
	protected boolean isDejaVu() {
		return getBinaryUnmarshaller().isDejaVu(smallId);
	}
	
	@Override
	protected Object getObjet() {
		return getBinaryUnmarshaller().getObject(smallId);
	}

	protected void stockeObjetId() {
		getBinaryUnmarshaller().stockObjectSmallId(smallId, obj);
	}
	
	protected void exporteObject() throws IllegalAccessException, InstantiationException, UnmarshallExeption{
		getBinaryUnmarshaller().integreObject(obj);
	}

	protected void set(FieldInformations fieldInformations, int smallId) throws IOException, InstantiationException, IllegalAccessException, UnmarshallExeption, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.fieldInformations = fieldInformations;
		this.smallId = smallId;
		initialise();
	}

	protected abstract void initialise() throws IOException, InstantiationException, IllegalAccessException, UnmarshallExeption, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

	protected abstract void deserialisePariellement() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption;

	protected boolean isDejaTotalementDeSerialise() {
		return getBinaryUnmarshaller().isDejaTotalementDeSerialise(smallId);
	}
	protected void setDejaTotalementDeSerialise() {
		getBinaryUnmarshaller().setDejaTotalementDeSerialise(smallId);
	}
	
	//methode inutiles en binary
	@Override
	protected void rempliData(String donnees){}
	@Override
	protected void construitObjet(){}
}
