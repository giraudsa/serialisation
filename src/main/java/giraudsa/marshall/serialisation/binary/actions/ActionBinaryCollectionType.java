package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType extends ActionBinary<Collection> {


	public ActionBinaryCollectionType(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Collection obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Stack<Comportement> tmp = new Stack<>();
		if (!isDejaTotalementSerialise(obj)){
			setDejaTotalementSerialise(obj);
			writeInt(((Collection)obj).size());
			for (Object value : (Collection)obj) {
				tmp.push(new ComportementMarshallValue(value, relation, false));
			}
		}else if(!isCompleteMarshalling() && relation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object value : (Collection)obj){
				tmp.push(new ComportementMarshallValue(value, relation, false));
			}
		}
		pushComportements(tmp);
	}
}
