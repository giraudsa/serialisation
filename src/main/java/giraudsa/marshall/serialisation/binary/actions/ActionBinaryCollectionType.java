package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType<T extends Collection> extends ActionBinary<T> {


	public ActionBinaryCollectionType(Class<? super T> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			if(!isDejaVu){
				writeInt(((Collection)objetASerialiser).size());
				for (Object value : (Collection)objetASerialiser) {
					traiteObject(value, typeRelation, true);
				}
			}else if (!isCompleteMarshalling && typeRelation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
				for(Object value : (Collection)objetASerialiser){
					traiteObject(value, typeRelation, true);
				}
			}
		} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NotImplementedSerializeException e) {
			e.printStackTrace();
		}
	}
}
