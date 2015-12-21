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
import java.util.Iterator;
import java.util.List;
import utils.TypeExtension;
import utils.champ.Champ;


public class ActionBinaryObject<O extends Object> extends ActionBinary<O> {
	private List<Champ> listeChamps = null;
	private Iterator<Champ> iteratorChamp = null;
	private Champ champEnAttente = null;
		
	public static ActionAbstrait<?> getInstance(BinaryUnmarshaller<?> binaryUnmarshaller) {
		return new ActionBinaryObject<>(Object.class, binaryUnmarshaller);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends O> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryObject<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	public ActionBinaryObject(Class<O> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}


	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		boolean isDejaVu = isDejaVu();
		if(isDejaVu) obj = getObjetDejaVu();
		else{
			obj = type.newInstance();
			stockeObjetId();
		}
		Champ champId = TypeExtension.getChampId(type);		
		boolean deserialiseToutSaufId = (isDeserialisationComplete() && ! isDejaTotalementDeSerialise()) ||
				(!isDeserialisationComplete() && relation == TypeRelation.COMPOSITION && !isDejaTotalementDeSerialise());
		boolean deserialiseId = !champId.isFakeId() &&
			((isDeserialisationComplete() && ! isDejaTotalementDeSerialise())||
					(!isDeserialisationComplete() && !isDejaVu));
		
		initialiseListeChamps(deserialiseToutSaufId, deserialiseId);
		if(listeChamps != null){
			iteratorChamp = listeChamps.iterator();
			champEnAttente = iteratorChamp.next();
		}
	}

	private void initialiseListeChamps(boolean deserialiseToutSaufId, boolean deserialiseId) {
		Champ champId = TypeExtension.getChampId(type);
		if(deserialiseId && !deserialiseToutSaufId){
			listeChamps = new ArrayList<>();
			listeChamps.add(champId);
		}else if(deserialiseId && deserialiseToutSaufId){
			listeChamps = TypeExtension.getSerializableFields(type);
		}else if(!deserialiseId && deserialiseToutSaufId){
			listeChamps = new ArrayList<>();
			for(Champ champ : TypeExtension.getSerializableFields(type)){
				if(champ != champId)
					listeChamps.add(champ);
			}
		}
	}

	@Override
	public void deserialisePariellement() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			ClassNotFoundException, IOException, NotImplementedSerializeException {
		if(champEnAttente != null){
			if(champEnAttente != TypeExtension.getChampId(type)) setDejaTotalementDeSerialise();
			litObject(champEnAttente.relation, champEnAttente.valueType);
		}else{
			exporteObject();
		}
	}

	@Override
	public void integreObject(Object objet) throws IllegalArgumentException, IllegalAccessException {
		if(champEnAttente.get(obj) != objet && !champEnAttente.isFakeId())
			champEnAttente.set(obj, objet);
		if (iteratorChamp.hasNext()) champEnAttente = iteratorChamp.next();
		else champEnAttente = null;
	}
}