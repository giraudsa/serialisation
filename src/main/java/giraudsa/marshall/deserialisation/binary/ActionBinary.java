package giraudsa.marshall.deserialisation.binary;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FieldInformations;

public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	private int profondeur;
	protected int smallId;

	protected ActionBinary(final Class<T> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		if (unmarshaller != null)
			profondeur = getBinaryUnmarshaller().getProfondeur() + 1;
	}

	@Override
	protected void construitObjet() {
	}

	protected abstract void deserialisePariellement()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException;

	protected void exporteObject() throws IllegalAccessException, EntityManagerImplementationException,
			InstanciationException, SetValueException {
		getBinaryUnmarshaller().integreObject(obj);
	}

	@SuppressWarnings("unchecked")
	protected <U> BinaryUnmarshaller<U> getBinaryUnmarshaller() {
		return (BinaryUnmarshaller<U>) unmarshaller;
	}

	@Override
	protected Object getObjet() {
		return getBinaryUnmarshaller().getObject(smallId);
	}

	int getProfondeur() {
		return profondeur;
	}

	protected abstract void initialise() throws InstanciationException, IOException, UnmarshallExeption;

	protected boolean isDejaTotalementDeSerialise() {
		return getBinaryUnmarshaller().isDejaTotalementDeSerialise(smallId);
	}

	protected boolean isDejaVu() {
		return getBinaryUnmarshaller().isDejaVu(smallId);
	}

	protected void litObject(final FieldInformations f)
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		getBinaryUnmarshaller().litObject(f);
	}

	protected boolean readBoolean() throws IOException {
		return getBinaryUnmarshaller().readBoolean();
	}

	protected byte readByte() throws IOException {
		return getBinaryUnmarshaller().readByte();
	}

	protected char readChar() throws IOException {
		return getBinaryUnmarshaller().readChar();
	}

	protected double readDouble() throws IOException {
		return getBinaryUnmarshaller().readDouble();
	}

	protected float readFloat() throws IOException {
		return getBinaryUnmarshaller().readFloat();
	}

	protected int readInt() throws IOException {
		return getBinaryUnmarshaller().readInt();
	}

	protected long readLong() throws IOException {
		return getBinaryUnmarshaller().readLong();
	}

	protected short readShort() throws IOException {
		return getBinaryUnmarshaller().readShort();
	}

	protected String readUTF() throws IOException {
		return getBinaryUnmarshaller().readUTF();
	}

	// methode inutiles en binary
	@Override
	protected void rempliData(final String donnees) {
	}

	protected void set(final FieldInformations fieldInformations, final int smallId)
			throws InstanciationException, IOException, UnmarshallExeption {
		this.fieldInformations = fieldInformations;
		this.smallId = smallId;
		initialise();
	}

	protected void setDejaTotalementDeSerialise() {
		getBinaryUnmarshaller().setDejaTotalementDeSerialise(smallId);
	}

	protected void stockeObjetId() {
		getBinaryUnmarshaller().stockObjectSmallId(smallId, obj);
	}

	protected boolean strategieDeSerialiseTout() {
		return getBinaryUnmarshaller().getStrategie().serialiseTout(profondeur, fieldInformations);
	}
}
