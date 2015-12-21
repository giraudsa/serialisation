package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionary extends ActionJson<Map> {
	
	public ActionJsonDictionary(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override
	protected void ecritValeur(Map obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		Stack<Comportement> tmp = new Stack<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(new ComportementMarshallValue(entry.getKey(), null, relation, false, ecrisSeparateur));
			ecrisSeparateur = true;
			tmp.push(new ComportementMarshallValue(entry.getValue(), null, relation, false, ecrisSeparateur));
		}
		pushComportements(tmp);//on remet dans le bon ordre
	}
	
	@Override
	protected boolean ouvreAccolade(Map obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			write("[");
		}else{
			write("{");
			ecritType(obj);
			writeSeparator();
			ecritClef(Constants.VALEUR);
			write("[");	
		}
		return false;
	}
	
	@Override
	protected void fermeAccolade(Map obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			write("]");
		}else{
			write("]}");
		}
	}
}
