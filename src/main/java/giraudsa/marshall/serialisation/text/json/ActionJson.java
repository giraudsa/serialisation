package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller.SetQueue;
import giraudsa.marshall.serialisation.text.ActionText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import utils.Constants;

public class ActionJson<T> extends ActionText<T>  {
	
	private final String nomClef;
	protected JsonMarshaller getJsonMarshaller(){
		return (JsonMarshaller)marshaller;
	}
	
	public ActionJson(Class<T> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM);
		this.nomClef = nomClef;
	}

	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		ecritClef(nomClef);
		ouvreAccolade();
		ecritValeur(obj, relation);
		fermeAccolade(obj);
	}
	
	protected void fermeAccolade(T obj) throws IOException {
		write("}");
	}

	protected void ouvreAccolade() throws IOException {
		write("{");
		ecritType();
	}

	protected void ecritClef(String clef) throws IOException{
		getJsonMarshaller().ecritClef(clef);
	}
	protected void ecritType() throws IOException{
		getJsonMarshaller().ecritType(getType());
	}
	
	protected boolean isTypeConnu(){
		return (nomClef != null && (!nomClef.equals(Constants.MAP_CLEF) || !nomClef.equals(Constants.MAP_VALEUR)))
				|| type == Integer.class || type == int.class || type == boolean.class || type == Boolean.class;
	}
	protected void writeWithQuote(String string) throws IOException{
		getJsonMarshaller().writeWithQuote(string);
	}
	
	@Override
	protected void writeSeparator() throws IOException {
		write(",");
	}
}
