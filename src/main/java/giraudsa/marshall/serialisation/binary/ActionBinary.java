package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import utils.Constants;


public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	
	protected byte getHeaderType(Class<?> type, boolean typeDevinable){
		return Constants.Type.getByteHeader(type, typeDevinable);
	}
	
	protected byte[] getHeaderConstant(Class<?> type, boolean typeDevinable){
		return new byte[]{getHeaderType(type, typeDevinable)};
	}
	
	protected BinaryMarshaller getBinaryMarshaller(){
		return (BinaryMarshaller)marshaller;
	}
	
	public ActionBinary(BinaryMarshaller b){
		super(b);
	}
	
	protected boolean writeHeaders(T objetASerialiser, TypeRelation typeRelation, boolean typeDevinable){
		try {
			boolean isDejaVu = isDejaVu(objetASerialiser);
			getBinaryMarshaller().writeByteArray(calculHeaders(objetASerialiser, typeRelation, typeDevinable, isDejaVu));
			return isDejaVu;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected byte[] calculHeaders(T objetASerialiser, TypeRelation typeRelation, boolean typeDevinable, boolean isDejaVu) throws IOException {
		byte headerType = getHeaderType(objetASerialiser.getClass(), typeDevinable);
		return getBinaryMarshaller().calculHeader(objetASerialiser, typeRelation, headerType, isDejaVu);
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
	
	@SuppressWarnings("unchecked")
	protected void marshall(Object objetASerialiser, TypeRelation typeRelation, boolean typeDevinable){
		pushComportement(new ComportementEcrisValeur((T) objetASerialiser, typeRelation));
		pushComportement(new ComportementWriteHeader(objetASerialiser, typeRelation, typeDevinable));
	}
	
	abstract protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;
	
	protected class ComportementWriteHeader extends Comportement{

		private Object objetASerialiser;
		private TypeRelation typeRelation;
		private boolean typeDevinable;

		public ComportementWriteHeader(Object objetASerialiser, TypeRelation typeRelation, boolean typeDevinable) {
			super();
			this.objetASerialiser = objetASerialiser;
			this.typeRelation = typeRelation;
			this.typeDevinable = typeDevinable;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void evalue()
				throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
			writeHeaders((T) objetASerialiser, typeRelation, typeDevinable);
		}
		
	}
	protected class ComportementEcrisValeur extends Comportement{

		private T obj;
		private TypeRelation relation;

		public ComportementEcrisValeur(T obj, TypeRelation relation) {
			super();
			this.obj = obj;
			this.relation = relation;
		}

		@Override
		public void evalue()
				throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
			ecritValeur(obj, relation);
		}
	}
	
	@Override
	protected <TypeValue> boolean aTraiter(TypeValue value) throws IOException {
		return true;
	}
}
