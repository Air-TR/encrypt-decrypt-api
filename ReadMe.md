## 项目使用 EncryptRequestFilter / ControllerRequestAdvice 处理解析加密请求
### (1) ControllerRequestAdvice 需解决只拦截接口参数被 @RequestBody 定义的请求，否则不拦截
### (2) 需保证一个请求不会既被 Filter 处理，又被 Advice 处理（两者只被其中之一拦截处理）

## 使用 ControllerResponseAdvice 控制响应数据加密返回
### 两种控制方式：(1) 全局控制 (2) 指定控制