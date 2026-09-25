package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.EntityManager;
import utils.TypeExtension;
import utils.TypeExtension.ChampsDuType;
import utils.champ.AccesChamp;
import utils.champ.Champ;
import utils.champ.EcrivainChamps;
import utils.champ.GenerateurSerialiseurs;
import utils.champ.FieldInformations;
import utils.io.Primitifs;

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
		if (serialiseToutSaufId && serialiseId) {
			// cas courant (objet neuf, tous ses champs) : écrivain généré pour la classe, s'il existe
			final EcrivainChamps ecrivain = ecrivain(champsDuType, objetASerialiser.getClass());
			if (ecrivain != null) {
				ecrit(ecrivain, objetASerialiser, marshaller, champs);
				empileDifferes(marshaller);
				return;
			}
		}
		final Map<Object, UUID> dicoObjToFakeId = champsDuType.getChampId().isFakeId() ? getDicoObjToFakeId(marshaller)
				: null;
		final EntityManager entityManager = getEntityManager(marshaller);
		for (final Champ champ : champs) {
			final int nature = champ.getNaturePrimitive();
			// champ primitif : sans en-tête ni boxing, s'il peut être écrit tout de suite (sinon, même codage par
			// l'action du type enveloppe)
			if (nature != AccesChamp.AUCUNE && aucuneAttente(marshaller)) {
				Primitifs.ecrit(getOutput(marshaller), nature, champ.getAcces(), objetASerialiser);
				continue;
			}
			final Object valeur = champ.get(objetASerialiser, dicoObjToFakeId, entityManager);
			if (aTraiter(valeur, champ))
				ecritOuDiffere(marshaller, valeur, champ);
		}
		empileDifferes(marshaller);
	}

	/** @return l'écrivain généré pour la classe (créé au premier appel), ou null s'il ne peut pas l'être. */
	public static EcrivainChamps ecrivain(final ChampsDuType champsDuType, final Class<?> type) {
		Object ecrivain = champsDuType.getEcrivainBinaire();
		if (ecrivain == null) {
			ecrivain = GenerateurSerialiseurs.ecrivain(type, champsDuType.getTableauChamps(), BinaryMarshaller.class);
			if (ecrivain == null)
				ecrivain = Boolean.FALSE; // génération impossible : chemin générique
			champsDuType.setEcrivainBinaire(ecrivain);
		}
		return ecrivain instanceof EcrivainChamps ? (EcrivainChamps) ecrivain : null;
	}

	private void ecrit(final EcrivainChamps ecrivain, final Object objet, final Marshaller marshaller,
			final Champ[] champs) throws IOException, NotImplementedSerializeException, MarshallExeption {
		try {
			ecrivain.ecrit(objet, getBinaryMarshaller(marshaller), champs);
		} catch (IOException | NotImplementedSerializeException | MarshallExeption | RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new MarshallExeption(e);
		}
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
