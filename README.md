# 👨‍👩‍👧‍👦 FamilyLink

<p align="center">
  <img src="https://github.com/user-attachments/assets/ab66d802-1e82-401a-bd78-6cd1c8656d51" width="700"/>
</p>
<p align="center">
  🔗 <b>Original Repository</b> :
  <a href="https://github.com/404-capstone/FamilyLink-Android">
    Organization GitHub
  </a>
  <br>
  🎓 Capstone Design Project (2025)
</p>

---

## 🌍 프로젝트 개요 (Overview)

<p>
<b>FamilyLink</b>는 가족 간 커뮤니케이션 증진을 목표로 설계된 캡스톤 디자인(졸업작품) 프로젝트입니다.<br><br>

단순한 일정 공유 앱이 아닌, <b>가족 그룹(Family Group) 도메인을 중심으로 설계된 서비스</b>입니다.<br>
가족을 하나의 단위로 묶고, 그룹 기반 일정 · 활동 · 감정 · 사진 · 알림을 연결하여<br>
가족 간 상호작용을 자연스럽게 증가시키는 것을 목표로 합니다.
</p>

---

# 🧩 도메인 구조 (Domain Architecture)

FamilyLink는 기능 나열이 아닌 **도메인 중심 구조**로 설계되었습니다.  
모든 기능은 "가족 그룹"을 기준으로 연결됩니다.

---

<details>
<summary><b>👨‍👩‍👧 그룹 도메인 (Group Domain)</b></summary>

<br>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f82d696e-59ce-45ac-8828-ac47d2b69aad" width="800"/>
</p>

가족 단위의 서비스 이용을 위한 핵심 도메인입니다.

### 주요 기능
- 그룹 생성
- 그룹 정보 수정
- 초대 코드 생성(그룹원 초대)
- 초대 코드 기반 그룹 가입
- 그룹원 추방
- 그룹장 이전
- 그룹 탈퇴
- 그룹 삭제

### 가족 의사소통 척도 설문
- 그룹 가입 후 개인 설문 진행
- 논문 기반 가족 의사소통 척도 질문지 응답
- 결과 확인 및 분석 제공

👉 모든 도메인의 기준점 역할 수행

</details>

---

<details>
<summary><b>📅 캘린더 도메인 (Calendar Domain)</b></summary>

<br>

<p align="center">
  <img src="https://github.com/user-attachments/assets/2e06c203-3229-4f14-951c-3ec8ba2071af" width="800"/>
</p>

가족 간 일정 공유 및 시간 조율을 담당하는 핵심 기능 도메인입니다.

### 개인 일정 관리
- 개인 일정 추가
- 일정 제목 비공개 설정  
  → 그룹 내에서는 `역할의 일정`으로 표시
- 일정 변동 가능 여부 설정
- 개인 일정 댓글 작성 기능 제공 (개인-그룹 간 소통 보조)

### 가족 일정 관리
- 가족 일정 추가
- 참여자 관리
- 일정 댓글 작성

### 일정 최적화 (Schedule Optimization)
- 가족 일정 추가 시 개인 일정과 시간 충돌 탐색
- 변동 가능 일정만 자동 재배치
- 가족 일정 우선 배치

### AI 가족 활동 추천 (AI Activity Recommendation)

#### 입력 요소
- 지역
- 시간대
- 참여자 (역할 기반)
- 활동 공간
- 활동 성격 (최대 3개)

#### 역할 기반 추천 설계
- 역할 → 연령대 / 성별 정보
- 해당 정보를 AI 프롬프트에 포함

#### 서버 처리
- 활동 성격 당 5개의 추천 리스트 제공
- 추천 후 즉시 가족 일정으로 등록 가능

</details>

---

<details>
<summary><b>📖 다이어리 도메인 (Diary Domain)</b></summary>

<br>

