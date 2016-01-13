package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.UUID;

public class ActionBinaryUUID extends ActionBinarySimple<UUID> {

	private ActionBinaryUUID(Class<UUID> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<UUID> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryUUID(UUID.class, bu);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryUUID(UUID.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected void initialise() throws IOException {
		boolean isDejaVu = isDejaVu();
		if(isDejaVu)
			obj = getObjetDejaVu();
		else{
			obj = UUID.fromString(readUTF());
			stockeObjetId();
		}
	}
}
