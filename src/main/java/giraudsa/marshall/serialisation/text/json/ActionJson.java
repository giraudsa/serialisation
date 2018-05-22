package giraudsa.marshall.serialisation.text.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.champ.FieldInformations;

public abstract class ActionJson<T> extends ActionText<T> {
	protected class ComportementEcritClefOuvreAccoladeEtEcrisValeur extends Comportement {
		private final FieldInformations fieldInformations;
		private final String nomClef;
		private final Object obj;
		private final boolean typeDevinable;

		protected ComportementEcritClefOuvreAccoladeEtEcrisValeur(final String nomClef, final boolean typeDevinable,
				final FieldInformations fieldInformations, final Object obj) {
			super();
			this.nomClef = nomClef;
			this.typeDevinable = typeDevinable;
			this.fieldInformations = fieldInformations;
			this.obj = obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			ecritClef(marshaller, nomClef);
			final boolean nePasEcrireType = writeType(marshaller) ? typeDevinable : true;
			final boolean separateurAEcrire = commenceObject(marshaller, (T) obj, nePasEcrireType);
			ecritValeur(marshaller, (T) obj, fieldInformations, separateurAEcrire);
		}
	}

	protected class ComportementFermeAccolade extends Comportement {

		private final Object obj;
		private final boolean typeDevinable;

		protected ComportementFermeAccolade(final Object obj, final boolean typeDevinable) {
			super();
			this.obj = obj;
			this.typeDevinable = typeDevinable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void evalue(final Marshaller marshaller) throws IOException {
			final boolean nePasEcrireType = writeType(marshaller) ? typeDevinable : true;
			clotureObject(marshaller, (T) obj, nePasEcrireType);
		}

	}

	private static final Map<Character, String> REMPLACEMENT_CHARS;

	static {
		final Map<Character, String> t = new HashMap<>();
		for (char c = 0; c <= 0x1F; c++)
			t.put(c, String.format("\\u%04x", (int) c));
		t.put('"', "\\\"");
		t.put('\\', "\\\\");
		t.put('\t', "\\t");
		t.put('\b', "\\b");
		t.put('\n', "\\n");
		t.put('\r', "\\r");
		t.put('\f', "\\f");
		t.put('<', "\\u003c");
		t.put('>', "\\u003e");
		t.put('&', "\\u0026");
		t.put('=', "\\u003d");
		t.put('\'', "\\u0027");
		t.put('\u2028', "\\u2028");
		t.put('\u2029', "\\u2029");
		REMPLACEMENT_CHARS = Collections.unmodifiableMap(t);
	}

	protected ActionJson() {
		super();
	}

	protected abstract void clotureObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException;

	protected abstract boolean commenceObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException;

	protected void ecritClef(final Marshaller marshaller, final String clef) throws IOException {
		getJsonMarshaller(marshaller).ecritClef(clef);
	}

	protected void ecritType(final Marshaller marshaller, final T obj) throws IOException {
		getJsonMarshaller(marshaller).ecritType(getType(obj));
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations,
			boolean ecrisSeparateur) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;

	protected void fermeAccolade(final Marshaller marshaller) throws IOException {
		getJsonMarshaller(marshaller).fermeAccolade();
	}

	protected void fermeCrochet(final Marshaller marshaller, final boolean aLaLigne) throws IOException {
		getJsonMarshaller(marshaller).fermeCrochet(aLaLigne);
	}

	protected JsonMarshaller getJsonMarshaller(final Marshaller marshaller) {
		return (JsonMarshaller) marshaller;
	}

	@Override
	protected Map<Character, String> getRemplacementChar() {
		return REMPLACEMENT_CHARS;
	}

	@Override
	protected void marshall(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations) {
		final String nomClef = fieldInformations.getName();
		final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
		pushComportement(marshaller, new ComportementFermeAccolade(obj, typeDevinable));
		pushComportement(marshaller,
				new ComportementEcritClefOuvreAccoladeEtEcrisValeur(nomClef, typeDevinable, fieldInformations, obj));
	}

	protected void ouvreAccolade(final Marshaller marshaller) throws IOException {
		getJsonMarshaller(marshaller).ouvreAccolade();
	}

	protected void ouvreCrochet(final Marshaller marshaller) throws IOException {
		getJsonMarshaller(marshaller).ouvreCrochet();
	}

	@Override
	protected void writeSeparator(final Marshaller marshaller) throws IOException {
		getJsonMarshaller(marshaller).writeSeparator();
	}

	protected boolean writeType(final Marshaller marshaller) {
		return getJsonMarshaller(marshaller).writeType;
	}

	protected void writeWithQuote(final Marshaller marshaller, final String string) throws IOException {
		getJsonMarshaller(marshaller).writeQuote();
		writeEscape(marshaller, string);
		getJsonMarshaller(marshaller).writeQuote();
	}
}
