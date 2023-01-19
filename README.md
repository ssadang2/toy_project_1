# 기차 예약 시스템 with spring framework
---

## 어플 컨셉 및 조건
기본적으로 네이버에서 자체적으로 제공하는 기차 예약 시스템(서비스)을 레퍼런스로 하고 있는 기차 예약 어플(웹 서비스)입니다.

<br>
  
사용자는 당연히 회원가입, 로그인, 로그아웃 할 수 있고, 로그인 했을 때만 예약할 수 있습니다.

사용자는 기본적으로 시간표 검색, 예매할 시간표 선택, 선택된 시간표 열차의 호실 및 좌석 선택, 예매 완료 순으로 예약 서비스를 이용하게 됩니다. 

사용자는 자신의 예약 내역을 마이페이지에서 확인할 수 있다고 예매 티켓의 사용 기간이 지나지 않았다면, 예매 취소를 할 수 있습니다.

<br>

관리자는 admin page를 이용하여 스크립트를 직접 짜서 시간표 데이터를 밀어 넣을 필요없이 만들어진 form에 알맞는 정보만 입력해서 시간표 데이터를 넣을 수 있습니다.

관리자는 모든 시간표를 볼 수 있으며 출발 시간, 도착 시간을 조건으로 시간표를 검색할 수 있습니다.

관리자는 시간표를 삭제할 수 있고, 시간표 별 예약 현황을 확인할 수 있으며, 유사시에 문제가 되는 예약 내역을 임의로 삭제할 수 있습니다.

<br>

api
- 많이 쓰일 것 같은 entity 및 join 데이터 위주로 조회 api controller를 만들어 향후, 이 어플에 붙을 수 있는 front framework와 json 통신을 가능하게 했습니다.

<br>

@PostConstruct를 사용해서 어플 실행시 샘플 데이터를 자동 insert하게 했으므로, application.properties에서 datasource만 사용자의 db에 맞게 설정하면 따로 sample data insert 없이 어플의 기능을 사용하거나 테스트해 볼 수 있습니다.

<br>

원래 iamport api를 도입해서 결제 logic도 구현하려 했는데 구현시 jquery, javascript 코드를 많이 작성해야 했습니다. 그래서 전 결제 전까지의 예약 logic에 집중하는 것이 spring framework를 더 잘 이해하고 배우게 되는 방향이라는 생각이 들어 구현하지 않았습니다.

<br>

가정 및 제약 조건

- 레퍼런스에 따르면 하나의 예약은 하나의 호실에서만 이뤄져야 합니다. 예를 들어 4명이 예약을 하려 하는데 1호차에 2명, 2호차에 2명 이런 식으로 예약할 수 없습니다.
- 이 서비스에서는 ktx, 무궁화호, 새마을호만 다루고 itx-청춘, srt는 배제하였는데, 실제 검색해 보면 나오다시피 itx-청춘, srt는 거의 배차가 없었고 두 열차의 종류를 굳이 추가한다고 해서
무언가 깨달음을 얻을 수 있다고 보기 힘들며 결국엔 단순 반복 작업이므로, 앱과 포트폴리오의 compact함을 위해 생략했습니다.
- 실제로 ktx는 일반실/특실 구분이 있고 무궁화호, 새마을호는 없는데 이 점을 어플에 반영했습니다.
- 여러 블로그 글과 공식 사이트에서 확인 결과 ktx:새마을호:무궁화호의 요금의 비가 1:2/3:1/2 정도인 걸로 확인했고 어플에 이 점을 적용하여 예약시 기차 종류 별로 다른 요금이 적용되게 했습니다.
- 여러 블로그 글과 공식 사이트에서 확인 결과 유아(만 6세 미만, toddler):어린이(만 6-12세, kids):어른(만 13세 이상, adult):경로(만 65세 이상, senior)의 요금의 비가 대략 0.25:0.5:1:0.7로 확인되어 이 점을 어플에 반영했습니다.
- 실제 열차의 호차수를 대략젹으로 반영하여 ktx는 1-10호차, 무궁화호와 새마을호는 1-5호차까지 있다고 가정했습니다.
- 레퍼런스에 따라서 하나의 예약에 10명 이상 예약할 수 없습니다

<br>

## ERD(FINAL VER)
<img width="1061" alt="스크린샷 2023-01-16 오후 2 26 20" src="https://user-images.githubusercontent.com/95601414/212604749-151fffe4-c865-4bcb-bb96-eb578e8a7712.png">

