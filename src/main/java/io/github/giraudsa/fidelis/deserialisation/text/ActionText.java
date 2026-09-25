package io.github.giraudsa.fidelis.deserialisation.text;

import java.text.DateFormat;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.exception.EntityManagerImplementationException;
import io.github.giraudsa.fidelis.exception.InstanciationException;
import io.github.giraudsa.fidelis.utils.champ.ChampUid;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public abstract class ActionText<T> extends ActionAbstrait<T> {

	protected String nom;

	protected <U> ActionText(final Class<T> type, final TextUnmarshaller<U> unmarshaller) {
		super(type, unmarshaller);
	}

	protected DateFormat getDateFormat() {
		return getTextUnarshaller().df;
	}

	/** @return true si les dates sont au format ISO UTC par défaut (lecture rapide possible). */
	protected boolean isDateIsoUtc() {
		return getTextUnarshaller().dateIsoUtc;
	}

	FieldInformations getFieldInformation(final String nom) {
		return getFieldInformationSpecialise(nom);
	}

	protected abstract FieldInformations getFieldInformationSpecialise(String nom);

	String getNom() {
		return nom;
	}

	@SuppressWarnings("unchecked")
	protected <U> TextUnmarshaller<U> getTextUnarshaller() {
		return (TextUnmarshaller<U>) unmarshaller;
	}

	Class<?> getType(final String nomAttribut) {
		return getTypeAttribute(nomAttribut);
	}

	protected abstract Class<?> getTypeAttribute(String nomAttribut);

	@SuppressWarnings("unchecked")
	protected <W> void preciseLeTypeSiIdConnu(final String idAttribut, final String idCandidat)
			throws EntityManagerImplementationException, InstanciationException {
		if (idCandidat == null)
			return;
		if (ChampUid.UID_FIELD_NAME.equals(idAttribut)) {
			obj = getObject(idCandidat, type);
			if (obj != null)
				type = (Class<T>) obj.getClass();
		}
	}

	void setFieldInformation(final FieldInformations fi) {
		fieldInformations = fi;
	}

	void setNom(final String nom) {
		this.nom = nom;
	}
}
