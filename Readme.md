# 멍스토어

## 프로젝트 기본 정보
- **개발 기간** : `2024.05.04` - `2024.06.21`
- **깃허브 링크** : https://github.com/juljasong/mungstore
- **목표** : 반려동물 커머스 서버를 구축한다.

### 개발 환경
| 분류            | 설명                                                           |
|---------------|--------------------------------------------------------------|
| 운영체제          | Windows 10                                                   |
| 버전 관리 시스템     | Git, Github                                                  |
| 개발 도구         | IntelliJ IDEA                                                |
| 개발 언어 및 프레임워크 | Java 17 <br/> Spring Framework 6.1.6 </br> Spring Boot 3.2.5 |
| 데이터베이스 | MySQL 8.4.0 <br/> Redis 7.2.4                                |

---

## 기술 스택
### 메인 기술
![Static Badge](https://img.shields.io/badge/java%2017-007396?style=for-the-badge&logo=Java&logoColor=white)
![Static Badge](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)
![Static Badge](https://img.shields.io/badge/spring%20boot%203.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Static Badge](https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

### 데이터베이스
![Static Badge](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Static Badge](https://img.shields.io/badge/spring%20data%20jpa-6DB33F?style=for-the-badge&logo=jpa&logoColor=white)
![Static Badge](https://img.shields.io/badge/qeurydsl-007396?style=for-the-badge&logo=qeurydsl&logoColor=white)
![Static Badge](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Static Badge](https://img.shields.io/badge/flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

### etc
![Static Badge](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Static Badge](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Static Badge](https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![Static Badge](https://img.shields.io/badge/mailgun-F06B66?style=for-the-badge&logo=mailgun&logoColor=white)
![Static Badge](https://img.shields.io/badge/open%20feign-6DB33F?style=for-the-badge&logo=openfeign&logoColor=white)

---

## 서비스 기본 설명
### ERD
- https://drive.google.com/file/d/1Dfvb0-fhXgXYNWcyS46xf1XYDItISyF8/view?usp=sharing

### Gradle Multi-Module 구조
```
.
├── api
├── common
├── member
├── order
├── payment
├── product
├── stock
```

### 모듈 설명
- api
  - application 영역
  - 외부에서의 진입점 (Controller)
- common
  - 전체 모듈에서 공통적으로 사용할 domain, exception, request, response, utils 등을 모아놓은 모듈
- member
  - 인증 (jwt), 회원, 장바구니 기능을 담당하는 모듈 
- order
  - 주문 기능을 담당하는 모듈
- payment
  - 결제 기능을 담당하는 모듈
- product
  - 상품, 옵션, 카테고리 기능을 담당하는 모듈
- stock
  - 재고 기능을 담당하는 모듈

---

## 기능
### member
- [x] 회원가입
  - [x] 비밀번호 유효성 체크
- [x] 로그인
  - [x] 비밀번호 오류 5회 시 패스워드 초기화
  - [x] 로그인 이력 저장
  - [x] 로그인 이후 인증 필요한 요청 토큰 체크
  - [x] access token 만료 시 refresh token으로 access token 재발행
- [x] 로그아웃
- [x] 비밀번호 재설정
  - [x] 비밀번호 재설정 url 생성 후 이메일 송신
  - [x] 비밀번호 재설정
- [x] 회원 조회
  - [x] 상세
  - [x] 리스트(검색)
- [x] 회원 정보 수정
- [x] 장바구니 조회
- [x] 장바구니 등록
- [x] 장바구니 삭제

### order
- [x] 주문
- [x] 주문 취소
- [x] 주문 조회
  - [x] 상세
  - [x] 리스트(검색)

### payment
- [x] 결제 요청 (kakaopay)
- [x] 결제 취소 (kakaopay)

### product
- [x] 카테고리 등록
- [x] 옵션 등록
- [x] 상품 등록
- [x] 상품 수정
- [x] 상품 삭제
- [x] 상품 조회
  - [x] 상세
  - [x] 리스트(검색)

### stock
- [x] 재고 확인