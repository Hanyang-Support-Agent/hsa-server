# HSA (Hanyang Support Agent)
  
  한양대학교 ERICA 클라우드캡스톤디자인 프로젝트

  고객 문의를 AI가 자동 분류/응답하고, 관리자가 검토/발송하는 CS 자동화 시스템의 **백엔드 서버**입니다.

  ## 아키텍처

  Frontend (React) <-> Backend (Spring Boot) <-> AI (FastAPI)
                            |
                      PostgreSQL (RDS)

  - **인프라**: AWS ECS Fargate
  - **서비스 디스커버리**: AWS Cloud Map (`hsa-ai.hsa.local`)
  - **CI/CD**: GitHub Actions -> ECR -> ECS 자동 배포

  ## 기술 스택
  
  | 영역 | 기술 |
  |---|---|
  | Language | Java 21 |
  | Framework | Spring Boot 3.4.5 |
  | ORM | Spring Data JPA / Hibernate |
  | DB | PostgreSQL 15 (AWS RDS) |
  | API 문서 | Springdoc OpenAPI (Swagger) |
  | HTTP Client | Apache HttpClient 5 |
  | 빌드 | Gradle |
  | 인프라 | AWS ECS Fargate, ECR, Cloud Map, RDS |
  | CI/CD | GitHub Actions |
  
  ## 도메인 구조

  | 도메인 | 설명 |
  |---|---|
  | `channel` | 채널 관리 (카카오, 메일, 웹) 및 웹훅 접수 |
  | `customer` | 고객 정보 관리 |
  | `inquiry` | 문의 생성/조회, AI 처리 요청/결과 저장 |
  | `response` | AI 답변 초안 및 관리자 최종 응답 관리 |
  | `admin` | 관리자 문의 상세 조회 |
  | `mock` | 테스트용 주문/배송 목데이터 |

  ## 주요 흐름
  
  1. 외부 채널(카카오/메일/웹)에서 웹훅으로 문의 접수
  2. 백엔드가 문의 저장 후 AI 서버에 처리 요청
  3. AI가 문의 분류/답변 초안 생성 후 반환
  4. 자동응답 가능 시 즉시 발송, 아닐 경우 관리자 검토 대기
  5. 관리자가 검토/수정 후 최종 응답 발송
