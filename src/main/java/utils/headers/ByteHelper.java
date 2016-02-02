package utils.headers;

import java.math.BigInteger;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ByteHelper {
	
	protected static int getMinimumEncodage(Number o){
		if(o == null)
			return 0;
		if(Byte.class.isInstance(o) || Short.class.isInstance(o) || Integer.class.isInstance(o) || Long.class.isInstance(o))
			return o.longValue() == 0 ? 0 : BigInteger.valueOf(o.longValue()).toByteArray().length;
		if (Float.class.isInstance(o))
			return o.floatValue() == 0.0 ? 0 : 4;
		if(Double.class.isInstance(o))
			return o.doubleValue() == 0.0 ? 0 : 8;
		return -1;
	
	}
	
	protected static Object getObject(Class<?> simpleType, BigInteger bi) throws UnmarshallExeption {
		if(simpleType == byte.class)
			return bi.byteValue();
		if(simpleType == short.class)
			return bi.shortValue();
		if(simpleType == int.class)
			return bi.intValue();
		if(simpleType == long.class)
			return bi.longValue();
		return 0;
	}

}
