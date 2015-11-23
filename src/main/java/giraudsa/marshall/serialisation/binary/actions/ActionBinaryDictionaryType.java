package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType<T extends Map> extends ActionBinary<T> {

	public ActionBinaryDictionaryType(Class<? super T> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			if(!isDejaVu){
					writeInt(((Map) objetASerialiser).size());
				for (Object entry : ((Map)objetASerialiser).entrySet()) {
					Object key = ((Entry)entry).getKey();
					Object value = ((Entry)entry).getValue();
					traiteObject(key, typeRelation, true);
					traiteObject(value, typeRelation, true);
				}
			}else if(!isCompleteMarshalling && typeRelation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
				for(Object entry : ((Map)objetASerialiser).entrySet()){
					traiteObject(((Entry)entry).getKey(), typeRelation, true);
					traiteObject(((Entry)entry).getValue(), typeRelation, true);
				}
			}
		} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NotImplementedSerializeException e) {
			e.printStackTrace();
		}
	}
}
