package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.EntityManager;
import utils.TypeExtension;
import utils.TypeExtension.ChampsDuType;
import utils.champ.AccesChamp;
import utils.champ.Champ;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryObject extends ActionBinary<Object> {
	private static final Champ[] AUCUN_CHAMP = new Champ[0];

	public ActionBinaryObject() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final ChampsDuType champsDuType = TypeExtension.getChampsDuType(objetASerialiser.getClass());
		final boolean serialiseToutSaufId = serialiseToutSaufId(marshaller, objetASerialiser, fieldInformations,
				isDejaVu);
		final boolean serialiseId = !champsDuType.getChampId().isFakeId() && !isDejaVu;

		if (serialiseToutSaufId)
			setDejaTotalementSerialise(marshaller, objetASerialiser);

		final Champ[] champs;
		if (serialiseToutSaufId)
			champs = serialiseId ? champsDuType.getTableauChamps() : champsDuType.getTableauSaufId();
		else
			champs = serialiseId ? champsDuType.getTableauIdSeul() : AUCUN_CHAMP;
		// seul un faux id (ChampUid) a besoin de la table des faux ids
		final Map<Object, UUID> dicoObjToFakeId = champsDuType.getChampId().isFakeId() ? getDicoObjToFakeId(marshaller)
				: null;
		final EntityManager entityManager = getEntityManager(marshaller);
		for (final Champ champ : champs) {
			final int nature = champ.getNaturePrimitive();
			// champ primitif (hors byte, écrit sans en-tête) : sans boxing, s'il peut être écrit tout de suite
			if (nature != AccesChamp.AUCUNE && nature != AccesChamp.BYTE && aucuneAttente(marshaller)) {
				HeaderSimpleType.ecritPrimitif(getOutput(marshaller), champ.getAcces(), nature, objetASerialiser);
				continue;
			}
			final Object valeur = champ.get(objetASerialiser, dicoObjToFakeId, entityManager);
			if (aTraiter(valeur, champ))
				ecritOuDiffere(marshaller, valeur, champ);
		}
		empileDifferes(marshaller);
	}

	@Override
	protected boolean isFeuille() {
		return false;
	}

	private boolean serialiseToutSaufId(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) {
		// un objet vu pour la première fois n'a pas encore été sérialisé
		return strategieSerialiseTout(marshaller, fieldInformations)
				&& (!isDejaVu || !isDejaTotalementSerialise(marshaller, objetASerialiser));
	}

}
