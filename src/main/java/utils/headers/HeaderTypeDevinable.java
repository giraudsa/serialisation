package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderTypeDevinable extends Header<Object> {
	private static final Map<Integer, HeaderTypeDevinable> encodageSmallIdToHeaderTypeDevinable = new HashMap<>();
	protected static void init(){
		new HeaderTypeDevinable(1);
		new HeaderTypeDevinable(2);
		new HeaderTypeDevinable(3);
		new HeaderTypeDevinable(4);
	}
	private int encodageSmallId;

	private HeaderTypeDevinable(int encodageSmallId) {
		super();
		this.encodageSmallId = encodageSmallId;
		encodageSmallIdToHeaderTypeDevinable.put(encodageSmallId, this);
	}
	
	protected static Header<?> getHeader(int smallId){
		int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId() ? smallId - HeaderVerySmallId.getMaxVerySmallId() : smallId;
		int encodageSmallId = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes);
		return  encodageSmallIdToHeaderTypeDevinable.get(encodageSmallId);
	}
	
	@Override
	public void write(DataOutput output, int smallId, short smallIdType, boolean isDejaVuType, Class<?> type) throws IOException {
		output.writeByte(headerByte);
		int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId() ? smallId - HeaderVerySmallId.getMaxVerySmallId() : smallId;
		output.write(BigInteger.valueOf(toBeConsideredForNextBytes).toByteArray());
	}

	@Override
	public int readSmallId(DataInputStream input, int maxId) throws IOException, UnmarshallExeption {
		byte[] tmp = new byte[encodageSmallId];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return maxId >= HeaderVerySmallId.getMaxVerySmallId() ? bi.intValue() + HeaderVerySmallId.getMaxVerySmallId() : bi.intValue();
	}
	

}
