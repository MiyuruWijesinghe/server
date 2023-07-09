package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class DesignationData extends AbstractSeedClass {

    public DesignationData(){
        addIdNameData(1, "Pharmacist");
        addIdNameData(2, "Manager");
        addIdNameData(3, "Cashier");
        addIdNameData(4, "Checker");
    }
}
