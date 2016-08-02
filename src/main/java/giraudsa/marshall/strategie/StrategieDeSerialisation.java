package giraudsa.marshall.strategie;

import utils.champ.FieldInformations;

public abstract class StrategieDeSerialisation {
	
	public abstract boolean serialiseTout(int profondeur, FieldInformations fieldInformation);
}
