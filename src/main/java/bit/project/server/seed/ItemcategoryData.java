package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

@SeedClass
public class ItemcategoryData extends AbstractSeedClass {

    public ItemcategoryData(){
        addIdNameData(1, "Dangerous");
        addIdNameData(2, "Normal");
    }
}
