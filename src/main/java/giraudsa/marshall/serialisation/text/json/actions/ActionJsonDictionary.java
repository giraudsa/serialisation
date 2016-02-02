package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
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
	
	public ActionJsonDictionary() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Map obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NotImplementedSerializeException, IOException{
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
		Deque<Comportement> tmp = new ArrayDeque<Comportement>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey, ecrisSeparateur));
			ecrisSeparateur = true;
			tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
		}
		pushComportements(marshaller, tmp);//on remet dans le bon ordre
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, Map obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			ouvreCrochet(marshaller);
		}else{
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			ouvreCrochet(marshaller);
		}
		return false;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, Map obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			fermeCrochet(marshaller);
		}else{
			fermeCrochet(marshaller);
			fermeAccolade(marshaller);
		}
	}
}
