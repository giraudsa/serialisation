package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlEscapeUtil;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FieldInformations;

public class ActionXmlObject<T> extends ActionXmlComplexeObject<T> {
	public static ActionAbstrait<Object> getInstance() {
		return new ActionXmlObject<>(Object.class, null);
	}

	private final Map<String, Object> dicoNomChampToValue;

	private ActionXmlObject(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
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
		return new ActionXmlObject<>(type, (XmlUnmarshaller<?>) unmarshaller);
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

	public void setId(final String id) throws InstanciationException, EntityManagerImplementationException {
		preciseLeTypeSiIdConnu(ChampUid.UID_FIELD_NAME, id);
		final Champ champId = TypeExtension.getChampId(type);
		Class<?> typeId = champId.getValueType();
		if (typeId.isAssignableFrom(UUID.class)) {
			dicoNomChampToValue.put(ChampUid.UID_FIELD_NAME, UUID.fromString(id));
			return;
		}
		Object objId;
		typeId = TypeExtension.getTypeEnveloppe(typeId);
		if (TypeExtension.isEnveloppe(typeId) || typeId.isAssignableFrom(String.class)) {
			try {
				objId = typeId.getConstructor(String.class).newInstance(XmlEscapeUtil.unescape(id));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new InstanciationException("impossible d'instancier un objet de type " + typeId
						+ " avec la valeur " + System.lineSeparator() + XmlEscapeUtil.unescape(id), e);
			}
			dicoNomChampToValue.put(ChampUid.UID_FIELD_NAME, objId);
		}
	}
}
