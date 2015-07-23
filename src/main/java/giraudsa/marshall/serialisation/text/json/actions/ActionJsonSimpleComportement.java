package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller.SetQueue;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	public ActionJsonSimpleComportement(Class<T> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(StringEscapeUtils.escapeJson(obj.toString()));
	}
	
	@Override
	protected void ouvreAccolade() throws IOException {
		if(!isTypeConnu()){
			write("{");
			ecritType();
			write(",");
			ecritClef("valeur");
		}
	}
	
	@Override
	protected void fermeAccolade(T obj) throws IOException {
		if(!isTypeConnu()){
			write("}");
		}
	}

}
