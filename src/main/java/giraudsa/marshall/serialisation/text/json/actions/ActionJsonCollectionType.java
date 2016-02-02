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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType extends ActionJson<Collection> {
		
	public ActionJsonCollectionType() {
		super();
	}

	@Override
	protected Class<?> getType(Collection obj) {
		return (obj.getClass().getName().toLowerCase().indexOf("hibernate") != -1) ? ArrayList.class : obj.getClass();
	}

	@Override
	protected boolean commenceObject(Marshaller marshaller, Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable || obj instanceof ArrayList){
			ouvreCrochet(marshaller);
		}else{//type inconnu pour deserialisation
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			ouvreCrochet(marshaller);
		}
		return false;
	}
	@Override
	protected void clotureObject(Marshaller marshaller, Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable || obj instanceof ArrayList){
			fermeCrochet(marshaller);
		}else{
			fermeCrochet(marshaller);
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Collection obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NotImplementedSerializeException, IOException {
		
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fieldInformations.getRelation());
		
		Deque<Comportement> tmp = new ArrayDeque<Comportement>();
		for (Object value : obj) {
			tmp.push(traiteChamp(marshaller, value, fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);//on remet dans l'ordre
	}
}
