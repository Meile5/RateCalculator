package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.dal.regionDao.IRegionDAO;
import easv.dal.regionDao.RegionDAO;
import easv.exception.RateException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionManager implements IRegionManager{

    private IRegionDAO regionDAO;

    public RegionManager() throws RateException {
        this.regionDAO = new RegionDAO();
    }

    @Override
    public Region addRegion(Region region, List<Country> countries) throws RateException {
        int regionID = regionDAO.addRegion(region, countries);
        if (regionID > 0) {
            region.setId(regionID);
            region.setCountries(countries);
        }
        return region;
    }

    @Override
    public Region updateRegion(Region region, List<Country> countries) throws RateException {
        List<Country> existingCountries = region.getCountries();

        Set<Country> existingSet = new HashSet<>(existingCountries);
        Set<Country> newSet = new HashSet<>(countries);
        Set<Country> addedCountries = new HashSet<>(newSet);
        addedCountries.removeAll(existingSet);
        Set<Country> removedCountries = new HashSet<>(existingSet);
        removedCountries.removeAll(newSet);

        regionDAO.updateRegion(region, addedCountries.stream().toList(), removedCountries.stream().toList());
        region.setCountries(countries);
        return region;
    }

    @Override
    public boolean deleteRegion(Region region) throws RateException {
        return regionDAO.deleteRegion(region);
    }

    @Override
    public List<Region> performSearchRegionFilter(String filter, Collection<Region> allRegions) {
        String filterToLowerCase = filter.toLowerCase();
        return allRegions.stream().filter((region) -> {
            String name = region.getRegionName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }
}
