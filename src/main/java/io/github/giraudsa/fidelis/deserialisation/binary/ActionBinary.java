package io.github.giraudsa.fidelis.deserialisation.binary;

import java.io.IOException;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.exception.EntityManagerImplementationException;
import io.github.giraudsa.fidelis.exception.InstanciationException;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.exception.SetValueException;
import io.github.giraudsa.fidelis.exception.UnmarshallExeption;
import io.github.giraudsa.fidelis.utils.champ.Champ;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	private int profondeur;
	protected int smallId;

	protected ActionBinary(final Class<T> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		if (unmarshaller != null)
			profondeur = getBinaryUnmarshaller().getProfondeur() + 1;
	}

	/** Remet l'action à zéro pour lire une nouvelle valeur de type nouveauType (sous-classes : leurs champs). */
	protected void recycle(final Class<T> nouveauType, final BinaryUnmarshaller<?> nouvelUnmarshaller) {
		type = nouveauType;
		unmarshaller = nouvelUnmarshaller; // une action en réserve peut venir d'une désérialisation précédente
		obj = null;
		fieldInformations = null;
		smallId = 0;
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

	/** voir {@link BinaryUnmarshaller#litValeur(FieldInformations)}. */
	protected Object litValeur(final FieldInformations f)
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		return getBinaryUnmarshaller().litValeur(f);
	}

	/** voir {@link BinaryUnmarshaller#litPrimitif(Champ, Object)}. */
	protected boolean litPrimitif(final Champ champ, final Object objet) throws IOException {
		return getBinaryUnmarshaller().litPrimitif(champ, objet);
	}

	protected static boolean isEnAttente(final Object valeur) {
		return valeur == BinaryUnmarshaller.EN_ATTENTE;
	}

	/** valeur construite par l'action (et non l'objet déjà vu de même smallId). */
	Object valeurLue() {
		return obj;
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

	protected int readVarInt() throws IOException {
		return getBinaryUnmarshaller().readVarInt();
	}

	protected byte[] readBytes(final int taille) throws IOException {
		return getBinaryUnmarshaller().readBytes(taille);
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
