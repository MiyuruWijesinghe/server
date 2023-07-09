package bit.project.server.seed;

import bit.project.server.util.seed.AbstractSeedClass;
import bit.project.server.util.seed.SeedClass;

import java.util.AbstractCollection;

@SeedClass
public class PorderstatusData extends AbstractSeedClass {

    public PorderstatusData(){
        addIdNameData(1, "order requested");
        addIdNameData(2, "order cancelled");
        addIdNameData(3, "order accepted");

    }
}
