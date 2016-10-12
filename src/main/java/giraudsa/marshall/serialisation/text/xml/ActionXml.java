package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlObject;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ActionXml<T> extends ActionText<T> {
	private static final Map<Character, String> REMPLACEMENT_CHARS;
	static {
		Map<Character, String> tmp = new HashMap<>();
		for (char c = 0; c <= 0x1F; c++) {
			if (c != '\t' && c != '\n' && c != '\r') {
				tmp.put(c, "\uFFFD");
			}
		}
		tmp.put('&', "&amp;");
		tmp.put('<', "&lt;");
		tmp.put('>', "&gt;");
		REMPLACEMENT_CHARS = Collections.unmodifiableMap(tmp);
	}
	public ActionXml(){
		super();
	}

	protected XmlMarshaller getXmlMarshaller(Marshaller marshaller){
		return (XmlMarshaller)marshaller;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(Marshaller marshaller, Object obj,FieldInformations fieldInformations){
		String nomBalise = fieldInformations.getName();
		if (nomBalise == null) 
			nomBalise = getType((T)obj).getSimpleName();
		if (pushComportementParticulier(marshaller, obj, nomBalise, fieldInformations))
			return;
		pushComportement(marshaller, new ComportementFermeBalise(nomBalise));
		pushComportement(marshaller, new ComportementOuvreBaliseEtEcrisValeur((T)obj, nomBalise, fieldInformations));
	}

	protected boolean pushComportementParticulier(Marshaller marshaller, Object obj ,String nomBalise, FieldInformations fieldInformations){
		return false;
	}
	
	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption;

	protected void ouvreBaliseEcritIdFermeBalise(Marshaller marshaller, T obj, String nomBalise, boolean typeDevinable, String id) throws IOException{
		Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
		getXmlMarshaller(marshaller).openTagAddIdCloseTag(nomBalise, classeAEcrire,id);
	}
	
	private void ouvreBalise(Marshaller marshaller, T obj, String nomBalise, boolean typeDevinable) throws IOException{
		Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
		getXmlMarshaller(marshaller).openTag(nomBalise, classeAEcrire);
	}

	private Class<?> classeAEcrire(T obj, boolean typeDevinable) {
		return !typeDevinable ? getType(obj) : null;
	}

	private void fermeBalise(Marshaller marshaller, String nomBalise) throws IOException{
		getXmlMarshaller(marshaller).closeTag(nomBalise);
	}
	@Override 
	protected Map<Character,String> getRemplacementChar() {
		return REMPLACEMENT_CHARS;
	}
	
	protected class ComportementOuvreBaliseEtEcrisValeur extends Comportement{
		private T obj;
		private String nomBalise;
		private FieldInformations fieldInformations;
		
		protected ComportementOuvreBaliseEtEcrisValeur(T obj, String nomBalise, FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
			ecritValeur(marshaller, obj, fieldInformations);
		}
		
	}
	protected class ComportementFermeBalise extends Comportement{

		private String nomBalise;
		
		protected ComportementFermeBalise(String nomBalise) {
			super();
			this.nomBalise = nomBalise;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException{
			fermeBalise(marshaller, nomBalise);
		}
		
	}

}
