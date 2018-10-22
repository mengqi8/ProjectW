程序使用说明
===============

## 1. 后端API程序（go语言编写）

### 1.1 安装依赖

    go get -u github.com/gin-gonic/gin
    go get -u github.com/go-ini/ini
    go get -u github.com/go-sql-driver/mysql

### 1.2 将源码编译为可执行程序

如可执行程序已经存在，可忽略此步骤。

#### 1.2.1 编译为windows 64位平台的可执行程序

    CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -o backend.exe backend.go

#### 1.2.2 编译为linux 64位平台的可执行程序

    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o backend backend.go

### 1.3 编辑配置文件

配置文件与可执行程序位于同一目录下，文件名：config.ini，内容如下：

    [db]

    ip = 10.52.200.34
    password = 123456
    username = root
    port = 3306
    dbname = saleanalysis_db

    [server]

    port = 9002

其中db节点下面包含了数据库的ip、用户名、密码、端口、数据库名称的设置。

server节点下面包含了http服务端口设置，程序运行后将在此端口开启http服务。

井号开头的行为注释。

### 1.4 运行程序

在windows系统下，执行backend.exe运行后端API程序。

在linux系统下，执行backend运行后端API程序。

