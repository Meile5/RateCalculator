package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.dal.regionDAO.IRegionDAO;
import easv.dal.regionDAO.RegionDAO;
import easv.exception.RateException;

import java.util.ArrayList;
import java.util.List;

public class RegionManager implements IRegionManager{

    private IRegionDAO regionDAO;

    public RegionManager() throws RateException {
        this.regionDAO = new RegionDAO();
    }

    @Override
    public int addRegion(Region region, List<Country> countries) throws RateException {
        return regionDAO.addRegion(region, countries);
    }

    @Override
    public void updateRegion(Region region, List<Country> countries) throws RateException {
        regionDAO.updateRegion(region, countries);
    }
}
