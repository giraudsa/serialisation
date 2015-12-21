package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType extends ActionJson<Collection> {
	
	Class<?> _type;
	
	@Override
	protected Class<?> getType(Collection obj) {
		return (obj.getClass().getName().toLowerCase().indexOf("hibernate") != -1) ? ArrayList.class : obj.getClass();
	}

	public ActionJsonCollectionType(JsonMarshaller jsonM) {
		super(jsonM);
	}
	
	@Override
	protected boolean ouvreAccolade(Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			write("[");
		}else{//type inconnu pour deserialisation
			write("{");
			ecritType(obj);
			writeSeparator();
			ecritClef(Constants.VALEUR);
			write("[");
		}
		return false;
	}
	@Override
	protected void fermeAccolade(Collection obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			write("]");
		}else{
			write("]}");
		}
	}

	@Override
	protected void ecritValeur(Collection obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Stack<Comportement> tmp = new Stack<>();
		for (Object value : obj) {
			tmp.push(new ComportementMarshallValue(value, null, relation, false, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(tmp);//on remet dans l'ordre
	}
}
