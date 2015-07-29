package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryChar  extends ActionBinarySimple<Character>{

	public ActionBinaryChar(Class<? super Character> type, BinaryMarshaller b) {
		super(type, b);
	}
	
	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeChar((char)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
