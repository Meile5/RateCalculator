package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.List;

public interface IRegionManager {
    Region addRegion(Region region, List<Country> countries) throws RateException;

    Region updateRegion(Region region, List<Country> countries) throws RateException;

    boolean deleteRegion(Region region) throws RateException;

    List<Region> performSearchRegionFilter(String filter, Collection<Region> allRegions);
}
