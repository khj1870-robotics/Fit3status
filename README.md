# Fit3 상태 문자

Galaxy Fit3의 고정 알림에서 빠른 답장을 선택하면 지정한 전화번호로 SMS를 전송하는 Android 앱입니다.

## 사용 흐름

1. 앱에서 상대방 전화번호를 저장하고 고정 알림을 켭니다.
2. Galaxy Wearable의 앱 알림에서 `Fit3 상태 문자`를 허용합니다.
3. Fit3에서 `연인에게 상태 보내기` 알림을 엽니다.
4. `상태 보내기`를 누르고 빠른 응답 문구를 선택합니다.
5. 앱이 해당 문구를 SMS로 전송하고 고정 알림을 다시 유지합니다.

## 권장 빠른 응답 문구

- 그냥 생각나서 콕
- 집에 있어
- 다른 일 하는 중이야
- 씻고 잘 준비 중이야
- 조금 있다 연락할게
- 오늘은 먼저 잘게

빠른 응답 문구는 Galaxy Wearable 앱의 빠른 응답 관리 화면에서 편집합니다.

## 빌드

Android Studio에서 이 폴더를 열고 Gradle 동기화 후 실행합니다.

- `compileSdk`: 35
- `minSdk`: 26
- 외부 라이브러리 없음

APK 생성:

```text
Build → Build APK(s)
```

생성 위치:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub에서 APK 자동 빌드

프로젝트를 GitHub 저장소 최상위에 올리면 `Build APK` 작업이 자동 실행됩니다.

```text
GitHub 저장소 → Actions → Build APK → 실행 결과 → Artifacts
```

`Fit3StatusSMS-debug` 파일을 내려받아 압축을 풀면 `app-debug.apk`가 들어 있습니다.

## 주의사항

- 첫 실행 시 알림 권한과 SMS 권한을 모두 허용해야 합니다.
- 듀얼 SIM을 사용하면 갤럭시폰에서 기본 문자 SIM을 먼저 지정하는 것이 좋습니다.
- 통신사 요금제에 따라 SMS 요금이 부과될 수 있습니다.
- Fit3가 사용자 정의 `RemoteInput` 알림을 빠른 답장 대상으로 표시하는지는 펌웨어에 따라 다를 수 있으므로 먼저 시험해야 합니다.
- Google Play 배포는 SMS 권한 정책의 제한을 받습니다. 개인용 APK 설치를 전제로 한 프로젝트입니다.
