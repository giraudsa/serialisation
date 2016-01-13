package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionary extends ActionJson<Map> {
	
	public ActionJsonDictionary(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override
	protected void ecritValeur(Map obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NotImplementedSerializeException, IOException{
		Type[] types = fieldInformations.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if(types != null && types.length > 1){
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		FakeChamp fakeChampKey = new FakeChamp(null, genericTypeKey, fieldInformations.getRelation());
		FakeChamp fakeChampValue = new FakeChamp(null, genericTypeValue, fieldInformations.getRelation());
		
		Map<?,?> map = (Map<?,?>) obj;
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(entry.getKey(), fakeChampKey, ecrisSeparateur));
			ecrisSeparateur = true;
			tmp.push(traiteChamp(entry.getValue(), fakeChampValue));
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
