package org.paasta.container.terraman.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@RequiredArgsConstructor
public class VaultConfig extends AbstractVaultConfiguration {

    @Value("${vault.props.host}")
    private String vaultHost;
    @Value("${vault.props.port}")
    private int vaultPort;
    @Value("${vault.props.scheme}")
    private String vaultSchema;
    @Value("${vault.props.app-role.role-id}")
    private String vaultRoleId;
    @Value("${vault.props.app-role.secret-id}")
    private String vaultSecretId;

    @Override
    public VaultEndpoint vaultEndpoint() {

        VaultEndpoint vaultEndpoint = VaultEndpoint.create(vaultHost, vaultPort);
        vaultEndpoint.setScheme(vaultSchema);
        return vaultEndpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        // ID값 오류 ==> VaultLoginException: Cannot unwrap Role id using AppRole: wrapping token is not valid or does not exist
        // 미허용 IP ==> HttpClientErrorException$BadRequest: 400 Bad Request: [{"errors":["source address ... unauthorized by CIDR restrictions on the role: ..."]}
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .roleId(AppRoleAuthenticationOptions.RoleId.provided(vaultRoleId))
                .secretId(AppRoleAuthenticationOptions.SecretId.provided(vaultSecretId))
                .build();
        return new AppRoleAuthentication(options, restOperations());
    }
}
