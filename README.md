![image](https://user-images.githubusercontent.com/67575226/117615561-d74f8d80-b1a4-11eb-8dc0-70117f96cdad.png)
컨테이너플랫폼을 구성하는 포털 UI에 해당하는 Repository이며 아래 Repository와 함께 플랫폼의 서비스를 구성합니다.
- [K-PaaS 컨테이너 플랫폼 포털 Common API](https://github.com/PaaS-TA/container-platform-portal-common-api)
- [K-PaaS 컨테이너 플랫폼 포털 API](https://github.com/PaaS-TA/container-platform-portal-api)



---

# K-PaaS 컨테이너 플랫폼 WEB UI
K-PaaS에서 제공하는 컨테이너 플랫폼 포털 웹 애플리케이션입니다.

- [시작하기](#시작하기)
  - [컨테이너 플랫폼 WEB UI 빌드 방법](#컨테이너-플랫폼-WEB-UI-빌드-방법)
- [문서](#문서)
- [개발 환경](#개발-환경)
- [라이선스](#라이선스)

## 시작하기
K-PaaS 컨테이너 플랫폼 WEB UI가 수행하는 애플리케이션 관리 작업은 다음과 같습니다.

- 컨테이너 플랫폼 자원 관리
- 권한 관리
- 사용자 관리

### 컨테이너 플랫폼 WEB UI 빌드 방법
K-PaaS 컨테이너 플랫폼 WEB UI 소스 코드를 활용하여 로컬 환경에서 빌드가 필요한 경우 다음 명령어를 입력합니다.
```
$ gradle build
```


## 문서
- 컨테이너 플랫폼 활용에 대한 정보는 [K-PaaS 컨테이너 플랫폼](https://github.com/K-PaaS/container-platform)을 참조하십시오. 


## 개발 환경
K-PaaS 컨테이너 플랫폼 WEB UI의 개발 환경은 다음과 같습니다.

| Situation                      | Version |
| ------------------------------ | ------- |
| JDK                            | 8       |
| Gradle                         | 6.9.2   |
| Spring Boot                    | 2.7.3   |
| Spring Boot Management         | 1.0.11.RELEASE  |
| Spring Security Tag Libs       | 5.3.3.RELEASE   |
| Tomcat Embed Core              | 9.0.65  |
| Jstl                           | 1.2     |
| Apache Tiles                   | 3.0.5   | 
| Jsp                            | 2.3.1   |
| Gson                           | 2.8.6   |
| Lombok                         | 1.18.12 |
| Swagger	                     | 2.9.2   |


## 라이선스
K-PaaS 컨테이너 플랫폼 WEB UI는 [Apache-2.0 License](http://www.apache.org/licenses/LICENSE-2.0)를 사용합니다.
