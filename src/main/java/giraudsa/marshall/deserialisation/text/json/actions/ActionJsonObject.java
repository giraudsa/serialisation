package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;
import utils.TypeExtension;
import utils.champ.FieldInformations;

public class ActionJsonObject<T> extends ActionJson<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonObject.class);

	public static ActionAbstrait<Object> getInstance() {
		return new ActionJsonObject<>(Object.class, null);
	}

	private final Map<String, Object> dicoNomChampToValue;

	private ActionJsonObject(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		dicoNomChampToValue = new HashMap<>();
	}

	@Override
	protected void construitObjet()
			throws EntityManagerImplementationException, InstanciationException, SetValueException {
		for (final Entry<String, Object> entry : dicoNomChampToValue.entrySet()) {
			final FieldInformations champ = TypeExtension.getChampByName(type, entry.getKey());
			champ.set(obj, entry.getValue(), getDicoObjToFakeId());
		}
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nomAttribut) {
		return TypeExtension.getChampByName(type, nomAttribut);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonObject<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		final FieldInformations champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());// on renvoie Integer à la place de int, Double
																		// au lieu de double, etc...
		return champ.getValueType();
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet)
			throws EntityManagerImplementationException, InstanciationException {
		preciseLeTypeSiIdConnu(nomAttribut, objet != null ? objet.toString() : null);
		dicoNomChampToValue.put(nomAttribut, objet);
	}

	@Override
	protected void rempliData(final String donnees) {
		LOGGER.error("on est pas supposé avoir de données avec un objet.");
		// rien a faire avec un objet, il n'y a pas de data
	}

}
