package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollection<C extends Collection> extends ActionBinary<C> {

	private boolean deserialisationFini = false;
	private int tailleCollection;
	private int index = 0;
	
	public static ActionAbstrait<?> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryCollection<>(Collection.class, bu);
	}
	
	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryCollection<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	private ActionBinaryCollection(Class<C> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}
	
	

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		if (isDejaVu() && !isDeserialisationComplete() && relation == TypeRelation.COMPOSITION){
			obj = getObjetDejaVu();
			tailleCollection = ((Collection)obj).size();
			deserialisationFini = index < tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjetDejaVu();
		}else if(!isDejaVu()){
			obj = newInstance();
			stockeObjetId();
			tailleCollection = readInt();
			deserialisationFini = index < tailleCollection;
		}
	}

	@Override
	public void deserialisePariellement() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		if(!deserialisationFini){
			litObject(relation, null);
			deserialisationFini = ++index < tailleCollection;
		}else{
			exporteObject();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void integreObject(Object objet) {
		if(((Collection)obj).size() < index) ((Collection)obj).add(objet);
	}

	private Collection newInstance() {
		Collection objetADeserialiser = null;
		try {
			if(type == ArrayList.class) objetADeserialiser = new ArrayList();
			else if(type == LinkedList.class) objetADeserialiser = new LinkedList();
			else if(type.getName().indexOf("ArrayList") != -1) objetADeserialiser = new ArrayList();
			else if(type == HashSet.class) objetADeserialiser = new HashSet();
			else if(type.getName().toLowerCase().indexOf("hibernate") != -1) objetADeserialiser = new ArrayList();
			else objetADeserialiser = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objetADeserialiser;
	}
}
