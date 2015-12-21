package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

import utils.TypeExtension;
import utils.champ.Champ;

public class ActionBinaryObject extends ActionBinary<Object> {



	public ActionBinaryObject(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Object objetASerialiser, TypeRelation relation) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException {
		Stack<Comportement> tmp = new Stack<>();
		Class<?> typeObj = (Class<?>) objetASerialiser.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseToutSaufId = (isCompleteMarshalling() && ! isDejaTotalementSerialise(objetASerialiser)) ||
									(!isCompleteMarshalling() && relation == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(objetASerialiser));
		boolean serialiseId = !champId.isFakeId() &&
								((isCompleteMarshalling() && ! isDejaTotalementSerialise(objetASerialiser))||
										(!isCompleteMarshalling() && !isDejaVu(objetASerialiser)));
		if (serialiseId){
			tmp.push(traiteChamp(objetASerialiser, champId));
			setDejaVu(objetASerialiser);
		}
		if(serialiseToutSaufId){
			setDejaVu(objetASerialiser);
			setDejaTotalementSerialise(objetASerialiser);
			for (Champ champ : champs){
				if (champ != champId){
					Comportement comportement = traiteChamp(objetASerialiser, champ);
					if(comportement != null) tmp.push(comportement);
				}
			}
		}
		pushComportements(tmp);
	}

}
