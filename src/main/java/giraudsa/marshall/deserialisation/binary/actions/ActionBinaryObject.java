package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.TypeExtension;
import utils.TypeExtension.ChampsDuType;
import utils.champ.AccesChamp;
import utils.champ.Champ;

public class ActionBinaryObject<O extends Object> extends ActionBinary<O> {
	public static ActionAbstrait<Object> getInstance() {
		return new ActionBinaryObject<>(Object.class, null);
	}

	/** Prototype pour une classe donnée : ses champs sont calculés une fois et transmis aux actions recyclées. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ActionBinaryObject<?> prototype(final Class<?> type) {
		final ActionBinaryObject prototype = new ActionBinaryObject(type, null);
		prototype.champsDuType = TypeExtension.getChampsDuType(type);
		prototype.generationChamps = TypeExtension.getGeneration();
		return prototype;
	}

	private Champ champEnAttente = null;
	private Champ champId = null;
	private int indexChamp;
	/** objet créé par cette désérialisation (pas fourni par l'EntityManager) : ses champs sont vierges. */
	private boolean objetNeuf;

	private static final Champ[] AUCUN_CHAMP = new Champ[0];
	private Champ[] listeChamps = null;
	/** l'objet a été marqué totalement désérialisé (avant son premier champ hors id). */
	private boolean marqueTotal;
	private ChampsDuType champsDuType;
	private int generationChamps;

	private ActionBinaryObject(final Class<O> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	private boolean deserialiseId(final boolean isDejaVu) {
		if (champId.isFakeId())
			return false;
		return !isDejaVu;

	}

	@Override
	protected void deserialisePariellement()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		// les valeurs complètes sont lues à la suite ; on rend la main dès qu'un sous-objet est empilé
		while (champEnAttente != null) {
			if (!marqueTotal && champEnAttente != champId) {
				setDejaTotalementDeSerialise();
				marqueTotal = true;
			}
			// champ primitif d'un objet neuf : lu et écrit sans boxing
			if (objetNeuf && champEnAttente.getNaturePrimitive() != AccesChamp.AUCUNE && champEnAttente != champId
					&& litPrimitif(champEnAttente, obj)) {
				champEnAttente = indexChamp < listeChamps.length ? listeChamps[indexChamp++] : null;
				continue;
			}
			final Object valeur = litValeur(champEnAttente);
			if (isEnAttente(valeur))
				return;
			integreChamp(valeur);
		}
		exporteObject();
	}

	private boolean deserialiseToutSaufId() {
		return strategieDeSerialiseTout() && !isDejaTotalementDeSerialise();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends O> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryObject<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws InstanciationException {
		if (champsDuType == null)
			champsDuType = TypeExtension.getChampsDuType(type);
		champId = champsDuType.getChampId();
		final boolean isDejaVu = isDejaVu();
		if (isDejaVu)
			obj = getObjet();
		else if (champId.isFakeId()) {
			obj = newInstanceOfType();
			stockeObjetId();
		}
		final boolean deserialiseToutSaufId = deserialiseToutSaufId();
		final boolean deserialiseId = deserialiseId(isDejaVu);

		initialiseListeChamps(deserialiseToutSaufId, deserialiseId);
		objetNeuf = !isDejaVu && getEntityManager() == null;
		if (listeChamps.length > 0)
			champEnAttente = listeChamps[indexChamp++];
	}

	private void initialiseListeChamps(final boolean deserialiseToutSaufId, final boolean deserialiseId) {
		// l'id d'abord, puis les autres champs : listes précalculées par type
		if (!deserialiseToutSaufId)
			listeChamps = deserialiseId ? champsDuType.getTableauIdSeul() : AUCUN_CHAMP;
		else
			listeChamps = deserialiseId ? champsDuType.getTableauIdEnTete() : champsDuType.getTableauSaufId();
	}

	/** Remet l'action à zéro pour lire un nouvel objet (voir BinaryUnmarshaller.getAction). */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void recycle(final Class nouveauType, final ActionBinaryObject<?> prototype,
			final BinaryUnmarshaller<?> nouvelUnmarshaller) {
		super.recycle(nouveauType, nouvelUnmarshaller);
		champEnAttente = null;
		champId = null;
		indexChamp = 0;
		objetNeuf = false;
		listeChamps = null;
		marqueTotal = false;
		// champs du prototype, sauf si la configuration a changé depuis (ils sont alors recalculés)
		champsDuType = prototype.generationChamps == TypeExtension.getGeneration() ? prototype.champsDuType : null;
	}

	/** Oublie l'objet lu, pour qu'une action gardée en réserve ne retienne pas le graphe. */
	public void nettoie() {
		obj = null;
		fieldInformations = null;
		champEnAttente = null;
		listeChamps = null;
	}

	@Override
	protected void exporteObject() throws IllegalAccessException, EntityManagerImplementationException,
			InstanciationException, SetValueException {
		super.exporteObject();
		// plus rien ne référence cette action : elle peut servir à l'objet suivant
		getBinaryUnmarshaller().libere(this);
	}
	@Override
	protected void integreObjet(final String nom, final Object objet) throws EntityManagerImplementationException,
			InstanciationException, SetValueException, IllegalAccessException {
		integreChamp(objet);
		if (champEnAttente == null)
			exporteObject();
	}

	/** Affecte la valeur au champ en attente et passe au suivant (null après le dernier). */
	private void integreChamp(final Object objet) throws EntityManagerImplementationException,
			InstanciationException, SetValueException, IllegalAccessException {
		if (champEnAttente == champId)
			creeObjet(objet);
		if (objetNeuf) // seul un faux id (ChampUid) a besoin de la table des faux ids
			champEnAttente.affecte(obj, objet, champEnAttente.isFakeId() ? getDicoObjToFakeId() : null);
		else
			integreChampObjetExistant(objet);
		champEnAttente = indexChamp < listeChamps.length ? listeChamps[indexChamp++] : null;
	}

	/** L'id est lu : on obtient l'objet (créé, ou fourni par le cache ou l'EntityManager). */
	private void creeObjet(final Object id) throws EntityManagerImplementationException, InstanciationException {
		obj = getObject(id.toString(), type);
		stockeObjetId();
	}

	/** Un objet fourni par l'EntityManager peut déjà porter la valeur : on ne la réaffecte pas. */
	private void integreChampObjetExistant(final Object objet) throws IllegalAccessException, SetValueException {
		if (!champEnAttente.isFakeId()
				&& champEnAttente.get(obj, getDicoObjToFakeId(), getEntityManager()) != objet)
			champEnAttente.set(obj, objet, getDicoObjToFakeId());
	}}