from locust import HttpUser, between
from scenario.login import LoginScenario

class WebsiteUser(HttpUser):
    tasks = [LoginScenario]
    wait_time = between(1, 3)