[ERDCloud URL](https://www.erdcloud.com/d/vzbcpBsPGFhpEhbCB)

ktx_seat_normal, ktx_seat_vip, mugunghwa_seat, saemaul_seat에 있는 ...과 skip은 좌석을 의미하는 column이 너무 많아 다 표현하지 않은 것으로

- ktx_seat_normal(ktx 일반실 좌석)은 K1A, K1B, K1C, K1D, K2A ... K14D 이렇게 총 56(column)자리,
- ktx_seat_vip(ktx 특실 좌석)은 K1A, K1B, K1C, K2A, K2B ... K14C 이렇게 총 42(column)자리, 
- mugunghwa_seat(무궁화호 좌석)은 M1, M2, M3, M4 ... M72 이렇게 총 72(column)자리, 
- saemaul_seat(새마을호 좌석)은 S1A, S1B, S1C, S1D, S2A ... S14D 이렇게 총 56(column)자리가 있습니다.

테이블의 역할

- member: 말 그대로 서비스에 가입한 멤버입니다. 권한에 따라 평범한 사용자와 관리자로 나뉩니다.
- reservation: 말 그대로 사용자들의 예약 내역을 담은 테이블로 요금, 예약한 사용자의 id 등의 정보를 가지고 있습니다.  
- passenger: reservation table과 oneToOne 관계로 해당되는 예약의 인원수를 기록한 테이블입니다. 인원과 관련된 통계성 쿼리를 날려야 하는 요구사항이 생길 때 효율적으로 쓰일 것 같습니다. 
- deploy: 시간표에 해당하는 테이블입니다. 출발 시간, 출발 장소 등의 정보를 가지고 있습니다.
- train: 기차에 해당하는 테이블로 ktx, 무궁화호, 새마을호 테이블과 상속관계로 구조가 간단해 단일 테이블 전략으로 상속관계 매핑했습니다. 
- ktx: ktx에 해당되는 테이블입니다.
- mugunghwa: 무궁화호에 해당되는 테이블입니다.
- saemaul: 새마을호에 해당되는 테이블입니다.
- ktx_room: ktx 호실에 해당되는 테이블입니다.
- mugunghwa_room: 무궁화호 호실에 해당되는 테이블입니다.
- saemaul_room: 새마을호 호실에 해당되는 테이블입니다.
- ktx_seat: ktx 좌석에 해당되는 테이블입니다. 일반실과 특실과 상속관계로 조인 전략으로 상속관계 매핑했습니다.
- ktx_seat_normal: ktx 일반실 좌석에 해당되는 테이블입니다.
- ktx_seat_vip: ktx 특실 좌석에 해당되는 테이블입니다.
- mugunghwa_seat: 무궁화호 좌석에 해당되는 테이블입니다.
- saemaul_seat: 새마을호 좌석에 해당되는 테이블입니다.

<br>

## 기술 스택
server side rendering 방식으로 앱을 구성했고 사용한 스택은 아래와 같습니다.

<img width="852" alt="스크린샷 2023-01-18 오후 2 27 32" src="https://user-images.githubusercontent.com/95601414/213091637-15b14d88-b3b8-4f5d-8a0a-027d96f78f90.png">


<br>

## 핵심 기능

1. 회원가입, 로그인, 로그아웃
<img width="1352" alt="스크린샷 2023-01-16 오후 5 10 53" src="https://user-images.githubusercontent.com/95601414/212628615-061c2ab3-9fae-4642-9f41-8ea7e59aaee3.png">
<img width="208" alt="스크린샷 2023-01-16 오후 5 10 26" src="https://user-images.githubusercontent.com/95601414/212628600-39b3521b-e837-4a0f-8865-02f48c4c4a47.png">
<img width="1405" alt="스크린샷 2023-01-16 오후 5 10 33" src="https://user-images.githubusercontent.com/95601414/212628605-9824ba5d-ac3c-4a3d-b56f-21245c97a673.png">
<img width="254" alt="스크린샷 2023-01-16 오후 5 10 42" src="https://user-images.githubusercontent.com/95601414/212628612-752e46c2-d03c-449c-a860-3c30c13ac471.png">

2. 시간표 검색하기
<img width="1440" alt="스크린샷 2023-01-16 오후 5 12 12" src="https://user-images.githubusercontent.com/95601414/212628878-85b2e919-f31e-4f5c-a681-c8e31031700a.png">

3. 예매할 시간표 선택하기

    좌석 선택하기
    
<img width="1424" alt="스크린샷 2023-01-16 오후 5 13 57" src="https://user-images.githubusercontent.com/95601414/212629861-302f1cd7-35c5-42d2-960a-ecce35b3bc2e.png">
<img width="1420" alt="스크린샷 2023-01-16 오후 5 14 08" src="https://user-images.githubusercontent.com/95601414/212629869-abbfb8f5-de9b-47a1-a0ae-c2563e287101.png">

    날짜를 자유롭게 탐색할 수 있음
   
<img width="1323" alt="스크린샷 2023-01-16 오후 5 18 54" src="https://user-images.githubusercontent.com/95601414/212630066-40e0d655-839a-4596-bb99-f305a00296da.png">
<img width="1318" alt="스크린샷 2023-01-16 오후 5 19 02" src="https://user-images.githubusercontent.com/95601414/212630069-9bdbb712-2795-418b-bfcd-d66bdfc71bdf.png">

4. ktx의 경우 일반실/특실 선택하기

<img width="1248" alt="스크린샷 2023-01-16 오후 5 27 28" src="https://user-images.githubusercontent.com/95601414/212631757-5ffad5d8-18b2-48c8-990a-3fc579d54b0d.png">


5. 열차의 호실 및 좌석 선택
    
<img width="1205" alt="스크린샷 2023-01-16 오후 5 23 10" src="https://user-images.githubusercontent.com/95601414/212630941-7f7e0bf4-6c18-452b-89d9-84408264881d.png">
<img width="1300" alt="스크린샷 2023-01-16 오후 5 23 26" src="https://user-images.githubusercontent.com/95601414/212630944-3b10e6b2-29b4-447c-b9f3-43d80fa7c9a3.png">

6. 사용자 마이페이지

    예약 내역을 볼 수 있음
    
<img width="1380" alt="스크린샷 2023-01-16 오후 5 24 21" src="https://user-images.githubusercontent.com/95601414/212631155-aa4ab103-93fb-46b7-b64c-2f16f7bb9e1c.png">

    예약을 삭제할 수 있음
    
<img width="953" alt="스크린샷 2023-01-16 오후 5 25 52" src="https://user-images.githubusercontent.com/95601414/212631444-8ff70774-4a60-4c2c-9f47-cc0093ef730a.png">
 
7. 관리자 마이페이지
    출발 시간, 도착 시간을 조건으로 시간표를 검색할 수 있음(queryDsl을 사용한 동적 query)
    
<img width="1338" alt="스크린샷 2023-01-16 오후 5 31 41" src="https://user-images.githubusercontent.com/95601414/212633407-6a7860b6-a521-4d0a-9579-9576f6f7c6a8.png">

    
    시간표를 입력할 수 있음
    
<img width="704" alt="스크린샷 2023-01-16 오후 5 32 25" src="https://user-images.githubusercontent.com/95601414/212633616-f2be5294-01c6-4cb5-98c4-bdaf180796bf.png">

    모든 시간표를 볼 수 있음
    
<img width="694" alt="스크린샷 2023-01-16 오후 5 32 45" src="https://user-images.githubusercontent.com/95601414/212633649-141e0dac-aa6a-434d-9f3d-7af1c21547c4.png">

    각 시간표의 예약 현황을 볼 수 있음
    
<img width="1409" alt="스크린샷 2023-01-16 오후 5 32 53" src="https://user-images.githubusercontent.com/95601414/212633665-1d4b5ce6-ddf6-4780-8b0f-7d381ab884df.png">

    예약을 임의로 삭제할 수 있음
    
<img width="1431" alt="스크린샷 2023-01-16 오후 5 36 19" src="https://user-images.githubusercontent.com/95601414/212633676-2ab86559-b0c9-4a68-a822-c573a38336cc.png">

<br>

## test case(세부 로직)

(네이버 기차 예약 레퍼런스)라고 적혀 있는 것의 의미는 해당 기능 및 테스트를 수행한 이유이자 근거가 차용한 레퍼런스에 있다는 뜻입니다.

로그인 기능
- 로그인 기능이 잘 동작하여 세션이 유지됨 :ok_hand:
- 로그아웃(invalidate)이 잘 동작함 :ok_hand:
- 로그인 했을 때 상단의 메뉴가 로그인, 회원가입에서 마이페이지, 로그아웃으로 바뀜 :ok_hand:

로그인 할 때 validation
- notBlank 잘 동작함 :ok_hand:
- 아이디 혹은 비밀번호가 틀렸을 때 validation이 잘 동작함 :ok_hand:

회원가입 기능
- notBlank 잘 동작함 :ok_hand:
- 나이 칸에 문자를 넣었을 때 typeMismatch validation 잘 동작함 :ok_hand:

인터셉터 기능(로그인 인증 체크)
- 세션이 없으면 login page로 redirect 됨 :ok_hand:
- 세션이 없어 Login page로 왔을 때, 로그인 성공 시 원래 가려고 했던 페이지로 Redirect 됨 :ok_hand:

스프링부트 오류페이징 기능
- 403: 미인가 사용자의 접근 차단함 :ok_hand:
- 404: 존재하지 않는 Resource에 대한 접근을 차단함 :ok_hand:
- 405: method not allowed :ok_hand:

기차 조회 예매 조건 검색 시 validation
- 출발지 또는 도착지가 없을 때 validation 동작함 :ok_hand:
- 출발지 또는 도착지가 실존하는 역명이 아닐 때 validation 동작함 :ok_hand:
- 출발지와 도착지가 같을 때 validation 동작함 :ok_hand:
- 오는 날이 가는 날보다 빠르거나 같을 때 validation 동작함 :ok_hand:
- 예약 인원수가 0명일 때 validation 동작함 :ok_hand:
- 편도인데 가는 날이 없을 때 validation 동작함 :ok_hand:
- 왕복을 선택했는데 오는 날을 입력하지 않았을 때 validation 동작함 :ok_hand:
- 하나의 예약에서 9명이 넘는 인원수를 선택했을 때 (네이버 기차 예약 레퍼런스) validation 동작함 :ok_hand:

시간표 선택할 때 
- 검색 시간이 1시간 단위라서 예를 들어 15:00 이후 기차를 검색했고 현재 시간이 15:40이며 15:20에 출발하는 기차가 있다면 15시 이후라는 것에만 집중하면 15:20에 출발하는 기차가 사용자에게 노출되어야 맞지만 지나간 시간대는 노출 안 하는 게 대원칙이자 당연한 것이기 때문에 백엔드에서 계산후 걸러줌 (네이버 기차 예약 레퍼런스) :ok_hand:
- 화살표로 날짜를 앞뒤로 이동시킬 수 있으나 예약 가능 시간대는 오늘(분 제외 현재 시간(hh:00) 이후)부터 대략 1달이기 때문에 그 기간이 넘어가면 시간표를 볼 수 없음(네이버 기차 예약 레퍼런스) :ok_hand:

- 편도일 떄
    - 날짜 이동 버튼에 부합하는 날짜 이동이 되어야 하며, 해당 날짜와 출발지, 도착지 등의 조건에 맞고 db에 있는 실제 예약 내역이 보여야 함 :ok_hand:
    - ktx 같은 경우 일반실 예약실이 있기 때문에 예를 들어 일반실만 남은 자리의 문제로 예약이 불가하다면 일반실은 불가, 특실은 가능하다고 표시되어야 하고 만약 둘 다 예약이 불가라면 아예 예약 버튼을 disable하여 사용자가 진입하지 못하도록 해야 한다 :ok_hand:
    - 무궁화나 새마을 같은 경우 일반실 특실이 없어 구분없이 자리가 부족하다면 버튼을 disabled하여 사용자가 진입하지 못하도록 해야 된다 :ok_hand:
    - 출발 시간에 따라 예약 내역을 정렬했는지 확인(네이버 기차 예약 레퍼런스) :ok_hand:
    - 만약 예약할 수 있는 시간표가 없다면 좌석 선택하기 버튼은 사라져야 됨 :ok_hand:
    
- 왕복일 때
    - 가는 날: 날짜 이동 버튼에 부합하는 날짜 이동이 되어야 하며, 해당 날짜와 출발지, 도착지 등의 조건에 맞고 db에 있는 실제 예약 내역이 보여야 함 :ok_hand:
    - 오는 날: 날짜 이동 버튼에 부합하는 날짜 이동이 되어야 하며, 해당 날짜와 출발지, 도착지 등의 조건에 맞고 db에 있는 실제 예약 내역이 보여야 함 :ok_hand:
    - ktx 같은 경우 일반실 예약실이 있기 때문에 예를 들어 일반실만 남은 자리의 문제로 예약이 불가하다면 일반실은 불가, 특실은 가능하다고 표시되어야 하고 만약 둘 다 예약이 불가라면 아예 예약 버튼을 disable하여 사용자가 진입하지 못하도록 해야 함 :ok_hand:
    - 무궁화나 새마을 같은 경우 일반실 특실이 없어 구분없이 자리가 부족하다면 버튼을 disabled하여 사용자가 진입하지 못하도록 해야 된다 :ok_hand:
    - 출발 시간에 따라 예약 내역을 정렬했는지 확인(네이버 기차 예약 레퍼런스) :ok_hand:
    - 만약 가는 날 또는 오는 날에 예약할 수 있는 시간표가 없다면 좌석 선택하기 버튼은 사라져야 됨 :ok_hand:

일반실 특실 선택 및 좌석 선택

**편도**

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함

위 케이스에 대하여 2가지 경우 모두 봐야 됨
- 일반실일 때 :ok_hand:
- 특실일 때 :ok_hand:

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

**왕복**

ktx -> 무궁화

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함

위 케이스에 대하여 2가지 경우 모두 봐야 됨
- 일반실일 때 :ok_hand:
- 특실일 때 :ok_hand:

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

ktx -> 새마을

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함

위 케이스에 대하여 2가지 경우 모두 봐야 됨
- 일반실일 때 :ok_hand:
- 특실일 때 :ok_hand:

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

ktx -> ktx

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함

위 케이스에 대하여 4가지 경우 모두 봐야 됨
- 일반실 -> 일반실일 때 :ok_hand:
- 일반실 -> 특실일 때 :ok_hand:
- 특실 -> 일반실일 때 :ok_hand:
- 특실 -> 특실일 때 :ok_hand:

무궁화 -> ktx

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함

위 케이스에 대하여 2가지 경우 모두 봐야 됨
- 일반실일 때 :ok_hand:
- 특실일 때 :ok_hand:

무궁화 -> 새마을

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

무궁화 -> 무궁화

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함

새마을 -> ktx

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:

ktx
- ktx는 일반실과 특실이 나뉘기 때문에 좌석 선택 전에 일반실/특실부터 선택하게 해야 함
- 만약 남은 자리 부족의 문제로 일반실 혹은 특실을 예약할 수 없다면 button disable을 통해 해당 실에 들어가지 못하게 해야 함  
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음
- 호차 간의 이동이 제대로 작동함 
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함

위 케이스에 대하여 2가지 경우 모두 봐야 됨
- 일반실일 때 :ok_hand:
- 특실일 때 :ok_hand:

새마을 -> 무궁화

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:

무궁화
- 무궁화호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

새마을 -> 새마을

새마을
- 새마을호는 특실 일반실이 없기 때문에 바로 좌석 선택 페이지로 가게 해야 함 :ok_hand:
- 좌석 선택 페이지에서 남은 자리 부족의 문제로 예약 불가한 호차가 있다면 접근하지 못하게 button disable해야 함 :ok_hand:
- 예약된 좌석이라면 좌석을 중복 예약할 수 없게 미리 disabled 처리가 되어 있음 :ok_hand:
- 호차 간의 이동이 제대로 작동함 :ok_hand:
- 좌석을 고를 때 선택한 인원수에 맞게 선택하지 않으면 validation이 동작함 :ok_hand:
- 예약 완료시 사용자 마이페이지에서 자신이 선택한 내용과 알맞은 예약 티켓이 보여야 함 :ok_hand:

myPage 진입 시 member의 authorization에 따라 다른 page가 보여야 됨 :ok_hand:

사용자 myPage Logic
- 해당 사용자의 예약 내역이 예약한 순서대로 잘 보여야 됨 :ok_hand:
- 사용 기간이 만료된 예약이라면 예매 취소 불가, 사용 기간이 남았다면 예매 취소 가능이라는 표시가 되어야 함 :ok_hand:
- 예약 삭제 Logic
  - 예약 삭제 시 reservation, passenger entity가 제대로 삭제됨 :ok_hand:
  - 이미 출발 시간이 지나 사용 기간이 만료된 예약 내역은 삭제 버튼을 disable하게 해 삭제하지 못하게 함 :ok_hand:

관리자 myPage Logic
- 시간표 검색하기(동적 query) 기능이 제대로 동작함 :ok_hand:

- 시간표 추가하기 validation
  - Not blank 잘 동작함 :ok_hand:
  - 시간의 fotmat(HH:SS)이 맞지 않을 때 validation이 잘 동작함 :ok_hand:
  - 역을 제대로 입력하지 않았을 때 validation이 잘 동작함 :ok_hand:
  - 이상한 열차 이름이 입력됐을 때 validation이 잘 동작함 :ok_hand:
  
* 시간표(Deploy) 관련 Logic
  - 한 명이라도 해당되는 시간표에 예약을 했으면 그 시간표는 삭제될 수 없음(via disabled button) :ok_hand:
  - 예약 현황 버튼을 누르면 해당 deploy의 예약 내역들이 보여야 됨 :ok_hand:
  - X 버튼을 해당 예약 내역을 삭제할 수 있음 :ok_hand:
  
@PostConstructor를 사용한 샘플 데이터 자동 insert(InitSampleData)
- member(user, admin)이 의도한 대로 잘 들어갔음 :ok_hand:
- 시간표(deploy)가 의도한 대로 잘 들어갔음 :ok_hand:
- 기차(train)가 의도한 대로 잘 들어갔음 :ok_hand:
- 예약(reservation)가 의도한 대로 잘 들어갔음 :ok_hand:
- 승객(passenger)가 의도한 대로 잘 들어갔음 :ok_hand:

- ktx room이 의도한 대로 잘 들어갔음
- mugunghwa room이 의도한 대로 잘 들어갔음 :ok_hand:
- saemaul room이 의도한 대로 잘 들어갔음 :ok_hand:
- ktx seat이 의도한 대로 잘 들어갔음 :ok_hand:
- ktx normal seat이 의도한 대로 잘 들어갔음 :ok_hand:
- ktx vip seat이 의도한 대로 잘 들어갔음 :ok_hand:
- mugunghwa seat이 의도한 대로 잘 들어갔음 :ok_hand:
- saemaul seat이 의도한 대로 잘 들어갔음 :ok_hand:

조회 api (api이기 default로 dto 반환)
- deploy(시간표) 관련 조회 api(No join)
    * 동적 쿼리로 시간표 검색(출발 시간, 도착 시간 조건) + paging 가능 :ok_hand:
- member 관련 조회 api(oneToMany)
    * member와 reservation을 fetch join but oneToMany 관계라 no paging :ok_hand:
    * paging 가능한 member만 조회 후, reservation 초기화 시 batch fetch 기능으로 최적화 :ok_hand:
    * paging 가능한 dto 조회 + 메모리 작업으로 1+N => 1+1으로 최적화 :ok_hand:
- reservation(예약) 관련 조회 api(ManyToOne)
    * join 없이 findAll + paging 가능 :ok_hand:
    * join 없이 dto 조회 후 + paging 가능 :ok_hand:
    * fetch join 후 findAll + paging 가능 :ok_hand:
    * fetch join 후 dto 조회 + paging 가능 :ok_hand:
- ktx 관련 조회 api(OneToMany)
    * ktx, ktx room, ktx seat을 fetch join but oneToMany 관계라 no paging :ok_hand:
    * paging 가능한 ktx 조회 후, ktx room, ktx seat 초기화 시 batch fetch 기능으로 최적화 :ok_hand:
- mugunghwa(무궁화호) 관련 조회 api(OneToMany)
    * mugunghwa, mugunghwa room, mugunghwa seat을 fetch join but oneToMany 관계라 no paging :ok_hand:
    * paging 가능한 mugunghwa 조회 후, mugunghwa room, mugunghwa seat 초기화 시 batch fetch 기능으로 최적화 :ok_hand:
- saemaul(새마을호) 관련 조회 api(OneToMany)
    * saemaul, saemaul room, saemaul seat을 fetch join but oneToMany 관계라 no paging :ok_hand:
    * paging 가능한 saemaul 조회 후, saemaul room, saemaul seat 초기화 시 batch fetch 기능으로 최적화 :ok_hand:
- 에러가 났을 때(controllerAdvice)
    * 시간표 search 동적 query를 날릴 때 출발 시간, 도착 시간이 스펙에 안 맞으면 개발자 미리 지정한 객체를 잘 render함 :ok_hand:

컨버터
- 날짜와 시간을 문자열이 아닌 LocalDateTime type으로 바로 받기 위한 컨버터가 잘 작동함 :ok_hand:

comparator
- 시간표를 출발 시간으로 1차 정렬하고 열차의 종류에 따라(Ktx, 무궁화호, 새마을호 순) 2차 정렬하는 것이 잘 작동함 :ok_hand:

jquery 기능 
- 기차역 입력 시 미리 넣어 놓은 기차역을 대상으로 auto complete(자동 완성) 기능이 잘 작동함 :ok_hand:
- 기차 시간표 볼 때, 모든 시간표가 한 번에 나오는 게 아니라 더보기 버튼을 클릭 해 순차적으로 나오게 한 게 잘 동작함 :ok_hand:

<br>

## 문제점 및 해결 방안
**problem solving 1. oneToOne 관계에서 연관관계 주인이 아닌 쪽을 select 시, 쿼리가 데이터의 개수만큼 나가는 문제**

<img width="695" alt="스크린샷 2023-01-18 오후 4 06 20" src="https://user-images.githubusercontent.com/95601414/213106361-70323371-514d-49ad-8d14-da11af906ca9.png">

보이는 것처럼 train과 Deploy는 oneToOne 관계이며 train은 연관관계의 주인이 아닙니다. 

<img width="207" alt="스크린샷 2023-01-18 오후 4 36 54" src="https://user-images.githubusercontent.com/95601414/213111457-6a7ed9d1-7f3b-4493-8512-5d8f1f6e462e.png">
<img width="583" alt="스크린샷 2023-01-18 오후 4 10 22" src="https://user-images.githubusercontent.com/95601414/213107799-727b594b-bef8-40fb-9fde-053d41b74b92.png">

이 때, train을 findAll() 하기만 해도 마치 N+1 problem처럼 train 데이터의 개수 만큼 연관된 deploy를 찾는 쿼리가 나가게 됩니다. 이는 oneToOne 관계에서 당연한 것이므로 양방향 연관관계를 맺지 않으면 해결되는 문제이나 해당 양방향을 단방향으로 바꾸면 기존 구조에 꽤 많은 수정이 있어야 했고 전 언젠가 실무에서도 oneToOne 양방향은 써야 되는데 저런 N+1 같은 쿼리가 나가게 되는 상황이 나올 수도 있다고 생각하여 매핑 관계나 구조의 수정 없이 나름 합리적으로 이 문제를 해결하려 했습니다.

<img width="234" alt="스크린샷 2023-01-18 오후 4 35 47" src="https://user-images.githubusercontent.com/95601414/213111279-dff937a6-8324-4e73-ada6-45db7bdf2891.png">
<img width="998" alt="스크린샷 2023-01-18 오후 4 35 53" src="https://user-images.githubusercontent.com/95601414/213111283-ede1104c-5e34-46d4-980e-96f595c6bb84.png">

그래서, 저는 deploy가 train의 데이터의 개수만큼 하나씩 나가는 걸 막고자 train 관련 로직 전에 deploy를 미리 긁어오는 코드를 넣었습니다. 이를 통해, 쿼리가 마치가 N+1처럼 나가던 것을 1+1로 줄이게 되었습니다. 그리고, @transactional이 걸려 있는 service 밖에서 쿼리 2개를 날렸는데 마치 2개의 쿼리가 하나의 영속성 컨텍스트를 쓰는 것 처럼 미리 긁어 오는 작업이 유효한데, 그 이유는 open session in view가 켜져 있어 서비스 단을 벗어나도 영속성 컨텍스트가 db connection을 물고 살아 있기 때문입니다. 만약 osiv 설정을 끄면 위와 같은 미리 당기기 작업은 원하는대로 동작하지 않게 됩니다.

참고로 예시 코드에서는 데이터가 적어 deploy를 바로 findAll()로 찾아 왔지만 데이터가 많아지거나 쿼리의 최적화가 요구된다면 해당되는 train들의 아이디만을 받아와서 where 조건에 넣어 퍼올리는 데이터의 양을 줄이는 최적화를 할 수 있을 것 같습니다.

<br>

**problem solving 2. Comparator를 통한 이중 정렬**

<img width="852" alt="스크린샷 2023-01-18 오후 4 49 33" src="https://user-images.githubusercontent.com/95601414/213113747-25c90dae-9267-4dfb-8ede-90b4dc5e35ab.png">

저는 레퍼런스에 따라 기차 시간표를 출발 시간 순서로 보여주기로 했습니다. 근데, 만약 출발 시간이 같으면서 열차 종류가 다른 시간표가 있다면 이들을 단순히 db에서 긁어온 순서로 보여주고 싶지 않았고 어떠한 기준에 의거해 보여주고 싶었습니다. 

따라서, 저는 우선 출발 시간 순으로 정렬하고, 출발 시간이 같다면 ktx -> 무궁화호 -> 새마을호 순으로 정렬하자는 임의의 기준을 세웠습니다. 이를 구현하기 위해 정보를 찾던 중 List<E>의 정렬을 가능하게 해주는 Comparator 객체를 알게 됐고, 1차 정렬은 출발 시간으로 하고 2차 정렬은 ktx, 무궁화호, 새마을호 순으로 하게 하는 comparator를 구현해 위 문제를 해결했습니다.

<br>

**problem solving 3. 리플렉션 기술을 활용한 동적 로직 작성**

예약 로직 중에서 seat entity를 대상으로 예약이 완료된 자리를 update set true(자리 체크) 하는 로직이 있는데, 예약이 왕복일 경우에 이 문제를 해결하기 참 힘들었습니다. 왜냐하면 편도의 경우 사용자가 체크한 좌석 내역을 @modelAttribute로 dto에 바로 받으면 그 dto를 어떻게든 seat entity에 반영하면 끝납니다. 

하지만, 왕복일 경우에는 가는 날에 고른 좌석 내용을 오는 날 좌석 선택이 끝나서 예약 entity를 persist할 때까지 네트워크를 타고 돌아 다녀야 합니다. 그래서 전 처음에 가는 날 좌석 dto를 넘기는 식으로 해결하고자 했는데 이 방법에는 치명적인 결함이 있었습니다. 왜냐하면 결국에 reservation controller에 공유 변수를 만들어 가는 날 좌석을 거기에 담아야 하는데, 당연하게도 공유 변수는 멀티쓰레드 환경에서 동시성 문제를 야기합니다. 따라서, 가는 날 좌석 Dto를 담는 변수를 ThreadLocal로 감싸야 하는데, 아시다시피 threadLocal은 쓰레드 별로 보관함을 할당합니다. 따라서, 가는 날 로직을 끝내고 다시 http 요청으로 오는 날 로직을 수행할 때 같은 쓰레드를 쓴다는 보장이 없습니다. 결론적으로, 공유 변수를 써야 하지만 동시에 ThreadLocal은 쓰지 못하는 상황이기에 위 방법으로는 해결할 수 없다고 판단했습니다. 그리고 공유 변수를 쓰지 않고 문제 상황을 해결할 수도 있겠지만 그렇게 된다면 네트워크를 타야 하는 데이터가 너무 많아지고 코드의 집약성도 떨어진다고 판단하여 다른 방법을 찾았습니다.

<img width="928" alt="스크린샷 2023-01-19 오후 8 44 37" src="https://user-images.githubusercontent.com/95601414/213434158-e2f63bbf-5583-420c-a222-c63f112205a7.png">

그러다가 리플렉션을 이 상황에서 써보면 어떨까 하는 생각이 들었습니다. Dto 자체를 네트워크로 전달할 필요 없이 가는 날에 선택된 좌석들을 한 줄의 문자열로 받아 자리 체크를 해야 되는 순간에 해당 seat entity의 setter 메소드를 호출하여 setK + "받은 좌석 이름" 이런 식으로 동적으로 자리를 체크하려 했습니다. 결과적으로, 무거운 dto를 네트워크로 주고 받을 필요 없이 문자열 한 줄로 해당 문제를 해결했고 리플렉션을 써서 checkSeats 하는 부분이 도메인 안에 있어 코드의 집약성도 올라갔습니다. 

<img width="935" alt="스크린샷 2023-01-19 오후 8 50 58" src="https://user-images.githubusercontent.com/95601414/213435286-f5e6b341-b3d2-4c5a-89c9-6db14a452e4a.png">

추가적으로 편도일 때 dto를 Entity에 반영할 때도 리플렉션 기술을 쓰게 되면 O(n^2) 걸리던 작업을 O(n)으로 할 수 있게 됩니다.

<br>

## reference
<img width="682" alt="스크린샷 2023-01-16 오후 2 16 39" src="https://user-images.githubusercontent.com/95601414/212604760-0c5da0d3-19b9-43de-887f-6de11cd32f7b.png">
     
네이버에 검색하면 나오는 기차 예약 페이지 및 예약 로직 및 서비스
[URL](https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EA%B8%B0%EC%B0%A8+%EC%98%88%EC%95%BD)



