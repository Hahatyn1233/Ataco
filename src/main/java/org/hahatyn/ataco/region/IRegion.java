package org.hahatyn.ataco.region;

import java.util.Collection;

public interface IRegion {

    void save(Region region);
    void delete(String regionId);
    Region findById(String regionId);
    Collection<Region> findAll();

}
