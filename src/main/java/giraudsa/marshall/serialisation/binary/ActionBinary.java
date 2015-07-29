package giraudsa.marshall.serialisation.binary;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import utils.Constants;


public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	
	protected byte headerType;
	protected byte[] headerConstant;

	protected boolean isCompleteMarshalling;//ignore relation
	
	protected BinaryMarshaller getBinaryMarshaller(){
		return (BinaryMarshaller)marshaller;
	}
	
	public ActionBinary(Class<? super T> type, BinaryMarshaller b){
		super(type, b);
		headerType = Constants.Type.getByteHeader(type);
		headerConstant = new byte[]{headerType};
		this.isCompleteMarshalling = getBinaryMarshaller().isCompleteSerialisation;
	}
	
	protected boolean writeHeaders(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific){
		try {
			boolean isDejaVu = isDejaVu(objetASerialiser);
			getBinaryMarshaller().writeByteArray(calculHeaders(objetASerialiser, typeRelation, couldBeLessSpecific, isDejaVu));
			return isDejaVu;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected byte[] calculHeaders(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific, boolean isDejaVu) throws IOException {
		
		return getBinaryMarshaller().calculHeader(objetASerialiser, typeRelation, couldBeLessSpecific, headerType, isDejaVu);
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

	@Override
	protected void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {}
	
	protected void traiteChampsComplexes(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific){}
	
	protected void pushComportement(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		getBinaryMarshaller().aFaire.push(new Comportement(objetASerialiser, typeRelation, couldBeLessSpecific));
	}
	
	class Comportement {
		private Object objet;
		private TypeRelation relation;
		private boolean laRelationsAuraitPuEtreMoinsSpecifique;
		protected Comportement(Object objet, TypeRelation relation, boolean laRelationsAuraitPuEtreMoinsSpecifique) {
			super();
			this.objet = objet;
			this.relation = relation;
			this.laRelationsAuraitPuEtreMoinsSpecifique = laRelationsAuraitPuEtreMoinsSpecifique;
		}
		
		void evalue(){
			traiteChampsComplexes(objet, relation, laRelationsAuraitPuEtreMoinsSpecifique);
		}
		
	}

	protected abstract void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific);
	
}
