package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionBinaryObject extends ActionBinary<Object> {



	public ActionBinaryObject() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Object objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Deque<Comportement> tmp = new ArrayDeque<>();
		
		boolean serialiseToutSaufId = serialiseToutSaufId(marshaller, objetASerialiser, fieldInformations);
		boolean serialiseId = serialiseId(marshaller, objetASerialiser, isDejaVu);
		
		if(serialiseToutSaufId)
			setDejaTotalementSerialise(marshaller, objetASerialiser);
		
		List<Champ> champs = getListeChamp(objetASerialiser, serialiseId, serialiseToutSaufId);
		for(Champ champ : champs){
			Comportement comportement = traiteChamp(marshaller, objetASerialiser, champ);
			if(comportement != null)
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}

	private List<Champ> getListeChamp(Object objetASerialiser, boolean serialiseId, boolean serialiseToutSaufId) {
		List<Champ> ret = new ArrayList<>();
		Champ champId = TypeExtension.getChampId(objetASerialiser.getClass());
		List<Champ> champs = TypeExtension.getSerializableFields(objetASerialiser.getClass());
		for(Champ champ : champs){
			if(champ == champId && serialiseId || champ != champId && serialiseToutSaufId)
				ret.add(champ);
		}
		return ret;
	}

	private boolean serialiseToutSaufId(Marshaller marshaller, Object objetASerialiser, FieldInformations fieldInformations) {
		if (isCompleteMarshalling(marshaller) && ! isDejaTotalementSerialise(marshaller, objetASerialiser)) 
			return true;
		return !isCompleteMarshalling(marshaller) && fieldInformations.getRelation() == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(marshaller, objetASerialiser);
	}

	private boolean serialiseId(Marshaller marshaller, Object objetASerialiser, boolean isDejaVu) {
		Class<?> typeObj = (Class<?>) objetASerialiser.getClass();
		if(!TypeExtension.getChampId(typeObj).isFakeId())
			return (isCompleteMarshalling(marshaller) && ! isDejaTotalementSerialise(marshaller, objetASerialiser))||
										(!isCompleteMarshalling(marshaller) && !isDejaVu);
		return false;
	}

}
