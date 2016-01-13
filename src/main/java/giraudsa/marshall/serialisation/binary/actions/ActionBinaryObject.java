package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

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



	public ActionBinaryObject(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Object objetASerialiser, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Deque<Comportement> tmp = new ArrayDeque<>();
		
		boolean serialiseToutSaufId = serialiseToutSaufId(objetASerialiser, fieldInformations);
		boolean serialiseId = serialiseId(objetASerialiser);
		
		setDejaVu(objetASerialiser);
		List<Champ> champs = getListeChamp(objetASerialiser, serialiseId, serialiseToutSaufId);
		Champ champId = TypeExtension.getChampId(objetASerialiser.getClass());
		for(Champ champ : champs){
			Comportement comportement = traiteChamp(objetASerialiser, champ);
			if(comportement != null)
				tmp.push(comportement);
			if(champ != champId)
				setDejaTotalementSerialise(objetASerialiser);
		}
		pushComportements(tmp);
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

	private boolean serialiseToutSaufId(Object objetASerialiser, FieldInformations fieldInformations) {
		if (isCompleteMarshalling() && ! isDejaTotalementSerialise(objetASerialiser)) 
			return true;
		return !isCompleteMarshalling() && fieldInformations.getRelation() == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(objetASerialiser);
	}

	private boolean serialiseId(Object objetASerialiser) {
		Class<?> typeObj = (Class<?>) objetASerialiser.getClass();
		if(!TypeExtension.getChampId(typeObj).isFakeId())
			return (isCompleteMarshalling() && ! isDejaTotalementSerialise(objetASerialiser))||
										(!isCompleteMarshalling() && !isDejaVu(objetASerialiser));
		return false;
	}

}
