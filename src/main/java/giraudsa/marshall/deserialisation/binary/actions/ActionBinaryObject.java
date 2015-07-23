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


public class ActionBinaryObject<T> extends ActionBinary<T> {

	public ActionBinaryObject(Class<? extends T> type,  TypeRelation relation, T objetPreconstruit, Unmarshaller<?> b) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type, relation, objetPreconstruit, b);
	}
	
	@Override
	protected T readObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		boolean onlyReadId = relation != TypeRelation.COMPOSITION;
		List<Champ> champs = TypeExtension.getSerializableFields(type);
		Champ champId = TypeExtension.getChampId(type);
		if(!onlyReadId){
			if(!champId.isFakeId() && !isDejaVu) champId.set(obj, litObject(relation, champId.valueType));
			for(Champ champ : champs){
				if(!champ.isFakeId() && champ != champId){
					champ.set(obj, litObject(relation, champ.valueType));
				}
			}
		}else if(!isDejaVu){
			if(!champId.isFakeId() && !isDejaVu) champId.set(obj, litObject(relation, champId.valueType));
		}
		return obj;		
	}
}
