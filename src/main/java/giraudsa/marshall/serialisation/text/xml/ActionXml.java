package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlObject;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
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
		pushComportement(marshaller, new ComportementFermeBalise(nomBalise));
		pushComportement(marshaller, new ComportementOuvreBaliseEtEcrisValeur((T)obj, nomBalise, fieldInformations));
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException;

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
	
	@Override
	protected Comportement traiteChamp(Marshaller marshaller, Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, IllegalAccessException {
		Object value = fieldInformations.get(obj);
		if(aTraiter(marshaller, value, fieldInformations)){
			if(marshaller.getAction(value) instanceof ActionXmlObject && serialiseTout(marshaller, obj, fieldInformations) )
				return new ComportementIdDansBalise(value, fieldInformations, ecrisSeparateur);
			return new ComportementMarshallValue(value, fieldInformations, ecrisSeparateur);
		}
		return null;
	}
	
	@Override
	protected Comportement traiteChamp(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		return traiteChamp(marshaller, obj, fieldInformations, true);
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
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
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
	
	protected class ComportementIdDansBalise extends Comportement{

		private Object value;
		private FieldInformations fieldInformations;
		private boolean writeSeparateur;
		
		public ComportementIdDansBalise(Object value, FieldInformations fieldInformations, boolean writeSeparateur) {
			super();
			this.value = value;
			this.fieldInformations = fieldInformations;
			this.writeSeparateur = writeSeparateur;
		}

		@Override
		public void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			if(writeSeparateur)
					writeSeparator(marshaller);
			//TODO : methode ouvreBalise Ecris Id ferme Balsie			
		}
	
	}
	

}