<p align="center">
  <img src="https://github.com/user-attachments/assets/68aa8d29-1c06-46ed-b87e-6597395cdd6d" width="800"/>
</p>

감정 기반 소통을 위한 정서 중심 도메인입니다.

### 다이어리 작성
- 일기 작성
- 매일 갱신되는 공통 질문 3개 응답 작성

### 감정 분석
- KC ELECTRA 기반 감정 분석
- 대표 감정 3가지 추출

### AI 피드백
- 일기 내용 + 대표 감정 기반 맞춤형 AI 피드백 제공

### 가족 공유 구조
- 일기 내용은 비공개
- 가장 큰 대표 감정 1가지만 그룹에 공유

</details>

---

<details>
<summary><b>🖼 앨범 도메인 (Album Domain)</b></summary>

<br>

<p align="center">
  <img src="https://github.com/user-attachments/assets/65b029f5-f079-4022-b8ea-d64ee71ba98f" width="800"/>
</p>

가족 추억 공유를 위한 미디어 도메인입니다.

- 사진 업로드
- 사진 조회
- 사진 삭제
- 그룹 단위 공유

</details>

---

<details>
<summary><b>🔔 알림 도메인 (Notification Domain)</b></summary>

<br>

Firebase FCM 기반 실시간 알림 시스템입니다.

### 알림 트리거
- 그룹원 추가/제거
- 가족 일정 추가/수정
- 일정 댓글 작성
- 다이어리 작성
- 사진 업로드

→ 그룹에 속한 다른 사용자에게 실시간 전송

</details>

---

# 👨‍💻 담당 역할 (My Contribution - Android)

본 프로젝트에서 Android 영역의 핵심 설계 및 도메인 구현을 담당하였습니다.

---

<details>
<summary><b>🎨 UI/UX 설계 및 디자인 시스템 구축 (UI/UX & Design System)</b></summary>

<br>

- 전체 화면 와이어프레임 설계 및 디자인
- Jetpack Compose(Material3) 기반 UI 구현
- TopBar, BottomBar, FAB, Button, Dialog 등 공용 컴포넌트 시스템 구축
- 도메인 간 일관된 UI/UX 흐름 설계
- 상태 기반 UI(State-driven UI) 구조 구현

</details>

---

<details>
<summary><b>🔐 인증 및 보안 설계 (Authentication & Security)</b></summary>

<br>

- Kakao 소셜 로그인 구현
- 서버 발급 Access Token 처리 구조 설계
- Android KeyStore + AES 기반 토큰 암호화 저장
- 암호화/복호화 유틸 구현
- API 호출 시 토큰을 안전하게 헤더에 주입하는 구조 설계

→ 민감 정보 보호를 고려한 보안 중심 설계 적용

</details>

---

<details>
<summary><b>🏗 아키텍처 설계 및 의존성 관리 (Architecture & Dependency Injection)</b></summary>

<br>

- MVVM 기반 아키텍처 설계
- Hilt를 활용한 의존성 주입(DI) 구조 구현
- Network Module, Repository, ViewModel 계층 분리
- TokenManager 및 암호화 유틸 주입 구조 설계
- 도메인 간 결합도 감소 및 확장성과 테스트 용이성 확보

</details>

---

<details>
<summary><b>🌐 네트워크 모듈 구현 (Network Module)</b></summary>

<br>

- Retrofit 기반 통신 모듈 설계
- OkHttp 인터셉터 구성
- Access Token 자동 주입 구조 구현
- API 계층 모듈화 및 공통 처리 로직 분리

</details>

---

<details>
<summary><b>👨‍👩‍👧 그룹 도메인 전체 설계 및 구현 (Group Domain Implementation)</b></summary>

<br>

- 그룹 생성 / 초대 코드 기반 가입 플로우 설계 및 예외 케이스 처리
- 그룹장/구성원 권한에 따른 그룹 관리 기능(추방/이전/탈퇴/삭제) 구현
- 그룹 단위 데이터(groupId) 기반 API 연동 및 UI 상태 분기 설계
- 가족 의사소통 척도 설문 진행/결과 조회 기능 구현

