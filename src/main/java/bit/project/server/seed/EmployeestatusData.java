package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class EmployeestatusData extends AbstractSeedClass {

    public EmployeestatusData(){
        addIdNameData(1, "Working");
        addIdNameData(2, "Resigned");
        addIdNameData(3, "Suspended");
    }
}
