package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.EntityManager;
import utils.TypeExtension;
import utils.champ.AccesChamp;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionJsonObject extends ActionJson<Object> {

	public ActionJsonObject() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
			throws IOException {
		fermeAccolade(marshaller);
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
			throws IOException {
		ouvreAccolade(marshaller);
		if (!typeDevinable) {
			ecritType(marshaller, obj);
			return true;
		}
		return false;
	}

	/**
	 * Champ primitif (hors char) ou chaîne : écrit sans passer par l'action de la valeur, avec le même texte (une
	 * valeur de ces types a toujours un type devinable et n'est jamais enveloppée) ; les primitifs sont lus sans
	 * boxing.
	 *
	 * @return false si le champ n'est pas de ces types (l'appelant l'écrit par le chemin général).
	 */
	private boolean ecritSimple(final Marshaller marshaller, final Object obj, final Champ champ,
			final boolean virgule, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entityManager)
			throws IOException, IllegalAccessException, MarshallExeption {
		final int nature = champ.getNaturePrimitive();
		if (nature == AccesChamp.AUCUNE) {
			if (champ.getValueType() != String.class)
				return false;
			final Object valeur = champ.get(obj, dicoObjToFakeId, entityManager);
			if (aTraiter(valeur, champ)) {
				if (virgule)
					writeSeparator(marshaller);
				ecritClef(marshaller, champ);
				writeWithQuote(marshaller, valeur.toString());
			}
			return true;
		}
		final AccesChamp acces = champ.getAcces();
		switch (nature) {
		case AccesChamp.INT:
			ecritClefSimple(marshaller, champ, virgule);
			ecritEntier(marshaller, acces.getInt(obj));
			return true;
		case AccesChamp.LONG:
			ecritClefSimple(marshaller, champ, virgule);
			ecritEntier(marshaller, acces.getLong(obj));
			return true;
		case AccesChamp.SHORT:
			ecritClefSimple(marshaller, champ, virgule);
			ecritEntier(marshaller, acces.getShort(obj));
			return true;
		case AccesChamp.BYTE:
			ecritClefSimple(marshaller, champ, virgule);
			ecritEntier(marshaller, acces.getByte(obj));
			return true;
		case AccesChamp.DOUBLE:
			ecritClefSimple(marshaller, champ, virgule);
			ecritBrut(marshaller, Double.toString(acces.getDouble(obj)));
			return true;
		case AccesChamp.FLOAT:
			ecritClefSimple(marshaller, champ, virgule);
			ecritBrut(marshaller, Float.toString(acces.getFloat(obj)));
			return true;
		case AccesChamp.BOOLEAN:
			ecritClefSimple(marshaller, champ, virgule);
			ecritBrut(marshaller, acces.getBoolean(obj) ? "true" : "false");
			return true;
		default:
			return false;
		}
	}

	private void ecritClefSimple(final Marshaller marshaller, final Champ champ, final boolean virgule)
			throws IOException {
		if (virgule)
			writeSeparator(marshaller);
		ecritClef(marshaller, champ);
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Class<?> typeObj = obj.getClass();
		final List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		final Champ champId = TypeExtension.getChampId(typeObj);
		final boolean serialiseTout = serialiseTout(marshaller, obj, fieldInformations);
		setDejaVu(marshaller, obj);
		if (ecritureDirecte(marshaller)) {
			// écriture récursive : mêmes champs, dans le même ordre, sans comportement empilé
			final Map<Object, UUID> dicoObjToFakeId = getDicoObjToFakeId(marshaller);
			final EntityManager entityManager = getEntityManager(marshaller);
			if (!serialiseTout) {
				final Object id = champId.get(obj, dicoObjToFakeId, entityManager);
				if (aTraiter(id, champId))
					ecritDirect(marshaller, id, champId, ecrisSeparateur);
				return;
			}
			setDejaTotalementSerialise(marshaller, obj);
			boolean virgule = ecrisSeparateur;
			for (final Champ champ : champs) {
				if (!ecritSimple(marshaller, obj, champ, virgule, dicoObjToFakeId, entityManager)) {
					final Object valeur = champ.get(obj, dicoObjToFakeId, entityManager);
					if (aTraiter(valeur, champ))
						ecritDirect(marshaller, valeur, champ, virgule);
				}
				virgule = true;
			}
			return;
		}
		if (!serialiseTout) {
			pushComportement(marshaller, traiteChamp(marshaller, obj, champId, ecrisSeparateur));
			return;
		}
		setDejaTotalementSerialise(marshaller, obj);
		final Deque<Comportement> tmp = new ArrayDeque<>();
		boolean virgule = ecrisSeparateur;
		for (final Champ champ : champs) {
			final Comportement comportement = traiteChamp(marshaller, obj, champ, virgule);
			virgule = true;
			if (comportement != null)
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}
}
