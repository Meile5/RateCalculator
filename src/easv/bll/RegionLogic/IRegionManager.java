package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.exception.RateException;

import java.util.List;

public interface IRegionManager {
    int addRegion(Region region, List<Country> countries) throws RateException;

    void updateRegion(Region region, List<Country> countries) throws RateException;
}
