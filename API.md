归属地及中台API
===============

下文提供的API地址中的IP和端口仅供测试使用，请勿写死到代码中。

- IP: 10.52.200.46
- 归属地查询API端口：8801
- 中台系统API端口：9002

## 1. 归属地查询（go）

api返回字段：result、error

GET <http://10.52.200.46:8801/api/number/15533333333>

## 2. 中台系统API（go）

api返回字段：result、error，可根据需要，增加返回码字段，具体返回码字段含义需要和刘兵辉沟通确认

### 2.1 API服务健康度检查，永远返回ok

GET <http://10.52.200.46:9002/api/ping>

### 2.2 返回20条状态为可用的号码资源：

GET <http://10.52.200.46:9002/api/number/random>

### 2.3 订单查询（已废弃）

GET <http://10.52.200.46:9002/api/order/list>

### 2.4 根据不同的输入条件，查询订单信息

根据不同的查询条件，查询订单表（orderdetail）的信息。

查询条件：

- 订单号（orderdetail.order_id）
- 或 联系电话（customer.contact_phone）
- 或 要办理的手机号（service_num.svc_num）
- 或 订单状态（orderdetail.status）

API可能会返回0个或多个结果

API返回举例：

    { result :
        [{
            customer_id: xxx,           客户id
            customer_name: "xxx",       客户姓名
            cert_num: "xxx",            身份证号
            contact_phone: "xxx",       联系方式

            order_id: xxx,              订单id
            order_province: "xxx",      省
            order_city: "xxx",          市
            address: "xxx",             邮寄地址
            create_time: "xxx",         创建时间
            update_time: "xxx",         更新时间
            status: xxx,                订单状态：0：未完成，1：已完成
            status_text: "xxx",         未完成 或 已完成
            remark: "xxx",              订单回访结果
            product_name: "xxx",        产品名称

            svc_id: xxx,                号码id
            svc_num: "xxx",             号码
            province: "xxx",            归属省
            city: "xxx",                归属市
            number_status: xxx          号码状态（数字）：0：可用，1：预占，2：在用
        }, {
            ... 另一条信息
        }],
        error: null
    }

具体调用方式请见 2.4.1 ～ 2.4.4 节。

#### 2.4.1 此API是否需要待定

value可能是订单号或联系电话或要办理的手机号，返回所有查询到的信息

GET <http://10.52.200.46:9002/api/order?key=input&value=12345>

#### 2.4.2 根据订单号查询订单

value是订单号

GET <http://10.52.200.46:9002/api/order?key=order_id&value=12345>

#### 2.4.3 根据联系电话查询

value是联系电话

GET <http://10.52.200.46:9002/api/order?key=contact_phone&value=15555555555>

#### 2.4.4 根据要办理的手机号查询

value是要办理的手机号

GET <http://10.52.200.46:9002/api/order?key=svc_num&value=18666666666>

#### 2.4.5 根据订单状态查询

value对应orderdetail.status

GET <http://10.52.200.46:9002/api/order?key=status&value=xxxxx>

### 2.5 根据日期对订单进行统计

输入：date：要查询的日期，格式为YYYY-MM-DD

GET <http://10.52.200.46:9002/api/order/count_by_day?date=2018-10-18>

输出内容包含省分名称、数量、日期（这个日期和输入的参数一样），举例：

    {"error":null,"result":[{"province":"浙江","count":2,"date":"2018-10-18"},{"province":"山西","count":1,"date":"2018-10-18"}]}

## 3. Java API

订单提交 接口，后面的是邮寄地址

<http://192.168.1.128:8090/add/insert?svcId=3&customerName=姓名&c姓名&certNum=123456789012344&contactPhone=15657175987&province=浙江&city=杭州&address=中河北路>

