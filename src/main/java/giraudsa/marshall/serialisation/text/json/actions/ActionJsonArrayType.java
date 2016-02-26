package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Stack;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonArrayType  extends ActionJson<Object> {
	
	public ActionJsonArrayType() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur)
			throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException {
		Type genericType = obj.getClass().getComponentType();
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fieldInformations.getRelation());
		Stack<Comportement> tmp = new Stack<Comportement>();
		for (int i = 0; i < Array.getLength(obj); ++i) {
			tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected boolean commenceObject(Marshaller marshaller, Object obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			ouvreCrochet(marshaller);
		}else{//type inconnu pour deserialisation
			ouvreAccolade(marshaller);
			ecritType(marshaller,obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			ouvreCrochet(marshaller);
		}
		return false;
	}
	@Override
	protected void clotureObject(Marshaller marshaller, Object obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			fermeCrochet(marshaller);
		}else{
			fermeCrochet(marshaller);
			fermeAccolade(marshaller);
		}
	}
}
