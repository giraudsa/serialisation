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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import utils.TypeExtension;
import utils.champ.Champ;


public class ActionBinaryObject<O extends Object> extends ActionBinary<O> {
	private List<Champ> listeChamps = null;
	private Iterator<Champ> iteratorChamp = null;
	private Champ champEnAttente = null;
	private Champ champId = null;
		
	private ActionBinaryObject(Class<O> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<Object> getInstance() {
		return new ActionBinaryObject<>(Object.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends O> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryObject<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws InstanciationException{
		champId = TypeExtension.getChampId(type);
		boolean isDejaVu = isDejaVu();
		if(isDejaVu) 
			obj = getObjet();
		else if(champId.isFakeId()){
			obj = newInstanceOfType();
			stockeObjetId();
		}
		boolean deserialiseToutSaufId = deserialiseToutSaufId();
		boolean deserialiseId = deserialiseId(isDejaVu);
		
		initialiseListeChamps(deserialiseToutSaufId, deserialiseId);
		if(listeChamps != null && !listeChamps.isEmpty()){
			iteratorChamp = listeChamps.iterator();
			champEnAttente = iteratorChamp.next();
		}
	}

	private boolean deserialiseId(boolean isDejaVu) {
		if(champId.isFakeId())
			return false;
		return !isDejaVu;
			
	}

	private boolean deserialiseToutSaufId() {
		return strategieDeSerialiseTout()
				&& !isDejaTotalementDeSerialise();
	}


	private void initialiseListeChamps(boolean deserialiseToutSaufId, boolean deserialiseId) {
		listeChamps = new ArrayList<>();
		if(deserialiseId)
			listeChamps.add(champId);
		if(deserialiseToutSaufId){
			for(Champ champ : TypeExtension.getSerializableFields(type)){
				if(champ != champId)
					listeChamps.add(champ);
			}
		}
	}

	@Override
	protected void deserialisePariellement() throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException{
		if(champEnAttente != null){
			if(champEnAttente != TypeExtension.getChampId(type))
				setDejaTotalementDeSerialise();
			litObject(champEnAttente);
		}else{
			exporteObject();
		}
	}

	@Override
	protected void integreObjet(String nom, Object objet) throws EntityManagerImplementationException, InstanciationException, SetValueException, IllegalAccessException{
		if(champEnAttente == champId){
			String id = objet.toString();
			obj = getObject(id, type);
			stockeObjetId();
		}
		if(champEnAttente.get(obj, getDicoObjToFakeId()) != objet && !champEnAttente.isFakeId())
			champEnAttente.set(obj, objet, getDicoObjToFakeId());
		if (iteratorChamp.hasNext())
			champEnAttente = iteratorChamp.next();
		else 
			exporteObject();
	}
}