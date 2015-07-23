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

	public ActionBinaryObject(Class<? super T> type, T obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, relation, isDejaVu, b);
	}

	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {
		boolean onlyWriteId = relation != TypeRelation.COMPOSITION;
		List<Champ> champs = TypeExtension.getSerializableFields(getType());
		Champ champId = TypeExtension.getChampId(getType());
		if(!champId.isFakeId() || !isDejaVu) traiteChamp(obj, champId);
		if(!onlyWriteId){//on ecrit tout
			for (Champ champ : champs){
				if (!champ.isFakeId() && champ != champId){
					traiteChamp(obj, champ);
				}
			}
		}
	}
	
	@Override
	protected void traiteChamp(T obj, Champ champ) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException, IOException {
		if(champ.get(obj) == null) writeNull();
		else if(champ.isSimple)	traiteObject(champ.get(obj), champ.relation, false);
		else if(champ.valueType == obj.getClass()) traiteObject(obj, champ.relation, false);
		else traiteObject(champ.get(obj), relation, true);
	}
}
