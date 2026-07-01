# Bills

Bills 是一个基于 Spring Boot 的单机账单导入工具。项目使用 JDK 21、Maven 3.9、MyBatis 和 H2，前端页面与 H2 数据库能力都随 Spring Boot 应用一起启动，打包后只需要运行一个 Jar 包即可使用。

项目定位为本地单机工具，不提供用户、角色、登录、注册、认证或权限管控能力。

## 技术栈

- JDK 21
- Maven 3.9+
- Spring Boot 3.4.1
- MyBatis 3
- H2 文件数据库
- Apache POI
- 原生 HTML/CSS/JavaScript 前端

## 单 Jar 运行方式

构建：

```bash
./mvnw clean package
```

启动：

```bash
java -jar target/bills-0.0.1-SNAPSHOT.jar
```

启动后访问：

- 前端页面：http://localhost:8080/
- H2 控制台：http://localhost:8080/h2-console
- 账单导入接口：`POST http://localhost:8080/bill/import`

H2 默认使用文件数据库，数据会写入启动目录下的 `data/billsdb`。首次启动时，应用会自动执行 `schema.sql` 创建所需表结构。
H2 控制台仅用于本地查看和维护内嵌数据库，不属于应用用户、角色或权限体系。

可以通过环境变量修改数据目录：

```bash
BILLS_DATA_DIR=/path/to/data java -jar target/bills-0.0.1-SNAPSHOT.jar
```

## 导入文件格式

前端页面支持上传 `.csv`、`.xls`、`.xlsx` 文件。第一行需要包含以下表头，顺序不限：

```text
账本,分类,子分类,货币,金额,账户,记录人,日期,时间,标签,备注,收支
```

仓库内置示例文件：

```text
src/main/resources/Budget.csv
```

## API

### 导入账单

```http
POST /bill/import
Content-Type: multipart/form-data
```

表单字段：

- `file`：CSV、XLS 或 XLSX 账单文件

成功响应示例：

```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {
    "totalRows": 10,
    "importedRows": 10,
    "skippedRows": 0
  }
}
```