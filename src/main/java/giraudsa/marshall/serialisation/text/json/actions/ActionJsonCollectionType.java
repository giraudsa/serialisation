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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType extends ActionJson<Collection> {
		
	public ActionJsonCollectionType(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override
	protected Class<?> getType(Collection obj) {
		return (obj.getClass().getName().toLowerCase().indexOf("hibernate") != -1) ? ArrayList.class : obj.getClass();
	}

	@Override
	protected boolean commenceObject(Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable || obj instanceof ArrayList){
			ouvreCrochet();
		}else{//type inconnu pour deserialisation
			ouvreAccolade();
			ecritType(obj);
			writeSeparator();
			ecritClef(Constants.VALEUR);
			ouvreCrochet();
		}
		return false;
	}
	@Override
	protected void clotureObject(Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable || obj instanceof ArrayList){
			fermeCrochet();
		}else{
			fermeCrochet();
			fermeAccolade();
		}
	}

	@Override
	protected void ecritValeur(Collection obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NotImplementedSerializeException, IOException {
		
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fieldInformations.getRelation());
		
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Object value : obj) {
			tmp.push(traiteChamp(value, fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(tmp);//on remet dans l'ordre
	}
}
