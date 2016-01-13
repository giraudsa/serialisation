package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionXml<T> extends ActionText<T> {
	public ActionXml(XmlMarshaller xmlMarshaller){
		super(xmlMarshaller);
	}

	protected XmlMarshaller getXmlMarshaller(){
		return (XmlMarshaller)marshaller;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(Object obj,FieldInformations fieldInformations){
		String nomBalise = fieldInformations.getName();
		if (nomBalise == null) 
			nomBalise = getType((T)obj).getSimpleName();
		pushComportement(new ComportementFermeBalise(nomBalise));
		pushComportement(new ComportementOuvreBaliseEtEcrisValeur((T)obj, nomBalise, fieldInformations));
	}

	protected abstract void ecritValeur(T obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException;

	private void ouvreBalise(T obj, String nomBalise, boolean typeDevinable) throws IOException{
		Class<?> classeAEcrire = classeAEcrire(obj, typeDevinable);
		getXmlMarshaller().openTag(nomBalise, classeAEcrire);
	}


	private Class<?> classeAEcrire(T obj, boolean typeDevinable) {
		return !typeDevinable ? getType(obj) : null;
	}

	private void fermeBalise(String nomBalise) throws IOException{
		getXmlMarshaller().closeTag(nomBalise);
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
		protected void evalue() throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
			boolean typeDevinable = isTypeDevinable(obj, fieldInformations);
			ouvreBalise(obj, nomBalise, typeDevinable);
			ecritValeur(obj, fieldInformations);
		}
		
	}
	protected class ComportementFermeBalise extends Comportement{

		private String nomBalise;
		
		protected ComportementFermeBalise(String nomBalise) {
			super();
			this.nomBalise = nomBalise;
		}

		@Override
		protected void evalue() throws IOException{
			fermeBalise(nomBalise);
		}
		
	}
	

}
