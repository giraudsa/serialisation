package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import utils.TypeExtension;
import utils.champ.Champ;


public class ActionBinaryObject extends ActionBinary<Object> {

	public ActionBinaryObject(Class<Object> type, Unmarshaller<?> b){
		super(type, b);
	}

	@Override
	protected Object readObject(Class<? extends Object> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		boolean isDejaVu = isDejaVu(smallId);
		boolean unmarshallAll = (!isDejaVu && deserialisationComplete) || (!deserialisationComplete && typeRelation == TypeRelation.COMPOSITION);
		Champ champId = TypeExtension.getChampId(typeADeserialiser);
		Object objetADeserialiser = getObjet(smallId);
		if(!isDejaVu){
			if(!champId.isFakeId()){
				Object id = litObject(typeRelation, champId.valueType);
				objetADeserialiser = getObject(id.toString(), typeADeserialiser, false);
				champId.set(objetADeserialiser, id);
			}else{
				objetADeserialiser = typeADeserialiser.newInstance();
			}
			stockeObjetId(smallId, objetADeserialiser);
		}
		if(unmarshallAll){
			List<Champ> champs = TypeExtension.getSerializableFields(typeADeserialiser);
			boolean ilResteDesChampsComplexes = false;
			for (Champ champ : champs){
				if (!champ.isFakeId() && champ != champId && champ.isSimple){
					champ.set(objetADeserialiser, litObject(champ.relation, champ.valueType));
				}else if(!champ.isSimple){
					ilResteDesChampsComplexes = true;
					break;
				}
			}
			if(ilResteDesChampsComplexes) pushComportement(objetADeserialiser, typeRelation);
		}
		return objetADeserialiser;
	}
	
	@Override
	public void traiteChampsRestant(Object objetADeserialiser, TypeRelation typeRelation) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		Champ champId =TypeExtension.getChampId(objetADeserialiser.getClass());
		for(Champ champ : TypeExtension.getSerializableFields(objetADeserialiser.getClass())){
			if(champ != champId && !champ.isSimple)
				champ.set(objetADeserialiser, litObject(champ.relation, champ.valueType));
		}
	}

}