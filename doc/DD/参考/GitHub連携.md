全リポジトリ一覧を取得
GET /users/{username}/repos
https://api.github.com/users/yuzuasan/repos

---

各リポジトリごとにコミット数取得
GET /repos/{owner}/{repo}/commits
https://api.github.com/repos/yuzuasan/taskapp/commits

日付指定
?since=2024-01-01T00:00:00Z
&until=2024-12-31T23:59:59Z
&author=USERNAME

---

※注意※
認証しないとレート制限がきつい

レート制限の確認
GET /rate_limit
https://api.github.com/rate_limit

---

GitHub API の主な認証方法

① Personal Access Token（PAT）← 最も簡単
最初はこれでOKです。

手順
1. GitHubでトークンを発行
   Settings → Developer settings → Personal access tokens

2. 必要なスコープを選択

   * 公開リポジトリだけ → `public_repo`
   * 非公開含む → `repo`

3. トークンを取得

---

Spring Boot での呼び出し例

RestTemplate版

```java
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + token);
headers.set("Accept", "application/vnd.github+json");

HttpEntity<String> entity = new HttpEntity<>(headers);

ResponseEntity<String> response = restTemplate.exchange(
    "https://api.github.com/users/{username}/repos",
    HttpMethod.GET,
    entity,
    String.class,
    username
);
```

---

WebClient（推奨）

```java
WebClient webClient = WebClient.builder()
    .baseUrl("https://api.github.com")
    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
    .build();

String repos = webClient.get()
    .uri("/users/{username}/repos", username)
    .retrieve()
    .bodyToMono(String.class)
    .block();
```
