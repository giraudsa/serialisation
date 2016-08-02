package giraudsa.marshall.strategie;

import utils.champ.FieldInformations;

public class StrategieSerialisationComplete extends StrategieDeSerialisation {

	public StrategieSerialisationComplete() {
		
	}

	@Override
	public boolean serialiseTout(int profondeur, FieldInformations fieldInformation) {
		return true;
	}

}
