# AICanRead

**项目网址**: [http://1.92.81.128](http://1.92.81.128)
![输入图片说明](Nacos/QQ%E6%88%AA%E5%9B%BE20240610192334.png)

**后端开发**: [AICanRead GitHub 项目](https://gitee.com/jiwmn/aican-read)

## 项目简介

AICanRead是一个使用分布式架构和消息队列的AI分析平台。用户可以根据提示和上传的文件进行智能分析，并通过表格或图表形式直接展示数据。

## 功能模块

### 后端功能

1. **异步调用服务**:
   - 使用自定义线程池将调用AI的服务异步化，提升用户体验。

2. **消息队列处理**:
   - 使用RabbitMQ接收并持久化任务消息并和模块解耦，提升系统的可靠性。

3. **数据解析**:
   - 使用Easy Excel解析用户上传的XLSX表格数据文件并压缩为CSV，节约成本。

4. **请求负载均衡**:
   - 使用GateWay整合Nacos对请求进行负载均衡，减少单台服务器压力。服务之间使用Dubbo和Zookeeper进行远程服务调用对请求进行负载均衡，减少单台服务器压力。
![输入图片说明](Nacos/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202024-06-10%20102440.png)![输入图片说明](Nacos/QQ%E6%88%AA%E5%9B%BE20240610192334.png)
5. **分布式锁**:
   - 使用Redisson实现分布式锁，保证多用户同时注册时用户名不重复。

6. **域名转换与负载均衡**:
   - 使用Nginx对请求进行负载均衡，转发实现域名转换。

### 前端功能

1. **脚手架与项目模板**:
   - 基于Ant Design Pro脚手架快速构建项目，并根据业务定制项目模板，如封装全局异常处理逻辑。

2. **自动生成代码**:
   - 使用Umi OpenAPI插件，根据后端Swagger接口文档自动生成请求service层代码，大幅提高开发效率。

3. **图表展示**:
   - 选用兼容性较好的Echarts库，接收后端AI生成的动态JSON自动渲染可视化图表。

## 部署指南

### 后端部署

1. 确保安装Java和Maven。
2. 克隆项目代码:
   ```bash
   git clone https://gitee.com/jiwmn/aican-read.git
   ```
3. 进入项目目录并编译:
   ```bash
   cd aican-read
   mvn clean install
   ```
4. 配置应用程序配置文件（如application.yml），确保RabbitMQ、Nacos、Redis等服务正确配置。
5. 启动应用:
   ```bash
   mvn spring-boot:run
   ```

### 前端部署

1. 确保安装Node.js和Yarn。
2. 进入前端项目目录:
   ```bash
   cd aican-read-frontend
   ```
3. 安装依赖:
   ```bash
   yarn install
   ```
4. 配置前端项目（如config/config.js），确保后端API接口正确配置。
5. 启动开发服务器:
   ```bash
   yarn start
   ```

## 使用说明

1. 访问项目网址: [http://1.92.81.128](http://1.92.81.128)
2. 根据提示上传文件或输入数据。
3. 查看AI分析结果并以图表或表格形式展示。
