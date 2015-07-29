package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID(Class<? extends UUID> type, Unmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected UUID readObject(Class<? extends UUID> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		boolean isDejaVu = isDejaVu(smallId);
		if(isDejaVu) return (UUID) getObjet(smallId);
		UUID id = UUID.fromString(readUTF());
		stockeObjetId(smallId, id);
		return id;
	}

	

}
