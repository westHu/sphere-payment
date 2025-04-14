# sphere-trade

    交易系统

## 组件构成

* **sphere-start**：
* **sphere-api**：
* **sphere-application**：
* **sphere-domain**：
* **sphere-infrastructure**：
* **sphere-share**：



### 方法名约定

| CRUD操作   | 方法名约定                                             |
|----------|---------------------------------------------------|
| 新增       | create                                            |
| 添加       | add                                               |
| 删除       | remove（application和Domain层），delete（Infrastructure |
| 修改       | update                                            |
| 查询（单个结果） | get                                               |
| 查询（多个结果） | list                                              |
| 分页查询     | page                                              |
| 统计       | count                                             |

### 命名规范

| 规范               | 用途                  | 解释                                    |
|------------------|---------------------|---------------------------------------|
| xxxCO            | Client Object       | 客户对象，用于传输数据，等同于DTO                    |
| xxxCmd           | Client Request      | Cmd代表Command，表示一个写请求                  |
| xxxQuery         | Client Request      | Query，表示一个读请求                         |
| xxxCmdExe        | Command Executor    | 命令模式，每一个写请求对应一个执行器                    |
| xxxQueryExe      | Query Executor      | 命令模式，每一个读请求对应一个执行器                    |
| xxxVO            | Value Object        | 值对象                                   |
| xxxEntity        | Entity              | 领域实体                                  |
| xxxDO            | Data Object         | 数据对象，用于持久化                            |
| xxxInterceptor   | Command Interceptor | 拦截器，用于处理切面逻辑                          |
| IxxxService      | API Service         | xxxServiceI                           |
| xxxDomainService | Domain Service      | 需要多个领域对象协作时，使用DomainService           |
| xxxValidator     | Validator           | 校验器，用于校验的类                            |
| xxxAssembler     | Assembler           | 组装器，DTO <---> Entity，用于Application层   |
| xxxConvertor     | Convertor           | 转化器，Entity <---> DO，用于Infrastructure层 |



## 待办列表

- [x] job
- [x] 导出