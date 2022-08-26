package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import org.paasta.container.terraman.api.common.config.TrackExecutionTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VaultService {
    Logger logger = LoggerFactory.getLogger(VaultService.class);

    @Autowired
    VaultTemplate vaultTemplate;

    @Autowired
    PropertyService propertyService;

    @Autowired
    CommonService commonService;

    /**
     * Vault read를 위한 method
     *
     * @param path the path
     * @return the object
     */
    @TrackExecutionTime
    public <T> T read(String path,  Class<T> requestClass) {
        path = setPath(path);

        Object response = Optional.ofNullable(vaultTemplate.read(path))
                .map(VaultResponse::getData)
                .filter(x -> x.keySet().contains("data"))
                .orElseGet(HashMap::new)
                .getOrDefault("data", null);
        logger.info("vault object :: " + response.toString());
        Gson gson = new Gson();
        String aa = gson.toJson(response);
        logger.info("aa :: " + aa);
        logger.info("bb :: " + gson.fromJson(aa, requestClass).toString());
        return commonService.setResultObject(response, requestClass);
    }

    /**
     * Vault write를 위한 method
     *
     * @param path the path
     * @return the object
     */
    @TrackExecutionTime
    public Object write(String path, Object body){
        path = setPath(path);

        Map<String, Object> data = new HashMap<>();
        data.put("data", body);

        return vaultTemplate.write(path, data);
    }

    /**
     * Vault delete를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public void delete(String path){
        path = setPath(path);
        vaultTemplate.delete(path);
    }

    /**
     * Vault path 처리 를 위한 method
     *
     * @param path the path
     * @return the String
     */
    String setPath(String path){
        return new StringBuilder(path).insert(path.indexOf("/") + 1, "data/").toString();
    }


}
