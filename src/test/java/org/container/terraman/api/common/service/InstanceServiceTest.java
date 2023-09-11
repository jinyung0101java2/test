package org.container.terraman.api.common.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.InstanceModel;
import org.container.terraman.api.common.terramanproc.TerramanInstanceProcess;
import org.container.terraman.api.common.util.CommonFileUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class InstanceServiceTest {

    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_HOST = "testHost";
    private static final String TEST_ID_RSA = "testIdRsa";
    private static final String TEST_PROVIDER = "testProvider";
    private static final String TEST_PROCESS_GB = "testProcessGb";

    private static final String TEST_AWS = "AWS";
    private static final String TEST_OPENSTCK = "OPENSTACK";
    private static final String TEST_GCP = "GCP";
    private static final String TEST_VSPHERE = "VSPHERE";

    private static final String TEST_RESOURCE_NAME = "testResourceName";
    private static final String TEST_INSTANCE_NAME = "testInstanceName";
    private static final String TEST_PRIVATE_IP = "testPrivateIp";
    private static final String TEST_PUBLIC_IP = "testPublicIp";

    private static final String TEST_IP_ADDR = "1.1.1.1";
    private static final String TEST_INSTANCE_ID = "test";

    private static final String TEST_FLOATING_IP = "203.255.255.115";
    private static final String TEST_CH_PRIVATE_IP = "ip-1-1-1-1";

    private static final String TEST_FILE_NAME = "fileName";
    private static final String TEST_CONTAINER = "CONTAINER";

    private InstanceModel instanceModel;
    private InstanceModel instanceResultModel;
    private List<InstanceModel> instancesModel;
    private List<InstanceModel> instancesResultModel;
    private JsonObject readStateFile;
    private JsonObject jsonObject;

    @Mock
    private TerramanInstanceProcess terramanInstanceProcess;
    @Mock
    private CommandService commandService;
    @Mock
    private CommonFileUtils commonFileUtils;
    @Mock
    private InstanceService instanceService;

    @InjectMocks
    private InstanceService instanceServiceMock;

    @Before
    public void setUp() {
        instanceResultModel = new InstanceModel("","","","");
        instancesResultModel = new ArrayList<>();

        instanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        instancesModel = new ArrayList<>();
        instancesModel.add(instanceModel);

        readStateFile = new JsonObject();
        jsonObject = new JsonObject();

        String tmp = "{\n" +
                "  \"version\": 0,\n" +
                "  \"terraform_version\": \"0\",\n" +
                "  \"serial\": 0,\n" +
                "  \"lineage\": \"test\",\n" +
                "  \"outputs\": {},\n" +
                "  \"resources\": [\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_compute_keypair_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"fingerprint\": \"test\",\n" +
                "            \"id\": \"passta-cp-terraform-keypair\",\n" +
                "            \"name\": \"test\",\n" +
                "            \"public_key\": \"test\\n\",\n" +
                "            \"region\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_images_image_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"checksum\": \"test\",\n" +
                "            \"container_format\": \"test\",\n" +
                "            \"created_at\": \"test\",\n" +
                "            \"disk_format\": \"test\",\n" +
                "            \"file\": \"test\",\n" +
                "            \"hidden\": test,\n" +
                "            \"id\": \"test\",\n" +
                "            \"member_status\": \"test\",\n" +
                "            \"metadata\": {},\n" +
                "            \"min_disk_gb\": 0,\n" +
                "            \"min_ram_mb\": 0,\n" +
                "            \"most_recent\": test,\n" +
                "            \"name\": \"test\",\n" +
                "            \"owner\": \"test\",\n" +
                "            \"properties\": test,\n" +
                "            \"protected\": test,\n" +
                "            \"region\": \"test\",\n" +
                "            \"schema\": \"test\",\n" +
                "            \"size_bytes\": \"test\",\n" +
                "            \"size_max\": \"test\",\n" +
                "            \"size_min\": \"test\",\n" +
                "            \"sort_direction\": \"test\",\n" +
                "            \"sort_key\": \"test\",\n" +
                "            \"tag\": \"test\",\n" +
                "            \"tags\": [],\n" +
                "            \"updated_at\": \"test\",\n" +
                "            \"visibility\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_floatingip_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"address\": \"1.1.1.1\",\n" +
                "            \"all_tags\": [],\n" +
                "            \"description\": \"\",\n" +
                "            \"dns_domain\": \"\",\n" +
                "            \"dns_name\": \"\",\n" +
                "            \"fixed_ip\": \"\",\n" +
                "            \"id\": \"test\",\n" +
                "            \"pool\": \"test\",\n" +
                "            \"port_id\": \"\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"status\": \"test\",\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_floatingip_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"address\": \"1.1.1.1\",\n" +
                "            \"all_tags\": [],\n" +
                "            \"description\": \"\",\n" +
                "            \"dns_domain\": \"\",\n" +
                "            \"dns_name\": \"\",\n" +
                "            \"fixed_ip\": \"\",\n" +
                "            \"id\": \"test\",\n" +
                "            \"pool\": \"test\",\n" +
                "            \"port_id\": \"\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"status\": \"test\",\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_network_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"admin_state_up\": \"test\",\n" +
                "            \"all_tags\": [],\n" +
                "            \"availability_zone_hints\": [],\n" +
                "            \"description\": \"\",\n" +
                "            \"dns_domain\": \"\",\n" +
                "            \"external\": false,\n" +
                "            \"id\": \"test\",\n" +
                "            \"matching_subnet_cidr\": null,\n" +
                "            \"mtu\": 1450,\n" +
                "            \"name\": \"test\",\n" +
                "            \"network_id\": null,\n" +
                "            \"region\": \"test\",\n" +
                "            \"shared\": \"test\",\n" +
                "            \"status\": null,\n" +
                "            \"subnets\": [\n" +
                "              \"test\"\n" +
                "            ],\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\",\n" +
                "            \"transparent_vlan\": false\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_router_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"admin_state_up\": true,\n" +
                "            \"all_tags\": [],\n" +
                "            \"availability_zone_hints\": [],\n" +
                "            \"description\": \"\",\n" +
                "            \"distributed\": false,\n" +
                "            \"enable_snat\": true,\n" +
                "            \"external_fixed_ip\": [\n" +
                "              {\n" +
                "                \"ip_address\": \"1.1.1.1\",\n" +
                "                \"subnet_id\": \"test\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"external_network_id\": \"test\",\n" +
                "            \"id\": \"test\",\n" +
                "            \"name\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"router_id\": null,\n" +
                "            \"status\": \"test\",\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_secgroup_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"all_tags\": [],\n" +
                "            \"description\": \"test\",\n" +
                "            \"id\": \"test\",\n" +
                "            \"name\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"secgroup_id\": null,\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"data\",\n" +
                "      \"type\": \"openstack_networking_subnet_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"all_tags\": [],\n" +
                "            \"allocation_pools\": [\n" +
                "              {\n" +
                "                \"end\": \"1.1.1.1\",\n" +
                "                \"start\": \"1.1.1.1\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"cidr\": \"1.1.1.1/24\",\n" +
                "            \"description\": \"\",\n" +
                "            \"dhcp_disabled\": null,\n" +
                "            \"dhcp_enabled\": null,\n" +
                "            \"dns_nameservers\": [\n" +
                "              \"1.1.1.1\"\n" +
                "            ],\n" +
                "            \"enable_dhcp\": true,\n" +
                "            \"gateway_ip\": \"1.1.1.1\",\n" +
                "            \"host_routes\": [],\n" +
                "            \"id\": \"test\",\n" +
                "            \"ip_version\": 4,\n" +
                "            \"ipv6_address_mode\": \"\",\n" +
                "            \"ipv6_ra_mode\": \"\",\n" +
                "            \"name\": \"test\",\n" +
                "            \"network_id\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"subnet_id\": null,\n" +
                "            \"subnetpool_id\": \"\",\n" +
                "            \"tags\": null,\n" +
                "            \"tenant_id\": \"test\"\n" +
                "          },\n" +
                "          \"sensitive_attributes\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"managed\",\n" +
                "      \"type\": \"openstack_compute_floatingip_associate_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"fixed_ip\": \"\",\n" +
                "            \"floating_ip\": \"1.1.1.1\",\n" +
                "            \"id\": \"1.1.1.1/test/\",\n" +
                "            \"instance_id\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"timeouts\": null,\n" +
                "            \"wait_until_associated\": true\n" +
                "          },\n" +
                "          \"sensitive_attributes\": [],\n" +
                "          \"private\": \"test\",\n" +
                "          \"dependencies\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"managed\",\n" +
                "      \"type\": \"openstack_compute_floatingip_associate_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"fixed_ip\": \"\",\n" +
                "            \"floating_ip\": \"1.1.1.1\",\n" +
                "            \"id\": \"1.1.1.1/test/\",\n" +
                "            \"instance_id\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"timeouts\": null,\n" +
                "            \"wait_until_associated\": true\n" +
                "          },\n" +
                "          \"sensitive_attributes\": [],\n" +
                "          \"private\": \"test==\",\n" +
                "          \"dependencies\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"managed\",\n" +
                "      \"type\": \"openstack_compute_instance_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"access_ip_v4\": \"1.1.1.1\",\n" +
                "            \"access_ip_v6\": \"\",\n" +
                "            \"admin_pass\": null,\n" +
                "            \"all_metadata\": {},\n" +
                "            \"all_tags\": [],\n" +
                "            \"availability_zone\": \"test\",\n" +
                "            \"availability_zone_hints\": null,\n" +
                "            \"block_device\": [\n" +
                "              {\n" +
                "                \"boot_index\": 0,\n" +
                "                \"delete_on_termination\": true,\n" +
                "                \"destination_type\": \"test\",\n" +
                "                \"device_type\": \"\",\n" +
                "                \"disk_bus\": \"\",\n" +
                "                \"guest_format\": \"\",\n" +
                "                \"source_type\": \"test\",\n" +
                "                \"uuid\": \"test\",\n" +
                "                \"volume_size\": 80,\n" +
                "                \"volume_type\": \"\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"config_drive\": null,\n" +
                "            \"flavor_id\": \"test\",\n" +
                "            \"flavor_name\": \"test\",\n" +
                "            \"floating_ip\": null,\n" +
                "            \"force_delete\": false,\n" +
                "            \"id\": \"test\",\n" +
                "            \"image_id\": \"test\",\n" +
                "            \"image_name\": null,\n" +
                "            \"key_pair\": \"test\",\n" +
                "            \"metadata\": null,\n" +
                "            \"name\": \"test\",\n" +
                "            \"network\": [\n" +
                "              {\n" +
                "                \"access_network\": false,\n" +
                "                \"fixed_ip_v4\": \"1.1.1.1\",\n" +
                "                \"fixed_ip_v6\": \"\",\n" +
                "                \"floating_ip\": \"\",\n" +
                "                \"mac\": \"test\",\n" +
                "                \"name\": \"test\",\n" +
                "                \"port\": \"\",\n" +
                "                \"uuid\": \"test\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"network_mode\": null,\n" +
                "            \"personality\": [],\n" +
                "            \"power_state\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"scheduler_hints\": [],\n" +
                "            \"security_groups\": [\n" +
                "              \"test\"\n" +
                "            ],\n" +
                "            \"stop_before_destroy\": false,\n" +
                "            \"tags\": null,\n" +
                "            \"timeouts\": null,\n" +
                "            \"user_data\": null,\n" +
                "            \"vendor_options\": [],\n" +
                "            \"volume\": []\n" +
                "          },\n" +
                "          \"sensitive_attributes\": [],\n" +
                "          \"private\": \"test\",\n" +
                "          \"dependencies\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"managed\",\n" +
                "      \"type\": \"openstack_compute_instance_v2\",\n" +
                "      \"name\": \"test\",\n" +
                "      \"provider\": \"test\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"schema_version\": 0,\n" +
                "          \"attributes\": {\n" +
                "            \"access_ip_v4\": \"1.1.1.1\",\n" +
                "            \"access_ip_v6\": \"\",\n" +
                "            \"admin_pass\": null,\n" +
                "            \"all_metadata\": {},\n" +
                "            \"all_tags\": [],\n" +
                "            \"availability_zone\": \"test\",\n" +
                "            \"availability_zone_hints\": null,\n" +
                "            \"block_device\": [\n" +
                "              {\n" +
                "                \"boot_index\": 0,\n" +
                "                \"delete_on_termination\": true,\n" +
                "                \"destination_type\": \"test\",\n" +
                "                \"device_type\": \"\",\n" +
                "                \"disk_bus\": \"\",\n" +
                "                \"guest_format\": \"\",\n" +
                "                \"source_type\": \"test\",\n" +
                "                \"uuid\": \"test\",\n" +
                "                \"volume_size\": 80,\n" +
                "                \"volume_type\": \"\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"config_drive\": null,\n" +
                "            \"flavor_id\": \"test\",\n" +
                "            \"flavor_name\": \"test\",\n" +
                "            \"floating_ip\": null,\n" +
                "            \"force_delete\": false,\n" +
                "            \"id\": \"test\",\n" +
                "            \"image_id\": \"test\",\n" +
                "            \"image_name\": null,\n" +
                "            \"key_pair\": \"test\",\n" +
                "            \"metadata\": null,\n" +
                "            \"name\": \"test\",\n" +
                "            \"network\": [\n" +
                "              {\n" +
                "                \"access_network\": false,\n" +
                "                \"fixed_ip_v4\": \"1.1.1.1\",\n" +
                "                \"fixed_ip_v6\": \"\",\n" +
                "                \"floating_ip\": \"\",\n" +
                "                \"mac\": \"test\",\n" +
                "                \"name\": \"test\",\n" +
                "                \"port\": \"\",\n" +
                "                \"uuid\": \"test\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"network_mode\": null,\n" +
                "            \"personality\": [],\n" +
                "            \"power_state\": \"test\",\n" +
                "            \"region\": \"test\",\n" +
                "            \"scheduler_hints\": [],\n" +
                "            \"security_groups\": [\n" +
                "              \"test\"\n" +
                "            ],\n" +
                "            \"stop_before_destroy\": false,\n" +
                "            \"tags\": null,\n" +
                "            \"timeouts\": null,\n" +
                "            \"user_data\": null,\n" +
                "            \"vendor_options\": [],\n" +
                "            \"volume\": []\n" +
                "          },\n" +
                "          \"sensitive_attributes\": [],\n" +
                "          \"private\": \"test\",\n" +
                "          \"dependencies\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        jsonObject = (JsonObject) JsonParser.parseString(tmp);
    }

    @Test
    public void getInstanceTestOfDefault() {
        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceTestOfAws() {
        when(instanceService.getInstanceInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_AWS, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceTestOfOpenstack() {
        when(instanceService.getInstanceInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_OPENSTCK, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceTestOfGcp() {
        when(instanceService.getInstanceInfoGcp()).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_GCP, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceTestOfVsphere() {
        when(instanceService.getInstanceInfoVSphere()).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_VSPHERE, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstancesTestOfDefault() {
        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesTestOfAws() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_AWS, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesTestOfOpenstack() {
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_OPENSTCK, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesTestOfGcp() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoVSphere()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_GCP, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesTestOfVsphere() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoVSphere()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_VSPHERE, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstanceInfoAwsTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);
        when(terramanInstanceProcess.getInstanceInfoAws(jsonObject)).thenReturn(instanceResultModel);

        InstanceModel result = instanceServiceMock.getInstanceInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceInfoOpenstackTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);
        when(terramanInstanceProcess.getInstanceInfoOpenstack(jsonObject)).thenReturn(instanceResultModel);

        InstanceModel result = instanceServiceMock.getInstanceInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceInfoGcpTest() {
        InstanceModel result = instanceServiceMock.getInstanceInfoGcp();

        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstanceInfoVsphereTest() {
        InstanceModel result = instanceServiceMock.getInstanceInfoVSphere();

        assertEquals(instanceResultModel, result);
    }

    @Test
    public void getInstancesInfoAwsTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);
        when(terramanInstanceProcess.getInstancesInfoAws(jsonObject)).thenReturn(instancesResultModel);

        List<InstanceModel> result = instanceServiceMock.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesInfoOpenstackTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);
        when(terramanInstanceProcess.getInstancesInfoOpenstack(jsonObject)).thenReturn(instancesResultModel);

        List<InstanceModel> result = instanceServiceMock.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesInfoGcpTest() {
        List<InstanceModel> result = instanceServiceMock.getInstancesInfoGcp();

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void getInstancesInfoVsphereTest() {
        List<InstanceModel> result = instanceServiceMock.getInstancesInfoVSphere();

        assertEquals(instancesResultModel, result);
    }

    @Test
    public void readStateFileTest() {
        when(commonFileUtils.fileRead(TEST_FILE_NAME)).thenReturn(readStateFile);

        JsonObject result = instanceServiceMock.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB);

        assertEquals(null, result);
    }
}
