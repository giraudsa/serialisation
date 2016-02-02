package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.BiHashMap;

public class HeaderTypeNonDevinable extends Header<Object>{
	private static final BiHashMap<Integer, Integer, HeaderTypeNonDevinable> encodageSmallIdEtSmallIdTypeToHeader = new BiHashMap<Integer, Integer, HeaderTypeNonDevinable>();
	protected static void init(){
		new HeaderTypeNonDevinable(1, 1);
		new HeaderTypeNonDevinable(2, 1);
		new HeaderTypeNonDevinable(3, 1);
		new HeaderTypeNonDevinable(4, 1);
		new HeaderTypeNonDevinable(1, 2);
		new HeaderTypeNonDevinable(2, 2);
		new HeaderTypeNonDevinable(3, 2);
		new HeaderTypeNonDevinable(4, 2);
	}
	
	private int encodageSmallId;
	private int encodageSmallIdType;
	public HeaderTypeNonDevinable(int encodageSmallId, int encodageSmallIdType) {
		super();
		this.encodageSmallId = encodageSmallId;
		this.encodageSmallIdType = encodageSmallIdType;
		encodageSmallIdEtSmallIdTypeToHeader.put(encodageSmallId, encodageSmallIdType, this);
	}
	
	protected static Header<?> getHeader(int smallId, short smallIdType){
		int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId() ? smallId - HeaderVerySmallId.getMaxVerySmallId() : smallId;
		int encodageSmallId = 0;
		int encodageSmallIdType = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes);
		encodageSmallIdType = ByteHelper.getMinimumEncodage(smallIdType);
		return  encodageSmallIdEtSmallIdTypeToHeader.get(encodageSmallId, encodageSmallIdType);
	}
	
	@Override
	public void write(DataOutput output, int smallId, short smallIdType, boolean isDejaVuType, Class<?> type) throws IOException {
		output.writeByte(headerByte);
		int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId() ? smallId - HeaderVerySmallId.getMaxVerySmallId() : smallId;
		output.write(BigInteger.valueOf(toBeConsideredForNextBytes).toByteArray());
		output.write(BigInteger.valueOf(smallIdType).toByteArray());
		if(!isDejaVuType)
			output.writeUTF(type.getName());
		
	}
	
	@Override
	public int readSmallId(DataInputStream input, int maxId) throws IOException, UnmarshallExeption {
		byte[] tmp = new byte[encodageSmallId];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return maxId >= HeaderVerySmallId.getMaxVerySmallId() ? bi.intValue() + HeaderVerySmallId.getMaxVerySmallId() : bi.intValue();
	}
	
	@Override
	public boolean isTypeDevinable() {
		return false;
	}
	
	@Override
	public short getSmallIdType(DataInputStream input) throws IOException, UnmarshallExeption {
		byte[] tmp = new byte[encodageSmallIdType];
		input.read(tmp);
		BigInteger bi = new BigInteger(tmp);
		return bi.shortValue();
	}

}
