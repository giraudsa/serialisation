package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SmallIdTypeException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionary<D extends Map> extends ActionBinary<D> {

	private boolean deserialisationFini = false;
	private int tailleCollection;
	private int index = 0;
	private Object clefTampon;
	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;
	
	private ActionBinaryDictionary(Class<D> type, BinaryUnmarshaller<?> b) {
		super(type, b);
	}


	public static ActionAbstrait<Map> getInstance(){
		return new ActionBinaryDictionary<>(Map.class, null);
	}
	

	@Override
	public <U extends D> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryDictionary<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws InstantiationException, IllegalAccessException, IOException{
		if (isDejaVu() && !isDeserialisationComplete() && fieldInformations.getRelation() == TypeRelation.COMPOSITION){
			obj = getObjet();
			setDejaTotalementDeSerialise();
			tailleCollection = ((Map)obj).size();
			deserialisationFini = index < tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjet();
		}else if(!isDejaVu()){
			obj = type.newInstance();
			stockeObjetId();
			if(isDeserialisationComplete() || fieldInformations.getRelation() == TypeRelation.COMPOSITION)
				setDejaTotalementDeSerialise();
			tailleCollection = readInt();
			deserialisationFini = index >= tailleCollection;
		}
		Type[] types = fieldInformations.getParametreType();
		Type parametreTypeKey = Object.class;
		Type parametreTypeValue = Object.class;
		if(types.length > 1){
			parametreTypeKey = types[0];
			parametreTypeValue = types[1];
		}
		fakeChampKey = new FakeChamp(null, parametreTypeKey, fieldInformations.getRelation());
		fakeChampValue = new FakeChamp(null, parametreTypeValue, fieldInformations.getRelation());
	}

	@Override
	public void deserialisePariellement() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, SmallIdTypeException, UnmarshallExeption{
		if(!deserialisationFini){
			if(clefTampon == null) 
				litObject(fakeChampKey);
			else{
				litObject(fakeChampValue);
			}
		}else{
			exporteObject();
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(String name, Object objet) throws IllegalAccessException, InstantiationException, UnmarshallExeption {
		if(clefTampon == null) 
			clefTampon = objet;
		else if(((Collection)obj).size() < index){
			((Map)obj).put(clefTampon, objet);
			clefTampon = null;
			deserialisationFini = ++index >= tailleCollection;
		}
		if(deserialisationFini)
			exporteObject();
	}
	
}