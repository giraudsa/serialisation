package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.exception.MarshallExeption;
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
import java.util.HashSet;
import java.util.TreeSet;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType extends ActionJson<Collection> {
		
	public ActionJsonCollectionType() {
		super();
	}

	@Override
	protected Class<?> getType(Collection obj) {
		Class<?> clazz = obj.getClass();
		
		if(clazz.getName().toLowerCase().indexOf("hibernate") != -1){
			if(obj.getClass().getName().toLowerCase().indexOf("persistentlist") != -1)
				return ArrayList.class;
			if(obj.getClass().getName().toLowerCase().indexOf("persistentbag") != -1)
				return ArrayList.class;
			if(obj.getClass().getName().toLowerCase().indexOf("persistentset") != -1)
				return HashSet.class;
			if(obj.getClass().getName().toLowerCase().indexOf("persistentsortedset") != -1)
				return TreeSet.class;
			else return ArrayList.class;
		}
		return clazz;
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
			fermeCrochet(marshaller, !obj.isEmpty());
		}else{
			fermeCrochet(marshaller, !obj.isEmpty());
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Collection obj, FieldInformations fi, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NotImplementedSerializeException, IOException, MarshallExeption {
		
		Type[] types = fi.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fi.getRelation(), fi.getAnnotations());
		
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Object value : obj) {
			tmp.push(traiteChamp(marshaller, value, fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);//on remet dans l'ordre
	}
}
