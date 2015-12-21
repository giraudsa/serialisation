package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType extends ActionBinary<Map> {

	public ActionBinaryDictionaryType(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Map map, TypeRelation typeRelation) throws IOException {
		Stack<Comportement> tmp = new Stack<>();
		if(!isDejaTotalementSerialise(map)){
			setDejaTotalementSerialise(map);
			writeInt(map.size());
			for (Object entry : map.entrySet()) {
				tmp.push(new ComportementMarshallValue(((Entry)entry).getKey(), typeRelation, false));
				tmp.push(new ComportementMarshallValue(((Entry)entry).getValue(), typeRelation, false));
			}
		}else if(!isCompleteMarshalling() && typeRelation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object entry : map.entrySet()){
				tmp.push(new ComportementMarshallValue(((Entry)entry).getKey(), typeRelation, false));
				tmp.push(new ComportementMarshallValue(((Entry)entry).getValue(), typeRelation, false));
			}
		}
	}
}
