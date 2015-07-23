package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJson<T> {
	
	Class<?> _type;
	
	@Override
	protected Class<?> getType() {
		if(_type != null) return _type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1) _type = ArrayList.class;
		else _type = type;
		return _type;
	}

	public ActionJsonCollectionType(Class<T> type, JsonMarshaller jsonM, String nomClef) {
		super(type,jsonM, nomClef);
	}
	
	@Override
	protected boolean isTypeConnu() {
		return getType() == ArrayList.class;
	}
	
	@Override
	protected void ouvreAccolade() throws IOException {
		if(isTypeConnu()){
			write("[");
		}else{//type inconnu pour deserialisation
			write("{");
			ecritType();
			writeSeparator();
			ecritClef("valeur");
			write("[");
		}
		
	}
	@Override
	protected void fermeAccolade(T obj) throws IOException {
		if(isTypeConnu()){
			write("]");
		}else{
			write("]}");
		}
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		int i=0;
		for (Object value : obj) {
			if(i++ > 0) write(",");
			marshallValue(value, null, relation);
		}
	}
}
