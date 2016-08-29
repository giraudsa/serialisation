package giraudsa.marshall.deserialisation.binary;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FieldInformations;

import java.io.IOException;

public abstract class ActionBinary<T> extends ActionAbstrait<T>{
	protected int smallId;
	private int profondeur;
	
	protected ActionBinary(Class<T> type, BinaryUnmarshaller<?> unmarshaller){
		super(type, unmarshaller);
		if(unmarshaller != null)
			profondeur = getBinaryUnmarshaller().getProfondeur() + 1;
	}
	
	int getProfondeur(){
		return profondeur;
	}
	
	protected boolean strategieDeSerialiseTout() {
		return getBinaryUnmarshaller().getStrategie().serialiseTout(profondeur, fieldInformations);
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

	protected void litObject(FieldInformations f) throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
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
	
	protected void exporteObject() throws IllegalAccessException, EntityManagerImplementationException, InstanciationException, SetValueException{
		getBinaryUnmarshaller().integreObject(obj);
	}

	protected void set(FieldInformations fieldInformations, int smallId) throws InstanciationException, IOException, UnmarshallExeption {
		this.fieldInformations = fieldInformations;
		this.smallId = smallId;
		initialise();
	}

	protected abstract void initialise() throws InstanciationException, IOException, UnmarshallExeption;

	protected abstract void deserialisePariellement() throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException;

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
