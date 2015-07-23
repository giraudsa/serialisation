package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionary<T extends Map> extends ActionJson<T> {
	
	@Override
	protected Class<?> getType() {
		return type;
	}
	
	public ActionJsonDictionary(Class<T> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		int i = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			Pair pair = new Pair(entry.getKey(), entry.getValue());
			if(i++ > 0) writeSeparator();
			marshallValue(pair, null, relation);
		}
	}
	
	@Override
	protected void ouvreAccolade() throws IOException {
		write("{");
		ecritType();
		write(",");
		ecritClef("valeur");
		write("[");		
	}
	@Override
	protected void fermeAccolade(T obj) throws IOException {
		if(isTypeConnu()){
			write("]");
		}else{
			write("]}");
		}
	}
}
