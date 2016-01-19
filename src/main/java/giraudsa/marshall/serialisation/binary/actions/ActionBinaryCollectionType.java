package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType extends ActionBinary<Collection> {


	public ActionBinaryCollectionType() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Collection obj, FieldInformations fieldInformations) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fieldInformations.getRelation());
		
		Deque<Comportement> tmp = new ArrayDeque<>();
		if (!isDejaTotalementSerialise(marshaller, obj)){
			setDejaTotalementSerialise(marshaller, obj);
			writeInt(marshaller, obj.size());
			for (Object value : obj) {
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
			}
		}else if(!isCompleteMarshalling(marshaller) && fieldInformations.getRelation() == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object value : obj){
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
			}
		}
		pushComportements(marshaller, tmp);
	}
}
