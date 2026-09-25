package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.Arrays;

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

	/**
	 * valeurs lues, dans l'ordre (une clé répétée : la dernière valeur l'emporte, comme avec une map), avec le champ
	 * résolu et le type pour lequel il l'a été
	 */
	/** par valeur, 4 cases : nom, valeur, champ, type pour lequel le champ a été résolu. */
	private Object[] lues = new Object[32];
	private int nbLues;
	/** dernier champ résolu : le même nom (même instance) est demandé pour le type, les informations et la valeur. */
	private String dernierNom;
	private Class<?> dernierType;
	private FieldInformations dernierChamp;

	private ActionJsonObject(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	private FieldInformations champ(final String nom) {
		if (nom == dernierNom && type == dernierType) // NOSONAR : comparaison d'identité voulue
			return dernierChamp;
		final FieldInformations champ = TypeExtension.getChampByName(type, nom);
		dernierNom = nom;
		dernierType = type;
		dernierChamp = champ;
		return champ;
	}

	@Override
	protected void construitObjet()
			throws EntityManagerImplementationException, InstanciationException, SetValueException {
		final Object[] t = lues;
		for (int i = 0; i < nbLues; i += 4) {
			// le type a pu changer depuis (id connu d'un objet d'une sous-classe) : le champ est alors recherché
			final FieldInformations champ = t[i + 3] == type ? (FieldInformations) t[i + 2]
					: TypeExtension.getChampByName(type, (String) t[i]);
			champ.set(obj, t[i + 1], getDicoObjToFakeId());
		}
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nomAttribut) {
		return champ(nomAttribut);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonObject<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		final FieldInformations champ = champ(nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());// on renvoie Integer à la place de int, Double
																		// au lieu de double, etc...
		return champ.getValueType();
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet)
			throws EntityManagerImplementationException, InstanciationException {
		preciseLeTypeSiIdConnu(nomAttribut, objet != null ? objet.toString() : null);
		if (nbLues + 4 > lues.length)
			lues = Arrays.copyOf(lues, lues.length * 2);
		lues[nbLues++] = nomAttribut;
		lues[nbLues++] = objet;
		lues[nbLues++] = champ(nomAttribut);
		lues[nbLues++] = type;
	}

	@Override
	protected void rempliData(final String donnees) {
		LOGGER.error("on est pas supposé avoir de données avec un objet.");
		// rien a faire avec un objet, il n'y a pas de data
	}

}
