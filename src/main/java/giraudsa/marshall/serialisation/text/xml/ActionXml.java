package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.ActionText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ActionXml<T> extends ActionText<T> {
	protected XmlMarshaller getXmlMarshaller(){
		return (XmlMarshaller)marshaller;
	}
	
	public ActionXml(XmlMarshaller xmlMarshaller){
		super(xmlMarshaller);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshall(Object obj, TypeRelation relation, String nomBalise, boolean typeDevinable) 
					throws IOException, InstantiationException,
					IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
					SecurityException, NotImplementedSerializeException {
		if (nomBalise == null) nomBalise = getType((T)obj).getSimpleName();
		pushComportement(new ComportementFermeBalise(nomBalise));
		pushComportement(new ComportementOuvreBaliseEtEcrisValeur((T)obj, nomBalise, typeDevinable, relation));
	}

	protected abstract void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;

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
		private boolean typeDevinable;
		private TypeRelation relation;
		
		public ComportementOuvreBaliseEtEcrisValeur(T obj, String nomBalise, boolean typeDevinable,
				TypeRelation relation) {
			super();
			this.obj = obj;
			this.nomBalise = nomBalise;
			this.typeDevinable = typeDevinable;
			this.relation = relation;
		}

		@Override
		public void evalue()
				throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
			ouvreBalise(obj, nomBalise, typeDevinable);
			ecritValeur(obj, relation);
		}
		
	}
	protected class ComportementFermeBalise extends Comportement{

		private String nomBalise;
		
		public ComportementFermeBalise(String nomBalise) {
			super();
			this.nomBalise = nomBalise;
		}

		@Override
		public void evalue()
				throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
			fermeBalise(nomBalise);
		}
		
	}
	

}
