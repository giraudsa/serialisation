package giraudsa.marshall.serialisation.text.xml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public abstract class ActionXml<T> extends ActionText<T> {
	protected class ComportementFermeBalise extends Comportement {

		private final String nomBalise;

		protected ComportementFermeBalise(final String nomBalise) {
			super();
			this.nomBalise = nomBalise;
		}

		@Override
		protected void evalue(final Marshaller marshaller) throws IOException {
			fermeBalise(marshaller, nomBalise);
		}

	}

	protected class ComportementOuvreBaliseEcritValeurEtFermeBalise extends Comportement {
		private final FieldInformations fieldInformations;
		private final String nomBalise;
		private final T obj;

		protected ComportementOuvreBaliseEcritValeurEtFermeBalise(final T obj, final String nomBalise,
				final FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
			ecritValeur(marshaller, obj, fieldInformations, true);
			fermeBalise(marshaller, nomBalise);
		}

	}

	protected class ComportementOuvreBaliseEtEcritValeur extends Comportement {
		private final FieldInformations fieldInformations;
		private final String nomBalise;
		private final T obj;

		protected ComportementOuvreBaliseEtEcritValeur(final T obj, final String nomBalise,
				final FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
			ecritValeur(marshaller, obj, fieldInformations, true);
		}

	}

	protected class ComportementOuvreEtFermeBalise extends Comportement {

		private final FieldInformations fieldInformations;
		private final String nomBalise;
		private final T obj;

		public ComportementOuvreEtFermeBalise(final T obj, final String nomBalise,
				final FieldInformations fieldInformations) {
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			final Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
			getXmlMarshaller(marshaller).openTagAddIdIfNotNullAndCloseTag(nomBalise, classeAEcrire, null);
		}

	}

	protected class ComportementSerialiseObject extends Comportement {
		private final FieldInformations fieldInformations;
		private final String nomBalise;
		private final T obj;

		protected ComportementSerialiseObject(final T obj, final String nomBalise,
				final FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			final boolean serialiseraTout = serialiseraTout(marshaller, obj, fieldInformations);
			final boolean isComportementIdDansBalise = !serialiseraTout;
			final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			setDejaVu(marshaller, obj);
			if (isComportementIdDansBalise) {
				final Class<?> typeObj = obj.getClass();
				final Champ champId = TypeExtension.getChampId(typeObj);
				final Object id = champId.get(obj, getDicoObjToFakeId(marshaller), getEntityManager(marshaller));
				if (id == null)
					throw new MarshallExeption("l'objet de type " + typeObj.getName() + " a un id null");
				ouvreBaliseEcritIdFermeBalise(marshaller, obj, nomBalise, typeDevinable, id.toString());
			} else {
				pushComportement(marshaller, newComportementFermeBalise(nomBalise));
				ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
				ecritValeur(marshaller, obj, fieldInformations, serialiseraTout);
			}

		}

		private void ouvreBaliseEcritIdFermeBalise(final Marshaller marshaller, final T obj, final String nomBalise,
				final boolean typeDevinable, final String id) throws IOException {
			final Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
			getXmlMarshaller(marshaller).openTagAddIdIfNotNullAndCloseTag(nomBalise, classeAEcrire, id);
		}

	}

	private static final Map<Character, String> REMPLACEMENT_CHARS;

	static {
		final Map<Character, String> tmp = new HashMap<>();
		for (char c = 0; c <= 0x1F; c++)
			if (c != '\t' && c != '\n' && c != '\r')
				tmp.put(c, "\uFFFD");
		tmp.put('&', "&amp;");
		tmp.put('<', "&lt;");
		tmp.put('>', "&gt;");
		REMPLACEMENT_CHARS = Collections.unmodifiableMap(tmp);
	}

	public ActionXml() {
		super();
	}

	private Class<?> classeAEcrire(final T obj, final boolean typeDevinable) {
		return !typeDevinable ? getType(obj) : null;
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations,
			boolean serialiseTout) throws IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption;

	private void fermeBalise(final Marshaller marshaller, final String nomBalise) throws IOException {
		getXmlMarshaller(marshaller).closeTag(nomBalise);
	}

	@Override
	protected Map<Character, String> getRemplacementChar() {
		return REMPLACEMENT_CHARS;
	}

	protected XmlMarshaller getXmlMarshaller(final Marshaller marshaller) {
		return (XmlMarshaller) marshaller;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations) {
		String nomBalise = fieldInformations.getName();
		if (nomBalise == null)
			nomBalise = getType((T) obj).getSimpleName();
		pushComportementParticulier(marshaller, (T) obj, nomBalise, fieldInformations);
	}

	protected Comportement newComportementFermeBalise(final String nomBalise) {
		return new ComportementFermeBalise(nomBalise);
	}

	protected Comportement newComportementOuvreBaliseEtEcritValeur(final T obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		return new ComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations);
	}

	protected Comportement newComportementOuvreEtFermeBalise(final T obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		return new ComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations);
	}

	protected Comportement newComportementSerialiseObject(final T obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		return new ComportementSerialiseObject(obj, nomBalise, fieldInformations);
	}

	protected void ouvreBalise(final Marshaller marshaller, final T obj, final String nomBalise,
			final boolean typeDevinable) throws IOException {
		final Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
		getXmlMarshaller(marshaller).openTag(nomBalise, classeAEcrire);
	}

	protected void pushComportementParticulier(final Marshaller marshaller, final T obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		pushComportement(marshaller,
				new ComportementOuvreBaliseEcritValeurEtFermeBalise(obj, nomBalise, fieldInformations));
	}

	protected boolean serialiseraTout(final Marshaller marshaller, final Object obj,
			final FieldInformations fieldInformations) {
		return strategieSerialiseraTout(marshaller, fieldInformations) && !isDejaTotalementSerialise(marshaller, obj);
	}

	private boolean strategieSerialiseraTout(final Marshaller marshaller, final FieldInformations fieldInformations) {
		return getStrategie(marshaller).serialiseTout(getProfondeur(marshaller) + 1, fieldInformations);
	}

}
