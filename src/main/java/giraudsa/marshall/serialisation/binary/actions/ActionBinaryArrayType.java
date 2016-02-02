package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ActionBinaryArrayType extends ActionBinary<Object> {


	public ActionBinaryArrayType() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations, boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		FakeChamp fakeChamp = new FakeChamp(null, obj.getClass().getComponentType(), fieldInformations.getRelation());
		Deque<Comportement> tmp = new ArrayDeque<Comportement>();
		if (!isDejaVu){
			if(isCompleteMarshalling(marshaller) || fieldInformations.getRelation()==TypeRelation.COMPOSITION)
				setDejaTotalementSerialise(marshaller, obj);
			int size = Array.getLength(obj);
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++) {
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
			}
		}else if(!isCompleteMarshalling(marshaller) && fieldInformations.getRelation() == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			setDejaTotalementSerialise(marshaller, obj);
			for (int i = 0; i < Array.getLength(obj); i++) {
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
			}
		}
		pushComportements(marshaller, tmp);
	}
}
