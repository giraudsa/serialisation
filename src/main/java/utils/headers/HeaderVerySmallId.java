package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderVerySmallId extends Header<Object> {
	private static int maxVerySmallId = 0;
	private static final Map<Integer, HeaderVerySmallId> verySmallIdToHeader = new HashMap<Integer, HeaderVerySmallId>();
	
	private int smallId;
	
	HeaderVerySmallId() {
		smallId = ++maxVerySmallId;
		verySmallIdToHeader.put(smallId, this);
	}
	
	protected static int getMaxVerySmallId(){
		return maxVerySmallId;
	}
	
	protected static Header<?> getHeader(int smallId){
		return verySmallIdToHeader.get(smallId);
	}
	@Override
	public void write(DataOutput output, int smallId, short smallIdType, boolean isDejaVuType, Class<?> type) throws IOException {
		output.writeByte(headerByte);
	}

	@Override
	public int readSmallId(DataInputStream input, int maxId) {
		return smallId;
	}

}
