package giraudsa.marshall.deserialisation.text;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public abstract class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final DateFormat df;
	protected final Reader reader;

	protected TextUnmarshaller(final Reader reader, final EntityManager entity, final SimpleDateFormat dateFormat)
			throws FabriqueInstantiationException {
		super(entity);
		this.reader = reader;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
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
