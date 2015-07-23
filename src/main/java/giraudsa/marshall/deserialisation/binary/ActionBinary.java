package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionBinary<T> extends ActionAbstrait<T>{
	
	protected boolean isDejaVu;
	
	public ActionBinary(Class<? extends T> type, TypeRelation relation, T objetPreConstruit, Unmarshaller<?> b) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		super(type, relation, b);
		this.isDejaVu = objetPreConstruit != null;
		obj = isDejaVu ? objetPreConstruit : type.newInstance();
	}
	
	public ActionBinary(Class<? extends T> type, Unmarshaller<?> unmarshaller){
		super(type, unmarshaller);
	}
	
	@Override
	protected void construitObjet() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		obj = readObject();
	}

	int smallId;
	Class<? extends T> _type;
	
	protected BinaryUnmarshaller<?> getBinaryUnmarshaller() {
		return (BinaryUnmarshaller<?>)unmarshaller;
	}
	
	
	T unmarshall() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		return readObject();
	}
	
	protected abstract T readObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException;
	
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

}
