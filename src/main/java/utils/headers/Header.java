package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public abstract class Header<T>{
	private static byte constructionByte = 0;
	private static final Map<Byte, Header<?>> dicoByteToHeader = new HashMap<Byte, Header<?>>();
	static{
		HeaderSimpleType.init();
		HeaderTypeCourant.init();
		HeaderEnum.init();
		HeaderTypeDevinable.init();
		HeaderTypeNonDevinable.init();
		while(constructionByte != 0){
			new HeaderVerySmallId();
		}
	}
	
	protected byte headerByte;

	protected Header() {
		super();
		headerByte = constructionByte++;
		dicoByteToHeader.put(headerByte, this);
	}
	
	public void write(DataOutput output, int smallId, short smallIdType, boolean isDejaVuType, Class<?> type) throws IOException{
		//A spécifier dans les classes dérivées ad hoc
	}
	
	public void writeValue(DataOutput output, Object o) throws IOException{
		//A spécifier dans les classes dérivées ad hoc
	}
	
	//type autre
	public static Header<?> getHeader(boolean isDejaVu, boolean isTypeDevinable, int smallId, short smallIdType){
		if(isDejaVu){
			return smallId <= HeaderVerySmallId.getMaxVerySmallId() ? HeaderVerySmallId.getHeader(smallId) : HeaderTypeDevinable.getHeader(smallId);
		}else{
			return isTypeDevinable ? HeaderTypeDevinable.getHeader(smallId) : HeaderTypeNonDevinable.getHeader(smallId, smallIdType);
		}
	}

	public static Header<?> getHeader(byte b) {
		return dicoByteToHeader.get(b);
	}

	public abstract int readSmallId(DataInputStream input, int i) throws IOException, UnmarshallExeption;

	public boolean isTypeDevinable() {
		return true;
	}

	public short getSmallIdType(DataInputStream input) throws IOException, UnmarshallExeption {
		return 0;
	}
		
}
