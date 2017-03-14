package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.TypeExtension;
import utils.champ.Champ;
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
		pushComportementParticulier(marshaller, (T)obj, nomBalise, fieldInformations);
	}

	protected void pushComportementParticulier(Marshaller marshaller, T obj ,String nomBalise, FieldInformations fieldInformations){
		pushComportement(marshaller, new ComportementOuvreBaliseEcritValeurEtFermeBalise(obj, nomBalise, fieldInformations));
	}
	
	protected boolean serialiseraTout(Marshaller marshaller, Object obj, FieldInformations fieldInformations){
		return strategieSerialiseraTout(marshaller, fieldInformations)
				&& !isDejaTotalementSerialise(marshaller, obj);
	}

	private boolean strategieSerialiseraTout(Marshaller marshaller, FieldInformations fieldInformations) {
		return getStrategie(marshaller).serialiseTout(getProfondeur(marshaller) + 1, fieldInformations);
	}

	protected abstract void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations, boolean serialiseTout) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption;

	
	protected void ouvreBalise(Marshaller marshaller, T obj, String nomBalise, boolean typeDevinable) throws IOException{
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
	
	protected class ComportementOuvreBaliseEcritValeurEtFermeBalise extends Comportement{
		private T obj;
		private String nomBalise;
		private FieldInformations fieldInformations;
		
		protected ComportementOuvreBaliseEcritValeurEtFermeBalise(T obj, String nomBalise, FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
			ecritValeur(marshaller, obj, fieldInformations, true);
			fermeBalise(marshaller, nomBalise);
		}
		
	}
	
	protected Comportement newComportementOuvreBaliseEtEcritValeur(T obj, String nomBalise, FieldInformations fieldInformations) {
		return new ComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations);
	}
	protected class ComportementOuvreBaliseEtEcritValeur extends Comportement{
		private T obj;
		private String nomBalise;
		private FieldInformations fieldInformations;
		
		protected ComportementOuvreBaliseEtEcritValeur(T obj, String nomBalise, FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
			ecritValeur(marshaller, obj, fieldInformations, true);
		}
		
	}
	

	protected Comportement newComportementOuvreEtFermeBalise(T obj, String nomBalise, FieldInformations fieldInformations){
		return new ComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations);
	}
	protected class ComportementOuvreEtFermeBalise extends Comportement{

		private T obj;
		private String nomBalise;
		private FieldInformations fieldInformations;
		public ComportementOuvreEtFermeBalise(T obj, String nomBalise, FieldInformations fieldInformations) {
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}
		@Override
		protected void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
			getXmlMarshaller(marshaller).openTagAddIdIfNotNullAndCloseTag(nomBalise, classeAEcrire, null);
		}
	
	}
	
	protected Comportement newComportementFermeBalise(String nomBalise) {
		return new ComportementFermeBalise(nomBalise);
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
	
	protected Comportement newComportementSerialiseObject(T obj, String nomBalise, FieldInformations fieldInformations) {
		return new ComportementSerialiseObject(obj, nomBalise, fieldInformations);
	}
	
	protected class ComportementSerialiseObject extends Comportement{
		private T obj;
		private String nomBalise;
		private FieldInformations fieldInformations;
		
		protected ComportementSerialiseObject(T obj, String nomBalise, FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.fieldInformations = fieldInformations;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			boolean serialiseraTout = serialiseraTout(marshaller, obj, fieldInformations);
			boolean isComportementIdDansBalise = !serialiseraTout;
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			setDejaVu(marshaller, obj);
			if (isComportementIdDansBalise){
				Class<?> typeObj = obj.getClass();
				Champ champId = TypeExtension.getChampId(typeObj);
				Object id = champId.get(obj, getDicoObjToFakeId(marshaller), getEntityManager(marshaller));
				if(id == null)
					throw new MarshallExeption("l'objet de type " + typeObj.getName() + " a un id null");
				ouvreBaliseEcritIdFermeBalise(marshaller, obj, nomBalise, typeDevinable,id.toString());
			}else{
				pushComportement(marshaller, newComportementFermeBalise(nomBalise));
				ouvreBalise(marshaller, obj, nomBalise, typeDevinable);
				ecritValeur(marshaller, obj, fieldInformations, serialiseraTout);
			}
			
		}
		
		private void ouvreBaliseEcritIdFermeBalise(Marshaller marshaller, T obj, String nomBalise, boolean typeDevinable, String id) throws IOException{
			Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
			getXmlMarshaller(marshaller).openTagAddIdIfNotNullAndCloseTag(nomBalise, classeAEcrire,id);
		}
		
	}

}
