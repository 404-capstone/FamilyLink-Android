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
