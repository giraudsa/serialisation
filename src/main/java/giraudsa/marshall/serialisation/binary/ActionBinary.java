package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import utils.Constants;
import utils.champ.FieldInformations;


public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	
	protected ActionBinary(){
		super();
	}

	protected byte getHeaderType(Class<?> type, boolean typeDevinable){
		return Constants.Type.getByteHeader(type, typeDevinable);
	}
	
	protected byte[] getHeaderConstant(Class<?> type, boolean typeDevinable){
		return new byte[]{getHeaderType(type, typeDevinable)};
	}
	
	protected BinaryMarshaller getBinaryMarshaller(Marshaller marshaller){
		return (BinaryMarshaller)marshaller;
	}
	
	protected boolean writeHeaders(Marshaller marshaller, T objetASerialiser, boolean typeDevinable) throws MarshallExeption, IOException{
		boolean isDejaVu = isDejaVu(marshaller, objetASerialiser);
		getBinaryMarshaller(marshaller).writeByteArray(calculHeaders(marshaller, objetASerialiser, typeDevinable, isDejaVu));
		return isDejaVu;
	}

	protected byte[] calculHeaders(Marshaller marshaller, T objetASerialiser, boolean typeDevinable, boolean isDejaVu) throws IOException, MarshallExeption {
		byte headerType = getHeaderType(objetASerialiser.getClass(), typeDevinable);
		return getBinaryMarshaller(marshaller).calculHeader(objetASerialiser, headerType, isDejaVu);
	}
	protected void writeBoolean(Marshaller marshaller, boolean v) throws IOException {
		getBinaryMarshaller(marshaller).writeBoolean(v);
	}
	protected void writeByte(Marshaller marshaller, byte v) throws IOException {
		getBinaryMarshaller(marshaller).writeByte(v);
	}
	protected void writeShort(Marshaller marshaller, short v) throws IOException {
		getBinaryMarshaller(marshaller).writeShort(v);
	}
	protected void writeChar(Marshaller marshaller, char v) throws IOException {
		getBinaryMarshaller(marshaller).writeChar(v);
	}
	protected void writeInt(Marshaller marshaller, int v) throws IOException {
		getBinaryMarshaller(marshaller).writeInt(v);
	}
	protected void writeLong(Marshaller marshaller, long v) throws IOException {
		getBinaryMarshaller(marshaller).writeLong(v);
	}
	protected void writeFloat(Marshaller marshaller, float v) throws IOException {
		getBinaryMarshaller(marshaller).writeFloat(v);
	}
	protected void writeDouble(Marshaller marshaller, double v) throws IOException {
		getBinaryMarshaller(marshaller).writeDouble(v);
	}
	protected void writeUTF(Marshaller marshaller, String s) throws IOException {
		getBinaryMarshaller(marshaller).writeUTF(s);
	}
	protected void writeNull(Marshaller marshaller) throws IOException{
		writeByte(marshaller, (byte) 0);
	}
	
	@SuppressWarnings("unchecked")
	@Override protected void marshall(Marshaller marshaller, Object objetASerialiser, FieldInformations fieldInformation){
		pushComportement(marshaller, new ComportementEcrisValeur((T) objetASerialiser, fieldInformation));
		pushComportement(marshaller, new ComportementWriteHeader(objetASerialiser, fieldInformation));
	}
	
	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformation) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;
	
	@Override
	protected <V> boolean aTraiter(Marshaller marshaller, V value, FieldInformations f){
		return true;
	}

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
		protected void evalue(Marshaller marshaller) throws MarshallExeption, IOException{
			boolean typeDevinable = isTypeDevinable(marshaller, objetASerialiser, fieldInformation);
			writeHeaders(marshaller, (T) objetASerialiser, typeDevinable);
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
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			ecritValeur(marshaller, obj, fieldInformations);
		}
	}
}
