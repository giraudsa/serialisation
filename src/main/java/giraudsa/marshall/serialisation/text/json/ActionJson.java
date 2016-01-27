package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ActionJson<T> extends ActionText<T>  {
	private static final Map<Character, String> REMPLACEMENT_CHARS;
	static {
		Map<Character, String> t = new HashMap<>();
		for (char c = 0; c <= 0x1F; c++) {
			t.put(c, String.format("\\u%04x", (int) c));
		}
		t.put('"', "\\\"");
		t.put('\\', "\\\\");
		t.put('\t', "\\t");
		t.put('\b', "\\b");
		t.put('\n', "\\n");
		t.put('\r', "\\r");
		t.put('\f', "\\f");
		t.put('<',"\\u003c");
		t.put('>',"\\u003e");
		t.put('&',"\\u0026");
		t.put('=',"\\u003d");
		t.put('\'',"\\u0027");
		t.put('\u2028', "\\u2028");
		t.put('\u2029', "\\u2029");
		REMPLACEMENT_CHARS = Collections.unmodifiableMap(t);
	}
	

	protected ActionJson() {
		super();
	}

	protected JsonMarshaller getJsonMarshaller(Marshaller marshaller){
		return (JsonMarshaller)marshaller;
	}
	
	@Override protected void marshall(Marshaller marshaller, Object obj, FieldInformations fieldInformations){
		String nomClef = fieldInformations.getName();
		boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
		pushComportement(marshaller, new ComportementFermeAccolade(obj, typeDevinable));
		pushComportement(marshaller, new ComportementEcritClefOuvreAccoladeEtEcrisValeur(nomClef, typeDevinable, fieldInformations, obj));
	}
	
	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException;

	protected abstract boolean commenceObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException;

	protected abstract void clotureObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException;
	
	protected void ouvreAccolade(Marshaller marshaller) throws IOException{
		getJsonMarshaller(marshaller).ouvreAccolade();
	}
	
	protected void fermeAccolade(Marshaller marshaller) throws IOException{
		getJsonMarshaller(marshaller).fermeAccolade();
	}

	protected void ouvreCrochet(Marshaller marshaller) throws IOException{
		getJsonMarshaller(marshaller).ouvreCrochet();
	}
	protected void fermeCrochet(Marshaller marshaller) throws IOException{
		getJsonMarshaller(marshaller).fermeCrochet();
	}
	
	protected void ecritClef(Marshaller marshaller, String clef) throws IOException{
		getJsonMarshaller(marshaller).ecritClef(clef);
	}
	protected void ecritType(Marshaller marshaller, T obj) throws IOException{
		getJsonMarshaller(marshaller).ecritType(getType(obj));
	}

	protected void writeWithQuote(Marshaller marshaller, String string) throws IOException{
		getJsonMarshaller(marshaller).writeQuote();
		writeEscape(marshaller, string);
		getJsonMarshaller(marshaller).writeQuote();
	}
	
	@Override 
	protected Map<Character,String> getRemplacementChar() {
		return REMPLACEMENT_CHARS;
	}
	
	@Override
	protected void writeSeparator(Marshaller marshaller) throws IOException {
		getJsonMarshaller(marshaller).writeSeparator();
	}
	
	protected class ComportementEcritClefOuvreAccoladeEtEcrisValeur extends Comportement{
		private String nomClef;
		private boolean typeDevinable;
		private FieldInformations fieldInformations;
		private Object obj;
		

		protected ComportementEcritClefOuvreAccoladeEtEcrisValeur(String nomClef, boolean typeDevinable,
				FieldInformations fieldInformations, Object obj) {
			super();
			this.nomClef = nomClef;
			this.typeDevinable = typeDevinable;
			this.fieldInformations = fieldInformations;
			this.obj = obj;
		}


		@SuppressWarnings("unchecked")
		@Override
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
			ecritClef(marshaller, nomClef);
			boolean separateurAEcrire = commenceObject(marshaller, (T)obj, typeDevinable);
			ecritValeur(marshaller, (T)obj, fieldInformations, separateurAEcrire);
		}
	}


	protected class ComportementFermeAccolade extends Comportement{

		private Object obj;
		private boolean typeDevinable;
		
		protected ComportementFermeAccolade(Object obj, boolean typeDevinable) {
			super();
			this.obj = obj;
			this.typeDevinable = typeDevinable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void evalue(Marshaller marshaller) throws IOException{
			clotureObject(marshaller, (T)obj, typeDevinable);		
		}
		
	}
}
