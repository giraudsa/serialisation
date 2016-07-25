package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollection<C extends Collection> extends ActionBinary<C> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryCollection.class);
	private boolean deserialisationFini = false;
	private int tailleCollection;
	private int index = 0;
	private FakeChamp fakeChamp;
	
	private ActionBinaryCollection(Class<C> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<?> getInstance(){ // NOSONAR
		return new ActionBinaryCollection<>(Collection.class, null);
	}
	
	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryCollection<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	@Override
	public void deserialisePariellement() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption {
		if(!deserialisationFini){
			litObject(fakeChamp);
		}else{
			exporteObject();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(String nom, Object objet) throws IllegalAccessException, UnmarshallExeption {
		((Collection)obj).add(objet);
		deserialisationFini = ++index >= tailleCollection;
		if(deserialisationFini)
			exporteObject();
	}

	@Override
	protected void initialise() throws UnmarshallExeption, IOException{
		if (isDejaVu() && !isDeserialisationComplete() && fieldInformations.getRelation() == TypeRelation.COMPOSITION){
			obj = getObjet();
			tailleCollection = ((Collection)obj).size();
			setDejaTotalementDeSerialise();
			deserialisationFini = index < tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjet();
		}else{//!isDejaVu
			obj = newInstance();
			stockeObjetId();
			if(isDeserialisationComplete() || fieldInformations.getRelation() == TypeRelation.COMPOSITION)
				setDejaTotalementDeSerialise();
			tailleCollection = readInt();
			deserialisationFini = index >= tailleCollection;
		}
		
		Type[] types = fieldInformations.getParametreType();
		Type parametreType = Object.class;
		if(types.length > 0)
			parametreType = types[0];
		fakeChamp = new FakeChamp(null, parametreType, fieldInformations.getRelation());
	}

	private Collection newInstance() throws UnmarshallExeption {
		Collection objetADeserialiser = null;
		try {
			if(type == ArrayList.class) 
				objetADeserialiser = new ArrayList();
			else if(type == LinkedList.class)
				objetADeserialiser = new LinkedList();
			else if(type.getName().indexOf("ArrayList") != -1)
				objetADeserialiser = new ArrayList();
			else if(type == HashSet.class)
				objetADeserialiser = new HashSet();
			else if(type.getName().toLowerCase().indexOf("hibernate") != -1){
				if(fieldInformations.getValueType().isAssignableFrom(ArrayList.class))
					objetADeserialiser = new ArrayList();
				else if(fieldInformations.getValueType().isAssignableFrom(HashSet.class))
					objetADeserialiser = new HashSet();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(), new InstantiationException());
			}else
				objetADeserialiser = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error("impossible d'instancier la collection " + type.getName(), e);
			throw new UnmarshallExeption("impossible d'instancier la collection " + type.getName(), e);
		}
		return objetADeserialiser;
	}
}
