package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import utils.TypeExtension;
import utils.champ.Champ;

public class ActionBinaryObject<T> extends ActionBinary<T> {



	public ActionBinaryObject(Class<? super T> type, BinaryMarshaller b) {
		super(type,  b);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		boolean marshallAll = (!isDejaVu && isCompleteMarshalling) || (!isCompleteMarshalling && relation == TypeRelation.COMPOSITION);
		Champ champId = TypeExtension.getChampId(objetASerialiser.getClass());
		if(!champId.isFakeId() && !isDejaVu)
			try {
				traiteChamp((T)objetASerialiser, champId);
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException
					| NotImplementedSerializeException | IOException e) {
				e.printStackTrace();
			}
		if(marshallAll){
			List<Champ> champs = TypeExtension.getSerializableFields(objetASerialiser.getClass());
			boolean ilResteDesChampsComplexes = false;
			try{
				for (Champ champ : champs){
					if (!champ.isFakeId() && champ != champId && champ.isSimple){
						traiteChamp((T)objetASerialiser, champ);
					}else if(!champ.isSimple){
						ilResteDesChampsComplexes = true;
					}
				}
			}catch(IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | IOException | NotImplementedSerializeException e){
				e.printStackTrace();
			}
			if(ilResteDesChampsComplexes) pushComportement(objetASerialiser, typeRelation, couldBeLessSpecific);
		}
	}

	@SuppressWarnings("unchecked") @Override
	protected void traiteChampsComplexes(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific){
		try{
			List<Champ> champs = TypeExtension.getSerializableFields(objetASerialiser.getClass());
			Champ champId = TypeExtension.getChampId(objetASerialiser.getClass());
			for (Champ champ : champs){
				if (champ != champId && !champ.isSimple){
					traiteChamp((T)objetASerialiser, champ);
				}
			}
		}catch(IOException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | NotImplementedSerializeException e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void traiteChamp(T obj, Champ champ) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException, IOException {
		if(champ.get(obj) == null) writeNull();
		else if(champ.isSimple)	traiteObject(champ.get(obj), TypeRelation.COMPOSITION, false);
		else if(champ.valueType == obj.getClass()) traiteObject(obj, champ.relation, false);
		else traiteObject(champ.get(obj), champ.relation, true);
	}
}
