package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderTypeCourant;

import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate extends ActionBinary<Date> {


	public ActionBinaryDate() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Date date, FieldInformations fieldInformations, boolean isDejaVu) throws IOException{
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, date);
			writeLong(marshaller, date.getTime());
		}
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Date date, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		if(date.getClass() == Date.class){
			boolean isDejaVu = isDejaVuDate(marshaller, date);
			int smallId = getSmallIdDateAndStockDate(marshaller, date);
			HeaderTypeCourant<?> header = HeaderTypeCourant.getHeader(date, smallId);
			header.write(getOutput(marshaller), smallId);
			return isDejaVu;
		}
		Class<?> typeObj = getTypeObjProblemeHibernate(date);
		boolean isDejaVu = isDejaVu(marshaller, date);
		boolean isTypeDevinable = isTypeDevinable(marshaller, date, fieldInformations);
		boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		int smallId = getSmallIdAndStockObj(marshaller, date);
		short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		Header<?> header = Header.getHeader(isDejaVu, isTypeDevinable, smallId, smallIdType);
		header.write(getOutput(marshaller), smallId, smallIdType, isDejaVuType, typeObj);
		return isDejaVu;		
	}	
}
