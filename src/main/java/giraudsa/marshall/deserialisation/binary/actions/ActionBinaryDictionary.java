package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionary<Dico extends Map> extends ActionBinary<Dico> {

	private boolean deserialisationFini = false;
	private int tailleCollection;
	private int index = 0;
	private Object clefTampon;
	
	public static ActionAbstrait<?> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryDictionary<>(Map.class, bu);
	}
	

	@Override
	public <U extends Dico> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryDictionary<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	private ActionBinaryDictionary(Class<Dico> type, BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	
	
	
	
	@Override
	protected void initialise() throws InstantiationException, IllegalAccessException, IOException{
		if (isDejaVu() && !isDeserialisationComplete() && relation == TypeRelation.COMPOSITION){
			obj = getObjetDejaVu();
			tailleCollection = ((Map)obj).size();
			deserialisationFini = index < tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjetDejaVu();
		}else if(!isDejaVu()){
			obj = type.newInstance();
			stockeObjetId();
			tailleCollection = readInt();
			deserialisationFini = index >= tailleCollection;
		}
	}

	@Override
	public void deserialisePariellement() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException{
		if(!deserialisationFini){
			litObject(relation, null);
			deserialisationFini = clefTampon != null && ++index >= tailleCollection;
		}else{
			exporteObject();
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public void integreObject(Object objet) {
		if(clefTampon == null) clefTampon = objet;
		else if(((Collection)obj).size() < index){
			((Map)obj).put(clefTampon, objet);
			clefTampon = null;
		}
	}
	
}