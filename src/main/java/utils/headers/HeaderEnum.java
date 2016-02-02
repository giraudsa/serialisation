package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderEnum extends Header<Enum> {
	
	private static final Map<Integer, HeaderEnum> encodageSmallIdToHeaderEnum = new HashMap<Integer, HeaderEnum>();
	
	protected static void init(){
		new HeaderEnum(0);//type devinable
		new HeaderEnum(1);
		new HeaderEnum(2);
	}
	
	private int encodageSmallIdType;

	public HeaderEnum(int encodageSmallIdType) {
		super();
		this.encodageSmallIdType = encodageSmallIdType;
		encodageSmallIdToHeaderEnum.put(encodageSmallIdType, this);
	}
	
	public static HeaderEnum getHeader(short smallIdType, boolean typeDevinable){
		int encodageSmallIdType = typeDevinable ? 0 : ByteHelper.getMinimumEncodage(smallIdType);
		return encodageSmallIdToHeaderEnum.get(encodageSmallIdType);
	}
	
	public void write(DataOutput output, short smallIdType, Class<?> type, boolean isDejaVuType) throws IOException{
		output.writeByte(headerByte);
		if(encodageSmallIdType > 0){//type non devinable
			output.write(BigInteger.valueOf(smallIdType).toByteArray());
			if(!isDejaVuType)
				output.writeUTF(type.getName());
		}
	}


	@Override
	public int readSmallId(DataInputStream input, int i) throws IOException, UnmarshallExeption {
		return -1;
	}
	
	public boolean isTypeDevinable() {
		return encodageSmallIdType == 0;
	}
	
	@Override
	public short getSmallIdType(DataInputStream input) throws IOException, UnmarshallExeption {
		byte[] tmp = new byte[encodageSmallIdType];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return bi.shortValue();
	}

}
