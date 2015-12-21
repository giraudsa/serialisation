package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.ActionText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionJson<T> extends ActionText<T>  {
	
	protected JsonMarshaller getJsonMarshaller(){
		return (JsonMarshaller)marshaller;
	}
	
	public ActionJson(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override public void marshall(Object obj, TypeRelation relation, String nomClef, boolean typeDevinable) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		pushComportement(new ComportementFermeAccolade(obj, typeDevinable));
		pushComportement(new ComportementEcritClefOuvreAccoladeEtEcrisValeur(nomClef, typeDevinable, relation, obj));
	}
	
	abstract protected void ecritValeur(T obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;

	abstract protected void fermeAccolade(T obj, boolean typeDevinable) throws IOException;

	abstract protected boolean ouvreAccolade(T obj, boolean typeDevinable) throws IOException;

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
		private TypeRelation relation;
		private Object obj;
		

		public ComportementEcritClefOuvreAccoladeEtEcrisValeur(String nomClef, boolean typeDevinable,
				TypeRelation relation, Object obj) {
			super();
			this.nomClef = nomClef;
			this.typeDevinable = typeDevinable;
			this.relation = relation;
			this.obj = obj;
		}


		@SuppressWarnings("unchecked")
		@Override
		public void evalue() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
			ecritClef(nomClef);
			boolean separateurAEcrire = ouvreAccolade((T)obj, typeDevinable);
			ecritValeur((T)obj, relation, separateurAEcrire);
		}
	}


	protected class ComportementFermeAccolade extends Comportement{

		private Object obj;
		private boolean typeDevinable;
		
		public ComportementFermeAccolade(Object obj, boolean typeDevinable) {
			super();
			this.obj = obj;
			this.typeDevinable = typeDevinable;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void evalue() throws IOException{
			fermeAccolade((T)obj, typeDevinable);		
		}
		
	}
}
