package com.hgicreate.rno.lte.common.service;

import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.model.User;
import com.hgicreate.rno.lte.common.repo.AreaRepository;
import com.hgicreate.rno.lte.common.repo.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@CacheConfig(cacheNames = "areas_s")
public class AreaService {
    private final AreaRepository areaRepository;

    private final UserRepository userRepository;

    public AreaService(AreaRepository areaRepository, UserRepository userRepository) {
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
    }

    @Cacheable
    public List<Area> getAreaByAccount(String account, long cityId) {
        // 获取省列表
        List<Area> provinces = areaRepository.findByAreaLevel(1);
        if (provinces != null && !provinces.isEmpty()) {
            long topProvinceId = -1;
            long topCityId = -1;
            if (cityId > 0) {
                topProvinceId = findParent(provinces, cityId);
                if (topProvinceId > 0) {
                    topCityId = cityId;
                }
            } else {
                User user = userRepository.findByUsername(account);
                long defaultCity = user.getDefaultCity();
                if (defaultCity > 0) {
                    topProvinceId = findParent(provinces, defaultCity);
                    if (topProvinceId > 0) {
                        topCityId = defaultCity;
                    }
                }
            }

            if (topProvinceId > 0) {
                move2Top(provinces, topProvinceId);
                //move2Top(provinces.get(0).getChildren(), topCityId);
            }
        } else {
            return Collections.emptyList();
        }
        return provinces;
    }

    private long findParent(List<Area> areas, long areaId) {
        long parentId = -1;
        for (Area province : areas) {
            List<Area> cities = null;//= province.getChildren();
            if (cities != null && !cities.isEmpty()) {
                for (Area city : cities) {
                    if (city.getId() == areaId) {
                        parentId = province.getId();
                        break;
                    }
                }
            }
        }
        return parentId;
    }

    private void move2Top(List<Area> areas, long areaId) {
        if (areaId > 0 && areas.size() > 0) {
            Area tmp = areas.get(0);
            for (int j = 0; j < areas.size(); j++) {
                if (areas.get(j).getId() == areaId) {
                    areas.set(0, areas.get(j));
                    areas.set(j, tmp);
                }
            }
        }
    }
}