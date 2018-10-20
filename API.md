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

输出内容包含省分名称、数量、日期（这个日期和输入的参数一样），这些订单只包括状态为*处理完*的订单。处理完的定义：`orderdetail.status = 1` 且 `orderdetail.update_time is not null`

返回内容举例：

    {"error":null,"result":[{"name":"浙江","value":2,"date":"2018-10-18"},{"name":"山西","value":1,"date":"2018-10-18"}]}

### 2.6 订单确认接口

功能：对某个订单进行确认，包括确认办理和取消办理。在进行订单确认的同时，可以根据需要修改姓名、证件号码和地址。

调用方式：

GET <http://10.52.200.46:9002/api/order/confirm?order_id=订单ID&status=状态码&customer_name=姓名&cert_num=证件号码&address=地址>

必填参数：
- order_id：订单号：数字
- status：是否确认办理：1：确认办理，2：取消办理

可选参数
- customer_name：姓名：如果要修改，则提供此参数
- cert_num：证件号码：如果要修改，则提供此参数
- address：地址：如果要修改，则提供此参数

#### 流程说明

A: 在进行确认办理或取消办理前需要进行的操作

1. 判断orderdetail表有没有这条订单，如果没有，则无法继续处理
2. 判断orderdetail表中的这条订单的状态（status）是否为0，只有为0的订单才能设置为1（办理成功）或2（取消办理）

B: 当用户选择确认办理，要进行的操作：

1. 更新 service_num.status 修改为2（表示再用）
2. 更新orderdetail.status修改为1（表示办理成功）
3. 如果用户提供了姓名，修改用户id对应的姓名
4. 如果用户提供了身份证，修改用户id对应的身份证
5. 如果用户提供了地址，要么就修改orderdetail表的address字段

C: 当用户选择取消办理，要进行的操作：

1. 更新service_num.status，修改为0（表示可用）
2. 更新orderdetail.status，修改为2（表示不再办理）

D: 更新时间orderstatus.update_time

当用户完成确认办理或取消办理后，将orderstatus.update_time这个时间更新为当前时间。

### 2.7 获取所有省分名称（只返回有订单的省分名称）

GET <http://10.52.200.46:9002/api/address/province_name>

返回内容示例：

    {"error":null,"result":["浙江","山西","北京","广东","重庆","河南","福建","安徽","江西","天津","湖北","山东","上海","黑龙江","甘肃","河北","吉林","贵州","辽宁","广西","陕西","江苏","内蒙古","新疆","湖南","四川","云南","青海","海南","宁夏","西藏"]}

### 2.8 返回已处理的所有的订单

返回已处理的所有的订单，即订单状态为1或2的订单都返回:

GET <http://10.52.200.46:9002/api/order/processed>

返回结果样例请参考2.4节。

### 2.9 订单明细查询接口

GET <http://10.52.200.46:9002/api/order/detail>

可选参数：

- status：订单状态，对应数据库orderdetail.status字段，可用的值为：0、1、2，分别表示未处理、已处理、已取消
- province：省分名称，对应数据库orderdetail.province字段，如果不提供，则返回全国的数据
- processed：当值为true时，返回订单状态为已处理或已取消状态的订单，也就是说，这两种状态的订单都会返回，即 orderdetail.status in (1, 2)
- name：姓名，当提供了此字段数据后，将会进行模糊查询，即：c.customer_name like '%姓名%'

举例：

<http://10.52.200.46:9002/api/order/detail?province=山西&status=1>
<http://10.52.200.46:9002/api/order/detail?province=山西&processed=true>
<http://10.52.200.46:9002/api/order/detail?name=王>

返回结果样例请参考2.4节。

### 2.10 订单数量统计接口

GET <http://10.52.200.46:9002/api/stat/order_number>

返回值样例：

    {"error":null,"result":{"0":506,"1":4603,"2":4,"all":5113}}

- result["0"] 表示 orderdetail.status 为 0 的数据（未处理的）
- result["1"] 表示 orderdetail.status 为 1 的数据（已完成的）
- result["2"] 表示 orderdetail.status 为 2 的数据（已取消的）
- result["all"] 表示所有的数据

### 2.11 根据月份统计订单数量（所有状态的订单）

GET <http://10.52.200.46:9002/api/stat/order_number_by_month>

返回数据示例：

    {
        "error": null,
        "result": [
            {
                "month": "2018-01",
                "count": 503
            },
            {
                "month": "2018-02",
                "count": 544
            },
            {
                "month": "2018-03",
                "count": 504
            },
            {
                "month": "2018-04",
                "count": 482
            },
            {
                "month": "2018-05",
                "count": 541
            },
            {
                "month": "2018-06",
                "count": 488
            },
            {
                "month": "2018-07",
                "count": 486
            },
            {
                "month": "2018-08",
                "count": 501
            },
            {
                "month": "2018-09",
                "count": 557
            },
            {
                "month": "2018-10",
                "count": 507
            }
        ]
    }

### 2.12 登录校验接口

目前为模拟登录

返回登录成功：

GET <http://10.52.200.46:9002/login?success=true>

返回登录失败：

GET <http://10.52.200.46:9002/login>

### 2.13 按天统计订单量接口

本接口返回从第一个有订单的日期，到最后一个有订单的日期之间的所有数据，包括日期和当日的订单数量，数据来源为 orderdetail 表。

GET <http://10.52.200.46:9002/api/stat/order_number_by_day>

返回数据示例：

    {"error":null,"result":[{"x":"2018-01-03","y":62},{"x":"2018-01-04","y":61}

## 3. Java API

订单提交 接口，后面的是邮寄地址

POST <http://192.168.1.128:8090/add/insert?svcId=3&customerName=姓名&certNum=123456789012344&contactPhone=15657175987&province=浙江&city=杭州&address=中河北路>

curl 测试方法：

    curl -d "svcId=7&customerName=张三&certNum=12345&contactPhone=15657175999&address=并州路" http://10.52.200.34:8090/app/insert

