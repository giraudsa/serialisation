package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionary extends ActionBinary<Map> {

	public ActionBinaryDictionary(Class<Map> type,Unmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	protected Map readObject(Class<? extends Map> typeADeserialiser, TypeRelation typeRelation, int smallId) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, ClassNotFoundException {
		boolean isDejaVu = isDejaVu(smallId);
		Map objetADeserialiser =  null;
		if(!isDejaVu){
			objetADeserialiser = typeADeserialiser.newInstance();
			stockeObjetId(smallId, objetADeserialiser);
			for(int i = 0 ; i < readInt(); i++){
				Object key = litObject(typeRelation, Object.class);
				Object value = litObject(typeRelation, Object.class);
				objetADeserialiser.put(key, value);
			}
		}else if(!deserialisationComplete && typeRelation == TypeRelation.COMPOSITION){
			objetADeserialiser = (Map) getObjet(smallId);
			for(Object entry : objetADeserialiser.entrySet()){
				litObject(typeRelation, Object.class);
				litObject(typeRelation, Object.class);
			}
		}else{
			objetADeserialiser = (Map) getObjet(smallId);
		}
		return objetADeserialiser;
	}
}