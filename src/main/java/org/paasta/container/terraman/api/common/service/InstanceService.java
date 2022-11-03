package org.paasta.container.terraman.api.common.service;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.terramanproc.TerramanInstanceProcess;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class InstanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceService.class);

    private final CommonFileUtils commonFileUtils;
    private final CommandService commandService;

    @Autowired
    public InstanceService(CommonFileUtils commonFileUtils, CommandService commandService) {
        this.commonFileUtils = commonFileUtils;
        this.commandService = commandService;
    }

    /**
     * Select Instance Info
     *
     * @param clusterId the clusterId
     * @param provider the provider
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public InstanceModel getInstance(String clusterId, String provider, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;

        switch(provider.toUpperCase()) {
            case Constants.UPPER_AWS : resultModel = getInstanceInfoAws(clusterId, host, idRsa, processGb); break;
            case Constants.UPPER_GCP : resultModel = getInstanceInfoGcp(); break;
            case Constants.UPPER_VSPHERE : resultModel = getInstanceInfoVSphere(); break;
            case Constants.UPPER_OPENSTACK : resultModel = getInstanceInfoOpenstack(clusterId, host, idRsa, processGb); break;
            default : LOGGER.error("{} is Cloud not supported.", provider);
                resultModel = new InstanceModel("", "", "", "");
                break;
        }

        return resultModel;
    }

    /**
     * Select Instances Info
     *
     * @param clusterId the clusterId
     * @param provider the provider
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getInstances(String clusterId, String provider, String host, String idRsa, String processGb) {
        List<InstanceModel> resultList = new ArrayList<>();

        switch (provider.toUpperCase()) {
            case Constants.UPPER_AWS : resultList = getInstancesInfoAws(clusterId, host, idRsa, processGb); break;
            case Constants.UPPER_GCP : resultList = getInstancesInfoGcp(); break;
            case Constants.UPPER_VSPHERE : resultList = getInstancesInfoVSphere(); break;
            case Constants.UPPER_OPENSTACK : resultList = getInstancesInfoOpenstack(clusterId, host, idRsa, processGb); break;
            default : LOGGER.error("{} is Cloud not supported.", provider); break;
        }

        return resultList;
    }

    /**
     * Select Instance Info AWS
     *
     * @param clusterId the cluster
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoAws(String clusterId, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), TerramanConstant.CONTAINER_MSG)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa
                    , TerramanConstant.DEFAULT_USER_NAME);
        }
        JsonObject jsonObject = readStateFile(clusterId, processGb);

        return new TerramanInstanceProcess().getInstanceInfoAws(jsonObject);
    }

    /**
     * Select Instance Info GCP
     *
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoGcp() {
        return new InstanceModel("", "", "", "");
    }

    /**
     * Select Instance Info VSphere
     *
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoVSphere() {
        return new InstanceModel("", "", "", "");
    }

    /**
     * Select Instance Info OpenStack
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoOpenstack(String clusterId, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), TerramanConstant.CONTAINER_MSG)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa
                    , TerramanConstant.DEFAULT_USER_NAME);
        }

        JsonObject jsonObject = readStateFile(clusterId, processGb);

        return new TerramanInstanceProcess().getInstanceInfoOpenstack(jsonObject);
    }

    /**
     * Select Instances Info AWS
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstancesInfoAws(String clusterId, String host, String idRsa, String processGb) {
        List<InstanceModel> modelList = new ArrayList<>();
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), TerramanConstant.CONTAINER_MSG)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa
                    , TerramanConstant.DEFAULT_USER_NAME);
        }

        JsonObject jsonObject = readStateFile(clusterId, processGb);

        return new TerramanInstanceProcess().getInstancesInfoAws(jsonObject);
    }

    /**
     * Select Instances Info GCP
     *
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getInstancesInfoGcp() {
        return new ArrayList<>();
    }

    /**
     * Select Instances Info VSphere
     *
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getInstancesInfoVSphere() {
        return new ArrayList<>();
    }

    /**
     * Select Instances Info OpenStack
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstancesInfoOpenstack(String clusterId, String host, String idRsa, String processGb) {
        List<InstanceModel> modelList = new ArrayList<>();
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), TerramanConstant.CONTAINER_MSG)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa
                    , TerramanConstant.DEFAULT_USER_NAME);
        }
        JsonObject jsonObject = readStateFile(clusterId, processGb);

        return new TerramanInstanceProcess().getInstancesInfoOpenstack(jsonObject);
    }

    /**
     * Read State File
     *
     * @param clusterId the clusterId
     * @param processGb the processGb
     * @return the JsonObject
     */
    public JsonObject readStateFile(String clusterId, String processGb) {
        return commonFileUtils.fileRead(TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));
    }
}
