package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class NametitleData extends AbstractSeedClass {

    public NametitleData(){
        addIdNameData(1, "Mr.");
        addIdNameData(2, "Mrs.");
        addIdNameData(3, "Miss");
        addIdNameData(4, "Rev.");
    }
}
