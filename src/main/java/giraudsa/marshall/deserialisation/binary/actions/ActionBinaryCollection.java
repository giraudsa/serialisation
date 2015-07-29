package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollection extends ActionBinary<Collection> {

	public ActionBinaryCollection(Class<Collection> type, Unmarshaller<?> b){
		super(type, b);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	protected Collection readObject(Class<? extends Collection> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		boolean isDejaVu = isDejaVu(smallId);
		Collection objetADeserialiser = null;
		if(!isDejaVu){
			objetADeserialiser = newInstance(typeADeserialiser);
			stockeObjetId(smallId, objetADeserialiser);
			int taille = readInt();
			for(int i = 0 ; i < taille; i++){
				objetADeserialiser.add(litObject(typeRelation, Object.class));
			}
		}else if (!deserialisationComplete && typeRelation == TypeRelation.COMPOSITION){
			objetADeserialiser = (Collection)getObjet(smallId);
			for(Object value : objetADeserialiser){
				litObject(typeRelation, Object.class);
			}
		}else{
			objetADeserialiser = (Collection)getObjet(smallId);
		}
		return objetADeserialiser;
	}

	private Collection newInstance(Class<? extends Collection> typeADeserialiser) {
		Collection objetADeserialiser = null;
		try {
			if(typeADeserialiser == ArrayList.class) objetADeserialiser = new ArrayList();
			else if(typeADeserialiser == LinkedList.class) objetADeserialiser = new LinkedList();
			else if(typeADeserialiser.getName().indexOf("ArrayList") != -1) objetADeserialiser = new ArrayList();
			else if(typeADeserialiser == HashSet.class) objetADeserialiser = new HashSet();
			else objetADeserialiser = typeADeserialiser.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objetADeserialiser;
	}


}
