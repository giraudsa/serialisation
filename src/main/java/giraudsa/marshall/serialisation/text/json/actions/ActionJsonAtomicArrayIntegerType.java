package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonAtomicArrayIntegerType  extends ActionJson<AtomicIntegerArray> {
	
	public ActionJsonAtomicArrayIntegerType() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicIntegerArray array, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		FakeChamp fakeChamp = new FakeChamp("V", Integer.class, fieldInformations.getRelation());
		Stack<Comportement> tmp = new Stack<Comportement>();
		for (int i = 0; i < array.length(); ++i) {
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}
	
	protected boolean commenceObject(Marshaller marshaller, AtomicIntegerArray obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
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

	protected void clotureObject(Marshaller marshaller, AtomicIntegerArray obj, boolean typeDevinable) throws IOException {
		if(typeDevinable){
			fermeCrochet(marshaller);
		}else{
			fermeCrochet(marshaller);
			fermeAccolade(marshaller);
		}
	}
}
