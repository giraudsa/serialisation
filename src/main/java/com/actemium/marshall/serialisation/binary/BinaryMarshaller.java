package com.actemium.marshall.serialisation.binary;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;

public class BinaryMarshaller extends Marshaller{

	@Override
	protected <T> void marshallSpecialise(T value, TypeRelation typeRelation, String nom) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		// TODO Auto-generated method stub
		
	}

}
