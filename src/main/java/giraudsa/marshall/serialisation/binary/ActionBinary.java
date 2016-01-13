package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import utils.Constants;
import utils.champ.FieldInformations;


public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	
	protected ActionBinary(BinaryMarshaller b){
		super(b);
	}

	protected byte getHeaderType(Class<?> type, boolean typeDevinable){
		return Constants.Type.getByteHeader(type, typeDevinable);
	}
	
	protected byte[] getHeaderConstant(Class<?> type, boolean typeDevinable){
		return new byte[]{getHeaderType(type, typeDevinable)};
	}
	
	protected BinaryMarshaller getBinaryMarshaller(){
		return (BinaryMarshaller)marshaller;
	}
	
	protected boolean writeHeaders(T objetASerialiser, boolean typeDevinable) throws MarshallExeption, IOException{
		boolean isDejaVu = isDejaVu(objetASerialiser);
		getBinaryMarshaller().writeByteArray(calculHeaders(objetASerialiser, typeDevinable, isDejaVu));
		return isDejaVu;
	}

	protected byte[] calculHeaders(T objetASerialiser, boolean typeDevinable, boolean isDejaVu) throws IOException, MarshallExeption {
		byte headerType = getHeaderType(objetASerialiser.getClass(), typeDevinable);
		return getBinaryMarshaller().calculHeader(objetASerialiser, headerType, isDejaVu);
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
	@Override protected void marshall(Object objetASerialiser, FieldInformations fieldInformation){
		pushComportement(new ComportementEcrisValeur((T) objetASerialiser, fieldInformation));
		pushComportement(new ComportementWriteHeader(objetASerialiser, fieldInformation));
	}
	
	protected abstract void ecritValeur(T obj, FieldInformations fieldInformation) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;
	
	protected class ComportementWriteHeader extends Comportement{

		private Object objetASerialiser;
		private FieldInformations fieldInformation;

		protected ComportementWriteHeader(Object objetASerialiser, FieldInformations fieldInformation) {
			super();
			this.objetASerialiser = objetASerialiser;
			this.fieldInformation = fieldInformation;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void evalue() throws MarshallExeption, IOException{
			boolean typeDevinable = isTypeDevinable(objetASerialiser, fieldInformation);
			writeHeaders((T) objetASerialiser, typeDevinable);
		}
		
	}
	protected class ComportementEcrisValeur extends Comportement{

		private T obj;
		private FieldInformations fieldInformations;

		protected ComportementEcrisValeur(T obj, FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue() throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			ecritValeur(obj, fieldInformations);
		}
	}
	
	@Override
	protected <V> boolean aTraiter(V value, FieldInformations f){
		return true;
	}
}
