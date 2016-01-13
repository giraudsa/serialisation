package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
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


	public ActionBinaryCollectionType(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Collection obj, FieldInformations fieldInformations) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp(null, genericType, fieldInformations.getRelation());
		
		Deque<Comportement> tmp = new ArrayDeque<>();
		if (!isDejaTotalementSerialise(obj)){
			setDejaTotalementSerialise(obj);
			writeInt(obj.size());
			for (Object value : obj) {
				tmp.push(traiteChamp(value, fakeChamp));
			}
		}else if(!isCompleteMarshalling() && fieldInformations.getRelation() == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object value : obj){
				tmp.push(traiteChamp(value, fakeChamp));
			}
		}
		pushComportements(tmp);
	}
}
