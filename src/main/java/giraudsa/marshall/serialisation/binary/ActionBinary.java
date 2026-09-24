package giraudsa.marshall.serialisation.binary;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.AccesChamp;
import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderTypeCourant;
import utils.io.Primitifs;
import utils.io.SortieBinaire;
import utils.TypeExtension;

public abstract class ActionBinary<T> extends ActionAbstrait<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinary.class);

	protected ActionBinary() {
		super();
	}

	@Override
	protected <V> boolean aTraiter(final V value, final FieldInformations f) {
		return true;
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformation,
			boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;

	/**
	 * Écrit la valeur tout de suite si c'est une feuille et qu'aucune valeur précédente n'est en attente (l'ordre du
	 * flux est préservé), sinon la met en attente. Les valeurs en attente sont empilées par
	 * {@link #empileDifferes(Marshaller)}, à appeler à la fin de {@link #ecritValeur}.
	 */
	@SuppressWarnings("rawtypes")
	protected void ecritOuDiffere(final Marshaller marshaller, final Object valeur,
			final FieldInformations fieldInformations) throws NotImplementedSerializeException, MarshallExeption {
		ecritOuDiffere(getBinaryMarshaller(marshaller), valeur, fieldInformations);
	}

	static void ecritOuDiffere(final BinaryMarshaller binaryMarshaller, final Object valeur,
			final FieldInformations fieldInformations) throws NotImplementedSerializeException, MarshallExeption {
		if (binaryMarshaller.debutAttente < 0) {
			if (valeur != null && valeur.getClass() == String.class) { // cas le plus fréquent : sans aiguillage
				ecritChaine(binaryMarshaller, (String) valeur);
				return;
			}
			final ActionAbstrait action;
			if (valeur != null) {
				// un seul accès au plan de la classe : action, feuille, chemin direct
				final BinaryMarshaller.Plan plan = BinaryMarshaller.plan(valeur);
				if (plan.feuille) {
					marshallAvec(plan.action, binaryMarshaller, valeur, fieldInformations);
					return;
				}
				// objet ou collection neufs : chemin direct (voir BinaryMarshaller.ecritDirect)
				if (binaryMarshaller.recursion < BinaryMarshaller.RECURSION_MAX
						&& binaryMarshaller.ecritDirect(plan, valeur, fieldInformations))
					return;
				action = plan.action != null ? plan.action : binaryMarshaller.getAction(valeur);
			} else
				action = binaryMarshaller.getAction(valeur);
			// feuille, ou sous-objet écrit récursivement tant que la profondeur le permet
			if (action instanceof ActionBinary && (((ActionBinary) action).isFeuille()
					|| binaryMarshaller.recursion < BinaryMarshaller.RECURSION_MAX)) {
				marshallAvec(action, binaryMarshaller, valeur, fieldInformations);
				return;
			}
		}
		binaryMarshaller.differe(valeur, fieldInformations);
	}

	/** Même écriture que ActionBinaryString (en-tête puis, à la première apparition, la chaîne). */
	private static void ecritChaine(final BinaryMarshaller binaryMarshaller, final String chaine)
			throws MarshallExeption {
		try {
			final int id = binaryMarshaller.smallIdString(chaine);
			final boolean isDejaVu = id > 0;
			final int smallId = isDejaVu ? id : -id;
			HeaderTypeCourant.getHeader(chaine, smallId, isDejaVu).write(binaryMarshaller.output, smallId);
			if (!isDejaVu)
				binaryMarshaller.output.writeString(chaine);
		} catch (final IOException e) {
			throw new MarshallExeption(e);
		}
	}

	/**
	 * Valeur d'un type déclaré primitif (champ, élément de tableau) : écrite sans en-tête, le lecteur connaît le type
	 * (voir Primitifs). @return true si c'est le cas et que la valeur est écrite.
	 */
	protected boolean ecritSansEnTeteSiPrimitif(final Marshaller marshaller, final FieldInformations fi,
			final Object valeur) throws IOException {
		final int nature = fi.getNaturePrimitive();
		if (nature == AccesChamp.AUCUNE)
			return false;
		Primitifs.ecritValeur(getOutput(marshaller), nature, valeur);
		return true;
	}

	/** @return true si aucune valeur de l'objet courant n'est en attente : une valeur peut être écrite tout de suite. */
	protected boolean aucuneAttente(final Marshaller marshaller) {
		return getBinaryMarshaller(marshaller).debutAttente < 0;
	}

	/** Remet les valeurs en attente dans leur ordre d'origine sur la pile. */
	protected void empileDifferes(final Marshaller marshaller) {
		getBinaryMarshaller(marshaller).empileDifferes();
	}

	/**
	 * @return true si l'action écrit sa valeur sans rien empiler (types simples, chaînes, dates, BigDecimal...). Les
	 *         actions qui ont des sous-valeurs (objets, collections, maps, tableaux) renvoient false.
	 */
	protected boolean isFeuille() {
		return true;
	}

	protected BinaryMarshaller getBinaryMarshaller(final Marshaller marshaller) {
		return (BinaryMarshaller) marshaller;
	}

	protected SortieBinaire getOutput(final Marshaller marshaller) {
		return getBinaryMarshaller(marshaller).output;
	}

	/** voir {@link BinaryMarshaller#smallIdDate(Date)} : > 0 si déjà vue, opposé du nouveau smallId sinon. */
	protected int smallIdDate(final Marshaller marshaller, final Date date) {
		return getBinaryMarshaller(marshaller).smallIdDate(date);
	}

	protected int smallIdString(final Marshaller marshaller, final String string) {
		return getBinaryMarshaller(marshaller).smallIdString(string);
	}

	protected int smallIdType(final Marshaller marshaller, final Class<?> type) {
		return getBinaryMarshaller(marshaller).smallIdType(type);
	}

	protected int smallIdUUID(final Marshaller marshaller, final UUID uuid) {
		return getBinaryMarshaller(marshaller).smallIdUUID(uuid);
	}

	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		if (object == null)
			return void.class;
		return TypeExtension.getClasseASerialiser(object);
	}

	@Override
	protected <U> boolean isDejaVu(final Marshaller marshaller, final U objet) {
		return getBinaryMarshaller(marshaller).isSmallIdDefined(objet);
	}

	@Override
	protected boolean isTypeDevinable(final Marshaller marshaller, final Object value,
			final FieldInformations fieldInformations) {
		return fieldInformations.isTypeDevinable(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformation) throws MarshallExeption {
		try {
			final boolean isDejaVu = writeHeaders(marshaller, (T) objetASerialiser, fieldInformation);
			if (isFeuille()) {
				// rien à empiler : la profondeur n'a pas à être suivie
				ecritValeur(marshaller, (T) objetASerialiser, fieldInformation, isDejaVu);
				return;
			}
			final BinaryMarshaller binaryMarshaller = getBinaryMarshaller(marshaller);
			augmenteProdondeur(marshaller);
			if (binaryMarshaller.recursion < BinaryMarshaller.RECURSION_MAX) {
				// écriture récursive : les sous-objets sont écrits sur place (même ordre que par la pile)
				final int base = binaryMarshaller.hautPile();
				binaryMarshaller.recursion++;
				try {
					ecritValeur(marshaller, (T) objetASerialiser, fieldInformation, isDejaVu);
					// valeurs mises en attente au-delà de la limite : écrites avant de rendre la main
					binaryMarshaller.videPileJusqua(base);
				} finally {
					binaryMarshaller.recursion--;
				}
				diminueProfondeur(marshaller);
				return;
			}
			binaryMarshaller.empile(null, null); // fin de l'objet : la profondeur diminuera
			ecritValeur(marshaller, (T) objetASerialiser, fieldInformation, isDejaVu);
		} catch (MarshallExeption | IOException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("problème à la sérialisation de l'objet " + objetASerialiser.toString(), e);
			throw new MarshallExeption(e);
		}

	}

	protected void writeBoolean(final Marshaller marshaller, final boolean v) throws IOException {
		getBinaryMarshaller(marshaller).writeBoolean(v);
	}

	protected void writeByte(final Marshaller marshaller, final byte v) throws IOException {
		getBinaryMarshaller(marshaller).writeByte(v);
	}

	protected void writeByteArray(final Marshaller marshaller, final byte[] v) throws IOException {
		getBinaryMarshaller(marshaller).writeByteArray(v);
	}

	protected void writeChar(final Marshaller marshaller, final char v) throws IOException {
		getBinaryMarshaller(marshaller).writeChar(v);
	}

	protected void writeDouble(final Marshaller marshaller, final double v) throws IOException {
		getBinaryMarshaller(marshaller).writeDouble(v);
	}

	protected void writeFloat(final Marshaller marshaller, final float v) throws IOException {
		getBinaryMarshaller(marshaller).writeFloat(v);
	}

	protected boolean writeHeaders(final Marshaller marshaller, final T objetASerialiser,
			final FieldInformations fieldInformations) throws IOException {
		return writeHeadersObjet(marshaller, objetASerialiser, fieldInformations);
	}

	/** En-tête d'un objet avec identité : référence s'il est déjà vu, sinon type (si nécessaire). */
	protected boolean writeHeadersObjet(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations) throws IOException {
		final BinaryMarshaller binaryMarshaller = getBinaryMarshaller(marshaller);
		final SortieBinaire output = binaryMarshaller.output;
		final Class<?> typeObj = getTypeObjProblemeHibernate(objetASerialiser);
		if (TypeExtension.isValeurImmuableBinaire(typeObj)) {
			// valeur sans identité : toujours écrite, sans smallId (même règle à la lecture)
			ecritEnTeteNouveau(marshaller, binaryMarshaller, objetASerialiser, fieldInformations, typeObj, 0);
			return false;
		}
		final int id = binaryMarshaller.smallIdObjet(objetASerialiser);
		if (id > 0) {
			Header.getHeader(true, true, id, (short) 0).write(output, id, (short) 0, true, typeObj);
			return true;
		}
		ecritEnTeteNouveau(marshaller, binaryMarshaller, objetASerialiser, fieldInformations, typeObj, -id);
		return false;
	}

	/**
	 * En-tête d'une première apparition. Un type devinable (celui du champ) n'est pas écrit et ne reçoit pas de
	 * numéro : seuls les types écrits sont numérotés, dans l'ordre (même règle à la lecture).
	 */
	private void ecritEnTeteNouveau(final Marshaller marshaller, final BinaryMarshaller binaryMarshaller,
			final Object objet, final FieldInformations fieldInformations, final Class<?> typeObj, final int smallId)
			throws IOException {
		binaryMarshaller.ecritEnTeteNouveau(isTypeDevinable(marshaller, objet, fieldInformations), typeObj, smallId);
	}

	protected void writeInt(final Marshaller marshaller, final int v) throws IOException {
		getBinaryMarshaller(marshaller).writeInt(v);
	}

	protected void writeLong(final Marshaller marshaller, final long v) throws IOException {
		getBinaryMarshaller(marshaller).writeLong(v);
	}

	protected void writeNull(final Marshaller marshaller) throws IOException {
		writeByte(marshaller, (byte) 0);
	}

	protected void writeShort(final Marshaller marshaller, final short v) throws IOException {
		getBinaryMarshaller(marshaller).writeShort(v);
	}

	protected void writeUTF(final Marshaller marshaller, final String s) throws IOException {
		getBinaryMarshaller(marshaller).writeUTF(s);
	}

	protected void writeVarInt(final Marshaller marshaller, final int v) throws IOException {
		getBinaryMarshaller(marshaller).writeVarInt(v);
	}
}
