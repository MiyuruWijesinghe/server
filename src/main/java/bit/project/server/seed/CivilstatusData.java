package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class CivilstatusData extends AbstractSeedClass {

    public CivilstatusData(){
        addIdNameData(1, "Single");
        addIdNameData(2, "Married");
        addIdNameData(3, "Other");
    }
}
