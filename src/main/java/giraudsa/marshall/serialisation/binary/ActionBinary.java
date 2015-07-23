package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionBinary<T> extends ActionAbstrait<T> {

	protected boolean isDejaVu;
	
	protected BinaryMarshaller getBinaryMarshaller(){
		return (BinaryMarshaller)marshaller;
	}
	
	public ActionBinary(Class<? super T> type, T obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type, b);
		this.obj = obj;
		this.relation = relation;
		this.isDejaVu = isDejaVu;
		if(relation == TypeRelation.COMPOSITION) setEstSerialise(obj);
		else stockeASerialiser(obj);
		marshall(obj, relation);
	}

	protected void writeBoolean(boolean v) throws IOException {
		getBinaryMarshaller().writeBoolean(v);
	}
	protected void writeByte(byte v) throws IOException {
		getBinaryMarshaller().writeByte(v);
	}
	protected void writeShort(short v) throws IOException {
		getBinaryMarshaller().writeShort(v);
	}
	protected void writeChar(char v) throws IOException {
		getBinaryMarshaller().writeChar(v);
	}
	protected void writeInt(int v) throws IOException {
		getBinaryMarshaller().writeInt(v);
	}
	protected void writeLong(long v) throws IOException {
		getBinaryMarshaller().writeLong(v);
	}
	protected void writeFloat(float v) throws IOException {
		getBinaryMarshaller().writeFloat(v);
	}
	protected void writeDouble(double v) throws IOException {
		getBinaryMarshaller().writeDouble(v);
	}
	protected void writeUTF(String s) throws IOException {
		getBinaryMarshaller().writeUTF(s);
	}
	protected void writeNull() throws IOException{
		writeByte((byte) 0);
	}
	
	protected <U> void traiteObject(U obj, TypeRelation relation, Boolean couldTypeBeLessSpecifique) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		getBinaryMarshaller().marshallSpecialise(obj, relation, couldTypeBeLessSpecifique);
	}
	
}
