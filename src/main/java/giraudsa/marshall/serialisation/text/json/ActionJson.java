package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.ActionText;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionJson<T> extends ActionText<T>  {
	
	protected ActionJson(JsonMarshaller jsonM) {
		super(jsonM);
	}

	protected JsonMarshaller getJsonMarshaller(){
		return (JsonMarshaller)marshaller;
	}
	
	@Override protected void marshall(Object obj, FieldInformations fieldInformations){
		String nomClef = fieldInformations.getName();
		boolean typeDevinable = isTypeDevinable(obj, fieldInformations);
		pushComportement(new ComportementFermeAccolade(obj, typeDevinable));
		pushComportement(new ComportementEcritClefOuvreAccoladeEtEcrisValeur(nomClef, typeDevinable, fieldInformations, obj));
	}
	
	protected abstract void ecritValeur(T obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException;

	protected abstract void fermeAccolade(T obj, boolean typeDevinable) throws IOException;

	protected abstract boolean ouvreAccolade(T obj, boolean typeDevinable) throws IOException;

	protected void ecritClef(String clef) throws IOException{
		getJsonMarshaller().ecritClef(clef);
	}
	protected void ecritType(T obj) throws IOException{
		getJsonMarshaller().ecritType(getType(obj));
	}
	
	protected void writeWithQuote(String string) throws IOException{
		getJsonMarshaller().writeWithQuote(string);
	}
	
	@Override
	protected void writeSeparator() throws IOException {
		getJsonMarshaller().writeSeparator();
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
		protected void evalue() throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
			ecritClef(nomClef);
			boolean separateurAEcrire = ouvreAccolade((T)obj, typeDevinable);
			ecritValeur((T)obj, fieldInformations, separateurAEcrire);
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
		protected void evalue() throws IOException{
			fermeAccolade((T)obj, typeDevinable);		
		}
		
	}
}