</details>

---

<details>
<summary><b>📅 캘린더 도메인 전체 설계 및 구현 (Calendar Domain Implementation)</b></summary>

<br>

- 개인 일정 / 가족 일정 구조 설계
- 제목 비공개 기능 구현
- 변동 가능 일정 기반 일정 최적화 로직 설계
- AI 활동 추천 UI 및 서버 요청 처리
- 일정 댓글 시스템 구현

</details>

---

<details>
<summary><b>🔔 알림 딥링크 처리 (Notification & DeepLink)</b></summary>

<br>

- 알림 클릭 시 딥링크 네비게이션 구현
- 도메인 화면으로 자연스럽게 연결되는 Navigation 구조 설계

</details>

---

# 🛠 기술 스택 (Tech Stack)

<details>
<summary><b>🏗 아키텍처 (Architecture)</b></summary>

- Kotlin  
- Jetpack Compose (Material3)  
- MVVM  
- Hilt  
- Navigation Compose  
- DataStore  

</details>

<details>
<summary><b>🌐 네트워크 (Network)</b></summary>

- Retrofit  
- OkHttp  
- Gson  
- Kotlinx Serialization  

</details>

<details>
<summary><b>📅 캘린더 (Calendar)</b></summary>

- Kizitonwose Compose Calendar  
- Custom Schedule Optimization Logic  

</details>

<details>
<summary><b>🔐 인증 (Authentication)</b></summary>

- Naver Login SDK  
- Kakao SDK  

</details>

<details>
<summary><b>🔔 알림 (Notification)</b></summary>

- Firebase BoM  
- Firebase Cloud Messaging  

</details>

---

# 🔥 기술적 도전 과제 (Technical Challenges)

<details>
<summary><b>1. 일정 충돌 해결 로직 설계 (Schedule Conflict Resolution)</b></summary>

<table>
<tr>
<td><b>문제</b></td>
<td>추가 하려는 가족 일정과 개인 일정 간 시간 충돌 발생</td>
</tr>
<tr>
<td><b>해결</b></td>
<td>변동 가능 개인 일정만 자동 재배치하도록 설계</td>
</tr>
<tr>
<td><b>결과</b></td>
<td>가족 일정 우선 배치 구조 구현</td>
</tr>
</table>

</details>

<details>
<summary><b>2. 프라이버시 보호 설계 (Privacy-Aware Design)</b></summary>

<table>
<tr>
<td><b>문제</b></td>
<td>일정 존재는 공유하되 내용은 보호 필요</td>
</tr>
<tr>
<td><b>해결</b></td>
<td>제목 비공개 시 역할 기반 일정 표시</td>
</tr>
<tr>
<td><b>결과</b></td>
<td>프라이버시와 정보 공유의 균형 확보</td>
</tr>
</table>

</details>

<details>
<summary><b>3. 역할 기반 AI 추천 설계 (Role-Based AI Recommendation)</b></summary>

<table>
<tr>
<td><b>문제</b></td>
<td>단순 카테고리 추천은 가족 특성 반영 부족</td>
</tr>
<tr>
<td><b>해결</b></td>
<td>역할 → 연령/성별 정보 프롬프트 반영</td>
</tr>
<tr>
<td><b>결과</b></td>
<td>맥락 기반 맞춤 추천 구현</td>
</tr>
</table>

</details>

---

# 🚀 프로젝트 요약 (Summary)

<p>
FamilyLink는 가족 그룹 도메인을 중심으로 설계된 캡스톤 프로젝트입니다.<br><br>
일정 최적화, 프라이버시 보호, 역할 기반 AI 추천 시스템을 통해<br>
단순 기능 앱이 아닌 <b>관계 중심 서비스</b>를 구현하였습니다.
</p>
