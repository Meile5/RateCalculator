package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.exception.RateException;

import java.util.List;

public interface IRegionManager {
    Region addRegion(Region region, List<Country> countries) throws RateException;

    Region updateRegion(Region region, List<Country> countries) throws RateException;

    boolean deleteRegion(Region region) throws RateException;
}
