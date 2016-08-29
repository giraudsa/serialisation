package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	protected void initialise() throws UnmarshallExeption, IOException{
		if (isDejaVu() && !isDejaTotalementDeSerialise() && strategieDeSerialiseTout()){
			obj = getObjet();
			setDejaTotalementDeSerialise();
			tailleCollection = ((Map)obj).size();
			deserialisationFini = index < tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjet();
		}else if(!isDejaVu()){
			obj = newInstance();
			stockeObjetId();
			if(strategieDeSerialiseTout())
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

	private Object newInstance() throws UnmarshallExeption {
		Map objetADeserialiser = null;
		try {
			if(type == HashMap.class) 
				objetADeserialiser = new HashMap<>();
			else if(type == LinkedHashMap.class)
				objetADeserialiser = new LinkedHashMap<>();
			else if(type.getName().indexOf("HashMap") != -1)
				objetADeserialiser = new HashMap<>();
			else if(type.getName().toLowerCase().indexOf("hibernate") != -1){
				if(fieldInformations.getValueType().isAssignableFrom(ConcurrentHashMap.class))
					objetADeserialiser = new ConcurrentHashMap<>();
				else if(fieldInformations.getValueType().isAssignableFrom(LinkedHashMap.class))
					objetADeserialiser = new LinkedHashMap<>();
				else if(fieldInformations.getValueType().isAssignableFrom(HashMap.class))
					objetADeserialiser = new HashMap<>();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(), new InstantiationException());
			}else
				objetADeserialiser = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new UnmarshallExeption("impossible d'instancier la collection " + type.getName(), e);
		}
		return objetADeserialiser;
	}


	@Override
	public void deserialisePariellement() throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
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
	protected void integreObjet(String name, Object objet) throws IllegalAccessException, EntityManagerImplementationException, InstanciationException, SetValueException{
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