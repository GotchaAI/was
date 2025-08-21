from locust import TaskSet, task

class LoginScenario(TaskSet):
    token = None

    @task
    def login_and_store_token(self):
        res = self.client.post(
            "/api/auth/sign-in",  
            json={
                "email": "test@gmail.com",
                "password": "asdfasdf1"
            }
        )
        if res.status_code == 200:
            self.token = res.json().get("accessToken")  # 응답 구조에 맞게 key 확인
            print(f"[성공] 토큰: {self.token}")
        else:
            print(f"[실패] 로그인 실패: {res.status_code} / {res.text}")
