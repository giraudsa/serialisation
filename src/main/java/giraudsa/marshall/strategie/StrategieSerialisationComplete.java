package giraudsa.marshall.strategie;

import utils.champ.FieldInformations;

public class StrategieSerialisationComplete extends StrategieDeSerialisation {

	public StrategieSerialisationComplete() {

	}

	@Override
	public boolean serialiseTout(final int profondeur, final FieldInformations fieldInformation) {
		return true;
	}

}
