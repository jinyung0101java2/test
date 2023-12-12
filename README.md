## Related Repositories

<table>
  <tr>
    <td colspan=2 align=center>플랫폼</td>
    <td colspan=2 align=center><a href="https://github.com/K-PaaS/cp-deployment">컨테이너 플랫폼</a></td>
    <td colspan=2 align=center><a href="https://github.com/K-PaaS/sidecar-deployment">사이드카</a></td>
    <td colspan=2 align=center><a href="https://github.com/K-PaaS/ap-deployment">어플리케이션 플랫폼</a></td>
  </tr>
  <tr>
    <td colspan=2 align=center>포털</td>
    <td colspan=2 align=center><a href="https://github.com/K-PaaS/cp-portal-release">CP 포털</a></td>
    <td colspan=2 align=center>-</td>
    <td colspan=2 align=center><a href="https://github.com/K-PaaS/portal-deployment">AP 포털</a></td>
  </tr>
  <tr align=center>
    <td colspan=2 rowspan=9>Component<br>/ 서비스</td>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-portal-common-api">Common API</a></td>
    <td colspan=2>-</td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-mongodb-shard-release">MongoDB</a></td>
  </tr>
  <tr align=center>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-metrics-api">Metric API</a></td>
    <td colspan=2>  </td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-mysql-release">MySQL</a></td>
  </tr>
  <tr align=center>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-portal-api">Portal API</a></td>
    <td colspan=2>  </td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-pipeline-release">Pipeline</a></td>
  </tr>
  <tr align=center>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-portal-ui">Portal UI</a></td>
    <td colspan=2>  </td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-rabbitmq-release">RabbintMQ</a></td>
  </tr>
  <tr align=center>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-portal-service-broker">Service Broker</a></td>
    <td colspan=2>  </td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-on-demand-redis-release">Redis</a></td>
  </tr>
  <tr align=center>
    <td colspan=2><a href="https://github.com/K-PaaS/cp-terraman">🚩Terraman API</a></td>
    <td colspan=2>  </td>
    <td colspan=2><a href="https://github.com/K-PaaS/ap-source-control-release">SoureceControl</a></td>
  </tr>
</table>

<i>🚩 You are here.</i>

<br>

## K-PaaS 컨테이너 플랫폼 Terraman API
K-PaaS 컨테이너 플랫폼 서비스로 Instance 생성 및 Cluster 설치 기능을 제공하는 REST API 입니다.
- [시작하기](#시작하기)
    - [Terraman API 빌드 방법](#terraman-api-빌드-방법)
- [문서](#문서)
- [개발 환경](#개발-환경)
- [라이선스](#라이선스)

<br>

## 시작하기
K-PaaS 컨테이너 플랫폼 Terraman API가 수행하는 관리 작업은 다음과 같습니다.
- IAAS 별 Instance 생성 및 Cluster 설치 

#### Terraman API 빌드 방법
K-PaaS 컨테이너 플랫폼 Terraman API 소스 코드를 활용하여 로컬 환경에서 빌드가 필요한 경우 다음 명령어를 입력합니다.
```
$ gradle build
```

<br>

## 문서
- 컨테이너 플랫폼 활용에 대한 정보는 [K-PaaS 컨테이너 플랫폼](https://github.com/K-PaaS/container-platform)을 참조하십시오.

<br>

## 개발 환경
K-PaaS 컨테이너 플랫폼 Terraman API의 개발 환경은 다음과 같습니다.

| Situation                      | Version |
| ------------------------------ |---------|
| JDK                            | 8       |
| Gradle                         | 7.5     |
| Spring Boot                    | 2.7.3   |
| Spring Boot Management         | 1.0.11  |
| ApacheHttpClient               | 4.5.12  |
| JJWT                           | 0.9.1   |
| Gson                           | 2.8.6   |
| Lombok		                  | 1.18.12 |
| Jacoco		                  | 0.8.5   |
| Swagger	                      | 2.9.2   |

<br>

## 라이선스
K-PaaS 컨테이너 플랫폼 Terraman API는 [Apache-2.0 License](http://www.apache.org/licenses/LICENSE-2.0)를 사용합니다.
