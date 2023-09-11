package org.container.terraman.api.common.util;

import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.MessageConstant;
import org.container.terraman.api.common.constants.CommonStatusCode;
import org.container.terraman.api.exception.ContainerTerramanException;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Map;

/**
 * Yaml Util 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.21
 **/
public class YamlUtil {

    /**
     * YAML 의 Resource 값 조회(Get YAML's resource)
     *
     * @param yaml the yaml
     * @param keyword the keyword
     * @return the string
     */
    public static String parsingYaml(String yaml, String keyword) {
        String value = null;
        try {
            Yaml y = new Yaml();
            Map<String,Object> yamlMap = y.load(yaml);

            if ("kind".equals(keyword)) {
                value = (String) yamlMap.get(keyword);
            } else if("metadata".equals(keyword)) {
                Map a = (Map) yamlMap.get(keyword);
                value = a.get("name").toString();
            }

        } catch (ClassCastException e) {
            throw new ContainerTerramanException(Constants.RESULT_STATUS_FAIL, MessageConstant.INVALID_YAML_FORMAT.getMsg() , CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.INVALID_YAML_FORMAT.getMsg());
        }

        return value;
    }


    /**
     * YAML Resource 값 조회(Get YAML resource value)
     *
     * @param yaml the yaml
     * @param keyword the keyword
     * @return the map
     */
    public static Map parsingYamlMap(String yaml, String keyword) {
        Map value = null;
        try {
            Yaml y = new Yaml();
            Map<String,Object> yamlMap = y.load(yaml);

            if ("metadata".equals(keyword)) {
                value = (Map) yamlMap.get(keyword);
            }

        } catch (ClassCastException e) {
            throw new ContainerTerramanException(Constants.RESULT_STATUS_FAIL, MessageConstant.INVALID_YAML_FORMAT.getMsg(),  CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.INVALID_YAML_FORMAT.getMsg());
        }

        return value;
    }


    /**
     * URL Resource 값과 비교할 YAML Resource 값 조회(Get YAML's resource to compare URL Resource)
     *
     * @param kind the kind
     * @return the string
     */
    public static String makeResourceNameYAML(String kind) {
        String YamlKind =  kind.toLowerCase();

        return YamlKind;
    }


    /**
     * 복합 yaml List 로 조회(Get list of multiple YAML)
     *
     * @param yaml the yaml
     * @return the string[]
     */
    public static String[] splitYaml(String yaml) {
        String[] yamlArray = yaml.split("---");
        ArrayList<String> returnList = new ArrayList<String>();

        for (String temp : yamlArray) {
            temp =  temp.trim();
            if ( temp.length() > 0 )  {
                returnList.add(temp);
            }
        }
        return returnList.toArray(new String[returnList.size()]);
    }
}
