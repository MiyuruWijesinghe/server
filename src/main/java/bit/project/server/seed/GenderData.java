package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class GenderData extends AbstractSeedClass {

    public GenderData(){
        addIdNameData(1, "Male");
        addIdNameData(2, "Female");
    }

}
