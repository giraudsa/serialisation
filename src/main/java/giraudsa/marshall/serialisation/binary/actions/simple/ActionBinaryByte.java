package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryByte extends ActionBinarySimple<Byte> {

	
	public ActionBinaryByte(Class<? super Byte> type, BinaryMarshaller b) {
		super(type, b);
	}
	
	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific){
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeByte((byte)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
