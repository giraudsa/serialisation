package io.github.giraudsa.fidelis.deserialisation.text;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.exception.FabriqueInstantiationException;
import io.github.giraudsa.fidelis.utils.EntityManager;
import io.github.giraudsa.fidelis.utils.TypeExtension;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.CopieFormatDate;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.io.DatesIso;

public abstract class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final DateFormat df;
	/** le format de date est le format ISO UTC par défaut : lecture rapide possible (DatesIso.lit). */
	protected final boolean dateIsoUtc;
	protected final Reader reader;

	protected TextUnmarshaller(final Reader reader, final EntityManager entity, final SimpleDateFormat dateFormat)
			throws FabriqueInstantiationException {
		super(entity);
		this.reader = reader;
		df = CopieFormatDate.copie(dateFormat);
		dateIsoUtc = DatesIso.estMotifIsoUtc(df);
	}

	/**
	 * Sans copie du format de date (df null) : pour une lecture qui ne s'en sert qu'au besoin, en le copiant alors
	 * elle-même.
	 */
	protected TextUnmarshaller(final EntityManager entity, final SimpleDateFormat dateFormat)
			throws FabriqueInstantiationException {
		super(entity);
		reader = null;
		df = null;
		dateIsoUtc = CopieFormatDate.estIsoUtc(dateFormat);
	}

	@Override
	public void dispose() throws IOException {
		reader.close();
	}

	protected String getNom(final ActionText<?> action) {
		return action.getNom();
	}

	protected Class<?> getType(final String nomAttribut) {
		final ActionText<?> action = (ActionText<?>) getActionEnCours();
		return action == null ? Object.class : TypeExtension.getTypeEnveloppe(action.getType(nomAttribut));
	}

	protected void setFieldInformation(final ActionText<?> action) {
		FieldInformations fi;
		final String nom = action.getNom();
		final ActionText<?> actionEnCours = (ActionText<?>) getActionEnCours();
		if (actionEnCours != null)
			fi = actionEnCours.getFieldInformation(nom);
		else
			fi = new FakeChamp(nom, Object.class, TypeRelation.COMPOSITION, null);
		action.setFieldInformation(fi);
	}

	protected void setNom(final ActionText<?> action, final String nom) {
		if (action != null)
			action.setNom(nom);
	}
}
