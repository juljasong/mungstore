# 멍스토어

## 프로젝트 기본 정보
- **개발 기간** : `2024.05.04` - `2024.06`
- **깃허브 링크** : https://github.com/juljasong/mungstore
- **목표** : 반려동물 커머스 서버를 구축한다.

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

### etc
![Static Badge](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Static Badge](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Static Badge](https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)

![Static Badge](https://img.shields.io/badge/mailgun-F06B66?style=for-the-badge&logo=mailgun&logoColor=white)
![Static Badge](https://img.shields.io/badge/open%20feign-6DB33F?style=for-the-badge&logo=openfeign&logoColor=white)

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
- [ ] 회원 탈퇴
- [ ] 주문 내역 조회

### order
- [ ] 장바구니 조회
- [ ] 장바구니 등록
- [ ] 장바구니 수정
- [ ] 장바구니 삭제
- [ ] 주문
- [ ] 주문 취소
- [ ] 주문 조회
  - [ ] 상세
  - [ ] 리스트(검색)

### payment
- [ ] 결제 요청
- [ ] 결제 취소

### product
- [x] 카테고리 등록
- [ ] ~~카테고리 수정~~
- [ ] ~~카테고리 삭제~~
- [ ] ~~카테고리 조회~~
- [x] 옵션 등록
- [ ] ~~옵션 수정~~
- [ ] ~~옵션 삭제~~
- [ ] ~~옵션 조회~~
- [x 상품 등록
- [ ] 상품 수정
- [ ] 상품 삭제
- [ ] 상품 조회
  - [ ] 상세
  - [ ] 리스트(검색)
- [ ] 상품 이력 관리
  - [ ] 상세
  - [ ] 리스트(검색)

### stock
- [ ] 재고 수량 변경 이력 관리
